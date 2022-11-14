package com.cristianovecchi.mikrokanon.AIMUSIC

import android.os.Build
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.alterateLegatosWithDistribution
import com.cristianovecchi.mikrokanon.midi.alterateArticulation
import kotlinx.coroutines.job
import kotlin.coroutines.CoroutineContext
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


data class SubstitutionNotes(val index: Int, val newPitches: List<Int> = emptyList(),
                             val newTicks: List<Int> = emptyList(),
                             val newDurations: List<Int> = emptyList(), val newVelocities: List<Int> = emptyList(),
                             val newGlissando: List<Int> = emptyList(), val newAttacks: List<Int> = emptyList(),
                             val newIsPreviousRest: List<Boolean> = emptyList(),
                             val newArticulationDurations: List<Int>? = null,
                             val newRibattutos: List<Int>? = null) {
//    fun check(replace: ReplaceType) {
//        if(newPitches.size != newVelocities.size) println("WARNING: SubstitutionNote from ${replace.title } is malformed $this")
//    }
}

data class NoteData(val pitch: Int, val tick: Int, val duration: Int, val velocity:Int, val glissando: Int,
val attack:Int, val isPreviousRest: Boolean, val articulationDuration: Int?, val ribattuto: Int?)

data class TrackData(val pitches: IntArray, val ticks: IntArray, var durations: IntArray,
                     val velocities: IntArray, val glissando: IntArray, val attacks: IntArray,
                     val isPreviousRest: BooleanArray,
                     var articulationDurations: IntArray? = null,
                     var ribattutos: IntArray? = null,
                     val channel: Int,  val velocityOff: Int = 80,
                     val vibrato: Int, val doublingFlags: Int = 0,
                     val audio8D: Boolean = false, val partIndex: Int,
                     val changes: List<TickChangeData> = listOf()  )// tick + instrument
{
    fun extractNoteDataAtIndex(index: Int): NoteData{
        return NoteData(pitches[index], ticks[index], durations[index], velocities[index], glissando[index],
        attacks[index], isPreviousRest[index],
        if(articulationDurations == null) null else articulationDurations!![index],
        if(ribattutos == null) null else ribattutos!![index] )
    }
    fun isConnectedToNextNote(index: Int, isStaccato: Boolean): Boolean{
        if(index >= pitches.size - 1) return false
        val next = index + 1
        if(isStaccato || isPreviousRest[next]) return false
        return true
    }
    fun isConnectedToPreviousNote(index: Int): Boolean{
        if(index < 1) return false
        val previous = index - 1
        val previousDuration = durations[previous]
        val actualPreviousDuration = articulationDurations?.get(previous) ?: previousDuration
        if(actualPreviousDuration < previousDuration || isPreviousRest[index]) return false
        return true
    }
    fun findSubstitutionNotes(checkAndReplaceData: CheckAndReplaceData,
                              start: Long, end: Long, trackDataList: List<TrackData>): List<SubstitutionNotes>{
        val substitutions = mutableListOf<SubstitutionNotes>()
        val check = provideCheckFunction(checkAndReplaceData.check)
        val range = checkAndReplaceData.range
        when(checkAndReplaceData.replace){
            is ReplaceType.Fantasia -> {
                for(index in pitches.indices){
                    if(ticks[index] >= end) break
                    val noteEnd = ticks[index] + durations[index]
                    if(noteEnd <= start) continue
                    //println("CHOSEN FOR SUBS: note=${ticks[index]}-$noteEnd slice=$start-$end ")
                    if(pitches[index] in range && check(this, index, trackDataList)) {
                        val available = provideFantasiaFunctions((0..checkAndReplaceData.replace.stress).random(),
                            checkAndReplaceData.replace.isRetrograde, checkAndReplaceData.replace.addGliss)
                        val replaceType = available.random()
                        val replace = provideReplaceFunction(replaceType)
                        substitutions.add(
                            replace(this, index, trackDataList)//.apply{ this.check(replaceType)}
                        )
                    }
                }
            }
            is ReplaceType.Tornado -> {
                val available = provideTornadoFunctions(checkAndReplaceData.replace.stress,
                    checkAndReplaceData.replace.isRetrograde, checkAndReplaceData.replace.addGliss)
                val ventoSize = available.size
                var ventoIndex = 0
                for(index in pitches.indices){
                    if(ticks[index] >= end) break
                    val noteEnd = ticks[index] + durations[index]
                    if(noteEnd <= start) continue
                    //println("CHOSEN FOR SUBS: note=${ticks[index]}-$noteEnd slice=$start-$end ")
                    if(pitches[index] in range && check(this, index, trackDataList)) {
                        val replaceType = available[ventoIndex % ventoSize]
                        val replace = provideReplaceFunction(replaceType)
                        substitutions.add(
                            replace(this, index, trackDataList) //.apply{ this.check(replaceType)}
                        )
                        ventoIndex++
                    }
                }
            }
            else -> {
                val replace = provideReplaceFunction(checkAndReplaceData.replace)
                for(index in pitches.indices){
                    if(ticks[index] >= end) break
                    val noteEnd = ticks[index] + durations[index]
                    if(noteEnd <= start) continue
                    //println("CHOSEN FOR SUBS: note=${ticks[index]}-$noteEnd slice=$start-$end ")
                    if(pitches[index] in range && check(this, index, trackDataList)) substitutions.add(
                        replace(this, index, trackDataList)//.apply{ this.check(checkAndReplaceData.replace)}
                    )
                }
            }
        }
        return substitutions.toList()
    }
    fun checkAndReplace(context: CoroutineContext, checkAndReplaceDataList: List<CheckAndReplaceData>,
                        totalLength: Long, trackDataList: List<TrackData>): TrackData {
        val substitutions = mutableListOf<SubstitutionNotes>()
        val slice = totalLength / checkAndReplaceDataList.size
        val job = context.job
        checkAndReplaceDataList.forEachIndexed { index, checkAndReplaceData ->
            if(job.isActive){
                val start = slice * index
                // println("totalLength:$totalLength start:$start end:${start+slice-1}")
                substitutions.addAll(findSubstitutionNotes(checkAndReplaceData, start, start + slice-1, trackDataList))
            }
        }
        return if(job.isActive) this.substitueNote(substitutions.filter{ it.index!=-1 }.distinctBy { it.index }) // to avoid overlapping
        else TrackData.emptyTrack()
    }
    fun substitueNote(substitutionNotes: List<SubstitutionNotes>): TrackData {
        var subsIndex = 0
        val pitchesData = mutableListOf<Int>()
        val ticksData = mutableListOf<Int>()
        val durationsData = mutableListOf<Int>()
        val velocitiesData = mutableListOf<Int>()
        val glissandoData = mutableListOf<Int>()
        val attackData = mutableListOf<Int>()
        val previousIsRestData = mutableListOf<Boolean>()
        val artDurData = mutableListOf<Int>()
        val ribattutosData = mutableListOf<Int>()
        //substitutionNotes.forEach { println(it) }
        for(noteIndex in pitches.indices){
            if(subsIndex < substitutionNotes.size && noteIndex == substitutionNotes[subsIndex].index){
                val subs = substitutionNotes[subsIndex]
                //println("SUBSTITUTION:${subs.index}")
                pitchesData.addAll(subs.newPitches)
                ticksData.addAll(subs.newTicks)
                durationsData.addAll(subs.newDurations)
                velocitiesData.addAll(subs.newVelocities)
                glissandoData.addAll(subs.newGlissando)
                attackData.addAll(subs.newAttacks)
                previousIsRestData.addAll(subs.newIsPreviousRest)
                articulationDurations?.let{
                    artDurData.addAll(subs.newArticulationDurations!!)
                }
                ribattutos?.let{
                    ribattutosData.addAll(subs.newRibattutos!!)
                }
                subsIndex++
            } else {
                pitchesData.add(pitches[noteIndex])
                ticksData.add(ticks[noteIndex])
                durationsData.add(durations[noteIndex])
                velocitiesData.add(velocities[noteIndex])
                glissandoData.add(glissando[noteIndex])
                attackData.add(attacks[noteIndex])
                previousIsRestData.add(isPreviousRest[noteIndex])
                articulationDurations?.let{
                    artDurData.add(articulationDurations!![noteIndex])
                }
                ribattutos?.let{
                    ribattutosData.add(ribattutos!![noteIndex])
                }
            }
        }


        return TrackData(pitchesData.toIntArray(), ticksData.toIntArray(), durationsData.toIntArray(),
            velocitiesData.toIntArray(), glissandoData.toIntArray(), attackData.toIntArray(),
            previousIsRestData.toBooleanArray(),
            if(this.articulationDurations == null) null else artDurData.toIntArray(),
            if(this.ribattutos == null) null else ribattutosData.toIntArray(),
            channel, 80, vibrato, doublingFlags,
            audio8D, partIndex, changes)
    }
    companion object {
        fun emptyTrack(): TrackData{
            return TrackData(intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(),
                intArrayOf(), booleanArrayOf(),null,null,
                0,80,0,0,false,0)
        }
        // TO CORRECT
        fun findPitchesInSlice(trackDataList: List<TrackData>, start: Long, end: Long): Set<Int>{
            val result = mutableSetOf<Int>()
            for(track in trackDataList){
                for(index in track.pitches.indices){
                    if(track.ticks[index] > end) break
                    val noteEnd = track.ticks[index] + track.durations[index]
                    if(noteEnd < start) continue
                    result.add(track.pitches[index])
                }
            }
            return result.toSet()
        }
    }
}
fun List<TrackData>.analysisInPattern(sectionIndices: List<IntRange?>,
                                        patternTicks: List<Int>, patternDurations: List<Int>): Triple<IntArray, IntArray, IntArray>{
    val resultTicks = IntArray(patternTicks.size)
    val resultVelocities = IntArray(patternTicks.size)
    val resultPitches = IntArray(patternTicks.size)
    val patternSize = patternTicks.size
    this.forEachIndexed { index, trackData ->
        val trackTicks = trackData.ticks
        val trackVelocities = trackData.velocities
        val trackPitches = trackData.pitches
        sectionIndices[index]?.let{
            var patternIndex = 0
            while(patternIndex < patternSize){
                val valueStart = patternTicks[patternIndex]
                val valueEnd = valueStart + patternDurations[patternIndex]
                notes@ for(i in it){
                    val noteTick = trackTicks[i]
                    if(noteTick >= valueEnd) {
                        break@notes
                    }
                    if(noteTick >= valueStart) {
                        resultTicks[patternIndex]++
                        val newVelocity = trackVelocities[i]
                        if(newVelocity > resultVelocities[patternIndex]) resultVelocities[patternIndex] = newVelocity
                        resultPitches[patternIndex] += trackPitches[i]
                    }
                }
                patternIndex++
            }
        }
    }
    return Triple(resultTicks, resultVelocities, resultPitches)
}
fun List<TrackData>.applyMultiCheckAndReplace(context:CoroutineContext, dispatch: (Triple<AppViewModel.Building, Int, Int>) -> Unit,
                                              checkAndReplace: List<List<CheckAndReplaceData>>,
                                                totalLength: Long) : List<TrackData>{
    var result = this
    var isFirstCheckAndReplace = true
    checkAndReplace.forEach{ cnr ->
        val trackDataToTransform = if(isFirstCheckAndReplace) this else result
        if(cnr.isNotEmpty() && cnr.any{ it.check !is CheckType.None }) {
            // println(checkAndReplace)
            result =
                trackDataToTransform.map{ trackData ->
                    //dispatch("Check'n'replace applied to trackData:${trackData.channel}")
                    dispatch(Triple(AppViewModel.Building.CHECK_N_REPLACE, trackData.channel,this.size * checkAndReplace.size))
                    trackData.checkAndReplace(context, cnr, totalLength, trackDataToTransform)
                }
            isFirstCheckAndReplace = false
        }
    }
    return result
}
fun List<TrackData>.addLegatoAndRibattuto(maxLegato: Int, articulations: FloatArray,
        legatoTypes: List<Pair<Int,Int>>, totalLength: Long) {
    val legatos =
        legatoTypes.map { articulations[it.first.absoluteValue] * (if (it.first < 0) -1 else 1) }
    val ribattutos = if (legatoTypes.size == 1) listOf(legatoTypes[0].second, legatoTypes[0].second)
    else legatoTypes.map { it.second }

//            println("Legatos: $legatos")
//            println("Ribattutos: $ribattutos")
    val (legatoAlterations, legatoDeltas, pivots) = alterateLegatosWithDistribution(
        legatos,
        ribattutos,
        0.005f,
        totalLength
    )

    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this
            .parallelStream()
            .forEach {
                val pair = alterateArticulation(
                    it.ticks,
                    it.durations,
                    legatoAlterations,
                    ribattutos,
                    legatoDeltas,
                    pivots,
                    it.isPreviousRest,
                    maxLegato,
                    it.changes
                )
                it.articulationDurations = pair.first
                it.ribattutos = pair.second.map { float -> float.roundToInt() }.toIntArray()
            }
    } else {
        this.forEach {
            val pair = alterateArticulation(
                it.ticks,
                it.durations,
                legatoAlterations,
                ribattutos,
                legatoDeltas,
                pivots,
                it.isPreviousRest,
                maxLegato,
                it.changes
            )
            it.articulationDurations = pair.first
            it.ribattutos = pair.second.map { float -> float.roundToInt() }.toIntArray()
        }
    }
}


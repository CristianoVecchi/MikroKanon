package com.cristianovecchi.mikrokanon.AIMUSIC


data class SubstitutionNotes(val index: Int, val newPitches: List<Int> = emptyList(),
                             val newTicks: List<Int> = emptyList(),
                             val newDurations: List<Int> = emptyList(), val newVelocities: List<Int> = emptyList(),
                             val newGlissando: List<Int> = emptyList(), val newAttacks: List<Int> = emptyList(),
                             val newIsPreviousRest: List<Boolean> = emptyList(),
                             val newArticulationDurations: List<Int>? = null,
                             val newRibattutos: List<Int>? = null)

data class NoteData(val pitch: Int, val tick: Int, val duration: Int, val velocity:Int, val glissando: Int,
val attack:Int, val isPreviousRest: Boolean, val articulationDuration: Int?, val ribattuto: Int?)

data class TrackData(val pitches: IntArray, val ticks: IntArray, var durations: IntArray,
                     val velocities: IntArray,val glissando: IntArray,  val attacks: IntArray,
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
        when(checkAndReplaceData.replace){
            is ReplaceType.Fantasia -> {
                for(index in pitches.indices){
                    if(ticks[index] >= end) break
                    val noteEnd = ticks[index] + durations[index]
                    if(noteEnd <= start) continue
                    //println("CHOSEN FOR SUBS: note=${ticks[index]}-$noteEnd slice=$start-$end ")
                    if(check(this, index, trackDataList)) {
                        val available = provideFantasiaFunctions((0..checkAndReplaceData.replace.stress).random(), checkAndReplaceData.replace.isRetrograde)
                        val replace = provideReplaceFunction(available.random())
                        substitutions.add(replace(this, index, trackDataList))
                    }
                }
            }
            is ReplaceType.Tornado -> {
                val available = provideTornadoFunctions(checkAndReplaceData.replace.stress, checkAndReplaceData.replace.isRetrograde)
                val ventoSize = available.size
                var ventoIndex = 0
                for(index in pitches.indices){
                    if(ticks[index] >= end) break
                    val noteEnd = ticks[index] + durations[index]
                    if(noteEnd <= start) continue
                    //println("CHOSEN FOR SUBS: note=${ticks[index]}-$noteEnd slice=$start-$end ")
                    if(check(this, index, trackDataList)) {
                        val replace = provideReplaceFunction(available[ventoIndex % ventoSize])
                        substitutions.add(replace(this, index, trackDataList))
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
                    if(check(this, index, trackDataList)) substitutions.add(
                        replace(this, index, trackDataList)
                    )
                }
            }
        }
        return substitutions.toList()
    }
    fun checkAndReplace(checkAndReplaceDataList: List<CheckAndReplaceData>,
                        totalLength: Long, trackDataList: List<TrackData>): TrackData {
        val substitutions = mutableListOf<SubstitutionNotes>()
        val slice = totalLength / checkAndReplaceDataList.size
        checkAndReplaceDataList.forEachIndexed { index, checkAndReplaceData ->
            val start = slice * index
           // println("totalLength:$totalLength start:$start end:${start+slice-1}")
            substitutions.addAll(findSubstitutionNotes(checkAndReplaceData, start, start + slice-1, trackDataList))
        }
        return this.substitueNote(substitutions.filter{ it.index!=-1 }.distinctBy { it.index }) // to avoid overlapping
    }
    fun substitueNote(substitutionNotes: List<SubstitutionNotes>): TrackData {
        var subsIndex = 0
        val pitchesData = mutableListOf<Int>()
        val ticksData = mutableListOf<Int>()
        val durationsData = mutableListOf<Int>()
        val velocitiesData = mutableListOf<Int>()
        val glissandoData = mutableListOf<Int>()
        val previousIsRestData = mutableListOf<Boolean>()
        val artDurData = mutableListOf<Int>()
        val ribattutosData = mutableListOf<Int>()
        substitutionNotes.forEach { println(it) }
        for(noteIndex in pitches.indices){
            if(subsIndex < substitutionNotes.size && noteIndex == substitutionNotes[subsIndex].index){
                val subs = substitutionNotes[subsIndex]
                //println("SUBSTITUTION:${subs.index}")
                pitchesData.addAll(subs.newPitches)
                ticksData.addAll(subs.newTicks)
                durationsData.addAll(subs.newDurations)
                velocitiesData.addAll(subs.newVelocities)
                glissandoData.addAll(subs.newGlissando)
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
            velocitiesData.toIntArray(), glissandoData.toIntArray(), IntArray(pitchesData.size),
            previousIsRestData.toBooleanArray(),
            if(this.articulationDurations == null) null else artDurData.toIntArray(),
            if(this.ribattutos == null) null else ribattutosData.toIntArray(),
            channel, 80, vibrato, doublingFlags,
            audio8D, partIndex, changes)
    }
    companion object {
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


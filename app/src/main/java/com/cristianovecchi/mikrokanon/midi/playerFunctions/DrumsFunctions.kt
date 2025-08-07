package com.cristianovecchi.mikrokanon.midi.playerFunctions

import com.cristianovecchi.mikrokanon.AIMUSIC.DrumKit
import com.cristianovecchi.mikrokanon.AIMUSIC.DrumsData
import com.cristianovecchi.mikrokanon.AIMUSIC.DrumsType
import com.cristianovecchi.mikrokanon.AIMUSIC.RhythmPatterns
import com.cristianovecchi.mikrokanon.AIMUSIC.TrackData
import com.cristianovecchi.mikrokanon.AIMUSIC.analysisInPattern
import com.cristianovecchi.mikrokanon.AIMUSIC.patternTicksAndDurationInSection
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.dataAnalysis.Point
import com.cristianovecchi.mikrokanon.dataAnalysis.lloyd
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.findIndicesInSection
import com.cristianovecchi.mikrokanon.nElements
import com.leff.midi.MidiTrack
import com.leff.midi.event.Controller
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn
import kotlinx.coroutines.job
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt

suspend fun createDrumsTrack(context: CoroutineContext, dispatch: (Triple<AppViewModel.Building, Int, Int>) -> Unit,
                             trackDatas: List<TrackData>, drumsDatas: List<DrumsData>, totalLength: Int): MidiTrack?
        = withContext(context) {
    if(drumsDatas.isEmpty() || drumsDatas.all {it.type == DrumsType.NONE} || totalLength == 0 ) {
        null
    } else {
        val track = MidiTrack()
        val sectionDurations = totalLength.divideDistributingRest(drumsDatas.size)
        var tick = 0
        val sectionTicks = sectionDurations.map{
            val oldTick = tick
            tick += it
            oldTick
        }
        //println("Total length: $totalLength")
        drumsDatas.forEachIndexed { index, drumsData ->
            val start = sectionTicks[index]
            val duration = sectionDurations[index]
            //println("$start-${start+duration-1}")
            //println("$drumsData")
        }
        val job = context.job
        drumsDatas.forEachIndexed { index, drumsData ->
            yield() //delay(1)
            if(job.isActive) {
                dispatch(Triple(AppViewModel.Building.DRUMS, index,drumsDatas.size))
                val start = sectionTicks[index]
                val duration = sectionDurations[index]
                when (drumsData.type) {
                    DrumsType.PITCHES_DURS, DrumsType.PITCHES_VELS,
                    DrumsType.DURS_VELS, DrumsType.DURS_PITCHES, DrumsType.VELS_PITCHES,
                    DrumsType.VELS_DURS, DrumsType.TICKS_PITCHES, DrumsType.PITCHES_TICKS,
                    DrumsType.TICKS_DURS, DrumsType.DURS_TICKS, DrumsType.TICKS_VELS,
                    DrumsType.VELS_TICKS ->
                        track.addDrumsToTrackByCentroids(trackDatas, drumsData.drumKit.drumKit,
                            start, duration, drumsData.density, drumsData.volume, drumsData.type)
                    DrumsType.PATTERN -> {
                        val patternDurations = RhythmPatterns.values()[drumsData.pattern].values
                        track.addDrumsToTrackByPattern(
                            trackDatas, start, duration,
                            drumsData.drumKit.drumKit, drumsData.density,
                            drumsData.volume, drumsData.type, patternDurations, drumsData.resize
                        )
                    }
                    else -> {}
                }
            }
        }
        track
    }

//    drumsTrack.addDrumsToTrackByCentroids(actualCounterpointTrackData, DrumKit(),
//        0, totalLength.toInt(),0.5F)
}

private fun MidiTrack.addDrumsToTrackByPattern(trackDatas: List<TrackData>, sectionStart:Int, sectionDuration:Int,
                                               drumKit: DrumKit, density: Float, volume: Float,
                                               type: DrumsType, patternValues: List<Int>, resize: Float,
) {
    try {
        val sectionIndices = trackDatas.map{ it.ticks }.findIndicesInSection(sectionStart, sectionDuration)
        //val nElements = sectionIndices.nElements()
        val soundList = drumKit.soundList()
        val soundListSize = soundList.size
        val resizedPatternValues = if(resize == 1f) patternValues else patternValues.map{ (it * resize).toInt()}
        val (patternTicks, patternDurations) = resizedPatternValues.patternTicksAndDurationInSection(sectionStart, sectionDuration)
        //println("pattern ticks(${patternTicks.size}): $patternTicks")
        //println("pattern durs(${patternDurations.size}): $patternDurations")
        val (weightTicks, weightVelocities, weightPitches, weightDurations) = trackDatas.analysisInPattern(sectionIndices, patternTicks, patternDurations)
        //println("ticks weights(${weightTicks.size}): ${weightTicks.contentToString()}")
        //println("velocities weights(${weightVelocities.size}): ${weightVelocities.contentToString()}")
        //println("pitches weights(${weightPitches.size}): ${weightPitches.contentToString()}")
        val weightTicksGrades = soundListSize + ((1f-density) * soundListSize).toInt()
        val pitchesSet = weightPitches.toSet().sortedDescending()//.take(soundListSize/2)
        //println("pitches set(${pitchesSet.size}): $pitchesSet")
        val auxiliarySoundsGrades = 4
        val pitchesMap = weightPitches.map { pitchesSet.indexOf(it) / auxiliarySoundsGrades}.toIntArray()
        //println("pitches map(${pitchesMap.size}): ${pitchesMap.contentToString()}")
        //println("durations weights(${weightDurations.size}): ${weightDurations.contentToString()}")
        val durationsSet = weightDurations.toSet().sortedDescending()//.take(soundListSize/2)
        //println("durations set(${durationsSet.size}): $durationsSet")
        val durationsMap = weightDurations.map { durationsSet.indexOf(it) / auxiliarySoundsGrades}.toIntArray()
        //println("durations map(${durationsMap.size}): ${durationsMap.contentToString()}")
        val densitySwitch = ((1f - density) * weightTicksGrades).roundToInt()
        val volumePercentage = findVolumePercentage(volume)
        val expression = Controller(sectionStart.toLong(), 9, 11, (127 * volume).toInt())
        this.insertEvent(expression)
        val weightTicksWithGrades = weightTicks.map { it / weightTicksGrades}.toIntArray()
        //println("Weight ticks grades: $weightTicksGrades")
        //println("Grades map(${weightTicksWithGrades.size}): ${weightTicksWithGrades.contentToString()}")
        weightTicksWithGrades.forEachIndexed{ index, weight ->
            val sound = soundList.getOrNull(weight)
            sound?.let{
                val sound2Index = if(pitchesMap[index] == -1) -1 else weight + pitchesMap[index] + 1 + densitySwitch
                val sound2 = soundList.getOrNull(sound2Index)
                val velocity = if(weightTicks[index] == 0) (104 * volumePercentage).toInt().coerceIn(0 , 127)
                else (weightVelocities[index] * volumePercentage).toInt().coerceIn(0 , 127)
                val start = patternTicks[index].toLong()
                val duration = patternDurations[index]
                val on = NoteOn(start, 9, sound, velocity)
                val off = NoteOff(start + duration, 9, sound, velocity)
                //println("Drum sound: $sound   tick: $start")
                this.insertEvent(on)
                this.insertEvent(off)
                //println("Index: $index Tick: ${patternTicks[index]}  Sounds: $sound $sound2 $sound3   Indices: $weight $sound2Index $sound3Index  Velocity: $velocity  VolumePercentage: $volumePercentage  Density: $density  DensitySwitch: $densitySwitch")
                sound2?.let{
                    val sound3Index = if(durationsMap[index] == -1) -1 else sound2Index  + durationsMap[index] + 1 + densitySwitch
                    val sound3 = soundList.getOrNull(sound3Index)
                    val on2 = NoteOn(start, 9, sound2, velocity)
                    val off2 = NoteOff(start + duration, 9, sound2, velocity)
                    //println("Drum sound: $sound   tick: $start")
                    this.insertEvent(on2)
                    this.insertEvent(off2)
                    sound3?.let{
                        val on3 = NoteOn(start, 9, sound3, velocity)
                        val off3 = NoteOff(start + duration, 9, sound3, velocity)
                        //println("Drum sound: $sound   tick: $start")
                        this.insertEvent(on3)
                        this.insertEvent(off3)
                    }
                }
//
            }

        }
    } catch(e: java.lang.Exception){
        println("Pattern Error: ${e.message}")
    }
}
fun findVolumePercentage(volume: Float): Float {
    return if(volume <= 0.5f) volume * 2 else 1f + volume / 4
}
fun MidiTrack.addDrumsToTrackByCentroids(trackDatas: List<TrackData>, drumKit: DrumKit,
                                         sectionStart:Int, sectionDuration:Int, density: Float, volume: Float, type: DrumsType){
    val sectionIndices = trackDatas.map{ it.ticks }.findIndicesInSection(sectionStart, sectionDuration)
    val nElements = sectionIndices.nElements()
//    println("Drums section: $sectionStart - ${sectionStart + sectionDuration -1}  density: $density")
//    println(sectionIndices)
//    println(nElements)
//    val sectionStarts = mutableListOf(sectionStart)
//    val sectionDurations = mutableListOf(sectionDuration)
//    val densities = mutableListOf(density)
    if(nElements > 3000){
        val sectionHalves = sectionDuration.divideDistributingRest(2)
        //println("--- DRUMS SECTION SPLIT ---")
        this.addDrumsToTrackByCentroids(trackDatas, drumKit, sectionStart, sectionHalves[0], density, volume, type)
        this.addDrumsToTrackByCentroids(trackDatas, drumKit, sectionStart + sectionHalves[0], sectionHalves[1], density, volume, type)
    } else {
        val soundList = drumKit.soundList()
        val emptyCentroids = ((1f - density) * soundList.size).toInt()// add density
        val nCentroids = soundList.size + emptyCentroids
        // println("empty Centroids: $emptyCentroids   nCentroids: $nCentroids   density:$density")
        val points = mutableListOf<Point>()
        trackDatas.forEachIndexed { index, trackData ->
            val (xs, ys) = when(type) {
                DrumsType.PITCHES_DURS -> trackData.pitches to trackData.durations
                DrumsType.PITCHES_VELS -> trackData.pitches to trackData.velocities
                DrumsType.DURS_VELS -> trackData.durations to trackData.velocities
                DrumsType.DURS_PITCHES -> trackData.durations to trackData.pitches
                DrumsType.VELS_PITCHES -> trackData.velocities to trackData.pitches
                DrumsType.VELS_DURS -> trackData.velocities to trackData.durations
                DrumsType.TICKS_PITCHES -> trackData.ticks to trackData.pitches
                DrumsType.PITCHES_TICKS -> trackData.pitches to trackData.ticks
                DrumsType.TICKS_DURS -> trackData.ticks to trackData.durations
                DrumsType.DURS_TICKS -> trackData.durations to trackData.ticks
                DrumsType.TICKS_VELS -> trackData.ticks to trackData.velocities
                DrumsType.VELS_TICKS -> trackData.velocities to trackData.ticks
                else -> trackData.pitches to trackData.durations
            }
            val partIndex = trackData.partIndex
            sectionIndices[index]?.let {
//                val ticks = trackData.ticks
//                val pitches = trackData.pitches
//                val durations = trackData.durations
//                val velocities = trackData.velocities
                for (noteIndex in it) {
                    points.add(
                        Point(
                            xs[noteIndex].toDouble(),
                            ys[noteIndex].toDouble(),
                            0,
                            partIndex,
                            noteIndex
                        )
                    )
                }
            }
        }
        if(points.isNotEmpty()){
            var count = 0
            val centroids = lloyd(points, points.size, nCentroids) // assign the group to points
            val sums = points.groupingBy { it.group }.eachCount().toSortedMap()
            val sumsPairs = sums.entries.sortedBy {
                it.value
            }
            val ascendantSums = IntArray(sumsPairs.size)
            sumsPairs.forEachIndexed{ i, entry ->
                ascendantSums[i] = entry.key
            }
//        println("sums: $sums")
//        println("sumsPairs: $sumsPairs")
//        println("ascendant sums: ${ascendantSums.contentToString()}")
            val volumePercentage = findVolumePercentage(volume)
            val trackDatasArray = trackDatas.sortedBy { it.partIndex }.toTypedArray()
            points.forEach {
                //println(it)
                val group = ascendantSums.indexOf(it.group)
                if(group < soundList.size) {
                    val part = trackDatasArray[it.partIndex]
                    val noteIndex = it.noteIndex
                    val duration = if(part.durations[noteIndex] > 1920) 1920L else part.durations[noteIndex].toLong()
                    val start = part.ticks[noteIndex].toLong()
                    val sound = soundList[group]
                    val velocity = (part.velocities[noteIndex] * volumePercentage).toInt().coerceIn(0,127)
                    //println("Volume:$volume ${it.velocity}->$velocity")
                    val on = NoteOn(start, 9, sound, velocity)
                    val off = NoteOff(start + duration, 9, sound, velocity)
                    //println("Drum sound: $sound   tick: $start")
                    this.insertEvent(on)
                    this.insertEvent(off)
                    count++
                } else {
                    //println("Note without drums")
                }
            }
            //println("Drum Sounds assigned: $count/${points.size} ")
        }
    }
}
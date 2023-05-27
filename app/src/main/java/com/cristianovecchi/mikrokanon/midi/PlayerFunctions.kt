package com.cristianovecchi.mikrokanon.midi

import com.cristianovecchi.mikrokanon.*
import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.dataAnalysis.Point
import com.cristianovecchi.mikrokanon.dataAnalysis.lloyd
import com.leff.midi.MidiTrack
import com.leff.midi.event.*
import com.leff.midi.event.meta.Tempo
import com.leff.midi.event.meta.TimeSignature
import kotlinx.coroutines.job
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlin.coroutines.CoroutineContext
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

fun main(){

}
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
                    DrumsType.VELS_DURS, DrumsType.TICKS_PITCHES,DrumsType.PITCHES_TICKS,
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
fun buildTempoTrack(bpms: List<Float>, totalLength: Long): MidiTrack {
    val tempoTrack = MidiTrack()
    val (bpmAlterations, bpmDeltas) = alterateBpmWithDistribution(bpms, 0.5f, totalLength)
    var tempoTick = 0L

    (0 until bpmAlterations.size - 1).forEach { index -> // doesn't take the last bpm
        val newTempo = Tempo(tempoTick, 0L, 500000)
        newTempo.bpm = bpmAlterations[index]
        tempoTrack.insertEvent(newTempo)
        tempoTick += bpmDeltas[index]
    }
    return tempoTrack
}
fun MidiTrack.addVolumeToTrack(dynamics: List<Float>, totalLength: Long) {
    //val dynamics = listOf(1f,0f,1f)
    val (dynamicAlterations, dynamicDeltas) = alterateBpmWithDistribution(dynamics, 0.01f, totalLength)
    var tempoTick = 0L

    (0 until dynamicAlterations.size - 1).forEach { index -> // doesn't take the last dynamic
        // 0x7F = universal immediatly message, 0x7F = all devices, 0x04 = device control message, 0x01 = master volume
        // bytes = first the low 7 bits, second the high 7 bits - volume is from 0x0000 to 0x3FFF
        val volumeBytes: Pair<Int, Int> = dynamicAlterations[index].convertDynamicToBytes()
        val data: ByteArray =
            listOf(0x7F, 0x7F, 0x04, 0x01, volumeBytes.first, volumeBytes.second, 0xF7)
                .foldIndexed(ByteArray(7)) { i, a, v -> a.apply { set(i, v.toByte()) } }
        val newGeneralVolume = SystemExclusiveEvent(0xF0, tempoTick, data)

        this.insertEvent(newGeneralVolume)
        tempoTick += dynamicDeltas[index]
    }
}


fun findExtendedWeightedHarmonyNotes(chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, roots: MutableList<Int>,
                                     diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, octaves: List<Int>) {
    data class Note(val pitch: Int, val tick: Long, var duration: Long, val velocity: Int)
    val notes = mutableListOf<Note>()
    val rootNotes = mutableListOf<Note>()
    val actualOctaves = octaves.map{ it +1 }
    for (absPitch in 0..11) {
        val contains = BooleanArray(bars.size) { false }
        bars.forEachIndexed { index, bar ->
           // println("Bar$index = ${bar.dodecaByte1stHalf!!.toString(2)}")
            if (convertDodecabyteToInts(bar.dodecaByte1stHalf!!).contains(absPitch)) contains[index] = true
        }
        var index = 0
        var lastNote = Note(-1, 0, 0,0)
        while (index < contains.size) {
            if (contains[index]) {
                val bar = bars[index]
                if (lastNote.pitch == -1) {
                    lastNote = Note(absPitch, bar.tick, bar.duration, bar.minVelocity!!)
                } else {
                    lastNote.duration += bar.duration
                }
            } else {
                if (lastNote.pitch != -1) {
                    notes.add(lastNote)
                    lastNote = Note(-1, 0, 0,0)
                }
            }
            index++
        }
        if (lastNote.pitch != -1) {
            notes.add(lastNote)
        }
    }
    if(!justVoicing){
        var lastRootNote = Note(-1,0,0, 0)
        var index = 0
        while (index < roots.size) {
            val bar = bars[index]
            val newRoot = roots[index]
            if (newRoot != lastRootNote.pitch) {
                lastRootNote = Note(newRoot, bar.tick, bar.duration, bar.minVelocity!!)
                rootNotes.add(lastRootNote)
            } else {
                lastRootNote.duration += bar.duration
            }
            index ++
        }
    }

        notes.sortedBy { it.tick }.forEach {
            //print("Chord note: ${it.pitch}, ")
            val absPitch = it.pitch
            val tick = it.tick
            val duration = it.duration
            val velocity = (it.velocity - diffChordVelocity).coerceIn(0,127)
            actualOctaves.forEach { octave ->
                Player.insertNoteWithGlissando(
                    chordsTrack, tick, duration, chordsChannel,
                    octave * 12 + absPitch, velocity, 70, 0
                )
            }
        }
            val addBassesTo2ndOctave = !actualOctaves.contains(2)
            val addBassesTo3rdOctave = !actualOctaves.contains(3)
            if(!justVoicing && (addBassesTo2ndOctave || addBassesTo3rdOctave)){
            rootNotes.sortedBy { it.tick }.forEach {
                //println("Root: $it")
                val absPitch = it.pitch
                val tick = it.tick
                val duration = it.duration
                val velocity = (it.velocity - diffRootVelocity).coerceIn(0,127)
                if(addBassesTo2ndOctave){
                    Player.insertNoteWithGlissando(
                        chordsTrack, tick, duration, chordsChannel,
                        24 + absPitch, velocity, 50, 0
                    )
                }
                if(addBassesTo3rdOctave) {
                    Player.insertNoteWithGlissando(
                        chordsTrack, tick, duration, chordsChannel,
                        36 + absPitch, velocity, 50, 0
                    )
                }
            }
        }
}
fun findNoteLine(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                   diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true) {
    //data class Note(val pitch: Int, val tick: Long, var duration: Long, val velocity: Int)
        //bars.forEach { println(it) }
        var lastPitch = -1
        val actualOctaves = octaves.map{ it +1 }
        val isRiver = when(harmonizationStyle){
            HarmonizationStyle.ASCENDING_RIVER, HarmonizationStyle.DESCENDING_RIVER, HarmonizationStyle.RANDOM_RIVER -> true
            else -> false
        }
            //.also{println("Octaves: $octaves -> Actual octaves: $it")}
        bars.forEachIndexed { i, bar ->
            val barDur = bar.duration
            var pitches = if(barDur < 48) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)}
                else {
                    when (harmonizationStyle){
                        HarmonizationStyle.DESCENDING, HarmonizationStyle.DESCENDING_RIVER ->  absPitches[i].reversed()
                        HarmonizationStyle.RANDOM, HarmonizationStyle.RANDOM_RIVER ->  absPitches[i].shuffled()
                        else -> absPitches[i]
                    }
                }
            if(pitches.isNotEmpty()){
                val durs = barDur.divideDistributingRest(pitches.size)
                val velocity = (bar.minVelocity!! - diffChordVelocity).coerceIn(0, 127)
                if(isRiver){
                    actualOctaves.forEach { octave ->
                        var tick = bar.tick
                        //pitches = if(pitches.first() == lastPitch) pitches.shiftCycling() else pitches
                        pitches.forEachIndexed { j, absPitch ->
                            val duration = durs[j]
                            Player.insertNoteWithGlissando(
                                chordsTrack, tick, duration, chordsChannel,
                                octave * 12 + absPitch, velocity, 70, 0
                            )
                            tick += duration
                        }
                        pitches = pitches.shiftCycling()
                       // lastPitch = pitches.last()
                    }
                } else {
                    var tick = bar.tick
                    pitches = if(pitches.first() == lastPitch) pitches.shiftCycling() else pitches
                    pitches.forEachIndexed { j, absPitch ->
                        val duration = durs[j]
                        actualOctaves.forEach{ octave ->
                            Player.insertNoteWithGlissando(
                                chordsTrack, tick, duration, chordsChannel,
                                octave * 12 + absPitch, velocity, 70, 0
                            )
                        }
                        tick += duration
                    }
                    lastPitch = pitches.last()
                }
            }

        }
}
fun findChordNotes(chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>,
                   diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, octaves: List<Int>) {
    data class Note(val pitch: Int, val tick: Long, var duration: Long, val velocity: Int)
    val actualOctaves = octaves.map{ it +1 }
    val notes = mutableListOf<Note>()
    val roots = mutableListOf<Note>()
    for (absPitch in 0..11) {
        val contains = BooleanArray(bars.size) { false }
        bars.forEachIndexed { index, bar ->
            if (bar.chord1!!.absoluteNotes.contains(absPitch)) contains[index] = true
        }
        var index = 0
        var lastNote = Note(-1, 0, 0,0)
        while (index < contains.size) {
            if (contains[index]) {
                val bar = bars[index]
                if (lastNote.pitch == -1) {
                    lastNote = Note(absPitch, bar.tick, bar.duration, bar.minVelocity!!)
                } else {
                    lastNote.duration += bar.duration
                }
            } else {
                if (lastNote.pitch != -1) {
                    notes.add(lastNote)
                    lastNote = Note(-1, 0, 0,0)
                }
            }
            index++
        }
        if (lastNote.pitch != -1) {
            notes.add(lastNote)
        }
    }
    if(!justVoicing){
        var lastRoot = Note(-1,0,0, 0)
        var index = 0
        while (index < bars.size) {
            val bar = bars[index]
            val newRoot = bar.chord1!!.root
            if (newRoot != lastRoot.pitch) {
                    lastRoot = Note(newRoot, bar.tick, bar.duration, bar.minVelocity!!)
                    roots.add(lastRoot)
            } else {
                lastRoot.duration += bar.duration
            }
            index ++
        }
    }
    notes.sortedBy { it.tick }.forEach {
        val absPitch = it.pitch
        val tick = it.tick
        val duration = it.duration
        val velocity = (it.velocity - diffChordVelocity).coerceIn(0,127)
        actualOctaves.forEach{ octave ->
            Player.insertNoteWithGlissando(
                chordsTrack, tick, duration, chordsChannel,
                octave * 12 + absPitch, velocity, 70, 0
            )
        }
    }
    val addBassesTo2ndOctave = !actualOctaves.contains(2)
    val addBassesTo3rdOctave = !actualOctaves.contains(3)
    if(!justVoicing && (addBassesTo2ndOctave || addBassesTo3rdOctave)){
        roots.sortedBy { it.tick }.forEach {
            //println("Root: $it")
            val absPitch = it.pitch
            val tick = it.tick
            val duration = it.duration
            val velocity = (it.velocity - diffRootVelocity).coerceIn(0,127)
            if(addBassesTo2ndOctave){
                Player.insertNoteWithGlissando(
                    chordsTrack, tick, duration, chordsChannel,
                    24 + absPitch, velocity, 50, 0
                )
            }
            if(addBassesTo3rdOctave) {
                Player.insertNoteWithGlissando(
                    chordsTrack, tick, duration, chordsChannel,
                    36 + absPitch, velocity, 50, 0
                )
            }
        }
    }
}

fun insertChordNotes(chordsTrack: MidiTrack, channel: Int, root: Int,
                     absPitches: IntArray, tick: Long, duration: Long, velocity: Int, justVoicing: Boolean = false) {
    if(!justVoicing){
        for(octave in 2..3){
            Player.insertNoteWithGlissando(chordsTrack, tick, duration, channel,
                octave * 12 + root, velocity, 70,0)

        }
    }
    for(octave in 4..8){
        for(absPitch in absPitches){
            Player.insertNoteWithGlissando(chordsTrack, tick, duration, channel,
                octave * 12 + absPitch, velocity, 70,0)
        }
    }
}
fun assignDodecaBytesToBars(bars: Array<Bar>, counterpointTrackData: List<TrackData>, withArticulation: Boolean = false) {
    bars.forEach { it.dodecaByte1stHalf = 0 ; it.dodecaByte2ndHalf = 0 ; it.minVelocity = 80}
    counterpointTrackData.forEach{ trackData ->
        val durations = if(trackData.articulationDurations != null && withArticulation) trackData.articulationDurations!! else trackData.durations
        var barIndex = 0
        var pitchIndex = 0
        while(pitchIndex < trackData.pitches.size){
            val bar = bars[barIndex]
            val pitch = trackData.pitches[pitchIndex]
            val velocity = trackData.velocities[pitchIndex]
            val barEnd = bar.tick + bar.duration
            val pitchStart = trackData.ticks[pitchIndex]
            val pitchEnd = pitchStart + durations[pitchIndex]
            if(trackData.ticks[pitchIndex] < barEnd ){
                bar.dodecaByte1stHalf = bar.dodecaByte1stHalf?.or((1 shl (pitch % 12)))
                if(velocity < bar.minVelocity!! ) bar.minVelocity = velocity
                if(pitchEnd > barEnd) barIndex++ else pitchIndex++

            } else {
                barIndex++
            }
        }
    }
}
fun printNoteLimits(ticks: IntArray, durations: IntArray) {
    for(i in ticks.indices){
        print("$i=${ticks[i]}-${ticks[i]+durations[i]} ")
    }
    println()
}
fun TrackData.convertToMidiTrack(nParts: Int, addAttack: Boolean = false): MidiTrack {
    val track = MidiTrack()
    val channel = this.channel
    val vibrato = this.vibrato
    val velocityOff = this.velocityOff
    val (pitches, ticks, durations, velocities, glissando) = this
    val articulationDurations = this.articulationDurations ?: durations
    val ribattutos = this.ribattutos ?: IntArray(pitches.size){ 1 }

    // Instrument changes
//    println()
//    println("CHANNEL: $channel")
    var lastTick = -1L // avoid overriding
   // var noteIndex = -1
    //printNoteLimits(ticks, articulationDurations)
    if(addAttack) this.addAttackToMidiTrack(track)

    this.changes.forEach{
       // println("Intrument change: $it")
        val tick = it.tick
//        do {
//            noteIndex++
//        } while(noteIndex < ticks.size && ticks[noteIndex]+articulationDurations[noteIndex]<= tick)
//        println("note index: $noteIndex")
//        println("old tick: $tick")
//        tick = if(tick <= ticks[noteIndex]) tick else ticks[noteIndex]+articulationDurations[noteIndex]+1.toLong()
//        println("new tick: $tick")
        if(tick > lastTick){
//            println(it)
            val pc: MidiEvent = ProgramChange(tick, channel, it.instrument) // cambia strumento
            track.insertEvent(pc)
            lastTick = tick
        }

    }
    // STEREO
    val panStep: Int = 127 / nParts
    val pans = (0 until nParts).map { it * panStep + panStep / 2 }//.also { println(it) }
    if (!this.audio8D) { // set a fixed pan if 8D AUDIO is not set on this track
        val pan = Controller(0, channel, 10, pans[this.partIndex])
        track.insertEvent(pan)
    }

//        var lastIsGliss = false
//        var attackIsDelayed = false
    if (this.doublingFlags == 0) {
        for (i in pitches.indices) {
            val tick = ticks[i].toLong()
            val gliss = glissando[i]
            val duration = durations[i]
            val articulationDuration = articulationDurations[i]
            val ribattuto = ribattutos[i]
            val overLegato = articulationDuration > duration
//                val attackDelay = if(lastIsGliss && (articulationDuration == duration || overLegato)) 127 else 0
            val dur = if(overLegato && (glissando.getOrElse(i) { 0 } >0 || gliss >0)  )
                duration.toLong() else articulationDuration.toLong()
            //println("note $i attack: $attackDelay")
            if (this.vibrato != 0) {
                addVibratoToTrack(track, tick, dur, channel, vibrato)
            }
//                if (attackDelay > 0){
//                   addAttackDelayToTrack(track, tick, channel, attackDelay)
//                    attackIsDelayed = true
//                } else {
//                    if(attackIsDelayed)  addAttackDelayToTrack(track, tick, channel, 0)
//                    attackIsDelayed = false
//                }
            Player.insertNoteWithGlissando(
                track, tick, dur, channel, pitches[i],
                velocities[i], velocityOff, gliss, ribattuto
            )
//                lastIsGliss = gliss > 0
        }

    } else {
        val doubling = convertFlagsToInts(this.doublingFlags)
        for (i in pitches.indices) {
            val tick = ticks[i].toLong()
            val gliss = glissando[i]
            val duration = durations[i]
            val articulationDuration = articulationDurations[i]
            val ribattuto = ribattutos[i]
            val overLegato = articulationDuration > duration
//                val attackDelay = if(lastIsGliss && (articulationDuration == duration || overLegato)) 127 else 0
            val dur = if(overLegato && (glissando.getOrElse(i) { 0 } >0 || gliss >0)  )
                duration.toLong() else articulationDuration.toLong()
            val pitch = pitches[i]
            val velocity = velocities[i]
            if (this.vibrato != 0) {
                addVibratoToTrack(track, tick, dur, channel, vibrato)
            }
            Player.insertNoteWithGlissando(
                track, tick, dur, channel, pitch,
                velocity, velocityOff, gliss, ribattuto
            )
            doubling.forEach {
                Player.insertDoublingNote(
                    track, tick, dur, channel, pitch + it,
                    velocity, velocityOff, ribattuto
                )
            }
        }
    }
    // STEREO ALTERATIONS FOR EACH TRACK
    if(this.audio8D && track.lengthInTicks > 0) {
        val nRevolutions = (12 - this.partIndex) * 2
        setAudio8D(track, nRevolutions, channel)
    }
    return track//.apply { this.dumpEvents() }
}
fun TrackData.addAttackToMidiTrack(midiTrack: MidiTrack) {
    //println("attacks: ${this.attacks.contentToString()}")
    ticks.forEachIndexed { i, intTick ->
        val newAttack = attacks[i]
        if(newAttack > 0){
            var tick = intTick.toLong()
            val attackDur = (durations[i] * (newAttack.toFloat() / 100)).toInt()
            if(attackDur >= 12){
                var values = (29..127).filter{it % 2 != 0 }
                val size = values.size
                if(attackDur < size) {
                    values = values.subList(size - attackDur, size).filter {it % 2 != 0 }
                }
                val step = attackDur / values.size // possibly reducted size
                //println("tick: $tick dur: $dur  attackDur: $attackDur  step: $step  ${values.size} $values")
                values.forEach{
                    val setAttack = Controller(tick, this.channel,11, it)
                    midiTrack.insertEvent(setAttack)
                    //println( "Attack: ${setAttack.value}  tick: $tick")
                    tick += step
                }
                //println("last tick: $tick")
            }

        } else if(newAttack < 0) {
            var tick = intTick.toLong()
            val attackDur = (durations[i] * (newAttack.absoluteValue.toFloat() / 100)).toInt()
            if(attackDur >= 12){
                var values = (125 downTo 29).filter{it % 2 != 0 }
                val size = values.size
                //println("newAttack:$newAttack attackDur:$attackDur values:$values")
                if(attackDur < size) {
                    values = values.subList(0, attackDur).filter{it % 2 != 0 }
                }
                val step = attackDur / values.size // possibly reducted size
                //println("tick: $tick dur: $dur  attackDur: $attackDur  step: $step  ${values.size} $values")
                val firstAttack = Controller(tick, this.channel,11, 127)
                midiTrack.insertEvent(firstAttack)
                tick = tick + durations[i] - attackDur
                values.forEach{
                    val setAttack = Controller(tick, this.channel,11, it)
                    midiTrack.insertEvent(setAttack)
                    //println( "Attack: ${setAttack.value}  tick: $tick")
                    tick += step
                }
                val nextAttack = attacks.getOrNull(i + 1)
                nextAttack?.let{
                    if(nextAttack == 0){
                        val setAttack = Controller(ticks[i+1].toLong(), this.channel,11, 127)
                        midiTrack.insertEvent(setAttack)
                    }
                }
            }
        }
        // 70 = Sound Variation
        // 71 = Sound Timbre
        // 72 = Release Time
        // 73 = Attack Time
        // 74 = Sound Brightness
        // 91 = Effects Level
        // 92 = Tremulo Level
        // 93 = Chorus Level
        // 94 = Celeste Level
        // 95 = Phaser Level
    }
}
fun setAudio8D(track: MidiTrack, nRevolutions: Int, channel: Int) {
    val length = track.lengthInTicks
    if(length == 0L) return
    val aims = mutableListOf<Float>()
    for(i in 0 until nRevolutions){
        aims.add(0f)
        aims.add(127f)
    }
    aims.add(0f)
    //println("TRACK N°$channel length:${track.lengthInTicks}")
    val (audio8Dalterations, audio8Ddeltas) = alterateBpmWithDistribution(aims, 2f, track.lengthInTicks)
    var tick = 0L
    //println("AUDIO 8D DELTAS: $audio8Ddeltas")
    (0 until audio8Dalterations.size -1).forEach { i -> // doesn't take the last bpm
        val newPan = Controller(tick, channel,10, audio8Dalterations[i].toInt())
        track.insertEvent(newPan)
        tick += audio8Ddeltas[i]
    }
}


fun MidiTrack.setTimeSignatures(rhythm: List<Triple<RhythmPatterns, Boolean, Int>>, totalLength: Long): List<Bar> {
    val bars = mutableListOf<Bar>()
    var tick = 0L
    var barTick = 0L
    var lastSignature = Pair(-1, -1)
    val signatures: List<Pair<Int, Pair<Int, Int>>> = rhythm.map {
        val patternDuration = it.first.patternDuration()
        val barDuration = it.first.barDuration()
        val nRepetitions = it.third
        val metro = it.first.metro
        for(i in 0 until nRepetitions * (patternDuration/barDuration)){
            bars.add(Bar(metro, barTick, barDuration.toLong()))
            barTick += barDuration
        }
        Pair(patternDuration * nRepetitions, metro)
    }
    var index = 0
    while (tick < totalLength) {
        var newSignature = signatures[index].second
        if (newSignature.first == 1) {
            newSignature =
                RhythmPatterns.mergeSequenceOfOnesInMetro(signatures[index].first, newSignature)
        }
        if (newSignature != lastSignature) {
            val ts = TimeSignature(
                tick, 0L, newSignature.first, newSignature.second,
                TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION
            )
            this.insertEvent(ts)
            // println("SIGNATURE #$index: tick = $tick  metro = ${newSignature.first}/${newSignature.second}")
            lastSignature = newSignature
        }
        tick += signatures[index].first
        index = ++index % signatures.size
    }
    return bars.toList()
}

fun alterateArticulation(
    ticks: IntArray, durations: IntArray,
    legatoAlterations: List<Float>, ribattutos: List<Int>, legatoDeltas: List<Long>,
    pivots: List<Int>, previousIsRest: BooleanArray, maxLegato: Int, changes: List<TickChangeData>
): Pair<IntArray, FloatArray> {
    if (durations.isEmpty()) return Pair(IntArray(0), FloatArray(0))
    val result = IntArray(durations.size)
    //val resultRibattutos = FloatArray(durations.size)

    var alterationIndex = 0
    var alterationTick = 0
    var durIndex = 0
    var legatoAlteration: Float
   // var ribattutoAlteration: Float
    var newDur: Int
    var nextDur: Int
    var thisDur: Int
    var legato: Int
    val notePivots = mutableListOf<Int>()
    var pivotIndex = 0
    var changeIndex = 0
    val changeNotes = if(changes.size == 1) listOf(0)
                    else changes.map{it.noteIndex}.drop(1)//.apply{println("changeNote: $this")}
    if (durations.isNotEmpty()) {
        while (durIndex < durations.size - 1) {
            while (alterationTick + legatoDeltas[alterationIndex] < ticks[durIndex]) {
                alterationTick += legatoDeltas[alterationIndex].toInt()
                alterationIndex++
            }
            if(alterationIndex >= pivots[pivotIndex]){
                notePivots.add(durIndex)
                pivotIndex++
            }
            legatoAlteration = legatoAlterations[alterationIndex]
           // ribattutoAlteration = ribattutosAlterations[alterationIndex]
            thisDur = durations[durIndex]
            if (legatoAlteration <= 1.0) {
                newDur = (thisDur * legatoAlteration).toInt()
                result[durIndex] = if (newDur < 12) 12 else newDur
             //   resultRibattutos[durIndex] = ribattutoAlteration
            } else {
                if (previousIsRest[durIndex + 1]) { // there is a rest between notes, legato is not requested
                    result[durIndex] = thisDur
                   // resultRibattutos[durIndex] = ribattutoAlteration
                } else if(changeIndex<changeNotes.size && durIndex + 1 == changeNotes[changeIndex]){ // no legato if the next notes has a program change on it
                    //println("Legato avoided on note $durIndex cause program change on the next one.")
                    result[durIndex] = thisDur
                    changeIndex++
                } else {
                    nextDur = durations[durIndex + 1]
                    legato = (nextDur * (legatoAlteration - 1f)).toInt()
                    result[durIndex] =
                        if (legato > maxLegato) thisDur + maxLegato else thisDur + legato
                  //  resultRibattutos[durIndex] = ribattutoAlteration
                }
            }
            durIndex++
        }
    }
    //println("durationSize=${durations.size} legatoDeltasSize=${legatoDeltas.size} lastTick=${ticks[durIndex]}")
    while (alterationIndex < legatoDeltas.size && alterationTick + legatoDeltas[alterationIndex] <= ticks[durIndex]) {
        alterationTick += legatoDeltas[alterationIndex].toInt()
        alterationIndex++
    }
    legatoAlteration = if (alterationIndex< legatoDeltas.size) legatoAlterations[alterationIndex]//.apply{println("not last $this")}
                    else legatoAlterations[legatoAlterations.size-1]//.apply{println("last $this")}
   // ribattutoAlteration = ribattutosAlterations[alterationIndex]
    thisDur = durations[durIndex]
    if (legatoAlteration <= 1.0) {
        newDur = (thisDur * legatoAlteration).toInt()
        result[durIndex] = if (newDur < 12) 12 else newDur
    } else {
        result[durIndex] = thisDur // last note doesn't need legato
    }

    //pivots.add(durations.size)
    //resultRibattutos[durIndex] = ribattutoAlteration

    notePivots.add(durations.size)
    val ribattutosAlterations: List<Float> = projectRibattutos(ribattutos.map{it.toFloat()}, notePivots)
//        println("Original durations: ${durations.contentToString()}")
//        result.also{ println("Alterate articulations: ${it.contentToString()}") }
//        ribattutosAlterations.also{ println("Alterate ribattutos: ${it}") }
//        println("Rounded ribattutos: ${ribattutosAlterations.map{it.roundToInt()}}")
//        println("notePivots: $notePivots")
    return Pair(result, ribattutosAlterations.toFloatArray())
}

fun projectRibattutos(ribattutos: List<Float>, notePivots: List<Int>): List<Float> {
    val result = mutableListOf<Float>()

    if(notePivots.size <= 1){ // just one section
        return List(notePivots.last()) {ribattutos[0]}
    }
    for(i in 0 until notePivots.size -1){
        val sectionSize = notePivots[i+1] - notePivots[i]
        //println("ribattutos in projection: $ribattutos")
        if(ribattutos[i] == ribattutos[i+1]){
            result.addAll(List(sectionSize){ribattutos[i]})//.apply{println("Rib section $i: $this")})
        } else {
            val startRibattuto = ribattutos[i]
            val step = (ribattutos[i+1] - startRibattuto) / sectionSize
            result.addAll( (0 until sectionSize).map{ startRibattuto + it * step})//.apply{println("Rib section $i: $this")})
        }
    }
    return result.toList()
}

fun insertNoteCheckingHigh(
    mt: MidiTrack, start: Int, duration: Int, channel: Int,
    pitch: Int, velOn: Int, velOff: Int
) {
    var actualPitch = pitch
    while (actualPitch > 108){
        actualPitch -= 12
    }
    val on = NoteOn(start.toLong(), channel, actualPitch, velOn)
    val off = NoteOff((start + duration).toLong(), channel, actualPitch, velOff)
    mt.insertEvent(on)
    mt.insertEvent(off)
}
fun insertNote(
    mt: MidiTrack, start: Long, duration: Long, channel: Int,
    pitch: Int, velOn: Int, velOff: Int
) {
    val on = NoteOn(start, channel, pitch, velOn)
    val off = NoteOff(start + duration, channel, pitch, velOff)
    mt.insertEvent(on)
    mt.insertEvent(off)
}
 fun addAttackDelayToTrack(mt: MidiTrack, start: Long, channel: Int, attackDelay: Int){
    val attackAmount = Controller(start,channel,73, attackDelay)
    mt.insertEvent(attackAmount)
}
fun addVibratoToTrack(mt: MidiTrack, start: Long, duration: Long, channel: Int, vibratoDivisor: Int){
    if(duration > 5) {
        val nVibrations = (duration / vibratoDivisor).toInt() // 4 vibrations in a quarter
        if(nVibrations == 0 || duration in 6..12) { // add just one vibration if possible
            val expressionOn = Controller(start + duration / 3,channel,1, 0b1111111)
            val expressionOff = Controller(start + duration - 2,channel,1, 0)
            mt.insertEvent(expressionOn)
            mt.insertEvent(expressionOff)
        } else {
            val vibrationDur = duration / nVibrations
            val vibrationHalfDur = vibrationDur / 2
            val vibrationQuarterDur = vibrationDur / 4
            (0 until nVibrations).forEach(){
                val expressionMiddle1 = Controller(start + vibrationDur * it + vibrationQuarterDur , channel,1, 64)
                val expressionOn = Controller(start + vibrationDur * it + vibrationHalfDur , channel,1, 0b1111111)
                val expressionMiddle2 = Controller(start + vibrationDur * it + vibrationHalfDur + vibrationQuarterDur , channel,1, 64)
                val expressionOff = Controller(start +  vibrationDur * (it + 1) ,channel,1, 0)
                mt.insertEvent(expressionMiddle1)
                mt.insertEvent(expressionOn)
                mt.insertEvent(expressionMiddle2)
                mt.insertEvent(expressionOff)
            }
        }
    }
}
fun octaveTest(mt: MidiTrack): MidiTrack {
    val pitches = listOf(60,72,83, 84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,96,84,72,60, 55, 48,36,24,23,22,21)
    //val pitches = listOf(60,72,84,60, 55, 48,36,24,23,22,21)
    var tick = 0L
    pitches.forEach {
        val pc: MidiEvent = ProgramChange(tick, 0, REVERSE_CYMBALS) // cambia strumento
        mt.insertEvent(pc)
        val on = NoteOn(tick, 0, it, 100)
        val off = NoteOff(tick + 480 - 1, 0, it, 80)
        mt.insertEvent(on)
        mt.insertEvent(off)
        tick += 480
    }
    return mt
}
//fun pitchBenderTest(mt: MidiTrack): MidiTrack {
//    val pitches2m = listOf(61, 60, 59, 61, 60, 59)
//    val noBend = 8192
//    val ht = 4096
//    val halfToneUp = noBend + ht
//    val halfToneDown = noBend - ht
//    val toneUp = noBend + ht * 2 - 1
//    val toneDown = noBend + ht * 2
//    val durs = listOf(480, 480, 480, 480, 480, 480)
//    val glissAmounts = listOf(noBend, noBend, noBend, toneDown, toneDown, noBend)
//    // var lastGliss = false
//    val pitches = pitches2m
//    var tick = 0L
//    (0 until 3).forEach {
//        val on = NoteOn(tick, 0, pitches[it], 100)
//        val off = NoteOff(tick + durs[it] - 1, 0, pitches[it], 80)
//        mt.insertEvent(on)
//        mt.insertEvent(off)
//        tick += durs[it]
//    }
//    (3 until 6).forEach {
//        val pitchBendOff = PitchBend(tick, 0, 0, 0)
//        pitchBendOff.bendAmount = noBend
//        mt.insertEvent(pitchBendOff)
//
//        if (it != 5) {
//            //7168 6144 5120 4096
//            val pitchBendOn1 = PitchBend(tick + 120, 0, 0, 0)
//            pitchBendOn1.bendAmount = 7168
//            val pitchBendOn2 = PitchBend(tick + 240, 0, 0, 0)
//            pitchBendOn2.bendAmount = 6144
//            val pitchBendOn3 = PitchBend(tick + 360, 0, 0, 0)
//            pitchBendOn3.bendAmount = 5120
//            val pitchBendOn4 = PitchBend(tick + 480 - 1, 0, 0, 0)
//            pitchBendOn4.bendAmount = 4096
//            mt.insertEvent(pitchBendOn1)
//            mt.insertEvent(pitchBendOn2)
//            mt.insertEvent(pitchBendOn3)
//            mt.insertEvent(pitchBendOn4)
//        }
//
//
//        val on = NoteOn(tick, 0, pitches[it], 100)
//        val off = NoteOff(tick + durs[it] - 1, 0, pitches[it], 80)
//        mt.insertEvent(on)
//        mt.insertEvent(off)
//        tick += durs[it]
//    }
//    return mt
//}
package com.cristianovecchi.mikrokanon.midi

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.AIMUSIC.JazzChord.*
import com.cristianovecchi.mikrokanon.convertDodecabyteToInts
import com.leff.midi.MidiTrack
import com.leff.midi.event.MidiEvent
import com.leff.midi.event.ProgramChange

fun foundAbsPitchesAndInitialize(barGroup: List<Bar>, harmonizationType: HarmonizationType,
chordsTrack: MidiTrack, chordsChannel: Int, chordsInstrument: Int): List<List<Int>> {
    barGroup.findChordSequence(harmonizationType)
    val absPitches: List<List<Int>> = barGroup.extractAbsPitchesFromDodecaBytes(harmonizationType)
    chordsTrack.initializeChordTrack(barGroup[0].tick, chordsChannel, chordsInstrument)
    return absPitches
}
fun addHarmonizationsToTrack(chordsTrack: MidiTrack, barGroups: List<List<Bar>>, harmonizations: List<HarmonizationData>, justVoicing: Boolean){
    barGroups.forEachIndexed{ index, barGroup ->
        val harmonizationData = harmonizations[index]
        val (type, instrument, volume, style, _, direction) = harmonizationData
        val octaves = harmonizationData.convertFromOctavesByte()
        val chordsChannel = 15
        if (type != HarmonizationType.NONE){
            val diffChordVelocity = 40 - (volume * 40).toInt()  // 1f = 0, 0f = 40
           // println("diff: $diffChordVelocity")
            when(style){
                HarmonizationStyle.CHORDS -> when (type){
                    HarmonizationType.POP -> createPopChordsTrack(chordsTrack, barGroup, false, instrument, diffChordVelocity, justVoicing, octaves)
                    HarmonizationType.POP7 -> createPopChordsTrack(chordsTrack, barGroup, true,instrument, diffChordVelocity, justVoicing, octaves)
                    HarmonizationType.JAZZ -> createJazzChordsTrack(chordsTrack, barGroup, false, instrument, diffChordVelocity, justVoicing, octaves)
                    HarmonizationType.JAZZ11 -> createJazzChordsTrack(chordsTrack, barGroup, true, instrument, diffChordVelocity, justVoicing, octaves)
                    HarmonizationType.XWH -> createExtendedWeightedHarmonyTrack(chordsTrack, barGroup, instrument,  diffChordVelocity, justVoicing, octaves)
                    HarmonizationType.FULL12 -> createFull12HarmonizedTrack(chordsTrack, barGroup, instrument,  diffChordVelocity, octaves)
                    else -> {}
                }

                HarmonizationStyle.DRAMMATICO, HarmonizationStyle.RIBATTUTO, HarmonizationStyle.RIBATTUTO_3,
                HarmonizationStyle.TREMOLO, HarmonizationStyle.TREMOLO_5, HarmonizationStyle.TREMOLO_6 -> {
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createRibattuto(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing)
                }
                HarmonizationStyle.SINCOPATO -> {
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createSincopato(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                }
                HarmonizationStyle.ALBERTI -> {
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createAlberti(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                }
                HarmonizationStyle.RICAMATO, HarmonizationStyle.RICAMATO_6, HarmonizationStyle.RICAMATO_8 -> {
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createRicamato(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                }
                HarmonizationStyle.TRILLO -> {
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createTrillo(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing)
                }
                HarmonizationStyle.ARPEGGIO ->{
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createArpeggio(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                }
                HarmonizationStyle.LINE, HarmonizationStyle.FLOW -> {
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createNoteLine(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                }
                HarmonizationStyle.BICINIUM -> {
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createNoteDoubleLine(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                }
            }

        }
    }
}

fun MidiTrack.initializeChordTrack(startTick:Long, chordsChannel: Int, chordsInstrument: Int){
    val pc: MidiEvent = ProgramChange(startTick, chordsChannel, chordsInstrument) // cambia strumento
    this.insertEvent(pc)
}

fun List<Bar>.extractAbsPitchesFromDodecaBytes(type: HarmonizationType): List<List<Int>> {
    return when (type){
        HarmonizationType.XWH -> {
            this.map { bar ->
                val root = bar.chord1!!.root
                val pitches = Insieme.absPitchesFromDodecaByte(bar.dodecaByte1stHalf!!).toMutableList()
                if(!pitches.contains(root)){pitches.add(root)}
                pitches.sorted()
//                    .also{
//                        println(it)
//                    }
            }
        }
        HarmonizationType.FULL12 -> {
            this.map { bar ->
                Insieme.absPitchesFromDodecaByte(bar.dodecaByte1stHalf!! xor 0B111111111111 ).toList()
//                .also{
//                    println("${bar.dodecaByte1stHalf!!.toString(2)} -> ${(bar.dodecaByte1stHalf!! xor 0B111111111111).toString(2)}")
//                }
            }
        }
        else -> {
            this.map { bar -> bar.extractChordAbsPitches().sorted() }
        }
    }
}

//private fun Int.selectAbsPitchesFromDodecaByte(type: HarmonizationType): List<Int> {
//    val pitches = Insieme.absPitchesFromDodecaByte(this)
//    val sortedPitches = when(type){
//        HarmonizationType.FULL12, HarmonizationType.NONE -> pitches
//        else ->
//    }
//}

fun ArrayList<MidiTrack>.addChordTrack(harmonizations: List<HarmonizationData>, bars: List<Bar>,
                                       trackData: List<TrackData>, audio8D: List<Int>,
                                       totalLength: Long, justVoicing: Boolean){
    if(harmonizations.isNotEmpty() && !harmonizations.all { it.type == HarmonizationType.NONE }) {
        val doubledBars = bars.mergeOnesInMetro()
            .resizeLastBar(totalLength)
            .splitBarsInTwoParts()
        // using trackDatas without replacing for a better chord definition
        assignDodecaBytesToBars(doubledBars.toTypedArray(), trackData, false)
        val barGroups = if(harmonizations.size == 1) listOf(doubledBars)
        else doubledBars.splitBarsInGroups(harmonizations.size)
        val chordsTrack = MidiTrack()
        addHarmonizationsToTrack(chordsTrack, barGroups, harmonizations, justVoicing)
        if(audio8D.isNotEmpty()){
            setAudio8D(chordsTrack, 12, 15)
        }
        this.add(chordsTrack)
    }
}
fun createFull12HarmonizedTrack(chordsTrack: MidiTrack, bars: List<Bar>, instrument: Int, diffChordVelocity: Int, octaves: List<Int>){
    data class Note(val pitch: Int, val tick: Long, var duration: Long, val velocity: Int)
    val notes = mutableListOf<Note>()
    val actualOctaves = octaves.map{ it +1 }
    for (absPitch in 0..11) {
        val contains = BooleanArray(bars.size) { false }
        bars.forEachIndexed { index, bar ->
            //println("Bar$index = ${bar.dodecaByte1stHalf!!.toString(2)}")
            if (!convertDodecabyteToInts(bar.dodecaByte1stHalf!!).contains(absPitch)) contains[index] = true
        }
        //println("Pitch: $absPitch ${contains.contentToString()}")
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
    val chordsChannel = 15
    val pc: MidiEvent = ProgramChange(bars[0].tick, chordsChannel, instrument) // cambia strumento
    chordsTrack.insertEvent(pc)
//    println("${bars.map{ convertFlagsToInts(it.dodecaByte1stHalf!!)}}")
//    println("XWH roots: $roots")
    notes.sortedBy { it.tick }.forEach {
        //print("Chord note: ${it.pitch}, ${it.tick}, ${it.duration}")
        val absPitch = it.pitch
        val tick = it.tick
        val duration = it.duration
        actualOctaves.forEach { octave ->
            Player.insertNoteWithGlissando(
                chordsTrack, tick, duration, chordsChannel,
                octave * 12 + absPitch, it.velocity - diffChordVelocity, 70, 0
            )
        }
    }
}
fun createExtendedWeightedHarmonyTrack(chordsTrack: MidiTrack, bars: List<Bar>, instrument: Int,
                                       diffChordVelocity: Int, justVoicing: Boolean, octaves: List<Int>){
    val priority = intArrayOf(5, 8, 2, 10, 6, 4, 1, 11, 9, 7, 3, 0)
    var lastRoot = (Insieme.trovaFond(bars[0].dodecaByte1stHalf!!)[0] - priority[0] + 12) % 12
    val roots = mutableListOf<Int>()
    bars.forEachIndexed { i, bar ->
        ///val ewhChords = selectChordArea(previousChord)
        //println("Bar $i: ${bar.dodecaByte1stHalf!!.toString(2)}")
        val bools = HarmonyEye.selNotesFrom12Byte(bar.dodecaByte1stHalf!!)//.apply {
            //println(this.contentToString()) }

        val harmonyResults = (0..11).map{
            val boolsWithRoot = bools.reversedArray()
            boolsWithRoot[it] = true
            HarmonyEye.findHarmonyResult(boolsWithRoot)
                .apply {
                this.dodecaByte = bar.dodecaByte1stHalf!! or (1 shl it)}
        }
//        harmonyResults.forEach{
//            println("HarResult ${it.roots.contentToString()} ${it.weight} ${it.dodecaByte.toString(2)}")
//        }
        val sortedHarmonyResults = harmonyResults.sortedBy { it.weight }
        val priorityTransposed = priority.map{ (it + lastRoot) % 12}
        rootSearch@ for( priorityTr in priorityTransposed){
            for(result in sortedHarmonyResults){
                if (result.roots.contains(priorityTr)){
                    roots.add(priorityTr)
                    lastRoot = priorityTr
                    bar.dodecaByte1stHalf = result.dodecaByte
                    break@rootSearch
                }
            }
        }
    }
    val chordsChannel = 15

    val pc: MidiEvent = ProgramChange(bars[0].tick, chordsChannel, instrument) // cambia strumento
    chordsTrack.insertEvent(pc)

//    println("${bars.map{ convertFlagsToInts(it.dodecaByte1stHalf!!)}}")
//    println("XWH roots: $roots")
    findExtendedWeightedHarmonyNotes(chordsTrack, chordsChannel, bars, roots, diffChordVelocity, diffChordVelocity / 2, justVoicing, octaves)
}
fun createPopChordsTrack(chordsTrack: MidiTrack, bars: List<Bar>, with7: Boolean = true, instrument: Int,
                         diffChordVelocity: Int, justVoicing: Boolean, octaves: List<Int>){
    var priority = priorityFrom2and5Just7 // assuming a dominant chord previously
    var lastRoot = (Insieme.trovaFond(bars[0].dodecaByte1stHalf!!)[0] - priority[0] + 12) % 12
    var previousChord = JazzChord.EMPTY
    val selectChordArea = if(with7) { previousChord: JazzChord -> JazzChord.selectChordArea_just_7(previousChord)}
    else {previousChord:JazzChord -> JazzChord.selectChordArea_no_7(previousChord)}
    //println("start root = $lastRoot")
    bars.forEach {
        val jazzChords = selectChordArea(previousChord)
        val chordFaultsGrid =  it.findChordFaultsGrid(jazzChords)
        priority = JazzChord.findRootMovementPriorityJust7(previousChord)
        val chordPosition = chordFaultsGrid.findBestChordPosition(lastRoot, priority)

        val chord = Chord(chordPosition.first, jazzChords[chordPosition.second])
        it.chord1 = chord
        lastRoot = chordPosition.first
        previousChord = chord.chord
        //println("Chord: ${it.dodecaByte1stHalf!!.toString(2)} ${chord.name} ${chord.absoluteNotes.contentToString()}")
    }
    val chordsChannel = 15
    val pc: MidiEvent = ProgramChange(bars[0].tick, chordsChannel, instrument) // cambia strumento
    chordsTrack.insertEvent(pc)
    findChordNotes(chordsTrack, chordsChannel, bars, diffChordVelocity, diffChordVelocity / 2, justVoicing, octaves)
}

fun createJazzChordsTrack(chordsTrack: MidiTrack, bars: List<Bar>, with11: Boolean = true, instrument: Int,
                          diffChordVelocity: Int, justVoicing: Boolean, octaves: List<Int>){
    var priority = priorityFrom2and5 // assuming a dominant chord previously
    var lastRoot = (Insieme.trovaFond(bars[0].dodecaByte1stHalf!!)[0] - priority[0] + 12) % 12
    var previousChord = JazzChord.EMPTY
    //println("start root = $lastRoot")
    val selectChordArea = if(with11) { prevChord: JazzChord -> JazzChord.selectChordArea_11(previousChord)}
         else {prevChord:JazzChord -> JazzChord.selectChordArea_no_11(previousChord)}
    bars.forEach {
        val jazzChords = selectChordArea(previousChord)
        val chordFaultsGrid =  it.findChordFaultsGrid(jazzChords)
        priority = JazzChord.findRootMovementPriority(previousChord)
        val chordPosition = chordFaultsGrid.findBestChordPosition(lastRoot, priority)

        val chord = Chord(chordPosition.first, jazzChords[chordPosition.second])
        it.chord1 = chord
        lastRoot = chordPosition.first
        previousChord = chord.chord
//        println("Chord: ${it.dodecaByte1stHalf!!.toString(2)} ${chord.name} ${chord.absoluteNotes.contentToString()}")
    }

    val chordsChannel = 15
    val pc: MidiEvent = ProgramChange(bars[0].tick, chordsChannel, instrument) // cambia strumento

    chordsTrack.insertEvent(pc)
    findChordNotes(chordsTrack, chordsChannel, bars, diffChordVelocity, diffChordVelocity / 2, justVoicing, octaves)
}
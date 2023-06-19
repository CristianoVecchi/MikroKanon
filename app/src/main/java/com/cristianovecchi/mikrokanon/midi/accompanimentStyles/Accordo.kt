package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.convertDodecabyteToInts
import com.cristianovecchi.mikrokanon.midi.Player
import com.leff.midi.MidiTrack
import com.leff.midi.event.MidiEvent
import com.leff.midi.event.ProgramChange

fun findExtendedWeightedHarmonyNotes(chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, roots: MutableList<Int>,
                                     diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, octaves: List<Int>) {
    data class Note(val pitch: Int, val tick: Long, var duration: Long, val velocity: Int)
    val notes = mutableListOf<Note>()
    val rootNotes = mutableListOf<Note>()
    val actualOctavePitches = octaves.map{ (it +1) * 12 }
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
        actualOctavePitches.forEach { octave ->
            Player.insertNoteWithGlissando(
                chordsTrack, tick, duration, chordsChannel,
                octave + absPitch, velocity, 70, 0
            )
        }
    }
    val addBassesTo2ndOctave = !actualOctavePitches.contains(24)
    val addBassesTo3rdOctave = !actualOctavePitches.contains(36)
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

fun findChordNotes(chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>,
                   diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, octaves: List<Int>) {
    data class Note(val pitch: Int, val tick: Long, var duration: Long, val velocity: Int)
    val actualOctavePitches = octaves.map{ (it +1) * 12 }
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
        actualOctavePitches.forEach{ octave ->
            Player.insertNoteWithGlissando(
                chordsTrack, tick, duration, chordsChannel,
                octave + absPitch, velocity, 70, 0
            )
        }
    }
    val addBassesTo2ndOctave = !actualOctavePitches.contains(24)
    val addBassesTo3rdOctave = !actualOctavePitches.contains(36)
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
    var priority = JazzChord.priorityFrom2and5Just7 // assuming a dominant chord previously
    var lastRoot = (Insieme.trovaFond(bars[0].dodecaByte1stHalf!!)[0] - priority[0] + 12) % 12
    var previousChord = JazzChord.EMPTY
    val selectChordArea = if(with7) { previousChord: JazzChord -> JazzChord.selectChordArea_just_7(previousChord)}
    else {previousChord: JazzChord -> JazzChord.selectChordArea_no_7(previousChord)}
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
    var priority = JazzChord.priorityFrom2and5 // assuming a dominant chord previously
    var lastRoot = (Insieme.trovaFond(bars[0].dodecaByte1stHalf!!)[0] - priority[0] + 12) % 12
    var previousChord = JazzChord.EMPTY
    //println("start root = $lastRoot")
    val selectChordArea = if(with11) { prevChord: JazzChord -> JazzChord.selectChordArea_11(previousChord)}
    else {prevChord: JazzChord -> JazzChord.selectChordArea_no_11(previousChord)}
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

package com.cristianovecchi.mikrokanon.midi

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.AIMUSIC.JazzChord.priorityFrom2and5
import com.cristianovecchi.mikrokanon.AIMUSIC.JazzChord.selectChordArea_just_7
import com.cristianovecchi.mikrokanon.convertDodecabyteToInts
import com.cristianovecchi.mikrokanon.convertFlagsToInts
import com.leff.midi.MidiTrack
import com.leff.midi.event.MidiEvent
import com.leff.midi.event.ProgramChange

enum class HarmonizationType(val title: String) {
    NONE("No Harm."), POP("POP"), JAZZ("JAZZ"), JAZZ11("JAZZ 11"),
    XWH("XW HARMONY"), FULL12("FULL 12")
}
val starredChordsInstruments = listOf(
    STRING_ORCHESTRA, HAMMOND_ORGAN, ACCORDION,
    TREMOLO_STRINGS,
    SYNTH_STRINGS_1, SYN_FANTASIA, BRASS_ENSEMBLE,
    62,63, 52, 53,54,
    //80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,99,100,101,102,103
)
val chordsInstruments = (0..103).toList()
data class HarmonizationData(val type: HarmonizationType = HarmonizationType.NONE,
                             val instrument: Int = 48, val volume: Float = 0.1f){
    fun describe(): String {
        return if(type == HarmonizationType.NONE) "  ---  ${this.type.title}  ---"
             else "  ---  ${this.type.title}  ---\n${ListaStrumenti.getNameByIndex(this.instrument)} ${String.format("%.0f%%",this.volume*100)}"
    }
    fun toCsv(): String {
        return "${this.type.ordinal}|${this.instrument}|${this.volume}"
    }
    companion object{
        fun createHarmonizationsFromCsv(csv: String): List<HarmonizationData>{
            if(csv.isBlank()) return listOf()
            val values = csv.split(",")
            val harmValues = HarmonizationType.values()
            return values.map{
                val subValues = it.split("|")
                HarmonizationData(harmValues[subValues[0].toInt()], subValues[1].toInt(), subValues[2].toFloat())
            }
        }
    }
}

fun addHarmonizationsToTrack(chordsTrack: MidiTrack, barGroups: List<List<Bar>>, harmonizations: List<HarmonizationData>){
    barGroups.forEachIndexed{ index, barGroup ->
        val harmonizationData = harmonizations[index]
        if (harmonizationData.type != HarmonizationType.NONE){
            val diffChordVelocity = 40 - (harmonizationData.volume * 40).toInt()  // 1f = 0, 0f = 40
           // println("diff: $diffChordVelocity")
            val chordsInstrument = harmonizationData.instrument
            when (harmonizationData.type){
                HarmonizationType.NONE -> Unit
                HarmonizationType.POP -> createPopChordsTrack(chordsTrack, barGroup, chordsInstrument, diffChordVelocity)
                HarmonizationType.JAZZ -> createJazzChordsTrack(chordsTrack, barGroup, false, chordsInstrument, diffChordVelocity)
                HarmonizationType.JAZZ11 -> createJazzChordsTrack(chordsTrack, barGroup, true, chordsInstrument, diffChordVelocity)
                HarmonizationType.XWH -> createExtendedWeightedHarmonyTrack(chordsTrack, barGroup, chordsInstrument,  diffChordVelocity)
                HarmonizationType.FULL12 -> createFull12HarmonizedTrack(chordsTrack, barGroup, chordsInstrument,  diffChordVelocity)
            }
        }
    }
}
fun createFull12HarmonizedTrack(chordsTrack: MidiTrack, bars: List<Bar>, instrument: Int, diffChordVelocity: Int){
    data class Note(val pitch: Int, val tick: Long, var duration: Long, val velocity: Int)
    val notes = mutableListOf<Note>()
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
        for (octave in 2..8) {
            Player.insertNoteWithGlissando(
                chordsTrack, tick, duration, chordsChannel,
                octave * 12 + absPitch, it.velocity - diffChordVelocity, 70, 0
            )
        }
    }
}
fun createExtendedWeightedHarmonyTrack(chordsTrack: MidiTrack, bars: List<Bar>, instrument: Int, diffChordVelocity: Int){
    val priority = listOf(5, 8, 2, 10, 6, 4, 1, 11, 9, 7, 3, 0).toIntArray()
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
    findExtendedWeightedHarmonyNotes(chordsTrack, chordsChannel, bars, roots, diffChordVelocity, diffChordVelocity / 2)
}
fun createPopChordsTrack(chordsTrack: MidiTrack, bars: List<Bar>, instrument: Int, diffChordVelocity: Int){
    var priority = priorityFrom2and5 // assuming a dominant chord previously
    var lastRoot = (Insieme.trovaFond(bars[0].dodecaByte1stHalf!!)[0] - priority[0] + 12) % 12
    var previousChord = JazzChord.EMPTY
    //println("start root = $lastRoot")
    bars.forEach {
        val jazzChords = selectChordArea_just_7(previousChord)
        val chordFaultsGrid =  it.findChordFaultsGrid(jazzChords)
        priority = JazzChord.findRootMovementPriorityJust7(previousChord)
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
    findChordNotes(chordsTrack, chordsChannel, bars, diffChordVelocity, diffChordVelocity / 2)
}

fun createJazzChordsTrack(chordsTrack: MidiTrack, bars: List<Bar>, with11: Boolean = true, instrument: Int, diffChordVelocity: Int){
    var priority = priorityFrom2and5 // assuming a dominant chord previously
    var lastRoot = (Insieme.trovaFond(bars[0].dodecaByte1stHalf!!)[0] - priority[0] + 12) % 12
    var previousChord = JazzChord.EMPTY
    //println("start root = $lastRoot")
    val selectChordArea = if(with11) { previousChord: JazzChord -> JazzChord.selectChordArea_11(previousChord)}
         else {previousChord:JazzChord -> JazzChord.selectChordArea_no_11(previousChord)}
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
    findChordNotes(chordsTrack, chordsChannel, bars, diffChordVelocity, diffChordVelocity / 2)
}
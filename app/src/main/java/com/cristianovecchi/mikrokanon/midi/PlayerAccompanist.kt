package com.cristianovecchi.mikrokanon.midi

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.convertFlagsToInts
import com.leff.midi.MidiTrack
import com.leff.midi.event.MidiEvent
import com.leff.midi.event.ProgramChange
fun createExtendedWeightedHarmonyTrack(bars: List<Bar>): MidiTrack {
    val priority = listOf(0, 5, 8, 2, 10, 6, 4, 1, 11, 9, 7, 3).toIntArray()
    var lastRoot = (Insieme.trovaFond(bars[0].dodecaByte1stHalf!!)[0] - priority[0] + 12) % 12
    var previousChord = 0
    val chordBytes = mutableListOf<Int>()
    val roots = mutableListOf<Int>()
    bars.forEach { bar ->
        ///val ewhChords = selectChordArea(previousChord)
        val bools = HarmonyEye.selNotesFrom12Byte(bar.dodecaByte1stHalf!!)
        val harmonyResults = (0..11).map{
            val boolsWithRoot = bools.clone()
            boolsWithRoot[it] = true
            HarmonyEye.findHarmonyResult(boolsWithRoot).apply {
                this.dodecaByte = bar.dodecaByte1stHalf!! or (1 shl it)
            }
        }
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
    val chordsTrack = MidiTrack()
    val chordsChannel = 15
    val randomInstrument = listOf(
        STRING_ORCHESTRA, SYN_BRASS_AND_LEAD, CHURCH_ORGAN, ACCORDION,
        CRYSTAL, BLUES_ORGAN, ELECTRIC_PIANO_2, TREMOLO_STRINGS, MUTED_TRUMPET,
        SYNTH_STRINGS_1, SYN_FANTASIA, VOICE_OOHS, FRENCH_HORN, BRASS_ENSEMBLE
    )
    val pc: MidiEvent = ProgramChange(0L, chordsChannel, randomInstrument.shuffled()[0]) // cambia strumento

    chordsTrack.insertEvent(pc)
    println("${bars.map{ convertFlagsToInts(it.dodecaByte1stHalf!!)}}")
    println(roots)
    findExtendedWeightedHarmonyNotes(chordsTrack, chordsChannel, bars, roots, 15, 8)
    return chordsTrack
}


fun createJazzChordsTrack(bars: List<Bar>, with11: Boolean = true): MidiTrack{
    val priority = listOf(5,11, 10,4, 3,9, 8,2, 1,7, 6,0).toIntArray()
    var lastRoot = (Insieme.trovaFond(bars[0].dodecaByte1stHalf!!)[0] - priority[0] + 12) % 12
    var previousChord = JazzChord.EMPTY
    //println("start root = $lastRoot")
    val selectChordArea = { previousChord: JazzChord ->
        if (with11) JazzChord.selectChordArea_11(previousChord) else JazzChord.selectChordArea_no_11(previousChord)
    }
    bars.forEach {
        val jazzChords = selectChordArea(previousChord)
        val chordFaultsGrid =  it.findChordFaultsGrid(jazzChords)
        val priority = JazzChord.findRootMovementPriority(previousChord)
        val chordPosition = chordFaultsGrid.findBestChordPosition(lastRoot, priority)

        val chord = Chord(chordPosition.first, jazzChords[chordPosition.second])
        it.chord1 = chord
        lastRoot = chordPosition.first
        previousChord = chord.chord
        println("Chord: ${it.dodecaByte1stHalf!!.toString(2)} ${chord.name} ${chord.absoluteNotes.contentToString()}")
    }
    val chordsTrack = MidiTrack()
    val chordsChannel = 15
    val randomInstrument = listOf(
        STRING_ORCHESTRA, SYN_BRASS_AND_LEAD, CHURCH_ORGAN, ACCORDION,
        CRYSTAL, BLUES_ORGAN, ELECTRIC_PIANO_2, TREMOLO_STRINGS, MUTED_TRUMPET,
        SYNTH_STRINGS_1, SYN_FANTASIA, VOICE_OOHS, FRENCH_HORN, BRASS_ENSEMBLE
    )
    val pc: MidiEvent = ProgramChange(0L, chordsChannel, randomInstrument.shuffled()[0]) // cambia strumento

    chordsTrack.insertEvent(pc)
    findChordNotes(chordsTrack, chordsChannel, bars, 15, 8)
    return chordsTrack
}
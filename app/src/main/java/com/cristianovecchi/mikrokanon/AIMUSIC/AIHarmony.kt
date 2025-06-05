package com.cristianovecchi.mikrokanon.AIMUSIC


import com.cristianovecchi.mikrokanon.AIMUSIC.JazzChord.extractJustFifthChord
import com.cristianovecchi.mikrokanon.AIMUSIC.JazzChord.extractRootChord
import com.leff.midi.MidiTrack
import com.leff.midi.event.MidiEvent
import com.leff.midi.event.ProgramChange
fun Int.findChordFaultsGrid(jazzChords: Array<JazzChord>): Array<IntArray>{
    val jazzChordBytes = jazzChords.map { it.dbyte }
    val chordFaultsGrid = Array(12) {IntArray(jazzChordBytes.size)}
    this.let{
        jazzChordBytes.forEachIndexed{ jazzChordIndex, jazzChordByte ->
            var transposedJazzByte = jazzChordByte
            for(transposition in 0 until 12){
                chordFaultsGrid[transposition][jazzChordIndex] = ((this xor transposedJazzByte) and this).countOneBits()
                transposedJazzByte = Insieme.trasponiDiUno(transposedJazzByte)
            }
        }
    }
    return chordFaultsGrid
}
fun createXWHChordsSequence(dodecaBytes: IntArray): Pair<Array<IntArray>,IntArray>{
    val chords = Array(dodecaBytes.size) { intArrayOf() }
    val roots = mutableListOf<Int>()
    if(dodecaBytes.isEmpty()) return chords to roots.toIntArray()
    val priority = intArrayOf(5, 8, 2, 10, 6, 4, 1, 11, 9, 7, 3, 0)
    var lastRoot = (Insieme.trovaFond(dodecaBytes[0])[0] - priority[0] + 12) % 12
    dodecaBytes.forEachIndexed { index, dodecaByte ->
        val bools = HarmonyEye.selNotesFrom12Byte(dodecaByte)
        val harmonyResults = (0..11).map{
            val boolsWithRoot = bools.reversedArray()
            boolsWithRoot[it] = true
            HarmonyEye.findHarmonyResult(boolsWithRoot)
                .apply {
                    this.dodecaByte = dodecaByte or (1 shl it)}
        }
        val sortedHarmonyResults = harmonyResults.sortedBy { it.weight }
        val priorityTransposed = priority.map{ (it + lastRoot) % 12}
        rootSearch@ for( priorityTr in priorityTransposed){
            for(result in sortedHarmonyResults){
                if (result.roots.contains(priorityTr)){
                    roots.add(priorityTr)
                    lastRoot = priorityTr
                    chords[index] = Insieme.absPitchesFromDodecaByte(result.dodecaByte).toIntArray()
                    break@rootSearch
                }
            }
        }
    }
    return chords to roots.toIntArray()
}
fun createPopJazzChordsSequence(dodecaBytes: IntArray, harmony: HarmonizationType, with7: Boolean = true, with11: Boolean = true): Pair<Array<IntArray>,IntArray>{ // chord notes+ root
    val chords = Array(dodecaBytes.size) { intArrayOf() }
    val roots = IntArray(dodecaBytes.size)
    if(dodecaBytes.isEmpty()) return chords to roots
    var priority = JazzChord.priorityFrom2and5Just7 // assuming a dominant chord previously
    var lastRoot = (Insieme.trovaFond(dodecaBytes[0])[0] - priority[0] + 12) % 12
    var previousChord = JazzChord.EMPTY
    val selectChordArea = when (harmony){
        HarmonizationType.ROOTS, HarmonizationType.ORGANUM, HarmonizationType.POP, HarmonizationType.POP7 -> {
            if(with7) { prevChord: JazzChord -> JazzChord.selectChordArea_just_7(prevChord)}
            else {prevChord:JazzChord -> JazzChord.selectChordArea_no_7(prevChord)}
        }
        HarmonizationType.LIBERTY -> {
            { prevChord: JazzChord -> JazzChord.selectChordAreaJust9(prevChord)}
        }
        HarmonizationType.JAZZ, HarmonizationType.JAZZ11 -> {
            if(with11) { prevChord: JazzChord -> JazzChord.selectChordArea_11(prevChord)}
            else {prevChord:JazzChord -> JazzChord.selectChordArea_no_11(prevChord)}
        }
        else -> {
            if(with7) { prevChord: JazzChord -> JazzChord.selectChordArea_just_7(prevChord)}
            else {prevChord:JazzChord -> JazzChord.selectChordArea_no_7(prevChord)}
        }
    }
    //println("start root = $lastRoot")
    dodecaBytes.forEachIndexed { index, dodecaByte ->
        val jazzChords = selectChordArea(previousChord)
        val chordFaultsGrid =  dodecaByte.findChordFaultsGrid(jazzChords)
        priority = JazzChord.findRootMovementPriorityJust7(previousChord)
        val chordPosition = chordFaultsGrid.findBestChordPosition(lastRoot, priority)
        val chord = when (harmony){
            HarmonizationType.ROOTS -> extractRootChord(Chord(chordPosition.first, jazzChords[chordPosition.second]))
            HarmonizationType.ORGANUM -> extractJustFifthChord(Chord(chordPosition.first, jazzChords[chordPosition.second]))
            else -> Chord(chordPosition.first, jazzChords[chordPosition.second])
        }
        chords[index] = chord.absoluteNotes
        roots[index] = chord.root
        lastRoot = chordPosition.first
        previousChord = chord.chord
        //println("Chord: ${it.dodecaByte1stHalf!!.toString(2)} ${chord.name} ${chord.absoluteNotes.contentToString()}")
    }
    return chords to roots
}
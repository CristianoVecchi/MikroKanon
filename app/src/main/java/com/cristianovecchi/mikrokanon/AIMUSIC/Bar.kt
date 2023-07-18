package com.cristianovecchi.mikrokanon.AIMUSIC

import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.locale.NoteNamesIt


fun List<Bar>.splitBarsInGroups(nGroups: Int):  List<List<Bar>>{
    if(nGroups >= this.size){
        return this.map{ listOf(it)}//.apply{ println("Size: ${this.size} Division: $this") }
    }
    val result = mutableListOf<List<Bar>>()
    val division = this.size.toLong().divideDistributingRest(nGroups)
    var index = 0
    division.forEach {
        result.add(this.subList(index, index + it.toInt()))
        index += it.toInt()
    }
    //println("Size: ${this.size} Division: $division")
    return result.toList()
}
fun Array<IntArray>.findBestChordPosition(
    lastRoot: Int,
    priority: IntArray = (0..11).toList().toIntArray()): Pair<Int, Int>{ // transposition, JazzChord type
    var maxFaultsNumber = Int.MAX_VALUE
    var result = Pair(0,0)
    val actualPriority = priority.map{ (it + lastRoot) % 12}.toIntArray()
    //println(actualPriority.toList())
    for(i in actualPriority.indices){
        val transposition = actualPriority[i]
        for(j in 0 until this[i].size){
            val nFaults = this[transposition][j]
            if(nFaults < maxFaultsNumber){
                maxFaultsNumber = nFaults
                result = Pair(transposition,j)
            }
        }
    }
    return result
}
fun List<Bar>.splitBarsInTwoParts(): List<Bar>{
    val result = mutableListOf<Bar>()
    for (bar in this) {
        //println("Input "+ bar)
        val (numerator, denominator) = bar.metro
        val quantumDur = RhythmPatterns.denominatorMidiValue(denominator).toLong()
        if(bar.duration < quantumDur * numerator || numerator == 1){ // don't split
            result.add(bar)
        } else {
            val den2nd = numerator / 2
            val den1st = den2nd + numerator % 2
            val duration1st = quantumDur * den1st
            result.add(Bar(Pair(den1st, denominator),bar.tick, duration1st, minVelocity = bar.minVelocity))
            result.add(Bar(Pair(den2nd, denominator),bar.tick + duration1st, quantumDur * den2nd, minVelocity = bar.minVelocity))
        }
    }
    return result.toList()
}
fun List<Bar>.resizeLastBar(totalDuration: Long): List<Bar>{
    val result = mutableListOf<Bar>()
    var indexLastBar = 0
//    this.forEachIndexed{ i, it ->
//        println("$i:${it.tick}-${it.tick+it.duration}, ")
//    }
    for(i in this.indices){
        if(this[i].tick  + this[i].duration >= totalDuration) break
        indexLastBar++
    }
    val realSequence = this.subList(0, indexLastBar)
    val barsDuration = realSequence.sumOf { it.duration.toInt() }
    val originalBarsDuration = this.sumOf { it.duration.toInt() }
    val diff = totalDuration - barsDuration
   // println("Bars duration = $barsDuration Total duration = $totalDuration  Diff = $diff LastBarIndex=$indexLastBar ")
    if(diff == 0L) return realSequence
    result.addAll(realSequence)
    val lastBar = if(totalDuration <= originalBarsDuration) {
        this[indexLastBar]
    } else {
        val metro = RhythmPatterns.createQuarterMetroFromDuration(diff.toInt())
        Bar(metro, barsDuration.toLong(), diff)
    }
    //println("new last bar: $lastBar")
    result.add(lastBar.copy(duration = diff))
    //println("input list size = ${this.size}  output list size = ${result.size}")
    //println(result)
    return result
}
fun List<Bar>.mergeOnesInMetro(): List<Bar>{
    val result = mutableListOf<Bar>()
    var index = 0
    var lastMetro = Pair(-1,-1)
    var lastBar = Bar(Pair(-1,-1),0L,0L, minVelocity = 0)
    while(index < size){
        val bar = this[index]
        if(bar.metro.first != 1){
            result.add(bar)
            lastMetro = bar.metro
            lastBar = bar
        } else {
            if(lastMetro != bar.metro){
                result.add(bar)
                lastMetro = bar.metro
                lastBar = bar
            } else {
                lastBar.duration += bar.duration
                lastBar.metro = Pair(lastBar.metro.first +1, lastBar.metro.second)
            }
        }
        index++
    }
   // println("mergeOneInMetro: nBars=${this.size} nResults=${result.size}")
    return result.toList()
}


data class Bar(var metro: Pair<Int,Int> = METRO_4_4, val tick: Long, var duration: Long,
               var dodecaByte1stHalf: Int? = null, var dodecaByte2ndHalf: Int? = null,
                var chord1: Chord? = null, var chord2: Chord? = null, var minVelocity: Int? = null){
    fun findChordFaultsGrid(jazzChords: Array<JazzChord>): Array<IntArray>{
        val jazzChordBytes = jazzChords.map { it.dbyte }
        val chordFaultsGrid = Array(12) {IntArray(jazzChordBytes.size)}
        dodecaByte1stHalf?.let{
            jazzChordBytes.forEachIndexed{ jazzChordIndex, jazzChordByte ->
                var transposedJazzByte = jazzChordByte
                for(transposition in 0 until 12){
                    chordFaultsGrid[transposition][jazzChordIndex] = ((dodecaByte1stHalf!! xor transposedJazzByte) and dodecaByte1stHalf!!).countOneBits()
                    transposedJazzByte = Insieme.trasponiDiUno(transposedJazzByte)
                }
            }
        }
        return chordFaultsGrid
    }
    fun extractChordAbsPitches(): List<Int>{
        return if (this.chord1 == null) {
            emptyList()
        } else this.chord1!!.absoluteNotes.toList()
    }
}
fun List<Bar>.getProgressiveVelocities(index: Int, div: Int, diffChordVelocity: Int, increase: Int): List<Int> {
    val velocity = (this[index].minVelocity!! - diffChordVelocity + increase).coerceIn(0, 127)
    //println("bar velocity: ${this[index].minVelocity} diffChordVelocity: $diffChordVelocity  increase: $increase result: $velocity")
    val goalVelocity = (this.getNextBarVelocity(index) - diffChordVelocity + increase).coerceIn(0, 127)
    return accumulateVelocities(div, velocity, goalVelocity - velocity)
}
fun List<Bar>.getNextBarVelocity(index: Int): Int {
    val nextBar = this.getOrNull(index+1)
    return nextBar?.let { it.minVelocity!! } ?: this[index].minVelocity!!
}

fun List<Bar>.findChordSequence(harmonizationType: HarmonizationType){
    if(harmonizationType == HarmonizationType.NONE || harmonizationType == HarmonizationType.FULL12 || this.isEmpty()) return
    var priority = JazzChord.choosePriority(harmonizationType)
    val originalRoots = Insieme.trovaFond(this[0].dodecaByte1stHalf!!)
    var lastRoot = (originalRoots.getOrElse(0){ 0 } - priority[0] + 12) % 12
    val roots = mutableListOf<Int>()
    var previousChord = JazzChord.EMPTY
    when (harmonizationType){
        HarmonizationType.POP, HarmonizationType.POP7 -> {
            val selectChordArea = if(harmonizationType == HarmonizationType.POP7) {
                prevChord: JazzChord -> JazzChord.selectChordArea_just_7(prevChord)}
            else {
                prevChord:JazzChord -> JazzChord.selectChordArea_no_7(prevChord)}
            this.forEach {
                val jazzChords = selectChordArea(previousChord)
                val chordFaultsGrid =  it.findChordFaultsGrid(jazzChords)
                priority = JazzChord.findRootMovementPriorityJust7(previousChord)
                val chordPosition = chordFaultsGrid.findBestChordPosition(lastRoot, priority)
                val chord = Chord(chordPosition.first, jazzChords[chordPosition.second])
                it.chord1 = chord
                lastRoot = chordPosition.first
                previousChord = chord.chord
                //println("Chord: ${it.dodecaByte1stHalf!!.toString(2)} ${chord.name} ${Clip.convertAbsPitchesToClipText(chord.absoluteNotes.toList(), NoteNamesIt.values().map{it.toString()})}")

            }
        }
        HarmonizationType.LIBERTY -> {
            val selectChordArea =
                { prevChord: JazzChord -> JazzChord.selectChordAreaJust9(prevChord)}
            this.forEach {
                val jazzChords = selectChordArea(previousChord)
                val chordFaultsGrid =  it.findChordFaultsGrid(jazzChords)
                priority = JazzChord.findRootMovementPriorityJust9(previousChord)
                val chordPosition = chordFaultsGrid.findBestChordPosition(lastRoot, priority)
                val chord = Chord(chordPosition.first, jazzChords[chordPosition.second])
                it.chord1 = chord
                lastRoot = chordPosition.first
                previousChord = chord.chord
                //println("Chord: ${it.dodecaByte1stHalf!!.toString(2)} ${chord.name} ${Clip.convertAbsPitchesToClipText(chord.absoluteNotes.toList(), NoteNamesIt.values().map{it.toString()})}")

            }
        }
        HarmonizationType.JAZZ, HarmonizationType.JAZZ11 -> {
            val selectChordArea = if(harmonizationType == HarmonizationType.JAZZ11) {
                    prevChord: JazzChord -> JazzChord.selectChordArea_11(prevChord)}
            else {
                    prevChord:JazzChord -> JazzChord.selectChordArea_no_11(prevChord)}
            this.forEach {
                val jazzChords = selectChordArea(previousChord)
                val chordFaultsGrid =  it.findChordFaultsGrid(jazzChords)
                priority = JazzChord.findRootMovementPriority(previousChord)
                val chordPosition = chordFaultsGrid.findBestChordPosition(lastRoot, priority)
                val chord = Chord(chordPosition.first, jazzChords[chordPosition.second])
                it.chord1 = chord
                lastRoot = chordPosition.first
                previousChord = chord.chord
                //println("Chord: ${it.dodecaByte1stHalf!!.toString(2)} ${chord.name} ${Clip.convertAbsPitchesToClipText(chord.absoluteNotes.toList(), NoteNamesIt.values().map{it.toString()})}")
            }
        }
        HarmonizationType.XWH -> {
            this.forEachIndexed { _, bar ->
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
                            bar.chord1 = Chord(priorityTr, JazzChord.EMPTY)
                            break@rootSearch
                        }
                    }
                }
            }
        }
        else -> return
    }
}


package com.cristianovecchi.mikrokanon.AIMUSIC

import com.cristianovecchi.mikrokanon.divideDistributingRest


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
    val diff = totalDuration - realSequence.sumBy { it.duration.toInt() }
//    println("Bar duration = ${realSequence.sumBy { it.duration.toInt() }} Total duration = $totalDuration  Diff = $diff LastBarIndex=$indexLastBar ")
    if(diff == 0L) return realSequence
    result.addAll(realSequence)
    val lastBar = this[indexLastBar]//.apply{println("old last bar = $this")}
    result.add(lastBar.copy(duration = diff))//.apply{println("new last bar = $this")})
//    println("input list size = ${this.size}  output list size = ${result.size}")
    return result.toList()
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
}


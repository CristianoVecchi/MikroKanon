package com.cristianovecchi.mikrokanon.AIMUSIC

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


data class Bar(val metro: Pair<Int,Int> = METRO_4_4, val tick: Long, val duration: Long,
               var dodecaByte1stHalf: Int? = null, var dodecaByte2ndHalf: Int? = null,
                var chord1: Chord? = null, var chord2: Chord? = null){
    fun findChordFaultsGrid(): Array<IntArray>{
        val jazzChordBytes = JazzChord.values().map { it.dbyte }
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


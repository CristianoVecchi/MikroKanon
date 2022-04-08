package com.cristianovecchi.mikrokanon.AIMUSIC

fun Array<IntArray>.findBestChordPosition(): Pair<Int, Int>{ // transposition, JazzChord type
    var maxFaultsNumber = Int.MAX_VALUE
    var result = Pair(0,0)
    for(i in 0 until this.size){
        for(j in 0 until this[i].size){
            val nFaults = this[i][j]
            if(nFaults < maxFaultsNumber){
                maxFaultsNumber = nFaults
                result = Pair(i,j)
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
            var dodecaByte = it
            for(transposition in 0 until 12){
                jazzChordBytes.forEachIndexed{ jazzChordIndex, jazzChordByte ->
                    chordFaultsGrid[transposition][jazzChordIndex] = ((dodecaByte xor jazzChordByte) and dodecaByte).countOneBits()
                }
                dodecaByte = Insieme.trasponiDiUno(dodecaByte)
            }
        }
        return chordFaultsGrid
    }
}


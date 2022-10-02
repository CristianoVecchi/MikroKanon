package com.cristianovecchi.mikrokanon.AIMUSIC

data class Byte128(var lowByte: Int = 0, var byte2nd: Int = 0, var byte3rd: Int = 0,var byte4th: Int = 0){
    fun bitOn(pos: Int){
        when (pos){
            in 0..31 -> {
                lowByte = lowByte or (1 shl pos)
            }
            in 32..63 -> {
                byte2nd = byte2nd or (1 shl (pos-32))
            }
            in 64..95 -> {
                byte3rd = byte3rd or (1 shl (pos-64))
            }
            in 96..127 -> {
                byte4th = byte4th or (1 shl (pos-96))
            }
            else -> Unit
        }
    }
    fun read(pos: Int): Boolean{
        return when (pos){
            in 0..31 -> {
                lowByte and (1 shl pos) != 0
            }
            in 32..63 -> {
                byte2nd and (1 shl (pos-32)) != 0
            }
            in 64..95 -> {
                byte3rd and (1 shl (pos-64)) != 0
            }
            in 96..127 -> {
                byte4th and (1 shl (pos-96)) != 0
            }
            else -> false
        }
    }
    fun display(){
        (127 downTo 0).forEach{
            if (read(it))print(1) else print(0)
            if(it % 8 == 0) print(" ")
        }
        println()
    }

    fun nRests(length: Int): Int {
        var pos = 0
        var nNotes = 0
        var nRests = 0
        while (nNotes < length){
            if(read(pos)) nNotes++ else nRests++
            pos++
        }
        return nRests
    }

    companion object{
        fun extractByte128(pitches: MutableList<Int>, restValue: Int = -1): Byte128{
            return empty().apply {
                pitches.forEachIndexed(){ i, pitch -> if (pitch != restValue) this.bitOn(i)}
            }
        }
        fun empty(): Byte128{
            return Byte128(0, 0,0,0)
        }
    }
}
//fun main(args : Array<String>){
//    Byte128.empty().apply{
//        bitOn(0)
//        bitOn(8)
//        bitOn(16)
//        bitOn(24)
//        bitOn(32)
//        bitOn(48)
//        bitOn(64)
//        bitOn(126)
//        bitOn(127)
//        display()
//    }
//    //println(Int.MAX_VALUE.toString(2))
//}
//
data class  MikroKanonByte128(val byte: Byte128, val delays: List<Int>, val transpositions: List<Int>,
                              val forms: List<Int>, var length: Int, ) {
    fun toMikroKanon(dux: List<Int>, comites: List<IntArray>, intervalSet: List<Int>): MikroKanon {
        val parts = mutableListOf<MutableList<Int>>()
        val booleans = mutableListOf<Boolean>()
        val duxPart = mutableListOf<Int>()
        var index = 0;
        var pos = 0
        while (index < dux.size) {
            if (byte.read(pos)) {
                duxPart.add(dux[index])
                booleans.add(true)
                index++; pos++
            } else {
                duxPart.add(-1)
                booleans.add(false)
                pos++
            }
        }
        parts.add(duxPart)
        for (n in comites.indices) {
            val comesPart = mutableListOf<Int>()
            val comes = comites[n]
            index = 0;
            (0 until delays[n]).forEach { _ -> comesPart.add(-1) }
            for (bool in booleans) {
                if (bool) {
                    comesPart.add(comes[index])
                    index++
                } else {
                    comesPart.add(-1)
                }
            }
            parts.add(comesPart)
        }
        return MikroKanon(parts.map { AbsPart(it) }.reversed(), intervalSet)
    }
    fun nRests(): Int {
        return byte.nRests(length)
    }
}





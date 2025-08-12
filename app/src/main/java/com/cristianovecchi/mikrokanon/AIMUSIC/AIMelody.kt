package com.cristianovecchi.mikrokanon.AIMUSIC

import kotlin.math.absoluteValue

enum class TREND(val directions: List<Int>) {
    ASCENDANT_DYNAMIC(Insieme.TREND_ASCENDANT_DYNAMIC.toList()),
    DESCENDANT_DYNAMIC(Insieme.TREND_DESCENDANT_DYNAMIC.toList()),
    ASCENDANT_STATIC(Insieme.TREND_ASCENDANT_STATIC.toList()),
    DESCENDANT_STATIC(Insieme.TREND_DESCENDANT_STATIC.toList())
}
fun List<Int>.exchangeNotes(): List<Int>{
    return when (size) {
        0 -> listOf()
        1, 2 -> this
        else -> {
            val result = mutableListOf<Int>()
            result.add(this[0])
            (1 until size step 2).forEach{
                if(it == size -1) result.add(this[it])
                else {
                    result.add(this[it+1])
                    result.add(this[it])
                }
            }
            result//.also { println("Exchange: $this -> $it") }
        }
    }
}
fun List<Int>.getWaveCycling(nNotes: Int): List<Int>{
    return when(this.size){
        1 -> List(nNotes) {this[0]}
        2 -> {
            val (e1, e2) = this
            when (nNotes) {
                1 -> listOf(e1)
                2 -> listOf(e1, e2)
                3 -> listOf(e1, e2, e1)
                4 -> listOf(e1, e2, e1, e2)
                5 -> listOf(e1, e2, e1, e2, e1)
                6 -> listOf(e1, e2, e1, e2, e1, e2)
                7 -> listOf(e1, e2, e1, e2, e1, e2, e1)
                8 -> listOf(e1, e2, e1, e2, e1, e2, e1, e2)
                9 -> listOf(e1, e2, e1, e2, e1, e2, e1, e2, e1)
                10 -> listOf(e1, e2, e1, e2, e1, e2, e1, e2, e1, e2)
                11 -> listOf(e1, e2, e1, e2, e1, e2, e1, e2, e1, e2, e1)
                12 -> listOf(e1, e2, e1, e2, e1, e2, e1, e2, e1, e2, e1, e2)
                13 -> listOf(e1, e2, e1, e2, e1, e2, e1, e2, e1, e2, e1, e2, e1)
                14 -> listOf(e1, e2, e1, e2, e1, e2, e1, e2, e1, e2, e1, e2, e1, e2)
                15 -> listOf(e1, e2, e1, e2, e1, e2, e1, e2, e1, e2, e1, e2, e1, e2, e1)
                16 -> listOf(e1, e2, e1, e2, e1, e2, e1, e2, e1, e2, e1, e2, e1, e2, e1, e2)
                else -> listOf()
            }
        }
        3 -> {
            val (e1, e2, e3) = this
            when (nNotes) {
                1 -> listOf(e1)
                2 -> listOf(e1, e2)
                3 -> listOf(e1, e2, e3)
                4 -> listOf(e1, e2, e3, e2)
                5 -> listOf(e1, e2, e3, e1, e2)
                6 -> listOf(e1, e2, e3, e2, e1, e2)
                7 -> listOf(e1, e2, e3, e2, e1, e2, e3)
                8 -> listOf(e1, e2, e3, e2, e1, e2, e3, e2)
                9 -> listOf(e1, e2, e3, e2, e1, e2, e3, e1, e2)
                10 -> listOf(e1, e2, e3, e2, e1, e2, e3, e2, e1, e2)
                11 -> listOf(e1, e2, e3, e2, e1, e2, e3, e2, e1, e2, e3)
                12 -> listOf(e1, e2, e3, e2, e1, e2, e3, e2, e1, e2, e3, e2)
                13 -> listOf(e1, e2, e3, e2, e1, e2, e3, e2, e1, e2, e3, e1, e2)
                14 -> listOf(e1, e2, e3, e2, e1, e2, e3, e2, e1, e2, e3, e2, e1, e2)
                15 -> listOf(e1, e2, e3, e2, e1, e2, e3, e2, e1, e2, e3, e2, e1, e2, e3)
                16 -> listOf(e1, e2, e3, e2, e1, e2, e3, e2, e1, e2, e3, e2, e1, e2, e3, e2)
                else -> listOf()
            }
        }
        else -> listOf()
    }
}
fun getPairIndices(nNotes: Int): List<Pair<Int,Int>> {
    return when (nNotes) {
        1 -> listOf(Pair(0,0), Pair(0,0), Pair(0,0), Pair(0,0))
        2 -> listOf(Pair(0,1), Pair(1,0), Pair(0,1), Pair(1,0))
        3 -> listOf(Pair(0,1), Pair(1,2), Pair(0,1), Pair(1,2))
        4 -> listOf(Pair(0,1), Pair(1,2), Pair(2,3), Pair(3,2))
        5 -> listOf(Pair(0,1), Pair(1,2), Pair(2,3), Pair(3,4))
        6 -> listOf(Pair(0,1), Pair(1,2), Pair(2,3), Pair(3,4), Pair(4,5))
        7 -> listOf(Pair(0,1), Pair(1,2), Pair(2,3), Pair(3,4), Pair(4,5), Pair(5,6))
        8 -> listOf(Pair(0,1), Pair(1,2), Pair(2,3), Pair(4,5), Pair(5,6), Pair(6,7))
        9 -> listOf(Pair(0,1), Pair(1,2), Pair(3,4), Pair(4,5), Pair(6,7), Pair(7,8))
        10 -> listOf(Pair(0,1), Pair(1,2), Pair(3,4), Pair(4,5), Pair(6,7), Pair(8,9))
        11 -> listOf(Pair(0,1), Pair(1,2), Pair(3,4), Pair(5,6), Pair(7,8), Pair(9,10))
        12 -> listOf(Pair(0,1), Pair(2,3), Pair(4,5), Pair(6,7), Pair(8,9), Pair(10,11))
        else -> listOf()
    }
}
fun getButterflyIndices(nNotes: Int): List<Int> {
    return when (nNotes) {
        2 -> listOf(0,1,0, 1,0,1 ,0,1,0, 1,0,1)
        3 -> listOf(0,1,0, 1,2,1 ,0,1,0, 1,2,1)
        4 -> listOf(0,1,0, 1,2,1 ,2,3,2, 1,2,1)
        5 -> listOf(0,1,0, 1,2,1 ,2,3,2, 3,4,3)
        6 -> listOf(0,1,0, 1,2,1 ,2,3,4, 3,4,5)
        7 -> listOf(0,1,0, 1,2,1 ,3,4,5, 4,5,6)
        8 -> listOf(0,1,2, 3,4,5 ,4,5,6, 5,6,7)
        9 -> listOf(0,1,2, 3,4,5 ,3,4,5, 6,7,8)
        10 -> listOf(0,1,2, 3,4,5 ,6,5,6, 7,8,9)
        11 -> listOf(0,1,2, 3,4,5 ,6,7,8, 9,10,9)
        12 -> listOf(0,1,2, 3,4,5 ,6,7,8, 9,10,11)
        else -> listOf(0,0,0, 0,0,0 ,0,0,0, 0,0,0)
    }
}
fun findWholeToneScale(startPitch: Int, endPitch: Int, isRetrograde: Boolean = false): List<Int> {
    val nSemitones = (startPitch - endPitch).absoluteValue
    val nNotes = nSemitones / 2 + nSemitones % 2
    val direction = if(startPitch < endPitch) 1 else -1
    return if(isRetrograde){ // start pitch is not present
        if(nSemitones % 2 == 0) (1..nNotes).map{ startPitch + it * direction * 2}
        else listOf(*(0 until nNotes-1).map{ startPitch + direction + it * direction * 2}.toTypedArray(), endPitch)
    } else { // end pitch is not present
        if(nSemitones % 2 == 0) (0 until nNotes).map{ startPitch + it * direction * 2}
        else listOf(startPitch, *(0 until nNotes - 1).map{ startPitch + direction + it * direction * 2}.toTypedArray())
    }
}
fun findChromaticScale(startPitch: Int, endPitch: Int, isRetrograde: Boolean = false): List<Int> {
    val nSemitones = (startPitch - endPitch).absoluteValue
    val direction = if(startPitch < endPitch) 1 else -1
    return if(isRetrograde){ // start pitch is not present
        (1..nSemitones).map{ startPitch + it * direction}
    } else { // end pitch is not present
        (0 until nSemitones).map{ startPitch + it * direction}
    }
}
fun findWholeToneScaleDiCambio(startPitch: Int, endPitch: Int, isRetrograde: Boolean = false): List<Int> {
    val nSemitones = (startPitch - endPitch).absoluteValue
    val nNotes = nSemitones / 2 + nSemitones % 2
    val direction = if(startPitch < endPitch) 1 else -1
    val scale = if(isRetrograde){ // start pitch is not present
        if(nSemitones % 2 == 0) (1..nNotes).map{ startPitch + it * direction * 2}
        else listOf(*(0 until nNotes-1).map{ startPitch + direction + it * direction * 2}.toTypedArray(), endPitch)
    } else { // end pitch is not present
        if(nSemitones % 2 == 0) (0 until nNotes).map{ startPitch + it * direction * 2}
        else listOf(startPitch, *(0 until nNotes - 1).map{ startPitch + direction + it * direction * 2}.toTypedArray())
    }
    val result = mutableListOf<Int>()
    val toneDirection = direction * 2
    if(scale.size == 1) {
        return if(isRetrograde){
            listOf(endPitch, startPitch, endPitch)
        } else {
            listOf(startPitch, endPitch, startPitch)
        }
    }
    scale.forEach {
        result.add(it); result.add(it - toneDirection)
    }
    //println("scale: $scale")
    if(isRetrograde){
        result.removeAt(result.size-1)
        result[1] = startPitch
    } else {
        result.removeAt(1)
        result[2] = result[0]
    }
    return result
}
fun findChromaticScaleDiCambio(startPitch: Int, endPitch: Int, isRetrograde: Boolean = false): List<Int> {
    val nSemitones = (startPitch - endPitch).absoluteValue
    val direction = if(startPitch < endPitch) 1 else -1
    val scale = if(isRetrograde){ // start pitch is not present
        (1..nSemitones).map{ startPitch + it * direction}
    } else { // end pitch is not present
        (0 until nSemitones).map{ startPitch + it * direction}
    }
    val result = mutableListOf<Int>()
    scale.forEach {
        result.add(it); result.add(it - direction)
    }
    if(isRetrograde){
        result.removeAt(result.size-1)
    } else {
        result.removeAt(1)
    }
    return result
}

fun findGruppettoPitches(pitch: Int, nextPitch: Int, addGliss: Boolean = false, finalGlissando:Int ): Pair<List<Int>,List<Int>>{
    val isAscendant = pitch <= nextPitch
    val (secondPitch, fourthPitch) = if(isAscendant) Pair(pitch+1, pitch -1) else Pair(pitch-1, pitch +1)
    val glissList = if(addGliss) {
        if(isAscendant) listOf(1,-1,-1,1,finalGlissando) else listOf(-1,1,1,-1,finalGlissando)
    } else {
        listOf(0,0,0,0, finalGlissando)
    }
    return Pair(listOf(pitch, secondPitch, pitch, fourthPitch, pitch), glissList)
}

fun findCambioPitches(pitch: Int, nextPitch: Int, addGliss: Boolean = false, finalGlissando:Int ): Pair<List<Int>,List<Int>>{
    val isAscendant = pitch <= nextPitch
    val (secondPitch, thirdPitch) = if(isAscendant) Pair(pitch+1, pitch -1) else Pair(pitch-1, pitch +1)
    val glissList = if(addGliss) {
        if(isAscendant) listOf(1,-2,1,finalGlissando) else listOf(-1,2,-1,finalGlissando)
    } else {
        listOf(0,0,0, finalGlissando)
    }
    return Pair(listOf(pitch, secondPitch, thirdPitch, pitch), glissList)
}
fun findOscillationPitches(nNotes: Int, pitch: Int, nextPitch: Int, radius:Int): List<Int>{
    val isAscendant = pitch <= nextPitch
    val (secondPitch, fourthPitch) = if(isAscendant) Pair(pitch+radius, pitch-radius) else Pair(pitch-radius, pitch+radius)
    val module = listOf(pitch, secondPitch, pitch, fourthPitch)
    val result = mutableListOf<Int>()
    for(i in 0 until nNotes/4){
        result += module
    }
    result += pitch
    return result
}
fun findIrregularPitches(nNotes: Int, startEndPitch: Int, pitches: MutableList<Int>): List<Int> {
    var lastPitch = startEndPitch
    val result = mutableListOf<Int>()
    result += startEndPitch
    for (i in 1 until nNotes-1){
        pitches.remove(lastPitch)
        val chosenPitch = pitches.random()
        result.add(chosenPitch)
        pitches += lastPitch
        lastPitch = chosenPitch
    }
    result += startEndPitch
    return result
}
fun findGlissandoForRetrogradeScales(pitches: List<Int>, finalGlissando: Int = 0, glissandoLimit: Int = 12): List<Int>{
    val result = mutableListOf<Int>()
    for(i in 0 until pitches.size-1){
        val gliss = pitches[i+1] - pitches[i]
        result += if(gliss > glissandoLimit) 0 else gliss
    }
    result.add(finalGlissando)
    return result
}
fun findGlissandoForScales(pitches: List<Int>, nextPitch:Int, glissandoLimit: Int = 12): List<Int>{
    val result = mutableListOf<Int>()
    for(i in 0 until pitches.size-1){
        val gliss = pitches[i+1] - pitches[i]
        result += if(gliss > glissandoLimit) 0 else gliss
    }
    val lastGliss = nextPitch - pitches[pitches.size-1]
    result += if(lastGliss > glissandoLimit) 0 else lastGliss
    return result
}


//fun main(){
//    println(accumulateVelocities(10, 64, 30))
//    println(accumulateVelocitiesCrescDim(10, 64, 30))
//    println(accumulateVelocities(1, 64, 30))
//    println(accumulateVelocitiesCrescDim(1, 64, 30))
//    println(accumulateVelocities(0, 64, 30))
//    println(accumulateVelocitiesCrescDim(0, 64, 30))
//    println( findWholeToneScale(10,12) )
//    println( findWholeToneScale(12,10) )
//    println( findWholeToneScaleDiCambio(10,12) )
//    println( findWholeToneScaleDiCambio(12,10) )
//    println( findWholeToneScale(10,12, true) )
//    println( findWholeToneScale(12,10, true) )
//    println( findWholeToneScaleDiCambio(10,12, true) )
//    println( findWholeToneScaleDiCambio(12,10, true) )
//    println( findWholeToneScale(10,21) )
//    println( findWholeToneScale(21,10) )
//    println( findWholeToneScaleDiCambio(10,21) )
//    println( findWholeToneScaleDiCambio(21,10) )
//    println( findWholeToneScale(10,21, true) )
//    println( findWholeToneScale(21,10, true) )
//    println( findWholeToneScaleDiCambio(10,21, true) )
//    println( findWholeToneScaleDiCambio(21,10, true) )

//    val durs = findScaleDurations(1000, 6, 60).reversedList()
//    println(durs)
//    println(findScaleTicks(0, durs))
//
//    println( findChromaticScale(10,21) )
//    println( findChromaticScale(21,10) )
//    println( findChromaticScale(10,21, true) )
//    println( findChromaticScale(21,10, true) )
//    println( findChromaticScaleDiCambio(10,21) )
//    println( findChromaticScaleDiCambio(21,10) )
//    println( findChromaticScaleDiCambio(10,21, true) )
//    println( findChromaticScaleDiCambio(21,10, true) )
//}
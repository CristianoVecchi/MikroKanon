package com.cristianovecchi.mikrokanon.AIMUSIC

import kotlin.math.absoluteValue

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

fun findGruppettoPitches(pitch: Int, nextPitch: Int): List<Int>{
    val (secondPitch, fourthPitch) = when {
        pitch <= nextPitch -> Pair(pitch+1, pitch -1)
        pitch > nextPitch -> Pair(pitch-1, pitch +1)
        else -> Pair(pitch+1, pitch -1)
    }
    return listOf(pitch, secondPitch, pitch, fourthPitch, pitch)
}

fun findCambioPitches(pitch: Int, nextPitch: Int): List<Int>{
    val (secondPitch, fourthPitch) = when {
        pitch <= nextPitch -> Pair(pitch+1, pitch -1)
        pitch > nextPitch -> Pair(pitch-1, pitch +1)
        else -> Pair(pitch+1, pitch -1)
    }
    return listOf(pitch, secondPitch, fourthPitch, pitch)
}

fun main(){
    println( findWholeToneScale(10,12) )
    println( findWholeToneScale(12,10) )
    println( findWholeToneScaleDiCambio(10,12) )
    println( findWholeToneScaleDiCambio(12,10) )
    println( findWholeToneScale(10,12, true) )
    println( findWholeToneScale(12,10, true) )
    println( findWholeToneScaleDiCambio(10,12, true) )
    println( findWholeToneScaleDiCambio(12,10, true) )
//    println( findWholeToneScale(10,21) )
//    println( findWholeToneScale(21,10) )
//    println( findWholeToneScaleDiCambio(10,21) )
//    println( findWholeToneScaleDiCambio(21,10) )
//    println( findWholeToneScale(10,21, true) )
//    println( findWholeToneScale(21,10, true) )
//    println( findWholeToneScaleDiCambio(10,21, true) )
//    println( findWholeToneScaleDiCambio(21,10, true) )

//    val durs = findScaleDurations(1000, 6, 60).reversed()
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
}
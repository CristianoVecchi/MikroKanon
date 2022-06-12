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

fun main(){
    println( findWholeToneScale(10,20) )
    println( findWholeToneScale(20,10) )
    println( findWholeToneScale(10,21) )
    println( findWholeToneScale(21,10) )
    println( findWholeToneScale(10,20, true) )
    println( findWholeToneScale(20,10, true) )
    println( findWholeToneScale(10,21, true) )
    println( findWholeToneScale(21,10, true) )

    val durs = findScaleDurations(1000, 6, 60).reversed()
    println(durs)
    println(findScaleTicks(0, durs))

    println( findChromaticScale(10,21) )
    println( findChromaticScale(21,10) )
    println( findChromaticScale(10,20, true) )
    println( findChromaticScale(20,10, true) )
}
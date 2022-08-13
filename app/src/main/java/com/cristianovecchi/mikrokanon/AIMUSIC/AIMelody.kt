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
fun findOscillationPitches(div: Int, pitch: Int, nextPitch: Int, radius:Int): List<Int>{
    val isAscendant = pitch <= nextPitch
    val (secondPitch, fourthPitch) = if(isAscendant) Pair(pitch+radius, pitch-radius) else Pair(pitch-radius, pitch+radius)
    val module = listOf(pitch, secondPitch, pitch, fourthPitch)
    val result = mutableListOf<Int>()
    for(i in 0 until div/4){
        result += module
    }
    result += pitch
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
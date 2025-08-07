package com.cristianovecchi.mikrokanon.AIMUSIC

import com.cristianovecchi.mikrokanon.divideDistributingRest

fun accumulateVelocities(nNotes: Int, start: Int, diff: Int): List<Int>{
    if(diff == 0 || nNotes == 1) return List(nNotes) { start }
    val result = mutableListOf<Int>()
    val increments = diff.divideDistributingRest(nNotes-1)
    //println("increments:$increments")
    var step = start
    increments.forEach {
        result += step
        step += it
    }
    //println("nNotes:$nNotes start:$start diff:$diff result:$result lastStep:$step" )
    return result.apply{
        this += step
    }
}
fun accumulateVelocitiesCrescDim(nNotes: Int, start: Int, diff: Int, isDimCresc: Boolean = false): List<Int>{
    if(diff == 0 || nNotes == 1) return List(nNotes) { start }
    val result = mutableListOf<Int>()
    val nNotesFirst = nNotes / 2 + nNotes % 2
    val firstArch = accumulateVelocities(nNotesFirst, start, diff)

    if(isDimCresc){
        val secondArch = if(nNotes % 2 == 0) firstArch else firstArch.drop(1)
        result += firstArch.reversed()
        result += secondArch

    } else {
        val secondArch = if(nNotes % 2 == 0) firstArch.reversed() else firstArch.reversed().drop(1)
        result += firstArch
        result += secondArch
    }
    return result//.apply{ println(this)}
}
//fun main() {
//    val list = listOf(1,2,3,4,5,6,7,8,9)
//    println(list.reversed())
//    throw Exception("Fake exception")
//    println(accumulateVelocitiesCrescDim(11, 67, 1, false))
//    println(accumulateVelocitiesCrescDim(11, 67, 0, false))
//    println(accumulateVelocities(4, 64, 30))
//    println(accumulateVelocities(5, 64, 30))
//    println(accumulateVelocitiesCrescDim(1, 64, 30))
//    println(accumulateVelocitiesCrescDim(7, 64, 30))
//    println(accumulateVelocitiesCrescDim(9, 64, 30))
//    println(accumulateVelocities(0, 64, 30))
//    println(accumulateVelocitiesCrescDim(0, 64, 30))
//}
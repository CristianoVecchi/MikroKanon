package com.cristianovecchi.mikrokanon.midi.playerFunctions

fun projectRibattutos(ribattutos: List<Float>, notePivots: List<Int>): List<Float> {
    val result = mutableListOf<Float>()

    if(notePivots.size <= 1){ // just one section
        return List(notePivots.last()) {ribattutos[0]}
    }
    for(i in 0 until notePivots.size -1){
        val sectionSize = notePivots[i+1] - notePivots[i]
        //println("ribattutos in projection: $ribattutos")
        if(ribattutos[i] == ribattutos[i+1]){
            result.addAll(List(sectionSize){ribattutos[i]})//.apply{println("Rib section $i: $this")})
        } else {
            val startRibattuto = ribattutos[i]
            val step = (ribattutos[i+1] - startRibattuto) / sectionSize
            result.addAll( (0 until sectionSize).map{ startRibattuto + it * step})//.apply{println("Rib section $i: $this")})
        }
    }
    return result.toList()
}
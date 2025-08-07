package com.cristianovecchi.mikrokanon.midi.playerFunctions

import com.cristianovecchi.mikrokanon.AIMUSIC.TickChangeData

fun alterArticulation(
    ticks: IntArray, durations: IntArray,
    legatoAlterations: List<Float>, ribattutos: List<Int>, legatoDeltas: List<Long>,
    pivots: List<Int>, previousIsRest: BooleanArray, maxLegato: Int, changes: List<TickChangeData>
): Pair<IntArray, FloatArray> {
    if (durations.isEmpty()) return Pair(IntArray(0), FloatArray(0))
    val result = IntArray(durations.size)
    //val resultRibattutos = FloatArray(durations.size)

    var alterationIndex = 0
    var alterationTick = 0
    var durIndex = 0
    var legatoAlteration: Float
    // var ribattutoAlteration: Float
    var newDur: Int
    var nextDur: Int
    var thisDur: Int
    var legato: Int
    val notePivots = mutableListOf<Int>()
    var pivotIndex = 0
    var changeIndex = 0
    val changeNotes = if(changes.size == 1) listOf(0)
    else changes.map{it.noteIndex}.drop(1)//.apply{println("changeNote: $this")}
    if (durations.isNotEmpty()) {
        while (durIndex < durations.size - 1) {
            while (alterationTick + legatoDeltas[alterationIndex] < ticks[durIndex]) {
                alterationTick += legatoDeltas[alterationIndex].toInt()
                alterationIndex++
            }
            if(alterationIndex >= pivots[pivotIndex]){
                notePivots.add(durIndex)
                pivotIndex++
            }
            legatoAlteration = legatoAlterations[alterationIndex]
            // ribattutoAlteration = ribattutosAlterations[alterationIndex]
            thisDur = durations[durIndex]
            if (legatoAlteration <= 1.0) {
                newDur = (thisDur * legatoAlteration).toInt()
                result[durIndex] = if (newDur < 12) 12 else newDur
                //   resultRibattutos[durIndex] = ribattutoAlteration
            } else {
                if (previousIsRest[durIndex + 1]) { // there is a rest between notes, legato is not requested
                    result[durIndex] = thisDur
                    // resultRibattutos[durIndex] = ribattutoAlteration
                } else if(changeIndex<changeNotes.size && durIndex + 1 == changeNotes[changeIndex]){ // no legato if the next notes has a program change on it
                    //println("Legato avoided on note $durIndex cause program change on the next one.")
                    result[durIndex] = thisDur
                    changeIndex++
                } else {
                    nextDur = durations[durIndex + 1]
                    legato = (nextDur * (legatoAlteration - 1f)).toInt()
                    result[durIndex] =
                        if (legato > maxLegato) thisDur + maxLegato else thisDur + legato
                    //  resultRibattutos[durIndex] = ribattutoAlteration
                }
            }
            durIndex++
        }
    }
    //println("durationSize=${durations.size} legatoDeltasSize=${legatoDeltas.size} lastTick=${ticks[durIndex]}")
    while (alterationIndex < legatoDeltas.size && alterationTick + legatoDeltas[alterationIndex] <= ticks[durIndex]) {
        alterationTick += legatoDeltas[alterationIndex].toInt()
        alterationIndex++
    }
    legatoAlteration = if (alterationIndex< legatoDeltas.size) legatoAlterations[alterationIndex]//.apply{println("not last $this")}
    else legatoAlterations[legatoAlterations.size-1]//.apply{println("last $this")}
    // ribattutoAlteration = ribattutosAlterations[alterationIndex]
    thisDur = durations[durIndex]
    if (legatoAlteration <= 1.0) {
        newDur = (thisDur * legatoAlteration).toInt()
        result[durIndex] = if (newDur < 12) 12 else newDur
    } else {
        result[durIndex] = thisDur // last note doesn't need legato
    }

    //pivots.add(durations.size)
    //resultRibattutos[durIndex] = ribattutoAlteration

    notePivots.add(durations.size)
    val ribattutosAlterations: List<Float> = projectRibattutos(ribattutos.map{it.toFloat()}, notePivots)
//        println("Original durations: ${durations.contentToString()}")
//        result.also{ println("Alterate articulations: ${it.contentToString()}") }
//        ribattutosAlterations.also{ println("Alterate ribattutos: ${it}") }
//        println("Rounded ribattutos: ${ribattutosAlterations.map{it.roundToInt()}}")
//        println("notePivots: $notePivots")
    return Pair(result, ribattutosAlterations.toFloatArray())
}

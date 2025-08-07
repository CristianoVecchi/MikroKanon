package com.cristianovecchi.mikrokanon.midi.playerFunctions

import com.cristianovecchi.mikrokanon.AIMUSIC.Bar
import com.cristianovecchi.mikrokanon.AIMUSIC.RhythmPatterns
import com.cristianovecchi.mikrokanon.AIMUSIC.mergeOnesInMetro
import com.cristianovecchi.mikrokanon.alterateBpmWithDistribution
import com.leff.midi.MidiTrack
import com.leff.midi.event.meta.Tempo
import com.leff.midi.event.meta.TimeSignature

fun buildTempoTrack(bpms: List<Float>, totalLength: Long): MidiTrack {
    val tempoTrack = MidiTrack()
    val (bpmAlterations, bpmDeltas) = alterateBpmWithDistribution(bpms, 0.5f, totalLength)
    var tempoTick = 0L

    (0 until bpmAlterations.size - 1).forEach { index -> // doesn't take the last bpm
        val newTempo = Tempo(tempoTick, 0L, 500000)
        newTempo.bpm = bpmAlterations[index]
        tempoTrack.insertEvent(newTempo)
        tempoTick += bpmDeltas[index]
    }
    return tempoTrack
}


fun MidiTrack.setTimeSignatures(rhythm: List<Triple<RhythmPatterns, Boolean, Int>>, totalLength: Long): List<Bar> {
    //println("total length: $totalLength")
    val bars = mutableListOf<Bar>()
    var tick = 0L
    var barTick = 0L
    var lastSignature = Pair(-1, -1)
    val signatures: List<Pair<Int, Pair<Int, Int>>> = rhythm.map {
        val patternDuration = it.first.patternDuration()
        val barDuration = it.first.barDuration()
        val nRepetitions = it.third
        val metro = it.first.metro
        for(i in 0 until nRepetitions * (patternDuration/barDuration)){
            bars.add(Bar(metro, barTick, barDuration.toLong()))
            barTick += barDuration
        }
        Pair(patternDuration * nRepetitions, metro)
    }
    var index = 0
    var withOnes = false
    while (tick < totalLength) {
        var newSignature = signatures[index].second
        if (newSignature.first == 1) {
            withOnes = true
            newSignature = RhythmPatterns.mergeSequenceOfOnesInMetro(signatures[index].first, newSignature)
        }
        if (newSignature != lastSignature) {
            val ts = TimeSignature(
                tick, 0L, newSignature.first, newSignature.second,
                TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION
            )
            this.insertEvent(ts)
            //println("SIGNATURE #$index: tick = $tick  metro = ${newSignature.first}/${newSignature.second}")
            lastSignature = newSignature
        }
        tick += signatures[index].first
        index = ++index % signatures.size
    }
    return if(withOnes) bars.mergeOnesInMetro() else bars
}
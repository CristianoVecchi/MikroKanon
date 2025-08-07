package com.cristianovecchi.mikrokanon.midi.playerFunctions

import com.cristianovecchi.mikrokanon.alterateBpmWithDistribution
import com.leff.midi.MidiTrack
import com.leff.midi.event.Controller

fun setAudio8D(track: MidiTrack, nRevolutions: Int, channel: Int) {
    val length = track.lengthInTicks
    if(length == 0L) return
    val aims = mutableListOf<Float>()
    for(i in 0 until nRevolutions){
        aims.add(0f)
        aims.add(127f)
    }
    aims.add(0f)
    //println("TRACK N°$channel length:${track.lengthInTicks}")
    val (audio8Dalterations, audio8Ddeltas) = alterateBpmWithDistribution(aims, 2f, track.lengthInTicks)
    var tick = 0L
    //println("AUDIO 8D DELTAS: $audio8Ddeltas")
    (0 until audio8Dalterations.size -1).forEach { i -> // doesn't take the last bpm
        val newPan = Controller(tick, channel,10, audio8Dalterations[i].toInt())
        track.insertEvent(newPan)
        tick += audio8Ddeltas[i]
    }
}

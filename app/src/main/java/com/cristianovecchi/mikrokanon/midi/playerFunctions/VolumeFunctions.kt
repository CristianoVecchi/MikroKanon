package com.cristianovecchi.mikrokanon.midi.playerFunctions

import com.cristianovecchi.mikrokanon.alterateBpmWithDistribution
import com.cristianovecchi.mikrokanon.convertDynamicToBytes
import com.leff.midi.MidiTrack
import com.leff.midi.event.SystemExclusiveEvent

fun MidiTrack.addVolumeToTrack(dynamics: List<Float>, totalLength: Long) {
    //val dynamics = listOf(1f,0f,1f)
    val (dynamicAlterations, dynamicDeltas) = alterateBpmWithDistribution(dynamics, 0.01f, totalLength)
    var tempoTick = 0L

    (0 until dynamicAlterations.size - 1).forEach { index -> // doesn't take the last dynamic
        // 0x7F = universal immediatly message, 0x7F = all devices, 0x04 = device control message, 0x01 = master volume
        // bytes = first the low 7 bits, second the high 7 bits - volume is from 0x0000 to 0x3FFF
        val volumeBytes: Pair<Int, Int> = dynamicAlterations[index].convertDynamicToBytes()
        val data: ByteArray =
            listOf(0x7F, 0x7F, 0x04, 0x01, volumeBytes.first, volumeBytes.second, 0xF7)
                .foldIndexed(ByteArray(7)) { i, a, v -> a.apply { set(i, v.toByte()) } }
        val newGeneralVolume = SystemExclusiveEvent(0xF0, tempoTick, data)

        this.insertEvent(newGeneralVolume)
        tempoTick += dynamicDeltas[index]
    }
}
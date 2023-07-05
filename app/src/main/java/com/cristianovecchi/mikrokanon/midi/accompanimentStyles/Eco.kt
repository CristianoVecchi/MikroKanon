package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.midi.Player
import com.cristianovecchi.mikrokanon.shiftCycling
import com.leff.midi.MidiTrack

fun createEco(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                   diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection
) {
    //data class Note(val pitch: Int, val tick: Long, var duration: Long, val velocity: Int)
    //bars.forEach { println(it) }
    var lastPitch = -1
    val actualOctavePitches = octaves.map { (it + 1) * 12 }
    val increase = harmonizationStyle.increase
    val delay = when (harmonizationStyle) {
        HarmonizationStyle.ECO_2 -> 2
        HarmonizationStyle.ECO_3 -> 3
        HarmonizationStyle.ECO_4 -> 4
        HarmonizationStyle.ECO_5 -> 5
        HarmonizationStyle.ECO_6 -> 6
        else -> 1
    }
    bars.forEachIndexed { i, bar ->

        val barDur = bar.duration
        var pitches = if(barDur < 64 ) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)}
        else direction.applyDirection(absPitches[i])
        val size = pitches.size
        if(size > 0){
            val nSteps = pitches.size + delay
            val durs = barDur.divideDistributingRest(nSteps)
            val velocities = bars.getProgressiveVelocities(i, nSteps, diffChordVelocity, increase)
            val comesVelocities = velocities.map{ (it - 18).coerceAtLeast(1) }
            val ticks = findScaleTicks(bar.tick, durs)
            val duxDurs = durs.toMutableList().apply { this[size-1] = this[size-1] / 2 }
            val comesDurs = durs.toMutableList().apply { this[nSteps-1] = this[nSteps-1] / 2 }
            pitches = if(pitches.first() == lastPitch) pitches.shiftCycling() else pitches
            pitches.forEachIndexed { j, absPitch ->
                val tick = ticks[j]
                val actualDuration = duxDurs[j]
                val velocity = velocities[j]
                actualOctavePitches.forEach{ octave ->
                    Player.insertNoteWithGlissando(
                        chordsTrack, tick, actualDuration, chordsChannel,
                        octave + absPitch, velocity, 70, 0
                    )
                }
            }
            var index = delay
            pitches.forEachIndexed { j, absPitch ->
                val tick = ticks[index]
                val actualDuration = comesDurs[index]
                val velocity = comesVelocities[index]
                actualOctavePitches.forEach{ octave ->
                    Player.insertNoteWithGlissando(
                        chordsTrack, tick, actualDuration, chordsChannel,
                        octave + absPitch, velocity, 70, 0
                    )
                }
                index++
            }
            lastPitch = pitches.last()
        }
    }
}
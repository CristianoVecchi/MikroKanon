package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.midi.Player
import com.leff.midi.MidiTrack

fun createGrazioso(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                   diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection
) {
    val actualOctavePitches = octaves.map { (it + 1) * 12 }
    val increase = harmonizationStyle.increase
    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration
        val pitches = if(barDur < 48 ) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)}
        else direction.applyDirection(absPitches[i])
        val size = pitches.size
        if(size > 0){
            val indices = getPairIndices(size)
            val nSteps = when(harmonizationStyle) {
                HarmonizationStyle.GRAZIOSO_3 -> indices.size * 3
                else -> indices.size * 4
            }
            if(barDur >= nSteps * 4){
                val durs = barDur.divideDistributingRest(nSteps)
                val velocities = bars.getProgressiveVelocities(i, nSteps, diffChordVelocity, increase)
                val pattern = mutableListOf<Int>()
                when (harmonizationStyle) {
                    HarmonizationStyle.GRAZIOSO_3 -> {
                        indices.forEach {
                            pattern.add(-1)
                            pattern.add(pitches[it.first])
                            pattern.add(pitches[it.second])
                        }
                    }
                    else -> {
                        indices.forEach {
                            pattern.add(-1)
                            pattern.add(pitches[it.first])
                            pattern.add(pitches[it.second])
                            pattern.add(pitches[it.first])
                        }
                    }
                }
                //println("pattern: $pattern")
                actualOctavePitches.forEach { octave ->
                    var tick = bar.tick
                    pattern.forEachIndexed { j, pitch ->
                        val dur = durs[j]
                        if(pitch > -1) {
                            Player.insertNoteWithGlissando(
                                chordsTrack, tick, dur, chordsChannel,
                                octave + pitch, velocities[j], 70, 0
                            )
                        }
                        tick += dur
                    }
                }
            }
        }
    }
}

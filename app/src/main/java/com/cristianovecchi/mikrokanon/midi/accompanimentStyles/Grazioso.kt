package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.midi.Player
import com.cristianovecchi.mikrokanon.reversedList
import com.cristianovecchi.mikrokanon.shiftCycling
import com.leff.midi.MidiTrack

fun createGrazioso(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                   diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection, isFlow: Boolean, density: Int
) {
    val actualOctavePitches = octaves.map { (it + 1) * 12 }.reversedList()
    val increase = harmonizationStyle.increase
    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration
        var pitches = if(barDur < 48 ) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)}
        else direction.applyDirection(absPitches[i])
        val size = pitches.size
        if(size > 0){
            val indices = getPairIndices(size)
            val nSteps = when(density) {
                1 -> indices.size * 3
                else -> indices.size * 4
            }
            if(barDur >= nSteps * 4){
                val durs = barDur.divideDistributingRest(nSteps)
                val phrasingDurs = when(density) {
                    1 -> {
                        durs.mapIndexed{ j, dur -> if(j % 3 == 2) dur / 2 else dur}
                    }
                    else -> {
                        durs.mapIndexed{ j, dur -> if(j % 4 == 3) dur / 2 else dur}
                    }
                }
                //println("durs: $durs  phrasing durs: $phrasingDurs")
                val velocities = bars.getProgressiveVelocities(i, nSteps, diffChordVelocity, increase)
                var pattern = mutableListOf<Int>()
                when (density) {
                    1 -> {
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
                    //println("Octave:$octave pattern:$pattern")
                    var tick = bar.tick
                    pattern.forEachIndexed { j, pitch ->
                        //val dur = durs[j]
                        if(pitch > -1) {
                            Player.insertNoteWithGlissando(
                                chordsTrack, tick, phrasingDurs[j], chordsChannel,
                                octave + pitch, velocities[j], 70, 0
                            )
                        }
                        tick += durs[j]
                    }
                    if(isFlow){
                        pitches = pitches.shiftCycling()
                        pattern.clear()
                        //TO DO optimize arrays
                        when (density) {
                            1 -> {
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
                    }
                }
            }
        }
    }
}

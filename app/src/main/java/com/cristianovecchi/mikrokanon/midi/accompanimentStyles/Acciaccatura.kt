package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.Bar
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationDirection
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationStyle
import com.cristianovecchi.mikrokanon.AIMUSIC.getProgressiveVelocities
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.midi.Player
import com.leff.midi.MidiTrack

fun createAcciaccatura(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                       diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection
) {
    val actualOctavePitches = octaves.map{ (it +1) * 12 }
    val increase = harmonizationStyle.increase
    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration
        val pitches = if(barDur < 16 ) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)} else absPitches[i]
        if(pitches.isNotEmpty()){
            val acciaccaturaPitches = when(direction){
                HarmonizationDirection.ASCENDING -> pitches.map { it - 1 }
                HarmonizationDirection.DESCENDING -> pitches.map { it + 1 }
                HarmonizationDirection.RANDOM -> (0..11).filter{ !pitches.contains(it) }
            }
            val durs = barDur.divideDistributingRest(4)
            var acciaccaturaDur = durs[0] / 4
            var actualDur = durs[0] - acciaccaturaDur
            val velocities = bars.getProgressiveVelocities(i, 4, diffChordVelocity, increase)
            var actualVelocity = velocities[0]
            var acciaccaturaVelocity = (actualVelocity + 12).coerceAtMost(127)

            actualOctavePitches.forEach { octave ->
                var tick = bar.tick
                acciaccaturaPitches.forEach { acciaccatura ->
                    Player.insertNoteWithGlissando(
                        chordsTrack, tick, acciaccaturaDur, chordsChannel,
                        octave + acciaccatura, acciaccaturaVelocity, 70, 0
                    )
                }
                tick += acciaccaturaDur
                pitches.forEach { pitch ->
                    Player.insertNoteWithGlissando(
                        chordsTrack, tick, actualDur, chordsChannel,
                        octave + pitch, actualVelocity, 70, 0
                    )
                }
            }
            if(harmonizationStyle == HarmonizationStyle.ACCIACCATURA_2){
                acciaccaturaDur = durs[2] / 4
                actualDur = durs[2] - acciaccaturaDur
                val halfBarTick = bar.tick + durs[0] + durs[1]
                actualVelocity = velocities[2]
                acciaccaturaVelocity = (actualVelocity + 12).coerceAtMost(127)
                actualOctavePitches.forEach { octave ->
                    var tick = halfBarTick
                    acciaccaturaPitches.forEach { acciaccatura ->
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, acciaccaturaDur, chordsChannel,
                            octave + acciaccatura, acciaccaturaVelocity, 70, 0
                        )
                    }
                    tick += acciaccaturaDur
                    pitches.forEach { pitch ->
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, actualDur, chordsChannel,
                            octave + pitch, actualVelocity, 70, 0
                        )
                    }
                }
            }
        }

    }
}
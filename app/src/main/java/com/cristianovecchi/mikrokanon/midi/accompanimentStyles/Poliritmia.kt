package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.Bar
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationDirection
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationStyle
import com.cristianovecchi.mikrokanon.AIMUSIC.getProgressiveVelocities
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.midi.Player
import com.leff.midi.MidiTrack

fun createPoliritmia(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                     diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection
) {
    val actualOctavePitches = octaves.map{ (it +1) * 12 }

    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration
        val size = absPitches[i].size
        val pitches = if(barDur / (size+1) < 4) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)}
        else direction.applyDirection(absPitches[i])
        if(size > 0){
            var division = 1
            pitches.forEach{ absPitch ->
                division++
                val durs = barDur.divideDistributingRest(division)
                val velocities = bars.getProgressiveVelocities(i, division, diffChordVelocity, 8)
                var tick = bar.tick
                durs.forEachIndexed{ j, dur ->
                    val velocity = velocities[j]
                    val staccatoDur = dur / 2
                    actualOctavePitches.forEach { octave ->
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, staccatoDur, chordsChannel,
                            octave + absPitch, velocity, 50, 0
                        )

                    }
                    tick += dur
                }
            }
        }
    }
}
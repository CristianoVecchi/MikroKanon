package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.Bar
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationDirection
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationStyle
import com.cristianovecchi.mikrokanon.AIMUSIC.getProgressiveVelocities
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.midi.Player
import com.leff.midi.MidiTrack

fun createBerlinese(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                       diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection, density: Int
) {
    val actualOctavePitches = octaves.map { (it + 1) * 12 }
    val increase = harmonizationStyle.increase
    bars.forEachIndexed { i, bar ->
        val nSteps = when (density) {
            2 -> bar.metro.first * 2
            3 -> bar.metro.first * 4
            else -> bar.metro.first
        }
        val barDur = bar.duration
        val pitches = if(barDur < nSteps * 16 ) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)} else absPitches[i]
        if(pitches.isNotEmpty() && nSteps > 1) {
            val acciaccaturaPitches = when (direction) {
                HarmonizationDirection.ASCENDING -> pitches.map { it - 1 }
                HarmonizationDirection.DESCENDING -> pitches.map { it + 1 }
                HarmonizationDirection.RANDOM -> (0..11).filter { !pitches.contains(it) }
            }
            val durs = barDur.divideDistributingRest(nSteps  * 4)
            val velocities = bars.getProgressiveVelocities(i, nSteps * 4, diffChordVelocity, increase)
            var tick = bar.tick + durs[0] + durs[1] + durs[2]
            //println("steps: $nSteps")
            (0 until nSteps-1).forEach {
                val index = it * 4 + 3
                val acciaccaturaDur = durs[index]
                val acciaccaturaVel = (velocities[index] + 2).coerceAtMost(127)
                acciaccaturaPitches.forEach { acciaccatura ->
                    actualOctavePitches.forEach { octave ->
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, acciaccaturaDur, chordsChannel,
                            octave + acciaccatura, acciaccaturaVel, 70, 0
                        )
                    }
                }
                tick += acciaccaturaDur
                val pitchDur = durs[index+1] + durs[index+2] + durs[index+3]
                val pitchVel = velocities[index+1]
                pitches.forEach { pitch ->
                    actualOctavePitches.forEach { octave ->
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, pitchDur, chordsChannel,
                            octave + pitch, pitchVel, 70, 0
                        )
                    }
                }
                tick += pitchDur
            }
        }
    }
}
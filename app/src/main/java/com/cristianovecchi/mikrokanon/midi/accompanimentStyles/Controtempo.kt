package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.Bar
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationStyle
import com.cristianovecchi.mikrokanon.AIMUSIC.getProgressiveVelocities
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.midi.Player
import com.leff.midi.MidiTrack

fun createControtempo(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                      diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true) {
    val actualOctavePitches = octaves.map{ (it +1) * 12 }
    val steps = when(harmonizationStyle){
        HarmonizationStyle.CONTROTEMPO_4 -> 4
        HarmonizationStyle.CONTROTEMPO_6 -> 6
        HarmonizationStyle.CONTROTEMPO_8 -> 8
        HarmonizationStyle.CONTROTEMPO_10 -> 10
        HarmonizationStyle.CONTROTEMPO_12 -> 12
        else -> 2
    }
    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration
        val pitches = absPitches[i]
        if(pitches.isNotEmpty()){
            val durs = barDur.divideDistributingRest(steps)
            val velocities = bars.getProgressiveVelocities(i, steps, diffChordVelocity, 20)
            // .also{println("velocities: $it")}
            actualOctavePitches.forEach { octave ->
                var tick = bar.tick
                for(step in 0 until steps step 2){
                    tick += durs[step]
                    val offBeatStep = step + 1
                    val offBeatDur = durs[offBeatStep]
                    val staccatoDur = offBeatDur / 4
                    val offBeatVelocity = velocities[offBeatStep]
                    // println("step:$step velocity:${velocities[offBeatStep]}")
                    pitches.forEach { absPitch ->
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, staccatoDur, chordsChannel,
                            octave + absPitch, offBeatVelocity, 70, 0
                        )
                    }
                    tick += offBeatDur
                }
            }
        }
    }
}
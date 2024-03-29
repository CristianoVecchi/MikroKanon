package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.Bar
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationStyle
import com.cristianovecchi.mikrokanon.AIMUSIC.RhythmPatterns
import com.cristianovecchi.mikrokanon.AIMUSIC.getProgressiveVelocities
import com.cristianovecchi.mikrokanon.midi.Player
import com.leff.midi.MidiTrack

fun createRibattuto(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                    diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, density: Int) {
    val actualOctavePitches = octaves.map{ (it +1) * 12 }
    val increase = harmonizationStyle.increase
    bars.forEachIndexed { i, bar ->
        val pitches = absPitches[i]
        val barDur = bar.duration
        var stepDur = when (density) {
            6 -> RhythmPatterns.denominatorMidiValue(bar.metro.second) / 6
            5 -> RhythmPatterns.denominatorMidiValue(bar.metro.second) / 5
            4 -> RhythmPatterns.denominatorMidiValue(bar.metro.second) / 4
            3 -> RhythmPatterns.denominatorMidiValue(bar.metro.second) / 3
            2 -> RhythmPatterns.denominatorMidiValue(bar.metro.second) / 2
            else -> RhythmPatterns.denominatorMidiValue(bar.metro.second)
        }
        val steps = if(stepDur < 8) (barDur.toInt() / 8) else barDur.toInt() / stepDur
        stepDur = if(stepDur < 8) (barDur.toInt() / steps) else stepDur
//        val stepDur = RhythmPatterns.denominatorMidiValue(bar.metro.second)
//        val steps = bar.metro.first
       // println("Bar#$i ${bar.metro.first}/${bar.metro.second}  barDur: $barDur  steps: $steps  stepDur: $stepDur")
        val staccatoDur = (stepDur / 4).coerceAtLeast(6).toLong()
        if(pitches.isNotEmpty()){
            val velocities = bars.getProgressiveVelocities(i, steps, diffChordVelocity, increase)//.also{println(it)}
            var tick = bar.tick
            (0 until steps).forEach { step ->
                //println("tick: $tick")
                val velocity = velocities[step]
                actualOctavePitches.forEach { octave ->
                    pitches.forEachIndexed { j, absPitch ->
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, staccatoDur, chordsChannel,
                            octave + absPitch, velocity, 50, 0
                        )
                    }
                }
                tick += stepDur
            }
        }
    }
}
package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.midi.Player
import com.cristianovecchi.mikrokanon.repeatCycling
import com.leff.midi.MidiTrack

fun createMultiPoliritmia(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                     diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection
) {
    val actualOctavePitches = octaves.map{ (it +1) * 12 }
    val increase = harmonizationStyle.increase
    val (div, start) = when (harmonizationStyle){
        HarmonizationStyle.POLIRITMIA_3 -> 3 to 3
        else -> 2 to 2
    }
    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration
        val size = absPitches[i].size
        val pitches = if(barDur / (size + start) < 4) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)}
        else direction.applyDirection(absPitches[i])
        if(size > 0){
            var division = start
            val groups = pitches.chunked(div)
            groups.forEach{ group ->
                division++
                val pitchArray = group.getWaveCycling(division)//.also{println("[bar#$i]  pitch array: $it")}
                val durs = barDur.divideDistributingRest(division)
                val velocities = bars.getProgressiveVelocities(i, division, diffChordVelocity, increase)
                var tick = bar.tick
                durs.forEachIndexed{ j, dur ->
                    val absPitch = pitchArray[j]
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

fun createPoliritmia(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                     diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection
) {
    val actualOctavePitches = octaves.map{ (it +1) * 12 }
    val increase = harmonizationStyle.increase
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
                val velocities = bars.getProgressiveVelocities(i, division, diffChordVelocity, increase)
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
package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.midi.Player
import com.cristianovecchi.mikrokanon.shiftCycling
import com.leff.midi.MidiTrack

fun createFarfalla(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                  diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection, isFlow: Boolean
) {
    val actualOctavePitches = octaves.map { (it + 1) * 12 }.reversed()
    val increase = harmonizationStyle.increase
    var lastPitch = -1
    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration
        val pitches = if(barDur < 48 ) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)}
                else direction.applyDirection(absPitches[i])
        val size = pitches.size
        if(size > 0){
            val indices = getButterflyIndices(size)
            val durs = barDur.divideDistributingRest(12)
            val velocities = bars.getProgressiveVelocities(i, 12, diffChordVelocity, increase)
            var pattern = indices.map{ pitches[it] }
            pattern = if(pattern.last() == lastPitch) pattern.shiftCycling() else pattern
            lastPitch = pattern.last()
            actualOctavePitches.forEach { octave ->
                //println("Octave:$octave pattern:$pattern")
                var tick = bar.tick
                pattern.forEachIndexed { j, pitch ->
                    val dur = durs[j]
                    Player.insertNoteWithGlissando(
                        chordsTrack, tick, dur, chordsChannel,
                        octave + pitch, velocities[j], 70, 0
                    )
                    tick += dur
                }
                if(isFlow){
                    pattern = pattern.shiftCycling()
                }
            }
        }
    }
}

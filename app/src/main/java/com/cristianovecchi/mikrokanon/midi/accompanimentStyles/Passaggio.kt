package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.Bar
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationDirection
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationStyle
import com.cristianovecchi.mikrokanon.AIMUSIC.getProgressiveVelocities
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.midi.Player
import com.cristianovecchi.mikrokanon.reversedList
import com.cristianovecchi.mikrokanon.shiftCycling
import com.leff.midi.MidiTrack

fun createPassaggio(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                       diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection, isFlow: Boolean
) {
    val actualOctavePitches = octaves.map{ (it +1) * 12 }.reversedList()
    val increase = harmonizationStyle.increase
    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration
        var pitches = if(barDur < 48 ) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)} else absPitches[i]
        if(pitches.isNotEmpty()) {
            var passaggioPitches = when (direction) {
                HarmonizationDirection.ASCENDING -> pitches.map { it - 1 }
                HarmonizationDirection.DESCENDING -> pitches.map { it + 1 }.reversedList()
                HarmonizationDirection.RANDOM -> (0..11).filter { !pitches.contains(it) }.shuffled()
            }
            //val goalPitches = direction.invert().applyDirection(pitches)
            var allPitches = passaggioPitches + direction.invert().applyDirection(pitches)

            val size = allPitches.size
            val durs = barDur.divideDistributingRest(size)
            val velocities = bars.getProgressiveVelocities(i, size, diffChordVelocity, increase)
//            print("Bar#$i ${bar.metro.first}/${bar.metro.second}  barDur: $barDur")
//            println("  allPitches: $allPitches  barDur: $barDur  durs: $durs  velocities: $velocities")
            actualOctavePitches.forEach { octave ->
               // println("Octave:$octave passaggio:$passaggioPitches pitches:$pitches  allPitches: $allPitches")
                var tick = bar.tick
                allPitches.forEachIndexed { j, pitch ->
                    val dur = durs[j]
                    Player.insertNoteWithGlissando(
                        chordsTrack, tick, dur, chordsChannel,
                        octave + pitch, velocities[j], 70, 0
                    )
                    tick += dur
                }
                if(isFlow){
                    pitches = pitches.shiftCycling()
                    passaggioPitches = passaggioPitches.shiftCycling()
                    allPitches = passaggioPitches + pitches
                }
            }
        }
    }
}
package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.midi.Player
import com.cristianovecchi.mikrokanon.repeatCycling
import com.cristianovecchi.mikrokanon.reversedList
import com.cristianovecchi.mikrokanon.shiftCycling
import com.leff.midi.MidiTrack

fun createMultiPoliritmia(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                     diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection, isFlow: Boolean, density: Int
) {
    val actualOctavePitches = octaves.map{ (it +1) * 12 }.reversedList()
    val increase = harmonizationStyle.increase
    val (div, start) = when (density){
        3 -> 3 to 3
        else -> 2 to 2
    }
    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration
        val size = absPitches[i].size
        var pitches = if(barDur / (size + start) < 4) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)}
        else direction.applyDirection(absPitches[i])
        if(size > 0){

            actualOctavePitches.forEach { octave ->
                //println("bar#$i pitches:$pitches")
                val groups = pitches.chunked(div)
                var division = start
                groups.forEach { group ->
                    division++
                    val pitchArray =
                        group.getWaveCycling(division)//.also{println("[bar#$i] division: $division pitch array: $it")}
                    // TO DO optimize arrays
                    val durs = barDur.divideDistributingRest(division)
                    val velocities = bars.getProgressiveVelocities(i, division, diffChordVelocity, increase)
                    var tick = bar.tick
                    durs.forEachIndexed { j, dur ->
                        val absPitch = pitchArray[j]
                        val velocity = velocities[j]
                        val staccatoDur = dur / 2
                       // println("division: $division  pitch: $absPitch")
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, staccatoDur, chordsChannel,
                            octave + absPitch, velocity, 50, 0
                        )
                        tick += dur
                    }
                }
                pitches = if (isFlow) pitches.shiftCycling() else pitches
            }
        }
    }
}

fun createPoliritmia(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                     diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection, isFlow: Boolean
) {
    val actualOctavePitches = octaves.map{ (it +1) * 12 }.reversedList()
    val increase = harmonizationStyle.increase
    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration
        val size = absPitches[i].size
        var pitches = if(barDur / (size+1) < 4) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)}
        else direction.applyDirection(absPitches[i])
        if(size > 0){
            actualOctavePitches.forEach { octave ->
                var division = 1
                //println("bar#$i pitches:$pitches")
                pitches.forEach { absPitch ->
                    var tick = bar.tick
                    division++
                    val durs = barDur.divideDistributingRest(division)
                    val velocities = // TO DO: optimize arrays
                        bars.getProgressiveVelocities(i, division, diffChordVelocity, increase)
                    durs.forEachIndexed { j, dur ->
                        val velocity = velocities[j]
                        val staccatoDur = dur / 2
                        //println("division: $division  pitch: $absPitch")
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, staccatoDur, chordsChannel,
                            octave + absPitch, velocity, 50, 0
                        )
                        tick += dur
                    }
                }
                pitches = if(isFlow) pitches.shiftCycling() else pitches
            }
        }
    }
}
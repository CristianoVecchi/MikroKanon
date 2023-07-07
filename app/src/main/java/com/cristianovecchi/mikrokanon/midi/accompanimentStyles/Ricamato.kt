package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.Bar
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationDirection
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationStyle
import com.cristianovecchi.mikrokanon.AIMUSIC.getProgressiveVelocities
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.midi.Player
import com.cristianovecchi.mikrokanon.shiftCycling
import com.leff.midi.MidiTrack

fun createRicamato(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                   diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection, isFlow: Boolean, density: Int
) {
    val actualOctavePitches = octaves.map{ (it +1) * 12 }.reversed()
    val increase = harmonizationStyle.increase
    var lastPitch = -1
    val nRepetitions = when (density) {
        2 -> 2
        3 -> 3
        4 -> 4
        5 -> 5
        else -> 1
    }
    val steps = 2 + 2 * nRepetitions
    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration
        var pitches = if(barDur < 4 * steps ) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)}
        else direction.applyDirection(absPitches[i])
        val size = pitches.size
        if(size > 0){
            var firstNote = pitches.first()
            var lastNote = pitches.last()
            if(firstNote == lastPitch) {
                firstNote -= lastNote
                lastNote += firstNote
                firstNote = lastNote - firstNote
            }
            var middleNotes = if(size < 3) pitches else pitches.subList(1, size -1)
            lastPitch = lastNote
            //println("Ricamato: $firstNote $middleNotes $lastNote")
            val durs = barDur.divideDistributingRest(steps)
            val velocities = bars.getProgressiveVelocities(i, steps, diffChordVelocity, increase)
            actualOctavePitches.forEach { octave ->
                //println("Octave: $octave  Syncope: $firstNote $middleNotes $lastNote")
                var tick = bar.tick
                Player.insertNoteWithGlissando(
                    chordsTrack, tick, durs[0], chordsChannel,
                    octave + firstNote, velocities[0], 70, 0
                )
                tick += durs[0]
                middleNotes.forEach { middleNote ->
                    Player.insertNoteWithGlissando(
                        chordsTrack, tick, durs[1], chordsChannel,
                        octave + middleNote, velocities[1], 70, 0
                    )
                }
                tick += durs[1]
                (0 until nRepetitions).forEach { repeat ->
                    var step = 2 + repeat * 2
                    Player.insertNoteWithGlissando(
                        chordsTrack, tick, durs[step], chordsChannel,
                        octave + lastNote, velocities[step], 70, 0
                    )
                    tick += durs[step]
                    step++
                    middleNotes.forEach { middleNote ->
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, durs[step], chordsChannel,
                            octave + middleNote, velocities[step], 70, 0
                        )
                    }
                    tick += durs[step]
                }
                if (isFlow) {
                    pitches = pitches.shiftCycling()
                    firstNote = pitches.first()
                    lastNote = pitches.last()
                    middleNotes = if(size < 3) pitches else pitches.subList(1, size -1)
                }
            }

        }
    }
}
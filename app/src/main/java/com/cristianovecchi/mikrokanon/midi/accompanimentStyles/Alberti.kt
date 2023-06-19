package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.Bar
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationDirection
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationStyle
import com.cristianovecchi.mikrokanon.AIMUSIC.getProgressiveVelocities
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.midi.Player
import com.leff.midi.MidiTrack

fun createAlberti(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                  diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection
) {
    val actualOctavePitches = octaves.map{ (it +1) * 12 }
    val increase = harmonizationStyle.increase
    var lastPitch = -1
    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration
        val pitches = if(barDur < 16 ) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)}
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
            val middleNotes = if(size < 3) pitches else pitches.subList(1, size -1)
            //println("Syncope: $firstNote $middleNotes $lastNote")
            val durs = barDur.divideDistributingRest(4)
            val velocities = bars.getProgressiveVelocities(i, 4, diffChordVelocity, increase)
            actualOctavePitches.forEach { octave ->
                var tick = bar.tick
                Player.insertNoteWithGlissando(
                    chordsTrack, tick, durs[0], chordsChannel,
                    octave + firstNote, velocities[0], 70, 0
                )
                tick += durs[0]
                Player.insertNoteWithGlissando(
                    chordsTrack, tick, durs[1], chordsChannel,
                    octave + lastNote, velocities[1], 70, 0
                )
                tick += durs[1]
                middleNotes.forEach { middleNote ->
                    Player.insertNoteWithGlissando(
                        chordsTrack, tick, durs[2], chordsChannel,
                        octave + middleNote, velocities[2], 70, 0
                    )
                }
                tick += durs[3]
                Player.insertNoteWithGlissando(
                    chordsTrack, tick, durs[3], chordsChannel,
                    octave + lastNote, velocities[3], 70, 0
                )
            }
            lastPitch = lastNote
        }
    }
}
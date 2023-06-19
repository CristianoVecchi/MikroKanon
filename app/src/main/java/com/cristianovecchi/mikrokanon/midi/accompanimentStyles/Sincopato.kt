package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.Bar
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationDirection
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationStyle
import com.cristianovecchi.mikrokanon.AIMUSIC.getProgressiveVelocities
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.midi.Player
import com.leff.midi.MidiTrack

fun createSincopato(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                    diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection
) {
    val actualOctavePitches = octaves.map{ (it +1) * 12 }
    val increase = harmonizationStyle.increase
    var lastPitch = -1
    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration
        val pitches = if(barDur < 16 ) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)}
        else direction.applyDirection(absPitches[i])
        if(pitches.isNotEmpty()){
            var firstNote = pitches[0]
            var lastNote = pitches.getOrElse(1) { pitches[0]}
            if(firstNote == lastPitch) {
                firstNote -= lastNote
                lastNote += firstNote
                firstNote = lastNote - firstNote
            }
            val syncopeNotes = if(pitches.size < 3) pitches else pitches.takeLast(pitches.size - 2)
            //println("Syncope: $firstNote $syncopeNotes $lastNote")
            val durs = barDur.divideDistributingRest(4)
            val velocities = bars.getProgressiveVelocities(i, 4, diffChordVelocity, increase)
            val firstDur = durs[0]
            val lastDur = durs[3]
            val syncopeDur = durs[1] + durs[2]
            actualOctavePitches.forEach { octave ->
                var tick = bar.tick
                Player.insertNoteWithGlissando(
                    chordsTrack, tick, firstDur, chordsChannel,
                    octave + firstNote, velocities[0], 70, 0
                )
                tick += firstDur
                syncopeNotes.forEach { syncopeNote ->
                    Player.insertNoteWithGlissando(
                        chordsTrack, tick, syncopeDur, chordsChannel,
                        octave + syncopeNote, velocities[1], 70, 0
                    )
                }
                tick += syncopeDur
                Player.insertNoteWithGlissando(
                    chordsTrack, tick, lastDur, chordsChannel,
                    octave + lastNote, velocities[3], 70, 0
                )
            }
            lastPitch = lastNote
        }
    }

}
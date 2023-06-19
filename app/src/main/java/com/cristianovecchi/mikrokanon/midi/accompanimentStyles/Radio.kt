package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.Bar
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationStyle
import com.cristianovecchi.mikrokanon.AIMUSIC.findTrillDurations
import com.cristianovecchi.mikrokanon.AIMUSIC.getProgressiveVelocities
import com.cristianovecchi.mikrokanon.midi.Player
import com.cristianovecchi.mikrokanon.shiftCycling
import com.leff.midi.MidiTrack

fun createRadio(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true) {
    val actualOctavePitches = octaves.map{ (it +1) * 12 }
    val increase = harmonizationStyle.increase
    var lastPitch = -1
    val isFlow = harmonizationStyle == HarmonizationStyle.RADIO_FLUSSO
    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration
        val pitches = if(barDur < 6 ) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)} else absPitches[i]
        if(pitches.isNotEmpty()){
            val (durs, div) = findTrillDurations(barDur.toInt())
            if (div != -1){
                val velocities = bars.getProgressiveVelocities(i, div, diffChordVelocity, increase)
                var extendedPitches = run {
                    val list = mutableListOf<Int>()
                    repeat((div / pitches.size) + 1){

                        var newPitches = pitches.shuffled()
                        newPitches = if(newPitches.first() == lastPitch) newPitches.shiftCycling() else newPitches
                        lastPitch = newPitches.last()
                        list.addAll(newPitches)
                    }
                    list.toList()
                }

                //println("Div = $div  Extended pitches = $extendedPitches")
                actualOctavePitches.forEach { octave ->
                    var tick = bar.tick
                    durs.forEachIndexed { j, dur ->
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, dur.toLong(), chordsChannel,
                            octave + extendedPitches[j], velocities[j], 70, 0
                        )
                        tick += dur
                    }
                    extendedPitches = if(isFlow) extendedPitches.shiftCycling() else extendedPitches
                }
            }
        }
    }
}
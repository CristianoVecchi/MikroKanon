package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.midi.Player
import com.leff.midi.MidiTrack

fun createArpeggio(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                   diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection
) {

    val actualOctavePitches = when(direction) {
        HarmonizationDirection.DESCENDING -> octaves.map{ (it +1) * 12}.reversed()
        HarmonizationDirection.RANDOM -> octaves.map{ (it +1) * 12 }.shuffled()
        else -> octaves.map{ (it +1) * 12}
    }
    val increase = harmonizationStyle.increase
    bars.forEachIndexed { i, bar ->
        //println("BAR #$i dur:${bar.duration}")
        val barDur = bar.duration
        var pitches = if(barDur < 48) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)}
        else direction.applyDirection(absPitches[i])
        if(pitches.isNotEmpty()){
            pitches = if (harmonizationStyle == HarmonizationStyle.SCAMBIARPEGGIO) pitches.exchangeNotes() else pitches
            val durs = barDur.divideDistributingRest(pitches.size * actualOctavePitches.size)
            // println("note durs: $durs")
            var tick = bar.tick

            if(durs[0] < 4) {
                val nNotes = bar.duration.toInt() / 4
                var noteIndex = 0
                val velocities = bars.getProgressiveVelocities(i, nNotes, diffChordVelocity, increase)
                reductedArpeggio@ for (octave in actualOctavePitches) {
                    //println("Octave: $octave")
                    for(absPitch in pitches) {
                        //println("${octave * 12 + absPitch}" + "tick:$tick dur:4")
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, 4, chordsChannel,
                            octave + absPitch, velocities[noteIndex], 70, 0
                        )
                        tick += 4
                        noteIndex++
                        if(noteIndex == nNotes) break@reductedArpeggio
                    }
                }
            } else {
                var durationIndex = 0
                val velocities = bars.getProgressiveVelocities(i, durs.size, diffChordVelocity, increase)
                actualOctavePitches.forEach { octave ->
                    // println("Octave: $octave")
                    pitches.forEach { absPitch ->
                        val dur = durs[durationIndex]
                        //println("${octave * 12 + absPitch}" + "tick:$tick dur:$dur")
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, dur, chordsChannel,
                            octave + absPitch, velocities[durationIndex], 70, 0
                        )
                        durationIndex++
                        tick += dur
                    }
                }
            }
        }
    }

}

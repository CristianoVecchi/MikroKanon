package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.midi.Player
import com.cristianovecchi.mikrokanon.shiftCycling
import com.leff.midi.MidiTrack

fun createCapriccio(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                    diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection, isFlow: Boolean
) {
    val actualOctavePitches = octaves.map{ (it +1) * 12 }.reversed()
    val increase = harmonizationStyle.increase
    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration
        var pitches = if(barDur < 16 ) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)}
        else direction.applyDirection(absPitches[i])
        val velocities = bars.getProgressiveVelocities(i, 4, diffChordVelocity, increase)
        // .also{println("Subdivision velocities: $it")}
        if(pitches.isNotEmpty()){
            var arpeggioPitches = pitches.dropLast(1)
            val size = arpeggioPitches.size
            var lastPitch = pitches.last()
            val lastVelocity = velocities[2]
            val durs = barDur.divideDistributingRest(4)
            val staccatoDur = durs[2] / 4

            actualOctavePitches.forEach { octave ->
                //println("Octave:$octave arpeggio:$arpeggioPitches last:$lastPitch")
                var tick = bar.tick
                if(i > 0 && harmonizationStyle == HarmonizationStyle.CAPRICCIO_2){

                    Player.insertNoteWithGlissando(
                        chordsTrack, tick, staccatoDur, chordsChannel,
                        octave + lastPitch, velocities[0], 70, 0
                    )
                }
                if(size > 0) {
                    val arpeggioVelocities =
                    accumulateVelocities(size, velocities[1], velocities[2] - velocities[1])
                //.also{println("${velocities[0]} + $it")}
                    val arpeggioDurs = durs[1].divideDistributingRest(size)
                    tick = bar.tick + durs[0]
                    arpeggioPitches.forEachIndexed { i, arpeggioPitch ->
                        val dur = arpeggioDurs[i]
                        //val velocity = arpeggioVelocities[i]
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, dur, chordsChannel,
                            octave + arpeggioPitch, arpeggioVelocities[i], 70, 0
                        )
                        tick += dur
                    }
                }
                tick = bar.tick + durs[0] + durs[1]
                Player.insertNoteWithGlissando(
                    chordsTrack, tick, staccatoDur, chordsChannel,
                    octave + lastPitch, lastVelocity, 70, 0
                )
                if(i != bars.size-1 && harmonizationStyle == HarmonizationStyle.CAPRICCIO_2 && size > 0) {
                    val goalVelocity = (bars.getNextBarVelocity(i) - diffChordVelocity + increase).coerceIn(0, 127)
                    val arpeggioVelocities = accumulateVelocities(size, velocities[3], goalVelocity - velocities[3])
                    //.also{println("${velocities[2]} + $it -> $goalVelocity")}
                    val arpeggioDurs = durs[3].divideDistributingRest(size)

                    tick = bar.tick + durs[0] + durs[1] + durs[2]
                    arpeggioPitches.forEachIndexed { i, arpeggioPitch ->
                        val dur = arpeggioDurs[i]
                        //val velocity = arpeggioVelocities[i]
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, dur, chordsChannel,
                            octave + arpeggioPitch, arpeggioVelocities[i], 70, 0
                        )
                        tick += dur
                    }
                }
                if(isFlow){
                    pitches = pitches.shiftCycling()
                    arpeggioPitches = pitches.dropLast(1)
                    lastPitch = pitches.last()
                }
            }







        }
    }
}
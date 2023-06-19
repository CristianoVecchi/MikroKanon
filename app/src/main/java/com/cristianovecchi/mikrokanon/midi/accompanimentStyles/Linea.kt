package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.Bar
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationDirection
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationStyle
import com.cristianovecchi.mikrokanon.AIMUSIC.getProgressiveVelocities
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.midi.Player
import com.cristianovecchi.mikrokanon.shiftCycling
import com.leff.midi.MidiTrack

fun createNoteLine(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                   diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection) {
    //data class Note(val pitch: Int, val tick: Long, var duration: Long, val velocity: Int)
    //bars.forEach { println(it) }
    var lastPitch = -1
    val actualOctavePitches = octaves.map{ (it +1) * 12 }
    val isFlow = when(harmonizationStyle){
        HarmonizationStyle.FLUSSO, HarmonizationStyle.ACCUMULO_FLUSSO -> true
        else -> false
    }
    val isAccumulo = when(harmonizationStyle){
        HarmonizationStyle.ACCUMULO, HarmonizationStyle.ACCUMULO_FLUSSO -> true
        else -> false
    }
    //.also{println("Octaves: $octaves -> Actual octaves: $it")}
    bars.forEachIndexed { i, bar ->

        val barDur = bar.duration
        var pitches = if(barDur < 48 ) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)}
        else direction.applyDirection(absPitches[i])
        if(pitches.isNotEmpty()){
            println("BAR #$i  pitches: $pitches")
            val durs = barDur.divideDistributingRest(pitches.size)
            val actualDurs = if(isAccumulo){
                var tick = -durs[0]
                durs.map{dur ->
                    tick += dur
                    barDur - tick }
                    .toMutableList()
            } else durs
            //println("actualdurs = $actualDurs")
            val velocities = bars.getProgressiveVelocities(i, durs.size, diffChordVelocity, 12)
            if(isFlow){
                actualOctavePitches.forEach { octave ->
                    var tick = bar.tick
                    //pitches = if(pitches.first() == lastPitch) pitches.shiftCycling() else pitches
                    pitches.forEachIndexed { j, absPitch ->
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, actualDurs[j], chordsChannel,
                            octave + absPitch, velocities[j], 70, 0
                        )
                        tick += durs[j]
                    }
                    pitches = pitches.shiftCycling()
                    // lastPitch = pitches.last()
                }
            } else {
                var tick = bar.tick
                pitches = if(pitches.first() == lastPitch) pitches.shiftCycling() else pitches
                pitches.forEachIndexed { j, absPitch ->
                    val actualDuration = actualDurs[j]
                    val velocity = velocities[j]
                    actualOctavePitches.forEach{ octave ->
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, actualDuration, chordsChannel,
                            octave + absPitch, velocity, 70, 0
                        )
                    }
                    tick += durs[j]
                }
                lastPitch = pitches.last()
            }
        }

    }
}

fun createNoteDoubleTripleLine(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                               diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection
) {
    var lastPitch1 = -1
    var lastPitch2 = -1
    var lastPitch3 = -1
    val actualOctavePitches = octaves.map{ (it +1) * 12 }
    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration
        val pitches = if(barDur < 48 ) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)}
        else direction.applyDirection(absPitches[i])
        if(pitches.isNotEmpty()){
            var (pitches1, pitches2, pitches3) = when (harmonizationStyle){
                HarmonizationStyle.TRICINIUM -> {
                    val nThirdPitches = pitches.size / 3
                    val nThirdPitches2 = nThirdPitches * 2
                    Triple(pitches.subList(0, nThirdPitches), pitches.subList(nThirdPitches, nThirdPitches2), pitches.subList(nThirdPitches2, pitches.size))
                }
                else -> {
                    val nHalfPitches = pitches.size / 2
                    Triple(pitches.subList(0, nHalfPitches), pitches.subList(nHalfPitches, pitches.size), listOf<Int>() )
                }
            }
            val increase = 10
            println("Original:$pitches -> A:$pitches1 B:$pitches2 C:$pitches3")
            if(pitches1.isNotEmpty()){
                val durs = barDur.divideDistributingRest(pitches1.size)
                val velocities = bars.getProgressiveVelocities(i, durs.size, diffChordVelocity, increase)
                var tick = bar.tick
                pitches1 = if(pitches1.first() == lastPitch1) pitches1.shiftCycling() else pitches1
                pitches1.forEachIndexed { j, absPitch ->
                    val duration = durs[j]
                    val velocity = velocities[j]
                    actualOctavePitches.forEach{ octave ->
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, duration, chordsChannel,
                            octave + absPitch, velocity, 70, 0
                        )
                    }
                    tick += duration
                }
                lastPitch1 = pitches1.last()
            }
            if(pitches2.isNotEmpty()){
                val durs = barDur.divideDistributingRest(pitches2.size)
                val velocities = bars.getProgressiveVelocities(i, durs.size, diffChordVelocity, increase)
                var tick = bar.tick
                pitches2 = if(pitches2.first() == lastPitch2) pitches2.shiftCycling() else pitches2
                pitches2.forEachIndexed { j, absPitch ->
                    val duration = durs[j]
                    val velocity = velocities[j]
                    actualOctavePitches.forEach{ octave ->
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, duration, chordsChannel,
                            octave + absPitch, velocity, 70, 0
                        )
                    }
                    tick += duration
                }
                lastPitch2 = pitches2.last()
            }
            if(pitches3.isNotEmpty()){
                val durs = barDur.divideDistributingRest(pitches3.size)
                val velocities = bars.getProgressiveVelocities(i, durs.size, diffChordVelocity, increase)
                var tick = bar.tick
                pitches3 = if(pitches3.first() == lastPitch3) pitches3.shiftCycling() else pitches3
                pitches3.forEachIndexed { j, absPitch ->
                    val duration = durs[j]
                    val velocity = velocities[j]
                    actualOctavePitches.forEach{ octave ->
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, duration, chordsChannel,
                            octave + absPitch, velocity, 70, 0
                        )
                    }
                    tick += duration
                }
                lastPitch3 = pitches3.last()
            }
        }
    }
}
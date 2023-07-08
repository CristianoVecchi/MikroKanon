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
                   diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection, isFlow: Boolean) {
    //data class Note(val pitch: Int, val tick: Long, var duration: Long, val velocity: Int)
    //bars.forEach { println(it) }
    var lastPitch = -1
    val actualOctavePitches = octaves.map{ (it +1) * 12 }.reversed()
    val increase = harmonizationStyle.increase
    val isAccumulo = when(harmonizationStyle){
        HarmonizationStyle.ACCUMULO -> true
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
            val velocities = bars.getProgressiveVelocities(i, durs.size, diffChordVelocity, increase)
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
                               diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection, isFlow: Boolean
) {
    var lastPitch1 = -1
    var lastPitch2 = -1
    var lastPitch3 = -1
    val actualOctavePitches = octaves.map{ (it +1) * 12 }.reversed()
    val increase = harmonizationStyle.increase
    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration
        var pitches = if(barDur < 48 ) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)}
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
            //println("Original:$pitches -> A:$pitches1 B:$pitches2 C:$pitches3")
            var durs1: MutableList<Long> = mutableListOf()
            var durs2: MutableList<Long> = mutableListOf()
            var durs3: MutableList<Long> = mutableListOf()
            var vels1: List<Int> = listOf()
            var vels2: List<Int> = listOf()
            var vels3: List<Int> = listOf()
            if(pitches1.isNotEmpty()) {
                if (pitches1.first() == lastPitch1) { pitches1 = pitches1.shiftCycling() }
                lastPitch1 = pitches1.last()
                durs1 = barDur.divideDistributingRest(pitches1.size)
                vels1 = bars.getProgressiveVelocities(i, durs1.size, diffChordVelocity, increase)
            } else { lastPitch1 = -1 }
            if(pitches2.isNotEmpty()) {
                if (pitches2.first() == lastPitch2) { pitches2 = pitches2.shiftCycling() }
                lastPitch2 = pitches2.last()
                durs2 = barDur.divideDistributingRest(pitches2.size)
                vels2 = bars.getProgressiveVelocities(i, durs2.size, diffChordVelocity, increase)
            } else { lastPitch2 = -1 }
            if(pitches3.isNotEmpty()) {
                if (pitches3.first() == lastPitch3) { pitches3 = pitches3.shiftCycling() }
                lastPitch3 = pitches3.last()
                durs3 = barDur.divideDistributingRest(pitches3.size)
                vels3 = bars.getProgressiveVelocities(i, durs3.size, diffChordVelocity, increase)
            } else { lastPitch3 = -1 }
            actualOctavePitches.forEach{ octave ->
                //println("Octave:$octave p1:$pitches1 p2:$pitches2 p3:$pitches3")
                if(pitches1.isNotEmpty()) {
                    var tick = bar.tick
                    pitches1.forEachIndexed { j, absPitch ->
                        val duration = durs1[j]
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, duration, chordsChannel,
                            octave + absPitch, vels1[j], 70, 0
                        )
                        tick += duration
                    }
                }
                if(pitches2.isNotEmpty()){
                    var tick = bar.tick
                    pitches2.forEachIndexed { j, absPitch ->
                        val duration = durs2[j]
                        actualOctavePitches.forEach{ octave ->
                            Player.insertNoteWithGlissando(
                                chordsTrack, tick, duration, chordsChannel,
                                octave + absPitch, vels2[j], 70, 0
                            )
                        }
                        tick += duration
                    }
                }
                if(pitches3.isNotEmpty()){
                    var tick = bar.tick
                    pitches3.forEachIndexed { j, absPitch ->
                        val duration = durs3[j]
                        actualOctavePitches.forEach{ octave ->
                            Player.insertNoteWithGlissando(
                                chordsTrack, tick, duration, chordsChannel,
                                octave + absPitch, vels3[j], 70, 0
                            )
                        }
                        tick += duration
                    }
                }
                if(isFlow){
//                    pitches1 = pitches1.shiftCycling()
//                    pitches2 = pitches2.shiftCycling()
//                    pitches3 = pitches3.shiftCycling()
                    pitches = pitches.shiftCycling()
                    val triple = when (harmonizationStyle){
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
                    pitches1 = triple.first; pitches2 = triple.second; pitches3 = triple.third
                }
            }

        }
    }
}
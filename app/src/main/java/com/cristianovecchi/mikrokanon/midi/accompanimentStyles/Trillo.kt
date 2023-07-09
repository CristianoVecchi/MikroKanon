package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.midi.Player
import com.cristianovecchi.mikrokanon.shiftCycling
import com.leff.midi.MidiTrack

fun createMultiTrillo(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                 diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection, isFlow: Boolean) {
    val actualOctavePitches = octaves.map{ (it +1) * 12 }.reversed()
    val increase = harmonizationStyle.increase
    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration.toInt()
        var pitches = if(barDur < 18 ) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)}
        else direction.applyDirection(absPitches[i])
        when (val size = pitches.size) {
            0 -> Unit
//            1 -> {
//                val pitch = pitches.first()
//                val velocity = (bar.minVelocity!! - diffChordVelocity + increase).coerceIn(0, 127)
//                actualOctavePitches.forEach { octave ->
//                    Player.insertNoteWithGlissando(
//                        chordsTrack, bar.tick, barDur, chordsChannel,
//                        octave + pitch, velocity, 70, 0
//                    )
//                }
//            }
            else -> {
                var trills = pitches.chunked(2).toMutableList()
                if(trills.last().size == 1){
                    trills[trills.size -1 ] = listOf(pitches[size-1], pitches.getOrElse(size-2){pitches[size-1]})
                }
                val nTrills = trills.size
                val durs = barDur.divideDistributingRest(nTrills)
                val velocities = bars.getProgressiveVelocities(i, nTrills, diffChordVelocity, increase)
               // println("Bar#$i bar vels: $velocities")
                val trillsDatas = durs.map{ findEvenTrillDurations(it)}
                    //.also{println("nTrills: $nTrills  durs: $durs  trillsdatas: $it")}
                val trillsVelocities = velocities.mapIndexed{ j, velocity ->
                    accumulateVelocities(trillsDatas[j].first.size,
                        velocities[j],
                        velocities.getOrElse(j+1){ (bars.getNextBarVelocity(i) - diffChordVelocity + increase).coerceIn(0, 127) } - velocities[j])
                }
                actualOctavePitches.forEach { octave ->
                    //println("Octave: $octave  Trills: $trills")
                    var tick = bar.tick
                    trills.forEachIndexed { j, trill ->
                        val firstNote = trill.first()
                        val secondNote = trill.last()
                        val (trillDurs, div) = trillsDatas[j]
                        val trillVelocities = trillsVelocities[j]
                        //println("Trill#$j: $trill  vels: $trillVelocities")
                        for(index in 0 until div step 2){
                                var trillDur = trillDurs[index]
                                Player.insertNoteWithGlissando(
                                    chordsTrack, tick, trillDur.toLong(), chordsChannel,
                                    octave + firstNote, trillVelocities[index], 70, 0
                                )
                                tick += trillDur
                                trillDur = trillDurs[index+1]
                                Player.insertNoteWithGlissando(
                                    chordsTrack, tick, trillDur.toLong(), chordsChannel,
                                    octave + secondNote, trillVelocities[index+1], 70, 0
                                )
                                tick += trillDur
                            }

                    }
                    if(isFlow){
                        pitches = pitches.shiftCycling()
                        trills = pitches.chunked(2).toMutableList()
                        if(trills.last().size == 1){
                            trills[trills.size -1 ] = listOf(pitches[size-1], pitches.getOrElse(size-2){pitches[size-1]})
                        }
                    }
                }
            }
        }
    }
}
fun createTrillo(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                 diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, isFlow: Boolean) {
    val actualOctavePitches = octaves.map{ (it +1) * 12 }.reversed()
    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration
        var pitches = if(barDur < 18 ) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)}
        else absPitches[i]

        if(pitches.isNotEmpty()){
            val (durs, div) = findEvenTrillDurations(barDur.toInt())
            if (div != -1){
                val velocities = bars.getProgressiveVelocities(i, div, diffChordVelocity, 12)
                //println("Interval: $velocity - $goalVelocity   Velocities:$velocities")
                var trills = pitches.chunked(2)//.also{println("Trills: $it")}
                var firstNotes = trills.map{ it.first() }
                var secondNotes = trills.map{ it.last() }.filter{!firstNotes.contains(it)}
                actualOctavePitches.forEach { octave ->
                    //println("Octave: $octave  Syncope: $trills $firstNotes $secondNotes")
                    var tick = bar.tick
                    for(index in 0 until div step 2){
                        var duration = durs[index]
                        val firstVelocity = velocities[index]
                        firstNotes.forEach { firstNote ->
                            Player.insertNoteWithGlissando(
                                chordsTrack, tick, duration.toLong(), chordsChannel,
                                octave + firstNote, firstVelocity, 70, 0
                            )
                        }
                        tick += duration
                        duration = durs[index +1]
                        val secondVelocity = velocities[index+1]
                        secondNotes.forEach { secondNote ->
                            Player.insertNoteWithGlissando(
                                chordsTrack, tick, duration.toLong(), chordsChannel,
                                octave + secondNote, secondVelocity, 70, 0
                            )
                        }
                        tick += duration
                    }
                    if (isFlow) {
                        //pitches = pitches.shiftCycling()
                        trills = trills.map{ it.reversed() }
                        firstNotes = trills.map{ it.first() }
                        secondNotes = trills.map{ it.last() }.filter{!firstNotes.contains(it)}
                    }
                }
//                trills.forEach { trill ->
//                    actualOctaves.forEach { octave ->
//                        val octavePitch = octave * 12
//                        val trillPitches = (0 until div).map { if(it % 2 == 0) trill[0] + octavePitch else trill.getOrElse(1){-octavePitch} + octavePitch}
//                        println("Octave: $octave  nNotes:$div  pitches:$trillPitches")
//                        var tick = bar.tick
//                        trillPitches.forEachIndexed { j, actualPitch ->
//                            val duration = durs[j]
//                            if(actualPitch > 0){
//                                Player.insertNoteWithGlissando(
//                                    chordsTrack, tick, duration.toLong(), chordsChannel,
//                                    actualPitch, velocity, 70, 0
//                                )
//                            }
//                            tick += duration
//                        }
//                    }
//                }
            }

        }

    }
}
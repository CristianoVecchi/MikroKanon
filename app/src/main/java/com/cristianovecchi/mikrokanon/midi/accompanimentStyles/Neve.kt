package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.midi.Player
import com.leff.midi.MidiTrack

fun createNeve2(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
               diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection
) {
    var actualOctavePitches = octaves.map { (it + 1) * 12 }
    actualOctavePitches = when (direction) {
        HarmonizationDirection.ASCENDING -> actualOctavePitches.reversed()
        HarmonizationDirection.RANDOM -> actualOctavePitches.shuffled()
        else -> actualOctavePitches
    }
    val increase = harmonizationStyle.increase
    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration
        val pitches = if(barDur < 48 ) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)} else absPitches[i]
        val size = pitches.size
        if(size > 0) {
            var nSteps = ((size * 2) + 3).coerceAtLeast(5)
            val tick = bar.tick
            actualOctavePitches.forEach { octave ->
                nSteps = (nSteps -1).coerceAtLeast(3)
                val durs = barDur.divideDistributingRest(nSteps)
                println("pitches: $pitches")
                if(durs[0] > 3) {
                    val velocities = bars.getProgressiveVelocities(i, nSteps, diffChordVelocity, increase)
                    val ticks = findScaleTicks(tick, durs)
                    val staccatoDurs = durs.map { it / 4 }
                    println("bar#$i octave:$octave nSteps:$nSteps durs:$durs ticks:$ticks vels:$velocities")
                    val half1 = IntRange(0, nSteps / 2 - 1)
                    val half2 = IntRange(nSteps / 2, nSteps -1)
                    println("1st half: $half1  2nd half: $half2")
                    pitches.forEach { pitch ->
                        val index = half1.random()
                        println("index:$index pitch:$pitch tick:${ticks[index]}  ")
                        Player.insertNoteWithGlissando(
                            chordsTrack, ticks[index], staccatoDurs[index], chordsChannel,
                            octave + pitch, velocities[index], 70, 0
                        )
                    }
                    pitches.forEach { pitch ->
                        val index = half2.random()
                        println("index:$index pitch:$pitch tick:${ticks[index]}  ")
                        Player.insertNoteWithGlissando(
                            chordsTrack, ticks[index], staccatoDurs[index], chordsChannel,
                            octave + pitch, velocities[index], 70, 0
                        )
                    }

                }
            }
        }
    }
}
fun createNeve(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, direction: HarmonizationDirection
) {
    var actualOctavePitches = octaves.map { (it + 1) * 12 }
    actualOctavePitches = when (direction) {
        HarmonizationDirection.ASCENDING -> actualOctavePitches.reversed()
        HarmonizationDirection.RANDOM -> actualOctavePitches.shuffled()
        else -> actualOctavePitches
    }
    val increase = harmonizationStyle.increase
    bars.forEachIndexed { i, bar ->
        val barDur = bar.duration
        val pitches = if(barDur < 48 ) {if(bar.chord1 == null) emptyList() else listOf(bar.chord1!!.root)} else absPitches[i]
        val size = pitches.size
        if(size > 0) {
            var nSteps = (size + 3).coerceAtLeast(5)
            val tick = bar.tick
            actualOctavePitches.forEach { octave ->
                nSteps = (nSteps -1).coerceAtLeast(3)
                val durs = barDur.divideDistributingRest(nSteps)
                //println("pitches: $pitches")
                if(durs[0] > 3) {
                    val velocities = bars.getProgressiveVelocities(i, nSteps, diffChordVelocity, increase)
                    val ticks = findScaleTicks(tick, durs)
                    val staccatoDurs = durs.map { it / 4 }
                    //println("bar#$i octave:$octave nSteps:$nSteps durs:$durs ticks:$ticks vels:$velocities")
                    pitches.forEach { pitch ->
                        val index = (0 until nSteps).random()
                        //println("index:$index pitch:$pitch tick:${ticks[index]}  ")
                        Player.insertNoteWithGlissando(
                            chordsTrack, ticks[index], staccatoDurs[index], chordsChannel,
                            octave + pitch, velocities[index], 70, 0
                        )
                    }

                }
            }
        }
    }
}
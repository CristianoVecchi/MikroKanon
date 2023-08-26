package com.cristianovecchi.mikrokanon.midi.accompanimentStyles

import com.cristianovecchi.mikrokanon.AIMUSIC.Bar
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationStyle
import com.cristianovecchi.mikrokanon.AIMUSIC.getProgressiveVelocities
import com.cristianovecchi.mikrokanon.midi.Player
import com.leff.midi.MidiTrack

fun createAttacco(harmonizationStyle: HarmonizationStyle, chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, absPitches: List<List<Int>>, octaves: List<Int>,
                    diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true, density: Int) {
    val actualOctavePitches = octaves.map { (it + 1) * 12 }
    val increase = harmonizationStyle.increase
    val densityDurs = listOf(15,30,45,60,90, 120, 180, 240, 360, 480, 720, 960, 1440, 1920)
    // 128, 64, 64. ,32, 32., 16, 16., 8, 8. , 4, 4. , 2, 2., 4/4
    bars.forEachIndexed { i, bar ->
        val pitches = absPitches[i]
        val barDur = bar.duration
        val attaccoDur = if(density -1 >= densityDurs.size) barDur-1L else (densityDurs[density-1].coerceAtMost(barDur.toInt() -1)).toLong()
        val velocity = (bar.minVelocity!! - diffChordVelocity + increase).coerceIn(0,127)
        val tick = bar.tick
        println("ATTACCO bar#$i $pitches dur:$attaccoDur vel:$velocity")
        if(pitches.isNotEmpty() && attaccoDur > 4){
                //println("tick: $tick")
                actualOctavePitches.forEach { octave ->
                    pitches.forEach{absPitch ->
                        Player.insertNoteWithGlissando(
                            chordsTrack, tick, attaccoDur, chordsChannel,
                            octave + absPitch, velocity, 50, 0
                        )
                    }
                }

        }
    }
}
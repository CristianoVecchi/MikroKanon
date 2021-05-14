package com.cristianovecchi.mikrokanon.AIMUSIC

import com.leff.midi.MidiTrack
import com.leff.midi.event.MidiEvent
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn
import com.leff.midi.event.ProgramChange
import java.lang.Math.abs

object CounterpointInterpreter {
    fun doTheMagic(counterpoint: Counterpoint,
                   durations: List<Int> = listOf(240), // 1/8
                   ensembleParts: List<EnsemblePart>,
                   nuances: Boolean): List<MidiTrack> {
        val result = mutableListOf<MidiTrack>()


        counterpoint.parts.forEachIndexed { partIndex, part ->
            val channel = partIndex //+ 1
            val track = MidiTrack()
            val pc: MidiEvent =
                ProgramChange(0, channel, ensembleParts[partIndex].instrument) // cambia strumento
            track.insertEvent(pc)

            var tick = 0
            var index = 0
            var durIndex = 0
            val actualPitches = Insieme.linearMelody(
                ensembleParts[partIndex].octave,
                part.absPitches.toIntArray(),
                21,
                108
            )
            val velocities: IntArray = if(nuances){
                val mssq = MelodySubSequencer(actualPitches)
                mssq.assignVelocities(0.90f, 0.50f)
                mssq.velocities
            } else IntArray(actualPitches.size){ 100 }
//            println("PART: #$partIndex")
//            println(actualPitches.asList())
//            println(velocities.asList())
            while (index < actualPitches.size) {
                val pitch = actualPitches[index]
                val velocity = velocities[index]
                //println("pitch: $pitch vel: $velocity")
                var dur = durations[durIndex % durations.size]
                if (dur < 0) { // negative values are considered as rests
                    dur *= -1
                    tick += dur
                    durIndex++
                } else {
                    if (pitch != -1) {
                        while (index + 1 < actualPitches.size && actualPitches[index + 1] == pitch) {
                            var nextDur = durations[(durIndex + 1) % durations.size]
                            if (nextDur < 0) {
                                nextDur *= -1
                                dur += nextDur
                                durIndex++
                            } else {
                                dur += nextDur
                                index++
                                durIndex++
                            }
                        }

                        insertNote(
                            track, tick, dur, channel, pitch,
                            velocity, 80
                        )
                    }
                    tick += dur
                    index++
                    durIndex++
                }
            }
            result.add(track)
        }
        return result
    }
    fun insertNote(
        mt: MidiTrack, start: Int, duration: Int, channel: Int,
        pitch: Int, velOn: Int, velOff: Int
    ) {
        val on = NoteOn(start.toLong(), channel, pitch, velOn)
        val off = NoteOff((start + duration).toLong(), channel, pitch, velOff)
        mt.insertEvent(on)
        mt.insertEvent(off)
    }
}
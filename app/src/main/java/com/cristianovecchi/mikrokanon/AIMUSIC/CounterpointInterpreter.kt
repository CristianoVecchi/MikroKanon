package com.cristianovecchi.mikrokanon.AIMUSIC


import com.cristianovecchi.mikrokanon.convertFlagsToInts
import com.leff.midi.MidiTrack
import com.leff.midi.event.MidiEvent
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn
import com.leff.midi.event.ProgramChange
import java.lang.Math.abs

fun findTopNuances(stabilities: List<Float>, minNuance: Float, maxNuance: Float) : List<Float>{
    val n = stabilities.size
    val step = (maxNuance - minNuance) / n
    val steps = (0 until n).map{ maxNuance - (step * it)}
    val orderedStabilities = stabilities.sorted()
    return (0 until n).map{ steps[orderedStabilities.indexOf(stabilities[it])]}
}
object CounterpointInterpreter {
    fun doTheMagic(counterpoint: Counterpoint,
                   durations: List<Int> = listOf(240), // 1/8
                   ensembleParts: List<EnsemblePart>,
                   nuances: Int,
                   doublingFlags: Int): List<MidiTrack> {
        val result = mutableListOf<MidiTrack>()

        if(counterpoint.parts.size > 15) {
            println("WARNING: Counterpoint n. parts: ${counterpoint.parts.size}")
        }

        counterpoint.parts.forEachIndexed { partIndex, part ->
            val channel =
                if (partIndex < 9) partIndex else partIndex + 1 // skip percussion midi channel
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
            val lowLimit = 0.4f
            val minNuance = 0.51f
            val maxNuance = 0.95f
            val stabilities = counterpoint.findStabilities()
            val velocities: IntArray = when(nuances) {
                1 -> {val mssq = MelodySubSequencer(actualPitches)
                        val topNuances = findTopNuances(stabilities, minNuance, maxNuance)
                        mssq.assignVelocities(topNuances[partIndex], lowLimit)
                        mssq.velocities}
                2 -> {val mssq = MelodySubSequencer(actualPitches)
                        val topNuances = findTopNuances(stabilities, maxNuance, minNuance)
                        mssq.assignVelocities(topNuances[partIndex], lowLimit)
                        mssq.velocities}
                else -> IntArray(actualPitches.size) { 100 } // case 0: no Nuances
            }
            if (doublingFlags == 0) {
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
            } else {
                val doubling = convertFlagsToInts(doublingFlags)
                while (index < actualPitches.size) {
                    val pitch = actualPitches[index]
                    val velocity = velocities[index]
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
                            doubling.forEach {
                                insertNote(
                                    track, tick, dur, channel, pitch + it,
                                    velocity, 80
                                )
                            }
                        }
                        tick += dur
                        index++
                        durIndex++
                    }
                }
                result.add(track)
            }
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
    fun insertNoteCheckingHigh(
        mt: MidiTrack, start: Int, duration: Int, channel: Int,
        pitch: Int, velOn: Int, velOff: Int
    ) {
        var actualPitch = pitch
        while (actualPitch > 108){
            actualPitch -= 12
        }
        val on = NoteOn(start.toLong(), channel, actualPitch, velOn)
        val off = NoteOff((start + duration).toLong(), channel, actualPitch, velOff)
        mt.insertEvent(on)
        mt.insertEvent(off)
    }
}
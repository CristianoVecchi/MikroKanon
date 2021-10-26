package com.cristianovecchi.mikrokanon.AIMUSIC


import com.cristianovecchi.mikrokanon.convertFlagsToInts
import com.cristianovecchi.mikrokanon.extractFromMiddle
import com.leff.midi.MidiTrack
import com.leff.midi.event.*
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
                   doublingFlags: Int,
                   rangeType: Int,
                   melodyType: Int,
                    glissando: List<Int> = listOf(-1,1)
        ): List<MidiTrack> {
        val result = mutableListOf<MidiTrack>()

        if(counterpoint.parts.size > 15) {
            println("WARNING: Counterpoint n. parts: ${counterpoint.parts.size}")
        }

        counterpoint.parts.forEachIndexed { partIndex, part ->
            val ensemblePart = ensembleParts[partIndex]
            val channel =
                if (partIndex < 9) partIndex else partIndex + 1 // skip percussion midi channel
            val track = MidiTrack()
            val pc: MidiEvent =
                ProgramChange(0, channel, ensemblePart.instrument) // cambia strumento
            track.insertEvent(pc)
//            if(glissando.isNotEmpty()){
//                val omniOff = Controller(0, channel,124,0)
//                val omniOn = Controller(0, channel,125,0)
//                val monophonicOn = Controller(0, channel,126,1)
//                track.insertEvent(omniOff)
//                track.insertEvent(monophonicOn)
//            }
            var tick = 0
            var index = 0
            var durIndex = 0
            val range = when(rangeType) {
                    1 -> ensemblePart.allRange
                    2 -> ensemblePart.colorRange
                    3 -> ensemblePart.colorRange.extractFromMiddle(8)
                    4 -> ensemblePart.colorRange.extractFromMiddle(6)
                    else -> PIANO_ALL
            }
            val actualPitches = Insieme.findMelody(ensemblePart.octave, part.absPitches.toIntArray(), range.first, range.last, melodyType)
            val glissandoChecks = Insieme.checkIntervalsInPitches(actualPitches, glissando.toIntArray())
            val lowLimit = 0.4f
            val minNuance = 0.51f
            val maxNuance = 0.97f
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
                    var gliss = glissandoChecks[index]
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
                                gliss = glissandoChecks[index + 1]
                                if (nextDur < 0) {
                                    gliss = 0
                                    nextDur *= -1
                                    dur += nextDur
                                    durIndex++
                                } else {

                                    dur += nextDur
                                    index++
                                    durIndex++
                                }
                            }

                            insertNoteWithGlissando(
                                track, tick.toLong(), dur.toLong(), channel, pitch,
                                velocity, 80, gliss
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
                    var gliss = glissandoChecks[index]
                    var dur = durations[durIndex % durations.size]
                    if (dur < 0) { // negative values are considered as rests
                        dur *= -1
                        tick += dur
                        durIndex++
                    } else {
                        if (pitch != -1) {
                            while (index + 1 < actualPitches.size && actualPitches[index + 1] == pitch) {
                                var nextDur = durations[(durIndex + 1) % durations.size]
                                gliss = glissandoChecks[index + 1]
                                if (nextDur < 0) {
                                    gliss = 0
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
                                insertNoteWithGlissando(
                                    track, tick.toLong(), dur.toLong(), channel, pitch + it,
                                    velocity, 80, gliss
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
    fun insertNoteWithGlissando(
        mt: MidiTrack, start: Long, duration: Long, channel: Int,
        pitch: Int, velOn: Int, velOff: Int, gliss: Int
    ) {
        if(gliss != 0) {
           // val portamentoOn = Controller(start, channel,65,127)
            if(gliss == 1) {
                val durationQuarter = duration / 4
                val pitchBendOn1= PitchBend(start + durationQuarter, channel,0,0)
                val pitchBendOn2= PitchBend(start + durationQuarter * 2, channel,0,0)
                val pitchBendOn3= PitchBend(start + durationQuarter * 3, channel,0,0)
                val pitchBendOff= PitchBend(start , channel,0,0)
                // 0 4096 8192 12288 (14335) 16383
                pitchBendOn1.bendAmount = 1024
                pitchBendOn2.bendAmount = 2048
                pitchBendOn3.bendAmount = 3072
                pitchBendOff.bendAmount = 0
                val on = NoteOn(start, channel, pitch, velOn)
                val off = NoteOff(start + duration, channel, pitch, velOff)
                mt.insertEvent(pitchBendOff)
                mt.insertEvent(on)

                mt.insertEvent(pitchBendOn1)
                mt.insertEvent(pitchBendOn2)
                mt.insertEvent(pitchBendOn3)
                mt.insertEvent(off)
            } else if (gliss == -1){
                val pitchBendOn= PitchBend(start + (duration/4 * 3), channel,0,0)
                val pitchBendOff= PitchBend(start , channel,0,0)
                // 0 4096 8192 12288 (14335) 16383
                pitchBendOn.bendAmount = 0
                pitchBendOff.bendAmount = 0
                val on = NoteOn(start, channel, pitch, velOn)
                val off = NoteOff(start + duration, channel, pitch, velOff)
                mt.insertEvent(pitchBendOff)
                mt.insertEvent(on)
                mt.insertEvent(pitchBendOn)
                mt.insertEvent(off)
            }

            // 5 - 37
//            val portamentoTimeCoarse = Controller(start, channel, 5, 100)
//            val portamentoTimeFine = Controller(start, channel, 37, fine)
//            val portamentoAmount = Controller(start, channel, 84, 60)
//            val portamentoOff = Controller(start+duration, channel,65, 0)

            //0b10000000000001 = 8193 bend off

            //mt.insertEvent(portamentoOn)
            //mt.insertEvent(portamentoTimeCoarse)
           // mt.insertEvent(portamentoTimeFine)
           // mt.insertEvent(portamentoAmount)


           // mt.insertEvent(portamentoOff)


             println("GLISSANDO: $pitch at $start")
        } else {
            val on = NoteOn(start, channel, pitch, velOn)
            val off = NoteOff(start + duration, channel, pitch, velOff)
            mt.insertEvent(on)
            mt.insertEvent(off)
        }
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

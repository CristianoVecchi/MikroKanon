package com.cristianovecchi.mikrokanon.AIMUSIC

import com.leff.midi.MidiTrack
import com.leff.midi.event.MidiEvent
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn
import com.leff.midi.event.ProgramChange

object CounterpointInterpreter {
    fun doTheMagic(counterpoint: Counterpoint,
                   durations: List<Int> = listOf(240), // 1/8
                   ensembleType: EnsembleType = EnsembleType.STRINGS): List<MidiTrack> {
        val result = mutableListOf<MidiTrack>()
        val ensParts: List<EnsemblePart> = Ensembles.getEnsemble(counterpoint.parts.size, ensembleType)
        counterpoint.parts.forEachIndexed { partIndex, part ->
            val channel = partIndex + 1
            val track = MidiTrack()
            val pc: MidiEvent = ProgramChange(0, channel, ensParts[partIndex].instrument) // cambia strumento
            track.insertEvent(pc)

            var tick = 0
            var index = 0
            val actualPitches = Insieme.linearMelody(ensParts[partIndex].octave, part.absPitches.toIntArray(),21,108)
            while(index < actualPitches.size) {
                val pitch = actualPitches[index]
                var dur = durations[index % durations.size]

                if(pitch != -1 ) {

                    while(index + 1 < actualPitches.size && actualPitches[index + 1] == pitch){
                        dur += durations[ (index + 1) % durations.size ]
                        index++
                    }
                    insertNote(
                        track, tick, dur, channel, pitch,
                       100, 80
                    )
                }
                tick += dur
                index++
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
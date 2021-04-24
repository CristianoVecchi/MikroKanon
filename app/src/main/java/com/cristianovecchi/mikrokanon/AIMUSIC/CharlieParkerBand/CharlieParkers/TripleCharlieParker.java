package com.cristianovecchi.mikrokanon.AIMUSIC.CharlieParkerBand.CharlieParkers;

import com.cristianovecchi.mikrokanon.AIMUSIC.BebopMelody;
import com.cristianovecchi.mikrokanon.AIMUSIC.CharlieParkerBand.CharlieParker;
import com.cristianovecchi.mikrokanon.AIMUSIC.MelodySubSequencer;
import com.cristianovecchi.mikrokanon.G;
import com.cristianovecchi.mikrokanon.AIMUSIC.BebopMelody;
import com.cristianovecchi.mikrokanon.AIMUSIC.Chord;
import com.cristianovecchi.mikrokanon.AIMUSIC.ChordSequence;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.ProgramChange;

public class TripleCharlieParker extends CharlieParker {
    @Override
    public MidiTrack doTheMagicMan(MidiTrack track, BebopMelody bebopMelody, ChordSequence chordSequence, float bpm, int soloInstrument, float shuffle) {
        int channel = 0; int velOnBeat = 80; int velOff = 70;
        int velOnPassage = 110;
        MidiEvent pc = new ProgramChange(0, 0, soloInstrument);// cambia strumento
        track.insertEvent(pc);

        BebopMelody bm = bebopMelody;
        float dur; float tick; int velOn;
        MelodySubSequencer mssq = new MelodySubSequencer(bm);
        mssq.assignVelocities(0.75f,0.3f);
        int[] velocities = mssq.velocities;

        int[] notePitches2 = bm.findLowerPitches(bm.notePitches, chordSequence);
        int[] notePitches3 = bm.findLowerPitches(notePitches2, chordSequence);
        for (int i = 0; i <bm.notePitches.length ; i++) {
            if(bm.noteQualities[i]==BebopMelody.BEAT_NOTE){
                dur = (float)bm.noteDurations[i] + (((float)bm.noteDurations[i])*(1f-shuffle));
                tick = (float)bm.noteTicks[i];
                velOn = velOnBeat;
            } else {
                dur = (float)bm.noteDurations[i] - (((float)bm.noteDurations[i])*(1f-shuffle));
                tick = (float)bm.noteTicks[i] + (((float)bm.noteDurations[i])*(1f-shuffle));
                velOn = velOnPassage;
            }
            if(bm.playableNotes[i] == BebopMelody.PLAYABLE_NOTE){

                insertNote(track,(int)tick,(int)dur,channel, bm.notePitches[i],byVolume(velocities[i]), velOff);
                insertNote(track,(int)tick,(int)dur,channel, notePitches2[i],byVolume(velocities[i]), velOff);
                insertNote(track,(int)tick,(int)dur,channel, notePitches3[i],byVolume(velocities[i]), velOff);
            }


        }


        return track;
    }
}

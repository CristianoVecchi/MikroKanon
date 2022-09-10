package com.cristianovecchi.mikrokanon.AIMUSIC.CharlieParkerBand.BebopBands;

import com.cristianovecchi.mikrokanon.AIMUSIC.CharlieParkerBand.BebopBand;
import com.cristianovecchi.mikrokanon.AIMUSIC.Chord;
import com.cristianovecchi.mikrokanon.AIMUSIC.ChordSequence;
import com.cristianovecchi.mikrokanon.AIMUSIC.DEF;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.ProgramChange;

public class SymphonicBand extends BebopBand {
    @Override
    public MidiTrack[] doTheMagicGuys(ChordSequence chordSequence) {
        int[] instr = {73,68,48, 60, 70, 43};//flute, oboe, ens.strings, fr.horns, bassoon , doublebass
        int[] instrVelDiffs = {-12,-12,0,-3,10, 7};
        int velocity = 55;//main
        MidiTrack[] tracks = new MidiTrack[instr.length];
        for (int i = 0; i < tracks.length ; i++) {
            MidiEvent pc = new ProgramChange(0, i+1, instr[i]);
            tracks[i] = new MidiTrack();
            tracks[i].insertEvent(pc);
        }
        ChordSequence chs = chordSequence;
        Chord[] chords = chs.getChords();
        int nChords = chords.length;



        // choose the FIRST voicing type, 3rd or 7th

        int[][]totalVoicingPitches = findVoicingPitches(chs,4);
        //flutes
        int count = 0;
        int[] chordTicks = chs.getChordTicks();
        int[] chordDurations = chs.getChordDurations();
        for (int[] pitches: totalVoicingPitches
        ) {
            if(pitches==null)continue;
            for (int i = 2; i < pitches.length; i++)

            { //aggiungi note dell'accordo
               // System.out.print(pitch+" ");
                insertNote(tracks[0], chordTicks[count],
                        chordDurations[count]-RIBATTUTO_SAVER, 1,
                        pitches[i]+24, byVolume(velocity+instrVelDiffs[0]), 0);
            }
            count++;
        }
        // oboes
        count = 0;
        for (int[] pitches: totalVoicingPitches
        ) {
            if(pitches==null)continue;
            for (int i = 0; i < pitches.length-2; i++)

            { //aggiungi note dell'accordo
                // System.out.print(pitch+" ");
                insertNote(tracks[1], chordTicks[count],
                        chordDurations[count]-RIBATTUTO_SAVER, 2,
                        pitches[i]+24,byVolume(velocity+instrVelDiffs[1]), 0);
            }
            count++;
        }
        // ens.strings
        count = 0;
        for (int[] pitches: totalVoicingPitches
        ) {
            if(pitches==null)continue;
            for (int i = 0; i < pitches.length; i++)

            { //aggiungi note dell'accordo
                // System.out.print(pitch+" ");
                insertNote(tracks[2], chordTicks[count],
                        chordDurations[count]-RIBATTUTO_SAVER, 3,
                        pitches[i]+12, byVolume(velocity+instrVelDiffs[2]), 0);
            }
            count++;
        }
        // fr.horns
        count = 0;
        for (int[] pitches: totalVoicingPitches
        ) {
            if(pitches==null)continue;
            for (int i = 0; i < pitches.length; i++)

            { //aggiungi note dell'accordo
                // System.out.print(pitch+" ");
                insertNote(tracks[3], chordTicks[count],
                        chordDurations[count]-RIBATTUTO_SAVER, 4,
                        pitches[i], byVolume(velocity+instrVelDiffs[3]), 0);
            }
            count++;
        }
        // bass for bassoons
        count = 0;
        for (Chord ch: chords
             ) {
            insertNote(tracks[4], chordTicks[count],
                    chordDurations[count]-RIBATTUTO_SAVER, 5,
                    ch.getRoot()+ DEF.MIDDLE_C-24, byBassVolume(velocity+instrVelDiffs[4]), 0);
            count++;
        }
        count = 0;
        for (Chord ch: chords
        ) {
            insertNote(tracks[5], chordTicks[count],
                    chordDurations[count]-RIBATTUTO_SAVER, 6,
                    ch.getRoot()+ DEF.MIDDLE_C-36,byBassVolume(velocity+instrVelDiffs[5]), 0);
            count++;
        }

        return tracks;
    }
}

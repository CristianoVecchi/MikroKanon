package com.cristianovecchi.mikrokanon.AIMUSIC.CharlieParkerBand.BebopBands;


import com.cristianovecchi.mikrokanon.AIMUSIC.CharlieParkerBand.BebopBand;
import com.cristianovecchi.mikrokanon.AIMUSIC.Chord;
import com.cristianovecchi.mikrokanon.AIMUSIC.ChordSequence;
import com.cristianovecchi.mikrokanon.AIMUSIC.DEF;
import com.cristianovecchi.mikrokanon.AIMUSIC.Interval;
import com.cristianovecchi.mikrokanon.AIMUSIC.JazzInterpreter;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.ProgramChange;

import java.util.List;

public class TheBebopBoys extends BebopBand {
    @Override
    public MidiTrack[] doTheMagicGuys(ChordSequence chordSequence) {
        MidiTrack track = new MidiTrack();
        int channel = 1; int velocity = 97;
        MidiEvent pc = new ProgramChange(0, channel, 48);// strumento STRING
        track.insertEvent(pc);
        ChordSequence chs = chordSequence;
        Chord[] chords = chs.getChords();
        int nChords = chords.length;
        int[] chordTicks = chs.getChordTicks();
        int[] chordDurations = chs.getChordDurations();

        for (int i = 0; i < nChords; i++) {
            Chord chord = chords[i];
            //System.out.println("Chord at "+i+" = "+ chord.getDef());
            int root = chord.getRoot();
            List<Interval> intervals = JazzInterpreter.getIntervals(chord.getChord());
            List<Interval> thirds = Interval.orderByThirds(intervals);
            //System.out.println("THIRDS = "+ thirds.toString());
            int actualFund = DEF.MIDDLE_C + root - 24;
            int[] pitches = new int[thirds.size()];
            for (int j = 0; j < thirds.size(); j++) {
                int actualThird = actualFund + 12 + thirds.get(j).getHalftones();
                if(thirds.get(j).getNoteDistance()%2 != 0) actualThird += 12;
                pitches[j] = actualThird;
            }
            int time = chordTicks[i];
            int dur  = chordDurations[i];
            //System.out.println("FUND = "+ fund+ " actualfund = "+ actualFund+ "  "+Arrays.toString(pitches));
            insertNote(track, time  ,dur-RIBATTUTO_SAVER , channel, actualFund, byBassVolume(velocity), 0);
            for (int pitch : pitches) { //aggiungi note dell'accordo
                insertNote(track, time, dur-RIBATTUTO_SAVER, channel, pitch, byVolume(velocity), 0);
            }


        }
        MidiTrack[] tracks = new MidiTrack[1];
        tracks[0]= track;
        return tracks;


    }
}

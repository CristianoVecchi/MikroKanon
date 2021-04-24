package com.cristianovecchi.mikrokanon.AIMUSIC.CharlieParkerBand.BebopBands;

import com.cristianovecchi.mikrokanon.AIMUSIC.CharlieParkerBand.BebopBand;
import com.cristianovecchi.mikrokanon.AIMUSIC.Chord;
import com.cristianovecchi.mikrokanon.AIMUSIC.ChordSequence;
import com.cristianovecchi.mikrokanon.G;
import com.cristianovecchi.mikrokanon.AIMUSIC.ChordSequence;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.ProgramChange;

public class VoicingBand extends BebopBand {
    @Override
    public MidiTrack[] doTheMagicGuys(ChordSequence chordSequence) {
        int channel = 1; int velocity = 85;
        MidiTrack track = new MidiTrack();
        MidiEvent pc = new ProgramChange(0, channel, 48);// strumento PIANO
        track.insertEvent(pc);
        ChordSequence chs = chordSequence;


        Chord[] chords = chs.getChords();
        int nChords = chords.length;
        int[] chordTicks = chs.getChordTicks();
        int[] chordDurations = chs.getChordDurations();
        // choose the FIRST voicing type, 3rd or 7th
        int[][] totalVoicingPitches = findVoicingPitches(chs, 4);

        int count = 0;
        for (int[] pitches: totalVoicingPitches
             ) {
            if(pitches == null) continue;
            for (int pitch : pitches) { //aggiungi note dell'accordo
                System.out.print(pitch+" ");
                insertNote(track, chordTicks[count],
                        chordDurations[count]-RIBATTUTO_SAVER, channel,
                        pitch,byVolume(velocity), 0);
            }
            count++;
            System.out.println();

        }
        MidiTrack[] tracks = new MidiTrack[1];
        tracks[0]= track;
        return tracks;
    }


}

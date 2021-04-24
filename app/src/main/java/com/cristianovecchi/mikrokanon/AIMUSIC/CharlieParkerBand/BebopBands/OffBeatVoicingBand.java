package com.cristianovecchi.mikrokanon.AIMUSIC.CharlieParkerBand.BebopBands;

import com.cristianovecchi.mikrokanon.AIMUSIC.AIRhythm;
import com.cristianovecchi.mikrokanon.AIMUSIC.CharlieParkerBand.BebopBand;
import com.cristianovecchi.mikrokanon.AIMUSIC.Chord;
import com.cristianovecchi.mikrokanon.AIMUSIC.ChordSequence;
import com.cristianovecchi.mikrokanon.G;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.ProgramChange;

public class OffBeatVoicingBand extends BebopBand {
    @Override
    public MidiTrack[] doTheMagicGuys(ChordSequence chordSequence) {
        MidiTrack track = new MidiTrack();
        MidiTrack bassTrack = new MidiTrack();

        int channel = 1;
        int bassChannel = 2;
        int velocity = 70; int bassVelocity = 127;

        float staccato = 0.90f; int oct = (int)(G.quarter/2) ;
        // 25 steel guitar, 26 jazz guitar
        MidiEvent pc = new ProgramChange(0, channel, 26);//50  strumento synth string
        track.insertEvent(pc);
        MidiEvent pc2 = new ProgramChange(0, bassChannel, 45);// strumento pizz. string
        bassTrack.insertEvent(pc2);
        ChordSequence chs = chordSequence;
        Chord[] chords = chs.getChords();
        int nChords = chords.length;
        int[] chordTicks = chs.getChordTicks();
        int[] chordDurations = chs.getChordDurations();
        int[][]totalVoicingPitches = findVoicingPitches(chs,4);


        for (int i = 0; i < nChords; i++) {
            int[] pitches = totalVoicingPitches[i];
           // int off;
            //int on;
            int dur8 = chordDurations[i]/oct;
            /*if(dur8==1){
                off=0;
               // on = 1;
            } else {
                off = dur8/2 + dur8%2;
                //on = dur8/2;
            }*/
            if(pitches==null)continue;
            int[] offBeats = AIRhythm.findOffBeats(dur8/2);
            for (int q = 0; q < offBeats.length ; q++) {
                double random = Math.random();
                int delay = random>0.9f ? (int)(oct*0.33f*2): 0;
                for (int j = 0; j <pitches.length ; j++) {
                    insertNote(track, chordTicks[i]+offBeats[q]*oct*2+delay,
                            (int)(oct*staccato),channel,
                            pitches[j],byVolume(velocity), 0);
                }
            }
            int nBassNotes = dur8/2 + dur8%2;
            for (int q = 0; q <nBassNotes ; q++) {
                int bassPitch = 0;
                double random = Math.random();
                if(random<0.40){
                    bassPitch = chords[i].getRoot()+24;
                } else {
                    bassPitch = pitches[(int)(Math.random()*(pitches.length))]-24;
                }
                double random8 = Math.random();
                if(random8>0.50){
                    bassPitch+=12;
                }
                if(random8>0.80){
                    bassPitch+=12;
                }
                if(bassPitch<21) bassPitch += 24;
                int delay = Math.random()>0.9f ? (int)(oct*0.33f*2): 0;
                insertNote(bassTrack, chordTicks[i]+q*oct*2+delay,
                        oct,bassChannel,
                        bassPitch,byBassVolume(bassVelocity), 0);
            }



        }



        return new MidiTrack[]{track,bassTrack};
    }
}

package com.cristianovecchi.mikrokanon.AIMUSIC.CharlieParkerBand;

import com.cristianovecchi.mikrokanon.AIMUSIC.Chord;
import com.cristianovecchi.mikrokanon.AIMUSIC.ChordSequence;
import com.cristianovecchi.mikrokanon.AIMUSIC.DEF;
import com.cristianovecchi.mikrokanon.AIMUSIC.Interval;
import com.cristianovecchi.mikrokanon.AIMUSIC.JazzChord;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;

abstract public class BebopBand {
    public static String[] AVAILABLE_BANDS = {"OffBeatVoicingBand","SymphonicBand","TheBebopBoys",
                                        "VoicingBand"};
    protected static int RIBATTUTO_SAVER = 20;
    abstract public MidiTrack[] doTheMagicGuys(ChordSequence chordSequence);

    protected int volume=95;
    public void setVolume(int volume){
        this.volume = volume;
    }
    public int getVolume(){
        return volume;
    }
    protected int bassVolume=104;
    public void setBassVolume(int bassVolume){
        this.bassVolume = bassVolume;
    }
    public int getBassVolume(){
        return bassVolume;
    }
    public int byVolume(int value){
        return (int)(((1.0f/127) * volume) * value);

    }
    public int byBassVolume(int value){
        return (int)(((1.0f/127) * bassVolume) * value);

    }
    protected void insertNote(MidiTrack mt, int start, int duration, int channel,
                           int pitch, int velOn, int velOff){
        NoteOn on = new NoteOn(start, channel, pitch, velOn);
        NoteOff off = new NoteOff(start+duration, channel, pitch, velOff);
        mt.insertEvent(on);
        mt.insertEvent(off);
    }
    protected int[][] findVoicingPitches(ChordSequence chordSequence, int nParts){
        Chord[] chords = chordSequence.getChords();
        int nChords = chords.length;
        int[][] totalPitches = new int[nChords][];
        Interval[][] intervals = chordSequence.findVoicing_13(nParts);
        int first = 0; int lastLowerPitch=-1;
        for (int i = 0; i < nChords; i++) {
            Chord chord = chords[i];
            if(intervals[i]==null){
                System.out.println("INTERVALS = NULL, JAZZCHORD DEF:"+chord.getChord().getDef());
                first++;
                continue;
            }

            if(chord.getChord()== JazzChord.EMPTY) {
                first++; continue;
            }
            int root = chord.getRoot();
            int[] pitches = new int[intervals[i].length];
            int actualRoot = DEF.MIDDLE_C + root - 12;
            int lastPitch = 0;

            for (int j = 0; j < intervals[i].length; j++) {
                if(intervals[i][j]==null){
                    System.out.println("INTERVAL NULL, JAZZCHORD DEF:"+chord.getChord().getDef());
                    continue;
                }
                int actualPitch = actualRoot + intervals[i][j].getHalftones();
                if (actualPitch < lastPitch) actualPitch += 12;
                lastPitch = actualPitch;

                pitches[j] = actualPitch;

            }
            if(i==first){
                centerPitches(pitches, DEF.MIDDLE_C);
            } else {
                centerPitches(pitches, DEF.MIDDLE_C);
                normalize(pitches, lastLowerPitch);
            }
            lastLowerPitch = pitches[0];


            totalPitches[i]=pitches;
        }
        return totalPitches;
    }
    protected void normalize(int[] pitches, int lastLowerPitch) {
        int lowerPitch = pitches[0];
        if(lowerPitch-lastLowerPitch>6){
            for (int q = 0; q < pitches.length ; q++) {
                pitches[q]-=12;
            }
            return;
        }
        if(lowerPitch-lastLowerPitch<-6){
            for (int q = 0; q < pitches.length ; q++) {
                pitches[q]+=12;
            }
            return;
        }
    }

    protected void centerPitches(int[]pitches, int center){
        if(pitches[0]>=center){
            for (int q = 0; q < pitches.length ; q++) {
                pitches[q]-=12;

            }
        }

    }
}

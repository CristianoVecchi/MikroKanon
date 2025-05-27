package com.cristianovecchi.mikrokanon.AIMUSIC.CharlieParkerBand;

import com.cristianovecchi.mikrokanon.AIMUSIC.BebopMelody;
import com.cristianovecchi.mikrokanon.AIMUSIC.ChordSequence;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;

abstract public class CharlieParker {

    public static String[] AVAILABLE_CHARLIES = {"YoungCharlieParker","TripleCharlieParker"};
    abstract public MidiTrack doTheMagicMan(MidiTrack track, BebopMelody bebopMelody, ChordSequence chordSequence, float bpm, int soloInstrument, float shuffle);
    protected void insertNote(MidiTrack mt, int start, int duration, int channel,
                              int pitch, int velOn, int velOff){
        NoteOn on = new NoteOn(start, channel, pitch, velOn);
        NoteOff off = new NoteOff(start+duration, channel, pitch, velOff);
        mt.insertEvent(on);
        mt.insertEvent(off);
    }
    protected int volume=120;
    public void setVolume(int volume){
        this.volume = volume;
    }
    public int getVolume(){
        return volume;
    }
    public int byVolume(int value){
        return (int)(((1.0f/127) * volume) * value);

    }
}

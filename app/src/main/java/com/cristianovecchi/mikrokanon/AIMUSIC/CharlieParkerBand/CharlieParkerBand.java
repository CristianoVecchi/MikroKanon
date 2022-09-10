package com.cristianovecchi.mikrokanon.AIMUSIC.CharlieParkerBand;

import com.cristianovecchi.mikrokanon.AIMUSIC.BebopMelody;
import com.cristianovecchi.mikrokanon.AIMUSIC.ChordSequence;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.ProgramChange;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.MidiFile;

import java.util.ArrayList;

// this class put together the solo player and the band
public class CharlieParkerBand {

    private BebopBand band;
    private CharlieParker charlie;

    public MidiFile playScheme(BebopBand bebopBand, CharlieParker charlieParker, BebopMelody bebopMelody,
                               ChordSequence chordSequence, float bpm, int soloInstrument, float shuffle){
        band = bebopBand; charlie = charlieParker;
        MidiTrack tempoTrack = new MidiTrack();
        MidiTrack charlieTrack = new MidiTrack();
        MidiTrack[] bandTracks;
        // 2. Add events to the tracks
        // 2a. Track 0 is typically the tempo map
        Tempo t = new Tempo();
        t.setBpm(bpm);
        tempoTrack.insertEvent(t);

        // 2b. Track 1 will have some notes in it
        int time = 0; int channel = 0; // channel of the solo part
        int velocity = 110; int instr = soloInstrument; // piano



        MidiEvent pc = new ProgramChange(0, channel,instr);// cambia strumento
        charlieTrack.insertEvent(pc);

        charlieTrack = charlie.doTheMagicMan(charlieTrack, bebopMelody,  chordSequence,bpm,soloInstrument,shuffle);
        bandTracks = band.doTheMagicGuys(chordSequence);


        // 3. Create a MidiFile with the tracks we created
        ArrayList<MidiTrack> tracks = new ArrayList<MidiTrack>();
        tracks.add(tempoTrack);
        tracks.add(charlieTrack);
        for (MidiTrack tr: bandTracks
             ) {
            tracks.add(tr);
        }

        return new MidiFile(MidiFile.DEFAULT_RESOLUTION,tracks); // Def_res = 480 quarter?


    }

    public void insertNote(MidiTrack mt, int start, int duration, int channel,
                           int pitch, int velOn, int velOff){
        NoteOn on = new NoteOn(start, channel, pitch, velOn);
        NoteOff off = new NoteOff(start+duration, channel, pitch, velOff);
        mt.insertEvent(on);
        mt.insertEvent(off);
    }
    public int chooseFund(int[] funds){
        return funds[(int)(Math.random()*funds.length)];
    }
}

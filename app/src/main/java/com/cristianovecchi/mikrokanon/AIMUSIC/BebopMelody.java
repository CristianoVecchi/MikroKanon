package com.cristianovecchi.mikrokanon.AIMUSIC;

import android.util.Log;


import com.cristianovecchi.mikrokanon.ui.G;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/** Class that encapsule in array the attributes of notes */
public class BebopMelody {

    private static String TAG = "BebopMelody";
    public int[] notePitches,  noteQualities;
    public long[] noteTicks, noteDurations;
    public int[] playableNotes;
    public static final int INDETERMINED_NOTE = -1, BEAT_NOTE = 0;
    public static final int PASSAGE_NOTE = 1 , REST_NOTE = 2;
    public static final int GHOST_NOTE = 3, PLAYABLE_NOTE = 4;
    public int[][] inWhichChord;

    /** Factory Method */
    public static BebopMelody createBebopMelody(int[] notePitches, long[] noteTicks,
                                                long[] noteDurations, int[] noteQualities) {
        int total = notePitches.length;
        if ((total != noteTicks.length) | (total != noteDurations.length)
            | (total != noteQualities.length)){
            Log.d(TAG, "number of elements is not the same in the factory method");
            return null;
        }
       return new BebopMelody(notePitches, noteTicks, noteDurations, noteQualities);
    }

    private BebopMelody(int[] notePitches, long[] noteTicks,
                        long[] noteDurations, int[] noteQualities){
        this.notePitches = notePitches; this.noteDurations = noteDurations;
        this.noteTicks = noteTicks; this.noteQualities = noteQualities;
        playableNotes = new int[notePitches.length];
        Arrays.fill(playableNotes,PLAYABLE_NOTE);
    }

    /** creates  a starting melody with 8notes * 4bars = 32 notes */
    public static BebopMelody createDefaultMelody(){
        int nNotes = 32;
        int[] notePit = new int[nNotes]; long[] noteTic = new long[nNotes];
        int[] noteQual = new int[nNotes]; long[] noteDur = new long[nNotes];

        long dur = G.totalPieceTickDuration / nNotes;
        for (int i = 0; i <nNotes ; i++) {
            notePit[i] = 60 + (i%2); //C4 central
            noteTic[i] = i*dur;
            noteDur[i] = dur;
            noteQual[i] = i%2; //alternates beat and passage notes
        }
        return new BebopMelody(notePit,noteTic,noteDur,noteQual);

    }

    public static BebopMelody createRandomMelody(int nNotes, int soloRangeLowerNote, int soloRangeUpperNote) {
        int[] notePit = new int[nNotes]; long[] noteTic = new long[nNotes];
        int[] noteQual = new int[nNotes]; long[] noteDur = new long[nNotes];
        long dur = G.totalPieceTickDuration / nNotes;
        Random generator=new Random();
        int[] intervals = {1,-1,2,-2,3,-3,4,-4,5,-5,6,-6};
        int pitch = generator.nextInt(soloRangeUpperNote-soloRangeLowerNote)+soloRangeLowerNote;
        for (int i = 0; i <nNotes ; i++) {
            pitch += intervals[generator.nextInt(intervals.length)];
            if (pitch>=soloRangeUpperNote) pitch = pitch -12;
            if (pitch<=soloRangeLowerNote) pitch = pitch +12;
            notePit[i] = pitch ;
            noteTic[i] = i*dur;
            noteDur[i] = dur;
            noteQual[i] = i%2; //alternates beat and passage notes
        }
        BebopMelody bm =  new BebopMelody(notePit,noteTic,noteDur,noteQual);
        //bm.assignChordsToNotes(G.currentChordSequence.notesInChord);
        int nRandom = (int)(((float)nNotes/100) *15); // add 15% ghost notes
        for (int i = 0; i <nRandom ; i++) {
            int index = (int)(Math.random()*nNotes);
            bm.playableNotes[index]=BebopMelody.GHOST_NOTE;
        }
        return bm;
    }
    public void upperPitches(ChordSequence chordSequence){
        this.notePitches = findUpperPitches(this.notePitches, chordSequence);
    }
    public void lowerPitches(ChordSequence chordSequence){
        this.notePitches = findLowerPitches(this.notePitches, chordSequence);
    }

    public int[] findLowerPitches(int[] notePitches, ChordSequence chordSequence) {
        int nNotes = notePitches.length;
        int[] lowerPitches = new int[nNotes];
        for (int i = 0; i < nNotes; i++) {
            int  chordIndex = inWhichChord[i][0];
            int  noteByte = 0;
            int root = chordSequence.getChords()[chordIndex].getRoot();
            if(noteQualities[i]== BebopMelody.BEAT_NOTE){
                noteByte = chordSequence.getBeatBytes()[chordIndex];
            } else if(noteQualities[i] == BebopMelody.PASSAGE_NOTE){
                if(chordSequence.isInBeatChord(chordIndex,noteQualities[i])){
                    noteByte = chordSequence.getBeatBytes()[chordIndex];
                } else {
                    noteByte = chordSequence.getPassageBytes()[chordIndex];
                }
            }
            noteByte = Insieme.rotate(root, noteByte);
            int lowerPitch = Insieme.findLowerChordNote(notePitches[i], noteByte);
            lowerPitches[i]=lowerPitch;
           // System.out.println("pitch="+notePitches[i]+" lower="+lowerPitch);
        }
        return lowerPitches;
    }
    public int[] findUpperPitches(int[] notePitches, ChordSequence chordSequence) {
        int nNotes = notePitches.length;
        int[] upperPitches = new int[nNotes];
        for (int i = 0; i < nNotes; i++) {
            int  chordIndex = inWhichChord[i][0];
            int  noteByte = 0;
            int root = chordSequence.getChords()[chordIndex].getRoot();
            if(noteQualities[i]== BebopMelody.BEAT_NOTE){
                noteByte = chordSequence.getBeatBytes()[chordIndex];
            } else if(noteQualities[i] == BebopMelody.PASSAGE_NOTE){
                if(chordSequence.isInBeatChord(chordIndex,noteQualities[i])){
                    noteByte = chordSequence.getBeatBytes()[chordIndex];
                } else {
                    noteByte = chordSequence.getPassageBytes()[chordIndex];
                }
            }
            noteByte = Insieme.rotate(root, noteByte);
            int upperPitch = Insieme.findUpperChordNote(notePitches[i], noteByte);
            upperPitches[i]=upperPitch;
            // System.out.println("pitch="+notePitches[i]+" lower="+lowerPitch);
        }
        return upperPitches;
    }

    public void assignChordsToNotes(int[][] notesInChords){
        inWhichChord = new int[notePitches.length][];
        for (int i = 0; i < inWhichChord.length; i++) {
            List<Integer> list = new LinkedList<Integer>();
            for (int j = 0; j < notesInChords.length; j++) {
                for (int k = 0; k < notesInChords[j].length ; k++) {
                    if(notesInChords[j][k]==i) list.add(j);
                }
            }
            inWhichChord[i] = G.convertIntegers(list);
            //System.out.println("note"+i+" @chords: "+list.toString());
        }
    }

    public void addNotes(int nNotesAdded, int tick) {
       // System.out.println("NNOTEADDED "+nNotesAdded);
        int nNotes = noteTicks.length;
        int[] newNotePitches = new int[nNotes+nNotesAdded];
        int[] newNoteQualities = new int[nNotes+nNotesAdded];
        long[] newNoteTicks = new long[nNotes+nNotesAdded];
        long[] newNoteDurations = new long[nNotes+nNotesAdded];
        int count = 0; int newCount = 0; int previousNote = 0; int saveNotesAdded = nNotesAdded;
        while(newCount<(nNotes+nNotesAdded)){
            if(noteTicks[count]<tick){
                newNoteTicks[newCount] = noteTicks[count];
                newNoteDurations[newCount] = noteDurations[count];
                newNoteQualities[newCount] = noteQualities[count];
                newNotePitches[newCount] = notePitches[count];
                previousNote = notePitches[count];
                count++; newCount++;
            } else if(noteTicks[count]>=tick & noteTicks[count]<(tick+(nNotesAdded*G.quarter/2))
                        & saveNotesAdded !=0){
                //noteTicks[count]+= nNotesAdded*(G.quarter/2);
                newNoteTicks[newCount] = tick+((newCount-count)*G.quarter/2);
                newNoteDurations[newCount] = G.quarter/2;
                newNoteQualities[newCount] = newCount%2;
                newNotePitches[newCount] = previousNote;
                newCount++; saveNotesAdded--;
            } else {
                newNoteTicks[newCount] = noteTicks[count]+(nNotesAdded*(G.quarter/2));
                newNoteDurations[newCount] = noteDurations[count];
                newNoteQualities[newCount] = noteQualities[count];
                newNotePitches[newCount] = notePitches[count];
                count++; newCount++;
            }
            //System.out.println("count:"+count+" newCount: "+newCount);
        }
        noteTicks = newNoteTicks; notePitches = newNotePitches;
        noteDurations = newNoteDurations; noteQualities = newNoteQualities;
    }
    public int getPreviousNoteIndex(int index){
        index--;
        if(index==-1) index = noteTicks.length-1;
        return index;
    }
    public int getNextNoteIndex(int index){
        index++;
        if (index == noteTicks.length) index = 0;
        return index;
    }

    public int getPreviousNoteWithQuality(int index, int QUALITY ){
        int start = index;
        for (int i = 0; i < noteTicks.length ; i++) {
            index--;
            if(index==-1) index = noteTicks.length-1;
            if (index == start) return -1;
            if (noteQualities[index] == QUALITY) return index;
        }
        return -1;
    }
    public int getNextNoteWithQuality(int index, int QUALITY ){
        int start = index;
        for (int i = 0; i < noteTicks.length ; i++) {
            index++;
            if (index == noteTicks.length) index = 0;
            if (index == start) return -1;
            if (noteQualities[index] == QUALITY) return index;
        }
        return -1;
    }

    public int[] chordsForNote(int noteSelected) {
        return inWhichChord[noteSelected];
    }

    public int transposeSingleNote(int index,int interval, int rangeLowerNote, int rangeUpperNote){
        int newNote = notePitches[index]+interval;
        notePitches[index] = newNote;
        if(newNote<rangeLowerNote) return transposeSingleNote(index,+12,rangeLowerNote,rangeUpperNote);
        if(newNote>rangeUpperNote) return transposeSingleNote(index,-12,rangeLowerNote,rangeUpperNote);
        return newNote;
    }
    public void transpose(int interval, int rangeLowerNote, int rangeUpperNote) {
        for (int i = 0; i < notePitches.length ; i++) {
            notePitches[i]= transposeSingleNote(i, interval,rangeLowerNote,rangeUpperNote);
        }
    }
    public static void insertNotesInVirtualMelody(List<ActualNote> insertList, int index,
                                                  List<ActualNote> virtualMelody){
        int durationStep = 0; //TODO change with Tick difference
        int count = 0;
        for (int i = index; i <insertList.size() + index ; i++) { //insert new notes
            ActualNote toInsert = insertList.get(count++);
            virtualMelody.add(i,toInsert);
            durationStep += toInsert.duration;
        }
        for (int i = index+insertList.size(); i <virtualMelody.size() ; i++) { //modify ticks of left notes
            ActualNote toChange = virtualMelody.get(i);
            toChange.tick = toChange.tick+durationStep;
        }
    }
    public static  void  deleteNotesInVirtualMelody(int index, int nNotes, List<ActualNote> virtualMelody){
        int durationStep = 0; //TODO change with Tick difference
        for (int i = 0; i <nNotes ; i++) {
            ActualNote toRemove = virtualMelody.get(index);
            durationStep += toRemove.duration;
            virtualMelody.remove(index);
        }
        for (int i = index; i < virtualMelody.size(); i++) {
            ActualNote toChange = virtualMelody.get(i);
            toChange.tick = toChange.tick-durationStep;
        }

    }

    public static List<ActualNote> createVirtualMelody(BebopMelody bebopMelody){
        List<ActualNote> mel = new LinkedList<>();
        for (int i = 0; i <bebopMelody.notePitches.length ; i++) {
            ActualNote note = new ActualNote(bebopMelody.notePitches[i],
                    (int)bebopMelody.noteTicks[i],(int)bebopMelody.noteDurations[i],
                    bebopMelody.noteQualities[i],bebopMelody.inWhichChord[i],
                    bebopMelody.playableNotes[i]);
        }
        return mel;
    }

    public static class ActualNote{
        public int pitch, tick, duration, quality, playability;
        public int[] inChords;

        public ActualNote(int pitch, int tick, int duration, int quality,
                          int[] inChords, int playability) {
            this.pitch = pitch;this.duration = duration;
            this.tick = tick;  this.quality = quality;this.inChords = inChords;
            this.playability = playability;
        }
        public ActualNote clone(){
            int[] newChords = Arrays.copyOf(inChords,inChords.length);// in which chords the note is
            return new ActualNote(pitch,tick,duration,quality,newChords, playability);
        }
    }

    public void checkRange(int max, int min){
        for (int i = 0; i <notePitches.length ; i++) {
            if(notePitches[i]>max){
                notePitches[i] -= 12;
                continue;
            }
            if(notePitches[i]<min) notePitches[i] += 12;
        }
    }
}

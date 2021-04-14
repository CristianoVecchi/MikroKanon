package com.cristianovecchi.mikrokanon.AIMUSIC;

import com.cristianovecchi.mikrokanon.G;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ChordSequence {
    static public String sep1level = "§";

    // it's a comma, not a dot!!! for the dot being REGEX it's necessary use: "\\."
    static public String sep2level = ",";

    static public String sep3level = "_";
    //public long lastUpdate = -1;
    private Chord[] chords;
    private int[] chordTicks, chordDurations;
    private int[] passageBytes;
    private int[] beatBytes;
    private int[][] notesInChord;
    private String title = "";
    private Interval[][] voicing;
    private Interval[][] fourPartsVoicing;

    public ChordSequence(Chord[] chords, int[] chordTicks, int[] chordDurations) {
        this.chords = chords;
        this.chordTicks = chordTicks;
        this.chordDurations = chordDurations;
        //lastUpdate = System.currentTimeMillis();
        refreshIntervalsData();
    }

    public boolean with13 = true;



    private void refreshIntervalsData() {
        if(with13){
            fourPartsVoicing = this.findVoicing_13(4);
        } else {
            fourPartsVoicing = this.findVoicing(4);
        }

    }

    public ChordSequence(List<ActualChord> sequence, String title){
        int n = sequence.size();
        this.title = title;
        chords = new Chord[n]; chordTicks = new int[n]; chordDurations = new int[n];
        beatBytes = new int[n]; passageBytes = new int[n];
        for (int i = 0; i < n ; i++) {
            ActualChord ach = sequence.get(i);
            chords[i]=ach.chord; chordTicks[i]=ach.tick; chordDurations[i]=ach.duration;
            beatBytes[i]=ach.beatByte; passageBytes[i]=ach.passageByte;
        }
        //lastUpdate = System.currentTimeMillis();
        refreshIntervalsData();
    }

    public void deleteChord(int indexChord){
        if (indexChord >= chords.length) return;

        removeElement(indexChord,chords);
        removeElementInTicks(indexChord,chordTicks,chordDurations[indexChord]);
        removeElement(indexChord,chordDurations);
        removeElement(indexChord,beatBytes);
        removeElement(indexChord,passageBytes);
        //assignNotesInChords(G.getCurrentBebopMelody());
        //G.getCurrentBebopMelody().assignChordsToNotes(this.notesInChord);
        //lastUpdate = System.currentTimeMillis();
        refreshIntervalsData();
    }

    private int[] removeElementInTicks(int element, int[] original, int duration) {
        int[] n = new int[original.length - 1];
        System.arraycopy(original, 0, n, 0, element );
        System.arraycopy(original, element+1, n, element, original.length - element-1);
        for (int i = element; i < original.length - element ; i++) {
            original[i]-=duration;
        }
        return n;
    }

    public void insertChord(int indexChord, Chord chord, int tick, int duration, int beatByte, int passageByte){
        if (indexChord >= chords.length+1) return;
        insertElement(chord,indexChord,chords);
        insertElementinTicks(tick,indexChord,chordTicks,duration);
        insertElement(duration, indexChord,chordDurations);
        insertElement(beatByte,indexChord,beatBytes);
        insertElement(passageByte,indexChord,passageBytes);
       // assignNotesInChords(G.getCurrentBebopMelody());
        //G.getCurrentBebopMelody().assignChordsToNotes(this.notesInChord);
        //lastUpdate = System.currentTimeMillis();
        refreshIntervalsData();
    }

    private int[] insertElementinTicks(int theElement, int index, int[] original,int duration) {
        int[] n = new int[original.length + 1];
        System.arraycopy(original, 0, n, 0, index );
        n[index] = theElement;
        System.arraycopy(original, index, n, index+1, original.length - index);
        for (int i = index +1; i < original.length - index ; i++) {
            original[i]+=duration;
        }
        return n;
    }

    public static Chord[] insertElement(Chord chord,int index, Chord[] original){
        Chord[] n = new Chord[original.length + 1];
        System.arraycopy(original, 0, n, 0, index ); //source, statimgPosSource, dest, startingPosDest, lenght
        n[index] = chord;
        System.arraycopy(original, index, n, index+1, original.length - index);

        return n;
    }
    public static int[] insertElement(int theElement,int index, int[] original){
        int[] n = new int[original.length + 1];
        System.arraycopy(original, 0, n, 0, index );
        n[index] = theElement;
        System.arraycopy(original, index, n, index+1, original.length - index);
        return n;
    }


    public static Chord[] removeElement(int element, Chord[] original){
        Chord[] n = new Chord[original.length - 1];
        System.arraycopy(original, 0, n, 0, element );
        System.arraycopy(original, element+1, n, element, original.length - element-1);
        return n;
    }

    public static int[] removeElement(int element, int[] original){
        int[] n = new int[original.length - 1];
        System.arraycopy(original, 0, n, 0, element );
        System.arraycopy(original, element+1, n, element, original.length - element-1);
        return n;
    }


    public void assignNotesInChords(BebopMelody bebopMelody){
        notesInChord  = new int[chords.length][];
        for (int i = 0; i < chords.length; i++) {
            List<Integer> list = new LinkedList<Integer>();
            long start = chordTicks[i]; long end = start + chordDurations[i]-1;
            for (int j = 0; j < bebopMelody.notePitches.length; j++) {
                long noteStart = bebopMelody.noteTicks[j];
                long noteEnd = noteStart + bebopMelody.noteDurations[j]-1;
                if(noteStart>end)continue;
                if(noteEnd<start)continue;
                list.add(j);


            }
            int[] notesInSingleChord = G.convertIntegers(list);
            //System.out.println("Chord: "+ chords[i].getName()+ " = "+ list.toString());
            notesInChord[i] = notesInSingleChord;
        }
        //lastUpdate = System.currentTimeMillis();
        refreshIntervalsData();
    }

    public void setBeatAndPassageBytes(Integer[][] bytes){
        beatBytes = new int[chords.length];
        passageBytes = new int[chords.length];
        for (int i = 0; i < chords.length; i++) {
            beatBytes[i] = bytes[i][0];
            passageBytes[i] = bytes[i][1];
        }
        //lastUpdate = System.currentTimeMillis();
        refreshIntervalsData();
    }
    public static ChordSequence createRandomChordSequence(int minQuantityChords, int maxQuantityChords){
        Random generator=new Random();
        if (minQuantityChords <1) minQuantityChords =1;
        if (maxQuantityChords<minQuantityChords) maxQuantityChords = minQuantityChords;
        int quantityChords = generator.nextInt(maxQuantityChords-minQuantityChords) + minQuantityChords;
        Chord[] chords = new Chord[quantityChords];
        int[] ticks = new int[quantityChords];
        int[] durs = new int[quantityChords];
        int time = 0;
        JazzChord[] JChords = JazzChord.values();
        for (int i = 0; i < quantityChords; i++) {
            Chord chord = new Chord(generator.nextInt(12),JChords[generator.nextInt(JChords.length)]);
            chords[i]= chord;
            ticks[i]= time ;
            int dur = (int) ((generator.nextInt(7) * G.quarter/2)+(G.quarter)); // from 2 to 8 halfquarters
            if (quantityChords == 1) dur = (int) ((G.quarter/2) * 3);
            durs[i] = dur;
            time += dur;
           // beatB[i] = findBeatByte(chord);
           // passB[i] = findPassageByte(chord);
        }
        ChordSequence chS = new ChordSequence(chords,ticks,durs);
        chS.setBeatAndPassageBytes(findBeatAndPassageBytes(chS));
        //chS.exportLikeTXT("MyChords");
        return chS;
    }
    public static ChordSequence createRandomChordSequenceByMatrix(ChordMatrix chordMatrix,
                                  int minQuantityChords, int maxQuantityChords, int divisor) {
        Random generator=new Random();
        if (minQuantityChords <1) minQuantityChords =1;
        if (maxQuantityChords<minQuantityChords) maxQuantityChords = minQuantityChords;
        int quantityChords = generator.nextInt(maxQuantityChords-minQuantityChords) + minQuantityChords;
        Chord[] chords = new Chord[quantityChords];
        int[] ticks = new int[quantityChords];
        int[] durs = new int[quantityChords];
        int time = 0;
        JazzChord[] JChords = JazzChord.values();

        for (int i = 0; i < quantityChords; i++) {
            Chord chord;
            if(i == 0){
                int root = generator.nextInt(12);
                JazzChord jz = chordMatrix.chooseRandomJazzChordFromWeightedList();
                chord = new Chord(root, jz);
            } else {

                chord = chordMatrix.chooseRandomChordFromWeightedList(chords[(i-1)%chords.length]);
            }

            chords[i]= chord.clone();// safer because the list has multiple pointers at unique objects
            ticks[i]= time ;
            int dur = (int) (chordMatrix.chooseRandomDurationFromWeightedList(chords[i].getChord()) * (G.quarter/2));
            if (quantityChords == 1) dur = (int) ((G.quarter/2) * 3);
            durs[i] = dur;
            time += dur;
        }
        ChordSequence chS = new ChordSequence(chords,ticks,durs);
        chS.setBeatAndPassageBytes(findBeatAndPassageBytes(chS));
        //chS.exportLikeTXT("MyChords");
        return chS;
    }
    public int getTotalDuration(){
        int dur = 0;
        for (int d : chordDurations) dur +=d;
        return dur;
    }
    public static Integer[][] findBeatAndPassageBytes(ChordSequence chordSequence){
        int nChords = chordSequence.chords.length;
        Chord[] chds = chordSequence.chords;
        Integer[][] grams = new Integer[nChords][];
        for (int i = 0; i < nChords ; i++) {
            grams[i] = BebopGrammar.getBeatandPassageNotes(chds[i].getChord());
        }
        return grams;

    }

    public static ChordSequence createDefaultChordSequence(){
        int nChords = 3;


        Chord[] chds = new Chord[nChords]; // schema of Jazz Pattern 1
        int q = (int) G.quarter; //
        int[] chT = {0,4*q,8*q};
        int[] chD = {4*q,4*q,8*q}; //480*4 = 8 notes

        chds[0] = new Chord(2,JazzChord.MINOR); // Dm7
        chds[1] = new Chord(7,JazzChord.DOM); // G7
        chds[2] = new Chord(0,JazzChord.MAJOR); //C Major 7

        Integer[][] grams = new Integer[nChords][];
        for (int i = 0; i < nChords ; i++) {
            grams[i] = BebopGrammar.getBeatandPassageNotes(chds[i].getChord());
        }
        ChordSequence cS = new ChordSequence(chds,chT,chD);
        cS.setBeatAndPassageBytes(grams);
        return cS;
    }
    public void setTitle(String title){
        this.title = title;
        //lastUpdate = System.currentTimeMillis();
    }

    public StringBuilder exportLikeTXT(String title){
        StringBuilder sb = new StringBuilder();
        //HEADER with TITLE,TOTALDURATION
        if (title.isEmpty()) {
            if (this.title.isEmpty()){
                sb.append("Untitled");
            } else {
                sb.append(this.title);
            }
        } else {
            sb.append(title);
        }
        sb.append(sep2level);
        sb.append(getTotalDuration());
        //CHORD LIST for each: ROOT, ENHARM, CHORDDEF(BYTE!!!), DURATION
        sb.append(sep1level); // HEADER|CHORDLIST
        for (int i = 0; i < chords.length ; i++) {
            sb.append(chords[i].getRoot()); sb.append(sep3level);
            sb.append(chords[i].getEnharmonic()); sb.append(sep3level);
            sb.append(chords[i].getChord().getDbyte()); sb.append(sep3level);
            sb.append(chordDurations[i]); //sb.append(sep3level);
            if(i!=chords.length-1) sb.append(sep2level);
        }
        System.out.println(sb.toString());
        return sb;
    }
   /* public static void main(String[] args){
        String file = "neworder,7680§2_0_1677_1920,7_0_1685_1920,9_0_2701_3840";
        ChordSequence.createChordSequenceFromTXT(file);
    }*/
    public static ChordSequence createChordSequenceFromTXT(String text){
        String[] level1 = text.split(sep1level);

        String[] header = level1[0].split(sep2level);

        String title = header[0];
        int totalDur = Integer.parseInt(header[1]);
        String[] chordList = level1[1].split(sep2level);
        int numChords = chordList.length;
        Chord[] chords = new Chord[numChords];
        int[] ticks = new int[numChords];
        int[] durs = new int[numChords];
        int tick = 0;
        for (int i = 0; i < numChords; i++) {
            String[] data = chordList[i].split(sep3level);
            Chord ch = new Chord(Integer.parseInt(data[0]),//ROOT
                    JazzChord.getJazzChordByDbyte(Integer.parseInt(data[2])),//JAZZCHORD
                    Integer.parseInt(data[1]) ); //ENHARM
            chords[i] = ch;
            ticks[i] = tick;
            int dur = Integer.parseInt(data[3]);
            durs[i] = dur;
            tick = tick + dur;
        }

        ChordSequence chs = new ChordSequence(chords,ticks,durs);
        chs.setBeatAndPassageBytes(findBeatAndPassageBytes(chs));
        chs.setTitle(title);
        return chs;
    }

//    public void addDurationToChord(int addDur, int chordSelected) {
//        Chord ch = chords[chordSelected];
//        int tick = chordTicks[chordSelected];
//        int dur = chordDurations[chordSelected];
//        int beatByte = beatBytes[chordSelected];
//        int passByte = passageBytes[chordSelected];
//        deleteChord(chordSelected);
//        insertChord(chordSelected,ch,tick,dur+addDur,beatByte,passByte);
//        int nNotesAdded = addDur/(G.quarter/2);
//        BebopMelody bm = G.getCurrentBebopMelody();
//        bm.addNotes(1,tick+dur);
//        assignNotesInChords(G.getCurrentBebopMelody());
//
//        G.getCurrentBebopMelody().assignChordsToNotes(this.notesInChord);
//        G.totalPieceTickDuration = G.currentChordSequence.getTotalDuration();
//        //lastUpdate = System.currentTimeMillis();
//        refreshIntervalsData();
//    }
    static public void changeDurationToChord(List<ActualChord> sequence, int difference,int chordSelected){
        ActualChord ach = sequence.get(chordSelected);
        int chordDuration = ach.duration;

        ach.duration +=difference;
        if (chordSelected==sequence.size()-1) return;
        for (int i = chordSelected+1; i <sequence.size() ; i++) {
            sequence.get(i).tick += difference;
        }
    }
    static public List<ActualChord> createVirtualSequence(ChordSequence chordSequence){
        List<ActualChord> seq = new LinkedList<>();
        for (int i = 0; i <chordSequence.chords.length ; i++) {
            ActualChord ach = new ActualChord(chordSequence.chords[i],chordSequence.chordTicks[i],
                    chordSequence.chordDurations[i],chordSequence.beatBytes[i],chordSequence.passageBytes[i]);
            seq.add(ach);
        }
        return seq;
    }

    public void transpose(int interval) {
        for (Chord ch: chords
             ) {
            ch.transpose(interval);
        }
        //lastUpdate = System.currentTimeMillis();
        refreshIntervalsData();
    }

    public void switchEnharmInAllChords() {
        for (int i = 0; i < chords.length; i++) {
            switchEnharmInChord(i);
        }
        //lastUpdate = System.currentTimeMillis();
        refreshIntervalsData();
    }

    public void switchEnharmInChord(int chordSelected) {
        Chord ch = chords[chordSelected];
        ch.setEnharmonic(ch.getEnharmonic()+1);
        //lastUpdate = System.currentTimeMillis();
        refreshIntervalsData();
    }



    public static class ActualChord{
        public Chord chord;
        public int tick, duration, beatByte, passageByte;

        public ActualChord(Chord chord, int tick, int duration, int beatByte,int passageByte) {
            this.chord = chord; this.tick = tick; this.duration = duration;
            this.beatByte =beatByte; this.passageByte = passageByte;
        }

        public ActualChord clone(){
            return new ActualChord(chord.clone(),tick,duration,beatByte,passageByte);
        }

        public void transpose(int interval){
            chord.transpose(interval);
        }
    }

    public boolean isInBeatChord(int indexChord, int notePitch ){
        int root = this.chords[indexChord].getRoot();
        int beatByte = this.beatBytes[indexChord];
        int actualByte= Insieme.rotate(root, beatByte);
        int pitchByte = 1 << (notePitch%12);
        return ((pitchByte & actualByte) !=0);
    }

    public Interval[][] findVoicing(int nParts){
        voicing = new Interval[chords.length][];
        int start = -1;
        for (int i = 0; i < chords.length; i++) {
            if(chords[i].getChord()!=JazzChord.EMPTY){
                start = i; break;
            }
        }
        if (start==-1) return voicing; // all the chords are empty;
        Chord startChord = chords[start];
        Interval[][] voicingType = JazzInterpreter.chooseVoicingType4Parts(
                startChord.getRoot(), startChord.getChord(), 12);
        int lastRoot= startChord.getRoot();
        for (int i = start; i < this.chords.length; i++) {

            Chord chord = this.chords[i];
            if (chord.getChord() == JazzChord.EMPTY) continue;
            int root = chord.getRoot();
            if(Math.abs(root-lastRoot)>2){
                voicingType = JazzInterpreter.switchVoicingType(voicingType);
            }
            lastRoot = root;

            //System.out.println("Chord at "+i+" = "+ chord.getDef());

            //if(nParts==4){};

            Interval[] intervals = JazzInterpreter.getVoicingIntervals(chord.getChord(), voicingType);
            voicing[i] = intervals;
        }
        return voicing;
    }
    public Interval[][] findVoicing_13(int nParts){
        voicing = new Interval[chords.length][];
        int start = -1;
        for (int i = 0; i < chords.length; i++) {
            if(chords[i].getChord()!=JazzChord.EMPTY){
                start = i; break;
            }
        }
        if (start==-1) return voicing; // all the chords are empty;
        Chord startChord = chords[start];
        Interval[][] voicingType = JazzInterpreter.chooseVoicingType4Parts_13(
                startChord.getRoot(), startChord.getChord(), 12);
        int lastRoot= startChord.getRoot();
        for (int i = start; i < this.chords.length; i++) {

            Chord chord = this.chords[i];
            if (chord.getChord() == JazzChord.EMPTY) continue;
            int root = chord.getRoot();
            if(Math.abs(root-lastRoot)>2){
                voicingType = JazzInterpreter.switchVoicingType_13(voicingType);
            }
            lastRoot = root;

            //System.out.println("Chord at "+i+" = "+ chord.getDef());

            //if(nParts==4){};

            Interval[] intervals = JazzInterpreter.getVoicingIntervals(chord.getChord(), voicingType);
            voicing[i] = intervals;
        }
        return voicing;
    }

    public static List<ActualChord> createProgression(ChordSequence chordSequence,int step, int nTimes){
        if(nTimes<1)return null;
        List<ActualChord> chordList = ChordSequence.createVirtualSequence(chordSequence);
        int totalDur = chordSequence.getTotalDuration();
        //int index = this.chords.length;
        int originalSize = chordList.size();
        int startTick = totalDur;
        int transposition =step;
        for (int i = 0; i <nTimes ; i++) {
            for (int j = 0; j <originalSize; j++) {
                ActualChord aChord = chordList.get(j);
                ActualChord newChord = aChord.clone();
                newChord.transpose(transposition);
                newChord.tick += startTick;
                chordList.add(newChord);
            }
            transposition += step;
            startTick += totalDur;
        }
        return chordList;
    }

    public Interval[][] get4PartsVoicing(){
        return fourPartsVoicing;
    }

    public int[][] getNotesInChord() {
        return notesInChord;
    }

    public int[] getPassageBytes() {
        return passageBytes;
    }

    public int[] getBeatBytes() {
        return beatBytes;
    }

    public int[] getChordDurations() {
        return chordDurations;
    }

    public int[] getChordTicks() {
        return chordTicks;
    }

    public Chord[] getChords() {
        return chords;
    }

    public String getTitle() {
        return title;
    }
    public void setRootAndDef(int index, int root, String def){
        chords[index].setRoot(root);
        chords[index].setDef(def);
        //lastUpdate = System.currentTimeMillis();
        refreshIntervalsData();
    }

    // public void setNotesInChord(int[][] notesInChord) {
    //    this.notesInChord = notesInChord;
   // }
}

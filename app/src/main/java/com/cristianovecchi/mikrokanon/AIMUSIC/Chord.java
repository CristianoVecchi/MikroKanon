package com.cristianovecchi.mikrokanon.AIMUSIC;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Cristiano on 12/02/2017.
 */
public class Chord {
    private int root;
    private JazzChord chord;
    private String[] chromaticScale;
    private int enharm;

    public static String[] findChromaticScale(int root, int enharm,
                                                    JazzChord jazzChord, String[] noteNames){
        return JazzInterpreter.getChromaticScaleNames(root,enharm,jazzChord,noteNames);
    }

    public String[]getChromaticScaleNoteNames(String[] noteNames){

            return findChromaticScale(this.root, this.enharm, this.getChord(), noteNames);

    }
    public Interval[] getChromaticScaleIntervals(){
        return JazzInterpreter.findIntervalsInChromaticScale(chord);
    }
    public Chord(int root, JazzChord chord) {
        this.root = root;
        this.chord= chord;
        enharm = 0;
    }
    public Chord(int root, JazzChord chord, int enharm) {
        this.root = root;
        this.chord= chord;
        this.enharm = enharm;
    }
    public Chord(int root, int dByte, int enharm) {
        this.root = root;
        this.chord = JazzChord.getJazzChordByDbyte(dByte);
        this.enharm = enharm;
    }

    public Chord(int root, String chordDefinition, int enharm) {
        this.root = root;
        this.chord = JazzChord.getChordByDef(chordDefinition);
        this.enharm = enharm;
    }

    public int notesCount(){
        return Insieme.contaNote(chord.getDbyte());
    }

    public int maxNotesCount(List<Chord> chordList){
        int max = 0;
        for (Chord ch:
                chordList ) {
            if (ch.notesCount()>max) max = ch.notesCount();
        }
        return max;
    }


    public int[] getAbsoluteNotes(){
        int db = chord.getDbyte();
        int[] abs = new int[notesCount()];
        int count = 0; int flag = 1;
        for (int i = 0; i < 12 ; i++) {
            if((flag & db) != 0) abs[count++] = (i+ root) %12;
            flag <<= 1;
        }
        return abs;
    }


    public String getName(){
        if(root ==-1){ return "No Chord.";}
        //return DEF.ITALNOTENAMES[root]+chord.getDef();
        return DEF.getEnharmonicName(root, enharm)+chord.getDef();
    }

    public String getName(String[] currentNoteNames){
        if(root ==-1){ return "No Chord.";}
        //return DEF.ITALNOTENAMES[root]+chord.getDef();
        return DEF.getEnharmonicName(root, enharm,currentNoteNames)+chord.getDef();
    }

    public String getDef(){

        return chord.getDef();
    }


    public JazzChord getChord() {
        return chord;
    }

    public int getRoot() {
        return root;
    }
    public int getEnharmonic() {
        return enharm;
    }

    public void setEnharmonic(int enharm){
        enharm = enharm % DEF.ENHARMONICS[this.getRoot()].length;
        this.enharm = enharm;
    }

    public static Chord createEmptyChord() {
        return new Chord(-1, JazzChord.EMPTY);
    }

    public void transpose(int transposition) {
        root = (root + transposition)%12;
       // if (root > 11) root -= 12;
        if (root < 0)  root = Math.abs(12+root);
    }

    public Chord clone(){
        return new Chord(root,chord,enharm);
    }
    public static void main (String[] args){
        Chord ch = new Chord(7,"",0); // SOL Maj
        System.out.println(Arrays.toString(ch.getAbsoluteNotes()));
    }

    public void setRoot(int root) {
        this.root = root;
    }

    public void setDef(String def) {

        this.chord = JazzChord.getChordByDef(def);
    }
}

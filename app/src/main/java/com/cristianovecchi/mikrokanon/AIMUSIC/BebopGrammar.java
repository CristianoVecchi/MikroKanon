package com.cristianovecchi.mikrokanon.AIMUSIC;

import java.util.HashMap;

public class BebopGrammar {
    private static HashMap<JazzChord,Integer[]> grammar;
    public static void initGrammar(){
        grammar = new HashMap<>();
        // SIMPLE TRIADS
        Integer[] valuesMajorTriad= {0b000010010001, 0b000100100100}; // beat notes and passage notes for Major Triad
        grammar.put(JazzChord.MAJOR_TRIAD, valuesMajorTriad);

        Integer[] valuesMinorTriad = {0b000010010001, 0b010000100100}; // beat notes and passage notes for Minor Triad
        grammar.put(JazzChord.MINOR_TRIAD, valuesMinorTriad);

        Integer[] valuesDimTriad = {0b000001001001, 0b010000100010}; // beat notes and passage notes for Diminished Triad
        grammar.put(JazzChord.DIM_TRIAD, valuesDimTriad);

        // JUST 7 or 6 CHORDS
        Integer[] valuesMajorJust7M = {0b001010010001,0b100100100100}; // beat notes and passage notes for Major7 just 7
        grammar.put(JazzChord.MAJOR_JUST7MAJ,valuesMajorJust7M);

        Integer[] valuesMajorJust7M_9 = {0b001010010101,0b100100100100}; // beat notes and passage notes for Major7 just 7
        grammar.put(JazzChord.MAJOR_JUST7MAJ_9,valuesMajorJust7M_9);

        Integer[] valuesMajorAdd6 = {0b001010010001,0b100100100100}; // beat notes and passage notes for Major add6
        grammar.put(JazzChord.MAJOR_ADD6,valuesMajorAdd6);

        Integer[] valuesMajorAdd6_9 = {0b001010010101,0b100100100100}; // beat notes and passage notes for Major add6 & 9M
        grammar.put(JazzChord.MAJOR_ADD6_9, valuesMajorAdd6_9);

        Integer[] valuesMinorJust7 = {0b010010001001,0b101000100100}; // beat notes and passage notes for minor just 7
        grammar.put(JazzChord.MINOR_JUST7,valuesMinorJust7);

        Integer[] valuesMinorJust7_9 = {0b010010001101,0b101000100100}; // beat notes and passage notes for minor just 7
        grammar.put(JazzChord.MINOR_JUST7_9,valuesMinorJust7_9);

        Integer[] valuesMinorAdd6 = {0b001010001001,0b100100100100}; // beat notes and passage notes for minor add 6M
        grammar.put(JazzChord.MINOR_ADD6,valuesMinorAdd6);

        Integer[] valuesMinorAdd6_9 = {0b001010001101,0b100100100100}; // beat notes and passage notes for minor add 6M & 9M
        grammar.put(JazzChord.MINOR_ADD6,valuesMinorAdd6_9);

        Integer[] valuesDomJust7 = {0b010010010001,0b101000100100}; // beat notes and passage notes for dominant 7 (just 7)
        grammar.put(JazzChord.DOM_JUST7,valuesDomJust7);

        Integer[] valuesDomJust7_9 = {0b010010010101,0b101000100100}; // beat notes and passage notes for dominant 7 (just 7) + 9M
        grammar.put(JazzChord.DOM_JUST7_9,valuesDomJust7_9);

        Integer[] valuesHalfDimJust7 = {0b010001001001,0b100100100010}; // beat notes and passage notes for an Empty Chord: Solo style
        grammar.put(JazzChord.HALFDIM_JUST7,valuesHalfDimJust7);

        Integer[] valuesHalfDimJust7_9m = {0b010001001011,0b100100100010}; // beat notes and passage notes for an Empty Chord: Solo style
        grammar.put(JazzChord.HALFDIM_JUST7_9MIN,valuesHalfDimJust7_9m);

        Integer[] valuesSus4just7 = {0b010010100001,0b101001000100}; // beat notes and passage notes for Sus4 (C D F F# G A Bb B)
        grammar.put(JazzChord.SUS4_JUST7,valuesSus4just7);

        Integer[] valuesSus4just7_9 = {0b010010100101,0b101001000100}; // beat notes and passage notes for Sus4 (C D F F# G A Bb B)
        grammar.put(JazzChord.SUS4_JUST7,valuesSus4just7_9);

        Integer[] valuesDim = {0b001001001001,0b100100100100}; // beat notes and passage notes for dim (CD EbF F#G# AB) octatonica
        grammar.put(JazzChord.DIM,valuesDim);
        // FULL JAZZ CHORDS
        Integer[] valuesMajor7add11aum = {0b001010010001,0b100101000100}; // beat notes and passage notes for Major7#11
        grammar.put(JazzChord.MAJOR11AUM,valuesMajor7add11aum);

//        Integer[] valuesMajorDoubleHarm = {0b100010010001,0b000100100010}; // beat notes and passage notes for Major7 DoubleHarmonic Scale
//        grammar.put(JazzChord.DOUBLEHARM,valuesMajorDoubleHarm);

        Integer[] valuesMajor7 = {0b001010010001,0b100100100100}; // beat notes and passage notes for Major7
        grammar.put(JazzChord.MAJOR,valuesMajor7);

        Integer[] valuesMinor7 = {0b010010001001,0b101000100100}; // beat notes and passage notes for minor7
        grammar.put(JazzChord.MINOR,valuesMinor7);

        Integer[] valuesMinor7add11 = {0b010010101001,0b101001010100}; // beat notes and passage notes for minor7add11
        grammar.put(JazzChord.MINOR11,valuesMinor7add11);

        Integer[] valuesMinor7b9add11b13= {0b010010001001,0b100100100010}; // beat notes and passage notes for minor7add11
        grammar.put(JazzChord.MINOR9MIN11MIN13,valuesMinor7b9add11b13);


        Integer[] valuesDom7 = {0b010010010001,0b101000100100}; // beat notes and passage notes for dominant 7
        grammar.put(JazzChord.DOM,valuesDom7);

        Integer[] valuesDom7b9 = {0b010010010001,0b101000100010}; // beat notes and passage notes for dominant 7 b9
        grammar.put(JazzChord.DOM9MIN,valuesDom7b9);



        Integer[] valuesEmpty = {0b111111111111,0b111111111111}; // beat notes and passage notes for an Empty Chord: Solo style
        grammar.put(JazzChord.EMPTY,valuesEmpty);

        Integer[] valuesHalfDim = {0b010001001001,0b100100100010}; // beat notes and passage notes for an Empty Chord: Solo style
        grammar.put(JazzChord.HALFDIM,valuesHalfDim);

        Integer[] valuesDom7add11 = {0b010010010001,0b101001000100}; // beat notes and passage notes for Dom7add11
        grammar.put(JazzChord.DOM11AUM,valuesDom7add11);


        Integer[] valuesDom7exatonal = {0b010100010001,0b101001000100}; // beat notes and passage notes for Dom7exatonal
        grammar.put(JazzChord.DOMEXATONAL,valuesDom7exatonal);
        Integer[] valuesDom7add5aum11aum = {0b010100010001,0b101001000100}; // beat notes and passage notes for Dom7add5aum11aum
        grammar.put(JazzChord.DOM5AUM11AUM,valuesDom7add5aum11aum);

        Integer[] valuesDom7alt = {0b010001001001,0b100100010010}; // beat notes and passage notes for Dom7alt,
        grammar.put(JazzChord.DOMALT,valuesDom7alt);

        Integer[] valuesMinor7Maj = {0b001010001001,0b100100100100}; // beat notes and passage notes for Minor7Maj, passage note from B to G#
        grammar.put(JazzChord.MINOR7MAJ,valuesMinor7Maj);

        Integer[] valuesSus4 = {0b010010100001,0b101001000100}; // beat notes and passage notes for Sus4 (C D F F# G A Bb B)
        grammar.put(JazzChord.SUS4,valuesSus4);

    }
    public static Integer[] getBeatandPassageNotes(JazzChord jazzChord){
        Integer[] bytes = new Integer[2];
        if(grammar.containsKey(jazzChord)) {
            bytes = grammar.get(jazzChord);
        } else {
            bytes[0] = jazzChord.getDbyte();
            bytes[1] = jazzChord.getDbyte();
        }

        return bytes;
    }

    public static String[] getChordDef() {

        String[] defs = new String[JazzChord.values().length];
        int count = 0;
        for (JazzChord ch: JazzChord.values()
             ) {
            defs[count++] =  ch.getDef();
        }
        return  defs;
    }
}

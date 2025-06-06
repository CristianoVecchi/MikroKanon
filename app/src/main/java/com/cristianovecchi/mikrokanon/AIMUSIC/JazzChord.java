package com.cristianovecchi.mikrokanon.AIMUSIC;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Cristiano on 02/11/2016.
 */

public enum JazzChord {
    ROOT("root", 0B000000000001), // solo fondamentale
    FIFTH("just5", 0B000010000001), // solo quinta
    DIMINISHED_FIFTH("just_d5", 0B000001000001), // solo quinta diminuita
    MAJOR_TRIAD("M", 0B000010010001), // triade maggiore
    MINOR_TRIAD("m", 0B000010001001), // triade minore
    DIM_TRIAD("dim", 0B000001001001), // triade diminuita

    MAJOR_JUST7MAJ("M7", 0B100010010001), // accordo maggiore solo 7M
    MAJOR_JUST7MAJ_9("M7add9", 0B100010010101), // accordo maggiore solo 7M + 9M
    MAJOR_ADD6("add6",0B001010010001 ), // accordo maggiore + 6M senza 7!!!
    MAJOR_ADD6_9("add6add9",0B001010010101 ), // accordo maggiore + 6M senza 7 + 9M!!!
    MINOR_JUST7("m7", 0B010010001001), // accordo minore solo 7m
    MINOR_JUST7_9("m7add9", 0B010010001101), // accordo minore solo 7m + 9M
    MINOR_ADD6("madd6",0B001010001001 ), // accordo minore + 6M senza 7!!!
    MINOR_ADD6_9("madd6add9",0B001010001101 ), // accordo minore + 6M senza 7 + 9M!!!
    DOM_JUST7("7", 0B010010010001), // accordo di dominante solo 7m
    DOM_JUST7_9("7add9", 0B010010010101), // accordo di dominante solo 7m + 9M
    HALFDIM_JUST7("m7b5",  0B010001001001), // accordo semidiminuito 5dim 7m
    HALFDIM_JUST7_9MIN("m7b5b9",  0B010001001011), // accordo semidiminuito 5dim 7m + 9m
    SUS4_JUST7("sus4",      0B010010100001), // accord0 sus4 (senza terze, solo 7m!!!)
    SUS4_JUST7_9("sus4add9",      0B010010100101), // accord0 sus4 (senza terze, solo 7m +9M!!!)

    MAJOR("M7913",         0B101010010101), // accordo maggiore 7M 9M 13M senza 11!!!
    MINOR("m7913",       0B011010001101), // accordo minore 7m 9M 13M senza 11!!!
    MAJOR11AUM("M7#11", 0B101011010101), // acc magg 7M 9M 11diesis 13M
    MINOR11("m711",  0B011010101101), // accordo minore 7m 9M 11giusta 13M
    MINOR7MAJ("mM7",  0B101010001101), // acc min con 7M 9M 13M senza 11!!!
    //MAJOR11("M11",     0B101010110101), // acc magg 7M 9M 11giusta 13M
    DOM("7913",          0B011010010101), // settima di dominante 7m 9M 13M
    DOMALT ("7alt",   0B010101011011), // sett dom. 3M 5# 7m 11# 9m-9# (NO 13)
    DOM9MIN("7b9",    0B011010010011), // settima dom. 7m 9m 13M
    SUS4("sus4913",      0B011010100101), // accord0 sus4 (senza terze, con 9 4 13M)
    DOM11AUM("7#11", 0B011011010101), // sett. dom. 7m 9M 11diesis 13M
    MINOR9MIN11MIN13("m7b911b13",  0B010110101011), // accordo minore 7m 9m 11giusta 13m
    DOM5AUM11AUM("7#5#11", 0B011101010101),// sett.dom. 5# 7m 9M 11# 13M
    DOMEXATONAL("7ex", 0B010101010101),// esatonale 5b 7m 9M 13b

    HALFDIM("m7b59b13",  0B010101001101), // accordo semidiminuito 5dim 7m 9M 13m
    DIM("o",          0B001001001001), //accordo diminuito 5dim 7dim [Passage notes: (13M) 9M]

    //DOUBLEHARM("Mb9b13", 0B100110010011), // scala double harmonic: 3M 5 7M 9m (11) 13b
    //MAJOR5AUM("5#",   0B101100010101), // acc magg 5# 9M 13M
    EMPTY("[ ]",         0B000000000000); // accordo vuoto

    // JAZZ & JAZZ11
    public static int[] priorityFromTonic = {2,8, 9,3, 4,10, 5,11, 6,0, 1,7};
    public static int[] priorityFrom2and5 = {5,11, 10,4, 3,9, 8,2, 1,7, 6,0};

    // POP & POP7
    public static int[] priorityFromTonicJust7 = {9, 5, 2, 7, 4, 10, 3, 8, 11, 1, 6, 0}; // from Do -> La, Fa, Re, Sol, Mi, Si, Sib, Mib, Lab, Si, Reb, Solb, Do
    public static int[] priorityFrom2and5Just7= {5, 10, 2, 9, 7, 3, 1, 8, 11, 4, 6, 0};//from Sol -> Do, Fa, La, Mi, Re, Sib, Lab, Mib, Solb, Si, Reb, Sol

    public static Chord extractRootChord(Chord chord){
        return new Chord(chord.getRoot(), JazzChord.ROOT);
    }
    public static Chord extractJustFifthChord(Chord chord){
        JazzChord oldJazzChord = chord.getChord();
        JazzChord newJazzChord;
        if((oldJazzChord.dbyte & 0B10000000) != 0) {
            newJazzChord =JazzChord.FIFTH;
        }
        else if ((oldJazzChord.dbyte & 0B1000000) != 0) {
            newJazzChord = JazzChord.DIMINISHED_FIFTH;
        } else {
            newJazzChord =JazzChord.FIFTH;
        }
        return new Chord(chord.getRoot(), newJazzChord);
    }
    public static int[] choosePriority(HarmonizationType harmType){
        switch (harmType) {
            case ROOTS: case ORGANUM: case POP: case POP7: case LIBERTY: return priorityFrom2and5Just7;
            case JAZZ: case JAZZ11: return priorityFrom2and5;
            case XWH: return  new int[] {5, 8, 2, 10, 6, 4, 1, 11, 9, 7, 3, 0};
        }
        return new int[] {5, 8, 2, 10, 6, 4, 1, 11, 9, 7, 3, 0};
    }
    // JAZZ
    public static int[] findRootMovementPriority(@NotNull JazzChord previousChord) {
        switch (previousChord){
            case MAJOR: case MAJOR11AUM: case MINOR7MAJ:
                return priorityFromTonic;
        }
        return priorityFrom2and5;
    }

    // POP
    public static int[] findRootMovementPriorityJust7(@NotNull JazzChord previousChord) {
        switch (previousChord){
            case MAJOR: case MAJOR11AUM: case MINOR7MAJ:
                return priorityFromTonicJust7;
        }
        return priorityFrom2and5Just7;
    }
//    public static JazzChord[] selectChordArea_no_5(JazzChord previousChord){
//        switch (previousChord){
//            case MAJOR_TRIAD:
//                return SECOND_GRADE_AREA_NO_5;
//            case MINOR_TRIAD: case DIM_TRIAD:
//                return DOMINANT_AREA_NO_5;
//            case DOM_JUST7:
//                return TONIC_AREA_NO_5;
//        }
//        return TONIC_AREA_NO_5;
//    }
    public static JazzChord[] selectChordArea_no_7(JazzChord previousChord){
        switch (previousChord){
            case MAJOR_TRIAD:
                return SECOND_GRADE_AREA_NO_7;
            case MINOR_TRIAD: case DIM_TRIAD:
                return DOMINANT_AREA_NO_7;
            case DOM_JUST7:
                return TONIC_AREA_NO_7;
        }
        return TONIC_AREA_NO_7;
    }

    //LIBERTY 7 9
    public static int[] findRootMovementPriorityJust9(@NotNull JazzChord previousChord) {
        switch (previousChord){
            case MAJOR_ADD6_9: case MAJOR_JUST7MAJ_9: case MINOR_ADD6_9:
                return priorityFromTonicJust7;
        }
        return priorityFrom2and5Just7;
    }
    public static JazzChord[] TONIC_AREA_JUST_9= {MAJOR_ADD6_9, MINOR_ADD6_9, MAJOR_JUST7MAJ_9, MINOR_JUST7_9,
            DOM_JUST7_9, SUS4_JUST7_9, HALFDIM_JUST7_9MIN, EMPTY};
    public static JazzChord[] SECOND_GRADE_AREA_JUST_9= {MINOR_JUST7_9, MINOR_ADD6_9, HALFDIM_JUST7_9MIN,
            DOM_JUST7_9, SUS4_JUST7_9, MAJOR_ADD6_9, MAJOR_JUST7MAJ_9, EMPTY};
    public static JazzChord[] DOMINANT_AREA_JUST_9 = {DOM_JUST7_9, SUS4_JUST7_9,
            HALFDIM_JUST7_9MIN, MAJOR_ADD6_9, MINOR_JUST7_9, MINOR_ADD6_9, MAJOR_JUST7MAJ_9, EMPTY};
    public static JazzChord[] selectChordAreaJust9(JazzChord previousChord){
        switch (previousChord){
            case MAJOR_ADD6_9: case MAJOR_JUST7MAJ_9:
                return SECOND_GRADE_AREA_JUST_9;
            case MINOR_ADD6_9: case MINOR_JUST7_9: case HALFDIM_JUST7_9MIN:
                return DOMINANT_AREA_JUST_9;
            case DOM_JUST7_9: case SUS4_JUST7_9: case EMPTY:
                return TONIC_AREA_JUST_9;
        }
        return TONIC_AREA_JUST_9;
    }

    public static JazzChord[] selectChordArea_just_7(JazzChord previousChord){
        switch (previousChord){
            case MAJOR_ADD6: case MAJOR_JUST7MAJ: case MINOR_ADD6:
                return SECOND_GRADE_AREA_JUST_7;
            case HALFDIM_JUST7: case DIM: case MINOR_JUST7:
                return DOMINANT_AREA_JUST_7;
            case DOM_JUST7: case SUS4_JUST7: case EMPTY:
                return TONIC_AREA_JUST_7;
        }
        return TONIC_AREA_JUST_7;
    }
    public static JazzChord[] selectChordArea_11(JazzChord previousChord){
        switch (previousChord){
            case MAJOR11AUM: case MINOR7MAJ:
                return SECOND_GRADE_AREA_11;
            case HALFDIM: case DIM: case MINOR11: case MINOR9MIN11MIN13:
                return DOMINANT_AREA_11;
            case DOM: case DOMALT: case DOM9MIN: case SUS4: case DOM11AUM: case DOM5AUM11AUM: case DOMEXATONAL: case EMPTY:
                return TONIC_AREA_11;
        }
        return TONIC_AREA_11;
    }
    public static JazzChord[] selectChordArea_no_11(JazzChord previousChord){
        switch (previousChord){
            case MAJOR: case MINOR7MAJ:
                return SECOND_GRADE_AREA_NO_11;
            case HALFDIM: case DIM: case MINOR:
                return DOMINANT_AREA_NO_11;
            case DOM: case DOMALT: case DOM9MIN: case SUS4: case DOMEXATONAL: case EMPTY:
                return TONIC_AREA_NO_11;
        }
        return TONIC_AREA_NO_11;
    }

//    //ROOTS
//    public static JazzChord[] TONIC_AREA_NO_5= {ROOT, EMPTY};
//    public static JazzChord[] SECOND_GRADE_AREA_NO_5= {ROOT, EMPTY};
//    public static JazzChord[] DOMINANT_AREA_NO_5 = {ROOT, EMPTY};
//    //ORGANUM (root + fifth or diminished fifth)
//    public static JazzChord[] TONIC_AREA_JUST_5= {FIFTH, DIMINISHED_FIFTH, EMPTY};
//    public static JazzChord[] SECOND_GRADE_AREA_JUST_5= {FIFTH, DIMINISHED_FIFTH, EMPTY};
//    public static JazzChord[] DOMINANT_AREA_JUST_5 = {FIFTH, DIMINISHED_FIFTH, EMPTY};
    //POP
    public static JazzChord[] TONIC_AREA_NO_7= {MAJOR_TRIAD, MINOR_TRIAD, DIM_TRIAD, DOM_JUST7, EMPTY};
    public static JazzChord[] SECOND_GRADE_AREA_NO_7= {MINOR_TRIAD, DIM_TRIAD, MAJOR_TRIAD, DOM_JUST7, EMPTY};
    public static JazzChord[] DOMINANT_AREA_NO_7 = {DOM_JUST7, DIM_TRIAD, MAJOR_TRIAD, MINOR_TRIAD, EMPTY};

    //POP 7
    public static JazzChord[] TONIC_AREA_JUST_7= {MAJOR_ADD6, MINOR_ADD6, MAJOR_JUST7MAJ, MINOR_JUST7,
            DOM_JUST7, SUS4_JUST7, HALFDIM_JUST7, DIM, EMPTY};
    public static JazzChord[] SECOND_GRADE_AREA_JUST_7= {MINOR_JUST7, MINOR_ADD6, HALFDIM_JUST7, DIM,
            DOM_JUST7, SUS4_JUST7, MAJOR_ADD6, MAJOR_JUST7MAJ, EMPTY};
    public static JazzChord[] DOMINANT_AREA_JUST_7 = {DOM_JUST7, SUS4_JUST7,
            HALFDIM_JUST7, DIM, MAJOR_ADD6, MINOR_JUST7, MINOR_ADD6, MAJOR_JUST7MAJ, EMPTY};


    // JAZZ 7 9 13
    public static JazzChord[] TONIC_AREA_NO_11= {MAJOR, MINOR, MINOR7MAJ,
            DOM, DOMALT, DOM9MIN, SUS4, DOMEXATONAL, HALFDIM, DIM, EMPTY};
    public static JazzChord[] SECOND_GRADE_AREA_NO_11 = {HALFDIM, DIM, MINOR,
            DOM, DOMALT, DOM9MIN, MAJOR, SUS4, DOMEXATONAL, MAJOR, MINOR7MAJ, EMPTY};
    public static JazzChord[] DOMINANT_AREA_NO_11 = {DOM, DOMALT, DOM9MIN, SUS4, DOMEXATONAL,
            HALFDIM, DIM, MAJOR, MINOR, MINOR7MAJ, EMPTY};

    // JAZZ 7 9 11 13
    public static JazzChord[] TONIC_AREA_11 = {MAJOR11AUM, MINOR11, MINOR7MAJ,
    DOM, DOMALT, DOM9MIN, SUS4, DOM11AUM, DOM5AUM11AUM, DOMEXATONAL, MINOR9MIN11MIN13, HALFDIM, DIM, EMPTY};
    public static JazzChord[] SECOND_GRADE_AREA_11 = {HALFDIM, DIM, MINOR11, MINOR9MIN11MIN13,
            DOM, DOMALT, DOM9MIN, SUS4, DOM11AUM,DOM5AUM11AUM, DOMEXATONAL, MAJOR11AUM, MINOR7MAJ, EMPTY};
    public static JazzChord[] DOMINANT_AREA_11 = {DOMALT, DOM9MIN, SUS4, DOM11AUM,DOM5AUM11AUM, DOMEXATONAL, DOM,
    HALFDIM, DIM, MAJOR11AUM, MINOR11, MINOR7MAJ, MINOR9MIN11MIN13, EMPTY};

    public int getDbyte() {
        return dbyte;
    }

    public String getDef() {
        return def;
    }

    private String def;
    private int dbyte;



    private JazzChord(String def, int dbyte){
        this.def = def;
        this.dbyte = dbyte;

    }

    public static JazzChord getChordByDef(String chordDefinition) {
        for (JazzChord chord : JazzChord.values() ) {
            if(chordDefinition.equals(chord.getDef()))
                return chord;
        }
        return EMPTY;
    }
    public static JazzChord getJazzChordByDbyte(int dByte){
        for (JazzChord ch :JazzChord.values()
             ) {
           // System.out.println(ch.getDef()+" "+ch.getDbyte()+" "+dByte);
            if(ch.getDbyte()==dByte) return ch;
        }
        return null;
    }

    public static int getJazzChordIndex(JazzChord jazzChord){
        JazzChord[] jzchs = JazzChord.values();
        for (int i = 0; i <jzchs.length ; i++) {
            if(jazzChord == jzchs[i]) return i;
        }
        return -1;
    }

}

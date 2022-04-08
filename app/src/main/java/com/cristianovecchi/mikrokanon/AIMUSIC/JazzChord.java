package com.cristianovecchi.mikrokanon.AIMUSIC;

/**
 * Created by Cristiano on 02/11/2016.
 */

public enum JazzChord {

    MAJOR("M7",         0B101010010101), // accordo maggiore 7M 9M 13M senza 11!!!
    MINOR("m7",       0B011010001101), // accordo minore 7m 9M 13M senza 11!!!

    MAJOR11AUM("M7#11", 0B101011010101), // acc magg 7M 9M 11diesis 13M
    MINOR11("m711",  0B011010101101), // accordo minore 7m 9M 11giusta 13M
    MINOR7MAJ("mM7",  0B101010001101), // acc min con 7M 9M 13M senza 11!!!
    //MAJOR11("M11",     0B101010110101), // acc magg 7M 9M 11giusta 13M
    DOM("7",          0B011010010101), // settima di dominante 7m 9M 13M
    DOMALT ("7alt",   0B010101011011), // sett dom. 3M 5# 7m 11# 9m-9# (NO 13)
    DOM9MIN("7b9",    0B011010010011), // settima dom. 7m 9m 13M
    SUS4("sus4",      0B011010100101), // accord0 sus4 (senza terze, con 9 4 13M)
    DOM11AUM("7#11", 0B011011010101), // sett. dom. 7m 9M 11diesis 13M
    MINOR9MIN11MIN13("m7b911b133",  0B010110101011), // accordo minore 7m 9m 11giusta 13m
    DOM5AUM11AUM("7#5#11", 0B011101010101),// sett.dom. 5# 7m 9M 11# 13M
    DOMEXATONAL("7ex", 0B010101010101),// esatonale 5b 7m 9M 13b

    HALFDIM("m7b5",  0B010101001101), // accordo semidiminuito 5dim 7m 9M 13m
    DIM("o",          0B001001001001), //accordo diminuito 5dim 7dim(13M) 9M

    //DOUBLEHARM("Mb9b13", 0B100110010011), // scala double harmonic: 3M 5 7M 9m (11) 13b
    //MAJOR5AUM("5#",   0B101100010101), // acc magg 5# 9M 13M
    EMPTY("[ ]",         0B000000000000); // accordo vuoto













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

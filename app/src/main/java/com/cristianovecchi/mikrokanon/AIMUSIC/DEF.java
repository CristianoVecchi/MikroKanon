package com.cristianovecchi.mikrokanon.AIMUSIC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Cristiano on 26/02/2017.
 */

public class DEF {
    public static final String[] ENGLNOTENAMES = {"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};
    public static final String[] ITALNOTENAMES = {"Do","Do#","Re","Re#","Mi","Fa","Fa#","Sol","Sol#","La","La#","Si"};
    public static final String[] FRANNOTENAMES = {"Ut","Ut#","Re","Re#","Mi","Fa","Fa#","Sol","Sol#","La","La#","Si"};
    public static int[] PRIORITYSTATICASC = {0,1,-1,2,-2,3,-3,4,-4,5,-5,6,-6,7,-7,8,-8,9,-9,10,-10,11,-11};
    public static final  int MIDDLE_C = 60 ;
    public static int[] SIMPLENOTES = {0,2,4,5,7,9,11}; //DO RE MI FA SOL LA SI
    public static int[] AXX = {0, +1, -1, +2, -2}; // bequadro, diesis, bemolle, doppio diesis, doppio bemolle
    public static String[] AXXSTRING = {"", "#", "b", "x", "bb"};
    public static int[][][] ENHARMONICS ={
            // sono privilegiate le tonalità più comuni MAGGIORI
            { {0,0}, {6,1},{1,4}}, //DO enarmonico = DO SI# REbb
            { {1,2},{0,1},{6,3}}, // DO# =  REb DO# SIx
            { {1,0},{0,3},{2,4} }, // RE = RE DOx MIbb
            { {2,2},{1,1},{3,4}}, // RE# =  MIb RE# FAbb
            { {2,0},{3,2},{1,3}}, // MI = MI FAb REx
            { {3,0},{2,1},{4,4}}, // FA = FA MI# SOLbb
            { {3,1},{4,2},{2,3}}, // FA# = FA# SOLb MIx
            { {4,0},{3,3},{5,4}}, // SOL = SOL FAx LAbb
            { {5,2},{4,1}},       // SOL# =  LAb SOL#
            { {5,0},{4,3},{6,4}}, // LA = LA SOLx SIbb
            { {6,2},{5,1},{0,4}}, // LA# = SIb LA#  DObb
            { {6,0},{0,2},{5,3}} // SI = SI DOb LAx
    };

    public static String getSecondNote(int fund, int enharm, Interval interval, String[] noteNames){
        int namefirstnote = ENHARMONICS[fund][enharm][0];
        int namesecondnote = (namefirstnote + interval.getNoteDistance()) % 7;
        int axx = 0;
        int realsecondnote = (fund+interval.getHalftones()) % 12;
        for (int i = 0; i < ENHARMONICS[realsecondnote].length ; i++) {
            if (ENHARMONICS[realsecondnote][i][0]==namesecondnote){
                axx = ENHARMONICS[realsecondnote][i][1];
                return noteNames[SIMPLENOTES[namesecondnote]]+AXXSTRING[axx];
            }
        }

        return "["+currentNoteNames[SIMPLENOTES[ENHARMONICS[realsecondnote][0][0]]]
                +AXXSTRING[ENHARMONICS[realsecondnote][0][1]]+"]";

    };

    //en = ++en % DEF.ENHARMONICS[ch.getFund()].length;
    //ch.setEnharmonic(en);
    public static String getEnharmonicName(int fund, int enharm) {
        int note, axx;
        note = ENHARMONICS[fund][enharm][0];
        axx = ENHARMONICS[fund][enharm][1];
        return currentNoteNames[SIMPLENOTES[note]]+AXXSTRING[axx];
    }

    public static String getEnharmonicName(int fund, int enharm, String[] currentNoteNames) {
        int note, axx;
        note = ENHARMONICS[fund][enharm][0];
        axx = ENHARMONICS[fund][enharm][1];
        return currentNoteNames[SIMPLENOTES[note]]+AXXSTRING[axx];
    }
    public static String[] currentNoteNames = ITALNOTENAMES;

    public static String[] getNoteNamesFromDodecaByte(int dodecaByte){
        List<String> notes = new LinkedList<String>();

        if(dodecaByte == -1){
            notes.add("No notes.");
            return (String[])notes.toArray(new String[notes.size()]);
        }
        int flag = 1;
        for (int i = 0; i <12; i++) {
           if((dodecaByte & flag) >0){
               notes.add(currentNoteNames[i]);
           }
            flag <<=1;
        }

        return (String[])notes.toArray(new String[notes.size()]);
    }
    public static String[] getEnharmonicNoteNames(int notes, Chord chord, String[] noteNames){
        List<String> names = new LinkedList<String>();
        if(notes == -1){
            names.add("No notes.");
            return (String[])names.toArray(new String[names.size()]);
        }
        Map<Integer,Interval> map = new HashMap<Integer,Interval>();
        List<Interval> list = JazzInterpreter.getIntervals(chord.getChord());
        for ( Interval in: list ) {
            map.put(in.getHalftones(),in);
        }
        int flag =1;
        int fund = chord.getRoot();
       // System.out.println(map.keySet().toString());
        for (int i = 0; i <12 ; i++) {
            if ((notes & flag)>0) {

                int interv;
                if (i>=fund) interv = (i-fund)%12; else interv = (12 - fund +i )%12;
                Interval in = map.get(interv);
               // System.out.println("i="+i+"  interv="+interv+ "  fund="+fund);
                if (in!=null){
                    names.add(DEF.getSecondNote(chord.getRoot(),chord.getEnharmonic(),in, noteNames)+" "+in.getDefinition());
                }

            }
            flag = flag << 1;
        }


        return (String[])names.toArray(new String[names.size()]);
    }

    public static ArrayList<Chord> findChordList (int dodecaByte) {
        ArrayList<Chord> list = new ArrayList<Chord>();
        int[] fourths = {0,5,10,3,8,1,6,11,4,9,2,7};
        for (int i = 0; i < 12; i++) {
            boolean flag = false;
            for (JazzChord chord : JazzChord.values()) {
                int dByte = rotate(fourths[i], chord.getDbyte());
                if ((dodecaByte & dByte) == dodecaByte) {
                 //   System.out.print(DEF.currentNoteNames[i] + chord.getDef() + " ");
                    list.add(new Chord(fourths[i],chord));
                    flag = true;
                }
            }
            if (flag) System.out.println();
        }
        return list;
    }


    static private int rotate(int nTimes, int dByte){
        for(int i=0; i<nTimes; i++){
            dByte <<=1;
            if((dByte & 0B1000000000000 )== 0B1000000000000 ) dByte= dByte+1;
        }
        return dByte;
    }


}

package com.cristianovecchi.mikrokanon.AIMUSIC;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by Cristiano on 14/03/2017.
 */

public class JazzInterpreter {
    public static Interval[][] VOICING_FROM3RD_4PARTS = {Interval.INTERVALSBYTHIRDS[1],
            Interval.INTERVALSBYTHIRDS[2],Interval.INTERVALSBYTHIRDS[3],
            Interval.INTERVALSBYTHIRDS[4]}; // 3 5 7 9

    public static Interval[][] VOICING_FROM7TH_4PARTS = {Interval.INTERVALSBYTHIRDS[3],
            Interval.INTERVALSBYTHIRDS[4],Interval.INTERVALSBYTHIRDS[1],
            Interval.INTERVALSBYTHIRDS[2]}; // 7 9 3 5

    public static Interval[][] VOICING_FROM3RD_4PARTS_SUS4 = {Interval.INTERVALSBYTHIRDS[5],
            Interval.INTERVALSBYTHIRDS[2],Interval.INTERVALSBYTHIRDS[3],
            Interval.INTERVALSBYTHIRDS[4]}; // 4 5 7 9

    public static Interval[][] VOICING_FROM7TH_4PARTS_SUS4 = {Interval.INTERVALSBYTHIRDS[3],
            Interval.INTERVALSBYTHIRDS[4],Interval.INTERVALSBYTHIRDS[5],
            Interval.INTERVALSBYTHIRDS[2]}; // 7 9 4 5

    public static Interval[][] VOICING_FROM3RD_4PARTS_HDIM = {Interval.INTERVALSBYTHIRDS[1],
            Interval.INTERVALSBYTHIRDS[2],Interval.INTERVALSBYTHIRDS[3],
            Interval.INTERVALSBYTHIRDS[0]}; // 3 5 7 R

    public static Interval[][] VOICING_FROM7TH_4PARTS_HDIM = {Interval.INTERVALSBYTHIRDS[3],
            Interval.INTERVALSBYTHIRDS[0],Interval.INTERVALSBYTHIRDS[1],
            Interval.INTERVALSBYTHIRDS[2]}; // 7 R 3 5
    //VOICING WITH 13 //change 2 with 6
    public static Interval[][] VOICING_FROM3RD_4PARTS_13 = {Interval.INTERVALSBYTHIRDS[1],
            Interval.INTERVALSBYTHIRDS[6],Interval.INTERVALSBYTHIRDS[3],
            Interval.INTERVALSBYTHIRDS[4]}; // 3 13 7 9

    public static Interval[][] VOICING_FROM7TH_4PARTS_13 = {Interval.INTERVALSBYTHIRDS[3],
            Interval.INTERVALSBYTHIRDS[4],Interval.INTERVALSBYTHIRDS[1],
            Interval.INTERVALSBYTHIRDS[6]}; // 7 9 3 13





    public static Interval[][] adaptVoicingToSpecialCases(JazzChord jazzChord, Interval[][]VOICINGTYPE){
        if(jazzChord == JazzChord.SUS4){
            if(VOICINGTYPE==VOICING_FROM3RD_4PARTS) return VOICING_FROM3RD_4PARTS_SUS4;
            if(VOICINGTYPE==VOICING_FROM7TH_4PARTS) return VOICING_FROM7TH_4PARTS_SUS4;
            if(VOICINGTYPE==VOICING_FROM3RD_4PARTS_13) return VOICING_FROM3RD_4PARTS_SUS4;
            if(VOICINGTYPE==VOICING_FROM7TH_4PARTS_13) return VOICING_FROM7TH_4PARTS_SUS4;
        }
        if(jazzChord == JazzChord.HALFDIM){
            if(VOICINGTYPE==VOICING_FROM3RD_4PARTS) return VOICING_FROM3RD_4PARTS_HDIM;
            if(VOICINGTYPE==VOICING_FROM7TH_4PARTS) return VOICING_FROM7TH_4PARTS_HDIM;
            if(VOICINGTYPE==VOICING_FROM3RD_4PARTS_13) return VOICING_FROM3RD_4PARTS_HDIM;
            if(VOICINGTYPE==VOICING_FROM7TH_4PARTS_13) return VOICING_FROM7TH_4PARTS_HDIM;
        }
        if(jazzChord == JazzChord.DIM){
            if(VOICINGTYPE==VOICING_FROM3RD_4PARTS) return VOICING_FROM3RD_4PARTS_HDIM;
            if(VOICINGTYPE==VOICING_FROM7TH_4PARTS) return VOICING_FROM7TH_4PARTS_HDIM;
            if(VOICINGTYPE==VOICING_FROM3RD_4PARTS_13) return VOICING_FROM3RD_4PARTS_HDIM;
            if(VOICINGTYPE==VOICING_FROM7TH_4PARTS_13) return VOICING_FROM7TH_4PARTS_HDIM;
        }
        if(jazzChord == JazzChord.DOMALT){ //this chord hasn't 13!!!
            if(VOICINGTYPE==VOICING_FROM3RD_4PARTS_13) return VOICING_FROM3RD_4PARTS;
            if(VOICINGTYPE==VOICING_FROM7TH_4PARTS_13) return VOICING_FROM7TH_4PARTS;
        }
        if(jazzChord == JazzChord.DOMEXATONAL){ //this chord hasn't 13!!!
            if(VOICINGTYPE==VOICING_FROM3RD_4PARTS_13) return VOICING_FROM3RD_4PARTS;
            if(VOICINGTYPE==VOICING_FROM7TH_4PARTS_13) return VOICING_FROM7TH_4PARTS;
        }
        return VOICINGTYPE;
    }
    public static Interval[] getVoicingIntervals(JazzChord jazzChord, Interval[][]VOICINGTYPE){
        VOICINGTYPE = adaptVoicingToSpecialCases(jazzChord, VOICINGTYPE);
        List<Interval> intervalsByThirds = Interval.orderByThirds(getIntervals(jazzChord));
        Interval[] voicing = new Interval[VOICINGTYPE.length];
        for (int i = 0; i < VOICINGTYPE.length ; i++) {
            for (int j = 0; j <VOICINGTYPE[i].length ; j++) {
                if (intervalsByThirds.contains(VOICINGTYPE[i][j])){
                    voicing[i]=VOICINGTYPE[i][j];
                }
            }
        }

        return voicing;
    }

    //private static int dodecaByte = 0;
    public static List<Interval> getIntervals(JazzChord jazzChord) {
        List<Interval> list = new LinkedList<Interval>();
        int db = jazzChord.getDbyte(); //dodecaByte
        boolean fifthAug = false;
        boolean just3min = false;
        if (isIn(0,db)) list.add(Interval.UNISON);
        if (isIn(1,db)) list.add(Interval.I2MIN); // 9a minore
        if (isIn(2,db)) list.add(Interval.I2MAJ); // 9a maggiore
        if (isIn(3,db)) {
            if (isIn(4,db)) {
                list.add(Interval.I2AUM);
                list.add(Interval.I3MAJ);
            } else {
                list.add(Interval.I3MIN);
                just3min = true;
            }
        } else {
            if (isIn(4,db)) list.add(Interval.I3MAJ); // solo 3Magg senza 9aum.
        }
        if (isIn(5,db)) list.add(Interval.I4); // 11a giusta
        if (isIn(6,db)) { // 11a diesis
            if (isIn(7,db)) {
                list.add(Interval.I4AUM);
                list.add(Interval.I5);
            } else {
                if (isIn(8,db)){
                    if(just3min){
                        list.add(Interval.I5DIM);
                        list.add(Interval.I6MIN);
                        fifthAug = true;
                    } else {
                        list.add(Interval.I4AUM);
                        list.add(Interval.I5AUM);
                        fifthAug = true;
                    }

                } else {
                    list.add(Interval.I5DIM);
                }

            }
        } else {
            if (isIn(7,db)) list.add(Interval.I5);
        }
        if(!fifthAug){
            if (isIn(8,db)){
                if (isIn(7,db)) {
                    list.add(Interval.I6MIN);
                } else {
                    list.add(Interval.I5AUM);
                }
            }
        }

        if (isIn(9,db)){ // 13maggiore
            if(isIn(10,db)){ // 7a minore
                list.add(Interval.I6MAJ);
                list.add(Interval.I7MIN);
            } else {
                if(isIn(11,db)){
                    list.add(Interval.I6MAJ);
                    list.add(Interval.I7MAJ);
                } else {
                    list.add(Interval.I7DIM);
                }

            }
        } else {
            if (isIn(10,db)) {
                list.add(Interval.I7MIN);
            } else {
                if (isIn(11,db)) {
                    list.add(Interval.I7MAJ);
                }
            }
        }




        return list;
    }

    private static boolean isIn(int note, int dodecaByte){ // da 0 a 11
        int flag = (int)Math.pow((double)2, (double)note);
        return ((dodecaByte & flag) >0);
    }

    public static Interval[][] chooseVoicingType4Parts(int root, JazzChord jazzChord, int center){
       // System.out.println("chooseVoicingType4Parts, jazzChord:" + jazzChord.getDef());
        double dcenter = (double) center;
       // System.out.println();
        Interval[] voicing3rd = JazzInterpreter.getVoicingIntervals(jazzChord,VOICING_FROM3RD_4PARTS);
        Interval[] voicing7th = JazzInterpreter.getVoicingIntervals(jazzChord,VOICING_FROM7TH_4PARTS);
       /* for(int i=0; i<voicing3rd.length; i++){
            String name = voicing3rd[i]==null ? "empty" : voicing3rd[i].getDefinition();
            System.out.print(name+" ");
        }
        System.out.println();*/
       // if(voicing3rd[0]==null) return VOICING_FROM3RD_4PARTS;
        int min3rd = voicing3rd[0].getHalftones()+root;
        int max3rd = voicing3rd[voicing3rd.length-1].getHalftones()+root;
        if (min3rd>center) {min3rd-=12;max3rd-=12;}
        if (max3rd<min3rd) max3rd+=12;
        //if(voicing7th[0]==null) return VOICING_FROM3RD_4PARTS;
        int min7th = voicing7th[0].getHalftones()+root;
        int max7th = voicing7th[voicing7th.length-1].getHalftones()+root;
        if (max7th>center) {min7th-=12;max7th-=12;}
        if (max7th<min7th) max7th+=12;
        double medium3rd = ((double)(min3rd + max3rd))/2;
        double medium7th = ((double)(min7th + max7th))/2;
       // System.out.println("Chord:"+ jazzChord.getDef()+" medium3rd="+medium3rd+" medium7th="+medium7th);
        if(Math.abs(dcenter-medium3rd)<Math.abs(dcenter-medium7th)){
            return VOICING_FROM3RD_4PARTS;
        } else {
            return VOICING_FROM7TH_4PARTS;
        }
    }
    public static Interval[][] chooseVoicingType4Parts_13(int root, JazzChord jazzChord, int center){
        // System.out.println("chooseVoicingType4Parts, jazzChord:" + jazzChord.getDef());
        double dcenter = (double) center;
        // System.out.println();
        Interval[] voicing3rd = JazzInterpreter.getVoicingIntervals(jazzChord,VOICING_FROM3RD_4PARTS_13);
        Interval[] voicing7th = JazzInterpreter.getVoicingIntervals(jazzChord,VOICING_FROM7TH_4PARTS_13);
       /* for(int i=0; i<voicing3rd.length; i++){
            String name = voicing3rd[i]==null ? "empty" : voicing3rd[i].getDefinition();
            System.out.print(name+" ");
        }
        System.out.println();*/
        // if(voicing3rd[0]==null) return VOICING_FROM3RD_4PARTS;
        int min3rd = voicing3rd[0].getHalftones()+root;
        int max3rd = voicing3rd[voicing3rd.length-1].getHalftones()+root;
        if (min3rd>center) {min3rd-=12;max3rd-=12;}
        if (max3rd<min3rd) max3rd+=12;
        //if(voicing7th[0]==null) return VOICING_FROM3RD_4PARTS;
        int min7th = voicing7th[0].getHalftones()+root;
        int max7th = voicing7th[voicing7th.length-1].getHalftones()+root;
        if (max7th>center) {min7th-=12;max7th-=12;}
        if (max7th<min7th) max7th+=12;
        double medium3rd = ((double)(min3rd + max3rd))/2;
        double medium7th = ((double)(min7th + max7th))/2;
        // System.out.println("Chord:"+ jazzChord.getDef()+" medium3rd="+medium3rd+" medium7th="+medium7th);
        if(Math.abs(dcenter-medium3rd)<Math.abs(dcenter-medium7th)){
            return VOICING_FROM3RD_4PARTS_13;
        } else {
            return VOICING_FROM7TH_4PARTS_13;
        }
    }
/*
    public static void main (String[] args) {
        for (JazzChord jc : JazzChord.values()
        ) {
            Interval[] voicing = JazzInterpreter.getVoicingIntervals(jc,VOICING_FROM3RD_4PARTS);
            System.out.print(jc.getDef() + " :   ");
            for(int i=0; i<voicing.length; i++){
                String name = voicing[i]==null ? "empty" : voicing[i].getDefinition();
                System.out.print(name+" ");
            }
            System.out.println();
            voicing = JazzInterpreter.getVoicingIntervals(jc,VOICING_FROM7TH_4PARTS);
            System.out.print(jc.getDef() + " :   ");
            for(int i=0; i<voicing.length; i++){
                String name = voicing[i]==null ? "empty" : voicing[i].getDefinition();
                System.out.print(name+" ");
            }
            System.out.println();
        }
    }*/
    // TEST : DO_ok DO#_ok REb_ok RE_ok RE#_ok MIb_ok MI_ok FA_ok
   /* public static void main (String[] args){
        int fund = 5 ; int enharm = 0;
        for (JazzChord jc: JazzChord.values()
             ) {
           List<Interval> list = getIntervals(jc);
            System.out.print(jc.getDef()+" :   ");
            for ( Interval in: list
                 ) {
               System.out.print(in.getDefinition()+" ");
                System.out.print(DEF.getSecondNote(fund,enharm,in)+"   ");
            }
            System.out.println();
        }
    }*/
   /*
    public static void main (String[] args) {
        int fund = 0;
        int enharm = 0;
        for (JazzChord jc : JazzChord.values()
        ) {
            Interval[] list = findIntervalsInChromaticScale(jc);
            System.out.print(jc.getDef() + " :   ");
            for(int i=0; i<12; i++){
                System.out.print(i+ "=");
                System.out.print(DEF.getSecondNote(fund, enharm, list[i], G.currentNoteNames) + "  ");
            }
            System.out.println();
        }
    }*/
    public static String[] getChromaticScaleNames(int root, int enharm,
                                                  JazzChord jazzChord, String[] noteNames){
        String[] scale = new String[12];
        Interval[] list = findIntervalsInChromaticScale(jazzChord);
        for(int i=0; i<12; i++){
            scale[i]=DEF.getSecondNote(root,enharm,list[i], noteNames);
        }
        return scale;
    }
    public static Interval[] findIntervalsInChromaticScale(JazzChord jc) {//pitch from 0 to 11
        Interval[] intervals = new Interval[12];
        List<Interval> list = getIntervals(jc);
        for (int i = 0; i < 12; i++) {
            intervals[i]= Interval.UNISON;
            for (Interval in : list
            ) {
                if (i == in.getHalftones()) {
                    intervals[i] = in;
                } else {
                    switch (i) {
                        case 1: //2m
                            intervals[i] = Interval.I2MIN;
                            break;
                        case 2: //2M
                            intervals[i] = Interval.I2MAJ;
                            break;
                        case 3: //3m or 2aum
                            if (list.contains(Interval.I3MAJ)) {
                                intervals[i] = Interval.I2AUM;
                            } else {
                                intervals[i] = Interval.I3MIN;
                            }
                            break;
                        case 4://3M or 4dim
                            if (list.contains(Interval.I3MIN) && list.contains(Interval.I5DIM)) {
                                intervals[i] = Interval.I4DIM;
                            } else {
                                intervals[i] = Interval.I3MAJ;
                            }
                            break;
                        case 5://4 or 3aum
                            if (list.contains(Interval.I2AUM) && list.contains(Interval.I5DIM)) {
                                intervals[i] = Interval.I3AUM;
                            } else {
                                intervals[i] = Interval.I4;
                            }
                            break;
                        case 6://4aum or 5dim
                            if (list.contains(Interval.I5) || list.contains(Interval.I5AUM)) {
                                intervals[i] = Interval.I4AUM;
                            } else {
                                intervals[i] = Interval.I5DIM;
                            }
                            break;
                        case 7://5
                            intervals[i] = Interval.I5;
                            break;
                        case 8://5aum or 6min
                            if (list.contains(Interval.I6MAJ) || list.contains(Interval.I6AUM)) {
                                intervals[i] = Interval.I5AUM;
                            } else {
                                intervals[i] = Interval.I6MIN;
                            }
                            break;
                        case 9://6MAJ or 7dim
                            if (list.contains(Interval.I7MIN) || list.contains(Interval.I7MAJ)) {
                                intervals[i] = Interval.I6MAJ;
                            } else {
                                intervals[i] = Interval.I7DIM;
                            }
                            break;
                        case 10://7min or 6aum
                            if (list.contains(Interval.I7MAJ)) {
                                intervals[i] = Interval.I6AUM;
                            } else {
                                intervals[i] = Interval.I7MIN;
                            }
                            break;
                        case 11://7Maj
                            intervals[i] = Interval.I7MAJ;
                    }
                }

            }
        }
        return intervals;
    }


    public static Interval[][] switchVoicingType(Interval[][] voicingType) {
        if (voicingType==VOICING_FROM3RD_4PARTS) return VOICING_FROM7TH_4PARTS;
        if (voicingType==VOICING_FROM7TH_4PARTS) return VOICING_FROM3RD_4PARTS;
        return voicingType;
    }
    public static Interval[][] switchVoicingType_13(Interval[][] voicingType) {
        if (voicingType==VOICING_FROM3RD_4PARTS_13) return VOICING_FROM7TH_4PARTS_13;
        if (voicingType==VOICING_FROM7TH_4PARTS_13) return VOICING_FROM3RD_4PARTS_13;
        return voicingType;
    }
}

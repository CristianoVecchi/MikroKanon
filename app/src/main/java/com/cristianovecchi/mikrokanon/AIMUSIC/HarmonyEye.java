package com.cristianovecchi.mikrokanon.AIMUSIC;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class HarmonyEye {
    public static final int I7M = 11;
    public static final int I7m = 10;
    public static final int I6M = 9;
    public static final int I6m = 8;
    public static final int I5 = 7;
    public static final int I4a = 6;
    public static final int I4 = 5;
    public static final int I3M = 4;
    public static final int I3m = 3;
    public static final int I2M = 2;
    public static final int I2m = 1;
    public static int[] findIntervals(boolean[] notes){
        int[] intervals = {0,0,0,0,0,0};

        for(int i=0; i<11; i++){
            for(int j=i+1;j<12; j++){
                if(notes[i] && notes[j]){
                    int d = j-i;
                    if(d == 1 || d==11) intervals[0]++;
                    if(d == 2 || d==10) intervals[1]++;
                    if(d == 3 || d==9) intervals[2]++;
                    if(d == 4 || d==8) intervals[3]++;
                    if(d == 5 || d==7) intervals[4]++;
                    if(d == 6) intervals[5]++;
                }
            }
        }
        return intervals;
    }
    public static int[] findIntervals(int by){
        int[] intervals = {0,0,0,0,0,0};
        int check = 1;
        int check2 =2;
        for(int i=0; i<11; i++){
            for(int j=i+1;j<12; j++){
                if(((check & by)>0) && ((check2 & by))>0){
                    int d = j-i;
                    if(d == 1 || d==11) intervals[0]++;
                    if(d == 2 || d==10) intervals[1]++;
                    if(d == 3 || d==9) intervals[2]++;
                    if(d == 4 || d==8) intervals[3]++;
                    if(d == 5 || d==7) intervals[4]++;
                    if(d == 6) intervals[5]++;
                }
                check2 = check2 << 1;
            }
            check = check << 1;
        }
        return intervals;
    }
    public static HarmonyResult findHarmonyResult(int dodecaByte){
        boolean[] bools = HarmonyEye.selNotesFrom12Byte(dodecaByte);
        boolean[] revBools = new boolean[12];
        for(int i=0; i<12; i++){
            revBools[i] = bools[11-i];
        }
        return findHarmonyResult(revBools);
    }
    public static HarmonyResult findHarmonyResult(boolean[] notes){
        boolean[] notes2 = new boolean[24];
        for(int i=0; i<12; i++){
            if(notes[i]){ notes2[i]=true; notes2[i+12]=true;}
            else{ notes2[i]=false; notes2[i+12]=false;}
        }
        HarmonyResult hr = null;
        int[] diss = {2048,8,128,4,512,2,64,1024,32,1,256,16,2048,8,128,4,512,2,64,1024,32,1,256,16};
        int[] fund = {0,0,0,0,0,0, 0,0,0,0,0,0};

        for(int i=0; i<12; i++){ // assegna il peso armonico a ogni possibile fondamentale
            if(!notes[i]){ continue; }
            for (int j=0; j<12; j++){

                if(notes2[j+i]){ fund[i] = fund[i]+diss[j];}
            }
        }
        int max=0;
        for(int i=0; i<12; i++){ // trova il valore massimo
            if(fund[i]>max) max=fund[i];
        }
        List<Integer> list = new LinkedList<Integer>();
        for(int i=0; i<12; i++){ // elenca le possibili fondamentali
            if(fund[i]==max) list.add(i);
        }
        if(list.isEmpty() || (max==0)){
            return new HarmonyResult(null,0);
        }
        int[] fundIndeces = new int[list.size()]; //costruisce l'oggetto HarmonyResult
        int count=0;
        for(Integer i: list){
            fundIndeces[count++] = i;
        }
        hr = new HarmonyResult(fundIndeces, max);
        return hr;
    }

    public static int countNotes(boolean[] notes){
        int c = 0;
        for(int i=0; i<notes.length; i++){
            if(notes[i]) c++;
        }
        return c;
    }
    public static int[] extractAbstractNotes(boolean[]notes){
        int c = countNotes(notes);
        if (c==0) return null;
        int[] abs = new int[c];
        int count = 0;
        for(int i=0; i<notes.length; i++){
            if(notes[i]) {abs[count]=i;count++;}
        }
        return abs;
    }
    public static int[] getActualScaleFromFund(int[]orderedAbsNotes){
        int[] scale = new int[orderedAbsNotes.length];
        scale[0] = orderedAbsNotes[0];
        //int[] notfund = Arrays.copyOfRange(orderedAbsNotes, 1, orderedAbsNotes.length);
        int[] notfund = new int[orderedAbsNotes.length-1];
        for(int i=0; i<notfund.length; i++){
            notfund[i] = orderedAbsNotes[i+1];
        }
        Arrays.sort(notfund);
        for(int i=0; i<notfund.length;i++){
            scale[i+1] = notfund[i]<scale[0] ? notfund[i]+12 : notfund[i];
        }
        Arrays.sort(scale);
        return scale;
    }

    public static int[] orderAbstractNotes(boolean[]notes, int root){
        int[] abs = extractAbstractNotes(notes);
        if(abs==null) return null;
        int c=0;
        int p = root; abs[0]=p;

        p = (root+ I5) %12;
        if(notes[p]) abs[++c]=p;
        p = (root+ I3M) %12;
        if(notes[p]) abs[++c]=p;
        p = (root+ I7m) %12;
        if(notes[p]) abs[++c]=p;
        p = (root+ I2M )%12;
        if(notes[p]) abs[++c]=p;
        p = (root+ I4a) %12;
        if(notes[p]) abs[++c]=p;
        p = (root+ I6m) %12;
        if(notes[p]) abs[++c]=p;
        p = (root+ I7M) %12;
        if(notes[p]) abs[++c]=p;
        p = (root+ I2m) %12;
        if(notes[p]) abs[++c]=p;
        p = (root+ I3m) %12;
        if(notes[p]) abs[++c]=p;
        p = (root+ I4) %12;
        if(notes[p]) abs[++c]=p;
        p = (root+ I6M) %12;
        if(notes[p]) abs[++c]=p;

        return abs;
    }
    public static String getOrderedNotesText(boolean[] selNotes, String[] noteNames){
        StringBuffer sb = new StringBuffer();
        HarmonyResult hr = findHarmonyResult(selNotes);
        if(hr.roots == null) return sb.append("NONE").toString();
        int[] list = orderAbstractNotes(selNotes, hr.roots[0]);

        if(list.length == 12) return sb.append("ALL").toString();
        for(int i=0; i<list.length;i++){
            sb.append(noteNames[list[i]]+" ");
        }
        //Log.d("HarmonyEye-getOrdered...", sb.toString());

        return sb.toString();
    }
    public static boolean areSelNotesInLimits(boolean[] selNotes, int[]limits){
        int[] intervals = HarmonyEye.findIntervals(selNotes);
        for(int i=0; i<limits.length; i++){
            if(intervals[i]>limits[i]) return false;
        }
        return true;
    }
    public static boolean is12ByteInLimits(int by, int[]limits){
        int[] intervals = HarmonyEye.findIntervals(by);
        for(int i=0; i<limits.length; i++){
            if(intervals[i]>limits[i]) return false;
        }
        return true;
    }

    public static List<ChordData> getChordDataList(int[] limits) {
        List<ChordData> list= new LinkedList<ChordData>();
        for(int i=0; i<4096; i++){
            boolean[] selNotes = selNotesFrom12Byte(i);
            if(areSelNotesInLimits(selNotes, limits)){
                //if(is12ByteInLimits(i, limits)){

                boolean[] sn = selNotesFrom12Byte(i);
                list.add(new ChordData(sn));
            }

        }
        Collections.sort(list);
        return list;
    }
    public static boolean[] selNotesFrom12Byte(int by){
        boolean[] bl = {false,false,false,false,false,false, false,false,false,false,false,false};
        int check = 2048;
        for(int i=0; i<12; i++){
            if((by & check) > 0) {
                bl[i] = true;

                //Log.d("selNotesFrom", "check="+check+" by="+by+" bl[i]=" + bl[i]);
            }
            check = check >>>1;
        }
        return bl;
    }
}

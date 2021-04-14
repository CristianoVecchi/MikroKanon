package com.cristianovecchi.mikrokanon.AIMUSIC;

public class AIRhythm {
    public static int[] findOffBeats(int nBeats){
        if (nBeats==1) return new int[]{1};
        int nOffBeats = nBeats/2;
        int[] offBeats = new int[nOffBeats];
        for (int i = 0; i <nOffBeats ; i++) {
           offBeats[i]= i*2+1;
        }
        return offBeats;
    }
}

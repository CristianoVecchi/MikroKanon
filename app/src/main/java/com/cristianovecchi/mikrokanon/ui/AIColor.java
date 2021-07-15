package com.cristianovecchi.mikrokanon.ui;

public class AIColor {

    public static int colorDistance(int startColor, int aimColor){
        int R1 = (startColor >> 16) & 0xff;
        int G1 = (startColor >>  8) & 0xff;
        int B1 = (startColor      ) & 0xff;

        int R2 = (aimColor >> 16) & 0xff;
        int G2 = (aimColor >>  8) & 0xff;
        int B2 = (aimColor      ) & 0xff;

        int dR = Math.abs(R2-R1);
        int dG = Math.abs(G2-G1);
        int dB = Math.abs(B2-B1);
        return dR + dG + dB;//it's not a color!!!!
    }

    public static boolean isLighterOf(int color, int limitColor){
        int R1 = (color >> 16) & 0xff;
        int G1 = (color >>  8) & 0xff;
        int B1 = (color      ) & 0xff;

        int R2 = (limitColor >> 16) & 0xff;
        int G2 = (limitColor >>  8) & 0xff;
        int B2 = (limitColor      ) & 0xff;

        if(R1>R2 && G1>G2 && B1>B2) return true;
        return false;
    }
}

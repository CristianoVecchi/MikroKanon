package com.cristianovecchi.mikrokanon.ui;

import android.content.Context;
import android.content.res.TypedArray;

import com.cristianovecchi.mikrokanon.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AIColor {

    public static List<Integer[]> getMultiTypedArray(Context context, String key ) {
        return getMultiTypedArray(context, key, 0f);
    }

    public static List<Integer[]> getMultiTypedArray(Context context, String key, Float shiftForDoubling ) {
        List<TypedArray> array = new ArrayList<>();

        try {
            Class<R.array> res = R.array.class;
            Field field;
            int counter = 0;

            do {
                field = res.getField(key + "_" + counter);
                array.add(context.getResources().obtainTypedArray(field.getInt(null)));
                counter++;
            } while (field != null);
        } catch (Exception e) {
            // e.printStackTrace();
        } finally {
            int size;
            List<Integer[]> result;
            if(shiftForDoubling != 0f){
                size = array.size() * 3;
            } else {
                size = array.size();
            }
            result = new ArrayList<>(size);
            for(TypedArray el : array){
                Integer[] intArray = new Integer[7];
                for(int i=0; i<7; i++){
                    intArray[i] = el.getColor(i,0);
                }
                result.add(intArray);
            }
            if(shiftForDoubling != 0f){
                for(int i=0; i<array.size(); i++){
                    Integer[] intArray = new Integer[7];
                    Integer[] intArray2 = new Integer[7];
                    Integer[] source = result.get(i);
                    for(int q=0; q<7; q++){
                        intArray[q] = AIColor.shiftColor(source[q],shiftForDoubling);
                        intArray2[q] = AIColor.shiftColor(source[q],-shiftForDoubling);
                    }
                    result.add(intArray);
                    result.add(intArray2);
                }
            }
            return result;
        }
    }

    public static int shiftColor(int color, float shift){
        int R = (color >> 16) & 0xff;
        int G = (color >>  8) & 0xff;
        int B = (color      ) & 0xff;
        int diff = (int) (shift * 256 );
        R += diff;
        if(R > 255) R = 255;
        if(R < 0) R = 0;
        G += diff;
        if(G > 255) G = 255;
        if(G < 0) G = 0;
        B += diff;
        if(B > 255) B = 255;
        if(B < 0) B = 0;
        return (255 << 24) | (R << 16) | (G << 8) | (B);
    }
    public static int averageColor(int color1, int color2){
        int R1 = (color1 >> 16) & 0xff;
        int G1 = (color1 >>  8) & 0xff;
        int B1 = (color1      ) & 0xff;
        int R2 = (color2 >> 16) & 0xff;
        int G2 = (color2 >>  8) & 0xff;
        int B2 = (color2      ) & 0xff;
        int Rsum = R1 + R2;
        int Gsum = G1 + G2;
        int Bsum = B1 + B2;
        int Ra, Ga, Ba;
        if(Rsum == 0) {Ra = 0;} else {Ra = Rsum / 2;}
        if(Gsum == 0) {Ga = 0;} else {Ga = Rsum / 2;}
        if(Bsum == 0) {Ba = 0;} else {Ba = Rsum / 2;}
        return (255 << 24) | (Ra << 16) | (Ga << 8) | (Ba);
    }

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

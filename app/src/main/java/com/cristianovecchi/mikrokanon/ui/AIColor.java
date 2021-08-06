package com.cristianovecchi.mikrokanon.ui;

import android.content.Context;
import android.content.res.TypedArray;

import com.cristianovecchi.mikrokanon.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AIColor {

    public static List<TypedArray> getMultiTypedArray(Context context, String key) {
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
            return array;
        }
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

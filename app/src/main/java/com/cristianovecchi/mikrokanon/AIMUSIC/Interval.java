package com.cristianovecchi.mikrokanon.AIMUSIC;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Cristiano on 09/03/2017.
 */


public enum Interval {
    UNISON(0,0, "R"),
    I2MIN(1,1, "m9"),
    I2MAJ(2,1, "M9"),
    I2AUM(3,1, "a9"),
    I3DIM(2,2, "d3"),
    I3MIN(3,2, "m3"),
    I3MAJ(4,2, "M3"),
    I3AUM(5,2, "a3"),
    I4DIM(4,3, "d11"),
    I4(5,3, "11"),
    I4AUM(6,3, "a11"),
    I5DIM(6,4, "d5"),
    I5(7,4, "5"),
    I5AUM(8,4, "a5"),
    I6MIN(8,5, "m13"),
    I6MAJ(9,5, "M13"),
    I6AUM(10,5, "a13"),
    I7DIM(9,6, "d7"),
    I7MIN(10,6, "m7"),
    I7MAJ(11,6, "M7")

    ;

    public static Interval[][] INTERVALSBYTHIRDS = {{UNISON},{I3DIM,I3MIN,I3MAJ,I3AUM},
            {I5DIM,I5,I5AUM},{I7DIM,I7MIN,I7MAJ},{I2MIN,I2MAJ,I2AUM},{I4DIM,I4,I4AUM},
            {I6MIN,I6MAJ,I6AUM}};

    public static List<Interval> orderByThirds(List<Interval> list){
        List<Interval> thirds = new LinkedList<>();
        for (int i = 0; i < INTERVALSBYTHIRDS.length ; i++) {
            for (int j = 0; j < INTERVALSBYTHIRDS[i].length ; j++) {
                for (int k = 0; k < list.size(); k++) {
                    Interval interval = list.get(k);
                    if(interval== INTERVALSBYTHIRDS[i][j]) thirds.add(interval);
                }
            }
        }
        return thirds;
    }
    private int halftones, noteDist;
    private String def;

    private Interval(int halftones, int noteDist, String def){
        this.halftones = halftones;
        this.noteDist = noteDist;
        this.def = def;
    }


    public int getHalftones() {return halftones;}
    public int getNoteDistance() {return noteDist;}
    public String getDefinition() {return def;}

}

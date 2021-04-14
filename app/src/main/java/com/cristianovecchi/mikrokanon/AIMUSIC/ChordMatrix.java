package com.cristianovecchi.mikrokanon.AIMUSIC;

import java.util.LinkedList;
import java.util.List;

public class ChordMatrix {
    private int[][][] matrix;
    private int[][] durMatrix;
    public ChordMatrix(int nChords, int nIntervals){
        matrix = new int[nChords][nIntervals][nChords];
        for (int i = 0; i <nChords ; i++) {
            for (int j = 0; j <nIntervals ; j++) {
                for (int k = 0; k <nChords ; k++) {
                    matrix[i][j][k] = 0;
                }
            }
        }
        durMatrix = new int[nChords][16];
        for (int i = 0; i < durMatrix.length ; i++) {
            for (int j = 0; j < durMatrix[i].length ; j++) {
                durMatrix[i][j] = 0;
            }
        }
    }
    public void setLink(int startChord, int interval, int aimChord){
        matrix[startChord][interval][aimChord]++;
        System.out.println("Matrix: set Link "+startChord+" "+interval+" "+aimChord);
    }
    public void unsetLink(int startChord, int interval, int aimChord){
        matrix[startChord][interval][aimChord]--;
        if(matrix[startChord][interval][aimChord] < 0) matrix[startChord][interval][aimChord] = 0;
    }
    public void setDurationLink(int chord, int duration){
        duration--;
        if(duration > durMatrix[0].length) duration = durMatrix[0].length;
        durMatrix[chord][duration]++;
    }
    public void unsetDurationLink(int chord, int duration){
        duration--;
        if(duration > durMatrix[0].length) duration = durMatrix[0].length;
        durMatrix[chord][duration]--;
        if(durMatrix[chord][duration]<0) durMatrix[chord][duration] = 0;
    }
    public void setLink(JazzChord startJazzChord, int interval, JazzChord aimJazzChord){
        int startChord = JazzChord.getJazzChordIndex(startJazzChord);
        int aimChord = JazzChord.getJazzChordIndex(aimJazzChord);
        if (startChord==-1 || aimChord==-1)return;
        setLink(startChord,interval,aimChord);
    }
    public void unsetLink(JazzChord startJazzChord, int interval, JazzChord aimJazzChord){
        int startChord = JazzChord.getJazzChordIndex(startJazzChord);
        int aimChord = JazzChord.getJazzChordIndex(aimJazzChord);
        if (startChord==-1 || aimChord==-1)return;
        unsetLink(startChord,interval,aimChord);
    }
    public void setLinks(Chord[] chords){

        for (int i = 0; i < chords.length ; i++) {
            Chord startChord = chords[i];
            Chord aimChord = chords[(i+1)%chords.length];
            int startRoot = startChord.getRoot(); int aimRoot = aimChord.getRoot();
            if(startRoot>aimRoot)startRoot -=12;
            int interval = aimRoot - startRoot;
            setLink(startChord.getChord(),interval,aimChord.getChord());
        }
    }
    public void setLinksAndDurations(Chord[] chords, int[] durations, int divisor){
        setLinks(chords);
        setDurationLinks(chords, durations, divisor);
    }
    public void setDurationLinks(Chord[] chords, int[] durations, int divisor){
        for (int i = 0; i < chords.length ; i++) {
            int ch = JazzChord.getJazzChordIndex(chords[i].getChord());
            int dur = durations[i] / divisor;
            setDurationLink(ch,dur);
        }
    }
    public List<Chord> findMostUsedChords(Chord startChord){
        int startRoot = startChord.getRoot();
        List<Chord> chords = new LinkedList<>();
        int max = -1;
        int[][] subMatrix = matrix[JazzChord.getJazzChordIndex(startChord.getChord())];
        for (int i = 0; i <subMatrix.length ; i++) { // find the max value
            for (int j = 0; j <subMatrix[i].length ; j++) {
                if(subMatrix[i][j]>max) max = subMatrix[i][j];
            }
        }
        for (int i = 0; i <subMatrix.length ; i++) { // add the chords
            for (int j = 0; j <subMatrix[i].length ; j++) {
                if(subMatrix[i][j]==max) {
                   chords.add(new Chord((startRoot+i)%12,JazzChord.values()[j]));
                }
            }
        }
        return chords;
    }
    public List<Chord> findWeightedChords(Chord startChord){
        int startRoot = startChord.getRoot();
        List<Chord> chords = new LinkedList<>();
        int[][] subMatrix = matrix[JazzChord.getJazzChordIndex(startChord.getChord())];
        for (int i = 0; i <subMatrix.length ; i++) {
            int newRoot = (startRoot+i)%12;
            for (int j = 0; j <subMatrix[i].length ; j++) {
                if(subMatrix[i][j]==0) continue;
                Chord newChord = new Chord(newRoot,JazzChord.values()[j]);
                for (int k = 0; k <subMatrix[i][j] ; k++) {
                    chords.add(newChord); // multiple pointers at single chord
                }
            }
        }
        return chords;
    }
    public List<Integer> findWeightedDurations(JazzChord chord){
        int index = JazzChord.getJazzChordIndex(chord);
        int[] subMatrix = durMatrix[index];
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < subMatrix.length; i++) {
            for (int j = 0; j <subMatrix[i] ; j++) {
                list.add(i+1);
            }
        }
        return list;
    }
    public List<JazzChord> findWeightedJazzChord(){
        List<JazzChord> chords = new LinkedList<>();
        for (int i = 0; i <matrix.length ; i++) {
            for (int j = 0; j <matrix[i].length ; j++) {
                for (int k = 0; k <matrix[i][j].length ; k++) {
                    JazzChord jc = JazzChord.values()[k];
                    for (int l = 0; l < matrix[i][j][k] ; l++) {
                        chords.add(jc);
                    }
                }
            }
        }
        return chords;
    }
    public JazzChord chooseRandomJazzChordFromWeightedList(){
        List<JazzChord> chords = findWeightedJazzChord();
        if(chords.isEmpty()){
            return JazzChord.values()[ (int)(Math.random()*JazzChord.values().length) ];
        }
        return chords.get((int)(Math.random()*chords.size()));
    }
    public Chord chooseRandomChordFromWeightedList(Chord startChord){
        List<Chord> chords = findWeightedChords(startChord);
        if(chords.isEmpty()){
            int randomIndex =(int) (Math.random()*JazzChord.values().length);
            return new Chord((int)(Math.random()*12),JazzChord.values()[randomIndex]);
        }
        return chords.get((int)(Math.random()*chords.size()));
    }
    public int chooseRandomDurationFromWeightedList(JazzChord chord){
        List<Integer> list = findWeightedDurations(chord);
        if(list.isEmpty()){
            return ((int)(Math.random()*durMatrix[0].length));
        }
        return list.get((int)(Math.random()*list.size()));
    }
    public List<JazzChord> findMostUsedJazzChords(){
        List<JazzChord> chords = new LinkedList<>();
        int max = -1;

        int[] sums = new int[matrix.length];
        for (int i = 0; i <sums.length ; i++) {
            int sum = 0;
            for (int j = 0; j <matrix[i].length ; j++) {
                for (int k = 0; k < matrix[i][j].length ; k++) {
                    sum += matrix[i][j][k];
                }
            }
            sums[i] = sum;
        }
        for (int i = 0; i <sums.length ; i++) {
            if(sums[i]>max){ max = sums[i];}
        }
        for (int i = 0; i <sums.length ; i++) {
            if(sums[i]==max) chords.add(JazzChord.values()[i]);
        }
        return chords;
    }
}

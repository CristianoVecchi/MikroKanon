package com.cristianovecchi.mikrokanon.AIMUSIC;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Cristiano on 30/03/2017.
 */

public class AbsVoice {

    public int[] absNotes;
    public AbsVoice(int[] absNotes){
        this.absNotes = absNotes;
    }
    public String toString(){
        return Arrays.toString(absNotes);
    }
    public static AbsVoice extractFromChordList(int nVoices, int start, int[] priority,
                                                int[][] absChords ){
        int nChords = absChords.length;
        AbsVoice voice = new AbsVoice(new int[nChords]);
        voice.absNotes[0] = start;
        if (nChords == 1) return voice;
        for (int i = 1; i < nChords; i++) {
           int prev = voice.absNotes[i-1];
            voice.absNotes[i] = -1;
            prioritySearch:
            for (int j = 0; j < priority.length; j++) {
                for (int k = 0; k < absChords[i].length ; k++) {
                    int note = absChords[i][k];

                    if((note==-1)||((prev-note) % 12 == priority[j])){
                        voice.absNotes[i] = note;
                        break prioritySearch;
                    }
                }
            }
        }
        return voice;
    }

    public static AbsVoice[] voicesFromChords(int[][] chords, int[] priority, boolean sort){
        int[][] nchords;
        if(sort) nchords = sortPolyphonicChords(chords,priority);
        else nchords = chords;
        AbsVoice[] voices = new AbsVoice[chords[0].length];
        for (int i = 0; i < voices.length ; i++) {
            voices[i] = new AbsVoice(new int[nchords.length]);
            for (int j = 0; j < nchords.length; j++) {
                //Log.i("AbsVoice", "i="+i+" j="+j);
               // System.out.println("i="+i+" j="+j);
                voices[i].absNotes[j] = nchords[j][i];
            }
        }
        return voices;
    }
    public static int[][] deleteAbsVoiceFromAbsChords(AbsVoice voice, int[][] chords){
        int[][] newChords = new int[chords.length][];
        for (int i = 0; i <voice.absNotes.length ; i++) {
            List<Integer> notes = new LinkedList<>();
            int flag = voice.absNotes[i];
            for (int j = 0; j <chords[i].length ; j++) {
                int note =  chords[i][j];
                if(note != flag) notes.add(note);
            }

            newChords[i] = new int[notes.size()];
            for (int j = 0; j < notes.size(); j++) {
                newChords[i][j] = notes.get(j);
            }
        }
        return newChords;
    }
    public static AbsVoice[] extractAbsVoicesFromAbsChords(int nVoices,
                                                           int[] priority, int[][] chords){
        int[] starts = normalize(nVoices,chords[0].clone());
        //if(chords.length>1) starts = findOptimalStarts(starts,chords[1], priority);
        AbsVoice[] voices = new AbsVoice[nVoices];
        for (int i = 0; i <nVoices ; i++) {
            voices[i] = extractFromChordList(nVoices, starts[i],priority, chords);
            chords = deleteAbsVoiceFromAbsChords(voices[i],chords);
        }
        return voices;
    }

    private static int[] findOptimalStarts(int[] starts, int[] chord, int[] priority) {
        int[] newStarts = new int[starts.length];
        int[] points = new int[starts.length];
        for (int i = 0; i <starts.length ; i++) {
            int point = Integer.MAX_VALUE;
            if(starts[i] == -1 ) {points[i]=point;continue;}
            search:
            for (int j = 0; j < chord.length ; j++) {
                for (int k = 0; k < priority.length; k++) {
                    if((starts[i]-chord[j])%12 == priority[k]) {
                        point = k; break search;
                    }
                }
            }

        }
        return newStarts;
    }

    private static int[] normalize(int nVoices, int[] chord) {
        if (nVoices <= chord.length) return chord;
        int diff = nVoices - chord.length;
        int[] newChord = new int[nVoices];
        for (int i = 0; i < chord.length; i++) {
            newChord[i] = chord[i];
        }
        for (int i = chord.length; i < chord.length + diff; i++) {
            newChord[i] = -1;
        }
        return newChord;
    }

    public static int maxCountInArray(int[][] arr){
        int max = 0;
        for (int i = 0; i <arr.length ; i++) {
            if(arr[i].length>max) max = arr[i].length;
        }
        return max;
    }

    public static int[][] permute(int[] array){
        List<Integer> arr = new LinkedList<Integer>();
        for (int i : array) arr.add(i);
        List<List<Integer>> result = new LinkedList<List<Integer>>();
        permute(arr,0, result);
        int[][] perms = new int[result.size()][];
        int count = 0;
        for (List perm: result
             ) {
            perms[count] = new int[perm.size()];
            for (int i = 0; i < perm.size() ; i++) {
                perms[count][i]= (Integer)perm.get(i);
            }
            count++;
        }
        return perms;
    }
    public static void  permute(List<Integer> array, int start, List<List<Integer>> result){
        for(int i = start; i < array.size(); i++){
            java.util.Collections.swap(array, i, start);
            permute(array, start+1, result);
            java.util.Collections.swap(array, start, i);
        }
        if (start == array.size() -1){
            List<Integer> clone = new LinkedList<Integer>();
            for (Integer i: array
                 ) {
                clone.add(i);
            }
           result.add(clone);
        }

    }

    public static int[][] sortPolyphonicChords(int[][] chords, int[] priority){
        if (chords.length == 1) return chords;
        int nVoices = maxCountInArray(chords);
        int[][] result = new int[chords.length][];
        int count = 0;
        for (int[] ch: chords) {
            result[count++] = normalize(nVoices,ch);
        }
        for (int i = 1; i < result.length ; i++) {
            int[][] perms = permute(result[i]);
            result[i] = findOptimalChord(result[i-1], perms, priority );
        }

        return result;
    }

    public static int[] findOptimalChord(int[] start, int[][] permutations, int[] priority) {

        int[] points = new int[permutations.length];
        for (int i = 0; i < permutations.length ; i++) { // N Permutations
            points[i]=0;
            for (int j = 0; j < start.length; j++) { // N Voices
                int noteA = start[j];
                int noteB = permutations[i][j];
                prioritySearch:
                for (int k = 0; k < priority.length; k++) {
                    if(noteA==-1 || noteB==-1){
                        points[i] += priority.length -1;
                        break prioritySearch;
                    }
                    if((noteA-noteB)<-6) { //int temp = noteA; noteA = noteB; noteB = temp;
                        noteA += 12;}
                    if((noteA-noteB)>6){ noteA -=12;}
                    if (-((noteA-noteB)%12)== priority[k]){
                        points[i] += k; // assegna un punteggio alla permutazione
                        break prioritySearch;
                    }
                }
            }

        }

        return permutations[findMinValuePosition(points)];

    }

    public static int findMinValuePosition(int[] points) {
        int min = Integer.MAX_VALUE; int position = 0;
        for (int i = 0; i < points.length; i++) {
            if (points[i]<min) { min = points[i]; position = i;}
        }
        return position;
    }

    public int[] getRealVoiceLinear(int octave){
        int[] real = new int[absNotes.length];
        octave = octave *12;
        real[0]= octave + absNotes[0];
        if(real.length==1)return real;
        for (int i = 1; i < absNotes.length ; i++) {
            if(absNotes[i]==-1){ real[i]=-1; continue;}
           real[i] = octave + absNotes[i];
            if(real[i-1]!=-1){
                if (real[i-1]-real[i]>6) real[i] +=12;
                else if (real[i-1]-real[i]<-6) real[i] -=12;
            }

        }
        System.out.println("linear:"+ Arrays.toString(real) );
        return real;
    }

    public static void main(String[] args){
        int[] priority = {0,1,-1,2,-2,3,-3,4,-4,5,-5,6,-6,7,-7,8,-8,9,-9,10,-10,11,-11};
       // int[][] absChords = { {1,4,8,11}, {0,4,9,11}, {0,2,9,10}, {0,2,8,9}};
        //int[][] absChords = { {11}, {11}, {3,10,4}, {10,5}};
       // int[][] absChords = {{4,6,7},{5,11},{8,10,4},{2,5,9,10},{0,1,7,9,10}};
       // int[][] absChords = {{0,6,7},{5,11},{8,10,4,0},{2,5,9,10},{0,1,7,9,10}};
        int[][] absChords = {{8,6,6,7},{5,11,0},{8,10,4,11},{2,5,5,9,10},{0,1,4,9,11}};
        int[][] voices = sortPolyphonicChords(absChords,priority);
        for (int i = 0; i < voices.length ; i++) {
            System.out.println(Arrays.toString(voices[i]));
        }
        AbsVoice[] absVoices = voicesFromChords(voices,priority,false);
        for (int i = 0; i <absVoices.length ; i++) {
            System.out.println(Arrays.toString(absVoices[i].absNotes));
        }
        int[][] reals = new int[absVoices.length][];
        for (int i = 0; i <reals.length ; i++) {
            reals[i] = absVoices[i].getRealVoiceLinear(4);
            System.out.println(Arrays.toString(reals[i]));
        }
      /*  AbsVoice[] voices = extractAbsVoicesFromAbsChords(maxCountInArray(absChords),
                                                            priority,absChords);
        for (int i = 0; i <voices.length ; i++) {
            System.out.println(Arrays.toString(voices[i].absNotes));
        } */
      /* int[] chord = {3,7,9,10,10};
       int [][] perms = permute(chord);
        for (int i = 0; i < perms.length; i++) {
            System.out.print(Arrays.toString(perms[i]));
        }*/

      /*  System.out.println(extractFromChordList(1, priority, absChords).toString());
        System.out.println(extractFromChordList(4, priority, absChords).toString());
        System.out.println(extractFromChordList(8, priority, absChords).toString());
        System.out.println(extractFromChordList(11, priority, absChords).toString());*/

    }


}

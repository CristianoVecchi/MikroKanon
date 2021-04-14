package com.cristianovecchi.mikrokanon.AIMUSIC;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class MelodySubSequencer {
    enum SubSeqQuality {
        ASCENDENT,
        DESCENDENT,
        EQUAL
    }
    private int[] pitches;
    public int[] velocities;
    private MelodySubSequence[] subSequences;
    public MelodySubSequencer(BebopMelody bebopMelody){
        pitches = bebopMelody.notePitches;
        subSequences = extractSubSequences();
    }

    public MelodySubSequencer(int[] pitches){
        this.pitches = pitches;
        subSequences = extractSubSequences();
    }

    private SubSeqQuality checkQuality(int pitch, int nextPitch){
        if(pitch<nextPitch) return SubSeqQuality.ASCENDENT;
        if(pitch>nextPitch) return SubSeqQuality.DESCENDENT;
        return SubSeqQuality.EQUAL;
    }

    public void printSubSequences(){
        for (MelodySubSequence msq: subSequences
             ) {
            int[] vels = new int[msq.nNotes];
            for (int i = 0; i < msq.nNotes; i++) {
               vels[i]= velocities[msq.start+i];
            }
            System.out.println(msq.toString()+" --- " +
                    Arrays.toString(getSubSequencePitches(msq))+
                    " vels: "+Arrays.toString(vels));
        }
    }



    private MelodySubSequence[] extractSubSequences() {
        if(pitches.length<2) {
            MelodySubSequence[] mss = new MelodySubSequence[1];
            mss[0] = new MelodySubSequence(0,1,SubSeqQuality.EQUAL);
            return mss;
        }
        Vector<MelodySubSequence> ssvector = new Vector<MelodySubSequence>();
        int start = 0;
       // int indexNext = (start+1) % pitches.length;
        //SubSeqQuality firstDirection = checkQuality(pitches[start], pitches[indexNext]);
        SubSeqQuality firstDirection = SubSeqQuality.ASCENDENT;
        for (int i = 0; i < pitches.length; i++) {
            int next = (i+1) % pitches.length;
            SubSeqQuality newDirection = checkQuality(pitches[i], pitches[next]);
           // System.out.println("i:"+ i+" next:"+next + " quality:" +newDirection.toString()+ " start:"+start);
            if(newDirection!=firstDirection || i==pitches.length-1){
                MelodySubSequence mss = new MelodySubSequence(start,i-start, firstDirection);
                ssvector.add(mss);
                firstDirection = newDirection;
                start = next-1;
            }
        }
       // ssvector.add(new MelodySubSequence(start+pitches.length, 1,
               // firstDirection));

        // Converting Vector to Object Array
        Object[] objArray = ssvector.toArray();

        // Convert Object[] to...
        MelodySubSequence[] mssq  = Arrays.copyOf(objArray,
                objArray.length,
                MelodySubSequence[].class);

        return mssq;
    }

    public int[] getSubSequencePitches(MelodySubSequence subSequence){
        int[] ssq = new int[subSequence.nNotes];
        for (int i = 0; i < subSequence.nNotes ; i++) {
            ssq[i] = pitches[(subSequence.start+i)];
        }
        return ssq;
    }

    public static void main(String[] args){
        int[] pitches = {2,3,8,10,7,6,4,9,12,46,67,32,64,43,0,1,9,8}; // the last value is not considered
        MelodySubSequencer mssq = new MelodySubSequencer(pitches);
        mssq.assignVelocities(0.90f,0.50f);
        mssq.printSubSequences();

    }

    public void assignVelocities(float firstNoteLimit, float lowLimit){
        float equalValue = (firstNoteLimit+lowLimit) / 2;
        int[] velocities = new int[pitches.length];
        Arrays.fill(velocities,(int)(equalValue*127));
        int nDescend, nAscend, nEqual;
        List<MelodySubSequence> descMsq = new LinkedList<MelodySubSequence>();
        List<MelodySubSequence> ascMsq = new LinkedList<MelodySubSequence>();
        List<MelodySubSequence> equalMsq = new LinkedList<MelodySubSequence>();
        nAscend = nDescend =nEqual = 0;
        for (MelodySubSequence msq: this.subSequences
             ) {
            switch (msq.quality) {
                case ASCENDENT: nAscend++;
                ascMsq.add(msq);
                break;
                case DESCENDENT: nDescend++;
                descMsq.add(msq);
                break;
                case EQUAL: nEqual++;
                equalMsq.add(msq);
                break;
            }
        }
        if(nDescend>0){
            // find the max value for the start notes
            float stepFirtNotes = (1f - firstNoteLimit) / nDescend;
            int[] maxPitches = new int[nDescend];
            Map<Integer, Integer> mapIndexPitches = new HashMap<Integer, Integer>();
            for (int i = 0; i < maxPitches.length ; i++) {
                maxPitches[i] = pitches[descMsq.get(i).start];
                mapIndexPitches.put(i,maxPitches[i]);
            }
           // System.out.println("map: "+mapIndexPitches.toString() );
            Arrays.sort(maxPitches);
           // System.out.println("maxpitches sorted: "+Arrays.toString(maxPitches) );
            float[] priorities = new float[nDescend];
            float startLimit = firstNoteLimit;
            for (int i = 0; i < nDescend; i++) {
                int pitch = maxPitches[i];
                int index = getKey(mapIndexPitches,pitch);
                mapIndexPitches.remove(index);
                priorities[index] = startLimit;
                startLimit += stepFirtNotes;
                //System.out.println("pitch:"+pitch+" index:"+index+" startLimit="+startLimit);
            }
           // System.out.println("priorities descend: "+Arrays.toString(priorities) );
            // creates descendant values
            int count = -1;
            for (MelodySubSequence msq: descMsq
                 ) {
                count++;
                float step = (priorities[count]-lowLimit)/msq.nNotes;

                float start = priorities[count];
                for (int i = msq.start; i < msq.start+msq.nNotes; i++) {
                    velocities[i]=(int)(127*start);
                    start-=step;
                }
            }
        }
        if(nAscend>0){
            // find the min value for the start notes
            float stepFirtNotes = (firstNoteLimit-lowLimit) / nAscend;
            int[] minPitches = new int[nAscend];
            Map<Integer, Integer> mapIndexPitches = new HashMap<Integer, Integer>();
            for (int i = 0; i < minPitches.length ; i++) {
                minPitches[i] = pitches[ascMsq.get(i).start];
                mapIndexPitches.put(i,minPitches[i]);
            }
            Arrays.sort(minPitches);
            float startLimit = lowLimit;
            float[] priorities = new float[nAscend];
            for (int i = 0; i < nAscend; i++) {
                int pitch = minPitches[i];
                int index = getKey(mapIndexPitches,pitch);
                mapIndexPitches.remove(index);
                priorities[index] = startLimit;
                startLimit += stepFirtNotes;
            }
            // creates ascendant values
            int count = -1;
            for (MelodySubSequence msq: ascMsq
            ) {
                count++;
                float step = (firstNoteLimit-priorities[count])/msq.nNotes;

                float start = priorities[count];
                for (int i = msq.start; i < msq.start+msq.nNotes; i++) {
                    velocities[i]=(int)(127*start);
                    start+=step;
                }
            }
        }
        if(nEqual >0){

            for (MelodySubSequence msq: equalMsq
            ) {

                for (int i = msq.start; i < msq.start+msq.nNotes; i++) {
                    velocities[i]=(int)(127*equalValue);

                }
            }
        }

        this.velocities = velocities;
        //printSubSequences();
    }
    public static <K, V> K getKey(Map<K, V> map, V value) {
        for (K key : map.keySet()) {
            if (value.equals(map.get(key))) {
                return key;
            }
        }
        return null;
    }


    public class MelodySubSequence{
        int start;
        int nNotes;
        SubSeqQuality quality;

        public MelodySubSequence(int start, int nNotes, SubSeqQuality quality) {
            this.start = start;
            this.nNotes = nNotes;
            this.quality = quality;
        }

        public String toString() {
            return "start: "+start+"  length: "+nNotes+ "  quality: "+ quality.toString();
        }
    }
}

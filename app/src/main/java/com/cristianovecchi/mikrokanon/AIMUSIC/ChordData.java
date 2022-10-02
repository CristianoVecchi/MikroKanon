package com.cristianovecchi.mikrokanon.AIMUSIC;


import com.cristianovecchi.mikrokanon.locale.Lang;

public class ChordData implements Comparable<ChordData>{
    public String noteList;
    public boolean[] selNotes;
    public int numNotes;

    public ChordData(boolean[] selNotes) {
        this.selNotes = selNotes;
        numNotes = HarmonyEye.countNotes(selNotes);
        String[] noteNames = Lang.Companion.italian().getNoteNames().toArray(new String[0]);
        noteList = HarmonyEye.getOrderedNotesText(selNotes, noteNames);
    }

    @Override
    public int compareTo(ChordData cd) {
        if (numNotes < cd.numNotes)
            return -1;
        else if (numNotes == cd.numNotes)
            return 0;
        else
            return 1;


    }
}
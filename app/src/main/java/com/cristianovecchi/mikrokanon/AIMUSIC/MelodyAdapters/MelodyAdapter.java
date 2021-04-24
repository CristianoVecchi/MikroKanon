package com.cristianovecchi.mikrokanon.AIMUSIC.MelodyAdapters;

import com.cristianovecchi.mikrokanon.AIMUSIC.BebopMelody;
import com.cristianovecchi.mikrokanon.AIMUSIC.ChordSequence;
import com.cristianovecchi.mikrokanon.AIMUSIC.Insieme;
import com.cristianovecchi.mikrokanon.G;
import com.cristianovecchi.mikrokanon.AIMUSIC.BebopMelody;
import com.cristianovecchi.mikrokanon.AIMUSIC.ChordSequence;

public class MelodyAdapter {
    public int getRealNote(int desiredNote, int noteSelected, BebopMelody bebopMelody,
                           ChordSequence chordSequence, int soloRangeLowerNote, int soloRangeUpperNote){
        int desiredNoteByte = 1 << (desiredNote % 12);
        if  (noteSelected >= bebopMelody.notePitches.length){
            noteSelected = bebopMelody.notePitches.length-1;
        }
        int chord = bebopMelody.inWhichChord[noteSelected][0];
        int beatNotesByte  = chordSequence.getBeatBytes()[chord];
        int passageNotesByte  = chordSequence.getPassageBytes()[chord];
        if (bebopMelody.noteQualities[noteSelected]==1) {
            beatNotesByte = passageNotesByte;
        }


        return realNote(desiredNoteByte,chord,beatNotesByte,desiredNote, soloRangeLowerNote, soloRangeUpperNote, chordSequence);
    }

    public void assignPassageNote(int selNote, int passageNote, BebopMelody bm , ChordSequence chs, int soloRangeLowerNote, int soloRangeUpperNote){

    }

    protected int realNote(int desiredNoteByte, int chord, int beatNotesByte, int desiredNote,
                           int soloRangeLowerNote, int soloRangeUpperNote, ChordSequence chordSequence) {
        int realNoteDistance = Insieme.searchClosestBit(desiredNoteByte,
                (Insieme.rotate(chordSequence.getChords()[chord].getRoot(), beatNotesByte)));
        // System.out.print("desired note= " + desiredNote + " realNoteDistance = "+ realNoteDistance + "desiredOctave="+(desiredOctave*12));
        int realNote = desiredNote + realNoteDistance;

        if (desiredNote - realNote > 6) realNote = realNote + 12;
        if (desiredNote - realNote < -6) realNote = realNote - 12;


        return rangeCheck(realNote, soloRangeLowerNote, soloRangeUpperNote);
    }

    protected int rangeCheck(int pitch , int soloRangeLowerNote, int soloRangeUpperNote){

        if (pitch < soloRangeLowerNote) pitch += 12;
        if (pitch > soloRangeUpperNote) pitch -= 12;
        return pitch;
    }
}

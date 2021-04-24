package com.cristianovecchi.mikrokanon.AIMUSIC.MelodyAdapters;

import com.cristianovecchi.mikrokanon
        .AIMUSIC.BebopMelody;
import com.cristianovecchi.mikrokanon
        .AIMUSIC.Chord;
import com.cristianovecchi.mikrokanon
        .AIMUSIC.ChordSequence;
import com.cristianovecchi.mikrokanon
        .AIMUSIC.Insieme;
import com.cristianovecchi.mikrokanon
        .G;

public class PassageMelodyAdapter extends MelodyAdapter {
    public int getRealNote(int desiredNote, int noteSelected, BebopMelody bebopMelody, ChordSequence chordSequence, int soloRangeLowerNote, int soloRangeUpperNote) {
        BebopMelody bm= bebopMelody;
        if  (noteSelected>= bm.notePitches.length){noteSelected= bm.notePitches.length-1; }

        int selNote = noteSelected;

        if (bm.noteQualities[selNote]==BebopMelody.PASSAGE_NOTE) { // if passage note, change to the next beat note
            selNote = bm.getNextNoteWithQuality(selNote,BebopMelody.BEAT_NOTE);
            noteSelected=selNote;
        }
        ChordSequence chs = chordSequence;
        int desiredNoteByte = 1 << (desiredNote % 12);

        int chord = bm.inWhichChord[selNote][0];
        int beatNotesByte  = chs.getBeatBytes()[chord];


        int nextNote = bm.getNextNoteIndex(selNote);
        int prevNote = bm.getPreviousNoteIndex(selNote);
        int realNote = realNote(desiredNoteByte,chord,beatNotesByte,desiredNote,soloRangeLowerNote,soloRangeUpperNote,chordSequence); //super method
        bm.notePitches[selNote]=realNote;
        int previousBeatNote = bm.getPreviousNoteWithQuality(selNote,BebopMelody.BEAT_NOTE);


        if(bm.noteQualities[prevNote]==BebopMelody.PASSAGE_NOTE){
            assignPassageNote(previousBeatNote,prevNote,bm,chs,soloRangeLowerNote,soloRangeUpperNote);//prevNote is the passage note
        }
        if(bm.noteQualities[nextNote]==BebopMelody.PASSAGE_NOTE){
            assignPassageNote(selNote,nextNote,bm,chs,soloRangeLowerNote,soloRangeUpperNote);
        }




        return realNote;
    }
    @Override
    public void assignPassageNote(int selNote, int passageNote, BebopMelody bm ,ChordSequence chs,int soloRangeLowerNote, int soloRangeUpperNote){
        int nextBeatIndex = bm.getNextNoteWithQuality(selNote,BebopMelody.BEAT_NOTE);
        int chord = bm.inWhichChord[passageNote][0];
        int beatNotesByte  = chs.getBeatBytes()[chord];
        int passageNotesByte  = chs.getPassageBytes()[chord];
        Chord[] chords = chs.getChords();
        passageNotesByte = Insieme.rotate(chords[chord].getRoot(), passageNotesByte);
        beatNotesByte = Insieme.rotate(chords[chord].getRoot(), beatNotesByte);
        int selNotePitch = bm.notePitches[selNote];
        int nextNotePitch = bm.notePitches[nextBeatIndex];
        if (selNotePitch==nextNotePitch) {
            bm.notePitches[passageNote] = rangeCheck(selNotePitch-1,soloRangeLowerNote,soloRangeUpperNote);//lower volta note
        }
        if(selNotePitch<nextNotePitch) { //ascendent movement

            int[] beatRes = Insieme.findPassagenotes(selNotePitch, nextNotePitch, beatNotesByte);
            if (beatRes.length != 0) {
                bm.notePitches[passageNote] = rangeCheck(beatRes[beatRes.length - 1],soloRangeLowerNote,soloRangeUpperNote);

            } else {
                int[] passRes = Insieme.findPassagenotes(selNotePitch, nextNotePitch, passageNotesByte);
                if (passRes.length != 0) {
                    bm.notePitches[passageNote] = rangeCheck(passRes[passRes.length - 1],soloRangeLowerNote,soloRangeUpperNote);

                } else {
                    bm.notePitches[passageNote] = rangeCheck(Insieme.findUpperChordNote(nextNotePitch, beatNotesByte),soloRangeLowerNote,soloRangeUpperNote);
                }
            }
        }
        if (selNotePitch > nextNotePitch) { //discendent movement

                int[] beatRes = Insieme.findPassagenotes(nextNotePitch, selNotePitch, beatNotesByte);
                if (beatRes.length != 0) {
                    bm.notePitches[passageNote] = rangeCheck(beatRes[0],soloRangeLowerNote,soloRangeUpperNote);

                } else {
                    int[] passRes = Insieme.findPassagenotes(nextNotePitch, selNotePitch, passageNotesByte);
                    if (passRes.length != 0) {
                        bm.notePitches[passageNote] = rangeCheck(passRes[0],soloRangeLowerNote,soloRangeUpperNote);
                    } else {
                        bm.notePitches[passageNote] = rangeCheck(nextNotePitch - 1,soloRangeLowerNote,soloRangeUpperNote);
                    }
                }
            }

    }
}

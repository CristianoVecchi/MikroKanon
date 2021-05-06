package com.cristianovecchi.mikrokanon.midi

import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.AIMUSIC.CharlieParkerBand.BebopBand
import com.cristianovecchi.mikrokanon.AIMUSIC.CharlieParkerBand.CharlieParker
import com.cristianovecchi.mikrokanon.AIMUSIC.CharlieParkerBand.CharlieParkerBand
import com.cristianovecchi.mikrokanon.AIMUSIC.DEF.MIDDLE_C

import com.leff.midi.MidiFile
import com.leff.midi.MidiTrack
import com.leff.midi.event.MidiEvent
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn
import com.leff.midi.event.ProgramChange
import com.leff.midi.event.meta.Tempo
import java.io.IOException
import com.leff.midi.event.meta.TimeSignature
import java.io.File


object Player {
    const val YOUNGCHARLIEPARKER = 1
    const val THEBEBOPBOYS = -1

    //public static MediaPlayer mediaPlayer2;
    private var genius: CharlieParkerBand? = null
    fun playNoteInMiddleOctave(
        player: MediaPlayer,
        note: Int,
        velocity: Int,
        instr: Int,
        looping: Boolean
    ) {
        playSingleNote(player, MIDDLE_C + note, velocity, instr, looping)
    }

    fun playSingleNote(
        player: MediaPlayer,
        pitch: Int,
        velocity: Int,
        instr: Int,
        looping: Boolean
    ) {
        if (player.isPlaying()) {
            player.stop()
        }
        player.reset()
        val tempoTrack = MidiTrack()
        val noteTrack = MidiTrack()

        // 2. Add events to the tracks
        // 2a. Track 0 is typically the tempo map
        val t = Tempo()
        t.bpm = 90f
        tempoTrack.insertEvent(t)

        // 2b. Track 1 will have some notes in it
        val time = 0
        val channel = 0
        val pc: MidiEvent = ProgramChange(0, channel, instr) // cambia strumento
        noteTrack.insertEvent(pc)
        val on = NoteOn(time.toLong(), channel, pitch, velocity)
        val off = NoteOff(60, channel, pitch, 0)
        noteTrack.insertEvent(on)
        noteTrack.insertEvent(off)

        // It's best not to manually insert EndOfTrack events; MidiTrack will
        // call closeTrack() on itself before writing itself to a file

        // 3. Create a MidiFile with the tracks we created
        val tracks: java.util.ArrayList<MidiTrack> = java.util.ArrayList<MidiTrack>()
        tracks.add(tempoTrack)
        tracks.add(noteTrack)
        val midi = MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks)
        saveAndPlayMidiFile(player, midi, looping, true, null)
    }

    fun playScheme(mediaPlayer: MediaPlayer, looping: Boolean, bebopBand: BebopBand, charlieParker: CharlieParker,
                   bebopMelody: BebopMelody, chordSequence: ChordSequence, bpm: Float, soloInstrument: Int, shuffle: Float) {
        if (genius == null) genius = CharlieParkerBand()
        val midi: MidiFile = genius!!.playScheme(bebopBand,charlieParker, bebopMelody, chordSequence,bpm,soloInstrument, shuffle) ?: return
        //System.out.println("Midifile creato");
        saveAndPlayMidiFile(mediaPlayer, midi, looping, true, null)


    }

    fun playCounterpoint(mediaPlayer: MediaPlayer, looping: Boolean,
                         counterpoint: Counterpoint, bpm: Float, shuffle: Float,
                         durations: List<Int>, ensembleType: EnsembleType,
                            play: Boolean, midiFile:File) : String {
        var error = ""
        val counterpointTracks = CounterpointInterpreter.doTheMagic(counterpoint,durations,ensembleType,true)
        if (counterpointTracks.isEmpty()) return "No Tracks in Counterpoint!!!"
        val tempoTrack = MidiTrack()
        // from TimeSignature.class
//        public static final int DEFAULT_METER = 24;
//        public static final int DEFAULT_DIVISION = 8;
        val ts = TimeSignature()
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION)
        val t = Tempo()
        t.bpm = bpm
        tempoTrack.insertEvent(ts);
        tempoTrack.insertEvent(t)
        val tracks: java.util.ArrayList<MidiTrack> = java.util.ArrayList<MidiTrack>()
        tracks.add(tempoTrack)
        tracks.addAll(counterpointTracks)
        val midi = MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks)
        return saveAndPlayMidiFile(mediaPlayer, midi, looping, play, midiFile)
    }

    fun saveAndPlayMidiFile(mediaPlayer: MediaPlayer, midi: MidiFile, looping: Boolean, play: Boolean, midiFile: File?) : String {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop()
        }
        mediaPlayer.reset()
        // 4. Write the MIDI data to a file
        //File output = new File(getExternalFilesDir(null), "example.mid");
        //File output = new File("/sdcard/example.mid");

        // ------------- WARNING!!!! -------------
        //DOESN'T WORK FOR LATEST ANDROID VERSIONS
        output = midiFile
        var error = ""
        //createDialog(output.toString());
        //mediaPlayer2 = new MediaPlayer();
        try {
            midi.writeToFile(output)
        } catch (e: IOException) {
            Log.e(Player::class.java.toString(), e.message, e)
            error = e.message.toString()
            return error
        }
        if(play){
            try {
                mediaPlayer.setDataSource(output!!.getAbsolutePath())
                mediaPlayer.prepare()
                //player.create(Harmony12.this, output.);
            } catch (e: java.lang.Exception) {
                Log.e(Player::class.java.toString(), e.message, e)
                error = e.message.toString()
                return error
            }
            //System.out.println("Midifile salvato");
            mediaPlayer.start()
            //mediaPlayer.setLooping(looping);
        }
        return error
    }

    private var output: java.io.File? = null
    var output1: java.io.File? = null
    var output2: java.io.File? = null
    fun createTwinMidiFiles(bebopBand: BebopBand, charlieParker: CharlieParker,
                            bebopMelody: BebopMelody, chordSequence: ChordSequence, bpm: Float, soloInstrument: Int, shuffle: Float) {
        if (genius == null) genius = CharlieParkerBand()
        val midi: MidiFile = genius!!.playScheme(bebopBand,charlieParker, bebopMelody, chordSequence,bpm,soloInstrument, shuffle)
        output1 = java.io.File(Environment.getExternalStorageDirectory(), "Genius1.mid")
        output2 = java.io.File(Environment.getExternalStorageDirectory(), "Genius2.mid")
        try {
            midi.writeToFile(output1)
            midi.writeToFile(output2)
        } catch (e: IOException) {
            Log.e(Player::class.java.toString(), e.message, e)
        }
    }//player.create(Harmony12.this, output.);

    // CLONE the mediaplayer
    val mediaPlayer2: MediaPlayer
        get() { // CLONE the mediaplayer
            val mediaPlayer2 = MediaPlayer()
            try {
                mediaPlayer2.setDataSource(output!!.getAbsolutePath())
                mediaPlayer2.prepare()
                //player.create(Harmony12.this, output.);
            } catch (e: java.lang.Exception) {
                Log.e(Player::class.java.toString(), e.message, e)
            }
            return mediaPlayer2
        }
}
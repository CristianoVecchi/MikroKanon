package com.cristianovecchi.mikrokanon.midi

import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import com.cristianovecchi.mikrokanon.*
import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.AIMUSIC.CharlieParkerBand.BebopBand
import com.cristianovecchi.mikrokanon.AIMUSIC.CharlieParkerBand.CharlieParker
import com.cristianovecchi.mikrokanon.AIMUSIC.CharlieParkerBand.CharlieParkerBand
import com.cristianovecchi.mikrokanon.AIMUSIC.DEF.MIDDLE_C

import com.leff.midi.MidiFile
import com.leff.midi.MidiTrack
import com.leff.midi.event.*
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

    fun playCounterpoint(
        mediaPlayer: MediaPlayer, looping: Boolean,
        counterpoints: List<Counterpoint?>, dynamics: List<Float>, bpms: List<Float>, shuffle: Float,
        rhythm: RhythmPatterns, ensembleTypes: List<EnsembleType>,
        play: Boolean, midiFile: File, rhythmShuffle: Boolean = false, partsShuffle: Boolean = false,
        rowForms: List<Pair<Int,Int>> = listOf(Pair(0,1)), ritornello: Int = 0, transpose: List<Int> = listOf(0),
        doublingFlags: Int = 0, nuances: Int = 0,
        rangeType: Int = 0, melodyType: Int = 0, glissandoFlags: Int = 0, audio8DFlags: Int = 0, vibrato: Int = 0
    ) : String {
        var error = ""
        val durations = rhythm.values
        val actualDurations = if (rhythmShuffle) listOf(*durations.toTypedArray(),*durations.toTypedArray(),*durations.toTypedArray()).shuffled() else durations
        val nParts = counterpoints.maxByOrNull { it?.parts?.size ?: 0}?.parts?.size ?: 0
        val ensembleParts: List<EnsemblePart> = if(ensembleTypes.size == 1) Ensembles.getEnsemble(nParts, ensembleTypes[0])
                                                else Ensembles.getEnsembleMix(nParts, ensembleTypes)
        //ensembleParts.display()
        val actualEnsembleParts = if (partsShuffle) ensembleParts.shuffled() else ensembleParts
        val firstCounterpoint = counterpoints.firstOrNull()
            ?: return "NOT EVEN ONE COUNTERPOINT TO PLAY!!!"
        val nNotesToSkip = rhythm.nNotesLeftInThePattern(firstCounterpoint.nNotes())
        //var actualCounterpoint = if (rowForms == listOf(1)) counterpoint else Counterpoint.explodeRowForms(counterpoint, rowForms, nNotesToSkip)
        var actualCounterpoint = if (rowForms == listOf(Pair(0,1)) ) firstCounterpoint
            else Counterpoint.explodeRowFormsAddingCps(counterpoints, rowForms, nNotesToSkip)
        actualCounterpoint = if(ritornello > 0)  actualCounterpoint.ritornello(ritornello, transpose)
                            else actualCounterpoint.transpose(transpose[0])
        val glissando: List<Int> = if(glissandoFlags == 0) listOf() else convertGlissandoFlags(glissandoFlags)
        val audio8D: List<Int> = if(audio8DFlags == 0) listOf() else convertFlagsToInts(audio8DFlags).toList()
        val vibratoExtensions = listOf(0, 360, 240 ,160, 120, 80 ,60, 30, 15)

        if (actualCounterpoint.isEmpty()) return "Counterpoint to play is empty!!!"
        if (actualCounterpoint.parts[0].absPitches.size == 0) return "Counterpoint parts are empty!!!"
        val counterpointTracks = CounterpointInterpreter.doTheMagic(actualCounterpoint, actualDurations, actualEnsembleParts,
                                                                    nuances, doublingFlags, rangeType, melodyType,
                                                                    glissando, audio8D, vibratoExtensions[vibrato])
        if (counterpointTracks.isEmpty()) return "No Tracks in Counterpoint!!!"

        val totalLength = counterpointTracks[0].lengthInTicks
        val tempoTrack = MidiTrack()
        // from TimeSignature.class
//        public static final int DEFAULT_METER = 24;
//        public static final int DEFAULT_DIVISION = 8;
        val ts = TimeSignature()
        ts.setTimeSignature(rhythm.metro.first, rhythm.metro.second,
                            TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION)
        tempoTrack.insertEvent(ts);

        //val t = Tempo()
        //t.bpm = bpm
        //val t2 = Tempo(totalLength/2,0L, 500000)
        //t2.bpm = bpm * 3
        //tempoTrack.insertEvent(t)
        //tempoTrack.insertEvent(t2)
        //val bpmAlterations = bpm.projectTo(bpm*2, 0.5f).projectTo(bpm, 0.5f).also{println(it)}
        // INSERT BPM ALTERATIONS
        val bpmAlterationsAndDeltas = alterateBpmWithDistribution(bpms, 0.5f, totalLength)
        var tempoTick = 0L
        val bpmAlterations = bpmAlterationsAndDeltas.first//.also { println("${it.size} + $it") }
        val bpmDeltas = bpmAlterationsAndDeltas.second//.also { println("${it.size} + $it") }
        (0 until bpmAlterations.size -1).forEach { index -> // doesn't take the last bpm
            val newTempo = Tempo(tempoTick, 0L, 500000)
            newTempo.bpm = bpmAlterations[index]
            tempoTrack.insertEvent(newTempo)
            tempoTick += bpmDeltas[index]
        }

        //val dynamics = listOf(1f,0f,1f)
        val dynamicAlterationsAndDeltas = alterateBpmWithDistribution(dynamics,0.01f, totalLength)
        tempoTick = 0L
        val dynamicAlterations = dynamicAlterationsAndDeltas.first//.also { println("${it.size} + $it") }
        val dynamicDeltas = dynamicAlterationsAndDeltas.second//.also { println("${it.size} + $it") }
        (0 until dynamicAlterations.size -1).forEach { index -> // doesn't take the last dynamic
            // 0x7F = universal immediatly message, 0x7F = all devices, 0x04 = device control message, 0x01 = master volume
            // bytes = first the low 7 bits, second the high 7 bits - volume is from 0x0000 to 0x3FFF
            val volumeBytes: Pair<Int, Int> = dynamicAlterations[index].convertDynamicToBytes()
            val data: ByteArray = listOf(0x7F,0x7F, 0x04, 0x01,volumeBytes.first,volumeBytes.second, 0xF7)
                                .foldIndexed(ByteArray(7)){i, a, v -> a.apply{ set(i, v.toByte())}}
            val newGeneralVolume = SystemExclusiveEvent(0xF0, tempoTick, data)

            tempoTrack.insertEvent(newGeneralVolume)
            tempoTick += dynamicDeltas[index]
        }

        val tracks: java.util.ArrayList<MidiTrack> = java.util.ArrayList<MidiTrack>()
        tracks.add(tempoTrack)
        tracks.addAll(counterpointTracks)
        val midi = MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks)
        return saveAndPlayMidiFile(mediaPlayer, midi, looping, play, midiFile)
    }

    fun saveAndPlayMidiFile(mediaPlayer: MediaPlayer, midi: MidiFile, looping: Boolean, play: Boolean, midiFile: File?) : String {
        if (mediaPlayer.isPlaying) {
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
            //mediaPlayer.setAuxEffectSendLevel()
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




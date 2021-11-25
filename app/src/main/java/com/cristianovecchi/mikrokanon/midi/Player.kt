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
        rangeTypes: List<Pair<Int,Int>> = listOf(Pair(2,0)),
        legatoTypes: List<Pair<Int,Int>> = listOf(Pair(2,0)),
        melodyTypes: List<Int> = listOf(0),
        glissandoFlags: Int = 0, audio8DFlags: Int = 0, vibrato: Int = 0
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
        var actualCounterpoint = if (rowForms == listOf(Pair(1,1)) ) firstCounterpoint
            else Counterpoint.explodeRowFormsAddingCps(counterpoints, rowForms, nNotesToSkip)
        actualCounterpoint = if(ritornello > 0)  actualCounterpoint.ritornello(ritornello, transpose)
                            else actualCounterpoint.transpose(transpose[0])
        val glissando: List<Int> = if(glissandoFlags == 0) listOf() else convertGlissandoFlags(glissandoFlags)
        val audio8D: List<Int> = if(audio8DFlags == 0) listOf() else convertFlagsToInts(audio8DFlags).toList()
        val vibratoExtensions = listOf(0, 360, 240 ,160, 120, 80 ,60, 30, 15)

        if (actualCounterpoint.isEmpty()) return "Counterpoint to play is empty!!!"
        if (actualCounterpoint.parts[0].absPitches.size == 0) return "Counterpoint parts are empty!!!"
        val counterpointTrackData: List<TrackData> = CounterpointInterpreter.doTheMagic(actualCounterpoint, actualDurations, actualEnsembleParts,
                                                                    nuances, doublingFlags, rangeTypes, legatoTypes, melodyTypes,
                                                                    glissando, audio8D, vibratoExtensions[vibrato])
        if (counterpointTrackData.isEmpty()) return "No Tracks in Counterpoint!!!"

        val totalLength = counterpointTrackData.maxOf{ it.ticks.last() + it.durations.last()}.toLong()//.also{println("Total length: $it")} // Empty tracks have 0 length
        //LEGATO AND RIBATTUTO DURATION ZONE
        // none, staccatissimo, staccato, portato, articolato, legato, legatissimo
        val artMap = mapOf(0 to 1.0f, 1 to 0.125f, 2 to 0.25f, 3 to 0.75f, 4 to 1.0f, 5 to 1.125f, 6 to 1.25f )
        val legatos = legatoTypes.map{ artMap[it.first]!!}
        val legatoAlterationsAndDeltas = alterateBpmWithDistribution(legatos,0.001f, totalLength)
        val legatoAlterations = legatoAlterationsAndDeltas.first//.also { println("${it.size} + $it") }
        val legatoDeltas = legatoAlterationsAndDeltas.second//.also { println("${it.size} + $it") }

        // TRANSFORM DATATRACKS IN MIDITRACKS
        val counterpointTracks = counterpointTrackData.map{ convertToMidiTrack(it, counterpointTrackData.size) }

       //val totalLength = counterpointTracks.maxOf{ it.lengthInTicks}//.also{println("Total length: $it")} // Empty tracks have 0 length
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
    private fun insertNoteWithGlissando(
        mt: MidiTrack, start: Long, duration: Long, channel: Int,
        pitch: Int, velOn: Int, velOff: Int, gliss: Int
    ) {
        if(gliss != 0) {
            // val portamentoOn = Controller(start, channel,65,127)
            if(gliss == 1) {
                val durationQuarter = duration / 4
                val pitchBendOn1= PitchBend(start + durationQuarter, channel,0,0)
                val pitchBendOn2= PitchBend(start + durationQuarter * 2, channel,0,0)
                val pitchBendOn3= PitchBend(start + durationQuarter * 3, channel,0,0)
                val pitchBendOn4= PitchBend(start + duration-1, channel,0,0)
                val pitchBendOff= PitchBend(start , channel,0,0)
                // 0 4096 8192 12288 (14335) 16383
                pitchBendOn1.bendAmount = 1024
                pitchBendOn2.bendAmount = 2048
                pitchBendOn3.bendAmount = 3072
                pitchBendOn4.bendAmount = 4096
                pitchBendOff.bendAmount = 0
                val on = NoteOn(start, channel, pitch, velOn)
                val off = NoteOff(start + duration, channel, pitch, velOff)
                mt.insertEvent(pitchBendOff)
                mt.insertEvent(on)
                mt.insertEvent(pitchBendOn1)
                mt.insertEvent(pitchBendOn2)
                mt.insertEvent(pitchBendOn3)
                mt.insertEvent(pitchBendOn4)
                mt.insertEvent(off)
            } else if(gliss == 2) {
                val durationOctave = duration / 8
                val pitchBendOn1= PitchBend(start + durationOctave, channel,0,0)
                val pitchBendOn2= PitchBend(start + durationOctave * 2, channel,0,0)
                val pitchBendOn3= PitchBend(start + durationOctave * 3, channel,0,0)
                val pitchBendOn4= PitchBend(start + durationOctave *4, channel,0,0)
                val pitchBendOn5= PitchBend(start + durationOctave * 5, channel,0,0)
                val pitchBendOn6= PitchBend(start + durationOctave * 6, channel,0,0)
                val pitchBendOn7= PitchBend(start + durationOctave * 7, channel,0,0)
                val pitchBendOn8= PitchBend(start + duration - 1, channel,0,0)
                val pitchBendOff= PitchBend(start , channel,0,0)
                // 0 1024 2048 3072 4096(ht) 5120 6144 7168 8192 9216 10240 11264 12288 13312 14335
                pitchBendOn1.bendAmount = 1024
                pitchBendOn2.bendAmount = 2048
                pitchBendOn3.bendAmount = 3072
                pitchBendOn4.bendAmount = 4096
                pitchBendOn5.bendAmount = 5120
                pitchBendOn6.bendAmount = 6144
                pitchBendOn7.bendAmount = 7168
                pitchBendOn8.bendAmount = 8192
                pitchBendOff.bendAmount = 0
                val on = NoteOn(start, channel, pitch, velOn)
                val off = NoteOff(start + duration, channel, pitch, velOff)
                mt.insertEvent(pitchBendOff)
                mt.insertEvent(on)
                mt.insertEvent(pitchBendOn1)
                mt.insertEvent(pitchBendOn2)
                mt.insertEvent(pitchBendOn3)
                mt.insertEvent(pitchBendOn4)
                mt.insertEvent(pitchBendOn5)
                mt.insertEvent(pitchBendOn6)
                mt.insertEvent(pitchBendOn7)
                mt.insertEvent(off)
            } else if(gliss == 3) {
                val duration12 = duration / 12
                val pitchBendOn1= PitchBend(start + duration12, channel,0,0)
                val pitchBendOn2= PitchBend(start + duration12 * 2, channel,0,0)
                val pitchBendOn3= PitchBend(start + duration12 * 3, channel,0,0)
                val pitchBendOn4= PitchBend(start + duration12 *4, channel,0,0)
                val pitchBendOn5= PitchBend(start + duration12 * 5, channel,0,0)
                val pitchBendOn6= PitchBend(start + duration12 * 6, channel,0,0)
                val pitchBendOn7= PitchBend(start + duration12 * 7, channel,0,0)
                val pitchBendOn8= PitchBend(start + duration12 * 8, channel,0,0)
                val pitchBendOn9= PitchBend(start + duration12 * 9, channel,0,0)
                val pitchBendOn10= PitchBend(start + duration12 * 10, channel,0,0)
                val pitchBendOn11= PitchBend(start + duration12 * 11, channel,0,0)
                val pitchBendOn12= PitchBend(start + duration - 1, channel,0,0)
                val pitchBendOff= PitchBend(start , channel,0,0)
                // 0 1024 2048 3072 4096(ht) 5120 6144 7168 8192 9216 10240 11264 12288 13312 14335
                pitchBendOn1.bendAmount = 1024
                pitchBendOn2.bendAmount = 2048
                pitchBendOn3.bendAmount = 3072
                pitchBendOn4.bendAmount = 4096
                pitchBendOn5.bendAmount = 5120
                pitchBendOn6.bendAmount = 6144
                pitchBendOn7.bendAmount = 7168
                pitchBendOn8.bendAmount = 8192
                pitchBendOn9.bendAmount = 9216
                pitchBendOn10.bendAmount = 10240
                pitchBendOn11.bendAmount = 11264
                pitchBendOn12.bendAmount = 12288
                pitchBendOff.bendAmount = 0
                val on = NoteOn(start, channel, pitch, velOn)
                val off = NoteOff(start + duration, channel, pitch, velOff)
                mt.insertEvent(pitchBendOff)
                mt.insertEvent(on)
                mt.insertEvent(pitchBendOn1)
                mt.insertEvent(pitchBendOn2)
                mt.insertEvent(pitchBendOn3)
                mt.insertEvent(pitchBendOn4)
                mt.insertEvent(pitchBendOn5)
                mt.insertEvent(pitchBendOn6)
                mt.insertEvent(pitchBendOn7)
                mt.insertEvent(pitchBendOn8)
                mt.insertEvent(pitchBendOn9)
                mt.insertEvent(pitchBendOn10)
                mt.insertEvent(pitchBendOn11)
                mt.insertEvent(pitchBendOn12)
                mt.insertEvent(off)
            }

            else if (gliss == -1){
                val durationQuarter = duration / 4
                val pitchBendOn1= PitchBend(start , channel,0,0)
                val pitchBendOn2= PitchBend(start + durationQuarter , channel,0,0)
                val pitchBendOn3= PitchBend(start + durationQuarter * 2, channel,0,0)
                val pitchBendOn4= PitchBend(start + durationQuarter * 3, channel,0,0)
                val pitchBendOff= PitchBend(start+ duration -1 , channel,0,0)
                // 0 4096 8192 12288 (14335) 16383
                pitchBendOn4.bendAmount = 1024
                pitchBendOn3.bendAmount = 2048
                pitchBendOn2.bendAmount = 3072
                pitchBendOn1.bendAmount = 4096
                pitchBendOff.bendAmount = 0
                val on = NoteOn(start, channel, pitch-1, velOn)
                val off = NoteOff(start + duration, channel, pitch-1, velOff)

                mt.insertEvent(pitchBendOn1)
                mt.insertEvent(on)
                mt.insertEvent(pitchBendOn2)
                mt.insertEvent(pitchBendOn3)
                mt.insertEvent(pitchBendOn4)
                mt.insertEvent(pitchBendOff)
                mt.insertEvent(off)
            }else if(gliss == -2) {
                val durationOctave = duration / 8
                val pitchBendOn1= PitchBend(start , channel,0,0)
                val pitchBendOn2= PitchBend(start + durationOctave , channel,0,0)
                val pitchBendOn3= PitchBend(start + durationOctave * 2, channel,0,0)
                val pitchBendOn4= PitchBend(start + durationOctave * 3, channel,0,0)
                val pitchBendOn5= PitchBend(start + durationOctave * 4, channel,0,0)
                val pitchBendOn6= PitchBend(start + durationOctave * 5, channel,0,0)
                val pitchBendOn7= PitchBend(start + durationOctave * 6, channel,0,0)
                val pitchBendOn8= PitchBend(start + durationOctave * 7, channel,0,0)
                val pitchBendOff= PitchBend(start + duration -1 , channel,0,0)
                // 0 1024 2048 3072 4096(ht) 5120 6144 7168 8192 9216 10240 11264 12288 13312 14335
                pitchBendOn8.bendAmount = 1024
                pitchBendOn7.bendAmount = 2048
                pitchBendOn6.bendAmount = 3072
                pitchBendOn5.bendAmount = 4096
                pitchBendOn4.bendAmount = 5120
                pitchBendOn3.bendAmount = 6144
                pitchBendOn2.bendAmount = 7168
                pitchBendOn1.bendAmount = 8192
                pitchBendOff.bendAmount = 0
                val on = NoteOn(start, channel, pitch-2, velOn)
                val off = NoteOff(start + duration, channel, pitch-2, velOff)


                mt.insertEvent(pitchBendOn1)
                mt.insertEvent(on)
                mt.insertEvent(pitchBendOn2)
                mt.insertEvent(pitchBendOn3)
                mt.insertEvent(pitchBendOn4)
                mt.insertEvent(pitchBendOn5)
                mt.insertEvent(pitchBendOn6)
                mt.insertEvent(pitchBendOn7)
                mt.insertEvent(pitchBendOn8)
                mt.insertEvent(pitchBendOff)
                mt.insertEvent(off)
            } else if(gliss == -3) {
                val duration12 = duration / 12
                val pitchBendOn1 = PitchBend(start, channel, 0, 0)
                val pitchBendOn2 = PitchBend(start + duration12, channel, 0, 0)
                val pitchBendOn3 = PitchBend(start + duration12 * 2, channel, 0, 0)
                val pitchBendOn4 = PitchBend(start + duration12 * 3, channel, 0, 0)
                val pitchBendOn5 = PitchBend(start + duration12 * 4, channel, 0, 0)
                val pitchBendOn6 = PitchBend(start + duration12 * 5, channel, 0, 0)
                val pitchBendOn7 = PitchBend(start + duration12 * 6, channel, 0, 0)
                val pitchBendOn8 = PitchBend(start + duration12 * 7, channel, 0, 0)
                val pitchBendOn9 = PitchBend(start + duration12 * 8, channel, 0, 0)
                val pitchBendOn10 = PitchBend(start + duration12 * 9, channel, 0, 0)
                val pitchBendOn11 = PitchBend(start + duration12 * 10, channel, 0, 0)
                val pitchBendOn12 = PitchBend(start + duration12 * 11, channel, 0, 0)
                val pitchBendOff = PitchBend(start + duration - 1, channel, 0, 0)
                // 0 1024 2048 3072 4096(ht) 5120 6144 7168 8192 9216 10240 11264 12288 13312 14335
                pitchBendOn12.bendAmount = 1024
                pitchBendOn11.bendAmount = 2048
                pitchBendOn10.bendAmount = 3072
                pitchBendOn9.bendAmount = 4096
                pitchBendOn8.bendAmount = 5120
                pitchBendOn7.bendAmount = 6144
                pitchBendOn6.bendAmount = 7168
                pitchBendOn5.bendAmount = 8192
                pitchBendOn4.bendAmount = 9216
                pitchBendOn3.bendAmount = 10240
                pitchBendOn2.bendAmount = 11264
                pitchBendOn1.bendAmount = 12288
                pitchBendOff.bendAmount = 0
                val on = NoteOn(start, channel, pitch-3, velOn)
                val off = NoteOff(start + duration, channel, pitch-3, velOff)

                mt.insertEvent(pitchBendOn1)
                mt.insertEvent(on)
                mt.insertEvent(pitchBendOn2)
                mt.insertEvent(pitchBendOn3)
                mt.insertEvent(pitchBendOn4)
                mt.insertEvent(pitchBendOn5)
                mt.insertEvent(pitchBendOn6)
                mt.insertEvent(pitchBendOn7)
                mt.insertEvent(pitchBendOn8)
                mt.insertEvent(pitchBendOff)
                mt.insertEvent(off)
            }
            // 5 - 37
//            val portamentoTimeCoarse = Controller(start, channel, 5, 100)
//            val portamentoTimeFine = Controller(start, channel, 37, fine)
//            val portamentoAmount = Controller(start, channel, 84, 60)
//            val portamentoOff = Controller(start+duration, channel,65, 0)

            //0b10000000000001 = 8193 bend off

            //mt.insertEvent(portamentoOn)
            //mt.insertEvent(portamentoTimeCoarse)
            // mt.insertEvent(portamentoTimeFine)
            // mt.insertEvent(portamentoAmount)


            // mt.insertEvent(portamentoOff)


            //println("GLISSANDO: $pitch at $start")
        } else {
            val on = NoteOn(start, channel, pitch, velOn)
            val off = NoteOff(start + duration, channel, pitch, velOff)
            mt.insertEvent(on)
            mt.insertEvent(off)
        }
    }
    fun insertNoteCheckingHigh(
        mt: MidiTrack, start: Int, duration: Int, channel: Int,
        pitch: Int, velOn: Int, velOff: Int
    ) {
        var actualPitch = pitch
        while (actualPitch > 108){
            actualPitch -= 12
        }
        val on = NoteOn(start.toLong(), channel, actualPitch, velOn)
        val off = NoteOff((start + duration).toLong(), channel, actualPitch, velOff)
        mt.insertEvent(on)
        mt.insertEvent(off)
    }
    fun insertNote(
        mt: MidiTrack, start: Long, duration: Long, channel: Int,
        pitch: Int, velOn: Int, velOff: Int
    ) {
        val on = NoteOn(start, channel, pitch, velOn)
        val off = NoteOff(start + duration, channel, pitch, velOff)
        mt.insertEvent(on)
        mt.insertEvent(off)
    }
    private fun addVibratoToTrack(mt: MidiTrack, start: Long, duration: Long, channel: Int, vibratoDivisor: Int){
        val nVibrations = (duration / vibratoDivisor).toInt() // 4 vibrations in a quarter
        if(nVibrations == 0 ) {
            val expressionOn = Controller(start + duration / 3,channel,1, 0b1111111)
            val expressionOff = Controller(start + duration - 4,channel,1, 0)
            mt.insertEvent(expressionOn)
            mt.insertEvent(expressionOff)
        } else {
            val vibrationDur = duration / nVibrations
            val vibrationHalfDur = vibrationDur / 2
            val vibrationQuarterDur = vibrationDur / 4
            (0 until nVibrations).forEach(){
                val expressionMiddle1 = Controller(start + vibrationDur * it + vibrationQuarterDur , channel,1, 64)
                val expressionOn = Controller(start + vibrationDur * it + vibrationHalfDur , channel,1, 0b1111111)
                val expressionMiddle2 = Controller(start + vibrationDur * it + vibrationHalfDur + vibrationQuarterDur , channel,1, 64)
                val expressionOff = Controller(start +  vibrationDur * (it + 1) ,channel,1, 0)
                mt.insertEvent(expressionMiddle1)
                mt.insertEvent(expressionOn)
                mt.insertEvent(expressionMiddle2)
                mt.insertEvent(expressionOff)
            }
        }
    }
    fun convertToMidiTrack(trackData: TrackData, nParts: Int): MidiTrack {
        val track = MidiTrack()
        val channel = trackData.channel
        val vibrato = trackData.vibrato
        val velocityOff = trackData.velocityOff
        val (pitches, ticks, durations, velocities, glissando) = trackData

        // STEREO AND INSTRUMENTS
        val pc: MidiEvent =
            ProgramChange(0, channel, trackData.instrument) // cambia strumento
        track.insertEvent(pc)
        val panStep: Int = 127 / nParts
        val pans = (0 until nParts).map { it * panStep + panStep / 2 }//.also { println(it) }
        if (!trackData.audio8D) { // set a fixed pan if 8D AUDIO is not set on this track
            val pan = Controller(0, channel, 10, pans[trackData.partIndex])
            track.insertEvent(pan)
        }
        if (trackData.doublingFlags == 0) {
            for (i in pitches.indices) {
                val tick = ticks[i].toLong()
                val dur = durations[i].toLong()
                if (trackData.vibrato != 0) {
                    addVibratoToTrack(track, tick, dur, channel, vibrato)
                }
                insertNoteWithGlissando(
                    track, tick, dur, channel, pitches[i],
                    velocities[i], velocityOff, glissando[i]
                )
            }

        } else {
            val doubling = convertFlagsToInts(trackData.doublingFlags)
            for (i in pitches.indices) {
                val tick = ticks[i].toLong()
                val dur = durations[i].toLong()
                val pitch = pitches[i]
                val velocity = velocities[i]
                if (trackData.vibrato != 0) {
                    addVibratoToTrack(track, tick, dur, channel, vibrato)
                }
                insertNoteWithGlissando(
                    track, tick, dur, channel, pitches[i],
                    velocities[i], velocityOff, glissando[i]
                )
                doubling.forEach {
                    insertNoteWithGlissando(
                        track, tick, dur, channel, pitch + it,
                        velocity, velocityOff, glissando[i]
                    )
                }
            }
        }

        // STEREO ALTERATIONS FOR EACH TRACK
        if(trackData.audio8D && track.lengthInTicks > 0){
            val nRevolutions = (12 - trackData.partIndex) * 2
            //val panStep: Int = 127 / counterpoint.parts.size
            val aims = mutableListOf<Float>()
            for(i in 0 until nRevolutions){
                aims.add(0f)
                aims.add(127f)
            }
            aims.add(0f)
            val audio8DalterationsAndDeltas = alterateBpmWithDistribution(aims, 2f, track.lengthInTicks)
            val audio8Dalterations= audio8DalterationsAndDeltas.first
            val audio8Ddeltas = audio8DalterationsAndDeltas.second.also{println(it)}
            var tempoTick = 0L
            (0 until audio8Dalterations.size -1).forEach { i -> // doesn't take the last bpm
                val newPan = Controller(tempoTick, channel,10, audio8Dalterations[i].toInt())
                track.insertEvent(newPan)
                tempoTick += audio8Ddeltas[i]
            }
        }

        return track
    }

}




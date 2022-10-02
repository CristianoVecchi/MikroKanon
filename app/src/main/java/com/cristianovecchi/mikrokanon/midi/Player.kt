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
import kotlinx.coroutines.*
import java.io.IOException
import java.io.File
import java.util.*
import kotlin.coroutines.CoroutineContext


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
        //saveAndPlayMidiFile(player, midi, looping, true, null)
    }

    fun playScheme(
        mediaPlayer: MediaPlayer,
        looping: Boolean,
        bebopBand: BebopBand,
        charlieParker: CharlieParker,
        bebopMelody: BebopMelody,
        chordSequence: ChordSequence,
        bpm: Float,
        soloInstrument: Int,
        shuffle: Float
    ) {
        if (genius == null) genius = CharlieParkerBand()
        val midi: MidiFile = genius!!.playScheme(
            bebopBand,
            charlieParker,
            bebopMelody,
            chordSequence,
            bpm,
            soloInstrument,
            shuffle
        ) ?: return
        //System.out.println("Midifile creato");
        //saveAndPlayMidiFile(mediaPlayer, midi, looping, true, null)


    }

    suspend fun playCounterpoint(
        context: CoroutineContext,
        dispatch: (Triple<AppViewModel.Building, Int, Int>) -> Unit,
        mediaPlayer: MediaPlayer?,
        looping: Boolean,
        counterpoints: List<Counterpoint?>,
        dynamics: List<Float>,
        bpms: List<Float>,
        swingShuffle: Float = 0.5f,
        rhythm: List<Triple<RhythmPatterns, Boolean, Int>>, // Triple: Pattern, isRetrograde, nRepetitions
        ensemblesList: List<List<EnsembleType>>,
        play: Boolean,
        midiFile: File,
        rhythmShuffle: Boolean = false,
        partsShuffle: Boolean = false,
        rowForms: List<Pair<Int, Int>> = listOf(Pair(0, 1)),
        ritornello: Int = 0,
        transpose: List<Pair<Int, Int>> = listOf(Pair(0, 1)),
        doublingFlags: Int = 0,
        nuances: Int = 0,
        rangeTypes: List<Pair<Int, Int>> = listOf(Pair(2, 0)),
        legatoTypes: List<Pair<Int, Int>> = listOf(Pair(2, 0)),
        melodyTypes: List<Int> = listOf(0),
        glissandoFlags: Int = 0,
        audio8DFlags: Int = 0,
        vibrato: Int = 0,
        checkAndReplace: List<List<CheckAndReplaceData>> = listOf(),
        harmonizations: List<HarmonizationData> = listOf(),
        chordsToEnhance: List<ChordToEnhanceData> = listOf(),
        enhanceChordsInTransposition: Boolean = false
    ): String = withContext(context){
        // BUILD ACTUAL COUNTERPOINT
        val job = context.job
        //job.cancelAndJoin()
        val firstCounterpoint = counterpoints.firstOrNull()
        if(firstCounterpoint != null){
            val nNotesToSkip = rhythm[0].first.nNotesLeftInThePattern(firstCounterpoint.nNotes())
            //var actualCounterpoint = if (rowForms == listOf(1)) counterpoint else Counterpoint.explodeRowForms(counterpoint, rowForms, nNotesToSkip)
            var actualCounterpoint = if (rowForms == listOf(Pair(1, 1))) firstCounterpoint
            else Counterpoint.explodeRowFormsAddingCps(counterpoints, rowForms, nNotesToSkip)
            actualCounterpoint = if(enhanceChordsInTransposition){
                actualCounterpoint.handleChordEnhancement(chordsToEnhance).handleRitornellos(ritornello, transpose)
            } else {
                actualCounterpoint.handleRitornellos(ritornello, transpose).handleChordEnhancement(chordsToEnhance)
            }
            if (actualCounterpoint.isEmpty()) {
                "Counterpoint to play is empty!!!"
            } else {
                if (actualCounterpoint.parts.any{ it.absPitches.size == 0}){
                    "Counterpoint parts are empty!!!"
                } else {
                    // COUNTERPOINT FINAL SIZE (NOT CONSIDERING CHECK AND REPLACE!!!)
                    val nTotalNotes = actualCounterpoint.nNotes()
                    // EXTRACT COUNTERPOINT TRACK DATA
                    val durations = rhythm.fold(listOf<Int>()) { acc, triple ->
                        acc + if (triple.second) triple.first.retrogradeValues()
                            .repeat(triple.third) else triple.first.values.repeat(triple.third)
                    }.mergeNegativeValues()
                    //println("durations: $durations")
                    var actualDurations = multiplyDurations(nTotalNotes, durations)
                    if (rhythmShuffle) actualDurations.shuffle()
                    actualDurations = if (swingShuffle != 0.5f) actualDurations.applySwing(swingShuffle) else actualDurations
                    //println(actualDurations.contentToString())
                    val nParts = counterpoints.maxByOrNull { it?.parts?.size ?: 0 }?.parts?.size ?: 0
                    val ensemblePartsList: List<List<EnsemblePart>> =
                        if (ensemblesList.size == 1) listOf(Ensembles.getEnsembleMix(nParts, ensemblesList[0]))
                        else Ensembles.getEnsemblesListMix(nParts, ensemblesList)
                    val actualEnsemblePartsList = if (partsShuffle) ensemblePartsList.map { it.shuffled() } else ensemblePartsList
                    val glissando: List<Int> = if (glissandoFlags == 0) listOf() else convertGlissandoFlags(glissandoFlags)
                    val audio8D: List<Int> = if (audio8DFlags == 0) listOf() else convertFlagsToInts(audio8DFlags).toList()
                    val vibratoExtensions = intArrayOf(0, 360, 240, 160, 120, 80, 60, 30, 15)
                    //dispatch(AppViewModel.Building.DATATRACKS to nParts)
                    val counterpointTrackData: List<TrackData> = CounterpointInterpreter.doTheMagic(
                        context, dispatch,
                        actualCounterpoint, actualDurations, actualEnsemblePartsList,
                        nuances, doublingFlags, rangeTypes, melodyTypes,
                        glissando, audio8D, vibratoExtensions[vibrato]
                    )
                    if (counterpointTrackData.isEmpty()) {
                        "No Tracks in Counterpoint!!!"
                    } else {
                        //counterpointTrackData.forEach{ println(it.pitches.contentToString())}
                        if (!job.isActive) {
                            //dispatch("Cancelled during DataTracks building!")
                            "Cancelled during DataTracks building!"
                        } else {
                            // TOTAL LENGTH OF THE PIECE
                            val totalLength = (counterpointTrackData.filter { it.ticks.isNotEmpty() }
                                .maxOfOrNull { it.ticks.last() + it.durations.last() }
                                ?: 0).toLong()//.also{println("Total length: $it")} // Empty tracks have 0 length
                            //LEGATO AND RIBATTUTO DURATION ZONE
                            // none, staccatissimo, staccato, portato, articolato, legato, legatissimo
                            //println("legatoTypes: $legatoTypes")
                            if (legatoTypes != listOf(Pair(4, 0))) {
                                val maxLegato = rhythm.minByOrNull { it.first.metroDenominatorMidiValue() }!!.first.metroDenominatorMidiValue() / 3
                                val articulations = floatArrayOf(1f, 0.125f, 0.25f, 0.75f, 1f, 1.125f, 1.25f)
                                counterpointTrackData.addLegatoAndRibattuto(maxLegato, articulations, legatoTypes, totalLength)
                            }
                            // CHECK AND REPLACE
                            val actualCounterpointTrackData = counterpointTrackData.applyMultiCheckAndReplace(job, dispatch, checkAndReplace,totalLength)
                            if (!job.isActive) {
                                //dispatch("Cancelled during Check'n'replace building!")
                                "Cancelled during Check'n'replace building!"
                            } else {
                                // TRANSFORM DATATRACKS IN MIDITRACKS
                                val counterpointTracks = actualCounterpointTrackData.map {
                                    yield() //delay(1)
                                    if(job.isActive) {
                                        //dispatch("Building MidiTrack Channel: ${it.channel}")
                                        dispatch(Triple(AppViewModel.Building.MIDITRACKS, it.channel ,nParts))
                                        convertToMidiTrack(it, actualCounterpointTrackData.size, checkAndReplace.any { it.any { it.replace is ReplaceType.Attack} })
                                    } else MidiTrack()
                                }
                                if (!job.isActive) {
                                    //dispatch("Cancelled during MidiTracks building!")
                                    "Cancelled during MidiTracks building!"
                                } else {
                                    // ADD ATTACK TO MIDITRACKS
//                                    if(checkAndReplace.any { it.any { it.replace is ReplaceType.Attack} }){
//                                        actualCounterpointTrackData.forEachIndexed { i, trackData ->
//                                            println(trackData)
//                                            trackData.addAttackToMidiTrack(counterpointTracks[i])
//                                        }
//                                    }
                                    // ADD BPM AND VOLUME CHANGES
                                    val tempoTrack = buildTempoTrack(bpms, totalLength)
                                    tempoTrack.addVolumeToTrack(dynamics, totalLength)

                                    // ADD METRO CHANGES
                                    val nRhythmSteps = durations.filter { it > -1 }.count()
                                    val actualRhythm = multiplyRhythmPatternDatas(nTotalNotes, nRhythmSteps, rhythm)
                                    val bars = tempoTrack.setTimeSignatures(actualRhythm, totalLength)

                                    //BUILD MIDI FILE
                                    val tracks: ArrayList<MidiTrack> = ArrayList<MidiTrack>()
                                    tracks.add(tempoTrack)
                                    tracks.addAll(counterpointTracks)

                                    //ADD CHORD TRACK IF NEEDED
                                    //println(harmonizations)
                                    tracks.addChordTrack(harmonizations, bars,
                                        counterpointTrackData, audio8D, totalLength, false)

                                    val midi = MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks)
                                    //println("JOB ACTIVE: ${job.isActive}")
                                    // WARNING: nTotalNotes returned is not considering check and replace modifications!!!
                                    //dispatch(Triple(AppViewModel.Building.NONE, 0,0))
                                    //delay(1)
                                    yield()
                                    if(job.isActive) {
                                        saveAndPlayMidiFile(job, dispatch, mediaPlayer,  midi, looping, play, midiFile, nTotalNotes)
//                                        val testTracks: ArrayList<MidiTrack> = ArrayList<MidiTrack>()
//                                        testTracks.add(octaveTest(MidiTrack()))
//                                        val testMidi = MidiFile(MidiFile.DEFAULT_RESOLUTION, testTracks)
//                                            saveAndPlayMidiFile(job, dispatch, mediaPlayer,  testMidi, looping, play, midiFile, nTotalNotes)
                                    }
                                    else {
                                        "Canceled before playing Midi File!"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else "NOT EVEN ONE COUNTERPOINT TO PLAY!!!"
    }



    suspend fun saveAndPlayMidiFile(
        context: CoroutineContext,
        dispatch: (Triple<AppViewModel.Building, Int, Int>) -> Unit,
        mediaPlayer: MediaPlayer?,
        midi: MidiFile,
        looping: Boolean,
        play: Boolean,
        midiFile: File?,
        nNotesCounterpoint: Int = 0
    ): String = withContext(context){
        //delay(5)
        yield()
        if(context.job.isActive) {
            if(mediaPlayer!=null){
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
                var error = "Try to play."
                //createDialog(output.toString());
                //mediaPlayer2 = new MediaPlayer();
                dispatch(Triple(AppViewModel.Building.WRITE_FILE, 1, 2))
                try {
                    midi.writeToFile(output)
                } catch (e: IOException) {
                    Log.e(Player::class.java.toString(), e.message, e)
                    error = e.message.toString()
                }
                //println("JOB ACTIVE IN SAVING MIDI: ${context.job.isActive}")
                dispatch(Triple(AppViewModel.Building.NONE, 0, 0))
                if (play && context.job.isActive) {
                    try {
                        mediaPlayer.setDataSource(output!!.getAbsolutePath())
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                        error = nNotesCounterpoint.toString()
                        //player.create(Harmony12.this, output.);
                    } catch (e: java.lang.Exception) {
                        Log.e(Player::class.java.toString(), e.message, e)
                        error = e.message.toString()
                    }
                    //mediaPlayer.setAuxEffectSendLevel()
                    //System.out.println("Midifile salvato");



                    //mediaPlayer.setLooping(looping);
                }
                error
            } else "MediaPlayer is null!!!"

        } else "Job cancelled starting save MIDI"

    }

    private var output: java.io.File? = null
    var output1: java.io.File? = null
    var output2: java.io.File? = null
    fun createTwinMidiFiles(
        bebopBand: BebopBand,
        charlieParker: CharlieParker,
        bebopMelody: BebopMelody,
        chordSequence: ChordSequence,
        bpm: Float,
        soloInstrument: Int,
        shuffle: Float
    ) {
        if (genius == null) genius = CharlieParkerBand()
        val midi: MidiFile = genius!!.playScheme(
            bebopBand,
            charlieParker,
            bebopMelody,
            chordSequence,
            bpm,
            soloInstrument,
            shuffle
        )
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



    fun insertDoublingNote(
        mt: MidiTrack, start: Long, duration: Long, channel: Int,
        pitch: Int, velOn: Int, velOff: Int, ribattuto: Int = 1
    ) {
        var actualPitch = pitch
        while (actualPitch > 108) {
            actualPitch -= 12
        }
        val dur = duration  - separator
        insertNoteWithRibattuto(mt, start, dur, channel, actualPitch, velOn, velOff, ribattuto)
    }
    fun insertNoteWithRibattuto(
        mt: MidiTrack, start: Long, dur: Long, channel: Int,
        pitch: Int, velOn: Int, velOff: Int, ribattuto: Int = 1
    ){
        when (ribattuto){
            in Int.MIN_VALUE..1 -> insertSimpleNote(mt, start, dur, channel, pitch, velOn, velOff)
            in 2..Int.MAX_VALUE -> {
                val ribVelOff = 0
                if(dur < 24 ) {
                    insertSimpleNote(mt, start, dur, channel, pitch, velOn, ribVelOff)
                } else {
                    val divDur = dur / ribattuto
                    if (divDur < 3) {
                        insertSimpleNote(mt, start, dur, channel, pitch, velOn, velOff)
                    } else {
                        val realDur = divDur  - separator
                        for( i in 0 until ribattuto){
                            insertSimpleNote(mt, start + i * divDur, realDur, channel, pitch, velOn, ribVelOff)
                        }
                    }
                }
            }
        }
    }
    fun insertSimpleNote(mt: MidiTrack, start: Long, dur: Long, channel: Int,
                         pitch: Int, velOn: Int, velOff: Int){
        //println("NOTE: start=$start dur=$dur pitch=$pitch ")
        val on = NoteOn(start, channel, pitch, velOn)
        val off = NoteOff(start + dur, channel, pitch, velOff)
        mt.insertEvent(on)
        mt.insertEvent(off)
    }

    private var separator = 1
    private var lastIsGliss = false
    fun insertNoteWithGlissando(
        mt: MidiTrack, start: Long, duration: Long, channel: Int,
        pitch: Int, velOn: Int, velOff: Int, gliss: Int, ribattuto: Int = 1
    ) {
        //println("pitch: $pitch   gliss: $gliss   duration: $duration  lastIsGliss: $lastIsGliss")
        val dur = duration - separator
        if (gliss == 0) {
            if (lastIsGliss) {
                val pitchBendOff = PitchBend(start, channel, 0, 0)
                pitchBendOff.bendAmount = 8192
                mt.insertEvent(pitchBendOff)
                lastIsGliss = false
            }
            insertNoteWithRibattuto(mt, start, dur, channel, pitch, velOn, velOff, ribattuto)
        } else {
            lastIsGliss = true
            val actualGliss = when(duration) {
                in 0..11 -> 0
                in 12..24 -> if(gliss > 0) 1 else -1
                else -> gliss
            }
            when (actualGliss) {
                1 -> {

                    val durationQuarter = duration / 4
                    val pitchBendOn1 = PitchBend(start + durationQuarter, channel, 0, 0)
                    val pitchBendOn2 = PitchBend(start + durationQuarter * 2, channel, 0, 0)
                    val pitchBendOn3 = PitchBend(start + durationQuarter * 3, channel, 0, 0)
                    val pitchBendOn4 = PitchBend(start + dur, channel, 0, 0)
                    val pitchBendOff = PitchBend(start, channel, 0, 0)
                    // 0 4096 8192 12288 (14335) 16383
                    // [4096] 5120 6144 7168 8192(+ht)
                    //val c = 4096
                    pitchBendOn1.bendAmount = 9216
                    pitchBendOn2.bendAmount = 10240
                    pitchBendOn3.bendAmount = 11264
                    pitchBendOn4.bendAmount = 12288
                    pitchBendOff.bendAmount = 8192//0 +c
                    val on = NoteOn(start, channel, pitch, velOn)
                    val off = NoteOff(start + dur, channel, pitch, velOff)
                    mt.insertEvent(pitchBendOff)
                    mt.insertEvent(on)
                    mt.insertEvent(pitchBendOn1)
                    mt.insertEvent(pitchBendOn2)
                    mt.insertEvent(pitchBendOn3)
                    mt.insertEvent(pitchBendOn4)
                    mt.insertEvent(off)
                }
                2 -> {
                    val durationOctave = duration / 8
                    val pitchBendOn1 = PitchBend(start + durationOctave, channel, 0, 0)
                    val pitchBendOn2 = PitchBend(start + durationOctave * 2, channel, 0, 0)
                    val pitchBendOn3 = PitchBend(start + durationOctave * 3, channel, 0, 0)
                    val pitchBendOn4 = PitchBend(start + durationOctave * 4, channel, 0, 0)
                    val pitchBendOn5 = PitchBend(start + durationOctave * 5, channel, 0, 0)
                    val pitchBendOn6 = PitchBend(start + durationOctave * 6, channel, 0, 0)
                    val pitchBendOn7 = PitchBend(start + durationOctave * 7, channel, 0, 0)
                    val pitchBendOn8 = PitchBend(start + dur, channel, 0, 0)
                    val pitchBendOff = PitchBend(start, channel, 0, 0)
                    val pitchBendOff2 = PitchBend(start + dur, channel, 0, 0)
                    // 0 1024 2048 3072 4096(ht) 5120 6144 7168 8192 9216 10240 11264 12288 13312 14336 15360 16384
                    pitchBendOn1.bendAmount = 9216
                    pitchBendOn2.bendAmount = 10240
                    pitchBendOn3.bendAmount = 11264
                    pitchBendOn4.bendAmount = 12288
                    pitchBendOn5.bendAmount = 13312
                    pitchBendOn6.bendAmount = 14336
                    pitchBendOn7.bendAmount = 15360
                    pitchBendOn8.bendAmount = 16383
                    pitchBendOff.bendAmount = 8192
                    val on = NoteOn(start, channel, pitch, velOn)
                    val off = NoteOff(start + dur, channel, pitch, velOff)
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
                    mt.insertEvent(off)


                }
                3 -> {
                    val duration2 = duration / 3
                    val duration1 = duration2 * 2 + duration % 3
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, 2)
                    insertNoteWithGlissando(
                        mt,
                        start + duration1,
                        duration2,
                        channel,
                        pitch + 2,
                        velOn,
                        velOff,
                        1
                    )

                }
                4 -> {
                    val duration2 = duration / 2
                    val duration1 = duration2 + duration % 2
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, 2)
                    insertNoteWithGlissando(
                        mt,
                        start + duration1,
                        duration2,
                        channel,
                        pitch + 2,
                        velOn,
                        velOff,
                        2
                    )
                }
                5 -> {
                    val duration3 = duration / 5
                    val duration2 = duration3 * 2
                    val duration1 = duration2 + duration % 5
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, 2)
                    insertNoteWithGlissando(
                        mt,
                        start + duration1,
                        duration2,
                        channel,
                        pitch + 2,
                        velOn,
                        velOff,
                        2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2,
                        duration3,
                        channel,
                        pitch + 4,
                        velOn,
                        velOff,
                        1
                    )

                }
                6 -> {
                    val duration2 = duration / 3
                    val duration1 = duration2 + duration % 3
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, 2)
                    insertNoteWithGlissando(
                        mt,
                        start + duration1,
                        duration2,
                        channel,
                        pitch + 2,
                        velOn,
                        velOff,
                        2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2,
                        duration2,
                        channel,
                        pitch + 4,
                        velOn,
                        velOff,
                        2
                    )
                }
                7 -> {
                    val duration3 = duration / 7
                    val duration2 = duration3 * 2
                    val duration1 = duration2 + duration % 7
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, 2)
                    insertNoteWithGlissando(
                        mt,
                        start + duration1,
                        duration2,
                        channel,
                        pitch + 2,
                        velOn,
                        velOff,
                        2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2,
                        duration2,
                        channel,
                        pitch + 4,
                        velOn,
                        velOff,
                        2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 2,
                        duration3,
                        channel,
                        pitch + 6,
                        velOn,
                        velOff,
                        1
                    )

                }
                8 -> {
                    val duration2 = duration / 4
                    val duration1 = duration2 + duration % 4
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, 2)
                    insertNoteWithGlissando(
                        mt,
                        start + duration1,
                        duration2,
                        channel,
                        pitch + 2,
                        velOn,
                        velOff,
                        2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2,
                        duration2,
                        channel,
                        pitch + 4,
                        velOn,
                        velOff,
                        2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 2,
                        duration2,
                        channel,
                        pitch + 6,
                        velOn,
                        velOff,
                        2
                    )

                }
                9 -> {
                    val duration3 = duration / 9
                    val duration2 = duration3 * 2
                    val duration1 = duration2 + duration % 9
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, 2)
                    insertNoteWithGlissando(
                        mt,
                        start + duration1,
                        duration2,
                        channel,
                        pitch + 2,
                        velOn,
                        velOff,
                        2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2,
                        duration2,
                        channel,
                        pitch + 4,
                        velOn,
                        velOff,
                        2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 2,
                        duration2,
                        channel,
                        pitch + 6,
                        velOn,
                        velOff,
                        2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 3,
                        duration3,
                        channel,
                        pitch + 8,
                        velOn,
                        velOff,
                        1
                    )

                }
                10 -> {
                    val duration2 = duration / 5
                    val duration1 = duration2 + duration % 5
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, 2)
                    insertNoteWithGlissando(
                        mt,
                        start + duration1,
                        duration2,
                        channel,
                        pitch + 2,
                        velOn,
                        velOff,
                        2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2,
                        duration2,
                        channel,
                        pitch + 4,
                        velOn,
                        velOff,
                        2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 2,
                        duration2,
                        channel,
                        pitch + 6,
                        velOn,
                        velOff,
                        2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 3,
                        duration2,
                        channel,
                        pitch + 8,
                        velOn,
                        velOff,
                        2
                    )

                }
                11 -> {
                    val duration3 = duration / 11
                    val duration2 = duration3 * 2
                    val duration1 = duration2 + duration % 11
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, 2)
                    insertNoteWithGlissando(
                        mt,
                        start + duration1,
                        duration2,
                        channel,
                        pitch + 2,
                        velOn,
                        velOff,
                        2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2,
                        duration2,
                        channel,
                        pitch + 4,
                        velOn,
                        velOff,
                        2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 2,
                        duration2,
                        channel,
                        pitch + 6,
                        velOn,
                        velOff,
                        2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 3,
                        duration2,
                        channel,
                        pitch + 8,
                        velOn,
                        velOff,
                        2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 4,
                        duration3,
                        channel,
                        pitch + 10,
                        velOn,
                        velOff,
                        1
                    )

                }
                12 -> {
                    val duration2 = duration / 6
                    val duration1 = duration2 + duration % 6
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, 2)
                    insertNoteWithGlissando(
                        mt,
                        start + duration1,
                        duration2,
                        channel,
                        pitch + 2,
                        velOn,
                        velOff,
                        2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2,
                        duration2,
                        channel,
                        pitch + 4,
                        velOn,
                        velOff,
                        2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 2,
                        duration2,
                        channel,
                        pitch + 6,
                        velOn,
                        velOff,
                        2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 3,
                        duration2,
                        channel,
                        pitch + 8,
                        velOn,
                        velOff,
                        2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 4,
                        duration2,
                        channel,
                        pitch + 10,
                        velOn,
                        velOff,
                        2
                    )

                }
                -1 -> {
                    val durationQuarter = duration / 4
                    val pitchBendOn1 = PitchBend(start + durationQuarter, channel, 0, 0)
                    val pitchBendOn2 = PitchBend(start + durationQuarter * 2, channel, 0, 0)
                    val pitchBendOn3 = PitchBend(start + durationQuarter * 3, channel, 0, 0)
                    val pitchBendOn4 = PitchBend(start + dur, channel, 0, 0)
                    val pitchBendOff = PitchBend(start, channel, 0, 0)
                    // 0 4096 8192 12288 (14335) 16383
                    // 0(-ht) 1024 2048 3072 [4096]off 5120 6144 7168 8162(+ht)
                    //val c = 4096
                    // 3584 3072 2560 2048 1536 1024 512 0
                    //7168 6144 5120 4096
                    pitchBendOn1.bendAmount = 7168
                    pitchBendOn2.bendAmount = 6144
                    pitchBendOn3.bendAmount = 5120
                    pitchBendOn4.bendAmount = 4096
                    pitchBendOff.bendAmount = 8192//0 +c
                    val on = NoteOn(start, channel, pitch, velOn)
                    val off = NoteOff(start + dur, channel, pitch, velOff)
                    mt.insertEvent(pitchBendOff)
                    mt.insertEvent(on)
                    mt.insertEvent(pitchBendOn1)
                    mt.insertEvent(pitchBendOn2)
                    mt.insertEvent(pitchBendOn3)
                    mt.insertEvent(pitchBendOn4)
                    mt.insertEvent(off)
                }
                -2 -> {
                    val durationOctave = duration / 8
                    val pitchBendOn1 = PitchBend(start + durationOctave, channel, 0, 0)
                    val pitchBendOn2 = PitchBend(start + durationOctave * 2, channel, 0, 0)
                    val pitchBendOn3 = PitchBend(start + durationOctave * 3, channel, 0, 0)
                    val pitchBendOn4 = PitchBend(start + durationOctave * 4, channel, 0, 0)
                    val pitchBendOn5 = PitchBend(start + durationOctave * 5, channel, 0, 0)
                    val pitchBendOn6 = PitchBend(start + durationOctave * 6, channel, 0, 0)
                    val pitchBendOn7 = PitchBend(start + durationOctave * 7, channel, 0, 0)
                    val pitchBendOn8 = PitchBend(start + dur, channel, 0, 0)
                    val pitchBendOff = PitchBend(start, channel, 0, 0)
                    // 0 1024 2048 3072 4096(ht) 5120 6144 7168 8192 9216 10240 11264 12288 13312 14336 15360 16384
                    pitchBendOn1.bendAmount = 7168
                    pitchBendOn2.bendAmount = 6144
                    pitchBendOn3.bendAmount = 5120
                    pitchBendOn4.bendAmount = 4096
                    pitchBendOn5.bendAmount = 3072
                    pitchBendOn6.bendAmount = 2048
                    pitchBendOn7.bendAmount = 1024
                    pitchBendOn8.bendAmount = 0
                    pitchBendOff.bendAmount = 8192
                    val on = NoteOn(start, channel, pitch, velOn)
                    val off = NoteOff(start + dur, channel, pitch, velOff)
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
                    mt.insertEvent(off)
                }
                -3 -> {
                    val duration2 = duration / 3
                    val duration1 = duration2 * 2 + duration % 3
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, -2)
                    insertNoteWithGlissando(
                        mt,
                        start + duration1,
                        duration2,
                        channel,
                        pitch - 2,
                        velOn,
                        velOff,
                        -1
                    )

                }
                -4 -> {
                    val duration2 = duration / 2
                    val duration1 = duration2 + duration % 2
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, -2)
                    insertNoteWithGlissando(
                        mt,
                        start + duration1,
                        duration2,
                        channel,
                        pitch - 2,
                        velOn,
                        velOff,
                        -2
                    )
                }
                -5 -> {
                    val duration3 = duration / 5
                    val duration2 = duration3 * 2
                    val duration1 = duration2 + duration % 5
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, -2)
                    insertNoteWithGlissando(
                        mt,
                        start + duration1,
                        duration2,
                        channel,
                        pitch - 2,
                        velOn,
                        velOff,
                        -2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2,
                        duration3,
                        channel,
                        pitch - 4,
                        velOn,
                        velOff,
                        -1
                    )

                }
                -6 -> {
                    val duration2 = duration / 3
                    val duration1 = duration2 + duration % 3
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, -2)
                    insertNoteWithGlissando(
                        mt,
                        start + duration1,
                        duration2,
                        channel,
                        pitch - 2,
                        velOn,
                        velOff,
                        -2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2,
                        duration2,
                        channel,
                        pitch - 4,
                        velOn,
                        velOff,
                        -2
                    )
                }
                -7 -> {
                    val duration3 = duration / 7
                    val duration2 = duration3 * 2
                    val duration1 = duration2 + duration % 7
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, -2)
                    insertNoteWithGlissando(
                        mt,
                        start + duration1,
                        duration2,
                        channel,
                        pitch - 2,
                        velOn,
                        velOff,
                        -2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2,
                        duration2,
                        channel,
                        pitch - 4,
                        velOn,
                        velOff,
                        -2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 2,
                        duration3,
                        channel,
                        pitch - 6,
                        velOn,
                        velOff,
                        -1
                    )

                }
                -8 -> {
                    val duration2 = duration / 4
                    val duration1 = duration2 + duration % 4
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, -2)
                    insertNoteWithGlissando(
                        mt,
                        start + duration1,
                        duration2,
                        channel,
                        pitch - 2,
                        velOn,
                        velOff,
                        -2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2,
                        duration2,
                        channel,
                        pitch - 4,
                        velOn,
                        velOff,
                        -2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 2,
                        duration2,
                        channel,
                        pitch - 6,
                        velOn,
                        velOff,
                        -2
                    )
                }
                -9 -> {
                    val duration3 = duration / 9
                    val duration2 = duration3 * 2
                    val duration1 = duration2 + duration % 9
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, -2)
                    insertNoteWithGlissando(
                        mt,
                        start + duration1,
                        duration2,
                        channel,
                        pitch - 2,
                        velOn,
                        velOff,
                        -2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2,
                        duration2,
                        channel,
                        pitch - 4,
                        velOn,
                        velOff,
                        -2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 2,
                        duration2,
                        channel,
                        pitch - 6,
                        velOn,
                        velOff,
                        -2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 3,
                        duration3,
                        channel,
                        pitch - 8,
                        velOn,
                        velOff,
                        -1
                    )

                }
                -10 -> {
                    val duration2 = duration / 5
                    val duration1 = duration2 + duration % 5
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, -2)
                    insertNoteWithGlissando(
                        mt,
                        start + duration1,
                        duration2,
                        channel,
                        pitch - 2,
                        velOn,
                        velOff,
                        -2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2,
                        duration2,
                        channel,
                        pitch - 4,
                        velOn,
                        velOff,
                        -2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 2,
                        duration2,
                        channel,
                        pitch - 6,
                        velOn,
                        velOff,
                        -2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 3,
                        duration2,
                        channel,
                        pitch - 8,
                        velOn,
                        velOff,
                        -2
                    )

                }
                -11 -> {
                    val duration3 = duration / 11
                    val duration2 = duration3 * 2
                    val duration1 = duration2 + duration % 11
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, -2)
                    insertNoteWithGlissando(
                        mt,
                        start + duration1,
                        duration2,
                        channel,
                        pitch - 2,
                        velOn,
                        velOff,
                        -2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2,
                        duration2,
                        channel,
                        pitch - 4,
                        velOn,
                        velOff,
                        -2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 2,
                        duration2,
                        channel,
                        pitch - 6,
                        velOn,
                        velOff,
                        -2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 3,
                        duration2,
                        channel,
                        pitch - 8,
                        velOn,
                        velOff,
                        -2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 4,
                        duration3,
                        channel,
                        pitch - 10,
                        velOn,
                        velOff,
                        -1
                    )

                }
                -12 -> {
                    val duration2 = duration / 6
                    val duration1 = duration2 + duration % 6
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, -2)
                    insertNoteWithGlissando(
                        mt,
                        start + duration1,
                        duration2,
                        channel,
                        pitch - 2,
                        velOn,
                        velOff,
                        -2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2,
                        duration2,
                        channel,
                        pitch - 4,
                        velOn,
                        velOff,
                        -2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 2,
                        duration2,
                        channel,
                        pitch - 6,
                        velOn,
                        velOff,
                        -2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 3,
                        duration2,
                        channel,
                        pitch - 8,
                        velOn,
                        velOff,
                        -2
                    )
                    insertNoteWithGlissando(
                        mt,
                        start + duration1 + duration2 * 4,
                        duration2,
                        channel,
                        pitch - 10,
                        velOn,
                        velOff,
                        -2
                    )

                }
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
        }
    }


}


//fun main(args : Array<String>){
//    val tr1 = Triple(RhythmPatterns.PLAIN_4_4_R16, false, 3)
//    val tr2 = Triple(RhythmPatterns.BASIC_8, false, 2)
//    val tr3 = Triple(RhythmPatterns.PLAIN_3_4_R16, false, 2)
//    val tr4 = Triple(RhythmPatterns.BASIC_32, false, 23)
//    val tr5 = Triple(RhythmPatterns.BASIC_32, false, 7)
//    val tr5adding = Triple(RhythmPatterns.BASIC_32, false, 7)
//    val tr6 = Triple(RhythmPatterns.BASIC_16, false, 1)
//    val rhythm = listOf(tr1, tr2, tr3, tr4, tr5, tr5adding, tr6)
//    val tempoTrack = MidiTrack()
//    Player.setTimeSignatures(tempoTrack, rhythm, 50000L)
//}







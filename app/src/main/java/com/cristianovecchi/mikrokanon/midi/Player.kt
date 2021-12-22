package com.cristianovecchi.mikrokanon.midi

import android.media.MediaPlayer
import android.os.Build
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
import java.util.*
import kotlin.math.absoluteValue


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
                                                                    nuances, doublingFlags, rangeTypes, melodyTypes,
                                                                    glissando, audio8D, vibratoExtensions[vibrato])
        if (counterpointTrackData.isEmpty()) return "No Tracks in Counterpoint!!!"

        val totalLength = (counterpointTrackData.filter{it.ticks.isNotEmpty()}.maxOfOrNull{ it.ticks.last() + it.durations.last()} ?: 0).toLong()//.also{println("Total length: $it")} // Empty tracks have 0 length

        //LEGATO AND RIBATTUTO DURATION ZONE
        // none, staccatissimo, staccato, portato, articolato, legato, legatissimo
        //println("legatoTypes: $legatoTypes")
        if(legatoTypes != listOf(Pair(4,0))){
            val artMap = mapOf(0 to 1.0f, 1 to 0.125f, 2 to 0.25f, 3 to 0.75f, 4 to 1.0f, 5 to 1.125f, 6 to 1.25f )
            val legatos = legatoTypes.map{ artMap[it.first.absoluteValue]!! * (if(it.first<0) -1 else 1)}
            val legatoAlterationsAndDeltas = alterateBpmWithDistribution(legatos,0.005f, totalLength)
            val legatoAlterations = legatoAlterationsAndDeltas.first//.also { println("${it.size} + $it") }
            val legatoDeltas = legatoAlterationsAndDeltas.second//.also { println("${it.size} + $it") }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                counterpointTrackData.parallelStream().forEach {
                    it.articulationDurations = alterateArticulation(it.ticks, it.durations, legatoAlterations, legatoDeltas) }
            } else {
                counterpointTrackData.forEach {
                    it.articulationDurations = alterateArticulation(it.ticks, it.durations, legatoAlterations, legatoDeltas) }
            }
        }


        // TRANSFORM DATATRACKS IN MIDITRACKS
        val counterpointTracks = counterpointTrackData.map{ convertToMidiTrack(it, counterpointTrackData.size) }

        //val counterpointTracks = listOf(pitchBenderTest(MidiTrack()))
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

    private fun alterateArticulation(ticks: IntArray, durations: IntArray,
                                     legatoAlterations: List<Float>, legatoDeltas: List<Long>): IntArray {
        val result = IntArray(durations.size)
        var alterationIndex = 0
        var alterationTick = 0
        var durIndex = 0
        var legatoAlteration: Float
        var newDur: Int
        var nextDur: Int
        var thisDur: Int
        var legatoLimit: Int
        while(durIndex < durations.size ){
            while(alterationTick + legatoDeltas[alterationIndex] < ticks[durIndex] ) {
                alterationTick += legatoDeltas[alterationIndex].toInt()
                alterationIndex++
            }
            legatoAlteration = legatoAlterations[alterationIndex]
            thisDur = durations[durIndex]
            if(legatoAlteration<=1.0){
                newDur = (thisDur * legatoAlteration).toInt()
                result[durIndex] = if(newDur < 12) 12 else newDur
            } else {
                nextDur = if(durIndex < durations.size - 1) durations[durIndex + 1] else 0
                newDur = (thisDur * legatoAlteration).toInt()
                legatoLimit = thisDur + (nextDur * (legatoAlteration - 1f)).toInt()
                result[durIndex] = if(newDur > legatoLimit) legatoLimit else newDur
            }
            durIndex++
        }
        //result.also{ println("Alterate articulations: ${it.contentToString()}") }
        return result
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
    private fun pitchBenderTest(mt: MidiTrack): MidiTrack{
        val pitches2m = listOf(61,60,59,61,60,59)
        val noBend = 8192
        val ht = 4096
        val halfToneUp = noBend + ht
        val halfToneDown = noBend - ht
        val toneUp = noBend + ht * 2 -1
        val toneDown = noBend + ht * 2
        val durs = listOf(480,480,480,480,480,480)
        val glissAmounts = listOf(noBend, noBend, noBend, toneDown, toneDown, noBend )
       // var lastGliss = false
        val pitches = pitches2m
        var tick = 0L
        (0 until 3).forEach{
            val on = NoteOn(tick, 0, pitches[it], 100)
            val off = NoteOff(tick + durs[it]-1, 0, pitches[it], 80)
            mt.insertEvent(on)
            mt.insertEvent(off)
            tick += durs[it]
        }
        (3 until 6).forEach{
            val pitchBendOff= PitchBend(tick , 0,0,0)
            pitchBendOff.bendAmount = noBend
            mt.insertEvent(pitchBendOff)

            if(it != 5){
                //7168 6144 5120 4096
                val pitchBendOn1 = PitchBend(tick+120, 0,0,0)
                pitchBendOn1.bendAmount = 7168
                val pitchBendOn2 = PitchBend(tick+240, 0,0,0)
                pitchBendOn2.bendAmount = 6144
                val pitchBendOn3 = PitchBend(tick+360, 0,0,0)
                pitchBendOn3.bendAmount = 5120
                val pitchBendOn4= PitchBend(tick+480-1, 0,0,0)
                pitchBendOn4.bendAmount = 4096
                mt.insertEvent(pitchBendOn1)
                mt.insertEvent(pitchBendOn2)
                mt.insertEvent(pitchBendOn3)
                mt.insertEvent(pitchBendOn4)
            }


            val on = NoteOn(tick, 0, pitches[it], 100)
            val off = NoteOff(tick + durs[it]-1, 0, pitches[it], 80)
            mt.insertEvent(on)
            mt.insertEvent(off)
            tick += durs[it]
        }
        return mt
    }
    private var separator = 1
    private var lastIsGliss = false
    private fun insertNoteWithGlissando(
        mt: MidiTrack, start: Long, duration: Long, channel: Int,
        pitch: Int, velOn: Int, velOff: Int, gliss: Int
    ) {
        //println("pitch: $pitch   gliss: $gliss   duration: $duration  lastIsGliss: $lastIsGliss")
        val dur = duration - separator
        if(gliss == 0) {
            if(lastIsGliss){
                val pitchBendOff = PitchBend(start, channel, 0, 0)
                pitchBendOff.bendAmount = 8192
                mt.insertEvent(pitchBendOff)
                lastIsGliss = false
            }
            val on = NoteOn(start, channel, pitch, velOn)
            val off = NoteOff(start + dur, channel, pitch, velOff)
            mt.insertEvent(on)
            mt.insertEvent(off)
        } else{
            lastIsGliss = true
            when (gliss) {
                1 -> {

                    val durationQuarter = duration / 4
                    val pitchBendOn1= PitchBend(start + durationQuarter, channel,0,0)
                    val pitchBendOn2= PitchBend(start + durationQuarter * 2, channel,0,0)
                    val pitchBendOn3= PitchBend(start + durationQuarter * 3, channel,0,0)
                    val pitchBendOn4= PitchBend(start + dur, channel,0,0)
                    val pitchBendOff= PitchBend(start , channel,0,0)
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
                    val pitchBendOn1= PitchBend(start + durationOctave, channel,0,0)
                    val pitchBendOn2= PitchBend(start + durationOctave * 2, channel,0,0)
                    val pitchBendOn3= PitchBend(start + durationOctave * 3, channel,0,0)
                    val pitchBendOn4= PitchBend(start + durationOctave * 4, channel,0,0)
                    val pitchBendOn5= PitchBend(start + durationOctave * 5, channel,0,0)
                    val pitchBendOn6= PitchBend(start + durationOctave * 6, channel,0,0)
                    val pitchBendOn7= PitchBend(start + durationOctave * 7, channel,0,0)
                    val pitchBendOn8= PitchBend(start + dur, channel,0,0)
                    val pitchBendOff= PitchBend(start , channel,0,0)
                    val pitchBendOff2= PitchBend(start + dur , channel,0,0)
                    // 0 1024 2048 3072 4096(ht) 5120 6144 7168 8192 9216 10240 11264 12288 13312 14336 15360 16384
                    pitchBendOn1.bendAmount = 9216
                    pitchBendOn2.bendAmount = 10240
                    pitchBendOn3.bendAmount = 11264
                    pitchBendOn4.bendAmount = 12288
                    pitchBendOn5.bendAmount = 13312
                    pitchBendOn6.bendAmount = 14336
                    pitchBendOn7.bendAmount = 15360
                    pitchBendOn8.bendAmount = 16384
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
                    insertNoteWithGlissando(mt, start + duration1, duration2, channel, pitch + 2, velOn, velOff,1)

                }
                4 -> {
                    val duration2 = duration / 2
                    val duration1 = duration2 + duration % 2
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, 2)
                    insertNoteWithGlissando(mt, start + duration1, duration2, channel, pitch + 2, velOn, velOff, 2)
                }
                5 -> {
                    val duration3 = duration / 5
                    val duration2 = duration3 * 2
                    val duration1 = duration2 + duration % 5
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, 2)
                    insertNoteWithGlissando(mt, start + duration1, duration2, channel, pitch + 2, velOn, velOff,2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2, duration3, channel, pitch + 4, velOn, velOff,1)

                }
                6 -> {
                    val duration2 = duration / 3
                    val duration1 = duration2 + duration % 3
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, 2)
                    insertNoteWithGlissando(mt, start + duration1, duration2, channel, pitch + 2, velOn, velOff,2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2, duration2, channel, pitch + 4, velOn, velOff,2)
                }
                7 -> {
                    val duration3 = duration / 7
                    val duration2 = duration3 * 2
                    val duration1 = duration2 + duration % 7
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, 2)
                    insertNoteWithGlissando(mt, start + duration1, duration2, channel, pitch + 2, velOn, velOff,2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 , duration2, channel, pitch + 4, velOn, velOff,2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 2, duration3, channel, pitch + 6, velOn, velOff,1)

                }
                8 -> {
                    val duration2 = duration / 4
                    val duration1 = duration2 + duration % 4
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, 2)
                    insertNoteWithGlissando(mt, start + duration1, duration2, channel, pitch + 2, velOn, velOff,2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2, duration2, channel, pitch + 4, velOn, velOff,2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 2, duration2, channel, pitch + 6, velOn, velOff,2)

                }
                9 -> {
                    val duration3 = duration / 9
                    val duration2 = duration3 * 2
                    val duration1 = duration2 + duration % 9
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, 2)
                    insertNoteWithGlissando(mt, start + duration1, duration2, channel, pitch + 2, velOn, velOff,2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 , duration2, channel, pitch + 4, velOn, velOff,2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 2, duration2, channel, pitch + 6, velOn, velOff,2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 3, duration3, channel, pitch + 8, velOn, velOff,1)

                }
                10 -> {
                    val duration2 = duration / 5
                    val duration1 = duration2 + duration % 5
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, 2)
                    insertNoteWithGlissando(mt, start + duration1, duration2, channel, pitch + 2, velOn, velOff,2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2, duration2, channel, pitch + 4, velOn, velOff,2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 2, duration2, channel, pitch + 6, velOn, velOff,2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 3, duration2, channel, pitch + 8, velOn, velOff,2)

                }
                11 -> {
                    val duration3 = duration / 11
                    val duration2 = duration3 * 2
                    val duration1 = duration2 + duration % 11
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, 2)
                    insertNoteWithGlissando(mt, start + duration1, duration2, channel, pitch + 2, velOn, velOff,2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 , duration2, channel, pitch + 4, velOn, velOff,2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 2, duration2, channel, pitch + 6, velOn, velOff,2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 3, duration2, channel, pitch + 8, velOn, velOff,2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 4, duration3, channel, pitch + 10, velOn, velOff,1)

                }
                12 -> {
                    val duration2 = duration / 6
                    val duration1 = duration2 + duration % 6
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, 2)
                    insertNoteWithGlissando(mt, start + duration1, duration2, channel, pitch + 2, velOn, velOff,2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2, duration2, channel, pitch + 4, velOn, velOff,2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 2, duration2, channel, pitch + 6, velOn, velOff,2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 3, duration2, channel, pitch + 8, velOn, velOff,2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 4, duration2, channel, pitch + 10, velOn, velOff,2)

                }
                -1 -> {
                    val durationQuarter = duration / 4
                    val pitchBendOn1= PitchBend(start + durationQuarter, channel,0,0)
                    val pitchBendOn2= PitchBend(start + durationQuarter * 2, channel,0,0)
                    val pitchBendOn3= PitchBend(start + durationQuarter * 3, channel,0,0)
                    val pitchBendOn4= PitchBend(start + dur, channel,0,0)
                    val pitchBendOff= PitchBend(start , channel,0,0)
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
                    val pitchBendOn1= PitchBend(start + durationOctave, channel,0,0)
                    val pitchBendOn2= PitchBend(start + durationOctave * 2, channel,0,0)
                    val pitchBendOn3= PitchBend(start + durationOctave * 3, channel,0,0)
                    val pitchBendOn4= PitchBend(start + durationOctave *4, channel,0,0)
                    val pitchBendOn5= PitchBend(start + durationOctave * 5, channel,0,0)
                    val pitchBendOn6= PitchBend(start + durationOctave * 6, channel,0,0)
                    val pitchBendOn7= PitchBend(start + durationOctave * 7, channel,0,0)
                    val pitchBendOn8= PitchBend(start + dur, channel,0,0)
                    val pitchBendOff= PitchBend(start , channel,0,0)
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
                    mt.insertEvent(off)
                }
                -3 -> {
                    val duration2 = duration / 3
                    val duration1 = duration2 * 2 + duration % 3
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, -2)
                    insertNoteWithGlissando(mt, start + duration1, duration2, channel, pitch - 2, velOn, velOff,-1)

                }
                -4 -> {
                    val duration2 = duration / 2
                    val duration1 = duration2 + duration % 2
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, -2)
                    insertNoteWithGlissando(mt, start + duration1, duration2, channel, pitch - 2, velOn, velOff, -2)
                }
                -5 -> {
                    val duration3 = duration / 5
                    val duration2 = duration3 * 2
                    val duration1 = duration2 + duration % 5
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, -2)
                    insertNoteWithGlissando(mt, start + duration1, duration2, channel, pitch - 2, velOn, velOff,-2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2, duration3, channel, pitch - 4, velOn, velOff,-1)

                }
                -6 -> {
                    val duration2 = duration / 3
                    val duration1 = duration2 + duration % 3
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, -2)
                    insertNoteWithGlissando(mt, start + duration1, duration2, channel, pitch - 2, velOn, velOff,-2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2, duration2, channel, pitch - 4, velOn, velOff,-2)
                }
                -7 -> {
                    val duration3 = duration / 7
                    val duration2 = duration3 * 2
                    val duration1 = duration2 + duration % 7
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, -2)
                    insertNoteWithGlissando(mt, start + duration1, duration2, channel, pitch -2, velOn, velOff,-2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 , duration2, channel, pitch - 4, velOn, velOff,-2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 2, duration3, channel, pitch - 6, velOn, velOff,-1)

                }
                -8 -> {
                    val duration2 = duration / 4
                    val duration1 = duration2 + duration % 4
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, -2)
                    insertNoteWithGlissando(mt, start + duration1, duration2, channel, pitch - 2, velOn, velOff,-2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2, duration2, channel, pitch - 4, velOn, velOff,-2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 *2, duration2, channel, pitch - 6, velOn, velOff,-2)
                }
                -9 -> {
                    val duration3 = duration / 9
                    val duration2 = duration3 * 2
                    val duration1 = duration2 + duration % 9
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, -2)
                    insertNoteWithGlissando(mt, start + duration1, duration2, channel, pitch - 2, velOn, velOff,-2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 , duration2, channel, pitch - 4, velOn, velOff,-2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 2, duration2, channel, pitch - 6, velOn, velOff,-2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 3, duration3, channel, pitch - 8, velOn, velOff,-1)

                }
                -10 -> {
                    val duration2 = duration / 5
                    val duration1 = duration2 + duration % 5
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, -2)
                    insertNoteWithGlissando(mt, start + duration1, duration2, channel, pitch - 2, velOn, velOff,-2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2, duration2, channel, pitch - 4, velOn, velOff,-2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 2, duration2, channel, pitch - 6, velOn, velOff,-2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 3, duration2, channel, pitch - 8, velOn, velOff,-2)

                }
                -11 -> {
                    val duration3 = duration / 11
                    val duration2 = duration3 * 2
                    val duration1 = duration2 + duration % 11
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, -2)
                    insertNoteWithGlissando(mt, start + duration1, duration2, channel, pitch - 2, velOn, velOff,-2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 , duration2, channel, pitch - 4, velOn, velOff,-2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 2, duration2, channel, pitch - 6, velOn, velOff,-2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 3, duration2, channel, pitch - 8, velOn, velOff,-2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 4, duration3, channel, pitch - 10, velOn, velOff,-1)

                }
                -12 -> {
                    val duration2 = duration / 6
                    val duration1 = duration2 + duration % 6
                    insertNoteWithGlissando(mt, start, duration1, channel, pitch, velOn, velOff, -2)
                    insertNoteWithGlissando(mt, start + duration1, duration2, channel, pitch - 2, velOn, velOff,-2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2, duration2, channel, pitch - 4, velOn, velOff,-2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 2, duration2, channel, pitch - 6, velOn, velOff,-2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 3, duration2, channel, pitch - 8, velOn, velOff,-2)
                    insertNoteWithGlissando(mt, start + duration1 + duration2 * 4, duration2, channel, pitch - 10, velOn, velOff,-2)

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
    private fun addAttackDelayToTrack(mt: MidiTrack, start: Long, channel: Int, attackDelay: Int){
        val attackAmount = Controller(start,channel,73, attackDelay)
        mt.insertEvent(attackAmount)
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
        val articulationDurations = trackData.articulationDurations ?: durations

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

//        var lastIsGliss = false
//        var attackIsDelayed = false
        if (trackData.doublingFlags == 0) {
            for (i in pitches.indices) {
                val tick = ticks[i].toLong()
                val gliss = glissando[i]
                val duration = durations[i]
                val articulationDuration = articulationDurations[i]
                val overLegato = articulationDuration > duration
//                val attackDelay = if(lastIsGliss && (articulationDuration == duration || overLegato)) 127 else 0
                val dur = if(overLegato && (glissando[(i+1) % glissando.size] >0 || gliss >0)  )
                    duration.toLong() else articulationDuration.toLong()
                //println("note $i attack: $attackDelay")
                if (trackData.vibrato != 0) {
                    addVibratoToTrack(track, tick, dur, channel, vibrato)
                }
//                if (attackDelay > 0){
//                   addAttackDelayToTrack(track, tick, channel, attackDelay)
//                    attackIsDelayed = true
//                } else {
//                    if(attackIsDelayed)  addAttackDelayToTrack(track, tick, channel, 0)
//                    attackIsDelayed = false
//                }
                insertNoteWithGlissando(
                    track, tick, dur, channel, pitches[i],
                    velocities[i], velocityOff, gliss
                )
//                lastIsGliss = gliss > 0
            }

        } else {
            val doubling = convertFlagsToInts(trackData.doublingFlags)
            for (i in pitches.indices) {
                val tick = ticks[i].toLong()
                val gliss = glissando[i]
                val duration = durations[i]
                val articulationDuration = articulationDurations[i]
                val dur = if(articulationDuration > duration && (glissando[(i+1) % glissando.size] >0 || gliss >0)  )
                    duration.toLong() else articulationDuration.toLong()
                val pitch = pitches[i]
                val velocity = velocities[i]

                if (trackData.vibrato != 0) {
                    addVibratoToTrack(track, tick, dur, channel, vibrato)
                }
                insertNoteWithGlissando(
                    track, tick, dur, channel, pitches[i],
                    velocities[i], velocityOff, gliss
                )
                doubling.forEach {
                    insertNoteWithGlissando(
                        track, tick, dur, channel, pitch + it,
                        velocity, velocityOff, gliss
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




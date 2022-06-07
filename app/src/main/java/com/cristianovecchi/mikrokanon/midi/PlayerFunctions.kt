package com.cristianovecchi.mikrokanon.midi

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.alterateBpmWithDistribution
import com.cristianovecchi.mikrokanon.convertDodecabyteToInts
import com.cristianovecchi.mikrokanon.convertFlagsToInts
import com.leff.midi.MidiTrack
import com.leff.midi.event.*
import com.leff.midi.event.meta.TimeSignature
import kotlin.math.abs
import kotlin.math.roundToInt

fun findExtendedWeightedHarmonyNotes(chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>, roots: MutableList<Int>,
                                     diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true) {
    data class Note(val pitch: Int, val tick: Long, var duration: Long, val velocity: Int)
    val notes = mutableListOf<Note>()
    val rootNotes = mutableListOf<Note>()
    for (absPitch in 0..11) {
        val contains = BooleanArray(bars.size) { false }
        bars.forEachIndexed { index, bar ->
           // println("Bar$index = ${bar.dodecaByte1stHalf!!.toString(2)}")
            if (convertDodecabyteToInts(bar.dodecaByte1stHalf!!).contains(absPitch)) contains[index] = true
        }
        var index = 0
        var lastNote = Note(-1, 0, 0,0)
        while (index < contains.size) {
            if (contains[index]) {
                val bar = bars[index]
                if (lastNote.pitch == -1) {
                    lastNote = Note(absPitch, bar.tick, bar.duration, bar.minVelocity!!)
                } else {
                    lastNote.duration += bar.duration
                }
            } else {
                if (lastNote.pitch != -1) {
                    notes.add(lastNote)
                    lastNote = Note(-1, 0, 0,0)
                }
            }
            index++
        }
        if (lastNote.pitch != -1) {
            notes.add(lastNote)
        }
    }
    if(!justVoicing){
        var lastRootNote = Note(-1,0,0, 0)
        var index = 0
        while (index < roots.size) {
            val bar = bars[index]
            val newRoot = roots[index]
            if (newRoot != lastRootNote.pitch) {
                lastRootNote = Note(newRoot, bar.tick, bar.duration, bar.minVelocity!!)
                rootNotes.add(lastRootNote)
            } else {
                lastRootNote.duration += bar.duration
            }
            index ++
        }
    }

        notes.sortedBy { it.tick }.forEach {
            //print("Chord note: ${it.pitch}, ")
            val absPitch = it.pitch
            val tick = it.tick
            val duration = it.duration
            val velocity = (it.velocity - diffChordVelocity).coerceIn(0,127)
            for (octave in 4..8) {
                Player.insertNoteWithGlissando(
                    chordsTrack, tick, duration, chordsChannel,
                    octave * 12 + absPitch, velocity, 70, 0
                )
            }
        }
            if(!justVoicing){
            rootNotes.sortedBy { it.tick }.forEach {
                println("Root: $it")
                val absPitch = it.pitch
                val tick = it.tick
                val duration = it.duration
                val velocity = (it.velocity - diffRootVelocity).coerceIn(0,127)
                for (octave in 2..3) {
                    Player.insertNoteWithGlissando(
                        chordsTrack, tick, duration, chordsChannel,
                        octave * 12 + absPitch, velocity, 50, 0
                    )
                }
            }
        }
}
fun findChordNotes(chordsTrack: MidiTrack, chordsChannel: Int, bars: List<Bar>,
                   diffChordVelocity:Int, diffRootVelocity:Int, justVoicing: Boolean = true) {
    data class Note(val pitch: Int, val tick: Long, var duration: Long, val velocity: Int)

    val notes = mutableListOf<Note>()
    val roots = mutableListOf<Note>()
    for (absPitch in 0..11) {
        val contains = BooleanArray(bars.size) { false }
        bars.forEachIndexed { index, bar ->
            if (bar.chord1!!.absoluteNotes.contains(absPitch)) contains[index] = true
        }
        var index = 0
        var lastNote = Note(-1, 0, 0,0)
        while (index < contains.size) {
            if (contains[index]) {
                val bar = bars[index]
                if (lastNote.pitch == -1) {
                    lastNote = Note(absPitch, bar.tick, bar.duration, bar.minVelocity!!)
                } else {
                    lastNote.duration += bar.duration
                }
            } else {
                if (lastNote.pitch != -1) {
                    notes.add(lastNote)
                    lastNote = Note(-1, 0, 0,0)
                }
            }
            index++
        }
        if (lastNote.pitch != -1) {
            notes.add(lastNote)
        }
    }
    if(!justVoicing){
        var lastRoot = Note(-1,0,0, 0)
        var index = 0
        while (index < bars.size) {
            val bar = bars[index]
            val newRoot = bar.chord1!!.root
            if (newRoot != lastRoot.pitch) {
                    lastRoot = Note(newRoot, bar.tick, bar.duration, bar.minVelocity!!)
                    roots.add(lastRoot)
            } else {
                lastRoot.duration += bar.duration
            }
            index ++
        }
    }
    notes.sortedBy { it.tick }.forEach {
        val absPitch = it.pitch
        val tick = it.tick
        val duration = it.duration
        val velocity = (it.velocity - diffChordVelocity).coerceIn(0,127)
        for (octave in 4..8) {
            Player.insertNoteWithGlissando(
                chordsTrack, tick, duration, chordsChannel,
                octave * 12 + absPitch, velocity, 70, 0
            )
        }
    }
    if(!justVoicing){
        roots.sortedBy { it.tick }.forEach {
            println("Root: $it")
            val absPitch = it.pitch
            val tick = it.tick
            val duration = it.duration
            val velocity = (it.velocity - diffRootVelocity).coerceIn(0,127)
            for (octave in 2..3) {
                //val pitch = octave * 12 + absPitch
                Player.insertNoteWithGlissando(
                    chordsTrack, tick, duration, chordsChannel,
                    octave * 12 + absPitch, velocity, 50, 0
                )
            }
        }
    }
}

fun insertChordNotes(chordsTrack: MidiTrack, channel: Int, root: Int,
                     absPitches: IntArray, tick: Long, duration: Long, velocity: Int, justVoicing: Boolean = false) {
    if(!justVoicing){
        for(octave in 2..3){
            Player.insertNoteWithGlissando(chordsTrack, tick, duration, channel,
                octave * 12 + root, velocity, 70,0)

        }
    }
    for(octave in 4..8){
        for(absPitch in absPitches){
            Player.insertNoteWithGlissando(chordsTrack, tick, duration, channel,
                octave * 12 + absPitch, velocity, 70,0)
        }
    }
}
fun assignDodecaBytesToBars(bars: Array<Bar>, counterpointTrackData: List<TrackData>, withArticulation: Boolean = false) {
    bars.forEach { it.dodecaByte1stHalf = 0 ; it.dodecaByte2ndHalf = 0 ; it.minVelocity = 80}
    counterpointTrackData.forEach{ trackData ->
        val durations = if(trackData.articulationDurations != null && withArticulation) trackData.articulationDurations!! else trackData.durations
        var barIndex = 0
        var pitchIndex = 0
        while(pitchIndex < trackData.pitches.size){
            val bar = bars[barIndex]
            val pitch = trackData.pitches[pitchIndex]
            val velocity = trackData.velocities[pitchIndex]
            val barEnd = bar.tick + bar.duration
            val pitchStart = trackData.ticks[pitchIndex]
            val pitchEnd = pitchStart + durations[pitchIndex]
            if(trackData.ticks[pitchIndex] < barEnd ){
                bar.dodecaByte1stHalf = bar.dodecaByte1stHalf?.or((1 shl (pitch % 12)))
                if(velocity < bar.minVelocity!! ) bar.minVelocity = velocity
                if(pitchEnd > barEnd) barIndex++ else pitchIndex++

            } else {
                barIndex++
            }
        }
    }
}
fun printNoteLimits(ticks: IntArray, durations: IntArray) {
    for(i in ticks.indices){
        print("$i=${ticks[i]}-${ticks[i]+durations[i]} ")
    }
    println()
}
fun convertToMidiTrack(trackData: TrackData, nParts: Int): MidiTrack {
    val track = MidiTrack()
    val channel = trackData.channel
    val vibrato = trackData.vibrato
    val velocityOff = trackData.velocityOff
    val (pitches, ticks, durations, velocities, glissando) = trackData
    val articulationDurations = trackData.articulationDurations ?: durations
    val ribattutos = trackData.ribattutos ?: IntArray(pitches.size){ 1 }

    // Instrument changes
//    println()
//    println("CHANNEL: $channel")
    var lastTick = -1L // avoid overriding
   // var noteIndex = -1
    //printNoteLimits(ticks, articulationDurations)
    trackData.changes.forEach{
       // println("Intrument change: $it")
        val tick = it.tick
//        do {
//            noteIndex++
//        } while(noteIndex < ticks.size && ticks[noteIndex]+articulationDurations[noteIndex]<= tick)
//        println("note index: $noteIndex")
//        println("old tick: $tick")
//        tick = if(tick <= ticks[noteIndex]) tick else ticks[noteIndex]+articulationDurations[noteIndex]+1.toLong()
//        println("new tick: $tick")
        if(tick > lastTick){
//            println(it)
            val pc: MidiEvent = ProgramChange(tick, channel, it.instrument) // cambia strumento
            track.insertEvent(pc)
            lastTick = tick
        }

    }
    // STEREO
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
            val ribattuto = ribattutos[i]
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
            Player.insertNoteWithGlissando(
                track, tick, dur, channel, pitches[i],
                velocities[i], velocityOff, gliss, ribattuto
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
            val ribattuto = ribattutos[i]
            val overLegato = articulationDuration > duration
//                val attackDelay = if(lastIsGliss && (articulationDuration == duration || overLegato)) 127 else 0
            val dur = if(overLegato && (glissando[(i+1) % glissando.size] >0 || gliss >0)  )
                duration.toLong() else articulationDuration.toLong()
            val pitch = pitches[i]
            val velocity = velocities[i]
            if (trackData.vibrato != 0) {
                addVibratoToTrack(track, tick, dur, channel, vibrato)
            }
            Player.insertNoteWithGlissando(
                track, tick, dur, channel, pitch,
                velocity, velocityOff, gliss, ribattuto
            )
            doubling.forEach {
                Player.insertDoublingNote(
                    track, tick, dur, channel, pitch + it,
                    velocity, velocityOff, ribattuto
                )
            }
        }
    }
    // STEREO ALTERATIONS FOR EACH TRACK
    if(trackData.audio8D && track.lengthInTicks > 0) {
        val nRevolutions = (12 - trackData.partIndex) * 2
        setAudio8D(track, nRevolutions, channel)
    }
    return track
}


fun setAudio8D(track: MidiTrack, nRevolutions: Int, channel: Int) {
    val aims = mutableListOf<Float>()
    for(i in 0 until nRevolutions){
        aims.add(0f)
        aims.add(127f)
    }
    aims.add(0f)
    val (audio8Dalterations, audio8Ddeltas) = alterateBpmWithDistribution(aims, 2f, track.lengthInTicks)
    var tick = 0L
    (0 until audio8Dalterations.size -1).forEach { i -> // doesn't take the last bpm
        val newPan = Controller(tick, channel,10, audio8Dalterations[i].toInt())
        track.insertEvent(newPan)
        tick += audio8Ddeltas[i]
    }
}


fun setTimeSignatures(
    tempoTrack: MidiTrack,
    rhythm: List<Triple<RhythmPatterns, Boolean, Int>>,
    totalLength: Long
): List<Bar> {
    val bars = mutableListOf<Bar>()
    var tick = 0L
    var barTick = 0L
    var lastSignature = Pair(-1, -1)
    val signatures: List<Pair<Int, Pair<Int, Int>>> = rhythm.map {
        val patternDuration = it.first.patternDuration()
        val barDuration = it.first.barDuration()
        val nRepetitions = it.third
        val metro = it.first.metro
        for(i in 0 until nRepetitions * (patternDuration/barDuration)){
            bars.add(Bar(metro, barTick, barDuration.toLong()))
            barTick += barDuration
        }
        Pair(patternDuration * nRepetitions, metro)
    }
    var index = 0
    while (tick < totalLength) {
        var newSignature = signatures[index].second
        if (newSignature.first == 1) {
            newSignature =
                RhythmPatterns.mergeSequenceOfOnesInMetro(signatures[index].first, newSignature)
        }
        if (newSignature != lastSignature) {
            val ts = TimeSignature(
                tick, 0L, newSignature.first, newSignature.second,
                TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION
            )
            tempoTrack.insertEvent(ts)
            // println("SIGNATURE #$index: tick = $tick  metro = ${newSignature.first}/${newSignature.second}")
            lastSignature = newSignature
        }
        tick += signatures[index].first
        index = ++index % signatures.size
    }
    return bars.toList()
}

fun alterateArticulation(
    ticks: IntArray, durations: IntArray,
    legatoAlterations: List<Float>, ribattutos: List<Int>, legatoDeltas: List<Long>,
    pivots: List<Int>, previousIsRest: BooleanArray, maxLegato: Int, changes: List<TickChangeData>
): Pair<IntArray, FloatArray> {
    if (durations.isEmpty()) return Pair(IntArray(0), FloatArray(0))
    val result = IntArray(durations.size)
    //val resultRibattutos = FloatArray(durations.size)

    var alterationIndex = 0
    var alterationTick = 0
    var durIndex = 0
    var legatoAlteration: Float
   // var ribattutoAlteration: Float
    var newDur: Int
    var nextDur: Int
    var thisDur: Int
    var legato: Int
    val notePivots = mutableListOf<Int>()
    var pivotIndex = 0
    var changeIndex = 0
    val changeNotes = if(changes.size == 1) listOf(0)
                    else changes.map{it.noteIndex}.drop(1)//.apply{println("changeNote: $this")}
    if (durations.isNotEmpty()) {
        while (durIndex < durations.size - 1) {
            while (alterationTick + legatoDeltas[alterationIndex] < ticks[durIndex]) {
                alterationTick += legatoDeltas[alterationIndex].toInt()
                alterationIndex++
            }
            if(alterationIndex >= pivots[pivotIndex]){
                notePivots.add(durIndex)
                pivotIndex++
            }
            legatoAlteration = legatoAlterations[alterationIndex]
           // ribattutoAlteration = ribattutosAlterations[alterationIndex]
            thisDur = durations[durIndex]
            if (legatoAlteration <= 1.0) {
                newDur = (thisDur * legatoAlteration).toInt()
                result[durIndex] = if (newDur < 12) 12 else newDur
             //   resultRibattutos[durIndex] = ribattutoAlteration
            } else {
                if (previousIsRest[durIndex + 1]) { // there is a rest between notes, legato is not requested
                    result[durIndex] = thisDur
                   // resultRibattutos[durIndex] = ribattutoAlteration
                } else if(changeIndex<changeNotes.size && durIndex + 1 == changeNotes[changeIndex]){ // no legato if the next notes has a program change on it
                    //println("Legato avoided on note $durIndex cause program change on the next one.")
                    result[durIndex] = thisDur
                    changeIndex++
                } else {
                    nextDur = durations[durIndex + 1]
                    legato = (nextDur * (legatoAlteration - 1f)).toInt()
                    result[durIndex] =
                        if (legato > maxLegato) thisDur + maxLegato else thisDur + legato
                  //  resultRibattutos[durIndex] = ribattutoAlteration
                }
            }
            durIndex++
        }
    }
    while (alterationTick + legatoDeltas[alterationIndex] < ticks[durIndex]) {
        alterationTick += legatoDeltas[alterationIndex].toInt()
        alterationIndex++
    }
    legatoAlteration = legatoAlterations[alterationIndex]
   // ribattutoAlteration = ribattutosAlterations[alterationIndex]
    thisDur = durations[durIndex]
    if (legatoAlteration <= 1.0) {
        newDur = (thisDur * legatoAlteration).toInt()
        result[durIndex] = if (newDur < 12) 12 else newDur
    } else {
        result[durIndex] = thisDur // last note doesn't need legato
    }

    //pivots.add(durations.size)
    //resultRibattutos[durIndex] = ribattutoAlteration

    notePivots.add(durations.size)
    val ribattutosAlterations: List<Float> = projectRibattutos(ribattutos.map{it.toFloat()}, notePivots)
//        println("Original durations: ${durations.contentToString()}")
//        result.also{ println("Alterate articulations: ${it.contentToString()}") }
//        ribattutosAlterations.also{ println("Alterate ribattutos: ${it}") }
//        println("Rounded ribattutos: ${ribattutosAlterations.map{it.roundToInt()}}")
//        println("notePivots: $notePivots")
    return Pair(result, ribattutosAlterations.toFloatArray())
}

fun projectRibattutos(ribattutos: List<Float>, notePivots: List<Int>): List<Float> {
    val result = mutableListOf<Float>()

    if(notePivots.size <= 1){ // just one section
        return List(notePivots.last()) {ribattutos[0]}
    }
    for(i in 0 until notePivots.size -1){
        val sectionSize = notePivots[i+1] - notePivots[i]
        //println("ribattutos in projection: $ribattutos")
        if(ribattutos[i] == ribattutos[i+1]){
            result.addAll(List(sectionSize){ribattutos[i]})//.apply{println("Rib section $i: $this")})
        } else {
            val startRibattuto = ribattutos[i]
            val step = (ribattutos[i+1] - startRibattuto) / sectionSize
            result.addAll( (0 until sectionSize).map{ startRibattuto + it * step})//.apply{println("Rib section $i: $this")})
        }
    }
    return result.toList()
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
 fun addAttackDelayToTrack(mt: MidiTrack, start: Long, channel: Int, attackDelay: Int){
    val attackAmount = Controller(start,channel,73, attackDelay)
    mt.insertEvent(attackAmount)
}
fun addVibratoToTrack(mt: MidiTrack, start: Long, duration: Long, channel: Int, vibratoDivisor: Int){
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
fun pitchBenderTest(mt: MidiTrack): MidiTrack {
    val pitches2m = listOf(61, 60, 59, 61, 60, 59)
    val noBend = 8192
    val ht = 4096
    val halfToneUp = noBend + ht
    val halfToneDown = noBend - ht
    val toneUp = noBend + ht * 2 - 1
    val toneDown = noBend + ht * 2
    val durs = listOf(480, 480, 480, 480, 480, 480)
    val glissAmounts = listOf(noBend, noBend, noBend, toneDown, toneDown, noBend)
    // var lastGliss = false
    val pitches = pitches2m
    var tick = 0L
    (0 until 3).forEach {
        val on = NoteOn(tick, 0, pitches[it], 100)
        val off = NoteOff(tick + durs[it] - 1, 0, pitches[it], 80)
        mt.insertEvent(on)
        mt.insertEvent(off)
        tick += durs[it]
    }
    (3 until 6).forEach {
        val pitchBendOff = PitchBend(tick, 0, 0, 0)
        pitchBendOff.bendAmount = noBend
        mt.insertEvent(pitchBendOff)

        if (it != 5) {
            //7168 6144 5120 4096
            val pitchBendOn1 = PitchBend(tick + 120, 0, 0, 0)
            pitchBendOn1.bendAmount = 7168
            val pitchBendOn2 = PitchBend(tick + 240, 0, 0, 0)
            pitchBendOn2.bendAmount = 6144
            val pitchBendOn3 = PitchBend(tick + 360, 0, 0, 0)
            pitchBendOn3.bendAmount = 5120
            val pitchBendOn4 = PitchBend(tick + 480 - 1, 0, 0, 0)
            pitchBendOn4.bendAmount = 4096
            mt.insertEvent(pitchBendOn1)
            mt.insertEvent(pitchBendOn2)
            mt.insertEvent(pitchBendOn3)
            mt.insertEvent(pitchBendOn4)
        }


        val on = NoteOn(tick, 0, pitches[it], 100)
        val off = NoteOff(tick + durs[it] - 1, 0, pitches[it], 80)
        mt.insertEvent(on)
        mt.insertEvent(off)
        tick += durs[it]
    }
    return mt
}
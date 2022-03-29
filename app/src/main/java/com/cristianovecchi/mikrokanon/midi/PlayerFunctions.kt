package com.cristianovecchi.mikrokanon.midi

import com.cristianovecchi.mikrokanon.AIMUSIC.RhythmPatterns
import com.cristianovecchi.mikrokanon.AIMUSIC.TrackData
import com.cristianovecchi.mikrokanon.alterateBpmWithDistribution
import com.cristianovecchi.mikrokanon.convertFlagsToInts
import com.leff.midi.MidiTrack
import com.leff.midi.event.*
import com.leff.midi.event.meta.TimeSignature


fun convertToMidiTrack(trackData: TrackData, nParts: Int): MidiTrack {
    val track = MidiTrack()
    val channel = trackData.channel
    val vibrato = trackData.vibrato
    val velocityOff = trackData.velocityOff
    val (pitches, ticks, durations, velocities, glissando) = trackData
    val articulationDurations = trackData.articulationDurations ?: durations

    // Instrument changes
    println()
    println("CHANNEL: $channel")
    var lastTick = -1L // avoid overriding
    trackData.changes.forEach{
        if(it.tick > lastTick){
            println(it)
            val pc: MidiEvent = ProgramChange(it.tick, channel, it.instrument) // cambia strumento
            track.insertEvent(pc)
            lastTick = it.tick
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
                track, tick, dur, channel, pitches[i],
                velocities[i], velocityOff, gliss
            )
            doubling.forEach {
                Player.insertDoublingNote(
                    track, tick, dur, channel, pitch + it,
                    velocity, velocityOff
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

fun setTimeSignatures(
    tempoTrack: MidiTrack,
    rhythm: List<Triple<RhythmPatterns, Boolean, Int>>,
    totalLength: Long
) {
    var tick = 0L
    var lastSignature = Pair(-1, -1)
    val signatures: List<Pair<Int, Pair<Int, Int>>> = rhythm.map {
        Pair(
            it.first.patternDuration() * it.third,
            Pair(it.first.metro.first, it.first.metro.second)
        )
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
}

fun alterateArticulation(
    ticks: IntArray, durations: IntArray,
    legatoAlterations: List<Float>, legatoDeltas: List<Long>,
    previousIsRest: BooleanArray, maxLegato: Int
): IntArray {
    if (durations.isEmpty()) return IntArray(0)
    val result = IntArray(durations.size)
    var alterationIndex = 0
    var alterationTick = 0
    var durIndex = 0
    var legatoAlteration: Float
    var newDur: Int
    var nextDur: Int
    var thisDur: Int
    var legato: Int
    if (durations.size > 1) {
        while (durIndex < durations.size - 1) {
            while (alterationTick + legatoDeltas[alterationIndex] < ticks[durIndex]) {
                alterationTick += legatoDeltas[alterationIndex].toInt()
                alterationIndex++
            }
            legatoAlteration = legatoAlterations[alterationIndex]
            thisDur = durations[durIndex]
            if (legatoAlteration <= 1.0) {
                newDur = (thisDur * legatoAlteration).toInt()
                result[durIndex] = if (newDur < 12) 12 else newDur
            } else {
                if (previousIsRest[durIndex + 1]) { // there is a rest between notes, legato is not requested
                    result[durIndex] = thisDur
                } else {
                    nextDur = durations[durIndex + 1]
                    legato = (nextDur * (legatoAlteration - 1f)).toInt()
                    result[durIndex] =
                        if (legato > maxLegato) thisDur + maxLegato else thisDur + legato
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
    thisDur = durations[durIndex]
    if (legatoAlteration <= 1.0) {
        newDur = (thisDur * legatoAlteration).toInt()
        result[durIndex] = if (newDur < 12) 12 else newDur
    } else {
        result[durIndex] = thisDur // last note doesn't need legato
    }
//        println("Original durations: ${durations.contentToString()}")
//        result.also{ println("Alterate articulations: ${it.contentToString()}") }
    return result
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
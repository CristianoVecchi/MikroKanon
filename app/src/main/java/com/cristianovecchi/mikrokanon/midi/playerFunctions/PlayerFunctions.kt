package com.cristianovecchi.mikrokanon.midi.playerFunctions

import com.cristianovecchi.mikrokanon.*
import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.midi.Player
import com.leff.midi.MidiTrack
import com.leff.midi.event.*




fun assignDodecaBytesToBars(bars: Array<Bar>, counterpointTrackData: List<TrackData>, withArticulation: Boolean = false) {
    bars.forEach { it.dodecaByte1stHalf = 0 ; it.dodecaByte2ndHalf = 0 ; it.minVelocity = 80}
    //println("nBars: ${bars.size}")
    val indices = counterpointTrackData.map{ it.ticks }.findIndicesInSection(bars.first().tick.toInt(),
        bars.sumOf { it.duration .toInt()}, counterpointTrackData.map {it.durations} )
    counterpointTrackData.forEachIndexed{ trackIndex, trackData ->
        val durations = if(trackData.articulationDurations != null && withArticulation) trackData.articulationDurations!! else trackData.durations
        var barIndex = 0

        val pitchIndices = indices[trackIndex]

        pitchIndices?.let {
            var pitchIndex = it.first
            while(pitchIndex <= it.last) {
                val pitch = trackData.pitches[pitchIndex]
                val velocity = trackData.velocities[pitchIndex]
                val pitchStart = trackData.ticks[pitchIndex]
                val pitchEnd = pitchStart + durations[pitchIndex]
                val bar = bars.getOrNull(barIndex)
                if(bar == null) {
                    break
                } else {
                    val barEnd = bar.tick + bar.duration
                    //println("pitch#$pitchIndex $pitchStart-$pitchEnd -> bar#$barIndex ${bar.tick}-$barEnd ${bar.metro} dur:${bar.duration}")
                    if(trackData.ticks[pitchIndex] < barEnd ){
                        //println("pitch#$pitchIndex $pitchStart-$pitchEnd  -> bar#$barIndex ${bar.tick}-$barEnd ${bar.metro} dur:${bar.duration}")
                        bar.dodecaByte1stHalf = bar.dodecaByte1stHalf?.or((1 shl (pitch % 12)))//.also{println("dByte: $it")}
                        if(velocity < bar.minVelocity!! ) bar.minVelocity = velocity
                        if(pitchEnd > barEnd) barIndex++ else pitchIndex++

                    } else {
                        barIndex++
                    }
                }
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
fun TrackData.convertToMidiTrack(nParts: Int, addAttack: Boolean = false): MidiTrack {
    val track = MidiTrack()
    val channel = this.channel
    val vibratos = this.vibratos ?: IntArray(pitches.size) { 0 }
    val velocityOff = this.velocityOff
    val (pitches, ticks, durations, velocities, glissando) = this
    val articulationDurations = this.articulationDurations ?: durations
    val ribattutos = this.ribattutos ?: IntArray(pitches.size){ 1 }

    // Instrument changes
//    println()
//    println("CHANNEL: $channel")
    var lastTick = -1L // avoid overriding
   // var noteIndex = -1
    //printNoteLimits(ticks, articulationDurations)
    if(addAttack) this.addAttackToMidiTrack(track)

    this.changes.forEach{
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
    if (!this.audio8D) { // set a fixed pan if 8D AUDIO is not set on this track
        val pan = Controller(0, channel, 10, pans[this.partIndex])
        track.insertEvent(pan)
    }

//        var lastIsGliss = false
//        var attackIsDelayed = false
    if (this.doublingFlags == 0) {
        for (i in pitches.indices) {
            val tick = ticks[i].toLong()
            val gliss = glissando[i]
            val duration = durations[i]
            val articulationDuration = articulationDurations[i]
            val ribattuto = ribattutos[i]
            val vibrato = vibratos[i]
            val overLegato = articulationDuration > duration
//                val attackDelay = if(lastIsGliss && (articulationDuration == duration || overLegato)) 127 else 0
            val dur = if(overLegato && (glissando.getOrElse(i) { 0 } >0 || gliss >0)  )
                duration.toLong() else articulationDuration.toLong()
            //println("note $i attack: $attackDelay")
            if (vibrato != 0) {
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
        val doubling = convertFlagsToInts(this.doublingFlags)
        for (i in pitches.indices) {
            val tick = ticks[i].toLong()
            val gliss = glissando[i]
            val duration = durations[i]
            val articulationDuration = articulationDurations[i]
            val ribattuto = ribattutos[i]
            val vibrato = vibratos[i]
            val overLegato = articulationDuration > duration
//                val attackDelay = if(lastIsGliss && (articulationDuration == duration || overLegato)) 127 else 0
            val dur = if(overLegato && (glissando.getOrElse(i) { 0 } >0 || gliss >0)  )
                duration.toLong() else articulationDuration.toLong()
            val pitch = pitches[i]
            val velocity = velocities[i]
            if (vibrato != 0) {
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
    if(this.audio8D && track.lengthInTicks > 0) {
        val nRevolutions = (12 - this.partIndex) * 2
        setAudio8D(track, nRevolutions, channel)
    }
    return track//.apply { this.dumpEvents() }
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

fun octaveTest(mt: MidiTrack): MidiTrack {
    val pitches = listOf(60,72,83, 84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,96,84,72,60, 55, 48,36,24,23,22,21)
    //val pitches = listOf(60,72,84,60, 55, 48,36,24,23,22,21)
    var tick = 0L
    pitches.forEach {
        val pc: MidiEvent = ProgramChange(tick, 0, SYN_CHARANG) // cambia strumento
        mt.insertEvent(pc)
        val on = NoteOn(tick, 0, it, 100)
        val off = NoteOff(tick + 480 - 1, 0, it, 80)
        mt.insertEvent(on)
        mt.insertEvent(off)
        tick += 480
    }
    return mt
}
//fun pitchBenderTest(mt: MidiTrack): MidiTrack {
//    val pitches2m = listOf(61, 60, 59, 61, 60, 59)
//    val noBend = 8192
//    val ht = 4096
//    val halfToneUp = noBend + ht
//    val halfToneDown = noBend - ht
//    val toneUp = noBend + ht * 2 - 1
//    val toneDown = noBend + ht * 2
//    val durs = listOf(480, 480, 480, 480, 480, 480)
//    val glissAmounts = listOf(noBend, noBend, noBend, toneDown, toneDown, noBend)
//    // var lastGliss = false
//    val pitches = pitches2m
//    var tick = 0L
//    (0 until 3).forEach {
//        val on = NoteOn(tick, 0, pitches[it], 100)
//        val off = NoteOff(tick + durs[it] - 1, 0, pitches[it], 80)
//        mt.insertEvent(on)
//        mt.insertEvent(off)
//        tick += durs[it]
//    }
//    (3 until 6).forEach {
//        val pitchBendOff = PitchBend(tick, 0, 0, 0)
//        pitchBendOff.bendAmount = noBend
//        mt.insertEvent(pitchBendOff)
//
//        if (it != 5) {
//            //7168 6144 5120 4096
//            val pitchBendOn1 = PitchBend(tick + 120, 0, 0, 0)
//            pitchBendOn1.bendAmount = 7168
//            val pitchBendOn2 = PitchBend(tick + 240, 0, 0, 0)
//            pitchBendOn2.bendAmount = 6144
//            val pitchBendOn3 = PitchBend(tick + 360, 0, 0, 0)
//            pitchBendOn3.bendAmount = 5120
//            val pitchBendOn4 = PitchBend(tick + 480 - 1, 0, 0, 0)
//            pitchBendOn4.bendAmount = 4096
//            mt.insertEvent(pitchBendOn1)
//            mt.insertEvent(pitchBendOn2)
//            mt.insertEvent(pitchBendOn3)
//            mt.insertEvent(pitchBendOn4)
//        }
//
//
//        val on = NoteOn(tick, 0, pitches[it], 100)
//        val off = NoteOff(tick + durs[it] - 1, 0, pitches[it], 80)
//        mt.insertEvent(on)
//        mt.insertEvent(off)
//        tick += durs[it]
//    }
//    return mt
//}
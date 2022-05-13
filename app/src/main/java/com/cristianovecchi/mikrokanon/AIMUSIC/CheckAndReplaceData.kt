package com.cristianovecchi.mikrokanon.AIMUSIC

import com.cristianovecchi.mikrokanon.divideDistributingRest

enum class CheckType(val title: String) {
    NONE("No action"), LONGER(">=")//, ALONE("].[ >=")
}
enum class ReplaceType(val title: String) {
   MORDENTE("Mordente"), MORDENTE_2X("Mordente 2x"),
    MORDENTE_3X("Mordente 3x"), GRUPPETTO("Gruppetto"), TRILLO("Trillo")
}
data class CheckAndReplaceData(val check: CheckType = CheckType.NONE,
                               val checkValues: List<Int> = emptyList(),
                               val replace: ReplaceType = ReplaceType.MORDENTE,
                               val replaceValues: List<Int> = emptyList())

fun provideCheckFunction(checkType: CheckType, checkValues: List<Int>): (TrackData, Int, List<TrackData>) -> Boolean {
    return when(checkType){
        CheckType.NONE -> { _, _, _ -> false }
        CheckType.LONGER -> { trackData, index, trackDataList -> trackData.durations[index] >= checkValues[0] }
        //CheckType.ALONE -> TODO()
    }
}

fun provideReplaceFunction(replaceType: ReplaceType, replaceValues: List<Int>):
            (TrackData, Int, List<TrackData>) -> SubstitutionNotes {
    return when(replaceType){
        ReplaceType.TRILLO -> { trackData, index, trackDataList ->
            val (pitch, tick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto)
            = trackData.extractNoteDataAtIndex(index)
            // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4
            val actualDuration = articulationDuration ?: duration
            val div = when(actualDuration){
                in (0..119) -> -1
                in (120..239) -> 3
                in (240..359) -> 5 // 34 - 42
                in (360..479) -> 7 // 32 - 38
                in (480..Int.MAX_VALUE) -> actualDuration / 50
                else -> -1
            }
            if (div == -1) { // fake substitution
                println("No Subs: actual duration = $actualDuration")
                SubstitutionNotes(-1) // will not be considered
            } else {
                val durs = actualDuration.toLong().divideDistributingRest(div)
                val stress = replaceValues[0]
                val stressedVelocity = if (velocity + stress > 127) 127 else velocity + stress
                val nextPitch = trackData.pitches.getOrElse(index + 1) { pitch }
                val secondPitch = when {
                    pitch == 108 -> pitch - 1
                    pitch == 21 -> pitch + 1
                    pitch <= nextPitch -> pitch - 1
                    pitch > nextPitch -> pitch + 1
                    else -> pitch - 1
                }
                var lastTick = tick
                val ticks = (0 until div-1).map{
                    lastTick += durs[it].toInt()
                    lastTick
                }
                SubstitutionNotes(
                    index,
                    (0 until div).map { if(it % 2 == 0) secondPitch else pitch },
                    listOf(tick, *ticks.toTypedArray()),
                    durs.map{ it.toInt()},
                    listOf(stressedVelocity, *List(div-1){velocity}.toTypedArray()),
                    List(div){glissando},
                    List(div){attack},
                    listOf(isPreviousRest, *List(div-1){false}.toTypedArray()),
                    if (articulationDuration == null) null else durs.map{it.toInt()},
                    if (ribattuto == null) null else List(div){ribattuto}
                )
                    .apply { println("duration: $duration, artDur: $articulationDuration $this") }
            }
        }
        ReplaceType.MORDENTE -> { trackData, index, trackDataList ->
            val (pitch, tick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto) = trackData.extractNoteDataAtIndex(
                index
            )
            // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4

            val actualDuration = articulationDuration ?: duration
            val dur = if (60 * 4 > actualDuration) actualDuration / 4 else 60
            if (dur < 12) { // fake substitution
                println("No Subs: actual duration = $actualDuration")
                SubstitutionNotes(-1) // will not be considered
            } else {
                val stress = replaceValues[0]
                val stressedVelocity = if (velocity + stress > 127) 127 else velocity + stress
                val nextPitch = trackData.pitches.getOrElse(index + 1) { pitch }
                val secondPitch = when {
                    pitch == 108 -> pitch - 1
                    pitch == 21 -> pitch + 1
                    pitch <= nextPitch -> pitch - 1
                    pitch > nextPitch -> pitch + 1
                    else -> pitch - 1
                }
                SubstitutionNotes(
                    index,
                    listOf(pitch, secondPitch, pitch),
                    listOf(tick, tick + dur, tick + dur * 2),
                    listOf(dur, dur, duration - dur * 2),
                    listOf(stressedVelocity, velocity, velocity),
                    listOf(glissando, glissando, glissando),
                    listOf(attack, attack, attack,),
                    listOf(isPreviousRest, false, false),
                    if (articulationDuration == null) null else listOf(
                        dur,
                        dur,
                        articulationDuration - dur * 2
                    ),
                    if (ribattuto == null) null else listOf(ribattuto, ribattuto, ribattuto)
                )
                    .apply { println("duration: $duration, artDur: $articulationDuration $this") }
            }
        }
            ReplaceType.MORDENTE_3X -> { trackData, index, trackDataList ->
                val (pitch, thick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto) = trackData.extractNoteDataAtIndex(index)
                // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4

                val actualDuration = articulationDuration ?: duration
                val dur = if(60 * 12 > actualDuration ) actualDuration / 12 else  60
                if(dur < 12) { // fake substitution
                    println("No Subs: actual duration = $actualDuration")
                    SubstitutionNotes(-1 ) // will not be considered
                } else {
                    val stress = replaceValues[0]
                    val stressedVelocity = if(velocity + stress > 127) 127 else velocity + stress
                    val nextPitch = trackData.pitches.getOrElse(index+1) { pitch }
                    val secondPitch = when{
                        pitch == 108 -> pitch - 1
                        pitch == 21 -> pitch + 1
                        pitch <= nextPitch -> pitch -1
                        pitch > nextPitch -> pitch +1
                        else -> pitch - 1
                    }
                    SubstitutionNotes(index, listOf(pitch, secondPitch, pitch, secondPitch, pitch, secondPitch, pitch),
                        listOf(thick, thick + dur, thick + dur * 2, thick + dur * 3, thick + dur * 4, thick + dur * 5, thick + dur * 6 ),
                        listOf(dur, dur, dur, dur, dur, dur, duration - dur * 6),
                        listOf(stressedVelocity, velocity, velocity, velocity, velocity, velocity, velocity),
                        listOf(glissando, glissando, glissando, glissando, glissando, glissando, glissando),
                    listOf(attack, attack, attack, attack, attack, attack, attack),
                    listOf(isPreviousRest, false, false, false, false, false, false),
                    if(articulationDuration == null) null else listOf(dur,dur, dur,dur, dur,dur, articulationDuration - dur * 4),
                    if(ribattuto == null) null else listOf(ribattuto, ribattuto, ribattuto, ribattuto, ribattuto, ribattuto, ribattuto) )
                    .apply { println("duration: $duration, artDur: $articulationDuration $this") }
                }
            }
        ReplaceType.MORDENTE_2X -> { trackData, index, trackDataList ->
            val (pitch, thick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto) = trackData.extractNoteDataAtIndex(index)
            // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4

            val actualDuration = articulationDuration ?: duration
            val dur = if(60 * 8 > actualDuration ) actualDuration / 8 else  60
            if(dur < 12) { // fake substitution
                println("No Subs: actual duration = $actualDuration")
                SubstitutionNotes(-1 ) // will not be considered
            } else {
                val stress = replaceValues[0]
                val stressedVelocity = if(velocity + stress > 127) 127 else velocity + stress
                val nextPitch = trackData.pitches.getOrElse(index+1) { pitch }
                val secondPitch = when{
                    pitch == 108 -> pitch - 1
                    pitch == 21 -> pitch + 1
                    pitch <= nextPitch -> pitch -1
                    pitch > nextPitch -> pitch +1
                    else -> pitch - 1
                }
                SubstitutionNotes(index, listOf(pitch, secondPitch, pitch, secondPitch, pitch),
                    listOf(thick, thick + dur, thick + dur * 2, thick + dur * 3, thick + dur * 4 ),
                    listOf(dur, dur, dur, dur, duration - dur * 4),
                    listOf(stressedVelocity, velocity, velocity, velocity, velocity),
                    listOf(glissando, glissando, glissando, glissando, glissando),
                    listOf(attack, attack, attack, attack, attack),
                    listOf(isPreviousRest, false, false, false, false),
                    if(articulationDuration == null) null else listOf(dur,dur, dur,dur, articulationDuration - dur * 4),
                    if(ribattuto == null) null else listOf(ribattuto,ribattuto,ribattuto, ribattuto, ribattuto) )
                    .apply { println("duration: $duration, artDur: $articulationDuration $this") }
            }
        }
        ReplaceType.GRUPPETTO -> { trackData, index, trackDataList ->
            val (pitch, thick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto) = trackData.extractNoteDataAtIndex(index)
            // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4

            val actualDuration = articulationDuration ?: duration + 1
            val dur = if(60 * 8 > actualDuration ) actualDuration / 8 else  60
            if(dur < 12 || pitch == 108 || pitch == 21) { // fake substitution
                println("No Subs: actual duration = $actualDuration")
                SubstitutionNotes(-1 ) // will not be considered
            } else {
                val stress = replaceValues[0]
                val stressedVelocity = if(velocity + stress > 127) 127 else velocity + stress
                val nextPitch = trackData.pitches.getOrElse(index+1) { pitch }
                val (secondPitch, fourthPitch) = when {
                    pitch <= nextPitch -> Pair(pitch+1, pitch -1)
                    pitch > nextPitch -> Pair(pitch-1, pitch +1)
                    else -> Pair(pitch+1, pitch -1)
                }
                SubstitutionNotes(index, listOf(pitch, secondPitch, pitch, fourthPitch, pitch),
                    listOf(thick, thick + dur, thick + dur * 2, thick + dur * 3, thick + dur * 4 ),
                    listOf(dur, dur, dur, dur, duration - dur * 4),
                    listOf(stressedVelocity, velocity, velocity, velocity, velocity),
                    listOf(glissando, glissando, glissando, glissando, glissando),
                    listOf(attack, attack, attack, attack, attack),
                    listOf(isPreviousRest, false, false, false, false),
                    if(articulationDuration == null) null else listOf(dur,dur, dur,dur, articulationDuration - dur * 4),
                    if(ribattuto == null) null else listOf(ribattuto,ribattuto,ribattuto, ribattuto, ribattuto) )
                    .apply { println("duration: $duration, artDur: $articulationDuration $this") }
            }
        }

    }
}
package com.cristianovecchi.mikrokanon.AIMUSIC

import com.cristianovecchi.mikrokanon.divideDistributingRest
import kotlin.math.absoluteValue


sealed class CheckType(open val title: String = "") {
    fun toCsv(): String {
        return when (this){
            is None -> "0"
            is EqualOrGreater -> "1#$limit"
        }
    }
    fun describe(): String {
        return when (this){
            is None -> this.title
            is EqualOrGreater -> if(this.limit == 0) "ALL" else "${this.title}${this.limit}"
        }
    }
    data class None(override val title: String = "No action"): CheckType()
    data class EqualOrGreater(override val title: String = ">=", val limit: Int = 240) : CheckType()
    //, ALONE("].[ >=")
    companion object {
        fun provideCheckType(index: Int, limit: Int = 240): CheckType{
            return when (index) {
                0 -> None()
                1 -> EqualOrGreater(limit = limit)
                else -> None()
            }
        }
        fun checkList(): List<CheckType>{
            return listOf( None(),
                EqualOrGreater(limit = 0),EqualOrGreater(limit = 120),
                EqualOrGreater(limit = 240), EqualOrGreater(limit = 480), )
        }
        fun getIndex(check: CheckType): Int {
            return when(check){
                is None -> 0
                is EqualOrGreater -> when(check.limit){
                    0 -> 1
                    120 -> 2
                    240 -> 3
                    480 -> 4
                    else -> 0
                }
            }
        }
    }
}
sealed class ReplaceType(open val title: String = "", open val stress: Int = 0) {
    fun clone(stress: Int): ReplaceType{
        return when (this){
            is Trillo -> this.copy(stress = stress)
            is Mordente -> this.copy(stress = stress)
            is Mordente2x -> this.copy(stress = stress)
            is Mordente3x -> this.copy(stress = stress)
            is Gruppetto -> this.copy(stress = stress)
            is Onda -> this.copy(stress = stress)
            is Cromatica -> this.copy(stress = stress)
            is Diatonica -> this.copy(stress = stress)
            is GruppettoRetr -> this.copy(stress = stress)
            is Fantasia -> this.copy(stress = stress)
        }
    }
    fun toCsv(): String {
        return when (this){
            is Trillo -> "0#$stress"
            is Mordente -> "1#$stress"
            is Mordente2x -> "2#$stress"
            is Mordente3x -> "3#$stress"
            is Gruppetto -> "4#$stress"
            is Onda -> "5#$stress"
            is Cromatica -> "6#$stress"
            is Diatonica -> "7#$stress"
            is GruppettoRetr -> "8#$stress"
            is Fantasia -> "9#$stress"
        }
    }
    companion object{
        val titles: List<String> = listOf( "Trillo", "Mordente", "Mordente 2X",
        "Mordente 3x", "Gruppetto", "Onda", "Cromatica", "Diatonica", "←Gruppetto", "Fantasia" )
        fun provideReplaceType(index: Int, stress: Int = 16): ReplaceType{
            return when (index) {
                0 -> Trillo(stress = stress)
                1 -> Mordente(stress = stress)
                2 -> Mordente2x(stress = stress)
                3 -> Mordente3x(stress = stress)
                4 -> Gruppetto(stress = stress)
                5 -> Onda(stress = stress)
                6 -> Cromatica(stress = stress)
                7 -> Diatonica(stress = stress)
                8 -> GruppettoRetr(stress = stress)
                9 -> Fantasia(stress = stress)
                else -> Trillo(stress = stress)
            }
        }
    }
    data class Trillo(override val title: String = "Trillo", override val stress: Int = 16): ReplaceType()
    data class Mordente(override val title: String = "Mordente", override val stress: Int = 16): ReplaceType()
    data class Mordente2x(override val title: String = "Mordente 2x", override val stress: Int = 16): ReplaceType()
    data class Mordente3x(override val title: String = "Mordente 3x", override val stress: Int = 16): ReplaceType()
    data class Gruppetto(override val title: String = "Gruppetto", override val stress: Int = 16): ReplaceType()
    data class Onda(override val title: String = "Onda", override val stress: Int = 16): ReplaceType()
    data class Cromatica(override val title: String = "Cromatica", override val stress: Int = 16): ReplaceType()
    data class Diatonica(override val title: String = "Diatonica", override val stress: Int = 16): ReplaceType()
    data class GruppettoRetr(override val title: String = "←Gruppetto", override val stress: Int = 16): ReplaceType()
    data class Fantasia(override val title: String = "Fantasia", override val stress: Int = 16): ReplaceType()
}
data class CheckAndReplaceData(val check: CheckType = CheckType.None(),
                               val replace: ReplaceType = ReplaceType.Mordente(),){
    fun describe(): String {
        return when(check){
            is CheckType.None -> check.describe()
            is CheckType.EqualOrGreater -> "${check.describe()} ${replace.title} ^${replace.stress}"
        }
    }
    fun toCsv(): String {
        return "${this.check.toCsv()}|${this.replace.toCsv()}"
    }
    companion object{
        fun createCheckAndReplaceDatasFromCsv(csv: String): List<CheckAndReplaceData>{
            if(csv.isBlank()) return listOf()
            val values = csv.split(",")
            return values.map{ createCheckAndReplaceDataFromCsv(it) }
        }
        fun createCheckAndReplaceDataFromCsv(csv: String): CheckAndReplaceData{
            if(csv.isBlank()) return CheckAndReplaceData()
            val (checkCsv, replaceCsv) = csv.split("|")
            val checkValues = checkCsv.split("#")
            val replaceValues = replaceCsv.split("#")
            val limit = if(checkValues.size>1) checkValues[1].toInt() else 0
            val check = CheckType.provideCheckType(checkValues[0].toInt(), limit)
            val stress = if(replaceValues.size>1) replaceValues[1].toInt() else 0
            val replace = ReplaceType.provideReplaceType(replaceValues[0].toInt(), stress)
            return CheckAndReplaceData(check, replace)
        }
    }
}



fun provideCheckFunction(checkType: CheckType): (TrackData, Int, List<TrackData>) -> Boolean {
    return when(checkType){
        is CheckType.None -> { _, _, _ -> false }
        is CheckType.EqualOrGreater -> { trackData, index, trackDataList -> trackData.durations[index] >= checkType.limit }
        //CheckType.ALONE -> TODO()
    }
}
fun provideAvailableReplaceFunction(stress: Int): List<ReplaceType>{
    return listOf(ReplaceType.Trillo(stress = stress), ReplaceType.Mordente(stress = stress),
        ReplaceType.Mordente2x(stress = stress), ReplaceType.Mordente3x(stress = stress),
        ReplaceType.Gruppetto(stress = stress), ReplaceType.Onda(stress = stress),
        ReplaceType.Cromatica(stress = stress), ReplaceType.Diatonica(stress = stress),
        ReplaceType.GruppettoRetr(stress = stress)
    )
}

fun provideReplaceFunction(replaceType: ReplaceType):
            (TrackData, Int, List<TrackData>) -> SubstitutionNotes {
    return when(replaceType){
        is ReplaceType.Fantasia -> { trackData, index, trackDataList ->
            SubstitutionNotes(-1)
        }
        is ReplaceType.Cromatica -> { trackData, index, trackDataList ->
            val (pitch, tick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto)
                    = trackData.extractNoteDataAtIndex(index)
            // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4
            val actualDuration = articulationDuration ?: duration
            val nextPitch = trackData.pitches.getOrElse(index + 1) { pitch }
            val isNextRest = trackData.isPreviousRest.getOrElse(index + 1) { true }
            val nNotes = (pitch - nextPitch).absoluteValue
            if(isNextRest || nextPitch == pitch || actualDuration < duration || nNotes == 1){
                SubstitutionNotes(-1)
            } else {

                val direction = if(pitch < nextPitch) 1 else -1
                val halfDuration = duration / 2
                var scaleDurs = halfDuration.toLong().divideDistributingRest(nNotes)
                if(scaleDurs[0] < 4){
                    SubstitutionNotes(-1)
                } else {
                    scaleDurs = if(scaleDurs[0] > 60) MutableList(scaleDurs.size){60} else scaleDurs
                    val diffArticolation = actualDuration - duration
                    val longDur = duration - scaleDurs.sum() + scaleDurs[0]
                    val durs = listOf(longDur, *scaleDurs.drop(1).dropLast(1).toTypedArray(), scaleDurs.last() + diffArticolation).map{ it.toInt()}
                    var lastTick = tick
                    val ticks = (0 until nNotes-1).map{
                        lastTick += durs[it]
                        lastTick
                    }
                    val pitches = (0 until nNotes).map{ pitch + it * direction}
                    val stress = replaceType.stress
                    val stressedVelocity = if (velocity + stress > 127) 127 else velocity + stress
                    SubstitutionNotes(
                        index, pitches,
                        listOf(tick, *ticks.toTypedArray()),
                        listOf(longDur, *scaleDurs.drop(1).toTypedArray()).map{ it.toInt()},
                        listOf(stressedVelocity, *List(nNotes-1){velocity}.toTypedArray()),
                        List(nNotes){glissando},
                        List(nNotes){attack},
                        listOf(isPreviousRest, *List(nNotes-1){false}.toTypedArray()),
                        if (articulationDuration == null) null else durs.map{it},
                        if (ribattuto == null) null else List(nNotes){ribattuto}
                    )
                }
            }
        }
        is ReplaceType.Diatonica -> { trackData, index, trackDataList ->
            val (pitch, tick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto)
                    = trackData.extractNoteDataAtIndex(index)
            // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4
            val actualDuration = articulationDuration ?: duration
            val nextPitch = trackData.pitches.getOrElse(index + 1) { pitch }
            val isNextRest = trackData.isPreviousRest.getOrElse(index + 1) { true }
            val nHalfTones = (pitch - nextPitch).absoluteValue
            if(isNextRest || nextPitch == pitch || actualDuration < duration || nHalfTones == 1 || nHalfTones == 2){
                SubstitutionNotes(-1)
            } else {
                val nNotes = nHalfTones / 2 + nHalfTones % 2
                val direction = if(pitch < nextPitch) 1 else -1
                val halfDuration = duration / 2
                var scaleDurs = halfDuration.toLong().divideDistributingRest(nNotes)
                if(scaleDurs[0] < 4){
                    SubstitutionNotes(-1)
                } else {
                    scaleDurs = if(scaleDurs[0] > 60) MutableList(scaleDurs.size){60} else scaleDurs
                    val diffArticolation = actualDuration - duration
                    val longDur = duration - scaleDurs.sum() + scaleDurs[0]
                    val durs = listOf(longDur, *scaleDurs.drop(1).dropLast(1).toTypedArray(), scaleDurs.last() + diffArticolation).map{ it.toInt()}
                    var lastTick = tick
                    val ticks = (0 until nNotes-1).map{
                        lastTick += durs[it]
                        lastTick
                    }
                    val pitches = if(nHalfTones/2 == 0) (0 until nNotes).map{ pitch + it * direction * 2}
                    else listOf(pitch, *(0 until nNotes - 1).map{ pitch + direction + it * direction * 2}.toTypedArray())
                    val stress = replaceType.stress
                    val stressedVelocity = if (velocity + stress > 127) 127 else velocity + stress
                    SubstitutionNotes(
                        index, pitches,
                        listOf(tick, *ticks.toTypedArray()),
                        listOf(longDur, *scaleDurs.drop(1).toTypedArray()).map{ it.toInt()},
                        listOf(stressedVelocity, *List(nNotes-1){velocity}.toTypedArray()),
                        List(nNotes){glissando},
                        List(nNotes){attack},
                        listOf(isPreviousRest, *List(nNotes-1){false}.toTypedArray()),
                        if (articulationDuration == null) null else durs,
                        if (ribattuto == null) null else List(nNotes){ribattuto}
                    )
                }
            }
        }
        is ReplaceType.Trillo -> { trackData, index, trackDataList ->
            val (pitch, tick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto)
            = trackData.extractNoteDataAtIndex(index)
            // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4
            val actualDuration = articulationDuration ?: duration
            val div = when(actualDuration){
                in (0..119) -> -1
                in (120..239) -> 3
                in (240..359) -> 5 // 34 - 42
                in (360..479) -> 7 // 32 - 38
                in (480..Int.MAX_VALUE) -> actualDuration / 43
                else -> -1
            }
            if (div == -1) { // fake substitution
                //println("TRILLO:$index No Subs: actual duration = $actualDuration")
                SubstitutionNotes(-1) // will not be considered
            } else {
                val durs = actualDuration.toLong().divideDistributingRest(div)
                val stress = replaceType.stress
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
                  // .apply { println("TRILLO:$index duration: $duration, artDur: $articulationDuration $this") }
            }
        }
        is ReplaceType.Mordente -> { trackData, index, trackDataList ->
            val (pitch, tick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto) = trackData.extractNoteDataAtIndex(
                index
            )
            // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4

            val actualDuration = articulationDuration ?: duration
            val dur = if (60 * 4 > actualDuration) actualDuration / 4 else 60
            if (dur < 12) { // fake substitution
               // println("MORDENTE:$index No Subs: actual duration = $actualDuration")
                SubstitutionNotes(-1) // will not be considered
            } else {
                val stress = replaceType.stress
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
                    listOf(attack, attack, attack),
                    listOf(isPreviousRest, false, false),
                    if (articulationDuration == null) null else listOf(dur, dur, articulationDuration - dur * 2),
                    if (ribattuto == null) null else listOf(ribattuto, ribattuto, ribattuto)
                )
                 //   .apply { println("MORDENTE:$index duration: $duration, artDur: $articulationDuration $this") }
            }
        }
           is ReplaceType.Mordente3x -> { trackData, index, trackDataList ->
                val (pitch, thick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto) = trackData.extractNoteDataAtIndex(index)
                // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4

                val actualDuration = articulationDuration ?: duration
                val dur = if(60 * 12 > actualDuration ) actualDuration / 12 else  60
                if(dur < 12) { // fake substitution
                  //  println("MORDENTE_3X:$index No Subs: actual duration = $actualDuration")
                    SubstitutionNotes(-1 ) // will not be considered
                } else {
                    val stress = replaceType.stress
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
                    if(articulationDuration == null) null else listOf(dur,dur, dur,dur, dur,dur, articulationDuration - dur * 6),
                    if(ribattuto == null) null else listOf(ribattuto, ribattuto, ribattuto, ribattuto, ribattuto, ribattuto, ribattuto) )
                   // .apply { println("MORDENTE 3X:$index duration: $duration, artDur: $articulationDuration $this") }
                }
            }
       is ReplaceType.Mordente2x -> { trackData, index, trackDataList ->
            val (pitch, thick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto) = trackData.extractNoteDataAtIndex(index)
            // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4

            val actualDuration = articulationDuration ?: duration
            val dur = if(60 * 8 > actualDuration ) actualDuration / 8 else  60
            if(dur < 12) { // fake substitution
                //println("MORDENTE 2X:$index actual duration = $actualDuration")
                SubstitutionNotes(-1 ) // will not be considered
            } else {
                val stress = replaceType.stress
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
                //    .apply { println("MORDENTE 2X:$index duration: $duration, artDur: $articulationDuration $this") }
            }
        }
        is ReplaceType.Gruppetto -> { trackData, index, trackDataList ->
            val (pitch, thick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto) = trackData.extractNoteDataAtIndex(index)
            // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4

            val actualDuration = articulationDuration ?: duration
            val dur = if(60 * 8 > actualDuration ) actualDuration / 8 else  60
            if(dur < 12 || pitch == 108 || pitch == 21) { // fake substitution
                //println("GRUPPETTO:$index No Subs: actual duration = $actualDuration")
                SubstitutionNotes(-1 ) // will not be considered
            } else {
                val stress = replaceType.stress
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
                  //  .apply { println("GRUPPETTO:$index duration: $duration, artDur: $articulationDuration $this") }
            }
        }
        is ReplaceType.GruppettoRetr -> { trackData, index, trackDataList ->
            val (pitch, tick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto) = trackData.extractNoteDataAtIndex(index)
            // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4

            val actualDuration = articulationDuration ?: duration + 1
            val dur = if(60 * 8 > actualDuration ) actualDuration / 8 else  60
            val isNextRest = trackData.isPreviousRest.getOrElse(index + 1) { true }
            val nextPitch = trackData.pitches.getOrElse(index+1) { pitch }
            if(nextPitch == pitch || actualDuration < duration || isNextRest || dur < 12 || pitch == 108 || pitch == 21) { // fake substitution
                //println("GRUPPETTO:$index No Subs: actual duration = $actualDuration")
                SubstitutionNotes(-1 ) // will not be considered
            } else {
                val stress = replaceType.stress
                val stressedVelocity = if(velocity + stress > 127) 127 else velocity + stress

                val (secondPitch, fourthPitch) = when {
                    pitch <= nextPitch -> Pair(pitch+1, pitch -1)
                    pitch > nextPitch -> Pair(pitch-1, pitch +1)
                    else -> Pair(pitch+1, pitch -1)
                }
                val diffArticulation = actualDuration - duration
                val longDuration = duration - dur * 4
                val startGruppettoTick = tick + longDuration
                SubstitutionNotes(index, listOf(pitch, secondPitch, pitch, fourthPitch, pitch),
                    listOf(tick, startGruppettoTick, startGruppettoTick + dur, startGruppettoTick + dur * 2, startGruppettoTick + dur * 3 ),
                    listOf(longDuration, dur, dur, dur, dur),
                    listOf(velocity, stressedVelocity, velocity, velocity, velocity),
                    listOf(glissando, glissando, glissando, glissando, glissando),
                    listOf(attack, attack, attack, attack, attack),
                    listOf(isPreviousRest, false, false, false, false),
                    if(articulationDuration == null) null else listOf(longDuration, dur, dur, dur, dur + diffArticulation),
                    if(ribattuto == null) null else listOf(ribattuto,ribattuto,ribattuto, ribattuto, ribattuto) )
                //  .apply { println("GRUPPETTO:$index duration: $duration, artDur: $articulationDuration $this") }
            }
        }
        is ReplaceType.Onda -> { trackData, index, trackDataList ->
            val (pitch, thick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto) = trackData.extractNoteDataAtIndex(index)
            // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4

            val actualDuration = articulationDuration ?: duration + 1
            val dur = if(60 * 8 > actualDuration ) actualDuration / 8 else  60
            if(dur < 12 || pitch == 108 || pitch == 107 || pitch == 21 || pitch == 22) { // fake substitution
             //   println("ONDA:$index No Subs: actual duration = $actualDuration")
                SubstitutionNotes(-1 ) // will not be considered
            } else {
                val stress = replaceType.stress
                val stressedVelocity = if(velocity + stress > 127) 127 else velocity + stress
                val middleVelocity = (stressedVelocity + velocity) / 2
                val nextPitch = trackData.pitches.getOrElse(index+1) { pitch }
                val (secondAndFourthPitch, thirdPitch) = when {
                    pitch <= nextPitch -> Pair(pitch-1, pitch -2)
                    pitch > nextPitch -> Pair(pitch+1, pitch +2)
                    else -> Pair(pitch-1, pitch -2)
                }
                SubstitutionNotes(index, listOf(pitch, secondAndFourthPitch, thirdPitch, secondAndFourthPitch, pitch),
                    listOf(thick, thick + dur, thick + dur * 2, thick + dur * 3, thick + dur * 4 ),
                    listOf(dur, dur, dur, dur, duration - dur * 4),
                    listOf(velocity, middleVelocity, stressedVelocity, middleVelocity, velocity),
                    listOf(glissando, glissando, glissando, glissando, glissando),
                    listOf(attack, attack, attack, attack, attack),
                    listOf(isPreviousRest, false, false, false, false),
                    if(articulationDuration == null) null else listOf(dur,dur, dur,dur, articulationDuration - dur * 4),
                    if(ribattuto == null) null else listOf(ribattuto,ribattuto,ribattuto, ribattuto, ribattuto) )
                  //  .apply { println("ONDA:$index duration: $duration, artDur: $articulationDuration $this") }
            }
        }

    }
}
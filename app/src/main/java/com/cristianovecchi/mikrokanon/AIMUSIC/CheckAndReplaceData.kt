package com.cristianovecchi.mikrokanon.AIMUSIC

import com.cristianovecchi.mikrokanon.divideDistributingRest
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


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
            is EqualOrGreater -> if(this.limit == 0) " ∞ " else "${this.title}${limitInString(this.limit)}"
        }
    }

    data class None(override val title: String = "   - - -   "): CheckType()
    data class EqualOrGreater(override val title: String = " \u2265 ", val limit: Int = 240) : CheckType()
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
                EqualOrGreater(limit = 160),EqualOrGreater(limit = 240),
                EqualOrGreater(limit = 320), EqualOrGreater(limit = 480),
                EqualOrGreater(limit = 960), EqualOrGreater(limit = 1440),
                EqualOrGreater(limit = 1920) )
        }
        fun limitInString(limit: Int): String {
            return when (limit) {
                120 -> "1/16"
                160 -> "1/8t"
                240 -> "1/8"
                320 -> "1/4t"
                480 -> "1/4"
                960 -> "2/4"
                1440 -> "3/4"
                1920 -> "4/4"
                else -> "?"
            }
        }
        fun getIndex(check: CheckType): Int {
            return when(check){
                is None -> 0
                is EqualOrGreater -> when(check.limit){
                    0 -> 1
                    120 -> 2
                    160 -> 3
                    240 -> 4
                    320 -> 5
                    480 -> 6
                    960 -> 7
                    1440 -> 8
                    1920 -> 9
                    else -> 0
                }
            }
        }
    }
}
sealed class ReplaceType(open val title: String = "", open val stress: Int = 0 ,
                         open val isRetrograde: Boolean = false) {
    fun clone(stress: Int = this.stress, isRetrograde: Boolean = this.isRetrograde ): ReplaceType{
        return when (this){
            is Trillo -> this.copy(stress = stress, isRetrograde = isRetrograde)
            is Mordente -> this.copy(stress = stress, isRetrograde = isRetrograde)
            is Mordente2x -> this.copy(stress = stress, isRetrograde = isRetrograde)
            is Mordente3x -> this.copy(stress = stress, isRetrograde = isRetrograde)
            is Gruppetto -> this.copy(stress = stress, isRetrograde = isRetrograde)
            is Onda -> this.copy(stress = stress, isRetrograde = isRetrograde)
            is Cromatica -> this.copy(stress = stress, isRetrograde = isRetrograde)
            is Diatonica -> this.copy(stress = stress, isRetrograde = isRetrograde)
            is Accento -> this.copy(stress = stress, isRetrograde = isRetrograde)
            is Tornado -> this.copy(stress = stress, isRetrograde = isRetrograde)
            is Fantasia -> this.copy(stress = stress, isRetrograde = isRetrograde)
        }
    }
    fun toCsv(): String {
        val retr = if(this.isRetrograde) 1 else 0
        return when (this){
            is Trillo -> "0#$stress#$retr"
            is Mordente -> "1#$stress#$retr"
            is Mordente2x -> "2#$stress#$retr"
            is Mordente3x -> "3#$stress#$retr"
            is Gruppetto -> "4#$stress#$retr"
            is Onda -> "5#$stress#$retr"
            is Cromatica -> "6#$stress#$retr"
            is Diatonica -> "7#$stress#$retr"
            is Accento -> "8#$stress#$retr"
            is Tornado -> "9#$stress#$retr"
            is Fantasia -> "10#$stress#$retr"
        }
    }
    companion object{
        val titles: List<String> = listOf( "Trillo", "Mordente", "Mordente 2X",
        "Mordente 3x", "Gruppetto", "Onda", "Cromatica", "Diatonica",
            "Accento", "Tornado", "Fantasia" )
        fun provideReplaceType(index: Int, stress: Int = 16, isRetrograde: Boolean = false): ReplaceType{
            return when (index) {
                0 -> Trillo(stress = stress, isRetrograde = isRetrograde)
                1 -> Mordente(stress = stress, isRetrograde = isRetrograde)
                2 -> Mordente2x(stress = stress, isRetrograde = isRetrograde)
                3 -> Mordente3x(stress = stress, isRetrograde = isRetrograde)
                4 -> Gruppetto(stress = stress, isRetrograde = isRetrograde)
                5 -> Onda(stress = stress, isRetrograde = isRetrograde)
                6 -> Cromatica(stress = stress, isRetrograde = isRetrograde)
                7 -> Diatonica(stress = stress, isRetrograde = isRetrograde)
                8 -> Accento(stress = stress, isRetrograde = isRetrograde)
                9 -> Tornado(stress = stress, isRetrograde = isRetrograde)
                10 -> Fantasia(stress = stress, isRetrograde = isRetrograde)
                else -> Trillo(stress = stress, isRetrograde = isRetrograde)
            }
        }
    }
    data class Trillo(override val title: String = "Trillo", override val stress: Int = 16, override val isRetrograde: Boolean = false): ReplaceType()
    data class Mordente(override val title: String = "Mordente", override val stress: Int = 16, override val isRetrograde: Boolean = false): ReplaceType()
    data class Mordente2x(override val title: String = "Mordente 2x", override val stress: Int = 16, override val isRetrograde: Boolean = false): ReplaceType()
    data class Mordente3x(override val title: String = "Mordente 3x", override val stress: Int = 16, override val isRetrograde: Boolean = false): ReplaceType()
    data class Gruppetto(override val title: String = "Gruppetto", override val stress: Int = 16, override val isRetrograde: Boolean = false): ReplaceType()
    data class Onda(override val title: String = "Onda", override val stress: Int = 16, override val isRetrograde: Boolean = false): ReplaceType()
    data class Cromatica(override val title: String = "Cromatica", override val stress: Int = 16, override val isRetrograde: Boolean = false): ReplaceType()
    data class Diatonica(override val title: String = "Diatonica", override val stress: Int = 16, override val isRetrograde: Boolean = false): ReplaceType()
    data class Accento(override val title: String = "Accento", override val stress: Int = 16, override val isRetrograde: Boolean = false): ReplaceType()
    data class Tornado(override val title: String = "Tornado", override val stress: Int = 16, override val isRetrograde: Boolean = false): ReplaceType()
    data class Fantasia(override val title: String = "Fantasia", override val stress: Int = 16, override val isRetrograde: Boolean = false): ReplaceType()
}
data class CheckAndReplaceData(val check: CheckType = CheckType.None(),
                               val replace: ReplaceType = ReplaceType.Mordente(),){
    fun describe(): String {
        val retr = if(replace.isRetrograde) "←" else ""
        return when(check){
            is CheckType.None -> check.describe()
            is CheckType.EqualOrGreater -> "${check.describe()} $retr${replace.title} ^${replace.stress}"
        }
    }
    fun toCsv(): String {
        return "${this.check.toCsv()}|${this.replace.toCsv()}"
    }
    companion object{
        fun insertInMultiCheckAndReplaceCsv(index: Int, cnrCsv: String, multiCsv: String): String {
            println("multiCsv = $multiCsv")
            val multiCnrCsv = multiCsv.split(";").toMutableList()
            multiCnrCsv[index] = cnrCsv
            return multiCnrCsv.joinToString(";")
        }
        fun createMultiCheckAndReplaceDatasFromCsv(csv: String): List<List<CheckAndReplaceData>>{
            if(csv.isBlank()) return listOf()
            val values = csv.split(";")
            return values.map{ createCheckAndReplaceDatasFromCsv(it) }
        }
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
            val limit = checkValues.getOrElse(1) { "0"}
            val check = CheckType.provideCheckType(checkValues[0].toInt(), limit.toInt())
            val stress = replaceValues.getOrElse(1) {"0"}
            val isRetrograde = replaceValues.getOrElse(2) {"0"}
            val replace = ReplaceType.provideReplaceType(replaceValues[0].toInt(), stress.toInt(), isRetrograde.toInt() != 0)
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
fun provideFantasiaFunctions(stress: Int, isRetrograde: Boolean): List<ReplaceType>{
    return if(isRetrograde) listOf(
        ReplaceType.Trillo(stress = stress), ReplaceType.Mordente(stress = stress),
        ReplaceType.Mordente2x(stress = stress), ReplaceType.Mordente3x(stress = stress),
        ReplaceType.Gruppetto(stress = stress), ReplaceType.Onda(stress = stress),
        ReplaceType.Cromatica(stress = stress), ReplaceType.Diatonica(stress = stress),
        ReplaceType.Accento(stress = stress),
        ReplaceType.Trillo(stress = stress, isRetrograde = isRetrograde), ReplaceType.Mordente(stress = stress, isRetrograde = isRetrograde),
        ReplaceType.Mordente2x(stress = stress, isRetrograde = isRetrograde), ReplaceType.Mordente3x(stress = stress, isRetrograde = isRetrograde),
        ReplaceType.Gruppetto(stress = stress, isRetrograde = isRetrograde), ReplaceType.Onda(stress = stress, isRetrograde = isRetrograde),
        ReplaceType.Cromatica(stress = stress, isRetrograde = isRetrograde), ReplaceType.Diatonica(stress = stress, isRetrograde = isRetrograde),
        ReplaceType.Accento(stress = stress, isRetrograde = isRetrograde)
    )
    else listOf(ReplaceType.Trillo(stress = stress), ReplaceType.Mordente(stress = stress),
    ReplaceType.Mordente2x(stress = stress), ReplaceType.Mordente3x(stress = stress),
    ReplaceType.Gruppetto(stress = stress), ReplaceType.Onda(stress = stress),
    ReplaceType.Cromatica(stress = stress), ReplaceType.Diatonica(stress = stress),
    ReplaceType.Accento(stress = stress))
}
fun provideTornadoFunctions(stress: Int, isRetrograde: Boolean): List<ReplaceType>{
    val step = stress / 3f
    return listOf(ReplaceType.Mordente(stress = 0, isRetrograde = isRetrograde),
        ReplaceType.Mordente2x(stress = step.roundToInt(), isRetrograde = isRetrograde),
        ReplaceType.Mordente3x(stress = (step * 2).roundToInt(), isRetrograde = isRetrograde),
        ReplaceType.Trillo(stress = stress, isRetrograde = isRetrograde),
        ReplaceType.Mordente3x(stress = (step * 2).roundToInt(), isRetrograde = isRetrograde),
        ReplaceType.Mordente2x(stress = step.roundToInt(), isRetrograde = isRetrograde)
    )
}
fun provideReplaceFunction(replaceType: ReplaceType):
            (TrackData, Int, List<TrackData>) -> SubstitutionNotes {
    return when(replaceType){
        is ReplaceType.Tornado -> { _, _, _ ->
        SubstitutionNotes(-1)
        }
        is ReplaceType.Fantasia -> { _, _, _ ->
            SubstitutionNotes(-1)
        }
        is ReplaceType.Accento -> { trackData, index, trackDataList ->
            val (pitch, tick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto)
                    = trackData.extractNoteDataAtIndex(index)
            val actualDuration = articulationDuration ?: duration
            val isRetrograde = replaceType.isRetrograde
            val accentDur = 6
            val accentPlusRestDur = accentDur * 10
            if(actualDuration < accentPlusRestDur * 3 / 2){
                SubstitutionNotes(-1)
            } else {
                val stress = replaceType.stress

                val stressedVelocity = if (velocity + stress > 127) 127 else velocity + stress
                if(isRetrograde){
                    SubstitutionNotes(
                        index, listOf(pitch, pitch),
                        listOf(tick, tick + actualDuration - accentPlusRestDur),
                        listOf(actualDuration - accentPlusRestDur, accentDur),
                        listOf(velocity, stressedVelocity),
                        listOf(0, 0),
                        listOf(0, attack),
                        listOf(isPreviousRest, false),
                        if (articulationDuration == null) null else listOf(actualDuration - accentPlusRestDur, accentDur),
                        if (ribattuto == null) null else listOf(ribattuto, ribattuto)
                    )
                } else {
                    SubstitutionNotes(
                        index, listOf(pitch, pitch),
                        listOf(tick, tick + accentPlusRestDur),
                        listOf(accentDur, actualDuration - accentPlusRestDur),
                        listOf(stressedVelocity, velocity),
                        listOf(0, glissando),
                        listOf(attack, 0),
                        listOf(isPreviousRest, true),
                        if (articulationDuration == null) null else listOf(accentDur, actualDuration - accentPlusRestDur),
                        if (ribattuto == null) null else listOf(ribattuto, ribattuto)
                    )
                }

            }

        }
//        is ReplaceType.Cromatica, is ReplaceType.Diatonica -> { trackData, index, trackDataList ->
//            val (pitch, tick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto)
//                    = trackData.extractNoteDataAtIndex(index)
//            // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4
//            val actualDuration = articulationDuration ?: duration
//            val isStaccato = actualDuration < duration
//            val isRetrograde = replaceType.isRetrograde
//            val (startPitch, endPitch) = if(isRetrograde){
//                Pair(trackData.pitches.getOrElse(index - 1) { pitch }, pitch)
//            } else {
//                Pair(pitch, trackData.pitches.getOrElse(index - 1) { pitch })
//            }
//            val nSemitones = (startPitch - endPitch).absoluteValue
//            val notPossible = if(isRetrograde){
//               !trackData.isConnectedToPreviousNoteAndDifferentPitch(index) || nSemitones == 1
//            } else {
//                !trackData.isConnectedToNextNoteAndDifferentPitch(index, isStaccato) || nSemitones == 1
//            }
//            if(notPossible){
//                SubstitutionNotes(-1)
//            } else {
//                val durs = findScaleDurations(if(isRetrograde) actualDuration else duration, nSemitones, 60)
//                if(durs.last() < 12){
//                    SubstitutionNotes(-1)
//                } else {
//                    val ticks = findScaleTicks(tick, durs)
//                    val pitches = findChromaticScale(startPitch, endPitch, isRetrograde)
//                    val stress = replaceType.stress
//                    val stressedVelocity = if (velocity + stress > 127) 127 else velocity + stress
//                    val diff = if(actualDuration <= duration) 0 else actualDuration - duration
//                    SubstitutionNotes(
//                        index, pitches, ticks, durs,
//                        if(isRetrograde) listOf(stressedVelocity, *List(nSemitones-1){velocity}.toTypedArray())
//                        else listOf(velocity, stressedVelocity, *List(nSemitones-2){velocity}.toTypedArray()),
//                        List(nSemitones){ 0 },
//                        List(nSemitones){attack},
//                        listOf(isPreviousRest, *List(nSemitones-1){false}.toTypedArray()),
//                        when {
//                            articulationDuration == null -> null
//                            isRetrograde -> durs
//                            else -> listOf(*durs.dropLast(1).toTypedArray(), durs.last() + diff)
//                        },
//                        if (ribattuto == null) null else List(nSemitones){ribattuto}
//                    )
//                }
//            }
//        }
        is ReplaceType.Cromatica, is ReplaceType.Diatonica -> { trackData, index, trackDataList ->
            val (pitch, tick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto)
                    = trackData.extractNoteDataAtIndex(index)
            // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4
            val actualDuration = articulationDuration ?: duration
            val isStaccato = actualDuration < duration
            val isRetrograde = replaceType.isRetrograde
            val (startPitch, endPitch) = if(isRetrograde){
                Pair(trackData.pitches.getOrElse(index - 1) { pitch }, pitch)
            } else {
                Pair(pitch, trackData.pitches.getOrElse(index + 1) { pitch })
            }
            val semitones = (startPitch - endPitch).absoluteValue
            val uselessRange = when(replaceType){
                is ReplaceType.Diatonica -> IntRange(0,2)
                else -> IntRange(0,1)
            }
            val notPossible = if(isRetrograde){
                !trackData.isConnectedToPreviousNote(index) || semitones in uselessRange
            } else {
                !trackData.isConnectedToNextNote(index, isStaccato) || semitones in uselessRange
            }
            if(notPossible){
                SubstitutionNotes(-1)
            } else {
                val nNotes = when(replaceType){
                    is ReplaceType.Diatonica -> semitones / 2 + semitones % 2
                    else -> semitones
                }
                var durs = findScaleDurations(if(isRetrograde) actualDuration else duration, nNotes, 60)
                durs = if(isRetrograde) durs.reversed() else durs
                if(durs.last()  < 12 || durs.first() < 12){
                    SubstitutionNotes(-1)
                } else {
                    val ticks = findScaleTicks(tick, durs)
                    val pitches = when(replaceType) {
                        is ReplaceType.Diatonica -> findWholeToneScale(startPitch, endPitch, isRetrograde)
                        else -> findChromaticScale(startPitch, endPitch, isRetrograde)
                    }
                    val stress = replaceType.stress
                    val stressedVelocity = if (velocity + stress > 127) 127 else velocity + stress
                    val diff = if(isStaccato) 0 else actualDuration - duration
                    SubstitutionNotes(
                        index, pitches, ticks, durs,
                        if(isRetrograde) listOf(stressedVelocity, *List(nNotes-1){velocity}.toTypedArray())
                        else listOf(velocity, stressedVelocity, *List(nNotes-2){velocity}.toTypedArray()),
                        List(nNotes) {0},
                        List(nNotes){attack},
                        listOf(isPreviousRest, *List(nNotes-1){false}.toTypedArray()),
                        if(articulationDuration == null) null
                            else listOf(*durs.dropLast(1).toTypedArray(), durs.last() + diff),
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
            val isStaccato = actualDuration < duration
            val (durs, div) = findTrillDurations(if (!isStaccato) duration else actualDuration)
            if (div == -1) { // fake substitution
                //println("TRILLO:$index No Subs: actual duration = $actualDuration")
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
                var lastTick = tick
                val ticks = (0 until div-1).map{
                    lastTick += durs[it]
                    lastTick
                }
                val diff = if(isStaccato) 0 else actualDuration - duration
                SubstitutionNotes(
                    index,
                    if(replaceType.isRetrograde) (0 until div).map { if(it % 2 != 0) secondPitch else pitch }
                        else (0 until div).map { if(it % 2 == 0) secondPitch else pitch },
                    listOf(tick, *ticks.toTypedArray()),
                    durs,
                    listOf(stressedVelocity, *List(div-1){velocity}.toTypedArray()),
                    listOf(*List(div-1){ 0 }.toTypedArray(), glissando),
                    List(div){attack},
                    listOf(isPreviousRest, *List(div-1){false}.toTypedArray()),
                    if (articulationDuration == null) null
                        else listOf(*durs.dropLast(1).toTypedArray(), durs.last() + diff),
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
            val isRetrograde = replaceType.isRetrograde
            val isStaccato = actualDuration < duration
            var durs = find2ShortAndLongDurations(if(!isStaccato) duration else actualDuration, 60)
            durs = if(isRetrograde) durs.reversed() else durs
            val dur = if(isRetrograde) durs.last() else durs.first()
            val nextPitch = trackData.pitches.getOrElse(index+1) { pitch }
            val notPossible = if(isRetrograde){
                !trackData.isConnectedToNextNote(index, isStaccato) || dur < 12 || nextPitch == pitch
            } else {
                dur < 12
            }
            if (notPossible) { // fake substitution
               // println("MORDENTE:$index No Subs: actual duration = $actualDuration")
                SubstitutionNotes(-1) // will not be considered
            } else {
                val stress = replaceType.stress
                val stressedVelocity = if (velocity + stress > 127) 127 else velocity + stress
                val secondPitch = when {
                    pitch == 108 -> pitch - 1
                    pitch == 21 -> pitch + 1
                    pitch <= nextPitch -> pitch - 1
                    pitch > nextPitch -> pitch + 1
                    else -> pitch - 1
                }
                val diff = if(isStaccato) 0 else actualDuration - duration
                SubstitutionNotes(
                    index,
                    listOf(pitch, secondPitch, pitch),
                    findTicksFromDurations(tick, durs),
                    durs,
                    if(isRetrograde) listOf(velocity, stressedVelocity, velocity)
                        else listOf(stressedVelocity, velocity, velocity),
                    listOf(0, 0, glissando),
                    listOf(attack, attack, attack),
                    listOf(isPreviousRest, false, false),
                    if (articulationDuration == null) null
                      else listOf(*durs.dropLast(1).toTypedArray(),durs.last() + diff),
                    if (ribattuto == null) null else listOf(ribattuto, ribattuto, ribattuto)
                )
                 //   .apply { println("MORDENTE:$index duration: $duration, artDur: $articulationDuration $this") }
            }
        }
           is ReplaceType.Mordente3x -> { trackData, index, trackDataList ->
                val (pitch, tick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto) = trackData.extractNoteDataAtIndex(index)
                // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4

                val actualDuration = articulationDuration ?: duration
               val isStaccato = actualDuration < duration
               val isRetrograde = replaceType.isRetrograde
                var durs = find6ShortAndLongDurations(
                    if(!isStaccato) duration else actualDuration, 60)
               durs = if(isRetrograde) durs.reversed() else durs
               val dur = if(isRetrograde) durs.last() else durs.first()
               val nextPitch = trackData.pitches.getOrElse(index+1) { pitch }
               val notPossible = if(isRetrograde){
                   !trackData.isConnectedToNextNote(index, isStaccato) || dur < 12 || nextPitch == pitch
               } else {
                   dur < 12
               }
                if(notPossible) { // fake substitution
                  //  println("MORDENTE_3X:$index No Subs: actual duration = $actualDuration")
                    SubstitutionNotes(-1 ) // will not be considered
                } else {
                    val stress = replaceType.stress
                    val stressedVelocity = if(velocity + stress > 127) 127 else velocity + stress
                    val secondPitch = when{
                        pitch == 108 -> pitch - 1
                        pitch == 21 -> pitch + 1
                        pitch <= nextPitch -> pitch -1
                        pitch > nextPitch -> pitch +1
                        else -> pitch - 1
                    }
                    val diff = if(isStaccato) 0 else actualDuration - duration
                    SubstitutionNotes(index, listOf(pitch, secondPitch, pitch, secondPitch, pitch, secondPitch, pitch),
                        findTicksFromDurations(tick, durs),
                        durs,
                        if(isRetrograde) listOf(velocity, stressedVelocity, velocity, velocity, velocity, velocity, velocity)
                        else listOf(stressedVelocity, velocity, velocity, velocity, velocity, velocity, velocity),
                        listOf(0,0,0,0,0,0,glissando),
                    listOf(attack, attack, attack, attack, attack, attack, attack),
                    listOf(isPreviousRest, false, false, false, false, false, false),
                    if(articulationDuration == null) null
                    else listOf(*durs.dropLast(1).toTypedArray(),durs.last() + diff),
                    if(ribattuto == null) null else listOf(ribattuto, ribattuto, ribattuto, ribattuto, ribattuto, ribattuto, ribattuto) )
                   // .apply { println("MORDENTE 3X:$index duration: $duration, artDur: $articulationDuration $this") }
                }
            }
       is ReplaceType.Mordente2x -> { trackData, index, trackDataList ->
            val (pitch, tick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto) = trackData.extractNoteDataAtIndex(index)
            // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4
            val actualDuration = articulationDuration ?: duration
           val isStaccato = actualDuration < duration
           val isRetrograde = replaceType.isRetrograde
            var durs = find4ShortAndLongDurations(
                if(!isStaccato) duration else actualDuration, 60)

           durs = if(isRetrograde) durs.reversed() else durs
           val dur = if(isRetrograde) durs.last() else durs.first()
           val nextPitch = trackData.pitches.getOrElse(index+1) { pitch }
           val notPossible = if(isRetrograde){
               !trackData.isConnectedToNextNote(index, isStaccato) || dur < 12 || nextPitch == pitch
           } else {
               dur < 12
           }
            if(notPossible) { // fake substitution
                //println("MORDENTE 2X:$index actual duration = $actualDuration")
                SubstitutionNotes(-1 ) // will not be considered
            } else {
                val stress = replaceType.stress
                val stressedVelocity = if(velocity + stress > 127) 127 else velocity + stress
                val secondPitch = when{
                    pitch == 108 -> pitch - 1
                    pitch == 21 -> pitch + 1
                    pitch <= nextPitch -> pitch -1
                    pitch > nextPitch -> pitch +1
                    else -> pitch - 1
                }
                val diff = if(isStaccato) 0 else actualDuration - duration
                SubstitutionNotes(index, listOf(pitch, secondPitch, pitch, secondPitch, pitch),
                    findTicksFromDurations(tick, durs),
                    durs,
                    if(isRetrograde) listOf(velocity, stressedVelocity, velocity, velocity, velocity)
                        else listOf(stressedVelocity, velocity, velocity, velocity, velocity),
                    listOf(0,0,0,0, glissando),
                    listOf(attack, attack, attack, attack, attack),
                    listOf(isPreviousRest, false, false, false, false),
                    if(articulationDuration == null) null
                        else listOf(*durs.dropLast(1).toTypedArray(),durs.last() + diff),
                    if(ribattuto == null) null else listOf(ribattuto,ribattuto,ribattuto, ribattuto, ribattuto) )
                //    .apply { println("MORDENTE 2X:$index duration: $duration, artDur: $articulationDuration $this") }
            }
        }
        is ReplaceType.Gruppetto-> { trackData, index, trackDataList ->
            val (pitch, tick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto) = trackData.extractNoteDataAtIndex(index)
            // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4
            val actualDuration = articulationDuration ?: duration
            val isStaccato = actualDuration < duration
            val isRetrograde = replaceType.isRetrograde
            var durs = find4ShortAndLongDurations(
                if(!isStaccato) duration else actualDuration, 60)
            durs = if(isRetrograde) durs.reversed() else durs
            val dur = if(isRetrograde) durs.last() else durs.first()
            val nextPitch = trackData.pitches.getOrElse(index+1) { pitch }
            val notPossible = if(isRetrograde){
                !trackData.isConnectedToNextNote(index, isStaccato) || dur < 12 || nextPitch == pitch || pitch == 108 || pitch == 21
            } else {
                dur < 12 || pitch == 108 || pitch == 21
            }
            if(notPossible) { // fake substitution
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
                val velocities = if(isRetrograde){
                    listOf(velocity, stressedVelocity, velocity, velocity, velocity)
                } else {
                    listOf(stressedVelocity, velocity, velocity, velocity, velocity)
                }
                val diff = if(isStaccato) 0 else actualDuration - duration
                SubstitutionNotes(index, listOf(pitch, secondPitch, pitch, fourthPitch, pitch),
                    findTicksFromDurations(tick, durs),
                    durs, velocities,
                    listOf(0,0,0,0, glissando),
                    listOf(attack, attack, attack, attack, attack),
                    listOf(isPreviousRest, false, false, false, false),
                    if(articulationDuration == null) null
                        else listOf(*durs.dropLast(1).toTypedArray(), durs.last() + diff),
                    if(ribattuto == null) null else listOf(ribattuto,ribattuto,ribattuto, ribattuto, ribattuto) )
                //  .apply { println("GRUPPETTO:$index duration: $duration, artDur: $articulationDuration $this") }
            }
        }
        is ReplaceType.Onda -> { trackData, index, trackDataList ->
            val (pitch, tick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto) = trackData.extractNoteDataAtIndex(index)
            // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4
            val actualDuration = articulationDuration ?: duration + 1
            val isStaccato = actualDuration < duration
            val isRetrograde = replaceType.isRetrograde
            var durs = find4ShortAndLongDurations(if(!isStaccato) duration else actualDuration, 60)
            durs = if(isRetrograde) durs.reversed() else durs
            val dur = if(isRetrograde) durs.last() else durs.first()
            val nextPitch = trackData.pitches.getOrElse(index+1) { pitch }
            val notPossible = if(isRetrograde){
                !trackData.isConnectedToNextNote(index, isStaccato) || dur < 12 || nextPitch == pitch
                        || pitch == 108 || pitch == 107 || pitch == 21 || pitch == 22
            } else {
                dur < 12 || pitch == 108 || pitch == 107 || pitch == 21 || pitch == 22
            }
            if(notPossible) { // fake substitution
             //   println("ONDA:$index No Subs: actual duration = $actualDuration")
                SubstitutionNotes(-1 ) // will not be considered
            } else {
                val stress = replaceType.stress
                val stressedVelocity = if(velocity + stress > 127) 127 else velocity + stress
                val middleVelocity = (stressedVelocity + velocity) / 2
                val (secondAndFourthPitch, thirdPitch) = when {
                    pitch <= nextPitch -> Pair(pitch-1, pitch -2)
                    pitch > nextPitch -> Pair(pitch+1, pitch +2)
                    else -> Pair(pitch-1, pitch -2)
                }
                val diff = if(isStaccato) 0 else actualDuration - duration
                SubstitutionNotes(index, listOf(pitch, secondAndFourthPitch, thirdPitch, secondAndFourthPitch, pitch),
                    findTicksFromDurations(tick, durs),
                    durs,
                    listOf(velocity, middleVelocity, stressedVelocity, middleVelocity, velocity),
                    listOf(0,0,0,0, glissando),
                    listOf(attack, attack, attack, attack, attack),
                    listOf(isPreviousRest, false, false, false, false),
                    if(articulationDuration == null) null
                        else listOf(*durs.dropLast(1).toTypedArray(), durs.last() + diff),
                    if(ribattuto == null) null else listOf(ribattuto,ribattuto,ribattuto, ribattuto, ribattuto) )
                  //  .apply { println("ONDA:$index duration: $duration, artDur: $articulationDuration $this") }
            }
        }

    }
}
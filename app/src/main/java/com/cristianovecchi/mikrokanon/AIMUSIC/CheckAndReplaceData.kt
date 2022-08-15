package com.cristianovecchi.mikrokanon.AIMUSIC

import com.cristianovecchi.mikrokanon.divideDistributingRest
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


sealed class CheckType(open val title: String = "") {
    fun toCsv(): String {
        return when (this){
            is None -> "0"
            is EqualOrGreater -> "1#$limit"
            is StartPhrase -> "2"
            is EndPhrase -> "3"
            is SingleNote -> "4"
        }
    }
    fun describe(): String {
        return when (this){
            is None -> this.title
            is EqualOrGreater -> if(this.limit == 0) " ∞ " else "${this.title}${limitInString(this.limit)}"
            is StartPhrase -> this.title
            is EndPhrase -> this.title
            is SingleNote -> this.title
        }
    }

    data class None(override val title: String = "   - - -   "): CheckType()
    data class EqualOrGreater(override val title: String = " \u2265 ", val limit: Int = 240) : CheckType()
    data class StartPhrase(override val title: String = " |- ") : CheckType()
    data class EndPhrase(override val title: String = " -| ") : CheckType()
    data class SingleNote(override val title: String = " |-| ") : CheckType()
    //, ALONE("].[ >=")
    companion object {
        fun provideCheckType(index: Int, limit: Int = 240): CheckType{
            return when (index) {
                0 -> None()
                1 -> EqualOrGreater(limit = limit)
                2 -> StartPhrase()
                3 -> EndPhrase()
                4 -> SingleNote()
                else -> None()
            }
        }
        fun checkList(): List<CheckType>{
            return listOf( None(),
                EqualOrGreater(limit = 0),EqualOrGreater(limit = 120),
                EqualOrGreater(limit = 160),EqualOrGreater(limit = 240),
                EqualOrGreater(limit = 320), EqualOrGreater(limit = 480),
                EqualOrGreater(limit = 640),
                EqualOrGreater(limit = 960), EqualOrGreater(limit = 1440),
                EqualOrGreater(limit = 1920), EqualOrGreater(limit = 2400),
                EqualOrGreater(limit = 2880), EqualOrGreater(limit = 3360),
                EqualOrGreater(limit = 3840), EqualOrGreater(limit = 4320),
                StartPhrase(), EndPhrase(), SingleNote()
            )
        }
        fun limitInString(limit: Int): String {
            return when (limit) {
                120 -> "1/16"
                160 -> "1/8t"
                240 -> "1/8"
                320 -> "1/4t"
                480 -> "1/4"
                640 -> "1/2t"
                960 -> "2/4"
                1440 -> "3/4"
                1920 -> "4/4"
                2400 -> "5/4"
                2880 -> "6/4"
                3360 -> "7/4"
                3840 -> "4/2"
                4320 -> "9/4"
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
                    640 ->7
                    960 -> 8
                    1440 -> 9
                    1920 -> 10
                    2400 -> 11
                    2880 -> 12
                    3360 -> 13
                    3840 -> 14
                    4320 -> 15
                    else -> 0
                }
                is StartPhrase -> 16
                is EndPhrase -> 17
                is SingleNote -> 18
            }
        }
    }
}
sealed class ReplaceType(open val title: String = "", open val stress: Int = 0 ,
                         open val isRetrograde: Boolean = false, open val addGliss: Boolean = false) {
    fun clone(stress: Int = this.stress, isRetrograde: Boolean = this.isRetrograde, addGliss: Boolean = this.addGliss ): ReplaceType{
        return when (this){
            is Trillo2m -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is Trillo2M -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is Mordente -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is Mordente2x -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is Mordente3x -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is Gruppetto -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is Cambio -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is Onda -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is OscillazioneCromatica -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is OscillazioneDiatonica -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is Cromatica -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is Diatonica -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is CromaticaDiCambio -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is DiatonicaDiCambio -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is Accento -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is TremoloCrescendo -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is TremoloCrescDim -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is Tornado -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is Fantasia -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is Attack -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is Glissando -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is Irregular2M -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is Irregular2m -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is Ribattuto -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is SOS -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            is Velocity -> this.copy(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
        }
    }
    fun toCsv(): String {
        val retr = if(this.isRetrograde) 1 else 0
        val gliss = if(this.addGliss) 1 else 0
        return when (this){

            is Mordente -> "0#$stress#$retr#$gliss"
            is Mordente2x -> "1#$stress#$retr#$gliss"
            is Mordente3x -> "2#$stress#$retr#$gliss"
            is Gruppetto -> "3#$stress#$retr#$gliss"
            is Cambio -> "4#$stress#$retr#$gliss"
            is Onda -> "5#$stress#$retr#$gliss"
            is Cromatica -> "6#$stress#$retr#$gliss"
            is Diatonica -> "7#$stress#$retr#$gliss"
            is CromaticaDiCambio -> "8#$stress#$retr#$gliss"
            is DiatonicaDiCambio -> "9#$stress#$retr#$gliss"
            is Accento -> "10#$stress#$retr#$gliss"
            is Ribattuto -> "11#$stress#$retr#$gliss"
            is SOS -> "12#$stress#$retr#$gliss"
            is TremoloCrescendo -> "13#$stress#$retr#$gliss"
            is TremoloCrescDim -> "14#$stress#$retr#$gliss"
            is Trillo2m -> "15#$stress#$retr#$gliss"
            is Trillo2M -> "16#$stress#$retr#$gliss"
            is OscillazioneCromatica -> "17#$stress#$retr#$gliss"
            is OscillazioneDiatonica -> "18#$stress#$retr#$gliss"
            is Irregular2m -> "19#$stress#$retr#$gliss"
            is Irregular2M -> "20#$stress#$retr#$gliss"
            is Glissando -> "21#$stress#$retr#$gliss"
            is Velocity -> "22#$stress#$retr#$gliss"
            is Attack -> "23#$stress#$retr#$gliss"
            is Tornado -> "24#$stress#$retr#$gliss"
            is Fantasia -> "25#$stress#$retr#$gliss"

        }
    }
    companion object{
        val titles: List<String> = listOf(  "Mordente", "Mordente 2X",
        "Mordente 3x", "Gruppetto", "Note di cambio", "Onda",
            "Cromatica", "Diatonica", "Cromatica di cambio", "Diatonica di cambio",
            "Accento", "Ribattuto", "SOS", "Tremolo<","Tremolo<>",
            "Trillo 2m", "Trillo 2M","Oscillazione 2m", "Oscillazione 2M", "Irregolare 2m", "Irregolare 2M",
            "Glissando", "Dinamica", "Attacco",
            "Tornado", "Fantasia" )
        fun provideReplaceType(index: Int, stress: Int = 16, isRetrograde: Boolean = false, addGliss: Boolean = false): ReplaceType{
            return when (index) {
                // PARTIAL SUBSTITUTION
                0 -> Mordente(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                1 -> Mordente2x(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                2 -> Mordente3x(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                3 -> Gruppetto(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                4 -> Cambio(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                5 -> Onda(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                6 -> Cromatica(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                7 -> Diatonica(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                8 -> CromaticaDiCambio(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                9 -> DiatonicaDiCambio(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                // REPETITION
                10 -> Accento(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                11 -> Ribattuto(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                12 -> SOS(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                13 -> TremoloCrescendo(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                14 -> TremoloCrescDim(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                // WHOLE SUBSTITUTION
                15 -> Trillo2m(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                16 -> Trillo2M(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                17 -> OscillazioneCromatica(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                18 -> OscillazioneDiatonica(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                19 -> Irregular2m(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                20 -> Irregular2M(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                // MIDI effects
                21 -> Glissando(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                22 -> Velocity(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                23 -> Attack(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                // MACRO FUNCTION
                24 -> Tornado(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                25 -> Fantasia(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
                else -> Trillo2m(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss)
            }
        }
    }
    data class Trillo2m(override val title: String = "Trillo 2m", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class Trillo2M(override val title: String = "Trillo 2M", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class Mordente(override val title: String = "Mordente", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class Mordente2x(override val title: String = "Mordente 2x", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class Mordente3x(override val title: String = "Mordente 3x", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class Gruppetto(override val title: String = "Gruppetto", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class Cambio(override val title: String = "Note di cambio", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class Onda(override val title: String = "Onda", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class OscillazioneCromatica(override val title: String = "Oscillazione 2m", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class OscillazioneDiatonica(override val title: String = "Oscillazione 2M", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class Cromatica(override val title: String = "Cromatica", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class Diatonica(override val title: String = "Diatonica", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class CromaticaDiCambio(override val title: String = "Cromatica di cambio", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class DiatonicaDiCambio(override val title: String = "Diatonica di cambio", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class Accento(override val title: String = "Accento", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class TremoloCrescendo(override val title: String = "Tremolo<", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class TremoloCrescDim(override val title: String = "Tremolo<>", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class Glissando(override val title: String = "Glissando", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class Velocity(override val title: String = "Dinamica", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class Attack(override val title: String = "Attacco", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class Irregular2m(override val title: String = "Irregolare 2m", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class Irregular2M(override val title: String = "Irregolare 2M", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class SOS(override val title: String = "SOS", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class Ribattuto(override val title: String = "Ribattuto", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()

    data class Tornado(override val title: String = "Tornado", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
    data class Fantasia(override val title: String = "Fantasia", override val stress: Int = 16, override val isRetrograde: Boolean = false, override val addGliss: Boolean = false): ReplaceType()
}
data class CheckAndReplaceData(val check: CheckType = CheckType.None(),
                               val replace: ReplaceType = ReplaceType.Mordente(),){
    fun describe(glissSymbols: String = "Gl"): String {
        val retr = if(replace.isRetrograde) "←" else ""
        val gliss = if(replace.addGliss) glissSymbols else ""
        return when(check){
            is CheckType.None -> check.describe()
            is CheckType.EqualOrGreater -> "${check.describe()} $retr${replace.title}$gliss ^${replace.stress}"
            is CheckType.StartPhrase -> "${check.describe()} $retr${replace.title}$gliss ^${replace.stress}"
            is CheckType.EndPhrase -> "${check.describe()} $retr${replace.title}$gliss ^${replace.stress}"
            is CheckType.SingleNote -> "${check.describe()} $retr${replace.title}$gliss ^${replace.stress}"
        }
    }
    fun toCsv(): String {
        return "${this.check.toCsv()}|${this.replace.toCsv()}"
    }
    companion object{
        fun insertInMultiCheckAndReplaceCsv(index: Int, cnrCsv: String, multiCsv: String): String {
            //println("multiCsv = $multiCsv")
            val multiCnrCsv = multiCsv.split(";").toMutableList()
            if(index >= multiCnrCsv.size){
                (index until multiCnrCsv.size).forEach { multiCnrCsv.add("")}
                multiCnrCsv.add(element = cnrCsv)
            } else {
                multiCnrCsv[index] = cnrCsv
            }
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
            val addGliss = replaceValues.getOrElse(3) {"0"}
            val replace = ReplaceType.provideReplaceType(replaceValues[0].toInt(),
                stress.toInt(), isRetrograde.toInt() != 0, addGliss.toInt() != 0)
            return CheckAndReplaceData(check, replace)
        }
    }
}



fun provideCheckFunction(checkType: CheckType): (TrackData, Int, List<TrackData>) -> Boolean {
    return when(checkType){
        is CheckType.None -> { _, _, _ -> false }
        is CheckType.EqualOrGreater -> { trackData, index, trackDataList -> trackData.durations[index] >= checkType.limit }
        //CheckType.ALONE -> TODO()
        is CheckType.StartPhrase -> { trackData, index, trackDataList ->
            trackData.isPreviousRest[index]
        }
        is CheckType.EndPhrase -> { trackData, index, trackDataList ->
            trackData.isPreviousRest.getOrElse(index + 1) {true}
        }
        is CheckType.SingleNote -> { trackData, index, trackDataList ->
            trackData.isPreviousRest[index] && trackData.isPreviousRest.getOrElse(index + 1) {true}
        }
    }
}
fun provideFantasiaFunctions(stress: Int, isRetrograde: Boolean, addGliss: Boolean): List<ReplaceType>{
    return if(isRetrograde) listOf(
        ReplaceType.Trillo2m(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss), ReplaceType.Trillo2M(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss),
        ReplaceType.Mordente(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss),
        ReplaceType.Mordente2x(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss), ReplaceType.Mordente3x(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss),
        ReplaceType.Gruppetto(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss), ReplaceType.Cambio(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss),
        ReplaceType.Onda(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss),
        ReplaceType.OscillazioneCromatica(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss), ReplaceType.OscillazioneDiatonica(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss),
        ReplaceType.Cromatica(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss), ReplaceType.Diatonica(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss),
        ReplaceType.CromaticaDiCambio(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss), ReplaceType.DiatonicaDiCambio(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss),
        ReplaceType.Accento(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss),
        ReplaceType.Ribattuto(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss),
        ReplaceType.SOS(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss),
        ReplaceType.Irregular2m(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss),
        ReplaceType.Irregular2M(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss),

        ReplaceType.TremoloCrescendo(stress = stress, isRetrograde = isRetrograde,addGliss = addGliss),
        ReplaceType.Trillo2m(stress = stress, addGliss = addGliss), ReplaceType.Trillo2M(stress = stress, addGliss = addGliss),
        ReplaceType.Mordente(stress = stress, addGliss = addGliss),
        ReplaceType.Mordente2x(stress = stress, addGliss = addGliss), ReplaceType.Mordente3x(stress = stress, addGliss = addGliss),
        ReplaceType.Gruppetto(stress = stress, addGliss = addGliss), ReplaceType.Onda(stress = stress, addGliss = addGliss),
        ReplaceType.Cromatica(stress = stress, addGliss = addGliss), ReplaceType.Diatonica(stress = stress, addGliss = addGliss),
        ReplaceType.CromaticaDiCambio(stress = stress, addGliss = addGliss), ReplaceType.DiatonicaDiCambio(stress = stress, addGliss = addGliss),
        ReplaceType.Accento(stress = stress, addGliss = addGliss),
        ReplaceType.Ribattuto(stress = stress, addGliss = addGliss),
        ReplaceType.SOS(stress = stress, addGliss = addGliss),
        ReplaceType.Irregular2m(stress = stress, addGliss = addGliss),
        ReplaceType.Irregular2M(stress = stress, addGliss = addGliss),
        ReplaceType.TremoloCrescendo(stress = stress, addGliss = addGliss)
    )
    else listOf(ReplaceType.Trillo2m(stress = stress, addGliss = addGliss), ReplaceType.Trillo2M(stress = stress, addGliss = addGliss),
        ReplaceType.Mordente(stress = stress, addGliss = addGliss),
    ReplaceType.Mordente2x(stress = stress, addGliss = addGliss), ReplaceType.Mordente3x(stress = stress, addGliss = addGliss),
    ReplaceType.Gruppetto(stress = stress, addGliss = addGliss), ReplaceType.Cambio(stress = stress, addGliss = addGliss),
    ReplaceType.Onda(stress = stress, addGliss = addGliss),
    ReplaceType.OscillazioneCromatica(stress = stress, addGliss = addGliss),
    ReplaceType.OscillazioneDiatonica(stress = stress, addGliss = addGliss),
    ReplaceType.Cromatica(stress = stress, addGliss = addGliss), ReplaceType.Diatonica(stress = stress, addGliss = addGliss),
    ReplaceType.CromaticaDiCambio(stress = stress, addGliss = addGliss), ReplaceType.DiatonicaDiCambio(stress = stress, addGliss = addGliss),
    ReplaceType.Accento(stress = stress, addGliss = addGliss),ReplaceType.TremoloCrescendo(stress = stress , addGliss = addGliss),
    ReplaceType.Accento(stress = stress, addGliss = addGliss),
    ReplaceType.Ribattuto(stress = stress, addGliss = addGliss),
    ReplaceType.SOS(stress = stress, addGliss = addGliss),
    ReplaceType.Irregular2m(stress = stress, addGliss = addGliss),
    ReplaceType.Irregular2M(stress = stress, addGliss = addGliss))
}
fun provideTornadoFunctions(stress: Int, isRetrograde: Boolean, addGliss: Boolean): List<ReplaceType>{
    val step = stress / 3f
    return listOf(ReplaceType.Mordente(stress = 0, addGliss = addGliss),
        ReplaceType.Mordente2x(stress = step.roundToInt(), isRetrograde = isRetrograde, addGliss = addGliss),
        ReplaceType.Mordente3x(stress = (step * 2).roundToInt(), isRetrograde = isRetrograde, addGliss = addGliss),
        ReplaceType.Trillo2m(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss),
        ReplaceType.Trillo2M(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss),
        ReplaceType.Trillo2m(stress = stress, isRetrograde = isRetrograde, addGliss = addGliss),
        ReplaceType.Mordente3x(stress = (step * 2).roundToInt(), isRetrograde = isRetrograde, addGliss = addGliss),
        ReplaceType.Mordente2x(stress = step.roundToInt(), isRetrograde = isRetrograde, addGliss = addGliss)
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
        is ReplaceType.TremoloCrescendo, is ReplaceType.TremoloCrescDim -> { trackData, index, trackDataList ->
            val (pitch, tick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto)
                    = trackData.extractNoteDataAtIndex(index)
            val actualDuration = articulationDuration ?: duration
            val grain = 12
            val minDur = if(replaceType is ReplaceType.TremoloCrescendo) grain * 2 else grain * 3
            if(actualDuration < minDur){
                SubstitutionNotes(-1)
            } else {
                val isStaccato = actualDuration < duration
                val stress = replaceType.stress
                val stressedVelocity = if (velocity + stress > 127) 127 else velocity + stress
                val velDiff = stressedVelocity - velocity
                val (nNotes, durs) = if(isStaccato){
                    if(velDiff * grain <= actualDuration) Pair(velDiff, actualDuration.divideDistributingRest(velDiff)) else
                    Pair(actualDuration / grain, actualDuration.divideDistributingRest(actualDuration/grain).toList())
                } else {
                    if(velDiff * grain <= duration) Pair(velDiff, duration.divideDistributingRest(velDiff)) else
                    Pair(duration / grain, duration.divideDistributingRest(duration/grain).toList())
                }
                val velocities = if(replaceType is ReplaceType.TremoloCrescendo){
                    var vels = accumulateVelocities(nNotes, velocity, velDiff)
                    vels = if(replaceType.isRetrograde) vels.reversed() else vels
                    vels
                } else {
                    accumulateVelocitiesCrescDim(nNotes, velocity, velDiff, replaceType.isRetrograde)
                }

                val diff = if(isStaccato || glissando != 0) 0 else actualDuration - duration
                SubstitutionNotes(
                    index, List(nNotes){pitch},
                    findTicksFromDurations(tick, durs),
                    durs,
                    velocities,
                    listOf(*List(nNotes-1){0}.toTypedArray(), glissando),
                    List(nNotes){attack},
                    listOf(isPreviousRest, *List(nNotes-1){false}.toTypedArray()),
                    if (articulationDuration == null) null else listOf(*durs.dropLast(1).toTypedArray(), durs.last() + diff),
                    if (ribattuto == null) null else List(nNotes){ribattuto}
                )//.apply{ println("nNotes:$nNotes start:$velocity end:$stressedVelocity $this") }
            }
        }
        is ReplaceType.Accento -> { trackData, index, trackDataList ->
            val (pitch, tick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto)
                    = trackData.extractNoteDataAtIndex(index)
            val actualDuration = articulationDuration ?: duration
            val isStaccato = actualDuration < duration

            val isRetrograde = replaceType.isRetrograde
            val accentDur = 6
            val accentPlusRestDur = accentDur * 10
            if(actualDuration < accentPlusRestDur * 3 / 2){
                SubstitutionNotes(-1)
            } else {
                val stress = replaceType.stress
                val longDur = if(isStaccato) actualDuration - accentPlusRestDur else duration - accentPlusRestDur
                val stressedVelocity = if (velocity + stress > 127) 127 else velocity + stress
                if(isRetrograde){
                    SubstitutionNotes(
                        index, listOf(pitch, pitch),
                        listOf(tick, tick + longDur),
                        listOf(longDur, accentDur),
                        listOf(velocity, stressedVelocity),
                        listOf(0, 0),
                        listOf(0, attack),
                        listOf(isPreviousRest, false),
                        if (articulationDuration == null) null else listOf(longDur, accentDur),
                        if (ribattuto == null) null else listOf(ribattuto, ribattuto)
                    )
                } else {
                    val diff = if(isStaccato || glissando != 0) 0 else actualDuration - duration
                    println("pitch:$pitch gliss:$glissando diff:$diff longDur:$longDur")
                    SubstitutionNotes(
                        index, listOf(pitch, pitch),
                        listOf(tick, tick + accentPlusRestDur),
                        listOf(accentDur, longDur),
                        listOf(stressedVelocity, velocity),
                        listOf(0, glissando),
                        listOf(attack, 0),
                        listOf(isPreviousRest, true),
                        if (articulationDuration == null) null else listOf(accentDur, longDur+diff),
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
        is ReplaceType.Cromatica, is ReplaceType.Diatonica,
        is ReplaceType.CromaticaDiCambio, is ReplaceType.DiatonicaDiCambio -> { trackData, index, trackDataList ->
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
                    is ReplaceType.CromaticaDiCambio -> semitones * 2 - 1
                    is ReplaceType.DiatonicaDiCambio -> if (semitones<=2) 3 else (semitones / 2 + semitones % 2) * 2 - 1
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
                        is ReplaceType.CromaticaDiCambio -> findChromaticScaleDiCambio(startPitch, endPitch, isRetrograde)
                        is ReplaceType.DiatonicaDiCambio -> findWholeToneScaleDiCambio(startPitch, endPitch, isRetrograde)
                        else -> findChromaticScale(startPitch, endPitch, isRetrograde)
                    }
                    //println("semitones=$semitones nNotes = $nNotes  interval=$startPitch-$endPitch pitches=$pitches")
                    val stress = replaceType.stress
                    val stressedVelocity = if (velocity + stress > 127) 127 else velocity + stress
                    val diff = if(isStaccato || glissando != 0) 0 else actualDuration - duration
                    SubstitutionNotes(
                        index, pitches, ticks, durs,
                        if(isRetrograde) listOf(stressedVelocity, *List(nNotes-1){velocity}.toTypedArray())
                        else listOf(velocity, stressedVelocity, *List(nNotes-2){velocity}.toTypedArray()),
                        //if(isRetrograde) listOf(*List(nNotes-1){0}.toTypedArray(), glissando) else List(nNotes) {0},
                        if(isRetrograde) findGlissandoForRetrogradeScales(pitches, glissando, 12)//.apply{println("glissando:$this")}
                            else findGlissandoForScales(pitches,endPitch,12),//.apply{println("glissando:$this")},
                        List(nNotes){attack},
                        listOf(isPreviousRest, *List(nNotes-1){false}.toTypedArray()),
                        if(articulationDuration == null) null
                            else listOf(*durs.dropLast(1).toTypedArray(), durs.last() + diff),
                        if (ribattuto == null) null else List(nNotes){ribattuto}
                    )
                }
            }
        }
        is ReplaceType.OscillazioneCromatica, is ReplaceType.OscillazioneDiatonica,
        is ReplaceType.Irregular2m, is ReplaceType.Irregular2M-> { trackData, index, trackDataList ->
            val (pitch, tick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto)
                    = trackData.extractNoteDataAtIndex(index)
            val actualDuration = articulationDuration ?: duration
            val isStaccato = actualDuration < duration
            val (durs, div) = findOscillationDurations(if (!isStaccato) duration else actualDuration)
            if (div == -1 || pitch > 106 || pitch < 23) { // fake substitution
                //println("TRILLO:$index No Subs: actual duration = $actualDuration")
                SubstitutionNotes(-1) // will not be considered
            } else {
                val stress = replaceType.stress
                val stressedVelocity = if (velocity + stress > 127) 127 else velocity + stress
                val nextPitch = trackData.pitches.getOrElse(index + 1) { pitch }
                val radius = if(replaceType is ReplaceType.OscillazioneDiatonica) 2 else 1
                val pitches = when(replaceType) {
                    is ReplaceType.OscillazioneCromatica, is ReplaceType.OscillazioneDiatonica -> findOscillationPitches(div, pitch, nextPitch, radius)
                    is ReplaceType.Irregular2m -> findIrregularPitches(div, pitch, mutableListOf(pitch-1, pitch, pitch+1))
                    is ReplaceType.Irregular2M -> findIrregularPitches(div, pitch, mutableListOf(pitch-2, pitch-1, pitch, pitch+1, pitch+2))
                    else -> findOscillationPitches(div, pitch, nextPitch, radius)
                }
                val glissList = if(replaceType.addGliss) findGlissandoForRetrogradeScales(pitches, glissando, 12)
                                else listOf(*List(div-1){0}.toTypedArray(), glissando)
                val diff = if(isStaccato || glissando != 0) 0 else actualDuration - duration
                val velocities = (0 until div).map{ if(it % 2 == 0) stressedVelocity else velocity }
                SubstitutionNotes(
                    index,
                    pitches,
                    findTicksFromDurations(tick, durs),
                    durs,
                    velocities,
                    glissList,
                    List(div){attack},
                    listOf(isPreviousRest, *List(div-1){false}.toTypedArray()),
                    if (articulationDuration == null) null
                    else listOf(*durs.dropLast(1).toTypedArray(), durs.last() + diff),
                    if (ribattuto == null) null else List(div){ribattuto}
                )//.apply{ println("div:$div $this") }
            }
        }
        is ReplaceType.Trillo2m, is ReplaceType.Trillo2M -> { trackData, index, trackDataList ->
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
                val secondPitch = if(replaceType is ReplaceType.Trillo2m) {
                    when {
                        pitch == 108 -> pitch - 1
                        pitch == 21 -> pitch + 1
                        pitch <= nextPitch -> pitch - 1
                        pitch > nextPitch -> pitch + 1
                        else -> pitch - 1
                    }
                } else {
                    when {
                        pitch >= 107 -> pitch - 2
                        pitch <= 22 -> pitch + 2
                        pitch <= nextPitch -> pitch - 2
                        pitch > nextPitch -> pitch + 2
                        else -> pitch - 2
                    }
                }
                var lastTick = tick
                val ticks = (0 until div-1).map{
                    lastTick += durs[it]
                    lastTick
                }
                val diff = if(isStaccato || glissando != 0) 0 else actualDuration - duration
                val pitches = if(replaceType.isRetrograde) (0 until div).map { if(it % 2 == 0) secondPitch else pitch }
                else (0 until div).map { if(it % 2 == 0) pitch else secondPitch }
                SubstitutionNotes(
                    index,
                    pitches,
                    listOf(tick, *ticks.toTypedArray()),
                    durs,
                    listOf(stressedVelocity, *List(div-1){velocity}.toTypedArray()),
                    findGlissandoForRetrogradeScales(pitches,glissando,12),
                    List(div){attack},
                    listOf(isPreviousRest, *List(div-1){false}.toTypedArray()),
                    if (articulationDuration == null) null
                        else listOf(*durs.dropLast(1).toTypedArray(), durs.last() + diff),
                    if (ribattuto == null) null else List(div){ribattuto}
                )
                  .apply { println("TRILLO:$index duration: $duration, artDur: $articulationDuration $this") }
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
                val glissList = if(replaceType.addGliss){
                    if(secondPitch > pitch) listOf(1,-1,glissando) else listOf(-1,1,glissando)
                } else listOf(0,0,glissando)
                val diff = if(isStaccato || glissando != 0) 0 else actualDuration - duration
                SubstitutionNotes(
                    index,
                    listOf(pitch, secondPitch, pitch),
                    findTicksFromDurations(tick, durs),
                    durs,
                    if(isRetrograde) listOf(velocity, stressedVelocity, velocity)
                        else listOf(stressedVelocity, velocity, velocity),
                    glissList,
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
                    val glissList = if(replaceType.addGliss){
                        if(secondPitch > pitch) listOf(1,-1,1,-1,1,-1,glissando) else listOf(-1,1,-1,1,-1,1,glissando)
                    } else listOf(0,0,0,0,0,0,glissando)
                    val diff = if(isStaccato || glissando != 0) 0 else actualDuration - duration
                    SubstitutionNotes(index, listOf(pitch, secondPitch, pitch, secondPitch, pitch, secondPitch, pitch),
                        findTicksFromDurations(tick, durs),
                        durs,
                        if(isRetrograde) listOf(velocity, stressedVelocity, velocity, velocity, velocity, velocity, velocity)
                        else listOf(stressedVelocity, velocity, velocity, velocity, velocity, velocity, velocity),
                        glissList,
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
                val glissList = if(replaceType.addGliss){
                    if(secondPitch > pitch) listOf(1,-1,1,-1,glissando) else listOf(-1,1,-1,1,glissando)
                } else listOf(0,0,0,0,glissando)
                val diff = if(isStaccato || glissando != 0) 0 else actualDuration - duration
                SubstitutionNotes(index, listOf(pitch, secondPitch, pitch, secondPitch, pitch),
                    findTicksFromDurations(tick, durs),
                    durs,
                    if(isRetrograde) listOf(velocity, stressedVelocity, velocity, velocity, velocity)
                        else listOf(stressedVelocity, velocity, velocity, velocity, velocity),
                    glissList,
                    listOf(attack, attack, attack, attack, attack),
                    listOf(isPreviousRest, false, false, false, false),
                    if(articulationDuration == null) null
                        else listOf(*durs.dropLast(1).toTypedArray(),durs.last() + diff),
                    if(ribattuto == null) null else listOf(ribattuto,ribattuto,ribattuto, ribattuto, ribattuto) )
                //    .apply { println("MORDENTE 2X:$index duration: $duration, artDur: $articulationDuration $this") }
            }
        }
        is ReplaceType.Gruppetto, is ReplaceType.Cambio -> { trackData, index, trackDataList ->
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
                val velocities = if(isRetrograde){
                    listOf(velocity, stressedVelocity, velocity, velocity, velocity)
                } else {
                    listOf(stressedVelocity, velocity, velocity, velocity, velocity)
                }
                val diff = if(isStaccato || glissando != 0) 0 else actualDuration - duration
                if(replaceType is ReplaceType.Cambio){
                    val (pitches, glissList) = findCambioPitches(pitch, nextPitch, replaceType.addGliss, glissando)//.apply { println(this) }
                    val newDurs = if(isRetrograde) listOf(durs[0] + durs[1], durs[2], durs[3], durs[4])
                                  else listOf(durs[0], durs[1], durs[2], durs[3] + durs[4])
                    SubstitutionNotes(index, pitches,
                        findTicksFromDurations(tick, newDurs),
                        newDurs, velocities.dropLast(1),
                        glissList,
                        listOf(attack, attack, attack, attack),
                        listOf(isPreviousRest, false, false, false),
                        if(articulationDuration == null) null
                        else listOf(*newDurs.dropLast(1).toTypedArray(), newDurs.last() + diff),
                        if(ribattuto == null) null else listOf(ribattuto,ribattuto,ribattuto, ribattuto) )
                    //  .apply { println("GRUPPETTO:$index duration: $duration, artDur: $articulationDuration $this") }
                } else {
                    val (pitches, glissList) =  findGruppettoPitches(pitch, nextPitch, replaceType.addGliss, glissando).apply { println(this) }
                    SubstitutionNotes(index, pitches,
                        findTicksFromDurations(tick, durs),
                        durs, velocities,
                        glissList,
                        listOf(attack, attack, attack, attack, attack),
                        listOf(isPreviousRest, false, false, false, false),
                        if(articulationDuration == null) null
                        else listOf(*durs.dropLast(1).toTypedArray(), durs.last() + diff),
                        if(ribattuto == null) null else listOf(ribattuto,ribattuto,ribattuto, ribattuto, ribattuto) )
                    //  .apply { println("GRUPPETTO:$index duration: $duration, artDur: $articulationDuration $this") }
                }
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
                val isAscendantDescendant = pitch > nextPitch
                val diff = if(isStaccato || glissando != 0) 0 else actualDuration - duration
                //println("pitch:$pitch gliss:$glissando diff:$diff")
                if(replaceType.addGliss){
                    val secondPitch = if(isAscendantDescendant) pitch + 2 else pitch - 2
                    val newDurs = if(isRetrograde) listOf(durs[0], durs[1]+durs[2], durs[3]+durs[4]) else
                        listOf(durs[0]+durs[1], durs[2]+durs[3], durs[4])
                    SubstitutionNotes(index, listOf(pitch, secondPitch, pitch),
                        findTicksFromDurations(tick, newDurs),
                        newDurs,
                        listOf(velocity, stressedVelocity, velocity),
                        if(isAscendantDescendant) listOf(2,-2,glissando) else listOf(-2,2,glissando),
                        listOf(attack, attack, attack),
                        listOf(isPreviousRest, false, false,),
                        if(articulationDuration == null) null
                        else listOf(*newDurs.dropLast(1).toTypedArray(), newDurs.last() + diff),
                        if(ribattuto == null) null else listOf(ribattuto,ribattuto,ribattuto) )
                    //  .apply { println("ONDA:$index duration: $duration, artDur: $articulationDuration $this") }
                } else {
                    val middleVelocity = (stressedVelocity + velocity) / 2
                    val (secondAndFourthPitch, thirdPitch) = if(isAscendantDescendant) Pair(pitch+1, pitch +2) else Pair(pitch-1, pitch -2)
                    //println("glissList:$glissList")
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
        is ReplaceType.Ribattuto -> { trackData, index, trackDataList ->
            val (pitch, tick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto) = trackData.extractNoteDataAtIndex(index)
            SubstitutionNotes(-1 )
        }
        is ReplaceType.SOS -> { trackData, index, trackDataList ->
            val (pitch, tick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto) = trackData.extractNoteDataAtIndex(index)
            SubstitutionNotes(-1 )
        }
        is ReplaceType.Velocity, is ReplaceType.Glissando, is ReplaceType.Attack   -> { trackData, index, trackDataList ->
                var (pitch, tick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto) = trackData.extractNoteDataAtIndex(index)
                val stress = replaceType.stress
                when (replaceType) {
                    is ReplaceType.Velocity -> {
                        val negativity = if(replaceType.isRetrograde) -1 else 1
                        velocity += (stress * negativity)
                        velocity = velocity.coerceIn(0,127)
                    }
                    is ReplaceType.Glissando -> {
                        val actualDuration = articulationDuration ?: duration + 1
                        val isStaccato = actualDuration < duration
                        articulationDuration = if(isStaccato) articulationDuration else duration // avoid Legato with Glissando
                        val nextPitch = trackData.pitches.getOrElse(index+1) { pitch }
                        glissando = (nextPitch - pitch).coerceIn(-12, +12)
                    }
                    is ReplaceType.Attack ->{
                        attack += stress
                        attack += attack.coerceIn(0, 127)
                    }
                    else -> {}
                }
            SubstitutionNotes(index, listOf(pitch),
                listOf(tick),
                listOf(duration),
                listOf(velocity),
                listOf(glissando),
                listOf(attack),
                listOf(isPreviousRest),
                if(articulationDuration == null) null
                else listOf(articulationDuration),
                if(ribattuto == null) null else listOf(ribattuto) )
            //  .apply { println("ONDA:$index duration: $duration, artDur: $articulationDuration $this") }
            }
    }
}


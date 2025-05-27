package com.cristianovecchi.mikrokanon.AIMUSIC

data class DrumsData(val type: DrumsType = DrumsType.NONE,val drumKit: DrumKits = DrumKits.FULL,
                     val density: Float = 0.5f, val volume: Float = 0.5f, val pattern: Int = 0, val resize: Float = 1f,
                    ){
    fun describe(): String {
        val patternTitle = RhythmPatterns.values()[pattern].title
        val resize = when {
            resize == 1f -> ""
            resize < 1f -> " →${(resize * 100).toInt()}%←"
            else -> " ←${(resize * 100).toInt()}%→"
        }
        return when(type){
            DrumsType.NONE -> "  ---  ${this.type.title}  ---"
            DrumsType.PATTERN -> "  ---  $patternTitle$resize  ${this.drumKit.title}  ---\n    Density: ${String.format("%.0f%%", this.density * 100)}   Volume: ${String.format("%.0f%%", this.volume * 100)}"
            else -> "  ---  ${this.type.title}  ${this.drumKit.title}  ---\n    Density: ${String.format("%.0f%%",this.density*100)}   Volume: ${String.format("%.0f%%",this.volume*100)}"
        }


    }
    fun toCsv(): String {
        return "${this.type.ordinal}|${this.drumKit.ordinal}|${this.density}|${this.volume}|$pattern|$resize"
    }
    companion object{
        fun createDrumsDatasFromCsv(csv: String): List<DrumsData>{
            if(csv.isBlank()) return listOf()
            val values = csv.split(",")
            val drumsTypeValues = DrumsType.values()
            val drumKitsValues = DrumKits.values()
            return values.map{
                val subValues = it.split("|")
                val drumKit = subValues.getOrElse(1) {"0"}
                val density = subValues.getOrElse(2) {"0.5"}
                val volume = subValues.getOrElse(3) {"0.5"}
                val pattern = subValues.getOrElse(4) {"0"}
                val resize = subValues.getOrElse(5) {"1.0"}
                DrumsData(drumsTypeValues[subValues[0].toInt()],
                    drumKitsValues[drumKit.toInt()],
                    density.toFloat(), volume.toFloat(), pattern.toInt(), resize.toFloat())
            }
        }
    }
}
enum class DrumsType(val title:String) {
    NONE("No Drums"),
    PATTERN("PATTERN"),
    PITCHES_DURS("NOTES+DURS"),
    DURS_PITCHES("DURS+NOTES"),
    PITCHES_VELS("NOTES+VELS"),
    VELS_PITCHES("VELS+NOTES"),
    DURS_VELS("DURS+VELS"),
    VELS_DURS("VELS+DURS"),
    TICKS_PITCHES("TIME+NOTES"),
    PITCHES_TICKS("NOTES+TIME"),
    TICKS_DURS("TIME+DURS"),
    DURS_TICKS("DURS+TIME"),
    TICKS_VELS("TIME+VELS"),
    VELS_TICKS("VELS+TIME"),

}
enum class DrumKits(val title:String, val drumKit: DrumKit){
    FULL("Full", DrumKit(
        intArrayOf(ELECTRIC_BASS_DRUM),
        intArrayOf(ACOUSTIC_SNARE, SNARE_OFF, TAMBOURINE),
        intArrayOf(SIDE_STICK, HAND_CLAP),
        intArrayOf(CLOSED_HI_HAT, PEDAL_HI_HAT, OPEN_HI_HAT),
        intArrayOf(LOW_TOM, LOW_MID_TOM, HI_MID_TOM, HIGH_TOM),
        intArrayOf(CRASH_CYMBAL_1, CRASH_CYMBAL_2, SPLASH_CYMBAL, RIDE_CYMBAL_1, RIDE_CYMBAL_2, CHINESE_CYMBAL),
        intArrayOf(MUTE_TRIANGLE, OPEN_TRIANGLE),
        intArrayOf(RIDE_BELL, COWBELL),
        intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(),
        intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(),
    )),
    SKIN("Skin", DrumKit(
        intArrayOf(ACOUSTIC_BASS_DRUM),
        intArrayOf(ACOUSTIC_SNARE, SNARE_OFF, TAMBOURINE),
        intArrayOf(), intArrayOf(),
        intArrayOf(LOW_TOM, LOW_MID_TOM, HI_MID_TOM, HIGH_TOM),
        intArrayOf(), intArrayOf(), intArrayOf(),
        intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(),
        intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(),
    )),
    IRON("Iron", DrumKit(
        intArrayOf(), intArrayOf(), intArrayOf(),
        intArrayOf(CLOSED_HI_HAT, PEDAL_HI_HAT, OPEN_HI_HAT),
        intArrayOf(),
        intArrayOf(CRASH_CYMBAL_1, CRASH_CYMBAL_2, SPLASH_CYMBAL, RIDE_CYMBAL_1, RIDE_CYMBAL_2, CHINESE_CYMBAL),
        intArrayOf(MUTE_TRIANGLE, OPEN_TRIANGLE),
        intArrayOf(RIDE_BELL, COWBELL),
        intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(),
        intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(),
    )),
    MINI("Mini", DrumKit(
        intArrayOf(ELECTRIC_BASS_DRUM),
        intArrayOf(SNARE_OFF),
        intArrayOf(SIDE_STICK),
        intArrayOf(CLOSED_HI_HAT),
        intArrayOf(HI_MID_TOM),
        intArrayOf(CRASH_CYMBAL_1, RIDE_CYMBAL_1),
        intArrayOf(MUTE_TRIANGLE),
        intArrayOf(COWBELL),
        intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(),
        intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(),
    )),
    TOMS("Toms", DrumKit(
        intArrayOf(),
        intArrayOf(),
        intArrayOf(),
        intArrayOf(),
        intArrayOf(LOW_TOM, LOW_MID_TOM, HI_MID_TOM, HIGH_TOM),
        intArrayOf(), intArrayOf(), intArrayOf(),
        intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(),
        intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf()
    )),
    BELL("Bell", DrumKit(
        intArrayOf(),
        intArrayOf(TAMBOURINE),
        intArrayOf(),
        intArrayOf(),
        intArrayOf(),
        intArrayOf(CHINESE_CYMBAL), intArrayOf(MUTE_TRIANGLE, OPEN_TRIANGLE), intArrayOf(RIDE_BELL, COWBELL),
        intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(LOW_AGOGO, HIGH_AGOGO), intArrayOf(),
        intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf()
    )),
    CUBA("Cuba", DrumKit(
        intArrayOf(ACOUSTIC_BASS_DRUM),
        intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf(), intArrayOf()
    )),
    MAXI("Maxi", DrumKit())

}
data class DrumKit(
    val bassDrum: IntArray = intArrayOf(ELECTRIC_BASS_DRUM),
    val snare: IntArray = intArrayOf(ELECTRIC_SNARE, SNARE_OFF, TAMBOURINE),
    val stickAndClaps: IntArray = intArrayOf(SIDE_STICK, HAND_CLAP),
    val hiHats: IntArray = intArrayOf(CLOSED_HI_HAT, PEDAL_HI_HAT, OPEN_HI_HAT),
    val toms: IntArray = intArrayOf(LOW_TOM, LOW_MID_TOM, HI_MID_TOM, HIGH_TOM),
    val cymbals: IntArray = intArrayOf(CRASH_CYMBAL_1, CRASH_CYMBAL_2, SPLASH_CYMBAL, RIDE_CYMBAL_1, RIDE_CYMBAL_2, CHINESE_CYMBAL),
    val triangles: IntArray = intArrayOf(MUTE_TRIANGLE, OPEN_TRIANGLE),
    val bells: IntArray = intArrayOf(RIDE_BELL, COWBELL),

    val bongos: IntArray = intArrayOf(LOW_BONGO, HIGH_BONGO),
    val congas: IntArray = intArrayOf(LOW_CONGA, OPEN_HIGH_CONGA, MUTE_HIGH_CONGA),
    val timbales: IntArray = intArrayOf(LOW_TIMBALE, HIGH_TIMBALE),
    val agogos: IntArray = intArrayOf(LOW_AGOGO, HIGH_AGOGO),
    val rubbers: IntArray = intArrayOf(VIBRASLAP, CABASA, MARACAS),
    val whistles: IntArray = intArrayOf(SHORT_WHISTLE, LONG_WHISTLE),
    val guiros: IntArray = intArrayOf(SHORT_GUIRO, LONG_GUIRO),
    val claves: IntArray = intArrayOf(CLAVES),
    val woodblocks: IntArray = intArrayOf(LOW_WOODBLOCK, HIGH_WOODBLOCK),
    val cuicas: IntArray = intArrayOf(MUTE_CUICA, OPEN_CUICA)

) {
    fun totalOfSounds(): Int {
        return bassDrum.size + snare.size + stickAndClaps.size + hiHats.size + toms.size + cymbals.size + triangles.size + bells.size + bongos.size + congas.size + timbales.size + agogos.size + rubbers.size + whistles.size + guiros.size + claves.size + woodblocks.size + cuicas.size
    }
    fun soundList(): IntArray {
        return bassDrum + snare + stickAndClaps + hiHats + toms + cymbals + triangles + bells + bongos + congas + timbales + agogos + rubbers + whistles + guiros + claves + woodblocks + cuicas
    }
}
const val ACOUSTIC_BASS_DRUM = 35
const val ELECTRIC_BASS_DRUM = 36

const val SIDE_STICK = 37
const val HAND_CLAP = 39
const val ACOUSTIC_SNARE = 38
const val ELECTRIC_SNARE = 40
const val SNARE_OFF = 50
const val TAMBOURINE = 54

const val CLOSED_HI_HAT = 42
const val PEDAL_HI_HAT = 44
const val OPEN_HI_HAT = 46

const val LOW_TOM = 45
const val LOW_MID_TOM = 47
const val HI_MID_TOM = 48
const val HIGH_TOM = 50

const val CRASH_CYMBAL_1 = 49
const val CRASH_CYMBAL_2 = 57
const val SPLASH_CYMBAL = 55
const val RIDE_CYMBAL_1 = 51
const val RIDE_CYMBAL_2 = 55
const val CHINESE_CYMBAL = 52

const val RIDE_BELL = 53
const val COWBELL = 59

const val LOW_BONGO = 61
const val HIGH_BONGO = 60

const val LOW_CONGA = 64
const val OPEN_HIGH_CONGA = 63
const val MUTE_HIGH_CONGA = 60

const val LOW_TIMBALE = 66
const val HIGH_TIMBALE = 65

const val LOW_AGOGO = 68
const val HIGH_AGOGO = 67

const val VIBRASLAP = 58
const val CABASA = 69
const val MARACAS = 70

const val SHORT_WHISTLE = 71
const val LONG_WHISTLE = 72

const val SHORT_GUIRO = 73
const val LONG_GUIRO = 74

const val CLAVES = 75

const val LOW_WOODBLOCK = 77
const val HIGH_WOODBLOCK = 76

const val MUTE_CUICA = 78
const val OPEN_CUICA = 79

const val MUTE_TRIANGLE = 80
const val OPEN_TRIANGLE = 81














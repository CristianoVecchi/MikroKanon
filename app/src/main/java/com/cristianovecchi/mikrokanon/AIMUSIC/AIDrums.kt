package com.cristianovecchi.mikrokanon.AIMUSIC

data class DrumsData(val type: DrumsType = DrumsType.NONE,val drumKit: DrumKits = DrumKits.FULL,
                     val density: Float = 0.5f, val volume: Float = 0.5f,
                    ){
    fun describe(): String {
        return if(type == DrumsType.NONE) "  ---  ${this.type.title}  ---"
        else "  ---  ${this.type.title}  ${this.drumKit.title}  ---\nDensity: ${String.format("%.0f%%",this.density*100)}   Volume: ${String.format("%.0f%%",this.volume*100)}"
    }
    fun toCsv(): String {
        return "${this.type.ordinal}|${this.drumKit.ordinal}|${this.density}|${this.volume}"
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
                DrumsData(drumsTypeValues[subValues[0].toInt()],
                    drumKitsValues[drumKit.toInt()],
                    density.toFloat(), volume.toFloat())
            }
        }
    }
}
enum class DrumsType(val title:String) {
    NONE("No Drums"), CENTROIDS("CENTROIDS")
}
enum class DrumKits(val title:String, val drumKit: DrumKit){
    FULL("Full", DrumKit()),
    SKIN("Skin", DrumKit(
        intArrayOf(ACOUSTIC_BASS_DRUM),
        intArrayOf(ACOUSTIC_SNARE, SNARE_OFF),
        intArrayOf(), intArrayOf(),
        intArrayOf(LOW_TOM, LOW_MID_TOM, HI_MID_TOM, HIGH_TOM),
        intArrayOf(), intArrayOf()
    )),
    IRON("Iron", DrumKit(
        intArrayOf(), intArrayOf(), intArrayOf(),
        intArrayOf(CLOSED_HI_HAT, PEDAL_HI_HAT, OPEN_HI_HAT),
        intArrayOf(),
        intArrayOf(CRASH_CYMBAL_1, CRASH_CYMBAL_2, SPLASH_CYMBAL, RIDE_CYMBAL_1, RIDE_CYMBAL_2, CHINESE_CYMBAL),
        intArrayOf(RIDE_BELL, COWBELL)
    ))

}
data class DrumKit(
    val bassDrum: IntArray = intArrayOf(ELECTRIC_BASS_DRUM),
    val snare: IntArray = intArrayOf(ELECTRIC_SNARE, SNARE_OFF),
    val stickAndClaps: IntArray = intArrayOf(SIDE_STICK, HAND_CLAP),
    val hiHats: IntArray = intArrayOf(CLOSED_HI_HAT, PEDAL_HI_HAT, OPEN_HI_HAT),
    val toms: IntArray = intArrayOf(LOW_TOM, LOW_MID_TOM, HI_MID_TOM, HIGH_TOM),
    val cymbals: IntArray = intArrayOf(CRASH_CYMBAL_1, CRASH_CYMBAL_2, SPLASH_CYMBAL, RIDE_CYMBAL_1, RIDE_CYMBAL_2, CHINESE_CYMBAL),
    val bells: IntArray = intArrayOf(RIDE_BELL, COWBELL)
) {
    fun totalOfSounds(): Int {
        return bassDrum.size + snare.size + stickAndClaps.size + hiHats.size + toms.size + cymbals.size + bells.size
    }
    fun soundList(): IntArray {
        return bassDrum + snare + stickAndClaps + hiHats + toms + cymbals + bells
    }
}
const val ACOUSTIC_BASS_DRUM = 35
const val ELECTRIC_BASS_DRUM = 36

const val SIDE_STICK = 37
const val HAND_CLAP = 39
const val ACOUSTIC_SNARE = 38
const val ELECTRIC_SNARE = 40
const val SNARE_OFF = 50

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
const val VIBRASLAP = 58






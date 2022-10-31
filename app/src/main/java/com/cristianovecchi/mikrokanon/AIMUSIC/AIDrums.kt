package com.cristianovecchi.mikrokanon.AIMUSIC

enum class DrumsType {
    NONE, CENTROIDS
}
data class DrumsData(val type: DrumsType = DrumsType.NONE,
                     val density: Float = 0.5f, val volume: Float = 0.5f,
                    val drumKit: DrumKit = DrumKit())

data class DrumKit(
    val bassDrum: IntArray = intArrayOf(ELECTRIC_BASS_DRUM),
    val snare: IntArray = intArrayOf(ELECTRIC_SNARE),
    val handClap: IntArray = intArrayOf(HAND_CLAP),
    val hiHats: IntArray = intArrayOf(CLOSED_HI_HAT, OPEN_HI_HAT),
    val toms: IntArray = intArrayOf(LOW_TOM, LOW_MID_TOM, HI_MID_TOM, HIGH_TOM),
    val cymbals: IntArray = intArrayOf(CRASH_CYMBAL_1, CRASH_CYMBAL_2, SPLASH_CYMBAL, RIDE_CYMBAL_1, RIDE_CYMBAL_2),
    val bells: IntArray = intArrayOf(RIDE_BELL, COWBELL)
) {
    fun totalOfSounds(): Int {
        return bassDrum.size + snare.size + handClap.size + hiHats.size + toms.size + cymbals.size + bells.size
    }
    fun soundList(): IntArray {
        return bassDrum + snare + handClap + hiHats + toms + cymbals + bells
    }
}
const val ACOUSTIC_BASS_DRUM = 35
const val ELECTRIC_BASS_DRUM = 36

const val HAND_CLAP = 39
const val ELECTRIC_SNARE = 40

const val CLOSED_HI_HAT = 42
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

const val RIDE_BELL = 53
const val COWBELL = 59






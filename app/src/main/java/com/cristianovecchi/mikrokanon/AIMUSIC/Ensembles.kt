package com.cristianovecchi.mikrokanon.AIMUSIC

import com.cristianovecchi.mikrokanon.extractFromMiddle
import com.cristianovecchi.mikrokanon.rangeTo

enum class EnsembleType {
    STRINGS, WOODWINDS, STRING_ORCHESTRA, BRASS, GAMELAN, SAXOPHONES, FLUTES,
    DOUBLE_REEDS,  CLARINETS, FRENCH_HORNS, BASSOONS, CELLOS, PIANO, HARP, PIERROT,
    BAROQUE, PLUCKED_STRINGS, CHOIR, SPOOKY,
    NYLON_GUITAR, STEEL_GUITAR, JAZZ_GUITAR, CLEAN_GUITAR, MUTED_GUITAR, OVERDRIVE_GUITAR, DISTORTION_GUITAR, BANJO,
    ACOUSTIC_BASS, FRETLESS_BASS, SLAP_BASS_1, SYN_BASS_1,
    TREMOLO_STRINGS, PIZZICATO, FIDDLE, MUTED_BRASS,
    BAG_PIPES, RECORDER, SHANAI, SITAR, SHAMISEN, KOTO,
    HARPSICHORD, XYLOPHONE, MARIMBA, KALIMBA, VIBRAPHONE, GLOCKENSPIEL, CELESTA, BELLS, TINKLE_BELLS, AGOGO, STEEL_DRUMS, REVERSE_CYMBALS,
    TIMPANI, WOODBLOCKS, TAIKO_DRUMS, MELODIC_TOMS, SYN_DRUMS,
    ELECTRIC_PIANO_1, ELECTRIC_PIANO_2,
    HAMMOND_ORGAN, PERC_ORGAN, BLUES_ORGAN, CHURCH_ORGAN, REED_ORGAN, ACCORDION, TANGO_ACCORDION,
    SYN_SQUARE_WAVE, SYN_SAW_WAVE, SYN_CALLIOPE, SYN_CHIFF,
    SYN_CHARANG, SYN_VOICE, SYN_FIFTHS_SAW, SYN_BRASS_AND_LEAD,
    SYN_FANTASIA, WARM_PAD, POLYSYNTH, SPACE_VOX,
    BOWED_GLASS, METAL_PAD, HALO_PAD, SWEEP_PAD,
    ICE_RAIN, SOUNDTRACK, CRYSTAL, ATMOSPHERE,
    BRIGHTNESS, GOBLINS, ECHO_DROPS, SCI_FI
}
fun  List<EnsemblePart>.display() {
    this.forEach {  println(it) }
    println()
}
data class EnsemblePart( val instrument: Int, val octave: Int,
                         val allRange: IntRange = PIANO_ALL,
                         val colorRange: IntRange = allRange,
                         val familyRange: IntRange = allRange,){

    fun getRangeByType(rangeType: Int): IntRange {
        return when(rangeType) {
            1 -> allRange
            2 -> colorRange
            3 -> colorRange.extractFromMiddle(8)
            4 -> colorRange.extractFromMiddle(6)
            else -> familyRange
        }
    }

    fun getOctavedRangeByType(rangeType: Int, octaveTranspose: Int, upperPart: Boolean, familyRange: IntRange): IntRange {
        val range = getRangeByType(rangeType)
        //println("rangeType:$rangeType octaveTranspose:$octaveTranspose upperPart:$upperPart familyRange:$familyRange range:$range")
        return when (octaveTranspose) {
            3 -> if(upperPart) range.octaveTranspose(1, familyRange) else range.octaveTranspose(-1, familyRange)
            4 -> if(upperPart) range.octaveTranspose(2, familyRange) else range.octaveTranspose(-2, familyRange)
            else -> range.octaveTranspose(octaveTranspose, familyRange)
        }//.apply{println("Return from OctavedRangeByType:$this")}
//    .also{
//        if(it == IntRange(10,1000)) println("range: $it rangeType:$rangeType octaveTranspose:$octaveTranspose upperPart:$upperPart")
    //        }
    }
}

object Ensembles {
    fun getEnsembleMix(nParts: Int, types: List<EnsembleType>):  List<EnsemblePart> {
       //println("nParts: $nParts    Ensemble Types: $types")
        val mix = types.map{ getEnsemble(nParts, it)}
     //   mix.forEachIndexed{ i, it -> println("ensemble #$i: $it")}
        return (0 until nParts).toList().map{
            mix[it % types.size][it]}
    }
    fun getEnsemblesListMix(nParts: Int, ensemblesList: List<List<EnsembleType>>): List<List<EnsemblePart>>{
        return ensemblesList.map {
            getEnsembleMix(nParts, it)
        }
    }
    fun getEnsemble(nParts: Int, type: EnsembleType): List<EnsemblePart> {
        return when (type) {
            EnsembleType.STRINGS -> getStrings(nParts)
            EnsembleType.WOODWINDS -> getWoodwinds(nParts)
            EnsembleType.STRING_ORCHESTRA -> getStringOrchestra(nParts)
            EnsembleType.BRASS -> getBrass(nParts)
            EnsembleType.GAMELAN -> getGamelan(nParts)
            EnsembleType.SAXOPHONES -> getSaxophones(nParts)
            EnsembleType.FLUTES -> getFlutes(nParts)
            EnsembleType.DOUBLE_REEDS -> getDoubleReeds(nParts)
            EnsembleType.CLARINETS -> getClarinets(nParts)
            EnsembleType.FRENCH_HORNS -> getFrenchHorns(nParts)
            EnsembleType.BASSOONS -> getBassoons(nParts)
            EnsembleType.CELLOS -> getCellos(nParts)
            EnsembleType.PIANO -> getPiano(nParts)
            EnsembleType.HARP -> getHarp(nParts)
            EnsembleType.PIERROT -> getPierrot(nParts)
            EnsembleType.BAROQUE -> getBaroque(nParts)
            EnsembleType.PLUCKED_STRINGS -> getPluckedStrings(nParts)
            EnsembleType.CHOIR -> getKeyboardInstrument(CHOIR_AAHS, nParts, RANGES.OCTAVES_2334455, IntRange(C2, C6 + 2))
            EnsembleType.SPOOKY -> getSpooky(nParts)
            EnsembleType.NYLON_GUITAR -> getKeyboardInstrument(NYLON_GUITAR, nParts, RANGES.GUITAR, IntRange(40, 83)) // 40 - B5
            EnsembleType.STEEL_GUITAR -> getKeyboardInstrument(STEEL_GUITAR, nParts, RANGES.GUITAR, IntRange(40, 83))
            EnsembleType.JAZZ_GUITAR -> getKeyboardInstrument(JAZZ_GUITAR, nParts, RANGES.GUITAR, IntRange(40, 83))
            EnsembleType.CLEAN_GUITAR -> getKeyboardInstrument(CLEAN_GUITAR, nParts, RANGES.GUITAR, IntRange(40, 83))
            EnsembleType.MUTED_GUITAR -> getKeyboardInstrument(MUTED_GUITAR, nParts, RANGES.GUITAR, IntRange(40, 83))
            EnsembleType.OVERDRIVE_GUITAR -> getKeyboardInstrument(OVERDRIVE_GUITAR, nParts, RANGES.GUITAR, IntRange(40, 83))
            EnsembleType.DISTORTION_GUITAR -> getKeyboardInstrument(DISTORTION_GUITAR, nParts, RANGES.GUITAR, IntRange(40, 83))
            EnsembleType.BANJO -> getKeyboardInstrument(BANJO, nParts, RANGES.SYN_SAW, BANJO_ALL)
            EnsembleType.ACOUSTIC_BASS -> getKeyboardInstrument(ACOUSTIC_BASS, nParts, RANGES.BASS_GUITAR, IntRange(E1, G4))
            EnsembleType.FRETLESS_BASS -> getKeyboardInstrument(FRETLESS_BASS, nParts, RANGES.BASS_GUITAR, IntRange(E1, G4))
            EnsembleType.SLAP_BASS_1 -> getKeyboardInstrument(SLAP_BASS_1, nParts, RANGES.BASS_GUITAR, IntRange(E1, G4))
            EnsembleType.SYN_BASS_1 -> getKeyboardInstrument(SYN_BASS_1, nParts, RANGES.BASS_GUITAR, IntRange(E1, G4))
            EnsembleType.TREMOLO_STRINGS -> getKeyboardInstrument(TREMOLO_STRINGS, nParts, RANGES.NO_EXTREME_OCTAVES, IntRange(E1, C8))
            EnsembleType.PIZZICATO -> getKeyboardInstrument(PIZZICATO, nParts, RANGES.NO_EXTREME_OCTAVES, IntRange(E1, C8))
            EnsembleType.FIDDLE -> getKeyboardInstrument(FIDDLE, nParts, RANGES.FIDDLE, IntRange(G3, C8))
            EnsembleType.MUTED_BRASS -> getKeyboardInstrument(MUTED_TRUMPET, nParts, RANGES.OCTAVES_2334455, IntRange(C2, C7))
            EnsembleType.BAG_PIPES -> getKeyboardInstrument(BAG_PIPE, nParts, RANGES.BAG_PIPES, IntRange(C1, C6))
            EnsembleType.RECORDER -> getKeyboardInstrument(RECORDER, nParts, RANGES.RECORDERS, IntRange(C3, C7))
            EnsembleType.SHANAI -> getKeyboardInstrument(SHANAI, nParts, RANGES.SHANAI, IntRange(C4 - 3, C6 - 3))
            EnsembleType.SITAR -> getKeyboardInstrument(SITAR, nParts, RANGES.OCTAVES_2334455, SITAR_ALL)
            EnsembleType.SHAMISEN -> getKeyboardInstrument(SHAMISEN, nParts, RANGES.SHAMISEN, SHAMISEN_ALL)
            EnsembleType.KOTO -> getKeyboardInstrument(KOTO, nParts, RANGES.SYN_SAW, KOTO_ALL)
            EnsembleType.HARPSICHORD -> getKeyboardInstrument(HARPSICHORD, nParts)
            EnsembleType.XYLOPHONE -> getKeyboardInstrument(XYLOPHONE, nParts, RANGES.HALF, IntRange(C4, C8))
            EnsembleType.MARIMBA -> getKeyboardInstrument(MARIMBA, nParts, RANGES.HALF_PLUS_1,  IntRange(C2, C7))
            EnsembleType.KALIMBA -> getKeyboardInstrument(KALIMBA, nParts, RANGES.SHAMISEN,  IntRange(G3, C6 + 2))
            EnsembleType.VIBRAPHONE -> getKeyboardInstrument(VIBRAPHONE, nParts, RANGES.HALF_PLUS_1, IntRange(53, 89)) // F3 - F6
            EnsembleType.GLOCKENSPIEL -> getKeyboardInstrument(GLOCKENSPIEL, nParts, RANGES.CELESTA, IntRange(C3, C7))
            EnsembleType.CELESTA -> getKeyboardInstrument(CELESTA, nParts, RANGES.CELESTA, IntRange(C4, C8))
            EnsembleType.BELLS -> getKeyboardInstrument(TUBULAR_BELLS, nParts, RANGES.BELLS, IntRange(C4, C6)) // G3 - C6
            EnsembleType.TINKLE_BELLS -> getKeyboardInstrument(TINKLE_BELLS, nParts, RANGES.PIANO, IntRange(C1, C8))
            EnsembleType.AGOGO -> getKeyboardInstrument(AGOGO, nParts, RANGES.OCTAVES_2334455, IntRange(C2, C6))
            EnsembleType.STEEL_DRUMS -> getKeyboardInstrument(STEEL_DRUMS, nParts, RANGES.NO_LOWER_OCTAVE, IntRange(C2, C7))
            EnsembleType.REVERSE_CYMBALS -> getKeyboardInstrument(REVERSE_CYMBALS, nParts, RANGES.OCTAVES_2334455, IntRange(C2, C6))
            EnsembleType.TIMPANI -> getKeyboardInstrument(TIMPANI, nParts, RANGES.TIMPANI, IntRange(C2, C4))
            EnsembleType.WOODBLOCKS -> getKeyboardInstrument(WOODBLOCKS, nParts, RANGES.WOODBLOCKS, IntRange(C2, C5))
            EnsembleType.TAIKO_DRUMS -> getKeyboardInstrument(TAIKO_DRUMS, nParts, RANGES.OCTAVES_2334455, IntRange(C2, C6))
            EnsembleType.MELODIC_TOMS -> getKeyboardInstrument(MELODIC_TOMS, nParts, RANGES.OCTAVES_2334455, IntRange(C2, C6))
            EnsembleType.SYN_DRUMS -> getKeyboardInstrument(SYN_DRUMS, nParts, RANGES.OCTAVES_2334455, IntRange(C2, C6))
            EnsembleType.ELECTRIC_PIANO_1 -> getKeyboardInstrument(ELECTRIC_PIANO_1, nParts, RANGES.NO_EXTREME_OCTAVES, IntRange(C2, C8))
            EnsembleType.ELECTRIC_PIANO_2-> getKeyboardInstrument(ELECTRIC_PIANO_2, nParts, RANGES.NO_EXTREME_OCTAVES, IntRange(C2, C8))
            EnsembleType.HAMMOND_ORGAN -> getKeyboardInstrument(HAMMOND_ORGAN, nParts,  RANGES.NO_EXTREME_OCTAVES, IntRange(C2, 89))
            EnsembleType.PERC_ORGAN ->  getKeyboardInstrument(PERC_ORGAN, nParts,  RANGES.NO_EXTREME_OCTAVES, IntRange(C2, C8))
            EnsembleType.BLUES_ORGAN ->  getKeyboardInstrument(BLUES_ORGAN, nParts, RANGES.NO_EXTREME_OCTAVES, IntRange(C2, C8))
            EnsembleType.CHURCH_ORGAN -> getKeyboardInstrument(CHURCH_ORGAN, nParts, RANGES.NO_EXTREME_OCTAVES, IntRange(C2, C8))
            EnsembleType.REED_ORGAN -> getKeyboardInstrument(REED_ORGAN, nParts, RANGES.OCTAVES_2334455, IntRange(C2, 89))
            EnsembleType.ACCORDION ->  getKeyboardInstrument(ACCORDION, nParts, RANGES.HALF_PLUS_1, IntRange(C3, C7))
            EnsembleType.TANGO_ACCORDION ->  getKeyboardInstrument(TANGO_ACCORDION, nParts, RANGES.HALF_PLUS_1, IntRange(C3, C7))
            EnsembleType.SYN_SQUARE_WAVE -> getKeyboardInstrument(SYN_SQUARE_WAVE, nParts)
            EnsembleType.SYN_SAW_WAVE -> getKeyboardInstrument(SYN_SAW_WAVE, nParts, RANGES.SYN_SAW, IntRange(C3, C6 - 1))
            EnsembleType.SYN_CALLIOPE -> getKeyboardInstrument(SYN_CALLIOPE, nParts, RANGES.NO_LOWER_OCTAVE, IntRange(C2, C8))
            EnsembleType.SYN_CHIFF -> getKeyboardInstrument(SYN_CHIFF, nParts, RANGES.NO_LOWER_OCTAVE, IntRange(C2, C8))
            EnsembleType.SYN_CHARANG -> getKeyboardInstrument(SYN_CHARANG, nParts)
            EnsembleType.SYN_VOICE -> getKeyboardInstrument(SYN_VOICE, nParts, RANGES.NO_LOWER_OCTAVE, IntRange(C2, C8))
            EnsembleType.SYN_FIFTHS_SAW -> getKeyboardInstrument(SYN_FIFTHS_SAW, nParts, RANGES.NO_LOWER_OCTAVE, IntRange(C2, C8))
            EnsembleType.SYN_BRASS_AND_LEAD -> getKeyboardInstrument(SYN_BRASS_AND_LEAD, nParts, RANGES.OCTAVES_2334455, IntRange(C2, C6 - 1))
            EnsembleType.SYN_FANTASIA -> getKeyboardInstrument(SYN_FANTASIA, nParts, RANGES.OCTAVES_2334455, IntRange(C2, C8))
            EnsembleType.WARM_PAD -> getKeyboardInstrument(WARM_PAD, nParts, RANGES.NO_LOWER_OCTAVE, IntRange(C2, C8))
            EnsembleType.POLYSYNTH -> getKeyboardInstrument(POLYSYNTH, nParts, RANGES.OCTAVES_2334455, IntRange(C2, C6 - 1))
            EnsembleType.SPACE_VOX -> getKeyboardInstrument(SPACE_VOX, nParts, RANGES.NO_EXTREME_OCTAVES, IntRange(C2, C8))
            EnsembleType.BOWED_GLASS -> getKeyboardInstrument(BOWED_GLASS, nParts)
            EnsembleType.METAL_PAD -> getKeyboardInstrument(METAL_PAD, nParts)
            EnsembleType.HALO_PAD -> getKeyboardInstrument(HALO_PAD, nParts, RANGES.NO_LOWER_OCTAVE, IntRange(C2, C8))
            EnsembleType.SWEEP_PAD -> getKeyboardInstrument(SWEEP_PAD, nParts, RANGES.OCTAVES_2334455, IntRange(C2, C6 - 1))
            EnsembleType.ICE_RAIN -> getKeyboardInstrument(ICE_RAIN, nParts)
            EnsembleType.SOUNDTRACK -> getKeyboardInstrument(SOUNDTRACK, nParts, RANGES.NO_LOWER_OCTAVE, IntRange(C2, C8))
            EnsembleType.CRYSTAL -> getKeyboardInstrument(CRYSTAL, nParts)
            EnsembleType.ATMOSPHERE -> getKeyboardInstrument(ATMOSPHERE, nParts, RANGES.NO_LOWER_OCTAVE, IntRange(C2, C8))
            EnsembleType.BRIGHTNESS -> getKeyboardInstrument(BRIGHTNESS, nParts, RANGES.NO_LOWER_OCTAVE, IntRange(C2, C8))
            EnsembleType.GOBLINS -> getKeyboardInstrument(GOBLINS, nParts, RANGES.NO_LOWER_OCTAVE, IntRange(C2, C8))
            EnsembleType.ECHO_DROPS -> getKeyboardInstrument(ECHO_DROPS, nParts, RANGES.NO_LOWER_OCTAVE, IntRange(C2, C8))
            EnsembleType.SCI_FI -> getKeyboardInstrument(SCI_FI, nParts, RANGES.NO_LOWER_OCTAVE, IntRange(C2, C8))
        }//.onEach { ensemble -> println(ensemble) }
    }

    fun getStrings(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2 -> listOf(
                EnsemblePart(40, 5, VIOLIN_ALL),
                EnsemblePart(42, 3, CELLO_ALL)
            )
            3 -> listOf(
                PART_VIOLIN_MIDDLE_HIGH_HIGHEST,
                PART_VIOLA_MIDDLE_HIGH,
                PART_CELLO_LOW_MIDDLE
            )
            4 -> listOf(
                PART_VIOLIN_HIGH_HIGHEST,
                PART_VIOLIN_MIDDLE_HIGH_HIGHEST,
                PART_VIOLA_LOW_MIDDLE,
                PART_STRING_ORCHESTRA_CELLO_LOW
            )
            5 -> listOf(
                PART_VIOLIN_HIGH_HIGHEST,
                PART_VIOLIN_MIDDLE_HIGH_HIGHEST,
                PART_VIOLA_MIDDLE_HIGH,
                PART_VIOLA_LOW_MIDDLE,
                PART_STRING_ORCHESTRA_CELLO_LOW
            )
            6 -> listOf(
                PART_VIOLIN_HIGH_HIGHEST,
                PART_VIOLIN_MIDDLE_HIGH_HIGHEST,
                PART_VIOLA_MIDDLE_HIGH,
                PART_VIOLA_LOW_MIDDLE,
                PART_CELLO_MIDDLE_HIGH,
                PART_STRING_ORCHESTRA_CELLO_LOW

            )
            7 -> listOf(
                PART_VIOLIN_HIGH_HIGHEST,
                PART_VIOLIN_MIDDLE_HIGH_HIGHEST,
                PART_VIOLA_MIDDLE_HIGH,
                PART_VIOLA_LOW_MIDDLE,
                PART_CELLO_MIDDLE_HIGH,
                PART_STRING_ORCHESTRA_CELLO_LOW,
                PART_STRING_ORCHESTRA_DB_LOW
            )
            8 -> listOf(
                PART_VIOLIN_HIGHEST,
                PART_VIOLIN_HIGH_HIGHEST,
                PART_VIOLIN_MIDDLE_HIGH_HIGHEST,
                PART_VIOLA_MIDDLE_HIGH,
                PART_VIOLA_LOW_MIDDLE,
                PART_CELLO_MIDDLE_HIGH,
                PART_STRING_ORCHESTRA_CELLO_LOW,
                PART_STRING_ORCHESTRA_DB_LOW
            )
            9 -> listOf(
                PART_VIOLIN_HIGHEST,
                PART_VIOLIN_HIGH_HIGHEST,
                PART_VIOLIN_MIDDLE_HIGH_HIGHEST,
                PART_VIOLA_MIDDLE_HIGH,
                PART_VIOLA_MIDDLE,
                PART_VIOLA_LOW_MIDDLE,
                PART_CELLO_MIDDLE_HIGH,
                PART_STRING_ORCHESTRA_CELLO_LOW,
                PART_STRING_ORCHESTRA_DB_LOW
            )
            in 10..12 -> listOf(
                PART_VIOLIN_HIGHEST,
                PART_VIOLIN_HIGHEST,
                PART_VIOLIN_HIGH_HIGHEST,
                PART_VIOLIN_HIGH_HIGHEST,
                PART_VIOLA_MIDDLE_HIGH,
                PART_VIOLA_MIDDLE,
                PART_VIOLA_LOW_MIDDLE,
                PART_CELLO_HIGH_HIGHEST,
                PART_STRING_ORCHESTRA_CELLO_LOW_MIDDLE,
                PART_STRING_ORCHESTRA_CELLO_LOW,
                PART_STRING_ORCHESTRA_DB_LOW_MIDDLE,
                PART_STRING_ORCHESTRA_DB_LOW
            )
            else -> listOf()
        }
    }

    fun getWoodwinds(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2 -> listOf(
                EnsemblePart(73, 5, FLUTE_ALL), //flute
                EnsemblePart(70, 3, BASSOON_ALL)  // bassoon
            )
            3 -> listOf(
                PART_FLUTE_MIDDLE_HIGH,
                PART_CLARINET_MIDDLE_HIGH,
                PART_CLARINET_LOW_MIDDLE
            )
            4 -> listOf(
                PART_FLUTE_MIDDLE_HIGH,
                PART_OBOE_LOW_MIDDLE,
                PART_CLARINET_LOW_MIDDLE,
                PART_BASSOON_LOW_MIDDLE
            )
            5 -> listOf(
                PART_FLUTE_MIDDLE_HIGH,
                PART_OBOE_MIDDLE_HIGH,
                PART_CLARINET_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_BASSOON_LOW_MIDDLE
            )
            6 -> listOf(
                PART_FLUTE_MIDDLE_HIGH,
                PART_OBOE_MIDDLE_HIGH,
                PART_CLARINET_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE,
                PART_BASS_CLARINET_LOW_MIDDLE,
                PART_BASSOON_LOW
            )

            7 -> listOf(
                PART_FLUTE_MIDDLE_HIGH,
                PART_OBOE_MIDDLE_HIGH,
                PART_CLARINET_MIDDLE_HIGH,
                PART_ENGLISH_HORN_LOW_MIDDLE,
                PART_FRENCH_HORN_MIDDLE,
                PART_BASS_CLARINET_LOW_MIDDLE,
                PART_BASSOON_LOW
            )
            8 -> listOf(
                PART_PICCOLO_MIDDLE_HIGH,
                PART_FLUTE_MIDDLE_HIGH,
                PART_OBOE_MIDDLE_HIGH,
                PART_CLARINET_MIDDLE_HIGH,
                PART_ENGLISH_HORN_LOW_MIDDLE,
                PART_FRENCH_HORN_MIDDLE,
                PART_BASS_CLARINET_LOW_MIDDLE,
                PART_BASSOON_LOW
            )
            9 -> listOf(
                PART_PICCOLO_MIDDLE_HIGH,
                PART_FLUTE_MIDDLE_HIGH,
                PART_OBOE_MIDDLE_HIGH,
                PART_CLARINET_MIDDLE_HIGH,
                PART_ENGLISH_HORN_LOW_MIDDLE,
                PART_FRENCH_HORN_MIDDLE,
                PART_BASS_CLARINET_LOW_MIDDLE,
                PART_BASSOON_LOW,
                PART_BASSOON_LOW
            )
            in 10..12 -> listOf(
                PART_PICCOLO_HIGH,
                PART_PICCOLO_MIDDLE_HIGH,
                PART_FLUTE_MIDDLE_HIGH,
                PART_OBOE_MIDDLE_HIGH,
                PART_CLARINET_MIDDLE_HIGH,
                PART_CLARINET_MIDDLE,
                PART_ENGLISH_HORN_LOW_MIDDLE,
                PART_FRENCH_HORN_MIDDLE,
                PART_FRENCH_HORN_MIDDLE,
                PART_BASS_CLARINET_LOW_MIDDLE,
                PART_BASSOON_LOW,
                PART_BASSOON_LOW,
            )
            else -> listOf()
        }
    }

    fun getStringOrchestra(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2, 3 -> listOf(
                PART_STRING_ORCHESTRA_VIOLIN_LOW_MIDDLE_HIGH,
                PART_STRING_ORCHESTRA_VIOLA_LOW_MIDDLE,
                PART_STRING_ORCHESTRA_CELLO_LOW_MIDDLE
            )
            4, 5 -> listOf(
                PART_STRING_ORCHESTRA_VIOLIN_MIDDLE_HIGH_HIGHEST,
                PART_STRING_ORCHESTRA_VIOLIN_LOW_MIDDLE_HIGH,
                PART_STRING_ORCHESTRA_VIOLA_LOW,
                PART_STRING_ORCHESTRA_VIOLA_LOW_MIDDLE,
                PART_STRING_ORCHESTRA_CELLO_LOW
            )
            6, 7 -> listOf(
                PART_STRING_ORCHESTRA_VIOLIN_HIGH_HIGHEST,
                PART_STRING_ORCHESTRA_VIOLIN_MIDDLE_HIGH,
                PART_STRING_ORCHESTRA_VIOLIN_LOW_MIDDLE_HIGH,
                PART_STRING_ORCHESTRA_VIOLA_LOW_MIDDLE,
                PART_STRING_ORCHESTRA_VIOLA_LOW,
                PART_STRING_ORCHESTRA_CELLO_LOW,
                PART_STRING_ORCHESTRA_DB_LOW
            )
            8, 9 -> listOf(
                PART_STRING_ORCHESTRA_VIOLIN_HIGHEST,
                PART_STRING_ORCHESTRA_VIOLIN_HIGH_HIGHEST,
                PART_STRING_ORCHESTRA_VIOLIN_MIDDLE_HIGH,
                PART_STRING_ORCHESTRA_VIOLIN_LOW_MIDDLE_HIGH,
                PART_STRING_ORCHESTRA_VIOLA_LOW_MIDDLE,
                PART_STRING_ORCHESTRA_VIOLA_LOW,
                PART_STRING_ORCHESTRA_CELLO_LOW_MIDDLE,
                PART_STRING_ORCHESTRA_CELLO_LOW,
                PART_STRING_ORCHESTRA_DB_LOW
            )
            in 10..12 -> listOf(
                PART_STRING_ORCHESTRA_VIOLIN_HIGHEST,
                PART_STRING_ORCHESTRA_VIOLIN_HIGHEST,
                PART_STRING_ORCHESTRA_VIOLIN_HIGH_HIGHEST,
                PART_STRING_ORCHESTRA_VIOLIN_HIGH_HIGHEST,
                PART_STRING_ORCHESTRA_VIOLIN_MIDDLE_HIGH,
                PART_STRING_ORCHESTRA_VIOLIN_MIDDLE_HIGH,
                PART_STRING_ORCHESTRA_VIOLA_LOW_MIDDLE,
                PART_STRING_ORCHESTRA_VIOLA_LOW,
                PART_STRING_ORCHESTRA_CELLO_LOW_MIDDLE,
                PART_STRING_ORCHESTRA_CELLO_LOW,
                PART_STRING_ORCHESTRA_DB_LOW_MIDDLE,
                PART_STRING_ORCHESTRA_DB_LOW,
            )
            else -> listOf()
        }
    }
    fun getBrass(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2 -> listOf(
                EnsemblePart(56, 4, TRUMPET_ALL), //trumpet
                EnsemblePart(57, 3, TROMBONE_ALL)  // trombone
            )
            3 -> listOf(
                PART_TRUMPET_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_TROMBONE_LOW_MIDDLE
            )
            4 -> listOf(
                PART_TRUMPET_HIGH,
                PART_TRUMPET_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_TROMBONE_LOW_MIDDLE
            )
            5 -> listOf(
                PART_TRUMPET_HIGH,
                PART_TRUMPET_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_TROMBONE_MIDDLE,
                PART_TUBA_LOW
            )
            6 -> listOf(
                PART_TRUMPET_HIGH,
                PART_TRUMPET_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE,
                PART_TROMBONE_MIDDLE,
                PART_TUBA_LOW
            )
            7 -> listOf(
                PART_TRUMPET_HIGH,
                PART_TRUMPET_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE,
                PART_TROMBONE_MIDDLE_HIGH,
                PART_TROMBONE_MIDDLE,
                PART_TUBA_LOW
            )
            8 -> listOf(
                PART_TRUMPET_HIGH,
                PART_TRUMPET_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE,
                PART_FRENCH_HORN_MIDDLE,
                PART_TROMBONE_MIDDLE_HIGH,
                PART_TROMBONE_MIDDLE,
                PART_TUBA_LOW
            )
            9 -> listOf(
                PART_TRUMPET_HIGH,
                PART_TRUMPET_HIGH,
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE,
                PART_FRENCH_HORN_MIDDLE,
                PART_TROMBONE_MIDDLE_HIGH,
                PART_TROMBONE_MIDDLE,
                PART_TUBA_LOW
            )
            in 10..12 -> listOf(
                PART_TRUMPET_HIGH,
                PART_TRUMPET_HIGH,
                PART_TRUMPET_HIGH,
                PART_TRUMPET_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE,
                PART_FRENCH_HORN_MIDDLE,
                PART_TROMBONE_HIGH,
                PART_TROMBONE_MIDDLE_HIGH,
                PART_TROMBONE_MIDDLE,
                PART_TUBA_LOW
            )
            else -> listOf()
        }
    }
    fun getGamelan(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2 -> listOf(
                PART_GLOCKENSPIEL_MIDDLE_HIGH,
                PART_VIBRAPHONE_LOW_MIDDLE
            )
            3 -> listOf(
                PART_GLOCKENSPIEL_MIDDLE_HIGH,
                PART_VIBRAPHONE_MIDDLE_HIGH,
                PART_VIBRAPHONE_LOW_MIDDLE
            )
            4 -> listOf(
                PART_GLOCKENSPIEL_HIGH,
                PART_VIBRAPHONE_MIDDLE_HIGH,
                PART_VIBRAPHONE_LOW_MIDDLE,
                PART_MARIMBA_LOW_MIDDLE
            )
            5 -> listOf(
                PART_GLOCKENSPIEL_HIGH,
                PART_VIBRAPHONE_HIGH,
                PART_VIBRAPHONE_MIDDLE_HIGH,
                PART_VIBRAPHONE_LOW_MIDDLE,
                PART_MARIMBA_LOW_MIDDLE
            )
            6 -> listOf(
                PART_GLOCKENSPIEL_HIGH,
                PART_VIBRAPHONE_HIGH,
                PART_VIBRAPHONE_MIDDLE_HIGH,
                PART_MARIMBA_MIDDLE_HIGH,
                PART_VIBRAPHONE_LOW_MIDDLE,
                PART_MARIMBA_LOW_MIDDLE
            )
            7 -> listOf(
                PARTS_MUSIC_BOX[6],
                PART_GLOCKENSPIEL_HIGH,
                PART_VIBRAPHONE_HIGH,
                PART_VIBRAPHONE_MIDDLE_HIGH,
                PART_MARIMBA_MIDDLE_HIGH,
                PART_VIBRAPHONE_LOW_MIDDLE,
                PART_MARIMBA_LOW_MIDDLE
            )
            8 -> listOf(
                PARTS_MUSIC_BOX[6],
                PART_GLOCKENSPIEL_HIGH,
                PART_VIBRAPHONE_HIGH,
                PART_MARIMBA_HIGH,
                PART_VIBRAPHONE_MIDDLE_HIGH,
                PART_MARIMBA_MIDDLE_HIGH,
                PART_VIBRAPHONE_LOW_MIDDLE,
                PART_MARIMBA_LOW_MIDDLE
            )
            9 -> listOf(
                PARTS_MUSIC_BOX[6],
                PART_GLOCKENSPIEL_HIGH,
                PART_VIBRAPHONE_HIGH,
                PART_MARIMBA_HIGH,
                PART_TUBULAR_BELLS_ALL,
                PART_VIBRAPHONE_MIDDLE_HIGH,
                PART_MARIMBA_MIDDLE_HIGH,
                PART_VIBRAPHONE_LOW_MIDDLE,
                PART_MARIMBA_LOW_MIDDLE
            )
            10 -> listOf(
                PARTS_MUSIC_BOX[6],
                PART_GLOCKENSPIEL_HIGH,
                PART_VIBRAPHONE_HIGH,
                PART_MARIMBA_HIGH,
                PART_TUBULAR_BELLS_ALL,
                PART_XYLOPHONE_MIDDLE_HIGH,
                PART_VIBRAPHONE_MIDDLE_HIGH,
                PART_MARIMBA_MIDDLE_HIGH,
                PART_VIBRAPHONE_LOW_MIDDLE,
                PART_MARIMBA_LOW_MIDDLE
            )
            11 -> listOf(
                PARTS_MUSIC_BOX[6],
                PART_GLOCKENSPIEL_HIGH,
                PART_VIBRAPHONE_HIGH,
                PART_MARIMBA_HIGH,
                PART_TUBULAR_BELLS_ALL,
                PART_TUBULAR_BELLS_ALL,
                PART_XYLOPHONE_MIDDLE_HIGH,
                PART_VIBRAPHONE_MIDDLE_HIGH,
                PART_MARIMBA_MIDDLE_HIGH,
                PART_VIBRAPHONE_LOW_MIDDLE,
                PART_MARIMBA_LOW_MIDDLE
            )
            12 -> listOf(
                PARTS_MUSIC_BOX[7],
                PART_XYLOPHONE_HIGH,
                PART_GLOCKENSPIEL_HIGH,
                PART_VIBRAPHONE_HIGH,
                PART_MARIMBA_HIGH,
                PART_TUBULAR_BELLS_ALL,
                PART_TUBULAR_BELLS_ALL,
                PART_XYLOPHONE_MIDDLE_HIGH,
                PART_VIBRAPHONE_MIDDLE_HIGH,
                PART_MARIMBA_MIDDLE_HIGH,
                PART_VIBRAPHONE_LOW_MIDDLE,
                PART_MARIMBA_LOW_MIDDLE
            )
            else -> listOf()
        }
    }
    fun getSaxophones(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2 -> listOf(
                PART_SAX_ALTO_MIDDLE_HIGH,
                PART_SAX_TENOR_LOW_MIDDLE
            )
            3 -> listOf(
                PART_SAX_SOPRANO_MIDDLE_HIGH,
                PART_SAX_ALTO_MIDDLE_HIGH,
                PART_SAX_TENOR_LOW_MIDDLE
            )
            4 -> listOf(
                PART_SAX_SOPRANO_MIDDLE_HIGH,
                PART_SAX_ALTO_MIDDLE_HIGH,
                PART_SAX_TENOR_MIDDLE,
                PART_SAX_BARITONE_LOW_MIDDLE
            )
            5 -> listOf(
                PART_SAX_SOPRANO_MIDDLE_HIGH,
                PART_SAX_ALTO_MIDDLE_HIGH,
                PART_SAX_ALTO_MIDDLE,
                PART_SAX_TENOR_MIDDLE,
                PART_SAX_BARITONE_LOW_MIDDLE
            )
            6 -> listOf(
                PART_SAX_SOPRANO_MIDDLE_HIGH,
                PART_SAX_SOPRANO_MIDDLE,
                PART_SAX_ALTO_MIDDLE_HIGH,
                PART_SAX_ALTO_MIDDLE,
                PART_SAX_TENOR_MIDDLE,
                PART_SAX_BARITONE_LOW_MIDDLE
            )
            7 -> listOf(
                PART_SAX_SOPRANO_MIDDLE_HIGH,
                PART_SAX_SOPRANO_MIDDLE,
                PART_SAX_ALTO_MIDDLE_HIGH,
                PART_SAX_ALTO_MIDDLE,
                PART_SAX_TENOR_MIDDLE_HIGH,
                PART_SAX_TENOR_MIDDLE,
                PART_SAX_BARITONE_LOW_MIDDLE
            )
            8 -> listOf(
                PART_SAX_SOPRANO_HIGH,
                PART_SAX_SOPRANO_MIDDLE_HIGH,
                PART_SAX_SOPRANO_MIDDLE,
                PART_SAX_ALTO_MIDDLE_HIGH,
                PART_SAX_ALTO_MIDDLE,
                PART_SAX_TENOR_MIDDLE_HIGH,
                PART_SAX_TENOR_MIDDLE,
                PART_SAX_BARITONE_LOW_MIDDLE
            )
            9 -> listOf(
                PART_SAX_SOPRANO_HIGH,
                PART_SAX_SOPRANO_MIDDLE_HIGH,
                PART_SAX_SOPRANO_MIDDLE,
                PART_SAX_ALTO_MIDDLE_HIGH,
                PART_SAX_ALTO_MIDDLE,
                PART_SAX_TENOR_MIDDLE_HIGH,
                PART_SAX_TENOR_MIDDLE,
                PART_SAX_BARITONE_LOW_MIDDLE,
                PART_SAX_BARITONE_LOW
            )
            in 10..12 -> listOf(
                PART_SAX_SOPRANO_HIGH,
                PART_SAX_SOPRANO_MIDDLE_HIGH,
                PART_SAX_SOPRANO_MIDDLE_HIGH,
                PART_SAX_SOPRANO_MIDDLE,
                PART_SAX_ALTO_MIDDLE_HIGH,
                PART_SAX_ALTO_MIDDLE_HIGH,
                PART_SAX_ALTO_MIDDLE,
                PART_SAX_ALTO_MIDDLE,
                PART_SAX_TENOR_MIDDLE_HIGH,
                PART_SAX_TENOR_MIDDLE,
                PART_SAX_BARITONE_LOW_MIDDLE,
                PART_SAX_BARITONE_LOW
            )
            else -> listOf()
        }
    }
        fun getFlutes(nParts: Int): List<EnsemblePart> {
            return when (nParts) {
                1, 2 -> listOf(
                    PART_FLUTE_MIDDLE_HIGH,
                    PART_FLUTE_LOW_MIDDLE
                )
                3, 4 -> listOf(
                    PART_PICCOLO_MIDDLE_HIGH,
                    PART_FLUTE_MIDDLE_HIGH,
                    PART_FLUTE_MIDDLE,
                    PART_FLUTE_LOW_MIDDLE
                )
                5 -> listOf(
                    PART_PICCOLO_MIDDLE_HIGH,
                    PART_FLUTE_MIDDLE_HIGH,
                    PART_FLUTE_MIDDLE,
                    PART_FLUTE_LOW_MIDDLE,
                    PART_BASS_FLUTE_LOW_MIDDLE
                )
                6 -> listOf(
                    PART_PICCOLO_HIGH,
                    PART_PICCOLO_MIDDLE_HIGH,
                    PART_FLUTE_MIDDLE_HIGH,
                    PART_FLUTE_MIDDLE,
                    PART_FLUTE_LOW_MIDDLE,
                    PART_BASS_FLUTE_LOW_MIDDLE
                )
                7 -> listOf(
                    PART_PICCOLO_HIGH,
                    PART_PICCOLO_MIDDLE_HIGH,
                    PART_FLUTE_MIDDLE_HIGH,
                    PART_FLUTE_MIDDLE,
                    PART_FLUTE_LOW_MIDDLE,
                    PART_FLUTE_LOW,
                    PART_BASS_FLUTE_LOW_MIDDLE
                )
                8 -> listOf(
                    PART_PICCOLO_HIGH,
                    PART_PICCOLO_MIDDLE_HIGH,
                    PART_FLUTE_MIDDLE_HIGH,
                    PART_FLUTE_MIDDLE,
                    PART_FLUTE_MIDDLE,
                    PART_FLUTE_LOW_MIDDLE,
                    PART_FLUTE_LOW,
                    PART_BASS_FLUTE_LOW_MIDDLE
                )
                9 -> listOf(
                    PART_PICCOLO_HIGH,
                    PART_PICCOLO_MIDDLE_HIGH,
                    PART_FLUTE_MIDDLE_HIGH,
                    PART_FLUTE_MIDDLE,
                    PART_FLUTE_MIDDLE,
                    PART_FLUTE_LOW_MIDDLE,
                    PART_FLUTE_LOW,
                    PART_BASS_FLUTE_LOW_MIDDLE,
                    PART_BASS_FLUTE_LOW
                )
                in 10..12 -> listOf(
                    PART_PICCOLO_HIGH,
                    PART_PICCOLO_MIDDLE_HIGH,
                    PART_PICCOLO_MIDDLE_HIGH,
                    PART_FLUTE_MIDDLE_HIGH,
                    PART_FLUTE_MIDDLE_HIGH,
                    PART_FLUTE_MIDDLE,
                    PART_FLUTE_MIDDLE,
                    PART_FLUTE_LOW_MIDDLE,
                    PART_FLUTE_LOW_MIDDLE,
                    PART_FLUTE_LOW,
                    PART_BASS_FLUTE_LOW_MIDDLE,
                    PART_BASS_FLUTE_LOW
                )
                else -> listOf()
            }
        }
    fun getDoubleReeds(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2 -> listOf(
                EnsemblePart(OBOE, 4, OBOE_ALL),
                EnsemblePart(BASSOON, 3, BASSOON_ALL)
            )
            3 -> listOf(
                PART_OBOE_MIDDLE_HIGH,
                PART_ENGLISH_HORN_LOW_MIDDLE,
                PART_BASSOON_LOW_MIDDLE
            )
            4 -> listOf(
                PART_OBOE_MIDDLE_HIGH,
                PART_ENGLISH_HORN_LOW_MIDDLE,
                PART_BASSOON_LOW_MIDDLE,
                PART_BASSOON_LOW
            )
            5 -> listOf(
                PART_OBOE_MIDDLE_HIGH,
                PART_OBOE_MIDDLE_HIGH,
                PART_ENGLISH_HORN_LOW_MIDDLE,
                PART_BASSOON_LOW_MIDDLE,
                PART_BASSOON_LOW
            )
            6 -> listOf(
                PART_OBOE_MIDDLE_HIGH,
                PART_OBOE_MIDDLE_HIGH,
                PART_ENGLISH_HORN_LOW_MIDDLE,
                PART_BASSOON_MIDDLE,
                PART_BASSOON_LOW_MIDDLE,
                PART_BASSOON_LOW
            )
            7, 8 -> listOf(
                PART_OBOE_MIDDLE_HIGH,
                PART_OBOE_MIDDLE_HIGH,
                PART_ENGLISH_HORN_LOW_MIDDLE,
                PART_ENGLISH_HORN_LOW,
                PART_BASSOON_MIDDLE_HIGH,
                PART_BASSOON_MIDDLE,
                PART_BASSOON_LOW_MIDDLE,
                PART_CONTRABASSOON_LOW_MIDDLE,
            )
            9 -> listOf(
                PART_OBOE_HIGH,
                PART_OBOE_MIDDLE_HIGH,
                PART_OBOE_MIDDLE_HIGH,
                PART_ENGLISH_HORN_LOW_MIDDLE,
                PART_ENGLISH_HORN_LOW,
                PART_BASSOON_MIDDLE_HIGH,
                PART_BASSOON_MIDDLE,
                PART_BASSOON_LOW_MIDDLE,
                PART_CONTRABASSOON_LOW
            )
            in 10..12 -> listOf(
                PART_OBOE_HIGH,
                PART_OBOE_HIGH,
                PART_OBOE_MIDDLE_HIGH,
                PART_OBOE_MIDDLE_HIGH,
                PART_OBOE_MIDDLE,
                PART_ENGLISH_HORN_MIDDLE_HIGH,
                PART_ENGLISH_HORN_LOW_MIDDLE,
                PART_ENGLISH_HORN_LOW,
                PART_BASSOON_MIDDLE_HIGH,
                PART_BASSOON_MIDDLE,
                PART_CONTRABASSOON_LOW_MIDDLE,
                PART_CONTRABASSOON_LOW
            )
            else -> listOf()
        }
    }
    fun getClarinets(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2, 3 -> listOf(
                PART_CLARINET_HIGH,
                PART_CLARINET_MIDDLE,
                PART_CLARINET_LOW_MIDDLE
            )
            4, 5 -> listOf(
                PART_CLARINET_HIGH,
                PART_CLARINET_MIDDLE_HIGH,
                PART_CLARINET_MIDDLE,
                PART_CLARINET_LOW_MIDDLE,
                PART_BASS_CLARINET_LOW_MIDDLE
            )
            6, 7 -> listOf(
                PART_CLARINET_HIGH_HIGHEST,
                PART_CLARINET_MIDDLE_HIGH,
                PART_CLARINET_MIDDLE,
                PART_CLARINET_LOW_MIDDLE,
                PART_CLARINET_LOW,
                PART_BASS_CLARINET_LOW_MIDDLE,
                PART_BASS_CLARINET_LOW
            )
            8, 9 -> listOf(
                PART_CLARINET_HIGH_HIGHEST,
                PART_CLARINET_HIGH,
                PART_CLARINET_MIDDLE_HIGH,
                PART_CLARINET_MIDDLE,
                PART_CLARINET_MIDDLE,
                PART_CLARINET_LOW_MIDDLE,
                PART_CLARINET_LOW,
                PART_BASS_CLARINET_LOW_MIDDLE,
                PART_BASS_CLARINET_LOW
            )
            in 10..12 -> listOf(
                PART_CLARINET_HIGHEST,
                PART_CLARINET_HIGH_HIGHEST,
                PART_CLARINET_HIGH,
                PART_CLARINET_MIDDLE_HIGH,
                PART_CLARINET_MIDDLE,
                PART_CLARINET_MIDDLE,
                PART_CLARINET_LOW_MIDDLE,
                PART_CLARINET_LOW_MIDDLE,
                PART_CLARINET_LOW,
                PART_BASS_CLARINET_LOW_MIDDLE,
                PART_BASS_CLARINET_LOW,
                PART_BASS_CLARINET_LOW
            )
            else -> listOf()
        }
    }
    fun getFrenchHorns(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2, 3 -> listOf(
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE,
                PART_FRENCH_HORN_MIDDLE
            )
            4, 5 -> listOf(
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE,
                PART_FRENCH_HORN_MIDDLE,
                PART_FRENCH_HORN_MIDDLE
            )
            6, 7 -> listOf(
                PART_FRENCH_HORN_HIGH,
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE,
                PART_FRENCH_HORN_MIDDLE,
                PART_FRENCH_HORN_LOW_MIDDLE,
                PART_FRENCH_HORN_LOW_MIDDLE
            )
            8, 9 -> listOf(
                PART_FRENCH_HORN_HIGH,
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE,
                PART_FRENCH_HORN_MIDDLE,
                PART_FRENCH_HORN_MIDDLE,
                PART_FRENCH_HORN_LOW_MIDDLE,
                PART_FRENCH_HORN_LOW_MIDDLE
            )
            in 10..12 -> listOf(
                PART_FRENCH_HORN_HIGH,
                PART_FRENCH_HORN_HIGH,
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE_HIGH,
                PART_FRENCH_HORN_MIDDLE,
                PART_FRENCH_HORN_MIDDLE,
                PART_FRENCH_HORN_MIDDLE,
                PART_FRENCH_HORN_LOW_MIDDLE,
                PART_FRENCH_HORN_LOW_MIDDLE,
                PART_TUBA_LOW,
                PART_TUBA_LOW
            )
            else -> listOf()
        }
    }
    fun getBassoons(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2, 3 -> listOf(
                PART_BASSOON_MIDDLE_HIGH,
                PART_BASSOON_LOW_MIDDLE,
                PART_BASSOON_LOW
            )
            in (4..6) -> listOf(
                PART_BASSOON_MIDDLE_HIGH,
                PART_BASSOON_MIDDLE_HIGH,
                PART_BASSOON_LOW_MIDDLE,
                PART_BASSOON_LOW_MIDDLE,
                PART_BASSOON_LOW,
                PART_BASSOON_LOW
            )
            in (7..9) -> listOf(
                PART_BASSOON_HIGH,
                PART_BASSOON_MIDDLE_HIGH,
                PART_BASSOON_MIDDLE_HIGH,
                PART_BASSOON_MIDDLE,
                PART_BASSOON_MIDDLE,
                PART_BASSOON_LOW_MIDDLE,
                PART_BASSOON_LOW_MIDDLE,
                PART_BASSOON_LOW,
                PART_CONTRABASSOON_LOW,

            )
            in (10..12) -> listOf(
                PART_BASSOON_HIGH,
                PART_BASSOON_HIGH,
                PART_BASSOON_MIDDLE_HIGH,
                PART_BASSOON_MIDDLE_HIGH,
                PART_BASSOON_MIDDLE_HIGH,
                PART_BASSOON_MIDDLE,
                PART_BASSOON_MIDDLE,
                PART_BASSOON_MIDDLE,
                PART_BASSOON_LOW_MIDDLE,
                PART_BASSOON_LOW_MIDDLE,
                PART_CONTRABASSOON_LOW_MIDDLE,
                PART_CONTRABASSOON_LOW,
            )
            else -> listOf()
        }
    }
    fun getCellos(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2, 3 -> listOf(
                PART_CELLO_HIGH_HIGHEST,
                PART_CELLO_MIDDLE,
                PART_STRING_ORCHESTRA_CELLO_LOW_MIDDLE
            )
            in (4..6) -> listOf(
                PART_CELLO_HIGH_HIGHEST,
                PART_CELLO_MIDDLE_HIGH,
                PART_CELLO_MIDDLE,
                PART_CELLO_MIDDLE,
                PART_STRING_ORCHESTRA_CELLO_LOW_MIDDLE,
                PART_STRING_ORCHESTRA_CELLO_LOW
            )
            in (7..9) -> listOf(
                PART_CELLO_HIGH_HIGHEST,
                PART_CELLO_HIGH,
                PART_CELLO_HIGH,
                PART_CELLO_MIDDLE_HIGH,
                PART_CELLO_MIDDLE_HIGH,
                PART_CELLO_MIDDLE,
                PART_CELLO_MIDDLE,
                PART_STRING_ORCHESTRA_CELLO_LOW_MIDDLE,
                PART_STRING_ORCHESTRA_CELLO_LOW
            )
            in 10..12 -> listOf(
                PART_CELLO_HIGHEST,
                PART_CELLO_HIGHEST,
                PART_CELLO_HIGH_HIGHEST,
                PART_CELLO_HIGH_HIGHEST,
                PART_CELLO_HIGH,
                PART_CELLO_HIGH,
                PART_CELLO_MIDDLE_HIGH,
                PART_CELLO_MIDDLE_HIGH,
                PART_CELLO_MIDDLE,
                PART_CELLO_MIDDLE,
                PART_STRING_ORCHESTRA_CELLO_LOW_MIDDLE,
                PART_STRING_ORCHESTRA_CELLO_LOW
            )
            else -> listOf()
        }
    }
    fun getPiano(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2, 3 -> listOf(
                PARTS_PIANO[5],
                PARTS_PIANO[4],
                PARTS_PIANO[2],
            )
            in (4..6) -> listOf(
                PARTS_PIANO[5],
                PARTS_PIANO[4],
                PARTS_PIANO[4],
                PARTS_PIANO[3],
                PARTS_PIANO[3],
                PARTS_PIANO[2]
            )
            7 -> listOf(
                PARTS_PIANO[5],
                PARTS_PIANO[4],
                PARTS_PIANO[4],
                PARTS_PIANO[3],
                PARTS_PIANO[3],
                PARTS_PIANO[2],
                PARTS_PIANO[1]
            )
            8 -> listOf(
                PARTS_PIANO[6],
                PARTS_PIANO[5],
                PARTS_PIANO[4],
                PARTS_PIANO[4],
                PARTS_PIANO[3],
                PARTS_PIANO[3],
                PARTS_PIANO[2],
                PARTS_PIANO[1]
            )
            9 -> listOf(
                PARTS_PIANO[6],
                PARTS_PIANO[5],
                PARTS_PIANO[5],
                PARTS_PIANO[4],
                PARTS_PIANO[4],
                PARTS_PIANO[3],
                PARTS_PIANO[3],
                PARTS_PIANO[2],
                PARTS_PIANO[1]
            )
            in 10..12 -> listOf(
                PARTS_PIANO[7],
                PARTS_PIANO[6],
                PARTS_PIANO[6],
                PARTS_PIANO[5],
                PARTS_PIANO[5],
                PARTS_PIANO[4],
                PARTS_PIANO[4],
                PARTS_PIANO[3],
                PARTS_PIANO[3],
                PARTS_PIANO[2],
                PARTS_PIANO[2],
                PARTS_PIANO[1]
            )
            else -> listOf()
        }
    }
    fun getHarp(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2, 3 -> listOf(
                PARTS_HARP[5],
                PARTS_HARP[4],
                PARTS_HARP[2],
            )
            in (4..6) -> listOf(
                PARTS_HARP[5],
                PARTS_HARP[4],
                PARTS_HARP[4],
                PARTS_HARP[3],
                PARTS_HARP[3],
                PARTS_HARP[2]
            )
            7 -> listOf(
                PARTS_HARP[5],
                PARTS_HARP[4],
                PARTS_HARP[4],
                PARTS_HARP[3],
                PARTS_HARP[3],
                PARTS_HARP[2],
                PARTS_HARP[1]
            )
            8 -> listOf(
                PARTS_HARP[6],
                PARTS_HARP[5],
                PARTS_HARP[4],
                PARTS_HARP[4],
                PARTS_HARP[3],
                PARTS_HARP[3],
                PARTS_HARP[2],
                PARTS_HARP[1]
            )
            9 -> listOf(
                PARTS_HARP[6],
                PARTS_HARP[5],
                PARTS_HARP[5],
                PARTS_HARP[4],
                PARTS_HARP[4],
                PARTS_HARP[3],
                PARTS_HARP[3],
                PARTS_HARP[2],
                PARTS_HARP[1]
            )
            in 10..12 -> listOf(
                PARTS_HARP[7],
                PARTS_HARP[6],
                PARTS_HARP[6],
                PARTS_HARP[5],
                PARTS_HARP[5],
                PARTS_HARP[4],
                PARTS_HARP[4],
                PARTS_HARP[3],
                PARTS_HARP[3],
                PARTS_HARP[2],
                PARTS_HARP[2],
                PARTS_HARP[1]
            )
            else -> listOf()
        }
    }
    fun getPierrot(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2, 3 -> listOf(
                PART_FLUTE_LOW_MIDDLE,
                PARTS_PIANO[3],
                PART_STRING_ORCHESTRA_CELLO_LOW
            )
            in (4..6) -> listOf(
                PART_FLUTE_MIDDLE_HIGH,
                PART_VIOLIN_MIDDLE_HIGH,
                PARTS_PIANO[4],
                PART_CLARINET_LOW,
                PART_STRING_ORCHESTRA_CELLO_LOW,
                PARTS_PIANO[1],
            )
            7 -> listOf(
                PART_FLUTE_MIDDLE_HIGH,
                PART_VIOLIN_HIGH_HIGHEST,
                PART_CLARINET_MIDDLE_HIGH,
                PARTS_PIANO[4],
                PART_CELLO_MIDDLE_HIGH,
                PARTS_PIANO[2],
                PARTS_PIANO[1],
            )
            8 -> listOf(
                PARTS_PIANO[6],
                PART_FLUTE_MIDDLE_HIGH,
                PART_VIOLIN_HIGH_HIGHEST,
                PART_CLARINET_MIDDLE_HIGH,
                PARTS_PIANO[3],
                PART_CELLO_HIGH_HIGHEST,
                PARTS_PIANO[2],
                PARTS_PIANO[1],
            )
            9 -> listOf(
                PARTS_PIANO[6],
                PART_FLUTE_MIDDLE_HIGH,
                PART_VIOLIN_HIGH_HIGHEST,
                PART_CLARINET_MIDDLE_HIGH,
                PARTS_PIANO[4],
                PARTS_PIANO[3],
                PART_CELLO_HIGH_HIGHEST,
                PARTS_PIANO[2],
                PARTS_PIANO[1],
            )
            in 10..12 -> listOf(
                PARTS_PIANO[7],
                PARTS_PIANO[6],
                PART_FLUTE_MIDDLE_HIGH,
                PARTS_PIANO[5],
                PART_VIOLIN_HIGH_HIGHEST,
                PART_CLARINET_MIDDLE_HIGH,
                PARTS_PIANO[4],
                PARTS_PIANO[3],
                PART_CELLO_HIGH_HIGHEST,
                PARTS_PIANO[2],
                PARTS_PIANO[2],
                PARTS_PIANO[1],
            )
            else -> listOf()
        }
    }
    fun getBaroque(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2 -> listOf(
                PARTS_HARPSICHORD[4],
                PARTS_HARPSICHORD[2],
            )
            3 -> listOf(
                PART_FLUTE_MIDDLE_HIGH,
                PARTS_HARPSICHORD[4],
                PARTS_HARPSICHORD[2],
            )
            4 -> listOf(
                PART_FLUTE_MIDDLE_HIGH,
                PARTS_HARPSICHORD[4],
                PARTS_HARPSICHORD[3],
                PART_STRING_ORCHESTRA_CELLO_LOW
            )
            5 -> listOf(
                PART_FLUTE_MIDDLE_HIGH,
                PARTS_HARPSICHORD[4],
                PARTS_HARPSICHORD[4],
                PARTS_HARPSICHORD[3],
                PART_STRING_ORCHESTRA_CELLO_LOW
            )
            6 -> listOf(
                PART_FLUTE_MIDDLE_HIGH,
                PART_VIOLIN_HIGHEST,
                PARTS_HARPSICHORD[4],
                PARTS_HARPSICHORD[4],
                PARTS_HARPSICHORD[3],
                PART_STRING_ORCHESTRA_CELLO_LOW
            )
            7 -> listOf(
                PART_FLUTE_MIDDLE_HIGH,
                PART_VIOLIN_HIGHEST,
                PARTS_HARPSICHORD[4],
                PARTS_HARPSICHORD[4],
                PARTS_HARPSICHORD[3],
                PARTS_HARPSICHORD[3],
                PART_STRING_ORCHESTRA_CELLO_LOW
            )
            8 -> listOf(
                PART_FLUTE_MIDDLE_HIGH,
                PART_OBOE_MIDDLE,
                PART_VIOLIN_HIGHEST,
                PARTS_HARPSICHORD[4],
                PARTS_HARPSICHORD[4],
                PARTS_HARPSICHORD[3],
                PARTS_HARPSICHORD[3],
                PART_STRING_ORCHESTRA_CELLO_LOW
            )
            9 -> listOf(
                PART_FLUTE_MIDDLE_HIGH,
                PART_OBOE_MIDDLE,
                PART_VIOLIN_HIGHEST,
                PART_VIOLA_MIDDLE_HIGH,
                PARTS_HARPSICHORD[4],
                PARTS_HARPSICHORD[4],
                PARTS_HARPSICHORD[3],
                PARTS_HARPSICHORD[3],
                PART_STRING_ORCHESTRA_CELLO_LOW
            )
            10 -> listOf(
                PART_FLUTE_MIDDLE_HIGH,
                PART_OBOE_MIDDLE,
                PART_VIOLIN_HIGHEST,
                PART_VIOLA_MIDDLE_HIGH,
                PARTS_HARPSICHORD[4],
                PARTS_HARPSICHORD[4],
                PARTS_HARPSICHORD[3],
                PARTS_HARPSICHORD[3],
                PARTS_HARPSICHORD[3],
                PART_STRING_ORCHESTRA_CELLO_LOW
            )
            11 -> listOf(
                PART_FLUTE_MIDDLE_HIGH,
                PART_OBOE_MIDDLE,
                PART_VIOLIN_HIGHEST,
                PART_VIOLA_MIDDLE_HIGH,
                PARTS_HARPSICHORD[4],
                PARTS_HARPSICHORD[4],
                PARTS_HARPSICHORD[3],
                PARTS_HARPSICHORD[3],
                PARTS_HARPSICHORD[3],
                PART_BASSOON_LOW,
                PART_STRING_ORCHESTRA_DB_LOW_MIDDLE
            )
            12 -> listOf(
                PART_FLUTE_MIDDLE_HIGH,
                PART_OBOE_MIDDLE,
                PART_VIOLIN_HIGHEST,
                PART_VIOLA_MIDDLE_HIGH,
                PART_VIOLA_MIDDLE,
                PARTS_HARPSICHORD[4],
                PARTS_HARPSICHORD[4],
                PARTS_HARPSICHORD[3],
                PARTS_HARPSICHORD[3],
                PARTS_HARPSICHORD[3],
                PART_BASSOON_LOW,
                PART_STRING_ORCHESTRA_DB_LOW_MIDDLE
            )
            else -> listOf()
        }
    }

    fun getPluckedStrings(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2 -> listOf(
                EnsemblePart(CLEAN_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5),
                EnsemblePart(NYLON_GUITAR, 3, GUITAR_ALL, GUITAR_LOW5)
            )
            3 -> listOf(
                EnsemblePart(CLEAN_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5),
                EnsemblePart(NYLON_GUITAR, 3, GUITAR_ALL, GUITAR_LOW5),
                PARTS_HARP[3]
            )
            4 -> listOf(
                PARTS_HARP[5],
                EnsemblePart(CLEAN_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5),
                EnsemblePart(NYLON_GUITAR, 3, GUITAR_ALL, GUITAR_LOW5),
                PARTS_HARP[3]
            )
            5 -> listOf(
                PARTS_HARP[5],
                EnsemblePart(CLEAN_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5..GUITAR_HIGH5),
                EnsemblePart(NYLON_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5),
                EnsemblePart(NYLON_GUITAR, 3, GUITAR_ALL, GUITAR_LOW5),
                PARTS_HARP[3]
            )
            6 -> listOf(
                PARTS_HARP[5],
                EnsemblePart(CLEAN_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5..GUITAR_HIGH5),
                EnsemblePart(NYLON_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5),
                PARTS_HARP[4],
                EnsemblePart(NYLON_GUITAR, 3, GUITAR_ALL, GUITAR_LOW5),
                PARTS_HARP[3]
            )
            7 -> listOf(
                EnsemblePart(GUITAR_HARMONICS, 6, GUITAR_HARMONIC_ALL, GUITAR_HARMONIC_HIGHEST5),
                PARTS_HARP[5],
                EnsemblePart(CLEAN_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5..GUITAR_HIGH5),
                EnsemblePart(NYLON_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5),
                PARTS_HARP[4],
                EnsemblePart(NYLON_GUITAR, 3, GUITAR_LOW5),
                PARTS_HARP[3]
            )
            8 -> listOf(
                EnsemblePart(GUITAR_HARMONICS, 6, GUITAR_HARMONIC_ALL, GUITAR_HARMONIC_HIGHEST5),
                PARTS_HARP[5],
                EnsemblePart(BANJO, 5, BANJO_ALL, BANJO_HIGHEST5),
                EnsemblePart(CLEAN_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5..GUITAR_HIGH5),
                EnsemblePart(NYLON_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5),
                PARTS_HARP[4],
                EnsemblePart(NYLON_GUITAR, 3, GUITAR_ALL, GUITAR_LOW5),
                PARTS_HARP[3]
            )
            9 -> listOf(
                EnsemblePart(GUITAR_HARMONICS, 6, GUITAR_HARMONIC_ALL, GUITAR_HARMONIC_HIGHEST5),
                PARTS_HARP[5],
                EnsemblePart(BANJO, 5, BANJO_ALL, BANJO_HIGHEST5),
                EnsemblePart(CLEAN_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5..GUITAR_HIGH5),
                EnsemblePart(NYLON_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5),
                EnsemblePart(SITAR, 4, SITAR_ALL, SITAR_HIGH5),
                PARTS_HARP[3],
                EnsemblePart(NYLON_GUITAR, 3, GUITAR_ALL, GUITAR_LOW5),
                PARTS_HARP[2]
            )
            10 -> listOf(
                EnsemblePart(GUITAR_HARMONICS, 6, GUITAR_HARMONIC_ALL, GUITAR_HARMONIC_HIGHEST5),
                PARTS_HARP[5],
                EnsemblePart(BANJO, 5, BANJO_ALL, BANJO_HIGHEST5),
                EnsemblePart(KOTO, 5, KOTO_ALL, KOTO_HIGHEST5),
                EnsemblePart(CLEAN_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5..GUITAR_HIGH5),
                EnsemblePart(NYLON_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5),
                EnsemblePart(SITAR, 4, SITAR_ALL, SITAR_HIGH5),
                PARTS_HARP[3],
                EnsemblePart(NYLON_GUITAR, 3, GUITAR_LOW5),
                PARTS_HARP[2]
            )
            11 -> listOf(
                EnsemblePart(GUITAR_HARMONICS, 6, GUITAR_HARMONIC_ALL, GUITAR_HARMONIC_HIGHEST5),
                PARTS_HARP[5],
                EnsemblePart(BANJO, 5, BANJO_ALL, BANJO_HIGHEST5),
                EnsemblePart(KOTO, 5, KOTO_ALL, KOTO_HIGHEST5),
                EnsemblePart(CLEAN_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5..GUITAR_HIGH5),
                EnsemblePart(NYLON_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5),
                EnsemblePart(SITAR, 4, SITAR_ALL, SITAR_HIGH5),
                PARTS_HARP[3],
                EnsemblePart(SHAMISEN, 4, SHAMISEN_ALL, SHAMISEN_LOW5),
                EnsemblePart(NYLON_GUITAR, 3, GUITAR_ALL, GUITAR_LOW5),
                PARTS_HARP[2]
            )
            12 -> listOf(
                EnsemblePart(GUITAR_HARMONICS, 6, GUITAR_HARMONIC_ALL, GUITAR_HARMONIC_HIGHEST5),
                PARTS_HARP[5],
                EnsemblePart(BANJO, 5, BANJO_ALL, BANJO_HIGHEST5),
                EnsemblePart(KOTO, 5, KOTO_ALL, KOTO_HIGHEST5),
                EnsemblePart(CLEAN_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5..GUITAR_HIGH5),
                EnsemblePart(NYLON_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5),
                EnsemblePart(SITAR, 4, SITAR_ALL, SITAR_HIGH5),
                PARTS_HARP[3],
                EnsemblePart(CLEAN_GUITAR, 3, GUITAR_ALL, GUITAR_LOW5..GUITAR_MIDDLE5),
                EnsemblePart(SHAMISEN, 4, SHAMISEN_ALL, SHAMISEN_LOW5),
                EnsemblePart(NYLON_GUITAR, 3, GUITAR_ALL, GUITAR_LOW5),
                PARTS_HARP[2]
            )
            else -> listOf()
        }
    }
    fun getSpooky(nParts: Int): List<EnsemblePart> {

        return when (nParts) {
            1, 2, 3 -> listOf(
                PARTS_BOWED_GLASS[5],
                PARTS_ECHO_DROPS[4],
                PARTS_WARM_PAD[2],
            )
            in (4..6) -> listOf(
                PARTS_BOWED_GLASS[5],
                PARTS_ECHO_DROPS[4],
                PARTS_WARM_PAD[4],
                PARTS_BOWED_GLASS[3],
                PARTS_SOUNDTRACK[3],
                PARTS_GOBLINS[2]
            )
            7 -> listOf(
                PARTS_BOWED_GLASS[5],
                PARTS_ECHO_DROPS[4],
                PARTS_WARM_PAD[4],
                PARTS_BOWED_GLASS[3],
                PARTS_SOUNDTRACK[3],
                PARTS_GOBLINS[2],
                EnsemblePart(SYN_CHARANG, 1, SYN_CHARANG_ALL, SYN_CHARANG_LOW5)
            )
            8 -> listOf(
                PARTS_ECHO_DROPS[6],
                PARTS_BOWED_GLASS[5],
                PARTS_ECHO_DROPS[4],
                PARTS_SOUNDTRACK[4],
                PARTS_GOBLINS[3],
                PARTS_WARM_PAD[3],
                PARTS_SYN_VOICE[2],
                EnsemblePart(SYN_CHARANG, 1, SYN_CHARANG_ALL, SYN_CHARANG_LOW5)
            )
            9 -> listOf(
                PARTS_ECHO_DROPS[6],
                PARTS_BOWED_GLASS[5],
                PARTS_ECHO_DROPS[5],
                PARTS_SYN_VOICE[4],
                PARTS_WARM_PAD[4],
                PARTS_BOWED_GLASS[3],
                PARTS_SOUNDTRACK[3],
                PARTS_GOBLINS[2],
                EnsemblePart(SYN_CHARANG, 1, SYN_CHARANG_ALL, SYN_CHARANG_LOW5)
            )
            in 10..12 -> listOf(
                PARTS_WARM_PAD[7],
                PARTS_WARM_PAD[6],
                PARTS_ECHO_DROPS[6],
                PARTS_BOWED_GLASS[5],
                PARTS_WARM_PAD[5],
                PARTS_ECHO_DROPS[4],
                PARTS_SYN_VOICE[4],
                PARTS_BOWED_GLASS[3],
                PARTS_SOUNDTRACK[3],
                PARTS_GOBLINS[2],
                PARTS_WARM_PAD[2],
                EnsemblePart(SYN_CHARANG, 1, SYN_CHARANG_ALL, SYN_CHARANG_LOW5)
            )
            else -> listOf()
        }
    }
    fun getKeyboardInstrument(keyboardInstrument: Int, nParts: Int, range: RANGES = RANGES.PIANO, rangeAll: IntRange = IntRange(A0, C8) ): List<EnsemblePart> {
        val instrument = createKeyboardInstrumentParts(keyboardInstrument, rangeAll)
        val oct = range.octaves
        return when (nParts) {
            1, 2, 3 -> listOf(
                instrument[oct[5]],
                instrument[oct[4]],
                instrument[oct[2]],
            )
            in (4..6) -> listOf(
                instrument[oct[5]],
                instrument[oct[4]],
                instrument[oct[4]],
                instrument[oct[3]],
                instrument[oct[3]],
                instrument[oct[2]]
            )
            7 -> listOf(
                instrument[oct[5]],
                instrument[oct[4]],
                instrument[oct[4]],
                instrument[oct[3]],
                instrument[oct[3]],
                instrument[oct[2]],
                instrument[oct[1]]
            )
            8 -> listOf(
                instrument[oct[6]],
                instrument[oct[5]],
                instrument[oct[4]],
                instrument[oct[4]],
                instrument[oct[3]],
                instrument[oct[3]],
                instrument[oct[2]],
                instrument[oct[1]]
            )
            9 -> listOf(
                instrument[oct[6]],
                instrument[oct[5]],
                instrument[oct[5]],
                instrument[oct[4]],
                instrument[oct[4]],
                instrument[oct[3]],
                instrument[oct[3]],
                instrument[oct[2]],
                instrument[oct[1]]
            )
            in 10..12 -> listOf(
                instrument[oct[7]],
                instrument[oct[6]],
                instrument[oct[6]],
                instrument[oct[5]],
                instrument[oct[5]],
                instrument[oct[4]],
                instrument[oct[4]],
                instrument[oct[3]],
                instrument[oct[3]],
                instrument[oct[2]],
                instrument[oct[2]],
                instrument[oct[1]]
            )
            else -> listOf()
        }

    }
}





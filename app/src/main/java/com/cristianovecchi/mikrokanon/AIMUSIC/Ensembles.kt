package com.cristianovecchi.mikrokanon.AIMUSIC

import com.cristianovecchi.mikrokanon.rangeTo

enum class EnsembleType {
    STRINGS, WOODWINDS, STRING_ORCHESTRA, BRASS, SAXOPHONES, FLUTES,
    DOUBLEREEDS,  CLARINETS, BASSOONS, CELLOS, PIANO, PIERROT,
    BAROQUE, PLUCKED_STRINGS
}
data class EnsemblePart( val instrument: Int, val octave: Int,
                         val allRange: IntRange = PIANO_ALL,
                         val colorRange: IntRange = allRange) // if colorRange is not specified, allRange will be taken


object Ensembles {
    fun getEnsemble(nParts: Int, type: EnsembleType): List<EnsemblePart> {
        return when (type) {
            EnsembleType.STRINGS -> getStrings(nParts)
            EnsembleType.WOODWINDS -> getWoodwinds(nParts)
            EnsembleType.STRING_ORCHESTRA -> getStringOrchestra(nParts)
            EnsembleType.BRASS -> getBrass(nParts)
            EnsembleType.SAXOPHONES -> getSaxophones(nParts)
            EnsembleType.FLUTES -> getFlutes(nParts)
            EnsembleType.DOUBLEREEDS -> getDoubleReeds(nParts)
            EnsembleType.CLARINETS -> getClarinets(nParts)
            EnsembleType.BASSOONS -> getBassoons(nParts)
            EnsembleType.CELLOS -> getCellos(nParts)
            EnsembleType.PIANO -> getPiano(nParts)
            EnsembleType.PIERROT -> getPierrot(nParts)
            EnsembleType.BAROQUE -> getBaroque(nParts)
            EnsembleType.PLUCKED_STRINGS -> getPluckedStrings(nParts)
        }
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
                PART_BASSOON_LOW
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
                PART_BASSOON_LOW
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
                PART_BASSOON_LOW_MIDDLE,
                PART_BASSOON_LOW
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
                PART_BASSOON_LOW,

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
                PART_BASSOON_LOW,
                PART_BASSOON_LOW,
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
                EnsemblePart(GUITAR_HARMONICS, 6, GUITAR_ALL, GUITAR_HIGHEST5),
                PARTS_HARP[5],
                EnsemblePart(CLEAN_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5..GUITAR_HIGH5),
                EnsemblePart(NYLON_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5),
                PARTS_HARP[4],
                EnsemblePart(NYLON_GUITAR, 3, GUITAR_LOW5),
                PARTS_HARP[3]
            )
            8 -> listOf(
                EnsemblePart(GUITAR_HARMONICS, 6, GUITAR_ALL, GUITAR_HIGHEST5),
                PARTS_HARP[5],
                EnsemblePart(BANJO, 5),
                EnsemblePart(CLEAN_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5..GUITAR_HIGH5),
                EnsemblePart(NYLON_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5),
                PARTS_HARP[4],
                EnsemblePart(NYLON_GUITAR, 3, GUITAR_ALL, GUITAR_LOW5),
                PARTS_HARP[3]
            )
            9 -> listOf(
                EnsemblePart(GUITAR_HARMONICS, 6, GUITAR_ALL, GUITAR_HIGHEST5),
                PARTS_HARP[5],
                EnsemblePart(BANJO, 5),
                EnsemblePart(CLEAN_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5..GUITAR_HIGH5),
                EnsemblePart(NYLON_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5),
                EnsemblePart(SITAR, 4),
                PARTS_HARP[3],
                EnsemblePart(NYLON_GUITAR, 3, GUITAR_ALL, GUITAR_LOW5),
                PARTS_HARP[2]
            )
            10 -> listOf(
                EnsemblePart(GUITAR_HARMONICS, 6, GUITAR_ALL, GUITAR_HIGHEST5),
                PARTS_HARP[5],
                EnsemblePart(BANJO, 5),
                EnsemblePart(KOTO, 5),
                EnsemblePart(CLEAN_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5..GUITAR_HIGH5),
                EnsemblePart(NYLON_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5),
                EnsemblePart(SITAR, 4),
                PARTS_HARP[3],
                EnsemblePart(NYLON_GUITAR, 3, GUITAR_LOW5),
                PARTS_HARP[2]
            )
            11 -> listOf(
                EnsemblePart(GUITAR_HARMONICS, 6, GUITAR_ALL, GUITAR_HIGHEST5),
                PARTS_HARP[5],
                EnsemblePart(BANJO, 5),
                EnsemblePart(KOTO, 5),
                EnsemblePart(CLEAN_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5..GUITAR_HIGH5),
                EnsemblePart(NYLON_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5),
                EnsemblePart(SITAR, 4),
                PARTS_HARP[3],
                EnsemblePart(SHAMISEN, 3),
                EnsemblePart(NYLON_GUITAR, 3, GUITAR_ALL, GUITAR_LOW5),
                PARTS_HARP[2]
            )
            12 -> listOf(
                EnsemblePart(GUITAR_HARMONICS, 6, GUITAR_ALL, GUITAR_HIGHEST5),
                PARTS_HARP[5],
                EnsemblePart(BANJO, 5),
                EnsemblePart(KOTO, 5),
                EnsemblePart(CLEAN_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5..GUITAR_HIGH5),
                EnsemblePart(NYLON_GUITAR, 4, GUITAR_ALL, GUITAR_MIDDLE5),
                EnsemblePart(SITAR, 4),
                PARTS_HARP[3],
                EnsemblePart(CLEAN_GUITAR, 3, GUITAR_ALL, GUITAR_LOW5..GUITAR_MIDDLE5),
                EnsemblePart(SHAMISEN, 3),
                EnsemblePart(NYLON_GUITAR, 3, GUITAR_ALL, GUITAR_LOW5),
                PARTS_HARP[2]
            )
            else -> listOf()
        }
    }
}





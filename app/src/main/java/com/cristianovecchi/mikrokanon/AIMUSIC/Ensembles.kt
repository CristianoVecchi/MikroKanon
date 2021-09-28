package com.cristianovecchi.mikrokanon.AIMUSIC

import com.cristianovecchi.mikrokanon.rangeTo

enum class EnsembleType {
    STRINGS, WOODWINDS, STRING_ORCHESTRA, BRASS, SAXOPHONES, FLUTES,
    DOUBLEREEDS,  CLARINETS, BASSOONS, CELLOS, PIANO, PIERROT,
    BAROQUE, PLUCKED_STRINGS
}


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
                PART_STRING_ORCHESTRA_CELLO_LOW3
            )
            5 -> listOf(
                PART_VIOLIN_HIGH_HIGHEST,
                PART_VIOLIN_MIDDLE_HIGH_HIGHEST,
                PART_VIOLA_MIDDLE_HIGH,
                PART_VIOLA_LOW_MIDDLE,
                PART_STRING_ORCHESTRA_CELLO_LOW3
            )
            6 -> listOf(
                PART_VIOLIN_HIGH_HIGHEST,
                PART_VIOLIN_MIDDLE_HIGH_HIGHEST,
                PART_VIOLA_MIDDLE_HIGH,
                PART_VIOLA_LOW_MIDDLE,
                PART_CELLO_MIDDLE_HIGH,
                PART_STRING_ORCHESTRA_CELLO_LOW3

            )
            7 -> listOf(
                PART_VIOLIN_HIGH_HIGHEST,
                PART_VIOLIN_MIDDLE_HIGH_HIGHEST,
                PART_VIOLA_MIDDLE_HIGH,
                PART_VIOLA_LOW_MIDDLE,
                PART_CELLO_MIDDLE_HIGH,
                PART_STRING_ORCHESTRA_CELLO_LOW3,
                PART_STRING_ORCHESTRA_DB_LOW3
            )
            8 -> listOf(
                PART_VIOLIN_HIGHEST,
                PART_VIOLIN_HIGH_HIGHEST,
                PART_VIOLIN_MIDDLE_HIGH_HIGHEST,
                PART_VIOLA_MIDDLE_HIGH,
                PART_VIOLA_LOW_MIDDLE,
                PART_CELLO_MIDDLE_HIGH,
                PART_STRING_ORCHESTRA_CELLO_LOW3,
                PART_STRING_ORCHESTRA_DB_LOW3
            )
            9 -> listOf(
                PART_VIOLIN_HIGHEST,
                PART_VIOLIN_HIGH_HIGHEST,
                PART_VIOLIN_MIDDLE_HIGH_HIGHEST,
                PART_VIOLA_MIDDLE_HIGH,
                PART_VIOLA_MIDDLE,
                PART_VIOLA_LOW_MIDDLE,
                PART_CELLO_MIDDLE_HIGH,
                PART_STRING_ORCHESTRA_CELLO_LOW3,
                PART_STRING_ORCHESTRA_DB_LOW3
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
                PART_STRING_ORCHESTRA_CELLO_LOW3,
                PART_STRING_ORCHESTRA_CELLO_LOW3,
                PART_STRING_ORCHESTRA_DB_LOW3,
                PART_STRING_ORCHESTRA_DB_LOW3
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
                EnsemblePart(48, 4), // string ensemble
                EnsemblePart(48, 3), // string ensemble
                EnsemblePart(48, 2), // string ensemble
            )
            4, 5 -> listOf(
                EnsemblePart(48, 4), // string ensemble
                EnsemblePart(48, 4), // string ensemble
                EnsemblePart(48, 3), // string ensemble
                EnsemblePart(48, 3), // string ensemble
                EnsemblePart(48, 2), // string ensemble
            )
            6, 7 -> listOf(
                EnsemblePart(48, 5), // string ensemble
                EnsemblePart(48, 4), // string ensemble
                EnsemblePart(48, 4), // string ensemble
                EnsemblePart(48, 3), // string ensemble
                EnsemblePart(48, 3), // string ensemble
                EnsemblePart(48, 2), // string ensemble
                EnsemblePart(48, 1), // string ensemble
            )
            8, 9 -> listOf(
                EnsemblePart(48, 5), // string ensemble
                EnsemblePart(48, 5), // string ensemble
                EnsemblePart(48, 4), // string ensemble
                EnsemblePart(48, 4), // string ensemble
                EnsemblePart(48, 3), // string ensemble
                EnsemblePart(48, 3), // string ensemble
                EnsemblePart(48, 2), // string ensemble
                EnsemblePart(48, 2), // string ensemble
                EnsemblePart(48, 1), // string ensemble
            )
            in 10..12 -> listOf(
                EnsemblePart(48, 6), // string ensemble
                EnsemblePart(48, 5), // string ensemble
                EnsemblePart(48, 5), // string ensemble
                EnsemblePart(48, 5), // string ensemble
                EnsemblePart(48, 4), // string ensemble
                EnsemblePart(48, 4), // string ensemble
                EnsemblePart(48, 4), // string ensemble
                EnsemblePart(48, 3), // string ensemble
                EnsemblePart(48, 3), // string ensemble
                EnsemblePart(48, 2), // string ensemble
                EnsemblePart(48, 2), // string ensemble
                EnsemblePart(48, 1), // string ensemble
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
                EnsemblePart(65, 4), // Alto Sax
                EnsemblePart(66, 3)  // Tenor Sax
            )
            3 -> listOf(
                EnsemblePart(64, 5), // Soprano Sax
                EnsemblePart(65, 4), // Alto Sax
                EnsemblePart(66, 3)  // Tenor Sax
            )
            4 -> listOf(
                EnsemblePart(64, 5), // Soprano Sax
                EnsemblePart(65, 4), // Alto Sax
                EnsemblePart(66, 3),  // Tenor Sax
                EnsemblePart(67, 2)  // Tenor Sax
            )
            5 -> listOf(
                EnsemblePart(64, 5), // Soprano Sax
                EnsemblePart(65, 4), // Alto Sax
                EnsemblePart(65, 4), // Alto Sax
                EnsemblePart(66, 3),  // Tenor Sax
                EnsemblePart(67, 2)  // Tenor Sax
            )
            6 -> listOf(
                EnsemblePart(64, 5), // Soprano Sax
                EnsemblePart(64, 5), // Soprano Sax
                EnsemblePart(65, 4), // Alto Sax
                EnsemblePart(65, 4), // Alto Sax
                EnsemblePart(66, 3),  // Tenor Sax
                EnsemblePart(67, 2)  // Tenor Sax
            )
            7 -> listOf(
                EnsemblePart(64, 5), // Soprano Sax
                EnsemblePart(64, 5), // Soprano Sax
                EnsemblePart(65, 4), // Alto Sax
                EnsemblePart(65, 4), // Alto Sax
                EnsemblePart(66, 3),  // Tenor Sax
                EnsemblePart(66, 3),  // Tenor Sax
                EnsemblePart(67, 2)  // Tenor Sax
            )
            8 -> listOf(
                EnsemblePart(64, 6), // Soprano Sax
                EnsemblePart(64, 5), // Soprano Sax
                EnsemblePart(64, 5), // Soprano Sax
                EnsemblePart(65, 4), // Alto Sax
                EnsemblePart(65, 4), // Alto Sax
                EnsemblePart(66, 3),  // Tenor Sax
                EnsemblePart(66, 3),  // Tenor Sax
                EnsemblePart(67, 2)  // Tenor Sax
            )
            9 -> listOf(
                EnsemblePart(64, 6), // Soprano Sax
                EnsemblePart(64, 5), // Soprano Sax
                EnsemblePart(64, 5), // Soprano Sax
                EnsemblePart(65, 4), // Alto Sax
                EnsemblePart(65, 4), // Alto Sax
                EnsemblePart(66, 3),  // Tenor Sax
                EnsemblePart(66, 3),  // Tenor Sax
                EnsemblePart(67, 2),  // Tenor Sax
                EnsemblePart(67, 2)  // Tenor Sax
            )
            in 10..12 -> listOf(
                EnsemblePart(64, 6), // Soprano Sax
                EnsemblePart(64, 5), // Soprano Sax
                EnsemblePart(64, 5), // Soprano Sax
                EnsemblePart(64, 5), // Soprano Sax
                EnsemblePart(65, 4), // Alto Sax
                EnsemblePart(65, 4), // Alto Sax
                EnsemblePart(65, 4), // Alto Sax
                EnsemblePart(65, 3), // Alto Sax
                EnsemblePart(66, 3),  // Tenor Sax
                EnsemblePart(66, 3),  // Tenor Sax
                EnsemblePart(67, 2),  // Tenor Sax
                EnsemblePart(67, 2)  // Tenor Sax
            )
            else -> listOf()
        }
    }
        fun getFlutes(nParts: Int): List<EnsemblePart> {
            return when (nParts) {
                1, 2 -> listOf(
                    EnsemblePart(73, 5), // Flute
                    EnsemblePart(73, 4)  // Flute
                )
                3, 4 -> listOf(
                    EnsemblePart(72, 6), // Piccolo
                    EnsemblePart(73, 5), // Flute
                    EnsemblePart(73, 4),  // Flute
                    EnsemblePart(73, 3)  // Flute
                )
                5 -> listOf(
                    EnsemblePart(72, 6), // Piccolo
                    EnsemblePart(73, 5), // Flute
                    EnsemblePart(73, 5), // Flute
                    EnsemblePart(73, 4),  // Flute
                    EnsemblePart(73, 3)  // Flute
                )
                6 -> listOf(
                    EnsemblePart(72, 6), // Piccolo
                    EnsemblePart(72, 6), // Piccolo
                    EnsemblePart(73, 5), // Flute
                    EnsemblePart(73, 5), // Flute
                    EnsemblePart(73, 4),  // Flute
                    EnsemblePart(73, 3)  // Flute
                )
                7 -> listOf(
                    EnsemblePart(72, 6), // Piccolo
                    EnsemblePart(72, 6), // Piccolo
                    EnsemblePart(73, 5), // Flute
                    EnsemblePart(73, 5), // Flute
                    EnsemblePart(73, 4),  // Flute
                    EnsemblePart(73, 4),  // Flute
                    EnsemblePart(73, 3)  // Flute
                )
                8 -> listOf(
                    EnsemblePart(72, 6), // Piccolo
                    EnsemblePart(72, 6), // Piccolo
                    EnsemblePart(73, 5), // Flute
                    EnsemblePart(73, 5), // Flute
                    EnsemblePart(73, 5), // Flute
                    EnsemblePart(73, 4),  // Flute
                    EnsemblePart(73, 4),  // Flute
                    EnsemblePart(73, 3)  // Flute
                )
                9 -> listOf(
                    EnsemblePart(72, 6), // Piccolo
                    EnsemblePart(72, 6), // Piccolo
                    EnsemblePart(73, 5), // Flute
                    EnsemblePart(73, 5), // Flute
                    EnsemblePart(73, 5), // Flute
                    EnsemblePart(73, 4),  // Flute
                    EnsemblePart(73, 4),  // Flute
                    EnsemblePart(73, 3),  // Flute
                    EnsemblePart(73, 3)  // Flute
                )
                in 10..12 -> listOf(
                    EnsemblePart(72, 6), // Piccolo
                    EnsemblePart(72, 6), // Piccolo
                    EnsemblePart(72, 6), // Piccolo
                    EnsemblePart(73, 5), // Flute
                    EnsemblePart(73, 5), // Flute
                    EnsemblePart(73, 5), // Flute
                    EnsemblePart(73, 5), // Flute
                    EnsemblePart(73, 4),  // Flute
                    EnsemblePart(73, 4),  // Flute
                    EnsemblePart(73, 4),  // Flute
                    EnsemblePart(73, 3),  // Flute
                    EnsemblePart(73, 3)  // Flute
                )
                else -> listOf()
            }
        }
    fun getDoubleReeds(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2 -> listOf(
                EnsemblePart(68, 4), // Oboe
                EnsemblePart(70, 3)  // Bassoon
            )
            3 -> listOf(
                EnsemblePart(68, 5), // Oboe
                EnsemblePart(69, 4), // English Horn
                EnsemblePart(70, 3)  // Bassoon
            )
            4 -> listOf(
                EnsemblePart(68, 5), // Oboe
                EnsemblePart(69, 4), // English Horn
                EnsemblePart(70, 3),  // Bassoon
                EnsemblePart(70, 2)  // Bassoon
            )
            5 -> listOf(
                EnsemblePart(68, 5), // Oboe
                EnsemblePart(68, 5), // Oboe
                EnsemblePart(69, 4), // English Horn
                EnsemblePart(70, 3),  // Bassoon
                EnsemblePart(70, 2)  // Bassoon
            )
            6 -> listOf(
                EnsemblePart(68, 5), // Oboe
                EnsemblePart(68, 5), // Oboe
                EnsemblePart(69, 4), // English Horn
                EnsemblePart(70, 3),  // Bassoon
                EnsemblePart(70, 3),  // Bassoon
                EnsemblePart(70, 2)  // Bassoon
            )
            7, 8 -> listOf(
                EnsemblePart(68, 5), // Oboe
                EnsemblePart(68, 5), // Oboe
                EnsemblePart(69, 4), // English Horn
                EnsemblePart(69, 4), // English Horn
                EnsemblePart(70, 3),  // Bassoon
                EnsemblePart(70, 3),  // Bassoon
                EnsemblePart(70, 2),  // Bassoon
                EnsemblePart(70, 2)  // Bassoon
            )
            9 -> listOf(
                EnsemblePart(68, 5), // Oboe
                EnsemblePart(68, 5), // Oboe
                EnsemblePart(68, 5), // Oboe
                EnsemblePart(69, 4), // English Horn
                EnsemblePart(69, 4), // English Horn
                EnsemblePart(70, 3),  // Bassoon
                EnsemblePart(70, 3),  // Bassoon
                EnsemblePart(70, 2),  // Bassoon
                EnsemblePart(70, 2)  // Bassoon
            )
            in 10..12 -> listOf(
                EnsemblePart(68, 6), // Oboe
                EnsemblePart(68, 5), // Oboe
                EnsemblePart(68, 5), // Oboe
                EnsemblePart(68, 5), // Oboe
                EnsemblePart(68, 4), // Oboe
                EnsemblePart(69, 4), // English Horn
                EnsemblePart(69, 4), // English Horn
                EnsemblePart(69, 3), // English Horn
                EnsemblePart(70, 3),  // Bassoon
                EnsemblePart(70, 3),  // Bassoon
                EnsemblePart(70, 2),  // Bassoon
                EnsemblePart(70, 2)  // Bassoon
            )
            else -> listOf()
        }
    }
    fun getClarinets(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2, 3 -> listOf(
                EnsemblePart(71, 5), // Clarinet
                EnsemblePart(71, 4),  // Clarinet
                EnsemblePart(71, 3)  // Clarinet
            )
            4, 5 -> listOf(
                EnsemblePart(71, 5), // Clarinet
                EnsemblePart(71, 5), // Clarinet
                EnsemblePart(71, 4), // Clarinet
                EnsemblePart(71, 3),  // Clarinet
                EnsemblePart(71, 2)  // Clarinet
            )
            6, 7 -> listOf(
                EnsemblePart(71, 5), // Clarinet
                EnsemblePart(71, 5), // Clarinet
                EnsemblePart(71, 4), // Clarinet
                EnsemblePart(71, 4), // Clarinet
                EnsemblePart(71, 3),  // Clarinet
                EnsemblePart(71, 3),  // Clarinet
                EnsemblePart(71, 2)  // Clarinet
            )
            8, 9 -> listOf(
                EnsemblePart(71, 5), // Clarinet
                EnsemblePart(71, 5), // Clarinet
                EnsemblePart(71, 5), // Clarinet
                EnsemblePart(71, 4), // Clarinet
                EnsemblePart(71, 4), // Clarinet
                EnsemblePart(71, 4), // Clarinet
                EnsemblePart(71, 3),  // Clarinet
                EnsemblePart(71, 3),  // Clarinet
                EnsemblePart(71, 2)  // Clarinet
            )
            in 10..12 -> listOf(
                EnsemblePart(71, 6), // Clarinet
                EnsemblePart(71, 5), // Clarinet
                EnsemblePart(71, 5), // Clarinet
                EnsemblePart(71, 5), // Clarinet
                EnsemblePart(71, 4), // Clarinet
                EnsemblePart(71, 4), // Clarinet
                EnsemblePart(71, 4), // Clarinet
                EnsemblePart(71, 3),  // Clarinet
                EnsemblePart(71, 3),  // Clarinet
                EnsemblePart(71, 3),  // Clarinet
                EnsemblePart(71, 2),  // Clarinet
                EnsemblePart(71, 2)  // Clarinet
            )
            else -> listOf()
        }
    }
    fun getBassoons(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2, 3 -> listOf(
                EnsemblePart(70, 4), // Bassoon
                EnsemblePart(70, 3), // Bassoon
                EnsemblePart(70, 2)  // Bassoon
            )
            in (4..6) -> listOf(
                EnsemblePart(70, 4), // Bassoon
                EnsemblePart(70, 4), // Bassoon
                EnsemblePart(70, 3), // Bassoon
                EnsemblePart(70, 3),  // Bassoon
                EnsemblePart(70, 2),  // Bassoon
                EnsemblePart(70, 2),  // Bassoon
            )
            in (7..9) -> listOf(
                EnsemblePart(70, 4), // Bassoon
                EnsemblePart(70, 4), // Bassoon
                EnsemblePart(70, 4), // Bassoon
                EnsemblePart(70, 4), // Bassoon
                EnsemblePart(70, 3), // Bassoon
                EnsemblePart(70, 3), // Bassoon
                EnsemblePart(70, 3),  // Bassoon
                EnsemblePart(70, 2),  // Bassoon
                EnsemblePart(70, 2),  // Bassoon
            )
            in (10..12) -> listOf(
                EnsemblePart(70, 5), // Bassoon
                EnsemblePart(70, 4), // Bassoon
                EnsemblePart(70, 4), // Bassoon
                EnsemblePart(70, 4), // Bassoon
                EnsemblePart(70, 4), // Bassoon
                EnsemblePart(70, 4), // Bassoon
                EnsemblePart(70, 3), // Bassoon
                EnsemblePart(70, 3), // Bassoon
                EnsemblePart(70, 3), // Bassoon
                EnsemblePart(70, 3),  // Bassoon
                EnsemblePart(70, 2),  // Bassoon
                EnsemblePart(70, 2),  // Bassoon
            )
            else -> listOf()
        }
    }
    fun getCellos(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2, 3 -> listOf(
                EnsemblePart(42, 4), // Cello
                EnsemblePart(42, 3), // Cello
                EnsemblePart(STRING_ORCHESTRA, 2),  // Cello

            )
            in (4..6) -> listOf(
                EnsemblePart(42, 4), // Cello
                EnsemblePart(42, 4), // Cello
                EnsemblePart(42, 3), // Cello
                EnsemblePart(42, 3),  // Cello
                EnsemblePart(STRING_ORCHESTRA, 2),  // Cello
                EnsemblePart(STRING_ORCHESTRA, 2),  // Cello
            )
            in (7..9) -> listOf(
                EnsemblePart(42, 4), // Cello
                EnsemblePart(42, 4), // Cello
                EnsemblePart(42, 4), // Cello
                EnsemblePart(42, 4), // Cello
                EnsemblePart(42, 3), // Cello
                EnsemblePart(42, 3), // Cello
                EnsemblePart(42, 3),  // Cello
                EnsemblePart(STRING_ORCHESTRA, 2),  // Cello
                EnsemblePart(STRING_ORCHESTRA, 2),  // Cello
            )
            in 10..12 -> listOf(
                EnsemblePart(42, 5), // Cello
                EnsemblePart(42, 4), // Cello
                EnsemblePart(42, 4), // Cello
                EnsemblePart(42, 4), // Cello
                EnsemblePart(42, 4), // Cello
                EnsemblePart(42, 4), // Cello
                EnsemblePart(42, 3), // Cello
                EnsemblePart(42, 3), // Cello
                EnsemblePart(42, 3), // Cello
                EnsemblePart(42, 3),  // Cello
                EnsemblePart(STRING_ORCHESTRA, 2),  // Cello
                EnsemblePart(STRING_ORCHESTRA, 2),  // Cello
            )
            else -> listOf()
        }
    }
    fun getPiano(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2, 3 -> listOf(
                EnsemblePart(0, 4), // Piano
                EnsemblePart(0, 3), // Piano
                EnsemblePart(0, 2)  // Piano
            )
            in (4..6) -> listOf(
                EnsemblePart(0, 5), // Piano
                EnsemblePart(0, 4), // Piano
                EnsemblePart(0, 4), // Piano
                EnsemblePart(0, 3),  // Piano
                EnsemblePart(0, 3),  // Piano
                EnsemblePart(0, 2),  // Piano
            )
            7 -> listOf(
                EnsemblePart(0, 5), // Piano
                EnsemblePart(0, 4), // Piano
                EnsemblePart(0, 4), // Piano
                EnsemblePart(0, 3),  // Piano
                EnsemblePart(0, 3),  // Piano
                EnsemblePart(0, 2),  // Piano
                EnsemblePart(0, 1),  // Piano
            )
            8 -> listOf(
                EnsemblePart(0, 6), // Piano
                EnsemblePart(0, 5), // Piano
                EnsemblePart(0, 5), // Piano
                EnsemblePart(0, 4), // Piano
                EnsemblePart(0, 4),  // Piano
                EnsemblePart(0, 3),  // Piano
                EnsemblePart(0, 2),  // Piano
                EnsemblePart(0, 1),  // Piano
            )
            9 -> listOf(
                EnsemblePart(0, 6), // Piano
                EnsemblePart(0, 5), // Piano
                EnsemblePart(0, 5), // Piano
                EnsemblePart(0, 4), // Piano
                EnsemblePart(0, 4),  // Piano
                EnsemblePart(0, 3),  // Piano
                EnsemblePart(0, 3),  // Piano
                EnsemblePart(0, 2),  // Piano
                EnsemblePart(0, 1),  // Piano
            )
            in 10..12 -> listOf(
                EnsemblePart(0, 6), // Piano
                EnsemblePart(0, 6), // Piano
                EnsemblePart(0, 5), // Piano
                EnsemblePart(0, 5), // Piano
                EnsemblePart(0, 5), // Piano
                EnsemblePart(0, 4), // Piano
                EnsemblePart(0, 4),  // Piano
                EnsemblePart(0, 3),  // Piano
                EnsemblePart(0, 3),  // Piano
                EnsemblePart(0, 2),  // Piano
                EnsemblePart(0, 2),  // Piano
                EnsemblePart(0, 1),  // Piano
            )
            else -> listOf()
        }
    }
    fun getPierrot(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2, 3 -> listOf(
                EnsemblePart(FLUTE, 4),
                EnsemblePart(PIANO, 3),
                EnsemblePart(STRING_ORCHESTRA, 2)
            )
            in (4..6) -> listOf(
                EnsemblePart(FLUTE, 5),
                EnsemblePart(VIOLIN, 4),
                EnsemblePart(PIANO, 4),
                EnsemblePart(CLARINET, 3),
                EnsemblePart(STRING_ORCHESTRA, 2),
                EnsemblePart(PIANO, 1),
            )
            7 -> listOf(
                EnsemblePart(FLUTE, 5),
                EnsemblePart(VIOLIN, 4),
                EnsemblePart(CLARINET, 4),
                EnsemblePart(PIANO, 4),
                EnsemblePart(CELLO, 3),
                EnsemblePart(PIANO, 2),
                EnsemblePart(PIANO, 1),
            )
            8 -> listOf(
                EnsemblePart(PIANO, 6),
                EnsemblePart(FLUTE, 5),
                EnsemblePart(VIOLIN, 4),
                EnsemblePart(CLARINET, 4),
                EnsemblePart(PIANO, 3),
                EnsemblePart(CELLO, 3),
                EnsemblePart(PIANO, 2),
                EnsemblePart(PIANO, 1),
            )
            9 -> listOf(
                EnsemblePart(PIANO, 6),
                EnsemblePart(FLUTE, 5),
                EnsemblePart(VIOLIN, 4),
                EnsemblePart(CLARINET, 4),
                EnsemblePart(PIANO, 4),
                EnsemblePart(PIANO, 3),
                EnsemblePart(CELLO, 3),
                EnsemblePart(PIANO, 2),
                EnsemblePart(PIANO, 1),
            )
            in 10..12 -> listOf(
                EnsemblePart(PIANO, 7),
                EnsemblePart(PIANO, 6),
                EnsemblePart(FLUTE, 5),
                EnsemblePart(PIANO, 5),
                EnsemblePart(VIOLIN, 4),
                EnsemblePart(CLARINET, 4),
                EnsemblePart(PIANO, 4),
                EnsemblePart(PIANO, 3),
                EnsemblePart(CELLO, 3),
                EnsemblePart(PIANO, 2),
                EnsemblePart(PIANO, 2),
                EnsemblePart(PIANO, 1),
            )
            else -> listOf()
        }
    }
    fun getBaroque(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2 -> listOf(
                EnsemblePart(HARPSICHORD, 4),
                EnsemblePart(HARPSICHORD, 2)
            )
            3 -> listOf(
                EnsemblePart(FLUTE, 5),
                EnsemblePart(HARPSICHORD, 4),
                EnsemblePart(HARPSICHORD, 3)
            )
            4 -> listOf(
                EnsemblePart(FLUTE, 5),
                EnsemblePart(HARPSICHORD, 4),
                EnsemblePart(HARPSICHORD, 3),
                EnsemblePart(STRING_ORCHESTRA, 2)
            )
            5 -> listOf(
                EnsemblePart(FLUTE, 5),
                EnsemblePart(HARPSICHORD, 4),
                EnsemblePart(HARPSICHORD, 4),
                EnsemblePart(HARPSICHORD, 3),
                EnsemblePart(STRING_ORCHESTRA, 2)
            )
            6 -> listOf(
                EnsemblePart(FLUTE, 5),
                EnsemblePart(VIOLIN, 5),
                EnsemblePart(HARPSICHORD, 4),
                EnsemblePart(HARPSICHORD, 4),
                EnsemblePart(HARPSICHORD, 3),
                EnsemblePart(STRING_ORCHESTRA, 2)
            )
            7 -> listOf(
                EnsemblePart(FLUTE, 5),
                EnsemblePart(VIOLIN, 5),
                EnsemblePart(HARPSICHORD, 4),
                EnsemblePart(HARPSICHORD, 4),
                EnsemblePart(HARPSICHORD, 3),
                EnsemblePart(HARPSICHORD, 3),
                EnsemblePart(STRING_ORCHESTRA, 2)
            )
            8 -> listOf(
                EnsemblePart(FLUTE, 5),
                EnsemblePart(VIOLIN, 5),
                EnsemblePart(OBOE, 5),
                EnsemblePart(HARPSICHORD, 4),
                EnsemblePart(HARPSICHORD, 4),
                EnsemblePart(HARPSICHORD, 3),
                EnsemblePart(HARPSICHORD, 3),
                EnsemblePart(STRING_ORCHESTRA, 2)
            )
            9 -> listOf(
                EnsemblePart(FLUTE, 5),
                EnsemblePart(VIOLIN, 5),
                EnsemblePart(OBOE, 5),
                EnsemblePart(VIOLA, 4),
                EnsemblePart(HARPSICHORD, 4),
                EnsemblePart(HARPSICHORD, 4),
                EnsemblePart(HARPSICHORD, 3),
                EnsemblePart(HARPSICHORD, 3),
                EnsemblePart(STRING_ORCHESTRA, 2)
            )
            10 -> listOf(
                EnsemblePart(FLUTE, 5),
                EnsemblePart(VIOLIN, 5),
                EnsemblePart(OBOE, 5),
                EnsemblePart(VIOLA, 4),
                EnsemblePart(HARPSICHORD, 4),
                EnsemblePart(HARPSICHORD, 4),
                EnsemblePart(HARPSICHORD, 3),
                EnsemblePart(HARPSICHORD, 3),
                EnsemblePart(HARPSICHORD, 3),
                EnsemblePart(STRING_ORCHESTRA, 2)
            )
            11 -> listOf(
                EnsemblePart(FLUTE, 5),
                EnsemblePart(VIOLIN, 5),
                EnsemblePart(OBOE, 5),
                EnsemblePart(VIOLA, 4),
                EnsemblePart(HARPSICHORD, 4),
                EnsemblePart(HARPSICHORD, 4),
                EnsemblePart(HARPSICHORD, 3),
                EnsemblePart(HARPSICHORD, 3),
                EnsemblePart(HARPSICHORD, 3),
                EnsemblePart(BASSOON, 3),
                EnsemblePart(STRING_ORCHESTRA, 2)
            )
            12 -> listOf(
                EnsemblePart(FLUTE, 5),
                EnsemblePart(VIOLIN, 5),
                EnsemblePart(OBOE, 5),
                EnsemblePart(VIOLA, 4),
                EnsemblePart(VIOLA, 4),
                EnsemblePart(HARPSICHORD, 4),
                EnsemblePart(HARPSICHORD, 4),
                EnsemblePart(HARPSICHORD, 3),
                EnsemblePart(HARPSICHORD, 3),
                EnsemblePart(HARPSICHORD, 3),
                EnsemblePart(BASSOON, 3),
                EnsemblePart(STRING_ORCHESTRA, 2)
            )
            else -> listOf()
        }
    }
    fun getPluckedStrings(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            1, 2 -> listOf(
                EnsemblePart(CLEAN_GUITAR, 4),
                EnsemblePart(NYLON_GUITAR, 3)
            )
            3 -> listOf(
                EnsemblePart(CLEAN_GUITAR, 4),
                EnsemblePart(NYLON_GUITAR, 3),
                EnsemblePart(HARP, 3)
            )
            4 -> listOf(
                EnsemblePart(HARP, 5),
                EnsemblePart(CLEAN_GUITAR, 4),
                EnsemblePart(NYLON_GUITAR, 3),
                EnsemblePart(HARP, 3)
            )
            5 -> listOf(
                EnsemblePart(HARP, 5),
                EnsemblePart(CLEAN_GUITAR, 4),
                EnsemblePart(NYLON_GUITAR, 4),
                EnsemblePart(NYLON_GUITAR, 3),
                EnsemblePart(HARP, 3)
            )
            6 -> listOf(
                EnsemblePart(HARP, 5),
                EnsemblePart(CLEAN_GUITAR, 4),
                EnsemblePart(NYLON_GUITAR, 4),
                EnsemblePart(HARP, 4),
                EnsemblePart(NYLON_GUITAR, 3),
                EnsemblePart(HARP, 3)
            )
            7 -> listOf(
                EnsemblePart(GUITAR_HARMONICS, 6),
                EnsemblePart(HARP, 5),
                EnsemblePart(CLEAN_GUITAR, 4),
                EnsemblePart(NYLON_GUITAR, 4),
                EnsemblePart(HARP, 4),
                EnsemblePart(NYLON_GUITAR, 3),
                EnsemblePart(HARP, 3)
            )
            8 -> listOf(
                EnsemblePart(GUITAR_HARMONICS, 6),
                EnsemblePart(HARP, 5),
                EnsemblePart(BANJO, 5),
                EnsemblePart(CLEAN_GUITAR, 4),
                EnsemblePart(NYLON_GUITAR, 4),
                EnsemblePart(HARP, 4),
                EnsemblePart(NYLON_GUITAR, 3),
                EnsemblePart(HARP, 3)
            )
            9 -> listOf(
                EnsemblePart(GUITAR_HARMONICS, 6),
                EnsemblePart(HARP, 5),
                EnsemblePart(BANJO, 5),
                EnsemblePart(CLEAN_GUITAR, 4),
                EnsemblePart(NYLON_GUITAR, 4),
                EnsemblePart(SITAR, 4),
                EnsemblePart(HARP, 3),
                EnsemblePart(NYLON_GUITAR, 3),
                EnsemblePart(HARP, 3)
            )
            10 -> listOf(
                EnsemblePart(GUITAR_HARMONICS, 6),
                EnsemblePart(HARP, 5),
                EnsemblePart(BANJO, 5),
                EnsemblePart(KOTO, 5),
                EnsemblePart(CLEAN_GUITAR, 4),
                EnsemblePart(NYLON_GUITAR, 4),
                EnsemblePart(SITAR, 4),
                EnsemblePart(HARP, 3),
                EnsemblePart(NYLON_GUITAR, 3),
                EnsemblePart(HARP, 3)
            )
            11 -> listOf(
                EnsemblePart(GUITAR_HARMONICS, 6),
                EnsemblePart(HARP, 5),
                EnsemblePart(BANJO, 5),
                EnsemblePart(KOTO, 5),
                EnsemblePart(CLEAN_GUITAR, 4),
                EnsemblePart(NYLON_GUITAR, 4),
                EnsemblePart(SITAR, 4),
                EnsemblePart(HARP, 3),
                EnsemblePart(SHAMISEN, 3),
                EnsemblePart(NYLON_GUITAR, 3),
                EnsemblePart(HARP, 3)
            )
            12 -> listOf(
                EnsemblePart(GUITAR_HARMONICS, 6),
                EnsemblePart(HARP, 5),
                EnsemblePart(BANJO, 5),
                EnsemblePart(KOTO, 5),
                EnsemblePart(CLEAN_GUITAR, 4),
                EnsemblePart(NYLON_GUITAR, 4),
                EnsemblePart(SITAR, 4),
                EnsemblePart(HARP, 3),
                EnsemblePart(CLEAN_GUITAR, 3),
                EnsemblePart(SHAMISEN, 3),
                EnsemblePart(NYLON_GUITAR, 3),
                EnsemblePart(HARP, 3)
            )
            else -> listOf()
        }
    }
}
data class EnsemblePart( val instrument: Int, val octave: Int,
                         val allRange: IntRange = PIANO_ALL,
                         val colorRange: IntRange = allRange) // if colorRange is not specified, allRange will be taken
const val PIANO = 0
const val HARPSICHORD = 6
const val NYLON_GUITAR = 24
const val STEEL_GUITAR = 25
const val CLEAN_GUITAR = 27
const val GUITAR_HARMONICS= 31
const val ACOUSTIC_BASS= 32
const val VIOLIN = 40
const val VIOLA = 41
const val CELLO = 42
const val DOUBLEBASS = 43
const val PIZZICATO = 45
const val HARP = 46
const val STRING_ORCHESTRA = 48
const val OBOE = 68
const val BASSOON = 70
const val FLUTE = 73
const val RECORDER = 74
const val CLARINET = 71
const val SITAR = 104
const val BANJO = 105
const val SHAMISEN = 106
const val KOTO = 107



package com.cristianovecchi.mikrokanon.AIMUSIC

enum class EnsembleType {
    STRINGS, WOODWINDS, STRING_ORCHESTRA, BRASS, SAXOPHONES, FLUTES, DOUBLEREEDS,  BASSOONS, CELLOS, CLARINETS, PIANO
}
object Ensembles {
    fun getEnsemble(nParts: Int, type: EnsembleType): List<EnsemblePart> {
        return when (type) {
            EnsembleType.STRINGS -> getStrings(nParts)
            EnsembleType.WOODWINDS -> getWoodwinds(nParts)
            EnsembleType.STRING_ORCHESTRA -> getStringOrchestra(nParts)
            EnsembleType.BRASS -> getBrass(nParts)
            else -> listOf()
        }
    }

    fun getStrings(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            2 -> listOf(
                EnsemblePart(40, 5), //violin
                EnsemblePart(42, 3)  // cello
            )
            3 -> listOf(
                EnsemblePart(40, 5), //violin
                EnsemblePart(41, 4), //viola
                EnsemblePart(42, 3)  // cello
            )
            4 -> listOf(
                EnsemblePart(40, 5), //violin
                EnsemblePart(40, 4), //violin
                EnsemblePart(41, 3), //viola
                EnsemblePart(42, 2)  // cello
            )
            5 -> listOf(
                EnsemblePart(40, 5), //violin
                EnsemblePart(40, 4), //violin
                EnsemblePart(41, 4), //viola
                EnsemblePart(41, 3), //viola
                EnsemblePart(42, 2)  // cello
            )
            6 -> listOf(
                EnsemblePart(40, 5), //violin
                EnsemblePart(40, 5), //violin
                EnsemblePart(41, 4), //viola
                EnsemblePart(41, 3), //viola
                EnsemblePart(42, 3),  // cello
                EnsemblePart(42, 2)  // cello
            )
            7 -> listOf(
                EnsemblePart(40, 5), //violin
                EnsemblePart(40, 5), //violin
                EnsemblePart(41, 4), //viola
                EnsemblePart(41, 3), //viola
                EnsemblePart(42, 3),  // cello
                EnsemblePart(42, 2),  // cello
                EnsemblePart(43, 1)  // double-bass
            )
            8 -> listOf(
                EnsemblePart(40, 6), //violin
                EnsemblePart(40, 5), //violin
                EnsemblePart(40, 5), //violin
                EnsemblePart(41, 4), //viola
                EnsemblePart(41, 3), //viola
                EnsemblePart(42, 3),  // cello
                EnsemblePart(42, 2),  // cello
                EnsemblePart(43, 1)  // double-bass
            )
            9 -> listOf(
                EnsemblePart(40, 6), //violin
                EnsemblePart(40, 5), //violin
                EnsemblePart(40, 5), //violin
                EnsemblePart(41, 4), //viola
                EnsemblePart(41, 4), //viola
                EnsemblePart(41, 3), //viola
                EnsemblePart(42, 3),  // cello
                EnsemblePart(42, 2),  // cello
                EnsemblePart(43, 1)  // double-bass
            )
            else -> listOf()
        }
    }

    fun getWoodwinds(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            2 -> listOf(
                EnsemblePart(73, 5), //flute
                EnsemblePart(70, 3)  // bassoon
            )
            3 -> listOf(
                EnsemblePart(73, 5), //flute
                EnsemblePart(71, 4), //clarinet
                EnsemblePart(70, 3)  // bassoon
            )
            4 -> listOf(
                EnsemblePart(73, 5), //flute
                EnsemblePart(60, 4), //oboe
                EnsemblePart(71, 3), //clarinet
                EnsemblePart(70, 2)  // bassoon
            )
            5 -> listOf(
                EnsemblePart(73, 5), //flute
                EnsemblePart(60, 5), //oboe
                EnsemblePart(71, 4), //clarinet
                EnsemblePart(60, 3), // french horn
                EnsemblePart(70, 2)  // bassoon
            )
            6 -> listOf(
                EnsemblePart(73, 5), //flute
                EnsemblePart(60, 5), //oboe
                EnsemblePart(71, 4), //clarinet
                EnsemblePart(60, 3), // french horn
                EnsemblePart(71, 3), // bass clarinet
                EnsemblePart(70, 2)  // bassoon
            )
            7 -> listOf(
                EnsemblePart(73, 5), //flute
                EnsemblePart(60, 5), //oboe
                EnsemblePart(69, 4), // english horn
                EnsemblePart(71, 4), //clarinet
                EnsemblePart(60, 3), // french horn
                EnsemblePart(71, 3), // bass clarinet
                EnsemblePart(70, 2)  // bassoon
            )
            8 -> listOf(
                EnsemblePart(72, 6), //piccolo
                EnsemblePart(73, 5), //flute
                EnsemblePart(60, 5), //oboe
                EnsemblePart(69, 4), // english horn
                EnsemblePart(71, 4), //clarinet
                EnsemblePart(60, 3), // french horn
                EnsemblePart(71, 3), // bass clarinet
                EnsemblePart(70, 2)  // bassoon
            )
            9 -> listOf(
                EnsemblePart(72, 6), //piccolo
                EnsemblePart(73, 5), //flute
                EnsemblePart(60, 5), //oboe
                EnsemblePart(69, 4), // english horn
                EnsemblePart(71, 4), //clarinet
                EnsemblePart(60, 3), // french horn
                EnsemblePart(71, 3), // bass clarinet
                EnsemblePart(70, 2),  // bassoon
                EnsemblePart(70, 1)  // contrabassoon
            )
            else -> listOf()
        }
    }

    fun getStringOrchestra(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            2, 3 -> listOf(
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
            else -> listOf()
        }
    }
    fun getBrass(nParts: Int): List<EnsemblePart> {
        return when (nParts) {
            2 -> listOf(
                EnsemblePart(56, 4), //trumpet
                EnsemblePart(57, 3)  // trombone
            )
            3 -> listOf(
                EnsemblePart(56, 5), //trumpet
                EnsemblePart(60, 4), //french horn
                EnsemblePart(57, 3)  // trombone
            )
            4 -> listOf(
                EnsemblePart(56, 5), //trumpet
                EnsemblePart(56, 4), //trumpet
                EnsemblePart(60, 3), //french horn
                EnsemblePart(57, 2),  // trombone
            )
            5 -> listOf(
                EnsemblePart(56, 5), //trumpet
                EnsemblePart(56, 5), //trumpet
                EnsemblePart(60, 4), //french horn
                EnsemblePart(57, 3),  // trombone
                EnsemblePart(58, 2)  //tuba
            )
            6 -> listOf(
                EnsemblePart(56, 5), //trumpet
                EnsemblePart(56, 5), //trumpet
                EnsemblePart(60, 4), //french horn
                EnsemblePart(60, 4), //french horn
                EnsemblePart(57, 3),  // trombone
                EnsemblePart(58, 2)  //tuba
            )
            7 -> listOf(
                EnsemblePart(56, 5), //trumpet
                EnsemblePart(56, 5), //trumpet
                EnsemblePart(60, 4), //french horn
                EnsemblePart(60, 4), //french horn
                EnsemblePart(57, 3),  // trombone
                EnsemblePart(57, 3),  // trombone
                EnsemblePart(58, 2)  //tuba
            )
            8 -> listOf(
                EnsemblePart(56, 5), //trumpet
                EnsemblePart(56, 5), //trumpet
                EnsemblePart(60, 4), //french horn
                EnsemblePart(60, 4), //french horn
                EnsemblePart(60, 3), //french horn
                EnsemblePart(57, 3),  // trombone
                EnsemblePart(57, 2),  // trombone
                EnsemblePart(58, 1)  //tuba
            )
            9 -> listOf(
                EnsemblePart(56, 5), //trumpet
                EnsemblePart(56, 5), //trumpet
                EnsemblePart(60, 4), //french horn
                EnsemblePart(60, 4), //french horn
                EnsemblePart(60, 3), //french horn
                EnsemblePart(60, 3), //french horn
                EnsemblePart(57, 3),  // trombone
                EnsemblePart(57, 2),  // trombone
                EnsemblePart(58, 1)  //tuba
            )
            else -> listOf()
        }
    }
}
data class EnsemblePart( val instrument: Int, val octave: Int)
package com.cristianovecchi.mikrokanon.AIMUSIC

enum class EnsembleType {
    STRINGS, WOODWINDS, FLUTES, DOUBLEREEDS, BRASS, SAXOPHONES, BASSOONS, CELLOS, CLARINETS, PIANO
}
object Ensembles {
    fun getEnsemble(nParts: Int, type: EnsembleType): List<EnsemblePart> {
        return when (type) {
            EnsembleType.STRINGS -> getStrings(nParts)
            EnsembleType.WOODWINDS -> getWoodwinds(nParts)
            else -> listOf()
        }
    }
    fun getStrings(nParts: Int): List<EnsemblePart>{
        return when (nParts) {
            2 -> listOf<EnsemblePart>(
                EnsemblePart(40, 5), //violin
                EnsemblePart(42, 3)  // cello
            )
            3 -> listOf<EnsemblePart>(
                EnsemblePart(40, 5), //violin
                EnsemblePart(41, 4), //viola
                EnsemblePart(42, 3)  // cello
            )
            4 -> listOf<EnsemblePart>(
                EnsemblePart(40, 5), //violin
                EnsemblePart(40, 4), //violin
                EnsemblePart(41, 3), //viola
                EnsemblePart(42, 2)  // cello
            )
            5 -> listOf<EnsemblePart>(
                EnsemblePart(40, 5), //violin
                EnsemblePart(40, 4), //violin
                EnsemblePart(41, 4), //viola
                EnsemblePart(41, 3), //viola
                EnsemblePart(42, 2)  // cello
            )
            6 -> listOf<EnsemblePart>(
                EnsemblePart(40, 5), //violin
                EnsemblePart(40, 5), //violin
                EnsemblePart(41, 4), //viola
                EnsemblePart(41, 3), //viola
                EnsemblePart(42, 3),  // cello
                EnsemblePart(42, 2)  // cello
            )
            7 -> listOf<EnsemblePart>(
                EnsemblePart(40, 5), //violin
                EnsemblePart(40, 5), //violin
                EnsemblePart(41, 4), //viola
                EnsemblePart(41, 3), //viola
                EnsemblePart(42, 3),  // cello
                EnsemblePart(42, 2),  // cello
                EnsemblePart(43, 1)  // double-bass
            )
            else -> listOf()
        }
    }
    fun getWoodwinds(nParts: Int): List<EnsemblePart>{
        return when (nParts) {
            2 -> listOf<EnsemblePart>(
                EnsemblePart(73, 5), //flute
                EnsemblePart(70, 3)  // bassoon
            )
            3 -> listOf<EnsemblePart>(
                EnsemblePart(73, 5), //flute
                EnsemblePart(71, 4), //clarinet
                EnsemblePart(70, 3)  // bassoon
            )
            4 -> listOf<EnsemblePart>(
                EnsemblePart(73, 5), //flute
                EnsemblePart(60, 4), //oboe
                EnsemblePart(71, 3), //clarinet
                EnsemblePart(70, 2)  // bassoon
            )
            else -> listOf()
        }
    }
}

data class EnsemblePart( val instrument: Int, val octave: Int)
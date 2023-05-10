package com.cristianovecchi.mikrokanon.AIMUSIC

import com.cristianovecchi.mikrokanon.locale.NoteNamesIt

enum class MelodyGenre {
    GREGORIAN, BEBOP //CHORALE, DODECAPHONY, SCALES, COUPLES, ALBERTI, TERZINATO
}
enum class MelodyQuotes(val genre: MelodyGenre, val pitches: List<Int>) {
    TEST_1(MelodyGenre.GREGORIAN, listOf(60,62,60)),
    TEST_2(MelodyGenre.GREGORIAN, listOf(68,67,65)),

    CP_1(MelodyGenre.BEBOP, listOf(62,57,60,57, 59,68,65,66, 67)),
    CP_2(MelodyGenre.BEBOP, listOf(65,63, 62,65,69, 72,70, 69,70, 74,77)),
    CP_3(MelodyGenre.BEBOP, listOf(66,67,70,74, 77,74,75,76, 72,73,74,72, 70,69,67,65)),
    CP_4(MelodyGenre.BEBOP, listOf(60,64,67, 66,63, 59,56, 54,55,58,62, 65,62, 64,69, 69,69, 65,60)),
    BE_1(MelodyGenre.BEBOP, listOf(66,70, 73,77,80, 78,73,69, 68,66)),
    BE_2(MelodyGenre.BEBOP, listOf(67,72, 75,75, 79,79, 86,86, 86,86, 84,79,75, 72,77, 77,75)),
    BE_3(MelodyGenre.BEBOP, listOf(73,73, 70,65, 61,58, -1,75, 75,75, 72,68, 64,60, -1,79, 79,79, 75,72, 67,63)),
    BE_4(MelodyGenre.BEBOP, listOf(62,59, 60,64,67, 71,74, 71,70, 66,63, 60)),
    BE_5(MelodyGenre.BEBOP, listOf(72,71, 70,67, 63,60, 61,62, 65,64, 63,63, -1,63, 60)),
    BE_6(MelodyGenre.BEBOP, listOf(77, 73,70,66, 76,72, 76,72, 75,72,68,65)),
    CA_1(MelodyGenre.BEBOP, listOf(67, 70,74, 69,70, 74,77, 81,79, 80,78, 73,70, 75,73, 68,70, 72)),
    CA_2(MelodyGenre.BEBOP, listOf(79, 80,82, 84,80, 77,79, 80,77, 74,75, 77,74, 71,72, 74,71, 68,67)),
    CA_3(MelodyGenre.BEBOP, listOf(76,77, 76,74, 73,69, 76,75, 74,71, 67,65, 64,67, 71,74, 73,71, 72,74, 76,79)),
    CA_4(MelodyGenre.BEBOP, listOf(79,80, 79,77, 76,76, 74,74, 73,73, 73,71, 70,70, 68,70,68, 67)),
    CA_5(MelodyGenre.BEBOP, listOf(55, 56,58, 60,62, 64,65, 67,66, 65,67, 68,70, 72,72, 68)),
    CA_6(MelodyGenre.BEBOP, listOf(67,66, 67,69, 70,69, 70,72, 73,70, -1,69, -1,67, -1,64, 65,64, 65,67, 68,67, 68,70, 72,68, -1,67, -1,65)),
    CA_7(MelodyGenre.BEBOP, listOf(80,79, 78,82, 77,76, 75,70, 72,73, 70,66, 65,63)),
    CA_8(MelodyGenre.BEBOP, listOf(72,69, 71,67, 69,65, 64,62, 67,65, 60,57, 55,56, 67,65, 64)),
    AN_1(MelodyGenre.BEBOP, listOf(75, 76,79, 78,76, 77,74,70, 69,68,67,66, 65)),
    AN_2(MelodyGenre.BEBOP, listOf(62, 65,62,65, 69,65,69, 72,69,72, 75,77,75, 74,72, 77,75,75,75)),
    AN_3(MelodyGenre.BEBOP, listOf(67,65, 64,65, 69,72, 76, -1, 76,74, 74,74)),
    AN_4(MelodyGenre.BEBOP, listOf(62,61, 62,64, 65,72, 69,65,62, 67,66, 65, 69, 64,62, 62,62)),
    SC_1(MelodyGenre.BEBOP, listOf(70,72,70, 69,68, 67,64, 61,58)),
    HM_1(MelodyGenre.BEBOP, listOf(72,72, 68,65, 68,68, 65,68,72, 70,71, 66,64, 59,59, 61,63)),
    HM_2(MelodyGenre.BEBOP, listOf(79,78,77, 76,73, 72,70, 68,66, 65)),
    HM_3(MelodyGenre.BEBOP, listOf(68,65, 66,70,73, 77,75, 80,78, 77)),
    HM_4(MelodyGenre.BEBOP, listOf(75, 80,75, 79,82, 80,75, 74,77, 75,72, 71,73, 72,68, 67,70, 68)),
    HM_5(MelodyGenre.BEBOP, listOf(58, 61,65,68, 72,73, 69,72, 70,65, 68,70,68, 67,65, 63,61, 60)),
    HM_6(MelodyGenre.BEBOP, listOf(73,72, 66,67, 70,70, 70,70, 70,69, 68,66, 65)),
    HM_7(MelodyGenre.BEBOP, listOf(66,68, 69,67, 68,66, 65,63, 61)),
    HM_8(MelodyGenre.BEBOP, listOf(60, 61,65,68, 72,65, -1,72, 71,71, 64,64)),
    ;
    companion object {
        fun getAbsPitchesByGenres(genres: List<MelodyGenre>): List<List<Int>> {
            val noteNames = NoteNamesIt.values().map{it.toString()}
            return values().filter { genres.contains(it.genre) }
                .map { quote ->
                    Insieme.fromMidiPitchesToAbsPitches(quote.pitches.toIntArray()).toList()
                        .also{
                             println(quote.genre.toString() + " " + quote.name + " " + Clip.convertAbsPitchesToClipText(it, noteNames))
                        }
                }
//                .also {
//                    val noteNames = NoteNamesIt.values().map{it.toString()}
//                    it.forEach {
//                        println("Melody: " + Clip.convertAbsPitchesToClipText(it, noteNames))
//                    }
//                }
        }
    }
}


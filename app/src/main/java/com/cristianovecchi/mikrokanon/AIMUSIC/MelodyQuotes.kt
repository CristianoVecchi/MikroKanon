package com.cristianovecchi.mikrokanon.AIMUSIC

import com.cristianovecchi.mikrokanon.locale.NoteNamesIt

enum class MelodyGenre {
    BEBOP, VARIOUS
}
enum class MelodyQuotes(val genre: MelodyGenre, val pitches: List<Int>) {
    CP_1(MelodyGenre.BEBOP, listOf(62,57,60,57, 59,68,65,66, 67)),
    CP_2(MelodyGenre.BEBOP, listOf(65,63, 62,65,69, 72,70, 69,70, 74,77)),
    CP_3(MelodyGenre.BEBOP, listOf(66,67,70,74, 77,74,75,76, 72,73,74,72, 70,69,67,65)),
    BE_1(MelodyGenre.BEBOP, listOf(66,70, 73,77,80, 78,73,69, 68,66))
    ;
    companion object {
        fun getAbsPitchesByGenres(genres: List<MelodyGenre>): List<List<Int>> {
            return values().filter { genres.contains(it.genre) }
                .map { Insieme.fromMidiPitchesToAbsPitches(it.pitches.toIntArray()).toList() }
                .also {
                    val noteNames = NoteNamesIt.values().map{it.toString()}
                    it.forEach {
                        println("Melody: " + Clip.convertAbsPitchesToClipText(it, noteNames))
                    }
                }
        }
    }
}


package com.cristianovecchi.mikrokanon.AIMUSIC

import com.cristianovecchi.mikrokanon.locale.NoteNamesIt

enum class MelodyGenre {
    GREGORIAN, BEBOP, COUPLE, TRIPLE, SCALES//, QUADRUPLE //CHORALE, DODECAPHONY, SCALES,  ALBERTI,
}
enum class MelodyQuotes(val genre: MelodyGenre, val pitches: List<Int>) {
    VENI_1(MelodyGenre.GREGORIAN, listOf(65,67,65,63,65,67,65,70,72,70)),
    VENI_2(MelodyGenre.GREGORIAN, listOf(70,65,67,70,72,70,72,74,72)),
    VENI_3(MelodyGenre.GREGORIAN, listOf(70,72,74,70,69,67,65,70,72,65,67,70)),
    VENI_4(MelodyGenre.GREGORIAN, listOf(69,70,67,65,63,67,67,69,67,65,63,65,-1,65,67,65,63,65)),
    VICTIMAE_1(MelodyGenre.GREGORIAN, listOf(50,48,50,53,55,53,52,50,57,55,52,55,53,52,50)),
    VICTIMAE_2(MelodyGenre.GREGORIAN, listOf(57,60,62,57,55,57,57,57,55,57,55,53,52,50,53,55,50,52,50,48,52,53,52,50)),
    VICTIMAE_3(MelodyGenre.GREGORIAN, listOf(45,48,50,53,55,52,50,48,53,52,50,52,48,50)),
    VICTIMAE_4(MelodyGenre.GREGORIAN, listOf(53,57,55,57,53,55,53,52,50,50,55,53,55,57,55,53,55,53,52,50)),
    VICTIMAE_5(MelodyGenre.GREGORIAN, listOf(57,60,62,57,57,55,57,57,57,60,55,53,52,50,48,53,52,55,57,57,53,55,53,52,50,-1,50,52,50,48,50,48,53,52,50,50)),
    A_D_V_1(MelodyGenre.GREGORIAN, listOf(62,62,65,62,60,65,67,65,69,69,-1,69,69,72,69,67,69,67,67,65,67,69,70,69,67,65,65)),
    A_D_V_2(MelodyGenre.GREGORIAN, listOf(67,67,69,67,65,64,62,60,62,65,64,62,62,-1,69,69,67,65,67,69,67,65)),
    D_A_P_1(MelodyGenre.GREGORIAN, listOf(67,72,69,69,67,69,67,65,67,69,69,67,67,-1,74,71,74,76,74,74,74,76,74,72,74,72,72,67)),
    D_A_P_2(MelodyGenre.GREGORIAN, listOf(69,72,72,71,69,72,67,-1,71,74,72,74,72,71,69,71,72,74,71,71)),
    D_A_P_3(MelodyGenre.GREGORIAN, listOf(71,71,71,72,74,74,74,67,69,72,69,71,69,67,67,-1,72,72,71,72,69,67)),
    E_S_1(MelodyGenre.GREGORIAN, listOf(67,67,67,65,62,64,65,67,69,69,67,67,69,67,67,69,71,67,69,67,65)),
    E_S_2(MelodyGenre.GREGORIAN, listOf(67,67,67,67,69,67,65,67,69,72,72,71,67,71,72,69,71,67,67,-1,72,72,71,72,69,67)),
    F_M_M_1(MelodyGenre.GREGORIAN, listOf(65,67,67,65,69,72,72,71,67,67,64,65,67,67,65,64,62)),
    F_M_M_2(MelodyGenre.GREGORIAN, listOf(62,64,65,67,65,69,71,69,67,67,65,64,65,67,67,67,-1,72,72,71,72,69,67)),
    G_I_E_D_1(MelodyGenre.GREGORIAN, listOf(65,67,69,67,67,65,67,69,67,69,67,69,65,67,69,67,65,67,67,69,71,72,69,67)),
    G_I_E_D_2(MelodyGenre.GREGORIAN, listOf(69,67,65,67,69,67,65,-1,67,69,65,62,64,65,67,69,69,67,67)),

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

    MIN2(MelodyGenre.COUPLE, listOf(60, 61)),
    MAJ2(MelodyGenre.COUPLE, listOf(60, 62)),
    MIN3(MelodyGenre.COUPLE, listOf(60, 63)),
    MAJ3(MelodyGenre.COUPLE, listOf(60, 64)),
    FOURTH(MelodyGenre.COUPLE, listOf(60, 65)),
    AUG_FOURTH(MelodyGenre.COUPLE, listOf(60, 66)),

    MIN2_MIN2(MelodyGenre.TRIPLE, listOf(60, 61, 62)),
    MIN2_MAJ2(MelodyGenre.TRIPLE, listOf(60, 61, 63)),
    MIN2_MIN3(MelodyGenre.TRIPLE, listOf(60, 61, 64)),
    MIN2_MAJ3(MelodyGenre.TRIPLE, listOf(60, 61, 65)),
    MIN2_FOURTH(MelodyGenre.TRIPLE, listOf(60, 61, 66)),
    MIN2_AUG_FOURTH(MelodyGenre.TRIPLE, listOf(60, 61, 67)),
    MAJ2_MAJ2(MelodyGenre.TRIPLE, listOf(60, 62, 64)),
    MAJ2_MIN3(MelodyGenre.TRIPLE, listOf(60, 62, 65)),
    MAJ2_MAJ3(MelodyGenre.TRIPLE, listOf(60, 62, 66)),
    MAJ2_FOURTH(MelodyGenre.TRIPLE, listOf(60, 62, 67)),
    MAJ2_AUG_FOURTH(MelodyGenre.TRIPLE, listOf(60, 62, 68)),
    MIN3_MIN3(MelodyGenre.TRIPLE, listOf(60, 63, 66)),
    MIN3_MAJ3(MelodyGenre.TRIPLE, listOf(60, 63, 67)),
    MIN3_FOURTH(MelodyGenre.TRIPLE, listOf(60, 63, 68)),
    MIN3_AUG_FOURTH(MelodyGenre.TRIPLE, listOf(60, 63, 69)),
    MAJ3_MAJ3(MelodyGenre.TRIPLE, listOf(60, 64, 68)),
    MAJ3_FOURTH(MelodyGenre.TRIPLE, listOf(60, 64, 69)),
    MAJ3_AUG_FOURTH(MelodyGenre.TRIPLE, listOf(60, 64, 70)),
    FOURTH_FOURTH(MelodyGenre.TRIPLE, listOf(60, 65, 70)),
    FOURTH_AUG_FOURTH(MelodyGenre.TRIPLE, listOf(60, 65, 71)),

    LYDIAN_DOM(MelodyGenre.SCALES, listOf(60,62,64,66,67,69,70)),
    AEOLIAN(MelodyGenre.SCALES, listOf(60,62,63,65,67,68,70)),
    ALGERIAN(MelodyGenre.SCALES, listOf(60,62,63,66,67,68,71,72,74,75,77)),
    ALTERED(MelodyGenre.SCALES, listOf(60,61,63,64,66,68,70)),
    AUGMENTED(MelodyGenre.SCALES, listOf(60,63,64,67,68,71)),
    BEBOP(MelodyGenre.SCALES, listOf(60,62,64,65,67,68,69,71)),
    BEBOP_DOM(MelodyGenre.SCALES, listOf(60,62,64,65,67,69,70,71)),
    BLUES(MelodyGenre.SCALES, listOf(60,63,65,66,67,70)),
    CHROMATIC(MelodyGenre.SCALES, listOf(60,61,62,63,64,65,66,67,68,69,70,71)),
    DORIAN(MelodyGenre.SCALES, listOf(60,62,63,65,67,69,70)),
    DOUBLE_HARMONIC(MelodyGenre.SCALES, listOf(60,61,64,65,67,68,71)),
    ENIGMATIC(MelodyGenre.SCALES, listOf(60,61,64,66,68,70,71)),
    GYPSY(MelodyGenre.SCALES, listOf(60,62,63,66,67,68,70)),
    HALF_DIM(MelodyGenre.SCALES, listOf(60,62,63,65,66,68,70)),
    HARMONIC_MAJ(MelodyGenre.SCALES, listOf(60,62,64,65,67,68,71)),
    HARMONIC_MIN(MelodyGenre.SCALES, listOf(60,62,63,65,67,68,71)),
    HIRAJOSHI(MelodyGenre.SCALES, listOf(60,64,66,67,71)),
    HUNGARIAN_MIN(MelodyGenre.SCALES, listOf(60,62,63,66,67,68,71)),
    HUNGARIAN_MAJ(MelodyGenre.SCALES, listOf(60,63,64,66,67,69,70)),
    IN(MelodyGenre.SCALES, listOf(62,63,67,69,70)),
    INSEN(MelodyGenre.SCALES, listOf(60,61,65,67,70)),
    IONIAN(MelodyGenre.SCALES, listOf(60,62,64,65,67,69,71)),
    ISTRIAN(MelodyGenre.SCALES, listOf(60,61,63,64,66,67)),
    IWATO(MelodyGenre.SCALES, listOf(60,61,65,66,70)),
    LOCRIAN(MelodyGenre.SCALES, listOf(60,61,63,65,66,68,70)),
    LYDIAN_AUG(MelodyGenre.SCALES, listOf(60,62,64,66,68,69,71)),
    LYDIAN_DIM(MelodyGenre.SCALES, listOf(60,62,63,66,67,69,71)),
    LYDIAN(MelodyGenre.SCALES, listOf(60,62,64,66,67,69,71)),
    LOCRIAN_MAJ(MelodyGenre.SCALES, listOf(60,62,64,65,66,68,70)),
    PENTATONIC_MAJ(MelodyGenre.SCALES, listOf(60,62,64,67,69)),
    PENTATONIC_MIN(MelodyGenre.SCALES, listOf(60,63,65,67,10)),
    MELODIC_MIN_ASC(MelodyGenre.SCALES, listOf(60,62,63,65,67,69,71)),
    MIXOLYDIAN(MelodyGenre.SCALES, listOf(60,62,64,65,67,69,70)),
    NEAPOLITAN_MAJ(MelodyGenre.SCALES, listOf(60,61,63,65,67,69,71)),
    NEAPOLITAN_MIN(MelodyGenre.SCALES, listOf(60,61,63,65,67,68,71)),
    OCTATONIC_TH(MelodyGenre.SCALES, listOf(60,62,63,65,66,68,69,71)),
    OCTATONIC_HT(MelodyGenre.SCALES, listOf(60,61,63,64,66,67,69,70)),
    PELOG(MelodyGenre.SCALES, listOf(62,63,65,68,69,70,72)),
    PERSIAN(MelodyGenre.SCALES, listOf(60,61,64,65,66,68,71)),
    PHRYGIAN_DOM(MelodyGenre.SCALES, listOf(60,61,64,65,67,68,70)),
    PHRYGIAN(MelodyGenre.SCALES, listOf(60,61,63,65,67,68,70)),
    PROMETHEUS(MelodyGenre.SCALES, listOf(60,62,64,66,69,70)),
    HARMONICS(MelodyGenre.SCALES, listOf(60,63,64,65,67,69)),
    HARMONICS_VIETNAMESE(MelodyGenre.SCALES, listOf(60,63,64,65,67)),
    SLENDRO(MelodyGenre.SCALES, listOf(60,62,65,67,69)),
    TRITONE(MelodyGenre.SCALES, listOf(60,61,64,66,67,70)),
    TWO_SEMITONE(MelodyGenre.SCALES, listOf(60,61,62,66,67,68)),
    UKRAINIAN_DORIAN(MelodyGenre.SCALES, listOf(60,62,63,66,67,69,70)),
    WHOLE_TONE(MelodyGenre.SCALES, listOf(60,62,64,66,68,70)),

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


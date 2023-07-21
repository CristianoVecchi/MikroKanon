package com.cristianovecchi.mikrokanon.AIMUSIC

import com.cristianovecchi.mikrokanon.locale.NoteNamesIt

enum class MelodyGenre {
    GREGORIAN, DODECAPHONY, BEBOP, COUPLE, TRIPLE, SCALES//, QUADRUPLE //CHORALE, SCALES,  ALBERTI,
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

    AW_32LATER(MelodyGenre.DODECAPHONY, listOf(0,1,2,11,10,9, 5,4,3,6,7,8)),
    IS_ABR_IS(MelodyGenre.DODECAPHONY, listOf(0,1,3,5,6,2, 4,8,7,9,11,10)),
    AS_SER24_M5(MelodyGenre.DODECAPHONY, listOf(0,1,3,6,7,9, 8,10,11,2,4,5)),
    AB_KAMMKONZ(MelodyGenre.DODECAPHONY, listOf(0,1,3,8,4,9, 10,7,6,5,11,2)),
    AS_SUITE25(MelodyGenre.DODECAPHONY, listOf(0,1,3,9,2,11, 4,10,7,8,5,6)),
    AW_VAR30(MelodyGenre.DODECAPHONY, listOf(0,1,4,3,2,5, 6,9,8,7,10,11)),
    IS_AGON_PDD_4T(MelodyGenre.DODECAPHONY, listOf(0,1,4,3,2,5, 6,9,8,11,10,7)),
    AS_JAKOB(MelodyGenre.DODECAPHONY, listOf(0,1,4,3,7,6, 11,2,10,9,5,8)),
    EK_JER93(MelodyGenre.DODECAPHONY, listOf(0,1,4,6,8,10, 5,7,9,11,2,3)),
    MB_COMP12(MelodyGenre.DODECAPHONY, listOf(0,1,4,9,5,8, 3,10,2,11,6,7)),
    LD_QUAD(MelodyGenre.DODECAPHONY, listOf(0,1,5,8,10,4, 3,7,9,2,11,6)),
    AS_VLK36(MelodyGenre.DODECAPHONY, listOf(0,1,6,2,7,9, 3,4,10,11,5,8)),
    AS_WARSAW(MelodyGenre.DODECAPHONY, listOf(0,1,6,2,10,9, 4,7,3,8,11,5)),
    MB_ALL_SET(MelodyGenre.DODECAPHONY, listOf(0,1,6,5,7,11, 2,10,3,9,8,4)),
    AS_MOSES(MelodyGenre.DODECAPHONY, listOf(0,1,7,5,6,4, 10,8,9,11,2,3)),
    IS_MOV(MelodyGenre.DODECAPHONY, listOf(0,1,7,5,6,11, 9,8,10,3,4,2)),
    KS_ZEIT(MelodyGenre.DODECAPHONY, listOf(0,1,8,11,7,3, 2,5,4,10,9,6)),
    AW_32ROW1(MelodyGenre.DODECAPHONY, listOf(0,1,9,11,3,2, 6,7,10,8,4,5)),
    AW_PIANO27(MelodyGenre.DODECAPHONY, listOf(0,1,9,11,8,10, 4,5,6,2,3,7)),
    LD_DIAL(MelodyGenre.DODECAPHONY, listOf(0,1,10,2,6,4, 5,3,7,11,8,9)),
    LN_CANTO(MelodyGenre.DODECAPHONY, listOf(0,1,11,2,10,3, 9,4,8,5,7,6)),
    IS_AGON_CODA_FLOOD(MelodyGenre.DODECAPHONY, listOf(0,2,1,3,4,6, 7,9,8,10,5,11)),
    AB_LULU(MelodyGenre.DODECAPHONY, listOf(0,2,3,1,4,5, 6,7,8,9,11,10)),
    IS_AGON_BRANSLES(MelodyGenre.DODECAPHONY, listOf(0,2,3,5,4,9, 7,8,10,11,1,6)),
    AB_LULU2(MelodyGenre.DODECAPHONY, listOf(0,2,4,6,10,8, 11,7,9,1,3,5)),
    IS_VAR(MelodyGenre.DODECAPHONY, listOf(0,2,5,3,10,4, 6,1,11,7,8,9)),
    IS_OWL(MelodyGenre.DODECAPHONY, listOf(0,2,9,11,8,6, 5,7,10,1,4,3)),
    IS_REQ1(MelodyGenre.DODECAPHONY, listOf(0,2,10,11,1,8, 6,7,9,4,3,5)),
    MB_W_QUIN(MelodyGenre.DODECAPHONY, listOf(0,3,1,2,10,11, 8,9,5,7,6,4)),
    KS_ARIES(MelodyGenre.DODECAPHONY, listOf(0,3,2,9,7,8, 11,6,1,5,4,10)),
    AW_SYM21(MelodyGenre.DODECAPHONY, listOf(0,3,2,1,5,4, 10,11,7,8,9,6)),
    KS_KLS_IV_2ND(MelodyGenre.DODECAPHONY, listOf(0,3,5,1,2,6, 4,11,7,8,10,9)),
    KS_AQUARIUS(MelodyGenre.DODECAPHONY, listOf(0,3,5,4,10,11, 9,8,7,6,2,1)),
    AB_VL_KNZ(MelodyGenre.DODECAPHONY, listOf(0,3,7,11,2,5, 9,1,4,6,8,10)),
    AW_31(MelodyGenre.DODECAPHONY, listOf(0,3,11,10,2,9, 1,5,4,8,7,6)),
    IS_ANTHEM(MelodyGenre.DODECAPHONY, listOf(0,4,2,5,7,8, 10,9,11,1,6,3)),
    KS_LICHT_EVE(MelodyGenre.DODECAPHONY, listOf(0,4,3,2,5,6,10,11,9,1,7,8)),
    AS_ODE29(MelodyGenre.DODECAPHONY, listOf(0,4,3,7,11,8, 9,6,5,1,2,10)),
    KS_SCORPIO(MelodyGenre.DODECAPHONY, listOf(0,4,3,11,7,8, 10,9,2,5,1,6)),
    AB_LULU3(MelodyGenre.DODECAPHONY, listOf(0,4,5,2,6,7, 8,9,11,10,3,1)),
    AB_LULU_BASIC(MelodyGenre.DODECAPHONY, listOf(0,4,5,2,7,9, 6,8,11,10,3,1)),
    KS_GEMINI(MelodyGenre.DODECAPHONY, listOf(0,4,5,10,7,6, 1,8,11,2,9,3)),
    BB_TURN(MelodyGenre.DODECAPHONY, listOf(0,5,2,7,4,9, 6,11,8,1,10,3)),
    DS_SYM14_M3(MelodyGenre.DODECAPHONY, listOf(0,5,2,7,10,1, 11,4,6,8,9,3)),
    IS_THRENI(MelodyGenre.DODECAPHONY, listOf(0,5,4,7,10,6, 11,8,1,9,2,3)),
    KS_LEO(MelodyGenre.DODECAPHONY, listOf(0,5,11,6,3,2, 8,7,9,10,4,1)),
    KS_SAG(MelodyGenre.DODECAPHONY, listOf(0,6,5,2,3,4, 7,8,9,10,11,1)),
    WL_FUN(MelodyGenre.DODECAPHONY, listOf(0,6,5,11,10,4, 3,9,8,2,1,7)),
    WAM_K428(MelodyGenre.DODECAPHONY, listOf(0,6,7,8,9,1, 2,5,11,4,10,3)),
    AS_VAR31(MelodyGenre.DODECAPHONY, listOf(0,6,8,5,7,11, 4,3,9,10,1,2)),
    KS_KLS_IV_3RD(MelodyGenre.DODECAPHONY, listOf(0,7,1,10,11,9, 5,8,4,3,2,6)),
    EC_P_CON(MelodyGenre.DODECAPHONY, listOf(0,7,4,11,6,10, 3,9,1,8,5,2)),
    IS_REQ_2ND(MelodyGenre.DODECAPHONY, listOf(0,7,6,4,5,9, 8,10,3,1,11,2)),
    AS_P_CON42(MelodyGenre.DODECAPHONY, listOf(0,7,11,2,1,9,3,5,10,6,8,4)),
    AW_23(MelodyGenre.DODECAPHONY, listOf(0,7,11,8,2,10, 6,9,5,4,1,3)),
    KS_CANCER(MelodyGenre.DODECAPHONY, listOf(0,7,11,10,9,8, 3,6,4,1,5,2)),
    KS_GR_KLS_VII_IX_X(MelodyGenre.DODECAPHONY, listOf(0,8,1,10,9,11, 5,3,4,7,2,6)),
    KS_KLS_V_VIII(MelodyGenre.DODECAPHONY, listOf(0,8,1,11,10,7, 5,9,4,6,3,2)),
    EC_P_CON_SOLO(MelodyGenre.DODECAPHONY, listOf(0,8,4,9,10,3, 1,6,11,2,7,5)),
    AW_VAR27_M3(MelodyGenre.DODECAPHONY, listOf(0,8,7,11,10,9, 3,1,4,2,6,5)),
    AW_29(MelodyGenre.DODECAPHONY, listOf(0,8,11,10,2,1, 4,3,7,6,9,5)),
    LD_PMN(MelodyGenre.DODECAPHONY, listOf(0,9,1,3,4,11, 2,8,7,5,10,6)),
    KS_CAPRICORN(MelodyGenre.DODECAPHONY, listOf(0,9,2,8,7,5, 6,1,3,4,10,11)),
    AW_22(MelodyGenre.DODECAPHONY, listOf(0,9,8,11,10,2, 3,4,5,7,1,6)),
    PB_MAITRE(MelodyGenre.DODECAPHONY, listOf(0,10,1,2,5,4, 6,3,7,11,8,9)),
    KS_KS(MelodyGenre.DODECAPHONY, listOf(0,10,9,11,7,2, 8,1,4,6,5,3)),
    IS_AGON_DPQ(MelodyGenre.DODECAPHONY, listOf(0,11,1,2,9,8, 10,7,5,6,4,3)),
    IS_FANFARE(MelodyGenre.DODECAPHONY, listOf(0,11,1,3,4,2, 5,7,6,8,10,9)),
    IS_CS_II_IV(MelodyGenre.DODECAPHONY, listOf(0,11,1,3,4,2, 7,6,9,5,8,10)),
    AW_SQ28(MelodyGenre.DODECAPHONY, listOf(0,11,2,1,5,6, 3,4,8,7,10,9)),
    EC_SQ3(MelodyGenre.DODECAPHONY, listOf(0,11,2,9,5,3, 4,8,10,7,1,6)),
    AS_VPH47(MelodyGenre.DODECAPHONY, listOf(0,11,3,1,7,9, 6,2,10,5,8,4)),
    AW_C9I24(MelodyGenre.DODECAPHONY, listOf(0,11,3,4,8,7, 9,5,6,1,2,10)),
    //LD_5C(MelodyGenre.DODECAPHONY, listOf(0,11,5,9,6,2, 7,3,1,4,10,9)),
    PB_STRUCT_IA(MelodyGenre.DODECAPHONY, listOf(0,11,6,5,4,3, 1,10,9,7,2,8)),
    AB_LYR_M3(MelodyGenre.DODECAPHONY, listOf(0,11,7,1,2,9, 3,8,10,4,5,6)),
    AB_LYR_M4_R1(MelodyGenre.DODECAPHONY, listOf(0,11,7,1,4,8, 3,9,10,2,5,6)),
    AB_LYR_M1(MelodyGenre.DODECAPHONY, listOf(0,11,7,4,2,9, 3,8,10,1,5,6)),
    AS_SQ4(MelodyGenre.DODECAPHONY, listOf(0,11,7,8,3,1, 2,10,6,5,4,9)),
    AB_LYR_R2(MelodyGenre.DODECAPHONY, listOf(0,11,8,9,7,6, 1,5,4,3,2,10)),
    DS_SYM14_M1(MelodyGenre.DODECAPHONY, listOf(0,11,9,4,3,10, 8,7,5,6,1,2)),
    IS_CS_II(MelodyGenre.DODECAPHONY, listOf(0,11,9,6,10,8, 7,5,2,4,3,1)),



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
        fun checkDodecaphonicIntegrity() {
            val twelveNotes = (0..11).toList()
            val wrongOnes = values().filter { it.genre == MelodyGenre.DODECAPHONY && it.pitches.sorted() != twelveNotes }
            if(wrongOnes.isEmpty()){
                println("All series are dodecaphonic ones!")
            } else {
                wrongOnes.forEach {
                    println("WARNING!!! ${it.name} is not a dodecaphonic series!!! ")
                }
            }
        }
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


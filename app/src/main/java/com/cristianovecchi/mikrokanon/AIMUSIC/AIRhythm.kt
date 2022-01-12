package com.cristianovecchi.mikrokanon.AIMUSIC

import kotlin.math.absoluteValue

object AIRhythm {
    @JvmStatic
    fun findOffBeats(nBeats: Int): IntArray {
        if (nBeats == 1) return intArrayOf(1)
        val nOffBeats = nBeats / 2
        val offBeats = IntArray(nOffBeats)
        for (i in 0 until nOffBeats) {
            offBeats[i] = i * 2 + 1
        }
        return offBeats
    }
}
val N10 = listOf(48,48,48,48,48,48,48,48,48,48)
val N10h = listOf(48,48,48,48,48)
val N9 = listOf(54,53,53,54,53,53,54,53,53)
val N8 = listOf(60,60,60,60,60,60,60,60)
val N8h = listOf(60,60,60,60)
val N7 = listOf(69,68,69,68,69,68,69)
val N6 = listOf(80,80,80,80,80,80)
val N6graziosetto = listOf(80,20,-60,20,-60,20,-60,20,-60,20,-60)
val N6rhythmDotted = listOf(60,20,60,20,60,20,60,20,60,20,60,20)
val N6h = listOf(80,80,80)
val N5 = listOf(96,96,96,96,96)
val N5graziosetto = listOf(96,24,-72,24,-72,24,-72,24,-72)
val N5rhythmDotted = listOf(72,24,72,24,72,24,72,24,72,24)
val N4 = listOf(120,120,120,120)
val N4rhythmDotted = listOf(90,30,90,30,90,30,90,30)
val N4détaché = listOf(90,-30,90,-30,90,-30,90,-30)
val N4staccato = listOf(30,-90,30,-90,30,-90,30,-90)
val N4staccatissimo = listOf(15,-105,15,-105,15,-105,15,-105)
val N4graziosetto = listOf(120,30,-90,30,-90,30,-90)
val N4h = listOf(120,120)
val N3 = listOf(160,160,160)
val N3rhythmDotted = listOf(120,40,120,40,120,40)
val N3graziosetto = listOf(160,40,-120,40,-120)
val N2 = listOf(240,240)
val N2grazioso = listOf(240,60-180)
val N2rhythmDotted = listOf(180,60,180,60)
val N2détaché = listOf(180,-60,180,-60)
val N1 = listOf(480); val N1h = listOf(240)
val Qx3staccato = listOf(120,-360,120,-360,120,-360)// Q = Quaver
val Ox3staccato = listOf(60,-180,60,-180,60,-180)// O = Octave
val N1dotted = listOf(720); val N1dottedH = listOf(360) // Octave x 3
val N1rhythmDotted = listOf(360,120)
val Ox4grz = listOf(240,60,-180,60,-180,60,-180); val Ox4grzH = listOf(120,30,-90,30,-90,30,-90)// Octave x 4 graziosetto
val Ox3grz = listOf(240,60,-180,60,-180); val Ox3grzH = listOf(120,30,-90,30,-90)// Octave x 3 graziosetto
val Ox2grz = listOf(240,60,-180); val Ox2grzH = listOf(120,30,-90)// Octave x 2 graziosetto
val H1 = listOf(960); val H1h = listOf(480) // H = 2/4
val METRO_2_4 = Pair(2,4)
val METRO_3_4 = Pair(3,4)
val METRO_4_4 = Pair(4,4)
enum class RhythmType{
     BASIC, PLAIN, BALLET, PUNTATO, DANCE, RAGTIME, QUOTE, BULGARIAN, HEMIOLIA, FLUX
}
// WARNING: can't have two negative values coupled (ex: -80, -20 ... write -100)
enum class RhythmPatterns(val type: RhythmType, val title: String, val values: List<Int>,val metro: Pair<Int,Int> = METRO_4_4) {
    BASIC_4(RhythmType.BASIC,"Basic ♩", listOf(480), METRO_2_4),
    BASIC_8(RhythmType.BASIC,"Basic ♪", listOf(240), Pair(2,8)),
    BASIC_16(RhythmType.BASIC,"Basic 16", listOf(120), Pair(2,16)),
    BASIC_32(RhythmType.BASIC,"Basic 32", listOf(60), Pair(2,32)),
    PLAIN_2_4_R4(RhythmType.PLAIN,"Plain 2/4♩♩", listOf(480,480), METRO_2_4),
    PLAIN_3_4_R4(RhythmType.PLAIN,"Plain 3/4♩♩♩", listOf(480,480,480), METRO_3_4),
    PLAIN_4_4_R4(RhythmType.PLAIN,"Plain 4/4♩♩♩♩", listOf(480,480,480,480)),
    PLAIN_2_4_R8(RhythmType.PLAIN,"Plain 2/4♫♫", listOf(240,240,240,240), METRO_2_4),
    PLAIN_3_4_R8(RhythmType.PLAIN,"Plain 3/4♫♫♫", listOf(240,240,240,240,240,240), METRO_3_4),
    PLAIN_4_4_R8(RhythmType.PLAIN,"Plain 4/4♫♫♫♫", listOf(240,240,240,240,240,240,240,240)),
    PLAIN_1_4_R8T(RhythmType.PLAIN,"Plain 1/4 ♪t", listOf(160,160,160), Pair(1,4)),
    PLAIN_2_4_R8T(RhythmType.PLAIN,"Plain 2/4 ♪t", listOf(160,160,160,160,160,160), METRO_2_4),
    PLAIN_3_4_R8T(RhythmType.PLAIN,"Plain 3/4 ♪t", listOf(160,160,160,160,160,160,160,160,160), METRO_3_4),
    PLAIN_4_4_R8T(RhythmType.PLAIN,"Plain 4/4 ♪t", listOf(160,160,160,160,160,160,160,160,160,160,160,160)),
    PLAIN_2_4_R16(RhythmType.PLAIN,"Plain 2/4 16", listOf(120,120,120,120,120,120,120,120)),
    PLAIN_3_4_R16(RhythmType.PLAIN,"Plain 3/4 16", listOf(120,120,120,120,120,120,120,120,120,120,120,120)),
    PLAIN_4_4_R16(RhythmType.PLAIN,"Plain 4/4 16", listOf(120,120,120,120,120,120,120,120,120,120,120,120,120,120,120,120)),
    PLAIN_2_4_R16T5(RhythmType.PLAIN,"Plain 2/4 16t(5)", listOf(96,96,96,96,96, 96,96,96,96,96), METRO_2_4),
    PLAIN_2_4_R16T6(RhythmType.PLAIN,"Plain 2/4 16t(6)", listOf(80,80,80,80,80,80, 80,80,80,80,80,80), METRO_2_4),
    PLAIN_2_4_R16T7(RhythmType.PLAIN,"Plain 2/4 16t(7)", listOf(N7, N7).flatten(), METRO_2_4),
    PLAIN_2_4_R32(RhythmType.PLAIN,"Plain 2/4 32", listOf(60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,), METRO_2_4),
    PLAIN_2_4_R32T9(RhythmType.PLAIN,"Plain 2/4 32t(9)", listOf(N9, N9).flatten(), METRO_2_4),
    PLAIN_2_8_R32T10(RhythmType.PLAIN,"Plain 2/8 32t(10)", N10, Pair(2,8)),
    PLAIN_3_8_R8(RhythmType.PLAIN,"Plain 3/8♪", listOf(240,240,240), Pair(3,8)),
    PLAIN_6_8_R8(RhythmType.PLAIN,"Plain 6/8♪", listOf(240,240,240,240,240,240), Pair(6,8)),
    PLAIN_9_8_R8(RhythmType.PLAIN,"Plain 9/8♪", listOf(240,240,240,240,240,240,240,240,240), Pair(9,8)),
    PLAIN_12_8_R8(RhythmType.PLAIN,"Plain 12/8♪", listOf(240,240,240,240,240,240,240,240,240,240,240,240), Pair(12,8)),
    PLAIN_1_4_R4T_8T(RhythmType.PLAIN,"Plain 1/4♩♪t", listOf(320,160), Pair(1,4)),
    PLAIN_2_4_R4T_8T(RhythmType.PLAIN,"Plain 2/4♩♪t", listOf(320,160,320,160), METRO_2_4),
    PLAIN_3_4_R4T_8T(RhythmType.PLAIN,"Plain 3/4♩♪t", listOf(320,160,320,160,320,160), METRO_3_4),
    PLAIN_4_4_R4T_8T(RhythmType.PLAIN,"Plain 4/4♩♪t", listOf(320,160,320,160,320,160,320,160), METRO_4_4),
    PLAIN_3_8_R4_8(RhythmType.PLAIN,"Plain 3/8♩♪", listOf(480,240), Pair(3,8)),
    PLAIN_6_8_R4_8(RhythmType.PLAIN,"Plain 6/8♩♪", listOf(480,240,480,240), Pair(6,8)),
    PLAIN_9_8_R4_8(RhythmType.PLAIN,"Plain 9/8♩♪", listOf(480,240,480,240,480,240), Pair(9,8)),
    PLAIN_12_8_R4_8(RhythmType.PLAIN,"Plain 12/8♩♪", listOf(480,240,480,240,480,240,480,240), Pair(12,8)),
    PLAIN_3_16_R8_16(RhythmType.PLAIN,"Plain 3/16♪16", listOf(240,120), Pair(3,16)),
    PLAIN_6_16_R8_16(RhythmType.PLAIN,"Plain 6/16♪16", listOf(240,120,240,120), Pair(6,16)),
    PLAIN_9_16_R8_16(RhythmType.PLAIN,"Plain 9/16♪16", listOf(240,120,240,120,240,120), Pair(9,16)),
    PLAIN_12_16_R8_16(RhythmType.PLAIN,"Plain 12/16♪16", listOf(240,120,240,120,240,120,240,120), Pair(12,16)),
    STACCATO_2_8_R8(RhythmType.BALLET,"Staccato 2/8♪",listOf(60,-180,60,-180), Pair(2,8)),
    STACCATO_3_8_R8(RhythmType.BALLET,"Staccato 3/8♪",listOf(60,-180,60,-180,60,-180), Pair(3,8)),
    STACCATO_2_4_R8(RhythmType.BALLET,"Staccato 2/4♪",listOf(60,-180,60,-180,60,-180,60,-180), METRO_2_4),
    STACCATO_2_4_R8T(RhythmType.BALLET,"Staccato 2/4♪t",listOf(40,-120,40,-120,40,-120,40,-120,40,-120,40,-120), METRO_2_4),
    STACCATO_2_4_R16(RhythmType.BALLET,"Staccato 2/4 16",listOf(30,-90,30,-90,30,-90,30,-90,30,-90,30,-90,30,-90,30,-90), METRO_2_4),
    GRAZIOSO_2_4_R8(RhythmType.BALLET,"Grazioso 2/4♪",listOf(240,60,-180,240,60,-180,240,60,-180,240,60,-180), METRO_2_4),
    GRAZIOSO_2_4_R8T(RhythmType.BALLET,"Grazioso 2/4♪",listOf(160,40,-120,160,40,-120,160,40,-120, 160,40,-120,160,40,-120,160,40,-120), METRO_2_4),
    GRAZIOSO_2_4_R16(RhythmType.BALLET,"Grazioso 2/4 16",listOf(120,30,-90,120,30,-90,120,30,-90,120,30,-90,120,30,-90,120,30,-90,120,30,-90,120,30,-90), METRO_2_4),
    GRAZIOSO_1_4_R4T_8T(RhythmType.BALLET,"Grazioso 1/4♩♪t", listOf(320,40,-120), Pair(1,4)),
    GRAZIOSO_2_4_R4T_8T(RhythmType.BALLET,"Grazioso 2/4♩♪t", listOf(320,40,-120,320,40,-120), METRO_2_4),
    GRAZIOSO_3_4_R4T_8T(RhythmType.BALLET,"Grazioso 3/4♩♪t", listOf(320,40,-120,320,40,-120,320,40,-120), METRO_3_4),
    GRAZIOSO_4_4_R4T_8T(RhythmType.BALLET,"Grazioso 4/4♩♪t", listOf(320,40,-120,320,40,-120,320,40,-120,320,40,-120), METRO_4_4),
    GRAZIOSO_3_8_R4_8(RhythmType.BALLET,"Grazioso 3/8♩♪", listOf(480,60,-180), Pair(3,8)),
    GRAZIOSO_6_8_R4_8(RhythmType.BALLET,"Grazioso 6/8♩♪", listOf(480,60,-180,480,60,-180), Pair(6,8)),
    GRAZIOSO_9_8_R4_8(RhythmType.BALLET,"Grazioso 9/8♩♪", listOf(480,60,-180,480,60,-180,480,60,-180), Pair(9,8)),
    GRAZIOSO_12_8_R4_8(RhythmType.BALLET,"Grazioso 12/8♩♪", listOf(480,60,-180,480,60,-180,480,60,-180,480,60,-180), Pair(12,8)),
    GRAZIOSO_3_16_R8_16(RhythmType.BALLET,"Grazioso 3/16♪16", listOf(240,30,-90), Pair(3,16)),
    GRAZIOSO_6_16_R8_16(RhythmType.BALLET,"Grazioso 6/16♪16", listOf(240,30,-90,240,30,-90), Pair(6,16)),
    GRAZIOSO_9_16_R8_16(RhythmType.BALLET,"Grazioso 9/16♪16", listOf(240,30,-90,240,30,-90,240,30,-90), Pair(9,16)),
    GRAZIOSO_12_16_R8_16(RhythmType.BALLET,"Grazioso 12/16♪16", listOf(240,30,-90,240,30,-90,240,30,-90,240,30,-90), Pair(12,16)),
    GRAZIOSETTO_3_8_R8(RhythmType.BALLET,"Graziosetto 3/8♪",listOf(240,60,-180,60,-180), Pair(6,8)),
    GRAZIOSETTO_6_8_R8(RhythmType.BALLET,"Graziosetto 6/8♪",listOf(240,60,-180,60,-180,240,60,-180,60,-180), Pair(6,8)),
    GRAZIOSETTO_2_4_R16(RhythmType.BALLET,"Graziosetto 16",listOf(N4graziosetto, N4graziosetto).flatten(), METRO_2_4),
    GRAZIOSETTO_2_4_R16_8T(RhythmType.BALLET,"Graziosetto 16+♪t",listOf(N4graziosetto, N3graziosetto).flatten(), METRO_2_4),
    GRAZIOSETTO_FLUX_2343(RhythmType.BALLET,"Graziosetto Flux 2343",listOf(N2grazioso, N4graziosetto, N5graziosetto, N4graziosetto).flatten(), Pair(4,4)),
    GRAZIOSETTO_FLUX_3454(RhythmType.BALLET,"Graziosetto Flux 3454",listOf(N3graziosetto, N4graziosetto, N5graziosetto, N4graziosetto).flatten(), Pair(4,4)),
    GRAZIOSETTO_FLUX_4564(RhythmType.BALLET,"Graziosetto Flux 4565",listOf(N4graziosetto, N5graziosetto, N6graziosetto, N5graziosetto).flatten(), Pair(4,4)),
    GRAZIOSETTO_FLUX_345654(RhythmType.BALLET,"Graziosetto Flux 345654",listOf(N3graziosetto, N4graziosetto, N5graziosetto, N6graziosetto, N5graziosetto, N4graziosetto).flatten(), Pair(3,4)),
    GRAZIOSETTO_FLUX_23456543(RhythmType.BALLET,"Graziosetto Flux 23456543",listOf(N2grazioso, N3graziosetto, N4graziosetto, N5graziosetto, N6graziosetto, N5graziosetto, N4graziosetto, N3graziosetto).flatten()),
    DOTTED1_1_4(RhythmType.PUNTATO,"Dotted1 1/4", listOf(360,120), Pair(1,4)),
    DOTTED1_2_4(RhythmType.PUNTATO,"Dotted1 2/4", listOf(360,120,360,120), METRO_2_4),
    DOTTED1_3_4(RhythmType.PUNTATO,"Dotted1 3/4", listOf(360,120,360,120,360,120), METRO_3_4),
    DOTTED2_1_4(RhythmType.PUNTATO,"Dotted2 1/4", listOf(360,60,60), Pair(1,4)),
    DOTTED2_2_4(RhythmType.PUNTATO,"Dotted2 2/4", listOf(360,60,60,360,60,60), METRO_2_4),
    DOTTED2_3_4(RhythmType.PUNTATO,"Dotted2 3/4", listOf(360,60,60,360,60,60,360,60,60,360,60,60), METRO_3_4),
    DOTTED3_1_4(RhythmType.PUNTATO,"Dotted3 1/4", listOf(300,60,60,60), Pair(1,4)),
    DOTTED3_2_4(RhythmType.PUNTATO,"Dotted3 2/4", listOf(300,60,60,60,300,60,60,60), METRO_2_4),
    DOTTED3_3_4(RhythmType.PUNTATO,"Dotted3 3/4", listOf(300,60,60,60,300,60,60,60,300,60,60,60), METRO_3_4),
    DOTTED_FLUX_1232(RhythmType.PUNTATO,"Dotted Flux 1232",
        listOf(N1rhythmDotted, N2rhythmDotted, N3rhythmDotted, N2rhythmDotted).flatten()),
    DOTTED_FLUX_2343(RhythmType.PUNTATO,"Dotted Flux 2343",
        listOf(N2rhythmDotted, N3rhythmDotted, N4rhythmDotted, N3rhythmDotted).flatten()),
    DOTTED_FLUX_3454(RhythmType.PUNTATO,"Dotted Flux 3454",
        listOf(N3rhythmDotted, N4rhythmDotted, N5rhythmDotted, N4rhythmDotted).flatten()),
    DOTTED_FLUX_123432(RhythmType.PUNTATO,"Dotted Flux 123432",
        listOf(N1rhythmDotted, N2rhythmDotted, N3rhythmDotted, N4rhythmDotted, N3rhythmDotted, N2rhythmDotted).flatten(),Pair(3,4)),
    DOTTED_FLUX_234543(RhythmType.PUNTATO,"Dotted Flux 234543",
        listOf(N2rhythmDotted, N3rhythmDotted, N4rhythmDotted, N5rhythmDotted, N4rhythmDotted, N3rhythmDotted).flatten(),Pair(3,4)),
    DOTTED_FLUX_12345432(RhythmType.PUNTATO,"Dotted Flux 12345432",
        listOf(N1rhythmDotted, N2rhythmDotted,N3rhythmDotted, N4rhythmDotted, N5rhythmDotted, N4rhythmDotted,N3rhythmDotted, N2rhythmDotted).flatten()),
    DOTTED_FLUX_23456543(RhythmType.PUNTATO,"Dotted Flux 23456543",
        listOf(N2rhythmDotted, N3rhythmDotted, N4rhythmDotted, N5rhythmDotted, N6rhythmDotted, N5rhythmDotted,N4rhythmDotted,N3rhythmDotted,).flatten()),
    DOTTED_FLUX_1234565432(RhythmType.PUNTATO,"Dotted Flux 1234565432",
        listOf(N1rhythmDotted,N2rhythmDotted, N3rhythmDotted, N4rhythmDotted, N5rhythmDotted, N6rhythmDotted, N5rhythmDotted,N4rhythmDotted, N3rhythmDotted, N2rhythmDotted).flatten(),Pair(5,4)),
    SNARE(RhythmType.DANCE,"Snare", listOf(90,-30,30,-30,30,-30,90,-30,30,-30,30,-30,90,-30,30,-30,30,-30,30,-30,30,-30,30,-30,30,-30)),
    CANCAN(RhythmType.DANCE,"Cancan", listOf(listOf(360,-120),N4graziosetto, N2détaché, N4graziosetto, N2détaché, N4graziosetto, N4staccatissimo, N4staccatissimo).flatten(),Pair(4,8)),

    HABANERA(RhythmType.DANCE,"Habanera", listOf(360,120,60,-180,60,-180), Pair(2,4)),
    SARABANDA(RhythmType.DANCE,"Sarabanda", listOf(360,-120,480,-240,240, 360,-120, 720,-240), Pair(3,4)),
    SICILIANA(RhythmType.DANCE,"Siciliana", listOf(240,60,-60, 120,120,60,-60, 240,60,-60, 120,120,60,-60, 180,-60,60,-60, 180,-60,60,-60,  120,120,60,-180,120,60,-60,
        240,60,-60, 120,120,60,-60, 240,60,-60, 120,120,60,-60, 120,120,60,-60,120,120,60,-60, 240,-240,120,60,-60), Pair(6,16)),
    GET5(RhythmType.DANCE,"Get Five",listOf(120,240,120,180,-60,180,-60,180,-60), Pair(5,8)),
    MAPLE_LEAF_1(RhythmType.RAGTIME,"Maple Leaf 1", listOf(-120,120,120,120, 120,240,120, 120,120,120,600), Pair(2,4)),
    MAPLE_LEAF_2(RhythmType.RAGTIME,"Maple Leaf 2", listOf(-120,120,120,120,120,240,120, 120,120,120,240,120,120,120 ), Pair(2,4)),
    MAPLE_LEAF_3(RhythmType.RAGTIME,"Maple Leaf 3", listOf(180,-60,180,-60, 180,-60,120,240,120,120,120, 120,240,240,120,120,120, 120,240,120, 120,120,180,-60, 180, -180,120), Pair(2,4)),
    CASCADES_1(RhythmType.RAGTIME,"Cascades 1", listOf(180,-60,60,-60,180,-60,60,-60,180,-60, 480,120,120,120,90,-30, 480,60,-60,180,-60,120, 120,-120,120,120, 120,120,120,90,-30 ), Pair(2,4)),
    CASCADES_2(RhythmType.RAGTIME,"Cascades 2", listOf(120,120,120,120,120,120,120,90,-30, 60,-180,60,-60,180,-60,60,-60,180,-60, 60,-180,60,-60,180,-60,60,-60,180,-60, 60,-180,60,-60,180,-60, 120,120,120), Pair(2,4)),

    JUPITER(RhythmType.QUOTE,"Jupiter",listOf(N1,listOf(-240,80,80,80),N1,listOf(-240,80,80,80),N1,listOf(-1200,180,-60, 720,240,720,180,-60,960,480,-480)).flatten()),
    SCHERZO(RhythmType.QUOTE,"Scherzo",listOf(listOf(360,60,-60,60,-180), Ox3staccato, Ox3staccato).flatten(), Pair(3,8)),
    ALFREDO(RhythmType.QUOTE,"Alfredo", listOf(240,-120,30,-30,30,-30, 30,-330,30,-30,30,-30, 30,-330,30,-30,30,-30, 30,-330,30,-30,30,-30,
        240,-120,30,-30,30,-30, 30,-210,30,-210, 30,-90,30,-90,30,-90,30,-90, 30,-90,30,-90,30,-90,30,-90)),
    LUDUS(RhythmType.QUOTE,"Ludus",listOf(60,-180,60,-180,60,-180,60,-180,60,-180, 120,120,180,-60,60,-60,60,-60,480), Pair(5,8)),
    PRECIPITATO(RhythmType.QUOTE,"Precipitato",listOf(120,-120,240, 480,180,-60 ,30,-210,30,-210,
        120,-120,180,-60 ,120,-120,180,-60 ,30,-210,30,-210,30,-210),Pair(7,8)),
    BULGARIAN1(RhythmType.BULGARIAN,"Bulgarian1 4+2+3♫",listOf(H1,N1,N1dotted).flatten(),Pair(9,8)),
    BULGARIAN2(RhythmType.BULGARIAN,"Bulgarian2 2+2+3♫",listOf(N1,N1,N1dotted).flatten(),Pair(7,8)),
    BULGARIAN3(RhythmType.BULGARIAN,"Bulgarian3 2+3♫",listOf(N1,N1dotted).flatten(),Pair(5,8)),
    BULGARIAN4(RhythmType.BULGARIAN,"Bulgarian4 3+2+3♫",listOf(N1dotted,N1,N1dotted).flatten(),Pair(8,8)),
    BULGARIAN5(RhythmType.BULGARIAN,"Bulgarian5 2+2+2+3♫",listOf(N1,N1,N1,N1dotted).flatten(),Pair(9,8)),
    BULGARIAN6(RhythmType.BULGARIAN,"Bulgarian6 3+3+2♫",listOf(N1dotted,N1dotted,N1).flatten(),Pair(8,8)),
    BULGARIAN1GRZ(RhythmType.BULGARIAN,"Bulg.1 grz. 4+2+3♫",listOf(Ox4grz,Ox2grz,Ox3grz).flatten(),Pair(9,8)),
    BULGARIAN2GRZ(RhythmType.BULGARIAN,"Bulg.2 grz. 2+2+3♫",listOf(Ox2grz,Ox2grz,Ox3grz).flatten(),Pair(7,8)),
    BULGARIAN3GRZ(RhythmType.BULGARIAN,"Bulg.3 grz. 2+3♫",listOf(Ox2grz,Ox3grz).flatten(),Pair(5,8)),
    BULGARIAN4GRZ(RhythmType.BULGARIAN,"Bulg.4 grz. 3+2+3♫",listOf(Ox3grz,Ox2grz,Ox3grz).flatten(),Pair(8,8)),
    BULGARIAN5GRZ(RhythmType.BULGARIAN,"Bulg.5 grz. 2+2+2+3♫",listOf(Ox2grz,Ox2grz,Ox2grz,Ox3grz).flatten(),Pair(9,8)),
    BULGARIAN6GRZ(RhythmType.BULGARIAN,"Bulg.6 grz. 3+3+2♫",listOf(Ox3grz,Ox3grz,Ox2grz).flatten(),Pair(8,8)),
    BULGARIAN1H(RhythmType.BULGARIAN,"Bulgarian1 4+2+3♬",listOf(H1h,N1h,N1dottedH).flatten(),Pair(9,8)),
    BULGARIAN2H(RhythmType.BULGARIAN,"Bulgarian2 2+2+3♬",listOf(N1h,N1h,N1dottedH).flatten(),Pair(7,8)),
    BULGARIAN3H(RhythmType.BULGARIAN,"Bulgarian3 2+3♬",listOf(N1h,N1dottedH).flatten(),Pair(5,8)),
    BULGARIAN4H(RhythmType.BULGARIAN,"Bulgarian4 3+2+3♬",listOf(N1dotted,N1h,N1dottedH).flatten(),Pair(8,8)),
    BULGARIAN5H(RhythmType.BULGARIAN,"Bulgarian5 2+2+2+3♬",listOf(N1h,N1h,N1h,N1dottedH).flatten(),Pair(9,8)),
    BULGARIAN6H(RhythmType.BULGARIAN,"Bulgarian6 3+3+2♬",listOf(N1dottedH,N1dottedH,N1h).flatten(),Pair(8,8)),
    BULGARIAN1GRZH(RhythmType.BULGARIAN,"Bulg.1 grz. 4+2+3♬",listOf(Ox4grzH,Ox2grzH,Ox3grzH).flatten(),Pair(9,8)),
    BULGARIAN2GRZH(RhythmType.BULGARIAN,"Bulg.2 grz. 2+2+3♬",listOf(Ox2grzH,Ox2grzH,Ox3grzH).flatten(),Pair(7,8)),
    BULGARIAN3GRZH(RhythmType.BULGARIAN,"Bulg.3 grz. 2+3♬",listOf(Ox2grzH,Ox3grzH).flatten(),Pair(5,8)),
    BULGARIAN4GRZH(RhythmType.BULGARIAN,"Bulg.4 grz. 3+2+3♬",listOf(Ox3grzH,Ox2grzH,Ox3grzH).flatten(),Pair(8,8)),
    BULGARIAN5GRZH(RhythmType.BULGARIAN,"Bulg.5 grz. 2+2+2+3♬",listOf(Ox2grzH,Ox2grzH,Ox2grzH,Ox3grzH).flatten(),Pair(9,8)),
    BULGARIAN6GRZH(RhythmType.BULGARIAN,"Bulg.6 grz. 3+3+2♬",listOf(Ox3grzH,Ox3grzH,Ox2grzH).flatten(),Pair(8,8)),

    HEMIOLIA32(RhythmType.HEMIOLIA,"Hemiolia 3=2", listOf(120,60,60,120),Pair(3,16)),
    HEMIOLIA43(RhythmType.HEMIOLIA,"Hemiolia 4=3", listOf(180,60,120,120,60,180),Pair(3,8)),
    HEMIOLIA54(RhythmType.HEMIOLIA,"Hemiolia 5=4", listOf(480,60,180,120,120,180,60,480),Pair(5,8)),
    HEMIOLIA65(RhythmType.HEMIOLIA,"Hemiolia 6=5", listOf(600,120,480,240,360,360, 240,480,120,600),Pair(6,16)),

    FLUX_3454(RhythmType.FLUX,"Flux 3454",listOf(160,160,160,120,120,120,120,96,96,96,96,96,120,120,120,120,160,160,160)),
    FLUX_345654(RhythmType.FLUX,"Flux 345654",listOf(160,160,160,120,120,120,120,96,96,96,96,96,80,80,80,80,80,80,96,96,96,96,96,120,120,120,120,160,160,160),Pair(3,4)),
    FLUX_34567654(RhythmType.FLUX,"Flux 34567654",listOf(160,160,160,120,120,120,120,96,96,96,96,96,80,80,80,80,80,80,69,68,69,68,69,68,69,80,80,80,80,80,80,96,96,96,96,96,120,120,120,120,160,160,160)),
    FLUX_3456787654(RhythmType.FLUX,"Flux 3456787654",listOf(160,160,160,120,120,120,120,96,96,96,96,96,80,80,80,80,80,80,69,68,69,68,69,68,69,60,60,60,60,60,60,60,60, 69,68,69,68,69,68,69,80,80,80,80,80,80,96,96,96,96,96,120,120,120,120,160,160,160),Pair(2,4)),
    FLUX_2H34H56H78H9TH98H76H54H3(RhythmType.FLUX,"Flux 2h34h56h78h9Th 98h76h54h3",
        listOf(listOf(120),N3,N4h,N5 ,N6h,N7,N8h,N9 ,N10h,N9,N8h,N7 ,N6h,N5,N4h,N3).flatten() ,Pair(3,4)),
    FLUX_234_345_456_567_876_765_654_543(RhythmType.FLUX,"Flux 234 345 456 567 876 765 654 543",
        listOf(N2,N3,N4, N3,N4,N5, N4,N5,N6, N5,N6,N7, N8,N7,N6, N7,N6,N5, N6,N5,N4, N5,N4,N3).flatten(),Pair(3,4)),
    ACC_345678(RhythmType.FLUX,"Acc 345678", listOf(N3,N4,N5, N6,N7,N8).flatten(), Pair(3,4)),
    ACC_23456789T(RhythmType.FLUX,"Acc 23456789T", listOf(N2,N3,N4,N5, N6,N7,N8,N9,N10).flatten(), Pair(3,4)),
    ACC_23456_45678_6789T(RhythmType.FLUX,"Acc 23456 45678 6789T", listOf(N2,N3,N4,N5,N5, N4,N5,N6,N7,N8, N6,N7,N8,N9,N10).flatten(), Pair(5,4)),
    DEC_876543(RhythmType.FLUX,"Dec 876543", listOf(N8,N7,N6,N5,N4,N3).flatten(), Pair(3,4)),
    DEC_T98765432(RhythmType.FLUX,"Dec T98765432", listOf(N10,N9,N8,N7,N6,N5,N4,N3,N2).flatten(), Pair(3,4)),
    DEC_T9876_87654_65432(RhythmType.FLUX,"Dec T9876 87654 65432", listOf(N10,N9,N8,N7,N6, N8,N7,N6,N5,N4, N6,N5,N4,N3,N2).flatten(), Pair(5,4)),
    ;
    fun durationSum(nNotes: Int): Int{
       return RhythmPatterns.durationSum(nNotes, values)
    }
    fun patternDuration(): Int {
        return this.values.map{it.absoluteValue}.sum()
    }
    fun nNotesLeftInThePattern(nNotes: Int) : Int {
        val nPositiveValues = nPositiveValues()
        return nPositiveValues - ( nNotes % nPositiveValues )
    }
    fun nPositiveValues(): Int {
        return values.filter { it > -1 }.count()
    }
    fun separatorValue(nNotes: Int): Int {
        val barValue = metro.first * denominatorMidiValue(metro.second)
        val notesValue = durationSum(nNotes)
        return barValue - (notesValue % barValue)
    }
    fun metroDenominatorMidiValue(): Int {
        return denominatorMidiValue(this.metro.second)
    }

    // can't have two negative values coupled (ex: -80, -20 ... write -100)
    fun retrogradeValues(): List<Int> {
        val size = values.size
        if (size < 2) return values
        val result = mutableListOf<List<Int>>()
        var index = 0
        while(index < size -1){
            if(values[index+1]<0){
                result.add(listOf(values[index], values[index+1]))
                index += 2
            } else {
                result.add(listOf(values[index]))
                index ++
            }
        }
        if (index == size - 1) result.add(listOf(values[index-1]))
        return result.reversed().flatten()
    }

    companion object {
        fun durationSum(nNotes: Int, values: List<Int>): Int{
            var index = 0;
            var sum = 0
            var valueIndex = 0
            while(index < nNotes){
                val value = values[valueIndex % values.size]
                if (value < 0){
                    sum += value * -1
                    valueIndex++
                } else {
                    sum += value
                    index++; valueIndex++
                }
            }
            return sum
        }
        fun denominatorMidiValue(denominator: Int): Int {
            return when(denominator) {
                4 -> 480
                8 -> 240
                16 -> 120
                32 -> 60
                2 -> 960
                64 -> 30
                else -> 480
            }
        }
        fun getTitles(): List<String> {
            return values().map { it.title }
        }
    }
}
fun main(args : Array<String>){
    println("${RhythmPatterns.GRAZIOSETTO_2_4_R16_8T.values}  ->  ${RhythmPatterns.GRAZIOSETTO_2_4_R16_8T.retrogradeValues()}")
    println("${RhythmPatterns.DOTTED3_2_4.values}  ->  ${RhythmPatterns.DOTTED3_2_4.retrogradeValues()}")
    println("${RhythmPatterns.BULGARIAN3GRZ.retrogradeValues() + RhythmPatterns.BULGARIAN3GRZ.values}")
}
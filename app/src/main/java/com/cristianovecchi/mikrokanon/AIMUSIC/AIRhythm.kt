package com.cristianovecchi.mikrokanon.AIMUSIC

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
val N6h = listOf(80,80,80)
val N5 = listOf(96,96,96,96,96)
val N4 = listOf(120,120,120,120)
val N4détaché = listOf(90,-30,90,-30,90,-30,90,-30)
val N4staccato = listOf(30,-90,30,-90,30,-90,30,-90)
val N4staccatissimo = listOf(15,-105,15,-105,15,-105,15,-105)
val N4graziosetto = listOf(120,30,-90,30,-90,30,-90)
val N4h = listOf(120,120)
val N3 = listOf(160,160,160)
val N3graziosetto = listOf(160,40,-120,40,-120)
val N2 = listOf(240,240)
val N2détaché = listOf(180,-60,180,-60)
val N1 = listOf(480)
val Qx3staccato = listOf(120,-360,120,-360,120,-360)// Q = Quaver
val Ox3staccato = listOf(60,-180,60,-180,60,-180)// O = Octave
val N1dotted = listOf(720) // Octave x 3
val Ox4grz = listOf(240,60,-180,60,-180,60,-180)// Octave x 4 graziosetto
val Ox3grz = listOf(240,60,-180,60,-180)// Octave x 3 graziosetto
val Ox2grz = listOf(240,60,-180)// Octave x 2 graziosetto
val H1 = listOf(960) // H = 2/4

enum class RhythmPatterns(val title: String, val values: List<Int>,val metro: Pair<Int,Int> = Pair(4,4)) {
    PLAIN_1_4("Plain 1/4", listOf(480,480,480,480)),
    PLAIN_1_8("Plain 1/8", listOf(240,240,240,240,240,240,240,240)),
    PLAIN_1_8T("Plain 1/8t", listOf(160,160,160,160,160,160,160,160,160,160,160,160)),
    PLAIN_1_16("Plain 1/16", listOf(120,120,120,120,120,120,120,120,120,120,120,120,120,120,120,120)),
    PLAIN_1_32("Plain 1/32", listOf(60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,), Pair(2,4)),
    STACCATO_1_16("Staccato 1/16",listOf(30,-90,30,-90,30,-90,30,-90,30,-90,30,-90,30,-90,30,-90), Pair(2,4)),
    GRAZIOSO_1_8("Grazioso 1/8",listOf(240,60,-180,240,60,-180), Pair(2,4)),
    GRAZIOSO_1_8t("Grazioso 1/8t",listOf(240,60,-180,60,-180,240,60,-180,60,-180), Pair(6,8)),
    GRAZIOSO_1_16("Grazioso 1/16",listOf(120,30,-90,120,30,-90,120,30,-90,120,30,-90,), Pair(2,4)),
    GRAZIOSETTO_1_16("Graziosetto 1/16",listOf(N4graziosetto, N4graziosetto).flatten(), Pair(2,4)),
    GRAZIOSETTO_1_16_1_8t("Graziosetto 1/16 1/8t",listOf(N4graziosetto, N3graziosetto).flatten(), Pair(2,4)),
    DOTTED_1_4("Dotted 1/4", listOf(360,120,360,120,360,120,360,120)),
    DOTTED2_1_4("Dotted2 1/4", listOf(360,60,60,360,60,60,360,60,60,360,60,60)),
    DOTTED3_1_4("Dotted3 1/4", listOf(300,60,60,60,300,60,60,60,300,60,60,60,300,60,60,60)),
    RAGTIME("Ragtime", listOf(120,240,120,60,-180,60,-180, 120,120,120,240,120,180,-60), Pair(2,4)),
    SNARE("Snare", listOf(90,-30,30,-30,30,-30,90,-30,30,-30,30,-30,90,-30,30,-30,30,-30,30,-30,30,-30,30,-30,30,-30)),
    CANCAN("Cancan", listOf(listOf(360,-120),N4graziosetto, N2détaché, N4graziosetto, N2détaché, N4graziosetto, N4staccatissimo, N4staccatissimo).flatten(),Pair(4,8)),
    GET5("Get Five",listOf(120,240,120,180,-60,180,-60,180,-60), Pair(5,8)),
    HABANERA("Habanera", listOf(360,120,60,-180,60,-180), Pair(2,4)),
    SARABANDA("Sarabanda", listOf(360,-120,480,-240,240, 360,-120, 720,-240), Pair(3,4)),
    SICILIANA("Siciliana", listOf(240,60,-60, 120,120,60,-60, 240,60,-60, 120,120,60,-60, 180,-60,60,-60, 180,-60,60,-60,  120,120,60,-180,120,60,-60,
        240,60,-60, 120,120,60,-60, 240,60,-60, 120,120,60,-60, 120,120,60,-60,120,120,60,-60, 240,-240,120,60,-60), Pair(6,16)),
    JUPITER("Jupiter",listOf(N1,listOf(-240,80,80,80),N1,listOf(-240,80,80,80),N1,listOf(-1200,180,-60, 720,240,720,180,-60,960,480,-480)).flatten()),
    SCHERZO("Scherzo",listOf(listOf(360,60,-60,60,-180), Ox3staccato, Ox3staccato).flatten(), Pair(3,8)),
    ALFREDO("Alfredo", listOf(240,-120,30,-30,30,-30, 30,-330,30,-30,30,-30, 30,-330,30,-30,30,-30, 30,-330,30,-30,30,-30,
        240,-120,30,-30,30,-30, 30,-210,30,-210, 30,-90,30,-90,30,-90,30,-90, 30,-90,30,-90,30,-90,30,-90)),
    LUDUS("Ludus",listOf(60,-180,60,-180,60,-180,60,-180,60,-180, 120,120,180,-60,60,-60,60,-60,480), Pair(5,8)),
    BULGARIAN1("Bulgarian1 4+2+3",listOf(H1,N1,N1dotted).flatten(),Pair(9,8)),
    BULGARIAN2("Bulgarian2 2+2+3",listOf(N1,N1,N1dotted).flatten(),Pair(7,8)),
    BULGARIAN3("Bulgarian3 2+3",listOf(N1,N1dotted).flatten(),Pair(5,8)),
    BULGARIAN4("Bulgarian4 3+2+3",listOf(N1dotted,N1,N1dotted).flatten(),Pair(8,8)),
    BULGARIAN5("Bulgarian5 2+2+2+3",listOf(N1,N1,N1,N1dotted).flatten(),Pair(9,8)),
    BULGARIAN6("Bulgarian6 3+3+2",listOf(N1dotted,N1dotted,N1).flatten(),Pair(8,8)),
    BULGARIAN1GRZ("Bulg.1 grz. 4+2+3",listOf(Ox4grz,Ox2grz,Ox3grz).flatten(),Pair(9,8)),
    BULGARIAN2GRZ("Bulg.2 grz. 2+2+3",listOf(Ox2grz,Ox2grz,Ox3grz).flatten(),Pair(7,8)),
    BULGARIAN3GRZ("Bulg.3 grz. 2+3",listOf(Ox2grz,Ox3grz).flatten(),Pair(5,8)),
    BULGARIAN4GRZ("Bulg.4 grz. 3+2+3",listOf(Ox3grz,Ox2grz,Ox3grz).flatten(),Pair(8,8)),
    BULGARIAN5GRZ("Bulg.5 grz. 2+2+2+3",listOf(Ox2grz,Ox2grz,Ox2grz,Ox3grz).flatten(),Pair(9,8)),
    BULGARIAN6GRZ("Bulg.6 grz. 3+3+2",listOf(Ox3grz,Ox3grz,Ox2grz).flatten(),Pair(8,8)),
    PRECIPITATO("Precipitato",listOf(120,-120,240, 480,180,-60 ,30,-210,30,-210,
        120,-120,180,-60 ,120,-120,180,-60 ,30,-210,30,-210,30,-210),Pair(7,8)),
    HEMIOLIA32("Hemiolia 3=2", listOf(120,60,60,120),Pair(3,16)),
    HEMIOLIA43("Hemiolia 4=3", listOf(180,60,120,120,60,180),Pair(3,8)),
    HEMIOLIA54("Hemiolia 5=4", listOf(480,60,180,120,120,180,60,480),Pair(5,8)),
    HEMIOLIA65("Hemiolia 6=5", listOf(600,120,480,240,360,360, 240,480,120,600),Pair(6,16)),

    FLUX_3454("Flux 3454",listOf(160,160,160,120,120,120,120,96,96,96,96,96,120,120,120,120,160,160,160)),
    FLUX_345654("Flux 345654",listOf(160,160,160,120,120,120,120,96,96,96,96,96,80,80,80,80,80,80,96,96,96,96,96,120,120,120,120,160,160,160),Pair(3,4)),
    FLUX_34567654("Flux 34567654",listOf(160,160,160,120,120,120,120,96,96,96,96,96,80,80,80,80,80,80,69,68,69,68,69,68,69,80,80,80,80,80,80,96,96,96,96,96,120,120,120,120,160,160,160)),
    FLUX_3456787654("Flux 3456787654",listOf(160,160,160,120,120,120,120,96,96,96,96,96,80,80,80,80,80,80,69,68,69,68,69,68,69,60,60,60,60,60,60,60,60, 69,68,69,68,69,68,69,80,80,80,80,80,80,96,96,96,96,96,120,120,120,120,160,160,160),Pair(2,4)),
    FLUX_2h34h56h78h9Th98h76h54h3("Flux 2h34h56h78h9Th 98h76h54h3",
        listOf(listOf(120),N3,N4h,N5 ,N6h,N7,N8h,N9 ,N10h,N9,N8h,N7 ,N6h,N5,N4h,N3).flatten() ,Pair(3,4)),
    FLUX_234_345_456_567_876_765_654_543("Flux 234 345 456 567 876 765 654 543",
        listOf(N2,N3,N4, N3,N4,N5, N4,N5,N6, N5,N6,N7, N8,N7,N6, N7,N6,N5, N6,N5,N4, N5,N4,N3).flatten(),Pair(3,4)),
    ACC_345678("Acc 345678", listOf(N3,N4,N5, N6,N7,N8).flatten(), Pair(3,4)),
    ACC_23456789T("Acc 23456789T", listOf(N2,N3,N4,N5, N6,N7,N8,N9,N10).flatten(), Pair(3,4)),
    ACC_23456_45678_6789T("Acc 23456 45678 6789T", listOf(N2,N3,N4,N5,N5, N4,N5,N6,N7,N8, N6,N7,N8,N9,N10).flatten(), Pair(5,4)),
    DEC_876543("Dec 876543", listOf(N8,N7,N6,N5,N4,N3).flatten(), Pair(3,4)),
    DEC_T98765432("Dec T98765432", listOf(N10,N9,N8,N7,N6,N5,N4,N3,N2).flatten(), Pair(3,4)),
    DEC_T9876_87654_65432("Dec T9876 87654 65432", listOf(N10,N9,N8,N7,N6, N8,N7,N6,N5,N4, N6,N5,N4,N3,N2).flatten(), Pair(5,4)),
    ;
    fun durationSum(nNotes: Int): Int{
       return RhythmPatterns.durationSum(nNotes, values)
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
            return values().map {
                it.title
            }
        }

    }
}

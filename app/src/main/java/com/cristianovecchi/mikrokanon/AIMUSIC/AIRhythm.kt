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
enum class RhythmPatterns(val title: String, val values: List<Int> ) {
    PLAIN_1_4("Plain 1/4", listOf(480)),
    PLAIN_1_8("Plain 1/8", listOf(240)),
    PLAIN_1_8T("Plain 1/8t", listOf(160)),
    PLAIN_1_16("Plain 1/16", listOf(120)),
    PLAIN_1_32("Plain 1/32", listOf(60)),
    DOTTED_1_4("Dotted 1/4", listOf(360,120)),
    DOTTED2_1_4("Dotted2 1/4", listOf(360,60,60)),
    DOTTED3_1_4("Dotted3 1/4", listOf(300,60,60,60)),
    RAGTIME("Ragtime", listOf(120,240,120,240,240)),
    SNARE("Snare", listOf(90,-30,30,-30,30,-30,90,-30,30,-30,30,-30,90,-30,30,-30,30,-30,30,-30,30,-30,30,-30,30,-30)),
    TAKE5("Take Five",listOf(120,240,120,240,240,240)),
    HABANERA("Habanera", listOf(360,120,60,-180,60,-180)),
    SARABANDA("Sarabanda", listOf(480,720,240)),
    STACCATO_1_16("Staccato 1/16",listOf(30,-90)),
    GRAZIOSO_1_8("Grazioso 1/8",listOf(240,60,-180)),
    GRAZIOSO_1_16("Grazioso 1/16",listOf(120,30,-90));
    companion object {
        fun getTitles(): List<String> {
            return values().map {
                it.title
            }
        }
    }
}

package com.cristianovecchi.mikrokanon.AIMUSIC

import com.cristianovecchi.mikrokanon.divideDistributingRest
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

fun List<Int>.patternTicksAndDurations(): Pair<List<Int>, List<Int>>{
    val ticks = mutableListOf<Int>()
    val durs = mutableListOf<Int>()
    var cursor = 0
    this.forEach {
        if(it < 0){
            cursor += it.absoluteValue
        } else {
            ticks += cursor
            durs += it
            cursor += it
        }
    }
    return ticks to durs
}
fun List<Int>.patternTicksAndDurationInSection(sectionStart: Int, sectionDuration: Int ): Pair<List<Int>, List<Int>>{
    if(sectionDuration == 0) return listOf<Int>() to listOf<Int>()
    val ticks = mutableListOf<Int>()
    val durs = mutableListOf<Int>()
    val sectionEnd = sectionStart + sectionDuration
    var cursor = sectionStart
    var index = 0
    val valuesSize = this.size
    while(cursor < sectionEnd){
        val value = this[index % valuesSize]
        if(value < 0) {
            cursor += value.absoluteValue
        } else {
            ticks += cursor
            val cursorEnd = cursor + value
            durs += if(cursorEnd < sectionEnd) value else sectionEnd - cursor

            cursor += value
        }
        index++
    }
    return ticks to durs
}
fun List<Int>.resizePatternByDuration(duration: Int, startTick: Int = 0): Pair<List<Int>, List<Int>>{
    val durs = mutableListOf<Int>()
    val ticks = mutableListOf<Int>()
    val thisDuration = this.sumOf { it.absoluteValue }
    val ratio = duration.toFloat() / thisDuration
    var tick = startTick
    var rest = 0f
    val areNegative = this.map{ it < 0 }
    val positivePattern = this.map{
        val floatValue = it.absoluteValue * ratio
        val intValue = floatValue.roundToInt()
        val decimal = floatValue - intValue
        rest += decimal
        val add: Int
        if(rest >=1f) {
            add = 1
            rest -= 1f
        } else {
            add = 0
        }
        intValue + add
    }.toMutableList()
    val diff = duration - positivePattern.sum()
    if (diff != 0){
        positivePattern[positivePattern.size-1] = positivePattern.last() + diff
    }
    val newPattern = positivePattern.mapIndexed { index, i -> if(areNegative[index]) i * -1 else i  }
    newPattern.forEach {
        if(it < 0){
            tick += it.absoluteValue
        } else {
            ticks += tick
            durs += it
            tick += it
        }
    }
//    println("durs: $durs")
//    println("ticks: $ticks")
//    println("ratio: $ratio")
//    println("pattern: $this  newPattern: $newPattern ")
//    println("duration requested: $duration  duration achieved: ${newPattern.map{it.absoluteValue}.sum()}")
    return durs to ticks
}
fun multiplyDurations(nTotalNotes: Int, durations: List<Int> ):IntArray {
    val actualDurations = IntArray(nTotalNotes * 2 + 1)// note + optional rests + optional initial rest
    (0 until nTotalNotes * 2 + 1).forEach {
        actualDurations[it] = durations[it % durations.size]
    }
    return actualDurations
}
fun multiplyRhythmPatternDatas(nTotalNotes:Int, nRhythmSteps: Int,
        rhythm:List<Triple<RhythmPatterns, Boolean, Int>> ): List<Triple<RhythmPatterns, Boolean, Int>>{
    val actualRhythm = mutableListOf<Triple<RhythmPatterns, Boolean, Int>>()
    //println("TOTAL NOTES: $nTotalNotes   STEPS: $nRhythmSteps")
    if(nTotalNotes>=nRhythmSteps){
        (0..(nTotalNotes / nRhythmSteps + (if (nTotalNotes % nRhythmSteps == 0) 0 else 1))).forEach { _ ->
            actualRhythm.addAll(rhythm)
        }
    } else {
        actualRhythm.addAll(rhythm)
    }
    return actualRhythm
}
fun findTicksFromDurations(tick:Int, durations: List<Int>): List<Int>{
    var lastTick = tick
    val ticks = (0 until durations.size -1).map {
        lastTick += durations[it]
        lastTick
    }
    return listOf(tick, *ticks.toTypedArray())
}
fun findScaleDurations(duration: Int, nNotes: Int, maxShortNote: Int): List<Int>{
    val halfDuration = duration / 2
    val shortDurs = halfDuration.divideDistributingRest(nNotes)
    return if (shortDurs[0] > maxShortNote) MutableList(nNotes){60}.apply{ this[0] += duration - maxShortNote * nNotes }
    else shortDurs.apply { this[0] += halfDuration }
}
fun findScaleTicks(tick: Int, scaleDurations: List<Int>): List<Int>{
        var lastTick = tick
        val ticks = (0 until scaleDurations.size -1).map {
            lastTick += scaleDurations[it]
            lastTick
        }
        return listOf(tick, *ticks.toTypedArray())

}
fun find2ShortAndLongDurations(duration: Int, maxShortNote: Int): List<Int>{
    val dur = if (maxShortNote * 4 > duration) duration / 4 else maxShortNote
    return listOf(dur, dur, duration - dur * 2)
}
fun find2ShortAndLongTicks(tick: Int, duration: Int, shortDur: Int, isRetrograde: Boolean = false): List<Int>{
    return if(isRetrograde){
        val longDuration = duration - shortDur * 2
        val startGruppettoTick = tick + longDuration
        listOf(tick, startGruppettoTick, startGruppettoTick + shortDur)
    } else {
        listOf(tick, tick + shortDur, tick + shortDur * 2)
    }
}
fun find4ShortAndLongDurations(duration: Int, maxShortNote: Int): List<Int>{
    val dur = if (maxShortNote * 8 > duration) duration / 8 else maxShortNote
    return listOf(dur, dur, dur, dur, duration - dur * 4)
}
fun find4ShortAndLongTicks(tick: Int, duration: Int, shortDur: Int, isRetrograde: Boolean = false): List<Int>{
    return if(isRetrograde){
        val longDuration = duration - shortDur * 4
        val startGruppettoTick = tick + longDuration
        listOf(tick, startGruppettoTick, startGruppettoTick + shortDur, startGruppettoTick + shortDur * 2, startGruppettoTick + shortDur * 3 )
    } else {
        listOf(tick, tick + shortDur, tick + shortDur * 2, tick + shortDur * 3, tick + shortDur * 4 )
    }
}
fun find6ShortAndLongDurations(duration: Int, maxShortNote: Int): List<Int>{
    val dur = if (maxShortNote * 12 > duration) duration / 12 else maxShortNote
    return listOf(dur, dur, dur, dur, dur, dur, duration - dur * 6)
}
fun find6ShortAndLongTicks(tick: Int, duration: Int, shortDur: Int, isRetrograde: Boolean = false): List<Int>{
    return if(isRetrograde){
        val longDuration = duration - shortDur * 6
        val startGruppettoTick = tick + longDuration
        listOf(tick, startGruppettoTick, startGruppettoTick + shortDur, startGruppettoTick + shortDur * 2, startGruppettoTick + shortDur * 3, startGruppettoTick + shortDur * 4, startGruppettoTick + shortDur * 5)
    } else {
        listOf(tick, tick + shortDur, tick + shortDur * 2, tick + shortDur * 3, tick + shortDur * 4, tick + shortDur * 5, tick + shortDur * 6 )
    }
}
fun findTrillDurations(duration: Int): Pair<List<Int>,Int>{
    val div = when(duration){
        in (0..59) -> -1
        in (60..239) -> 3
        in (240..359) -> 5 // 34 - 42
        in (360..479) -> 7 // 32 - 38
        in (480..Int.MAX_VALUE) -> (480 / 43) * (duration / 480)
        else -> -1
    }
    return if(div == -1) Pair(listOf(duration), div) else Pair(duration.divideDistributingRest(div), div)
}
fun findOscillationDurations(duration: Int): Pair<List<Int>,Int> {
    val div = when (duration) {
        in (0..59) -> -1
        in (60..119) -> duration / 60 * 4 + 1
        in (120..239) -> duration / 120 * 4 + 1
        in (240..359) -> duration / 240 * 4 + 1
        in (360..479) -> duration / 360 * 4 + 1
        in (480..719) -> duration / 480 * 4 + 1
        in (720..959) -> duration / 720 * 4 + 1
        in (960..1439) -> duration / 960 * 4 + 1
        else -> duration / 1440 * 4 + 1
    }
    return if (div == -1) Pair(listOf(duration), div)
            else Pair(duration.divideDistributingRest(div), div)
}
fun IntArray.applySwing(shuffle: Float, maxDur: Int = 240): IntArray {
    val result = mutableListOf<Int>()
    var index = 0
    while(index < this.size -1){
        val first = this[index]
        val second = this[index+1]
        if( first <= maxDur && first > 0 && second > 0 && first == second){
                    result += (first * shuffle).roundToInt()
                    result += (second * (1f - shuffle)).roundToInt()
                    index += 2
                    //println("Swinging: $first $second -> ${(first * shuffle).roundToInt()} ${(second * (1f - shuffle)).roundToInt()}")
        } else {
            result += first
            index++
        }
    }
    if(this.size != result.size) result += this.last()
//    println(this.contentToString())
//    println(result)
    if(this.size != result.size) {
        throw Exception("Shuffle is incorrect: size=${this.size} result size:${result.size}")
    }
    return result.toIntArray()//.apply{ println("nNotes: ${this.size}")}
}
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
val N2grazioso = listOf(240,60,-180)
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
val Ox5bow = listOf(240,240,240,240,60,-180)
val Ox4bow = listOf(240,240,240,60,-180)
val Ox3bow = listOf(240,240,60,-180)
val Ox2bow = listOf(240,60,-180)
val H1 = listOf(960); val H1h = listOf(480) // H = 2/4
val Q = listOf(480); val O = listOf(240); val Orest = listOf(-240); val Ostaccato = listOf(60, -180)
val METRO_2_4 = Pair(2,4)
val METRO_3_4 = Pair(3,4)
val METRO_4_4 = Pair(4,4)
enum class RhythmType{
     BASIC, PLAIN, BALLET, PUNTATO, MIX, DANCE, RAGTIME, LATIN, QUOTE, BULGARIAN, HEMIOLIA, FLUX
}
// WARNING: can't have two negative values coupled (ex: -80, -20 ... write -100)
enum class RhythmPatterns(val type: RhythmType, val title: String, val values: List<Int>,val metro: Pair<Int,Int> = METRO_4_4) {
    BASIC_9_4(RhythmType.BASIC,"Basic 9/4", listOf(4320), Pair(9,4)),
    BASIC_4_2(RhythmType.BASIC,"Basic 4/2", listOf(3840), Pair(4,2)),
    BASIC_7_4(RhythmType.BASIC,"Basic 7/4", listOf(3360), Pair(7,4)),
    BASIC_6_4(RhythmType.BASIC,"Basic 6/4", listOf(2880), Pair(6,4)),
    BASIC_5_4(RhythmType.BASIC,"Basic 5/4", listOf(2400), Pair(5,4)),
    BASIC_4_4(RhythmType.BASIC,"Basic 4/4", listOf(1920), Pair(4,4)),
    BASIC_3_4(RhythmType.BASIC,"Basic 3/4", listOf(1440), Pair(3,4)),
    BASIC_2_4(RhythmType.BASIC,"Basic 2/4", listOf(960), Pair(2,4)),
    BASIC_4(RhythmType.BASIC,"Basic ♩", listOf(480), Pair(1,4)),
    BASIC_9_8(RhythmType.BASIC,"Basic 9/8", listOf(2160), Pair(9,8)),
    BASIC_7_8(RhythmType.BASIC,"Basic 7/8", listOf(1680), Pair(7,8)),
    BASIC_5_8(RhythmType.BASIC,"Basic 5/8", listOf(1200), Pair(5,8)),
    BASIC_3_8(RhythmType.BASIC,"Basic 3/8", listOf(720), Pair(3,8)),
    BASIC_8(RhythmType.BASIC,"Basic ♪", listOf(240), Pair(1,8)),
    BASIC_9_16(RhythmType.BASIC,"Basic 9/16", listOf(1080), Pair(9,16)),
    BASIC_7_16(RhythmType.BASIC,"Basic 7/16", listOf(840), Pair(7,16)),
    BASIC_5_16(RhythmType.BASIC,"Basic 5/16", listOf(600), Pair(5,16)),
    BASIC_3_16(RhythmType.BASIC,"Basic 3/16", listOf(360), Pair(3,16)),
    BASIC_16(RhythmType.BASIC,"Basic 16", listOf(120), Pair(1,16)),
    BASIC_9_32(RhythmType.BASIC,"Basic 9/32", listOf(540), Pair(9,32)),
    BASIC_7_32(RhythmType.BASIC,"Basic 7/32", listOf(420), Pair(7,32)),
    BASIC_5_32(RhythmType.BASIC,"Basic 5/32", listOf(300), Pair(5,32)),
    BASIC_3_32(RhythmType.BASIC,"Basic 3/32", listOf(180), Pair(3,32)),
    BASIC_32(RhythmType.BASIC,"Basic 32", listOf(60), Pair(1,32)),
    BASIC_3_64(RhythmType.BASIC,"Basic 3/64", listOf(90), Pair(3,64)),
    BASIC_64(RhythmType.BASIC,"Basic 64", listOf(30), Pair(1,64)),
    BASIC_128(RhythmType.BASIC,"Basic 128", listOf(15), Pair(1,128)),
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
    PLAIN_2_4_R16(RhythmType.PLAIN,"Plain 2/4 16", listOf(120,120,120,120,120,120,120,120), METRO_2_4),
    PLAIN_3_4_R16(RhythmType.PLAIN,"Plain 3/4 16", listOf(120,120,120,120,120,120,120,120,120,120,120,120), METRO_3_4),
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
    GRAZIOSO_2_4_R8T(RhythmType.BALLET,"Grazioso 2/4♪t",listOf(160,40,-120,160,40,-120,160,40,-120, 160,40,-120,160,40,-120,160,40,-120), METRO_2_4),
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
    GRAZIOSETTO_3_8_R8(RhythmType.BALLET,"Graziosetto 3/8♪",listOf(240,60,-180,60,-180), Pair(3,8)),
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
    DOTTED2_3_4(RhythmType.PUNTATO,"Dotted2 3/4", listOf(360,60,60,360,60,60,360,60,60), METRO_3_4),
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
    MIXED_8_8T(RhythmType.MIX, "Mix 3/8 (♪+♪t)", listOf(240, 160,160,160), Pair(3,8)),
    MIXED_3X8_8T(RhythmType.MIX, "Mix 5/8 (3♪+♪t)", listOf(240,240,240, 160,160,160), Pair(5,8)),
    MIXED_8T_16(RhythmType.MIX, "Mix 5/16 (♪t+16)", listOf(160,160,160, 120), Pair(5,16)),
    MIXED_8T_3X16(RhythmType.MIX, "Mix 7/16 (♪t+3X16)", listOf(160,160,160, 120,120,120), Pair(7,16)),
    MIXED_16_16T(RhythmType.MIX, "Mix 3/16 (16+16t)", listOf(120, 80,80,80), Pair(3,16)),
    MIXED_3X16_16T(RhythmType.MIX, "Mix 5/16 (3X16+16t)", listOf(120,120,120, 80,80,80), Pair(5,16)),
    SNARE(RhythmType.DANCE,"Snare", listOf(90,-30, 30,-30,30,-30, 90,-30, 30,-30,30,-30, 90,-30, 30,-30,30,-30, 30,-30,30,-30, 30,-30,30,-30), Pair(2,4)),
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

    MAMBO_1(RhythmType.LATIN,"Mambo 1", listOf(240,240,240,240,480,240,240, -240,240,240,240,480,240,240), Pair(4,4)),
    MAMBO_2(RhythmType.LATIN,"Mambo 2", listOf(480,480,240,240,240,240, 480,240,240,-240,240,240,240), Pair(4,4)),
    MAMBO_3(RhythmType.LATIN,"Mambo 3", listOf(480,480,240,240,-240,240, 480,240,240,-240,240,-240,240), Pair(4,4)),
    MAMBO_4(RhythmType.LATIN,"Mambo 4", listOf(480,480,240,240,-240,480, 240,240,240,-240,240,-240,240), Pair(4,4)),
    MAMBO_1BOW(RhythmType.LATIN,"Mambo 1♫", listOf(Ox4bow,Ox2bow,Ox2bow, Orest,Ox3bow,Q,Ox2bow).flatten(), Pair(4,4)),
    MAMBO_2BOW(RhythmType.LATIN,"Mambo 2♫", listOf(Ox2bow,Ox2bow,Ox4bow, Ox2bow,Ox2bow,Orest,Ox3bow).flatten(), Pair(4,4)),
    MAMBO_3BOW(RhythmType.LATIN,"Mambo 3♫", listOf(Ox2bow,Ox2bow,Ox2bow,Orest,Ox3bow,Ox2bow,Orest,listOf(60,-420,240)).flatten(), Pair(4,4)),
    MAMBO_4BOW(RhythmType.LATIN,"Mambo 4♫", listOf(Ox2bow,Ox2bow,Ox2bow,Orest,Ox2bow,Ox3bow,Orest,listOf(60,-420,240)).flatten(), Pair(4,4)),
    BOSSANOVA_1(RhythmType.LATIN,"Bossanova 1", listOf(480,720,720), Pair(4,4)),
    BOSSANOVA_2(RhythmType.LATIN,"Bossanova 2", listOf(480,720,480,480,720,480,480), Pair(4,4)),
    BOSSANOVA_3(RhythmType.LATIN,"Bossanova 3", listOf(480,720,480,720,720,720), Pair(4,4)),
    BOSSANOVA_4(RhythmType.LATIN,"Bossanova 4", listOf(720,240,240,240,-240,480,240,-240,480,240,-240,960,240,240,240,-240,480,240,-240,480,240,-240,240), Pair(4,4)),
    BOSSANOVA_1BOW(RhythmType.LATIN,"Bossanova 1♫", listOf(Ox2bow,Ox3bow,Ox3bow).flatten(), Pair(4,4)),
    BOSSANOVA_2BOW(RhythmType.LATIN,"Bossanova 2♫", listOf(Ox2bow,Ox3bow,Ox2bow,Ox2bow,Ox3bow,Ox2bow,Ox2bow).flatten(), Pair(4,4)),
    BOSSANOVA_3BOW(RhythmType.LATIN,"Bossanova 3♫", listOf(Ox2bow,Ox3bow,Ox2bow,Ox3bow,Ox3bow,Ox3bow).flatten(), Pair(4,4)),
    BOSSANOVA_4BOW(RhythmType.LATIN,"Bossanova 4♫", listOf(Ox3bow,Ox3bow,Orest,Ox3bow,Orest,Ox3bow,Orest,Ox4bow,Ox3bow,Orest,Ox3bow,Orest,Ox3bow,Orest,O).flatten(), Pair(4,4)),
    SALSA_1(RhythmType.LATIN,"Salsa 1", listOf(480,480,240,240,240,240, -240,240,240,240,480,240,240), Pair(4,4)),
    SALSA_2(RhythmType.LATIN,"Salsa 2", listOf(480,480,240,480,480, 240,240,240,480,240,240), Pair(4,4)),
    SALSA_3(RhythmType.LATIN,"Salsa 3", listOf(480,480,240,480,480, 240,240,240,960), Pair(4,4)),
    SALSA_4(RhythmType.LATIN,"Salsa 4", listOf(240,240,240,240,-240,240,-240,240, -240,480,240,-240,480,240, 240,240,240,240,-240,240,-240,240, -240,480,240,-240,240,-240,240), Pair(4,4)),
    SALSA_1BOW(RhythmType.LATIN,"Salsa 1♫", listOf(Ox2bow,Ox2bow,Ox4bow, Orest,Ox3bow,Ox2bow,Ox2bow).flatten(), Pair(4,4)),
    SALSA_2BOW(RhythmType.LATIN,"Salsa 2♫", listOf(Ox2bow,Ox2bow,Ostaccato,Ox2bow,Ox2bow, Ox3bow,Ox2bow,Ox2bow).flatten(), Pair(4,4)),
    SALSA_3BOW(RhythmType.LATIN,"Salsa 3♫", listOf(Ox2bow,Ox2bow,Ostaccato,Ox2bow,Ox2bow,Ox3bow,Ox4bow).flatten(), Pair(4,4)),
    SALSA_4BOW(RhythmType.LATIN,"Salsa 4♫", listOf(Ox4bow,Orest,Ostaccato,Orest,Ostaccato, Orest,Ox2bow,Ostaccato,Orest,Ox2bow,Ox5bow,Orest,Ostaccato,Orest,Ostaccato, Orest,Ox2bow,Ostaccato,Orest,Ostaccato,Orest,Ostaccato).flatten(), Pair(4,4)),

    JUPITER(RhythmType.QUOTE,"Jupiter",listOf(N1,listOf(-240,80,80,80),N1,listOf(-240,80,80,80),N1,listOf(-1200,180,-60, 720,240,720,180,-60,960,480,-480)).flatten()),
    SCHERZO(RhythmType.QUOTE,"Scherzo",listOf(listOf(360,60,-60,60,-180), Ox3staccato, Ox3staccato).flatten(), Pair(3,8)),
    ALFREDO(RhythmType.QUOTE,"Alfredo", listOf(240,-120,30,-30,30,-30, 30,-330,30,-30,30,-30, 30,-330,30,-30,30,-30, 30,-330,30,-30,30,-30,
        240,-120,30,-30,30,-30, 30,-210,30,-210, 30,-90,30,-90,30,-90,30,-90, 30,-90,30,-90,30,-90,30,-90)),
    LUDUS(RhythmType.QUOTE,"Ludus",listOf(60,-180,60,-180,60,-180,60,-180,60,-180, 120,120,180,-60,60,-60,60,-60,480), Pair(5,8)),
    PRECIPITATO(RhythmType.QUOTE,"Precipitato",listOf(120,-120,240, 480,180,-60 ,30,-210,30,-210,
        120,-120,180,-60 ,120,-120,180,-60 ,30,-210,30,-210,30,-210),Pair(7,8)),
    HAPPY_BIRTHDAY_1(RhythmType.QUOTE,"Birthday 1",listOf(320,160,480,480,480,480,-480), Pair(3,4)),
    HAPPY_BIRTHDAY_2(RhythmType.QUOTE,"Birthday 2",listOf(320,160,480,480,480,480,480), Pair(3,4)),
    JINGLE_BELLS_1(RhythmType.QUOTE,"Jingle Bells 1",listOf(180,-60,180,-60,420,-60, 180,-60,180,-60,420,-60, 180,-60,180,-60,180,-60,180,-60,900,-60), Pair(4,4)),
    JINGLE_BELLS_2(RhythmType.QUOTE,"Jingle Bells 2",listOf(180,-60,180,-60,300,-60,60,-60, 180,-60,180,-60,300,-60,60,-60, 180,-60,180,-60,180,-60,180,-60,480,420,-60), Pair(4,4)),
    JINGLE_BELLS_3(RhythmType.QUOTE,"Jingle Bells 3",listOf(180,-60,180,-60,300,-60,60,-60, 180,-60,180,-60,180,-60,60,-60,60,-60, 180,-60,180,-60,180,-60,180,-60,900,-60), Pair(4,4)),
    JINGLE_BELLS_4(RhythmType.QUOTE,"Jingle Bells 4",listOf(180,-60,180,-60,180,-60,180,-60,900,-60, 180,-60,180,-60,180,-60,180,-60,900,-60, 180,-60,180,-60,180,-60,180,-60, 900,-60, 180,-60,180,-60,180,-60,180,-60,900,-60), Pair(4,4)),
    JINGLE_BELLS_5(RhythmType.QUOTE,"Jingle Bells 5",listOf(180,-60,180,-60,180,-60,180,-60,780,-60,60,-60, 180,-60,180,-60,180,-60,180,-60,660,-60,60,-60,60,-60, 180,-60,180,-60,180,-60,180,-60,180,-60,180,-60,180,-60,60,-60,60,-60,180,-60,180,-60,180,-60,180,-60,480,420,-60), Pair(4,4)),
    SOS(RhythmType.QUOTE,"SOS",listOf(90, -30,90, -30,90, -30,-120, 180,-60,180,-60,180,-300, 90, -30,90, -30,90, -630), Pair(5,4)),
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
    BULGARIAN1H(RhythmType.BULGARIAN,"Bulgarian1 4+2+3♬",listOf(H1h,N1h,N1dottedH).flatten(),Pair(9,16)),
    BULGARIAN2H(RhythmType.BULGARIAN,"Bulgarian2 2+2+3♬",listOf(N1h,N1h,N1dottedH).flatten(),Pair(7,16)),
    BULGARIAN3H(RhythmType.BULGARIAN,"Bulgarian3 2+3♬",listOf(N1h,N1dottedH).flatten(),Pair(5,16)),
    BULGARIAN4H(RhythmType.BULGARIAN,"Bulgarian4 3+2+3♬",listOf(N1dottedH,N1h,N1dottedH).flatten(),Pair(8,16)),
    BULGARIAN5H(RhythmType.BULGARIAN,"Bulgarian5 2+2+2+3♬",listOf(N1h,N1h,N1h,N1dottedH).flatten(),Pair(9,16)),
    BULGARIAN6H(RhythmType.BULGARIAN,"Bulgarian6 3+3+2♬",listOf(N1dottedH,N1dottedH,N1h).flatten(),Pair(8,16)),
    BULGARIAN1GRZH(RhythmType.BULGARIAN,"Bulg.1 grz. 4+2+3♬",listOf(Ox4grzH,Ox2grzH,Ox3grzH).flatten(),Pair(9,16)),
    BULGARIAN2GRZH(RhythmType.BULGARIAN,"Bulg.2 grz. 2+2+3♬",listOf(Ox2grzH,Ox2grzH,Ox3grzH).flatten(),Pair(7,16)),
    BULGARIAN3GRZH(RhythmType.BULGARIAN,"Bulg.3 grz. 2+3♬",listOf(Ox2grzH,Ox3grzH).flatten(),Pair(5,16)),
    BULGARIAN4GRZH(RhythmType.BULGARIAN,"Bulg.4 grz. 3+2+3♬",listOf(Ox3grzH,Ox2grzH,Ox3grzH).flatten(),Pair(8,16)),
    BULGARIAN5GRZH(RhythmType.BULGARIAN,"Bulg.5 grz. 2+2+2+3♬",listOf(Ox2grzH,Ox2grzH,Ox2grzH,Ox3grzH).flatten(),Pair(9,16)),
    BULGARIAN6GRZH(RhythmType.BULGARIAN,"Bulg.6 grz. 3+3+2♬",listOf(Ox3grzH,Ox3grzH,Ox2grzH).flatten(),Pair(8,16)),

    HEMIOLIA32(RhythmType.HEMIOLIA,"Hemiolia 3=2", listOf(120,60,60,120),Pair(3,16)),
    HEMIOLIA43(RhythmType.HEMIOLIA,"Hemiolia 4=3", listOf(180,60,120,120,60,180),Pair(3,8)),
    HEMIOLIA54(RhythmType.HEMIOLIA,"Hemiolia 5=4", listOf(240,60,180,120,120,180,60,240),Pair(5,8)),
    HEMIOLIA65(RhythmType.HEMIOLIA,"Hemiolia 6=5", listOf(600,120,480,240,360,360, 240,480,120,600),Pair(6,16)),

    FLUX_3454(RhythmType.FLUX,"Flux 3454",listOf(N3,N4,N5,N4).flatten()),
    FLUX_345654(RhythmType.FLUX,"Flux 345654",listOf(N3,N4,N5, N6,N5,N4).flatten(),Pair(3,4)),
    FLUX_34567654(RhythmType.FLUX,"Flux 34567654",listOf(N3,N4,N5,N6,N7,N6,N5,N4).flatten()),
    FLUX_3456787654(RhythmType.FLUX,"Flux 3456787654",listOf(N3,N4,N5, N6,N7,N8, N7,N6,N5,N4).flatten(),Pair(2,4)),
    FLUX_2H34H56H78H9TH98H76H54H3(RhythmType.FLUX,"Flux 2h34h56h78h9Th 98h76h54h3",
        listOf(listOf(240),N3,N4h,N5 ,N6h,N7,N8h,N9 ,N10h,N9,N8h,N7 ,N6h,N5,N4h,N3).flatten() ,Pair(3,4)),
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
    fun barDuration(): Int {
        return metro.first * RhythmPatterns.denominatorMidiValue(metro.second)
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
    fun isIntegrityOk(): Boolean {
        return this.patternDuration() % this.barDuration() == 0
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
        fun createQuarterMetroFromDuration(duration: Int): Pair<Int, Int>{
            if(duration <= 0) return 4 to 4
            val numerator = duration / 480
            val rest = if(duration % 480 == 0) 0 else 1
            return numerator + rest to 4
        }
        fun checkIntegrity(){
            val wrongPatterns = RhythmPatterns.values().filter{ !it.isIntegrityOk()}
            if(wrongPatterns.isEmpty()) println("Rhythm patterns are OK!!!")
            else println("WARNING!!! Damages in patterns: $wrongPatterns")
        }
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
                128 -> 15
                else -> 480
            }
        }
        fun getTitles(): List<String> {
            return values().map { it.title }
        }

        fun mergeSequenceOfOnesInMetro(duration: Int, metro: Pair<Int, Int>): Pair<Int, Int> {
            val denominatorMIDI = denominatorMidiValue(metro.second)
            val numerator = duration / denominatorMIDI
            return Pair(numerator, metro.second)
        }
    }
}
fun List<Int>.mergeNegativeValues(): List<Int> {
    val result = mutableListOf<Int>()
    var index = 0
    var previousIsNegative = false
    this.forEach {
        if(it < 0) {
            if (previousIsNegative) {
                result[result.size - 1] = it + result.last()
            } else {
                result.add(it)
            }
            previousIsNegative = true
        } else {
            result.add(it)
            previousIsNegative = false
        }
    }
    return result.toList()
}
//fun main(args : Array<String>){
//    println("${listOf(-20,100,-40,-30,-5,60,-80,-10,90,-90).mergeNegativeValues()}")
//    println("${listOf(-20,-40,200, 100,-40,-30,-5,60,-80,-10,90,5,-90,-40).mergeNegativeValues()}")
//    println("${listOf(-20,-40,200, 100,-40,-30,-5,60,-80,-10,-7,90,5,-15,80,-90,-40).mergeNegativeValues()}")
//    println("${RhythmPatterns.STACCATO_2_8_R8.values}  +  ${RhythmPatterns.MAPLE_LEAF_1.values}")
//    println("${(RhythmPatterns.STACCATO_2_8_R8.values
//            + RhythmPatterns.MAPLE_LEAF_1.values).mergeNegativeValues()}")
////    println("${RhythmPatterns.GRAZIOSETTO_2_4_R16_8T.values}  ->  ${RhythmPatterns.GRAZIOSETTO_2_4_R16_8T.retrogradeValues()}")
////    println("${RhythmPatterns.DOTTED3_2_4.values}  ->  ${RhythmPatterns.DOTTED3_2_4.retrogradeValues()}")
////    println("${RhythmPatterns.BULGARIAN3GRZ.retrogradeValues() + RhythmPatterns.BULGARIAN3GRZ.values}")
//}
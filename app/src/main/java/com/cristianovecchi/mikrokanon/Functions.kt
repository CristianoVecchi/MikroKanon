package com.cristianovecchi.mikrokanon

import android.content.res.Resources
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import com.cristianovecchi.mikrokanon.AIMUSIC.Clip
import kotlinx.coroutines.*
import kotlinx.coroutines.GlobalScope.coroutineContext
import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.stream.Stream
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext
import kotlin.math.absoluteValue

fun tritoneSubstitution(absPitch: Int): Int {
    return when (absPitch) {
        1 -> 7
        3 -> 9
        5 -> 11
        7 -> 1
        9 -> 3
        11 -> 5
        else -> absPitch
    }
}
fun tritoneSubstitutionOnIntervalSet(intervalSet: List<Int>): List<Int> {

    intervalSet.toMutableList().apply {
        if (containsAll(listOf(1, 11, 5, 7))) return intervalSet

        if (containsAll(listOf(1, 11))) {
            removeAll(listOf(1, 11))
            return also { addAll(listOf(5, 7)) }.toList()
        }
        if (containsAll(listOf(5, 7))) {
            removeAll(listOf(5, 7))
            return also { addAll(listOf(1, 11)) }.toList()
        }
    }
    return intervalSet
}



fun convertGlissandoFlags(glissandoFlags: Int): List<Int>{
    val intervals = listOf( 1, -1, 2, -2, 3, -3)
    return convertFlagsToInts(glissandoFlags).map { intervals[it-1] }
}
fun convertIntsToFlags(ints: Set<Int>): Int{
    var flags = 0
    ints.forEach{ flags = 1 shl it or flags }
    return flags
}
fun convertFlagsToInts(flags: Int): Set<Int>{
    val result = mutableSetOf<Int>()
    for (i in 0..24) {
        if (1 shl i and flags > 0) {
            result.add(i)
        }
    }
    return result.toSet()
}
fun ArrayList<Clip>.toStringAll(notesNames: List<String>, zodiacSigns: Boolean, emoji: Boolean): String {
    return if (this.isNotEmpty()) {
        if(zodiacSigns){
            this.map { clip -> clip.findZodiacSign(emoji) }.reduce { acc, string -> "$acc $string" }
        } else {
            this.map { clip -> clip.findText(notesNames = notesNames) }.reduce { acc, string -> "$acc $string" }
        }

    } else {
        "empty Sequence"
    }
}
fun ArrayList<Clip>.toAbsPitches(): List<Int> {
    return this.map { it.abstractNote }
}
fun List<Int>.toSequence(): ArrayList<Clip>{
    return ArrayList<Clip>( this.mapIndexed{index, pitch -> Clip(index,pitch)} ) // not complete
}
fun MutableList<Int>.getIntOrEmptyValue(index: Int): Int {
    return if(index < this.size) this[index] else -1
}

operator fun IntRange.rangeTo(nextRange: IntRange) = IntRange(this.first, nextRange.last)

fun IntRange.extractFromMiddle(halfRange: Int): IntRange {
    val middle  = (this.first + this.last) / 2
    return IntRange(middle - halfRange, middle + halfRange)
}

    fun createIntervalSetFromFlags(flags: Int): List<Int>{
        val result = mutableSetOf<Int>()
        if(flags and 1 > 0) result.addAll(listOf(1,11))
        if(flags and 0b10 > 0) result.addAll(listOf(2,10))
        if(flags and 0b100 > 0) result.addAll(listOf(3,9))
        if(flags and 0b1000 > 0) result.addAll(listOf(4,8))
        if(flags and 0b10000 > 0) result.addAll(listOf(5,7))
        if(flags and 0b100000 > 0) result.add(6)
        if(flags and 0b1000000 > 0) result.add(0)
        return result.toList()
    }

    fun createFlagsFromIntervalSet(intervalSet: List<Int>): Int{
        var flags = 0
        intervalSet.apply{
            if(this.containsAll(listOf(1,11))) flags = flags or 1
            if(this.containsAll(listOf(2,10))) flags = flags or 0b10
            if(this.containsAll(listOf(3,9))) flags = flags or 0b100
            if(this.containsAll(listOf(4,8))) flags = flags or 0b1000
            if(this.containsAll(listOf(5,7))) flags = flags or 0b10000
            if(this.contains(6))flags = flags or 0b100000
            if(this.contains(0)) flags = flags or 0b1000000
        }
        return flags
    }

    //TODO: implement in CounterpointInterpreter
    suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
        map { async() { f(it) } }.awaitAll()
    }
    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    fun <A, B> Iterable<A>.mapIf(condition: Boolean, f: (A) -> B): List<B> =
        map { (if(condition) f(it) else it) as B }

    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    suspend fun <A, B> Iterable<A>.pmapIf(condition: Boolean, f: suspend (A) -> B): List<B> = coroutineScope {
        map { async{(if (condition) f(it) else it) as B} }.awaitAll()
    }

    fun <T, R> Iterable<T>.tmap(
        numThreads: Int = Runtime.getRuntime().availableProcessors() - 2,
        exec: ExecutorService = Executors.newFixedThreadPool(numThreads),
        transform: (T) -> R): List<R> {

        // default size is just an inlined version of kotlin.collections.collectionSizeOrDefault
        val defaultSize = if (this is Collection<*>) this.size else 10
        val destination = Collections.synchronizedList(ArrayList<R>(defaultSize))

        for (item in this) {
            exec.submit { destination.add(transform(item)) }
        }

        exec.shutdown()
        exec.awaitTermination(1, TimeUnit.DAYS)

        return ArrayList<R>(destination)
    }

    fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    fun Float.projectTo(goal: Float, step: Float): List<Float>{
        return listOf(this).projectTo(goal, step)
    }
    fun List<Float>.projectTo(goal: Float, step: Float, deltas: MutableList<Long>? = null, sectionDuration: Long = 0L): List<Float>{
        val absoluteGoal = goal.absoluteValue
        if(this.isEmpty()) return listOf(absoluteGoal)
        val result = this.toMutableList()
        var newValue = this.last()

        if (newValue == absoluteGoal) {
            deltas?.let {
                //if (deltas.isNotEmpty() && deltas.last() == -1L) deltas.removeAt(deltas.size-1)
                deltas.add(sectionDuration)
            }
            result.add(absoluteGoal)
            return result.toList()
        }

        if (goal < 0) {
            result[result.size-1] = absoluteGoal
            //deltas?.add(-1L)
            return result.toList()
        }
        var count = 1
        if(newValue > absoluteGoal){
            newValue -= step
            while (newValue > absoluteGoal){
                count ++
                result.add(newValue)
                newValue -= step
            }
            result.add(absoluteGoal)

        } else {
            newValue += step
            while (newValue < absoluteGoal){
                count ++
                result.add(newValue)
                newValue += step
            }
            result.add(absoluteGoal)

        }

        deltas?.let{
            val delta = sectionDuration / count
            val rest = sectionDuration - (delta * count) // module operation without decimals
            println("section duration:$sectionDuration  count:$count  delta:$delta  rest:$rest")
            //if (deltas.isNotEmpty() && deltas.last() == -1L) deltas[deltas.size-1] = delta + rest else
            it.addAll(sectionDuration.divideDistributingRest(count))
        }
        return result.toList()
    }
fun alterateBpm(bpmValues: List<Float>, step:Float): List<Float>{
    return bpmValues.fold(listOf()) {acc, nextBpm -> acc.projectTo(nextBpm, step)}
}
fun alterateBpmWithDistribution(bpmValues: List<Float>, step:Float, totalDuration: Long): Pair<List<Float>, List<Long>>{
    //println("values: $bpmValues")
    if(bpmValues.size == 1){
        return Pair(listOf(bpmValues[0],0f), listOf(totalDuration))
    }
    val sectionDuration = totalDuration / (bpmValues.count{it >= 0} - 1)
    //println("total: $totalDuration | section: $sectionDuration")
    val deltas: MutableList<Long> = mutableListOf()
    val bpms: List<Float> = bpmValues.fold(listOf()) {
            acc, nextBpm -> acc.projectTo(nextBpm, step, deltas, sectionDuration)}
    //deltas.add(0) // to set the same lenght - last value is unused
    return Pair(bpms, deltas.toList() )
}



fun String.extractIntsFromCsv(): List<Int>{
    return this.split(',').mapNotNull { it.toInt()}
}
fun String.extractFloatsFromCsv(): List<Float>{
    return this.split(',').mapNotNull { it.toFloat() }
}
fun String.describe(): String {
    val ints = this.extractIntsFromCsv()
    return ints.foldIndexed("") { index, acc, i ->
        when {
            index == 0 -> acc + i.absoluteValue.toString()
            i < 0 -> "$acc | ${i.absoluteValue}"
            ints[index - 1].absoluteValue > i.absoluteValue -> "$acc ➘ $i"
            ints[index - 1].absoluteValue < i.absoluteValue -> "$acc ➚ $i"
            ints[index - 1].absoluteValue == i.absoluteValue -> "$acc - $i"
            else -> acc + i.toString()
        }
    }
}
fun String.describeForDynamic(map: Map<Float, String>, ascendingSymbol: String, descendingSymbol: String) : String {
    val floats = this.extractFloatsFromCsv()
    return floats.foldIndexed("") { index, acc, i ->
        when {
            index == 0 -> acc + map[i.absoluteValue]!!
            i < 0 -> "$acc | ${map[i.absoluteValue]!!}"
            floats[index - 1].absoluteValue > i.absoluteValue -> "$acc $descendingSymbol ${map[i]!!}"
            floats[index - 1].absoluteValue < i.absoluteValue -> "$acc $ascendingSymbol ${map[i]!!}"
            floats[index - 1].absoluteValue == i.absoluteValue -> "$acc - ${map[i]!!}"
            else -> acc + i.toString()
        }
    }
}
fun String.describeForTranspose(intervals: List<String>): String {
    val ints = this.extractIntsFromCsv()
    return ints.map{ intervals[it] }.joinToString(", ")
}
fun correctBpms(bpms: String): String{
    val result = bpms.extractIntsFromCsv().toMutableList()
    if (result.all{ it.absoluteValue == result[0].absoluteValue}) return result[0].absoluteValue.toString()
    repeat(2){
        if (result[0] < 0) result[0] = result[0].absoluteValue
        if (result.size > 1){
            if (result[1] < 0)  result.add(1, result[0])
            (1 until result.size).forEach{ index ->
                if (result[index-1].absoluteValue == result[index].absoluteValue) result[index] = result[index].absoluteValue
            }
            (1 until result.size - 1).forEach{ index ->
                if (result[index] < 0 && result[index+1] < 0 ) result.add(index+1, result[index].absoluteValue)
            }
            if (result.last() < 0) result.add(result.last().absoluteValue)
        }
    }
    return result.joinToString(",")
}
fun correctDynamics(dynamics: String): String{
    val result = dynamics.extractFloatsFromCsv().toMutableList()
    if (result.all{ it.absoluteValue == result[0].absoluteValue}) return result[0].absoluteValue.toString()
    repeat(2){
        if (result[0] < 0f) result[0] = result[0].absoluteValue
        if (result.size > 1){
            if (result[1] < 0f)  result.add(1, result[0])
            (1 until result.size).forEach{ index ->
                if (result[index-1].absoluteValue == result[index].absoluteValue) result[index] = result[index].absoluteValue
            }
            (1 until result.size - 1).forEach{ index ->
                if (result[index] < 0f && result[index+1] < 0f ) result.add(index+1, result[index].absoluteValue)
            }
            if (result.last() < 0f) result.add(result.last().absoluteValue)
        }
    }
    return result.joinToString(",")
}
fun String.valueFromCsv(index: Int): Int {
    return this.extractIntsFromCsv()[index]
}
fun Color.toHexString(): String {
    return "#${this.red.toColorHexString()}${this.green.toColorHexString()}${this.blue.toColorHexString()}"
}

fun Float.toColorHexString(): String {
    return (256 * this).toInt().toString(16)
}

// bytes = first the low 7 bits, second the high 7 bits - volume is from 0x0000 to 0x3FFF
fun Float.convertDynamicToBytes(): Pair<Int, Int> {
    val fl = this.coerceIn(0f,1f)
    val volumeInt = (0x3FFF * fl).toInt()
    val firstBite = volumeInt and 0b1111111
    val secondByte = (volumeInt shr(7)) and 0b1111111
    return Pair(firstBite, secondByte)
}

fun Long.divideDistributingRest(divisor: Int): MutableList<Long>{
    if(this == 0L || divisor == 0) return mutableListOf(this)
    val result = this / divisor
    val rest = this - (result * divisor) // module operation without decimals
    val halfDivisor = divisor / 2 + (divisor and 1)
    val restDiv = rest / halfDivisor
    val restOfRest = rest - (restDiv * halfDivisor )
    val addOne = rest % halfDivisor != 0L
    val list = mutableListOf<Long>()
    val resultPlusRestDiv = result + restDiv
    //println("result=$result rest=$rest restDiv=$restDiv restOfRest=$restOfRest")
    (0 until divisor).forEach{ i -> if(i and 1 == 1) list.add(result) else list.add(resultPlusRestDiv) }
    (0 until restOfRest.toInt()).forEach{ i -> list[i*2+1] = list[i*2+1]+1}
    //if(addOne) list[0] = list[0] + 1
    return list
}
fun main(args : Array<String>){
    val pairs = listOf(
        Pair(836L,127),
        Pair(500L,78),
        Pair(343L,45),
        Pair(1947L,37),
        Pair(12L,127),
        Pair(689L,78),
        Pair(100L,45),
        Pair(4L,3),
    )
    pairs.forEach {
        println("${it.first} / ${it.second} -> ${it.first.divideDistributingRest(it.second)}")
        println("check sum: ${it.first.divideDistributingRest(it.second).sum()}")
        println()
    }
    var success = true
    for(i in 0..100){
        val pair = Pair(Random().nextInt(10000).toLong(), Random().nextInt(10000)).also{println(it)}
        val list = pair.first.divideDistributingRest(pair.second)
        println(list)
        println()
        if(list.sum() != pair.first){
            println("TEST FAILED with: $pair")
            success = false
        }
    }
    if(success){
        println("SUCCESS!!!")
    }
}









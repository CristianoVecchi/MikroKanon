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
        if(this.isEmpty()) return listOf(goal)
        val result = this.toMutableList()
        var newValue = this.last()
        if (newValue == goal) {
            deltas?.add(sectionDuration)
            result.add(goal)
            return result.toList()
        }
        var count = 0
        if(newValue > goal){
            newValue -= step
            while (newValue > goal){
                count ++
                result.add(newValue)
                newValue -= step
            }
            result.add(goal)
            count ++
        } else {
            newValue += step
            while (newValue < goal){
                count ++
                result.add(newValue)
                newValue += step
            }
            result.add(goal)
            count ++
        }

        deltas?.let{
            val delta = sectionDuration / count
            (0 until count).forEach{ _ -> deltas.add(delta)}
        }
        return result.toList()
    }
fun alterateBpm(bpmValues: List<Float>, step:Float): List<Float>{
    return bpmValues.fold(listOf()) {acc, nextBpm -> acc.projectTo(nextBpm, step)}
}
fun alterateBpmWithDistribution(bpmValues: List<Float>, step:Float, totalDuration: Long): Pair<List<Float>, List<Long>>{
    val sectionDuration = totalDuration / bpmValues.size - 1
    val deltas: MutableList<Long> = mutableListOf()
    val bpms: List<Float> = bpmValues.fold(listOf()) {
            acc, nextBpm -> acc.projectTo(nextBpm, step, deltas, sectionDuration)}
    deltas.add(0) // to set the same lenght - last value is unused
    return Pair(bpms, deltas.toList() )
}

fun String.extractFromCsv(): List<Int>{
    return this.split(',').mapNotNull { it.toInt()}
}
fun String.describe(): String {
    val ints = this.extractFromCsv()
    return ints.foldIndexed("") { index, acc, i ->
        when {
            index == 0 -> acc + i.toString()
            ints[index - 1] > i -> "$acc ➘ $i"
            ints[index - 1] < i -> "$acc ➚ $i"
            ints[index - 1] == i -> "$acc - $i"
            else -> acc + i.toString()
        }
    }
}
fun String.valueFromCsv(index: Int): Int {
    return this.extractFromCsv()[index]
}
fun Color.toHexString(): String {
    return "#${this.red.toColorHexString()}${this.green.toColorHexString()}${this.blue.toColorHexString()}"
}

fun Float.toColorHexString(): String {
    return (256 * this).toInt().toString(16)
}








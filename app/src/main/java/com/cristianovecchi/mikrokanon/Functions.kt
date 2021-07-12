package com.cristianovecchi.mikrokanon

import android.os.Build
import androidx.annotation.RequiresApi
import com.cristianovecchi.mikrokanon.AIMUSIC.Clip
import kotlinx.coroutines.*
import kotlinx.coroutines.GlobalScope.coroutineContext
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.stream.Stream
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext


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
fun ArrayList<Clip>.toStringAll(notesNames: List<String>): String {
    return if (this.isNotEmpty()) {
        this.map { clip -> clip.findText(notesNames = notesNames) }.reduce { acc, string -> "$acc $string" }
    } else {
        "empty Sequence"
    }
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








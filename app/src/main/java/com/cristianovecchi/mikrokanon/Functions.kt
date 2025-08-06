package com.cristianovecchi.mikrokanon

import android.content.res.Resources
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.cristianovecchi.mikrokanon.AIMUSIC.ChangeData
import com.cristianovecchi.mikrokanon.AIMUSIC.EnsemblePart
import com.cristianovecchi.mikrokanon.AIMUSIC.Insieme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

//fun main(){
//    val arr = intArrayOf(0, 267, 348, 1024, 4056, 5890, 6056, 6987, 7654, 8751, 10987)
//    val arr2 = intArrayOf()
//    val arr3 = intArrayOf(5678, 10540, 20000)
//    val section1 = listOf(arr, arr2, arr3).findIndicesInSection(0, 1024)
//    val section2 = listOf(arr, arr2, arr3).findIndicesInSection(1024, 10000)
//    val section3 = listOf(arr, arr2, arr3).findIndicesInSection(11024, 10000)
//    println(section1)
//    println(section2)
//    println(section3)
//
//    println("Total section 1: ${section1.nElements()}")
//    println("Total section 2: ${section2.nElements()}")
//    println("Total section 3: ${section3.nElements()}")
//}

public inline operator fun <T> List<T>.component5(): T {
    return get(4)
}
public inline operator fun <T> List<T>.component6(): T {
    return get(5)
}
public inline operator fun <T> List<T>.component7(): T {
    return get(6)
}
public inline operator fun <T> List<T>.component8(): T {
    return get(7)
}

fun List<List<String>>.indexInTotalOf(item: String): Int{
    var partialTotal = 0
    for(i in 0 until this.size){
        val groupSize = this[i].size
        if(groupSize == 0) continue
        val partialIndex = this[i].indexOf(item)
        if(partialIndex != -1) return partialTotal + partialIndex
        partialTotal += groupSize
    }
    return -1
}
fun <E> List<E>.repeatCycling(nElements: Int): List<E>{
    if(this.isEmpty()) return emptyList()
    val result = mutableListOf<E>()
    var listIndex = 0
    while( result.size < nElements) {
        result.add(this[listIndex])
        listIndex++
        if(listIndex == this.size) listIndex = 0
    }
    return result
}
fun <E> List<List<E>>.retrieveByIndexInTotal(indexInTotal: Int): E?{
    if(indexInTotal<0) return null
    var partialTotal = 0
    for(i in 0 until this.size){
        val groupSize = this[i].size
        if(groupSize == 0) continue
        val partialIndex = indexInTotal - partialTotal
        if(partialIndex<groupSize) return this[i][partialIndex]
        partialTotal += groupSize
    }
    return null
}
fun List<List<Any>>.findRanges(): List<IntRange?>{
    var startGroup = 0
    val ranges = mutableListOf<IntRange?>()
    for(i in 0 until this.size){
        val groupSize = this[i].size
        if(groupSize == 0) {
            ranges += null
        } else {
            val nextStart = startGroup + groupSize
            ranges += IntRange(startGroup, nextStart-1)
            startGroup = nextStart
        }
    }
    return ranges//.also{ println("Ranges: $it")}
}
fun List<List<Any>>.findGroupIndex(itemIndex: Int): Int? {
    val ranges = this.findRanges()
    //println("Search item index:$itemIndex in $ranges")
    for(i in 0 until ranges.size){
        ranges[i]?.let{
            if(it.contains(itemIndex)) {
               // println("Item found in group: $i")
                return i
            }
        }
    }
    //println("Search item index:$itemIndex in $ranges")
    return null//.also{
       // println("Search item index: Null")
    //}
}
fun List<IntRange?>.nElements(): Int{
    return this.filterNotNull().sumOf { it.count() }
}
fun List<IntArray>.findIndicesInSection(sectionStart:Int, sectionDuration:Int, durations: List<IntArray>? = null): List<IntRange?>{
    val result = mutableListOf<IntRange?>()
    this.forEachIndexed{ arrayIndex , it ->
        var start = -1

        if(durations != null) {
            val durs = durations[arrayIndex]
            for(i in it.indices){
                val startTick = it[i]
                if(startTick >= sectionStart || startTick + durs[i] > sectionStart){
                    start = i
                    break
                }
            }
        } else {
            for(i in it.indices){
                if(it[i] >= sectionStart){
                    start = i
                    break
                }
            }
        }

        if(start > -1) {
            var end = -1
            val sectionLimit = sectionStart + sectionDuration
            for(j in it.size - 1 downTo start) {
                if(it[j] < sectionLimit) {
                    end = j
                    break
                }
            }
            if(end != -1){
                result += IntRange(start, end)
            } else {
                result += null
            }
        } else {
            result += null
        }
    }
    return result
}
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
    val intervals = listOf( 1, -1, 2, -2, 3, -3, 4,-4, 5,-5,6,-6,7,-7,8,-8,9,-9,10,-10,11,-11,12,-12)
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
fun convertDodecabyteToInts(flags: Int): Set<Int>{
    val result = mutableSetOf<Int>()
    for (i in 0..11) {
        if (1 shl i and flags > 0) {
            result.add(i)
        }
    }
    return result.toSet()
}
fun SnapshotStateMap<Int,Int>.removeAndScale(index: Int){
    if(this.containsKey(index)) this.remove(index)
    this.keys.sorted().map{ if(it > index) {this[it-1]= this[it]!!; this.remove(it)}}
}
fun SnapshotStateMap<Int,Int>.insertAndScale(index: Int, value: Int){
    this.keys.sortedDescending().map{ if(it >= index) {this[it+1]= this[it]!!; this.remove(it)} }
    this[index] = value
}
fun SnapshotStateMap<Int,Int>.swap(fromIndex: Int, toIndex: Int){
    val value1 = this[fromIndex]
    val value2 = this[toIndex]
    remove(fromIndex); remove(toIndex)
    value1?.let{this[toIndex] = it}
    value2?.let{this[fromIndex] = it}
}
fun MutableList<List<Int>>.swap(fromIndex: Int, toIndex: Int, oldIndex: Int = fromIndex): Int{
    return if(fromIndex in this.indices && toIndex in this.indices){
        val swap = this[fromIndex].toList()
        this[fromIndex] = this[toIndex].toList()
        this[toIndex] = swap
        toIndex // newIndex
    } else {
        oldIndex.coerceIn(0, this.size -1)
    }
}
fun <E> List<E>.repeat(nTimes: Int): List<E> {
    if (nTimes < 2) return this
    val result = mutableListOf<E>()
    (0 until nTimes).forEach { _ ->
        result.addAll(this)
    }
    return result.toList()
}
fun <E> List<E>.cutAdjacentRepetitions(): List<E> {
    val size = this.size
    if(size < 2) return this
    val result = mutableListOf<E>()
    for (i in 0 until size-1){
        val element = this[i]
        if(element != this[i+1]) result.add(element)
    }
    result.add(this[size-1])
    return  result.toList()
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
        return result.toList().sorted()
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
        //println("$intervalSet -> ${Integer.toBinaryString(flags)}")
        return flags
    }

    //TODO: implement in CounterpointInterpreter
    suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
        map { async { f(it) } }.awaitAll()
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

        deltas?.addAll(sectionDuration.divideDistributingRest(count))
        return result.toList()
    }
fun alterateBpm(bpmValues: List<Float>, step:Float): List<Float>{
    return bpmValues.fold(listOf()) {acc, nextBpm -> acc.projectTo(nextBpm, step)}
}
data class LegatosData(val legatos: List<Float>, val deltas: List<Long>, val pivots: List<Int>)
fun alterateLegatosWithDistribution(legatoValues: List<Float>, ribattutos: List<Int>,
                                    step:Float, totalDuration: Long): LegatosData {
    //println("values: $bpmValues")
    if(legatoValues.size == 1){
        return LegatosData(listOf(legatoValues[0],0f), listOf(totalDuration), listOf(0,1))
    }
    val sectionDuration = totalDuration / (legatoValues.count{it >= 0} - 1)
    //println("total: $totalDuration | section: $sectionDuration")
    val deltas: MutableList<Long> = mutableListOf()
   // val ribattutoAlterations = mutableListOf<Int>()
//    var lastSize = 0
//    var ribIndex = 0
//    val firstIsStatic = legatoValues[0] == legatoValues[1]
//    var actualRibattutos = ribattutos.filterIndexed { index, _ -> legatoValues[index] >= 0  }
//    actualRibattutos = if(firstIsStatic) actualRibattutos.drop(1) else actualRibattutos
    //println("actual ribattutos: $actualRibattutos")
    val pivots = mutableListOf<Int>()
    val legatos = legatoValues.foldIndexed<Float, List<Float>>(listOf()) {
            index, acc, nextLegato ->
        acc.projectTo(nextLegato, step, deltas, sectionDuration).apply {
            pivots.add(this.size - 1)
//            val newSize = this.size
//            if (!(index == 0 && firstIsStatic)){
//                if (nextLegato >= 0) {
//                    //println("last size = $lastSize  new size = $newSize $this")
//                    ribattutoAlterations.addAll(List(newSize - lastSize -1) { actualRibattutos[ribIndex] })
//                    lastSize = newSize - 1
//                    ribIndex++
//                }
//            }
        }
    }
//    println("pivots: $pivots")
//    println("legatos: $legatos")
    deltas.add(0) // to set the same lenght - last value is unused
//    ribattutoAlterations.add(1)
    return LegatosData(legatos, deltas.toList(), pivots.toList())
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
    deltas.add(0) // to set the same lenght - last value is unused
    return Pair(bpms, deltas.toList() )
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
    //val addOne = rest % halfDivisor != 0L
    val list = mutableListOf<Long>()
    val resultPlusRestDiv = result + restDiv
    val add = if(restOfRest < 0) -1 else 1
    //println("result=$result rest=$rest restDiv=$restDiv restOfRest=$restOfRest")
    (0 until divisor).forEach{ i -> if(i and 1 == 1) list.add(result) else list.add(resultPlusRestDiv) }
    (0 until restOfRest.toInt().absoluteValue).forEach{ i ->
        val index = i*2+1
        list[index] = list[index]+add
    }
    //if(addOne) list[0] = list[0] + 1
    return list
}
fun Int.divideDistributingRest(divisor: Int): MutableList<Int>{
    if(this == 0 || divisor == 0) return mutableListOf(this)
    val result = this / divisor
    val rest = this - (result * divisor) // module operation without decimals
    val halfDivisor = divisor / 2 + (divisor and 1)
    val restDiv = rest / halfDivisor
    val restOfRest = rest - (restDiv * halfDivisor )
    //val addOne = rest % halfDivisor != 0L
    val list = mutableListOf<Int>()
    val resultPlusRestDiv = result + restDiv
    val add = if(restOfRest < 0) -1 else 1
    //println("result=$result rest=$rest restDiv=$restDiv restOfRest=$restOfRest")
    (0 until divisor).forEach{ i -> if(i and 1 == 1) list.add(result) else list.add(resultPlusRestDiv) }
    (0 until restOfRest.absoluteValue).forEach{ i ->
        val index = i*2+1
        list[index] = list[index]+add
    }
    //if(addOne) list[0] = list[0] + 1
    return list
}



fun combineRangesAndEnsembleParts(rangeTypes: List<Pair<Int,Int>>, ensemblePartList: List<EnsemblePart>): List<Pair<Pair<Int,Int>, EnsemblePart>>{
    val result = mutableListOf<Pair<Pair<Int,Int>, EnsemblePart>>()
    val size = rangeTypes.size * ensemblePartList.size
    val rangeTypesTicks = size.toLong().divideDistributingRest(rangeTypes.size).sums()
    val ensemblePartsTicks = size.toLong().divideDistributingRest(ensemblePartList.size).sums()
    val allTicks = (rangeTypesTicks + ensemblePartsTicks).toSet().sorted()
    var rangesIndex = 0
    var ensemblesIndex = 0
    allTicks.forEach{
        when {
            rangeTypesTicks.contains(it) && ensemblePartsTicks.contains(it) -> {
                result.add(Pair(rangeTypes[rangesIndex], ensemblePartList[ensemblesIndex]))
                rangesIndex++; ensemblesIndex++
            }
            rangeTypesTicks.contains(it) -> {
                result.add(Pair(rangeTypes[rangesIndex], ensemblePartList[ensemblesIndex]))
                rangesIndex++
            }
            ensemblePartsTicks.contains(it) -> {
                result.add(Pair(rangeTypes[rangesIndex], ensemblePartList[ensemblesIndex]))
                ensemblesIndex++
            }
        }
    }
    return result.toList()
}

fun findMelodyWithStructure(
    octave: Int,
    absPitches: IntArray,
    lowerLimits: IntArray,
    upperLimits: IntArray,
    melodyTypes: IntArray,
    ensembleParts: List<EnsemblePart>
): Pair<IntArray, List<ChangeData>> {// :Pair<IntArray, List<ChangeData>> <- n of note when instrument changes and n of instrument

    val melTypes = mutableListOf<Int>()
    val lowLimits = mutableListOf<Int>()
    val upLimits = mutableListOf<Int>()
    val nNoteGroups = mutableListOf<Int>()
    val nEnsembles = mutableListOf<EnsemblePart>()
    val melTicks = absPitches.size.toLong().divideDistributingRest(melodyTypes.size).sums()//.also{ println("melTicks: $it")}
    val rangeTicks = absPitches.size.toLong().divideDistributingRest(lowerLimits.size).sums()//.also{ println("rangeTicks: $it")}
    val allTicks = (melTicks + rangeTicks).toSet().sorted()
    var lastTick = 0
    var melIndex = 0
    var lowIndex = 0 // index of ranges

    allTicks.forEachIndexed{ index, it ->
        when{
            melTicks.contains(it) && rangeTicks.contains(it) -> {
                melTypes.add(melodyTypes[melIndex])
                lowLimits.add(lowerLimits[lowIndex])
                upLimits.add(upperLimits[lowIndex]) // lowers and uppers share the same index
                nEnsembles.add(ensembleParts[lowIndex])
                nNoteGroups.add(it.toInt() - lastTick)
                lastTick = it.toInt()
                melIndex++
                lowIndex++
            }
            melTicks.contains(it) -> {
                melTypes.add(melodyTypes[melIndex])
                lowLimits.add(lowerLimits[lowIndex])
                upLimits.add(upperLimits[lowIndex]) // lowers and uppers share the same index
                nEnsembles.add(ensembleParts[lowIndex])
                nNoteGroups.add(it.toInt() - lastTick)
                lastTick = it.toInt()
                melIndex++
            }
            rangeTicks.contains(it) -> {
                melTypes.add(melodyTypes[melIndex])
                lowLimits.add(lowerLimits[lowIndex])
                upLimits.add(upperLimits[lowIndex]) // lowers and uppers share the same index
                nEnsembles.add(ensembleParts[lowIndex])
                nNoteGroups.add(it.toInt() - lastTick)
                lastTick = it.toInt()
                lowIndex++
            }
        }
    }
//    println("melTypes ${melTypes.size}: $melTypes")
//    println("lowLimits ${lowLimits.size}: $lowLimits")
//    println("upLimits ${upLimits.size}: $upLimits")
//    println("durs ${durs.size}: $durs")

    var lastOctave = octave
    var lastTick2 = 0
    var lastInstrument = Int.MIN_VALUE
    val changes = mutableListOf<ChangeData>()

    val sequences = nNoteGroups.mapIndexed{ index, nNotes ->
        val subSequence = absPitches.copyOfRange(lastTick2, lastTick2 + nNotes)//.also{println("subSequence $index: ${it.contentToString()}")}
        val sequence = Insieme.findMelody(lastOctave, subSequence,
                lowLimits[index], upLimits[index], melTypes[index])
        lastOctave = sequence.lastOrNull { it != -1 }?.let{ last -> last / 12 -1} ?: lastOctave // sequence could be empty
        val newInstrument = nEnsembles[index].instrument
        if(newInstrument != lastInstrument){
            changes.add(ChangeData(lastTick2, newInstrument))
            lastInstrument = newInstrument
        }
        lastTick2 += nNotes
        sequence
    }
//    sequences.forEach{println("subSequence: ${it.contentToString()}")}
    return Pair(sequences.reduce{ acc, arr -> acc + arr}, changes.toList())//.also{println("Result sequence: ${it}")}
}

fun List<Long>.sums(start: Long = 0): List<Long>{
    var last = start
    return this.map{ last += it; last }
}
fun List<Int>.sums(start: Int = 0): List<Int>{
    var last = start
    return this.map{ last += it; last }
}

fun <E> List<E>.addOrInsert(newItem: E, cursor: Int): Pair<List<E>,Int> {
    val mutableList = this.toMutableList()
    return when {
        this.isEmpty() -> {
            mutableList.add(newItem)
            Pair(mutableList, 0)
        }
        cursor == mutableList.size-1 -> {
            mutableList.add(newItem)
            Pair(mutableList, cursor+1)
        }
        else -> {
            mutableList.add(cursor + 1, newItem)
            Pair(mutableList, cursor + 1)
        }
    }
}

fun <E> List<E>.shiftCycling(): List<E> {
    if (this.isEmpty() || this.size == 1) return this
    val result = mutableListOf<E>()
    for(i in 1 until this.size){
        result.add(this[i])
    }
    result.add(this[0])
    return result
}
fun LazyListState.isScrolledToTheEnd() : Boolean {
    val lastItem = layoutInfo.visibleItemsInfo.lastOrNull()
    return lastItem == null || lastItem.size + lastItem.offset <= layoutInfo.viewportEndOffset
}

fun Int.sign(): Int {
    return if(this<0) -1 else 1
}
fun Float.sign(): Float {
    return if(this<0f) -1f else 1f
}














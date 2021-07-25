package com.cristianovecchi.mikrokanon.AIMUSIC

import android.os.Parcelable
import com.cristianovecchi.mikrokanon.AIMUSIC.RowForm.*
import com.cristianovecchi.mikrokanon.composables.NoteNamesEn
import com.cristianovecchi.mikrokanon.getIntOrEmptyValue
import com.cristianovecchi.mikrokanon.tritoneSubstitutionOnIntervalSet
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.math.abs

fun main(args : Array<String>){
//    val pitches = listOf(-1,-1,2,3,8,-1,-1,-1,10,12,46,67,32,64,43,0,1,9,8,-1,-1,8)
//    val mssq = MelodySubSequencer(pitches.toIntArray())
//    mssq.assignVelocities(0.90f, 0.50f)
//    mssq.printSubSequences()
//    val absPitches1 = mutableListOf(-1,1,0,6,11,5,7,8,8,3,3,3,6,9)
//    val absPitches2 = mutableListOf(1,11,2,10,3,-1,9,6)
//    val absPitches3 = mutableListOf(1,11,2,10)
    val absPitches1 = mutableListOf(0, 4, 7, 11, 2, 6, 9)
    val absPitches2 = mutableListOf(-1, 0, 4, 7, 11, 2, 6, 9)
    val absPitches3 = mutableListOf(-1,2, 0, -1, -1, 10, -1, 7, 5, 9, 10, 0, 2)
    val absParts = listOf(
        AbsPart(absPitches1),
        AbsPart(absPitches2),
        AbsPart(absPitches3),
    )
    val pentatonicIntervalSet = listOf(2, 10, 3, 9, 4, 8, 5, 7)
    val counterpoint = Counterpoint(absParts, pentatonicIntervalSet)
    val counterpoint2 = Counterpoint.findWave(counterpoint,pentatonicIntervalSet,3,
        listOf(0,1,2),TREND.ASCENDANT_STATIC.directions)
    val counterpoint3 = Counterpoint.findWave(counterpoint2,pentatonicIntervalSet,6,
        listOf(0,1,2),TREND.ASCENDANT_STATIC.directions).displayInNotes()
    counterpoint.normalizePartsSize(true).display()
    println("emptiness: ${counterpoint.emptiness}")
    val counterpointRound = counterpoint.normalizePartsSize(true).buildRound()
    counterpointRound.display()
    println("emptiness: ${counterpointRound.emptiness}")
    println("emptiness: ${counterpointRound.findEmptiness()}")
//    Counterpoint.expand(counterpoint, 2).display()
//    val repeatedSequence = Collections.nCopies(3, absPitches1).flatten()
//    println(repeatedSequence)
//    val list = listOf(-1,-1,0,10,5,6,11,2,-1,-1,3,-1,10)
//    println(Insieme.linearMelody(4,list.toIntArray(),21,108).asList())
}
@Parcelize
data class Counterpoint(val parts: List<AbsPart>,
                        val intervalSet: List<Int> = (0..11).toList(),
                        var emptiness: Float? = null) : Parcelable {

    init {
        emptiness ?: findEmptiness().also<Float> { it -> emptiness = it }
    }
    fun maxSize(): Int {
        return parts.maxOf { it.absPitches.size }
    }
    fun clone(): Counterpoint {
        return Counterpoint(parts.map{it.clone()}, ArrayList(intervalSet.toList()), emptiness)
    }
    fun cutExtraParts(nPartsLimit: Int): Counterpoint{
        return if(this.parts.size <= nPartsLimit) this
        else this.copy(parts = this.parts.take(nPartsLimit))
    }
    fun getColumnValues(index: Int): List<Int>{
        return parts.filter{index < it.absPitches.size }.map{ it.absPitches[index] }
    }
    fun getColumnValuesWithEmptyValues(index: Int): List<Int>{
        return parts.map{  if(index < it.absPitches.size) it.absPitches[index] else -1 }
    }
    private fun getNextAbsPitch(partIndex: Int, column: Int, maxSize: Int): Int {
        val nextColumn = (column + 1) % maxSize
        return if(nextColumn < parts[partIndex].absPitches.size) parts[partIndex].absPitches[nextColumn] else -1
    }
    fun enqueue(counterpoint: Counterpoint): Counterpoint{
        val newParts = parts.mapIndexed { index, absPart ->
            if(index<counterpoint.parts.size) absPart.enqueue( counterpoint.parts[index]) else absPart }
        return Counterpoint(newParts, intervalSet)
    }
    fun inverse(): Counterpoint {
        val newParts = parts.map{ it.inverse() }
        return Counterpoint(newParts, intervalSet)
    }
    fun retrograde(): Counterpoint {
        val newParts = parts.map{ it.retrograde() }
        return Counterpoint(newParts, intervalSet)
    }
    fun getAbsPitches(): List<List<Int>>{
        return parts.map{ it.absPitches }
    }
    fun addWave(intervalSet: List<Int>, startAbsPitch: Int, steps: List<Int> ): Counterpoint {
        return findWave(this, intervalSet, startAbsPitch, steps )
    }
    fun nNotes(): Int {
        return parts.maxOf { it.absPitches.size }
    }
    fun buildRound(): Counterpoint {
        val nParts = parts.size
        val normalizedCounterpoint = this.normalizePartsSize(false)
        var counterpoint = normalizedCounterpoint.copy()
        if(nParts == 0) return this
        if(nParts == 1) {
            counterpoint = addEmptyPart()
            val newParts: List<AbsPart> = listOf(counterpoint.parts[1].copy(), counterpoint.parts[0].copy())
            return counterpoint.enqueue(counterpoint.copy(parts = newParts))
        }
        (1 until nParts  ).forEach{ count ->
            val newParts: List<AbsPart> = (0 until nParts).map{ partIndex ->
                normalizedCounterpoint.parts[(partIndex + count) % nParts].copy()
            }
            counterpoint = counterpoint.enqueue(counterpoint.copy(parts = newParts))
        }
        return counterpoint
    }
    fun addEmptyPart(): Counterpoint {
        val newParts = listOf(parts, listOf( AbsPart.emptyPart(maxSize()) )).flatten()
        return this.copy(parts = newParts)
    }

    companion object {
        fun createSeparatorCounterpoint(nParts: Int, nNotesToSkip: Int) : Counterpoint{
            val absPart = AbsPart( (0 until nNotesToSkip).map{ -1 }.toMutableList() )
            val newParts = (0 until nParts).map{ absPart }
            return Counterpoint(newParts)
        }
        fun counterpointFromClipList(clipList: List<Clip>) : Counterpoint{
            return Counterpoint(listOf(AbsPart.absPartfromClipList(clipList)))
        }
        fun empty(): Counterpoint{
            return Counterpoint(emptyList(), emptyList(), 1.0f)
        }
        fun flourish(counterpoint: Counterpoint, intervalSet: List<Int>, horIntervalSet: List<Int>): Counterpoint{
            val actualDirections = TREND.ASCENDANT_DYNAMIC.directions.filter{ horIntervalSet.contains(it)}
            val maxSize = counterpoint.maxSize()
            val nParts = counterpoint.parts.size
            val newParts = (0 until nParts).map{ counterpoint.parts[it].copy( absPitches = mutableListOf() ) }
            columns@for (index in 0 until maxSize){
                val fioriture: List<MutableList<Int>> = (0 until nParts).map{ mutableListOf() }
                val absNotes = counterpoint.getColumnValuesWithEmptyValues(index)
                for (partIndex in 0 until nParts) {
                    val absPitches = counterpoint.parts[partIndex].absPitches
                    val startNote = absPitches.getIntOrEmptyValue(index)
                    val otherNotes =
                        absNotes.filterIndexed { i, _ -> i != partIndex }.filter { it != -1 }
                    val fioritura: List<Int> = Insieme.getPossibleAbsPitches(
                        otherNotes.toIntArray(),
                        intervalSet.toIntArray()
                    ).toList()
                    fioriture[partIndex].addAll(fioritura.filter { it != startNote })
                }
                //fioriture.map{ println(it)}
                val bestFiorituraSize = fioriture.filterIndexed{ i, _-> counterpoint.getNextAbsPitch(i, index, maxSize) != -1}
                                                .maxOfOrNull{ it.size } ?: 0
                newParts.mapIndexed { i, absPart -> absPart.absPitches.add(absNotes[i]) }
                if(bestFiorituraSize == 0){
                    continue@columns
                } else {
                    var bestFiorituraIndex: Int = -1
                    for(i in fioriture.indices) {
                        if(fioriture[i].size == bestFiorituraSize && counterpoint.getNextAbsPitch(i, index, maxSize) != -1)
                        {bestFiorituraIndex = i; break }
                    }
                    val targetNote = counterpoint.getNextAbsPitch(bestFiorituraIndex, index, maxSize)
                    val fiorituraNotes: List<Int> = Insieme.orderAbsPitchesByTrend(fioriture[bestFiorituraIndex].toTypedArray(),
                       targetNote, actualDirections.toTypedArray() ).toList().reversed().dropLastWhile { it ==targetNote }
                    if(fiorituraNotes.isNotEmpty()){

                        fiorituraNotes.forEach{ it ->
                            newParts.mapIndexed { i, absPart -> if(i == bestFiorituraIndex)
                                absPart.absPitches.add(it)
                            else absPart.absPitches.add(absNotes[i])}
                        }
                    }
                }
            }
            return Counterpoint(newParts, counterpoint.intervalSet)
        }
        fun expand(counterpoint: Counterpoint, nTimes: Int): Counterpoint{
            if(nTimes <= 0) return counterpoint
            val parts = mutableListOf<AbsPart>()
            counterpoint.parts.forEach { oldPart ->
                val pitches = mutableListOf<Int>()
                oldPart.absPitches.forEach { pitch ->
                    (0 until nTimes).forEach { _ ->
                        pitches.add(pitch)
                    }
                }
                parts.add(AbsPart(pitches, oldPart.rowForm, oldPart.transpose, oldPart.delay))
            }
            return Counterpoint(parts, counterpoint.intervalSet)
        }
        fun findWave(counterpoint: Counterpoint, intervalSet: List<Int>, startAbsPitch : Int,
                     steps: List<Int>, trend: List<Int> = TREND.ASCENDANT_STATIC.directions ) : Counterpoint {
            val result = mutableListOf<Int>()
            var index = 0
            val maxSize: Int = counterpoint.parts.maxOf { it.absPitches.size }
            var lastAbsPitch = startAbsPitch
            val absSteps = steps.map{(startAbsPitch + it) % 12}
            while(index < maxSize){
                var resultPitch = -1
                val trendSteps = trend.map{(lastAbsPitch + it) % 12}.filter{absSteps.contains(it)}
                trendSteps@for(step in trendSteps) {
                    val matchValues = counterpoint.getColumnValues(index)
                    val isValid = matchValues.map{
                        it == -1 || Insieme.isIntervalInSet(intervalSet.toIntArray(),it,step)
                    }.fold(true) { acc, b ->  acc && b}
                    if (isValid) {
                        resultPitch= step
                        lastAbsPitch = step
                        break@trendSteps
                    }
                }
                result.add(resultPitch)
                index ++
            }
            return Counterpoint(listOf(*counterpoint.parts.toTypedArray(), AbsPart(result)), intervalSet)
        }
        fun findAllWithWaves(counterpoints: List<Counterpoint>, intervalSet: List<Int>, nWaves: Int) : List<Counterpoint>{
            return counterpoints.map{ findOneWithWaves(it, intervalSet, nWaves)}
        }
        fun findOneWithWaves(counterpoint: Counterpoint, intervalSet: List<Int>, nWaves: Int, nPartsLimit: Int = 12) : Counterpoint{
            val steps4 = (0..3).toList()
            val steps3 = (0..2).toList()
            val steps2 = (0..1).toList()
            return when (nWaves){
                3 -> counterpoint.addWave(intervalSet, 0, steps4)
                                .addWave(intervalSet, 4, steps4)
                                .addWave(intervalSet, 8, steps4)
                                .cutExtraParts(nPartsLimit)
                4 -> counterpoint.addWave(intervalSet, 0, steps3)
                                .addWave(intervalSet, 3, steps3)
                                .addWave(intervalSet, 6, steps3)
                                .addWave(intervalSet, 9, steps3)
                                .cutExtraParts(nPartsLimit)
                6 -> counterpoint.addWave(intervalSet, 0, steps2)
                                .addWave(intervalSet, 2, steps2)
                                .addWave(intervalSet, 4, steps2)
                                .addWave(intervalSet, 6, steps2)
                                .addWave(intervalSet, 8, steps2)
                                .addWave(intervalSet, 10, steps2)
                                .cutExtraParts(nPartsLimit)
                else -> counterpoint
            }
        }
        fun findFreePart(counterpoint: Counterpoint, intervalSet: List<Int>, startAbsPitch : Int, trend: List<Int> ) : Counterpoint {
            var result = mutableListOf<Int>()
            val verticalList = listOf(0,1,2,3,4,5,6,7,8,9,10,11)
            val directions: Array<Int> = Insieme.extractDirectionsFromIntervalSet(verticalList.toTypedArray(), trend.toTypedArray())
            var index = 0
            counterpoint.display()
            val maxSize: Int = counterpoint.maxSize()
            var lastAbsPitch = startAbsPitch
            while(index < maxSize){
                var resultPitch = -1
                directions@for(dir in directions) {
                   var newAbsPitch = lastAbsPitch + dir
                    if( newAbsPitch > 11) newAbsPitch -= 12
                    val matchValues = mutableListOf<Int>()
                    for(j in counterpoint.parts.indices) {
                        if(index<counterpoint.parts[j].absPitches.size){
                            matchValues.add(counterpoint.parts[j].absPitches[index])
                        }
                    }
                    val isValid = matchValues.map{
                        it == -1 || Insieme.isIntervalInSet(intervalSet.toIntArray(),it,newAbsPitch)
                    }.fold(true) { acc, b ->  acc && b}
                    if (isValid) {
                        resultPitch= newAbsPitch
                        lastAbsPitch = newAbsPitch
                        break@directions
                    }
                }
                result.add(resultPitch)
                index ++
            }
            return Counterpoint(listOf(*counterpoint.parts.toTypedArray(), AbsPart(result)), intervalSet)
        }
        fun findAllFreeParts(counterpoint: Counterpoint, intervalSet: List<Int>, trend: List<Int>) : List<Counterpoint> {
            val result = mutableListOf<Counterpoint>()
            (0..11).forEach() {
                result.add(findFreePart(counterpoint, intervalSet, it, trend))
            }
            return result.distinctBy { it.parts } // several counterpoints could be equal
        }
        fun findCounterpoint(target: Counterpoint, sequence: List<Int>, intervalSet: List<Int>,
                             delay: Int, transpose: Int, rowForm: RowForm) : Counterpoint{
            var result = mutableListOf<Int>()

            var actualSeq = sequence.toMutableList()
            when (rowForm){
                INVERSE -> actualSeq = Insieme.invertAbsPitches(actualSeq.toIntArray()).toMutableList()
                RETROGRADE -> actualSeq = actualSeq.reversed().toMutableList()
                INV_RETROGRADE -> actualSeq = Insieme.invertAbsPitches(actualSeq.toIntArray()).reversedArray().toMutableList()
                else -> {}
            }
            actualSeq = actualSeq.map {Insieme.transposeAbsPitch(it, transpose) }.toMutableList()
            for(i in 0 until delay) {
                result.add(-1)
            }
            var resultIndex = delay
            var actualSeqIndex = 0
            while (actualSeqIndex < actualSeq.size) {
                val matchValues = mutableListOf<Int>()
                for(j in target.parts.indices){
                    if( resultIndex < target.parts[j].absPitches.size) matchValues.add(target.parts[j].absPitches[resultIndex])
                    else {
                        matchValues.add(-1)
                    }
                }
                val newAbsPitch = actualSeq[actualSeqIndex]
                val isValid = matchValues.map{
                    it == -1 || Insieme.isIntervalInSet(intervalSet.toIntArray(),it,newAbsPitch)
                }.fold(true) { acc, b ->  acc && b}
                if(isValid) {
                    actualSeqIndex++
                    result.add(newAbsPitch)
                } else {
                    result.add(-1)
                }
                resultIndex++
            }
            val resultAbsPart = AbsPart(result, rowForm, transpose, delay)
            return Counterpoint(listOf(*target.parts.toTypedArray(), resultAbsPart), intervalSet)
        }


        fun findAllCounterpointsWithRepeatedSequence(target: Counterpoint, sequence: List<Int>,
                                                     intervalSet: List<Int>, deepness: Int): List<Counterpoint>{
            val maxSize: Int = target.parts.maxOf { it.absPitches.size }
            val nRepetitions = if(sequence.size > maxSize) 2 else maxSize/sequence.size + 1
            val repeatedSequence = Collections.nCopies(nRepetitions, sequence).flatten()
            return findAllCounterpoints(target, repeatedSequence, intervalSet, deepness)
        }
        fun findAllCounterpoints(target: Counterpoint, sequence: List<Int>, intervalSet: List<Int>, deepness: Int) : List<Counterpoint> {
            val result = mutableListOf<Counterpoint>()
            for(delay in 0 until deepness) {
                var counterpoint: Counterpoint
                for (transpose in 0 until 12) {
                    for (form in 0 until 4) {
                        counterpoint = findCounterpoint(target, sequence, intervalSet, delay, transpose, values()[form])
                        result.add(counterpoint)
                    }
                }
            }
            return result
        }

        fun explodeRowForms(counterpoint: Counterpoint, rowFormsFlags: Int, nNotesToSkip: Int = 0, addFinal: Boolean = false): Counterpoint {
            //val separator = rowFormsFlags and 0b10000 != 0
            val separator = nNotesToSkip > 0
            val separatorCounterpoint: Counterpoint? = if(separator)
                Counterpoint.createSeparatorCounterpoint(counterpoint.parts.size, nNotesToSkip)
                else null
            val original = counterpoint.normalizePartsSize(true)
            var result = original.clone()
            result = if(separator) result.enqueue(separatorCounterpoint!!) else result
            result = if(rowFormsFlags and RowForm.RETROGRADE.flag != 0) result.enqueue(original.retrograde()) else result
            result = if(separator) result.enqueue(separatorCounterpoint!!) else result
            result = if(rowFormsFlags and RowForm.INVERSE.flag != 0) result.enqueue(original.inverse()) else result
            result = if(separator) result.enqueue(separatorCounterpoint!!) else result
            result = if(rowFormsFlags and RowForm.INV_RETROGRADE.flag != 0) result.enqueue(original.inverse().retrograde()) else result
            result = if(separator && addFinal) result.enqueue(separatorCounterpoint!!) else result
            return result
        }

        fun addBestPedal(counterpoint: Counterpoint, intervalSet: List<Int>): Pair<Counterpoint, List<Int>> {
            val alreadyPresentPedals: List<Int> = (0..11).filter{ pitch ->
                counterpoint.parts.map{ it.absPitches}.any{ it.all { it == pitch }}
            }
            data class Pedal(val pitch: Int, val intervalCounts: ArrayList<Int>)
            val pedals = mutableListOf<Pedal>()
            val nSize = counterpoint.maxSize()
            for(pitch in 0..11){
                if (alreadyPresentPedals.contains(pitch)) continue
                var intervalCount = ArrayList(listOf(0,0,0,0,0,0,0))
                for(part in counterpoint.parts){
                    Insieme.incrementIntervalCount(intervalCount, pitch, part.absPitches)
                }
                pedals.add(Pedal(pitch, intervalCount))
            }
            return if(pedals.isNotEmpty()){
                val sortedPedals = pedals.sortedBy { Insieme.intervalSetDifference(it.intervalCounts, intervalSet) }
                val minDifference = sortedPedals.take(1).map { Insieme.intervalSetDifference(it.intervalCounts, intervalSet) }[0]
                val bestPedal: Pedal =
                    sortedPedals.filter{Insieme.intervalSetDifference(it.intervalCounts, intervalSet) == minDifference }
                    .minByOrNull { Insieme.intervalSetDifferenceCount(it.intervalCounts, intervalSet) }
                    ?: sortedPedals.take(1)[0]
                Pair(counterpoint.addPedal(bestPedal.pitch),
                    Insieme.convertIntervalCountToIntervalSet(bestPedal.intervalCounts).toList())
            } else {
                Pair(counterpoint, intervalSet)
            }
        }

    }

    private fun addPedal(pitch: Int): Counterpoint {
        val pedalPart = AbsPart.fill(pitch, maxSize())
        return this.copy(parts = listOf(parts, listOf(pedalPart)).flatten())
    }

    fun normalizePartsSize(refreshEmptiness: Boolean): Counterpoint{
        val maxSize: Int = parts.maxOf { it.absPitches.size }
        val newParts = mutableListOf<AbsPart>()
        parts.forEach{ absPart ->
            val newPart = absPart.copy()
            if(absPart.absPitches.size != maxSize){
                val diff = maxSize - absPart.absPitches.size
                (0 until diff).forEach{ _ ->
                    newPart.absPitches.add(-1)
                }
            }
            newParts.add(newPart)
        }
        val newCounterpoint = this.copy(parts = newParts)
        if(refreshEmptiness) newCounterpoint.emptiness = findEmptiness()
        return newCounterpoint
    }


    fun spreadAsPossible() : Counterpoint {
        val clone = this.normalizePartsSize(false)// cloning is necessary in a coroutine context
        for(partIndex in clone.parts.indices){
            val part = clone.parts[partIndex]
            for(pitchIndex in part.absPitches.indices)
                if(part.absPitches[pitchIndex] == -1) {
                    val previous = if(pitchIndex > 0) {part.absPitches[pitchIndex-1]} else {null }
                    if(previous != null && previous != -1){
                        val matchValues = mutableListOf<Int>()
                        label@for(j in clone.parts.indices) {
                            if (j == partIndex) continue@label
                            matchValues.add(clone.parts[j].absPitches[pitchIndex])
                        }
                        val isValid = matchValues.map{
                            it == -1 || Insieme.isIntervalInSet(intervalSet.toIntArray(),it,previous)
                        }.fold(true) { acc, b ->  acc && b}
                        if (isValid) {
                            part.absPitches[pitchIndex] = previous
                        } else {
                            part.absPitches[pitchIndex] = -1
                        }
                    }
                }
            }
            clone.parts.forEachIndexed() { partIndex, part ->
                for(pitchIndex in part.absPitches.indices)
                    if(part.absPitches[pitchIndex] == -1) {
                        val next = if(pitchIndex < part.absPitches.size -1) {part.absPitches[pitchIndex+1]} else {null }
                        if (next != null && next != -1){
                            val matchValues = mutableListOf<Int>()
                            label@for(j in clone.parts.indices){
                                if(j == partIndex) continue@label
                                    matchValues.add(clone.parts[j].absPitches[pitchIndex])
                            }
                            val isValid = matchValues.map{
                                it == -1 || Insieme.isIntervalInSet(intervalSet.toIntArray(),it,next)
                            }.fold(true) { acc, b ->  acc && b}
                            if (isValid) {
                                part.absPitches[pitchIndex] = next
                            } else {
                                part.absPitches[pitchIndex] = -1
                            }

                        }
                    }
                }
        clone.emptiness = clone.findEmptiness()
        //clone.display()
        return clone
    }


    fun isEmpty() : Boolean {
        return parts.isEmpty()
    }
    fun display() {
        parts.forEachIndexed { index, absPart ->
            println("Part #$index: ${absPart.absPitches.toIntArray().contentToString()}")
        }
    }
    fun displayInNotes(noteNames: List<String> = NoteNamesEn.values().map{it.toString()}) {
        parts.forEachIndexed { index, absPart ->
            println("Part #$index: ${Clip.convertAbsPitchesToClipText(absPart.absPitches, noteNames)}")
        }
    }



    fun findEmptiness() : Float {
        val maxSize = parts.maxOf { it.absPitches.size }
        val nCells = maxSize * parts.size
        if (nCells == 0) return 1.0f
        // considering the counterpoint like a grid and counting every empty cell
        val nEmptyNotes = parts.map{  it.nEmptyNotes() + (maxSize - it.absPitches.size)  }
            .reduce { acc, nNotes -> nNotes + acc}
        if (nEmptyNotes == 0) return 0.0f
        // (100 : X = nCells : nEmptyNotes) / 100
        return nEmptyNotes.toFloat() / nCells
    }

    data class Match(val row1: Int, val row2: Int, val pitch1: Int, val pitch2: Int)

    fun detectParallelIntervals(detectorIntervalSet: List<Int>, delays: List<Int> = listOf(1)): List<List<Boolean>> {
        val maxSize = parts.maxOf { it.absPitches.size }
        val result = parts.map{  (0 until maxSize).map{ false }.toMutableList() }
        for (delay in delays) {
            for (index in 0 until maxSize - 1) {
                var matches: List<Match>
                for (interval in detectorIntervalSet) {
                    matches = detectIntervalInColumn(index, interval)
                    for (match in matches) {
                        // val check = getIntervalInPositions(index+1, triple.first, index+1, triple.second )
                        val nextPitch1 = getAbsPitchInPosition(index + delay, match.row1)
                        val nextPitch2 = getAbsPitchInPosition(index + delay, match.row2)
                        if (nextPitch1 != -1 && nextPitch2 != -1) {
                            if (abs(nextPitch2 - nextPitch1) == interval
                                && match.pitch1 != nextPitch1 && match.pitch2 != nextPitch2
                                && abs(match.pitch1 - nextPitch1) == abs(match.pitch2 - nextPitch2)
                            ) {
                                result[match.row1][index] = true
                                result[match.row2][index] = true
                                result[match.row1][index + delay] = true
                                result[match.row2][index + delay] = true
                            }
                        }
                    }
                }
            }
        }
        return result.map{ it.toList()}
    }
    fun getAbsPitchInPosition(col: Int, row: Int): Int {
        if(col >= parts[row].absPitches.size) return -1
        return parts[row].absPitches[col]
    }
//    fun getIntervalInPositions(col1: Int, row1: Int, col2: Int, row2: Int): Match{
//        if(col1 >= parts[row1].absPitches.size || col2 >= parts[row2].absPitches.size) return Match(-1,-1,-1,-1)
//        if( parts[row2].absPitches[col2] == -1 || parts[row1].absPitches[col1] == -1) return Match(-1,-1,-1,-1)
//        return Match(abs(parts[row2].absPitches[col2] - parts[row1].absPitches[col1]), parts[row1].absPitches[col1])
////    }
    fun detectIntervalInColumn(index: Int, interval: Int): List<Match> {
        val result = mutableListOf<Match>()
        for(i in 0 until parts.size-1) {
                if(index >= parts[i].absPitches.size) continue
                val pitch1 = parts[i].absPitches[index]
                if (pitch1 == -1) continue
            for (j in i+1 until parts.size){
                if(index >= parts[j].absPitches.size) continue
                val pitch2 = parts[j].absPitches[index]
                if (pitch2 == -1) continue
                if( abs(pitch2 - pitch1) == interval ) result.add( Match(i, j, pitch1, pitch2))
            }
        }
        return result
    }

    fun findStabilities(): List<Float> {
        return parts.map{ it.findStability() }
    }

    fun tritoneSubstitution(): Counterpoint {
        return Counterpoint(parts.map{ it.tritoneSubstitution()}, tritoneSubstitutionOnIntervalSet(intervalSet))
    }

    fun ritornello(ritornello: Int): Counterpoint {
        if (ritornello == 0) return this
        var newCounterpoint = this.copy()
        for (i in 0 until ritornello){
            newCounterpoint = this.enqueue(newCounterpoint)
        }
        return newCounterpoint
    }

}




    enum class TREND(val directions: List<Int>){
    ASCENDANT_DYNAMIC(Insieme.TREND_ASCENDANT_DYNAMIC.toList()),
    DESCENDANT_DYNAMIC(Insieme.TREND_DESCENDANT_DYNAMIC.toList()),
    ASCENDANT_STATIC(Insieme.TREND_ASCENDANT_STATIC.toList()),
    DESCENDANT_STATIC(Insieme.TREND_DESCENDANT_STATIC.toList())
}

@Parcelize
data class AbsPart(val absPitches: MutableList<Int>, val rowForm: RowForm = UNRELATED, val transpose: Int = 0, val delay: Int = 0) : Parcelable {
    companion object{
        fun absPartfromClipList(clipList: List<Clip>) : AbsPart {
            return AbsPart(clipList.map { it.abstractNote }.toMutableList())
        }

        fun emptyPart(nNotes: Int = 0): AbsPart {
            val emptyPart = (0 until nNotes).map{ -1 }.toMutableList()
            return AbsPart(emptyPart)
        }

        fun fill(pitch: Int, size: Int): AbsPart {
            return AbsPart((0 until size).map{pitch}.toMutableList())
        }

        val INVERTED_PITCHES = (11 downTo 0).toList()
    }
    fun inverse(): AbsPart{
        val newAbsPitches = mutableListOf<Int>()
        val newRowForm = when (rowForm){
            ORIGINAL -> INVERSE
            INVERSE -> ORIGINAL
            RETROGRADE -> INV_RETROGRADE
            INV_RETROGRADE -> RETROGRADE
            UNRELATED -> UNRELATED
        }
        absPitches.map{ pitch -> if (pitch == -1) newAbsPitches.add(pitch)
                                else newAbsPitches.add(INVERTED_PITCHES[pitch]) }
        return AbsPart(newAbsPitches, newRowForm, transpose, delay)
    }
    fun tritoneSubstitution(): AbsPart {
        return copy(absPitches = absPitches.map{ com.cristianovecchi.mikrokanon.tritoneSubstitution(it) }.toMutableList())
    }
    fun retrograde(): AbsPart {
        val newAbsPitches = absPitches.asReversed()
        return AbsPart(newAbsPitches, rowForm, transpose, delay)
    }
    fun enqueue(absPart: AbsPart) : AbsPart {
        val newAbsPitches = absPitches.toMutableList().also { it.addAll(absPart.absPitches)}
        return AbsPart(newAbsPitches, rowForm, transpose, delay)
    }
    fun clone(): AbsPart{
       return AbsPart(ArrayList(absPitches.toList()),rowForm, transpose, delay)
    }
    fun nEmptyNotes() : Int {
        return absPitches.count { it == -1 }
    }
    fun repeat(nTimes: Int) : AbsPart {
        if(nTimes == 0) return this.clone()
        val newAbsPitches = mutableListOf<Int>()
        (0 until nTimes).forEach{ _ ->
            absPitches.forEach { newAbsPitches.add(it) }
        }
        return AbsPart(newAbsPitches, this.rowForm, this.transpose, this.delay)
    }

    fun findEmptiness(): Float {
            val absPitches = this.absPitches
            val nEmptyNotes = absPitches.count{ it == -1 }
            if (nEmptyNotes == 0) return 0.0f
            // (100 : X = nCells : nEmptyNotes) / 100
            return nEmptyNotes.toFloat() / absPitches.size
    }

    fun findStability(): Float {
        var count = 0
        val pitches = absPitches.dropWhile { it == -1 }.dropLastWhile { it == -1 }
        for(i in 0 until pitches.size-1){
            if (pitches[i] == pitches[i+1]) count ++
        }
        if (count == 0) return 0f
        return count.toFloat() / pitches.size
    }
}
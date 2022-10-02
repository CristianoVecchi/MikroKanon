package com.cristianovecchi.mikrokanon.AIMUSIC

import android.os.Parcelable
import com.cristianovecchi.mikrokanon.AIMUSIC.RowForm.*
import com.cristianovecchi.mikrokanon.composables.NoteNamesEn
import com.cristianovecchi.mikrokanon.cutAdjacentRepetitions
import com.cristianovecchi.mikrokanon.getIntOrEmptyValue
import com.cristianovecchi.mikrokanon.shiftCycling
import com.cristianovecchi.mikrokanon.tritoneSubstitutionOnIntervalSet
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.pow
import kotlin.system.measureTimeMillis

enum class ARPEGGIO {
    ASCENDANT, SINUS, WAVES
}

@Parcelize
data class Counterpoint(val parts: List<AbsPart>,
                        val intervalSet: List<Int> = (0..11).toList(),
                        var emptiness: Float? = null, var timestamp: Long? = null) : Parcelable {

    init {
        emptiness ?: findEmptiness().also<Float> { it -> emptiness = it }
    }
    fun countAbsPitches(): Int {
        return parts.map{ it.absPitches }.fold(0){acc, pitches -> acc + pitches.size}
    }
    fun maxSize(): Int {
        return parts.maxOf { it.absPitches.size }
    }
    fun clone(): Counterpoint {
        return Counterpoint(parts.map{it.clone()}, ArrayList(intervalSet.toList()), emptiness)
    }
    fun cloneWithEmptyParts(): Counterpoint {
        val emptyParts = parts.map{ it.copy(absPitches = mutableListOf())}
        return Counterpoint(emptyParts, ArrayList(intervalSet.toList()))
    }
    fun cutExtraParts(nPartsLimit: Int): Counterpoint{
        return if(this.parts.size <= nPartsLimit) this
        else this.copy(parts = this.parts.take(nPartsLimit))
    }
    fun cutBlankParts(oneLeft: Boolean = true): Counterpoint {
        var newParts = parts.filter{ !it.isBlank()}
        newParts = if(newParts.isEmpty() && oneLeft) listOf(AbsPart.emptyPart(this.nNotes())) else newParts
        return this.copy(parts = newParts )
    }
    fun getColumnValues(index: Int): List<Int>{
        return parts.filter{index < it.absPitches.size }.map{ it.absPitches[index] }
    }
    fun getColumnValuesWithoutEmptyValues(index: Int): List<Int>{
        return parts.filter{index < it.absPitches.size }.map{ it.absPitches[index] }.filter{it != -1 }
    }
    fun getColumnValuesWithEmptyValues(index: Int): List<Int>{
        return parts.map{  if(index < it.absPitches.size) it.absPitches[index] else -1 }
    }
    private fun getNextAbsPitch(partIndex: Int, column: Int, maxSize: Int): Int {
        val nextColumn = (column + 1) % maxSize
        return if(nextColumn < parts[partIndex].absPitches.size) parts[partIndex].absPitches[nextColumn] else -1
    }
    fun enqueue(counterpoint: Counterpoint): Counterpoint{
        val nParts = this.parts.size
        val nPartsToAdd = counterpoint.parts.size
        val diff = nParts - counterpoint.parts.size
        //println("diff: $diff")
        val newParts = when {
            diff == 0 -> parts.mapIndexed { index, absPart ->
                absPart.enqueue( counterpoint.parts[index]) }
            diff < 0 -> { // previous is smaller
                val nNotes = this.parts[0].absPitches.size
                val initialParts = parts.mapIndexed { index, absPart ->
                     absPart.enqueue( counterpoint.parts[index])  }
                val addedParts =  (nParts until nParts + diff.absoluteValue).map { index ->
                    counterpoint.parts[index].fillAndEnqueue(nNotes)
                }
                initialParts + addedParts
            }
            diff > 0 -> { // previous is bigger
                val nNotes = counterpoint.parts[0].absPitches.size
                val initialParts = counterpoint.parts.mapIndexed { index, absPart ->
                     parts[index].enqueue( absPart ) }
                val addedParts =  (nPartsToAdd until nPartsToAdd + diff).map { index ->
                    this.parts[index].enqueueFilling(nNotes)
                }
                initialParts + addedParts
            }
            else -> listOf()
        }
        return Counterpoint(newParts, intervalSet).cutExtraNotes()//.also{ it.display(); println()}
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
    fun addWave(intervalSet: List<Int>, horIntervalSet: List<Int>, startAbsPitch: Int, steps: List<Int> ): Counterpoint {
        return findWave(this, intervalSet, horIntervalSet, startAbsPitch, steps )
    }
    fun nNotes(): Int {
        if (parts.isEmpty()) return 0
        return parts.maxOf { it.absPitches.size }
    }
    fun addPartsOfExtendedWeightedHarmony(nParts: Int, maxParts: Int): Counterpoint {
        val thisParts = this.parts.size
        val diff = thisParts + nParts - maxParts
        if(diff >= nParts) return this
        var result = this
        val nTimes = if(diff<=0) nParts else nParts - diff
        repeat(nTimes){
            result = result.applyExtendedWeightedHarmony()
        }
        return result
    }
    fun applyExtendedWeightedHarmony(duplicateRoot: Boolean = false): Counterpoint {
        var clone = this.normalizePartsSize(false)
        val size = clone.maxSize()
        if(clone.isEmpty() || size < 1) return this
        val priority = intArrayOf(0, 5, 8, 2, 10, 6, 4, 1, 11, 9, 7, 3)
        val columns = (0 until size).map{ clone.getColumnValuesWithEmptyValues(it)}.toMutableList()
        var lastRoot = (Insieme.trovaFond(Insieme.dodecaByteFromAbsPitches(columns[0].toIntArray()))[0] - priority[0] + 12) % 12
        val roots = mutableListOf<Int>()
        //val plusPitches = IntArray(size)
        columns.forEachIndexed { i, column ->
            val dodecaByte = Insieme.dodecaByteFromAbsPitches(column.toIntArray())
            val bools = HarmonyEye.selNotesFrom12Byte(dodecaByte)//.apply {
            //println(this.contentToString()) }
            val harmonyResults = (0..11).map{
                val boolsWithRoot = bools.reversedArray()
                boolsWithRoot[it] = true
                HarmonyEye.findHarmonyResult(boolsWithRoot)
                    .apply {
                        this.dodecaByte = dodecaByte or (1 shl it)}
            }
            val sortedHarmonyResults = harmonyResults.sortedBy { it.weight }
            val priorityTransposed = priority.map{ (it + lastRoot) % 12}
            rootSearch@ for( priorityTr in priorityTransposed){
                for(result in sortedHarmonyResults){
                    if (result.roots.contains(priorityTr)){
                        if(column.contains(priorityTr)){
                                if(duplicateRoot) roots.add(priorityTr)
                                else roots.add(Insieme.getNewAbsPitchesInEWH(priorityTr, column.toSet()))
                        }
                        else {
                            roots.add(priorityTr)
                        }
                        lastRoot = priorityTr
                        //dodecaBytes[i] = result.dodecaByte
                        break@rootSearch
                    }
                }
            }
        }
        if (roots.all{ it == -1}) return clone
        val newParts = this.parts + AbsPart(roots)
        return clone.copy(parts = newParts).apply { this.findAndSetEmptiness() }
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
        return counterpoint.cutExtraNotes()
    }
    fun addEmptyPart(): Counterpoint {
        val newParts = listOf(parts, listOf( AbsPart.emptyPart(maxSize()) )).flatten()
        return this.copy(parts = newParts)
    }
    fun shiftDown(shift: Int): Counterpoint {
        if(shift == 0) return this
        val emptyParts = List<AbsPart>(shift){ AbsPart.emptyPart(this.parts[0].absPitches.size) }
        return this.copy(parts = emptyParts + this.parts)
    }
    fun addBestPedal(intervalSet: List<Int>): Counterpoint {
        return Counterpoint.addBestPedal(this,  intervalSet).first
    }
    fun addPedal(pitch: Int): Counterpoint {
        val pedalPart = AbsPart.fill(pitch, maxSize())
        return this.copy(parts = listOf(parts, listOf(pedalPart)).flatten())
    }
    fun duplicateAllPhrases(): List<Counterpoint>{
        val result = mutableListOf<Counterpoint>()
        val clone = this.normalizePartsSize(false)
        if(clone.isEmpty() || clone.isBlank()) return listOf(clone)
        val parts = clone.parts
        val nNotes = this.nNotes()//.also { print(it) }
        val sectionsToDuplicate = parts.map { part ->
            part.subSequencesLastRepetition()}.flatten().distinct()
        sectionsToDuplicate.forEach{ sectionToDuplicate ->
            val nNotesOfSection = sectionToDuplicate.second - sectionToDuplicate.first
            val  newParts: List<IntArray> = List(this.parts.size){ IntArray(nNotes + nNotesOfSection) {-1} }
            for(i in (0 until sectionToDuplicate.second)){
                for (j in newParts.indices) {
                    newParts[j][i] = parts[j].absPitches[i]
                }
            }
            for(i in (sectionToDuplicate.first until sectionToDuplicate.second)){
                for (j in newParts.indices) {
                    newParts[j][i+nNotesOfSection] = parts[j].absPitches[i]
                }
            }
            for(i in (sectionToDuplicate.second  until nNotes )){
                for (j in newParts.indices) {
                    newParts[j][i+nNotesOfSection] = parts[j].absPitches[i]
                }
            }
            result.add(Counterpoint(newParts.map{ AbsPart(it.toMutableList()) }, this.intervalSet).cutExtraNotes())
                .also{ this.findAndSetEmptiness()}
        }
        return if(result.isEmpty()) {result.add(Counterpoint.empty(parts.size)); result.toList()
        } else {
            result.toList()
        }

    }

    fun normalizePartsSize(refreshEmptiness: Boolean): Counterpoint{
        if(parts.isEmpty()) return this.copy(parts = listOf(AbsPart(mutableListOf())))
        val maxSize: Int = parts.maxOf { it.absPitches.size }
        //if(this.parts.isEmpty()) return this
        val newParts = mutableListOf<AbsPart>()
        parts.forEach{ absPart ->
            val newPart = absPart.clone()
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
    fun isNormalized(): Boolean{
        if(this.isEmpty() || this.parts.size == 1) return true
        return this.parts.map{it.absPitches.size}.toSet().size == 1
    }
    fun spreadAsPossible(findEmptiness: Boolean = true, intervalSet: List<Int> = this.intervalSet) : Counterpoint {
        val clone = this.normalizePartsSize(false) // cloning is necessary in a coroutine context
        //clone.display()
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
            clone.parts.forEachIndexed { partIndex, part ->
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
        if(findEmptiness) clone.findAndSetEmptiness()
        //clone.display()
        return clone
    }
    fun isEmpty() : Boolean {
        return parts.isEmpty()
    }
    fun isBlank() : Boolean {
        return parts.all{ it.isBlank()}
    }
    fun display() {
        parts.forEachIndexed { index, absPart ->
            println("Part #$index(${absPart.absPitches.size}): ${absPart.absPitches.toIntArray().contentToString()}")
        }
    }
    fun displayInNotes(noteNames: List<String> = NoteNamesEn.values().map{it.toString()}) {
        parts.forEachIndexed { index, absPart ->
            val notes = Clip.convertAbsPitchesToClipText(absPart.absPitches, noteNames)
            println("Part #$index: $notes")
        }
    }
    fun findAndSetEmptiness() {
        this.emptiness = findEmptiness()
    }
    fun findEmptiness() : Float {
        if (parts.isEmpty()) return 1f
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
    fun addColumn(column: List<Int>){
        parts.mapIndexed { index, absPart -> absPart.absPitches.add(column[index]) }
    }
    fun addEmptyColumn(){
        parts.map { absPart -> absPart.absPitches.add(-1) }
    }
    fun addEmptyColumns(index: Int, nColumns: Int): Counterpoint {
        if(nColumns == 0) return this.copy()
        val newParts = parts.map{ absPart -> absPart.insert(index, nColumns, -1)}
        return this.copy(parts = newParts).cutExtraNotes().also{ it.findEmptiness()}
    }
    fun counterpointIsEmpty(): Boolean {
        return parts.all{ it.absPitches.isEmpty()}
    }
    fun handleRitornellos(ritornello: Int, transpose: List<Pair<Int,Int>>): Counterpoint {
        return when {
            ritornello > 0 -> this.ritornello(ritornello, transpose)
            transpose[0].first != 0 && transpose[0].second != 1 -> this.transpose(transpose[0].first, transpose[0].second)
            else -> this
        }
    }
    fun handleChordEnhancement(chordsToEnhance: List<ChordToEnhanceData>): Counterpoint {
        return if(chordsToEnhance.isEmpty() || chordsToEnhance.all{it == ChordToEnhanceData(setOf(),1)}) this
        else this.enhanceChords(chordsToEnhance.map{Pair(it.absPitches,it.repetitions)})
    }
    fun arpeggio(arpeggioType: ARPEGGIO): Counterpoint{
        if(counterpointIsEmpty()) return this
        if(this.parts.size == 1) return this
        val clone = this.normalizePartsSize(false)
        val maxSize = clone.maxSize()
        val nParts = clone.parts.size
        val result = clone.cloneWithEmptyParts()
        val computation = when (arpeggioType){
            ARPEGGIO.ASCENDANT -> { index: Int ->
                var column = clone.getColumnValuesWithEmptyValues(index)
                result.addColumn(column)
                for( j in 0 until nParts-1){
                    column = column.shiftCycling()
                    result.addColumn(column)
                }
            }
            ARPEGGIO.SINUS -> { index: Int ->
                var column = clone.getColumnValuesWithEmptyValues(index)
                result.addColumn(column)
                for( j in 0 until nParts-1){
                    column = column.shiftCycling()
                    result.addColumn(column)
                }
                val size = result.getAbsPitches()[0].size
                for( j in 2..nParts){
                    result.addColumn(result.getColumnValuesWithEmptyValues(size-j))
                }
            }
            ARPEGGIO.WAVES -> { index: Int ->
                var column = clone.getColumnValuesWithEmptyValues(index)
                var nextColumn = column.shiftCycling()
                for( j in 0 until nParts){
                    result.addColumn(column)
                    result.addColumn(nextColumn)
                    result.addColumn(column)
                    column = nextColumn
                    nextColumn = column.shiftCycling()
                }
            }
        }
        for( i in 0 until maxSize){
            computation(i)
        }
        return result.cutExtraNotes()
    }
    fun sortColumns(sortType: Int): Counterpoint {
        if(counterpointIsEmpty()) return this
        val clone = this.normalizePartsSize(false)
        val maxSize = clone.maxSize()
        val nParts = clone.parts.size
        val result = clone.cloneWithEmptyParts()
        var lastColumn = clone.getColumnValuesWithEmptyValues(0).sortedDescending()
        result.addColumn(lastColumn)
        val sorting = if ( sortType == 0 ){ p: Int, previousPitch: Int -> //Ascending Type
            val p2 = if( p < previousPitch ) p + 12 else p
            p2 - previousPitch
        } else {p: Int, previousPitch: Int -> //Descending Type
            val p2 = if( p > previousPitch ) p - 12 else p
            previousPitch - p2
        }
        for( i in 1 until maxSize){
            val newColumn = mutableListOf<Int>()
            val absPitches = clone.getColumnValuesWithEmptyValues(i).toMutableList()
            for(j in 0 until nParts){
                val lastPitch =  lastColumn[j]
                while(absPitches.isNotEmpty()){
                    val indexPitch2 = absPitches
                        .withIndex()
                        .filter{ (_, v) -> v != -1 }
                        .minByOrNull { (_, p) ->
                        sorting(p, lastPitch)
                    }?.index
                    if(indexPitch2 == null){
                        absPitches.removeAt(0)
                        newColumn.add(-1)
                    } else {
                        newColumn.add(absPitches[indexPitch2])
                        absPitches.removeAt(indexPitch2)
                    }
                }
            }

            result.addColumn(newColumn)
            lastColumn = newColumn.toList()
        }

        result.emptiness = result.findEmptiness()
        return result.cutBlankParts(true)
    }
    fun enhanceChords(chordsToEnhance: List<Pair<Set<Int>, Int>>): Counterpoint {
        //println("chords to enhance:$chordsToEnhance")
        if (chordsToEnhance.isEmpty()) return this
        val clone = if(isNormalized()) this else this.normalizePartsSize(false)
        val result = clone.cloneWithEmptyParts()
        var index = 0
        val maxSize = clone.maxSize()
        val chords = chordsToEnhance.map{ it.first.toSortedSet() }
        val repetitions = chordsToEnhance.map{ it.second }
        while (index < maxSize){
            val columnSorted = clone.getColumnValuesWithoutEmptyValues(index).toSortedSet()
            var found = false
            chords@for(i in chords.indices){
                if(chords[i] == columnSorted){
                    found = true
                    val repeatedColumn = clone.getColumnValuesWithEmptyValues(index)
                    //println("index: $index  chord: ${chords[i]}  column sorted: $columnSorted  repeated column: $repeatedColumn")
                    (0 until repetitions[i]).forEach{ _ ->
                        result.addColumn(repeatedColumn)
                    }
                    break@chords
                }
            }
            if(!found) result.addColumn(clone.getColumnValuesWithEmptyValues(index))
            index++
        }
        return result.cutExtraNotes().apply { this.findAndSetEmptiness() }
    }
    fun addMultipleDoublingParts(doublingList: List<Pair<Int,Int>>, maxParts: Int): Counterpoint {
        val clone = this.normalizePartsSize(false)
        if(doublingList.isEmpty()) return clone
        var result = clone.cloneWithEmptyParts()
        for((interval, rowForm) in doublingList){
            val newParts = this.parts.map{ it.transpose(interval, rowForm)}.sortedBy { it.findEmptiness() }
            val cloneWithDoubling = clone.addParts(newParts, maxParts)
            result = result.enqueue(cloneWithDoubling)
        }
        return result
    }
    fun addParts(newParts: List<AbsPart>, maxParts: Int): Counterpoint {
        return this.copy(parts = this.parts + newParts).cutExtraParts(maxParts).apply {
            this.findAndSetEmptiness()
        }
    }
    fun chessboard(range: Int = 1): Counterpoint {
        val clone = this.normalizePartsSize(false)
        val size = clone.maxSize()
        if(range < 1 || clone.parts.isEmpty() || size == 0) return clone
        val newParts = mutableListOf<AbsPart>()
        clone.parts.forEachIndexed { index, absPart ->
            val newPitches = mutableListOf<Int>()
            val chunks = if(range < size){
                absPart.absPitches.chunked(range)
            } else {
                listOf(absPart.absPitches + MutableList(range-size){-1})
            }
            chunks.forEach{
                if(index % 2 == 0){
                    newPitches += it
                    newPitches += MutableList(it.size){ -1 }
                } else {
                    newPitches += MutableList(it.size){ -1 }
                    newPitches += it
                }
            }
            newParts += absPart.copy(absPitches = newPitches)
        }
        return clone.copy(parts = newParts).cutExtraNotes().apply { this.findAndSetEmptiness() }
    }
    fun addResolutiones(absPitchesSet: Set<Int>, resolutioForm: List<Int> = listOf(0,1,0,1,0)): Counterpoint {
        val clone = this.normalizePartsSize(false)
        //if(absPitchesSet.isEmpty()) return clone() // if absPitchesSet is empty resolutioColumn == column
        val result = clone.cloneWithEmptyParts()
        val checks = clone.areColumnsInSet(absPitchesSet)
        var index = 0
        val maxSize = clone.maxSize()
        val (nBeforeRests, nFirstNotes, nMiddleRests, nSecondNotes, nAfterRests ) = resolutioForm
        while (index < maxSize){
            val column = clone.getColumnValuesWithEmptyValues(index)
            if(checks[index]){
                result.addColumn(column)
            } else {
                val resolutioColumn = column.map{Insieme.resolveOnAbsPitch(it, absPitchesSet)}

                (0 until nBeforeRests).forEach{ _ -> result.addEmptyColumn() }
                (0 until nFirstNotes).forEach{ _ -> result.addColumn(column) }
                (0 until nMiddleRests).forEach{ _ -> result.addEmptyColumn() }
                (0 until nSecondNotes).forEach{ _ -> result.addColumn(resolutioColumn) }
                (0 until nAfterRests).forEach{ _ -> result.addEmptyColumn() }

            }
            index++
        }
        return result.cutExtraNotes().apply { this.findAndSetEmptiness() }
    }

    fun addCadenzas(horizontalIntervalSet: List<Int>, values: List<Int> = listOf(0,1,0,1,1)): Counterpoint{
        val clone = this.normalizePartsSize(false)
        val checks = clone.detectIntervalsInColumns(horizontalIntervalSet)
        val result = clone.cloneWithEmptyParts()
        var index = 0
        val maxSize = clone.maxSize()
        val (nBeforeRests, nFirstNotes, nMiddleRests, nSecondNotes, nAfterRests ) = values
        while (index < maxSize){
            if(checks[index]){
                val nextIndex = (index+1) %  maxSize
                val column1 = clone.getColumnValuesWithEmptyValues(index)
                val column2 = clone.getColumnValuesWithEmptyValues(nextIndex)

                (0 until nBeforeRests).forEach{ _ -> result.addEmptyColumn() }
                (0 until nFirstNotes).forEach{ _ -> result.addColumn(column1) }
                (0 until nMiddleRests).forEach{ _ -> result.addEmptyColumn() }
                (0 until nSecondNotes).forEach{ _ -> result.addColumn(column2) }
                (0 until nAfterRests).forEach{ _ -> result.addEmptyColumn() }

                index += 2
            } else {
                val column1 = clone.getColumnValuesWithEmptyValues(index)
                result.addColumn(column1)
                index++
            }
        }
        return result.cutExtraNotes().apply { this.findAndSetEmptiness() }
    }
    fun eraseIntervalsOnBothNotes(horizontalIntervalSet: List<Int>): Counterpoint{
        val clone = this.normalizePartsSize(false)
        val newParts = mutableListOf<AbsPart>()
        clone.parts.forEach { part ->
            val checks = part.detectIntervalsReportingBothNotes(horizontalIntervalSet)
            newParts.add(part.setAbsPitchesByChecks(checks))
        }
        return Counterpoint(parts = newParts, this.intervalSet).apply { this.findAndSetEmptiness() }
    }
    fun reduceToSinglePart(): Counterpoint{
        val reducedAbsPitches = (0 until maxSize()).map{ this.getColumnValuesWithEmptyValues(it)}.flatten().toMutableList()
        return this.copy(parts = listOf(parts[0].copy(absPitches = reducedAbsPitches))).cutExtraNotes().apply { this.findAndSetEmptiness() }
    }
    fun explodeToDoppelgänger(maxParts: Int): Counterpoint{
        if(isEmpty() || isBlank()) return this.copy()
        val newParts = mutableListOf<AbsPart>()
        val nPartsToExplode = (maxParts - (parts.size * 2 - maxParts).absoluteValue ) / 2
        //val nNewParts = nPartsToExplode * 2 + ( parts.size - nPartsToExplode)

        parts.forEachIndexed { index, absPart ->
            if(index < nPartsToExplode){
                //val isUpperPart = index < this.parts.size / 2
                val partTwins: List<AbsPart> = absPart.divideAbsPitchesByDirection()
                newParts.addAll(partTwins)
            } else {
                newParts.add(absPart)
            }
        }
        return this.copy(parts = newParts.filter{!it.isBlank()}).also{ it.findAndSetEmptiness()}
    }
//    fun explodeToDoppelgänger2(maxParts: Int, ensembleTypes: List<EnsembleType>, rangeType: Pair<Int, Int>, melodyType: Int): Counterpoint {
//        val newParts = mutableListOf<AbsPart>()
//        val nPartsToExplode = (maxParts - (parts.size * 2 - maxParts).absoluteValue ) / 2
//        val nNewParts = nPartsToExplode * 2 + ( parts.size - nPartsToExplode)
//        val ensembles = if(ensembleTypes.size ==1) Ensembles.getEnsemble(nNewParts, ensembleTypes[0])
//                        else Ensembles.getEnsembleMix(nNewParts, ensembleTypes)
//        parts.forEachIndexed { index, absPart ->
//            if(index < nPartsToExplode){
//                val ensemble = ensembles[index]
//                val isUpperPart = index < this.parts.size / 2
//                val partTwins: List<AbsPart> =
//                    absPart.divideWithSubSequencer(ensemble.octave,
//                        ensemble.getOctavedRangeByType(rangeType.first, rangeType.second, isUpperPart), melodyType )
//                newParts.addAll(partTwins)
//            } else {
//                newParts.add(absPart)
//            }
//        }
//        return Counterpoint(newParts.toList(), this.intervalSet)
//    }
    fun areColumnsInSet(absPitchesSet: Set<Int>): List<Boolean> {
        val result = mutableListOf<Boolean>()
        val maxSize = maxSize()
        for( i in 0 until maxSize){
            val column = this.getColumnValuesWithoutEmptyValues(i)
            if(absPitchesSet.containsAll(column)) result.add(true) else result.add(false)
        }
        return result
    }
    fun detectIntervalsInColumns(detectorIntervalSet: List<Int>): List<Boolean> {
        val result = mutableListOf<Boolean>()
        val maxSize = maxSize()
        val intervalSet = detectorIntervalSet.toIntArray()
        for( i in 0 until maxSize){
            var check = false
            val nextIndex = (i+1) % maxSize
            for ( j in parts.indices){
                if(parts[j].absPitches[i] == -1 || parts[j].absPitches[nextIndex] == -1) continue
                if(Insieme.isIntervalInSet(intervalSet, parts[j].absPitches[i], parts[j].absPitches[nextIndex] )){
                    check = true
                    break
                }
            }
            result.add(check)
        }
        return result
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
                            val diffNextPitches = nextPitch2 - nextPitch1
                            if ((abs(diffNextPitches) == interval
                                        || abs(diffNextPitches - 12) == interval
                                        || abs(diffNextPitches + 12) == interval)
                                && match.pitch1 != nextPitch1 && match.pitch2 != nextPitch2
                            )
                                 {
                                     val diff1 = nextPitch1 - match.pitch1
                                     val diff2 = nextPitch2 - match.pitch2
                                     if(diff1 == diff2 || diff1 == diff2 - 12 || diff1 == diff2 + 12){
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
                    //.also{ println("pitch2:$pitch2 pitch1:$pitch1 interval:$interval")}
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
    fun transpose(transposition: Int, rowForm: Int = 1): Counterpoint{
        if (transposition == 0 && rowForm == 1) return this.clone()
        val newParts = this.parts.map{ it.transpose(transposition, rowForm)}
        return Counterpoint(newParts, this.intervalSet, this.emptiness)
    }
    fun ritornello(ritornello: Int, transpositions: List<Pair<Int,Int>> = listOf(Pair(0,1))): Counterpoint {
       // println(ritornello)
       // println(transpositions)
        val normalized = if(this.isNormalized()) this else this.normalizePartsSize(false)
        if (transpositions.all{ it == Pair(0,1)}) {
            if (ritornello == 0) return normalized
            var newCounterpoint = this.normalizePartsSize(false)
            for (i in 0 until ritornello){
                newCounterpoint = newCounterpoint.enqueue(normalized)
            }
            return newCounterpoint.apply { findAndSetEmptiness() }
        } else {
            var newCounterpoint = this.normalizePartsSize(false).transpose(transpositions[0].first, transpositions[0].second)
            for (i in 0 until ritornello){
                val (transpose, rowForm) = transpositions[(i + 1) % transpositions.size]
                newCounterpoint = newCounterpoint.enqueue(normalized.transpose(transpose, rowForm))
            }

            return newCounterpoint.cutExtraNotes().apply { findAndSetEmptiness() }
        }
    }
    fun convertPartsToCsv(partSeparator: String = "\n"): String{
        if(parts.isEmpty()) return ""
        return parts.joinToString(partSeparator) { it.absPitches.joinToString(",") }
    }

    fun upsideDown(): Counterpoint {
        return this.copy(parts = parts.reversed())
    }
    fun centerVertically(counterpoint2nd: Counterpoint): Pair<Counterpoint, Counterpoint>{
        val nParts1st = this.parts.size
        val nParts2nd = counterpoint2nd.parts.size
        val nParts = max(nParts1st,nParts2nd)
        return Pair(
            this.shiftDown((nParts - nParts1st) shr 1),
            counterpoint2nd.shiftDown((nParts - nParts2nd) shr 1)
        )
    }

    fun sortColumnsByProgressiveEWH(): Counterpoint{
        val nParts = this.parts.size
        val columns = (0 until this.maxSize()).map{this.getColumnValuesWithEmptyValues(it)}.toMutableList()
        val result = Counterpoint.empty(nParts)
        (1..nParts).forEach{ step ->
            val stepColumns = columns.filter{
                //it.count{ pitch -> pitch == -1} == nParts - step
                        it.filter{pitch -> pitch != -1}.toSet().size == step
            }
            val weightedColumns = stepColumns.map{
                val harmonyResult = HarmonyEye.findHarmonyResult(Insieme.dodecaByteFromAbsPitches(it.toIntArray()))
                //println("Column:$it Harmony Result: ${harmonyResult.weight} ${harmonyResult.roots.contentToString()}")
                Pair(it, harmonyResult.weight)
            }.sortedByDescending { it.second }
            weightedColumns.forEach { result.addColumn(it.first) }
            columns.removeAll(stepColumns)
        }
        result.findAndSetEmptiness()
        return result
    }
    fun transposingGlueWithRowFormsOf2nd(counterpoint2nd: Counterpoint, centerVertically: Boolean): List<Counterpoint>{
        val (counterpoint1st, original2nd) = this.normalizePartsSize(false)
                                                .centerVertically(counterpoint2nd.normalizePartsSize(false))
        val inverse = original2nd.inverse()
        val retrograde = original2nd.retrograde()
        val inverseRetrograde = retrograde.inverse()
        val result = mutableListOf<Counterpoint>()
        for(transpose in (0 until 12)){
            result.add(counterpoint1st.enqueue(original2nd).transpose(transpose))
            result.add(counterpoint1st.enqueue(inverse).transpose(transpose))
            result.add(counterpoint1st.enqueue(retrograde).transpose(transpose))
            result.add(counterpoint1st.enqueue(inverseRetrograde).transpose(transpose))
        }
        result.map{ it.cutExtraNotes().apply { findAndSetEmptiness()}}
        return result.toList()
    }
    suspend fun transposingOverlap(context: CoroutineContext, counterpoint2nd: Counterpoint,
                                   crossover: Boolean, intervalSet: List<Int>,
                                   compression: Boolean = true): List<Counterpoint> =
        withContext(context) {
        val counterpoint1st = this@Counterpoint.normalizePartsSize(true)
        val original2nd = counterpoint2nd.normalizePartsSize(true)
        val size1st = this@Counterpoint.maxSize()
        val size2nd = original2nd.maxSize()
        //println("OVERLAP: 1st_size=$size1st 2nd_size=$size2nd")
        var result = mutableListOf<Counterpoint>()
        if(size1st == 0 || size2nd == 0 || (size1st == 1 && crossover) ) {
            result.add(this@Counterpoint.copy())
        } else {
            val firstIsShorter = size1st <= size2nd
            val diff = (size2nd - size1st).absoluteValue
            val inverse = original2nd.inverse()
            val retrograde = original2nd.retrograde()
            val inverseRetrograde = retrograde.inverse()
            val stepRange: IntRange = if(crossover){
                    if(firstIsShorter) (1 until size1st) else ((size1st-size2nd  + 1) until size1st)
            } else {(0..diff)}
            try {
                val job = context.job
                val crossoverTake = 4
                val overlapTake = 16
                if(firstIsShorter && !crossover){ // shift the first counterpoint (overlap)
                 mainLoop@  for(step in stepRange){
                        val count1st = counterpoint1st.addEmptyColumns(0, step)
                        val partialResult =  mutableListOf<Counterpoint>()
                        for(transpose in (0 until 12)){
                            if(!job.isActive) break@mainLoop
                            partialResult.add(count1st.overlap(original2nd.transpose(
                                transpose
                            )))
                            partialResult.add(count1st.overlap(inverse.transpose(transpose)))
                            partialResult.add(count1st.overlap(retrograde.transpose(
                                transpose
                            )))
                            partialResult.add(count1st.overlap(inverseRetrograde.transpose(
                                transpose
                            )))
                        }
                        result.addAll(partialResult.sortedBy{ it.checkVerticalFaults(intervalSet)}.take(overlapTake))
                    }
                } else { // shift the second counterpoint (crossover or overlap)
                    mainLoop@ for(step in stepRange){
                        val orig = original2nd.addEmptyColumns(0, step)
                        val inv = inverse.addEmptyColumns(0, step)
                        val retr = retrograde.addEmptyColumns(0, step)
                        val invRetr = inverseRetrograde.addEmptyColumns(0, step)
                        val partialResult =  mutableListOf<Counterpoint>()
                        for(transpose in (0 until 12)){
                            if(!job.isActive) break@mainLoop
                            partialResult.add(counterpoint1st.overlap(orig.transpose(
                                transpose
                            )))
                            partialResult.add(counterpoint1st.overlap(inv.transpose(
                                transpose
                            )))
                            partialResult.add(counterpoint1st.overlap(retr.transpose(
                                transpose
                            )))
                            partialResult.add(counterpoint1st.overlap(invRetr.transpose(
                                transpose
                            )))
                        }
                        val take = if(crossover) crossoverTake else overlapTake
                        result.addAll(partialResult.sortedBy{ it.checkVerticalFaults(intervalSet)}.take(take))
                    }
                }
            }  catch (ex: OutOfMemoryError){
                println("Out of memory in Overlap Computation, result list is partial")
                result = result.take(40).toMutableList()
            }
        }
        if(compression){
            val fromPart = this@Counterpoint.parts.size
            result.map{ it.compress(fromPart)}
        } else {
            result.toList().map{ it.cutExtraNotes().apply { this.findAndSetEmptiness()}}
        }
    }
    fun compress(fromPart: Int): Counterpoint {
        val totalSize = parts.size
        if (fromPart >= totalSize) return this
        val compressedParts = mutableListOf<Int>()
        val deletedParts = mutableListOf<Int>()
        deletableLoop@ for (i in fromPart until totalSize) {
            for (j in 0 until fromPart) {
                val deletable = parts[i]
                val compressable = parts[j]
                if (!compressedParts.contains(j) && compressable.isCompressable(deletable)) {
                    compressable.compress(deletable)
                    compressedParts.add(j)
                    deletedParts.add(i)
                    continue@deletableLoop
                }
            }
        }
        val newParts = parts.filterIndexed { index, _ -> !deletedParts.contains(index) }
        val result = this.copy(parts = newParts)
        return result.cutExtraNotes().apply { this.findAndSetEmptiness() }
//        return copy(parts = newParts).apply {
//            findEmptiness()
//        }
    }
    fun overlap(counterpoint: Counterpoint): Counterpoint{
        val newParts = this.parts + counterpoint.parts
        return this.copy(parts = newParts).normalizePartsSize(true)
    }
    fun checkVerticalFaults(intervalSet: List<Int>): Int {
        if(parts.size < 2) return 0
        var faults = 0
        (0 until maxSize() ).forEach{ index ->
            faults += checkVerticalFaultsInColumn(intervalSet, index)
        }
        return faults
    }
    fun checkVerticalFaultsInColumn(intervalSet: List<Int>, index: Int): Int {
        val nCells = parts.size
        if(nCells < 2) return 0
        var faults = 0
        for(cell in 0 until (nCells - 1)){
           // println("$cell  $index")
            val cellValue = parts[cell].absPitches[index]
            if(cellValue == -1) continue
            for(cell2nd in cell+1 until nCells ){
                val cell2ndValue = parts[cell2nd].absPitches[index]
                if(cell2ndValue == -1) continue
                val interval = (cell2ndValue - cellValue).absoluteValue
                if (!intervalSet.contains(interval)) faults++
            }
        }
        return faults
    }

    fun getRibattutos(): List<List<Boolean>> {
        return parts.map { it.getRibattutos() }
    }
    fun cutLastNotesFrom(start: Int): Counterpoint{
        return this.copy(parts = parts.map{it.copy(absPitches = it.absPitches.subList(0, start))})
            .apply { this.findAndSetEmptiness() }
    }

    fun cutExtraNotes(): Counterpoint{
        return if(this.maxSize()<= MAX_N_NOTES) this else this.cutLastNotesFrom(MAX_N_NOTES)
    }

    companion object {
        const val MAX_N_NOTES = 32768
        fun createFromCsv(
            doubleLevelCsv: String,
            partSeparator: String = "\n",
            timestamp: Long? = null
        ): Counterpoint {
            val newParts = doubleLevelCsv.split(partSeparator)
                .map { csvPart -> csvPart.split(',').map { it.toInt() } }
                .map { intsPart -> AbsPart(intsPart.toMutableList()) }
            return Counterpoint(newParts, timestamp = timestamp).cutExtraNotes().apply { this.findAndSetEmptiness() }
        }

        fun createSeparatorCounterpoint(nParts: Int, nNotesToSkip: Int): Counterpoint {
            val absPart = AbsPart((0 until nNotesToSkip).map { -1 }.toMutableList())
            val newParts = (0 until nParts).map { absPart }
            return Counterpoint(newParts, emptiness = 1f).cutExtraNotes()
        }

        fun counterpointFromClipList(clipList: List<Clip>): Counterpoint {
            return Counterpoint(listOf(AbsPart.absPartfromClipList(clipList))).cutExtraNotes().apply { this.findAndSetEmptiness() }
        }

        fun empty(): Counterpoint {
            return Counterpoint(emptyList(), emptyList(), 1.0f)
        }

        fun empty(nParts: Int, nRests: Int = 0): Counterpoint {
            return Counterpoint(
                (0 until nParts).map { AbsPart.emptyPart(nRests) },
                emptyList(),
                1.0f
            )
        }

        fun flourish(
            counterpoint: Counterpoint,
            intervalSet: List<Int>,
            horIntervalSet: List<Int>
        ): Counterpoint {
            //val actualDirections = TREND.ASCENDANT_DYNAMIC.directions.filter{ horIntervalSet.contains(it)}
            //counterpoint.display()
            val ascDirections =
                TREND.ASCENDANT_DYNAMIC.directions.filter { horIntervalSet.contains(it) }
            val descDirections =
                TREND.DESCENDANT_DYNAMIC.directions.filter { horIntervalSet.contains(it) }
            val maxSize = counterpoint.maxSize()
            val nParts = counterpoint.parts.size
            val newParts =
                (0 until nParts).map { counterpoint.parts[it].copy(absPitches = mutableListOf()) }
            columns@ for (index in 0 until maxSize) {
                val fioriture: List<MutableList<Int>> = (0 until nParts).map { mutableListOf() }
                val absNotes = counterpoint.getColumnValuesWithEmptyValues(index)
                for (partIndex in 0 until nParts) {
                    val absPitches = counterpoint.parts[partIndex].absPitches
                    val startNote = absPitches.getIntOrEmptyValue(index)
                    val targetNote = counterpoint.getNextAbsPitch(partIndex, index, maxSize)
                    if (targetNote == startNote) continue
                    val otherNotes =
                        absNotes.filterIndexed { i, _ -> i != partIndex }.filter { it != -1 }
                    val fioritura: List<Int> = Insieme.getPossibleAbsPitches(
                        otherNotes.toIntArray(),
                        intervalSet.toIntArray()
                    ).toList()
                    fioriture[partIndex].addAll(fioritura.filter { it != startNote }
                        .filter { it != targetNote })
                }
                //fioriture.map{ println(it)}
                val bestFiorituraSize = fioriture.filterIndexed { i, _ ->
                    counterpoint.getNextAbsPitch(
                        i,
                        index,
                        maxSize
                    ) != -1
                }
                    .maxOfOrNull { it.size } ?: 0
                newParts.mapIndexed { i, absPart -> absPart.absPitches.add(absNotes[i]) }
                if (bestFiorituraSize == 0) {
                    continue@columns
                } else {
                    var bestFiorituraIndex: Int = -1
                    for (i in fioriture.indices) {
                        if (fioriture[i].size == bestFiorituraSize && counterpoint.getNextAbsPitch(
                                i,
                                index,
                                maxSize
                            ) != -1
                        ) {
                            bestFiorituraIndex = i; break
                        }
                    }
                    val originNote = counterpoint.getAbsPitchInPosition(index, bestFiorituraIndex)
                    val targetNote =
                        counterpoint.getNextAbsPitch(bestFiorituraIndex, index, maxSize)
                    val diff = abs(targetNote - originNote)
                    val actualDirections = if (originNote < targetNote) {
                        if (diff <= 6) ascDirections else descDirections
                    } else {
                        if (diff <= 6) descDirections else ascDirections
                    }
                    //println("part:$bestFiorituraIndex start:$originNote end:$targetNote diff:$diff dirs:$actualDirections")
                    val bestFioritura = fioriture[bestFiorituraIndex]
                    val fiorituraNotes: List<Int> = Insieme.orderAbsPitchesByTrend(
                        bestFioritura.toTypedArray(),
                        targetNote, actualDirections.toTypedArray()
                    ).toList().reversed()
                        .dropLastWhile { it == targetNote }
                        .dropWhile {
                            !Insieme.isIntervalInSet(
                                horIntervalSet.toIntArray(),
                                originNote,
                                it
                            )
                        }
                    //println("part:$bestFiorituraIndex start:$originNote end:$targetNote diff:$diff dirs:$actualDirections note:$bestFioritura fioritura:$fiorituraNotes")
                    if (fiorituraNotes.isNotEmpty()) {
                        fiorituraNotes.forEach {
                            newParts.mapIndexed { i, absPart ->
                                if (i == bestFiorituraIndex)
                                    absPart.absPitches.add(it)
                                else absPart.absPitches.add(absNotes[i])
                            }
                        }
                    }
                }
            }
            return Counterpoint(newParts, counterpoint.intervalSet).cutExtraNotes().apply { this.findAndSetEmptiness() }
        }

        fun expand(counterpoint: Counterpoint, nTimes: Int): Counterpoint {
            if (nTimes <= 0) return counterpoint
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
            return Counterpoint(parts, counterpoint.intervalSet).cutExtraNotes().apply { this.findAndSetEmptiness() }
        }

        fun findWave(
            counterpoint: Counterpoint,
            intervalSet: List<Int>,
            horIntervalSet: List<Int>,
            startAbsPitch: Int,
            steps: List<Int>,
            trend: List<Int> = TREND.ASCENDANT_STATIC.directions
        ): Counterpoint {
            val result = mutableListOf<Int>()
            var index = 0
            val maxSize: Int = counterpoint.parts.maxOf { it.absPitches.size }
            var lastAbsPitch = startAbsPitch
            val absSteps = steps.map { (startAbsPitch + it) % 12 }
            val filteredTrend = trend.filter { horIntervalSet.contains(it) }
            while (index < maxSize) {
                var resultPitch = -1
                val trendSteps =
                    filteredTrend.map { (lastAbsPitch + it) % 12 }.filter { absSteps.contains(it) }
                trendSteps@ for (step in trendSteps) {
                    val matchValues = counterpoint.getColumnValues(index)
                    val isValid = matchValues.map {
                        it == -1 || Insieme.isIntervalInSet(intervalSet.toIntArray(), it, step)
                    }.fold(true) { acc, b -> acc && b }
                    if (isValid) {
                        resultPitch = step
                        lastAbsPitch = step
                        break@trendSteps
                    }
                }
                result.add(resultPitch)
                index++
            }
            return Counterpoint(
                listOf(*counterpoint.parts.toTypedArray(), AbsPart(result)),
                intervalSet
            )
        }

        fun findAllWithWaves(
            counterpoints: List<Counterpoint>,
            intervalSet: List<Int>,
            horIntervalSet: List<Int>,
            nWaves: Int
        ): List<Counterpoint> {
            return counterpoints.map { findOneWithWaves(it, intervalSet, horIntervalSet, nWaves) }
        }

        fun findOneWithWaves(
            counterpoint: Counterpoint,
            intervalSet: List<Int>,
            horIntervalSet: List<Int>,
            nWaves: Int,
            nPartsLimit: Int = 12
        ): Counterpoint {
            val steps4 = (0..3).toList()
            val steps3 = (0..2).toList()
            val steps2 = (0..1).toList()
            return when (nWaves) {
                3 -> counterpoint.addWave(intervalSet, horIntervalSet, 0, steps4)
                    .addWave(intervalSet, horIntervalSet, 4, steps4)
                    .addWave(intervalSet, horIntervalSet, 8, steps4)
                    .cutExtraParts(nPartsLimit)
                4 -> counterpoint.addWave(intervalSet, horIntervalSet, 0, steps3)
                    .addWave(intervalSet, horIntervalSet, 3, steps3)
                    .addWave(intervalSet, horIntervalSet, 6, steps3)
                    .addWave(intervalSet, horIntervalSet, 9, steps3)
                    .cutExtraParts(nPartsLimit)
                6 -> counterpoint.addWave(intervalSet, horIntervalSet, 0, steps2)
                    .addWave(intervalSet, horIntervalSet, 2, steps2)
                    .addWave(intervalSet, horIntervalSet, 4, steps2)
                    .addWave(intervalSet, horIntervalSet, 6, steps2)
                    .addWave(intervalSet, horIntervalSet, 8, steps2)
                    .addWave(intervalSet, horIntervalSet, 10, steps2)
                    .cutExtraParts(nPartsLimit)
                else -> counterpoint
            }
        }

        fun findFreePart(
            counterpoint: Counterpoint,
            intervalSet: List<Int>,
            startAbsPitch: Int,
            trend: List<Int>
        ): Counterpoint {
            var result = mutableListOf<Int>()
            val verticalList = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
            val directions: Array<Int> = Insieme.extractDirectionsFromIntervalSet(
                verticalList.toTypedArray(),
                trend.toTypedArray()
            )
            var index = 0
            //counterpoint.display()
            val maxSize: Int = counterpoint.maxSize()
            var lastAbsPitch = startAbsPitch
            while (index < maxSize) {
                var resultPitch = -1
                directions@ for (dir in directions) {
                    var newAbsPitch = lastAbsPitch + dir
                    if (newAbsPitch > 11) newAbsPitch -= 12
                    val matchValues = mutableListOf<Int>()
                    for (j in counterpoint.parts.indices) {
                        if (index < counterpoint.parts[j].absPitches.size) {
                            matchValues.add(counterpoint.parts[j].absPitches[index])
                        }
                    }
                    val isValid = matchValues.map {
                        it == -1 || Insieme.isIntervalInSet(
                            intervalSet.toIntArray(),
                            it,
                            newAbsPitch
                        )
                    }.fold(true) { acc, b -> acc && b }
                    if (isValid) {
                        resultPitch = newAbsPitch
                        lastAbsPitch = newAbsPitch
                        break@directions
                    }
                }
                result.add(resultPitch)
                index++
            }
            return Counterpoint(
                listOf(*counterpoint.parts.toTypedArray(), AbsPart(result)),
                intervalSet
            )
        }

        fun findAllFreeParts(
            counterpoint: Counterpoint,
            intervalSet: List<Int>,
            trend: List<Int>
        ): List<Counterpoint> {
            val result = mutableListOf<Counterpoint>()
            (0..11).forEach{
                result.add(findFreePart(counterpoint, intervalSet, it, trend))
            }
            return result.distinctBy { it.parts } // several counterpoints could be equal
        }

        fun findCounterpoint(
            target: Counterpoint, sequence: List<Int>, intervalSet: List<Int>,
            delay: Int, transpose: Int, rowForm: RowForm
        ): Counterpoint {
            var result = mutableListOf<Int>()

            var actualSeq = sequence.toMutableList()
            when (rowForm) {
                INVERSE -> actualSeq =
                    Insieme.invertAbsPitches(actualSeq.toIntArray()).toMutableList()
                RETROGRADE -> actualSeq = actualSeq.reversed().toMutableList()
                INV_RETROGRADE -> actualSeq =
                    Insieme.invertAbsPitches(actualSeq.toIntArray()).reversedArray().toMutableList()
                else -> {
                }
            }
            actualSeq = actualSeq.map { Insieme.transposeAbsPitch(it, transpose) }.toMutableList()
            for (i in 0 until delay) {
                result.add(-1)
            }
            var resultIndex = delay
            var actualSeqIndex = 0
            while (actualSeqIndex < actualSeq.size) {
                val matchValues = mutableListOf<Int>()
                for (j in target.parts.indices) {
                    if (resultIndex < target.parts[j].absPitches.size) matchValues.add(target.parts[j].absPitches[resultIndex])
                    else {
                        matchValues.add(-1)
                    }
                }
                val newAbsPitch = actualSeq[actualSeqIndex]
                val isValid = matchValues.map {
                    it == -1 || Insieme.isIntervalInSet(intervalSet.toIntArray(), it, newAbsPitch)
                }.fold(true) { acc, b -> acc && b }
                if (isValid) {
                    actualSeqIndex++
                    result.add(newAbsPitch)
                } else {
                    result.add(-1)
                }
                resultIndex++
            }
            val resultAbsPart = AbsPart(result, rowForm, transpose, delay)
            return Counterpoint(listOf(*target.parts.toTypedArray(), resultAbsPart), intervalSet).cutExtraNotes().apply { this.findAndSetEmptiness() }
        }


        fun findAllCounterpointsWithRepeatedSequence(
            target: Counterpoint, sequence: List<Int>,
            intervalSet: List<Int>, deepness: Int
        ): List<Counterpoint> {
            val maxSize: Int = target.parts.maxOf { it.absPitches.size }
            val nRepetitions = if (sequence.size > maxSize) 2 else maxSize / sequence.size + 1
            val repeatedSequence = Collections.nCopies(nRepetitions, sequence).flatten()
            return findAllCounterpoints(target, repeatedSequence, intervalSet, deepness)
        }

        fun findAllCounterpoints(
            target: Counterpoint,
            sequence: List<Int>,
            intervalSet: List<Int>,
            deepness: Int
        ): List<Counterpoint> {
            val result = mutableListOf<Counterpoint>()
            for (delay in 0 until deepness) {
                var counterpoint: Counterpoint
                for (transpose in 0 until 12) {
                    for (form in 0 until 4) {
                        counterpoint = findCounterpoint(
                            target,
                            sequence,
                            intervalSet,
                            delay,
                            transpose,
                            values()[form]
                        )
                        result.add(counterpoint)
                    }
                }
            }
            return result
        }

        fun explodeRowForms(
            counterpoint: Counterpoint,
            rowFormsFlags: Int,
            nNotesToSkip: Int = 0,
            addFinal: Boolean = false
        ): Counterpoint {
            //val separator = rowFormsFlags and 0b10000 != 0
            val separator = nNotesToSkip > 0
            val separatorCounterpoint: Counterpoint? = if (separator)
                Counterpoint.createSeparatorCounterpoint(counterpoint.parts.size, nNotesToSkip)
            else null
            val original = counterpoint.normalizePartsSize(true)
            var result = original.clone()
            result = if (separator) result.enqueue(separatorCounterpoint!!) else result
            result =
                if (rowFormsFlags and RowForm.RETROGRADE.flag != 0) result.enqueue(original.retrograde()) else result
            result = if (separator) result.enqueue(separatorCounterpoint!!) else result
            result =
                if (rowFormsFlags and RowForm.INVERSE.flag != 0) result.enqueue(original.inverse()) else result
            result = if (separator) result.enqueue(separatorCounterpoint!!) else result
            result = if (rowFormsFlags and RowForm.INV_RETROGRADE.flag != 0) result.enqueue(
                original.inverse().retrograde()
            ) else result
            result = if (separator && addFinal) result.enqueue(separatorCounterpoint!!) else result
            return result.cutExtraNotes().apply { this.findAndSetEmptiness() }
        }

        fun explodeRowFormsAddingCps(
            counterpoints: List<Counterpoint?>,
            rowForms: List<Pair<Int, Int>> = listOf(Pair(1, 1)),
            nNotesToSkip: Int = 0
        ): Counterpoint {
            //val separator = rowFormsFlags and 0b10000 != 0
            val nParts = counterpoints.maxByOrNull { it?.parts?.size ?: 0 }?.parts?.size ?: 0
            if (nParts == 0) return empty()
            val separator = nNotesToSkip > 0 && rowForms.any { it.second < 0 }
            val containsShadows = rowForms.any { it.first < 0 }
            val separatorCounterpoint: Counterpoint? = if (separator)
                Counterpoint.createSeparatorCounterpoint(nParts, nNotesToSkip)
            else null

            var result = empty(nParts)
            val actualCounterpoints =
                counterpoints.map {
                    it?.normalizePartsSize(false)
                        ?.shiftDown((nParts - it.parts.size) shr 1)
                }
            val shadowCounterpoints = if (containsShadows)
                actualCounterpoints.map {
                    it?.tritoneSubstitution()
                } else listOf()

            rowForms.forEach { rowForm ->
                val original = if (rowForm.first > 0) actualCounterpoints[rowForm.first - 1]
                else shadowCounterpoints[rowForm.first.absoluteValue - 1]

                original?.let {
                    result = when (rowForm.second) {
                        1 -> result.enqueue(original.clone())
                        2 -> result.enqueue(original.inverse())
                        3 -> result.enqueue(original.retrograde())
                        4 -> result.enqueue(original.inverse().retrograde())
                        -1 -> result.enqueue(original.clone()).enqueue(separatorCounterpoint!!)
                        -2 -> result.enqueue(original.inverse()).enqueue(separatorCounterpoint!!)
                        -3 -> result.enqueue(original.retrograde())
                            .enqueue(separatorCounterpoint!!)
                        -4 -> result.enqueue(original.inverse().retrograde())
                            .enqueue(separatorCounterpoint!!)
                        else -> result
                    }
                }

            }

            return result.cutExtraNotes()//.apply { this.findAndSetEmptiness() }
        }

        fun explodeRowForms(
            counterpoint: Counterpoint,
            rowForms: List<Int> = listOf(1),
            nNotesToSkip: Int = 0
        ): Counterpoint {
            //val separator = rowFormsFlags and 0b10000 != 0
            if (counterpoint.parts.isEmpty()) return counterpoint
            val separator = nNotesToSkip > 0 && rowForms.any { it < 0 }
            val separatorCounterpoint: Counterpoint? = if (separator)
                Counterpoint.createSeparatorCounterpoint(counterpoint.parts.size, nNotesToSkip)
            else null
            val original = counterpoint.normalizePartsSize(true)
            var result = when (rowForms[0].absoluteValue) {
                1 -> original.clone()
                2 -> original.inverse()
                3 -> original.retrograde()
                4 -> original.inverse().retrograde()
                else -> original.clone()
            }
            result = if (rowForms[0] < 0) result.enqueue(separatorCounterpoint!!) else result
            if (rowForms.size > 1) {
                (1 until rowForms.size).forEach {
                    result = when (rowForms[it]) {
                        1 -> result.enqueue(original.clone())
                        2 -> result.enqueue(original.inverse())
                        3 -> result.enqueue(original.retrograde())
                        4 -> result.enqueue(original.inverse().retrograde())
                        -1 -> result.enqueue(original.clone()).enqueue(separatorCounterpoint!!)
                        -2 -> result.enqueue(original.inverse()).enqueue(separatorCounterpoint!!)
                        -3 -> result.enqueue(original.retrograde()).enqueue(separatorCounterpoint!!)
                        -4 -> result.enqueue(original.inverse().retrograde())
                            .enqueue(separatorCounterpoint!!)
                        else -> result
                    }
                }
            }
            return result.cutExtraNotes().apply { this.findAndSetEmptiness() }
        }

        fun addBestPedal(
            counterpoint: Counterpoint,
            intervalSet: List<Int>
        ): Pair<Counterpoint, List<Int>> {
            val alreadyPresentPedals: List<Int> = (0..11).filter { pitch ->
                counterpoint.parts.map { it.absPitches }.any { it.all { it == pitch } }
            }

            data class Pedal(val pitch: Int, val intervalCounts: ArrayList<Int>)

            val pedals = mutableListOf<Pedal>()
            val nSize = counterpoint.maxSize()
            for (pitch in 0..11) {
                if (alreadyPresentPedals.contains(pitch)) continue
                var intervalCount = ArrayList(listOf(0, 0, 0, 0, 0, 0, 0))
                for (part in counterpoint.parts) {
                    Insieme.incrementIntervalCount(intervalCount, pitch, part.absPitches)
                }
                pedals.add(Pedal(pitch, intervalCount))
            }
            return if (pedals.isNotEmpty()) {
                val sortedPedals = pedals.sortedBy {
                    Insieme.intervalSetDifference(
                        it.intervalCounts,
                        intervalSet
                    )
                }
                val minDifference = sortedPedals.take(1)
                    .map { Insieme.intervalSetDifference(it.intervalCounts, intervalSet) }[0]
                val bestPedal: Pedal =
                    sortedPedals.filter {
                        Insieme.intervalSetDifference(
                            it.intervalCounts,
                            intervalSet
                        ) == minDifference
                    }
                        .minByOrNull {
                            Insieme.intervalSetDifferenceCount(
                                it.intervalCounts,
                                intervalSet
                            )
                        }
                        ?: sortedPedals.take(1)[0]
                Pair(
                    counterpoint.addPedal(bestPedal.pitch),
                    Insieme.convertIntervalCountToIntervalSet(bestPedal.intervalCounts).toList()
                )
            } else {
                Pair(counterpoint, intervalSet)
            }
        }

        fun addPedals(
            nPedals: Int,
            counterpoint: Counterpoint,
            intervalSet: List<Int>,
            nPartsLimit: Int = 12
        ): Counterpoint {
            return (0 until nPedals).fold(counterpoint) { accCounterpoint, _ ->
                accCounterpoint.addBestPedal(intervalSet)
            }.cutExtraParts(nPartsLimit).apply { this.findAndSetEmptiness() }
        }

        fun createFromIntList(absPitches: List<Int>): Counterpoint {
            return Counterpoint(mutableListOf(AbsPart(absPitches.toMutableList()))).cutExtraNotes().apply {
                findAndSetEmptiness()
            }
        }
        fun createFromIntLists(absPitchesLists: List<List<Int>>): Counterpoint {
            val absParts = absPitchesLists.map{AbsPart(it.toMutableList())}
            return Counterpoint(absParts)
        }
        suspend fun findMazesWithRowForms(context: CoroutineContext, sequences: List<List<Int>>,
                              intervalSet: List<Int>, msTimeLimit: Long = 30000L): List<Counterpoint> {
            val nParts = sequences.size
            //println("SEQUENCES FOR MAZE: $sequences")
            if (sequences.all{it.isEmpty()}) return listOf(Counterpoint.empty(sequences.size, 1))
            if (sequences.all{it.isEmpty() || it.all{ absPitch -> absPitch == -1}}) return listOf(Counterpoint.empty(sequences.size, sequences.maxOf{it.size}))
            if (nParts == 0) return listOf()
            if (nParts == 1) return listOf(Counterpoint.createFromIntList(sequences[0]))
            val result = mutableListOf<Counterpoint>()
            val rows = (0 until 4.0.pow(nParts.toDouble()).toInt())
                .map { it.toString(radix = 4).padStart(nParts, '0').reversed() }
            val job = context.job
            var count = 0
            val start = System.currentTimeMillis()
            for (actionRow in rows){
                if(!job.isActive || System.currentTimeMillis() - start > msTimeLimit) break
                val tuple = mutableListOf<List<Int>>()
                for (partIndex in 0 until nParts){
                    val sequence = sequences[partIndex]
                    when (actionRow[partIndex]){
                        '0' -> tuple.add(sequence.toList())
                        '1' ->  tuple.add(Insieme.invertAbsPitches(sequence.toIntArray()).toList())
                        '2' -> tuple.add(sequence.reversed())
                        else -> tuple.add(Insieme.invertAbsPitches(sequence.toIntArray()).toList().reversed())
                    }
                }
                count++
                result.addAll(findMazes(context, tuple, intervalSet, 5).sortedBy { it.emptiness }.take(1))
            }
            return result.toList()
        }
        suspend fun findMazes(context: CoroutineContext, sequences: List<List<Int>>,
                      intervalSet: List<Int>, maxLimit: Int = 5): List<Counterpoint> {
            val nParts = sequences.size
            if (nParts == 0) return listOf()
            if (nParts == 1) return listOf(Counterpoint.createFromIntList(sequences[0]))
            val result = mutableListOf(List(nParts) { listOf<Int>() })
            val indices = IntArray(nParts) { 0 }
            val actions = (0 until 3.0.pow(nParts.toDouble()).toInt())
                .map { it.toString(radix = 3).padStart(nParts, '0').reversed() }
                .filter{ string -> !string.all{ch -> ch == '2'}}
                .filter{ string -> !string.all{ch -> ch == '0'}}
            val formattedSequences = sequences.map{ it.cutAdjacentRepetitions() }
            val lastColumn = IntArray(nParts) { -1 }
            val partial = Array(nParts) { listOf<Int>() }
            extractMazes(context, nParts, formattedSequences, indices, actions,
                        lastColumn, intervalSet.toIntArray(), partial, result, maxLimit, 0)
            return result.map{createFromIntLists(it)}//.filter{ !it.isEmpty()}
        }

        private suspend fun extractMazes(context: CoroutineContext,
                                         nParts: Int, sequences: List<List<Int>>, indices: IntArray, actions: List<String>,
                                         lastColumn: IntArray, intervalSet: IntArray,
                                         partial: Array<List<Int>>, result: MutableList<List<List<Int>>>, maxLimit: Int, nResults: Int
        ) {
            val columnsAndIndicesIncr: MutableList<Pair<IntArray, MutableSet<Int>>> = mutableListOf()
            val job = context.job
            actionsLoop@ for (action in actions) {
                if (!job.isActive) break@actionsLoop
                val newColumn = IntArray(nParts){-1}
                val ends = BooleanArray(nParts) { false }
                val indicesToIncrement = mutableSetOf<Int>()
                for (partIndex in 0 until nParts) {
                    val sequence = sequences[partIndex]
                    val sequenceIndex = indices[partIndex]
                    if (sequenceIndex == sequence.size) ends[partIndex] = true
                    when (action[partIndex]) {
                        '1' -> {
                            if (sequenceIndex >= sequence.size) {
                                newColumn[partIndex] = -1
                            } else {
                                val newPitch = sequence[sequenceIndex]
                                newColumn[partIndex] = newPitch
                                indicesToIncrement.add(partIndex)
                            }
                        }
                        '0' -> {
                            newColumn[partIndex] = lastColumn[partIndex]
                        }
                        else -> Unit
                    }
                }
                if (Insieme.areAbsPitchesValid(newColumn, intervalSet)) {
                    if (ends.all { it }) {
                        val newRests = newColumn.count { it == -1 }
                        val newPartial = if(newRests == nParts || newColumn.contentEquals(lastColumn)) partial.map{ it }
                        else newColumn.mapIndexed { index, pitch -> partial[index] + pitch }
                        if(newPartial.all{ it.isNotEmpty() }) result.add(newPartial)
                    } else {
                        columnsAndIndicesIncr.add(Pair(newColumn, indicesToIncrement))
                    }
                }
            }
            val pairs = columnsAndIndicesIncr.sortedBy { pair -> pair.first.count { absPitch -> absPitch == -1 } }
                .filter{!lastColumn.contentEquals(it.first)}
            for(pair in pairs){
                if(!job.isActive) break
                val (newColumn, indicesToIncrement) = pair
                    val newIndices = indices.copyOf()
                    indicesToIncrement.forEach{newIndices[it]++}
                    val newPartial = newColumn.mapIndexed { index, pitch -> partial[index] + pitch }.toTypedArray()
                if(newColumn.count { it == -1 } != nParts){
                    extractMazes( context,
                        nParts, sequences, newIndices.copyOf(), actions,
                        newColumn.copyOf(), intervalSet, newPartial, result,
                        maxLimit, nResults+1
                    )
                    break
                }
            }
        }

        fun addingAbsPitchIsValid(
            pitches: IntArray,
            intervalSet: List<Int>,
            newAbsPitch: Int
        ): Boolean {
            if (newAbsPitch == -1) return true
            return pitches.map {
                it == -1 || Insieme.isIntervalInSet(intervalSet.toIntArray(), it, newAbsPitch)
            }.fold(true) { acc, b -> acc && b }
        }
        fun mazeTest(sequences: List<MutableList<Int>>){
            val pentatonicIntervalSet = listOf(2, 10, 3, 9, 4, 8, 5, 7)
            val ms = measureTimeMillis {
                runBlocking {
                    launch{
                        val mazes: List<Counterpoint>
                        val ms = measureTimeMillis {
                            mazes = Counterpoint.findMazes(this.coroutineContext, sequences, pentatonicIntervalSet, maxLimit = 3).sortedBy { it.emptiness }
                            mazes.take(12).forEach { it.displayInNotes(); println() }
                        }
                        //println("maze in $ms ms, nResults: ${mazes.size}")
                    }
                }
            }
        }
    }
}

//fun main(args : Array<String>){
//    val pentatonicIntervalSet = listOf(2, 10, 3, 9, 4, 8, 5, 7)
//    val absPitches1 = mutableListOf(0, 1, 2, 11, 2, 6, 9)
//    val absPitches2 = mutableListOf(2, 3, 4, 4)//5, 6, 7, 8, 9, 10,11,0,1)
//    val absPitches3 = mutableListOf(4, 5, 6)//, 3, 4, 5, 6, 7, 8, 9, 10,11,0)
//    val sequences = listOf(absPitches1, absPitches2, absPitches3)
//    val notes = listOf("C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#", "A", "Bb", "B", "__")
    //val ens = getEnsemble(12, EnsembleType.STRING_ORCHESTRA).apply { println(this) }
    //val mix = getEnsembleMix(12, listOf(EnsembleType.STRING_ORCHESTRA, EnsembleType.WOODWINDS)).apply { println(this) }
   // Counterpoint.mazeTest(sequences)
    //println(Insieme.areAbsPitchesValid(absPitches1.toIntArray(), pentatonicIntervalSet.toIntArray() ))
//    val pitches = listOf(-1,-1,2,3,8,-1,-1,-1,10,12,46,67,32,64,43,0,1,9,8,-1,-1,8)
//    val mssq = MelodySubSequencer(pitches.toIntArray())
//    mssq.assignVelocities(0.90f, 0.50f)
//    mssq.printSubSequences()
//    val absPitches1 = mutableListOf(-1,1,0,6,11,5,7,8,8,3,3,3,6,9)
//    val absPitches2 = mutableListOf(1,11,2,10,3,-1,9,6)
//    val absPitches3 = mutableListOf(1,11,2,10)
//    val absPitches1 = mutableListOf(0, 4, 7, 11, 2, 6, 9)
//    val absPitches2 = mutableListOf(-1, 0, 4, 7, 11, 2, 6, 9)
//    val absPitches3 = mutableListOf(-1,2, 0, -1, -1, 10, -1, 7, 5, 9, 10, 0, 2)
//    val absParts = listOf(
//        //AbsPart(absPitches1),
//        AbsPart(absPitches2),
//        AbsPart(absPitches3),
//    )
//    val pentatonicIntervalSet = listOf(2, 10, 3, 9, 4, 8, 5, 7)
//    val counterpoint = Counterpoint(absParts, pentatonicIntervalSet)
//    counterpoint.display()
//    val detection = counterpoint.detectParallelIntervals(listOf(0,1,11))
//    detection.forEach { println(it) }
//    val counterpoint2 = Counterpoint.findWave(counterpoint,pentatonicIntervalSet,3,
//        listOf(0,1,2),TREND.ASCENDANT_STATIC.directions)
//    val counterpoint3 = Counterpoint.findWave(counterpoint2,pentatonicIntervalSet,6,
//        listOf(0,1,2),TREND.ASCENDANT_STATIC.directions).displayInNotes()
    //val normCounterpoint = counterpoint.normalizePartsSize(true)
    //normCounterpoint.displayInNotes()
    //normCounterpoint.addCadenzas(listOf(1,11))//.displayInNotes()
//    println("emptiness: ${counterpoint.emptiness}")
//    val counterpointRound = counterpoint.normalizePartsSize(true).buildRound()
//    counterpointRound.display()
//    println("emptiness: ${counterpointRound.emptiness}")
//    println("emptiness: ${counterpointRound.findEmptiness()}")

//    Counterpoint.expand(counterpoint, 2).display()
//    val repeatedSequence = Collections.nCopies(3, absPitches1).flatten()
//    println(repeatedSequence)
//    val list = listOf(-1,-1,0,10,5,6,11,2,-1,-1,3,-1,10)
//    println(Insieme.linearMelody(4,list.toIntArray(),21,108).asList())
//}



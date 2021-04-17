package com.cristianovecchi.mikrokanon.AIMUSIC

import android.inputmethodservice.Keyboard
import android.os.Parcelable
import com.cristianovecchi.mikrokanon.composables.Clip
import com.cristianovecchi.mikrokanon.composables.NoteNamesIt
import com.cristianovecchi.mikrokanon.composables.randomClipSequence
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.math.abs

fun main(args : Array<String>){
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
    val counterpoint = Counterpoint(absParts, listOf(2, 10, 3, 9, 4, 8, 5, 7))
    Counterpoint.findAllFreeParts(counterpoint, listOf(2, 10, 3, 9, 4, 8, 5, 7) ).map { it.display() }
//    counterpoint.normalizePartsSize(true)
//    counterpoint.display()
//    val counterpoints = listOf(counterpoint, counterpoint)
//    counterpoints.map{it.spreadAsPossible()}
//    counterpoints[0].display()
    //counterpoint.populate().display()
//    val sequence = listOf(2,10,5,10,3,9,6,7)
//    val intervalSet = listOf(0,1,11,2,10,3,9)
//    val newCounterpoints = Counterpoint.findAllCounterpoints(counterpoint,sequence, intervalSet, 5)
//    newCounterpoints.sortedBy { it.emptiness }.reversed().forEach{it.display(); println()}
//
//    val seq1 = ArrayList<Clip>(randomClipSequence(NoteNamesIt.values().map{it.toString()},0,10, false))
//    Counterpoint.counterpointFromClipList(seq1).display()

//    val delay = 3
//    val transpose = 0
//    val rowForm = 3
//    val mikroKanon = MikroKanon.find2AbsPartMikroKanon(absPitches, intervalSet, delay, transpose, rowForm)
//    mikroKanon.display()
}
@Parcelize
data class Counterpoint(val parts: List<AbsPart>,
                        val intervalSet: List<Int> = (0..11).toList(),
                        var emptiness: Float? = null) : Parcelable {

    init {
        emptiness ?: findEmptiness().also<Float> { it -> emptiness = it }
    }
    fun clone(): Counterpoint {

        return Counterpoint(parts.map{it.clone()}, ArrayList(intervalSet), emptiness)
    }

    companion object {

        fun counterpointFromClipList(clipList: List<Clip>) : Counterpoint{
            return Counterpoint(listOf(AbsPart.absPartfromClipList(clipList)))
        }
        fun empty(): Counterpoint{
            return Counterpoint(emptyList(), emptyList(), 1.0f)
        }
        fun findFreePart(counterpoint: Counterpoint, intervalSet: List<Int>, startAbsPitch : Int ) : Counterpoint {
            var result = mutableListOf<Int>()
            val verticalList = listOf(0,1,2,3,4,5,6,7,8,9,10,11)
            val directions: Array<Int> = Insieme.extractDirectionsFromIntervalSet(verticalList.toTypedArray(), true)
            var index = 0
            val maxSize: Int = counterpoint.parts.maxOf { it.absPitches.size }
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
        fun findAllFreeParts(counterpoint: Counterpoint, intervalSet: List<Int> ) : List<Counterpoint> {
            val result = mutableListOf<Counterpoint>()
            (0..11).forEach() {
                result.add(findFreePart(counterpoint, intervalSet, it))
            }
            return result.distinctBy { it.parts }
        }
        fun findCounterpoint(target: Counterpoint, sequence: List<Int>, intervalSet: List<Int>,
                             delay: Int, transpose: Int, rowForm: RowForm) : Counterpoint{
            var result = mutableListOf<Int>()

            var actualSeq = sequence.toMutableList()
            when (rowForm){
                RowForm.INVERSE -> actualSeq = Insieme.invertAbsPitches(actualSeq.toIntArray()).toMutableList()
                RowForm.RETROGRADE -> actualSeq = actualSeq.reversed().toMutableList()
                RowForm.INV_RETROGRADE -> actualSeq = Insieme.invertAbsPitches(actualSeq.toIntArray()).reversedArray().toMutableList()
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


        fun findAllCounterpoints(target: Counterpoint, sequence: List<Int>, intervalSet: List<Int>, deepness: Int) : List<Counterpoint> {
            val result = mutableListOf<Counterpoint>()

            for(delay in 0 until deepness) {
                var counterpoint: Counterpoint
                for (transpose in 0 until 12) {
                    for (form in 0 until 4) {
                        counterpoint = findCounterpoint(target, sequence, intervalSet, delay, transpose, RowForm.values()[form])
                        result.add(counterpoint)
                    }
                }
            }

            return result
        }
    }
    fun normalizePartsSize(refreshEmptiness: Boolean){
        val maxSize: Int = parts.maxOf { it.absPitches.size }
        parts.forEach(){ absPart ->
            if(absPart.absPitches.size != maxSize){
                val diff = maxSize - absPart.absPitches.size
                (0 until diff).forEach(){ _ ->
                    absPart.absPitches.add(-1)
                }
            }
        }
        if(refreshEmptiness) this.emptiness = findEmptiness()
    }


    fun spreadAsPossible() : Counterpoint {
        val clone = this.clone() // cloning is necessary in a coroutine context
        clone.normalizePartsSize(false)
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

        return clone
    }


    fun isEmpty() : Boolean {
        return parts.isEmpty()
    }
    fun display() {
        parts.forEachIndexed { index, absPart ->
            println("Part #$index: ${Arrays.toString(absPart.absPitches.toIntArray())}")
        }
        println("Emptiness: $emptiness")
    }

    private fun findEmptiness() : Float {
        val maxSize = parts.maxOf { it.absPitches.size }
        val nCells = maxSize * parts.size
        if (nCells == 0) return 1.0f
        // considering the counterpoint like a grid and counting every empty cell
        val nEmptyNotes = parts.map{ it -> it.nEmptyNotes() + (maxSize - it.absPitches.size)  }
            .reduce { acc, nNotes -> nNotes + acc}
        if (nEmptyNotes == 0) return 0.0f
        // (100 : X = nCells : nEmptyNotes) / 100
        return nEmptyNotes.toFloat() / nCells
    }
}



@Parcelize
data class AbsPart(val absPitches: MutableList<Int>, val rowForm: RowForm = RowForm.UNRELATED, val transpose: Int = 0, val delay: Int = 0) : Parcelable {
    companion object{
        fun absPartfromClipList(clipList: List<Clip>) : AbsPart {
            return AbsPart(clipList.map { it -> it.abstractNote }.toMutableList())
        }
    }
    fun clone(): AbsPart{
       return AbsPart(ArrayList(absPitches),rowForm, transpose, delay)
    }
    fun nEmptyNotes() : Int {
        return absPitches.count { it == -1 }
    }
}
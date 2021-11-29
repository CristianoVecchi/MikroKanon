package com.cristianovecchi.mikrokanon.AIMUSIC

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.lang.Math.abs
import java.util.ArrayList

@Parcelize
data class AbsPart(val absPitches: MutableList<Int>, val rowForm: RowForm = RowForm.UNRELATED, val transpose: Int = 0, val delay: Int = 0) :
    Parcelable {
    companion object{
        fun absPartfromClipList(clipList: List<Clip>) : AbsPart {
            return AbsPart(clipList.map { it.abstractNote }.toMutableList())
        }

        fun emptyPart(nRests: Int = 0): AbsPart {
            return AbsPart( MutableList(nRests){ -1 } )
        }

        fun fill(pitch: Int, size: Int): AbsPart {
            return AbsPart((0 until size).map{pitch}.toMutableList())
        }

        fun analyzeAbsPitches(absPitches: List<Int>): List<Int> {
            val result = mutableListOf(0,0,0,0,0,0,0)
            if (absPitches.isEmpty()) return result
            val intervals = listOf(listOf(1,11),listOf(2,10),listOf(3,9),listOf(4,8),listOf(5,7),listOf(6),listOf(0))
            val size = absPitches.size
            for(i in intervals.indices){
                for(j in absPitches.indices){
                    if(intervals[i].contains(abs(absPitches[ (j+1) % size ] - absPitches[j]))) result[i]++
                }
            }
            return result
        }

        val INVERTED_PITCHES = (11 downTo 0).toList()
    }
    fun inverse(): AbsPart{
        val newAbsPitches = mutableListOf<Int>()
        val newRowForm = when (rowForm){
            RowForm.ORIGINAL -> RowForm.INVERSE
            RowForm.INVERSE -> RowForm.ORIGINAL
            RowForm.RETROGRADE -> RowForm.INV_RETROGRADE
            RowForm.INV_RETROGRADE -> RowForm.RETROGRADE
            RowForm.UNRELATED -> RowForm.UNRELATED
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
    fun enqueue(absPart: AbsPart): AbsPart {
        val newAbsPitches = absPitches.toMutableList().also { it.addAll(absPart.absPitches)}
        return AbsPart(newAbsPitches, rowForm, transpose, delay)
    }
    fun enqueueFilling(nNotes: Int, value: Int = -1): AbsPart {
        if(nNotes == 0) return this
        val newAbsPitches =  absPitches + MutableList<Int>(nNotes){value}
        return AbsPart(newAbsPitches.toMutableList(), rowForm, transpose, delay)
    }
    fun fillAndEnqueue(nNotes: Int, value: Int = -1): AbsPart {
        if(nNotes == 0) return this
        val newAbsPitches = MutableList<Int>(nNotes){value} + absPitches
        return AbsPart(newAbsPitches.toMutableList(), rowForm, transpose, delay)
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

    fun transpose(transposition: Int): AbsPart {
        val newPart = absPitches.map{ if(it != -1) it + transposition else -1}.map{ if(it>11) it - 12 else it}
        return AbsPart(newPart.toMutableList(),rowForm,transpose,delay)
    }

    fun divideWithSubSequencer(octave: Int, range: IntRange, melodyType: Int): List<AbsPart> {
        val actualPitches = Insieme.findMelody(octave, absPitches.toIntArray(), range.first, range.last, melodyType).also{println(it.toList())}
        val mssq = MelodySubSequencer(actualPitches)
        val subSequences = mssq.subSequences
        //mssq.printSubSequences()
        val partAscendant = IntArray(this.absPitches.size).apply{fill(-1)}
        val partDescendant = IntArray(this.absPitches.size).apply{fill(-1)}
        subSequences.forEach { ss ->
            var lastQuality = MelodySubSequencer.SubSeqQuality.ASCENDENT
            when (ss.quality!!){
                MelodySubSequencer.SubSeqQuality.ASCENDENT -> {
                    lastQuality = MelodySubSequencer.SubSeqQuality.ASCENDENT
                    if(ss.nNotes > 1) (ss.start until ss.start+ss.nNotes).forEach { partAscendant[it] = absPitches[it] }
                }
                MelodySubSequencer.SubSeqQuality.DESCENDENT -> {
                    lastQuality = MelodySubSequencer.SubSeqQuality.DESCENDENT
                    if(ss.nNotes > 1) (ss.start until ss.start+ss.nNotes).forEach { partDescendant[it] = absPitches[it] }
                }
                MelodySubSequencer.SubSeqQuality.EQUAL ->
                    if(lastQuality == MelodySubSequencer.SubSeqQuality.ASCENDENT){
                        (ss.start until ss.start+ss.nNotes).forEach { partAscendant[it] = absPitches[it] }
                    } else {
                        (ss.start until ss.start+ss.nNotes).forEach { partDescendant[it] = absPitches[it] }
                    }

                MelodySubSequencer.SubSeqQuality.INTERRUPTED -> (ss.start until ss.start+ss.nNotes).forEach { partAscendant[it] = absPitches[it] }
            }
        }
        var countAsc = 0
        var countDesc = 0
        (1 until absPitches.size).forEach { i ->
            if(partAscendant[i] == partDescendant[i]) {
                if (partAscendant[i - 1] == partAscendant[i]) {
                    partDescendant[i] = -1
                } else if (partDescendant[i - 1] == partDescendant[i]) {
                    partAscendant[i] = -1
                } else if (countAsc <= countDesc) {
                    partDescendant[i] = -1
                } else {
                    partAscendant[i] = -1
                }
            }
            if(partAscendant[i] != -1) countAsc++
            if(partDescendant[i] != -1) countDesc++
        }
        //partAscendant.forEachIndexed { index, i -> if(i == partDescendant[index])  partDescendant[index] = -1}
        return listOf(copy(absPitches = partAscendant.toMutableList()), copy(absPitches = partDescendant.toMutableList()) )
    }
    fun detectIntervalsReportingBothNotes(intervalSet: List<Int>): Array<Boolean>{
        val intervalSetArray = intervalSet.toIntArray()
        val nNotes = absPitches.size
        val result = Array<Boolean>(nNotes) {false}
        var index = 0
        while (index < nNotes - 2){
            if(Insieme.isIntervalInSet(intervalSetArray, absPitches[index], absPitches[index+1])){
                result[index] = true; result[index+1] = true
                index += 2
            } else {
                index++
            }
        }
        if(Insieme.isIntervalInSet(intervalSetArray, absPitches[nNotes-1], absPitches[0])){
            result[nNotes-1] = true; result[0] = true}
        return result
    }
    fun setAbsPitchesByChecks(checks: Array<Boolean>, value: Int = -1): AbsPart{
        if(checks.size<this.absPitches.size) return this
        val newPart = this.absPitches.mapIndexed{ index, oldValue -> if(checks[index]) value else oldValue }
        return this.copy(absPitches = newPart.toMutableList())
    }
    fun subSequences(endSequence: Int = -1): List<Pair<Int, Int>>{
        val result = mutableListOf<Pair<Int, Int>>()
        var start = 0
        var index = 0
        val pitches = absPitches + endSequence
        while( index < pitches.size ) {
            if (pitches[index] == endSequence){
                if(index-start > 0) result.add(Pair(start, index))
                index++
                start = index
            } else {
                index++
            }
        }

        return result.toList().also{println("SubSequences: " + it)}
    }
    fun subSequencesLastRepetition(endSequence: Int = -1): List<Pair<Int, Int>>{
        val result = subSequences(endSequence)
        val checkedResult = mutableListOf<Pair<Int, Int>>()
        result.forEach{ pair ->
            val sequenceSize = pair.second - pair.first
            val divisors = (sequenceSize downTo 1).filter{ sequenceSize % it == 0 }.also{println("divisors: "+it)}
            if (sequenceSize == 1 || divisors == listOf(1)) {
                checkedResult.add(pair)
            } else {
                val sequence = this.absPitches.subList(pair.first, pair.second)
                for(divisor in divisors){
                    if(divisor == 1) {checkedResult.add(pair); break}
                    val nNotes = sequenceSize / divisor
                    val subSequences = sequence.chunked(nNotes)
                    var isRepeated = true
                    for(subs in (1 until subSequences.size)){
                        if(subSequences[0] != subSequences[subs]) {
                            isRepeated = false
                            break
                        }
                    }
                    if(isRepeated) {
                        checkedResult.add(Pair( pair.second - nNotes,pair.second))
                        break
                    }
                }
            }
        }
        return checkedResult.toList().also{println("SubSequencesRepetitions: " + it)}
    }



}
fun main(args : Array<String>){
    val part = AbsPart(mutableListOf(0,4,5,1,-1,-1,4,4,5,-1,5,7,8,1,1))
    println(part.subSequences())
}
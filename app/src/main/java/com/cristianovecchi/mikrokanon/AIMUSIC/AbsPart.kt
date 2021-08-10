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

        fun emptyPart(nNotes: Int = 0): AbsPart {
            val emptyPart = (0 until nNotes).map{ -1 }.toMutableList()
            return AbsPart(emptyPart)
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
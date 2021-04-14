package com.cristianovecchi.mikrokanon.AIMUSIC

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class MikroKanon(val parts: List<AbsPart>,
                      val intervalSet: List<Int>,
                      val emptiness: Float) : Parcelable {
    fun display(){
        println("DUX:   ${Arrays.toString(parts[0].absPitches.toIntArray())}" )
        println("COMES: ${Arrays.toString(parts[1].absPitches.toIntArray())}" )
        println("Emptiness: $emptiness")
        println("Interval Set: ${Arrays.toString(intervalSet.toIntArray())}" )

    }

    fun toCounterpoint(): Counterpoint {
        return Counterpoint(this.parts, this.intervalSet)
    }

    companion object {

        fun find2AbsPartMikroKanon(absPitches: List<Int>, intervalSet: List<Int>, delay: Int, transpose: Int, rowForm: Int): MikroKanon{
            if(delay == 0 && rowForm == 0){
                return MikroKanon(listOf(
                    AbsPart(mutableListOf(),RowForm.values()[rowForm],transpose, delay),
                    AbsPart(mutableListOf(),RowForm.values()[rowForm],transpose, delay)
                ),intervalSet,0f)
            }
            var list2 = absPitches.toMutableList()
            when (rowForm){
                1 -> list2 = Insieme.invertAbsPitches(list2.toIntArray()).toMutableList()
                2 -> list2 = list2.reversed().toMutableList()
                3 -> list2 = Insieme.invertAbsPitches(list2.toIntArray()).reversedArray().toMutableList()
                else -> {}
            }
            list2 = list2.map {Insieme.transposeAbsPitch(it, transpose) }.toMutableList()
            //println("list1: ${Arrays.toString(absPitches.toIntArray())}" )
            //println("list2: ${Arrays.toString(list2.toIntArray())}" )

            val dux = AbsPart( mutableListOf(), RowForm.ORIGINAL, 0 ,0 )
            val comes = AbsPart( mutableListOf(), RowForm.values()[rowForm], transpose, delay)
            var nRests = 0
            for(i in 0 until delay) {
                dux.absPitches.add(absPitches[i])
                comes.absPitches.add(-1)
            }
            for(i in 0 until delay) comes.absPitches.add(i + delay, list2[i])
            var listIndex = delay
            var index = delay
            while ( listIndex < absPitches.size) {
                val newPitch = absPitches[listIndex]
                val comesPitch = if(index > comes.absPitches.size -1) list2[listIndex] else comes.absPitches[index]
                if (comesPitch == - 1 || Insieme.isIntervalInSet(intervalSet.toIntArray(), newPitch, comesPitch)){
                    dux.absPitches.add(index,newPitch)
                    comes.absPitches.add(index+delay, list2[listIndex])
                    index ++
                    listIndex ++
                } else {
                    nRests ++
                    dux.absPitches.add(index, -1)
                    comes.absPitches.add(index+delay, -1)
                    index ++
                }
            }
            // 100: x = (size+delay) : rests
            //
            val emptiness = if (nRests == 0) 0f else (nRests * 100) / (comes.absPitches.size + delay).toFloat()

            return MikroKanon(listOf(dux,comes), intervalSet, emptiness)
        }

        fun findAll2AbsPartMikroKanons(absPitches: List<Int>, intervalSet: List<Int>, deepness: Int) : List<MikroKanon>{
            val result = mutableListOf<MikroKanon>()
            // delay = 0 CASE requires another algorhythm
            for(delay in 1 until deepness) {
                var mikroKanon: MikroKanon
                for (tr in 0 until 12) {
                    for (form in 0 until 4) {
                        mikroKanon = find2AbsPartMikroKanon(absPitches, intervalSet, delay, tr, form)
                        result.add(mikroKanon)
                    }
                }
            }

            return result
        }

    }
}



enum class RowForm {
    ORIGINAL, INVERSE, RETROGRADE, INV_RETROGRADE, UNRELATED
}

fun main(args : Array<String>){
    val absPitches = listOf(1,0,6,11,5,7,8,8,3,3,3,6,9)
    val intervalSet = listOf(1,11,2,10,3,9,6)

    val mikroKanons = MikroKanon.findAll2AbsPartMikroKanons(absPitches,intervalSet, 4)
    mikroKanons.forEach{it.display(); println()}

//    val delay = 3
//    val transpose = 0
//    val rowForm = 3
//    val mikroKanon = MikroKanon.find2AbsPartMikroKanon(absPitches, intervalSet, delay, transpose, rowForm)
//    mikroKanon.display()
}
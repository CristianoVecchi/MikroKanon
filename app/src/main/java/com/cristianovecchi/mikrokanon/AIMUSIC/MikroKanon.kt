package com.cristianovecchi.mikrokanon.AIMUSIC

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class MikroKanon(val parts: List<AbsPart>,
                      val intervalSet: List<Int> ) : Parcelable {
    fun display(){
        println("DUX:     ${Arrays.toString(parts[0].absPitches.toIntArray())}" )
        println("COMES:   ${Arrays.toString(parts[1].absPitches.toIntArray())}" )
        if(parts.size>1) println("COMES 2: ${Arrays.toString(parts[2].absPitches.toIntArray())}" )
        if(parts.size>2) println("COMES 3: ${Arrays.toString(parts[3].absPitches.toIntArray())}" )
        println("Interval Set: ${Arrays.toString(intervalSet.toIntArray())}" )
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

    fun toCounterpoint(): Counterpoint {
        return Counterpoint(this.parts, this.intervalSet)
    }

    companion object {

        fun find2AbsPartMikroKanon(
            absPitches: List<Int>,
            intervalSet: List<Int>,
            delay: Int,
            transpose: Int,
            rowForm: Int
        ): MikroKanon {
            if (delay == 0 && rowForm == 0) {
                return MikroKanon(
                    listOf(
                        AbsPart(mutableListOf(), RowForm.values()[rowForm], transpose, delay),
                        AbsPart(mutableListOf(), RowForm.values()[rowForm], transpose, delay)
                    ), intervalSet
                )
            }
            var list2 = absPitches.toMutableList()
            when (rowForm) {
                1 -> list2 = Insieme.invertAbsPitches(list2.toIntArray()).toMutableList()
                2 -> list2 = list2.reversed().toMutableList()
                3 -> list2 =
                    Insieme.invertAbsPitches(list2.toIntArray()).reversedArray().toMutableList()
                else -> {
                }
            }
            list2 = list2.map { Insieme.transposeAbsPitch(it, transpose) }.toMutableList()
            //println("list1: ${Arrays.toString(absPitches.toIntArray())}" )
            //println("list2: ${Arrays.toString(list2.toIntArray())}" )

            val dux = AbsPart(mutableListOf(), RowForm.ORIGINAL, 0, 0)
            val comes = AbsPart(mutableListOf(), RowForm.values()[rowForm], transpose, delay)
            var nRests = 0
            for (i in 0 until delay) {
                dux.absPitches.add(absPitches[i])
                comes.absPitches.add(-1)
            }
            for (i in 0 until delay) comes.absPitches.add(i + delay, list2[i])
            var listIndex = delay
            var index = delay
            while (listIndex < absPitches.size) {
                val newPitch = absPitches[listIndex]
                val comesPitch =
                    if (index > comes.absPitches.size - 1) list2[listIndex] else comes.absPitches[index]
                if (comesPitch == -1 || Insieme.isIntervalInSet(
                        intervalSet.toIntArray(),
                        newPitch,
                        comesPitch
                    )
                ) {
                    dux.absPitches.add(index, newPitch)
                    comes.absPitches.add(index + delay, list2[listIndex])
                    index++
                    listIndex++
                } else {
                    nRests++
                    dux.absPitches.add(index, -1)
                    comes.absPitches.add(index + delay, -1)
                    index++
                }
            }

            return MikroKanon(listOf(comes, dux), intervalSet)
        }

        fun find3AbsPartMikroKanon(
            absPitches: List<Int>, intervalSet: List<Int>,
            delay1: Int, transpose1: Int, rowForm1: Int,
            delay2: Int, transpose2: Int, rowForm2: Int
        ): MikroKanon {
            if ((delay1 == 0 || delay2 == 0) && rowForm1 == 0 && rowForm2 == 0) {
                return MikroKanon(
                    listOf(
                        AbsPart(mutableListOf(), RowForm.values()[rowForm1], transpose1, 0),
                        AbsPart(mutableListOf(), RowForm.values()[rowForm1], transpose1, delay1),
                        AbsPart(mutableListOf(), RowForm.values()[rowForm1], transpose1, delay2)
                    ), intervalSet
                )
            }
            var list2 = absPitches.toMutableList()
            var list3 = absPitches.toMutableList()
            when (rowForm1) {
                1 -> list2 = Insieme.invertAbsPitches(list2.toIntArray()).toMutableList()
                2 -> list2 = list2.reversed().toMutableList()
                3 -> list2 =
                    Insieme.invertAbsPitches(list2.toIntArray()).reversedArray().toMutableList()
                else -> {
                }
            }
            when (rowForm2) {
                1 -> list3 = Insieme.invertAbsPitches(list3.toIntArray()).toMutableList()
                2 -> list3 = list3.reversed().toMutableList()
                3 -> list3 =
                    Insieme.invertAbsPitches(list3.toIntArray()).reversedArray().toMutableList()
                else -> {
                }
            }
            list2 = list2.map { Insieme.transposeAbsPitch(it, transpose1) }.toMutableList()
            list3 = list3.map { Insieme.transposeAbsPitch(it, transpose2) }.toMutableList()

            val dux = AbsPart(mutableListOf(), RowForm.ORIGINAL, 0, 0)
            val comes1 = AbsPart(mutableListOf(), RowForm.values()[rowForm1], transpose1, delay1)
            val comes2 = AbsPart(mutableListOf(), RowForm.values()[rowForm2], transpose2, delay2)
            for (i in 0 until delay1) {
                comes1.absPitches.add(-1)
            }
            for (i in 0 until delay2) {
                comes2.absPitches.add(-1)
            }

            var absIndex = 0
            var duxIndex = 0
            while (absIndex < absPitches.size) {
                val duxPitch = absPitches[absIndex]
                val comes1Pitch = comes1.absPitches[duxIndex]
                val comes2Pitch = comes2.absPitches[duxIndex]

                if ((comes1Pitch == -1 || Insieme.isIntervalInSet(
                        intervalSet.toIntArray(),
                        duxPitch,
                        comes1Pitch
                    ))
                    && (comes2Pitch == -1 || Insieme.isIntervalInSet(
                        intervalSet.toIntArray(),
                        duxPitch,
                        comes2Pitch
                    ))
                    && (comes2.absPitches[duxIndex + delay1] == -1 || Insieme.isIntervalInSet(
                        intervalSet.toIntArray(),
                        list2[absIndex], comes2.absPitches[duxIndex + delay1]
                    ))
                ) {
                    dux.absPitches.add(duxIndex, duxPitch)
                    comes1.absPitches.add(duxIndex + delay1, list2[absIndex])
                    comes2.absPitches.add(duxIndex + delay2, list3[absIndex])
                    absIndex++
                    duxIndex++
                } else {
                    dux.absPitches.add(duxIndex, -1)
                    comes1.absPitches.add(duxIndex + delay1, -1)
                    comes2.absPitches.add(duxIndex + delay2, -1)
                    duxIndex++
                }
            }

            return MikroKanon(listOf(comes2, comes1, dux), intervalSet)
        }

        fun find4AbsPartMikroKanon(
            absPitches: List<Int>, intervalSet: List<Int>,
            delay1: Int, transpose1: Int, rowForm1: Int,
            delay2: Int, transpose2: Int, rowForm2: Int,
            delay3: Int, transpose3: Int, rowForm3: Int,
        ): MikroKanon {
            if ((delay1 == 0 || delay2 == 0 || delay3 == 0) && rowForm1 == 0 && rowForm2 == 0) {
                return MikroKanon(
                    listOf(
                        AbsPart(mutableListOf(), RowForm.values()[rowForm1], transpose1, 0),
                        AbsPart(mutableListOf(), RowForm.values()[rowForm1], transpose1, delay1),
                        AbsPart(mutableListOf(), RowForm.values()[rowForm1], transpose1, delay2),
                        AbsPart(mutableListOf(), RowForm.values()[rowForm1], transpose1, delay3)
                    ), intervalSet
                )
            }
            var list2 = absPitches.toMutableList()
            var list3 = absPitches.toMutableList()
            var list4 = absPitches.toMutableList()
            when (rowForm1) {
                1 -> list2 = Insieme.invertAbsPitches(list2.toIntArray()).toMutableList()
                2 -> list2 = list2.reversed().toMutableList()
                3 -> list2 =
                    Insieme.invertAbsPitches(list2.toIntArray()).reversedArray().toMutableList()
                else -> {
                }
            }
            when (rowForm2) {
                1 -> list3 = Insieme.invertAbsPitches(list3.toIntArray()).toMutableList()
                2 -> list3 = list3.reversed().toMutableList()
                3 -> list3 =
                    Insieme.invertAbsPitches(list3.toIntArray()).reversedArray().toMutableList()
                else -> {
                }
            }
            when (rowForm3) {
                1 -> list4 = Insieme.invertAbsPitches(list4.toIntArray()).toMutableList()
                2 -> list4 = list4.reversed().toMutableList()
                3 -> list4 =
                    Insieme.invertAbsPitches(list4.toIntArray()).reversedArray().toMutableList()
                else -> {
                }
            }
            list2 = list2.map { Insieme.transposeAbsPitch(it, transpose1) }.toMutableList()
            list3 = list3.map { Insieme.transposeAbsPitch(it, transpose2) }.toMutableList()
            list4 = list4.map { Insieme.transposeAbsPitch(it, transpose3) }.toMutableList()

            val dux = AbsPart(mutableListOf(), RowForm.ORIGINAL, 0, 0)
            val comes1 = AbsPart(mutableListOf(), RowForm.values()[rowForm1], transpose1, delay1)
            val comes2 = AbsPart(mutableListOf(), RowForm.values()[rowForm2], transpose2, delay2)
            val comes3 = AbsPart(mutableListOf(), RowForm.values()[rowForm3], transpose3, delay3)
            for (i in 0 until delay1) {
                comes1.absPitches.add(-1)
            }
            for (i in 0 until delay2) {
                comes2.absPitches.add(-1)
            }
            for (i in 0 until delay3) {
                comes3.absPitches.add(-1)
            }

            var absIndex = 0
            var duxIndex = 0

            while (absIndex < absPitches.size) {
                val duxPitch = absPitches[absIndex]
                val comes1Pitch = comes1.absPitches[duxIndex]
                val comes2Pitch = comes2.absPitches[duxIndex]
                val comes3Pitch = comes3.absPitches[duxIndex]

                if ((comes1Pitch == -1 || Insieme.isIntervalInSet(
                        intervalSet.toIntArray(),
                        duxPitch,
                        comes1Pitch
                    ))
                    && (comes2Pitch == -1 || Insieme.isIntervalInSet(
                        intervalSet.toIntArray(),
                        duxPitch,
                        comes2Pitch
                    ))
                    && (comes3Pitch == -1 || Insieme.isIntervalInSet(
                        intervalSet.toIntArray(),
                        duxPitch,
                        comes3Pitch
                    ))
                    && (comes2.absPitches[duxIndex + delay1] == -1 || Insieme.isIntervalInSet(
                        intervalSet.toIntArray(),
                        list2[absIndex], comes2.absPitches[duxIndex + delay1]
                    ))
                    && (comes3.absPitches[duxIndex + delay1] == -1 || Insieme.isIntervalInSet(
                        intervalSet.toIntArray(),
                        list2[absIndex], comes3.absPitches[duxIndex + delay1]
                    ))
                    && (comes3.absPitches[duxIndex + delay2] == -1 || Insieme.isIntervalInSet(
                        intervalSet.toIntArray(),
                        list3[absIndex], comes3.absPitches[duxIndex + delay2]
                    ))
                ) {
                    dux.absPitches.add(duxIndex, duxPitch)
                    comes1.absPitches.add(duxIndex + delay1, list2[absIndex])
                    comes2.absPitches.add(duxIndex + delay2, list3[absIndex])
                    comes3.absPitches.add(duxIndex + delay3, list4[absIndex])
                    absIndex++
                    duxIndex++
                } else {
                    dux.absPitches.add(duxIndex, -1)
                    comes1.absPitches.add(duxIndex + delay1, -1)
                    comes2.absPitches.add(duxIndex + delay2, -1)
                    comes3.absPitches.add(duxIndex + delay3, -1)
                    duxIndex++
                }
            }
            return MikroKanon(listOf(comes3, comes2, comes1, dux), intervalSet)
        }

        fun findAll2AbsPartMikroKanons(
            absPitches: List<Int>,
            intervalSet: List<Int>,
            deepness: Int
        ): List<MikroKanon> {
            val result = mutableListOf<MikroKanon>()
            // delay = 0 CASE requires another algorhythm
            for (delay in 1 until deepness) {
                var mikroKanon: MikroKanon
                for (tr in 0 until 12) {
                    for (form in 0 until 4) {
                        mikroKanon =
                            find2AbsPartMikroKanon(absPitches, intervalSet, delay, tr, form)
                        result.add(mikroKanon)
                    }
                }
            }
            return result
        }

        fun findAll3AbsPartMikroKanons(
            absPitches: List<Int>,
            intervalSet: List<Int>,
            deepness: Int
        ): List<MikroKanon> {
            val result = mutableListOf<MikroKanon>()
            // delay = 0 CASE requires another algorhythm
            for (delay1 in 1 until deepness) {
                for (delay2 in delay1 + 1 until deepness + delay1) {
                    var mikroKanon: MikroKanon
                    for (tr1 in 0 until 12) {
                        for (form1 in 0 until 4) {
                            for (tr2 in 0 until 12) {
                                for (form2 in 0 until 4) {
                                    mikroKanon = find3AbsPartMikroKanon(
                                        absPitches,
                                        intervalSet,
                                        delay1,
                                        tr1,
                                        form1,
                                        delay2,
                                        tr2,
                                        form2
                                    )
                                    result.add(mikroKanon)
                                }
                            }
                        }
                    }
                }
            }
            return result
        }


        fun findAll4AbsPartMikroKanons(
            absPitches: List<Int>,
            intervalSet: List<Int>,
            deepness: Int,
            emptinessGate: Float = 1.0f // no check
        ): List<MikroKanon> {
            val result = mutableListOf<MikroKanon>()
            // delay = 0 CASE requires another algorhythm
            try{
                for (delay1 in 1 until deepness) {
                    for (delay2 in delay1 + 1 until deepness + delay1) {
                        for (delay3 in delay2 + 1 until deepness + delay2) {

                            var mikroKanon: MikroKanon
                            for (tr1 in 0 until 12) {
                                for (form1 in 0 until 4) {
                                    for (tr2 in 0 until 12) {
                                        for (form2 in 0 until 4) {
                                            for (tr3 in 0 until 12) {
                                                for (form3 in 0 until 4) {
                                                    mikroKanon = find4AbsPartMikroKanon(
                                                        absPitches,
                                                        intervalSet,
                                                        delay1,
                                                        tr1,
                                                        form1,
                                                        delay2,
                                                        tr2,
                                                        form2,
                                                        delay3,
                                                        tr3,
                                                        form3
                                                    )
                                                    if(emptinessGate < 1.0f){
                                                        if(mikroKanon.findEmptiness() < emptinessGate) result.add(mikroKanon)
                                                    } else {
                                                        result.add(mikroKanon)
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (ex: OutOfMemoryError){
                return result
            }

            return result
        }
    }
}



enum class RowForm(val flag: Int) {
    ORIGINAL(1), INVERSE(2), RETROGRADE(4), INV_RETROGRADE(8), UNRELATED(0);
}

fun main(args : Array<String>){
    val absPitches = listOf(1,0,6,11,5,7,8,3,6,9)
    val intervalSet = listOf(1,11,2,10,3,9,6)

    val mikroKanons = MikroKanon.findAll4AbsPartMikroKanons(absPitches,intervalSet, 3)
    mikroKanons.forEach{it.display(); println()}

//    val mikroKanons = MikroKanon.findAll2AbsPartMikroKanons(absPitches,intervalSet, 4)
//    mikroKanons.forEach{it.display(); println()}

//    val delay = 3
//    val transpose = 0
//    val rowForm = 3
//    val mikroKanon = MikroKanon.find2AbsPartMikroKanon(absPitches, intervalSet, delay, transpose, rowForm)
//    mikroKanon.display()
}
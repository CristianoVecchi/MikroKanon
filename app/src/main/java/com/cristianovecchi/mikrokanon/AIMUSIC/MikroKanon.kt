package com.cristianovecchi.mikrokanon.AIMUSIC

import android.os.Build
import android.os.Parcelable
import com.cristianovecchi.mikrokanon.pmap
import kotlinx.android.parcel.Parcelize
import java.util.stream.Collectors

@Parcelize
data class MikroKanon(val parts: List<AbsPart>,
                      val intervalSet: List<Int> ) : Parcelable {
    fun display(){
        println("DUX:     ${parts[0].absPitches.toIntArray().contentToString()}" )
        println("COMES:   ${parts[1].absPitches.toIntArray().contentToString()}" )
        if(parts.size>1) println("COMES 2: ${parts[2].absPitches.toIntArray().contentToString()}" )
        if(parts.size>2) println("COMES 3: ${parts[3].absPitches.toIntArray().contentToString()}" )
        println("Interval Set: ${intervalSet.toIntArray().contentToString()}" )
    }

    private fun findEmptiness() : Float {
        val maxSize = parts.maxOf { it.absPitches.size }
        val nCells = maxSize * parts.size
        if (nCells == 0) return 1.0f
        // considering the counterpoint like a grid and counting every empty cell
        val nEmptyNotes = parts.map{ it.nEmptyNotes() + (maxSize - it.absPitches.size)  }
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
                val pitch = if(i < absPitches.size) absPitches[i] else -1
                dux.absPitches.add(pitch)
                comes.absPitches.add(-1)
            }
            for (i in 0 until delay) {
                val pitch = if(i < list2.size) list2[i] else -1
                comes.absPitches.add(i + delay, pitch)
            }
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
            absPitches: IntArray, intervalSet: IntArray,
            delay1: Int, transpose1: Int, rowForm1: IntArray,
            delay2: Int, transpose2: Int, rowForm2: IntArray
        ): MikroKanon {
            if (delay1 == 0 || delay2 == 0 || rowForm1.isEmpty() || rowForm2.isEmpty()) {
                return MikroKanon( listOf(AbsPart(absPitches.toMutableList(), transpose = transpose1, delay = 0), ), intervalSet.toList())
            }

            val list2 = rowForm1.map { Insieme.transposeAbsPitch(it, transpose1) }
            val list3 = rowForm2.map { Insieme.transposeAbsPitch(it, transpose2) }

            val dux = AbsPart(mutableListOf(), RowForm.ORIGINAL, 0, 0)
            val comes1 = AbsPart(mutableListOf(),RowForm.ORIGINAL , transpose1, delay1)
            val comes2 = AbsPart(mutableListOf(), RowForm.ORIGINAL, transpose2, delay2)
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
                        intervalSet,
                        duxPitch,
                        comes1Pitch
                    ))
                    && (comes2Pitch == -1 || Insieme.isIntervalInSet(
                        intervalSet,
                        duxPitch,
                        comes2Pitch
                    ))
                    && (comes2.absPitches[duxIndex + delay1] == -1 || Insieme.isIntervalInSet(
                        intervalSet,
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

            return MikroKanon(listOf(comes2, comes1, dux), intervalSet.toList())
        }

        fun find4AbsPartMikroKanon(
            absPitches: IntArray, intervalSet: IntArray,
            delay1: Int,  list2: IntArray,
            delay2: Int,  list3: IntArray,
            delay3: Int,  list4: IntArray,
        ): MikroKanon {
            if (delay1 == 0 || delay2 == 0 || delay3 == 0 || list2.isEmpty() || list3.isEmpty() || list4.isEmpty()) {
                return MikroKanon( listOf(AbsPart(absPitches.toMutableList(),  delay = 0), ), intervalSet.toList())
            }

            val dux = AbsPart(mutableListOf(), RowForm.UNRELATED,0, 0)
            val comes1 = AbsPart(mutableListOf(),  RowForm.UNRELATED, delay1)
            val comes2 = AbsPart(mutableListOf(), RowForm.UNRELATED,  delay2)
            val comes3 = AbsPart(mutableListOf(), RowForm.UNRELATED,  delay3)
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
                        intervalSet,
                        duxPitch,
                        comes1Pitch
                    ))
                    && (comes2Pitch == -1 || Insieme.isIntervalInSet(
                        intervalSet,
                        duxPitch,
                        comes2Pitch
                    ))
                    && (comes3Pitch == -1 || Insieme.isIntervalInSet(
                        intervalSet,
                        duxPitch,
                        comes3Pitch
                    ))
                    && (comes2.absPitches[duxIndex + delay1] == -1 || Insieme.isIntervalInSet(
                        intervalSet,
                        list2[absIndex], comes2.absPitches[duxIndex + delay1]
                    ))
                    && (comes3.absPitches[duxIndex + delay1] == -1 || Insieme.isIntervalInSet(
                        intervalSet,
                        list2[absIndex], comes3.absPitches[duxIndex + delay1]
                    ))
                    && (comes3.absPitches[duxIndex + delay2] == -1 || Insieme.isIntervalInSet(
                        intervalSet,
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
            return MikroKanon(listOf(comes3, comes2, comes1, dux), intervalSet.toList())
        }

        fun findAll2AbsPartMikroKanons(
            absPitches: List<Int>,
            intervalSet: List<Int>,
            depth: Int
        ): List<MikroKanon> {
            val result = mutableListOf<MikroKanon>()
            // delay = 0 CASE requires another algorhythm
            for (delay in 1 until depth) {
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

        suspend fun findAll3AbsPartMikroKanons(
            absPitches: List<Int>,
            intervalSet: List<Int>,
            depth: Int
        ): List<MikroKanon> {
            val result = mutableListOf<MikroKanon>()
            val intervalSetIntArray = intervalSet.toIntArray()
            val absPitchesIntArray = absPitches.toIntArray()
            val rowForms = listOf<IntArray>(absPitchesIntArray, Insieme.invertAbsPitches(absPitchesIntArray),
                absPitchesIntArray.reversedArray(), Insieme.invertAbsPitches(absPitchesIntArray).reversedArray())
            for (delay1 in 1 until depth) {
                for (delay2 in delay1 + 1 until depth + delay1) {
                    var mikroKanon: MikroKanon
                    for (tr1 in 0 until 12) {
                        for (form1 in 0 until 4) {
                            for (tr2 in 0 until 12) {
                                for (form2 in 0 until 4) {
                                    mikroKanon = find3AbsPartMikroKanon(
                                        absPitchesIntArray,
                                        intervalSetIntArray,
                                        delay1,
                                        tr1,
                                        rowForms[form1],
                                        delay2,
                                        tr2,
                                        rowForms[form2]
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
        suspend fun findAll3AbsPartMikroKanonsParallel(
            absPitches: List<Int>,
            intervalSet: List<Int>,
            depth: Int
        ): List<MikroKanon> {
            data class Params(
                              val delay1: Int, val transpose1: Int, val form1: Int,
                              val delay2: Int, val transpose2: Int, val form2: Int)
            val paramsList = mutableListOf<Params>()
            val intervalSetIntArray = intervalSet.toIntArray()
            val absPitchesIntArray = absPitches.toIntArray()
            val rowForms = listOf<IntArray>(absPitchesIntArray, Insieme.invertAbsPitches(absPitchesIntArray),
                absPitchesIntArray.reversedArray(), Insieme.invertAbsPitches(absPitchesIntArray).reversedArray())

            for (delay1 in 1 until depth) {
                for (delay2 in delay1 + 1 until depth + delay1) {
                    for (tr1 in 0 until 12) {
                        for (form1 in 0 until 4) {
                            for (tr2 in 0 until 12) {
                                for (form2 in 0 until 4) {
                                    paramsList.add(Params(delay1, tr1, form1, delay2, tr2, form2))
                                }
                            }
                        }
                    }
                }
            }
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                paramsList.parallelStream().map {  find3AbsPartMikroKanon(absPitchesIntArray, intervalSetIntArray,
                    it.delay1,it.transpose1, rowForms[it.form1],it.delay2, it.transpose2, rowForms[it.form2])}.collect(Collectors.toList())
            } else {
                paramsList.pmap {  find3AbsPartMikroKanon(absPitchesIntArray, intervalSetIntArray,
                    it.delay1,it.transpose1, rowForms[it.form1],it.delay2, it.transpose2, rowForms[it.form2])}
            }
        }



        suspend fun findAll4AbsPartMikroKanonsParallel(
            absPitches: List<Int>,
            intervalSet: List<Int>,
            depth: Int,
            emptinessGate: Float = 1.0f // no check
        ): List<MikroKanon> {
            data class Params(
                val delay1: Int, val transpose1: Int, val form1: Int,
                val delay2: Int, val transpose2: Int, val form2: Int,
                val delay3: Int, val transpose3: Int, val form3: Int
                )
            val paramsList = mutableListOf<Params>()
            val intervalSetIntArray = intervalSet.toIntArray()
            val absPitchesIntArray = absPitches.toIntArray()
            val rowForms = listOf<IntArray>(absPitchesIntArray, Insieme.invertAbsPitches(absPitchesIntArray),
            absPitchesIntArray.reversedArray(), Insieme.invertAbsPitches(absPitchesIntArray).reversedArray())
                val completeForms: List<List<IntArray>> = (0..11).map { transpose ->
                listOf(rowForms[0].map { pitch -> Insieme.transposeAbsPitch(pitch, transpose) }.toIntArray(),
                    rowForms[1].map { pitch -> Insieme.transposeAbsPitch(pitch, transpose) }.toIntArray(),
                    rowForms[2].map { pitch -> Insieme.transposeAbsPitch(pitch, transpose) }.toIntArray(),
                    rowForms[3].map { pitch -> Insieme.transposeAbsPitch(pitch, transpose) }.toIntArray()) }
            try{
                for (delay1 in 1 until depth) {
                    for (delay2 in delay1 + 1 until depth + delay1) {
                        for (delay3 in delay2 + 1 until depth + delay2) {
                            for (tr1 in 0 until 12) {
                                for (form1 in 0 until 4) {
                                    for (tr2 in 0 until 12) {
                                        for (form2 in 0 until 4) {
                                            for (tr3 in 0 until 12) {
                                                for (form3 in 0 until 4) {
                                                    paramsList.add(Params(delay1, tr1, form1, delay2, tr2, form2,delay3, tr3, form3))
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
                return MikroKanon.findAll2AbsPartMikroKanons(absPitches,intervalSet,2)
            }

            try{
                var gate = 1.0f
                val deepSearch = emptinessGate != 1.0f
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    paramsList.parallelStream().map{
                        val mk = find4AbsPartMikroKanon(absPitchesIntArray, intervalSetIntArray,
                            it.delay1, completeForms[it.transpose1][it.form1],
                            it.delay2, completeForms[it.transpose2][it.form2],
                            it.delay3, completeForms[it.transpose3][it.form3])
                        if(deepSearch) {
                            val emptiness = mk.findEmptiness()
                            if(emptiness > gate) null else {
                                println("NEW GATE: $gate")
                                gate = emptiness
                                mk
                            }
                        } else {
                            mk
                        }
                    }.filter{ it != null }.collect(Collectors.toList<MikroKanon>())
                } else {
                    return paramsList.pmap {
                        val mk = find4AbsPartMikroKanon(absPitchesIntArray, intervalSetIntArray,
                            it.delay1, completeForms[it.transpose1][it.form1],
                            it.delay2, completeForms[it.transpose2][it.form2],
                            it.delay3, completeForms[it.transpose3][it.form3])
                        if(deepSearch) {
                            val emptiness = mk.findEmptiness()
                            if(emptiness > gate) null else {
                                println("NEW GATE: $gate")
                                gate = emptiness
                                mk
                            }
                        } else {
                            mk
                        }
                    }.mapNotNull { it }
                }
            }catch (ex: OutOfMemoryError){
                return MikroKanon.findAll2AbsPartMikroKanons(absPitches,intervalSet,2)
            }

        }

        fun findAll4AbsPartMikroKanons(
            absPitches: List<Int>,
            intervalSet: List<Int>,
            depth: Int,
            emptinessGate: Float = 1.0f // no check
        ): List<MikroKanon> {
            val result = mutableListOf<MikroKanon>()
            val intervalSetIntArray = intervalSet.toIntArray()
            val absPitchesIntArray = absPitches.toIntArray()
            val rowForms = listOf<IntArray>(absPitchesIntArray, Insieme.invertAbsPitches(absPitchesIntArray),
                absPitchesIntArray.reversedArray(), Insieme.invertAbsPitches(absPitchesIntArray).reversedArray())
            val completeForms: List<List<IntArray>> = (0..11).map { transpose ->
                listOf(rowForms[0].map { pitch -> Insieme.transposeAbsPitch(pitch, transpose) }.toIntArray(),
                    rowForms[1].map { pitch -> Insieme.transposeAbsPitch(pitch, transpose) }.toIntArray(),
                    rowForms[2].map { pitch -> Insieme.transposeAbsPitch(pitch, transpose) }.toIntArray(),
                    rowForms[3].map { pitch -> Insieme.transposeAbsPitch(pitch, transpose) }.toIntArray()) }
            try{
                for (delay1 in 1 until depth) {
                    for (delay2 in delay1 + 1 until depth + delay1) {
                        for (delay3 in delay2 + 1 until depth + delay2) {

                            var mikroKanon: MikroKanon
                            for (tr1 in 0 until 12) {
                                for (form1 in 0 until 4) {
                                    for (tr2 in 0 until 12) {
                                        for (form2 in 0 until 4) {
                                            for (tr3 in 0 until 12) {
                                                for (form3 in 0 until 4) {
                                                    val mikroKanon = find4AbsPartMikroKanon(absPitchesIntArray, intervalSetIntArray,
                                                        delay1, completeForms[tr1][form1],
                                                        delay2, completeForms[tr2][form2],
                                                        delay3, completeForms[tr3][form3])
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
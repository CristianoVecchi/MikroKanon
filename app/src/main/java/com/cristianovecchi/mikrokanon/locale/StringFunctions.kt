package com.cristianovecchi.mikrokanon

import com.cristianovecchi.mikrokanon.locale.getRibattutoSymbols
import com.cristianovecchi.mikrokanon.locale.rowFormsMap
import kotlin.math.absoluteValue

fun newLineOrNot(list: Collection<Any>, newLineFromN: Int ): String{
    return if(list.size < newLineFromN) "" else "\n"
}
fun String.valueFromCsv(index: Int): Int {
    return this.extractIntsFromCsv()[index]
}
fun String.extractIntsFromCsv(): List<Int>{
    //if(this.isEmpty() ) return listOf(0)
    return this.split(',').mapNotNull { it.toInt()}
}
fun String.extractIntPairsFromCsv(): List<Pair<Int,Int>>{
    val pairs = this.split(',')
    return pairs.map{ val split = it.split('|'); Pair(split[0].toInt(), split[1].toInt())}
}
fun String.extractLongPairsFromCsv(): List<Pair<Long,Long>>{
    val pairs = this.split(',')
    return pairs.map{ val split = it.split('|'); Pair(split[0].toLong(), split[1].toLong())}
}
fun String.extractIntListsFromCsv(): List<List<Int>>{
    val list = this.split(',')
    return list.map{ subList -> subList.split('|').map{it.toInt()}}
}
fun  MutableList<Pair<Int,Int>>.toIntPairsString(): String {
    return this.joinToString(",") { "${it.first}|${it.second}" }
}
fun Pair<Int, Int>.describeSingleRowForm(rowFormsMap: Map<Int, String>, numbers: List<String>): String {
    val openBracket = (if(this.first < 0) "(" else "" )
    val counterpointNumber = (if (this.first.absoluteValue == 1) "" else numbers[this.first.absoluteValue-2])
    val rowForm = rowFormsMap[this.second.absoluteValue]
    val closeBracket = (if(this.first < 0) ")" else "")
    val separator = (if(this.second < 0)" |" else "")
    return openBracket + counterpointNumber + rowForm +closeBracket + separator
}
fun String.extractFloatsFromCsv(): List<Float>{
    return this.split(',').mapNotNull { it.toFloat() }
}
fun describeEnsembles(ensListIndexes: List<List<Int>>, ensNames: List<String>): String {
    if(ensListIndexes.size ==1){
        val ensIndexes = ensListIndexes[0]
        val nl = newLineOrNot(ensIndexes, 2)
        return "$nl${ensIndexes.joinToString(" + ") { ensNames[it] }}"
    }
    return ensListIndexes.foldIndexed(""){ index, acc, ensList ->
        acc + "\n${index+1}:  ${ensList.joinToString(", ") {ensNames[it]}}"
    }
}
fun String.describe(): String {
    val ints = this.extractIntsFromCsv()
    return ints.foldIndexed("") { index, acc, i ->
        when {
            index == 0 -> acc + i.absoluteValue.toString()
            i < 0 -> "$acc | ${i.absoluteValue}"
            ints[index - 1].absoluteValue > i.absoluteValue -> "$acc ➘ $i"
            ints[index - 1].absoluteValue < i.absoluteValue -> "$acc ➚ $i"
            ints[index - 1].absoluteValue == i.absoluteValue -> "$acc - $i"
            else -> acc + i.toString()
        }
    }
}

fun String.describeForArticulation(legatoMap: Map<Int, String>): String {
    val (ints, ribs) = this.extractIntPairsFromCsv().unzip()
    val ribSymbols = getRibattutoSymbols()
    return ints.foldIndexed("") { index, acc, i ->
        val name = legatoMap[i.absoluteValue -1]!!
        val ribattuto = ribSymbols[ribs[index]]
        //val previousRibattuto = if(index == 0) ribattuto else ribSymbols[ribs[index-1]]
        when {
            index == 0 -> acc + name + ribattuto
            i < 0 -> "$acc | $name$ribattuto"
            ints[index - 1].absoluteValue > i.absoluteValue -> "$acc ➘ $name$ribattuto"
            ints[index - 1].absoluteValue < i.absoluteValue -> "$acc ➚ $name$ribattuto"
            ints[index - 1].absoluteValue == i.absoluteValue -> "$acc - $name$ribattuto"
            else -> acc + i.toString()
        }
    }
}
fun String.describeForDynamic(map: Map<Float, String>, ascendingSymbol: String, descendingSymbol: String) : String {
    val floats = this.extractFloatsFromCsv()
    return floats.foldIndexed("") { index, acc, i ->
        when {
            index == 0 -> acc + map[i.absoluteValue]!!
            i < 0 -> "$acc | ${map[i.absoluteValue]!!}"
            floats[index - 1].absoluteValue > i.absoluteValue -> "$acc $descendingSymbol ${map[i]!!}"
            floats[index - 1].absoluteValue < i.absoluteValue -> "$acc $ascendingSymbol ${map[i]!!}"
            floats[index - 1].absoluteValue == i.absoluteValue -> "$acc - ${map[i]!!}"
            else -> acc + i.toString()
        }
    }
}
fun String.describeForTranspose(intervals: List<String>): String {
    val pairs = this.extractIntPairsFromCsv()
    return pairs.joinToString(", ") { intervals[it.first] + if(it.second==1) "" else " " + rowFormsMap[it.second] }
}
fun correctBpms(bpms: String): String{
    val result = bpms.extractIntsFromCsv().toMutableList()
    if (result.all{ it.absoluteValue == result[0].absoluteValue}) return result[0].absoluteValue.toString()
    repeat(2){
        if (result[0] < 0) result[0] = result[0].absoluteValue
        if (result.size > 1){
            if (result[1] < 0)  result.add(1, result[0])
            (1 until result.size).forEach{ index ->
                if (result[index-1].absoluteValue == result[index].absoluteValue) result[index] = result[index].absoluteValue
            }
            (1 until result.size - 1).forEach{ index ->
                if (result[index] < 0 && result[index+1] < 0 ) result.add(index+1, result[index].absoluteValue)
            }
            if (result.last() < 0) result.add(result.last().absoluteValue)
        }
    }
    return result.joinToString(",")
}
fun correctDynamics(dynamics: String): String{
    val result = dynamics.extractFloatsFromCsv().toMutableList()
    if (result.all{ it.absoluteValue == result[0].absoluteValue}) return result[0].absoluteValue.toString()
    repeat(2){
        if (result[0] < 0f) result[0] = result[0].absoluteValue
        if (result.size > 1){
            if (result[1] < 0f)  result.add(1, result[0])
            (1 until result.size).forEach{ index ->
                if (result[index-1].absoluteValue == result[index].absoluteValue) result[index] = result[index].absoluteValue
            }
            (1 until result.size - 1).forEach{ index ->
                if (result[index] < 0f && result[index+1] < 0f ) result.add(index+1, result[index].absoluteValue)
            }
            if (result.last() < 0f) result.add(result.last().absoluteValue)
        }
    }
    return result.joinToString(",")
}
fun correctLegatos(legatos: String): String {
    //println("legatos: $legatos")
    val (leg, rib) = legatos.extractIntPairsFromCsv().unzip()
    val result = leg.toMutableList()
    val result2 = rib.toMutableList()
    if (result.all{ it.absoluteValue == result[0].absoluteValue} && result2.all{ it.absoluteValue == result2[0].absoluteValue})
        return "${result[0].absoluteValue}|${result2[0].absoluteValue}"
    repeat(2){
        if (result[0] < 0f) result[0] = result[0].absoluteValue
        if (result.size > 1){
            if (result[1] < 0f)  {result.add(1, result[0]); result2.add(1, result2[0])}
            (1 until result.size).forEach{ index ->
                if (result[index-1].absoluteValue == result[index].absoluteValue) result[index] = result[index].absoluteValue
            }
            (1 until result.size - 1).forEach{ index ->
                if (result[index] < 0f && result[index+1] < 0f ) {
                    result.add(index+1, result[index].absoluteValue)
                    result2.add(index+1, result2[index].absoluteValue)
                }
            }
            if (result.last() < 0f) {
                result.add(result.last().absoluteValue)
                result2.add(result2.last().absoluteValue)
            }
        }
    }
    return result.zip(result2){a, b -> "$a|$b" }.joinToString(",")//.also{println("res: $it")}
}

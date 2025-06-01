package com.cristianovecchi.mikrokanon

import com.cristianovecchi.mikrokanon.AIMUSIC.Clip
import com.cristianovecchi.mikrokanon.locale.*
import java.text.DateFormat
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt

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
fun String.extractIntTriplesFromCsv(): List<Triple<Int,Int,Int>>{
    val triples = this.split(',')
    return triples.map{ val split = it.split('|'); Triple(split[0].toInt(), split[1].toInt(), (split.getOrElse(2) {"0"}).toInt())}
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
fun MutableList<Pair<Int,Int>>.toIntPairsString(): String {
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
fun getZodiacPlanets(emojis: Boolean): List<String>{
    return zodiacPlanets
//    return if(emojis) {
//        if(android.os.Build.VERSION.SDK_INT >=android.os.Build.VERSION_CODES.O) zodiacPlanets
//        else zodiacPlanets
//    } else zodiacPlanets
}
fun getZodiacSigns(emojis: Boolean): List<String>{
    return if(emojis) return zodiacSignsEmojis else zodiacSigns
}
fun getGlissandoSymbols(): Pair<String,String>{
    return if(android.os.Build.VERSION.SDK_INT >=android.os.Build.VERSION_CODES.O)
        Pair("\uD834\uDDB1", "\uD834\uDDB2")
    else Pair("➚", "➘")
}
fun getVibratoSymbol(): String {
    return if(android.os.Build.VERSION.SDK_INT >=android.os.Build.VERSION_CODES.O)
        "\u223f"
    else "~"
}
fun getNoteAndRestSymbols(): List<String> {
    return if(android.os.Build.VERSION.SDK_INT >=android.os.Build.VERSION_CODES.O)
        listOf("\uD834\uDD60" ,"\uD834\uDD3E")
    else listOf("♪", "-")
}

fun createGlissandoIntervals(doublingNames: List<String>): List<String>{
    val symbols = getGlissandoSymbols()
    val asc = symbols.first
    val desc = symbols.second
    return listOf("${doublingNames[0]}$asc", "${doublingNames[0]}$desc",
        "${doublingNames[1]}$asc", "${doublingNames[1]}$desc", "${doublingNames[2]}$asc", "${doublingNames[2]}$desc",
        "${doublingNames[3]}$asc", "${doublingNames[3]}$desc",
        "${doublingNames[4]}$asc", "${doublingNames[4]}$desc", "${doublingNames[5]}$asc", "${doublingNames[5]}$desc",
        "${doublingNames[6]}$asc", "${doublingNames[6]}$desc", "${doublingNames[7]}$asc", "${doublingNames[7]}$desc",
        "${doublingNames[8]}$asc", "${doublingNames[8]}$desc", "${doublingNames[9]}$asc", "${doublingNames[9]}$desc",
        "${doublingNames[10]}$asc", "${doublingNames[10]}$desc", "${doublingNames[11]}$asc", "${doublingNames[11]}$desc")
}
fun getDynamicSymbols(): List<String>{
    return if(android.os.Build.VERSION.SDK_INT >=android.os.Build.VERSION_CODES.O)
        listOf("\uD834\uDDC8","\uD834\uDD8F\uD834\uDD8F\uD834\uDD8F\uD834\uDD8F","\uD834\uDD8F\uD834\uDD8F\uD834\uDD8F","\uD834\uDD8F\uD834\uDD8F",
            "\uD834\uDD8F","\uD834\uDD90\uD834\uDD8F","\uD834\uDD90\uD834\uDD91","\uD834\uDD91",
            "\uD834\uDD91\uD834\uDD91","\uD834\uDD91\uD834\uDD91\uD834\uDD91","\uD834\uDD91\uD834\uDD91\uD834\uDD91\uD834\uDD91","\uD834\uDD91\uD834\uDD91\uD834\uDD91\uD834\uDD91\uD834\uDD91",
            "\uD834\uDD92", "\uD834\uDD93")
    else listOf("0","pppp","ppp","pp",  "p","mp","mf","f", "ff", "fff","ffff","fffff","<",">")
}
fun getOctaveSymbols(): List<String>{
    return listOf("➘15","➘8", "", "➚8","➚15", "\u21c58", "\u21c515")
}
fun getRibattutoSymbols(): List<String>{
    return listOf("","", "\"", "\"\'", "\"\"", "\"\"\'", "\"\"\"")
}
fun getIntervalsForTranspose(intervalSet: List<String> = intervalSetEn): List<String>{
    val split = intervalSet.map{ it.split("\n")}
    return listOf(split[6][0], split[0][0], split[1][0], split[2][0], split[3][0], split[4][0],
        split[5][0], split[4][1], split[3][1], split[2][1], split[1][1], split[0][1])
}
fun IntRange.describeWithNotes(noteNames: List<String>): String{
    val firstName = Clip.convertAbsToClipText(first % 12, noteNames)
    val lastName = Clip.convertAbsToClipText(last % 12, noteNames)
    return "$firstName${first/12-1}-$lastName${last/12-1}"
}
fun Int.describeAsNote(noteNames: List<String>): String {
    val name = Clip.convertAbsToClipText(this % 12, noteNames)
    return "$name${this/12-1}"
}

val convertToLocaleDate = { timestamps:List<String>, langDef:String ->
    //println("langDef = $langDef")
    val locale = Locale(langDef)
    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, 3, locale) // timeFormat: 0,1,3
    timestamps.map{
        if(it.isNotEmpty()){
            //val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", locale)
            val netDate = Date(it.toLong())
            dateFormat.format(netDate)
        } else {
            ""
        }
    }
}
val convertToFileDate = { timestamp:Long, langDef:String ->
    val actualLangDef = when (langDef) {
        "ar" -> "en"
        else -> langDef
    }
    val locale = Locale(actualLangDef)
    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, 2, locale) // timeFormat: 0,1,3
    val netDate = Date(timestamp)
    dateFormat.format(netDate).replace("[^A-Za-z0-9_]".toRegex(), "_").trim('_')
}

fun Float.formatDecimal(numberOfDecimals: Int = 2): String = "%.${numberOfDecimals}f".format(this)

fun Float.formatDecimalWithoutZero(): String =  "%,.1f".format(Locale.ENGLISH,this*100).replace(".0","")

fun Float.cutDecimals(decimalsToKeep: Int? = null): Float {
    if (decimalsToKeep == null) return this
    val check = 10.0.pow(decimalsToKeep).toFloat()
    return (this * check).roundToInt() / check
}
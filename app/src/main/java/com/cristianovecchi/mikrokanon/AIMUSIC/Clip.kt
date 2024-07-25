package com.cristianovecchi.mikrokanon.AIMUSIC

import android.os.Parcelable
import com.cristianovecchi.mikrokanon.composables.Accidents
import com.cristianovecchi.mikrokanon.composables.NoteNamesEn
import com.cristianovecchi.mikrokanon.db.ClipData
import com.cristianovecchi.mikrokanon.getZodiacSigns
import kotlinx.android.parcel.Parcelize
import kotlin.random.Random

fun ArrayList<Clip>.transpose(transpose: Int): List<Int>{
    if(transpose == 0) return this.map{ it.abstractNote}
    return this.map{ it.abstractNote }
                .map{ if (it == -1) -1 else (it + transpose) % 12}
}
fun List<Int>.transpose(transpose: Int): List<Int>{
    if(transpose == 0) return this
    return this.map{ if (it == -1) -1 else (it + transpose) % 12}
}
fun ArrayList<Clip>.toStringAll(notesNames: List<String>, zodiacSigns: Boolean, emoji: Boolean): String {
    return if (this.isNotEmpty()) {
        if(zodiacSigns){
            this.map { clip -> clip.findZodiacSign(emoji) }.reduce { acc, string -> "$acc $string" }
        } else {
            this.map { clip -> clip.findText(notesNames = notesNames) }.reduce { acc, string -> "$acc $string" }
        }

    } else {
        "empty Sequence"
    }
}
//fun ArrayList<Clip>.toAbsPitches(): List<Int> {
//    return this.map { it.abstractNote }
//}
@Parcelize // remove?
data class Clip(
    val id: Int = -1,
    var abstractNote: Int = -1,
    val name: NoteNamesEn = NoteNamesEn.EMPTY,
    val ax: Accidents = Accidents.EMPTY) : Parcelable {

    fun findText(notesNames: List<String>): String{
        val actualAx = if(ax == Accidents.NATURAL) "" else ax.ax
        return if (name == NoteNamesEn.EMPTY ) "" else "${notesNames[name.ordinal]}$actualAx"
    }
    fun findZodiacSign(emoji: Boolean ): String {
        return convertAbsToZodiacSign(abstractNote, emoji)
    }

    fun tritoneSubstitution(): Clip{
        return when (abstractNote){
            1 ->  copy(abstractNote = 7, name = NoteNamesEn.G, ax = Accidents.EMPTY)
            3 -> copy(abstractNote = 9, name = NoteNamesEn.A, ax = Accidents.EMPTY)
            5 -> copy(abstractNote = 11, name = NoteNamesEn.B, ax = Accidents.EMPTY)
            7 -> copy(abstractNote = 1, name = NoteNamesEn.C, ax = Accidents.SHARP)
            9 -> copy(abstractNote = 3, name = NoteNamesEn.E, ax = Accidents.FLAT)
            11 -> copy(abstractNote = 5, name = NoteNamesEn.F, ax = Accidents.EMPTY)
            else -> copy()
        }
    }

    companion object {
//        fun createClip(absNote: Int, ax: Int, id: Int) : Clip {
//            val absPitch = absNote + ax
//            return Clip(id,absPitch,
//                NoteNamesEn.values().first{it.abs == absNote},
//                Accidents.values().first{it.sum == ax})
//        }
        fun inAbsRange(absPitch: Int) : Int {
            if(absPitch > 11) return absPitch - 12
            if(absPitch < 0) return absPitch + 12
            return absPitch
        }
        fun convertAbsPitchesToClips(absPitches: List<Int>): ArrayList<Clip>{
            return ArrayList(
                absPitches.map{
                    val pair = convertAbsToNameAndAx(it)
                    Clip(abstractNote = it, name = pair.first, ax = pair.second)
                }
            )
        }
        fun convertAbsToNameAndAx(absPitch: Int): Pair<NoteNamesEn, Accidents> {
            return when (absPitch) {
                0 -> Pair( NoteNamesEn.C, Accidents.NATURAL )
                1 -> Pair( NoteNamesEn.C, Accidents.SHARP ) //C#
                2 -> Pair( NoteNamesEn.D, Accidents.NATURAL )
                3 -> Pair( NoteNamesEn.E, Accidents.FLAT ) //Eb
                4 -> Pair( NoteNamesEn.E, Accidents.NATURAL )
                5 -> Pair( NoteNamesEn.F, Accidents.NATURAL )
                6 -> Pair( NoteNamesEn.F, Accidents.SHARP ) //F#
                7 -> Pair( NoteNamesEn.G, Accidents.NATURAL )
                8 -> Pair( NoteNamesEn.G, Accidents.SHARP ) //G#
                9 -> Pair( NoteNamesEn.A, Accidents.NATURAL )
                10 -> Pair( NoteNamesEn.B, Accidents.FLAT ) //Bb
                11 -> Pair( NoteNamesEn.B, Accidents.NATURAL )
                else -> Pair( NoteNamesEn.EMPTY, Accidents.NATURAL )
            }
        }
        fun convertAbsPitchesToClipText(absPitches: List<Int>, notesNames: List<String>): String {
            return absPitches.map{ convertAbsToClipText(it, notesNames)}.joinToString()
        }
        fun convertAbsToClipText(absPitch: Int, notesNames: List<String> ): String {
            return when (absPitch) {
                0 -> notesNames[0]
                1 -> "${notesNames[0]}♯" //C#
                2 -> notesNames[1]
                3 -> "${notesNames[2]}♭" //Eb
                4 -> notesNames[2]
                5 -> notesNames[3]
                6 -> "${notesNames[3]}♯"//F#
                7 -> notesNames[4]
                8 -> "${notesNames[4]}♯"//G#
                9 -> notesNames[5]
                10 -> "${notesNames[6]}♭" //Bb
                11 -> notesNames[6]
                else -> ""
            }
        }
        fun convertAbsToZodiacSign(absPitch: Int, emoji: Boolean): String {
            return when (absPitch){
                in 0..11 -> getZodiacSigns(emoji)[absPitch]
                else -> ""
            }
        }
        fun clipDataToClip(clipData: ClipData): Clip{
            return Clip(clipData.clipId,clipData.abstractNote,
                NoteNamesEn.values().first { it.abs == clipData.name },
                Accidents.values().first { it.sum == clipData.ax }
            )
        }
        fun clipToDataClip(clip: Clip): ClipData {
            return ClipData (clip.id,clip.abstractNote,clip.name.abs,clip.ax.sum)
        }
//        fun clipSequenceToCsv(sequence : ArrayList<Clip>): String{
//            var csv = ""
//            sequence.forEach{
//                csv += "${it.name.abs},${it.ax.sum},"
//            }
//            csv.removeSuffix(",")
//            return csv
//        }
//        fun toClips(counterpoint: Counterpoint, noteNames: List<String>) : List<List<Clip>>{
//            return counterpoint.parts.map { part ->
//                part.absPitches.map{ absPitch ->
//                    val pair = Clip.convertAbsToNameAndAx(absPitch)
//                    Clip(-1,absPitch,pair.first,pair.second) }.toList()
//            }.toList()
//        }
        fun toClipsText(counterpoint: Counterpoint, noteNames: List<String>, zodiacSigns: Boolean = false, emoji: Boolean = false) : List<List<String>>{
            return if(zodiacSigns) {
                counterpoint.parts.map { part ->
                    part.absPitches.map{ absPitch ->
                        convertAbsToZodiacSign(absPitch, emoji)
                    }.toList()
                }.toList()
            } else {
                counterpoint.parts.map { part ->
                    part.absPitches.map{ absPitch ->
                       convertAbsToClipText(absPitch, noteNames)
                    }.toList()
                }.toList()
            }
        }
//        fun toClips(csv: String) : List<Clip>{
//            val array = csv.split(",")
//            println(array + Integer.parseInt(array[0]))
//            val list = mutableListOf<Clip>()
//            var count = 0
//
//            for(i in array.indices step 2){
//                val clip = Clip.createClip(Integer.parseInt(array[i]),Integer.parseInt(array[i+1]), count++)
//            }
//            return list
//        }
//        fun randomClip(id: Int, optRest: Boolean): Clip {
//            val n = if(optRest) Random.nextInt(0,8) else Random.nextInt(0,7)
//            val a = Random.nextInt(0, 5)
//            var absPitch: Int
//            if(optRest && n == 8) absPitch = -1 else {
//                absPitch = NoteNamesEn.values()[n].abs + Accidents.values()[a].sum
//                if(absPitch > 11) absPitch -= 12
//                if(absPitch < 0) absPitch += 12
//            }
//            val newId = id + 1
//            return when (n) {
//                in 0..6 -> {
//                    Clip(
//                        newId, absPitch,
//                        NoteNamesEn.values()[n], Accidents.values()[a])
//                }
//                else -> Clip()
//            }
//        }

//        fun randomClipSequence(noteNames: List<String>, id: Int, size: Int, optRests: Boolean): MutableList<Clip> {
//            val seq = mutableListOf<Clip>()
//            for (i in 0 until size) {
//                val newId = id + i
//                seq.add(randomClip(newId, optRests))
//            }
//            return seq
//        }
    }
}

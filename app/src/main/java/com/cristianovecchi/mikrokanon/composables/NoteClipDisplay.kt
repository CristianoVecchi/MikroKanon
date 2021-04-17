package com.cristianovecchi.mikrokanon.composables

import android.os.Parcelable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristianovecchi.mikrokanon.AIMUSIC.Counterpoint
import com.cristianovecchi.mikrokanon.dao.ClipData
import kotlinx.android.parcel.Parcelize
import kotlin.random.Random

@Composable
fun NoteClipDisplay(modifier: Modifier, noteClips: List<Clip>, cursor: Int = -1,
                    nCols: Int = 6, dispatch: (Int) -> Unit) {
    val listState = rememberLazyListState()
    val selectionBackColor = Color.White
    val selectionTextColor = Color.Red
    val selectionBorderColor = Color.Black
    val unselectionBackColor = Color.LightGray
    val unselectionTextColor = Color.Blue
    val unselectionBorderColor = Color.DarkGray
    if (noteClips.isEmpty()){
        Text(text= "ENTER SOME NOTES", modifier = Modifier.padding(16.dp))
    } else {
        val nRows = (noteClips.size / nCols) + 1
        var index = 0
       // val rows = mutableListOf<List<Clip>>()
        val rows = (0 until nRows).toList()
        LazyColumn(modifier = modifier, state = listState,
                ) { items(rows) { _ ->
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start ) {
                for(j in 0 until nCols) {
                    if (index != noteClips.size) {
                        val clip = noteClips[index]

                        Card(modifier = Modifier.background(Color.White).
                        clip(RoundedCornerShape(6.dp)).padding(6.dp).clickable {dispatch(clip.id)},

                            backgroundColor = if (cursor == index) selectionBackColor else unselectionBackColor,
                            contentColor = if (cursor == index) selectionTextColor else unselectionTextColor,
                            border = BorderStroke(2.dp, if (cursor == index) selectionBorderColor else unselectionBorderColor),
                            elevation = if (cursor == index) 4.dp else 4.dp
                        )
                        {
                            Text(text = clip.text, modifier = Modifier.padding(18.dp),
                                style = TextStyle(fontSize = if (cursor == index) 20.sp else 20.sp),
                                fontWeight = if (cursor == index) FontWeight.Bold else FontWeight.Normal)
                        }
                        index++
                    }
                }
        }



            }
        }
    }
}

@Parcelize // remove?
data class Clip(val text: String = "",
                val id: Int = -1,
                var abstractNote: Int = -1,
                val name: NoteNamesEn = NoteNamesEn.EMPTY,
                val ax: Accidents = Accidents.EMPTY) : Parcelable {

                    companion object {
                        fun createClip(absNote: Int, ax: Int, noteNames: List<String>, id: Int) : Clip {
                            val absPitch = absNote + ax
                            return Clip(Clip.convertAbsToString(absPitch,noteNames),id,absPitch,
                            NoteNamesEn.values().first{it.abs == absNote},
                            Accidents.values().first{it.sum == ax})
                        }
                        fun inAbsRange(absPitch: Int) : Int {
                            if(absPitch > 11) return absPitch - 12
                            if(absPitch < 0) return absPitch + 12
                            return absPitch
                        }
                        fun convertAbsToString(absPitch: Int, noteNames: List<String> ): String {
                            return when (absPitch) {
                                0 -> noteNames[0]
                                1 -> "${noteNames[0]}${Accidents.SHARP.ax}" //C#
                                2 -> noteNames[1]
                                3 -> "${noteNames[2]}${Accidents.FLAT.ax}"//Eb
                                4 -> noteNames[2]
                                5 -> noteNames[3]
                                6 -> "${noteNames[3]}${Accidents.SHARP.ax}"//F#
                                7 -> noteNames[4]
                                8 -> "${noteNames[4]}${Accidents.SHARP.ax}"//G#
                                9 -> noteNames[5]
                                10 -> "${noteNames[6]}${Accidents.FLAT.ax}"//Bb
                                11 -> noteNames[6]
                                else -> ""
                            }
                        }
                    }
                }
fun clipDataToClip(clipData: ClipData): Clip{
    return Clip(clipData.text,clipData.clipId,clipData.abstractNote,
        NoteNamesEn.values().first { it.abs == clipData.name },
        Accidents.values().first { it.sum == clipData.ax }
    )
}
fun clipToDataClip(clip: Clip): ClipData {
    return ClipData (clip.text,clip.id,clip.abstractNote,clip.name.abs,clip.ax.sum)
}
fun clipSequenceToCsv(sequence : ArrayList<Clip>): String{
    var csv = ""
    sequence.forEach{
        csv += "${it.name.abs},${it.ax.sum},"
    }
    csv.removeSuffix(",")
    return csv
}
fun toClips(counterpoint: Counterpoint, noteNames: List<String>) : List<List<Clip>>{
    return counterpoint.parts.map { part ->
        part.absPitches.map{ absPitch ->
            Clip(Clip.convertAbsToString(absPitch,noteNames),-1,absPitch) }.toList()
    }.toList()
}
fun toClips(csv: String, noteNames: List<String>) : List<Clip>{
    val array = csv.split(",")
    println(array + Integer.parseInt(array[0]))
    val list = mutableListOf<Clip>()
    var count = 0

    for(i in 0 until array.size step 2){
        val clip = Clip.createClip(Integer.parseInt(array[i]),Integer.parseInt(array[i+1]),noteNames,count++)
    }
    return list
}

fun main() {
    val list = toClips("0,0,0,0", NoteNamesIt.values().map{it.toString()})
    println(list.map{it.text}.reduce{ acc, text -> "$acc $text "})
}

fun randomClip(noteNames: List<String>, id: Int, optRest: Boolean): Clip {
    val n = if(optRest) Random.nextInt(0,8) else Random.nextInt(0,7)
    val a = Random.nextInt(0, 5)
    var absPitch: Int
    if(optRest && n == 8) absPitch = -1 else {
        absPitch = NoteNamesEn.values()[n].abs + Accidents.values()[a].sum
        if(absPitch > 11) absPitch -= 12
        if(absPitch < 0) absPitch += 12
    }
    val newId = id + 1
    return when (n) {
        in 0..6 -> {
            Clip(noteNames[n]+ if(a !=4) {Accidents.values()[a].ax} else "" ,
                    newId, absPitch,
                    NoteNamesEn.values()[n], Accidents.values()[a])
        }
        else -> Clip()
    }
}

fun randomClipSequence(noteNames: List<String>, id: Int, size: Int, optRests: Boolean): MutableList<Clip>{
    var seq = mutableListOf<Clip>()
    for(i in 0 until size){
        val newId = id + i
        seq.add(randomClip(noteNames, newId, optRests))
    }
    return seq
}
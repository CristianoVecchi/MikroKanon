package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.locale.Lang
import com.cristianovecchi.mikrokanon.AIMUSIC.Clip
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.locale.getZodiacPlanets
import kotlin.collections.HashMap

@Composable
fun AbstractNoteSequenceEditor(list: ArrayList<Clip> = ArrayList(), model: AppViewModel, editing: Boolean,
                               iconMap: Map<String,Int> = HashMap(), done_action: (ArrayList<Clip>, Boolean) -> Unit) {
    val dimensions = model.dimensions
    val nClipCols = dimensions.inputNclipColumns
    val clips: MutableList<Clip> = remember { mutableStateListOf(*list.toTypedArray()) }
    model.userOptionsData.observeAsState(initial = listOf()).value // to force recomposing when options change
    val appColors = model.appColors
    val language = Lang.provideLanguage(model.getUserLangDef())
    val notesNames = language.noteNames
    val playing by model.playing.asFlow().collectAsState(initial = false)
    val cursor = remember { mutableStateOf(clips.size -1) }
    val id = remember { mutableStateOf(0) }
    val lastOutIsNotUndo = remember { mutableStateOf(true) }
    val lastIsCursorChanged = remember { mutableStateOf(false) }


    data class Undo(val list: MutableList<Clip>, val cursor: Int)
    val stack: java.util.Stack<Undo> by remember { mutableStateOf( java.util.Stack<Undo>()) }

    Column(modifier = Modifier
        .fillMaxHeight()
        .background(appColors.inputBackgroundColor)) {
        val modifier1 = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            //.weight(1f)
        val modifier3 = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .weight(3f)
        val modifier5 = Modifier
            .fillMaxSize()
            .weight(7f)
        SequenceAnalyzer(modifier = modifier1, absPitches = clips.map{it.abstractNote},
                        fontSize = if(model.zodiacPlanetsActive) dimensions.outputIntervalSetFontSize + 7 + 10 else dimensions.outputIntervalSetFontSize + 7,
                        colors = appColors ,
                        intervalNames = if(model.zodiacPlanetsActive) getZodiacPlanets(model.zodiacEmojisActive) else language.intervalSet)
//        Row(modifier1) {
//            Text(text = "Build a Sequence!")
//        }
        Row(modifier3) {

            NoteClipDisplay(
                modifier = Modifier.fillMaxWidth(),  clips = clips.toList(), hintText = language.enterSomeNotes,
                notesNames = notesNames,  zodiacSigns = model.zodiacSignsActive, emoji = model.zodiacEmojisActive,
                colors = appColors,
                cursor = mutableStateOf(cursor.value), nCols = nClipCols, fontSize = dimensions.inputClipFontSize
            ) { id ->
                clips.forEachIndexed { index, clip ->
                    if (clip.id == id) {
                        cursor.value = index
                        stack.push(Undo(ArrayList<Clip>(clips), cursor.value))
                        lastOutIsNotUndo.value = true
                        lastIsCursorChanged.value = true
                    }
                }
            }
        }
        Row(modifier5) {
            NoteKeyboard(model, iconMap = iconMap, colors = appColors
            ) { out ->
                when (out) {
                    is Out.Note -> {
                        if (cursor.value == clips.size - 1) {
                            cursor.value++
                            clips.add(Clip(id.value++, out.note.abs, out.note, Accidents.NATURAL))

                            stack.push(Undo(ArrayList(clips), cursor.value))
                            lastOutIsNotUndo.value = true
                            lastIsCursorChanged.value = true

                        } else {
                            clips.add(
                                cursor.value,
                                Clip(id.value++, out.note.abs, out.note, Accidents.NATURAL)
                            )
                            stack.push(Undo(ArrayList(clips), cursor.value))
                            lastOutIsNotUndo.value = true
                        }
                    }
                    is Out.Accident -> {
                        if (clips.isNotEmpty()) {
                            val oldClip = clips[cursor.value]
                            val newClip: Clip
                            val change: Boolean
                            if (oldClip.ax != Accidents.NATURAL) { // Note has accident
                                if (out.ax == Accidents.NATURAL) { // Removes the previous accident
                                    newClip = Clip(
                                        oldClip.id,
                                        Clip.inAbsRange(oldClip.abstractNote - oldClip.ax.sum),
                                        oldClip.name,
                                        Accidents.NATURAL
                                    )
                                    change = true
                                } else {
                                    if (oldClip.findText(notesNames)
                                            .contains(out.ax.ax)
                                    ) { // Removes the same accident
                                        newClip = Clip(
                                            oldClip.id,
                                            Clip.inAbsRange(oldClip.abstractNote - oldClip.ax.sum),
                                            oldClip.name,
                                            Accidents.NATURAL
                                        )
                                        change = true
                                    } else { // Replaces the previous accident with a new one
                                        //val noteName = oldClip.text.removeSuffix(oldClip.ax.ax)
                                        newClip = Clip(
                                            oldClip.id,
                                            Clip.inAbsRange(oldClip.abstractNote - oldClip.ax.sum + out.ax.sum),
                                            oldClip.name,
                                            out.ax
                                        )
                                        change = true
                                    }
                                }
                            } else {
                                if (out.ax == Accidents.NATURAL) {
                                    change = false
                                    newClip = oldClip
                                } else {
                                    newClip = Clip(
                                        oldClip.id,
                                        Clip.inAbsRange(oldClip.abstractNote + out.ax.sum),
                                        oldClip.name,
                                        out.ax
                                    )
                                    change = true
                                }
                            }
                            if (change) {
                                clips[cursor.value] = newClip
                                stack.push(Undo(ArrayList(clips), cursor.value))
                                lastOutIsNotUndo.value = true
                            }
                        }
                    }
                    is Out.Delete -> {
                        var change = false
                        if (clips.isNotEmpty()) {
                            clips.removeAt(cursor.value)
                            change = true
                        }
                        if (cursor.value > 0) {
                            cursor.value--
                            change = true
                            lastIsCursorChanged.value = true

                        }
                        if (clips.isEmpty()) {
                            cursor.value = -1
                            change = true
                            lastIsCursorChanged.value = true

                        }
                        if (change) stack.push(Undo(ArrayList(clips), cursor.value))
                        lastOutIsNotUndo.value = true
                    }
                    is Out.Forward -> {
                        if (cursor.value < clips.size - 1) {
                            cursor.value++
                            stack.push(Undo(ArrayList(clips), cursor.value))
                            lastOutIsNotUndo.value = true
                            lastIsCursorChanged.value = true

                        }
                    }
                    is Out.Back -> {
                        if (clips.isNotEmpty() && cursor.value > 0) {
                            cursor.value--
                            stack.push(Undo(ArrayList<Clip>(clips), cursor.value))
                            lastOutIsNotUndo.value = true
                            lastIsCursorChanged.value = true

                        }
                    }
                    is Out.FullForward -> {
                        if (cursor.value < clips.size - 1) {
                            cursor.value = clips.size - 1
                            stack.push(Undo(ArrayList<Clip>(clips), cursor.value))
                            lastOutIsNotUndo.value = true
                            lastIsCursorChanged.value = true

                        }
                    }
                    is Out.FullBack -> {
                        if (clips.isNotEmpty() && cursor.value > 0) {
                            cursor.value = 0
                            stack.push(Undo(ArrayList<Clip>(clips), cursor.value))
                            lastOutIsNotUndo.value = true
                            lastIsCursorChanged.value = true

                        }
                    }
                    is Out.Undo -> {
                        if (stack.isNotEmpty()) {
                            if (lastOutIsNotUndo.value) {
                                stack.pop()
                                lastOutIsNotUndo.value = false
                            }
                            val undo = stack.pop()
                            clips.clear()
                            clips.addAll(0, undo.list)
                            cursor.value = undo.cursor
                            lastIsCursorChanged.value = true

                        } else {
                            clips.clear()
                            cursor.value = -1
                            lastIsCursorChanged.value = true

                        }
                    }
                    is Out.PlaySequence -> {
                        if (clips.isNotEmpty() && !playing) model.onPlaySequence(clips) else model.onStop
                    }
                    is Out.Enter -> {
                        val newList = ArrayList<Clip>()
                        clips.forEach { newList.add(it) }
                        done_action(newList, editing)
                    }
                }
            }
        }
    }
}

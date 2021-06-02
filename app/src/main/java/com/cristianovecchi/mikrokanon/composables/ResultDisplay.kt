package com.cristianovecchi.mikrokanon.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.AIMUSIC.Counterpoint
import com.cristianovecchi.mikrokanon.AIMUSIC.TREND
import com.cristianovecchi.mikrokanon.ActiveButtons
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.toStringAll
import com.cristianovecchi.mikrokanon.ui.buttonsDisplayBackgroundColor
import com.cristianovecchi.mikrokanon.ui.sequencesListBackgroundColor

@Composable
fun ResultDisplay(model: AppViewModel,
                  onKP: (Int, Boolean) -> Unit = { _, _ -> },
                  onClick: (Counterpoint) -> Unit = {},
                  onBack: () -> Unit = {},
                  onFreePart: (TREND) -> Unit = {},
                  onExpand: () -> Unit = {},
                  onPlay: () -> Unit = {}
                  )
{
    val notesNames by model.notesNames.asFlow().collectAsState(initial = listOf("do","re","mi","fa","sol","la","si"))
    val counterpoints by model.counterpoints.asFlow().collectAsState(initial = emptyList())
    val counterpointsData: List<Pair<Counterpoint, List<List<String>>>> = counterpoints.map{Pair(it, toClipsText(it, notesNames))}
    val elaborating by model.elaborating.asFlow().collectAsState(initial = false)
    val activeButtons by model.activeButtons.asFlow().collectAsState(initial = ActiveButtons(counterpoint = true, freeparts = true))
    val elaboratingBackgroundColor by animateColorAsState(
        if(elaborating) Color(0f,0f,0f,0.3f) else Color(0f,0f,0f,0.0f) )
    val backgroundColor = MaterialTheme.colors.sequencesListBackgroundColor
    val buttonsBackgroundColor = MaterialTheme.colors.buttonsDisplayBackgroundColor
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(backgroundColor)
        ) {
            val modifier4 = Modifier
                .fillMaxWidth()
                .weight(4f)
            val modifier1 = Modifier
                .fillMaxSize()
                .background(buttonsBackgroundColor)
                .weight(1f)
            val listState = rememberLazyListState()
            val buttonSize = 60.dp
            Box(modifier = modifier4) {
                LazyColumn(modifier = Modifier.fillMaxSize(), state = listState)
                {
                    items(counterpointsData) { counterpointsData ->
                        val counterpoint = counterpointsData.first
                        val parts = counterpointsData.second
                        val maxSize = parts.maxOf { it.size }
                        val clipsText: MutableList<MutableList<String>> = mutableListOf()
                        for (i in 0 until maxSize) {
                            val col: MutableList<String> = mutableListOf()
                            for (j in parts.indices) {
                                val text = if (i < parts[j].size) parts[j][i] else ""
                                col.add(text)
                            }
                            clipsText.add(col)
                        }
                        NoteTable(
                            model,
                            counterpoint,
                            clipsText,
                            16,
                            onClick = { onClick(counterpoint) })
                    }
                }
                if(elaborating){
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .background(elaboratingBackgroundColor),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally)
                    {
                        CircularProgressIndicator(color = Color.White,
                        strokeWidth = 6.dp)
                    }
                }
            }
            Column(
                modifier1,
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val dialogState by lazy { mutableStateOf(false) }

                SequencesDialog(dialogState = dialogState,
                    sequencesList = model.sequences.value!!.map { it.toStringAll(notesNames) },
                    onSubmitButtonClick = { index, repeat ->
                        dialogState.value = false
                        if (index != -1) {
                            onKP(index, repeat)
                        }
                    })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // UNDO BUTTON
                    CustomButton(iconId = model.iconMap["undo"]!!, isActive = activeButtons.undo, buttonSize = buttonSize) {
                        if(!elaborating) onBack()
                    }
                    // EXPAND BUTTON
                    CustomButton(iconId = model.iconMap["expand"]!!, isActive = activeButtons.expand, buttonSize = buttonSize) {
                        if(!elaborating) onExpand()
                    }
                    // Add Counterpoint Button
                    CustomButton(iconId = model.iconMap["counterpoint"]!!, isActive = activeButtons.counterpoint, buttonSize = buttonSize) {
                        if(!elaborating) dialogState.value = true
                    }

                    FreePartsButtons(
                        fontSize = 22, isActive = activeButtons.freeparts,
                        onAscDynamicClick = { if(!elaborating) onFreePart(TREND.ASCENDANT_DYNAMIC) },
                        onAscStaticClick = { if(!elaborating) onFreePart(TREND.ASCENDANT_STATIC) },
                        onDescDynamicClick = { if(!elaborating) onFreePart(TREND.DESCENDANT_DYNAMIC) },
                        onDescStaticClick = { if(!elaborating) onFreePart(TREND.DESCENDANT_STATIC) }
                    )
                    // PLAY BUTTON
                    CustomButton(iconId = model.iconMap["play"]!!, isActive = activeButtons.play, buttonSize = buttonSize) {
                        if(!elaborating) onPlay()
                    }
                }
            }
            Column(
                modifier1,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IntervalSetSelector(
                    model, fontSize = 10
                )
            }
        }
}

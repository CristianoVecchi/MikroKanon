package com.cristianovecchi.mikrokanon.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.AIMUSIC.Counterpoint
import com.cristianovecchi.mikrokanon.AIMUSIC.TREND
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.toStringAll
import com.cristianovecchi.mikrokanon.ui.*

@Composable
fun ResultDisplay(model: AppViewModel,
                  onKP: (Int, Boolean) -> Unit = { _, _ -> },
                  onClick: (Counterpoint) -> Unit = {},
                  onBack: () -> Unit = {},
                  onFreePart: (TREND) -> Unit = {},
                  onExpand: () -> Unit = {},
                  onPlay: () -> Unit = {}
                  ) {
    val notesNames by model.notesNames.asFlow().collectAsState(initial = listOf("do","re","mi","fa","sol","la","si"))
    val counterpoints by model.counterpoints.asFlow().collectAsState(initial = emptyList())
    val counterpointsData: List<Pair<Counterpoint, List<List<String>>>> = counterpoints.map{Pair(it, toClipsText(it, notesNames))}
    val elaborating by model.elaborating.asFlow().collectAsState(initial = false)
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
                val selectedDialogSequence by lazy { mutableStateOf(-1) }

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
                    IconButton(modifier = Modifier
                        .padding(2.dp)
                        .background(
                            MaterialTheme.colors.iconButtonBackgroundColor,
                            RoundedCornerShape(4.dp)
                        )
                        .then(
                            Modifier
                                .size(buttonSize)
                                .border(2.dp, MaterialTheme.colors.iconButtonBorderColor)
                        ),
                        onClick = { onBack() })
                    {
                        Icon(
                            painter = painterResource(id = model.iconMap["undo"]!!),
                            contentDescription = null, // decorative element
                            tint = MaterialTheme.colors.iconButtonIconColor
                        )
                    }

                    // EX BUTTON
                    IconButton(modifier = Modifier
                        .padding(2.dp)
                        .background(
                            MaterialTheme.colors.iconButtonBackgroundColor,
                            RoundedCornerShape(4.dp)
                        )
                        .then(
                            Modifier
                                .size(buttonSize)
                                .border(2.dp, MaterialTheme.colors.iconButtonBorderColor)
                        ),
                        onClick = { onExpand() })
                    {
                        Icon(
                            painter = painterResource(id = model.iconMap["expand"]!!),
                            contentDescription = null, // decorative element
                            tint = MaterialTheme.colors.iconButtonIconColor
                        )
                    }

                    // Add Counterpoint Button
                    IconButton(modifier = Modifier
                        .padding(2.dp)
                        .background(
                            MaterialTheme.colors.iconButtonBackgroundColor,
                            RoundedCornerShape(4.dp)
                        )
                        .then(
                            Modifier
                                .size(buttonSize)
                                .border(2.dp, MaterialTheme.colors.iconButtonBorderColor)
                        ),
                        onClick = {
                            dialogState.value = true
                        })
                    {
                        Icon(
                            painter = painterResource(id = model.iconMap["counterpoint"]!!),
                            contentDescription = null, // decorative element
                            tint = MaterialTheme.colors.iconButtonIconColor
                        )
                    }

                    FreePartsButtons(
                        fontSize = 22,
                        onAscDynamicClick = { onFreePart(TREND.ASCENDANT_DYNAMIC) },
                        onAscStaticClick = { onFreePart(TREND.ASCENDANT_STATIC) },
                        onDescDynamicClick = { onFreePart(TREND.DESCENDANT_DYNAMIC) },
                        onDescStaticClick = { onFreePart(TREND.DESCENDANT_STATIC) }
                    )
                    // PLAY BUTTON
                    IconButton(modifier = Modifier
                        .padding(2.dp)
                        .background(
                            MaterialTheme.colors.iconButtonBackgroundColor,
                            RoundedCornerShape(4.dp)
                        )
                        .then(
                            Modifier
                                .size(buttonSize)
                                .border(2.dp, MaterialTheme.colors.iconButtonBorderColor)
                        ),
                        onClick = { onPlay() })
                    {
                        Icon(
                            painter = painterResource(id = model.iconMap["play"]!!),
                            contentDescription = null, // decorative element
                            tint = MaterialTheme.colors.iconButtonIconColor
                        )
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

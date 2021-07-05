package com.cristianovecchi.mikrokanon.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.AIMUSIC.Clip
import com.cristianovecchi.mikrokanon.AIMUSIC.Counterpoint
import com.cristianovecchi.mikrokanon.AIMUSIC.TREND
import com.cristianovecchi.mikrokanon.ActiveButtons
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.db.UserOptionsData
import com.cristianovecchi.mikrokanon.locale.Lang
import com.cristianovecchi.mikrokanon.toStringAll
import com.cristianovecchi.mikrokanon.ui.buttonsDisplayBackgroundColor
import com.cristianovecchi.mikrokanon.ui.sequencesListBackgroundColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ResultDisplay(model: AppViewModel, iconMap: Map<String, Int>,
                  onKP: (Int, Boolean) -> Unit = { _, _ -> },
                  onClick: (Counterpoint) -> Unit = {},
                  onBack: () -> Unit = {},
                  onFreePart: (TREND) -> Unit = {},
                  onExpand: () -> Unit = {},
                  onPlay: () -> Unit = {},
                  onStop: () -> Unit = {}
                  )
{
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    model.userOptionsData.observeAsState(initial = listOf()).value // to force recomposing when options change
    val language = Lang.provideLanguage(model.getUserLangDef())
    val notesNames = language.noteNames
    val counterpoints by model.counterpoints.asFlow().collectAsState(initial = emptyList())
    val counterpointsData: List<Pair<Counterpoint, List<List<String>>>> = counterpoints.map{Pair(it, Clip.toClipsText(it, notesNames))}

    val elaborating by model.elaborating.asFlow().collectAsState(initial = false)
    val playing by model.playing.asFlow().collectAsState(initial = false)
    var scrollToTopList by remember{mutableStateOf(false)}
    val activeButtons by model.activeButtons.asFlow().collectAsState(initial = ActiveButtons(counterpoint = true, freeparts = true))

    val elaboratingBackgroundColor by animateColorAsState(
        if(elaborating) Color(0f,0f,0f,0.3f) else Color(0f,0f,0f,0.0f) )
    val backgroundColor = MaterialTheme.colors.sequencesListBackgroundColor
    val buttonsBackgroundColor = MaterialTheme.colors.buttonsDisplayBackgroundColor
    val dimensions = model.dimensions

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
            val buttonSize = dimensions.outputButtonSize
            Box(modifier = modifier4) {
                LazyColumn(modifier = Modifier.fillMaxSize(), state = listState)
                {
                    itemsIndexed(counterpointsData) { index, counterpointsData ->
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
                            dimensions.outputNoteTableFontSize,
                            onClick = { onClick(counterpoint) })

                       // if(model.selectedCounterpoint.value!! == counterpoint) indexSelected = index
                    }
                }
                if(scrollToTopList && !elaborating) {
                    coroutineScope.launch {
                        delay(200)
                        listState.animateScrollToItem(0)
                        scrollToTopList = false
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

                SequencesDialog(dialogState = dialogState, fontSize = dimensions.sequenceDialogFontSize,
                    title = language.choose2ndSequence, repeatText = language.repeatSequence, okText = language.OKbutton,
                    sequencesList = model.sequences.value!!.map { it.toStringAll(notesNames) },
                    onSubmitButtonClick = { index, repeat ->
                        dialogState.value = false
                        if (index != -1) {
                            onKP(index, repeat); scrollToTopList = true
                        }
                    })
                Row(verticalAlignment = Alignment.CenterVertically) {

                    // UNDO BUTTON
                    CustomButton(
                        iconId = iconMap["undo"]!!,
                        isActive = activeButtons.undo,
                        buttonSize = buttonSize
                    ) {
                        if (!elaborating) onBack(); scrollToTopList = !model.lastComputationIsExpansion()
                    }
                    // EXPAND BUTTON
                    CustomButton(
                        iconId = iconMap["expand"]!!,
                        isActive = activeButtons.expand,
                        buttonSize = buttonSize
                    ) {
                        if (!elaborating) onExpand();scrollToTopList = false
                    }
                    // Add Counterpoint Button
                    CustomButton(
                        iconId = iconMap["counterpoint"]!!,
                        isActive = activeButtons.counterpoint,
                        buttonSize = buttonSize
                    ) {
                        if (!elaborating) dialogState.value = true
                    }

                    FreePartsButtons(
                        fontSize = dimensions.outputFPbuttonFontSize,
                        isActive = activeButtons.freeparts,
                        onAscDynamicClick = {
                            if (!elaborating) onFreePart(TREND.ASCENDANT_DYNAMIC);
                            scrollToTopList = true
                        },
                        onAscStaticClick = {
                            if (!elaborating) onFreePart(TREND.ASCENDANT_STATIC);
                            scrollToTopList = true

                        },
                        onDescDynamicClick = {
                            if (!elaborating) onFreePart(TREND.DESCENDANT_DYNAMIC);
                            scrollToTopList = true

                        },
                        onDescStaticClick = {
                            if (!elaborating) onFreePart(TREND.DESCENDANT_STATIC);
                            scrollToTopList = true
                        }
                    )
                    // PLAY||STOP BUTTON
                    if (!playing) {
                        CustomButton(
                            iconId = iconMap["play"]!!,
                            isActive = activeButtons.playOrStop,
                            buttonSize = buttonSize
                        ) {
                            if (!elaborating) {
                                onPlay(); scrollToTopList = false
                            }
                        }
                    } else {
                        CustomButton(
                            iconId = iconMap["stop"]!!,
                            isActive = activeButtons.playOrStop,
                            buttonSize = buttonSize
                        ) {
                            onStop(); scrollToTopList = false
                        }
                    }

                }
            }
            Column(
                modifier1,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IntervalSetSelector(
                    model, fontSize = dimensions.outputIntervalSetFontSize
                ) { scrollToTopList = true }
            }
        }
}

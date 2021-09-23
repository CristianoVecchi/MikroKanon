package com.cristianovecchi.mikrokanon.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.*
import com.cristianovecchi.mikrokanon.AIMUSIC.Clip
import com.cristianovecchi.mikrokanon.AIMUSIC.Counterpoint
import com.cristianovecchi.mikrokanon.AIMUSIC.TREND
import com.cristianovecchi.mikrokanon.locale.Lang
import com.cristianovecchi.mikrokanon.locale.getZodiacPlanets

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ResultDisplay(model: AppViewModel, iconMap: Map<String, Int>,
                  onKP: (Int, Boolean) -> Unit = { _, _ -> },
                  onWave: (Int) -> Unit,
                  onTritoneSubstitution: () -> Unit = {},
                  onRound: () -> Unit = {},
                  onCadenza: () -> Unit = {},
                  onSingle: () -> Unit = {},
                  onPedal: (Int) -> Unit = {},
                  onClick: (Counterpoint) -> Unit = {},
                  onBack: () -> Unit = {},
                  onFreePart: (TREND) -> Unit = {},
                  onExpand: () -> Unit = {},
                  onFlourish: () -> Unit = {},
                  onPlay: () -> Unit = {},
                  onStop: () -> Unit = {}
                  )
{
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val userOptionsData = model.userOptionsData.observeAsState(initial = listOf()).value // to force recomposing when options change
    val detectorIntervalSet: List<Int> = if(userOptionsData.isNotEmpty())
        createIntervalSetFromFlags(userOptionsData[0].detectorFlags)
        else listOf()
    val detectorExtensions: List<Int> = if(userOptionsData.isNotEmpty()) (1..userOptionsData[0].detectorExtension).toList()
    else listOf()
    val language = Lang.provideLanguage(model.getUserLangDef())
    val notesNames = language.noteNames
    val colors = model.appColors
    val counterpoints by model.counterpoints.asFlow().collectAsState(initial = emptyList())
    val counterpointsData: List<Pair<Counterpoint, List<List<String>>>> = counterpoints.map{Pair(it, Clip.toClipsText(it, notesNames, model.zodiacSignsActive, model.zodiacEmojisActive))}

    val elaborating by model.elaborating.asFlow().collectAsState(initial = false)
    val playing by model.playing.asFlow().collectAsState(initial = false)
    var scrollToTopList by remember{mutableStateOf(false)}
    val activeButtons by model.activeButtons.asFlow().collectAsState(initial = ActiveButtons(counterpoint = true, specialFunctions = true,freeparts = true))

    val elaboratingBackgroundColor by animateColorAsState(
        if(elaborating) Color(0f,0f,0f,0.3f) else Color(0f,0f,0f,0.0f) )
    val backgroundColor = colors.sequencesListBackgroundColor
    val buttonsBackgroundColor = colors.buttonsDisplayBackgroundColor
    val dimensions = model.dimensions

    val dialogState = remember { mutableStateOf(false) }
    val buttonsDialogData = remember { mutableStateOf(ButtonsDialogData(model = model))}
    val intervalSetDialogData = remember { mutableStateOf(MultiListDialogData())}

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
                    itemsIndexed(counterpointsData) { _ , counterpointsData ->
                        val counterpoint = counterpointsData.first
                        val parts = counterpointsData.second
                        val maxSize = parts.maxOf { it.size }

                        var redNotes: List<List<Boolean>>? = if(detectorIntervalSet.isNotEmpty())
                            counterpoint.detectParallelIntervals(detectorIntervalSet, detectorExtensions) else null
                        redNotes =  if (redNotes?.flatten()?.count{ it } ?: 0 == 0) null else redNotes

                        val _clipsText: MutableList<List<String>> = mutableListOf()
                        for (i in 0 until maxSize) {
                            val col: MutableList<String> = mutableListOf()
                            for (j in parts.indices) {
                                val text = if (i < parts[j].size) parts[j][i] else ""
                                col.add(text)
                            }
                            _clipsText.add(col.toList())
                        }
                        val clipsText = _clipsText.toList()

                        NoteTable(
                            model,
                            counterpoint,
                            clipsText, colors,
                            dimensions.outputNoteTableFontSize,
                            redNotes,
                            onClick = { onClick(counterpoint) })

                       // if(model.selectedCounterpoint.value!! == counterpoint) indexSelected = index
                    }
                }
                if(scrollToTopList && !elaborating) {
                    coroutineScope.launch {
                        delay(200)
                        if(counterpointsData.isNotEmpty()) listState.animateScrollToItem(0)
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
                modifier1.padding(top = 6.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                val sequencesToString = model.sequences.value!!.map { it.toStringAll(notesNames, model.zodiacSignsActive, model.zodiacEmojisActive) }
                SequencesDialog(dialogState = dialogState, fontSize = dimensions.sequenceDialogFontSize,
                    title = language.choose2ndSequence, repeatText = language.repeatSequence, okText = language.OKbutton,
                    sequencesList = sequencesToString,
                    onSubmitButtonClick = { index, repeat ->
                        dialogState.value = false
                        if (index != -1) {
                            onKP(index, repeat); scrollToTopList = true
                        }
                    })

                ButtonsDialog(buttonsDialogData, language.OKbutton, model)
                MultiListDialog(intervalSetDialogData, dimensions.sequenceDialogFontSize, language.OKbutton)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column{
                        // UNDO BUTTON
                        CustomButton(
                            iconId = iconMap["undo"]!!,
                            isActive = activeButtons.undo,
                            buttonSize = buttonSize, colors = colors
                        ) {
                            if (!elaborating) onBack(); scrollToTopList = !model.lastComputationIsExpansion()
                        }
                        // HORIZONTAL INTERVAL SET DIALOG BUTTON
                        CustomButton(
                            iconId = iconMap["horizontal_movements"]!!,
                            isActive = true,
                            buttonSize = buttonSize, colors = colors
                        ) {
                            if (!elaborating) {
                                val flags = model.userOptionsData.value!![0].intSetHorFlags
                                val intsFromFlags = convertFlagsToInts(flags)
                                val intervalNames = if(model.zodiacPlanetsActive) getZodiacPlanets(model.zodiacEmojisActive) else language.intervalSet.map{ it.replace("\n"," / ") }
                                    intervalSetDialogData.value = MultiListDialogData(true, intervalNames,
                                        intsFromFlags.toSet(), dialogTitle = "${language.selectIntervalsForFP}\n${language.FPremember}"
                                    ) { indexes ->
                                        model.updateUserOptions(
                                            "intSetHorFlags",
                                            if(indexes.isEmpty()) 0b1111111 else convertIntsToFlags(indexes.toSortedSet())
                                        )
                                        intervalSetDialogData.value = MultiListDialogData(itemList = intervalSetDialogData.value.itemList)
                                    }

                            }
                        }

                    }
                    ExtensionButtons(model = model, isActive = activeButtons.expand, buttonSize = buttonSize, colors = colors,
                         onExpand = { if (!elaborating) onExpand();scrollToTopList = false },
                         onFlourish ={ if (!elaborating) onFlourish();scrollToTopList = false }
                    )

                    // Add and Special Functions
                    FunctionButtons(model = model, isActiveCounterpoint = activeButtons.counterpoint,
                        isActiveSpecialFunctions = activeButtons.specialFunctions,
                        buttonSize = buttonSize, colors = colors,
                        onAdd = { if (!elaborating) dialogState.value = true },
                        onSpecialFunctions = {
                            val close = { buttonsDialogData.value = ButtonsDialogData(model = model) }
                            if(!elaborating) {
                                buttonsDialogData.value = ButtonsDialogData(true,
                                    language.selectSpecialFunction,
                                    model, isActiveWaves = activeButtons.waves, isActivePedals = activeButtons.pedals,
                                    onWave3 = { onWave(3); close(); scrollToTopList = true },
                                    onWave4 = { onWave(4); close(); scrollToTopList = true },
                                    onWave6 = { onWave(6); close(); scrollToTopList = true },
                                    onTritoneSubstitution = { onTritoneSubstitution(); close() },
                                    onRound = { onRound(); close() },
                                    onCadenza = { onCadenza(); close() },
                                    onSingle = { onSingle(); close() },
                                    onPedal1 = { onPedal(1); close() },
                                    onPedal3 = { onPedal(3); close() },
                                    onPedal5 = { onPedal(5); close() },
                                    )
                                {
                                    close()
                                }
                            }
                        }
                    )

                    FreePartsButtons(
                        colors = colors,
                        fontSize = dimensions.outputFPbuttonFontSize,
                        isActive = activeButtons.freeparts,
                        onAscDynamicClick = {
                            if (!elaborating) onFreePart(TREND.ASCENDANT_DYNAMIC)
                            scrollToTopList = true
                        },
                        onAscStaticClick = {
                            if (!elaborating) onFreePart(TREND.ASCENDANT_STATIC)
                            scrollToTopList = true
                        },
                        onDescDynamicClick = {
                            if (!elaborating) onFreePart(TREND.DESCENDANT_DYNAMIC)
                            scrollToTopList = true
                        },
                        onDescStaticClick = {
                            if (!elaborating) onFreePart(TREND.DESCENDANT_STATIC)
                            scrollToTopList = true
                        }
                    )
                    // PLAY||STOP BUTTON
                    if (!playing) {
                        CustomButton(
                            iconId = iconMap["play"]!!,
                            isActive = activeButtons.playOrStop,
                            buttonSize = buttonSize, colors = colors
                        ) {
                            if (!elaborating) {
                                onPlay(); scrollToTopList = false
                            }
                        }
                    } else {
                        CustomButton(
                            iconId = iconMap["stop"]!!,
                            isActive = activeButtons.playOrStop,
                            buttonSize = buttonSize, colors = colors
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
                    model,
                    fontSize = if(model.zodiacPlanetsActive) dimensions.outputIntervalSetFontSize + 8 else dimensions.outputIntervalSetFontSize,
                    names = if(model.zodiacPlanetsActive) getZodiacPlanets(model.zodiacEmojisActive) else language.intervalSet, colors = colors
                ) { scrollToTopList = true }
            }
        }
}

package com.cristianovecchi.mikrokanon.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.*
import com.cristianovecchi.mikrokanon.AIMUSIC.Clip
import com.cristianovecchi.mikrokanon.AIMUSIC.Counterpoint
import com.cristianovecchi.mikrokanon.AIMUSIC.TREND
import com.cristianovecchi.mikrokanon.composables.dialogs.*
import com.cristianovecchi.mikrokanon.locale.Lang
import com.cristianovecchi.mikrokanon.locale.getIntervalsForTranspose
import com.cristianovecchi.mikrokanon.locale.getZodiacPlanets
import com.cristianovecchi.mikrokanon.ui.shift

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun ResultDisplay(model: AppViewModel, iconMap: Map<String, Int>,
                  selectedCounterpointFlow: Flow<Counterpoint>,
                  elaboratingFlow: Flow<Boolean>,
                  onKP: (Int, Boolean) -> Unit = { _, _ -> },
                  onTranspose: (List<Int>) -> Unit,
                  onWave: (Int) -> Unit,
                  onTritoneSubstitution: () -> Unit = {},
                  onRound: () -> Unit = {},
                  onCadenza: (List<Int>) -> Unit = {},
                  onScarlatti: () -> Unit = {},
                  onOverlap: (Int, Boolean) -> Unit,
                  onGlue: (Int) -> Unit,
                  onEraseIntervals: () -> Unit = {},
                  onSingle: () -> Unit = {},
                  onDoppelgänger: () -> Unit = {},
                  onPedal: (Int) -> Unit = {},
                  onClick: (Counterpoint) -> Unit = {},
                  onSort: (Int) -> Unit = {},
                  onUpsideDown: () -> Unit = {},
                  onSavingCounterpoint: (Int) -> Unit = {},
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

    val elaborating: Boolean by elaboratingFlow.collectAsState(initial = false)
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
    val transposeDialogData = remember { mutableStateOf(MultiNumberDialogData(model = model))}
    val cadenzaDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val selectCounterpointDialogData by lazy { mutableStateOf(ButtonsDialogData(model = model))}

    val selCounterpoint: Counterpoint by selectedCounterpointFlow.collectAsState(initial = model.selectedCounterpoint.value!!)

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(backgroundColor)
        ) {
            val weights = dimensions.outputWeights
            val modifierAbove = Modifier
                .fillMaxWidth()
                .weight(weights.first)
            val modifierBottom = Modifier
                .fillMaxSize()
                .background(buttonsBackgroundColor)
                .weight(weights.second)
            val buttonSize = dimensions.outputButtonSize
            Box(modifier = modifierAbove) {
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
                            dimensions.outputNoteTableFontSize, dimensions.outputNoteTableCellWidth,
                            redNotes,
                            onClick = { onClick(counterpoint); })

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
                modifierBottom.padding(top = 6.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                val sequencesToString = model.sequences.value!!.map { it.toStringAll(notesNames, model.zodiacSignsActive, model.zodiacEmojisActive) }
                SequencesDialog(dialogState = dialogState, dimensions = dimensions,
                    title = language.choose2ndSequence, repeatText = language.repeatSequence, okText = language.OKbutton,
                    sequencesList = sequencesToString,
                    onSubmitButtonClick = { index, repeat ->
                        dialogState.value = false
                        if (index != -1) {
                            onKP(index, repeat); scrollToTopList = true
                        }
                    })
                ButtonsDialog(buttonsDialogData, dimensions, language.OKbutton, model, language)
                MultiListDialog(intervalSetDialogData, dimensions, language.OKbutton)
                TransposeDialog(transposeDialogData, dimensions, getIntervalsForTranspose(language.intervalSet))
                CadenzaDialog(cadenzaDialogData, dimensions, language.OKbutton)
                SelectCounterpointDialog( buttonsDialogData = selectCounterpointDialogData,
                    dimensions = dimensions,model = model,language = language)
                // STACK ICONS
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 15.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween){
                    val stackIconSize = dimensions.outputStackIconSize
                    val percentFontSize = dimensions.outputPercentFontSize
                    Row{
                        val icons = model.stackIcons.takeLast(12).map{model.iconMap[it]!!}
                        icons.forEach{ iconId ->
                            Icon(
                                modifier = Modifier.size(stackIconSize.dp),
                                painter = painterResource(id = iconId),
                                contentDescription = null, // decorative element
                                tint = colors.selCardTextColorSelected.shift(0.1f)
                            )
                        }
                    }
                    Row{
                        val percentStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = percentFontSize.sp,
                            color = colors.selCardTextColorSelected.shift(0.1f), fontWeight = FontWeight.Bold
                        )
                        val percent = ( 1f - selCounterpoint.findEmptiness() ) * 100f
                        val nNotes = selCounterpoint.nNotes()
                        val formattedPercent = String.format("%.1f", percent)
                        Text( text = "$nNotes♪    ${formattedPercent}%",
                            style =  percentStyle  )

                    }
                }

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
                                        intsFromFlags.toSet(), dialogTitle = "${language.selectHorizontalIntervals}" // \n${language.FPremember}"
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
                         onExpand = { if (!elaborating) onExpand(); scrollToTopList = false },
                         onTranspose = {  if (!elaborating) {
                             transposeDialogData.value = MultiNumberDialogData(
                                 true, language.selectTranspositions,
                                 model = model) { transpositions ->
                                 onTranspose(transpositions.extractIntsFromCsv())
                                 transposeDialogData.value = MultiNumberDialogData(model = model)

                             }
                             scrollToTopList = false
                            }
                         }
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
                                    onCadenza = {
                                        close();
                                        cadenzaDialogData.value = MultiNumberDialogData(true,
                                            language.selectCadenzaForm, model.cadenzaValues, 0, 16, model = model,
                                        ){ newValues ->
                                            model.cadenzaValues = newValues
                                            onCadenza( newValues.extractIntsFromCsv() ) // CADENZA DIALOG OK BUTTON
                                        }
                                                },
                                    onScarlatti = { onScarlatti(); close() },
                                    onOverlap = {
                                        buttonsDialogData.value = ButtonsDialogData(model = model)// Close Buttons Dialog
                                        selectCounterpointDialogData.value = ButtonsDialogData(true,
                                            language.selectToOverlap, model,
                                            onCounterpointSelected = { position ->
                                                onOverlap(position, false)
                                                selectCounterpointDialogData.value = ButtonsDialogData(model = model) // Close Counterpoint Dialog
                                            })
                                    },
                                    onCrossover = {
                                        buttonsDialogData.value = ButtonsDialogData(model = model)// Close Buttons Dialog
                                        selectCounterpointDialogData.value = ButtonsDialogData(true,
                                            language.selectToCrossOver, model,
                                            onCounterpointSelected = { position ->
                                                onOverlap(position, true)
                                                selectCounterpointDialogData.value = ButtonsDialogData(model = model) // Close Counterpoint Dialog
                                            })
                                    },
                                    onGlue = {
                                        buttonsDialogData.value = ButtonsDialogData(model = model)// Close Buttons Dialog
                                        selectCounterpointDialogData.value = ButtonsDialogData(true,
                                            language.selectToOverlap, model,
                                            onCounterpointSelected = { position ->
                                                onGlue(position)
                                                selectCounterpointDialogData.value = ButtonsDialogData(model = model) // Close Counterpoint Dialog
                                            })
                                    },
                                    onFlourish = {onFlourish(); close(); scrollToTopList = false},
                                    onEraseIntervals = { onEraseIntervals(); close() },
                                    onSingle = { onSingle(); close() },
                                    onSort = { sortType -> onSort(sortType); close() },
                                    onUpsideDown = { onUpsideDown(); close()},
                                    onCounterpointSelected = { position -> onSavingCounterpoint(position); close()},
                                    onDoppelgänger = { onDoppelgänger(); close()},
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
                modifier = Modifier.background(colors.buttonsDisplayBackgroundColor)
                    .padding(start = 10.dp, top = 8.dp, end = 15.dp, bottom = 15.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IntervalSetSelector(
                    model,
                    fontSize = if(model.zodiacPlanetsActive) dimensions.outputIntervalSetFontSize * 2 else dimensions.outputIntervalSetFontSize,
                    names = if(model.zodiacPlanetsActive) getZodiacPlanets(model.zodiacEmojisActive) else language.intervalSet, colors = colors
                ) { scrollToTopList = true }
            }
        }
}

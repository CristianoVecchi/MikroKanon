package com.cristianovecchi.mikrokanon.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.*
import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.composables.counterpointviews.*
import com.cristianovecchi.mikrokanon.composables.dialogs.*
import com.cristianovecchi.mikrokanon.convertFlagsToInts
import com.cristianovecchi.mikrokanon.convertIntsToFlags
import com.cristianovecchi.mikrokanon.extractIntsFromCsv
import com.cristianovecchi.mikrokanon.locale.Lang
import com.cristianovecchi.mikrokanon.locale.getIntervalsForTranspose
import com.cristianovecchi.mikrokanon.locale.getZodiacPlanets
import com.cristianovecchi.mikrokanon.ui.Dimensions
import com.cristianovecchi.mikrokanon.ui.shift

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun ResultDisplay(model: AppViewModel,
                  dimensionsFlow: Flow<Dimensions>,
                  iconMap: Map<String, Int>,
                  selectedCounterpointFlow: Flow<Counterpoint>,
                  counterpointsFlow: Flow<List<Counterpoint>>,
                  elaboratingFlow: Flow<Boolean>,
                  onKP: (Int, Boolean) -> Unit = { _, _ -> },
                  onTranspose: (List<Pair<Int,Int>>) -> Unit,
                  onWave: (Int) -> Unit,
                  onTritoneSubstitution: () -> Unit = {},
                  onRound: () -> Unit = {},
                  onCadenza: (List<Int>) -> Unit = {},
                  onScarlatti: () -> Unit = {},
                  onOverlap: (Int, Boolean) -> Unit,
                  onGlue: (Int) -> Unit,
                  onMaze: (List<List<Int>>) -> Unit,
                  onEraseIntervals: () -> Unit = {},
                  onSingle: () -> Unit = {},
                  onDoppelgänger: () -> Unit = {},
                  onPedal: (Int) -> Unit = {},
                  onClick: (Counterpoint) -> Unit = {},
                  onSort: (Int) -> Unit = {},
                  onUpsideDown: () -> Unit = {},
                  onArpeggio: (ARPEGGIO) -> Unit = {},
                  onSavingCounterpoint: (Int) -> Unit = {},
                  onBack: () -> Unit = {},
                  onFreePart: (TREND) -> Unit = {},
                  onExpand: () -> Unit = {},
                  onFlourish: () -> Unit = {},
                  onEWH: (Int) -> Unit = {},
                  onResolutio: (Pair<Set<Int>,String>) -> Unit,
                  onDoubling: (List<Pair<Int,Int>>) -> Unit,
                  onPlay: () -> Unit = {},
                  onStop: () -> Unit = {}
                  )
{
    //val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val userOptionsData by model.userOptionsData.asFlow().collectAsState(initial = listOf())
    val triple by derivedStateOf {
        if(userOptionsData.isNotEmpty()) {
            model.setAppColors(userOptionsData[0].colors)
            Triple(
                model.appColors,
                createIntervalSetFromFlags(userOptionsData[0].detectorFlags),
                (1..userOptionsData[0].detectorExtension).toList()
            )
        } else Triple(model.appColors, listOf(), listOf())
    }
    val (colors, detectorIntervalSet, detectorExtensions) = triple
    val language by model.language.asFlow().collectAsState(initial = Lang.provideLanguage(model.getUserLangDef()))
    val notesNames = language.noteNames
    val counterpointView = model.counterpointView
    val counterpoints by counterpointsFlow.collectAsState(initial = emptyList())
    val counterpointsData: List<Pair<Counterpoint, List<List<Any>>>> by derivedStateOf {
        when (counterpointView){
            0 -> counterpoints.map{Pair(it, Clip.toClipsText(it, notesNames, model.zodiacSignsActive, model.zodiacEmojisActive))}
            1 -> counterpoints.map{Pair(it, it.getRibattutos())}
            else -> counterpoints.map{Pair(it, listOf(listOf()))}
        }
    }
    val elaborating: Boolean by elaboratingFlow.collectAsState(initial = false)
    val playing by model.playing.asFlow().collectAsState(initial = false)
    //var scrollToTopList by remember{mutableStateOf(false)}
    var scrollToItem by remember{mutableStateOf(-1)}
    val activeButtons by model.activeButtons.asFlow().collectAsState(initial = ActiveButtons(counterpoint = true, specialFunctions = true,freeParts = true))

    val elaboratingBackgroundColor by animateColorAsState(
        if(elaborating) Color(0f,0f,0f,0.3f) else Color(0f,0f,0f,0.0f) )
    val backgroundColor = colors.sequencesListBackgroundColor
    val buttonsBackgroundColor = colors.buttonsDisplayBackgroundColor
    val dimensions by dimensionsFlow.collectAsState(initial = model.dimensions.value!!)
    val noteTableHeight = if(counterpoints.isNotEmpty()) counterpoints[0].parts.size * 80 else 80
    val marbleHeight = 360
    val quantumSquareSide = (dimensions.width / 4 * 3)

    val dialogState = remember { mutableStateOf(false) }
    val buttonsDialogData = remember { mutableStateOf(ButtonsDialogData(model = model))}
    val intervalSetDialogData = remember { mutableStateOf(MultiListDialogData())}
    val transposeDialogData = remember { mutableStateOf(MultiNumberDialogData(model = model))}
    val doublingDialogData = remember { mutableStateOf(MultiNumberDialogData(model = model))}
    val cadenzaDialogData = remember { mutableStateOf(MultiNumberDialogData(model = model))}
    val resolutioDialogData = remember { mutableStateOf(MultiNumberDialogData(model = model))}
    val selectCounterpointDialogData = remember { mutableStateOf(ButtonsDialogData(model = model))}
    val multiSequenceDialogData = remember { mutableStateOf(MultiNumberDialogData(model = model))}
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
                    itemsIndexed(counterpointsData) { _, counterpointsData ->
                        val counterpoint = counterpointsData.first
                        val parts = counterpointsData.second
                        val maxSize = parts.maxOf { it.size }
                        var redNotes: List<List<Boolean>>? = if (detectorIntervalSet.isNotEmpty())
                            counterpoint.detectParallelIntervals(
                                detectorIntervalSet,
                                detectorExtensions
                            ) else null
                        redNotes =
                            if (redNotes?.flatten()?.count { it } ?: 0 == 0) null else redNotes
                        when (counterpointView) {
                            0 -> {
                                val clipsText: MutableList<List<String>> = mutableListOf()
                                for (i in 0 until maxSize) {
                                    val col: MutableList<String> = mutableListOf()
                                    for (j in parts.indices) {
                                        val text =
                                            if (i < parts[j].size) parts[j][i] as String else ""
                                        col.add(text)
                                    }
                                    clipsText.add(col.toList())
                                }
                                NoteCounterpointView(
                                    model,
                                    counterpoint,
                                    clipsText.toList(),
                                    colors,
                                    dimensions.outputNoteTableFontSize,
                                    dimensions.outputNoteTableCellWidth,
                                    redNotes,
                                    onClick = { onClick(counterpoint) })
                            }
                            1 -> MarbleCounterpointView(
                                model = model,
                                counterpoint = counterpoint,
                                ribattutos = counterpointsData.second,
                                colors = colors,
                                totalWidthDp = dimensions.width,
                                totalHeightDp = 360,
                                padding = 10,
                                dpDensity = dimensions.dpDensity,
                                redNotes = redNotes,
                                onClick = { onClick(counterpoint) })
                            2 -> Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                val sideColumnWidth =
                                    (dimensions.width / 4 / 2 / dimensions.dpDensity).dp
                                val sideColumnHeight = (quantumSquareSide / dimensions.dpDensity).dp
                                Column(
                                    Modifier
                                        .width(sideColumnWidth)
                                        .height(sideColumnHeight)
                                        .clickable(onClick = { model.shiftColors(-1) })
                                ) {}
                                QuantumCounterpointView(
                                    model = model,
                                    counterpoint = counterpoint,
                                    appColors = colors,
                                    totalWidthDp = quantumSquareSide,
                                    totalHeightDp = quantumSquareSide,
                                    dpDensity = dimensions.dpDensity,
                                    redNotes = redNotes,
                                    padding = 10,
                                    onClick = { onClick(counterpoint) })
                                Column(
                                    Modifier
                                        .width(sideColumnWidth)
                                        .height(sideColumnHeight)
                                        .clickable(onClick = { model.shiftColors(+1) })
                                ) {}
                            }

                            else -> Unit
                        }
                    }
                    item {
                            if (model.scrollToTopList && !elaborating) {
                                scope.launch { listState.scrollToItem(0) }
                                model.scrollToTopList = false
                        }
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

                val filledSlots by model.filledSlots.asFlow().collectAsState(initial = setOf())
                val sequencesToString by lazy {model.sequences.value!!.map { it.toStringAll(notesNames, model.zodiacSignsActive, model.zodiacEmojisActive) }}
                SequencesDialog(dialogState = dialogState, dimensions = dimensions,
                    title = language.choose2ndSequence, repeatText = language.repeatSequence,
                    okText = language.OkButton, appColors = colors,
                    sequencesList = sequencesToString,
                    onSubmitButtonClick = { index, repeat ->
                        dialogState.value = false
                        if (index != -1) {
                            onKP(index, repeat);
                        }
                    })
                ButtonsDialog(buttonsDialogData, dimensions, language.OkButton, model, language, filledSlots = filledSlots)
                MultiListDialog(intervalSetDialogData, dimensions, language.OkButton, colors)
                TransposeDialog(transposeDialogData, dimensions, getIntervalsForTranspose(language.intervalSet))
                TransposeDialog(doublingDialogData, dimensions, getIntervalsForTranspose(language.intervalSet))
                CadenzaDialog(cadenzaDialogData, buttonsDialogData, dimensions, language.OkButton, model)
                ResolutioDialog(resolutioDialogData, buttonsDialogData, dimensions, language.OkButton, model)
                SelectCounterpointDialog( buttonsDialogData = selectCounterpointDialogData,
                    dimensions = dimensions,model = model,language = language, filledSlots = filledSlots)
                MultiSequenceDialog(multiSequenceDialogData, buttonsDialogData, dimensions, model)
                // STACK ICONS
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 15.dp, bottom = 10.dp)
                    .clickable {
                        val indexItem = model.indexOfSelectedCounterpoint()
                        model.updateUserOptions(
                            "counterpointView",
                            ++model.counterpointView % 3
                        )
                        scrollToItem = indexItem
//                        if(indexItem != -1) {
//                            coroutineScope.launch {
//                                listState.scrollToItem(indexItem)
//                            }
//                        }
                    },
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
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
                            if (!elaborating) onBack(); model.scrollToTopList = !model.lastComputationIsExpansion()
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
                                    //model.dispatchIntervals
                                    intervalSetDialogData.value = MultiListDialogData(itemList = intervalSetDialogData.value.itemList)
                                }
                            }
                        }

                    }
                    ExtensionButtons(model = model, isActive = activeButtons.expand, buttonSize = buttonSize, colors = colors,
                        onExpand = { if (!elaborating) onExpand() },
                        onTranspose = {  if (!elaborating) {
                            transposeDialogData.value = MultiNumberDialogData(
                                true, language.selectTranspositions, value = "0|1",
                                model = model) { transpositions ->
                                onTranspose(transpositions.extractIntPairsFromCsv())
                                transposeDialogData.value = MultiNumberDialogData(model = model)
                            }
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
                                    model, isActiveWaves = activeButtons.waves,
                                    isActivePedals = activeButtons.pedals,
                                    isActiveSpecialFunctions1 = activeButtons.specialFunctions1,
                                    onWave3 = { onWave(3); close() },
                                    onWave4 = { onWave(4); close() },
                                    onWave6 = { onWave(6); close() },
                                    onTritoneSubstitution = { onTritoneSubstitution(); close() },
                                    onRound = { onRound(); close() },
                                    onCadenza = {
                                        cadenzaDialogData.value = MultiNumberDialogData(true,
                                            language.selectCadenzaForm, model.cadenzaValues, 0, 16, model = model,
                                            dispatchCsv= { newValues ->
                                                close()
                                                model.cadenzaValues = newValues
                                                onCadenza( newValues.extractIntsFromCsv() ) // CADENZA DIALOG OK BUTTON
                                            }
                                        )
                                    },
                                    onResolutio = {
                                        resolutioDialogData.value = MultiNumberDialogData(true,
                                            language.selectResolutioForm, model.cadenzaValues, 0, 16, model = model,
                                            dispatchResolutio = { resolutioData ->
                                                model.resolutioValues = resolutioData
                                                onResolutio(resolutioData)
                                                resolutioDialogData.value = MultiNumberDialogData(model = model)
                                            }) },
                                    onDoubling = {
                                        doublingDialogData.value = MultiNumberDialogData(
                                            true, language.selectDoubling, value = "0|1",
                                            model = model) { transpositions ->
                                            close()
                                            onDoubling(transpositions.extractIntPairsFromCsv())
                                            doublingDialogData.value = MultiNumberDialogData(model = model)
                                        }
                                    },
                                    onScarlatti = { onScarlatti(); close() },
                                    onOverlap = {
                                        selectCounterpointDialogData.value = ButtonsDialogData(true,
                                            language.selectToOverlap, model,
                                            onCounterpointSelected = { position ->
                                                close()
                                                onOverlap(position, false)
                                                selectCounterpointDialogData.value = ButtonsDialogData(model = model) // Close Counterpoint Dialog
                                            })
                                    },
                                    onCrossover = {
                                        selectCounterpointDialogData.value = ButtonsDialogData(true,
                                            language.selectToCrossOver, model,
                                            onCounterpointSelected = { position ->
                                                close()
                                                onOverlap(position, true)
                                                selectCounterpointDialogData.value = ButtonsDialogData(model = model) // Close Counterpoint Dialog
                                            })
                                    },
                                    onGlue = {
                                        selectCounterpointDialogData.value = ButtonsDialogData(true,
                                            language.selectToGlue, model,
                                            onCounterpointSelected = { position ->
                                                close()
                                                onGlue(position)
                                                selectCounterpointDialogData.value = ButtonsDialogData(model = model) // Close Counterpoint Dialog
                                            })
                                    },
                                    onMaze = {
                                        //onMaze(listOf(1,2,3,4))//,6,7,8,9,10, 11))
                                        //buttonsDialogData.value = ButtonsDialogData(model = model)// Close Buttons Dialog
                                        val inputIntSequences = selCounterpoint.parts.map{ it.absPitches.toList() }
                                        multiSequenceDialogData.value = MultiNumberDialogData(true,
                                            language.addSequencesToMaze, intSequences = inputIntSequences, model = model,
                                            dispatchIntLists = { intSequences ->
                                                close()
                                                onMaze(intSequences)
                                            }
                                        )
                                    },
                                    onFlourish = {onFlourish(); close(); },
                                    onEraseIntervals = { onEraseIntervals(); close() },
                                    onSingle = { onSingle(); close() },
                                    onSort = { sortType -> onSort(sortType); close() },
                                    onUpsideDown = { onUpsideDown(); close()},
                                    onArpeggio = { arpeggioType -> onArpeggio(arpeggioType); close()},
                                    onCounterpointSelected = { position -> onSavingCounterpoint(position)},
                                    onDoppelgänger = { onDoppelgänger(); close()},
                                    onEWH = { nParts -> onEWH(nParts); close()},
                                    onPedal1 = { onPedal(1); close() },
                                    onPedal3 = { onPedal(3); close() },
                                    onPedal5 = { onPedal(5); close() },
                                )
                            }
                        }
                    )
                    FreePartsButtons(
                        colors = colors,
                        fontSize = dimensions.outputFPbuttonFontSize,
                        isActive = activeButtons.freeParts,
                        onAscDynamicClick = {
                            if (!elaborating) onFreePart(TREND.ASCENDANT_DYNAMIC)
                        },
                        onAscStaticClick = {
                            if (!elaborating) onFreePart(TREND.ASCENDANT_STATIC)
                        },
                        onDescDynamicClick = {
                            if (!elaborating) onFreePart(TREND.DESCENDANT_DYNAMIC)
                        },
                        onDescStaticClick = {
                            if (!elaborating) onFreePart(TREND.DESCENDANT_STATIC)
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
                                onPlay();
                            }
                        }
                    } else {
                        CustomButton(
                            iconId = iconMap["stop"]!!,
                            isActive = activeButtons.playOrStop,
                            buttonSize = buttonSize, colors = colors
                        ) {
                            onStop();
                        }
                    }

                }
            }
            Column(
                modifier = Modifier
                    .background(colors.buttonsDisplayBackgroundColor)
                    .padding(start = 10.dp, top = 8.dp, end = 15.dp, bottom = 15.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IntervalSetSelector(
                    model,
                    fontSize = if(model.zodiacPlanetsActive) dimensions.outputIntervalSetFontSize * 2 else dimensions.outputIntervalSetFontSize,
                    names = if(model.zodiacPlanetsActive) getZodiacPlanets(model.zodiacEmojisActive) else language.intervalSet, colors = colors
                ) {  }
            }
            LaunchedEffect(key1 = counterpointView) {
                if(scrollToItem > -1 && counterpoints.isNotEmpty() && counterpoints.size > 1) {
                    delay(100)
                    if (scrollToItem == 0 || scrollToItem == counterpoints.size - 1) {
                        listState.scrollToItem(scrollToItem)
                    } else {
                        val offset = when (counterpointView) {
                            0 -> noteTableHeight / 8 * 7
                            1 -> marbleHeight / 4 * 3
                            else -> quantumSquareSide / 5 * 4
                        }
                        listState.scrollToItem(scrollToItem - 1, offset)
                    }
                    scrollToItem = -1
                }
            }
        }

    }


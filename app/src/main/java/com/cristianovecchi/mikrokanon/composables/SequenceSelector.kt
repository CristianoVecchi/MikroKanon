package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.*
import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.composables.dialogs.*
import com.cristianovecchi.mikrokanon.db.UserOptionsData
import com.cristianovecchi.mikrokanon.locale.Lang
import com.cristianovecchi.mikrokanon.locale.getIntervalsForTranspose
import com.cristianovecchi.mikrokanon.ui.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun SequenceSelector(model: AppViewModel,
                     dimensionsFlow: Flow<Dimensions>,
                     userOptionsDataFlow: Flow<List<UserOptionsData>>,
                     onSelect: (Int) -> Unit = model::changeSequenceSelection,
                     onDelete: (Int) -> Unit = model::deleteSequence,
                     onAdd: (ArrayList<Clip>, Boolean) -> Unit,
                     onWave: (Int, ArrayList<Clip>) -> Unit,
                     onQuote: (ArrayList<Clip>, Boolean) -> Unit,
                     onLoadingCounterpoint: (Int) -> Unit,
                     onTritoneSubstitution: (Int) -> Unit,
                     onRound: (ArrayList<Clip>, List<Pair<Int,Int>>) -> Unit,
                     onCadenza: (ArrayList<Clip>, List<Int>) -> Unit,
                     onFormat: (ArrayList<Clip>, List<Int>) -> Unit,
                     onScarlatti: (ArrayList<Clip>) -> Unit,
                     onOverlap: (ArrayList<Clip>, Int, Boolean) -> Unit,
                     onGlue: (ArrayList<Clip>, Int) -> Unit,
                     onFlourish: (ArrayList<Clip>) -> Unit,
                     onEraseIntervals: (ArrayList<Clip>) -> Unit,
                     onSingle: (ArrayList<Clip>) -> Unit,
                     onDoppelgänger: (ArrayList<Clip>) -> Unit,
                     onPedal: (Int, ArrayList<Clip>) -> Unit,
                     onKP: (ArrayList<Clip>, Int, Boolean) -> Unit,
                     onFreePart: (ArrayList<Clip>, TREND) -> Unit,
                     onMikroKanons2: (ArrayList<Clip>) -> Unit,
                     onMikroKanons3: (ArrayList<Clip>) -> Unit,
                     onMikroKanons4: (ArrayList<Clip>) -> Unit,
                     onMikroKanons5reducted: (ArrayList<Clip>) -> Unit,
                     onMikroKanons6reducted: (ArrayList<Clip>) -> Unit,
                     onMaze: (List<List<Int>>) -> Unit,
                     onEWH: (ArrayList<Clip>, Int) -> Unit,
                     onResolutio: (ArrayList<Clip>, Triple<Set<Int>, String, Int>) -> Unit,
                     onDoubling: (ArrayList<Clip>, List<Pair<Int,Int>>) -> Unit,
                     onChess: (ArrayList<Clip>, Int) -> Unit
                    )
{

    val activeButtons by model.activeButtons.asFlow().collectAsState(initial = ActiveButtons())
    val userOptionsData by model.userOptionsData.asFlow().collectAsState(initial = listOf())// to force recomposing when options change
    val appColors by derivedStateOf {
        if(userOptionsData.isNotEmpty()) model.setAppColors(userOptionsData[0].colors)
        model.appColors // default ALL BLACK
    }
    val language by model.language.asFlow().collectAsState(initial = Lang.provideLanguage(model.getUserLangDef()))
    val backgroundColor = appColors.sequencesListBackgroundColor
    val buttonsBackgroundColor = appColors.buttonsDisplayBackgroundColor
    //val listState = rememberLazyListState()
    val notesNames = language.noteNames
    val dimensions by dimensionsFlow.collectAsState(initial = model.dimensions.value!!)
    val weights = dimensions.selectorWeights

    var selectedIndex by remember{ mutableStateOf(-1) }
    val sequences by model.sequences.asFlow().collectAsState(initial = emptyList())
    //val snackbarVisibleState = remember { mutableStateOf(false) }
    val dialogState = remember { mutableStateOf(false) }
    val buttonsDialogData = remember { mutableStateOf(ButtonsDialogData(model = model))}
    val cadenzaDialogData = remember { mutableStateOf(MultiNumberDialogData(model = model))}
    val resolutioDialogData = remember { mutableStateOf(MultiNumberDialogData(model = model))}
    val formatDialogData = remember { mutableStateOf(MultiNumberDialogData(model = model))}
    val doublingDialogData = remember { mutableStateOf(MultiNumberDialogData(model = model))}
    val roundDialogData = remember { mutableStateOf(MultiNumberDialogData(model = model))}
    val selectCounterpointDialogData = remember { mutableStateOf(ButtonsDialogData(model = model))}
    val chessDialogData = remember { mutableStateOf(ListDialogData())}
    val multiSequenceDialogData = remember { mutableStateOf(MultiNumberDialogData(model = model))}
    val privacyDialogData = remember { mutableStateOf(TextDialogData())}
    val buttonSize = dimensions.selectorButtonSize
    val zodiacFlags by model.zodiacFlags.asFlow().collectAsState(initial = Triple(false,false,false))
    val (zodiacPlanetsActive, zodiacSignsActive, zodiacEmojisActive) = zodiacFlags
    val sequencesToString = model.sequences.value!!.map { it.toStringAll(notesNames, zodiacSignsActive, zodiacEmojisActive) }
    val filledSlots by model.filledSlots.asFlow().collectAsState(initial = setOf())

        Column(modifier = Modifier
            .fillMaxHeight()
            .background(appColors.drawerBackgroundColor)) {
            val modifier3 = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .weight(weights.first)
            val modifier1 = Modifier
                .fillMaxSize()
                .background(buttonsBackgroundColor)
                .fillMaxWidth()
                .weight(weights.second)
//            val selected by model.selectedSequence.observeAsState(initial = -1)
//            val sequences by model.sequences.observeAsState(emptyList())
            //val selected by model.selectedSequence.asFlow().collectAsState(initial = -1)


            if(!model.privacyIsAccepted){
                privacyDialogData.value = TextDialogData(
                    true, "",
                ) {
                    model.updateUserOptions("privacy", 1)
                    privacyDialogData.value = TextDialogData()
                }
            }
            SequencesDialog(dialogState = dialogState, dimensions = dimensions,
                title = language.choose2ndSequence, repeatText = language.repeatSequence,
                okText = language.OkButton, appColors = appColors,
                sequencesList = sequencesToString,
                onSubmitButtonClick = { index, repeat ->
                    dialogState.value = false
                    if (index != -1) {
                        onKP(sequences[selectedIndex], index, repeat)
                    }
                }
            )
            ButtonsDialog(buttonsDialogData, dimensions, language.OkButton, workingOnSequences = true,
                model = model, language = language, filledSlots = filledSlots )
            SelectCounterpointDialog( buttonsDialogData = selectCounterpointDialogData,
                dimensions = dimensions,model = model,language = language, filledSlots = filledSlots)
            CadenzaDialog(cadenzaDialogData, buttonsDialogData, dimensions, language.OkButton, model)
            ResolutioDialog(resolutioDialogData, buttonsDialogData, dimensions, language.OkButton, model)
            FormatDialog(formatDialogData, buttonsDialogData, dimensions, language.OkButton, model)
            TransposeDialog(doublingDialogData, dimensions, getIntervalsForTranspose(language.intervalSet))
            TransposeDialog(roundDialogData, dimensions, getIntervalsForTranspose(language.intervalSet))
            MultiSequenceDialog(multiSequenceDialogData, buttonsDialogData, dimensions, model)
            ListDialog(listDialogData = chessDialogData, dimensions = dimensions, appColors = appColors, fillPrevious = true )
            PrivacyDialog(privacyDialogData, dimensions, language.OkButton)

            SequenceScrollableColumn( //listState = listState,
                colors = appColors,
                modifier = modifier3, fontSize = dimensions.selectorClipFontSize,
                notesNames = notesNames,
                zodiacSigns = zodiacSignsActive, emoji = zodiacEmojisActive,
                sequences = sequences,
                selected = selectedIndex, onSelect = {
                    selectedIndex = it
                    onSelect(it)
                }
                    )
                    Column(modifier1) {
                        Row(modifier = Modifier.fillMaxSize(),horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                            SequenceEditingButtons(
                                model = model, isActive = activeButtons.editing,
                                buttonSize = buttonSize, colors = appColors,
                                onDelete = {
                                                onDelete(selectedIndex)
                                                selectedIndex = -1
                                           },
                                onEdit = { onAdd(sequences[selectedIndex], true) },
                                onAdd= { onAdd(ArrayList<Clip>(), false) }
                            )
                            MikroKanonsButtons(
                                model = model, isActive = activeButtons.mikrokanon,
                                buttonSize = buttonSize / 10 * 9, colors = appColors,
                                fontSize = dimensions.selectorMKbuttonFontSize,
                                onMK2Click = {
                                    onMikroKanons2(sequences[selectedIndex])
                                },
                                onMK3Click = {
                                    onMikroKanons3(sequences[selectedIndex])
                                },
                                onMK4Click = {
                                    onMikroKanons4(sequences[selectedIndex])
                                }
                            )
                            // Add and Special Functions
                            FunctionButtons(model = model, colors = appColors,
                                isActiveCounterpoint = activeButtons.counterpoint,
                                isActiveSpecialFunctions = activeButtons.specialFunctions,
                                buttonSize = buttonSize,
                                onAdd = { dialogState.value = true },
                                onSpecialFunctions = {
                                    //val close = { buttonsDialogData.value = ButtonsDialogData(model = model) }
                                    buttonsDialogData.value = ButtonsDialogData(true,
                                        language.selectSpecialFunction,
                                        model, isActiveWaves = activeButtons.waves,
                                        isActivePedals = activeButtons.pedals,
                                        onWave3 = { onWave(3, sequences[selectedIndex]) },
                                        onWave4 = { onWave(4, sequences[selectedIndex]) },
                                        onWave6 = { onWave(6, sequences[selectedIndex]) },
                                        onQuote = { onQuote(sequences[selectedIndex], false)},
                                        onTritoneSubstitution = { onTritoneSubstitution(selectedIndex) },
                                        onRound = {
                                            roundDialogData.value = MultiNumberDialogData(
                                                true, language.selectTranspositions, value = "0|1",
                                                model = model) { transpositions ->
                                                onRound(sequences[selectedIndex], transpositions.extractIntPairsFromCsv())
                                            } },
                                        // onCadenza = { onCadenza(sequences[selected]) },
                                        onCadenza = {
                                            cadenzaDialogData.value = MultiNumberDialogData(true,
                                                language.selectCadenzaForm, model.cadenzaValues, 0, Int.MAX_VALUE, model = model,
                                                dispatchCsv= { newValues ->
                                                    model.cadenzaValues = newValues
                                                    onCadenza( sequences[selectedIndex], newValues.extractIntsFromCsv() ) // CADENZA DIALOG OK BUTTON
                                                }) },
                                        onResolutio = {
                                            resolutioDialogData.value = MultiNumberDialogData(true,
                                                language.selectResolutioForm, model.resolutioValues.second, 0, Int.MAX_VALUE, model = model,
                                                dispatchResolutio = { resolutioData ->
                                                    model.resolutioValues = resolutioData
                                                    onResolutio(sequences[selectedIndex], resolutioData)
                                                }) },
                                        onFormat = {
                                            val counterpointSize = sequences[selectedIndex].size
                                            formatDialogData.value = MultiNumberDialogData(true,
                                                language.selectFormatForm, model.formatValues, 0, counterpointSize, model = model,
                                                dispatchCsv= { newValues ->
                                                    model.formatValues = newValues
                                                    onFormat( sequences[selectedIndex], newValues.extractIntsFromCsv() ) // CADENZA DIALOG OK BUTTON
                                                }
                                            )
                                        },
                                        onDoubling = {
                                            doublingDialogData.value = MultiNumberDialogData(
                                                true, language.selectDoubling, value = "0|1",
                                                model = model) { transpositions ->
                                                onDoubling(sequences[selectedIndex], transpositions.extractIntPairsFromCsv())
                                            }
                                        },
                                        onScarlatti = { onScarlatti(sequences[selectedIndex]) },
                                        onChess = {
                                            chessDialogData.value = ListDialogData(true,
                                                (1..sequences[selectedIndex].size * 2).map{ it.toString()}, 0,
                                                language.selectChessRange) {
                                                onChess(sequences[selectedIndex], it + 1)
                                            }
                                        },
                                        onOverlap = {
                                            selectCounterpointDialogData.value = ButtonsDialogData(true,
                                                language.selectToOverlap, model,
                                                onCounterpointSelected = { position ->
                                                    onOverlap(sequences[selectedIndex],position, false)
                                                    selectCounterpointDialogData.value = ButtonsDialogData(model = model) // Close Counterpoint Dialog
                                                })
                                        },
                                        onCrossover = {
                                            selectCounterpointDialogData.value = ButtonsDialogData(true,
                                                language.selectToCrossOver, model,
                                                onCounterpointSelected = { position ->
                                                    onOverlap(sequences[selectedIndex],position, true)
                                                    selectCounterpointDialogData.value = ButtonsDialogData(model = model) // Close Counterpoint Dialog
                                                })
                                        },
                                        onGlue = {
                                            selectCounterpointDialogData.value = ButtonsDialogData(true,
                                                language.selectToGlue, model,
                                                onCounterpointSelected = { position ->
                                                    onGlue(sequences[selectedIndex],position)
                                                    selectCounterpointDialogData.value = ButtonsDialogData(model = model) // Close Counterpoint Dialog
                                                })
                                        },
                                        onFlourish = { onFlourish(sequences[selectedIndex]) },
                                        onEraseIntervals = { onEraseIntervals(sequences[selectedIndex]) },
                                        onSingle = { onSingle(sequences[selectedIndex]) },
                                        onDoppelgänger = { onDoppelgänger(sequences[selectedIndex])},
                                        onPedal1 = { onPedal(1, sequences[selectedIndex]) },
                                        onPedal3 = { onPedal(3, sequences[selectedIndex]) },
                                        onPedal5 = { onPedal(5, sequences[selectedIndex]) },
                                        onMK5reducted = { onMikroKanons5reducted(sequences[selectedIndex]) },
                                        onMK6reducted = { onMikroKanons6reducted(sequences[selectedIndex]) },
                                        onEWH = { nParts -> onEWH(sequences[selectedIndex], nParts)},
                                        onMaze = {
                                            //onMaze(listOf(1,2,3,4))//,6,7,8,9,10, 11))
                                            val intSequences = listOf(sequences[selectedIndex].map{ it.abstractNote })
                                            multiSequenceDialogData.value = MultiNumberDialogData(true,
                                                language.addSequencesToMaze, intSequences = intSequences, model = model,
                                                dispatchIntLists = { intSequences ->
                                                    onMaze(intSequences)
                                                }
                                            )
                                        },
                                        onCounterpointSelected = { position -> onLoadingCounterpoint(position)}
                                    )
                                }
                            )
                            FreePartsButtons(
                                model = model,
                                colors = appColors,
                                fontSize = dimensions.selectorMKbuttonFontSize,
                                isActive = activeButtons.freeParts,
                                buttonSize = buttonSize / 10 * 9,
                                onAscDynamicClick = { onFreePart(sequences[selectedIndex], TREND.ASCENDANT_DYNAMIC ) },
                                onAscStaticClick = { onFreePart( sequences[selectedIndex], TREND.ASCENDANT_STATIC) },
                                onDescDynamicClick = { onFreePart( sequences[selectedIndex], TREND.DESCENDANT_DYNAMIC ) },
                                onDescStaticClick = { onFreePart(sequences[selectedIndex],TREND.DESCENDANT_STATIC) }
                            )
                        }
                    }
//        if (snackbarVisibleState.value) {
//            Snackbar(
//                action = {
//                    Button(onClick = { snackbarVisibleState.value = false }) {
//                        Text("OK")
//                    }
//                },
//                modifier = Modifier.padding(8.dp)
//            ) { Text(text = "Please, select a Sequence!") }
//        }
                }
        }












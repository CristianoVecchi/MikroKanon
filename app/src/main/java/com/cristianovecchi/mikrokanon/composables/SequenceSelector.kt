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
                     onLoadingCounterpoint: (Int) -> Unit,
                     onTritoneSubstitution: (Int) -> Unit,
                     onRound: (ArrayList<Clip>) -> Unit,
                     onCadenza: (ArrayList<Clip>, List<Int>) -> Unit,
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
                     onResolutio: (ArrayList<Clip>, Pair<Set<Int>,String>) -> Unit,
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
    val listState = rememberLazyListState()
    val notesNames = language.noteNames
    val dimensions by dimensionsFlow.collectAsState(initial = model.dimensions.value!!)
    val weights = dimensions.selectorWeights
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
            val selected by model.selectedSequence.observeAsState(initial = -1)
            val sequences by model.sequences.observeAsState(emptyList())
            //val snackbarVisibleState = remember { mutableStateOf(false) }
            val dialogState = remember { mutableStateOf(false) }
            val buttonsDialogData = remember { mutableStateOf(ButtonsDialogData(model = model))}
            val cadenzaDialogData = remember { mutableStateOf(MultiNumberDialogData(model = model))}
            val resolutioDialogData = remember { mutableStateOf(MultiNumberDialogData(model = model))}
            val doublingDialogData = remember { mutableStateOf(MultiNumberDialogData(model = model))}
            val selectCounterpointDialogData = remember { mutableStateOf(ButtonsDialogData(model = model))}
            val chessDialogData = remember { mutableStateOf(ListDialogData())}
            val multiSequenceDialogData = remember { mutableStateOf(MultiNumberDialogData(model = model))}
            val privacyDialogData = remember { mutableStateOf(TextDialogData())}
            val buttonSize = dimensions.selectorButtonSize
            val zodiacFlags by model.zodiacFlags.asFlow().collectAsState(initial = Triple(false,false,false))
            val (zodiacPlanetsActive, zodiacSignsActive, zodiacEmojisActive) = zodiacFlags
            val sequencesToString = model.sequences.value!!.map { it.toStringAll(notesNames, zodiacSignsActive, zodiacEmojisActive) }
            val filledSlots by model.filledSlots.asFlow().collectAsState(initial = setOf())

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
                        onKP(sequences[selected], index, repeat)
                    }
                }
            )
            ButtonsDialog(buttonsDialogData, dimensions, language.OkButton, workingOnSequences = true,
                model = model, language = language, filledSlots = filledSlots )
            val onSelectComposition = { index: Int ->
                onSelect(index)
            }
            SelectCounterpointDialog( buttonsDialogData = selectCounterpointDialogData,
                dimensions = dimensions,model = model,language = language, filledSlots = filledSlots)
            CadenzaDialog(cadenzaDialogData, buttonsDialogData, dimensions, language.OkButton, model)
            ResolutioDialog(resolutioDialogData, buttonsDialogData, dimensions, language.OkButton, model)
            TransposeDialog(doublingDialogData, dimensions, getIntervalsForTranspose(language.intervalSet))
            SequenceScrollableColumn( listState = listState, colors = appColors,
                modifier = modifier3, fontSize = dimensions.selectorClipFontSize,
                notesNames = notesNames,
                zodiacSigns = zodiacSignsActive, emoji = zodiacEmojisActive,
                sequences = sequences,
                selected = selected, onSelect = onSelectComposition
            )
            MultiSequenceDialog(multiSequenceDialogData, buttonsDialogData, dimensions, model)
            ListDialog(listDialogData = chessDialogData, dimensions = dimensions, appColors = appColors, fillPrevious = true )
            PrivacyDialog(privacyDialogData, dimensions, language.OkButton)

            Column(modifier1) {
                Row(modifier = Modifier.fillMaxSize(),horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                    SequenceEditingButtons(
                        model = model, isActive = activeButtons.editing,
                        buttonSize = buttonSize, colors = appColors,
                        onDelete = { onDelete(selected) },
                        onEdit = { onAdd(sequences[selected], true) },
                        onAdd= { onAdd(ArrayList<Clip>(), false) }
                    )
                    MikroKanonsButtons(
                        model = model, isActive = activeButtons.mikrokanon,
                        buttonSize = buttonSize / 10 * 9, colors = appColors,
                        fontSize = dimensions.selectorMKbuttonFontSize,
                        onMK2Click = {
                            onMikroKanons2(sequences[selected])
                        },
                        onMK3Click = {
                            onMikroKanons3(sequences[selected])
                        },
                        onMK4Click = {
                            onMikroKanons4(sequences[selected])
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
                                onWave3 = { onWave(3, sequences[selected]) },
                                onWave4 = { onWave(4, sequences[selected]) },
                                onWave6 = { onWave(6, sequences[selected]) },
                                onTritoneSubstitution = { onTritoneSubstitution(selected) },
                                onRound = { onRound(sequences[selected]) },
                               // onCadenza = { onCadenza(sequences[selected]) },
                                onCadenza = {
                                    cadenzaDialogData.value = MultiNumberDialogData(true,
                                    language.selectCadenzaForm, model.cadenzaValues, 0, 16, model = model,
                                        dispatchCsv= { newValues ->
                                            model.cadenzaValues = newValues
                                            onCadenza( sequences[selected], newValues.extractIntsFromCsv() ) // CADENZA DIALOG OK BUTTON
                                }) },
                                onResolutio = {
                                     resolutioDialogData.value = MultiNumberDialogData(true,
                                         language.selectResolutioForm, model.cadenzaValues, 0, 16, model = model,
                                    dispatchResolutio = { resolutioData ->
                                        model.resolutioValues = resolutioData
                                        onResolutio(sequences[selected], resolutioData)
                                }) },
                                onDoubling = {
                                    doublingDialogData.value = MultiNumberDialogData(
                                        true, language.selectDoubling, value = "0|1",
                                        model = model) { transpositions ->
                                        onDoubling(sequences[selected], transpositions.extractIntPairsFromCsv())
                                    }
                                },
                                onScarlatti = { onScarlatti(sequences[selected]) },
                                onChess = {
                                    chessDialogData.value = ListDialogData(true,
                                        (1..sequences[selected].size * 2).map{ it.toString()}, 0,
                                        language.selectChessRange) {
                                            onChess(sequences[selected], it + 1)
                                         }
                                },
                                onOverlap = {
                                    selectCounterpointDialogData.value = ButtonsDialogData(true,
                                    language.selectToOverlap, model,
                                    onCounterpointSelected = { position ->
                                        onOverlap(sequences[selected],position, false)
                                        selectCounterpointDialogData.value = ButtonsDialogData(model = model) // Close Counterpoint Dialog
                                    })
                                },
                                onCrossover = {
                                    selectCounterpointDialogData.value = ButtonsDialogData(true,
                                        language.selectToCrossOver, model,
                                        onCounterpointSelected = { position ->
                                            onOverlap(sequences[selected],position, true)
                                            selectCounterpointDialogData.value = ButtonsDialogData(model = model) // Close Counterpoint Dialog
                                        })
                                },
                                onGlue = {
                                    selectCounterpointDialogData.value = ButtonsDialogData(true,
                                        language.selectToGlue, model,
                                        onCounterpointSelected = { position ->
                                            onGlue(sequences[selected],position)
                                            selectCounterpointDialogData.value = ButtonsDialogData(model = model) // Close Counterpoint Dialog
                                        })
                                },
                                onFlourish = { onFlourish(sequences[selected]) },
                                onEraseIntervals = { onEraseIntervals(sequences[selected]) },
                                onSingle = { onSingle(sequences[selected]) },
                                onDoppelgänger = { onDoppelgänger(sequences[selected])},
                                onPedal1 = { onPedal(1, sequences[selected]) },
                                onPedal3 = { onPedal(3, sequences[selected]) },
                                onPedal5 = { onPedal(5, sequences[selected]) },
                                onMK5reducted = { onMikroKanons5reducted(sequences[selected]) },
                                onMK6reducted = { onMikroKanons6reducted(sequences[selected]) },
                                onEWH = { nParts -> onEWH(sequences[selected], nParts)},
                                onMaze = {
                                         //onMaze(listOf(1,2,3,4))//,6,7,8,9,10, 11))
                                    val intSequences = listOf(sequences[selected].map{ it.abstractNote })
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
                        colors = appColors,
                        fontSize = dimensions.selectorFPbuttonFontSize, isActive = activeButtons.freeParts,
                        onAscDynamicClick = { onFreePart(sequences[selected], TREND.ASCENDANT_DYNAMIC ) },
                        onAscStaticClick = { onFreePart( sequences[selected], TREND.ASCENDANT_STATIC) },
                        onDescDynamicClick = { onFreePart( sequences[selected], TREND.DESCENDANT_DYNAMIC ) },
                        onDescStaticClick = { onFreePart(sequences[selected],TREND.DESCENDANT_STATIC) }
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
@Composable
fun SequenceScrollableColumn(
        listState: LazyListState,
        modifier: Modifier, fontSize:Int, notesNames: List<String>, zodiacSigns: Boolean, emoji: Boolean, colors: AppColors,
        sequences: List<ArrayList<Clip>>, selected:Int, onSelect: (Int) -> Unit
    )
{
val coroutineScope = rememberCoroutineScope()
if(sequences.isNotEmpty()){
    val padding = 8.dp
        LazyColumn(state = listState, modifier = modifier.padding(top = padding))

        {
            itemsIndexed(items = sequences) { index, sequence ->
                Row(modifier = Modifier.padding(padding)){
                    if (index == selected) {
                        SelectableCard(sequence.toStringAll(notesNames, zodiacSigns, emoji), fontSize, isSelected = true, colors = colors, onClick = {})
                    } else {
                        SelectableCard(text = sequence.toStringAll(notesNames, zodiacSigns, emoji), fontSize - (fontSize / 20), isSelected = false,colors = colors, onClick = {
                            onSelect(index)})
                    }
                }
            }

            coroutineScope.launch {
                delay(200)
                if(sequences.isNotEmpty() && (selected == -1 || selected >= sequences.size))
                    listState.animateScrollToItem(sequences.size -1)
            }
        }
    } else {
        Column(modifier){}
    }
}











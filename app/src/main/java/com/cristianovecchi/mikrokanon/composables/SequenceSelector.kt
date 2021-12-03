package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.*
import com.cristianovecchi.mikrokanon.AIMUSIC.Clip
import com.cristianovecchi.mikrokanon.AIMUSIC.TREND
import com.cristianovecchi.mikrokanon.composables.dialogs.ButtonsDialog
import com.cristianovecchi.mikrokanon.composables.dialogs.CadenzaDialog
import com.cristianovecchi.mikrokanon.composables.dialogs.SequencesDialog
import com.cristianovecchi.mikrokanon.db.UserOptionsData
import com.cristianovecchi.mikrokanon.locale.Lang
import com.cristianovecchi.mikrokanon.ui.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun SequenceSelector(model: AppViewModel,
                     userOptionsDataFlow: Flow<List<UserOptionsData>>,
                     onSelect: (Int) -> Unit = model::changeSequenceSelection,
                     onDelete: (Int) -> Unit = model::deleteSequence,
                     onAdd: (ArrayList<Clip>, Boolean) -> Unit,
                     onSort: (Int) -> Unit,
                     onWave: (Int, ArrayList<Clip>) -> Unit,
                     onLoadingCounterpoint: (Int) -> Unit,
                     onTritoneSubstitution: (Int) -> Unit,
                     onRound: (ArrayList<Clip>) -> Unit,
                     onCadenza: (ArrayList<Clip>, List<Int>) -> Unit,
                     onScarlatti: (ArrayList<Clip>) -> Unit,
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
                     onMikroKanons5reducted: (ArrayList<Clip>) -> Unit
                    )
{

    val activeButtons by model.activeButtons.asFlow().collectAsState(initial = ActiveButtons())
    //model.userOptionsData.observeAsState(initial = listOf()).value // to force recomposing when options change
    userOptionsDataFlow.collectAsState(initial = listOf()).value
    //if(userOptionsData.isNotEmpty()) model.setAppColors(userOptionsData[0].colors)
    var appColors = model.appColors
    var language = Lang.provideLanguage(model.getUserLangDef())
    val backgroundColor = appColors.sequencesListBackgroundColor
    val buttonsBackgroundColor = appColors.buttonsDisplayBackgroundColor
    val listState = rememberLazyListState()

    val notesNames = language.noteNames
        Column(modifier = Modifier
            .fillMaxHeight()
            .background(appColors.drawerBackgroundColor)) {
            val modifier3 = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .weight(10f)
            val modifier1 = Modifier
                .fillMaxSize()
                .background(buttonsBackgroundColor)
                .fillMaxWidth()
                .weight(6f)
            val selected by model.selectedSequence.observeAsState(initial = -1)
            val sequences by model.sequences.observeAsState(emptyList())
            //val snackbarVisibleState = remember { mutableStateOf(false) }
            val dialogState by lazy { mutableStateOf(false) }
            val buttonsDialogData by lazy { mutableStateOf(ButtonsDialogData(model = model))}
            val cadenzaDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
            val dimensions = model.dimensions
            val buttonSize = dimensions.selectorButtonSize
            val sequencesToString = model.sequences.value!!.map { it.toStringAll(notesNames, model.zodiacSignsActive, model.zodiacEmojisActive) }
            SequencesDialog(dialogState = dialogState, fontSize = dimensions.sequenceDialogFontSize,
                title = language.choose2ndSequence, repeatText = language.repeatSequence, okText = language.OKbutton,
                sequencesList = sequencesToString,
                onSubmitButtonClick = { index, repeat ->
                    dialogState.value = false
                    if (index != -1) {
                        onKP(sequences[selected], index, repeat)
                    }
                }
            )
            ButtonsDialog(buttonsDialogData, language.OKbutton, workingOnSequences = true, model = model)
            val onSelectComposition = { index: Int ->
                onSelect(index)
            }
            CadenzaDialog(cadenzaDialogData, language.OKbutton)
            SequenceScrollableColumn( listState = listState, colors = appColors,
                modifier = modifier3, notesNames = notesNames,
                zodiacSigns = model.zodiacSignsActive, emoji = model.zodiacEmojisActive,
                sequences = sequences,
                selected = selected, onSelect = onSelectComposition
            )

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
                        buttonSize = buttonSize, colors = appColors,
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
                            buttonsDialogData.value = ButtonsDialogData(true,
                                language.selectSpecialFunction,
                                model, isActiveWaves = activeButtons.waves,
                                isActivePedals = activeButtons.pedals,
                                onSort = { sortType -> onSort(sortType)},
                                onWave3 = { onWave(3, sequences[selected]) },
                                onWave4 = { onWave(4, sequences[selected]) },
                                onWave6 = { onWave(6, sequences[selected]) },
                                onTritoneSubstitution = { onTritoneSubstitution(selected) },
                                onRound = { onRound(sequences[selected]) },
                               // onCadenza = { onCadenza(sequences[selected]) },
                                onCadenza = {
                                    buttonsDialogData.value = ButtonsDialogData(model = model)// Close Buttons Dialog
                                    cadenzaDialogData.value = MultiNumberDialogData(true,
                                    language.selectCadenzaForm, model.cadenzaValues, 0, 16, model = model,
                                        ){ newValues ->
                                        model.cadenzaValues = newValues
                                        onCadenza(sequences[selected], newValues.extractIntsFromCsv()) // CADENZA DIALOG OK BUTTON
                                    }
                                },
                                onScarlatti = { onScarlatti(sequences[selected]) },
                                onFlourish = { onFlourish(sequences[selected]) },
                                onEraseIntervals = { onEraseIntervals(sequences[selected]) },
                                onSingle = { onSingle(sequences[selected]) },
                                onDoppelgänger = { onDoppelgänger(sequences[selected])},
                                onPedal1 = { onPedal(1, sequences[selected]) },
                                onPedal3 = { onPedal(3, sequences[selected]) },
                                onPedal5 = { onPedal(5, sequences[selected]) },
                                onMK5reducted = { onMikroKanons5reducted(sequences[selected]) },
                                onSavingCounterpoint = { position -> onLoadingCounterpoint(position)}
                            )
                            {

                            }
                        }
                    )

                    FreePartsButtons(
                        colors = appColors,
                        fontSize = dimensions.selectorFPbuttonFontSize, isActive = activeButtons.freeparts,
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
        modifier: Modifier, notesNames: List<String>, zodiacSigns: Boolean, emoji: Boolean, colors: AppColors,
        sequences: List<ArrayList<Clip>>, selected:Int, onSelect: (Int) -> Unit
    )
{
val coroutineScope = rememberCoroutineScope()
if(sequences.isNotEmpty()){
        LazyColumn(state = listState,modifier = modifier)

        {
            itemsIndexed(items = sequences) { index, sequence ->
                Row(modifier = Modifier.padding(8.dp)){
                    if (index == selected) {
                        SelectableCard(sequence.toStringAll(notesNames, zodiacSigns, emoji), 20, isSelected = true, colors = colors, onClick = {})
                    } else {
                        SelectableCard(text = sequence.toStringAll(notesNames, zodiacSigns, emoji), 18, isSelected = false,colors = colors, onClick = {
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











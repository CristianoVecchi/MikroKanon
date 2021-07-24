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
import com.cristianovecchi.mikrokanon.AIMUSIC.Clip
import com.cristianovecchi.mikrokanon.AIMUSIC.TREND
import com.cristianovecchi.mikrokanon.ActiveButtons
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.locale.Lang
import com.cristianovecchi.mikrokanon.toStringAll
import com.cristianovecchi.mikrokanon.ui.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SequenceSelector(model: AppViewModel,
                     onSelect: (Int) -> Unit = model::changeSequenceSelection,
                     onDelete: (Int) -> Unit = model::deleteSequence,
                     onAdd: (ArrayList<Clip>, Boolean) -> Unit,
                     onWave: (Int, ArrayList<Clip>) -> Unit,
                     onTritoneSubstitution: (Int) -> Unit,
                     onRound: (ArrayList<Clip>) -> Unit,
                     onKP: (ArrayList<Clip>, Int, Boolean) -> Unit,
                     onFreePart: (ArrayList<Clip>, TREND) -> Unit,
                     onMikroKanons2: (ArrayList<Clip>) -> Unit,
                     onMikroKanons3: (ArrayList<Clip>) -> Unit,
                     onMikroKanons4: (ArrayList<Clip>) -> Unit
                    )
{
    val backgroundColor = MaterialTheme.colors.sequencesListBackgroundColor
    val buttonsBackgroundColor = MaterialTheme.colors.buttonsDisplayBackgroundColor
    val activeButtons by model.activeButtons.asFlow().collectAsState(initial = ActiveButtons())
    model.userOptionsData.observeAsState(initial = listOf()).value // to force recomposing when options change
    var language = Lang.provideLanguage(model.getUserLangDef())

    val listState = rememberLazyListState()

    val notesNames = language.noteNames
    Column(modifier = Modifier
        .fillMaxHeight()
        .background(MaterialTheme.colors.drawerBackgroundColor)) {
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
        val dimensions = model.dimensions
        val buttonSize = dimensions.selectorButtonSize

        SequencesDialog(dialogState = dialogState, fontSize = dimensions.sequenceDialogFontSize,
            title = language.choose2ndSequence, repeatText = language.repeatSequence, okText = language.OKbutton,
            sequencesList = model.sequences.value!!.map { it.toStringAll(notesNames) },
            onSubmitButtonClick = { index, repeat ->
                dialogState.value = false
                if (index != -1) {
                    onKP(sequences[selected], index, repeat)
                }
            }
        )
        ButtonsDialog(buttonsDialogData, language.OKbutton, model)
        val onSelectComposition = { index: Int ->
            onSelect(index)
        }
        SequenceScrollableColumn( listState = listState,
            modifier = modifier3, notesNames = notesNames, sequences = sequences,
            selected = selected, onSelect = onSelectComposition
        )

        Column(modifier1) {
            Row(modifier = Modifier.fillMaxSize(),horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                SequenceEditingButtons(
                    model = model, isActive = activeButtons.editing,
                    buttonSize = buttonSize,
                    onDelete = { onDelete(selected) },
                    onEdit = { onAdd(sequences[selected], true) },
                    onAdd= { onAdd(ArrayList<Clip>(), false) }
                )
                MikroKanonsButtons(
                    model = model, isActive = activeButtons.mikrokanon,
                    buttonSize = buttonSize,
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
                FunctionButtons(model = model,
                    isActiveCounterpoint = activeButtons.counterpoint,
                    isActiveSpecialFunctions = activeButtons.specialFunctions,
                    buttonSize = buttonSize,
                    onAdd = { dialogState.value = true },
                    onSpecialFunctions = {
                        buttonsDialogData.value = ButtonsDialogData(true,
                            language.selectSpecialFunction,
                            model, isActiveWaves = activeButtons.waves,
                            onWave3 = { onWave(3, sequences[selected]) },
                            onWave4 = { onWave(4, sequences[selected]) },
                            onWave6 = { onWave(6, sequences[selected]) },
                            onTritoneSubstitution = { onTritoneSubstitution(selected) },
                            onRound = { onRound(sequences[selected]) }
                        )
                        {
                            buttonsDialogData.value = ButtonsDialogData(model = model)
                        }
                    }
                )

                FreePartsButtons(
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
        modifier: Modifier, notesNames: List<String>,
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
                        SelectableCard(sequence.toStringAll(notesNames), 20, isSelected = true, onClick = {})
                    } else {
                        SelectableCard(text = sequence.toStringAll(notesNames), 18, isSelected = false,onClick = {
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











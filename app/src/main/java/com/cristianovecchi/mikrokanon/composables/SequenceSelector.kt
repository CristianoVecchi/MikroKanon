package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.AIMUSIC.TREND
import com.cristianovecchi.mikrokanon.ActiveButtons
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.toStringAll
import com.cristianovecchi.mikrokanon.ui.*

@Composable
fun SequenceSelector(model: AppViewModel,
                     onSelect: (Int) -> Unit = model::changeSequenceSelection,
                     onDelete: (Int) -> Unit = model::deleteSequence,
                     onAdd: (ArrayList<Clip>, Boolean) -> Unit,
                     onKP: (ArrayList<Clip>, Int, Boolean) -> Unit,
                     onFreePart: (ArrayList<Clip>, TREND) -> Unit,
                     onMikroKanons: (ArrayList<Clip>) -> Unit,
                     onMikroKanons3: (ArrayList<Clip>) -> Unit,
                     onMikroKanons4: (ArrayList<Clip>) -> Unit
) {
    val backgroundColor = MaterialTheme.colors.sequencesListBackgroundColor
    val buttonsBackgroundColor = MaterialTheme.colors.buttonsDisplayBackgroundColor
    val activeButtons by model.activeButtons.asFlow().collectAsState(initial = ActiveButtons())
    val notesNames by model.notesNames.asFlow().collectAsState(initial = listOf("do","re","mi","fa","sol","la","si"))
    Column(modifier = Modifier.fillMaxHeight().background(MaterialTheme.colors.drawerBackgroundColor)) {
        val modifier3 = Modifier
            .fillMaxWidth().background(backgroundColor)
            .weight(3f)
        val modifier1 = Modifier
            .fillMaxSize().background(buttonsBackgroundColor)
            .fillMaxWidth()
            .weight(1f)
        val selected by model.selectedSequence.observeAsState(initial = -1)
        val sequences by model.sequences.observeAsState(emptyList())
        val snackbarVisibleState = remember { mutableStateOf(false) }
        val dialogState by lazy { mutableStateOf(false) }
        val buttonSize = 54.dp

        SequencesDialog(dialogState = dialogState,
            sequencesList = model.sequences.value!!.map { it.toStringAll(notesNames) },
            onSubmitButtonClick = { index, repeat ->
                dialogState.value = false
                if (index != -1) {
                    onKP(sequences[selected], index, repeat)
                }
            }
        )
        val onSelectComposition = { index: Int ->
            snackbarVisibleState.value = false
            onSelect(index)
        }
        SequenceScrollableColumn(
            modifier = modifier3, notesNames = notesNames, sequences = sequences, selected = selected, onSelect = onSelectComposition
        )

        Column(modifier1) {
            Row(modifier = Modifier.fillMaxSize(),horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                SequenceEditingButtons(
                    model = model, isActive = activeButtons.editing,
                    buttonSize = buttonSize,
                    onDelete = {if (selected in sequences.indices)
                        onDelete(selected)
                    else {
                        snackbarVisibleState.value = true
                    } },
                    onEdit = { if (selected in sequences.indices) {
                        onAdd(sequences[selected], true)
                    } else {
                        snackbarVisibleState.value = true
                    }},
                    onAdd= { onAdd(ArrayList<Clip>(), false)})

                MikroKanonsButtons(
                    model = model, isActive = activeButtons.mikrokanon,
                    buttonSize = buttonSize,
                    fontSize = 18,
                    onMK2Click = {
                        if (selected in sequences.indices) onMikroKanons(sequences[selected]) else {
                            snackbarVisibleState.value = true
                        }
                    },
                    onMK3Click = {
                        if (selected in sequences.indices) onMikroKanons3(sequences[selected]) else {
                            snackbarVisibleState.value = true
                        }
                    },
                    onMK4Click = {
                        if (selected in sequences.indices) onMikroKanons4(sequences[selected]) else {
                            snackbarVisibleState.value = true
                        }
                    }
                )
                // Add Counterpoint Button
                CustomButton(iconId = model.iconMap["counterpoint"]!!, isActive = activeButtons.counterpoint, buttonSize = buttonSize) {
                    if (selected in sequences.indices) {
                        dialogState.value = true
                    } else {
                        snackbarVisibleState.value = true
                    }
                }
                FreePartsButtons(
                    fontSize = 22, isActive = activeButtons.freeparts,
                    onAscDynamicClick = {
                        if (selected in sequences.indices) onFreePart(
                            sequences[selected],
                            TREND.ASCENDANT_DYNAMIC
                        ) else {
                            snackbarVisibleState.value = true
                        }
                    },
                    onAscStaticClick = {
                        if (selected in sequences.indices) onFreePart(
                            sequences[selected],
                            TREND.ASCENDANT_STATIC
                        ) else {
                            snackbarVisibleState.value = true
                        }
                    },
                    onDescDynamicClick = {
                        if (selected in sequences.indices) onFreePart(
                            sequences[selected],
                            TREND.DESCENDANT_DYNAMIC
                        ) else {
                            snackbarVisibleState.value = true
                        }
                    },
                    onDescStaticClick = {
                        if (selected in sequences.indices) onFreePart(
                            sequences[selected],
                            TREND.DESCENDANT_STATIC
                        ) else {
                            snackbarVisibleState.value = true
                        }
                    }
                )
            }
        }
        if (snackbarVisibleState.value) {
            Snackbar(
                action = {
                    Button(onClick = { snackbarVisibleState.value = false }) {
                        Text("OK")
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) { Text(text = "Please, select a Sequence!") }
        }

    }
}


@Composable
fun SequenceScrollableColumn(
    modifier: Modifier, notesNames: List<String>,
    sequences: List<ArrayList<Clip>>, selected:Int, onSelect: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    LazyColumn(state = listState,
        modifier = modifier
    ) {
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
    }
}








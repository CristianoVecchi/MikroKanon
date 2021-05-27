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
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.toStringAll

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
    Column(modifier = Modifier.fillMaxHeight()) {
        val modifier3 = Modifier
            .fillMaxWidth()
            .weight(3f)
        val modifier1 = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .weight(1f)
        val selected by model.selectedSequence.observeAsState(initial = -1)
        val sequences by model.sequences.observeAsState(emptyList())
        val snackbarVisibleState = remember { mutableStateOf(false) }
        val dialogState by lazy { mutableStateOf(false) }
        val buttonSize = 54.dp

        SequencesDialog(dialogState = dialogState,
            sequencesList = model.sequences.value!!.map { it.toStringAll() },
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
            modifier = modifier3, sequences = sequences, selected, onSelect = onSelectComposition
        )

        Column(modifier1) {
            Row(modifier = Modifier.fillMaxSize(),horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                SequenceEditingButtons(
                    model = model,
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
                    model = model,
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
                IconButton(modifier = Modifier
                    .padding(2.dp)
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .then(
                        Modifier
                            .size(buttonSize)
                            .border(2.dp, Color.Black)
                    ),
                    onClick = { if (selected in sequences.indices) {
                        dialogState.value = true
                    } else {
                        snackbarVisibleState.value = true
                    }})
                {
                    Icon(
                        painter = painterResource(id = model.iconMap["counterpoint"]!!),
                        contentDescription = null, // decorative element
                        tint = Color.Blue
                    )
                }


                FreePartsButtons(
                    fontSize = 22,
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
    modifier: Modifier,
    sequences: List<ArrayList<Clip>>, selected:Int, onSelect: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    LazyColumn(state = listState,
        modifier = modifier
    ) {
        itemsIndexed(items = sequences) { index, sequence ->
            Row(modifier = Modifier.padding(8.dp)){
                if (index == selected) {
                    SelectableCard(text = sequence.toStringAll(), 20, isSelected = true, onClick = {})
                } else {
                    SelectableCard(text = sequence.toStringAll(), 18, isSelected = false,onClick = {
                        onSelect(index)})
                }
            }

        }
    }
}








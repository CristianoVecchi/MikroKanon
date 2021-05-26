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
        val userOptionsData by model.userOptionsData.asFlow().collectAsState(initial = listOf())
        //val userOptions = if(userOptionsData.isEmpty()) UserOptionsData(0,"0","90","0","0") else userOptionsData[0]
        val snackbarVisibleState = remember { mutableStateOf(false) }
        val dialogState by lazy { mutableStateOf(false) }
        //val selectedDialogSequence by lazy { mutableStateOf(-1) }
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
        //Text("number of sequences: ${sequences.size}")
        val onSelectComposition = { index: Int ->
            snackbarVisibleState.value = false
            onSelect(index)
        }
        SequenceScrollableColumn(
            modifier = modifier3, sequences = sequences, selected, onSelect = onSelectComposition
        )

        //  DEL | EDIT | ADD | KP
        // FPad | FPdd | FPas | FPds
        // MK | MK3 | MK4 | RP
        Column(modifier1) {
            Row() {

                Column(horizontalAlignment = Alignment.Start) {
                    // DEL
                    IconButton(modifier = Modifier.padding(2.dp).
                    background(Color.White, RoundedCornerShape(4.dp))
                        .then(Modifier.size(buttonSize).border(2.dp, Color.Black)),
                        onClick = {
                            if (selected in sequences.indices)
                                onDelete(selected)
                            else {
                                snackbarVisibleState.value = true
                            }
                        })
                    {
                        Icon(
                            painter = painterResource(id = model.iconMap["delete"]!!),
                            contentDescription = null, // decorative element
                            tint =  Color.Blue )
                    }
                    //Edit Button
                    IconButton(modifier = Modifier.padding(2.dp).
                    background(Color.White, RoundedCornerShape(4.dp))
                        .then(Modifier.size(buttonSize).border(2.dp, Color.Black)),
                        onClick = {
                            if (selected in sequences.indices) {
                                onAdd(sequences[selected], true)
                            } else {
                                snackbarVisibleState.value = true
                            }
                        })
                    {
                        Icon(
                            painter = painterResource(id = model.iconMap["edit"]!!),
                            contentDescription = null, // decorative element
                            tint =  Color.Blue )
                    }


                    //ADD Button
                    IconButton(modifier = Modifier.padding(2.dp).
                    background(Color.White, RoundedCornerShape(4.dp))
                        .then(Modifier.size(buttonSize).border(2.dp, Color.Black)),
                        onClick = { onAdd(ArrayList<Clip>(), false) })
                    {
                        Icon(
                            painter = painterResource(id = model.iconMap["add"]!!),
                            contentDescription = null, // decorative element
                            tint =  Color.Blue )
                    }

                }
                Column(horizontalAlignment = Alignment.Start) {
                    //MK Button
                    Button(modifier = Modifier.padding(2.dp),
                        onClick = {
                            if (selected in sequences.indices) onMikroKanons(sequences[selected]) else {
                                snackbarVisibleState.value = true
                            }
                        })
                    {
                        Text(
                            text = "MK2",
                            style = TextStyle(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    //MK3 Button
                    Button(modifier = Modifier.padding(2.dp),
                        onClick = {
                            if (selected in sequences.indices) onMikroKanons3(sequences[selected]) else {
                                snackbarVisibleState.value = true
                            }
                        })
                    {
                        Text(
                            text = "MK3",
                            style = TextStyle(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    //MK4 Button
                    Button(modifier = Modifier.padding(2.dp),
                        onClick = {
                            if (selected in sequences.indices) onMikroKanons4(sequences[selected]) else {
                                snackbarVisibleState.value = true
                            }
                        })
                    {
                        Text(
                            text = "MK4",
                            style = TextStyle(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }


                //KP Button
                Button(modifier = Modifier.padding(2.dp),
                    onClick = {
                        if (selected in sequences.indices) {
                            dialogState.value = true
                        } else {
                            snackbarVisibleState.value = true
                        }
                    })
                {
                    Text(
                        text = "KP",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
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
                    Button(onClick = { snackbarVisibleState.value = false}) {
                        Text("OK")
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) { Text(text = "Please, select a Sequence!") }
        }
    }
}

@Composable
fun FreePartsButtons(
    fontSize: Int,
    onAscDynamicClick: () -> Unit, onAscStaticClick: () -> Unit,
    onDescDynamicClick: () -> Unit, onDescStaticClick: () -> Unit
) {
    Row() {
        Column() {
            //FPad Button
            Button(modifier = Modifier.padding(2.dp),
                onClick = { onAscDynamicClick() })
            {
                Text(
                    text = "∼\u279A",
                    style = TextStyle(
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            //FPdd Button
            Button(modifier = Modifier.padding(2.dp),
                onClick = { onDescDynamicClick() })
            {
                Text(
                    text = "∼\u2798",
                    style = TextStyle(
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

        }
        Column() {
            // \u2B08
            //FPas Button
            Button(modifier = Modifier.padding(2.dp),
                onClick = { onAscStaticClick() })
            {
                Text(
                    text = "-➚",
                    style = TextStyle(
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            // \u2B0A
            //FPds Button
            Button(modifier = Modifier.padding(2.dp),
                onClick = { onDescStaticClick() })
            {
                Text(
                    text = "-➘",
                    style = TextStyle(
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
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








package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristianovecchi.mikrokanon.AIMUSIC.EnsembleType
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
        val userOptions by model.userOptions.observeAsState(HashMap<String,String>())
        val snackbarVisibleState = remember { mutableStateOf(false) }
        val dialogState by lazy { mutableStateOf(false) }
        val selectedDialogSequence by lazy { mutableStateOf(-1) }

        SequencesDialog(dialogState = dialogState, sequencesList = model.sequences.value!!.map{ it.toStringAll()},
            onSubmitButtonClick = { index, repeat ->
                dialogState.value = false
                if(index != -1) {
                    onKP(sequences[selected], index, repeat)
                }
            }
        )

        Text("number of sequences: ${sequences.size}")
        val ensType: String = userOptions["ensemble_type"] ?: "0"
        Text("ensemble type selected: ${EnsembleType.values()[Integer.parseInt(ensType)]}")
        SequenceScrollableColumn(
            modifier = modifier3, sequences = sequences, selected, onSelect
        )
        //  DEL | EDIT | ADD | KP
        // FPad | FPdd | FPas | FPds
        // MK | MK3 | MK4 | RP
        Column(modifier1) {
            Row(verticalAlignment = Alignment.CenterVertically){
                // DEL
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { if(selected in sequences.indices)  onDelete(selected)   else {snackbarVisibleState.value = true} } )
                {
                    Text(text = "DEL",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }

                //Edit Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { if(selected in sequences.indices)  {onAdd(sequences[selected], true)}  else {snackbarVisibleState.value = true} } )
                {
                    Text(text = "EDIT",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }

                //ADD Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = {onAdd(ArrayList<Clip>(), false)})
                {
                    Text(text = "ADD",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }

                //KP Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { if(selected in sequences.indices) {dialogState.value = true} else {snackbarVisibleState.value = true} } )
                {
                    Text(text = "KP",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically){
                //FPad Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { if(selected in sequences.indices) onFreePart(sequences[selected], TREND.ASCENDANT_DYNAMIC)  else {snackbarVisibleState.value = true} } )
                {
                    Text(text = "FPad",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }
                //FPdd Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { if(selected in sequences.indices) onFreePart(sequences[selected], TREND.DESCENDANT_DYNAMIC)  else {snackbarVisibleState.value = true} } )
                {
                    Text(text = "FPdd",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }
                //FPas Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { if(selected in sequences.indices) onFreePart(sequences[selected], TREND.ASCENDANT_STATIC)  else {snackbarVisibleState.value = true} } )
                {
                    Text(text = "FPas",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }
                //FPds Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { if(selected in sequences.indices) onFreePart(sequences[selected], TREND.DESCENDANT_STATIC)  else {snackbarVisibleState.value = true} } )
                {
                    Text(text = "FPds",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically){
                //MK Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { if(selected in sequences.indices) onMikroKanons(sequences[selected])  else {snackbarVisibleState.value = true} } )
                {
                    Text(text = "MK",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }

                //MK3 Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { if(selected in sequences.indices) onMikroKanons3(sequences[selected])  else {snackbarVisibleState.value = true} } )
                {
                    Text(text = "MK3",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }

                //MK4 Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { if(selected in sequences.indices) onMikroKanons4(sequences[selected])  else {snackbarVisibleState.value = true} } )
                {
                    Text(text = "MK4",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }

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
fun SequenceScrollableColumn(
    modifier: Modifier,
    sequences: List<ArrayList<Clip>>, selected:Int, onSelect: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    LazyColumn(state = listState,
        modifier = modifier
    ) {
        itemsIndexed(items = sequences) { index, sequence ->
            if (index == selected) {
                SelectedCard(text = sequence.toStringAll(), 20,onClick = {})
            } else {
                UnSelectedCard(text = sequence.toStringAll(), 18,onClick = {onSelect(index)})
                }
        }
    }
}








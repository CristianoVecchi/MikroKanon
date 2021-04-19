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
                     onMikrokanons: (ArrayList<Clip>) -> Unit,
                     onMikrokanons3: (ArrayList<Clip>) -> Unit,
                     onMikrokanons4: (ArrayList<Clip>) -> Unit
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

        Text("${sequences.size}")
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
                    onClick = { if(selected in sequences.indices)  onDelete(selected)   else {} } )
                {
                    Text(text = "DEL",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }

                //Edit Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { if(selected in sequences.indices)  {onAdd(sequences[selected], true)}  else {} } )
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
                    onClick = { if(selected in sequences.indices) {dialogState.value = true} else {} } )
                {
                    Text(text = "KP",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically){
                //FPad Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { if(selected in sequences.indices) onFreePart(sequences[selected], TREND.ASCENDANT_DYNAMIC)  else {} } )
                {
                    Text(text = "FPad",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }
                //FPdd Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { if(selected in sequences.indices) onFreePart(sequences[selected], TREND.DESCENDANT_DYNAMIC)  else {} } )
                {
                    Text(text = "FPdd",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }
                //FPas Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { if(selected in sequences.indices) onFreePart(sequences[selected], TREND.ASCENDANT_STATIC)  else {} } )
                {
                    Text(text = "FPas",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }
                //FPds Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { if(selected in sequences.indices) onFreePart(sequences[selected], TREND.DESCENDANT_STATIC)  else {} } )
                {
                    Text(text = "FPds",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically){
                //MK Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { if(selected in sequences.indices) onMikrokanons(sequences[selected])  else {} } )
                {
                    Text(text = "MK",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }

                //MK3 Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { if(selected in sequences.indices) onMikrokanons3(sequences[selected])  else {} } )
                {
                    Text(text = "MK3",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }

                //MK4 Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { if(selected in sequences.indices) onMikrokanons4(sequences[selected])  else {} } )
                {
                    Text(text = "MK4",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }

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
            if (index == selected) {
                SelectedCard(text = sequence.toStringAll(), 20,onClick = {})
            } else {
                UnSelectedCard(text = sequence.toStringAll(), 18,onClick = {onSelect(index)})
                }
        }
    }
}








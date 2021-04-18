package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristianovecchi.mikrokanon.AIMUSIC.Counterpoint
import com.cristianovecchi.mikrokanon.AIMUSIC.TREND
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.toStringAll

@Composable
fun ResultDisplay(model: AppViewModel,
                  onKP: (Int) -> Unit = {},
                  onClick: (Counterpoint) -> Unit = {},
                  onBack: () -> Unit = {},
                  onFreePart: (TREND) -> Unit = {},
                  onExpand: () -> Unit = {},
                  dispatchIntervals: (List<Int>) -> Unit = {}) {

    val counterpoints by model.counterpoints.observeAsState(emptyList())
    val backgroundColor = Color(0.5f,0.5f,1.0f,1.0f)
    Column(modifier = Modifier.fillMaxHeight().background(backgroundColor)) {

        val modifier4 = Modifier.fillMaxWidth().weight(4f)
        val modifier1 = Modifier.fillMaxSize().weight(1f)
        val listState = rememberLazyListState()
        Text(text = "N. of Results found: ${counterpoints.size} STACK SIZE: ${model.counterpointStack.size}")

        if(counterpoints.isEmpty()) Text(text = "ELABORATING...")

        LazyColumn( modifier = modifier4, state = listState,)
         {
            items(counterpoints) { counterpoint ->
                NoteTable(model,counterpoint , 16, onClick = {onClick(counterpoint)})
            }
        }
        Column(modifier1){

            val dialogState by lazy { mutableStateOf(false) }
            val selectedDialogSequence by lazy { mutableStateOf(-1) }

            SequencesDialog(dialogState = dialogState, sequencesList = model.sequences.value!!.map{ it.toStringAll()},
                onSubmitButtonClick = { index ->
                    dialogState.value = false
                    if(index != -1) { onKP(index) }
                } )

            Row(verticalAlignment = Alignment.CenterVertically){
                //FPad Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { onFreePart(TREND.ASCENDANT_DYNAMIC) } )
                {
                    Text(text = "FPad",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }
                //FPdd Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = {  onFreePart(TREND.DESCENDANT_DYNAMIC) } )
                {
                    Text(text = "FPdd",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }
                //FPas Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { onFreePart(TREND.ASCENDANT_STATIC) } )
                {
                    Text(text = "FPas",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }
                //FPds Button
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { onFreePart(TREND.DESCENDANT_STATIC)} )
                {
                    Text(text = "FPds",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {

                // BACK BUTTON
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { onBack() } )
                {
                    Text(text = "Back",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }

                // EX BUTTON
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { onExpand() } )
                {
                    Text(text = "EX",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }

                // KP BUTTON
                Button(modifier= Modifier.padding(2.dp),
                    onClick = { dialogState.value = true } )
                {
                    Text(text = "KP",
                        style = TextStyle(fontSize = 22.sp,
                            fontWeight = FontWeight.Bold) )
                }

            }
        }

        Row(modifier1){
            IntervalSetSelector(
                    model, fontSize = 10,
                    dispatchIntervals = { newIntervals ->
                        dispatchIntervals(newIntervals)
                    }
            )
        }
    }
}

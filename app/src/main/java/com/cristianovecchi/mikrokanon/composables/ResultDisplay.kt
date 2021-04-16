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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristianovecchi.mikrokanon.AIMUSIC.Counterpoint
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.toStringAll

@Composable
fun ResultDisplay(model: AppViewModel,
                  onKP: (Int) -> Unit = {},
                  onClick: (Counterpoint) -> Unit = {},
                  onBack: () -> Unit = {},
                  dispatchIntervals: (List<Int>) -> Unit = {})
                   {

    val counterpoints by model.counterpoints.observeAsState(emptyList())
    val backgroundColor = Color(0.5f,0.5f,1.0f,1.0f)
    Column(modifier = Modifier.fillMaxHeight().background(backgroundColor)) {
        val modifier5 = Modifier
            .fillMaxWidth()
            .weight(5f)
        val modifier1 = Modifier
            .fillMaxSize()
            .weight(1f)
        val listState = rememberLazyListState()
        Text(text = "N. of Results found: ${counterpoints.size}")
        if(counterpoints.isEmpty()) Text(text = "ELABORATING...")
        LazyColumn(
            modifier = modifier5, state = listState,
        ) {
            items(counterpoints) { counterpoint ->
                NoteTable(model,counterpoint , 16,
                    onClick = {onClick(counterpoint)})
            }

        }
        Row(modifier1){
            val dialogState by lazy { mutableStateOf(false) }
            val selectedDialogSequence by lazy { mutableStateOf(-1) }
            SequencesDialog(dialogState = dialogState, sequencesList = model.sequences.value!!.map{ it.toStringAll()},
                onSubmitButtonClick = { index ->
                    dialogState.value = false
                    if(index != -1) {
                        onKP(index)
                    }
                }
            )
            // BACK BUTTON
            Button(modifier= Modifier.padding(2.dp),
                onClick = {
                    onBack()
                })
            {
                Text(text = "Back",
                    style = TextStyle(fontSize = 22.sp,
                        fontWeight = FontWeight.Bold) )
            }
            // KP BUTTON
            Button(modifier= Modifier.padding(2.dp),
                onClick = {
                    dialogState.value = true
                })
            {
                Text(text = "KP",
                    style = TextStyle(fontSize = 22.sp,
                        fontWeight = FontWeight.Bold) )
            }
        }
        Row(modifier1){
            IntervalSetSelector(
                    model, fontSize = 14,
                    dispatchIntervals = { newIntervals ->
                        dispatchIntervals(newIntervals)
                    }
            )
        }
    }
}

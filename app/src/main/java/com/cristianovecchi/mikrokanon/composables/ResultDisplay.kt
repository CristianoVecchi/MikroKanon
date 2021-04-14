package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.cristianovecchi.mikrokanon.AIMUSIC.Counterpoint
import com.cristianovecchi.mikrokanon.AppViewModel

@Composable
fun ResultDisplay(model: AppViewModel,
                  onKP: (Int) -> Unit = {},
                  onClick: (Counterpoint) -> Unit = {},
                  dispatchIntervals: (List<Int>) -> Unit = {})
                   {

    val counterpoints by model.counterpoints.observeAsState(emptyList())

    Column(modifier = Modifier.fillMaxHeight()) {
        val modifier4 = Modifier
            .fillMaxWidth()
            .weight(4f)
        val modifier1 = Modifier
            .fillMaxSize()
            .weight(1f)
        val listState = rememberLazyListState()
        Text(text = "N. of Results found: ${counterpoints.size}")
        if(counterpoints.isEmpty()) Text(text = "ELABORATING...")
        LazyColumn(
            modifier = modifier4, state = listState,
        ) {
            items(counterpoints) { counterpoint ->
                NoteTable(model,counterpoint ,
                    onClick = {onClick(counterpoint)})
            }

        }
        Row(modifier1){
            IntervalSetSelector(
                    model,

                    onKP = { index ->
                        onKP(index)
                    },
                    dispatchIntervals = { newIntervals ->
                        dispatchIntervals(newIntervals)
                    }
            )
        }
    }
}

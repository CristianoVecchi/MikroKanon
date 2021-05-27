package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
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
import com.cristianovecchi.mikrokanon.AIMUSIC.Counterpoint
import com.cristianovecchi.mikrokanon.AIMUSIC.TREND
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.toStringAll

@Composable
fun ResultDisplay(model: AppViewModel,
                  onKP: (Int, Boolean) -> Unit = { _, _ -> },
                  onClick: (Counterpoint) -> Unit = {},
                  onBack: () -> Unit = {},
                  onFreePart: (TREND) -> Unit = {},
                  onExpand: () -> Unit = {},
                  onPlay: () -> Unit = {},
                  dispatchIntervals: (List<Int>) -> Unit = {}) {

    val counterpoints by model.counterpoints.asFlow().collectAsState(initial = emptyList())
    val backgroundColor = Color(0.5f,0.5f,1.0f,1.0f)
    Column(modifier = Modifier
        .fillMaxHeight()
        .background(backgroundColor)) {

        val modifier4 = Modifier
            .fillMaxWidth()
            .weight(4f)
        val modifier1 = Modifier
            .fillMaxSize()
            .weight(1f)
        val listState = rememberLazyListState()

        val buttonSize = 60.dp
        //Text(text = "N. of Results found: ${counterpoints.size} STACK SIZE: ${model.counterpointStack.size}")

        if(counterpoints.isEmpty()) Text(text = "ELABORATING...")

        LazyColumn( modifier = modifier4, state = listState,)
         {
            items(counterpoints) { counterpoint ->
                val parts = toClips(counterpoint, NoteNamesIt.values().map { value -> value.toString() })
                val maxSize = parts.maxOf{ it.size}
                val clips: MutableList<MutableList<Clip>> = mutableListOf()
                for( i in 0 until maxSize){
                    val col: MutableList<Clip> = mutableListOf()
                    for(j in parts.indices) {
                        val clip = if (i < parts[j].size) parts[j][i] else Clip()
                        col.add(clip)
                    }
                    clips.add(col)
                }
                NoteTable(model,counterpoint , clips,16, onClick = {onClick(counterpoint)})
            }
        }
        Column(modifier1, verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally){

            val dialogState by lazy { mutableStateOf(false) }
            val selectedDialogSequence by lazy { mutableStateOf(-1) }

            SequencesDialog(dialogState = dialogState, sequencesList = model.sequences.value!!.map{ it.toStringAll()},
                onSubmitButtonClick = { index, repeat ->
                    dialogState.value = false
                    if(index != -1) { onKP(index, repeat) }
                } )


            Row(verticalAlignment = Alignment.CenterVertically) {

                // UNDO BUTTON
                IconButton(modifier = Modifier
                    .padding(2.dp)
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .then(
                        Modifier
                            .size(buttonSize)
                            .border(2.dp, Color.Black)
                    ),
                onClick = { onBack() } )
                {
                    Icon(
                        painter = painterResource(id = model.iconMap["undo"]!!),
                        contentDescription = null, // decorative element
                        tint =  Color.Blue )
                }

                // EX BUTTON
                IconButton(modifier = Modifier
                    .padding(2.dp)
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .then(
                        Modifier
                            .size(buttonSize)
                            .border(2.dp, Color.Black)
                    ),
                    onClick = { onExpand() } )
                {
                    Icon(
                        painter = painterResource(id = model.iconMap["expand"]!!),
                        contentDescription = null, // decorative element
                        tint =  Color.Blue )
                }

                // Add Counterpoint Button
                IconButton(modifier = Modifier
                    .padding(2.dp)
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .then(
                        Modifier
                            .size(buttonSize)
                            .border(2.dp, Color.Black)
                    ),
                    onClick = {
                        dialogState.value = true
                   })
                {
                    Icon(
                        painter = painterResource(id = model.iconMap["counterpoint"]!!),
                        contentDescription = null, // decorative element
                        tint = Color.Blue
                    )
                }

                FreePartsButtons(
                    fontSize = 22,
                    onAscDynamicClick = { onFreePart(TREND.ASCENDANT_DYNAMIC) },
                    onAscStaticClick = { onFreePart(TREND.ASCENDANT_STATIC) },
                    onDescDynamicClick = {  onFreePart(TREND.DESCENDANT_DYNAMIC) },
                    onDescStaticClick =  { onFreePart(TREND.DESCENDANT_STATIC)}
                )
                // PLAY BUTTON
                IconButton(modifier = Modifier
                    .padding(2.dp)
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .then(
                        Modifier
                            .size(buttonSize)
                            .border(2.dp, Color.Black)
                    ),
                    onClick = { onPlay() } )
                {
                    Icon(
                        painter = painterResource(id = model.iconMap["play"]!!),
                        contentDescription = null, // decorative element
                        tint =  Color.Blue )
                }
            }
        }
        Column(modifier1, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
            IntervalSetSelector(
                    model, fontSize = 10,
                    dispatchIntervals = { newIntervals ->
                        dispatchIntervals(newIntervals)
                    }
            )
        }
    }
}

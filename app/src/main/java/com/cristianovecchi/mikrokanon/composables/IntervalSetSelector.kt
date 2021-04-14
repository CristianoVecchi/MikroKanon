package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.toStringAll

@Composable
fun IntervalSetSelector(model: AppViewModel,
                        onKP: (Int) -> Unit, dispatchIntervals: (List<Int>) -> Unit) {
    val intervals by model.intervalSet.observeAsState(emptyList())
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

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
        Button(modifier= Modifier.padding(2.dp),
            onClick = {
                dialogState.value = true
            })
        {
            Text(text = "KP",
                style = TextStyle(fontSize = 22.sp,
                    fontWeight = FontWeight.Bold) )
        }
        if(intervals.containsAll(listOf(1,11))){
            SelectedCard(text = "2m\n7M", onClick = { model.removeIntervals(listOf(1,11)); dispatchIntervals(intervals) })
        } else {
            UnSelectedCard(text = "2m\n7M", onClick = {model.addIntervals(listOf(1,11)); dispatchIntervals(intervals)})
        }
        if(intervals.containsAll(listOf(2,10))){
            SelectedCard(text = "2M\n7m", onClick = {model.removeIntervals(listOf(2,10)); dispatchIntervals(intervals)})
        } else {
            UnSelectedCard(text = "2M\n7m", onClick = {model.addIntervals(listOf(2,10)); dispatchIntervals(intervals)})
        }
        if(intervals.containsAll(listOf(3,9))){
            SelectedCard(text = "3m\n6M", onClick = {model.removeIntervals(listOf(3,9)); dispatchIntervals(intervals)})
        } else {
            UnSelectedCard(text = "3m\n6M", onClick = {model.addIntervals(listOf(3,9)); dispatchIntervals(intervals)})
        }
        if(intervals.containsAll(listOf(4,8))){
            SelectedCard(text = "3M\n6m", onClick = {model.removeIntervals(listOf(4,8)); dispatchIntervals(intervals)})
        } else {
            UnSelectedCard(text = "3M\n6m", onClick = {model.addIntervals(listOf(4,8)); dispatchIntervals(intervals)})
        }
        if(intervals.containsAll(listOf(5,7))){
            SelectedCard(text = "4\n5", onClick = {model.removeIntervals(listOf(5,7)); dispatchIntervals(intervals)})
        } else {
            UnSelectedCard(text = "4\n5", onClick = {model.addIntervals(listOf(5,7)); dispatchIntervals(intervals)})
        }
        if(intervals.contains(6)){
            SelectedCard(text = "4a\n5d", onClick = {model.removeIntervals(listOf(6)); dispatchIntervals(intervals)})
        } else {
            UnSelectedCard(text = "4a\n5d", onClick = {model.addIntervals(listOf(6)); dispatchIntervals(intervals)})
        }
        if(intervals.contains(0)){
            SelectedCard(text = "un\noct", onClick = {model.removeIntervals(listOf(0)); dispatchIntervals(intervals)})
        } else {
            UnSelectedCard(text = "un\noct", onClick = {model.addIntervals(listOf(0)); dispatchIntervals(intervals)})
        }
    }
}

@Composable
fun SelectedCard(text: String, onClick: () -> Unit){
    val selectionBackColor = Color.White
    val selectionTextColor = Color.Red
    val selectionBorderColor = Color.Black
    Card(modifier = Modifier
        .background(Color.White)
        .clip(RoundedCornerShape(6.dp))
        .padding(6.dp)
        .clickable { onClick() },
                            backgroundColor = selectionBackColor,
                            contentColor = selectionTextColor,
                            border = BorderStroke(2.dp, selectionBorderColor )) {

        Text(text = text, modifier = Modifier.padding(18.dp),
                style = TextStyle(fontSize = 18.sp,
                        fontWeight = FontWeight.Bold))
    }
}

@Composable
fun UnSelectedCard(text: String, onClick: () -> Unit){
    val unselectionBackColor = Color.LightGray
    val unselectionTextColor = Color.Blue
    val unselectionBorderColor = Color.DarkGray
    Card(modifier = Modifier
            .background(Color.White)
            .clip(RoundedCornerShape(6.dp))
            .padding(6.dp)
            .clickable { onClick() },
            backgroundColor = unselectionBackColor,
            contentColor = unselectionTextColor,
            border = BorderStroke(2.dp, unselectionBorderColor )) {

        Text(text = text, modifier = Modifier.padding(18.dp),
                style = TextStyle(fontSize = 16.sp,
                        fontWeight = FontWeight.Bold))
    }
}
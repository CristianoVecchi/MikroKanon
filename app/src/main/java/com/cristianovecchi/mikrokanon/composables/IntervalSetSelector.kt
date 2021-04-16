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
fun IntervalSetSelector(model: AppViewModel, fontSize: Int,
                        dispatchIntervals: (List<Int>) -> Unit) {
    val intervals by model.intervalSet.observeAsState(emptyList())
    val backgroundColor = Color(0.5f,0.5f,1.0f,1.0f)
    Row(modifier = Modifier.fillMaxWidth().background(backgroundColor), verticalAlignment = Alignment.CenterVertically) {


        if(intervals.containsAll(listOf(1,11))){
            SelectedCard(text = "2m\n7M", fontSize + 2, onClick = { model.removeIntervals(listOf(1,11)); dispatchIntervals(intervals) })
        } else {
            UnSelectedCard(text = "2m\n7M",fontSize,  onClick = {model.addIntervals(listOf(1,11)); dispatchIntervals(intervals)})
        }
        if(intervals.containsAll(listOf(2,10))){
            SelectedCard(text = "2M\n7m",fontSize + 2,  onClick = {model.removeIntervals(listOf(2,10)); dispatchIntervals(intervals)})
        } else {
            UnSelectedCard(text = "2M\n7m", fontSize, onClick = {model.addIntervals(listOf(2,10)); dispatchIntervals(intervals)})
        }
        if(intervals.containsAll(listOf(3,9))){
            SelectedCard(text = "3m\n6M", fontSize + 2, onClick = {model.removeIntervals(listOf(3,9)); dispatchIntervals(intervals)})
        } else {
            UnSelectedCard(text = "3m\n6M", fontSize, onClick = {model.addIntervals(listOf(3,9)); dispatchIntervals(intervals)})
        }
        if(intervals.containsAll(listOf(4,8))){
            SelectedCard(text = "3M\n6m", fontSize + 2, onClick = {model.removeIntervals(listOf(4,8)); dispatchIntervals(intervals)})
        } else {
            UnSelectedCard(text = "3M\n6m", fontSize, onClick = {model.addIntervals(listOf(4,8)); dispatchIntervals(intervals)})
        }
        if(intervals.containsAll(listOf(5,7))){
            SelectedCard(text = "4\n5", fontSize + 2, onClick = {model.removeIntervals(listOf(5,7)); dispatchIntervals(intervals)})
        } else {
            UnSelectedCard(text = "4\n5", fontSize, onClick = {model.addIntervals(listOf(5,7)); dispatchIntervals(intervals)})
        }
        if(intervals.contains(6)){
            SelectedCard(text = "4a\n5d", fontSize + 2, onClick = {model.removeIntervals(listOf(6)); dispatchIntervals(intervals)})
        } else {
            UnSelectedCard(text = "4a\n5d", fontSize, onClick = {model.addIntervals(listOf(6)); dispatchIntervals(intervals)})
        }
        if(intervals.contains(0)){
            SelectedCard(text = "1\n8", fontSize + 2, onClick = {model.removeIntervals(listOf(0)); dispatchIntervals(intervals)})
        } else {
            UnSelectedCard(text = "1\n8", fontSize, onClick = {model.addIntervals(listOf(0)); dispatchIntervals(intervals)})
        }
    }
}


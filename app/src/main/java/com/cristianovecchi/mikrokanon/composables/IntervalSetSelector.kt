package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.cristianovecchi.mikrokanon.AppViewModel

@Composable
fun IntervalSetSelector(model: AppViewModel, fontSize: Int,
                        dispatchIntervals: (List<Int>) -> Unit) {
    val intervals by model.intervalSet.observeAsState(emptyList())
    val backgroundColor = Color(0.5f,0.5f,1.0f,1.0f)
    Row(modifier = Modifier.fillMaxWidth().background(backgroundColor), verticalAlignment = Alignment.CenterVertically) {


        if(intervals.containsAll(listOf(1,11))){
            SelectableCard(text = "2m\n7M", fontSize + 2, isSelected = true, onClick = { model.removeIntervalsAndRefresh(listOf(1,11)) })
        } else {
            SelectableCard(text = "2m\n7M",fontSize, isSelected = false, onClick = {model.addIntervalsAndRefresh(listOf(1,11))})
        }
        if(intervals.containsAll(listOf(2,10))){
            SelectableCard(text = "2M\n7m",fontSize + 2, isSelected = true, onClick = {model.removeIntervalsAndRefresh(listOf(2,10))})
        } else {
            SelectableCard(text = "2M\n7m", fontSize,isSelected = false,  onClick = {model.addIntervalsAndRefresh(listOf(2,10))})
        }
        if(intervals.containsAll(listOf(3,9))){
            SelectableCard(text = "3m\n6M", fontSize + 2,isSelected = true, onClick = {model.removeIntervalsAndRefresh(listOf(3,9))})
        } else {
            SelectableCard(text = "3m\n6M", fontSize,isSelected = false,  onClick = {model.addIntervalsAndRefresh(listOf(3,9))})
        }
        if(intervals.containsAll(listOf(4,8))){
            SelectableCard(text = "3M\n6m", fontSize + 2,isSelected = true, onClick = {model.removeIntervalsAndRefresh(listOf(4,8))})
        } else {
            SelectableCard(text = "3M\n6m", fontSize, isSelected = false, onClick = {model.addIntervalsAndRefresh(listOf(4,8))})
        }
        if(intervals.containsAll(listOf(5,7))){
            SelectableCard(text = "4\n5", fontSize + 2,isSelected = true, onClick = {model.removeIntervalsAndRefresh(listOf(5,7))})
        } else {
            SelectableCard(text = "4\n5", fontSize, isSelected = false, onClick = {model.addIntervalsAndRefresh(listOf(5,7))})
        }
        if(intervals.contains(6)){
            SelectableCard(text = "4a\n5d", fontSize + 2, isSelected = true, onClick = {model.removeIntervalsAndRefresh(listOf(6))})
        } else {
            SelectableCard(text = "4a\n5d", fontSize,isSelected = false,  onClick = {model.addIntervalsAndRefresh(listOf(6))})
        }
        if(intervals.contains(0)){
            SelectableCard(text = "1\n8", fontSize + 2, isSelected = true, onClick = {model.removeIntervalsAndRefresh(listOf(0))})
        } else {
            SelectableCard(text = "1\n8", fontSize,isSelected = false,  onClick = {model.addIntervalsAndRefresh(listOf(0))})
        }
    }
}


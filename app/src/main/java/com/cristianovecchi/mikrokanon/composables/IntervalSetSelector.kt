package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.ui.buttonsDisplayBackgroundColor

@Composable
fun IntervalSetSelector(model: AppViewModel, fontSize: Int) {
    val intervals by model.intervalSet.observeAsState(emptyList())
    val backgroundColor = MaterialTheme.colors.buttonsDisplayBackgroundColor
    val elaborating by model.elaborating.asFlow().collectAsState(initial = false)
    val removeIntervalsAndRefresh = { list:List<Int> ->
        if(!elaborating) model.removeIntervalsAndRefresh(list)
    }
    val addIntervalsAndRefresh = { list:List<Int> ->
        if(!elaborating) model.addIntervalsAndRefresh(list)
    }
    Row(modifier = Modifier.fillMaxWidth().background(backgroundColor), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {


        if(intervals.containsAll(listOf(1,11))){
            SelectableCard(text = "2m\n7M", fontSize + 2, isSelected = true, onClick = { removeIntervalsAndRefresh(listOf(1,11)) })
        } else {
            SelectableCard(text = "2m\n7M",fontSize, isSelected = false, onClick = {addIntervalsAndRefresh(listOf(1,11))})
        }
        if(intervals.containsAll(listOf(2,10))){
            SelectableCard(text = "2M\n7m",fontSize + 2, isSelected = true, onClick = {removeIntervalsAndRefresh(listOf(2,10))})
        } else {
            SelectableCard(text = "2M\n7m", fontSize,isSelected = false,  onClick = {addIntervalsAndRefresh(listOf(2,10))})
        }
        if(intervals.containsAll(listOf(3,9))){
            SelectableCard(text = "3m\n6M", fontSize + 2,isSelected = true, onClick = {removeIntervalsAndRefresh(listOf(3,9))})
        } else {
            SelectableCard(text = "3m\n6M", fontSize,isSelected = false,  onClick = {addIntervalsAndRefresh(listOf(3,9))})
        }
        if(intervals.containsAll(listOf(4,8))){
            SelectableCard(text = "3M\n6m", fontSize + 2,isSelected = true, onClick = {removeIntervalsAndRefresh(listOf(4,8))})
        } else {
            SelectableCard(text = "3M\n6m", fontSize, isSelected = false, onClick = {addIntervalsAndRefresh(listOf(4,8))})
        }
        if(intervals.containsAll(listOf(5,7))){
            SelectableCard(text = "4\n5", fontSize + 2,isSelected = true, onClick = {removeIntervalsAndRefresh(listOf(5,7))})
        } else {
            SelectableCard(text = "4\n5", fontSize, isSelected = false, onClick = {addIntervalsAndRefresh(listOf(5,7))})
        }
        if(intervals.contains(6)){
            SelectableCard(text = "4a\n5d", fontSize + 2, isSelected = true, onClick = {removeIntervalsAndRefresh(listOf(6))})
        } else {
            SelectableCard(text = "4a\n5d", fontSize,isSelected = false,  onClick = {addIntervalsAndRefresh(listOf(6))})
        }
        if(intervals.contains(0)){
            SelectableCard(text = "1\n8", fontSize + 2, isSelected = true, onClick = {removeIntervalsAndRefresh(listOf(0))})
        } else {
            SelectableCard(text = "1\n8", fontSize,isSelected = false,  onClick = {addIntervalsAndRefresh(listOf(0))})
        }
    }
}


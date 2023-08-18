package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.ui.AppColors

@Composable
fun IntervalSetSelector(model: AppViewModel, fontSize: Int,
                        colors: AppColors, names: List<String>, callback: () -> Unit = {}) {
    val intervals by model.intervalSetVertical.observeAsState(model.intervalSetVertical.value!!)
    val backgroundColor = colors.buttonsDisplayBackgroundColor
    val elaborating by model.elaborating.asFlow().collectAsState(initial = false)
    val removeIntervalsAndRefresh = { list:List<Int> ->
        if(!elaborating) {
            model.removeIntervalsAndRefresh(list)
            callback()
        }
    }
    val addIntervalsAndRefresh = { list:List<Int> ->
        if(!elaborating){
            model.addIntervalsAndRefresh(list)
            callback()
        }
    }
    Row(modifier = Modifier.fillMaxWidth().background(backgroundColor), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        if(intervals.containsAll(listOf(1,11))){
            SelectableCard(text = names[0], fontSize + 2, isSelected = true, colors = colors, onClick = { removeIntervalsAndRefresh(listOf(1,11)) })
        } else {
            SelectableCard(text = names[0],fontSize,  colors = colors,  isSelected = false, onClick = {addIntervalsAndRefresh(listOf(1,11))})
        }
        if(intervals.containsAll(listOf(2,10))){
            SelectableCard(text = names[1],fontSize + 2,  colors = colors, isSelected = true, onClick = {removeIntervalsAndRefresh(listOf(2,10))})
        } else {
            SelectableCard(text = names[1], fontSize,isSelected = false,   colors = colors, onClick = {addIntervalsAndRefresh(listOf(2,10))})
        }
        if(intervals.containsAll(listOf(3,9))){
            SelectableCard(text = names[2], fontSize + 2,isSelected = true,  colors = colors, onClick = {removeIntervalsAndRefresh(listOf(3,9))})
        } else {
            SelectableCard(text = names[2], fontSize,isSelected = false,  colors = colors,  onClick = {addIntervalsAndRefresh(listOf(3,9))})
        }
        if(intervals.containsAll(listOf(4,8))){
            SelectableCard(text = names[3], fontSize + 2,isSelected = true, colors = colors,  onClick = {removeIntervalsAndRefresh(listOf(4,8))})
        } else {
            SelectableCard(text = names[3], fontSize, isSelected = false,  colors = colors, onClick = {addIntervalsAndRefresh(listOf(4,8))})
        }
        if(intervals.containsAll(listOf(5,7))){
            SelectableCard(text = names[4], fontSize + 2,isSelected = true,  colors = colors, onClick = {removeIntervalsAndRefresh(listOf(5,7))})
        } else {
            SelectableCard(text = names[4], fontSize, isSelected = false,  colors = colors, onClick = {addIntervalsAndRefresh(listOf(5,7))})
        }
        if(intervals.contains(6)){
            SelectableCard(text = names[5], fontSize + 2, isSelected = true,  colors = colors, onClick = {removeIntervalsAndRefresh(listOf(6))})
        } else {
            SelectableCard(text = names[5], fontSize,isSelected = false,   colors = colors, onClick = {addIntervalsAndRefresh(listOf(6))})
        }
        if(intervals.contains(0)){
            SelectableCard(text = names[6], fontSize + 2, isSelected = true,  colors = colors, onClick = {removeIntervalsAndRefresh(listOf(0))})
        } else {
            SelectableCard(text = names[6], fontSize,isSelected = false,   colors = colors, onClick = {addIntervalsAndRefresh(listOf(0))})
        }
    }
}


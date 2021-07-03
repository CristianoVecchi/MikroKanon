package com.cristianovecchi.mikrokanon.composables


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.AIMUSIC.Counterpoint
import com.cristianovecchi.mikrokanon.AppViewModel


@Composable
fun NoteTable(model: AppViewModel, counterpoint: Counterpoint, clipsText: MutableList<MutableList<String>>, fontSize: Int,
              onClick: (Counterpoint) -> Unit){

    val listState = rememberLazyListState()
    val selectedCounterpoint by model.selectedCounterpoint.observeAsState()
    val isSelected = counterpoint == selectedCounterpoint
    val borderWidth by animateIntAsState(if(isSelected) 10 else 0)
    val fontWeight = if(isSelected) FontWeight.ExtraBold else FontWeight.Normal
    val finalFontSize by animateIntAsState(if (isSelected) (fontSize  ) else fontSize)
    val cellDarkColor by animateColorAsState( if(isSelected) Color(0.3f,0.3f,0.9f,1.0f)
                        else Color(0.1f,0.1f,0.65f,1.0f) )
    val cellLightColor by animateColorAsState( if(isSelected) Color(0.3f,0.3f,1f,1.0f)
                        else Color(0.1f,0.1f,0.70f,1.0f) )
    val cellColors = listOf(cellDarkColor, cellLightColor)
    val selectionColor = Color(0.8f,0.8f,0.9f,1.0f)
    val textColor by animateColorAsState( if(isSelected) Color.White
                    else Color(0.8f,0.8f,0.8f,1.0f) )
    val textStyle = TextStyle(
        fontSize = finalFontSize.sp,
        color = textColor, fontWeight = fontWeight
    )
        LazyRow(modifier = Modifier
            .animateContentSize(animationSpec = tween(30, easing = LinearEasing))
            .padding(10.dp)
            .border(BorderStroke(borderWidth.dp, selectionColor))
            .clickable {
                onClick(counterpoint)
            }, state = listState)
        {
            itemsIndexed(clipsText) { i, col ->
                Column(
                    Modifier.width(70.dp)
                ) {
                    for (j in col.indices) {
                        val clipText = col[j]
                        Text(
                            text = clipText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(cellColors[(i + j) % 2])
                                .padding(8.dp),
                            style = textStyle
                        )
                    }
                }
            }
        }
}





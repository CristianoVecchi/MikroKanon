package com.cristianovecchi.mikrokanon.composables


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
import com.cristianovecchi.mikrokanon.AIMUSIC.Counterpoint
import com.cristianovecchi.mikrokanon.AppViewModel


@Composable
fun NoteTable(model: AppViewModel, counterpoint: Counterpoint, fontSize: Int,
              onClick: (Counterpoint) -> Unit){

    val listState = rememberLazyListState()
    val selectedCounterpoint by model.selectedCounterpoint.observeAsState()
    val isSelected = counterpoint == selectedCounterpoint
    val borderWidth = if(isSelected) 10 else 0
    val fontWeight = if(isSelected) FontWeight.ExtraBold else FontWeight.Normal
    val parts = toClips(counterpoint, NoteNamesIt.values().map { value -> value.toString() })
    val maxSize = parts.maxOf{ it.size}
    val cellDarkColor = if(isSelected) Color(0.0f,0.0f,0.9f,1.0f)
                        else Color(0.0f,0.0f,0.5f,1.0f)
    val cellLightColor = if(isSelected) Color(0.0f,0.0f,1f,1.0f)
                        else Color(0.0f,0.0f,0.6f,1.0f)
    val selectionColor = Color(0.8f,0.8f,0.9f,1.0f)
    val textColor = if(isSelected) Color.White
                    else Color(0.9f,0.9f,0.9f,1.0f)

    LazyRow(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp).border(BorderStroke(borderWidth.dp, selectionColor))
        .clickable {
            onClick(counterpoint)
        }, state = listState)
            {
                itemsIndexed((0 until maxSize).toList()) { i, _ ->
                Column(modifier = Modifier.fillMaxWidth()
                    ) {
                            for(j in parts.indices){

                                val clip = if (i < parts[j].size) parts[j][i] else Clip()

                                Box(modifier = Modifier
                                    .width(80.dp)
                                    .background(if ((i + j) % 2 == 0) cellDarkColor else cellLightColor)
                                ) {
                                    Text(text = clip.text, modifier = Modifier.padding(8.dp),
                                        style = TextStyle(fontSize = if(isSelected) (fontSize+3).sp else fontSize.sp,
                                        color = textColor, fontWeight = fontWeight))
                                }
                            }
                     }
            }
    }
}





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
import com.cristianovecchi.mikrokanon.ui.AppColors


@Composable
fun NoteTable(model: AppViewModel, counterpoint: Counterpoint, clipsText: List<List<String>>, colors: AppColors,
              fontSize: Int, cellWidth: Int, redNotes: List<List<Boolean>>? = null,
              onClick: (Counterpoint) -> Unit){

    val errorColor = Color.Red
    val error = redNotes != null
    val listState = rememberLazyListState()
    val selectedCounterpoint by model.selectedCounterpoint.observeAsState()
    val isSelected = counterpoint == selectedCounterpoint
    val borderWidth by animateIntAsState(if(isSelected) 10 else 0)
    val fontWeight = if(isSelected) FontWeight.ExtraBold else FontWeight.Normal
    val finalFontSize by animateIntAsState(if (isSelected) (fontSize  ) else fontSize)
    val cellDarkColor by animateColorAsState( if(isSelected) colors.cellDarkColorSelected else colors.cellDarkColorUnselected )
    val cellLightColor by animateColorAsState( if(isSelected) colors.cellLightColorSelected else colors.cellLightColorUnselected )
    val cellColors = listOf(cellDarkColor, cellLightColor)
    val selectionColor = if(error) errorColor else colors.selectionBorderColor
    val textColor by animateColorAsState( if(isSelected) colors.cellTextColorSelected else colors.cellTextColorUnselected )


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
                    Modifier.width(cellWidth.dp)// 70
                ) {
                    for (j in col.indices) {
                        val clipText = col[j]
                        Text(
                            text = clipText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if(error && redNotes!![j][i]) errorColor else cellColors[(i + j) % 2])
                                .padding(8.dp),
                            style = textStyle
                        )
                    }
                }
            }
        }
}





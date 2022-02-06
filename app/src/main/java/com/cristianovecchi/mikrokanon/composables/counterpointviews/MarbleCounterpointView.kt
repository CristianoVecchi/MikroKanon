package com.cristianovecchi.mikrokanon.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cristianovecchi.mikrokanon.AIMUSIC.Counterpoint
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.ui.AppColors

@Composable
fun MarbleCounterpointView(model: AppViewModel, counterpoint: Counterpoint, clipsText: List<List<String>>, colors: AppColors,
                           totalWidthDp: Int, totalHeightDp: Int, dpDensity: Float, padding: Int, redNotes: List<List<Boolean>>? = null,
                           onClick: (Counterpoint) -> Unit){

    val errorColor = Color.Red
    val error = redNotes != null
    val selectedCounterpoint by model.selectedCounterpoint.observeAsState()
    val isSelected = counterpoint == selectedCounterpoint
    val borderWidth by animateIntAsState(if(isSelected) 4 else 0)
    val cellDarkColor by animateColorAsState( if(isSelected) colors.cellDarkColorSelected else colors.cellDarkColorUnselected )
    val cellLightColor by animateColorAsState( if(isSelected) colors.cellLightColorSelected else colors.cellLightColorUnselected )
    val cellColors = listOf(cellDarkColor, cellLightColor)
    val selectionColor = if(error) errorColor else colors.selectionBorderColor
    val textColor by animateColorAsState( if(isSelected) colors.cellTextColorSelected else colors.cellTextColorUnselected )

    Canvas(modifier = Modifier.width((totalWidthDp / dpDensity).dp).height((totalHeightDp / dpDensity).dp)
        .padding(padding.dp)
        .border(BorderStroke(borderWidth.dp, selectionColor))
        .clickable {
            onClick(counterpoint)
        }) {
        val cellWidth = ((100f / clipsText.size) * ((this.size.width) )/ 100f)
        val cellHeight = ((100f / clipsText[0].size) * ((this.size.height ) ) / 100f)
        val cellWidthHalf = cellWidth / 2
        val cellHeightHalf = cellHeight / 2
        val minDimensionHalf = if(cellWidth <= cellHeight) cellWidth * 0.4f else cellHeight * 0.4f
        drawRect(cellDarkColor, Offset(0f,0f ),
            Size(this.size.width, this.size.height ))

        for(y in clipsText[0].indices){
            for(x in clipsText.indices){
                val newX = x * cellWidth
                val newY = y * cellHeight
                if((x + y) % 2 == 0) {
                    drawRect(cellLightColor, Offset(newX, newY), Size(cellWidth,cellHeight))
                }
                if (clipsText[x][y] != "") {
                    drawCircle( if(error && redNotes!![y][x]) errorColor else textColor,
                        center = Offset(newX + cellWidthHalf, newY + cellHeightHalf),
                        radius = minDimensionHalf)
                }
            }
        }
    }

}
package com.cristianovecchi.mikrokanon.composables.counterpointviews

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.*
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
fun MarbleCounterpointView(model: AppViewModel, counterpoint: Counterpoint, ribattutos: List<List<Any>>, colors: AppColors,
                           totalWidthDp: Int, totalHeightDp: Int, dpDensity: Float, padding: Int, redNotes: List<List<Boolean>>? = null,
                           onClick: (Counterpoint) -> Unit){

    val errorColor = Color.Red
    val error = redNotes != null
    val selectedCounterpoint by model.selectedCounterpoint.observeAsState()
    val isSelected = counterpoint == selectedCounterpoint
    val borderWidth by animateIntAsState(if(isSelected) 4 else 0)
    val cellDarkColor by animateColorAsState( if(isSelected) colors.cellDarkColorSelected else colors.cellDarkColorUnselected )
    val cellLightColor by animateColorAsState( if(isSelected) colors.cellLightColorSelected else colors.cellLightColorUnselected )
    val selectionColor = if(error) errorColor else colors.selectionBorderColor
    val textColor by animateColorAsState( if(isSelected) colors.cellTextColorSelected else colors.cellTextColorUnselected )
    //val alphas = colors.alphas

    Canvas(modifier = Modifier.width((totalWidthDp / dpDensity).dp).height((totalHeightDp / dpDensity).dp)
        .padding(padding.dp)
        .border(BorderStroke(borderWidth.dp, selectionColor))
        .clickable(
            onClick = {onClick(counterpoint)}
        )
        ) {
        val nParts = counterpoint.parts.size
        val maxLength = counterpoint.maxSize().coerceAtMost(2048)
        val cellWidth = ((100f / maxLength) * ((this.size.width) )/ 100f)
        val cellHeight = ((100f / nParts) * ((this.size.height ) ) / 100f)
        val cellWidthHalf = cellWidth / 2
        val cellHeightHalf = cellHeight / 2
        val minDimensionHalf = if(cellWidth <= cellHeight) cellWidth * 0.4f else cellHeight * 0.4f
        val strokeWidth = minDimensionHalf * 2
            drawRect(cellDarkColor, Offset(0f,0f ),
            Size(this.size.width, this.size.height ))

        for(y in 0 until nParts){
            val part = counterpoint.parts[y]
            for(x in 0 until maxLength){
                if((x + y) % 2 == 0) {
                    drawRect(cellLightColor, Offset(x * cellWidth, y * cellHeight), Size(cellWidth,cellHeight))
                }
            }
            for(x in 0 until maxLength){
                val newX = x * cellWidth
                val newY = y * cellHeight
                val value = if (x < part.absPitches.size) part.absPitches[x] else -1
                val marbleColor = if(error && redNotes!![y][x]) errorColor else textColor
                if (value != -1) {
                    if (ribattutos[y][x] as Boolean){
                        drawLine(textColor, Offset(newX + cellWidthHalf, newY + cellHeightHalf),
                            Offset(newX + cellWidthHalf + cellWidth, newY + cellHeightHalf),
                            strokeWidth = strokeWidth)
                    }
                    drawCircle( marbleColor,
                        center = Offset(newX + cellWidthHalf, newY + cellHeightHalf),
                        radius = minDimensionHalf,
                        //alpha = alphas[value]
                    )
                }
            }
        }
    }

}
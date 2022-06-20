package com.cristianovecchi.mikrokanon.composables.counterpointviews

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import com.cristianovecchi.mikrokanon.AIMUSIC.Counterpoint
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.ui.AIColor
import com.cristianovecchi.mikrokanon.ui.AppColors
import com.cristianovecchi.mikrokanon.ui.shift
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class DrawingInfo(val counterpoint: Counterpoint, val totalWidthDp: Int, val totalHeightDp: Int, val dpDensity: Float, val padding: Int, val redNotes: List<List<Boolean>>? = null,
                       val borderWidth: Int, val selectionColor: Color, val cellLightColor: Color, val textColor: Color, val errorColor: Color, val error: Boolean, val onClick: (Counterpoint) -> Unit)
@Composable
fun QuantumCounterpointView(model: AppViewModel, counterpoint: Counterpoint, appColors: AppColors,
                            totalWidthDp: Int, totalHeightDp: Int, dpDensity: Float, padding: Int, redNotes: List<List<Boolean>>? = null,
                            onClick: (Counterpoint) -> Unit){

    val colors = appColors
    val errorColor = Color.Red
    val error = redNotes != null
    val selectedCounterpoint by model.selectedCounterpoint.observeAsState()
    val isSelected = counterpoint == selectedCounterpoint
    val borderWidth by animateIntAsState(if(isSelected) 4 else 0)
   // val cellDarkColor by animateColorAsState( if(isSelected) colors.cellDarkColorSelected else colors.cellDarkColorUnselected )
    val cellLightColor by animateColorAsState( if(isSelected) colors.cellLightColorSelected else colors.cellLightColorUnselected )
    val selectionColor = if(error) errorColor else colors.selectionBorderColor
    val textColor by animateColorAsState( if(isSelected) colors.cellTextColorSelected else colors.cellTextColorUnselected )
    val drawingInfo by derivedStateOf{
        DrawingInfo(
            counterpoint, totalWidthDp, totalHeightDp, dpDensity, padding, redNotes,
            borderWidth, selectionColor, cellLightColor, textColor, errorColor, error, onClick
        )
    }
    QuantumCanvas(drawingInfo = drawingInfo)

        //val alphas = colors.alphas

//      ANGLES TEST
//        val angles = listOf(0.0, 30.0, 60.0, 90.0)
//        for(a in angles){
//            val x = allX / 2f
//            val y = allY / 2f
//            val radAngle = Math.toRadians(a)
//            val seg = 300.0
//            val toX = x + cos(radAngle) * seg
//            val toY = y + sin(radAngle) * seg
//            drawLine(Color.Red, Offset(x, y), Offset(toX.toFloat(), toY.toFloat()), strokeWidth = strokeWidth)
//        }
    
}
@Composable
fun  QuantumCanvas(drawingInfo: DrawingInfo) {
    val (counterpoint, totalWidthDp, totalHeightDp, dpDensity, padding, redNotes,
    borderWidth, selectionColor, cellLightColor, textColor, errorColor, error, onClick) = drawingInfo
    Canvas(modifier = Modifier
        .width((totalWidthDp / dpDensity).dp)
        .height((totalHeightDp / dpDensity).dp)
        .padding(padding.dp)
        .border(BorderStroke(borderWidth.dp, selectionColor))
        .clickable(
            onClick = { onClick(counterpoint) }
        )
    ) {
        val nParts = counterpoint.parts.size
        val maxLength = counterpoint.maxSize().coerceAtMost(1024)
        val strokeWidth = 8f
        val colorStep = 0.03f
        var newTextColor = textColor.shift(-colorStep * (nParts-1))
        val allX = this.size.width
        val allY = this.size.height
        val allXhalf = allX / 2f
        val segment: Double = (totalWidthDp.toDouble() / maxLength * 0.45)
        drawRect(Brush.radialGradient(listOf(cellLightColor, Color.Black), Offset(allXhalf,allXhalf), allX, TileMode.Clamp),
            Offset(0f,0f ), Size( allX, allY ) )

        for(noteY in nParts-1 downTo 0){
            val partPitches = counterpoint.parts[noteY].absPitches
            var x = allX / 2f
            var y = allY / 2f
            var angle = -90.0
            for(noteX in 0 until maxLength){
                val value = if (noteX < partPitches.size) partPitches[noteX] else -1
                angle = if (value==-1) angle else (value - 3) * 30.0
                angle = if(angle < 0.0) angle + 360.0 else angle
                val radAngle = Math.toRadians(angle)
                val nx = (x + cos(radAngle) * segment).toFloat()
                val ny = (y + sin(radAngle) * segment).toFloat()

                val lineColor = if(value == -1) Color.Transparent else if(error && redNotes!![noteY][noteX]) errorColor else newTextColor
                drawLine(lineColor, Offset(x, y), Offset(nx, ny), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                x = nx; y = ny

            }
            newTextColor = newTextColor.shift(colorStep)
        }

    }


}
package com.cristianovecchi.mikrokanon.composables

import android.util.Size
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.cristianovecchi.mikrokanon.toDp
import kotlin.math.roundToInt

@Composable
fun ColorSelector(widths: Triple<Int, Int, Int> = Triple(50,50,50), height: Dp,
                  barHeight: Int = (height.value / 8f).toInt(), //+ (height.value % 4f).toInt(),
    startColor: Color = Color.Black, refresh: Boolean, dispatchColor : (Color) -> Unit ) {

    var sizeR by remember { mutableStateOf(IntSize.Zero) }
    var sizeG by remember { mutableStateOf(IntSize.Zero) }
    var sizeB by remember { mutableStateOf(IntSize.Zero) }
    var redY by remember { mutableStateOf(0f) }
    var greenY by remember { mutableStateOf(0f) }
    var blueY by remember { mutableStateOf(0f) }
    var firstColor by remember { mutableStateOf(true) }

    if(firstColor && sizeR.height != 0 && sizeG.height != 0 && sizeB.height != 0){
        false.also { firstColor = it } // LOL (instead of [firstColor = false] that gets a warning)
        redY = (1f - startColor.red) * (sizeR.height - barHeight)
        greenY = (1f - startColor.green) * (sizeR.height - barHeight)
        blueY = (1f - startColor.blue) * (sizeR.height - barHeight)
    }
    if(refresh && sizeR.height != 0 && sizeG.height != 0 && sizeB.height != 0){
        redY = (1f - startColor.red) * (sizeR.height - barHeight)
        greenY = (1f - startColor.green) * (sizeR.height - barHeight)
        blueY = (1f - startColor.blue) * (sizeR.height - barHeight)
    }
    val red =
        if (sizeR.height == 0) 0f else (1f - (redY / (sizeR.height - barHeight))).coerceIn(0f, 1f)
    val green =
        if (sizeG.height == 0) 0f else (1f - (greenY / (sizeG.height - barHeight))).coerceIn(0f, 1f)
    val blue =
        if (sizeB.height == 0) 0f else (1f - (blueY / (sizeB.height - barHeight))).coerceIn(0f, 1f)

    if(sizeR.height == 0 || sizeG.height == 0 || sizeB.height == 0) dispatchColor(startColor)
    else dispatchColor(Color(red,green,blue))

    val (wR, wG, wB) = widths
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

        Column(Modifier.width(wR.dp).onGloballyPositioned { coordinates ->
            sizeR = IntSize(wR, coordinates.size.height)}){
            Box(
                Modifier
                    .background(Color(red, 0f, 0f)).size(wR.dp, height)
            ) {
                Box(
                    Modifier
                        .offset { IntOffset(0, redY.roundToInt()) }
                        .background(Color.White)
                        .size(wR.dp, barHeight.toDp().dp +1.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consumeAllChanges()
                                redY += dragAmount.y
                                redY = redY.coerceIn(0f, (sizeR.height - barHeight).toFloat())
                            }
                        }
                )
            }
        }
        Column(Modifier.width(wG.dp).onGloballyPositioned { coordinates ->
            sizeG = IntSize(wG, coordinates.size.height)})
         {
            Box(Modifier.background(Color(0f, green, 0f)).size(wG.dp, height ))
             {
                Box(
                    Modifier
                        .offset { IntOffset(0, greenY.roundToInt()) }
                        .background(Color.White)
                        .size(wG.dp, barHeight.toDp().dp+1.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consumeAllChanges()
                                greenY += dragAmount.y
                                greenY = greenY.coerceIn(0f, (sizeG.height - barHeight).toFloat())
                            }
                        }
                )
            }
        }
        Column(Modifier.width(wB.dp).onGloballyPositioned { coordinates ->
            sizeB = IntSize(wB, coordinates.size.height)} )
        {
            Box(Modifier.background(Color(0f, 0f, blue)).size(wB.dp, height))
            {
                Box(Modifier.offset { IntOffset(0, blueY.roundToInt()) }
                            .background(Color.White)
                            .size(wB.dp, barHeight.toDp().dp+1.dp)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consumeAllChanges()
                                    blueY += dragAmount.y
                                    blueY = blueY.coerceIn(0f, (sizeB.height - barHeight).toFloat())
                                }
                            }
                )
            }
        }
    }
}


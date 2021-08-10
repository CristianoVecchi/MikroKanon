package com.cristianovecchi.mikrokanon.composables

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
fun ColorSelector(height: Dp = 300.dp, startColor: Color = Color.Black, dispatchColor : (Color) -> Unit ) {
    var sizeR by remember { mutableStateOf(IntSize.Zero) }
    var sizeG by remember { mutableStateOf(IntSize.Zero) }
    var sizeB by remember { mutableStateOf(IntSize.Zero) }
    var redY by remember { mutableStateOf(0f) }
    var greenY by remember { mutableStateOf(0f) }
    var blueY by remember { mutableStateOf(0f) }
    var firstColor by remember { mutableStateOf(true) }
    val barHeight = 80
    if(firstColor && sizeR.height != 0 && sizeG.height != 0 && sizeB.height != 0){
        false.also { firstColor = it } // LOL (instead of [firstColor = false] that gets a warning)
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

    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
        Column(Modifier.weight(1f).onGloballyPositioned { coordinates ->
            sizeR = coordinates.size}){
            Box(
                Modifier
                    .background(Color(red, 0f, 0f)).size(sizeR.width.toDp().dp+1.dp, height)
            ) {
                Box(
                    Modifier
                        .offset { IntOffset(0, redY.roundToInt()) }
                        .background(Color.White)
                        .size(sizeR.width.toDp().dp+1.dp, barHeight.toDp().dp)
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
        Column(Modifier.weight(1f).onGloballyPositioned { coordinates -> sizeG = coordinates.size})
         {
            Box(Modifier.background(Color(0f, green, 0f)).size(sizeG.width.toDp().dp+1.dp, height ))
             {
                Box(
                    Modifier
                        .offset { IntOffset(0, greenY.roundToInt()) }
                        .background(Color.White)
                        .size(sizeG.width.toDp().dp+1.dp, barHeight.toDp().dp)
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
        Column(Modifier.weight(1f).onGloballyPositioned { coordinates -> sizeB = coordinates.size })
        {
            Box(Modifier.background(Color(0f, 0f, blue)).size(sizeB.width.toDp().dp+1.dp, height))
            {
                Box(Modifier.offset { IntOffset(0, blueY.roundToInt()) }
                            .background(Color.White)
                            .size(sizeB.width.toDp().dp+1.dp, barHeight.toDp().dp)
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


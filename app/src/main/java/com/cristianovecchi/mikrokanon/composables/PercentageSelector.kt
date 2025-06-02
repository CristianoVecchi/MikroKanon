package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.cristianovecchi.mikrokanon.toDp
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun PercentageSelector(width: Dp = 150.dp, height: Dp,
                       barHeight: Int = (height.value / 8f).toInt(), //+ (height.value % 4f).toInt(),
                       topColor : Color = Color.LightGray, groundColor: Color = Color.Black,
                       startPercentage: Float = 0f, refresh: Boolean, dispatchPercentage : (Float) -> Unit ) {


    var sizeSlide by remember { mutableStateOf(IntSize.Zero) }
    var percentageY by remember { mutableStateOf(0f) }
    var isFirstPercentage by remember { mutableStateOf(true) }
    var percentage by remember { mutableStateOf(startPercentage) }

    if(isFirstPercentage && sizeSlide.height != 0){
        //maxHeightSlide = sizeSlide.height.toFloat() - barHeight.toFloat()
        false.also { isFirstPercentage = it } // LOL (instead of [firstColor = false] that gets a warning)
        percentageY = (1f - percentage) * (sizeSlide.height - barHeight)
        percentage = if (sizeSlide.height == 0) startPercentage else (1f - (percentageY / (sizeSlide.height - barHeight))).coerceIn(0f, 1f)
    }
    if(refresh  && sizeSlide.height != 0){
        //maxHeightSlide = sizeSlide.height.toFloat() - barHeight.toFloat()
        percentage = startPercentage
        percentageY = (1f - percentage) * (sizeSlide.height - barHeight)
    }
    // 1f - percentage = percentageY / (sizeSlide.height - barHeight)
    // - percentage = percentageY / (sizeSlide.height - barHeight) -1f
    // percentage = -(percentageY / (sizeSlide.height -barHeight) -1f)
    //val red = if (sizeSlide.height == 0) 0f else (1f - (redY / (sizeSlide.height - barHeight))).coerceIn(0f, 1f)
    //percentage = if (sizeSlide.height == 0) startPercentage else (1f - (percentageY / (sizeSlide.height - barHeight))).coerceIn(0f, 1f)

    //val blue = if (sizeSlide.height == 0) 0f else (1f - (blueY / (sizeSlide.height - barHeight))).coerceIn(0f, 1f)
    val topRed = topColor.red; val topGreen = topColor.green; val topBlue = topColor.blue
    val groundRed = groundColor.red; val groundGreen = groundColor.green; val groundBlue = groundColor.blue

    val red = (groundRed + (topRed - groundRed) * percentage).coerceIn(0f, 1f)
    val green = (groundGreen + (topGreen - groundGreen) * percentage).coerceIn(0f, 1f)
    val blue = (groundBlue + (topBlue - groundBlue) * percentage).coerceIn(0f, 1f)
    dispatchPercentage(percentage)

    // quantity : x = 100 : perc
    // quantity * perc = x * 100
    // quantity * perc / 100 = x

        Column(Modifier.width(width).onGloballyPositioned { coordinates ->
            sizeSlide = coordinates.size})
            //sizeSlide = IntSize(width.value.toInt(), coordinates.size.height)})
        {
            Box(Modifier.background(Color(red, green, blue)).size(width, height))
            {
                Box(
                    Modifier
                        .offset { IntOffset(0, percentageY.roundToInt()) }
                        .background(Color.White)
                        .size(width, barHeight.toDp().dp+1.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consumeAllChanges()
                                val maxHeightSlide = sizeSlide.height.toFloat() - barHeight.toFloat()
                                percentageY = (percentageY+dragAmount.y).coerceIn(0f, maxHeightSlide)
                                percentage = (-(percentageY / (sizeSlide.height -barHeight) -1f)).absoluteValue
                            }
                        }
                )
            }
        }
//        Column(Modifier.width(wB.dp).onGloballyPositioned { coordinates ->
//            sizeRight = IntSize(wB, coordinates.size.height)} ) {
//
//        }
    }



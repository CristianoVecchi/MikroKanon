package com.cristianovecchi.mikrokanon.composables.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cristianovecchi.mikrokanon.composables.PercentageSelector
import com.cristianovecchi.mikrokanon.cutDecimals
import com.cristianovecchi.mikrokanon.ui.AppColors
import com.cristianovecchi.mikrokanon.ui.Dimensions
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun PercentageDialog(percentageDialogData: MutableState<PercentageDialogData>,
                       dimensions: Dimensions, okText: String = "OK", appColors: AppColors,
                       onRefreshRendering: (Boolean) -> Unit =
                           {percentageDialogData.value = percentageDialogData.value.copy(firstRendering = it, isRefreshing = true)},
                       onStopRefresh: () -> Unit =
                           {percentageDialogData.value = percentageDialogData.value.copy(isRefreshing = false)},
                       onSetPercentageAndRefresh: (Float) -> Unit =
                           {percentageDialogData.value = percentageDialogData.value.copy(percentageValue = it, firstRendering = true)},
                       onDismissRequest: () -> Unit =
                           { percentageDialogData.value = PercentageDialogData(model = percentageDialogData.value.model) })
{
    var percentage by remember { mutableStateOf(percentageDialogData.value.percentageValue) }
    val percentageFormat: NumberFormat = NumberFormat.getPercentInstance()
    percentageFormat.setMinimumFractionDigits(1)
    val decimalsToKeep = percentageDialogData.value.decimalsToKeep
    val showInts = percentageDialogData.value.showInts
    val increase = if(showInts) 0.01f else 0.001f
    val fontColor = appColors.dialogFontColor
    val back1Color = appColors.dialogBackgroundColor
    val back2Color = appColors.drawerBackgroundColor
    val beatColor = appColors.selectionBorderColor
//    val pass1Color: Color
//    val pass2Color: Color
//    val radarColor: Color
    val topDisplayColor = beatColor
    val groundDisplayColor = back2Color
    val internalColumnTopColor = back1Color
    val internalColumnGroundColor = back2Color
    val topRed = topDisplayColor.red; val topGreen = topDisplayColor.green; val topBlue = topDisplayColor.blue
    val groundRed = groundDisplayColor.red; val groundGreen = groundDisplayColor.green; val groundBlue = groundDisplayColor.blue
    val displayRed = (groundRed + (topRed - groundRed) * percentage).coerceIn(0f, 1f)
    val displayGreen = (groundGreen + (topGreen - groundGreen) * percentage).coerceIn(0f, 1f)
    val displayBlue = (groundBlue + (topBlue - groundBlue) * percentage).coerceIn(0f, 1f)
    val externalColumnColor = back2Color
    val percButtonBackColor = back2Color
    val percButtonFontColor = fontColor
    val percButtonSize = MaterialTheme.shapes.medium
    val title = percentageDialogData.value.title
    val fontSize = dimensions.dialogFontSize
    val percButtonFontSize = fontSize -2
    val middlePartHeight = dimensions.dialogHeight / 6 * 4
    if(percentageDialogData.value.dialogState){
        val padding = 10
        val halfPadding = padding / 2
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier
                    .width(dimensions.dialogWidth)
                    .height(dimensions.dialogHeight),
                shape = RoundedCornerShape(10.dp)
            ) {
                // MAIN COLUMN
                Column(modifier = Modifier.padding(padding.dp)) {
                    Spacer(modifier = Modifier.height(10.dp))
                    var customColor by remember{ mutableStateOf(Color.Black) }
                    val context = percentageDialogData.value.model.getContext()
                    if(percentageDialogData.value.firstRendering){

                        percentage = percentageDialogData.value.percentageValue
                        onRefreshRendering.invoke(false)
                    }
                    val h = dimensions.dialogHeight / 7
                    var size by remember { mutableStateOf(IntSize.Zero) }
                    val w = (dimensions.dialogWidth - padding.dp * 2 )
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(h)
                            .onGloballyPositioned { coordinates ->
                                size = coordinates.size
                            }
                            .background(Color(displayRed, displayGreen, displayBlue)),
                    ){
                        Text(text = title, Modifier.offset(halfPadding.dp, halfPadding.dp),
                            fontWeight = FontWeight.Bold, color = fontColor,
                            style = TextStyle(fontSize = fontSize.sp))
                        Row(Modifier.fillMaxWidth().height(h),
                            horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                                    Text(text = if(showInts) "${(percentage * 100).roundToInt()}"
                                                else "%,.1f%%".format(Locale.ENGLISH,percentage*100).replace(".0",""),
                                                style = TextStyle(fontSize = (dimensions.selectorClipFontSize * 2).sp, fontWeight = FontWeight.ExtraBold, color = fontColor))
                                }
                    }
                    Row(horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically ) {
                        Column(Modifier.width(w/3).height(middlePartHeight).background(externalColumnColor),
                                verticalArrangement = Arrangement.SpaceAround,
                                horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(onClick = { onSetPercentageAndRefresh.invoke(1f/6f*5f) },
                                shape = percButtonSize,
                                colors = ButtonDefaults.buttonColors(backgroundColor = percButtonBackColor, contentColor = percButtonFontColor),) {
                                Text(text = "5/6", fontSize = percButtonFontSize.sp) }
                            Button(
                                onClick = {
                                    onSetPercentageAndRefresh.invoke(1f/3f*2f)
                                },
                                shape = percButtonSize,
                                colors = ButtonDefaults.buttonColors(backgroundColor = percButtonBackColor, contentColor = percButtonFontColor),
                            ) {
                                Text(text = "2/3", fontSize = percButtonFontSize.sp)
                            }
                            Button( // left for visual symmetry
                                onClick = {
                                    onSetPercentageAndRefresh.invoke(1f/6f*3)
                                },
                                shape = percButtonSize,
                                colors = ButtonDefaults.buttonColors(backgroundColor = percButtonBackColor, contentColor = percButtonFontColor),
                                ) {
                                Text(text = "3/6", fontSize = percButtonFontSize.sp)
                            }
                            Button(
                                onClick = {
                                    onSetPercentageAndRefresh.invoke(1f/3f)
                                },
                                shape = percButtonSize,
                                colors = ButtonDefaults.buttonColors(backgroundColor = percButtonBackColor, contentColor = percButtonFontColor),
                                ) {
                                Text(text = "1/3", fontSize = percButtonFontSize.sp)
                            }
                            Button(
                                onClick = {
                                    onSetPercentageAndRefresh.invoke(1f/6f)
                                },
                                shape = percButtonSize,
                                colors = ButtonDefaults.buttonColors(backgroundColor = percButtonBackColor, contentColor = percButtonFontColor),
                                ) {
                                Text(text = "1/6", fontSize = percButtonFontSize.sp)
                            }
                        }
//                      //INTERNAL COLUMN
                        PercentageSelector(
                            width = w/3,
                            height = middlePartHeight,
                            topColor = internalColumnTopColor.copy(),
                            groundColor = internalColumnGroundColor.copy(),
                            startPercentage = percentage,
                            refresh = percentageDialogData.value.isRefreshing
                        ) { percentageFloat ->
                                percentage = percentageFloat
                                if(percentageDialogData.value.isRefreshing){ onStopRefresh.invoke() }
                        }
                        Column(Modifier.width(w/3).height(middlePartHeight).background(externalColumnColor),
                            verticalArrangement = Arrangement.SpaceAround,
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(
                                onClick = {
                                    onSetPercentageAndRefresh.invoke(0.25f/2f*7f)
                                },
                                shape = percButtonSize,
                                colors = ButtonDefaults.buttonColors(backgroundColor = percButtonBackColor, contentColor = percButtonFontColor),
                                ) {
                                Text(text = "7/8", fontSize = percButtonFontSize.sp)
                            }
                            Button(
                                onClick = {
                                    onSetPercentageAndRefresh.invoke(0.75f)
                                },
                                shape = percButtonSize,
                                colors = ButtonDefaults.buttonColors(backgroundColor = percButtonBackColor, contentColor = percButtonFontColor),
                                ) {
                                Text(text = "3/4", fontSize = percButtonFontSize.sp)
                            }
                            Button(
                                onClick = {
                                    onSetPercentageAndRefresh.invoke(0.25f/2f*5f)
                                },
                                shape = percButtonSize,
                                colors = ButtonDefaults.buttonColors(backgroundColor = percButtonBackColor, contentColor = percButtonFontColor),
                                ) {
                                Text(text = "5/8", fontSize = percButtonFontSize.sp)
                            }
                            Button(
                                onClick = {
                                    onSetPercentageAndRefresh.invoke(0.5f)
                                },
                                shape = percButtonSize,
                                colors = ButtonDefaults.buttonColors(backgroundColor = percButtonBackColor, contentColor = percButtonFontColor),
                                ) {
                                Text(text = "1/2", fontSize = percButtonFontSize.sp)
                            }
                            Button(
                                onClick = {
                                    onSetPercentageAndRefresh.invoke(0.25f/2f*3)
                                },
                                shape = percButtonSize,
                                colors = ButtonDefaults.buttonColors(backgroundColor = percButtonBackColor, contentColor = percButtonFontColor),
                                ) {
                                Text(text = "3/8", fontSize = percButtonFontSize.sp)
                            }
                            Button(
                                onClick = {
                                    onSetPercentageAndRefresh.invoke(0.25f)
                                },
                                shape = percButtonSize,
                                colors = ButtonDefaults.buttonColors(backgroundColor = percButtonBackColor, contentColor = percButtonFontColor),
                                ) {
                                Text(text = "1/4", fontSize = percButtonFontSize.sp)
                            }
                            Button(
                                onClick = {
                                    onSetPercentageAndRefresh.invoke(0.25f/2f)
                                },
                                shape = percButtonSize,
                                colors = ButtonDefaults.buttonColors(backgroundColor = percButtonBackColor, contentColor = percButtonFontColor),
                                ) {
                                Text(text = "1/8", fontSize = percButtonFontSize.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .height(h),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically ){
                        Button(
                            onClick = {
                                //println("Percentage: $percentage")
                                percentageDialogData.value.onSubmitButtonClick.invoke(percentage.cutDecimals(decimalsToKeep))
                                onDismissRequest.invoke()
                            },
                            shape = MaterialTheme.shapes.large
                        ) {
                            Text(text = okText, fontSize = fontSize.sp)
                        }
                        Row{
                            Button(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                onClick = {
                                    val newPercentage = (percentage - increase).coerceIn(0f, 1f)//.cutDecimals(percentageDialogData.value.decimalsToKeep)
                                    percentage = newPercentage
                                    onSetPercentageAndRefresh.invoke(newPercentage)
                                },
                                shape = MaterialTheme.shapes.large
                            ) {
                                Text(text = "-", fontSize = fontSize.sp)
                            }
                            Button(
                                onClick = {
                                    val newPercentage = (percentage + increase).coerceIn(0f, 1f)
                                    percentage = newPercentage
                                    onSetPercentageAndRefresh.invoke(newPercentage)
                                },
                                shape = MaterialTheme.shapes.large
                            ) {
                                Text(text = "+", fontSize = fontSize.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}




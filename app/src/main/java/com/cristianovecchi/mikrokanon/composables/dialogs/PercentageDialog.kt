package com.cristianovecchi.mikrokanon.composables.dialogs




import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
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
import com.cristianovecchi.mikrokanon.formatDecimal
import com.cristianovecchi.mikrokanon.ui.AppColors
import com.cristianovecchi.mikrokanon.ui.Dimensions
import java.text.NumberFormat
import java.util.Locale

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
    val fontColor = appColors.dialogFontColor
    val back1Color = appColors.dialogBackgroundColor
    val back2Color = appColors.drawerBackgroundColor
    val beatColor = appColors.selectionBorderColor
    val pass1Color: Color
    val pass2Color: Color
    val radarColor: Color
    if(percentageDialogData.value.dialogState){
        val padding = 10
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier
                    .width(dimensions.dialogWidth)
                    .height(dimensions.dialogHeight),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(padding.dp)) {
                    Spacer(modifier = Modifier.height(10.dp))


                    var customColor by remember{ mutableStateOf(Color.Black) }
                    val context = percentageDialogData.value.model.getContext()
                    if(percentageDialogData.value.firstRendering){

                        //indexColors = G.indexColorArray
                        percentage = percentageDialogData.value.percentageValue
                        onRefreshRendering.invoke(false)
                    } else {
                        //percentage = percentageDialogData.value.percentageValue

                        //indexColors = G.indexColorArray

                    }
                    val h = dimensions.dialogHeight / 7//80.dp
                    var size by remember { mutableStateOf(IntSize.Zero) }
                    //val w = if (size.width == 0) listOf(0,0,0,0,0,0) else
                       // ((size.width - padding) / dimensions.dpDensity + (size.width - padding) % dimensions.dpDensity ).toLong().divideDistributingRest(6).map{it.toInt()}
                    val w = if (size.width == 0) 0 else size.width
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(h)
                            .onGloballyPositioned { coordinates ->
                                size = coordinates.size
                            }
                            .background(back2Color),
                    ){

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(h), horizontalArrangement = Arrangement.Center) {

                        }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(h),
                            horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                            Text( //text = String.format("%.0f%%",percentage*100),
                                text = "%,.1f".format(Locale.ENGLISH,percentage*100),
                                style = TextStyle(fontSize = (dimensions.selectorClipFontSize * 2).sp, fontWeight = FontWeight.ExtraBold,
                                    color = fontColor

                                ) )
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

                        PercentageSelector(
                            width = w,
                            height = dimensions.dialogHeight / 6 * 4,
                            startColor = back1Color.copy(),
                            startPercentage = percentage,
                            refresh = percentageDialogData.value.isRefreshing
                        ) { percentageFloat ->
                                percentage = percentageFloat
                                if(percentageDialogData.value.isRefreshing){
                                    percentage = percentageFloat
                                    onStopRefresh.invoke()
                                } else {

                                }

                        }

                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .height(h),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically ){
                        val fontSize = dimensions.dialogFontSize
                        Button(
                            onClick = {
                                percentageDialogData.value.onSubmitButtonClick.invoke(percentage)
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
                                    val newPercentage = (percentage - 0.001f).coerceIn(0f, 1f)
                                    percentage = newPercentage
                                    onSetPercentageAndRefresh.invoke(newPercentage)
                                },
                                shape = MaterialTheme.shapes.large
                            ) {
                                Text(text = "-", fontSize = fontSize.sp)
                            }
                            Button(
                                onClick = {
                                    val newPercentage = (percentage + 0.001f).coerceIn(0f, 1f)
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




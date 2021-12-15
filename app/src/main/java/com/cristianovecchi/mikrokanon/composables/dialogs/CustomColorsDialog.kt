package com.cristianovecchi.mikrokanon.composables.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cristianovecchi.mikrokanon.G
import com.cristianovecchi.mikrokanon.composables.ColorSelector
import com.cristianovecchi.mikrokanon.composables.CustomColorsDialogData
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.cristianovecchi.mikrokanon.toDp
import com.cristianovecchi.mikrokanon.ui.Dimensions

@Composable
fun CustomColorsDialog(customColorsDialogData: MutableState<CustomColorsDialogData>,
                       dimensions: Dimensions, okText: String = "OK",
                       onRefreshRendering: (Boolean) -> Unit =
                           {customColorsDialogData.value = customColorsDialogData.value.copy(firstRendering = it, isRefreshing = true)},
                       onStopRefresh: () -> Unit =
                           {customColorsDialogData.value = customColorsDialogData.value.copy(isRefreshing = false)},
                       onSetArrayIndexAndRefresh: (Int) -> Unit =
                           {customColorsDialogData.value = customColorsDialogData.value.copy(arrayColorIndex = it, firstRendering = true)},
                       onDismissRequest: () -> Unit =
                           {G.deleteColorArrays(); customColorsDialogData.value = CustomColorsDialogData(model = customColorsDialogData.value.model) })
{

    var indexColors = customColorsDialogData.value.arrayColorIndex

    if(customColorsDialogData.value.dialogState){
        val padding = 10
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier.width(dimensions.dialogWidth).height(dimensions.dialogHeight),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(padding.dp)) {
                    Spacer(modifier = Modifier.height(10.dp))

                    val fontColor: Color
                    val back1Color: Color
                    val back2Color: Color
                    val beatColor: Color
                    val pass1Color: Color
                    val pass2Color: Color
                    val radarColor: Color
                    val arraySize: Int
                    var customColor by remember{ mutableStateOf(Color.Black) }
                    val context = customColorsDialogData.value.model.getContext()
                    if(customColorsDialogData.value.firstRendering){
                        if(G.loadColorArrays(context)) G.setColorArray(context,indexColors)
                        fontColor = Color(G.colorFont)
                        back1Color = Color(G.colorBackground1)
                        back2Color = Color(G.colorBackground2)
                        beatColor = Color(G.colorBeatNotes)
                        pass1Color = Color(G.colorPassageNotes1)
                        pass2Color = Color(G.colorPassageNotes2)
                        radarColor = Color(G.colorRadar)
                        arraySize = G.getArraySize()
                        indexColors = G.indexColorArray
                        onRefreshRendering.invoke(false)
                    } else {
                        if(G.loadColorArrays(context))
                            G.setColorArrayBySearchFromIndex(context, customColor.toArgb(), indexColors)
                        fontColor = Color(G.colorFont)
                        back1Color = Color(G.colorBackground1)
                        back2Color = Color(G.colorBackground2)
                        beatColor = Color(G.colorBeatNotes)
                        pass1Color = Color(G.colorPassageNotes1)
                        pass2Color = Color(G.colorPassageNotes2)
                        radarColor = Color(G.colorRadar)
                        arraySize = G.getArraySize()
                        indexColors = G.indexColorArray
                    }
                    val h = dimensions.dialogHeight / 7//80.dp
                    var size by remember { mutableStateOf(IntSize.Zero) }
                    val w = if (size.width == 0) listOf(0,0,0,0,0,0) else
                            ((size.width - padding) / dimensions.dpDensity + (size.width - padding) % dimensions.dpDensity + 1 ).toLong().divideDistributingRest(6).map{it.toInt()}
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(h)
                            .onGloballyPositioned { coordinates ->
                                size = coordinates.size
                            }){

                        Row(
                            Modifier
                                .height(h)
                                .background(customColor), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Box(
                                Modifier
                                    .size(w[0].dp, h)
                                    .background(radarColor))
                            Box(
                                Modifier
                                    .size(w[1].dp, h)
                                    .background(beatColor))
                            Box(
                                Modifier
                                    .size(w[2].dp, h)
                                    .background(back1Color))
                            Box(
                                Modifier
                                    .size(w[3].dp, h)
                                    .background(back2Color))
                            Box(
                                Modifier
                                    .size(w[4].dp, h)
                                    .background(pass1Color)
                                    .clickable {
                                        if (indexColors > 0) onSetArrayIndexAndRefresh.invoke(indexColors-1)
                                    })
                            Box(
                                Modifier
                                    .size(w[5].dp, h)
                                    .background(pass2Color)
                                    .clickable {
                                        if (indexColors < arraySize ) onSetArrayIndexAndRefresh.invoke(indexColors+1)
                                    })
                        }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(h),
                            horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                            Text( text = "$indexColors",
                                style = TextStyle(fontSize = (dimensions.selectorClipFontSize * 2).sp, fontWeight = FontWeight.ExtraBold, color = fontColor) )
                        }


                    }

                    Row(Modifier.fillMaxWidth()) {

                            ColorSelector(
                                height = dimensions.dialogHeight / 6 * 4,
                               startColor = back1Color.copy(),
                                refresh = customColorsDialogData.value.isRefreshing
                            ) { color ->
                                if(customColorsDialogData.value.isRefreshing){
                                    customColor = color.copy()

                                    onStopRefresh.invoke()
                                } else {
                                    customColor = color.copy()
                                }

                           }

                  }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth().height(h),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically ){
                        val fontSize = dimensions.dialogFontSize
                        Button(
                            onClick = {
                                customColorsDialogData.value.onSubmitButtonClick.invoke(indexColors)
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
                                    if (indexColors > 0) onSetArrayIndexAndRefresh.invoke(indexColors-1)
                                },
                                shape = MaterialTheme.shapes.large
                            ) {
                                Text(text = "-", fontSize = fontSize.sp)
                            }
                            Button(
                                onClick = {
                                    if (indexColors < arraySize ) onSetArrayIndexAndRefresh.invoke(indexColors+1)
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




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
import com.cristianovecchi.mikrokanon.toDp

@Composable
fun CustomColorsDialog(customColorsDialogData: MutableState<CustomColorsDialogData>, okText: String = "OK",
                       onDismissRequest: () -> Unit = { G.deleteColorArrays(); customColorsDialogData.value = CustomColorsDialogData(model = customColorsDialogData.value.model) })
{
    if(customColorsDialogData.value.dialogState){
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier.width(300.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Spacer(modifier = Modifier.height(10.dp))
                    var firstRendering by remember{ mutableStateOf(true) }
                    val fontColor: Color
                    val back1Color: Color
                    val back2Color: Color
                    val beatColor: Color
                    val pass1Color: Color
                    val pass2Color: Color
                    val radarColor: Color
                    var indexColors = customColorsDialogData.value.arrayColorIndex
                    var customColor by remember{ mutableStateOf(Color.Black) }
                    val context = customColorsDialogData.value.model.getContext()
                    if(firstRendering){
                        if(G.loadColorArrays(context)) G.setColorArray(context,indexColors)
                        fontColor = Color(G.colorFont)
                        back1Color = Color(G.colorBackground1)
                        back2Color = Color(G.colorBackground2)
                        beatColor = Color(G.colorBeatNotes)
                        pass1Color = Color(G.colorPassageNotes1)
                        pass2Color = Color(G.colorPassageNotes2)
                        radarColor = Color(G.colorRadar)
                        indexColors = G.indexColorArray
                        firstRendering = false
                    } else {
                        if(G.loadColorArrays(context))
                            G.setColorArrayBySearch(context, customColor.toArgb())
                        fontColor = Color(G.colorFont)
                        back1Color = Color(G.colorBackground1)
                        back2Color = Color(G.colorBackground2)
                        beatColor = Color(G.colorBeatNotes)
                        pass1Color = Color(G.colorPassageNotes1)
                        pass2Color = Color(G.colorPassageNotes2)
                        radarColor = Color(G.colorRadar)
                        indexColors = G.indexColorArray
                    }
                    val h = 80.dp
                    var size by remember { mutableStateOf(IntSize.Zero) }
                    val w = if (size.width == 0) 0.dp else (size.width / 6).toDp().dp + 1.dp
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
                                    .size(w, h)
                                    .background(radarColor))
                            Box(
                                Modifier
                                    .size(w, h)
                                    .background(beatColor))
                            Box(
                                Modifier
                                    .size(w, h)
                                    .background(back1Color))
                            Box(
                                Modifier
                                    .size(w, h)
                                    .background(back2Color))
                            Box(
                                Modifier
                                    .size(w, h)
                                    .background(pass1Color))
                            Box(
                                Modifier
                                    .size(w, h)
                                    .background(pass2Color))
                        }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(h),
                            horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                            Text( text = "$indexColors",
                                style = TextStyle(fontSize = 35.sp, fontWeight = FontWeight.ExtraBold, color = fontColor) )
                        }


                    }

                    Row(Modifier.fillMaxWidth()) {
                        ColorSelector(height = 300.dp, startColor = back1Color) { color ->
                            customColor =  color.copy()
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            customColorsDialogData.value.onSubmitButtonClick.invoke(indexColors)
                            onDismissRequest.invoke()
                        },
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(text = okText)
                    }
                }
            }
        }
    }
}




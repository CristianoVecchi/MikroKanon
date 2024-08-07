package com.cristianovecchi.mikrokanon.composables.dialogs


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.composables.CustomButton
import com.cristianovecchi.mikrokanon.extractIntsFromCsv
import com.cristianovecchi.mikrokanon.getNoteAndRestSymbols
import com.cristianovecchi.mikrokanon.ui.Dimensions


@Composable
fun CadenzaDialog(
    multiNumberDialogData: MutableState<MultiNumberDialogData>,
    parentDialogData: MutableState<ButtonsDialogData>, // is necessary a reference for the parent dialog
    dimensions: Dimensions,
    model: AppViewModel,
    onDismissRequest: () -> Unit = {
        multiNumberDialogData.value =
            MultiNumberDialogData(
                model = multiNumberDialogData.value.model,
                value = multiNumberDialogData.value.value
            )
        parentDialogData.value = ButtonsDialogData(model = model)

    }
) {

    if (multiNumberDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            val fontColor = model.appColors.dialogFontColor
            val backgroundColor = model.appColors.dialogBackgroundColor
            //val model = multiNumberDialogData.value.model
            val width = if(dimensions.width <= 884) (dimensions.width / dimensions.dpDensity).toInt().dp
            else dimensions.dialogWidth
            Surface(
                modifier = Modifier.width(width).height(dimensions.dialogHeight / 2),
                color = backgroundColor,
                shape = RoundedCornerShape(10.dp)
            ) {

                Column(modifier = Modifier.padding(10.dp)) {
                    val modifierA = Modifier
                        //.fillMaxSize()
                        .padding(8.dp)
                        .weight(3f)
                    val modifierB = Modifier
                        //.fillMaxSize()
                        .weight(1f)
                    val columnModifier = Modifier.weight(1f)
                    var cadenzaText by remember { mutableStateOf(multiNumberDialogData.value.value) }
                    val setCadenza = { index: Int, cadenzaValue: Int ->
                        val newCadenza = cadenzaValue.coerceIn(
                            multiNumberDialogData.value.min,
                            multiNumberDialogData.value.max
                        )
                        val cadenzaValues = cadenzaText.extractIntsFromCsv().toMutableList()
                        cadenzaValues[index] = newCadenza
                        cadenzaText = cadenzaValues.joinToString(",")
                    }
                    val fontSize = dimensions.dialogFontSize.sp//20.sp // buttons plus & minus
                    //val textSize = 24.sp
                    val fontWeight = FontWeight.Normal
                    val numberFontWeight = FontWeight.Bold
                    val buttonPadding = 4.dp
                    Text(text = multiNumberDialogData.value.title, fontWeight = FontWeight.Bold, color = fontColor, style = TextStyle(fontSize = fontSize))
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = modifierA.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {

                        //val colors = model.appColors
                        //val intervalPadding = 4.dp
                        //val innerPadding = 10.dp
                        val cadenzaValues = cadenzaText.extractIntsFromCsv()
                        val (noteSymbol, restSymbol) = getNoteAndRestSymbols()

                        Column( modifier = columnModifier,
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment= Alignment.CenterHorizontally
                        ){
                            Button(modifier = Modifier// PLUS BUTTON
                                .padding(buttonPadding), onClick = { setCadenza(0, cadenzaValues[0] +1) })
                            {
                                Text(
                                    text = "+",
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        fontWeight = fontWeight
                                    ),
                                    color = fontColor
                                )
                            }


                            Text(text = cadenzaValues[0].toString() + restSymbol, style = TextStyle(
                                fontSize = fontSize,
                                fontWeight = numberFontWeight,
                                color = fontColor
                            ))


                            Button(modifier = Modifier// MINUS BUTTON
                                .padding(buttonPadding), onClick = { setCadenza(0, cadenzaValues[0] -1) })
                            {
                                Text(
                                    text = "-",
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        fontWeight = fontWeight
                                    ),
                                    color = fontColor
                                )
                            }
                        }
                        Column(modifier = columnModifier,
                            verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment= Alignment.CenterHorizontally){
                            Button(modifier = Modifier// PLUS BUTTON
                                .padding(buttonPadding), onClick = { setCadenza(1, cadenzaValues[1] +1) })
                            {
                                Text(
                                    text = "+",
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        fontWeight = fontWeight
                                    ),
                                    color = fontColor
                                )
                            }
                            Text(text = cadenzaValues[1].toString() + noteSymbol, color = fontColor,
                                style = TextStyle(
                                fontSize = fontSize,
                                fontWeight = numberFontWeight))
                            Button(modifier = Modifier// MINUS BUTTON
                                .padding(buttonPadding), onClick = { setCadenza(1, cadenzaValues[1] -1) })
                            {
                                Text(
                                    text = "-",
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        fontWeight = fontWeight
                                    ),
                                    color = fontColor
                                )
                            }
                        }
                        Column(modifier = columnModifier,
                            verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment= Alignment.CenterHorizontally){
                            Button(modifier = Modifier// PLUS BUTTON
                                .padding(buttonPadding), onClick = { setCadenza(2, cadenzaValues[2] +1) })
                            {
                                Text(
                                    text = "+",
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        fontWeight = fontWeight
                                    )
                                ,color = fontColor
                                )
                            }
                            Text(text = cadenzaValues[2].toString() + restSymbol, color = fontColor,
                                style = TextStyle(
                                fontSize = fontSize,
                                fontWeight = numberFontWeight))
                            Button(modifier = Modifier// MINUS BUTTON
                                .padding(buttonPadding), onClick = { setCadenza(2, cadenzaValues[2] -1) })
                            {
                                Text(
                                    text = "-",
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        fontWeight = fontWeight
                                    ),
                                    color = fontColor
                                )
                            }
                        }
                        Column(modifier = columnModifier,
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment= Alignment.CenterHorizontally){
                            Button(modifier = Modifier// PLUS BUTTON
                                .padding(buttonPadding), onClick = { setCadenza(3, cadenzaValues[3] +1) })
                            {
                                Text(
                                    text = "+",
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        fontWeight = fontWeight
                                    ),
                                    color = fontColor
                                )
                            }
                            Text(text = cadenzaValues[3].toString() + noteSymbol, color = fontColor,
                                style = TextStyle(
                                fontSize = fontSize,
                                fontWeight = numberFontWeight))
                            Button(modifier = Modifier// MINUS BUTTON
                                .padding(buttonPadding), onClick = { setCadenza(3, cadenzaValues[3] -1) })
                            {
                                Text(
                                    text = "-",
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        fontWeight = fontWeight
                                    ),
                                    color = fontColor
                                )
                            }
                        }
                        Column(modifier = columnModifier,
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment= Alignment.CenterHorizontally){
                            Button(modifier = Modifier// PLUS BUTTON
                                .padding(buttonPadding), onClick = { setCadenza(4, cadenzaValues[4] +1) })
                            {
                                Text(
                                    text = "+",
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        fontWeight = fontWeight
                                    ),
                                    color = fontColor
                                )
                            }
                            Text(text = cadenzaValues[4].toString() + restSymbol, color = fontColor,
                                style = TextStyle(
                                fontSize = fontSize,
                                fontWeight = numberFontWeight))
                            Button(modifier = Modifier// MINUS BUTTON
                                .padding(buttonPadding), onClick = { setCadenza(4, cadenzaValues[4] -1) })
                            {
                                Text(
                                    text = "-",
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        fontWeight = fontWeight
                                    ),
                                    color = fontColor
                                )
                            }
                        }


                    } // END ROW


                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifierB.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val buttonDimensions by model.dimensions.asFlow().collectAsState(initial = model.dimensions.value!!)
                            val buttonSize = buttonDimensions.inputButtonSize - 10.dp
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "",
                                iconId = model.iconMap["done"]!!,
                                buttonSize = buttonSize,
                                iconColor = Color.Green,
                                colors = model.appColors
                            ) {
                                multiNumberDialogData.value.dispatchCsv.invoke(cadenzaText)
                                onDismissRequest.invoke()
                            }

                        }

                    }
                }
            }
        }
    }






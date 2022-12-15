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
import com.cristianovecchi.mikrokanon.locale.getNoteAndRestSymbols
import com.cristianovecchi.mikrokanon.ui.Dimensions


@Composable
fun FormatDialog(
    multiNumberDialogData: MutableState<MultiNumberDialogData>,
    parentDialogData: MutableState<ButtonsDialogData>, // is necessary a reference for the parent dialog
    dimensions: Dimensions,
    okText: String = "OK",
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
            val (noteSymbol, restSymbol) = getNoteAndRestSymbols()
            val counterpointSize = multiNumberDialogData.value.max
            val model = multiNumberDialogData.value.model
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
                    var columnModifier = Modifier.weight(1f)
                    var formatText by remember { mutableStateOf(multiNumberDialogData.value.value) }
                    val setFormat = { index: Int, formatValue: Int ->
                        val newCadenza = formatValue.coerceIn(
                            0,
                            Int.MAX_VALUE
                        )
                        val cadenzaValues = formatText.extractIntsFromCsv().toMutableList()
                        cadenzaValues[index] = newCadenza
                        formatText = cadenzaValues.joinToString(",")
                    }
                    val formatValues = formatText.extractIntsFromCsv().toMutableList()
                    val resultSize = if(counterpointSize>1) formatValues[0]+formatValues[1]+counterpointSize+formatValues[2]+formatValues[3]
                                    else formatValues[0]+formatValues[1]+counterpointSize+formatValues[3]
                    val result = if(counterpointSize>1) "${formatValues[0]}$restSymbol + ${formatValues[1]}$noteSymbol + $counterpointSize$noteSymbol + ${formatValues[2]}$noteSymbol + ${formatValues[3]}$restSymbol"
                                else "${formatValues[0]}$restSymbol + ${formatValues[1]}$noteSymbol + $counterpointSize$noteSymbol + ${formatValues[3]}$restSymbol"
                    val fontSize = dimensions.dialogFontSize.sp//20.sp // buttons plus & minus
                    val fontSizeInt = dimensions.dialogFontSize

                    val textSize = 24.sp
                    val fontWeight = FontWeight.Normal
                    val numberFontWeight = FontWeight.Bold
                    val buttonPadding = 4.dp
                    Text(text = multiNumberDialogData.value.title, fontWeight = FontWeight.Bold, color = fontColor, style = TextStyle(fontSize = fontSize))
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically){
                        Text(text = result, fontWeight = FontWeight.ExtraBold, color = fontColor, style = TextStyle(fontSize = (fontSizeInt + 2).sp))
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically){
                        Text(text = "= $resultSize$noteSymbol", fontWeight = FontWeight.ExtraBold, color = fontColor, style = TextStyle(fontSize = (fontSizeInt + 8).sp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = modifierA.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {

                        val colors = model.appColors
                        val intervalPadding = 4.dp
                        val innerPadding = 10.dp


                        Column( modifier = columnModifier,
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment= Alignment.CenterHorizontally
                        ){
                            Button(modifier = Modifier// PLUS BUTTON
                                .padding(buttonPadding), onClick = { setFormat(0, formatValues[0] +1) })
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


                            Text(text = formatValues[0].toString() + restSymbol, style = TextStyle(
                                fontSize = fontSize,
                                fontWeight = numberFontWeight,
                                color = fontColor
                            ))


                            Button(modifier = Modifier// MINUS BUTTON
                                .padding(buttonPadding), onClick = { setFormat(0, formatValues[0] -1) })
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
                                .padding(buttonPadding), onClick = { setFormat(1, formatValues[1] +1) })
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
                            Text(text = formatValues[1].toString() + noteSymbol, color = fontColor,
                                style = TextStyle(
                                    fontSize = fontSize,
                                    fontWeight = numberFontWeight))
                            Button(modifier = Modifier// MINUS BUTTON
                                .padding(buttonPadding), onClick = { setFormat(1, formatValues[1] -1) })
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
                        Spacer(modifier = Modifier.width(15.dp))
                        if(counterpointSize > 1){
                            Column(modifier = columnModifier,
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment= Alignment.CenterHorizontally){
                                Button(modifier = Modifier// PLUS BUTTON
                                    .padding(buttonPadding), onClick = { setFormat(2, formatValues[2] +1) })
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
                                Text(text = formatValues[2].toString() + noteSymbol, color = fontColor,
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        fontWeight = numberFontWeight))
                                Button(modifier = Modifier// MINUS BUTTON
                                    .padding(buttonPadding), onClick = { setFormat(2, formatValues[2] -1) })
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
                        }

                        Column(modifier = columnModifier,
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment= Alignment.CenterHorizontally){
                            Button(modifier = Modifier// PLUS BUTTON
                                .padding(buttonPadding), onClick = { setFormat(3, formatValues[3] +1) })
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
                            Text(text = formatValues[3].toString() + restSymbol, color = fontColor,
                                style = TextStyle(
                                    fontSize = fontSize,
                                    fontWeight = numberFontWeight))
                            Button(modifier = Modifier// MINUS BUTTON
                                .padding(buttonPadding), onClick = { setFormat(3, formatValues[3] -1) })
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
                        val dimensions by model.dimensions.asFlow().collectAsState(initial = model.dimensions.value!!)
                        val buttonSize = dimensions.inputButtonSize - 10.dp
                        CustomButton(
                            adaptSizeToIconButton = true,
                            text = "",
                            iconId = model.iconMap["done"]!!,
                            buttonSize = buttonSize,
                            iconColor = Color.Green,
                            colors = model.appColors
                        ) {
                            multiNumberDialogData.value.dispatchCsv.invoke(formatText)
                            onDismissRequest.invoke()
                        }

                    }

                }
            }
        }
    }
}








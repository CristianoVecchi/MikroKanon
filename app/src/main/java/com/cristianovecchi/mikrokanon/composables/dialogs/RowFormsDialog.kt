package com.cristianovecchi.mikrokanon.composables.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.*
import com.cristianovecchi.mikrokanon.composables.CustomButton
import com.cristianovecchi.mikrokanon.locale.rowFormsMap
import com.cristianovecchi.mikrokanon.ui.Dimensions
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun RowFormsDialog(multiNumberDialogData: MutableState<MultiNumberDialogData>,
                   dimensions: Dimensions, forms: List<String>, numbers: List<String>,
                   onDismissRequest: () -> Unit = { multiNumberDialogData.value = MultiNumberDialogData(model = multiNumberDialogData.value.model, value = multiNumberDialogData.value.value) })
{

    if (multiNumberDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            val model = multiNumberDialogData.value.model
            val appColors = model.appColors
            val fontColor = appColors.dialogFontColor
            val backgroundColor = appColors.dialogBackgroundColor

            val playing by model.playing.asFlow().collectAsState(initial = false)
            val width = if(dimensions.width <=884) (dimensions.width / 10 * 8 / dimensions.dpDensity).toInt().dp
            else dimensions.dialogWidth
            //val height = if(dimensions.height < 1280) (dimensions.height / dimensions.dpDensity).toInt().dp
            val height = (dimensions.height / dimensions.dpDensity).toInt().dp
            Surface(
                modifier = Modifier.width(width).height(height),
                color = backgroundColor,
                shape = RoundedCornerShape(10.dp)
            ) {

                Column(modifier = Modifier.height((dimensions.height / 6 * 5).dp)
                    .padding(10.dp)) {
                    val weights = dimensions.fullDialogWeights
                    val modifierA = Modifier
                        //.fillMaxSize()
                        .padding(8.dp)
                        .weight(weights.first)
                    val modifierB = Modifier
                        //.fillMaxSize()
                        .weight(weights.second)
                    val modifierC = Modifier
                        //.fillMaxSize()
                        .padding(8.dp)
                        .weight(weights.third)
                    var formsText by remember { mutableStateOf(multiNumberDialogData.value.value) }
                    var cursor by remember { mutableStateOf(0) }
                    val setForms = { index: Int, newForm: Int ->
                        val pairs = formsText.extractIntPairsFromCsv().toMutableList()
                        pairs[index] = Pair(pairs[index].first, newForm)
                        formsText = pairs.toIntPairsString()
                    }
                    val fontSize = dimensions.dialogFontSize
                    val fontWeight = FontWeight.Normal
                    val buttonPadding = 4.dp
                    Column(modifier = modifierA) {
                        Text(text = multiNumberDialogData.value.title, color = fontColor)
                        Spacer(modifier = Modifier.height(5.dp))

                        val colors = model.appColors
                        val listState = rememberLazyListState()
                        val coroutineScope = rememberCoroutineScope()
                        val selectionBackColor = colors.selCardBackColorSelected
                        val selectionTextColor = colors.selCardTextColorSelected
                        val selectionBorderColor = colors.selCardBorderColorSelected
                        val unselectionBackColor = colors.selCardBackColorUnselected
                        val unselectionTextColor = colors.selCardTextColorUnselected
                        val unselectionBorderColor = colors.selCardBorderColorUnselected
                        val intervalPadding = 2.dp
                        val innerPadding = 8.dp
                        val formPairs = formsText.extractIntPairsFromCsv()
                        val nCols = 4
                        val nRows = (formPairs.size / nCols) + 1
                        val rows = (0 until nRows).toList()
                        LazyColumn(state = listState) {
                            items(rows) { row ->
                                var index = row * nCols
                                //Text(text = "ROW #$row")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    for (j in 0 until nCols) {
                                        if (index != formPairs.size) {
                                            val text = formPairs[index].describeSingleRowForm(rowFormsMap, numbers)
                                            val id = index
                                            Card(
                                                modifier = Modifier
                                                    .background(backgroundColor)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .padding(intervalPadding)
                                                    .clickable { cursor = id },
                                                backgroundColor = if (cursor == index) selectionBackColor else unselectionBackColor,
                                                contentColor = if (cursor == index) selectionTextColor else unselectionTextColor,
                                                border = BorderStroke(
                                                    2.dp,
                                                    if (cursor == index) selectionBorderColor else unselectionBorderColor
                                                ),
                                                elevation = if (cursor == index) 4.dp else 4.dp
                                            )
                                            {
                                                Text(
                                                    text = text,
                                                    modifier = Modifier.padding(innerPadding),
                                                    style = TextStyle(fontSize = fontSize.sp),
                                                    fontWeight = if (cursor == index) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                            index++
                                        }
                                    }
                                }
                            }
                            if (cursor > -1) coroutineScope.launch {
                                val rowIndex = if (formPairs.size <= nCols) 1 else cursor / nCols
                                listState.animateScrollToItem(rowIndex)
                            }
                        }
                    }
                    Column(modifier = modifierB, verticalArrangement = Arrangement.Bottom) {
                        val textFontSize = forms.map{if(it.length>22) fontSize / 3 * 2  else fontSize}
                        val textButtonPadding = 1.dp
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // ROW FORM BUTTONS
                            Column(
                                modifier = Modifier.width(IntrinsicSize.Max),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(textButtonPadding), onClick = {
                                    val pair = formsText.extractIntPairsFromCsv()[cursor]
                                    val barSign = pair.second.sign()
                                    setForms(cursor, 1 * barSign) })
                                {
                                    Text(
                                        text = forms[1],
                                        style = TextStyle(
                                            fontSize = textFontSize[1].sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(textButtonPadding),
                                    onClick = {
                                        val pair = formsText.extractIntPairsFromCsv()[cursor]
                                        val barSign = pair.second.sign()
                                        setForms(cursor, 3 * barSign) })
                                {
                                    Text(
                                        text = forms[3],
                                        style = TextStyle(
                                            fontSize = textFontSize[3].sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(textButtonPadding),
                                    onClick = {
                                        val pair = formsText.extractIntPairsFromCsv()[cursor]
                                        val barSign = pair.second.sign()
                                        setForms(cursor, 2 * barSign) })
                                {
                                    Text(
                                        text = forms[2],
                                        style = TextStyle(
                                            fontSize = textFontSize[2].sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(textButtonPadding),
                                    onClick = {
                                        val pair = formsText.extractIntPairsFromCsv()[cursor]
                                        val barSign = pair.second.sign()
                                        setForms(cursor, 4 * barSign) })
                                {
                                    Text(
                                        text = forms[4],
                                        style = TextStyle(
                                            fontSize = textFontSize[4].sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }

                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        val buttonSize = dimensions.dialogButtonSize
                        val numberFontSize = dimensions.dialogFontSize
                        LazyColumn{
                            items((0..3).toList()) { row ->
                                val step = row * 4
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    CustomButton(
                                        adaptSizeToIconButton = true,
                                        text = numbers[step+0],
                                        fontSize = numberFontSize,
                                        buttonSize = buttonSize.dp,
                                        iconColor = model.appColors.iconButtonIconColor,
                                        colors = model.appColors
                                    ) {
                                        val pairs = formsText.extractIntPairsFromCsv().toMutableList()
                                        val (counterpoint, form) = pairs[cursor]
                                        val tritoneSign = counterpoint.sign()
                                        pairs[cursor] = Pair( if(counterpoint.absoluteValue == step+2) 1 * tritoneSign else (step+2) * tritoneSign, form)
                                        formsText = pairs.toIntPairsString()
                                    }
                                    CustomButton(
                                        adaptSizeToIconButton = true,
                                        text = numbers[step+1],
                                        fontSize = numberFontSize,
                                        buttonSize = buttonSize.dp,
                                        iconColor = model.appColors.iconButtonIconColor,
                                        colors = model.appColors
                                    ) {
                                        val pairs = formsText.extractIntPairsFromCsv().toMutableList()
                                        val (counterpoint, form) = pairs[cursor]
                                        val tritoneSign = counterpoint.sign()
                                        pairs[cursor] = Pair( if(counterpoint.absoluteValue == step+3) 1 * tritoneSign else (step+3) * tritoneSign, form)
                                        formsText = pairs.toIntPairsString()
                                    }
                                    CustomButton(
                                        adaptSizeToIconButton = true,
                                        text = numbers[step+2],
                                        fontSize = numberFontSize,
                                        buttonSize = buttonSize.dp,
                                        iconColor = model.appColors.iconButtonIconColor,
                                        colors = model.appColors
                                    ) {
                                        val pairs = formsText.extractIntPairsFromCsv().toMutableList()
                                        val (counterpoint, form) = pairs[cursor]
                                        val tritoneSign = counterpoint.sign()
                                        pairs[cursor] = Pair( if(counterpoint.absoluteValue == step+4) 1 * tritoneSign else (step+4) * tritoneSign, form)
                                        formsText = pairs.toIntPairsString()
                                    }
                                    CustomButton(
                                        adaptSizeToIconButton = true,
                                        text = numbers[step+3],
                                        fontSize = numberFontSize,
                                        buttonSize = buttonSize.dp,
                                        iconColor = model.appColors.iconButtonIconColor,
                                        colors = model.appColors
                                    ) {
                                        val pairs = formsText.extractIntPairsFromCsv().toMutableList()
                                        val (counterpoint, form) = pairs[cursor]
                                        val tritoneSign = counterpoint.sign()
                                        pairs[cursor] = Pair( if(counterpoint.absoluteValue == step+5) 1 * tritoneSign else (step+5) * tritoneSign, form)
                                        formsText = pairs.toIntPairsString()
                                    }

                                }
                            }
                        }
                    }
                    Row(
                        modifier = modifierC.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val buttonSize = dimensions.dialogButtonSize / 4 * 3
                        CustomButton(
                            adaptSizeToIconButton = true,
                            text = "",
                            iconId = model.iconMap["done"]!!,
                            fontSize = fontSize,
                            buttonSize = buttonSize.dp,
                            iconColor = Color.Green,
                            colors = model.appColors
                        ) {
                            multiNumberDialogData.value.onSubmitButtonClick.invoke(formsText)
                            onDismissRequest.invoke()
                        }
                        CustomButton(
                            adaptSizeToIconButton = true,
                            text = "",
                            iconId = model.iconMap["bar"]!!,
                            fontSize = fontSize,
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            val pairs = formsText.extractIntPairsFromCsv().toMutableList()
                            val (counterpoint, form) = pairs[cursor]
                            pairs[cursor] = Pair( counterpoint, form * -1)
                            formsText = pairs.toIntPairsString()
                        }
                        CustomButton(
                            adaptSizeToIconButton = true,
                            text = "",
                            iconId = model.iconMap["delete"]!!,
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            val pairs = formsText.extractIntPairsFromCsv().toMutableList()
                            if(pairs.size > 1){
                                pairs.removeAt(cursor)
                                formsText = pairs.toIntPairsString()
                                val newCursor = if (pairs.size > 1) cursor - 1 else 0
                                cursor = if (newCursor < 0) 0 else newCursor
                            }
                        }
                        CustomButton(
                            adaptSizeToIconButton = true,
                            text = "",
                            iconId = model.iconMap["add"]!!,
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            var values = formsText.extractIntPairsFromCsv()
                            val selectedPair = values[cursor].copy()
                            val rebuilding = values.addOrInsert(selectedPair, cursor)
                            values = rebuilding.first
                            cursor = rebuilding.second
                            formsText = values.toMutableList().toIntPairsString()
                        }
                        CustomButton(
                            adaptSizeToIconButton = true,
                            text = "",
                            iconId = model.iconMap["tritone_substitution"]!!,
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            val pairs = formsText.extractIntPairsFromCsv().toMutableList()
                            val (counterpoint, form) = pairs[cursor]
                            pairs[cursor] = Pair( counterpoint * -1 ,form )
                            formsText = pairs.toIntPairsString()
                        }
                        if (!playing) {
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "",
                                iconId = model.iconMap["play"]!!,
                                buttonSize = buttonSize.dp,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val pairs = formsText.extractIntPairsFromCsv().toMutableList()
                                val (counterpoint, form) = pairs[cursor]
                                model.onPlayExample(counterpoint, form)
                            }
                        } else {
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "",
                                iconId = model.iconMap["stop"]!!,
                                fontSize = fontSize,
                                buttonSize = buttonSize.dp,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                model.onStop()
                            }
                        }
                    }

                }
            }
        }
    }
}




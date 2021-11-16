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
import com.cristianovecchi.mikrokanon.composables.CustomButton
import com.cristianovecchi.mikrokanon.composables.MultiNumberDialogData
import com.cristianovecchi.mikrokanon.describeSingleBpm
import com.cristianovecchi.mikrokanon.extractIntPairsFromCsv
import com.cristianovecchi.mikrokanon.extractIntsFromCsv
import com.cristianovecchi.mikrokanon.toIntPairsString
import com.cristianovecchi.mikrokanon.locale.rowFormsMap
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun RowFormsDialog(multiNumberDialogData: MutableState<MultiNumberDialogData>, forms: List<String>, okText: String = "OK",
                   onDismissRequest: () -> Unit = { multiNumberDialogData.value = MultiNumberDialogData(model = multiNumberDialogData.value.model, value = multiNumberDialogData.value.value) })
{

    if (multiNumberDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            val model = multiNumberDialogData.value.model

            val playing by model.playing.asFlow().collectAsState(initial = false)

            Surface(
                modifier = Modifier.width(350.dp).height(650.dp),
                shape = RoundedCornerShape(10.dp)
            ) {

                Column(modifier = Modifier.padding(10.dp)) {
                    val modifierA = Modifier
                        //.fillMaxSize()
                        .padding(8.dp)
                        .weight(5f)
                    val modifierB = Modifier
                        //.fillMaxSize()
                        .weight(9f)
                    var formsText by remember { mutableStateOf(multiNumberDialogData.value.value) }
                    var cursor by remember { mutableStateOf(0) }
                    val setForms = { index: Int, newForm: Int ->
                        val pairs = formsText.extractIntPairsFromCsv().toMutableList()
                        pairs[index] = Pair(pairs[index].first, newForm)
                        formsText = pairs.toIntPairsString()
                    }
                    val fontSize = 16.sp
                    val fontWeight = FontWeight.Normal
                    val buttonPadding = 4.dp
                    Column(modifier = modifierA) {
                        Text(text = multiNumberDialogData.value.title)
                        Spacer(modifier = Modifier.height(10.dp))

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
                                            val text = formPairs[index].describeSingleBpm(rowFormsMap)
                                            val id = index
                                            Card(
                                                modifier = Modifier
                                                    .background(Color.White)
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
                                                    style = TextStyle(fontSize = if (cursor == index) fontSize else fontSize),
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
                        val textFontSize = if(forms.maxOf{it.length}>26) 12.sp else 16.sp
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
                                    .padding(textButtonPadding), onClick = { setForms(cursor, 1) })
                                {
                                    Text(
                                        text = forms[1],
                                        style = TextStyle(
                                            fontSize = textFontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(textButtonPadding),
                                    onClick = { setForms(cursor, 3) })
                                {
                                    Text(
                                        text = forms[3],
                                        style = TextStyle(
                                            fontSize = textFontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(textButtonPadding),
                                    onClick = { setForms(cursor, 2) })
                                {
                                    Text(
                                        text = forms[2],
                                        style = TextStyle(
                                            fontSize = textFontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(textButtonPadding),
                                    onClick = { setForms(cursor, 4) })
                                {
                                    Text(
                                        text = forms[4],
                                        style = TextStyle(
                                            fontSize = textFontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }

                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val buttonSize = model.dimensions.inputButtonSize - 14.dp
                        val fontSize = 14
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "1",
                                fontSize = fontSize,
                                buttonSize = buttonSize,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val pairs = formsText.extractIntPairsFromCsv().toMutableList()
                                val (counterpoint, form) = pairs[cursor]
                                pairs[cursor] = Pair( if(counterpoint == 2) 1 else 2,form)
                                formsText = pairs.toIntPairsString()
                            }
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "2",
                                fontSize = fontSize,
                                buttonSize = buttonSize,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val pairs = formsText.extractIntPairsFromCsv().toMutableList()
                                val (counterpoint, form) = pairs[cursor]
                                pairs[cursor] = Pair( if(counterpoint == 3) 1 else 3,form)
                                formsText = pairs.toIntPairsString()
                            }
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "3",
                                fontSize = fontSize,
                                buttonSize = buttonSize,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val pairs = formsText.extractIntPairsFromCsv().toMutableList()
                                val (counterpoint, form) = pairs[cursor]
                                pairs[cursor] = Pair( if(counterpoint == 4) 1 else 4,form)
                                formsText = pairs.toIntPairsString()
                            }
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "",
                                iconId = model.iconMap["tritone_substitution"]!!,
                                fontSize = fontSize,
                                buttonSize = buttonSize,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val pairs = formsText.extractIntPairsFromCsv().toMutableList()
                                val (counterpoint, form) = pairs[cursor]
                                pairs[cursor] = Pair( counterpoint * -1 ,form )
                                formsText = pairs.toIntPairsString()
                            }
                        }
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "4",
                                fontSize = fontSize,
                                buttonSize = buttonSize,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val pairs = formsText.extractIntPairsFromCsv().toMutableList()
                                val (counterpoint, form) = pairs[cursor]
                                pairs[cursor] = Pair( if(counterpoint == 5) 1 else 5,form)
                                formsText = pairs.toIntPairsString()
                            }
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "5",
                                fontSize = fontSize,
                                buttonSize = buttonSize,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val pairs = formsText.extractIntPairsFromCsv().toMutableList()
                                val (counterpoint, form) = pairs[cursor]
                                pairs[cursor] = Pair( if(counterpoint == 6) 1 else 6,form)
                                formsText = pairs.toIntPairsString()
                            }
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "6",
                                fontSize = fontSize,
                                buttonSize = buttonSize,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val pairs = formsText.extractIntPairsFromCsv().toMutableList()
                                val (counterpoint, form) = pairs[cursor]
                                pairs[cursor] = Pair( if(counterpoint == 7) 1 else 7,form)
                                formsText = pairs.toIntPairsString()
                            }
                            if (!playing) {
                                CustomButton(
                                    adaptSizeToIconButton = true,
                                    text = "",
                                    iconId = model.iconMap["play"]!!,
                                    fontSize = fontSize,
                                    buttonSize = buttonSize,
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
                                    buttonSize = buttonSize,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    model.onStop()
                                }
                            }

                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "",
                                iconId = model.iconMap["done"]!!,
                                fontSize = fontSize,
                                buttonSize = buttonSize,
                                iconColor = Color.Green,
                                colors = model.appColors
                            ) {
                                multiNumberDialogData.value.onSubmitButtonClick.invoke(formsText)
                                onDismissRequest.invoke()
                            }
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "|",
                                fontSize = fontSize,
                                buttonSize = buttonSize,
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
                                fontSize = fontSize,
                                buttonSize = buttonSize,
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
                                fontSize = fontSize,
                                buttonSize = buttonSize,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val pairs = formsText.extractIntPairsFromCsv().toMutableList()
                                val lastPair = pairs[pairs.size - 1].copy()
                                pairs.add(lastPair)
                                formsText = pairs.toIntPairsString()
                                cursor = pairs.size - 1
                            }
                        }
                    }

                }
            }
        }
    }
}




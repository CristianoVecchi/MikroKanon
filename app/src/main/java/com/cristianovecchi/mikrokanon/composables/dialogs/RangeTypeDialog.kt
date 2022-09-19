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
import com.cristianovecchi.mikrokanon.addOrInsert
import com.cristianovecchi.mikrokanon.composables.CustomButton
import com.cristianovecchi.mikrokanon.extractFloatsFromCsv
import com.cristianovecchi.mikrokanon.extractIntPairsFromCsv
import com.cristianovecchi.mikrokanon.locale.getOctaveSymbols
import com.cristianovecchi.mikrokanon.locale.rangeTypeMap
import com.cristianovecchi.mikrokanon.toIntPairsString
import com.cristianovecchi.mikrokanon.ui.Dimensions
import kotlinx.coroutines.launch

@Composable
fun RangeTypeDialog(multiNumberDialogData: MutableState<MultiNumberDialogData>,
                    dimensions: Dimensions, types: List<String>,
                     onDismissRequest: () -> Unit = { multiNumberDialogData.value = MultiNumberDialogData(model = multiNumberDialogData.value.model, value = multiNumberDialogData.value.value) })
{

    if (multiNumberDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            val model = multiNumberDialogData.value.model
            val appColors = model.appColors
            val fontColor = appColors.dialogFontColor
            val backgroundColor = appColors.dialogBackgroundColor
            val octaves= getOctaveSymbols()
            Surface(
                modifier = Modifier.width(dimensions.dialogWidth).height(dimensions.height.dp),
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
                    var rangeText by remember { mutableStateOf(multiNumberDialogData.value.value) }
                    var cursor by remember { mutableStateOf(0) }
                    val setRange = { index: Int, newRange: Int ->
                        val ranges = rangeText.extractIntPairsFromCsv().toMutableList()
                        ranges[index] = Pair(newRange, ranges[index].second)
                        rangeText = ranges.toIntPairsString()
                    }
                    val fontSize = dimensions.dialogFontSize
                    val fontWeight = FontWeight.Normal
                    val buttonPadding = 4.dp
                    Column(modifier = modifierA) {
                        Text(text = multiNumberDialogData.value.title, fontWeight = FontWeight.Bold, color = fontColor, style = TextStyle(fontSize = fontSize.sp))
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
                        val ranges = rangeText.extractIntPairsFromCsv()
                        val nCols = 4
                        val nRows = (ranges.size / nCols) + 1
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
                                        if (index != ranges.size) {

                                            val text = "${rangeTypeMap[ranges[index].first]!!}${octaves[ranges[index].second+2]}"
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
                                                    style = TextStyle(fontSize = (fontSize / 4 * 3).sp),
                                                    fontWeight = if (cursor == index) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                            index++
                                        }
                                    }
                                }
                            }
                            if (cursor > -1) coroutineScope.launch {
                                val rowIndex = if (ranges.size <= nCols) 1 else cursor / nCols
                                listState.animateScrollToItem(rowIndex)
                            }
                        }
                    }
                    Column(modifier = modifierB, verticalArrangement = Arrangement.Center) {
                        val textFontSize = if(types.maxOf{it.length}>26) fontSize / 3 * 2 else fontSize
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
                                    .padding(textButtonPadding), onClick = { setRange(cursor, 0) })
                                {
                                    Text(
                                        text = types[0],
                                        style = TextStyle(
                                            fontSize = textFontSize.sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(textButtonPadding),
                                    onClick = { setRange(cursor, 1) })
                                {
                                    Text(
                                        text = types[1],
                                        style = TextStyle(
                                            fontSize = textFontSize.sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(textButtonPadding),
                                    onClick = { setRange(cursor, 2) })
                                {
                                    Text(
                                        text = types[2],
                                        style = TextStyle(
                                            fontSize = textFontSize.sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(textButtonPadding),
                                    onClick = { setRange(cursor, 3) })
                                {
                                    Text(
                                        text = types[3],
                                        style = TextStyle(
                                            fontSize = textFontSize.sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(textButtonPadding),
                                    onClick = { setRange(cursor, 4) })
                                {
                                    Text(
                                        text = types[4],
                                        style = TextStyle(
                                            fontSize = textFontSize.sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }

                            }
                            Column(
                                modifier = Modifier.width(IntrinsicSize.Max),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                val dimensions by model.dimensions.asFlow().collectAsState(initial = model.dimensions.value!!)
                                val buttonSize = dimensions.dialogButtonSize / 4 * 3
                                val fontSizeOctave = dimensions.dialogFontSize / 3 * 2
                                CustomButton(
                                    adaptSizeToIconButton = false,
                                    text = octaves[4], // +15
                                    fontSize = fontSizeOctave,
                                    buttonSize = buttonSize.dp,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val pairs = rangeText.extractIntPairsFromCsv().toMutableList()
                                    val (range, octave) = pairs[cursor]
                                    pairs[cursor] = Pair( range, if(octave ==2) 0 else 2 )
                                    rangeText = pairs.toIntPairsString()
                                }
                                CustomButton(
                                    adaptSizeToIconButton = false,
                                    text = octaves[3], // +8
                                    fontSize = fontSizeOctave,
                                    buttonSize = buttonSize.dp,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val pairs = rangeText.extractIntPairsFromCsv().toMutableList()
                                    val (range, octave) = pairs[cursor]
                                    pairs[cursor] = Pair( range, if(octave ==1) 0 else 1 )
                                    rangeText = pairs.toIntPairsString()
                                }
                                CustomButton(
                                    adaptSizeToIconButton = false,
                                    text = octaves[1], // -8
                                    fontSize = fontSizeOctave,
                                    buttonSize = buttonSize.dp,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val pairs = rangeText.extractIntPairsFromCsv().toMutableList()
                                    val (range, octave) = pairs[cursor]
                                    pairs[cursor] = Pair( range, if(octave == -1) 0 else -1)
                                    rangeText = pairs.toIntPairsString()
                                }
                                CustomButton(
                                    adaptSizeToIconButton = false,
                                    text = octaves[0], // -15
                                    fontSize = fontSizeOctave,
                                    buttonSize = buttonSize.dp,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val pairs = rangeText.extractIntPairsFromCsv().toMutableList()
                                    val (range, octave) = pairs[cursor]
                                    pairs[cursor] = Pair( range, if(octave == -2) 0 else -2)
                                    rangeText = pairs.toIntPairsString()
                                }
                                CustomButton(
                                    adaptSizeToIconButton = false,
                                    text = octaves[5], // |8
                                    fontSize = fontSizeOctave,
                                    buttonSize = buttonSize.dp,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val pairs = rangeText.extractIntPairsFromCsv().toMutableList()
                                    val (range, octave) = pairs[cursor]
                                    pairs[cursor] = Pair( range, if(octave == 3) 0 else 3)
                                    rangeText = pairs.toIntPairsString()
                                }
                                CustomButton(
                                    adaptSizeToIconButton = false,
                                    text = octaves[6], // |15
                                    fontSize = fontSizeOctave,
                                    buttonSize = buttonSize.dp,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val pairs = rangeText.extractIntPairsFromCsv().toMutableList()
                                    val (range, octave) = pairs[cursor]
                                    pairs[cursor] = Pair( range, if(octave == 4) 0 else 4)
                                    rangeText = pairs.toIntPairsString()
                                }


                            }
                        }
                    }
                    Row(
                        modifier = modifierC.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val buttonSize = dimensions.dialogButtonSize
                        CustomButton(
                            adaptSizeToIconButton = true,
                            text = "",
                            iconId = model.iconMap["done"]!!,
                            buttonSize = buttonSize.dp,
                            iconColor = Color.Green,
                            colors = model.appColors
                        ) {
                            multiNumberDialogData.value.onSubmitButtonClick.invoke(rangeText)
                            onDismissRequest.invoke()
                        }

                        CustomButton(
                            adaptSizeToIconButton = true,
                            text = "",
                            iconId = model.iconMap["delete"]!!,
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            val ranges = rangeText.extractIntPairsFromCsv().toMutableList()
                            if(ranges.size > 1){
                                ranges.removeAt(cursor)
                                rangeText = ranges.toIntPairsString()
                                val newCursor = if (ranges.size > 1) cursor - 1 else 0
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
                            var ranges = rangeText.extractIntPairsFromCsv()
                            val selectedRange = ranges[cursor]
                            val rebuilding = ranges.addOrInsert(selectedRange, cursor)
                            ranges = rebuilding.first
                            cursor = rebuilding.second
                            rangeText = ranges.toMutableList().toIntPairsString()
                        }
                    }

                }
            }
        }
    }
}





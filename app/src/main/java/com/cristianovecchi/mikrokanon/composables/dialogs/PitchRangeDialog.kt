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
import com.cristianovecchi.mikrokanon.AIMUSIC.A0
import com.cristianovecchi.mikrokanon.AIMUSIC.C8
import com.cristianovecchi.mikrokanon.AIMUSIC.Clip
import com.cristianovecchi.mikrokanon.composables.CustomButton
import com.cristianovecchi.mikrokanon.locale.describeAsNote
import com.cristianovecchi.mikrokanon.locale.getDynamicSymbols
import com.cristianovecchi.mikrokanon.ui.Dimensions
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun PitchRangeDialog(multiFloatDialogData: MutableState<MultiFloatDialogData>,
                       dimensions: Dimensions, okText: String = "OK",
                       onDismissRequest: () -> Unit = { multiFloatDialogData.value = MultiFloatDialogData(model = multiFloatDialogData.value.model, value = multiFloatDialogData.value.value) }) {

    if (multiFloatDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            val model = multiFloatDialogData.value.model
            val appColors = model.appColors
            val fontColor = appColors.dialogFontColor
            val backgroundColor = appColors.dialogBackgroundColor
            val width = if(dimensions.width <=884) (dimensions.width / 10 * 8 / dimensions.dpDensity).toInt().dp
            else dimensions.dialogWidth
            val height = dimensions.height.dp
//                if(dimensions.height < 1280) (dimensions.height / dimensions.dpDensity).toInt().dp
//            else dimensions.dialogHeight
            Surface(
                modifier = Modifier
                    .width(width)
                    .height(dimensions.dialogHeight),
                color = backgroundColor,
                shape = RoundedCornerShape(10.dp)
            ) {

                Column(modifier = Modifier
                    .height((dimensions.height / 6 * 5).dp)
                    .padding(10.dp)) {
                    val weights = dimensions.dialogWeights
                    val modifierA = Modifier
                        //.fillMaxSize()
                        .padding(8.dp)
                        .weight(weights.first/2)
                    val modifierB = Modifier
                        //.fillMaxSize()
                        .weight(weights.second + weights.first/2 )
                    val modifierC = Modifier
                        //.fillMaxSize()
                        .padding(8.dp)
                        .weight(weights.third)
                    var rangeText by remember { mutableStateOf(multiFloatDialogData.value.value) }
                    var cursor by remember { mutableStateOf(0) }
                    val setLimit = { index: Int, absPitch: Int, octave: Int ->
                        var newLimit = (absPitch + octave * 12)
                        newLimit = if(newLimit<A0) newLimit+12 else newLimit
                        newLimit = if(newLimit>C8) newLimit-12 else newLimit
                        val limitValues = rangeText.extractIntsFromCsv().toMutableList()
                        limitValues[index] = when(index){
                            0 -> if(newLimit > limitValues[1]) limitValues[1] else newLimit
                            else -> if(newLimit < limitValues[0]) limitValues[0] else newLimit
                        }
                        rangeText = limitValues.joinToString(",")
                    }
                    val limits = rangeText.extractIntsFromCsv()
                    val fontSize = dimensions.dialogFontSize
                    val fontWeight = FontWeight.Normal
                    val buttonPadding = 4.dp
                    val noteNames = model.language.value!!.noteNames
                    val symbols = (0..11).map{ Clip.convertAbsToClipText(it, noteNames)}

                    Column(modifier = modifierA) {
                        Text(text = multiFloatDialogData.value.title, fontWeight = FontWeight.Bold, color = fontColor, style = TextStyle(fontSize = fontSize.sp))
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
                        val intervalPadding = 4.dp
                        val innerPadding = 10.dp
                        val nCols = 2
                        val nRows = (limits.size / nCols) + 1
                        val rows = (0 until nRows).toList()
                        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally, state = listState) {
                            items(rows) { row ->
                                var index = row * nCols
                                //Text(text = "ROW #$row")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    for (j in 0 until nCols) {
                                        if (index != limits.size) {
                                            val text = limits[index]
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
                                                    text = limits[index].describeAsNote(noteNames),
                                                    modifier = Modifier.padding(innerPadding),
                                                    style = TextStyle(fontSize = (fontSize * 2).sp),
                                                    fontWeight = if (cursor == index) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                            index++
                                        }
                                    }
                                }
                            }
                            if (cursor > -1) coroutineScope.launch {
                                val rowIndex = if (limits.size <= nCols) 1 else cursor / nCols
                                listState.animateScrollToItem(rowIndex)
                            }
                        }
                    }

                    Column(modifier = modifierB) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(
                                modifier = Modifier.width(IntrinsicSize.Max),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding), onClick = {
                                    val octave = limits[cursor] / 12
                                    setLimit(cursor, 11, octave)
                                })
                                {
                                    Text(
                                        text = symbols[11],
                                        style = TextStyle(
                                            fontSize = fontSize.sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(buttonPadding),
                                    onClick = {
                                        val octave = limits[cursor] / 12
                                        setLimit(cursor, 10, octave)
                                    })
                                {
                                    Text(
                                        text = symbols[10],
                                        style = TextStyle(
                                            fontSize = fontSize.sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(buttonPadding),
                                    onClick = {
                                        val octave = limits[cursor] / 12
                                        setLimit(cursor, 9, octave)
                                    })
                                {
                                    Text(
                                        text = symbols[9],
                                        style = TextStyle(
                                            fontSize = fontSize.sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(buttonPadding),
                                    onClick = {
                                        val octave = limits[cursor] / 12
                                        setLimit(cursor, 8, octave)
                                    })
                                {
                                    Text(
                                        text = symbols[8],
                                        style = TextStyle(
                                            fontSize = fontSize.sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }

                            }
                            Column(
                                modifier = Modifier.width(IntrinsicSize.Max),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding), onClick = {
                                    val octave = limits[cursor] / 12
                                    setLimit(cursor, 7, octave)
                                })
                                {
                                    Text(
                                        text = symbols[7],
                                        style = TextStyle(
                                            fontSize = fontSize.sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = {
                                        val octave = limits[cursor] / 12
                                        setLimit(cursor, 6, octave)
                                    })
                                {
                                    Text(
                                        text = symbols[6],
                                        style = TextStyle(
                                            fontSize = fontSize.sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = {
                                        val octave = limits[cursor] / 12
                                        setLimit(cursor, 5, octave)
                                    })
                                {
                                    Text(
                                        text = symbols[5],
                                        style = TextStyle(
                                            fontSize = fontSize.sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = {
                                        val octave = limits[cursor] / 12
                                        setLimit(cursor, 4, octave)
                                    })
                                {
                                    Text(
                                        text = symbols[4],
                                        style = TextStyle(
                                            fontSize = fontSize.sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }

                            }
                            Column(
                                modifier = Modifier.width(IntrinsicSize.Max),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding), onClick = {
                                    val octave = limits[cursor] / 12
                                    setLimit(cursor, 3, octave)
                                })
                                {
                                    Text(
                                        text = symbols[3],
                                        style = TextStyle(
                                            fontSize = fontSize.sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = {
                                        val octave = limits[cursor] / 12
                                        setLimit(cursor, 2, octave)
                                    })
                                {
                                    Text(
                                        text = symbols[2],
                                        style = TextStyle(
                                            fontSize = fontSize.sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = {
                                        val octave = limits[cursor] / 12
                                        setLimit(cursor, 1, octave)
                                    })
                                {
                                    Text(
                                        text = symbols[1],
                                        style = TextStyle(
                                            fontSize = fontSize.sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = {
                                        val octave = limits[cursor] / 12
                                        setLimit(cursor, 0, octave)
                                    })
                                {
                                    Text(
                                        text = symbols[0],
                                        style = TextStyle(
                                            fontSize = fontSize.sp,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                            }
                        } // END NOTE BUTTONS
                        Spacer(modifier = Modifier.height(10.dp))
                        Column() {
                            val buttonSize = dimensions.dialogButtonSize / 4 * 5
                            val numberFontSize = dimensions.dialogFontSize
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                CustomButton(
                                    adaptSizeToIconButton = true,
                                    text = "0",
                                    fontSize = numberFontSize,
                                    buttonSize = buttonSize.dp,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val absPitch = limits[cursor] % 12
                                    setLimit(cursor, absPitch, 1)
                                }
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "1",
                                fontSize = numberFontSize,
                                buttonSize = buttonSize.dp,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val absPitch = limits[cursor] % 12
                                setLimit(cursor, absPitch, 2)
                            }
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "2",
                                fontSize = numberFontSize,
                                buttonSize = buttonSize.dp,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val absPitch = limits[cursor] % 12
                                setLimit(cursor, absPitch, 3)
                            }
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "3",
                                fontSize = numberFontSize,
                                buttonSize = buttonSize.dp,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val absPitch = limits[cursor] % 12
                                setLimit(cursor, absPitch, 4)
                            }
                        }
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                CustomButton(
                                    adaptSizeToIconButton = true,
                                    text = "4",
                                    fontSize = numberFontSize,
                                    buttonSize = buttonSize.dp,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val absPitch = limits[cursor] % 12
                                    setLimit(cursor, absPitch, 5)
                                }
                                CustomButton(
                                    adaptSizeToIconButton = true,
                                    text = "5",
                                    fontSize = numberFontSize,
                                    buttonSize = buttonSize.dp,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val absPitch = limits[cursor] % 12
                                    setLimit(cursor, absPitch, 6)
                                }
                                CustomButton(
                                    adaptSizeToIconButton = true,
                                    text = "6",
                                    fontSize = numberFontSize,
                                    buttonSize = buttonSize.dp,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val absPitch = limits[cursor] % 12
                                    setLimit(cursor, absPitch, 7)
                                }
                                CustomButton(
                                    adaptSizeToIconButton = true,
                                    text = "7",
                                    fontSize = numberFontSize,
                                    buttonSize = buttonSize.dp,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val absPitch = limits[cursor] % 12
                                    setLimit(cursor, absPitch, 8)
                                }
                            }
                    }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier= modifierC.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val dimensions by model.dimensions.asFlow().collectAsState(initial = model.dimensions.value!!)
                            val buttonSize = dimensions.dialogButtonSize
                            CustomButton(
                                adaptSizeToIconButton = false,
                                text = "",
                                iconId = model.iconMap["done"]!!,
                                buttonSize = buttonSize.dp,
                                iconColor = Color.Green,
                                colors = model.appColors
                            ) {
                                multiFloatDialogData.value.onSubmitButtonClick.invoke(
                                    rangeText
                                )
                                onDismissRequest.invoke()
                            }
                            Button(modifier = Modifier
                                .padding(buttonPadding),
                                onClick = {
                                    setLimit(0,9,1)
                                    setLimit(1,0,9)
                                })
                            {
                                Text(
                                    text = "∞",
                                    style = TextStyle(
                                        fontSize = fontSize.sp,
                                        fontWeight = FontWeight.Bold
                                    ), color = fontColor
                                )
                            }
//                            CustomButton(
//                                adaptSizeToIconButton = true,
//                                text = "∞",
//                                buttonSize = buttonSize.dp,
//                                iconColor = model.appColors.iconButtonIconColor,
//                                colors = model.appColors
//                            ) {
//                                setLimit(0,9,1)
//                                setLimit(1,0,9)
//                            }

                        }

                    }
                }
            }
        }
    }}








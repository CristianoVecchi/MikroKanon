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
import com.cristianovecchi.mikrokanon.composables.CustomButton
import com.cristianovecchi.mikrokanon.correctBpms
import com.cristianovecchi.mikrokanon.extractIntsFromCsv
import com.cristianovecchi.mikrokanon.ui.Dimensions
import com.cristianovecchi.mikrokanon.valueFromCsv
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun MultiBpmDialog(multiNumberDialogData: MutableState<MultiNumberDialogData>,
                   dimensions: Dimensions, okText: String = "OK",
                   onDismissRequest: () -> Unit = { multiNumberDialogData.value = MultiNumberDialogData(model = multiNumberDialogData.value.model, value = multiNumberDialogData.value.value) }) {

    if (multiNumberDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            val model = multiNumberDialogData.value.model
            val width = if(dimensions.width <= 884) (dimensions.width / 10 * 8 / dimensions.dpDensity).toInt().dp
            else dimensions.dialogWidth
            Surface(
                modifier = Modifier.width(width).height(dimensions.dialogHeight),
                shape = RoundedCornerShape(10.dp)
            ) {

                Column(modifier = Modifier.height((dimensions.height / 6 * 5).dp)
                    .padding(10.dp)) {
                    val weights = dimensions.dialogWeights
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
                    var bpmText by remember { mutableStateOf(multiNumberDialogData.value.value) }
                    var cursor by remember { mutableStateOf(0) }
                    val setBpm = { index: Int, bpmToCheck: Int ->
                        val newBpm = bpmToCheck.coerceIn(
                            multiNumberDialogData.value.min,
                            multiNumberDialogData.value.max
                        )
                        val bpmValues = bpmText.extractIntsFromCsv().toMutableList()
                        bpmValues[index] = newBpm
                        bpmText = bpmValues.joinToString(",")
                    }
                    val fontSize = dimensions.dialogFontSize.sp
                    val fontWeight = FontWeight.Normal
                    val buttonPadding = 4.dp
                    Column(modifier = modifierA) {
                        Text(text = multiNumberDialogData.value.title)
                        Spacer(modifier = Modifier.height(20.dp))

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
                        val bpms = bpmText.extractIntsFromCsv()
                        val nCols = 4
                        val nRows = (bpms.size / nCols) + 1
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
                                        if (index != bpms.size) {
                                            val text = bpms[index]
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
                                                    text = if (text < 0) "|${text.absoluteValue}" else text.toString(),
                                                    modifier = Modifier.padding(innerPadding),
                                                    style = TextStyle(fontSize = fontSize),
                                                    fontWeight = if (cursor == index) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                            index++
                                        }
                                    }
                                }
                            }
                            if (cursor > -1) coroutineScope.launch {
                                val rowIndex = if (bpms.size <= nCols) 1 else cursor / nCols
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
                                    .padding(buttonPadding), onClick = { setBpm(cursor, 240) })
                                {
                                    Text(
                                        text = "240",
                                        style = TextStyle(
                                            fontSize = fontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(buttonPadding),
                                    onClick = { setBpm(cursor, bpmText.valueFromCsv(cursor) + 30) })
                                {
                                    Text(
                                        text = "+30",
                                        style = TextStyle(
                                            fontSize = fontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(buttonPadding),
                                    onClick = { setBpm(cursor, bpmText.valueFromCsv(cursor) - 30) })
                                {
                                    Text(
                                        text = "-30",
                                        style = TextStyle(
                                            fontSize = fontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }

                            }
                            Column(
                                modifier = Modifier.width(IntrinsicSize.Max),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding), onClick = { setBpm(cursor, 150) })
                                {
                                    Text(
                                        text = "150",
                                        style = TextStyle(
                                            fontSize = fontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = { setBpm(cursor, bpmText.valueFromCsv(cursor) + 6) })
                                {
                                    Text(
                                        text = "+6",
                                        style = TextStyle(
                                            fontSize = fontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = { setBpm(cursor, bpmText.valueFromCsv(cursor) - 6) })
                                {
                                    Text(
                                        text = "-6",
                                        style = TextStyle(
                                            fontSize = fontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }

                            }
                            Column(
                                modifier = Modifier.width(IntrinsicSize.Max),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding), onClick = { setBpm(cursor, 60) })
                                {
                                    Text(
                                        text = "60",
                                        style = TextStyle(
                                            fontSize = fontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = { setBpm(cursor, bpmText.valueFromCsv(cursor) + 1) })
                                {
                                    Text(
                                        text = "+1",
                                        style = TextStyle(
                                            fontSize = fontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = { setBpm(cursor, bpmText.valueFromCsv(cursor) - 1) })
                                {
                                    Text(
                                        text = "-1",
                                        style = TextStyle(
                                            fontSize = fontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = modifierC.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            val buttonSize = model.dimensions.dialogButtonSize
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "",
                                iconId = model.iconMap["done"]!!,
                                buttonSize = buttonSize.dp,
                                iconColor = Color.Green,
                                colors = model.appColors
                            ) {
                                multiNumberDialogData.value.onSubmitButtonClick.invoke(
                                    correctBpms(
                                        bpmText
                                    )
                                )
                                onDismissRequest.invoke()
                            }
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "",
                                iconId = model.iconMap["bar"]!!,
                                buttonSize = buttonSize.dp,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val values = bpmText.extractIntsFromCsv().toMutableList()
                                val value = values[cursor]
                                values.set(cursor, value * -1)
                                bpmText = values.joinToString(",")
                            }
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "",
                                iconId = model.iconMap["delete"]!!,
                                buttonSize = buttonSize.dp,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val values = bpmText.extractIntsFromCsv().toMutableList()
                                if (values.size > 1) {
                                    values.removeAt(cursor)
                                    bpmText = values.joinToString(",")
                                    val newCursor = if (values.size > 1) cursor - 1 else 0
                                    cursor = if (newCursor < 0) 0 else newCursor
                                    bpmText = values.joinToString(",")
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
                                val values = bpmText.extractIntsFromCsv().toMutableList()
                                val lastValue = values[values.size - 1]
                                values.add(lastValue)
                                bpmText = values.joinToString(",")
                                cursor = values.size - 1
                            }

                        }

                    }
                }
            }
        }
    }}




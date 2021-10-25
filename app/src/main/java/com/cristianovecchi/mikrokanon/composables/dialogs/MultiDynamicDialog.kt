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
import com.cristianovecchi.mikrokanon.*
import com.cristianovecchi.mikrokanon.composables.CustomButton
import com.cristianovecchi.mikrokanon.composables.MultiFloatDialogData
import com.cristianovecchi.mikrokanon.composables.MultiNumberDialogData
import com.cristianovecchi.mikrokanon.locale.getDynamicSymbols
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun MultiDynamicDialog(multiFloatDialogData: MutableState<MultiFloatDialogData>, okText: String = "OK",
                       onDismissRequest: () -> Unit = { multiFloatDialogData.value = MultiFloatDialogData(model = multiFloatDialogData.value.model, value = multiFloatDialogData.value.value) }) {

    if (multiFloatDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            val model = multiFloatDialogData.value.model
            Surface(
                modifier = Modifier.width(350.dp).height(700.dp),
                shape = RoundedCornerShape(10.dp)
            ) {

                Column(modifier = Modifier.padding(10.dp)) {
                    val modifierA = Modifier
                        //.fillMaxSize()
                        .padding(8.dp)
                        .weight(5f)
                    val modifierB = Modifier
                        //.fillMaxSize()
                        .weight(4f)
                    var dynamicText by remember { mutableStateOf(multiFloatDialogData.value.value) }
                    var cursor by remember { mutableStateOf(0) }
                    val setDynamic = { index: Int, dynamicToCheck: Float ->
                        val newDynamic = dynamicToCheck.coerceIn(
                            multiFloatDialogData.value.min,
                            multiFloatDialogData.value.max
                        )
                        val dynamicValues = dynamicText.extractFloatsFromCsv().toMutableList()
                        dynamicValues[index] = newDynamic
                        dynamicText = dynamicValues.joinToString(",")
                    }
                    val fontSize = 22.sp
                    val fontWeight = FontWeight.Normal
                    val buttonPadding = 4.dp
                    val symbols = getDynamicSymbols()
                    // avoiding negative zero
                    val steps = model.dynamicSteps
                    val dynamicMap: Map<Float,String> =  model.dynamicMap

                    Column(modifier = modifierA) {
                        Text(text = multiFloatDialogData.value.title)
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
                        val dynamics = dynamicText.extractFloatsFromCsv()
                        val nCols = 4
                        val nRows = (dynamics.size / nCols) + 1
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
                                        if (index != dynamics.size) {
                                            val text = dynamics[index]
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
                                                    text = if (text < 0f) "| ${dynamicMap[text.absoluteValue]!!}" else dynamicMap[text]!!,
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
                                val rowIndex = if (dynamics.size <= nCols) 1 else cursor / nCols
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
                                    .padding(buttonPadding), onClick = { setDynamic(cursor, steps[0]) })
                                {
                                    Text(
                                        text = symbols[0],
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
                                    onClick = { setDynamic(cursor, steps[1]) })
                                {
                                    Text(
                                        text = symbols[1],
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
                                    onClick = { setDynamic(cursor, steps[2]) })
                                {
                                    Text(
                                        text = symbols[2],
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
                                    onClick = { setDynamic(cursor, steps[3]) })
                                {
                                    Text(
                                        text = symbols[3],
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
                                    .padding(buttonPadding), onClick = { setDynamic(cursor, steps[4]) })
                                {
                                    Text(
                                        text = symbols[4],
                                        style = TextStyle(
                                            fontSize = fontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = { setDynamic(cursor, steps[5])})
                                {
                                    Text(
                                        text = symbols[5],
                                        style = TextStyle(
                                            fontSize = fontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = { setDynamic(cursor, steps[6]) })
                                {
                                    Text(
                                        text = symbols[6],
                                        style = TextStyle(
                                            fontSize = fontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = { setDynamic(cursor, steps[7]) })
                                {
                                    Text(
                                        text = symbols[7],
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
                                    .padding(buttonPadding), onClick = { setDynamic(cursor, steps[8]) })
                                {
                                    Text(
                                        text = symbols[8],
                                        style = TextStyle(
                                            fontSize = fontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = { setDynamic(cursor, steps[9]) })
                                {
                                    Text(
                                        text = symbols[9],
                                        style = TextStyle(
                                            fontSize = fontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = { setDynamic(cursor, steps[10]) })
                                {
                                    Text(
                                        text = symbols[10],
                                        style = TextStyle(
                                            fontSize = fontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = { setDynamic(cursor, steps[11]) })
                                {
                                    Text(
                                        text = symbols[11],
                                        style = TextStyle(
                                            fontSize = fontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {

                            val buttonSize = model.dimensions.inputButtonSize - 10.dp
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "",
                                iconId = model.iconMap["done"]!!,
                                fontSize = 2,
                                buttonSize = buttonSize,
                                iconColor = Color.Green,
                                colors = model.appColors
                            ) {
                                multiFloatDialogData.value.onSubmitButtonClick.invoke(
                                    correctDynamics(
                                        dynamicText
                                    )
                                )
                                onDismissRequest.invoke()
                            }
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "|",
                                fontSize = 10,
                                buttonSize = buttonSize,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val values = dynamicText.extractFloatsFromCsv().toMutableList()
                                val value = values[cursor]
                                values.set(cursor, value * -1f)
                                dynamicText = values.joinToString(",")
                            }
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "",
                                iconId = model.iconMap["delete"]!!,
                                fontSize = 2,
                                buttonSize = buttonSize,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val values = dynamicText.extractFloatsFromCsv().toMutableList()
                                if (values.size > 1) {
                                    values.removeAt(cursor)
                                    dynamicText = values.joinToString(",")
                                    val newCursor = if (values.size > 1f) cursor - 1 else 0
                                    cursor = if (newCursor < 0) 0 else newCursor
                                    dynamicText = values.joinToString(",")
                                }
                            }
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "",
                                iconId = model.iconMap["add"]!!,
                                fontSize = 2,
                                buttonSize = buttonSize,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val values = dynamicText.extractFloatsFromCsv().toMutableList()
                                val lastValue = values[values.size - 1]
                                values.add(lastValue)
                                dynamicText = values.joinToString(",")
                                cursor = values.size - 1
                            }

                        }

                    }
                }
            }
        }
    }}





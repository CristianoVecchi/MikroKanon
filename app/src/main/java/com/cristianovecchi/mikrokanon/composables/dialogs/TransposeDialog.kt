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
import com.cristianovecchi.mikrokanon.addOrInsert
import com.cristianovecchi.mikrokanon.composables.CustomButton
import com.cristianovecchi.mikrokanon.extractIntPairsFromCsv
import com.cristianovecchi.mikrokanon.locale.rowFormsMap
import com.cristianovecchi.mikrokanon.toIntPairsString
import com.cristianovecchi.mikrokanon.ui.Dimensions
import kotlinx.coroutines.launch

@Composable
fun TransposeDialog(multiNumberDialogData: MutableState<MultiNumberDialogData>,
                    dimensions: Dimensions, intervals: List<String>, okText: String = "OK",
                    onDismissRequest: () -> Unit = { multiNumberDialogData.value = MultiNumberDialogData(model = multiNumberDialogData.value.model, value = multiNumberDialogData.value.value) })
{

    if (multiNumberDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            val model = multiNumberDialogData.value.model
            val appColors = model.appColors
            val fontColor = appColors.dialogFontColor
            val backgroundColor = appColors.dialogBackgroundColor
            val inverseSymbol = rowFormsMap[2]!!
            val retrogradeSymbol = rowFormsMap[3]!!
            val width = if(dimensions.width <=884) (dimensions.width / 10 * 8 / dimensions.dpDensity).toInt().dp
            else dimensions.dialogWidth
            val height = (dimensions.height / dimensions.dpDensity).toInt().dp
//            val height = if(dimensions.height < 1280) (dimensions.height / dimensions.dpDensity).toInt().dp
//            else dimensions.dialogHeight
            Surface(
                modifier = Modifier.width(width).height(height),
                color = backgroundColor,
                shape = RoundedCornerShape(10.dp)
            ) {

                Column(modifier = Modifier.height((dimensions.height / 6 * 5).dp)
                                    .padding(10.dp)) {
                    val weights = dimensions.dialogWeights
                    val modifierA = Modifier
                        //.fillMaxSize()
                        .padding(8.dp)
                        .weight(weights.first / 4 * 3)
                    val modifierB = Modifier
                        //.fillMaxSize()
                        .weight(weights.second + weights.first / 4)//  + weights.third/2)
                    val modifierC = Modifier
                        //.fillMaxSize()
                        .padding(8.dp)
                        .weight(weights.third)
                    var transposeText by remember { mutableStateOf(multiNumberDialogData.value.value) }
                    var cursor by remember{ mutableStateOf(0) }
                    val setTranspose = { index: Int, newTranspose: Int ->
                        val bpmValues = transposeText.extractIntPairsFromCsv().toMutableList()
                        bpmValues[index] = Pair(newTranspose, bpmValues[index].second)
                        transposeText = bpmValues.toIntPairsString()
                    }
                    val fontSize = dimensions.dialogFontSize.sp
                    val fontWeight = FontWeight.Normal
                    val buttonPadding = 4.dp
                    Column(modifier = modifierA) {
                        Text(text = multiNumberDialogData.value.title, fontWeight = FontWeight.Bold, color = fontColor, style = TextStyle(fontSize = fontSize))
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
                        val transpositions = transposeText.extractIntPairsFromCsv()
                        val nCols = 3
                        val nRows = (transpositions.size / nCols) + 1
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
                                        if (index != transpositions.size) {
                                            val text = intervals[transpositions[index].first]
                                            val features = if(transpositions[index].second == 1) "" else " " + rowFormsMap[transpositions[index].second]!!
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
                                                    text = text + features,
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
                                val rowIndex = if (transpositions.size <= nCols) 1 else cursor / nCols
                                listState.animateScrollToItem(rowIndex)
                            }
                        }
                    }
                    Column(modifier=modifierB){
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(
                                modifier = Modifier.width(IntrinsicSize.Max),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding), onClick = { setTranspose(cursor, 11) })
                                {
                                    Text(
                                        text = intervals[11],
                                        style = TextStyle(fontSize = fontSize, fontWeight = fontWeight),
                                        color = fontColor
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(buttonPadding),
                                    onClick = { setTranspose(cursor, 10) })
                                {
                                    Text(
                                        text = intervals[10],
                                        style = TextStyle(fontSize = fontSize, fontWeight = fontWeight), color = fontColor
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(buttonPadding),
                                    onClick = { setTranspose(cursor, 9) })
                                {
                                    Text(
                                        text = intervals[9],
                                        style = TextStyle(fontSize = fontSize, fontWeight = fontWeight), color = fontColor
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(buttonPadding),
                                    onClick = { setTranspose(cursor, 8) })
                                {
                                    Text(
                                        text = intervals[8],
                                        style = TextStyle(fontSize = fontSize, fontWeight = fontWeight), color = fontColor
                                    )
                                }

                            }
                            Column(
                                modifier = Modifier.width(IntrinsicSize.Max),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = { setTranspose(cursor, 7) })
                                {
                                    Text(
                                        text = intervals[7],
                                        style = TextStyle(fontSize = fontSize, fontWeight = fontWeight), color = fontColor
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = { setTranspose(cursor, 6) })
                                {
                                    Text(
                                        text = intervals[6],
                                        style = TextStyle(fontSize = fontSize, fontWeight = fontWeight), color = fontColor
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = { setTranspose(cursor, 5) })
                                {
                                    Text(
                                        text = intervals[5],
                                        style = TextStyle(fontSize = fontSize, fontWeight = fontWeight), color = fontColor
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = { setTranspose(cursor, 4) })
                                {
                                    Text(
                                        text = intervals[4],
                                        style = TextStyle(fontSize = fontSize, fontWeight = fontWeight), color = fontColor
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier.width(IntrinsicSize.Max),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = { setTranspose(cursor, 3) })
                                {
                                    Text(
                                        text = intervals[3],
                                        style = TextStyle(fontSize = fontSize, fontWeight = fontWeight), color = fontColor
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = { setTranspose(cursor, 2) })
                                {
                                    Text(
                                        text = intervals[2],
                                        style = TextStyle(fontSize = fontSize, fontWeight = fontWeight), color = fontColor
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = { setTranspose(cursor, 1) })
                                {
                                    Text(
                                        text = intervals[1],
                                        style = TextStyle(fontSize = fontSize, fontWeight = fontWeight), color = fontColor
                                    )
                                }
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                    onClick = { setTranspose(cursor, 0) })
                                {
                                    Text(
                                        text = intervals[0],
                                        style = TextStyle(fontSize = fontSize, fontWeight = fontWeight), color = fontColor
                                    )
                                }

                            }
                        }

                        // Inverse and Retrograde Buttons
                        Row(modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically) {
                            Button(modifier = Modifier
                                .padding(buttonPadding),
                                onClick = {
                                    val pairs = transposeText.extractIntPairsFromCsv().toMutableList()
                                    val (transpose, rowForm) = pairs[cursor]
                                    val newRowForm = when(rowForm){
                                        1 -> 2
                                        2 -> 1
                                        3 -> 4
                                        4 -> 3
                                        else -> 1
                                    }
                                    pairs[cursor] = Pair( transpose, newRowForm)
                                    transposeText = pairs.toIntPairsString()
                                })
                            {
                                Text(
                                    text = inverseSymbol,
                                    style = TextStyle(fontSize = fontSize, fontWeight = fontWeight), color = fontColor
                                )
                            }
                            Button(modifier = Modifier
                                .padding(buttonPadding),
                                onClick = {
                                    val pairs =
                                        transposeText.extractIntPairsFromCsv().toMutableList()
                                    val (transpose, rowForm) = pairs[cursor]
                                    val newRowForm = when (rowForm) {
                                        1 -> 3
                                        3 -> 1
                                        2 -> 4
                                        4 -> 2
                                        else -> 1
                                    }
                                    pairs[cursor] = Pair(transpose, newRowForm)
                                    transposeText = pairs.toIntPairsString()
                                })
                            {
                                Text(
                                    text = retrogradeSymbol,
                                    style = TextStyle(fontSize = fontSize, fontWeight = fontWeight), color = fontColor
                                )
                            }
                        }


                    } // end modifierB
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
                                multiNumberDialogData.value.onSubmitButtonClick.invoke(transposeText)
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
                                val values = transposeText.extractIntPairsFromCsv().toMutableList()
                                if(values.size > 1) {
                                    values.removeAt(cursor)
                                    transposeText = values.toIntPairsString()
                                    val newCursor = if(values.size > 1) cursor-1 else 0
                                    cursor = if(newCursor < 0) 0 else newCursor
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
                                var values = transposeText.extractIntPairsFromCsv()
                                val selectedValue = values[cursor]
                                val rebuilding = values.addOrInsert(selectedValue, cursor)
                                values = rebuilding.first
                                cursor = rebuilding.second
                                transposeText = values.toMutableList().toIntPairsString()
                            }
                    }
                }
            }
        }
    }
}

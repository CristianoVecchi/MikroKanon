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
import com.cristianovecchi.mikrokanon.locale.melodyTypeMap
import com.cristianovecchi.mikrokanon.toIntPairsString
import com.cristianovecchi.mikrokanon.locale.rowFormsMap
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun MelodyTypeDialog(multiNumberDialogData: MutableState<MultiNumberDialogData>, types: List<String>,
                   onDismissRequest: () -> Unit = { multiNumberDialogData.value = MultiNumberDialogData(model = multiNumberDialogData.value.model, value = multiNumberDialogData.value.value) })
{

    if (multiNumberDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            val model = multiNumberDialogData.value.model

            Surface(
                modifier = Modifier.width(350.dp).height(400.dp),
                shape = RoundedCornerShape(10.dp)
            ) {

                Column(modifier = Modifier.padding(10.dp)) {
                    val modifierA = Modifier
                        //.fillMaxSize()
                        .padding(8.dp)
                        .weight(5f)
                    val modifierB = Modifier
                        //.fillMaxSize()
                        .weight(3f)
                    var melodyText by remember { mutableStateOf(multiNumberDialogData.value.value) }
                    var cursor by remember { mutableStateOf(0) }
                    val setMelody = { index: Int, newMelody: Int ->
                        val melodies = melodyText.extractIntsFromCsv().toMutableList()
                        melodies[index] = newMelody
                        melodyText = melodies.joinToString(",")
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
                        val melodies = melodyText.extractIntsFromCsv()
                        val nCols = 4
                        val nRows = (melodies.size / nCols) + 1
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
                                        if (index != melodies.size) {
                                            val text = melodyTypeMap[melodies[index]]!!
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
                                val rowIndex = if (melodies.size <= nCols) 1 else cursor / nCols
                                listState.animateScrollToItem(rowIndex)
                            }
                        }
                    }
                    Column(modifier = modifierB, verticalArrangement = Arrangement.Bottom) {
                        val textFontSize = if(types.maxOf{it.length}>26) 16.sp else 20.sp
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
                                    .padding(textButtonPadding), onClick = { setMelody(cursor, 0) })
                                {
                                    Text(
                                        text = types[0],
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
                                    onClick = { setMelody(cursor, 1) })
                                {
                                    Text(
                                        text = types[1],
                                        style = TextStyle(
                                            fontSize = textFontSize,
                                            fontWeight = fontWeight
                                        )
                                    )
                                }

                            }
                        }
                        val buttonSize = model.dimensions.inputButtonSize - 14.dp
                        val fontSize = 14

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
                                multiNumberDialogData.value.onSubmitButtonClick.invoke(melodyText)
                                onDismissRequest.invoke()
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
                                val melodies = melodyText.extractIntsFromCsv().toMutableList()
                                if(melodies.size > 1){
                                    melodies.removeAt(cursor)
                                    melodyText = melodies.joinToString(",")
                                    val newCursor = if (melodies.size > 1) cursor - 1 else 0
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
                                val melodies = melodyText.extractIntsFromCsv().toMutableList()
                                val lastMelody = melodies[melodies.size - 1]
                                melodies.add(lastMelody)
                                melodyText = melodies.joinToString(",")
                                cursor = melodies.size - 1
                            }
                        }
                    }

                }
            }
        }
    }
}





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
import com.cristianovecchi.mikrokanon.locale.*
import com.cristianovecchi.mikrokanon.ui.Dimensions
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun LegatoTypeDialog(multiNumberDialogData: MutableState<MultiNumberDialogData>,
                     dimensions: Dimensions, types: List<String>,
                     onDismissRequest: () -> Unit = { multiNumberDialogData.value = MultiNumberDialogData(model = multiNumberDialogData.value.model, value = multiNumberDialogData.value.value) })
{

    if (multiNumberDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            val model = multiNumberDialogData.value.model
            val fontColor = model.appColors.dialogFontColor
            val backgroundColor = model.appColors.dialogBackgroundColor
            val ribattutos = getRibattutoSymbols()
            val height = dimensions.height.dp
            Surface(
                modifier = Modifier.width(dimensions.dialogWidth).height(height),
                color = backgroundColor,
                shape = RoundedCornerShape(10.dp)
            ) {

                Column(modifier = Modifier.padding(10.dp)) {
                    val modifierA = Modifier
                        //.fillMaxSize()
                        .padding(8.dp)
                        .weight(5f)
                    val modifierB = Modifier
                        //.fillMaxSize()
                        .weight(8f)
                    var legatoText by remember { mutableStateOf(multiNumberDialogData.value.value) }
                    var cursor by remember { mutableStateOf(0) }
                    val setLegato = { index: Int, newLegato: Int ->
                        val legatos = legatoText.extractIntPairsFromCsv().toMutableList()
                        legatos[index] = Pair(newLegato, legatos[index].second)
                        legatoText = legatos.toIntPairsString()
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
                        val ranges = legatoText.extractIntPairsFromCsv()
                        val nCols = 4
                        val nRows = (ranges.size / nCols) + 1
                        val rows = (0 until nRows).toList()
                        LazyColumn(state = listState) {
                            items(rows) { row ->
                                var index = row * nCols
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    for (j in 0 until nCols) {
                                        if (index != ranges.size) {

                                            val text = (if(ranges[index].first < 0) "|" else "") +
                                                "${legatoTypeMap[ranges[index].first.absoluteValue -1]!!}${ribattutos[ranges[index].second]}"
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
                                val rowIndex = if (ranges.size <= nCols) 1 else cursor / nCols
                                listState.animateScrollToItem(rowIndex)
                            }
                        }
                    }
                    Column(modifier = modifierB, verticalArrangement = Arrangement.Center) {
                        val textFontSize = (dimensions.dialogFontSize / 3 * 3).sp
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
                                    val sign = legatoText.extractIntPairsFromCsv()[cursor].first.sign()
                                    setLegato(cursor, 1 * sign) })
                                {
                                    Text(
                                        text = types[0],
                                        style = TextStyle(
                                            fontSize = textFontSize,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(textButtonPadding),
                                    onClick = {
                                        val sign = legatoText.extractIntPairsFromCsv()[cursor].first.sign()
                                        setLegato(cursor, 2 * sign) })
                                {
                                    Text(
                                        text = types[1],
                                        style = TextStyle(
                                            fontSize = textFontSize,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(textButtonPadding),
                                    onClick = {
                                        val sign = legatoText.extractIntPairsFromCsv()[cursor].first.sign()
                                        setLegato(cursor, 3 * sign) })
                                {
                                    Text(
                                        text = types[2],
                                        style = TextStyle(
                                            fontSize = textFontSize,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(textButtonPadding),
                                    onClick = {
                                        val sign = legatoText.extractIntPairsFromCsv()[cursor].first.sign()
                                        setLegato(cursor, 4 * sign) })
                                {
                                    Text(
                                        text = types[3],
                                        style = TextStyle(
                                            fontSize = textFontSize,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(textButtonPadding),
                                    onClick = {
                                        val sign = legatoText.extractIntPairsFromCsv()[cursor].first.sign()
                                        setLegato(cursor, 5 * sign) })
                                {
                                    Text(
                                        text = types[4],
                                        style = TextStyle(
                                            fontSize = textFontSize,
                                            fontWeight = fontWeight
                                        ), color = fontColor
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(textButtonPadding),
                                    onClick = {
                                        val sign = legatoText.extractIntPairsFromCsv()[cursor].first.sign()
                                        setLegato(cursor, 6 * sign) })
                                {
                                    Text(
                                        text = types[5],
                                        style = TextStyle(
                                            fontSize = textFontSize,
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
                                val buttonSize = dimensions.selectorButtonSize
                                val ribattutoFontSize = dimensions.dialogFontSize
                                CustomButton(
                                    adaptSizeToIconButton = false,
                                    text = ribattutos[6],
                                    fontSize = ribattutoFontSize,
                                    buttonSize = buttonSize,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val pairs = legatoText.extractIntPairsFromCsv().toMutableList()
                                    val (legato, ribattuto) = pairs[cursor]
                                    pairs[cursor] = Pair( legato, if(ribattuto == 6) 1 else 6 )
                                    legatoText = pairs.toIntPairsString()
                                }
                                CustomButton(
                                    adaptSizeToIconButton = false,
                                    text = ribattutos[5],
                                    fontSize = ribattutoFontSize,
                                    buttonSize = buttonSize,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val pairs = legatoText.extractIntPairsFromCsv().toMutableList()
                                    val (legato, ribattuto) = pairs[cursor]
                                    pairs[cursor] = Pair( legato, if(ribattuto == 5) 1 else 5 )
                                    legatoText = pairs.toIntPairsString()
                                }
                                CustomButton(
                                    adaptSizeToIconButton = false,
                                    text = ribattutos[4],
                                    fontSize = ribattutoFontSize,
                                    buttonSize = buttonSize,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val pairs = legatoText.extractIntPairsFromCsv().toMutableList()
                                    val (legato, ribattuto) = pairs[cursor]
                                    pairs[cursor] = Pair( legato, if(ribattuto == 4) 1 else 4 )
                                    legatoText = pairs.toIntPairsString()
                                }
                                CustomButton(
                                    adaptSizeToIconButton = false,
                                    text = ribattutos[3],
                                    fontSize = ribattutoFontSize,
                                    buttonSize = buttonSize,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val pairs = legatoText.extractIntPairsFromCsv().toMutableList()
                                    val (legato, ribattuto) = pairs[cursor]
                                    pairs[cursor] = Pair( legato, if(ribattuto == 3) 1 else 3 )
                                    legatoText = pairs.toIntPairsString()
                                }
                                CustomButton(
                                    adaptSizeToIconButton = false,
                                    text = ribattutos[2],
                                    fontSize = ribattutoFontSize,
                                    buttonSize = buttonSize,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val pairs = legatoText.extractIntPairsFromCsv().toMutableList()
                                    val (legato, ribattuto) = pairs[cursor]
                                    pairs[cursor] = Pair( legato, if(ribattuto == 2) 1 else 2 )
                                    legatoText = pairs.toIntPairsString()
                                }
                            }
                        }
                        val buttonSize = dimensions.dialogButtonSize.dp

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "",
                                iconId = model.iconMap["done"]!!,
                                buttonSize = buttonSize,
                                iconColor = Color.Green,
                                colors = model.appColors
                            ) {
                                multiNumberDialogData.value.onSubmitButtonClick.invoke(
                                    correctLegatos(legatoText))
                                onDismissRequest.invoke()
                            }

                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "",
                                iconId = model.iconMap["bar"]!!,
                                buttonSize = buttonSize,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val pairs = legatoText.extractIntPairsFromCsv().toMutableList()
                                val (legato, ribattuto) = pairs[cursor]
                                pairs[cursor] = Pair( legato * -1, ribattuto)
                                legatoText = pairs.toIntPairsString()
                            }

                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "",
                                iconId = model.iconMap["delete"]!!,

                                buttonSize = buttonSize,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val legatos = legatoText.extractIntPairsFromCsv().toMutableList()
                                if(legatos.size > 1){
                                    legatos.removeAt(cursor)
                                    legatoText = legatos.toIntPairsString()
                                    val newCursor = if (legatos.size > 1) cursor - 1 else 0
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
                                var legatos = legatoText.extractIntPairsFromCsv()
                                val selectedRange = legatos[cursor]
                                val rebuilding = legatos.addOrInsert(selectedRange, cursor)
                                legatos = rebuilding.first
                                cursor = rebuilding.second
                                legatoText = legatos.toMutableList().toIntPairsString()
                            }
                        }
                    }

                }
            }
        }
    }
}







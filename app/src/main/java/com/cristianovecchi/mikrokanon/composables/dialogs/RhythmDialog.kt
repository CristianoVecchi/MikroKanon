package com.cristianovecchi.mikrokanon.composables.dialogs

import com.cristianovecchi.mikrokanon.AIMUSIC.RhythmPatterns
import com.cristianovecchi.mikrokanon.locale.Lang

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.cristianovecchi.mikrokanon.ui.Dimensions
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun RhythmDialog(multiNumberDialogData: MutableState<MultiNumberDialogData>, dimensions: Dimensions,
                 patterns: List<RhythmPatterns>, okText: String = "OK",
                 onDismissRequest: () -> Unit = { multiNumberDialogData.value = MultiNumberDialogData(model = multiNumberDialogData.value.model, value = multiNumberDialogData.value.value) }) {

    if (multiNumberDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        val model = multiNumberDialogData.value.model
        val lang = Lang.provideLanguage(model.getUserLangDef())
        val appColors = model.appColors
        val fontColor = appColors.dialogFontColor
        val backgroundColor = appColors.dialogBackgroundColor
            val patternNames = patterns.map{ it.title }
            val listDialogData by lazy { mutableStateOf(ListDialogData())}
            val repetitionsDialogData by lazy { mutableStateOf(ListDialogData()) }
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            ListDialog(listDialogData, dimensions, lang.OkButton, appColors)
            ListDialog(repetitionsDialogData, dimensions, lang.OkButton, appColors, fillPrevious = true)
            val width = if(dimensions.width <= 884) (dimensions.width / 10 * 8 / dimensions.dpDensity).toInt().dp
            else dimensions.dialogWidth
            val height = (dimensions.height / dimensions.dpDensity).toInt().dp

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
                        .weight(weights.first + weights.second / 6 * 5)
                    val modifierB = Modifier
                        //.fillMaxSize()
                        .weight(weights.second / 6)
                    val modifierC = Modifier
                        //.fillMaxSize()
                        .padding(8.dp)
                        .weight(weights.third)
                    var patternText by remember { mutableStateOf(multiNumberDialogData.value.value) }
                    var cursor by remember { mutableStateOf(0) }
                    val setPattern = { index: Int, newPattern: Int, newRepetitions: Int ->
                        val pairs = patternText.extractIntPairsFromCsv().toMutableList()
                        pairs[index] = Pair(newPattern, newRepetitions)
                        patternText = pairs.toIntPairsString()
                    }
                    val fontSize = dimensions.dialogFontSize.sp
                    val fontWeight = FontWeight.Normal
                    val buttonPadding = 4.dp
                    Column(modifier = modifierA) {
                        Text(text = multiNumberDialogData.value.title, color = fontColor, style = TextStyle(fontSize = fontSize))
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
                        val pairs = patternText.extractIntPairsFromCsv()
                        val nCols = 1
                        val nRows = (pairs.size / nCols) + 1
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
                                        if (index != pairs.size) {
                                            val pattern = pairs[index].first
                                            val repetitions = pairs[index].second.absoluteValue
                                            val feature = if(repetitions >1 ) " (${repetitions}x)" else ""
                                            val text = patternNames[pattern] + feature
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
                                                    text = if (pairs[index].second < 0) "←$text" else text,
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
                                val rowIndex = if (patterns.size <= nCols) 1 else cursor / nCols
                                listState.animateScrollToItem(rowIndex)
                            }
                        }
                    }
                    Row(
                        modifier = modifierB.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val buttonSize = dimensions.dialogButtonSize
                        CustomButton(
                            adaptSizeToIconButton = true,
                            iconId = model.iconMap["back"]!!,
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            val values = patternText.extractIntPairsFromCsv().toMutableList()
                            val value = values[cursor]
                            val pattern = value.first
                            val repetitions = value.second
                            setPattern(cursor, pattern, repetitions * -1)
                        }
//                        CustomButton(
//                            adaptSizeToIconButton = true,
//                            iconId = model.iconMap["minus_one"]!!, // [+1] Icon
//                            buttonSize = buttonSize.dp,
//                            iconColor = model.appColors.iconButtonIconColor,
//                            colors = model.appColors
//                        ) {
//                            val values = patternText.extractIntPairsFromCsv().toMutableList()
//                            val value = values[cursor]
//                            val pattern = value.first
//                            val back = if(value.second < 0) -1 else 1
//                            var repetitions = value.second.absoluteValue - 1
//                            repetitions = if(repetitions < 1) 1 else repetitions
//                            setPattern(cursor, pattern, repetitions * back)
//                        }
//                        CustomButton(
//                            adaptSizeToIconButton = true,
//                            iconId = model.iconMap["Scarlatti"]!!, // [+1] Icon
//                            buttonSize = buttonSize.dp,
//                            iconColor = model.appColors.iconButtonIconColor,
//                            colors = model.appColors
//                        ) {
//                            val values = patternText.extractIntPairsFromCsv().toMutableList()
//                            val value = values[cursor]
//                            val pattern = value.first
//                            val back = if(value.second < 0) -1 else 1
//                            val repetitions = value.second.absoluteValue + 1
//                            setPattern(cursor, pattern, repetitions * back)
//                        }
                        CustomButton( // Replace edit
                            adaptSizeToIconButton = true,
                            text = "",
                            iconId = model.iconMap["glue"]!!,
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            val values = patternText.extractIntPairsFromCsv().toMutableList()
                            val value = values[cursor]
                            val (pattern, repetitions) = value
                            val back = if(repetitions < 0) -1 else 1
                            repetitionsDialogData.value = ListDialogData(
                                true,
                                (1..128).map { it.toString() },
                                repetitions.absoluteValue - 1,
                                lang.selectRitornello
                            ) { newRepetitions ->
                                setPattern(cursor, pattern, (newRepetitions + 1) * back)
                                ListDialogData(itemList = repetitionsDialogData.value.itemList)
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
                                    multiNumberDialogData.value.onSubmitButtonClick.invoke(
                                        patternText
                                    )
                                    onDismissRequest.invoke()
                                }
                                CustomButton(
                                    adaptSizeToIconButton = true,
                                    text = "",
                                    iconId = model.iconMap["edit"]!!,
                                    buttonSize = buttonSize.dp,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val values = patternText.extractIntPairsFromCsv().toMutableList()
                                    val value = values[cursor]
                                    val pattern = value.first
                                    val repetitions = value.second.absoluteValue
                                    val back = if(value.second < 0) -1 else 1
                                    listDialogData.value = ListDialogData(
                                        true, patternNames, pattern, lang.selectRhythm
                                    ) { index ->
                                        values[cursor] = Pair(index, repetitions * back)
                                        patternText = values.toIntPairsString()

                                        listDialogData.value =
                                            ListDialogData(itemList = listDialogData.value.itemList)
                                    }

                                }
                                CustomButton(
                                    adaptSizeToIconButton = true,
                                    text = "",
                                    iconId = model.iconMap["delete"]!!,
                                    buttonSize = buttonSize.dp,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val values = patternText.split(",").toMutableList()
                                    if (values.size > 1) {
                                        values.removeAt(cursor)
                                        patternText = values.joinToString(",")
                                        val newCursor = if (values.size > 1) cursor - 1 else 0
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
                                    var values = patternText.extractIntPairsFromCsv()
                                    val selectedValue = values[cursor]
                                    val pattern = selectedValue.first
                                    listDialogData.value = ListDialogData(
                                        true, patternNames, pattern, lang.selectRhythm
                                    ) { index ->
                                        val rebuilding = values.addOrInsert(Pair(index,1), cursor)
                                        values = rebuilding.first
                                        cursor = rebuilding.second
                                        patternText = values.toMutableList().toIntPairsString()

                                        listDialogData.value =
                                            ListDialogData(itemList = listDialogData.value.itemList)
                                    }

                                }

                            }



                    }
                }
            }
        }
    }




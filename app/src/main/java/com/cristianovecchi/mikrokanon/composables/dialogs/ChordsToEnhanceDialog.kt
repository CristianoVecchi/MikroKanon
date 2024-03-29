package com.cristianovecchi.mikrokanon.composables.dialogs

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
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.addOrInsert
import com.cristianovecchi.mikrokanon.composables.CustomButton
import com.cristianovecchi.mikrokanon.locale.Lang
import com.cristianovecchi.mikrokanon.ui.Dimensions
import kotlinx.coroutines.launch


@Composable
fun ChordsToEnhanceDialog(multiNumberDialogData: MutableState<MultiNumberDialogData>,
                          dimensions: Dimensions,
                          onDismissRequest: () -> Unit = {
                              multiNumberDialogData.value =
                                  MultiNumberDialogData(model = multiNumberDialogData.value.model,
                                      value = multiNumberDialogData.value.value) }) {
    if (multiNumberDialogData.value.dialogState) {
        val model = multiNumberDialogData.value.model
        val appColors = model.appColors
        val fontColor = appColors.dialogFontColor
        val backgroundColor = appColors.dialogBackgroundColor
        val lang = Lang.provideLanguage(model.getUserLangDef())
        val absPitchNames = multiNumberDialogData.value.names
        val pitchesDialogData by lazy { mutableStateOf(MultiListDialogData()) }
        val repetitionsDialogData by lazy { mutableStateOf(ListDialogData()) }
        val isSubSetSymbol = " [ ... ♪♪♪♪]  "
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            MultiListDialog(pitchesDialogData, dimensions, lang.OkButton, appColors, isSubSetSymbol)
            ListDialog(repetitionsDialogData, dimensions, lang.OkButton, appColors, fillPrevious = true)

            val width =
                if (dimensions.width <= 884) (dimensions.width / 10 * 8 / dimensions.dpDensity).toInt().dp
                else dimensions.dialogWidth
            val height = (dimensions.height / dimensions.dpDensity).toInt().dp
            Surface(
                modifier = Modifier
                    .width(width)
                    .height(height),
                color = backgroundColor,
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .height((dimensions.height / 6 * 5).dp)
                        .padding(10.dp)
                ) {
                    val weights = dimensions.dialogWeights
                    val modifierA = Modifier
                        .padding(8.dp)
                        .weight(weights.first + weights.second / 6 * 5)
                    val modifierB = Modifier
                        .weight(weights.second / 6)
                    val modifierC = Modifier
                        .padding(8.dp)
                        .weight(weights.third)

                    var chordsToEnhanceDatas by remember { mutableStateOf(multiNumberDialogData.value.anySequence.map{ it as ChordToEnhanceData} )}
                    var cursor by remember { mutableStateOf(0) }
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
                        val nCols = 1
                        val nRows = (chordsToEnhanceDatas.size / nCols) + 1
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
                                        if (index != chordsToEnhanceDatas.size) {
                                            val text = chordsToEnhanceDatas[index].describe(absPitchNames)
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
                                val rowIndex = if (chordsToEnhanceDatas.size <= nCols) 1 else cursor / nCols
                                listState.animateScrollToItem(rowIndex)
                            }
                        }
                    }
                    Row(
                        modifier = modifierB.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val dimensions by model.dimensions.asFlow().collectAsState(initial = model.dimensions.value!!)
                        val buttonSize = dimensions.dialogButtonSize
                        CustomButton( // Check edit
                            adaptSizeToIconButton = true,
                            text = "",
                            iconId = model.iconMap["sound"]!!,
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            val chordToEnhanceData = chordsToEnhanceDatas[cursor]
                            pitchesDialogData.value = MultiListDialogData(
                                true,
                                absPitchNames.reversed(),
                                chordToEnhanceData.absPitches.map{11 - it}.toSet(),
                                lang.selectChordsToEnhance,
                                isSubSetSymbol,
                                chordToEnhanceData.isSubSet
                            ) { pitchIndeces, isSubSet ->
                                val actualPitches = pitchIndeces.map{11 - it}.toSortedSet()
                                if(chordsToEnhanceDatas.all{it.absPitches != actualPitches || it.isSubSet != isSubSet}){
                                    val newCteDatas = chordsToEnhanceDatas.toMutableList()
                                    newCteDatas[cursor] = chordsToEnhanceDatas[cursor].copy(absPitches = actualPitches, isSubSet = isSubSet)
                                    chordsToEnhanceDatas = newCteDatas
                                }
                                MultiListDialogData(itemList = pitchesDialogData.value.itemList)
                            }
                        }
                        CustomButton( // Replace edit
                            adaptSizeToIconButton = true,
                            text = "",
                            iconId = model.iconMap["glue"]!!,
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            val chordToEnhanceData = chordsToEnhanceDatas[cursor]
                            repetitionsDialogData.value = ListDialogData(
                                true,
                                listOf(listOf("0 → DELETE", "1 → NO CHANGES"),(2..256).map { it.toString() }).flatten(),
                                chordToEnhanceData.repetitions,
                                lang.selectRitornello
                            ) { newRepetitions ->
                                val newCteDatas = chordsToEnhanceDatas.toMutableList()
                                val oldCteData = chordsToEnhanceDatas[cursor]
                                newCteDatas[cursor] = oldCteData.copy(repetitions = newRepetitions)
                                chordsToEnhanceDatas = newCteDatas
                                ListDialogData(itemList = repetitionsDialogData.value.itemList)
                            }
                        }
                    }
                    Row(
                        modifier = modifierC.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val dimensions by model.dimensions.asFlow().collectAsState(initial = model.dimensions.value!!)
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
                                chordsToEnhanceDatas//.distinctBy { it.absPitches  it.isSubSet}
                                    .sortedByDescending { it.repetitions }
                                    .joinToString(",") { it.toCsv()}
                            )
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
                            if (chordsToEnhanceDatas.size > 1) {
                                val newCteDatas = chordsToEnhanceDatas.toMutableList()
                                newCteDatas.removeAt(cursor)
                                chordsToEnhanceDatas = newCteDatas.toList()
                                val newCursor = if (chordsToEnhanceDatas.size > 1) cursor - 1 else 0
                                cursor = if (newCursor < 0) 0 else newCursor
                            } else {
                                chordsToEnhanceDatas = listOf(ChordToEnhanceData(setOf(),1))
                            }
                        }
                        CustomButton(
                            adaptSizeToIconButton = true,
                            iconId = model.iconMap["add"]!!,
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            //val cteData = chordsToEnhanceDatas[cursor]

                            pitchesDialogData.value = MultiListDialogData(
                                true,
                                absPitchNames.reversed(),
                                setOf(),
                                lang.selectChordsToEnhance,
                                isSubSetSymbol
                            ) { pitchIndeces, isSubSet ->
                                val actualPitches = pitchIndeces.map{11 - it}.toSortedSet()
                                if(chordsToEnhanceDatas.all{it.absPitches != actualPitches || it.isSubSet != isSubSet}){
                                    val rebuilding = chordsToEnhanceDatas.addOrInsert(
                                        ChordToEnhanceData(actualPitches, 1, isSubSet), cursor)
                                    chordsToEnhanceDatas = rebuilding.first
                                    cursor = rebuilding.second
                                }
                                ListDialogData(itemList = pitchesDialogData.value.itemList)
                            }
                        }
                    }
                }
            }
        }
    }
}



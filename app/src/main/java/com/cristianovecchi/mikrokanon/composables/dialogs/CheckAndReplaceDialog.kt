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
import com.cristianovecchi.mikrokanon.extractIntsFromCsv
import com.cristianovecchi.mikrokanon.locale.Lang
import com.cristianovecchi.mikrokanon.getGlissandoSymbols
import com.cristianovecchi.mikrokanon.ui.Dimensions
import kotlinx.coroutines.launch


@Composable
fun CheckAndReplaceDialog(multiNumberDialogData: MutableState<MultiNumberDialogData>,
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
        val checkList = CheckType.checkList()
        val checkNames = checkList.map{ it.describe() }
        val replaceNames = ReplaceType.titles
        val glissSymbol = getGlissandoSymbols().first
        val checkTypeDialogData by lazy { mutableStateOf(ListDialogData()) }
        val replaceTypeDialogData by lazy { mutableStateOf(ListDialogData()) }
        val stressDialogData by lazy { mutableStateOf(ListDialogData()) }
        val rangeDialogData by lazy { mutableStateOf(MultiFloatDialogData(model = model)) }
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            ListDialog(checkTypeDialogData, dimensions, lang.OkButton, appColors)
            ListDialog(replaceTypeDialogData, dimensions, lang.OkButton, appColors)
            ListDialog(stressDialogData, dimensions, lang.OkButton, appColors)
            PitchRangeDialog(rangeDialogData, dimensions, lang.OkButton)
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
                        .weight(weights.first + weights.second / 5 * 2)
                    val modifierB = Modifier
                        .weight(weights.second / 5 * 3)
                    val modifierC = Modifier
                        .padding(8.dp)
                        .weight(weights.third)

                    var checkAndReplaceDatas by remember { mutableStateOf(multiNumberDialogData.value.anySequence.map{ it as CheckAndReplaceData}) }
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
                        val nRows = (checkAndReplaceDatas.size / nCols) + 1
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
                                        if (index != checkAndReplaceDatas.size) {
                                            val text = checkAndReplaceDatas[index].describe(glissSymbol, lang.noteNames)
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
                                val rowIndex = if (checkAndReplaceDatas.size <= nCols) 1 else cursor / nCols
                                listState.animateScrollToItem(rowIndex)
                            }
                        }
                    }
                    Column(
                        modifier = modifierB.fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        val dimensions by model.dimensions.asFlow().collectAsState(initial = model.dimensions.value!!)
                        val buttonSize = dimensions.dialogButtonSize
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            CustomButton( // Check edit
                                iconId = model.iconMap["check"]!!,
                                buttonSize = buttonSize.dp,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val checkAndReplaceData = checkAndReplaceDatas[cursor]
                                checkTypeDialogData.value = ListDialogData(
                                    true,
                                    checkNames,
                                    CheckType.getIndex(checkAndReplaceData.check),
                                    lang.selectCheckType
                                ) { newCheckTypeIndex ->
                                    val newCnrDatas = checkAndReplaceDatas.toMutableList()
                                    newCnrDatas[cursor] = checkAndReplaceDatas[cursor].copy(check = checkList[newCheckTypeIndex])
                                    checkAndReplaceDatas = newCnrDatas
                                    ListDialogData(itemList = checkTypeDialogData.value.itemList)
                                }
                            }
                            CustomButton( // Replace edit
                                isActive = checkAndReplaceDatas[cursor].check !is CheckType.None,
                                iconId = model.iconMap["edit"]!!,
                                buttonSize = buttonSize.dp,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val checkAndReplaceData = checkAndReplaceDatas[cursor]
                                checkTypeDialogData.value = ListDialogData(
                                    true,
                                    replaceNames,
                                    replaceNames.indexOf(checkAndReplaceData.replace.title),
                                    lang.selectReplaceType
                                ) { newReplaceTypeIndex ->
                                    val newCnrDatas = checkAndReplaceDatas.toMutableList()
                                    val oldCnrData = checkAndReplaceDatas[cursor]
                                    val oldStress = oldCnrData.replace.stress
                                    val oldIsRetrograde = oldCnrData.replace.isRetrograde
                                    val oldAddGliss = oldCnrData.replace.addGliss
                                    newCnrDatas[cursor] = oldCnrData.copy(replace = ReplaceType.provideReplaceType(newReplaceTypeIndex, oldStress, oldIsRetrograde, oldAddGliss))
                                    checkAndReplaceDatas = newCnrDatas
                                    ListDialogData(itemList = replaceTypeDialogData.value.itemList)
                                }
                            }

                            CustomButton(
                                isActive = checkAndReplaceDatas[cursor].check !is CheckType.None && checkAndReplaceDatas[cursor].requiresGlissando(),
                                iconId = model.iconMap["glissando"]!!,
//                                iconId = model.iconMap["back"]!!,
                                buttonSize = buttonSize.dp,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val newCnrDatas = checkAndReplaceDatas.toMutableList()
                                val oldCnrData = checkAndReplaceDatas[cursor]
                                val oldReplace = oldCnrData.replace

                                newCnrDatas[cursor] = oldCnrData.copy(
                                    replace = oldReplace.clone(addGliss = !oldReplace.addGliss))
                                checkAndReplaceDatas = newCnrDatas
                                ListDialogData(itemList = stressDialogData.value.itemList)
                            }
                        }
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomButton( // range button
                                isActive = checkAndReplaceDatas[cursor].check !is CheckType.None,
                                iconId = model.iconMap["range"]!!,
                                buttonSize = buttonSize.dp,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val cnrDatas = checkAndReplaceDatas[cursor]
                                val limits = listOf(cnrDatas.range.first, cnrDatas.range.last)
                                rangeDialogData.value = MultiFloatDialogData(
                                    true,
                                    lang.selectRange,
                                    "${limits[0]},${limits[1]}",
                                    A0.toFloat(), C8.toFloat(), model
                                ) { rangeCsv ->
                                    val newCnrDatas = checkAndReplaceDatas.toMutableList()
                                    val oldCnrData = checkAndReplaceDatas[cursor]
                                    val (down, up) = rangeCsv.extractIntsFromCsv()
                                    newCnrDatas[cursor] = oldCnrData.copy(range = IntRange(down, up))
                                    checkAndReplaceDatas = newCnrDatas
                                    ListDialogData(itemList = stressDialogData.value.itemList)
                                }
                            }
                            CustomButton(
                                isActive = checkAndReplaceDatas[cursor].check !is CheckType.None && checkAndReplaceDatas[cursor].requiresRetrograde(),
                                iconId = model.iconMap["back"]!!,
                                buttonSize = buttonSize.dp,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val newCnrDatas = checkAndReplaceDatas.toMutableList()
                                val oldCnrData = checkAndReplaceDatas[cursor]
                                val oldReplace = oldCnrData.replace

                                newCnrDatas[cursor] = oldCnrData.copy(
                                    replace = oldReplace.clone(isRetrograde = !oldReplace.isRetrograde))
                                checkAndReplaceDatas = newCnrDatas
                                ListDialogData(itemList = stressDialogData.value.itemList)
                            }
                            CustomButton( // stress edit
                                isActive = checkAndReplaceDatas[cursor].check !is CheckType.None && checkAndReplaceDatas[cursor].requiresStress(),
                                iconId = model.iconMap["volume"]!!,
                                buttonSize = buttonSize.dp,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val cnrDatas = checkAndReplaceDatas[cursor]
                                val stresses = listOf(100,90,80,75,70,66, 60, 50, 45, 40, 35, 33,30, 25, 20, 16, 13, 10, 8, 6, 4, 2, 0)
                                stressDialogData.value = ListDialogData(
                                    true,
                                    stresses.map{"$it"},
                                    stresses.indexOf(cnrDatas.replace.stress),
                                    lang.selectStress
                                ) { stressIndex ->
                                    val newCnrDatas = checkAndReplaceDatas.toMutableList()
                                    val oldCnrData = checkAndReplaceDatas[cursor]
                                    val oldReplace = oldCnrData.replace
                                    newCnrDatas[cursor] = oldCnrData.copy(replace = oldReplace.clone(stresses[stressIndex]))
                                    checkAndReplaceDatas = newCnrDatas
                                    ListDialogData(itemList = stressDialogData.value.itemList)
                                }
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
                            iconId = model.iconMap["done"]!!,
                            buttonSize = buttonSize.dp,
                            iconColor = Color.Green,
                            colors = model.appColors
                        ) {
                            multiNumberDialogData.value.onSubmitButtonClick.invoke(
                                checkAndReplaceDatas.joinToString(",") { it.toCsv()}
                            )
                            onDismissRequest.invoke()
                        }

                        CustomButton(
                            iconId = model.iconMap["delete"]!!,
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            if (checkAndReplaceDatas.size > 1) {
                                val newHarmDatas = checkAndReplaceDatas.toMutableList()
                                newHarmDatas.removeAt(cursor)
                                checkAndReplaceDatas = newHarmDatas.toList()
                                val newCursor = if (checkAndReplaceDatas.size > 1) cursor - 1 else 0
                                cursor = if (newCursor < 0) 0 else newCursor
                            }
                        }
                        CustomButton(
                            iconId = model.iconMap["add"]!!,
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            val checkAndReplaceData = checkAndReplaceDatas[cursor]
                            checkTypeDialogData.value = ListDialogData(
                                true,
                                checkNames,
                                CheckType.getIndex(checkAndReplaceData.check),
                                lang.selectCheckType
                            ) { newCheckTypeIndex ->
                                val rebuilding = checkAndReplaceDatas.addOrInsert(CheckAndReplaceData(checkList[newCheckTypeIndex], checkAndReplaceData.replace, checkAndReplaceData.range), cursor)
                                checkAndReplaceDatas = rebuilding.first
                                cursor = rebuilding.second
                                ListDialogData(itemList = checkTypeDialogData.value.itemList)
                            }
                        }
                    }
                }
            }
        }
    }
}



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
import com.cristianovecchi.mikrokanon.AIMUSIC.DrumKits
import com.cristianovecchi.mikrokanon.AIMUSIC.DrumsData
import com.cristianovecchi.mikrokanon.AIMUSIC.DrumsType
import com.cristianovecchi.mikrokanon.addOrInsert
import com.cristianovecchi.mikrokanon.composables.CustomButton
import com.cristianovecchi.mikrokanon.locale.Lang
import com.cristianovecchi.mikrokanon.ui.Dimensions
import kotlinx.coroutines.launch


@Composable
fun DrumsDialog(multiNumberDialogData: MutableState<MultiNumberDialogData>,
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
        val drumKits = DrumKits.values().map { it.title }
        val drumsTypes = DrumsType.values().map{ it.title }
        val lang = Lang.provideLanguage(model.getUserLangDef())

        val drumsTypeDialogData by lazy { mutableStateOf(ListDialogData()) }
        val drumKitsDialogData by lazy { mutableStateOf(ListDialogData()) }
        val volumeDialogData by lazy { mutableStateOf(ListDialogData()) }
        val densityDialogData by lazy { mutableStateOf(ListDialogData()) }
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            ListDialog(drumsTypeDialogData, dimensions, lang.OkButton, appColors)
            ListDialog(drumKitsDialogData, dimensions, lang.OkButton, appColors)
            ListDialog(volumeDialogData, dimensions, lang.OkButton, appColors)
            ListDialog(densityDialogData, dimensions, lang.OkButton, appColors)
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

                    var drumsDatas by remember { mutableStateOf(multiNumberDialogData.value.anySequence.map{ it as DrumsData}) }
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
                        val nRows = (drumsDatas.size / nCols) + 1
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
                                        if (index != drumsDatas.size) {
                                            val text = drumsDatas[index].describe()
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
                                val rowIndex = if (drumsDatas.size <= nCols) 1 else cursor / nCols
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
                        CustomButton(
                            adaptSizeToIconButton = true,
                            text = "",
                            iconId = model.iconMap["edit"]!!,
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            val drumData = drumsDatas[cursor]
                            drumsTypeDialogData.value = ListDialogData(
                                true,
                                drumsTypes,
                                drumData.type.ordinal,
                                lang.selectDrumsType
                            ) { newDrumsType ->
                                val newDrumsDatas = drumsDatas.toMutableList()
                                newDrumsDatas[cursor] = drumsDatas[cursor].copy(type = DrumsType.values()[newDrumsType])
                                drumsDatas = newDrumsDatas
                                ListDialogData(itemList = drumsTypeDialogData.value.itemList)
                            }
                        }
                        CustomButton(
                            adaptSizeToIconButton = true,
                            text = "",
                            isActive = drumsDatas[cursor].type != DrumsType.NONE,
                            iconId = model.iconMap["sound"]!!,
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            val drumData = drumsDatas[cursor]
                            drumKitsDialogData.value = ListDialogData(
                                true,
                                drumKits,
                                drumData.drumKit.ordinal,
                                lang.selectDrumKit
                            ) { drumKitIndex ->
                                val newDrumsDatas = drumsDatas.toMutableList()
                                newDrumsDatas[cursor] = drumsDatas[cursor].copy(drumKit = DrumKits.values()[drumKitIndex])
                                drumsDatas = newDrumsDatas
                                ListDialogData(itemList = drumKitsDialogData.value.itemList)
                            }
                        }
                        CustomButton(
                            adaptSizeToIconButton = true,
                            text = "",
                            isActive = drumsDatas[cursor].type != DrumsType.NONE,
                            iconId = model.iconMap["density"]!!,
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            val drumData = drumsDatas[cursor]
                            val densities = listOf(100,90,80,70,60,50,45,40,35,30,26,23,20,16,13,10,8,6,4,2)
                            densityDialogData.value = ListDialogData(
                                true,
                                densities.map{"$it%"},
                                densities.indexOf((drumData.volume * 100).toInt()),
                                lang.selectDrumsDensity
                            ) { densityIndex ->
                                val newDrumsDatas = drumsDatas.toMutableList()
                                newDrumsDatas[cursor] = drumsDatas[cursor].copy(density = densities[densityIndex] / 100f)
                                drumsDatas = newDrumsDatas
                                ListDialogData(itemList = densityDialogData.value.itemList)
                            }
                        }
                        CustomButton(
                            adaptSizeToIconButton = true,
                            text = "",
                            isActive = drumsDatas[cursor].type != DrumsType.NONE,
                            iconId = model.iconMap["volume"]!!,
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            val drumData = drumsDatas[cursor]
                            val volumes = listOf(100,90,80,70,60,50,45,40,35,30,26,23,20,16,13,10,8,6,4,2)
                            volumeDialogData.value = ListDialogData(
                                true,
                                volumes.map{"$it%"},
                                volumes.indexOf((drumData.volume * 100).toInt()),
                                lang.selectDrumsVolume
                            ) { volumeIndex ->
                                val newDrumsDatas = drumsDatas.toMutableList()
                                newDrumsDatas[cursor] = drumsDatas[cursor].copy(volume = volumes[volumeIndex] / 100f)
                                drumsDatas = newDrumsDatas
                                ListDialogData(itemList = volumeDialogData.value.itemList)
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
                                drumsDatas.joinToString(",") { it.toCsv()}
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
                            if (drumsDatas.size > 1) {
                                val newDrumsDatas = drumsDatas.toMutableList()
                                newDrumsDatas.removeAt(cursor)
                                drumsDatas = newDrumsDatas.toList()
                                val newCursor = if (drumsDatas.size > 1) cursor - 1 else 0
                                cursor = if (newCursor < 0) 0 else newCursor
                            }
                        }
                        CustomButton(
                            adaptSizeToIconButton = true,
                            iconId = model.iconMap["add"]!!,
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            val drumsData = drumsDatas[cursor]
                            drumsTypeDialogData.value = ListDialogData(
                                true,
                                drumsTypes,
                                drumsData.type.ordinal,
                                lang.selectDrumsType
                            ) { newDrumsType ->
                                val rebuilding = drumsDatas.addOrInsert(
                                    DrumsData(type = DrumsType.values()[newDrumsType]), cursor)
                                drumsDatas = rebuilding.first
                                cursor = rebuilding.second
                                ListDialogData(itemList = drumsTypeDialogData.value.itemList)
                            }
                        }
                    }
                }
            }
        }
    }
}



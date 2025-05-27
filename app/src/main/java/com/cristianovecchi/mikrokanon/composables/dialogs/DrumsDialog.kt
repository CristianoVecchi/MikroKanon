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
import com.cristianovecchi.mikrokanon.toIntPairsString
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
        val groupingDialogData by lazy { mutableStateOf(GroupingDialogData())}
        val resizeDialogData by lazy { mutableStateOf(ListDialogData())}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            GroupingListDialog(groupingDialogData, dimensions, lang.OkButton, appColors)
            ListDialog(drumsTypeDialogData, dimensions, lang.OkButton, appColors)
            ListDialog(drumKitsDialogData, dimensions, lang.OkButton, appColors)
            ListDialog(volumeDialogData, dimensions, lang.OkButton, appColors)
            ListDialog(densityDialogData, dimensions, lang.OkButton, appColors)
            ListDialog(resizeDialogData, dimensions, lang.OkButton, appColors)
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
                        .weight(weights.first + weights.second / 2)
                    val modifierB = Modifier
                        .weight(weights.second / 2)
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
                    Column(
                        modifierB.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val dimensions by model.dimensions.asFlow().collectAsState(initial = model.dimensions.value!!)
                        val buttonSize = dimensions.dialogButtonSize
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "",
                                iconId = model.iconMap["edit"]!!,
                                buttonSize = buttonSize.dp,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val drumData = drumsDatas[cursor]
                                val pattern = drumData.pattern
                                drumsTypeDialogData.value = ListDialogData(
                                    true,
                                    drumsTypes,
                                    drumData.type.ordinal,
                                    lang.selectDrumsType
                                ) { newDrumsType ->
                                    if (newDrumsType == 1) { // choose pattern
                                        val itemGroups = RhythmPatterns.values()
                                            .groupBy { it.type }.values.map { it.map { it.title } }
                                        val groupNames = RhythmType.values().map { it.name }
                                        groupingDialogData.value = GroupingDialogData(
                                            true, itemGroups, groupNames,
                                            pattern,
                                            lang.selectRhythm
                                        ) { index ->
                                            ListDialogData(itemList = drumsTypeDialogData.value.itemList)
                                            val newDrumData = drumData.copy(
                                                type = DrumsType.PATTERN,
                                                pattern = index
                                            )
                                            groupingDialogData.value =
                                                GroupingDialogData(
                                                    itemGroups = groupingDialogData.value.itemGroups,
                                                    groupNames = groupingDialogData.value.groupNames,
                                                    selectedListDialogItem = index
                                                )
                                            val newDrumsDatas = drumsDatas.toMutableList()
                                            newDrumsDatas[cursor] = newDrumData
                                            drumsDatas = newDrumsDatas
                                        }
                                    } else {
                                        val newDrumsDatas = drumsDatas.toMutableList()
                                        newDrumsDatas[cursor] =
                                            drumsDatas[cursor].copy(type = DrumsType.values()[newDrumsType])
                                        drumsDatas = newDrumsDatas
                                        ListDialogData(itemList = drumsTypeDialogData.value.itemList)
                                    }

                                }
                            }
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "",
                                iconId = model.iconMap["pattern"]!!,
                                buttonSize = buttonSize.dp,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val drumData = drumsDatas[cursor]
                                val pattern = drumData.pattern
                                val itemGroups = RhythmPatterns.values()
                                    .groupBy { it.type }.values.map { it.map { it.title } }
                                val groupNames = RhythmType.values().map { it.name }
                                groupingDialogData.value = GroupingDialogData(
                                    true, itemGroups, groupNames,
                                    pattern,
                                    lang.selectRhythm
                                ) { index ->
                                    val newDrumData = drumData.copy(
                                        type = DrumsType.PATTERN,
                                        pattern = index
                                    )
                                    val newDrumsDatas = drumsDatas.toMutableList()
                                    newDrumsDatas[cursor] = newDrumData
                                    drumsDatas = newDrumsDatas
                                }
                            }
                            CustomButton(
                                adaptSizeToIconButton = true,
                                text = "",
                                isActive = drumsDatas[cursor].type == DrumsType.PATTERN,
                                iconId = model.iconMap["expand"]!!,
                                buttonSize = buttonSize.dp,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val drumData = drumsDatas[cursor]
                                val selectedValue = drumData.resize
                                val values = listOf(
                                    8f, 7.875f, 7.75f, 7.66f, 7.625f, 7.5f, 7.375f, 7.33f, 7.25f, 7.125f,
                                    7f, 6.875f, 6.75f, 6.66f, 6.625f, 6.5f, 6.375f, 6.33f, 6.25f, 6.125f,
                                    6f, 5.875f, 5.75f, 5.66f, 5.625f, 5.5f, 5.375f, 5.33f, 5.25f, 5.125f,
                                    5f, 4.875f, 4.75f, 4.66f, 4.625f, 4.5f, 4.375f, 4.33f, 4.25f, 4.125f,
                                    4f, 3.875f, 3.75f, 3.66f, 3.625f, 3.5f, 3.375f, 3.33f, 3.25f, 3.125f,
                                    3f, 2.875f, 2.75f, 2.66f, 2.625f, 2.5f, 2.375f, 2.33f, 2.25f, 2.125f,
                                    2f, 1.875f, 1.75f, 1.66f, 1.625f, 1.5f, 1.375f, 1.33f, 1.25f, 1.125f,
                                    1f, 0.875f, 0.75f, 0.66f, 0.625f, 0.5f, 0.375f, 0.33f, 0.25f, 0.125f
                                ).reversed()
                                resizeDialogData.value = ListDialogData(
                                    true,
                                    values.map{"${(it * 100).toInt()}%"},
                                    values.indexOf(selectedValue),
                                    lang.selectDrumsResize
                                ) { index ->
                                    val newDrumData = drumData.copy(
                                        resize = values[index]
                                    )
                                    val newDrumsDatas = drumsDatas.toMutableList()
                                    newDrumsDatas[cursor] = newDrumData
                                    drumsDatas = newDrumsDatas
                                }
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                                val densities = listOf(100,95,90,85,80,75,70,65,60,55,50,45,40,35,30,25,20,15,10,5)
                                densityDialogData.value = ListDialogData(
                                    true,
                                    densities.map{"$it%"},
                                    densities.indexOf((drumData.density * 100).toInt()),
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
                            val pattern = drumsData.pattern
                            drumsTypeDialogData.value = ListDialogData(
                                true,
                                drumsTypes,
                                drumsData.type.ordinal,
                                lang.selectDrumsType
                            ) { newDrumsType ->
                                if(newDrumsType == 1){ // choose pattern
                                    val itemGroups = RhythmPatterns.values()
                                        .groupBy { it.type }.values.map{ it.map{ it.title}}
                                    val groupNames = RhythmType.values().map { it.name }
                                    groupingDialogData.value = GroupingDialogData(
                                        true, itemGroups, groupNames,
                                        pattern,
                                        lang.selectRhythm
                                    ) { index ->
                                        ListDialogData(itemList = drumsTypeDialogData.value.itemList)
                                        val newDrumData = drumsData.copy(DrumsType.PATTERN, pattern = index)
                                        groupingDialogData.value =
                                            GroupingDialogData(itemGroups = groupingDialogData.value.itemGroups,
                                                groupNames = groupingDialogData.value.groupNames,
                                                selectedListDialogItem = index
                                            )
                                        val rebuilding = drumsDatas.addOrInsert(newDrumData, cursor)
                                        drumsDatas = rebuilding.first
                                        cursor = rebuilding.second
                                    }
                                } else {
                                    val newDrumData = drumsData.copy(type = DrumsType.values()[newDrumsType])
                                    val rebuilding = drumsDatas.addOrInsert(newDrumData, cursor)
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
}



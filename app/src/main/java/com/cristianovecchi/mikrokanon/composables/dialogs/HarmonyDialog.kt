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
import com.cristianovecchi.mikrokanon.extractIntsFromCsv
import com.cristianovecchi.mikrokanon.ui.Dimensions
import kotlinx.coroutines.launch


@Composable
fun HarmonyDialog(multiNumberDialogData: MutableState<MultiNumberDialogData>,
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
        val ratedChordsInstruments = chordsInstruments.mapIndexed{i, ch ->
            "${ListaStrumenti.getNameByIndex(i)}${if(starredChordsInstruments.contains(i)) " *" else ""}"
        }
        val lang = Lang.provideLanguage(model.getUserLangDef())
        val harmNames = HarmonizationType.values().map{ it.title }
        val styleNames = HarmonizationStyle.values().map{ it.title }
        val harmTypeDialogData by lazy { mutableStateOf(ListDialogData()) }
        val instrumentDialogData by lazy { mutableStateOf(ListDialogData()) }
        val volumeDialogData by lazy { mutableStateOf(ListDialogData()) }
        val octavesDialogData by lazy { mutableStateOf(MultiListDialogData()) }
        val styleDialogData by lazy { mutableStateOf(ListDialogData()) }
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            ListDialog(harmTypeDialogData, dimensions, lang.OkButton, appColors)
            ListDialog(instrumentDialogData, dimensions, lang.OkButton, appColors)
            ListDialog(volumeDialogData, dimensions, lang.OkButton, appColors)
            MultiListDialog(octavesDialogData, dimensions, lang.OkButton, appColors)
            ListDialog(styleDialogData, dimensions, lang.OkButton, appColors)
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

                    var harmDatas by remember { mutableStateOf(multiNumberDialogData.value.anySequence.map{ it as HarmonizationData}) }
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
                        val nRows = (harmDatas.size / nCols) + 1
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
                                        if (index != harmDatas.size) {
                                            val text = harmDatas[index].describe()
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
                                val rowIndex = if (harmDatas.size <= nCols) 1 else cursor / nCols
                                listState.animateScrollToItem(rowIndex)
                            }
                        }
                    }
                    Column(
                        modifier = modifierB.fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val dimensions by model.dimensions.asFlow()
                            .collectAsState(initial = model.dimensions.value!!)
                        val buttonSize = dimensions.dialogButtonSize
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                                val harmData = harmDatas[cursor]
                                harmTypeDialogData.value = ListDialogData(
                                    true,
                                    harmNames,
                                    harmData.type.ordinal,
                                    lang.selectHarmonizationType
                                ) { newHarmonizationType ->
                                    val newHarmDatas = harmDatas.toMutableList()
                                    newHarmDatas[cursor] =
                                        harmDatas[cursor].copy(type = HarmonizationType.values()[newHarmonizationType])
                                    harmDatas = newHarmDatas
                                    ListDialogData(itemList = harmTypeDialogData.value.itemList)
                                }
                            }
                            CustomButton(
                                isActive = harmDatas[cursor].type != HarmonizationType.NONE,
                                iconId = model.iconMap["accompanist"]!!,
                                buttonSize = buttonSize.dp,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val harmData = harmDatas[cursor]
                                harmTypeDialogData.value = ListDialogData(
                                    true,
                                    styleNames,
                                    harmData.style.ordinal,
                                    lang.selectHarmonizationStyle
                                ) { newHarmonizationStyle ->
                                    val newHarmDatas = harmDatas.toMutableList()
                                    newHarmDatas[cursor] =
                                        harmDatas[cursor].copy(style = HarmonizationStyle.values()[newHarmonizationStyle])
                                    harmDatas = newHarmDatas
                                    ListDialogData(itemList = harmTypeDialogData.value.itemList)
                                }
                            }
                         }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CustomButton(
                                    adaptSizeToIconButton = true,
                                    text = "",
                                    isActive = harmDatas[cursor].type != HarmonizationType.NONE,
                                    iconId = model.iconMap["sound"]!!,
                                    buttonSize = buttonSize.dp,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val harmData = harmDatas[cursor]
                                    harmTypeDialogData.value = ListDialogData(
                                        true,
                                        ratedChordsInstruments,
                                        chordsInstruments.indexOf(harmData.instrument),
                                        lang.selectHarmonizationInstruments
                                    ) { instrumentIndex ->
                                        val newHarmDatas = harmDatas.toMutableList()
                                        newHarmDatas[cursor] =
                                            harmDatas[cursor].copy(instrument = chordsInstruments[instrumentIndex])
                                        harmDatas = newHarmDatas
                                        ListDialogData(itemList = harmTypeDialogData.value.itemList)
                                    }
                                }
                                CustomButton( // octaves button
                                    isActive = harmDatas[cursor].type != HarmonizationType.NONE,
                                    iconId = model.iconMap["range"]!!,
                                    buttonSize = buttonSize.dp,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val harmData = harmDatas[cursor]
                                    val octaves = harmData.convertFromOctavesByte()
                                    octavesDialogData.value = MultiListDialogData(
                                        true,
                                        (1..7).map{it.toString()}.reversed(),
                                        octaves.map{7 - it}.toSet(),
                                        lang.selectOctaves
                                    ) { octaveList, _ ->
                                        val newHarmDatas = harmDatas.toMutableList()
                                        val correctedOctaveList = if(octaveList.isEmpty()) listOf(0,1,2,3,4) else octaveList
                                        newHarmDatas[cursor] =
                                            harmDatas[cursor].copy(octavesByte = correctedOctaveList.map{7 - it}.convertToOctavesByte())
                                        harmDatas = newHarmDatas
                                        ListDialogData(itemList = harmTypeDialogData.value.itemList)
                                    }
                                }
                                CustomButton(
                                    adaptSizeToIconButton = true,
                                    text = "",
                                    isActive = harmDatas[cursor].type != HarmonizationType.NONE,
                                    iconId = model.iconMap["volume"]!!,
                                    buttonSize = buttonSize.dp,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val harmData = harmDatas[cursor]
                                    val volumes = listOf(
                                        100,
                                        90,
                                        80,
                                        70,
                                        60,
                                        50,
                                        45,
                                        40,
                                        35,
                                        30,
                                        26,
                                        23,
                                        20,
                                        16,
                                        13,
                                        10,
                                        8,
                                        6,
                                        4,
                                        2
                                    )
                                    harmTypeDialogData.value = ListDialogData(
                                        true,
                                        volumes.map { "$it%" },
                                        volumes.indexOf((harmData.volume * 100).toInt()),
                                        lang.selectHarmonizationVolume
                                    ) { volumeIndex ->
                                        val newHarmDatas = harmDatas.toMutableList()
                                        newHarmDatas[cursor] =
                                            harmDatas[cursor].copy(volume = volumes[volumeIndex] / 100f)
                                        harmDatas = newHarmDatas
                                        ListDialogData(itemList = harmTypeDialogData.value.itemList)
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
                                harmDatas.joinToString(",") { it.toCsv()}
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
                            if (harmDatas.size > 1) {
                                val newHarmDatas = harmDatas.toMutableList()
                                newHarmDatas.removeAt(cursor)
                                harmDatas = newHarmDatas.toList()
                                val newCursor = if (harmDatas.size > 1) cursor - 1 else 0
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
                            val harmData = harmDatas[cursor]
                            harmTypeDialogData.value = ListDialogData(
                                true,
                                harmNames,
                                harmData.type.ordinal,
                                lang.selectHarmonizationType
                            ) { newHarmonizationType ->
                                val rebuilding = harmDatas.addOrInsert(
                                    harmData.copy(type = HarmonizationType.values()[newHarmonizationType]), cursor)
                                harmDatas = rebuilding.first
                                cursor = rebuilding.second
                                ListDialogData(itemList = harmTypeDialogData.value.itemList)
                            }
                        }
                    }
                }
            }
        }
    }
}



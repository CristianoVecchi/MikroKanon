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
import com.cristianovecchi.mikrokanon.toIntPairsString
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
        val harmTypeDialogData by lazy { mutableStateOf(ListDialogData()) }
        val harmDivisionDialogData by lazy { mutableStateOf(ListDialogData()) }
        val densityDialogData by lazy { mutableStateOf(ListDialogData()) }
        val instrumentDialogData by lazy { mutableStateOf(MultiListDialogData()) }
        val volumeDialogData by lazy { mutableStateOf(PercentageDialogData(model = model)) }
        val octavesDialogData by lazy { mutableStateOf(MultiListDialogData()) }
        val groupingDialogData by lazy { mutableStateOf(GroupingDialogData())}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            ListDialog(harmTypeDialogData, dimensions, lang.OkButton, appColors)
            ListDialog(harmDivisionDialogData, dimensions, lang.OkButton, appColors)
            ListDialog(densityDialogData, dimensions, lang.OkButton, appColors)
            MultiListDialog(instrumentDialogData, dimensions, lang.OkButton, appColors)
            PercentageDialog(volumeDialogData, dimensions, lang.OkButton, appColors)
            MultiListDialog(octavesDialogData, dimensions, lang.OkButton, appColors)
            GroupingListDialog(groupingDialogData, dimensions, lang.OkButton, appColors)
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
//                    val fontWeight = FontWeight.Normal
//                    val buttonPadding = 4.dp
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
//                        val dimensions by model.dimensions.asFlow()
//                            .collectAsState(initial = model.dimensions.value!!)
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
                                val divValues = HarmonizationDivision.values()
                                harmTypeDialogData.value = ListDialogData(
                                    true,
                                    harmNames,
                                    harmData.type.ordinal,
                                    lang.selectHarmonizationType
                                ) { newHarmonizationType ->
                                    val newHarmDatas = harmDatas.toMutableList()
                                    if(newHarmonizationType != 0) {
                                        harmDivisionDialogData.value = ListDialogData(
                                            true,
                                            divValues.map { it.symbol },
                                            harmData.division.ordinal,
                                            lang.selectHarmonizationDivision
                                        ) { newHarmonizationDivision ->
                                            newHarmDatas[cursor] = harmDatas[cursor].copy(
                                                    type = HarmonizationType.values()[newHarmonizationType],
                                                    division = divValues[newHarmonizationDivision]
                                                )
                                            harmDatas = newHarmDatas
                                            //ListDialogData(itemList = harmTypeDialogData.value.itemList)
                                        }
                                    } else {
                                        newHarmDatas[cursor] = harmDatas[cursor].copy(type = HarmonizationType.values()[newHarmonizationType],)
                                        harmDatas = newHarmDatas
                                    }
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
                                val itemGroups = HarmonizationStyle.values()
                                    .groupBy { it.type }.values.map { it.map { it.title } }
                                val groupNames = StyleType.values().map { it.name }

                                groupingDialogData.value = GroupingDialogData(
                                    true, itemGroups, groupNames,
                                    harmData.style.ordinal,
                                    lang.selectHarmonizationStyle
                                ) { index ->
                                    val newHarmDatas = harmDatas.toMutableList()
                                    val newStyle = HarmonizationStyle.values()[index]
//                                    val maxDensity = newStyle.maxDensity
//                                    val checkedDensity = if(harmData.density > maxDensity) maxDensity else harmData.density
                                    newHarmDatas[cursor] =
                                        harmDatas[cursor].copy(style = newStyle)//, density = checkedDensity)
                                    harmDatas = newHarmDatas
                                    GroupingDialogData(
                                        itemGroups = groupingDialogData.value.itemGroups,
                                        groupNames = groupingDialogData.value.groupNames,
                                        selectedListDialogItem = index
                                    )
                                }
                            }
                            CustomButton( // density
                                isActive = harmDatas[cursor].style.maxDensity > 1,
                                iconId = model.iconMap["density"]!!,
                                buttonSize = buttonSize.dp,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val harmData = harmDatas[cursor]
                                val maxDensity = harmData.style.maxDensity
                                val initialValue = if(harmData.density > maxDensity) maxDensity -1 else harmData.density -1
                                densityDialogData.value = ListDialogData(
                                    true,
                                    (0 until maxDensity).map{ it +1 }.map{ it.toString() },
                                    initialValue,
                                    lang.selectStyleDensity
                                ) { densityIndex ->
                                    val newHarmDatas = harmDatas.toMutableList()
                                    newHarmDatas[cursor] =
                                        harmDatas[cursor].copy(density = densityIndex + 1)
                                    harmDatas = newHarmDatas
                                    ListDialogData(itemList = densityDialogData.value.itemList)
                                }
                            }
                            CustomButton( // style flow
                                isActive = harmDatas[cursor].style.hasFlow,
                                iconId = model.iconMap["arpeggio"]!!,
                                buttonSize = buttonSize.dp,
                                iconColor = model.appColors.iconButtonIconColor,
                                colors = model.appColors
                            ) {
                                val newHarmDatas = harmDatas.toMutableList()
                                val oldHarmData = harmDatas[cursor]
                                val newFlow = !oldHarmData.isFlow
                                newHarmDatas[cursor] = oldHarmData.copy(isFlow = newFlow)
                                harmDatas = newHarmDatas
                                ListDialogData(itemList = harmTypeDialogData.value.itemList)
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
                                    instrumentDialogData.value = MultiListDialogData(
                                        true,
                                        ratedChordsInstruments,
                                        harmData.instruments.map{chordsInstruments.indexOf(it)}.toSet(),
                                        lang.selectHarmonizationInstruments
                                    ) { instrumentIndices, _ ->
                                        val newHarmDatas = harmDatas.toMutableList()
                                        newHarmDatas[cursor] =
                                            harmDatas[cursor].copy(instruments = if(instrumentIndices.isEmpty()) listOf(48) else instrumentIndices)
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
                                        MultiListDialogData(itemList = harmTypeDialogData.value.itemList)
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

                                    volumeDialogData.value = PercentageDialogData(
                                        true, lang.selectHarmonizationVolume,
                                        harmData.volume, firstRendering = true, model = model
                                    ) { volumePercentage ->
                                        val newHarmDatas = harmDatas.toMutableList()
                                        newHarmDatas[cursor] =
                                            harmDatas[cursor].copy(volume = volumePercentage)
                                        harmDatas = newHarmDatas
                                        PercentageDialogData(model = model)
                                    }
                                }
                                CustomButton(
                                    isActive = harmDatas[cursor].style.hasDirection,
                                    iconId = model.iconMap["horizontal_movements"]!!,
                                    buttonSize = buttonSize.dp,
                                    iconColor = model.appColors.iconButtonIconColor,
                                    colors = model.appColors
                                ) {
                                    val newHarmDatas = harmDatas.toMutableList()
                                    val oldHarmData = harmDatas[cursor]
                                    val directions = HarmonizationDirection.values()
                                    val newDirection = directions[(oldHarmData.direction.ordinal + 1) % directions.size]
                                    newHarmDatas[cursor] = oldHarmData.copy(direction = newDirection)
                                    harmDatas = newHarmDatas
                                    ListDialogData(itemList = harmTypeDialogData.value.itemList)
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
                            val divValues = HarmonizationDivision.values()
                            harmTypeDialogData.value = ListDialogData(
                                true,
                                harmNames,
                                harmData.type.ordinal,
                                lang.selectHarmonizationType
                            ) { newHarmonizationType ->
                                if(newHarmonizationType != 0) {
                                    harmDivisionDialogData.value = ListDialogData(
                                        true,
                                        divValues.map { it.symbol },
                                        harmData.division.ordinal,
                                        lang.selectHarmonizationDivision
                                    ) { newHarmonizationDivision ->
                                        val rebuilding = harmDatas.addOrInsert(
                                            harmData.copy(type = HarmonizationType.values()[newHarmonizationType],division = divValues[newHarmonizationDivision]), cursor)
                                        harmDatas = rebuilding.first
                                        cursor = rebuilding.second
                                        //ListDialogData(itemList = harmTypeDialogData.value.itemList)
                                    }
                                } else {
                                    val rebuilding = harmDatas.addOrInsert(
                                        harmData.copy(type = HarmonizationType.values()[newHarmonizationType]), cursor)
                                    harmDatas = rebuilding.first
                                    cursor = rebuilding.second
                                }
                                ListDialogData(itemList = harmTypeDialogData.value.itemList)
                            }
                        }
                    }
                }
            }
        }
    }
}



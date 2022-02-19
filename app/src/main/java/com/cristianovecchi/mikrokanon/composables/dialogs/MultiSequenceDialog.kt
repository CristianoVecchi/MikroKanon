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
import com.cristianovecchi.mikrokanon.AIMUSIC.Clip
import com.cristianovecchi.mikrokanon.AIMUSIC.toStringAll
import com.cristianovecchi.mikrokanon.AIMUSIC.transpose
import com.cristianovecchi.mikrokanon.composables.CustomButton
import com.cristianovecchi.mikrokanon.ui.Dimensions
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun MultiSequenceDialog(multiNumberDialogData: MutableState<MultiNumberDialogData>,
                        dimensions: Dimensions,
                        onDismissRequest: () -> Unit = { multiNumberDialogData.value = MultiNumberDialogData(model = multiNumberDialogData.value.model, value = multiNumberDialogData.value.value) }) {

    if (multiNumberDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        val model = multiNumberDialogData.value.model
        val lang = Lang.provideLanguage(model.getUserLangDef())
        val notesNames = lang.noteNames
        //val sequenceTexts = sequences.map{ it.toStringAll(notesNames, model.zodiacSignsActive, model.zodiacEmojisActive) }
        val listDialogData by lazy { mutableStateOf(ListDialogData())}
        val allSequences = model.sequences.value!!.map{ it.map{it.abstractNote}.cutAdjacentRepetitions()}.map{ Clip.convertAbsPitchesToClips(it)}
        var sequences by mutableStateOf(multiNumberDialogData.value.intSequences.map{it.cutAdjacentRepetitions()})
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {

            ListDialog(listDialogData, dimensions, lang.OKbutton)
            val width = if(dimensions.width <= 884) (dimensions.width / 10 * 8 / dimensions.dpDensity).toInt().dp
            else dimensions.dialogWidth
            val height = (dimensions.height / dimensions.dpDensity).toInt().dp

            Surface(
                modifier = Modifier
                    .width(width)
                    .height(height),
                shape = RoundedCornerShape(10.dp)
            ) {

                Column(modifier = Modifier
                    .height((dimensions.height / 6 * 5).dp)
                    .padding(10.dp)) {
                    val weights = dimensions.dialogWeights
                    val modifierA = Modifier
                        //.fillMaxSize()
                        .padding(8.dp)
                        .weight(weights.first + weights.second / 7 * 6 + weights.third / 3)
                    val modifierB = Modifier
                        //.fillMaxSize()
                        .weight(weights.second / 7)
                    val modifierC = Modifier
                        //.fillMaxSize()
                        .padding(8.dp)
                        .weight(weights.third / 3 * 2)
                    //var sequencesCsv by remember { mutableStateOf(multiNumberDialogData.value.value) }
                    //val pairs = sequencesCsv.extractIntPairsFromCsv()
                    //val sequences = pairs.filter{ it.first < allSequences.size }.map{ allSequences[it.first]}

                    var cursor by remember { mutableStateOf(0) }
                    val transposeMap = remember { mutableStateMapOf<Int, Int>()}
//                    val setPattern = { index: Int, newPattern: Int, newRepetitions: Int ->
//                        val pairs = sequencesCsv.extractIntPairsFromCsv().toMutableList()
//                        pairs[index] = Pair(newPattern, newRepetitions)
//                        sequencesCsv = pairs.toIntPairsString()
//                    }
                    val fontSize = dimensions.dialogFontSize.sp
                    val fontWeight = FontWeight.Normal
                    val buttonPadding = 4.dp
                    Column(modifier = modifierA) {
                        Text(text = multiNumberDialogData.value.title)
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
                        val nRows = (sequences.size / nCols) + 1
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
                                        if (index != sequences.size) {
                                            //val sequenceIndex = sequences[index].first
                                            //val repetitions = pairs[index].second
                                            //val feature = if(repetitions >1 ) " (${repetitions}x)" else ""
                                            val transpose = if(transposeMap.contains(index)) transposeMap[index]!! else 0
                                            val sequence = Clip.convertAbsPitchesToClips(sequences[index].transpose(transpose))
                                            val text = sequence.toStringAll(notesNames, model.zodiacSignsActive, model.zodiacEmojisActive)
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
                                val rowIndex = if (sequences.size <= nCols) 1 else cursor / nCols
                                listState.animateScrollToItem(rowIndex)
                            }
                        }
                    }
                    Row(
                        modifier = modifierB.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        val buttonSize = model.dimensions.dialogButtonSize
                        CustomButton(
                            adaptSizeToIconButton = true,
                            text = "➚",
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            if(transposeMap.containsKey(cursor)){
                                transposeMap[cursor] = (transposeMap[cursor]!! +1) % 12
                            } else {
                                transposeMap[cursor] = 1
                            }
                        }
                        CustomButton(
                            adaptSizeToIconButton = true,
                            text = "➘",
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {

                            if(transposeMap.containsKey(cursor)){
                                transposeMap[cursor] = (transposeMap[cursor]!! + 11) % 12
                            } else {
                                transposeMap[cursor] = 11
                            }
                        }
                        CustomButton(
                            adaptSizeToIconButton = true,
                            text = "↑",
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            //val values = sequencesCsv.extractIntPairsFromCsv().toMutableList()
                            transposeMap.swap(cursor, cursor -1)
                            val mutableSequences = sequences.toMutableList()
                            cursor = mutableSequences.swap(cursor, cursor - 1)
                            sequences = mutableSequences.toList()
                            //sequencesCsv = values.toIntPairsString()
                        }
                        CustomButton(
                            adaptSizeToIconButton = true,
                            text = "↓",
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            //val values = sequencesCsv.extractIntPairsFromCsv().toMutableList()
                            transposeMap.swap(cursor, cursor + 1)
                            val mutableSequences = sequences.toMutableList()
                            cursor = mutableSequences.swap(cursor, cursor + 1)
                            sequences = mutableSequences.toList()
                            //sequencesCsv = values.toIntPairsString()
                        }
                    }
                    Row(
                        modifier = modifierC.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        val buttonSize = model.dimensions.dialogButtonSize
                        CustomButton(
                            adaptSizeToIconButton = true,
                            text = "",
                            iconId = model.iconMap["done"]!!,
                            buttonSize = buttonSize.dp,
                            iconColor = Color.Green,
                            colors = model.appColors
                        ) {
                            multiNumberDialogData.value.dispatchIntLists.invoke(
                                sequences.mapIndexed{ i, seq ->
                                    val transpose = if(transposeMap.contains(i)) transposeMap[i]!! else 0
                                    seq.transpose(transpose)
                                }
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
//                            val values = sequencesCsv.extractIntPairsFromCsv().toMutableList()
//                            val value = values[cursor]
//                            val sequence = value.first
//                            val repetitions = value.second
                            val sequence = sequences[cursor]
                            val newSequencesAll = (allSequences + sequences.map{Clip.convertAbsPitchesToClips(it)}).distinctBy{it.map{it.abstractNote}}
                            val newSequenceTexts = newSequencesAll.map{ it.toStringAll(notesNames, model.zodiacSignsActive, model.zodiacEmojisActive) }
                            val newIndex = newSequencesAll.indexOfFirst {
                                it.map{it.abstractNote} == sequence }
                            listDialogData.value = ListDialogData(
                                true, newSequenceTexts, newIndex, lang.selectRhythm
                            ) { index ->
//                                values[cursor] = Pair(index, repetitions)
//                                sequencesCsv = values.toIntPairsString()
                                val newSequence = newSequencesAll[index].map{it.abstractNote}
                                val mutableSequences = sequences.toMutableList()
                                mutableSequences[cursor] = newSequence
                                sequences = mutableSequences.toList()
                                transposeMap[cursor]?.let{
                                    transposeMap[cursor] = 0 }
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
                            if (sequences.size > 1) {
                                val mutableSequences = sequences.toMutableList()
                                mutableSequences.removeAt(cursor)
                                sequences = mutableSequences.toList()
                                transposeMap.removeAndScale(cursor)
                                //sequencesCsv = values.joinToString(",")
                                val newCursor = if (sequences.size > 1) cursor - 1 else 0
                                cursor = if (newCursor < 0) 0 else newCursor
                            }
                        }
                        CustomButton(
                            adaptSizeToIconButton = true,
                            //isActive = sequencesCsv.extractIntPairsFromCsv().size < model.MAX_SEQUENCES_IN_MAZE,
                            iconId = model.iconMap["add"]!!,
                            buttonSize = buttonSize.dp,
                            iconColor = model.appColors.iconButtonIconColor,
                            colors = model.appColors
                        ) {
                            val sequence = sequences[cursor]
                            val newSequencesAll = (allSequences + multiNumberDialogData.value.intSequences
                                .map{Clip.convertAbsPitchesToClips(it.cutAdjacentRepetitions())})
                                .distinctBy{it.map{it.abstractNote}}
                            val newSequenceTexts = newSequencesAll.map{ it.toStringAll(notesNames, model.zodiacSignsActive, model.zodiacEmojisActive) }
                            val newIndex = newSequencesAll.indexOfFirst {
                                it.map{it.abstractNote} == sequence }
                            listDialogData.value = ListDialogData(
                                true, newSequenceTexts, newIndex, lang.chooseAnotherSequence
                            ) { index ->
                                val mutableSequences = sequences.toMutableList()
                                mutableSequences.add(newSequencesAll[index].map{it.abstractNote})
                                sequences = mutableSequences.toList()
                                cursor = sequences.size - 1
                                transposeMap.insertAndScale(cursor, 0)
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








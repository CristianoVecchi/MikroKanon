package com.cristianovecchi.mikrokanon.composables.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.composables.*
import com.cristianovecchi.mikrokanon.locale.Lang
import com.cristianovecchi.mikrokanon.ui.Dimensions

@Composable
fun ButtonsDialog(
    buttonsDialogData: MutableState<ButtonsDialogData>,
    dimensions: Dimensions,
    okText: String = "OK",
    model: AppViewModel,
    language: Lang,
    workingOnSequences: Boolean = false,
    filledSlots: Set<Int>,
    onDismissRequest: () -> Unit = {
        buttonsDialogData.value = ButtonsDialogData(model = model)
    }
) {
    if (buttonsDialogData.value.dialogState) {
        val buttonSize = dimensions.dialogButtonSize.dp
        val fontSize = dimensions.dialogFontSize
        val fontColor = model.appColors.dialogFontColor
        val backgroundColor = model.appColors.dialogBackgroundColor
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier
                    .width(dimensions.dialogWidth)
                    .height(dimensions.dialogHeight),
                color = backgroundColor,
                shape = RoundedCornerShape(10.dp),
            ) {
                Column(modifier = Modifier
                    .padding(10.dp)
                    .background(backgroundColor),) {
                    Text(text = buttonsDialogData.value.title, fontWeight = FontWeight.Bold, color = fontColor, style = TextStyle(fontSize = fontSize.sp))
                    Spacer(modifier = Modifier.height(10.dp))
                    val listState = rememberLazyListState()
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.height(dimensions.dialogHeight)
                    ) {
                        items((0..10).toList()) { item ->
                            when (item) {
                                0 -> if(!workingOnSequences) {
                                    NotFromSelectorButtons(
                                        model = buttonsDialogData.value.model,
                                        buttonSize = buttonSize,
                                        fontSize = fontSize,
                                        colors = model.appColors,
                                        onSort = buttonsDialogData.value.onSort,
                                        onUpsideDown = buttonsDialogData.value.onUpsideDown,
                                        onArpeggio = buttonsDialogData.value.onArpeggio,
                                        onParade = buttonsDialogData.value.onParade,
                                        progressiveEWH = buttonsDialogData.value.progressiveEWH
                                    ) }
                                    else {
                                        BoostedMikroKanonsButtons(
                                            model = buttonsDialogData.value.model,
                                            buttonSize = buttonSize,
                                            fontSize = fontSize,
                                            colors = model.appColors,
                                            onMK5reductedClick = buttonsDialogData.value.onMK5reducted,
                                            onMK6reductedClick = buttonsDialogData.value.onMK6reducted,
                                        )
                                    }
                                1 -> ExtendedWeightedHarmonyButtons(
                                    model = buttonsDialogData.value.model,
                                    isActive = buttonsDialogData.value.isActiveWaves,
                                    buttonSize = buttonSize,
                                    fontSize = fontSize,
                                    colors = model.appColors,
                                    onEWH = buttonsDialogData.value.onEWH
                                )
                                2 -> BuildingButtons(
                                    model = buttonsDialogData.value.model,
                                    buttonSize = buttonSize,
                                    colors = model.appColors,
                                    isActive = buttonsDialogData.value.isActiveSpecialFunctions1,
                                    onScarlatti = buttonsDialogData.value.onScarlatti,
                                    onOverlap = buttonsDialogData.value.onOverlap,
                                    onCrossover = buttonsDialogData.value.onCrossover,
                                    onGlue = buttonsDialogData.value.onGlue,

                                    )
                                3 -> SpecialFunctions1Buttons(
                                    model = buttonsDialogData.value.model,
                                    buttonSize = buttonSize,
                                    fontSize = fontSize,
                                    colors = model.appColors,
                                    isActive = buttonsDialogData.value.isActiveSpecialFunctions1,
                                    onRound = buttonsDialogData.value.onRound,
                                    onCadenza = buttonsDialogData.value.onCadenza,
                                    onMaze = buttonsDialogData.value.onMaze,
                                    onFlourish = buttonsDialogData.value.onFlourish,
                                    onEraseIntervals = buttonsDialogData.value.onEraseIntervals,
                                    onSingle = buttonsDialogData.value.onSingle,
                                    onTritoneSubstitution = buttonsDialogData.value.onTritoneSubstitution,
                                    onDoppelgänger = buttonsDialogData.value.onDoppelgänger,
                                    onResolutio = buttonsDialogData.value.onResolutio,
                                    onDoubling = buttonsDialogData.value.onDoubling,
                                    onChess = buttonsDialogData.value.onChess,
                                    onFormat = buttonsDialogData.value.onFormat
                                )
                                4 -> WavesButtons(
                                    model = buttonsDialogData.value.model,
                                    isActive = buttonsDialogData.value.isActiveWaves,
                                    buttonSize = buttonSize,
                                    fontSize = fontSize,
                                    colors = model.appColors,
                                    onWave3Click = buttonsDialogData.value.onWave3,
                                    onWave4Click = buttonsDialogData.value.onWave4,
                                    onWave6Click = buttonsDialogData.value.onWave6,
                                    onQuote = buttonsDialogData.value.onQuote,
                                )
                                5 -> PedalsButtons(
                                    model = buttonsDialogData.value.model,
                                    isActive = buttonsDialogData.value.isActivePedals,
                                    buttonSize = buttonSize,
                                    fontSize = fontSize,
                                    colors = model.appColors,
                                    onPedal1Click = buttonsDialogData.value.onPedal1,
                                    onPedal3Click = buttonsDialogData.value.onPedal3,
                                    onPedal5Click = buttonsDialogData.value.onPedal5
                                )
                                6 -> Spacer(modifier = Modifier.height(6.dp))
                                7 -> SlotButtons(
                                    model = buttonsDialogData.value.model,
                                    buttonSize = buttonSize,
                                    fontSize = fontSize,
                                    colors = model.appColors,
                                    start = 0,
                                    numbers = language.slotNumbers,
                                    filled = filledSlots,
                                    onCounterpointSelected = buttonsDialogData.value.onCounterpointSelected)
                                8 -> SlotButtons(
                                    model = buttonsDialogData.value.model,
                                    buttonSize = buttonSize,
                                    fontSize = fontSize,
                                    colors = model.appColors,
                                    start = 4,
                                    numbers = language.slotNumbers,
                                    filled = filledSlots,
                                    onCounterpointSelected = buttonsDialogData.value.onCounterpointSelected)
                                9 -> SlotButtons(
                                    model = buttonsDialogData.value.model,
                                    buttonSize = buttonSize,
                                    fontSize = fontSize,
                                    colors = model.appColors,
                                    start = 8,
                                    numbers = language.slotNumbers,
                                    filled = filledSlots,
                                    onCounterpointSelected = buttonsDialogData.value.onCounterpointSelected)
                                10 -> SlotButtons(
                                    model = buttonsDialogData.value.model,
                                    buttonSize = buttonSize,
                                    fontSize = fontSize,
                                    colors = model.appColors,
                                    start = 12,
                                    numbers = language.slotNumbers,
                                    filled = filledSlots,
                                    onCounterpointSelected = buttonsDialogData.value.onCounterpointSelected)
                            }

                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
//                    Button(
//                        onClick = {
//                            //buttonDialogData.value.onSubmitButtonClick.invoke(bpm)
//                            onDismissRequest.invoke()
//                        },
//                        shape = MaterialTheme.shapes.large
//                    ) {
//                        Text(text = okText)
//                    }
                }
            }
        }
    }
}


package com.cristianovecchi.mikrokanon.composables.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.composables.*
import com.cristianovecchi.mikrokanon.locale.Lang
import com.cristianovecchi.mikrokanon.ui.Dimensions

@Composable
fun SelectCounterpointDialog(
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
        val appColors = model.appColors
        val fontColor = appColors.dialogFontColor
        val backgroundColor = appColors.dialogBackgroundColor
        val buttonSize = dimensions.dialogButtonSize.dp
        val fontSize = dimensions.dialogFontSize
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier.width(dimensions.dialogWidth).height(dimensions.dialogHeight),
                color = backgroundColor,
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = buttonsDialogData.value.title, color = fontColor)
                    Spacer(modifier = Modifier.height(10.dp))
                    val listState = rememberLazyListState()
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.height(dimensions.dialogHeight)
                    ) {
                        items((0..1).toList()) { item ->
                            when (item) {
                                0 -> Column(modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally){
                                    SlotButtons(
                                        model = buttonsDialogData.value.model,
                                        buttonSize = buttonSize,
                                        fontSize = fontSize,
                                        colors = model.appColors,
                                        start = 0,
                                        numbers = language.slotNumbers,
                                        filled = filledSlots,
                                        onCounterpointSelected = buttonsDialogData.value.onCounterpointSelected
                                    )
                                    SlotButtons(
                                        model = buttonsDialogData.value.model,
                                        buttonSize = buttonSize,
                                        fontSize = fontSize,
                                        colors = model.appColors,
                                        start = 4,
                                        numbers = language.slotNumbers,
                                        filled = filledSlots,
                                        onCounterpointSelected = buttonsDialogData.value.onCounterpointSelected
                                    )
                                    SlotButtons(
                                        model = buttonsDialogData.value.model,
                                        buttonSize = buttonSize,
                                        fontSize = fontSize,
                                        colors = model.appColors,
                                        start = 8,
                                        numbers = language.slotNumbers,
                                        filled = filledSlots,
                                        onCounterpointSelected = buttonsDialogData.value.onCounterpointSelected
                                    )
                                    SlotButtons(
                                        model = buttonsDialogData.value.model,
                                        buttonSize = buttonSize,
                                        fontSize = fontSize,
                                        colors = model.appColors,
                                        start = 12,
                                        numbers = language.slotNumbers,
                                        filled = filledSlots,
                                        onCounterpointSelected = buttonsDialogData.value.onCounterpointSelected
                                    )
                                }
                                1 -> Column(modifier = Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally){
                                    CustomButton(iconId = model.iconMap["self"]!!, isActive = true, buttonSize = buttonSize,
                                                fontSize = fontSize,colors = model.appColors) {
                                        buttonsDialogData.value.onCounterpointSelected(-1)
                                    }
                                }
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


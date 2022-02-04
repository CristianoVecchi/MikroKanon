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
    onDismissRequest: () -> Unit = {
        buttonsDialogData.value = ButtonsDialogData(model = model)
    }
) {
    if (buttonsDialogData.value.dialogState) {
        val buttonSize = dimensions.dialogButtonSize.dp
        val fontSize = dimensions.dialogFontSize
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier.width(dimensions.dialogWidth).height(dimensions.dialogHeight),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = buttonsDialogData.value.title)
                    Spacer(modifier = Modifier.height(10.dp))
                    val listState = rememberLazyListState()
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.height(dimensions.dialogHeight)
                    ) {
                        items((0..0).toList()) { item ->
                            when (item) {
                                0 -> SavingButtons(
                                    model = buttonsDialogData.value.model,
                                    buttonSize = buttonSize,
                                    fontSize = fontSize,
                                    colors = model.appColors,
                                    numbers = language.slotNumbers,
                                    onSavingCounterpoint = buttonsDialogData.value.onSavingCounterpoint
                                )
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


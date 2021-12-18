package com.cristianovecchi.mikrokanon.composables.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cristianovecchi.mikrokanon.composables.ListDialogData
import com.cristianovecchi.mikrokanon.ui.Dimensions

@Composable
fun ListDialog(listDialogData: MutableState<ListDialogData>, dimensions: Dimensions,
               okText: String = "OK", fillPrevious: Boolean = false) {
    SingleSelectListDialog(
        listDialogData = listDialogData, dimensions = dimensions,
        okText = okText, fillPrevious,
        onDismissRequest = { listDialogData.value = ListDialogData(itemList = listDialogData.value.itemList)  }
    )
}
@Composable
fun SingleSelectListDialog(
    listDialogData: MutableState<ListDialogData>, dimensions: Dimensions,
    okText: String = "OK", fillPrevious: Boolean = false,
    onDismissRequest: () -> Unit
) {
    if (listDialogData.value.dialogState) {
        val fontSize = dimensions.dialogFontSize
        var selectedOption by remember{ mutableStateOf(listDialogData.value.selectedListDialogItem) }
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier.width(dimensions.dialogWidth).height(dimensions.dialogHeight),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = listDialogData.value.dialogTitle)
                    Spacer(modifier = Modifier.height(5.dp))
                    val listState = rememberLazyListState()
                    val weights = dimensions.listDialogWeights
                    if(listDialogData.value.itemList.isNotEmpty()){
                        val modifierA = Modifier
                            .weight(weights.first)
                        val modifierB = Modifier
                            .weight(weights.second)
                        LazyColumn( state = listState,
                            modifier = modifierA.padding(end = 5.dp)
                        ) { itemsIndexed(listDialogData.value.itemList) { index, item ->
                            val selected = if (selectedOption == -1) {
                                ""
                            } else {
                                listDialogData.value.itemList[selectedOption]
                                //sequencesList[selectedOption.value]
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            val showAsSelected = if (!fillPrevious) false
                            else index <= listDialogData.value.itemList.indexOf(selected)
                            RadioButton(item, selected, showAsSelected, fontSize.sp) { selectedValue ->
                                selectedOption = listDialogData.value.itemList.indexOf(selectedValue)
                            }
                        }

                        }

                        Spacer(modifier = Modifier.height(5.dp))
                        Row( modifier = modifierB,
                            verticalAlignment = Alignment.CenterVertically)
                        {
                            Button(
                                onClick = {
                                    listDialogData.value.onSubmitButtonClick.invoke(selectedOption)
                                    onDismissRequest.invoke()
                                },
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(text = okText, style = TextStyle(
                                    fontSize = dimensions.dialogFontSize.sp ) )
                            }
                        }

                    }

                }
            }
        }
    }
}
@Composable
fun RadioButton(text: String, selectedValue: String, showAsSelected: Boolean = false, fontSize: TextUnit, onClickListener: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = (text == selectedValue),
                onClick = {
                    onClickListener(text)
                }
            )
            .background(if (showAsSelected) Color.LightGray else Color.White)
            .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        // The Default Radio Button in Jetpack Compose doesn't accept text as an argument.
        // So have Text Composable to show text.
        androidx.compose.material.RadioButton(
            selected = (text == selectedValue),
            onClick = {
                onClickListener(text)
            }
        )
        Text(
            text = text,
            style = MaterialTheme.typography.body1.merge().copy(fontSize = fontSize),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}


package com.cristianovecchi.mikrokanon.composables.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cristianovecchi.mikrokanon.ui.AppColors
import com.cristianovecchi.mikrokanon.ui.Dimensions
import com.cristianovecchi.mikrokanon.ui.shift

@Composable
fun MultiListDialog(listDialogData: MutableState<MultiListDialogData>, dimensions: Dimensions,
                    okText: String = "OK", appColors: AppColors) {
    MultiSelectListDialog(
        listDialogData = listDialogData,
        dimensions = dimensions,  okText = okText, appColors = appColors,
        onDismissRequest = { listDialogData.value = MultiListDialogData(itemList = listDialogData.value.itemList)  }
    )
}
@Composable
fun MultiSelectListDialog(
    listDialogData: MutableState<MultiListDialogData>,
    dimensions: Dimensions, okText: String = "OK", appColors: AppColors,
    onDismissRequest: () -> Unit
) {
    if (listDialogData.value.dialogState) {
        val fontColor = appColors.dialogFontColor
        val backgroundColor = appColors.dialogBackgroundColor
        val fontSize = dimensions.dialogFontSize
        var selectedOptions by remember{ mutableStateOf(listDialogData.value.selectedListDialogItems) }
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier.width(dimensions.dialogWidth).height(dimensions.dialogHeight),
                color = backgroundColor,
                shape = RoundedCornerShape(10.dp)
            ) {

                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = listDialogData.value.dialogTitle, color = fontColor)
                    Spacer(modifier = Modifier.height(5.dp))
                    val weights = dimensions.listDialogWeights
                    val modifierA = Modifier
                        .weight(weights.first)
                    val modifierB = Modifier
                        .weight(weights.second)
                    val listState = rememberLazyListState()
                    if(listDialogData.value.itemList.isNotEmpty()){
                        LazyColumn( state = listState,
                            modifier = modifierA
                        ) { items(listDialogData.value.itemList) { item ->
                            val selected = if (selectedOptions.isEmpty()) {
                                listOf<String>()
                            } else {
                                listDialogData.value.itemList.filterIndexed{ index, _ -> selectedOptions.contains(index)}
                                //sequencesList[selectedOption.value]
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            MultiRadioButton(item, selected, fontSize.sp, appColors) { selectedValue ->
                                val index = listDialogData.value.itemList.indexOf(selectedValue)
                                selectedOptions = if(selectedOptions.contains(index)){
                                    selectedOptions.toMutableSet().also{
                                        it.remove(index)}.sorted().toSet()
                                } else {
                                    selectedOptions.toMutableSet().also{
                                        it.add(index)}.sorted().toSet()
                                }

                            }
                        }

                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Row( modifier = modifierB,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Button(
                                onClick = {
                                    listDialogData.value.onSubmitButtonClick.invoke(selectedOptions.toList())
                                    onDismissRequest.invoke()
                                },
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(text = okText ,style = TextStyle(
                                        fontSize = dimensions.dialogFontSize.sp)
                                )
                            }
                        }

                    }

                }
            }
        }
    }
}
@Composable
fun MultiRadioButton(text: String, selectedValues: List<String>, fontSize: TextUnit, appColors: AppColors,
                     onClickListener: (String) -> Unit) {
    val fontColor = appColors.dialogFontColor
    val backgroundColor = appColors.dialogBackgroundColor
    val backgroundColorLighter = backgroundColor.shift(0.15f)
    val isSelected = selectedValues.contains(text)
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = (selectedValues.contains(text)),
                onClick = {
                    onClickListener(text)
                }
            )
            .background(if (isSelected) backgroundColorLighter else backgroundColor)
            .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        // The Default Radio Button in Jetpack Compose doesn't accept text as an argument.
        // So have Text Composable to show text.
        androidx.compose.material.RadioButton(
            selected = isSelected,
            onClick = {
                onClickListener(text)
            }
        )
        Text(
            text = text,
            color = fontColor,
            style = MaterialTheme.typography.body1.merge().copy(fontSize = fontSize),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cristianovecchi.mikrokanon.AIMUSIC.TREND
import com.cristianovecchi.mikrokanon.composables.RadioButton

@Composable
fun ExportDialog(exportDialogData: MutableState<ExportDialogData>,
                 onDismissRequest: () -> Unit = { exportDialogData.value = ExportDialogData(path = exportDialogData.value.path, error = exportDialogData.value.error)})
{
    if (exportDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier.width(300.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = exportDialogData.value.title)
                    Spacer(modifier = Modifier.height(10.dp))

                   // var error by remember { mutableStateOf(exportDialogData.value.error) }
                    val fontSize = 26.sp
                    val fontWeight = FontWeight.Normal
                    Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "MIDI path: ${exportDialogData.value.path}", style = TextStyle(fontSize = 12.sp,fontWeight = FontWeight.Bold) )

                        if(exportDialogData.value.error.isNotEmpty()){
                            Text(text = "error: ${exportDialogData.value.error}", style = TextStyle(fontSize = 12.sp,fontWeight = FontWeight.Bold) )
                        } else {
                            Text(
                                text = "Here you can find your MIDI!!!",
                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            )
                        }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            exportDialogData.value.onSubmitButtonClick.invoke()
                            onDismissRequest.invoke()
                        },
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(text = "Done")
                    }
                }
            }
        }
    }

}
@Composable
fun BpmDialog(numberDialogData: MutableState<NumberDialogData>, onDismissRequest: () -> Unit = { numberDialogData.value = NumberDialogData(value = numberDialogData.value.value)})
{
    if (numberDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier.width(300.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = numberDialogData.value.title)
                    Spacer(modifier = Modifier.height(10.dp))

                    var bpm by remember { mutableStateOf(numberDialogData.value.value) }
                    val setBpm = { bpmToCheck: Int ->
                        val newBpm = bpmToCheck.coerceIn(numberDialogData.value.min, numberDialogData.value.max )
                        bpm = newBpm
                    }
                    val fontSize = 26.sp
                    val fontWeight = FontWeight.Normal
                    Spacer(modifier = Modifier.height(10.dp))
                    Row() {
                        Text(text = "$bpm", style = TextStyle(fontSize = 32.sp,fontWeight = FontWeight.Bold) )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    // 240 | 150 | 60
                    // +30 | +6 | +1
                    // -30 | -6 | -1
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly){
                        Button(modifier= Modifier.padding(2.dp), onClick = { setBpm(240) } )
                        {
                            Text(text = "240",style = TextStyle(fontSize = fontSize,fontWeight = fontWeight) )
                        }
                        Button(modifier= Modifier.padding(2.dp), onClick = { setBpm(150) } )
                        {
                            Text(text = "150",style = TextStyle(fontSize = fontSize,fontWeight = fontWeight) )
                        }
                        Button(modifier= Modifier.padding(2.dp), onClick = { setBpm(60) } )
                        {
                            Text(text = "60",style = TextStyle(fontSize = fontSize,fontWeight = fontWeight) )
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly){
                        Button(modifier= Modifier.padding(2.dp), onClick = { setBpm(bpm + 30) } )
                        {
                            Text(text = "+30",style = TextStyle(fontSize = fontSize,fontWeight = fontWeight) )
                        }
                        Button(modifier= Modifier.padding(2.dp), onClick = { setBpm(bpm + 6) } )
                        {
                            Text(text = "+6",style = TextStyle(fontSize = fontSize,fontWeight = fontWeight) )
                        }
                        Button(modifier= Modifier.padding(2.dp), onClick = { setBpm(bpm + 1) } )
                        {
                            Text(text = "+1",style = TextStyle(fontSize = fontSize,fontWeight = fontWeight) )
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly){
                        Button(modifier= Modifier.padding(2.dp), onClick = { setBpm(bpm - 30) } )
                        {
                            Text(text = "-30",style = TextStyle(fontSize = fontSize,fontWeight = fontWeight) )
                        }
                        Button(modifier= Modifier.padding(2.dp), onClick = { setBpm(bpm - 6) } )
                        {
                            Text(text = "-6",style = TextStyle(fontSize = fontSize,fontWeight = fontWeight) )
                        }
                        Button(modifier= Modifier.padding(2.dp), onClick = { setBpm(bpm - 1) } )
                        {
                            Text(text = "-1",style = TextStyle(fontSize = fontSize,fontWeight = fontWeight) )
                        }
                    }


                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            numberDialogData.value.onSubmitButtonClick.invoke(bpm)
                            onDismissRequest.invoke()
                        },
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(text = "Select")
                    }
                }
            }
        }
    }
}
@Composable
fun NumberDialog(numberDialogData: MutableState<NumberDialogData>, onDismissRequest: () -> Unit = { numberDialogData.value = NumberDialogData(value = numberDialogData.value.value)})
{
    if (numberDialogData.value.dialogState) {
       // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier.width(300.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = numberDialogData.value.title)
                    Spacer(modifier = Modifier.height(10.dp))

                    val textState = remember { mutableStateOf(TextFieldValue("${numberDialogData.value.value}")) }
                    TextField(
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        value = textState.value,
                        onValueChange = { textState.value = it }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val bpm = if(textState.value.text.isBlank()) numberDialogData.value.value
                                else Integer.parseInt(textState.value.text)
                            val newBpm = bpm.coerceIn(numberDialogData.value.min, numberDialogData.value.max )
                            numberDialogData.value.onSubmitButtonClick.invoke(newBpm)
                            onDismissRequest.invoke()
                        },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = "Select")
                    }
                }
            }
        }
    }

}
@Composable
fun ListDialog(listDialogData: MutableState<ListDialogData>) {
    SingleSelectListDialog(
        listDialogData = listDialogData,
        submitButtonText = "Select",

        onDismissRequest = { listDialogData.value = ListDialogData(itemList = listDialogData.value.itemList)  }
    )
}
@Composable
fun SingleSelectListDialog(
    listDialogData: MutableState<ListDialogData>,
    submitButtonText: String,

    onDismissRequest: () -> Unit
) {
    if (listDialogData.value.dialogState) {
        var selectedOption by remember{ mutableStateOf(listDialogData.value.selectedListDialogItem) }
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier.width(300.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = listDialogData.value.dialogTitle)
                    Spacer(modifier = Modifier.height(10.dp))
                    val listState = rememberLazyListState()
                    if(listDialogData.value.itemList.isNotEmpty()){
                        LazyColumn( state = listState,
                            modifier = Modifier.height(500.dp)
                        ) { items(listDialogData.value.itemList) { item ->
                            val selected = if (selectedOption == -1) {
                                ""
                            } else {
                                listDialogData.value.itemList[selectedOption]
                                //sequencesList[selectedOption.value]
                            }

                            RadioButton(item, selected) { selectedValue ->
                                selectedOption = listDialogData.value.itemList.indexOf(selectedValue)
                            }
                        }

                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                listDialogData.value.onSubmitButtonClick.invoke(selectedOption)
                                onDismissRequest.invoke()
                            },
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(text = submitButtonText)
                        }
                    }

                }
            }
        }
    }
}
@Composable
fun RadioListButton(text: String, selectedValue: String, onClickListener: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = (text == selectedValue),
                onClick = {
                    onClickListener(text)
                }
            )
            .padding(horizontal = 16.dp)
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
            style = MaterialTheme.typography.body1.merge(),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
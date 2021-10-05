package com.cristianovecchi.mikrokanon.composables.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


@Composable
fun SequencesDialog(dialogState: MutableState<Boolean>, sequencesList: List<String>,
                    fontSize: TextUnit, title: String, repeatText: String, okText: String = "OK",
                    onSubmitButtonClick: (Int, Boolean) -> Unit) {
    SingleSelectDialog(
        dialogState = dialogState,
        title = title,
        repeatText = repeatText, okText = okText,
        sequencesList = sequencesList,
        fontSize = fontSize,
        onSubmitButtonClick = {  index, repeated -> onSubmitButtonClick(index, repeated)},
        onDismissRequest = { dialogState.value = false }
    )
}
@Composable
fun SingleSelectDialog(
    dialogState: MutableState<Boolean>,
    title: String, repeatText: String, okText: String = "OK",
    sequencesList: List<String>,
    defaultSelected: Int = -1,

    fontSize: TextUnit,
    onSubmitButtonClick: (Int, Boolean) -> Unit,
    onDismissRequest: () -> Unit
) {
    if (dialogState.value) {
        var selectedOption by remember{ mutableStateOf(defaultSelected) }
        var repeated by remember{ mutableStateOf(false) }
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier.width(300.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = title)
                    Spacer(modifier = Modifier.height(10.dp))
                    val listState = rememberLazyListState()
                    LazyColumn( state = listState,
                        modifier = Modifier.height(420.dp)
                    ) { items(sequencesList) { sequence ->
                        val selected = if (selectedOption == -1) {
                            ""
                        } else {
                            sequencesList[selectedOption]
                            //sequencesList[selectedOption.value]
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        RadioButton(sequence, selected, fontSize = fontSize) { selectedValue ->
                            selectedOption = sequencesList.indexOf(selectedValue)
                        }
                    }

                    }
                    Row(){
                        Checkbox(
                            checked = repeated,
                            onCheckedChange = { checked ->
                                repeated = !repeated
                            }
                        )
                        Text(text = repeatText)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            onSubmitButtonClick.invoke(selectedOption, repeated)
                            onDismissRequest.invoke()
                        },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = okText, style = TextStyle(fontSize = fontSize))
                    }
                }
            }
        }
    }
}
package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.cristianovecchi.mikrokanon.composables.RadioButton

@Composable
fun SequencesDialog(dialogState: MutableState<Boolean>, sequencesList: List<String>,
                    onSubmitButtonClick: (Int, Boolean) -> Unit) {
    SingleSelectDialog(
        dialogState = dialogState,
        title = "Choose the second Sequence",
        sequencesList = sequencesList,
        submitButtonText = "Select",
        onSubmitButtonClick = {  index, repeated -> onSubmitButtonClick(index, repeated)},
        onDismissRequest = { dialogState.value = false }
    )
}
@Composable
fun SingleSelectDialog(
    dialogState: MutableState<Boolean>,
    title: String,
    sequencesList: List<String>,
    defaultSelected: Int = -1,
    submitButtonText: String,
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
                        modifier = Modifier.height(500.dp)
                    ) { items(sequencesList) { sequence ->
                        val selected = if (selectedOption == -1) {
                            ""
                        } else {
                            sequencesList[selectedOption]
                            //sequencesList[selectedOption.value]
                        }

                        RadioButton(sequence, selected) { selectedValue ->
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
                        Text(text="repeat sequence")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            onSubmitButtonClick.invoke(selectedOption, repeated)
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
@Composable
fun RadioButton(text: String, selectedValue: String, onClickListener: (String) -> Unit) {
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
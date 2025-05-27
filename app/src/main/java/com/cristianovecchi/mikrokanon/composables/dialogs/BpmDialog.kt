package com.cristianovecchi.mikrokanon.composables.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cristianovecchi.mikrokanon.ui.Dimensions

@Composable
fun BpmDialog(
    numberDialogData: MutableState<NumberDialogData>, dimensions: Dimensions, okText: String = "OK",
    onDismissRequest: () -> Unit = {
        numberDialogData.value = NumberDialogData(value = numberDialogData.value.value)
    }
) {
    if (numberDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier.width(dimensions.dialogWidth).height(dimensions.dialogHeight),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = numberDialogData.value.title, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    var bpm by remember { mutableStateOf(numberDialogData.value.value) }
                    val setBpm = { bpmToCheck: Int ->
                        val newBpm = bpmToCheck.coerceIn(
                            numberDialogData.value.min,
                            numberDialogData.value.max
                        )
                        bpm = newBpm
                    }
                    val fontSize = dimensions.dialogFontSize.sp
                    val fontWeight = FontWeight.Normal
                    val buttonPadding = 4.dp
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "$bpm",
                            style = TextStyle(
                                fontSize = (dimensions.dialogFontSize + dimensions.dialogFontSize/2).sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    // 240 | 150 | 60
                    // +30 | +6 | +1
                    // -30 | -6 | -1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            modifier = Modifier.width(IntrinsicSize.Max),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(modifier = Modifier
                                .fillMaxWidth()
                                .padding(buttonPadding), onClick = { setBpm(240) })
                            {
                                Text(
                                    text = "240",
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        fontWeight = fontWeight
                                    )
                                )
                            }
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                onClick = { setBpm(bpm + 30) })
                            {
                                Text(
                                    text = "+30",
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        fontWeight = fontWeight
                                    )
                                )
                            }
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                onClick = { setBpm(bpm - 30) })
                            {
                                Text(
                                    text = "-30",
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        fontWeight = fontWeight
                                    )
                                )
                            }

                        }
                        Column(
                            modifier = Modifier.width(IntrinsicSize.Max),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(modifier = Modifier
                                .fillMaxWidth()
                                .padding(buttonPadding), onClick = { setBpm(150) })
                            {
                                Text(
                                    text = "150",
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        fontWeight = fontWeight
                                    )
                                )
                            }
                            Button(modifier = Modifier
                                .fillMaxWidth()
                                .padding(buttonPadding), onClick = { setBpm(bpm + 6) })
                            {
                                Text(
                                    text = "+6",
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        fontWeight = fontWeight
                                    )
                                )
                            }
                            Button(modifier = Modifier
                                .fillMaxWidth()
                                .padding(buttonPadding), onClick = { setBpm(bpm - 6) })
                            {
                                Text(
                                    text = "-6",
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        fontWeight = fontWeight
                                    )
                                )
                            }

                        }
                        Column(
                            modifier = Modifier.width(IntrinsicSize.Max),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(modifier = Modifier
                                .fillMaxWidth()
                                .padding(buttonPadding), onClick = { setBpm(60) })
                            {
                                Text(
                                    text = "60",
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        fontWeight = fontWeight
                                    )
                                )
                            }
                            Button(modifier = Modifier
                                .fillMaxWidth()
                                .padding(buttonPadding), onClick = { setBpm(bpm + 1) })
                            {
                                Text(
                                    text = "+1",
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        fontWeight = fontWeight
                                    )
                                )
                            }
                            Button(modifier = Modifier
                                .fillMaxWidth()
                                .padding(buttonPadding), onClick = { setBpm(bpm - 1) })
                            {
                                Text(
                                    text = "-1",
                                    style = TextStyle(
                                        fontSize = fontSize,
                                        fontWeight = fontWeight
                                    )
                                )
                            }
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
                        Text(text = okText)
                    }
                }
            }
        }
    }
}

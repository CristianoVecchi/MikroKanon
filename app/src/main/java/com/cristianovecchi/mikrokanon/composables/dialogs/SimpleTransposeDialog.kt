package com.cristianovecchi.mikrokanon.composables.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
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
import com.cristianovecchi.mikrokanon.ui.Dimensions

@Composable
fun SimpleTransposeDialog(multiNumberDialogData: MutableState<MultiNumberDialogData>,
                          dimensions: Dimensions, intervals: List<String>,
                          onDismissRequest: () -> Unit = { multiNumberDialogData.value = MultiNumberDialogData(model = multiNumberDialogData.value.model, value = multiNumberDialogData.value.value) })
{

    if (multiNumberDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            val model = multiNumberDialogData.value.model
            val appColors = model.appColors
            val fontColor = appColors.dialogFontColor
            val backgroundColor = appColors.dialogBackgroundColor
            val height = dimensions.dialogHeight / 3 * 2
            val width = if(dimensions.width <= 884) (dimensions.width / dimensions.dpDensity).toInt().dp
            else dimensions.dialogWidth
            Surface(
                modifier = Modifier.width(width).height(height),
                color = backgroundColor,
                shape = RoundedCornerShape(10.dp)
            ) {

                    val modifierA = Modifier
                        //.fillMaxSize()
                        .padding(8.dp)

                    val fontSize = dimensions.dialogFontSize.sp
                    val fontWeight = FontWeight.Normal
                    val buttonPadding = 4.dp
                    Column(modifier = modifierA) {
                        Text(text = multiNumberDialogData.value.title, fontWeight = FontWeight.Bold, color = fontColor, style = TextStyle(fontSize = fontSize))
                        Spacer(modifier = Modifier.height(20.dp))

                        Column(modifier = Modifier.height(height / 6 * 5).padding(10.dp), verticalArrangement = Arrangement.Center) {
                            Spacer(modifier = Modifier.height(10.dp))
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
                                        .padding(buttonPadding), onClick = {
                                        multiNumberDialogData.value.onSubmitButtonClick.invoke(
                                            11.toString()
                                        )
                                        onDismissRequest.invoke()
                                    })
                                    {
                                        Text(
                                            text = intervals[11],
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
                                        onClick = {
                                            multiNumberDialogData.value.onSubmitButtonClick.invoke(
                                                10.toString()
                                            )
                                            onDismissRequest.invoke()
                                        })
                                    {
                                        Text(
                                            text = intervals[10],
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
                                        onClick = {
                                            multiNumberDialogData.value.onSubmitButtonClick.invoke(9.toString())
                                            onDismissRequest.invoke()
                                        })
                                    {
                                        Text(
                                            text = intervals[9],
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
                                        onClick = {
                                            multiNumberDialogData.value.onSubmitButtonClick.invoke(8.toString())
                                            onDismissRequest.invoke()
                                        })
                                    {
                                        Text(
                                            text = intervals[8],
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
                                        .padding(buttonPadding),
                                        onClick = {
                                            multiNumberDialogData.value.onSubmitButtonClick.invoke(7.toString())
                                            onDismissRequest.invoke()
                                        })
                                    {
                                        Text(
                                            text = intervals[7],
                                            style = TextStyle(
                                                fontSize = fontSize,
                                                fontWeight = fontWeight
                                            )
                                        )
                                    }
                                    Button(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(buttonPadding),
                                        onClick = {
                                            multiNumberDialogData.value.onSubmitButtonClick.invoke(6.toString())
                                            onDismissRequest.invoke()
                                        })
                                    {
                                        Text(
                                            text = intervals[6],
                                            style = TextStyle(
                                                fontSize = fontSize,
                                                fontWeight = fontWeight
                                            )
                                        )
                                    }
                                    Button(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(buttonPadding),
                                        onClick = {
                                            multiNumberDialogData.value.onSubmitButtonClick.invoke(5.toString())
                                            onDismissRequest.invoke()
                                        })
                                    {
                                        Text(
                                            text = intervals[5],
                                            style = TextStyle(
                                                fontSize = fontSize,
                                                fontWeight = fontWeight
                                            )
                                        )
                                    }
                                    Button(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(buttonPadding),
                                        onClick = {
                                            multiNumberDialogData.value.onSubmitButtonClick.invoke(4.toString())
                                            onDismissRequest.invoke()
                                        })
                                    {
                                        Text(
                                            text = intervals[4],
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
                                        .padding(buttonPadding),
                                        onClick = {
                                            multiNumberDialogData.value.onSubmitButtonClick.invoke(3.toString())
                                            onDismissRequest.invoke()
                                        })
                                    {
                                        Text(
                                            text = intervals[3],
                                            style = TextStyle(
                                                fontSize = fontSize,
                                                fontWeight = fontWeight
                                            )
                                        )
                                    }
                                    Button(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(buttonPadding),
                                        onClick = {
                                            multiNumberDialogData.value.onSubmitButtonClick.invoke(2.toString())
                                            onDismissRequest.invoke()
                                        })
                                    {
                                        Text(
                                            text = intervals[2],
                                            style = TextStyle(
                                                fontSize = fontSize,
                                                fontWeight = fontWeight
                                            )
                                        )
                                    }
                                    Button(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(buttonPadding),
                                        onClick = {
                                            multiNumberDialogData.value.onSubmitButtonClick.invoke(1.toString())
                                            onDismissRequest.invoke()
                                        })
                                    {
                                        Text(
                                            text = intervals[1],
                                            style = TextStyle(
                                                fontSize = fontSize,
                                                fontWeight = fontWeight
                                            )
                                        )
                                    }
                                    Button(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(buttonPadding),
                                        onClick = {
                                            multiNumberDialogData.value.onSubmitButtonClick.invoke(0.toString())
                                            onDismissRequest.invoke()
                                        })
                                    {
                                        Text(
                                            text = intervals[0],
                                            style = TextStyle(
                                                fontSize = fontSize,
                                                fontWeight = fontWeight
                                            )
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }

        }
    }
}

package com.cristianovecchi.mikrokanon.composables.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
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
import com.cristianovecchi.mikrokanon.composables.ExportDialogData

@Composable
fun ExportDialog(exportDialogData: MutableState<ExportDialogData>, okText: String = "OK",
                 onDismissRequest: () -> Unit = { exportDialogData.value = ExportDialogData(path = exportDialogData.value.path, error = exportDialogData.value.error) })
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
                    if(exportDialogData.value.path.isNotEmpty()){
                        Text(text = "MIDI path: ${exportDialogData.value.path}", style = TextStyle(fontSize = 12.sp,fontWeight = FontWeight.Bold) )
                    }

                    if(exportDialogData.value.error.isNotEmpty()){
                        Text(text = "${exportDialogData.value.error}", style = TextStyle(fontSize = 23.sp,fontWeight = FontWeight.Bold) )
                    } else {
//                            Text(
//                                text = "Here you can find your MIDI!!!",
//                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
//                            )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            exportDialogData.value.onSubmitButtonClick.invoke()
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

package com.cristianovecchi.mikrokanon.composables.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cristianovecchi.mikrokanon.ui.Dimensions

@Composable
fun PrivacyDialog(creditsDialogData: MutableState<TextDialogData>, dimensions: Dimensions,
                  okText: String = "OK",
                  onDismissRequest: () -> Unit = {creditsDialogData.value = TextDialogData() })
{
    if (creditsDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        val privacyPolicyRawUri = "https://raw.githubusercontent.com/CristianoVecchi/MikroKanon/master/PRIVACY_POLICY"
        val uriHandler = LocalUriHandler.current
        val uriStyle = SpanStyle(
            fontSize = dimensions.dialogFontSize.sp,
            color = Color.Blue)
        val titleStyle = SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = (dimensions.dialogFontSize + dimensions.dialogFontSize/20).sp,
            color = Color.Black)
        val commentStyle = SpanStyle(
            fontSize = (dimensions.dialogFontSize - dimensions.dialogFontSize/20).sp,
            color = Color.DarkGray)
//        val uriStyle = SpanStyle(
//            fontSize = dimensions.dialogFontSize.sp,
//            color = Color.Blue)
//        val uriHandler = LocalUriHandler.current
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier
                    .width(dimensions.dialogWidth)
                    .height(dimensions.dialogHeight),
                shape = RoundedCornerShape(10.dp)
            ) {
                val weights = dimensions.listDialogWeights
                Column(verticalArrangement = Arrangement.SpaceBetween){
                    val modifierA = Modifier
                        .weight(weights.first)
                    val modifierB = Modifier
                        .weight(weights.second)
                    LazyColumn(modifier = modifierA.padding(start = 10.dp, end = 10.dp)

                    ) {
                        item {
                            Text(text = creditsDialogData.value.title)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        item{
                            Text(text = buildAnnotatedString {
                                withStyle(titleStyle){
                                    append("Privacy Policy\n")
                                }
                                withStyle(commentStyle){
                                    append("Cristiano Vecchi built the MikroKanon app as a Free app, provided at no cost and intended for use as is.")
                                }
                            })
                        }
                        item{
                            Text(text = buildAnnotatedString {
                                withStyle(titleStyle){
                                    append("Information Collection and Use\n")
                                }
                                withStyle(commentStyle){
                                    append("The MikroKanon app does not collect any user's information of any kind, also does not profile the user in any manner and does not connect to any server or to any device.")
                                }
                            })
                        }
                        item{
                            Text(text = buildAnnotatedString {
                                withStyle(titleStyle){
                                    append("Bugs Report\n")
                                }
                                withStyle(commentStyle){
                                    append("Please report bugs at cristianovecchi@alice.it")
                                }
                            })
                        }
                        item{
                            Text(text = buildAnnotatedString {
                                withStyle(titleStyle){
                                    append("Privacy Policy Modification\n")
                                }
                                withStyle(commentStyle){
                                    append("You will receive a notification each time this Privacy Policy is modified to read it.")
                                }
                            })
                        }
                        item{
                            ClickableText(text = buildAnnotatedString {
                                withStyle(uriStyle){
                                    append("Link to this Privacy Policy")
                                }
                            },onClick = {
                                uriHandler.openUri(privacyPolicyRawUri)
                            })
                        }
                    }
                    Column(modifier = modifierB.padding(10.dp)) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                creditsDialogData.value.onSubmitButtonClick.invoke()
                                onDismissRequest.invoke()
                            },
                            shape = MaterialTheme.shapes.large
                        ) {
                            Text(text = okText, style = TextStyle(fontSize = dimensions.dialogFontSize.sp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                }

            }
        }
    }

}

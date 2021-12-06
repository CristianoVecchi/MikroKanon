package com.cristianovecchi.mikrokanon.composables.dialogs

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cristianovecchi.mikrokanon.composables.CreditsDialogData
import com.cristianovecchi.mikrokanon.ui.Dimensions

@Composable
fun CreditsDialog(creditsDialogData: MutableState<CreditsDialogData>, dimensions: Dimensions,
                  okText: String = "OK",
                  onDismissRequest: () -> Unit = {creditsDialogData.value = CreditsDialogData() })
{
    if (creditsDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        val nameStyle = SpanStyle(
            fontSize = (dimensions.dialogFontSize + dimensions.dialogFontSize/20).sp,
            color = Color.Black)
        val commentStyle = SpanStyle(
            fontSize = (dimensions.dialogFontSize - dimensions.dialogFontSize/20).sp,
            color = Color.DarkGray)
        val uriStyle = SpanStyle(
            fontSize = dimensions.dialogFontSize.sp,
            color = Color.Blue)
        val uriHandler = LocalUriHandler.current
        val youtubeChannelUri = "https://www.youtube.com/channel/UCe9Kd87V90fbPsUBU5gaXKw/playlists?view=1&sort=dd&shelf_id=0"
        val youtubeMikroKanonExamplesUri = "https://www.youtube.com/watch?v=zaa3d3FVqA4&list=PLO0dKPP71phouGDmrOQA_yXEp0Z1L1PLV&index=2"
        val instagramUri = "https://www.instagram.com/cristiano.vecchi"
        val linkedinUri = "https://www.linkedin.com/in/cristiano-vecchi-ba1a311a"
        val githubUri = "https://github.com/CristianoVecchi"
        val githubLeffelManiaUri = "https://github.com/LeffelMania"
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier
                    .width(dimensions.dialogWidth)
                    .height(dimensions.dialogHeight),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.SpaceBetween){
                    Column(modifier = Modifier.padding(10.dp)

                    ) {
                        Text(text = creditsDialogData.value.title)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = buildAnnotatedString {
                            withStyle(commentStyle){
                                append("the MikroKanon App has been conceived and developed in 2021 by\n")
                            }
                            withStyle(nameStyle){
                                append("Cristiano Vecchi")
                            }
                        })
                        ClickableText(text = buildAnnotatedString {
                            withStyle(uriStyle){
                                append("Youtube Channel")
                            }
                        },onClick = {
                            uriHandler.openUri(youtubeChannelUri)
                        })
                        ClickableText(text = buildAnnotatedString {
                            withStyle(uriStyle){
                                append("Youtube MK examples")
                            }
                        },onClick = {
                            uriHandler.openUri(youtubeMikroKanonExamplesUri)
                        })
                        ClickableText(text = buildAnnotatedString {
                            withStyle(uriStyle){
                                append("Instagram")
                            }
                        },onClick = {
                            uriHandler.openUri(instagramUri)
                        })
                        ClickableText(text = buildAnnotatedString {
                            withStyle(uriStyle){
                                append("Linkedin")
                            }
                        },onClick = {
                            uriHandler.openUri(linkedinUri)
                        })
                        ClickableText(text = buildAnnotatedString {
                            withStyle(uriStyle){
                                append("GitHub")
                            }
                        },onClick = {
                            uriHandler.openUri(githubUri)
                        })
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = buildAnnotatedString {
                            withStyle(commentStyle){
                                append("this App uses the android-midi-lib library by\n")
                            }
                            withStyle(nameStyle){
                                append("Alex Leffelman")
                            }
                        })
                        ClickableText(text = buildAnnotatedString {
                            withStyle(uriStyle){
                                append("GitHub")
                            }
                        },onClick = {
                            uriHandler.openUri(githubLeffelManiaUri)
                        })


                    }
                    Column(modifier = Modifier.padding(10.dp)) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                creditsDialogData.value.onSubmitButtonClick.invoke()
                                onDismissRequest.invoke()
                            },
                            shape = MaterialTheme.shapes.large
                        ) {
                            Text(text = okText)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                }

            }
        }
    }

}

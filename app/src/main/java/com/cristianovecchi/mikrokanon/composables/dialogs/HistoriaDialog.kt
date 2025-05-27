package com.cristianovecchi.mikrokanon.composables.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun HistoriaDialog(creditsDialogData: MutableState<TextDialogData>, dimensions: Dimensions,
                  okText: String = "OK",
                  onDismissRequest: () -> Unit = {creditsDialogData.value = TextDialogData() })
{
    if (creditsDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        val titleStyle = SpanStyle(
            fontWeight = FontWeight.Bold,
            fontSize = (dimensions.dialogFontSize + dimensions.dialogFontSize/20).sp,
            color = Color.Black)
        val commentStyle = SpanStyle(
            fontSize = (dimensions.dialogFontSize - dimensions.dialogFontSize/20).sp,
            color = Color.DarkGray)
        val par1 = "Cristiano Vecchi has applied the Quantistic Counterpoint (multiple counterpoint on sets of vertical and horizontal intervals) first time in works like Primo Quartetto - Diurna Venus (1998), Emisfero Opposto (1999), and then in Lettere da Marte (2000), Chi semina vento (2000), Prossima del Centauro (2001), Dromomania (2003) and other works."
        val par2 = "The Extended Weighted Harmony algorithm has been conceived and coded by Cristiano Vecchi in 2005 and applied in works like Anelli di Saturno (2006), Introversione - Estroversione (2008)."
        val par3 = "All composition algorithms (MBTI interpreter included) used in the MikroKanon App have been conceived and coded by Cristiano Vecchi during 1998 - 2023."
//        val par4 = ""
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
                        }
                        item{
                            Text(text = buildAnnotatedString {
                                withStyle(titleStyle){
                                    append("Quantistic Counterpoint\n")
                                }
                                withStyle(commentStyle){
                                    append(par1)
                                }
                            })
                        }
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        item{
                            Text(text = buildAnnotatedString {
                                withStyle(titleStyle){
                                    append("Extended Weighted Harmony\n")
                                }
                                withStyle(commentStyle){
                                    append(par2)
                                }
                            })
                        }
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        item{
                            Text(text = buildAnnotatedString {
                                withStyle(titleStyle){
                                    append("Composition techniques converted into algorithms\n")
                                }
                                withStyle(commentStyle){
                                    append(par3)
                                }
                            })
                        }
//                        item{
//                            Text(text = buildAnnotatedString {
//
//                                withStyle(commentStyle){
//                                    append(par4)
//                                }
//                            })
//                        }
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

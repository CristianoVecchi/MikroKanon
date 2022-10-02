package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristianovecchi.mikrokanon.AIMUSIC.AbsPart
import com.cristianovecchi.mikrokanon.ui.AppColors
import com.cristianovecchi.mikrokanon.ui.shift

@Composable
fun SequenceAnalyzer(modifier: Modifier, absPitches: List<Int>, fontSize: Int, colors: AppColors, intervalNames: List<String> ) {
    val results: List<Int>  = AbsPart.analyzeAbsPitches(absPitches)
    val total = results.reduce{i, acc -> acc + i}
    val resultsPercent: List<Float> = results.map{
        if(it == 0 || total == 0) 0f else it/total.toFloat() * 100f
    }
    var isPercent by remember { mutableStateOf(false) }
    val intervalStyle = androidx.compose.ui.text.TextStyle(
        fontSize = fontSize.sp,
        color = colors.selCardTextColorSelected.shift(0.1f), fontWeight = FontWeight.Normal
    )
    val resultStyle = androidx.compose.ui.text.TextStyle(
        fontSize = (fontSize + 4).sp,
        color = colors.selCardTextColorSelected.shift(0.2f), fontWeight = FontWeight.Bold
    )
    val percentStyle = androidx.compose.ui.text.TextStyle(
        fontSize = (fontSize-4).sp,
        color = colors.selCardTextColorSelected.shift(0.2f), fontWeight = FontWeight.Bold
    )

        Row(modifier.background(colors.selCardBackColorSelected)
            .border(BorderStroke(2.dp, colors.selCardBackColorSelected.shift(-0.3f)) )
            .clickable{
                    isPercent = !isPercent
            },
            horizontalArrangement = Arrangement.SpaceEvenly){
            (0..6).forEach {
                Column(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally){
                    Text( text = intervalNames[it], style = intervalStyle )
                    Spacer(modifier = Modifier.height(5.dp))
                    val resultPercent = String.format("%.1f", resultsPercent[it])
                    Text( text = if(isPercent) "$resultPercent%" else "${results[it]}",
                        style = if(isPercent) percentStyle else resultStyle )
                }

            }
        }
}
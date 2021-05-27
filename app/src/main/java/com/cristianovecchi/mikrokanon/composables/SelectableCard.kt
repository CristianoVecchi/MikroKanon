package com.cristianovecchi.mikrokanon.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SelectableCard(text: String, fontSize: Int, isSelected: Boolean, onClick: (Int) -> Unit ){
    val backColor by animateColorAsState( if(isSelected) Color.White else Color.LightGray )
    val textColor  by animateColorAsState( if(isSelected) Color.Red else Color.Blue )
    val borderColor by animateColorAsState( if(isSelected) Color.Black else Color.DarkGray )
    val padding by animateDpAsState( if(isSelected) 3.dp else 2.dp )
    Card(modifier = Modifier
        .animateContentSize(animationSpec = tween(25,  easing = LinearEasing))
        //.aspectRatio( if (portraitMode) 3/4f else 16/9f)
        .padding(padding)
        .clip(RoundedCornerShape(6.dp))
        .clickable { if (isSelected) onClick(1) else onClick(-1) },
        backgroundColor = backColor,
        contentColor = textColor,
        border = BorderStroke(2.dp, borderColor )
    ) {
        Text(text = text, modifier = Modifier.padding(18.dp),
            style = TextStyle(fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold)
        )
    }
}
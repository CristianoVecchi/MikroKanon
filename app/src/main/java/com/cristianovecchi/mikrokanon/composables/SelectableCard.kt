package com.cristianovecchi.mikrokanon.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristianovecchi.mikrokanon.ui.AppColors
import com.cristianovecchi.mikrokanon.ui.shift


@Composable
fun SelectableCard(text: String, fontSize: Int, colors:AppColors, isSelected: Boolean, onClick: (Int) -> Unit ){

    val backColor by animateColorAsState( if(isSelected) colors.selCardBackColorSelected.shift(-0.1f) else colors.selCardBackColorUnselected.shift(-0.2f) )
    val textColor  by animateColorAsState( if(isSelected) colors.selCardTextColorSelected.shift(0.1f) else colors.selCardTextColorUnselected )
    val borderColor by animateColorAsState( if(isSelected) colors.selCardBorderColorSelected else colors.selCardBorderColorUnselected )
    val padding by animateDpAsState( if(isSelected) 2.dp else 1.dp )
    Card(backgroundColor = backColor,
        contentColor = textColor,
        border = BorderStroke(2.dp, borderColor),modifier = Modifier
        .animateContentSize(animationSpec = tween(25,  easing = LinearEasing))
        //.aspectRatio( if (portraitMode) 3/4f else 16/9f)
        .padding(padding)
        .clip(RoundedCornerShape(4.dp))
        .clickable { if (isSelected) onClick(1) else onClick(-1) }

    ) {
        Text(text = text, modifier = Modifier.padding(14.dp),
            style = TextStyle(fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold)
        )
    }
}
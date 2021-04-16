package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SelectedCard(text: String,  fontSize: Int, onClick: () -> Unit){
    val selectionBackColor = Color.White
    val selectionTextColor = Color.Red
    val selectionBorderColor = Color.Black
    Card(modifier = Modifier

        .clip(RoundedCornerShape(6.dp))
        .padding(3.dp)
        .clickable { onClick() },
        backgroundColor = selectionBackColor,
        contentColor = selectionTextColor,
        border = BorderStroke(2.dp, selectionBorderColor )
    ) {

        Text(text = text, modifier = Modifier.padding(18.dp),
            style = TextStyle(fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun UnSelectedCard(text: String, fontSize: Int, onClick: (Int) -> Unit ){
    val unselectionBackColor = Color.LightGray
    val unselectionTextColor = Color.Blue
    val unselectionBorderColor = Color.DarkGray
    Card(modifier = Modifier
        .clip(RoundedCornerShape(6.dp))
        .padding(2.dp)
        .clickable { onClick(-1) },
        backgroundColor = unselectionBackColor,
        contentColor = unselectionTextColor,
        border = BorderStroke(2.dp, unselectionBorderColor )
    ) {

        Text(text = text, modifier = Modifier.padding(18.dp),
            style = TextStyle(fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold)
        )
    }
}
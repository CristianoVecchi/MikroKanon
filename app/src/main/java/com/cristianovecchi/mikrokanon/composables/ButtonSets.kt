package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.ui.MikroKanonTheme
import com.cristianovecchi.mikrokanon.ui.iconButtonBackgroundColor
import com.cristianovecchi.mikrokanon.ui.iconButtonBorderColor
import com.cristianovecchi.mikrokanon.ui.iconButtonIconColor

@Composable
fun SequenceEditingButtons(
    model: AppViewModel, buttonSize: Dp,
    onDelete: () -> Unit, onEdit: () -> Unit, onAdd: () -> Unit)
{
    val borderColor = MaterialTheme.colors.iconButtonBorderColor
    val iconColor = MaterialTheme.colors.iconButtonIconColor
    val backgroundColor = MaterialTheme.colors.iconButtonBackgroundColor

    Column(horizontalAlignment = Alignment.Start) {
        // DEL
        IconButton(modifier = Modifier
            .padding(2.dp)
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .then(
                Modifier
                    .size(buttonSize)
                    .border(2.dp, borderColor)
            ),
            onClick = { onDelete() }
        )
        {
            Icon(
                painter = painterResource(id = model.iconMap["delete"]!!),
                contentDescription = null, // decorative element
                tint = iconColor
            )
        }
        //Edit Button
        IconButton(modifier = Modifier
            .padding(2.dp)
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .then(
                Modifier
                    .size(buttonSize)
                    .border(2.dp, borderColor)
            ),
            onClick = {
                onEdit()
            })
        {
            Icon(
                painter = painterResource(id = model.iconMap["edit"]!!),
                contentDescription = null, // decorative element
                tint = iconColor
            )
        }
        //ADD Button
        IconButton(modifier = Modifier
            .padding(2.dp)
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .then(
                Modifier
                    .size(buttonSize)
                    .border(2.dp, borderColor)
            ),
            onClick = { onAdd() })
        {
            Icon(
                painter = painterResource(id = model.iconMap["add"]!!),
                contentDescription = null, // decorative element
                tint = iconColor
            )
        }

    }
}


@Composable
fun MikroKanonsButtons(
    model: AppViewModel, buttonSize: Dp, fontSize: Int,
    onMK2Click: () -> Unit, onMK3Click: () -> Unit, onMK4Click: () -> Unit
) {
    val borderColor = MaterialTheme.colors.iconButtonBorderColor
    val iconColor = MaterialTheme.colors.iconButtonIconColor
    val backgroundColor = MaterialTheme.colors.iconButtonBackgroundColor
    Column(){
        val textStyle = TextStyle(
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            color = iconColor
        )
        IconButton(modifier = Modifier
            .padding(2.dp)
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .then(
                Modifier
                    .size(buttonSize)
                    .border(2.dp, borderColor)
            ),
            onClick = { onMK2Click() } )
        {
            Row(){
                Icon(
                    painter = painterResource(id = model.iconMap["mikrokanon"]!!),
                    contentDescription = null, // decorative element
                    tint =  iconColor )
                Text(text = "2", style = textStyle)
            }

        }
        IconButton(modifier = Modifier
            .padding(2.dp)
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .then(
                Modifier
                    .size(buttonSize)
                    .border(2.dp, borderColor)
            ),
            onClick = { onMK3Click() } )
        {
            Row() {
                Icon(
                    painter = painterResource(id = model.iconMap["mikrokanon"]!!),
                    contentDescription = null, // decorative element
                    tint = iconColor
                )
                Text(text = "3", style = textStyle)
            }
        }
        IconButton(modifier = Modifier
            .padding(2.dp)
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .then(
                Modifier
                    .size(buttonSize)
                    .border(2.dp, borderColor)
            ),
            onClick = { onMK4Click() } )
        {
            Row() {
                Icon(
                    painter = painterResource(id = model.iconMap["mikrokanon"]!!),
                    contentDescription = null, // decorative element
                    tint = iconColor
                )
                Text(text = "4", style = textStyle)
            }
        }
    }
}
@Composable
fun FreePartsButtons(
    fontSize: Int,
    onAscDynamicClick: () -> Unit, onAscStaticClick: () -> Unit,
    onDescDynamicClick: () -> Unit, onDescStaticClick: () -> Unit
) {
    val borderColor = MaterialTheme.colors.iconButtonBorderColor
    val iconColor = MaterialTheme.colors.iconButtonIconColor
    val backgroundColor = MaterialTheme.colors.iconButtonBackgroundColor

    Row() {
        Column() {
            //FPad Button
            Button(modifier = Modifier.padding(2.dp).border(2.dp, borderColor),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = backgroundColor,
                    contentColor = iconColor),
                onClick = { onAscDynamicClick() })
            {
                Text(
                    text = "∼\u279A",
                    style = TextStyle(
                        color = iconColor,
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            //FPdd Button
            Button(modifier = Modifier.padding(2.dp).border(2.dp, borderColor),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = backgroundColor,
                    contentColor = iconColor),
                onClick = { onDescDynamicClick() })
            {
                Text(
                    text = "∼\u2798",
                    style = TextStyle(
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold,
                        color = iconColor
                    )
                )
            }

        }
        Column() {
            // \u2B08
            //FPas Button
            Button(modifier = Modifier.padding(2.dp).border(2.dp, borderColor),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = backgroundColor,
                    contentColor = iconColor),
                onClick = { onAscStaticClick() })
            {
                Text(
                    text = "-➚",
                    style = TextStyle(
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold,
                        color = iconColor
                    )
                )
            }
            // \u2B0A
            //FPds Button
            Button(modifier = Modifier.padding(2.dp).border(2.dp, borderColor),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = backgroundColor,
                    contentColor = iconColor),
                onClick = { onDescStaticClick() })
            {
                Text(
                    text = "-➘",
                    style = TextStyle(
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold,
                        color = iconColor
                    )
                )
            }
        }
    }
}
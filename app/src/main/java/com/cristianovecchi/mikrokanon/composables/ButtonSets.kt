package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
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

@Composable
fun SequenceEditingButtons(
    model: AppViewModel, buttonSize: Dp,
    onDelete: () -> Unit, onEdit: () -> Unit, onAdd: () -> Unit)
{
    Column(horizontalAlignment = Alignment.Start) {
        // DEL
        IconButton(modifier = Modifier
            .padding(2.dp)
            .background(Color.White, RoundedCornerShape(4.dp))
            .then(
                Modifier
                    .size(buttonSize)
                    .border(2.dp, Color.Black)
            ),
            onClick = { onDelete() }


        )
        {
            Icon(
                painter = painterResource(id = model.iconMap["delete"]!!),
                contentDescription = null, // decorative element
                tint = Color.Blue
            )
        }
        //Edit Button
        IconButton(modifier = Modifier
            .padding(2.dp)
            .background(Color.White, RoundedCornerShape(4.dp))
            .then(
                Modifier
                    .size(buttonSize)
                    .border(2.dp, Color.Black)
            ),
            onClick = {
                onEdit()
            })
        {
            Icon(
                painter = painterResource(id = model.iconMap["edit"]!!),
                contentDescription = null, // decorative element
                tint = Color.Blue
            )
        }
        //ADD Button
        IconButton(modifier = Modifier
            .padding(2.dp)
            .background(Color.White, RoundedCornerShape(4.dp))
            .then(
                Modifier
                    .size(buttonSize)
                    .border(2.dp, Color.Black)
            ),
            onClick = { onAdd() })
        {
            Icon(
                painter = painterResource(id = model.iconMap["add"]!!),
                contentDescription = null, // decorative element
                tint = Color.Blue
            )
        }

    }
}


@Composable
fun MikroKanonsButtons(
    model: AppViewModel, buttonSize: Dp, fontSize: Int,
    onMK2Click: () -> Unit, onMK3Click: () -> Unit, onMK4Click: () -> Unit
) {
    Column(){
        val textStyle = TextStyle(
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Blue
        )
        IconButton(modifier = Modifier
            .padding(2.dp)
            .background(Color.White, RoundedCornerShape(4.dp))
            .then(
                Modifier
                    .size(buttonSize)
                    .border(2.dp, Color.Black)
            ),
            onClick = { onMK2Click() } )
        {
            Row(){
                Icon(
                    painter = painterResource(id = model.iconMap["mikrokanon"]!!),
                    contentDescription = null, // decorative element
                    tint =  Color.Blue )
                Text(text = "2", style = textStyle)
            }

        }
        IconButton(modifier = Modifier
            .padding(2.dp)
            .background(Color.White, RoundedCornerShape(4.dp))
            .then(
                Modifier
                    .size(buttonSize)
                    .border(2.dp, Color.Black)
            ),
            onClick = { onMK3Click() } )
        {
            Row() {
                Icon(
                    painter = painterResource(id = model.iconMap["mikrokanon"]!!),
                    contentDescription = null, // decorative element
                    tint = Color.Blue
                )
                Text(text = "3", style = textStyle)
            }
        }
        IconButton(modifier = Modifier
            .padding(2.dp)
            .background(Color.White, RoundedCornerShape(4.dp))
            .then(
                Modifier
                    .size(buttonSize)
                    .border(2.dp, Color.Black)
            ),
            onClick = { onMK4Click() } )
        {
            Row() {
                Icon(
                    painter = painterResource(id = model.iconMap["mikrokanon"]!!),
                    contentDescription = null, // decorative element
                    tint = Color.Blue
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
    Row() {
        Column() {
            //FPad Button
            Button(modifier = Modifier.padding(2.dp),
                onClick = { onAscDynamicClick() })
            {
                Text(
                    text = "∼\u279A",
                    style = TextStyle(
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            //FPdd Button
            Button(modifier = Modifier.padding(2.dp),
                onClick = { onDescDynamicClick() })
            {
                Text(
                    text = "∼\u2798",
                    style = TextStyle(
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

        }
        Column() {
            // \u2B08
            //FPas Button
            Button(modifier = Modifier.padding(2.dp),
                onClick = { onAscStaticClick() })
            {
                Text(
                    text = "-➚",
                    style = TextStyle(
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            // \u2B0A
            //FPds Button
            Button(modifier = Modifier.padding(2.dp),
                onClick = { onDescStaticClick() })
            {
                Text(
                    text = "-➘",
                    style = TextStyle(
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
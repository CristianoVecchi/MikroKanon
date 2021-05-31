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
import com.cristianovecchi.mikrokanon.ui.*

@Composable
fun CustomButton(iconId: Int = -1, text: String = "", isActive: Boolean = true, buttonSize: Dp = 60.dp, fontSize: Int = 16,
                 borderColor: Color = MaterialTheme.colors.iconButtonBorderColor,
                 iconColor: Color = MaterialTheme.colors.iconButtonIconColor,
                 backgroundColor: Color = MaterialTheme.colors.iconButtonBackgroundColor,
                 inactiveColor: Color = MaterialTheme.colors.iconButtonInactiveColor,
                 onClick : () -> Unit) {
    if (iconId != -1) {
        if (text.isNotEmpty()) { //MK button (Icon + Text)
            val textStyle = TextStyle(
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) iconColor else inactiveColor
            )
            IconButton(modifier = Modifier
                .padding(2.dp)
                .background(backgroundColor, RoundedCornerShape(4.dp))
                .then(
                    Modifier
                        .size(buttonSize)
                        .border(2.dp, borderColor)
                ),
                onClick = { if (isActive) onClick() })
            {
                Row() {
                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = null, // decorative element
                        tint = if (isActive) iconColor else inactiveColor
                    )
                    Text(text = text, style = textStyle)
                }

            }
        } else { // Icon button
            IconButton(modifier = Modifier
                .padding(2.dp)
                .background(backgroundColor, RoundedCornerShape(4.dp))
                .then(
                    Modifier
                        .size(buttonSize)
                        .border(2.dp, borderColor)
                ),
                onClick = { if (isActive) onClick() }
            )
            {
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = null, // decorative element
                    tint = if (isActive) iconColor else inactiveColor
                )
            }
        }
    } else {
        if (text.isNotEmpty()) { // text button
            Button(modifier = Modifier
                .padding(2.dp)
                .border(2.dp, borderColor),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = backgroundColor,
                    contentColor = iconColor
                ),
                onClick = { if (isActive) onClick() })
            {
                Text(
                    text = text,
                    style = TextStyle(
                        color = if (isActive) iconColor else inactiveColor,
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
@Composable
fun SequenceEditingButtons(
    model: AppViewModel, isActive: Boolean = true, buttonSize: Dp,
    onDelete: () -> Unit, onEdit: () -> Unit, onAdd: () -> Unit)
{
    Column(horizontalAlignment = Alignment.Start) {
        //DEL
        CustomButton(iconId = model.iconMap["delete"]!!, isActive = isActive, buttonSize = buttonSize) {
            onDelete()
        }
        //EDIT
        CustomButton(iconId = model.iconMap["edit"]!!, isActive = isActive, buttonSize = buttonSize) {
            onEdit()
        }
        //ADD -- ALWAYS ACTIVE!!!
        CustomButton(iconId = model.iconMap["add"]!!, isActive = true, buttonSize = buttonSize) {
            onAdd()
        }
    }
}

@Composable
fun MikroKanonsButtons(
    model: AppViewModel, isActive: Boolean = true, buttonSize: Dp, fontSize: Int,
    onMK2Click: () -> Unit, onMK3Click: () -> Unit, onMK4Click: () -> Unit
) {
    Column(){
        CustomButton(iconId = model.iconMap["mikrokanon"]!!, text = "2", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize) {
            onMK2Click()
        }
        CustomButton(iconId = model.iconMap["mikrokanon"]!!, text = "3", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize) {
            onMK3Click()
        }
        CustomButton(iconId = model.iconMap["mikrokanon"]!!, text = "4", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize) {
            onMK4Click()
        }
    }
}
@Composable
fun FreePartsButtons(
    fontSize: Int, isActive: Boolean = true,
    onAscDynamicClick: () -> Unit, onAscStaticClick: () -> Unit,
    onDescDynamicClick: () -> Unit, onDescStaticClick: () -> Unit
) {
    
    Row() {
        Column() {
            //FPad Button
            CustomButton(fontSize = fontSize, text = "∼➚", isActive = isActive) {
                onAscDynamicClick()
            }
            //FPdd Button
            CustomButton(fontSize = fontSize, text = "∼➘", isActive = isActive) {
                onDescDynamicClick()
            }
        }
        Column() {
            // \u2B08
            //FPas Button
            CustomButton(fontSize = fontSize, text = "-➚", isActive = isActive) {
                onAscStaticClick()
            }
            // \u2B0A
            //FPds Button
            CustomButton(fontSize = fontSize, text = "-➘", isActive = isActive) {
                onDescStaticClick()
            }
        }
    }
}
package com.cristianovecchi.mikrokanon.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
fun CustomButton(iconId: Int = -1, text: String = "",
                 isActive: Boolean = true, buttonSize: Dp = 60.dp, adaptSizeToIconButton: Boolean = false,
                 fontSize: Int = 16,
                 borderColor: Color = MaterialTheme.colors.iconButtonBorderColor,
                 iconColor: Color = MaterialTheme.colors.iconButtonIconColor,
                 backgroundColor: Color = MaterialTheme.colors.iconButtonBackgroundColor,
                 inactiveIconColor: Color = MaterialTheme.colors.iconButtonInactiveIconColor,
                 inactiveBackgroundColor: Color = MaterialTheme.colors.iconButtonInactiveBackgroundColor,
                 inactiveBorderColor: Color = MaterialTheme.colors.iconButtonInactiveBorderColor,
                 onClick : () -> Unit) {
    val actualBackgroundColor by animateColorAsState( if(isActive) backgroundColor else inactiveBackgroundColor )
    val actualIconColor  by animateColorAsState( if(isActive) iconColor else inactiveIconColor )
    val actualBorderColor by animateColorAsState( if(isActive) borderColor else inactiveBorderColor )
    if (iconId != -1) {
        if (text.isNotEmpty()) { //MK button (Icon + Text)
            val textStyle = TextStyle(
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold,
                color = actualIconColor
            )
            IconButton(modifier = Modifier
                .padding(2.dp)
                .background(actualBackgroundColor, RoundedCornerShape(4.dp))
                .then(
                    Modifier
                        .size(buttonSize)
                        .border(2.dp, actualBorderColor)
                ),
                onClick = { if (isActive) onClick() })
            {
                Row() {
                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = null, // decorative element
                        tint = actualIconColor
                    )
                    Text(text = text, style = textStyle)
                }

            }
        } else { // Icon button
            IconButton(modifier = Modifier
                .padding(2.dp)
                .background(actualBackgroundColor, RoundedCornerShape(4.dp))
                .then(
                    Modifier
                        .size(buttonSize)
                        .border(2.dp, actualBorderColor)
                ),
                onClick = { if (isActive) onClick() }
            )
            {
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = null, // decorative element
                    tint = actualIconColor
                )
            }
        }
    } else {
        if (text.isNotEmpty()) { // text button
            val actualModifier = if (adaptSizeToIconButton)
                Modifier.padding(2.dp).border(2.dp, actualBorderColor).width(buttonSize).height(buttonSize)
            else Modifier.padding(2.dp).border(2.dp, actualBorderColor)
            Button(modifier = actualModifier,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = actualBackgroundColor,
                    contentColor = iconColor
                ),
                onClick = { if (isActive) onClick() })
            {
                Text(
                    text = text,
                    style = TextStyle(
                        color = actualIconColor,
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
fun SpecialFunctions1Buttons(
    model: AppViewModel, isActive: Boolean = true, buttonSize: Dp, fontSize: Int,
    onTritoneSubstitution: () -> Unit, onRound: () -> Unit
) {
    Row {
        CustomButton(iconId = model.iconMap["tritone_substitution"]!!, isActive = isActive, buttonSize = buttonSize) {
            onTritoneSubstitution()
        }
        CustomButton(iconId = model.iconMap["round"]!!, isActive = isActive, buttonSize = buttonSize) {
            onRound()
        }
    }
}
@Composable
fun WavesButtons(
    model: AppViewModel, isActive: Boolean = true, buttonSize: Dp, fontSize: Int,
    onWave3Click: () -> Unit, onWave4Click: () -> Unit, onWave6Click: () -> Unit
) {
    Row(){
        CustomButton(iconId = model.iconMap["waves"]!!, text = "3", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize) {
            onWave3Click()
        }
        CustomButton(iconId = model.iconMap["waves"]!!, text = "4", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize) {
            onWave4Click()
        }
        CustomButton(iconId = model.iconMap["waves"]!!, text = "6", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize) {
            onWave6Click()
        }
    }
}
@Composable
fun FunctionButtons(
    model: AppViewModel, isActiveCounterpoint: Boolean = true, isActiveSpecialFunctions: Boolean = true, buttonSize: Dp,
    onAdd: () -> Unit, onSpecialFunctions: () -> Unit
) {
    Column(){
        CustomButton(iconId = model.iconMap["counterpoint"]!!, isActive = isActiveCounterpoint, buttonSize = buttonSize) {
            onAdd()
        }
        CustomButton(iconId = model.iconMap["special_functions"]!!, isActive = isActiveSpecialFunctions, buttonSize = buttonSize) {
            onSpecialFunctions()
        }
    }
}
@Composable
fun ExtensionButtons(
    model: AppViewModel, isActive: Boolean = true, buttonSize: Dp,
    onExpand: () -> Unit, onFlourish: () -> Unit
) {
    Column(){
        CustomButton(
            iconId = model.iconMap["expand"]!!,
            isActive = isActive,
            buttonSize = buttonSize
        ) {
            onExpand()
        }
        CustomButton(
            iconId = model.iconMap["fioritura"]!!,
            isActive = isActive,
            buttonSize = buttonSize
        ) {
            onFlourish()
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
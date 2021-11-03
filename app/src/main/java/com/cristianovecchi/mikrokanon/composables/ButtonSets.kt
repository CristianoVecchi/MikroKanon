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
                 colors: AppColors,
                 borderColor: Color = colors.iconButtonBorderColor,
                 iconColor: Color = colors.iconButtonIconColor,
                 backgroundColor: Color = colors.iconButtonBackgroundColor,
                 inactiveIconColor: Color = colors.iconButtonInactiveIconColor,
                 inactiveBackgroundColor: Color = colors.iconButtonInactiveBackgroundColor,
                 inactiveBorderColor: Color = colors.iconButtonInactiveBorderColor,
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
    model: AppViewModel, isActive: Boolean = true, buttonSize: Dp, colors: AppColors,
    onDelete: () -> Unit, onEdit: () -> Unit, onAdd: () -> Unit)
{
    Column(horizontalAlignment = Alignment.Start) {
        //DEL
        CustomButton(iconId = model.iconMap["delete"]!!, isActive = isActive, buttonSize = buttonSize, colors = colors) {
            onDelete()
        }
        //EDIT
        CustomButton(iconId = model.iconMap["edit"]!!, isActive = isActive, buttonSize = buttonSize, colors = colors) {
            onEdit()
        }
        //ADD -- ALWAYS ACTIVE!!!
        CustomButton(iconId = model.iconMap["add"]!!, isActive = true, buttonSize = buttonSize,colors = colors) {
            onAdd()
        }
    }
}

@Composable
fun MikroKanonsButtons(
    model: AppViewModel, isActive: Boolean = true, buttonSize: Dp, fontSize: Int, colors: AppColors,
    onMK2Click: () -> Unit, onMK3Click: () -> Unit, onMK4Click: () -> Unit
) {
    Column(){
        CustomButton(iconId = model.iconMap["mikrokanon"]!!, text = "2", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize,colors = colors) {
            onMK2Click()
        }
        CustomButton(iconId = model.iconMap["mikrokanon"]!!, text = "3", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize,colors = colors) {
            onMK3Click()
        }
        CustomButton(iconId = model.iconMap["mikrokanon"]!!, text = "4", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize,colors = colors) {
            onMK4Click()
        }
    }
}
@Composable
fun BoostedMikroKanonsButtons(
    model: AppViewModel, isActive: Boolean = true, buttonSize: Dp, fontSize: Int, colors: AppColors,
    onMK5reductedClick: () -> Unit
) {
    Row(){
        CustomButton(iconId = model.iconMap["mikrokanon"]!!, text = "5", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize,colors = colors) {
            onMK5reductedClick()
        }
    }
}
@Composable
fun SpecialFunctions1Buttons(
    model: AppViewModel, isActive: Boolean = true, buttonSize: Dp, fontSize: Int, colors: AppColors,
    onTritoneSubstitution: () -> Unit, onRound: () -> Unit, onCadenza: () -> Unit,
    onSingle: () -> Unit, onDoppelgänger: () -> Unit,
) {
    Column {
        Row{
            CustomButton(iconId = model.iconMap["single"]!!, isActive = isActive, buttonSize = buttonSize, colors = colors) {
                onSingle()
            }
            CustomButton(iconId = model.iconMap["doppelgänger"]!!, isActive = isActive, buttonSize = buttonSize, colors = colors) {
                onDoppelgänger()
            }
        }
        Row {
            CustomButton(iconId = model.iconMap["tritone_substitution"]!!, isActive = isActive, buttonSize = buttonSize, colors = colors) {
                onTritoneSubstitution()
            }
            CustomButton(iconId = model.iconMap["round"]!!, isActive = isActive, buttonSize = buttonSize, colors = colors) {
                onRound()
            }
            CustomButton(iconId = model.iconMap["cadenza"]!!, isActive = isActive, buttonSize = buttonSize, colors = colors) {
                onCadenza()
            }

        }
    }

}
@Composable
fun WavesButtons(
    model: AppViewModel, isActive: Boolean = true, buttonSize: Dp, fontSize: Int, colors: AppColors,
    onWave3Click: () -> Unit, onWave4Click: () -> Unit, onWave6Click: () -> Unit
) {
    Row(){
        CustomButton(iconId = model.iconMap["waves"]!!, text = "3", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize, colors = colors) {
            onWave3Click()
        }
        CustomButton(iconId = model.iconMap["waves"]!!, text = "4", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize, colors = colors) {
            onWave4Click()
        }
        CustomButton(iconId = model.iconMap["waves"]!!, text = "6", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize, colors = colors) {
            onWave6Click()
        }
    }
}
@Composable
fun PedalsButtons(
    model: AppViewModel, isActive: Boolean = true, buttonSize: Dp, fontSize: Int, colors: AppColors,
    onPedal1Click: () -> Unit, onPedal3Click: () -> Unit, onPedal5Click: () -> Unit
) {
    Row(){
        CustomButton(iconId = model.iconMap["pedal"]!!, text = "1",isActive = isActive, buttonSize = buttonSize, fontSize = fontSize, colors = colors) {
            onPedal1Click()
        }
        CustomButton(iconId = model.iconMap["pedal"]!!, text = "3", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize, colors = colors) {
            onPedal3Click()
        }
        CustomButton(iconId = model.iconMap["pedal"]!!, text = "5", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize, colors = colors) {
            onPedal5Click()
        }
    }
}
@Composable
fun FunctionButtons(
    model: AppViewModel, isActiveCounterpoint: Boolean = true, isActiveSpecialFunctions: Boolean = true, buttonSize: Dp, colors: AppColors,
    onAdd: () -> Unit, onSpecialFunctions: () -> Unit
) {
    Column(){
        CustomButton(iconId = model.iconMap["counterpoint"]!!, isActive = isActiveCounterpoint, buttonSize = buttonSize, colors = colors) {
            onAdd()
        }
        CustomButton(iconId = model.iconMap["special_functions"]!!, isActive = isActiveSpecialFunctions, buttonSize = buttonSize, colors = colors) {
            onSpecialFunctions()
        }
    }
}
@Composable
fun ExtensionButtons(
    model: AppViewModel, isActive: Boolean = true, buttonSize: Dp, colors: AppColors,
    onExpand: () -> Unit, onFlourish: () -> Unit
) {
    Column(){
        CustomButton(
            iconId = model.iconMap["expand"]!!,
            isActive = isActive,
            buttonSize = buttonSize , colors = colors
        ) {
            onExpand()
        }
        CustomButton(
            iconId = model.iconMap["fioritura"]!!,
            isActive = isActive,
            buttonSize = buttonSize, colors = colors
        ) {
            onFlourish()
        }


    }
}

@Composable
fun FreePartsButtons(
    fontSize: Int, isActive: Boolean = true, colors: AppColors,
    onAscDynamicClick: () -> Unit, onAscStaticClick: () -> Unit,
    onDescDynamicClick: () -> Unit, onDescStaticClick: () -> Unit
) {
    
    Row() {
        Column() {
            //FPad Button
            CustomButton(fontSize = fontSize, text = "∼➚", isActive = isActive, colors = colors) {
                onAscDynamicClick()
            }
            //FPdd Button
            CustomButton(fontSize = fontSize, text = "∼➘", isActive = isActive, colors = colors) {
                onDescDynamicClick()
            }
        }
        Column() {
            // \u2B08
            //FPas Button
            CustomButton(fontSize = fontSize, text = "-➚", isActive = isActive, colors = colors) {
                onAscStaticClick()
            }
            // \u2B0A
            //FPds Button
            CustomButton(fontSize = fontSize, text = "-➘", isActive = isActive, colors = colors) {
                onDescStaticClick()
            }
        }
    }
}
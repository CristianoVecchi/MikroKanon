package com.cristianovecchi.mikrokanon.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristianovecchi.mikrokanon.AIMUSIC.ARPEGGIO
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.ui.*

@Composable
fun SimpleIconButton(
    iconId: Int,
    buttonSize: Dp,
    borderWidth: Dp,
    iconColor: Color,
    colors: AppColors,
    isActive: Boolean = true,
    onClick: () -> Unit,
) {
    val borderColor: Color = colors.iconButtonBorderColor
    val backgroundColor: Color = colors.iconButtonBackgroundColor
    val inactiveIconColor: Color = colors.iconButtonInactiveIconColor
    val inactiveBackgroundColor: Color = colors.iconButtonInactiveBackgroundColor
    val inactiveBorderColor: Color = colors.iconButtonInactiveBorderColor
    val actualBackgroundColor by animateColorAsState( if(isActive) backgroundColor else inactiveBackgroundColor )
    val actualBorderColor by animateColorAsState( if(isActive) borderColor else inactiveBorderColor )
    val actualIconColor  by animateColorAsState( if(isActive) iconColor else inactiveIconColor )
    IconButton(
        modifier = Modifier//.size(buttonSize)
            .border(1.dp, actualBorderColor, RectangleShape)
            .background(actualBackgroundColor, RectangleShape)
            .size(buttonSize)
            ,
//        colors = ButtonDefaults.outlinedButtonColors(
//            backgroundColor = actualBackgroundColor
//        ),
//        border = BorderStroke(borderWidth, actualBorderColor),
//        shape = RectangleShape,
        onClick = { if (isActive) onClick() }
    ){
        Icon(
            modifier = Modifier.size(buttonSize / 4 * 3),
            painter = painterResource(id = iconId),
            contentDescription = null, // decorative element
            tint = actualIconColor
        )
    }
}
@Composable
fun CustomButton(iconId: Int = -1, text: String = "",
                 isActive: Boolean = true, buttonSize: Dp = 60.dp, adaptSizeToIconButton: Boolean = false,
                 fontSize: Int = 16, borderWidth: Dp = 2.dp,
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
    val padding = 4.dp
    if (iconId != -1) {
        if (text.isNotEmpty()) { //MK button (Icon + Text)
            val textStyle = TextStyle(
                fontSize = (fontSize + fontSize/7).sp,
                fontWeight = FontWeight.Bold,
                color = actualIconColor
            )
            IconButton(modifier = Modifier


                .padding(padding)
                .size(buttonSize)
                .border(borderWidth, actualBorderColor, RectangleShape)
                .background(actualBackgroundColor)

                ,
//                colors = ButtonDefaults.outlinedButtonColors(
//                    backgroundColor = actualBackgroundColor,
//                    contentColor = actualIconColor
//                ),
//                border = BorderStroke(border, actualBorderColor),
//                shape = RectangleShape,
                onClick = { if (isActive) onClick() })
            {
                Row{
                    Icon(
                        modifier = Modifier.size(buttonSize/3 + buttonSize/7),
                        painter = painterResource(id = iconId),
                        contentDescription = null, // decorative element
                        tint = actualIconColor
                    )
                    Text(text = text, style = textStyle)
                }
            }
        } else { // Icon button
            IconButton(modifier = Modifier
                .padding(padding)
                .size(buttonSize)
                .border(borderWidth, actualBorderColor, RectangleShape)
                .background(actualBackgroundColor, RectangleShape),
                onClick = { if (isActive) onClick() }
            )
            {
                Icon(
                    modifier = Modifier.size(buttonSize/2),
                    painter = painterResource(id = iconId),
                    contentDescription = null, // decorative element
                    tint = actualIconColor
                )
            }
        }
    } else {
        if (text.isNotEmpty()) { // text button
            if(adaptSizeToIconButton){
                Button(
                    modifier = Modifier


                        .padding(padding)
                        .border(borderWidth, actualBorderColor, RectangleShape)
                        .background(actualBackgroundColor, RectangleShape)
                        .size(buttonSize)
                    ,
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = actualBackgroundColor
                    ),

                    onClick = { if (isActive) onClick() })
                {
                    Text(
                        text = text,
                        style = TextStyle(
                            color = actualIconColor,
                            background= actualBackgroundColor,
                            fontSize = fontSize.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        overflow = TextOverflow.Clip,
                        maxLines = 1
                    )
                }
            } else {
                Button(
                    modifier = Modifier

                        .padding(padding)
                    .background(actualBackgroundColor)
                    .border(borderWidth, actualBorderColor, RectangleShape)
                    .height(buttonSize),
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = actualBackgroundColor
                    ),
                    onClick = { if (isActive) onClick() })
                {
                        Text(
                            text = text,
                            textAlign = TextAlign.Center,
                            fontSize = fontSize.sp,
                            fontWeight = FontWeight.Bold,
                            color = actualIconColor,
                            overflow = TextOverflow.Clip,
                            maxLines = 1,
                            modifier = Modifier.height(buttonSize / 10 * 7).wrapContentHeight()
                        )
                }
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
fun ExtendedWeightedHarmonyButtons(
    model: AppViewModel, isActive: Boolean = true, buttonSize: Dp, fontSize: Int,
    colors: AppColors,
    onEWH: (Int) -> Unit
) {
    Row {
        CustomButton(iconId = model.iconMap["accompanist"]!!, text = "1" , isActive = isActive, buttonSize = buttonSize, fontSize = fontSize,colors = colors) {
            onEWH(1)
        }
        CustomButton(iconId = model.iconMap["accompanist"]!!, text = "2" , isActive = isActive, buttonSize = buttonSize, fontSize = fontSize,colors = colors) {
            onEWH(2)
        }
        CustomButton(iconId = model.iconMap["accompanist"]!!, text = "3" , isActive = isActive, buttonSize = buttonSize, fontSize = fontSize,colors = colors) {
            onEWH(3)
        }
        CustomButton(iconId = model.iconMap["accompanist"]!!, text = "4" , isActive = isActive, buttonSize = buttonSize, fontSize = fontSize,colors = colors) {
            onEWH(4)
        }
    }
}
@Composable
fun SlotButtons(
    model: AppViewModel, isActive: Boolean = true, buttonSize: Dp, fontSize: Int,
    colors: AppColors, start: Int, numbers: List<String>, filled: Set<Int>,
    onCounterpointSelected: (Int) -> Unit
) {
            Row {
                CustomButton(iconId = model.iconMap["save"]!!, text = if(filled.contains(start+0)) numbers[start+0] else " ", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize,colors = colors) {
                    onCounterpointSelected(start+0)
                }
                CustomButton(iconId = model.iconMap["save"]!!, text = if(filled.contains(start+1)) numbers[start+1] else " ", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize,colors = colors) {
                    onCounterpointSelected(start+1)
                }
                CustomButton(iconId = model.iconMap["save"]!!, text = if(filled.contains(start+2)) numbers[start+2] else " ", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize,colors = colors) {
                    onCounterpointSelected(start+2)
                }
                CustomButton(iconId = model.iconMap["save"]!!, text = if(filled.contains(start+3)) numbers[start+3] else " ", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize,colors = colors) {
                    onCounterpointSelected(start+3)
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
    onMK5reductedClick: () -> Unit, onMK6reductedClick: () -> Unit
) {
    Row(){
        CustomButton(iconId = model.iconMap["mikrokanon"]!!, text = "5", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize,colors = colors) {
            onMK5reductedClick()
        }
        CustomButton(iconId = model.iconMap["mikrokanon"]!!, text = "6", isActive = isActive, buttonSize = buttonSize, fontSize = fontSize,colors = colors) {
            onMK6reductedClick()
        }
    }
}
@Composable
fun NotFromSelectorButtons(
    model: AppViewModel, isActive: Boolean = true, buttonSize: Dp, fontSize: Int, colors: AppColors,
    onUpsideDown: () -> Unit, onSort: (Int) -> Unit, onArpeggio: (ARPEGGIO) -> Unit, onParade: () -> Unit,
    progressiveEWH: () -> Unit,
) {
    Row{
        CustomButton(iconId = model.iconMap["sort_up"]!!, isActive = isActive, buttonSize = buttonSize, colors = colors) {
            onSort(0)
        }
        CustomButton(iconId = model.iconMap["sort_down"]!!, isActive = isActive, buttonSize = buttonSize, colors = colors) {
            onSort(1)
        }
        CustomButton(iconId = model.iconMap["upside_down"]!!, isActive = isActive, buttonSize = buttonSize, colors = colors) {
            onUpsideDown()
        }
        CustomButton(iconId = model.iconMap["parade"]!!, isActive = isActive, buttonSize = buttonSize, colors = colors) {
            onParade()
        }
    }
    Row{
        CustomButton(iconId = model.iconMap["arpeggio"]!!, text = "➚", isActive = isActive, buttonSize = buttonSize, colors = colors) {
            onArpeggio(ARPEGGIO.ASCENDANT)
        }
        CustomButton(iconId = model.iconMap["arpeggio"]!!, text = "⇅", isActive = isActive, buttonSize = buttonSize, colors = colors) {
            onArpeggio(ARPEGGIO.SINUS)
        }
        CustomButton(iconId = model.iconMap["arpeggio"]!!, text = "≈", isActive = isActive, buttonSize = buttonSize, colors = colors) {
            onArpeggio(ARPEGGIO.WAVES)
        }
        CustomButton(iconId = model.iconMap["accompanist"]!!, text = "<", isActive = isActive, buttonSize = buttonSize, colors = colors) {
            progressiveEWH()
        }
    }
}
@Composable
fun BuildingButtons(
    model: AppViewModel, isActive: Boolean = true, buttonSize: Dp, colors: AppColors,
    onOverlap: () -> Unit, onCrossover: () -> Unit, onGlue: () -> Unit, onScarlatti: () -> Unit
) {

        Row {
            CustomButton(iconId = model.iconMap["Scarlatti"]!!, isActive = true, buttonSize = buttonSize, colors = colors) {
                onScarlatti()
            }
            CustomButton(iconId = model.iconMap["overlap"]!!, isActive = isActive, buttonSize = buttonSize, colors = colors) {
                onOverlap()
            }
            CustomButton(iconId = model.iconMap["crossover"]!!, isActive = true, buttonSize = buttonSize, colors = colors) {
                onCrossover()
            }
            CustomButton(iconId = model.iconMap["glue"]!!, isActive = true, buttonSize = buttonSize, colors = colors) {
                onGlue()
            }
        }
    }
@Composable
fun SpecialFunctions1Buttons(
    model: AppViewModel, isActive: Boolean = true, buttonSize: Dp, fontSize: Int, colors: AppColors,
    onTritoneSubstitution: () -> Unit, onRound: () -> Unit, onCadenza: () -> Unit, onFlourish: () -> Unit,
    onMaze: () -> Unit, onSingle: () -> Unit, onDoppelgänger: () -> Unit, onEraseIntervals: () -> Unit,
    onResolutio: () -> Unit, onDoubling: () -> Unit, onChess: () -> Unit, onFormat: () -> Unit
) {
    Column {
        Row {
            CustomButton(iconId = model.iconMap["cadenza"]!!, isActive = true, buttonSize = buttonSize, colors = colors) {
                onCadenza()
            }
            CustomButton(iconId = model.iconMap["resolutio"]!!, isActive = true, buttonSize = buttonSize, colors = colors) {
                onResolutio()
            }
            CustomButton(iconId = model.iconMap["format"]!!, isActive = true, buttonSize = buttonSize, colors = colors) {
                onFormat()
            }
            CustomButton(iconId = model.iconMap["doubling"]!!, isActive = isActive, buttonSize = buttonSize, colors = colors) {
                onDoubling()
            }
        }
        Row{
            CustomButton(iconId = model.iconMap["single"]!!, isActive = true, buttonSize = buttonSize, colors = colors) {
                onSingle()
            }
            CustomButton(iconId = model.iconMap["fioritura"]!!, isActive = true, buttonSize = buttonSize, colors = colors) {
                onFlourish()
            }
            CustomButton(iconId = model.iconMap["doppelgänger"]!!, isActive = isActive, buttonSize = buttonSize, colors = colors) {
                onDoppelgänger()
            }
            CustomButton(iconId = model.iconMap["chess"]!!, isActive = true, buttonSize = buttonSize, colors = colors) {
                onChess()
            }
        }
        Row {
            CustomButton(iconId = model.iconMap["tritone_substitution"]!!, isActive = true, buttonSize = buttonSize, colors = colors) {
                onTritoneSubstitution()
            }
            CustomButton(iconId = model.iconMap["round"]!!, isActive = true, buttonSize = buttonSize, colors = colors) {
                onRound()
            }
            CustomButton(iconId = model.iconMap["maze"]!!, isActive = true, buttonSize = buttonSize, fontSize = fontSize,colors = colors) {
                onMaze()
            }
            CustomButton(iconId = model.iconMap["erase"]!!, isActive = true, buttonSize = buttonSize, colors = colors) {
                onEraseIntervals()
            }
        }
    }

}
@Composable
fun WavesButtons(
    model: AppViewModel, isActive: Boolean = true, buttonSize: Dp, fontSize: Int, colors: AppColors,
    onWave3Click: () -> Unit, onWave4Click: () -> Unit, onWave6Click: () -> Unit, onQuote: () -> Unit
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
        CustomButton(iconId = model.iconMap["quote"]!!, isActive = isActive, buttonSize = buttonSize, fontSize = fontSize, colors = colors) {
            onQuote()
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
    onExpand: () -> Unit, onTranspose: () -> Unit//onFlourish: () -> Unit
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
            iconId = model.iconMap["transpose"]!!,
            isActive = isActive,
            buttonSize = buttonSize, colors = colors
        ) {
            onTranspose()
        }


    }
}

@Composable
fun FreePartsButtons(
    model: AppViewModel, fontSize: Int, isActive: Boolean = true, buttonSize:Dp, colors: AppColors,
    onAscDynamicClick: () -> Unit, onAscStaticClick: () -> Unit,
    onDescDynamicClick: () -> Unit, onDescStaticClick: () -> Unit
) {
    //"∼➚" "-➚" "∼➘" "-➘"
    Column() {
        Row(verticalAlignment = Alignment.Bottom) {
            //FPad Button
            CustomButton(iconId = model.iconMap["dynamic"]!!, fontSize = fontSize, buttonSize = buttonSize, text = "➚", isActive = isActive, colors = colors) {
                onAscDynamicClick()
            }
            // \u2B08
            //FPas Button
            CustomButton(iconId = model.iconMap["static"]!!, fontSize = fontSize, buttonSize = buttonSize, text = "➚", isActive = isActive, colors = colors) {
                onAscStaticClick()
            }

        }
        Row(verticalAlignment = Alignment.Bottom) {
            //FPdd Button
            CustomButton(iconId = model.iconMap["dynamic"]!!, fontSize = fontSize, buttonSize = buttonSize,  text = "➘", isActive = isActive, colors = colors) {
                onDescDynamicClick()
            }
            // \u2B0A
            //FPds Button
            CustomButton(iconId = model.iconMap["static"]!!, fontSize = fontSize, buttonSize = buttonSize,  text = "➘", isActive = isActive, colors = colors) {
                onDescStaticClick()
            }
        }
    }
}
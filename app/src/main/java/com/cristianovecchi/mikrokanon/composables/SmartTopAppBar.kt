package com.cristianovecchi.mikrokanon.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.ui.AIColor
import com.cristianovecchi.mikrokanon.ui.AppColors
import com.cristianovecchi.mikrokanon.ui.Dimensions
import com.cristianovecchi.mikrokanon.ui.shift

@Composable
fun SmartTopAppBar(modifier: Modifier, dimensions: Dimensions, colors: AppColors, model: AppViewModel,
onMenuClick: () -> Unit, onCreditsClick: () -> Unit){
    val titleStyle = SpanStyle(
        fontSize = dimensions.titleTextSize.first.sp,
        color = colors.cellTextColorSelected)
    val creditColor = if(AIColor.colorDistanceAverage(colors.cellTextColorUnselected.toArgb(),colors.selCardBorderColorSelected.toArgb()) <= 0.124) colors.cellTextColorUnselected.shift(-0.4f) else colors.cellTextColorUnselected
    val creditStyle = SpanStyle(
        fontSize = dimensions.titleTextSize.second.sp,
        color = creditColor)
    val buildingState by model.buildingState.asFlow().collectAsState(initial = Triple(AppViewModel.Building.NONE, listOf(),0))
    var buildingHasStarted by remember {
        mutableStateOf(false)
    }
    buildingHasStarted = when (buildingState.first) {
        AppViewModel.Building.START -> true
        AppViewModel.Building.NONE -> false
        else -> buildingHasStarted
    }
    TopAppBar(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(colors.selCardBorderColorSelected), //color of the top app bar
            //.border(1.dp, Color.Transparent),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        )
        {
            val jobIsActive = model.jobPlay?.let{
                it.isActive || !it.isCancelled || !it.isCompleted
            } ?: false
            if(!jobIsActive || !buildingHasStarted || buildingState.first == AppViewModel.Building.NONE) {
                //if(!buildingHasStarted || buildingState.first == AppViewModel.Building.NONE) {
                IconButton(
                    modifier = Modifier.size(dimensions.selectorButtonSize),
                    onClick = { onMenuClick() }
                ) {
                    Icon(
                        Icons.Filled.Menu, "",
                        modifier = Modifier.size(dimensions.selectorButtonSize/2),
                        tint = colors.cellTextColorSelected)
                }
                ClickableText(text = buildAnnotatedString {
                    withStyle(titleStyle){
                        append("MikroKanon\n")
                    }
                    withStyle(creditStyle) {
                        append("by Cristiano Vecchi")
                    }
                },onClick = { onCreditsClick() })
            } else {
                val iconId = when(buildingState.first){
                    AppViewModel.Building.NONE -> model.iconMap["building"] //unused
                    AppViewModel.Building.START -> model.iconMap["sound"]
                    AppViewModel.Building.DATATRACKS -> model.iconMap["building"]
                    AppViewModel.Building.CHECK_N_REPLACE -> model.iconMap["accompanist"]
                    AppViewModel.Building.MIDITRACKS -> model.iconMap["construction"]
                    AppViewModel.Building.WRITE_FILE -> model.iconMap["save"]
                }
                Box(
                    Modifier
                        .width(dimensions.width.dp)
                        .fillMaxHeight()
                ){
                    val percentage by derivedStateOf{
                        when (buildingState.first) {
                            AppViewModel.Building.WRITE_FILE -> 15f/16
                            AppViewModel.Building.START -> 1f/16
                            else -> buildingState.second.toSet().size.toFloat() / buildingState.third
                        }
                    }
                    val percWidth by animateFloatAsState(targetValue = percentage)
                    Canvas(modifier = Modifier
                        .fillMaxSize()
                        .padding(0.dp, 4.dp)
                    ) {
                        val allX = this.size.width
                        val allY = this.size.height
                        drawRect(creditColor, Offset(0f,0f ),
                            Size(allX,allY)
                        )
                        drawRect(colors.selCardBorderColorSelected, Offset(0f,0f ),
                            Size(allX * percWidth,allY)
                        )
                    }

                    Row(
                        Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                            .clickable{ onMenuClick() },
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painterResource(id = iconId!!), "",
                            modifier = Modifier.size(dimensions.selectorButtonSize/2),
                            tint = colors.cellTextColorSelected)
                    }
                }

            }
        }
    }
}
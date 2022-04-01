package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.ScaffoldTabs
import com.cristianovecchi.mikrokanon.ui.AppColors
import com.cristianovecchi.mikrokanon.ui.Dimensions
import com.cristianovecchi.mikrokanon.ui.shift

@Composable
fun SettingTabs(selectedTab: ScaffoldTabs, dimensions: Dimensions, colors: AppColors, model: AppViewModel) {
    Row(Modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
        val buttonSize = dimensions.selectorButtonSize
        val iconSize = dimensions.selectorButtonSize / 2

        Column(Modifier
            .weight(1f)
            .background(
                if (selectedTab == ScaffoldTabs.SOUND) colors.drawerBackgroundColor
                else colors.drawerBackgroundColor.shift(-0.2f)
            )
            .clickable(onClick = {
                model._lastScaffoldTab.value = ScaffoldTabs.SOUND
            }), horizontalAlignment = Alignment.CenterHorizontally){
            IconButton(modifier = androidx.compose.ui.Modifier
                .background(
                    if (selectedTab == ScaffoldTabs.SOUND) colors.drawerBackgroundColor else colors.drawerBackgroundColor.shift(
                        -0.2f
                    ), RoundedCornerShape(4.dp)
                )
                .then(Modifier.size(buttonSize / 4 * 3)), onClick = { model._lastScaffoldTab.value = ScaffoldTabs.SOUND }
            )
            {
                Icon(
                    modifier = Modifier.size(iconSize),
                    painter = painterResource(id = model.iconMap["sound"]!!),
                    contentDescription = null, // decorative element
                    tint = if(selectedTab == ScaffoldTabs.SOUND) colors.selCardTextColorSelected else colors.selCardTextColorUnselected.shift(-0.2f)
                )
            }
        }
        Column(Modifier
            .weight(1f)
            .background(
                if (selectedTab == ScaffoldTabs.BUILDING) colors.drawerBackgroundColor
                else colors.drawerBackgroundColor.shift(-0.2f)
            )
            .clickable(onClick = {
                model._lastScaffoldTab.value = ScaffoldTabs.BUILDING
            }), horizontalAlignment = Alignment.CenterHorizontally){
            IconButton(modifier = androidx.compose.ui.Modifier
                .background(
                    if (selectedTab == ScaffoldTabs.BUILDING) colors.drawerBackgroundColor else colors.drawerBackgroundColor.shift(
                        -0.2f
                    ), RoundedCornerShape(4.dp)
                )
                .then(Modifier.size(buttonSize / 4 * 3)), onClick = { model._lastScaffoldTab.value = ScaffoldTabs.BUILDING  }
            )
            {
                Icon(
                    modifier = Modifier.size(iconSize),
                    painter = painterResource(id = model.iconMap["building"]!!),
                    contentDescription = null, // decorative element
                    tint = if(selectedTab == ScaffoldTabs.BUILDING) colors.selCardTextColorSelected else colors.selCardTextColorUnselected.shift(-0.2f)
                )
            }
        }
        Column(
            Modifier
                .weight(1f)
                .background(
                    if (selectedTab == ScaffoldTabs.SETTINGS) colors.drawerBackgroundColor
                    else colors.drawerBackgroundColor.shift(-0.2f)
                )
                .clickable(onClick = {
                    model._lastScaffoldTab.value = ScaffoldTabs.SETTINGS
                }), horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            IconButton(modifier = androidx.compose.ui.Modifier
                .background(
                    if (selectedTab == ScaffoldTabs.SETTINGS) colors.drawerBackgroundColor else colors.drawerBackgroundColor.shift(
                        -0.2f
                    ), RoundedCornerShape(4.dp)
                )
                .then(Modifier.size(buttonSize / 4 * 3)), onClick = { model._lastScaffoldTab.value = ScaffoldTabs.SETTINGS }
            )
            {
                Icon(
                    modifier = Modifier.size(iconSize),
                    painter = painterResource(id = model.iconMap["settings"]!!),
                    contentDescription = null, // decorative element
                    tint = if(selectedTab == ScaffoldTabs.SETTINGS) colors.selCardTextColorSelected else colors.selCardTextColorUnselected.shift(-0.2f)
                )
            }
        }

    }
}
package com.cristianovecchi.mikrokanon.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.AppViewModel

private val DarkColorPalette = darkColors(
        primary = purple200,
        primaryVariant = purple700,
        secondary = teal200


)

private val LightColorPalette = lightColors(
        primary = purple500,
        primaryVariant = purple700,
        secondary = teal200,

        /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun MikroKanonTheme(model: AppViewModel, darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
//    val colors = if (darkTheme) {
//        DarkColorPalette
//    } else {
//        LightColorPalette
//    }
//   // val colors = LightColorPalette
    val userOptionsData by model.userOptionsData.asFlow().collectAsState(initial = listOf())
    val appColors by derivedStateOf {
        if(userOptionsData.isNotEmpty()) model.setAppColors(userOptionsData[0].colors)
        model.appColors // default ALL BLACK
    }
    val overridingColor = appColors.selCardBorderColorSelected
    val colors = lightColors(primary = overridingColor,
                                primaryVariant = overridingColor,
                                secondaryVariant = Color.Black,
                                secondary = overridingColor,
                                onSurface = overridingColor,
                                )
    MaterialTheme(
            colors = colors,
            typography = typography,
            shapes = shapes,
            content = content
    )
}
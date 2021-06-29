package com.cristianovecchi.mikrokanon.ui

import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val purple200 = Color(0xFFBB86FC)
//val purple500 = Color(0xFF6200EE)
val purple500 = Color(0.0f,0.2f,1.0f,1.0f)
val purple700 = Color(0xFF3700B3)
val teal200 = Color(0xFF03DAC5)


val Colors.selCardBackColorSelected: Color
    @Composable
    get() = if (isLight) Color.White else Color.White
val Colors.selCardBackColorUnselected: Color
    @Composable
    get() = if (isLight) Color.Gray else Color.LightGray

val Colors.selCardTextColorSelected: Color
    @Composable
    get() = if (isLight) Color.Red else Color.Red
val Colors.selCardTextColorUnselected: Color
    @Composable
    get() = if (isLight) Color(0.2f,0.2f,1.0f,1.0f) else Color.Blue

val Colors.selCardBorderColorSelected: Color
    @Composable
    get() = if (isLight) Color.Black else Color.Black

val Colors.selCardBorderColorUnselected: Color
    @Composable
    get() = if (isLight) Color.DarkGray else Color.DarkGray

val Colors.iconButtonBorderColor: Color
    @Composable
    get() = if (isLight) Color.Black else Color.Black
val Colors.iconButtonIconColor: Color
    @Composable
    get() = if (isLight) Color.Blue else Color.Blue
val Colors.iconButtonBackgroundColor: Color
    @Composable
    get() = if (isLight) Color.White else Color.White
val Colors.iconButtonInactiveIconColor: Color
    @Composable
    get() = if (isLight) Color.White else Color.LightGray
val Colors.iconButtonInactiveBackgroundColor: Color
    @Composable
    get() = if (isLight) Color(0.8f,0.8f,0.8f,1.0f) else Color.LightGray
val Colors.iconButtonInactiveBorderColor: Color
    @Composable
    get() = if (isLight) Color(0.5f,0.5f,0.5f,1.0f) else Color.LightGray

val Colors.sequencesListBackgroundColor: Color
    @Composable
    get() = if (isLight) Color(0.35f,0.35f,1.0f,1.0f) else Color(0.5f,0.5f,1.0f,1.0f)
val Colors.buttonsDisplayBackgroundColor: Color
    @Composable
    get() = if (isLight) Color(0.30f,0.30f,1.0f,1.0f) else Color(0.4f,0.4f,1.0f,1.0f)

val Colors.drawerBackgroundColor: Color
    @Composable
    get() = if (isLight) Color(0.2f,0.2f,1.0f,1.0f) else Color(0.4f,0.4f,1.0f,1.0f)

val Colors.inputBackgroundColor: Color
    @Composable
    get() = if (isLight) Color(0.3f,0.3f,0.6f,1.0f) else Color(0.4f,0.4f,1.0f,1.0f)
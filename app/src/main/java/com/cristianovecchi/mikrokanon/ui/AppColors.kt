package com.cristianovecchi.mikrokanon.ui

import androidx.compose.ui.graphics.Color

enum class AppColorThemes(val title :String){
    GEMINI_BLUE("Gemini Blue")
}
data class AppColors(
    val color: Color = Color.Blue
){
    companion object{
        fun provideAppColors(colorTheme: AppColorThemes): AppColors {
            return when (colorTheme) {
                AppColorThemes.GEMINI_BLUE -> AppColors() // DEFAULT
            }
        }
    }
}
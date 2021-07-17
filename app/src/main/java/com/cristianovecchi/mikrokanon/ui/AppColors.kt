package com.cristianovecchi.mikrokanon.ui

import androidx.compose.ui.graphics.Color

enum class AppColorThemes(val title :String){
    GEMINI_BLUE("Gemini Blue")
}
data class AppColors(
    val selCardBackColorSelected: Color = Color.White,
    val selCardBackColorUnselected: Color = Color.Gray,
    val selCardTextColorSelected: Color = Color.Red,
    val selCardTextColorUnselected: Color = Color(0.2f,0.2f,1.0f,1.0f),
    val selCardBorderColorSelected: Color = Color.Black,
    val selCardBorderColorUnselected: Color = Color.DarkGray,

    val iconButtonBorderColor: Color = Color.Black,
    val iconButtonIconColor: Color = Color.Blue,
    val iconButtonBackgroundColor: Color = Color.White,
    val iconButtonInactiveBorderColor: Color = Color(0.5f,0.5f,0.5f,1.0f),
    val iconButtonInactiveIconColor: Color = Color.White,
    val iconButtonInactiveBackgroundColor: Color = Color(0.8f,0.8f,0.8f,1.0f),

    val sequencesListBackgroundColor: Color = Color(0.35f,0.35f,1.0f,1.0f),
    val buttonsDisplayBackgroundColor: Color = Color(0.30f,0.30f,1.0f,1.0f),

    val drawerBackgroundColor: Color = Color(0.2f,0.2f,1.0f,1.0f),
    val inputBackgroundColor: Color = Color(0.3f,0.3f,0.6f,1.0f)
){
    companion object{
        fun provideAppColors(colorTheme: AppColorThemes): AppColors {
            return when (colorTheme) {
                AppColorThemes.GEMINI_BLUE -> AppColors() // DEFAULT
            }
        }
    }
}
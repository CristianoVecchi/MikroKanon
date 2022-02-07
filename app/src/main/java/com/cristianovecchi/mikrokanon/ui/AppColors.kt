package com.cristianovecchi.mikrokanon.ui

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.text.isDigitsOnly
import com.cristianovecchi.mikrokanon.G

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

    val cellDarkColorSelected: Color = Color(0.3f,0.3f,0.9f,1.0f),
    val cellDarkColorUnselected: Color = Color(0.1f,0.1f,0.65f,1.0f),
    val cellLightColorSelected: Color = Color(0.3f,0.3f,1f,1.0f),
    val cellLightColorUnselected: Color = Color(0.1f,0.1f,0.70f,1.0f),
    val selectionBorderColor: Color = Color(0.8f,0.8f,0.9f,1.0f),
    val cellTextColorSelected: Color = Color.White,
    val cellTextColorUnselected: Color = Color(0.8f,0.8f,0.8f,1.0f),

    val iconButtonBorderColor: Color = Color.Black,
    val iconButtonIconColor: Color = Color.Blue,
    val iconButtonBackgroundColor: Color = Color.White,
    val iconButtonInactiveBorderColor: Color = Color(0.5f,0.5f,0.5f,1.0f),
    val iconButtonInactiveIconColor: Color = Color.White,
    val iconButtonInactiveBackgroundColor: Color = Color(0.8f,0.8f,0.8f,1.0f),

    val sequencesListBackgroundColor: Color = Color(0.35f,0.35f,1.0f,1.0f),
    val buttonsDisplayBackgroundColor: Color = Color(0.30f,0.30f,1.0f,1.0f),

    val drawerBackgroundColor: Color = Color(0.2f,0.2f,1.0f,1.0f),
    val inputBackgroundColor: Color = Color(0.3f,0.3f,0.6f,1.0f),

    val alphas: Array<Float> = (0..12).map { 1f - it * 0.03f }.toTypedArray()
){
    companion object{
        fun allBlack(): AppColors{
            return AppColors(
                selCardBackColorSelected = Color.Black,
                selCardBackColorUnselected = Color.Black,
                selCardTextColorSelected = Color.Black,
                selCardTextColorUnselected = Color.Black,
                selCardBorderColorSelected = Color.Black,
                selCardBorderColorUnselected = Color.Black,

                cellDarkColorUnselected = Color.Black,
                cellLightColorUnselected = Color.Black,
                cellDarkColorSelected = Color.Black,
                cellLightColorSelected = Color.Black,
                selectionBorderColor = Color.Black,
                cellTextColorSelected = Color.Black,
                cellTextColorUnselected = Color.Black,

                iconButtonBorderColor = Color.Black,
                iconButtonIconColor = Color.Black,
                iconButtonBackgroundColor = Color.Black,
                iconButtonInactiveBorderColor = Color.Black,
                iconButtonInactiveIconColor = Color.Black,
                iconButtonInactiveBackgroundColor = Color.Black,

                sequencesListBackgroundColor = Color.Black,
                buttonsDisplayBackgroundColor = Color.Black,
                drawerBackgroundColor = Color.Black,
                inputBackgroundColor =  Color.Black

            )
        }
        fun createCustomColors(fontColor: Color, backgroundColor1: Color, backgroundColor2: Color,
                               beat: Color, pass1: Color, pass2: Color, radar: Color): AppColors{
            return AppColors(
                selCardBackColorSelected = pass1,
                selCardBackColorUnselected = pass2,
                selCardTextColorSelected = beat.shift(0.2f),
                selCardTextColorUnselected = beat,
                selCardBorderColorSelected = radar,
                selCardBorderColorUnselected = radar.shift(-0.1f),

                cellDarkColorUnselected = backgroundColor1.shift(-0.02f),
                cellLightColorUnselected = backgroundColor1,
                cellDarkColorSelected = backgroundColor1.shift(0.08f),
                cellLightColorSelected = backgroundColor1.shift(0.1f),
                selectionBorderColor = fontColor.shift(-0.1f),
                cellTextColorSelected = fontColor,
                cellTextColorUnselected = fontColor.shift(-0.2f),

                iconButtonBorderColor = fontColor.shift(-0.15f),
                iconButtonIconColor = fontColor.shift(0.3f),
                iconButtonBackgroundColor = radar,
                iconButtonInactiveBorderColor = fontColor.shift(-0.35f),
                iconButtonInactiveIconColor = radar.shift(-0.2f),
                iconButtonInactiveBackgroundColor = fontColor.shift(-0.3f),

                sequencesListBackgroundColor = backgroundColor2,
                buttonsDisplayBackgroundColor = backgroundColor2.shift(-0.1f),
                drawerBackgroundColor = backgroundColor1.shift(-0.4f),
                inputBackgroundColor =  backgroundColor2.shift(-0.2f)

            )
        }
        fun provideAppColors(colorTheme: AppColorThemes): AppColors {
            return when (colorTheme) {
                AppColorThemes.GEMINI_BLUE -> AppColors() // DEFAULT
            }
        }

        fun getCustomColorsFromIndex(context: Context, index: Int): AppColors{
            if(G.loadColorArrays(context))
                G.setColorArray(context, index)
            val fontColor = Color(G.colorFont)
            val back1Color = Color(G.colorBackground1)
            val back2Color = Color(G.colorBackground2)
            val beatColor = Color(G.colorBeatNotes)
            val pass1Color = Color(G.colorPassageNotes1)
            val pass2Color = Color(G.colorPassageNotes2)
            val radarColor = Color(G.colorRadar)
            return createCustomColors(fontColor,back1Color,back2Color,beatColor,pass1Color,pass2Color,radarColor)
        }
    }

}

fun Color.shift(diff: Float): Color {
  val r = (this.red + diff).coerceIn(0f,1f)
  val g = (this.green + diff).coerceIn(0f,1f)
  val b = (this.blue + diff).coerceIn(0f,1f)
    return Color(r,g,b,this.alpha)
}

data class ColorDefs(val app:String = "System", val custom: Int = 0, val isCustom: Boolean = false)
fun extractColorDefs(defs: String): ColorDefs{
    val couple = defs.split("|")
    return if(couple[0].isDigitsOnly()) ColorDefs(couple[1],couple[0].toInt(),true)
    else ColorDefs(couple[0], couple[1].toInt(),false)
}
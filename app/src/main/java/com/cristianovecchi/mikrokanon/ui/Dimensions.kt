package com.cristianovecchi.mikrokanon.ui

import android.content.res.Resources
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Int.pxToSp: Int
    get() = (this / Resources.getSystem().displayMetrics.scaledDensity).toInt()

val Int.pxToDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

data class Dimensions( // 1600px width and over
    val width: Int,
    val height: Int,
    val dpDensity: Float,
    val selectorButtonSize: Dp = 70.dp, // 54
    val selectorMKbuttonFontSize: Int = 18, // sp
    val selectorFPbuttonFontSize: Int = 26, // sp
    val selectorClipFontSize: Int = (width/24).pxToSp,

    val inputNclipColumns: Int = 8, // 6
    val inputClipFontSize: TextUnit = 18.sp,
    val inputButtonSize: Dp = 80.dp,
    val inputButtonFontSize: Int = 20, // 14

    val outputNoteTableFontSize: Int = 16,
    val outputButtonSize: Dp = 64.dp, // 60
    val outputFPbuttonFontSize: Int = 28, // 22
    val outputIntervalSetFontSize: Int = 16, //10

    val sequenceDialogFontSize: TextUnit = 20.sp,

    val optionsFontSize: Int = (width/30).pxToSp,
    val dialogWidth: Dp = ((width/3*2)/dpDensity).toInt().dp,
    val dialogHeight: Dp = ((height/6*5)/dpDensity).toInt().dp,
    val dialogFontSize: Int = (width/25).pxToSp
){
    companion object {
        fun provideDimensions(width: Int, height: Int, dpDensity: Float) : Dimensions {
            println("DISPLAY [ X=$width Y=$height dpDensity=$dpDensity ]")
            return if (width == 1080 && height in 1920..2159){
                // with short height
                res1080x1920(width, height, dpDensity) // 1080x1920 xxh |
            } else {
                when(width) {
                    in Int.MIN_VALUE..1079 -> micro(width, height, dpDensity) // 720x1280 xh |
                    in 1080..1439 -> mini(width, height, dpDensity) // 1080x2160 xxh |
                    in 1440..1535 -> medium(width, height, dpDensity) // 1440x2880 | 1440x2560 |
                    in 1536..Int.MAX_VALUE -> maxi(width, height, dpDensity) // 1600x2426
                    else -> Dimensions(width, height, dpDensity)
                }
            }
        }
        fun maxi(width: Int, height: Int, dpDensity: Float) : Dimensions {
            println("Dimensions provided: MAXI")
            return Dimensions(width, height, dpDensity)
        }
        fun medium(width: Int, height: Int, dpDensity: Float) : Dimensions {
            println("Dimensions provided: MEDIUM")
            return Dimensions(
                width = width,
                height = height,
                dpDensity = dpDensity,
                selectorButtonSize = 58.dp,
                inputNclipColumns = 6,
                inputButtonSize = 56.dp,
                inputButtonFontSize = 16,
                outputButtonSize = 52.dp,
                outputFPbuttonFontSize = 20,
                outputIntervalSetFontSize = 10
            )
        }
        fun mini(width: Int, height: Int, dpDensity: Float) : Dimensions {
            println("Dimensions provided: MINI")
            return Dimensions(
                width = width,
                height = height,
                dpDensity = dpDensity,
                selectorButtonSize = (159.5 / dpDensity).toInt().dp,//54.dp,
                inputNclipColumns = 6,
                inputButtonSize = (154 / dpDensity).toInt().dp,//56.dp,
                inputButtonFontSize = 17,
                outputButtonSize =(154 / dpDensity).toInt().dp,//56.dp,
                outputFPbuttonFontSize = 22,
                outputIntervalSetFontSize = 10
            )

        }
        fun micro(width: Int, height: Int, dpDensity: Float) : Dimensions {
            println("Dimensions provided: MICRO")
            val selectorButtonSizePx = width/7
            return Dimensions(
                width = width,
                height = height,
                dpDensity = dpDensity,
                selectorButtonSize = (selectorButtonSizePx / dpDensity).toInt().dp,//50.dp,
                selectorMKbuttonFontSize =(selectorButtonSizePx /4).pxToSp,
                selectorFPbuttonFontSize= (selectorButtonSizePx /2).pxToSp,// sp20,

                inputNclipColumns = 6,
                inputButtonSize = 56.dp,
                inputButtonFontSize = 3,
                outputNoteTableFontSize = 12,
                outputButtonSize = 38.dp,
                outputFPbuttonFontSize = 16,
                outputIntervalSetFontSize = 6,

                //sequenceDialogFontSize = (width/16).pxToSp.sp
            )
        }
        fun res1080x1920(width: Int, height: Int, dpDensity: Float) : Dimensions {
            println("Dimensions provided: 1080x1920")
            return Dimensions(
                width = width,
                height = height,
                dpDensity = dpDensity,
                selectorButtonSize = 50.dp,
                selectorFPbuttonFontSize= 20,
                inputNclipColumns = 6,
                inputButtonSize = 56.dp,
                inputButtonFontSize = 3,
                outputNoteTableFontSize = 12,
                outputButtonSize = 38.dp,
                outputFPbuttonFontSize = 16,
                outputIntervalSetFontSize = 6
            )

        }
    }
}

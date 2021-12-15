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
    val titleTextSize: Pair<Int,Int> = Pair((width/30).pxToSp, (width/35).pxToSp),
    val selectorButtonSize: Dp = (width/9).pxToDp.dp, // 71
    val selectorMKbuttonFontSize: Int = 18, // sp
    val selectorFPbuttonFontSize: Int = 26, // sp
    val selectorClipFontSize: Int = (width/27).pxToSp,

    val inputNclipColumns: Int = 8, // 6
    val inputAnalyzerFontSize: Int = (width/27).pxToSp,
    val inputClipFontSize: Int = (width/27).pxToSp,
    val inputButtonSize: Dp = (width/9).pxToDp.dp,
    val inputButtonFontSize: Int = (width/27).pxToSp, // 14
    val inputWeights: Pair<Float, Float> = Pair(5f, 8f),

    val outputNoteTableFontSize: Int = 16,
    val outputButtonSize: Dp = 64.dp, // 60
    val outputFPbuttonFontSize: Int = 28, // 22
    val outputIntervalSetFontSize: Int = 16, //10

    val sequenceDialogFontSize: TextUnit = 20.sp,

    val optionsFontSize: Int = (width/25).pxToSp,
    val dialogWidth: Dp = ((width/3*2)/dpDensity).toInt().dp,
    val dialogHeight: Dp = ((height/6*5)/dpDensity).toInt().dp,
    val dialogFontSize: Int = (width/25).pxToSp,
    val dialogButtonSize: Int = (width/9).pxToDp, //60.dp in Maxi
    val dialogWeights: Triple<Float, Float, Float> = Triple(4f, 3f, 1f),
    val fullDialogWeights: Triple<Float, Float, Float> = Triple(4f, 4f, 1f)
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
            val selectorButtonSizePx = width/8
            return Dimensions(width, height, dpDensity,
                selectorButtonSize = (selectorButtonSizePx / dpDensity).toInt().dp,//50.dp,
                selectorMKbuttonFontSize =(selectorButtonSizePx /4).pxToSp,
                selectorFPbuttonFontSize= (selectorButtonSizePx /3).pxToSp,
            )
        }
        fun medium(width: Int, height: Int, dpDensity: Float) : Dimensions {
            println("Dimensions provided: MEDIUM")
            val selectorButtonSizePx = width/7
            return Dimensions(
                width = width,
                height = height,
                dpDensity = dpDensity,
                //selectorButtonSize = 58.dp,
                selectorButtonSize = (selectorButtonSizePx / dpDensity).toInt().dp,//50.dp,
                selectorMKbuttonFontSize =(selectorButtonSizePx /4).pxToSp,
                selectorFPbuttonFontSize= (selectorButtonSizePx /2).pxToSp,
                inputNclipColumns = 6,
                inputButtonSize = (width/7).pxToDp.dp,
                inputButtonFontSize = (width/24).pxToSp, // 14
                inputWeights = Pair(4f, 8f),

                outputButtonSize = 52.dp,
                outputFPbuttonFontSize = 20,
                outputIntervalSetFontSize = 10
            )
        }
        fun mini(width: Int, height: Int, dpDensity: Float) : Dimensions {
            println("Dimensions provided: MINI")
            val selectorButtonSizePx = width/7
            return Dimensions(
                width = width,
                height = height,
                dpDensity = dpDensity,
                //selectorButtonSize = (159.5 / dpDensity).toInt().dp,//54.dp,
                selectorButtonSize = (selectorButtonSizePx / dpDensity).toInt().dp,//50.dp,
                selectorMKbuttonFontSize =(selectorButtonSizePx /4).pxToSp,
                selectorFPbuttonFontSize= (selectorButtonSizePx /2).pxToSp,
                inputButtonSize = (width/7).pxToDp.dp,
                inputButtonFontSize = (width/24).pxToSp, // 14
                inputWeights = Pair(4f, 8f),
                inputNclipColumns = 6,
                outputButtonSize =(154 / dpDensity).toInt().dp,//56.dp,
                outputFPbuttonFontSize = 22,
                outputIntervalSetFontSize = 10,


            )

        }
        fun micro(width: Int, height: Int, dpDensity: Float) : Dimensions {
            println("Dimensions provided: MICRO")
            val selectorButtonSizePx = width/7
            return Dimensions(
                width = width,
                height = height,
                titleTextSize = Pair((width/40).pxToSp, (width/50).pxToSp),
                dpDensity = dpDensity,
                selectorButtonSize = (selectorButtonSizePx / dpDensity).toInt().dp,//50.dp,
                selectorMKbuttonFontSize =(selectorButtonSizePx /4).pxToSp,
                selectorFPbuttonFontSize= (selectorButtonSizePx /2).pxToSp,// sp20,
                selectorClipFontSize = (width/30).pxToSp,

                //inputAnalyzerFontSize = (width/10).pxToSp,
                inputClipFontSize = (width/24).pxToSp,
                inputButtonSize = (width/7).pxToDp.dp,
                inputButtonFontSize = (width/32).pxToSp, // 14
                inputWeights = Pair(4f, 8f),
                inputNclipColumns = 6,

                outputNoteTableFontSize = 12,
                outputButtonSize = 38.dp,
                outputFPbuttonFontSize = 16,
                outputIntervalSetFontSize = 6,

                dialogFontSize = (width/27).pxToSp,
                dialogWeights = Triple(4f, 4f, 1f),
                fullDialogWeights = Triple(3f, 4f, 1f)

                //sequenceDialogFontSize = (width/16).pxToSp.sp
            )
        }
        fun res1080x1920(width: Int, height: Int, dpDensity: Float) : Dimensions {
            println("Dimensions provided: 1080x1920")
            val selectorButtonSizePx = width/7
            return Dimensions(
                width = width,
                height = height,
                dpDensity = dpDensity,
                //selectorButtonSize = 50.dp,
               // selectorFPbuttonFontSize= 20,
                selectorButtonSize = (selectorButtonSizePx / dpDensity).toInt().dp,
                selectorMKbuttonFontSize =(selectorButtonSizePx /3).pxToSp,
                selectorFPbuttonFontSize= (selectorButtonSizePx /3).pxToSp,// sp20,
                selectorClipFontSize = (width/30).pxToSp,

                inputButtonSize = (width/8).pxToDp.dp,
                inputButtonFontSize = (width/38).pxToSp, // 14
                inputWeights = Pair(4f, 8f),
                inputNclipColumns = 6,

                outputNoteTableFontSize = 12,
                outputButtonSize = 38.dp,
                outputFPbuttonFontSize = 16,
                outputIntervalSetFontSize = 6,

                dialogFontSize = (width/27).pxToSp,
                dialogWeights = Triple(4f, 4f, 1f),
                fullDialogWeights = Triple(3f, 4f, 1f)
            )

        }
    }
}

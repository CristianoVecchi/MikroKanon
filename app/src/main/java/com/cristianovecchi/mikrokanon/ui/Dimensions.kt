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
    val selectorWeights: Pair<Float, Float> = Pair(10f, 6f),
    val sequenceDialogFontSize: TextUnit = 20.sp,

    val inputNclipColumns: Int = 8, // 6
    val inputAnalyzerFontSize: Int = (width/27).pxToSp,
    val inputClipFontSize: Int = (width/27).pxToSp,
    val inputButtonSize: Dp = (width/9).pxToDp.dp,
    val inputButtonFontSize: Int = (width/27).pxToSp, // 14
    val inputWeights: Pair<Float, Float> = Pair(4f, 9f),

    val outputNoteTableFontSize: Int = (width/36).pxToSp,//16,
    val outputNoteTableCellWidth: Int = (width/8).pxToDp,
    val outputStackIconSize: Int = (width/22).pxToDp,
    val outputPercentFontSize: Int = (width/32).pxToSp,
    val outputButtonSize: Dp = (width/9).pxToDp.dp, // 60 // 64
    val outputFPbuttonFontSize: Int = (width/8 /3).pxToSp,
    val outputIntervalSetFontSize: Int = (width/8 /5).pxToSp,
    val outputWeights: Pair<Float, Float> = Pair(16f, 7f),

    val optionsFontSize: Int = (width/25).pxToSp,
    val dialogWidth: Dp = ((width/4*3)/dpDensity).toInt().dp,
    val dialogHeight: Dp = ((height/6*5)/dpDensity).toInt().dp,
    val dialogFontSize: Int = (width/25).pxToSp,
    val dialogButtonSize: Int = (width/9).pxToDp, //60.dp in Maxi
    val dialogWeights: Triple<Float, Float, Float> = Triple(3f, 3f, 2f),
    val fullDialogWeights: Triple<Float, Float, Float> = Triple(4f, 4f, 1f),
    val listDialogWeights: Pair<Float, Float> = Pair(6f, 1f)
){


    companion object {
        val default = provideDimensions(720, 1280, 2.0f)
        fun provideDimensions(width: Int, height: Int, dpDensity: Float) : Dimensions {
            println("DISPLAY [ X=$width Y=$height dpDensity=$dpDensity ]")
            return if (width == 1080 && height in 1920..2159){
                // with short height
                res1080x1920(width, height, dpDensity) // 1080x1920 xxh |
            } else {
                when(width) {
                    in Int.MIN_VALUE..719 -> atomic(width, height, dpDensity)
                    in 720..1079 -> micro(width, height, dpDensity) // 720x1280 xh |
                    in 1080..1439 -> mini(width, height, dpDensity) // 1080x2160 xxh |
                    in 1440..1535 -> medium(width, height, dpDensity) // 1440x2880 | 1440x2560 |
                    in 1536..Int.MAX_VALUE -> maxi(width, height, dpDensity) // 1600x2426
                    else -> Dimensions(width, height, dpDensity)
                }
            }
        }
        fun maxi(width: Int, height: Int, dpDensity: Float) : Dimensions {
            //println("Dimensions provided: MAXI")
            val selectorButtonSizePx = width/8
            return Dimensions(width, height, dpDensity,
                dialogFontSize = (width/23).pxToSp,
                dialogButtonSize = (width/8).pxToDp,
                selectorButtonSize = (selectorButtonSizePx / dpDensity).toInt().dp,//50.dp,
                selectorMKbuttonFontSize =(selectorButtonSizePx /4).pxToSp,
                selectorFPbuttonFontSize= (selectorButtonSizePx /3).pxToSp,

            )
        }
        fun medium(width: Int, height: Int, dpDensity: Float) : Dimensions {
           // println("Dimensions provided: MEDIUM")
            val selectorButtonSizePx = width / 7
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

                outputButtonSize = (selectorButtonSizePx).pxToDp.dp,
                outputFPbuttonFontSize = (selectorButtonSizePx /3).pxToSp,
                outputIntervalSetFontSize= (selectorButtonSizePx /6).pxToSp,
                outputWeights= if(height >= 2880) Pair(16f,6f) else Pair(16f, 7f),

            )
        }
        fun mini(width: Int, height: Int, dpDensity: Float) : Dimensions {
            //println("Dimensions provided: MINI")
            val selectorButtonSizePx = width/7
            val outputButtonSizePx = width / 8
            return Dimensions(
                width = width,
                height = height,
                dpDensity = dpDensity,
                //selectorButtonSize = (159.5 / dpDensity).toInt().dp,//54.dp,
                selectorClipFontSize = (width/22).pxToSp,
                selectorButtonSize = (selectorButtonSizePx / dpDensity).toInt().dp,//50.dp,
                selectorMKbuttonFontSize = (selectorButtonSizePx /4).pxToSp,
                selectorFPbuttonFontSize = (selectorButtonSizePx /2).pxToSp,
                selectorWeights = if(height >= 2400) Pair(10f,5f) else Pair(10f, 6f),
                optionsFontSize = (width/22).pxToSp,

                inputButtonSize = (width/7).pxToDp.dp,
                inputButtonFontSize = (width/24).pxToSp, // 14
                inputWeights = Pair(4f, 8f),
                inputNclipColumns = 6,

                outputNoteTableFontSize= (width/28).pxToSp,//16,
                outputNoteTableCellWidth = (width/7).pxToDp,
                outputButtonSize = (outputButtonSizePx/ dpDensity).toInt().dp,
                outputFPbuttonFontSize = (outputButtonSizePx /3).pxToSp,
                outputIntervalSetFontSize= (outputButtonSizePx /5).pxToSp,
                outputWeights= if(height >= 2400) Pair(16f,5f) else Pair(15f, 7f),


            )

        }
        fun micro(width: Int, height: Int, dpDensity: Float) : Dimensions {
            //println("Dimensions provided: MICRO")
            val selectorButtonSizePx = width/7
            val outputButtonSizePx = width / 9
            return Dimensions(
                width = width,
                height = height,
                titleTextSize = Pair((width/40).pxToSp, (width/50).pxToSp),
                dpDensity = dpDensity,
                selectorButtonSize = (selectorButtonSizePx / dpDensity).toInt().dp,//50.dp,
                selectorMKbuttonFontSize =(selectorButtonSizePx /4).pxToSp,
                selectorFPbuttonFontSize= (selectorButtonSizePx /2).pxToSp,// sp20,
                selectorClipFontSize = (width/30).pxToSp,
                dialogFontSize = (width/27).pxToSp,
                dialogWeights = Triple(4f, 4f, 1f),
                fullDialogWeights = Triple(3f, 4f, 1f),

                //inputAnalyzerFontSize = (width/10).pxToSp,
                inputClipFontSize = (width/24).pxToSp,
                inputButtonSize = (width/8).pxToDp.dp,
                inputButtonFontSize = (width/38).pxToSp, // 14
                inputWeights = Pair(4f, 8f),
                inputNclipColumns = 6,

                outputNoteTableFontSize= (width/28).pxToSp,//16,
                outputNoteTableCellWidth = (width/6).pxToDp,
                outputButtonSize = (outputButtonSizePx/ dpDensity).toInt().dp,
                outputFPbuttonFontSize = (outputButtonSizePx /2).pxToSp,
                outputIntervalSetFontSize= if(dpDensity==1.0f) (outputButtonSizePx /3).pxToSp else (outputButtonSizePx /7).pxToSp,
                outputWeights= if(height >= 1280) Pair(16f,7f) else Pair(16f, 9f),

                //sequenceDialogFontSize = (width/16).pxToSp.sp
            )
        }
        fun atomic(width: Int, height: Int, dpDensity: Float) : Dimensions {
            //println("Dimensions provided: ATOMIC")
            val selectorButtonSizePx = width/7
            val outputButtonSizePx = width / 9
            return Dimensions(
                width = width,
                height = height,
                titleTextSize = Pair((width/40).pxToSp, (width/50).pxToSp),
                dpDensity = dpDensity,
                selectorButtonSize = (selectorButtonSizePx / dpDensity).toInt().dp,//50.dp,
                selectorMKbuttonFontSize =(selectorButtonSizePx /4).pxToSp,
                selectorFPbuttonFontSize= (selectorButtonSizePx /3).pxToSp,// sp20,
                selectorClipFontSize = (width/30).pxToSp,

                dialogFontSize = (width/34).pxToSp,
                dialogWeights = Triple(3f, 4f, 2f),
                fullDialogWeights = Triple(2f, 5f, 1f),
                listDialogWeights = Pair(9f, 2f),
                //inputAnalyzerFontSize = (width/10).pxToSp,
                inputClipFontSize = (width/24).pxToSp,
                inputButtonSize = (width/8).pxToDp.dp,
                inputButtonFontSize = (width/36).pxToSp, // 14
                inputWeights = Pair(3f, 9f),
                inputNclipColumns = 6,

                outputNoteTableFontSize= (width/30).pxToSp,//16,
                outputNoteTableCellWidth = (width/6).pxToDp,
                outputButtonSize = (outputButtonSizePx/ dpDensity).toInt().dp,
                outputFPbuttonFontSize = (outputButtonSizePx /3).pxToSp,
                outputIntervalSetFontSize= (outputButtonSizePx /7).pxToSp,
                outputWeights= if(height >= 1280) Pair(16f,7f) else Pair(16f, 9f),

                //sequenceDialogFontSize = (width/16).pxToSp.sp
            )
        }
        fun res1080x1920(width: Int, height: Int, dpDensity: Float) : Dimensions {
            //println("Dimensions provided: 1080x1920")
            val selectorButtonSizePx = width/7
            val outputButtonSizePx = width / 9
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
                dialogFontSize = (width/27).pxToSp,
                dialogWeights = Triple(4f, 4f, 1f),
                fullDialogWeights = Triple(3f, 4f, 1f),

                inputButtonSize = (width/8).pxToDp.dp,
                inputButtonFontSize = (width/38).pxToSp, // 14
                inputWeights = Pair(4f, 8f),
                inputNclipColumns = 6,

                outputNoteTableFontSize= (width/44).pxToSp,//16,
                outputNoteTableCellWidth = (width/8).pxToDp,
                outputButtonSize = (outputButtonSizePx/ dpDensity).toInt().dp,
                outputFPbuttonFontSize = (outputButtonSizePx /3).pxToSp,
                outputIntervalSetFontSize= (outputButtonSizePx /5).pxToSp,
                outputWeights= Pair(16f,6f)
            )//.also{ println("Dialog width: ${it.dialogWidth}")}
        }
        fun default(): Dimensions {
            return Dimensions.default
        }
    }
}

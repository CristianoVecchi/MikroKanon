package com.cristianovecchi.mikrokanon.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Dimensions( // 1600px width and over
    val selectorButtonSize: Dp = 70.dp, // 54
    val selectorMKbuttonFontSize: Int = 18,
    val selectorFPbuttonFontSize: Int = 26,

    val inputNclipColumns: Int = 8, // 6
    val inputClipFontSize: TextUnit = 18.sp,
    val inputButtonSize: Dp = 80.dp,
    val inputButtonFontSize: TextUnit = 20.sp, // 14

    val outputNoteTableFontSize: Int = 16,
    val outputButtonSize: Dp = 70.dp, // 60
    val outputFPbuttonFontSize: Int = 28, // 22
    val outputIntervalSetFontSize: Int = 16, //10

    val sequenceDialogFontSize: TextUnit = 20.sp,

    val optionsFontSize: Int = 20
){
    companion object {
        fun provideDimensions(width: Int, height: Int) : Dimensions {
            return if (width == 1080 && height in 1920..2159){
                // with short height
                Dimensions.res1080x1920() // 1080x1920 xxh |
            } else {
                when(width) {
                    in Int.MIN_VALUE..1079 -> Dimensions.micro() // 720x1280 xh |
                    in 1080..1439 -> Dimensions.mini() // 1080x2160 xxh |
                    in 1440..1535 -> Dimensions.medium() // 1440x2880 | 1440x2560 |
                    in 1536..Int.MAX_VALUE -> Dimensions.maxi() // 1600x2426
                    else -> Dimensions()
                }
            }
        }
        fun maxi() : Dimensions {
            return Dimensions()
        }
        fun medium() : Dimensions {
            return Dimensions().copy(
                selectorButtonSize = 58.dp,
                inputNclipColumns = 6,
                inputButtonSize = 56.dp,
                inputButtonFontSize = 16.sp,
                outputButtonSize = 52.dp,
                outputFPbuttonFontSize = 20,
                outputIntervalSetFontSize = 10
            )
        }
        fun mini() : Dimensions {
            return Dimensions().copy(
                selectorButtonSize = 54.dp,
                inputNclipColumns = 6,
                inputButtonSize = 56.dp,
                inputButtonFontSize = 17.sp,
                outputButtonSize =56.dp,
                outputFPbuttonFontSize = 22,
                outputIntervalSetFontSize = 10
            )

        }
        fun micro() : Dimensions {
            return Dimensions().copy(
                selectorButtonSize = 50.dp,
                selectorFPbuttonFontSize= 20,
                inputNclipColumns = 6,
                inputButtonSize = 56.dp,
                inputButtonFontSize = 3.sp,
                outputNoteTableFontSize = 12,
                outputButtonSize = 38.dp,
                outputFPbuttonFontSize = 16,
                outputIntervalSetFontSize = 6
            )
        }
        fun res1080x1920() : Dimensions {
            return Dimensions().copy(
                selectorButtonSize = 50.dp,
                selectorFPbuttonFontSize= 20,
                inputNclipColumns = 6,
                inputButtonSize = 56.dp,
                inputButtonFontSize = 3.sp,
                outputNoteTableFontSize = 12,
                outputButtonSize = 38.dp,
                outputFPbuttonFontSize = 16,
                outputIntervalSetFontSize = 6
            )

        }
    }
}

package com.cristianovecchi.mikrokanon.composables.dialogs

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cristianovecchi.mikrokanon.AIMUSIC.ARPEGGIO
import com.cristianovecchi.mikrokanon.AppViewModel

data class GroupingDialogData (val dialogState: Boolean = false,
                           val itemGroups: List<List<String>> = listOf(),
                           val groupNames: List<String> = listOf(),
                           val selectedListDialogItem: Int = -1,
                           val dialogTitle: String = "",
                           val onSubmitButtonClick: (Int) -> Unit = {} )
data class ListDialogData (val dialogState: Boolean = false,
                           val itemList: List<String> = listOf(),
                          val selectedListDialogItem: Int = -1,
                          val dialogTitle: String = "",
                          val onSubmitButtonClick: (Int) -> Unit = {} )

data class MultiListDialogData(val dialogState: Boolean = false, val itemList: List<String> = listOf(),
                               val selectedListDialogItems: Set<Int> = setOf(),
                               val dialogTitle: String = "",
                               val optionText: String = "", val optionChecked: Boolean = false,
                               val onSubmitButtonClick: (List<Int>, Boolean) -> Unit = { list, option ->  })


data class NumberDialogData(val dialogState: Boolean = false, val title:String = "", val value:Int = 0,
                            val min: Int = 0, val max: Int = 360, val onSubmitButtonClick: (Int) -> Unit = {})

data class MultiNumberDialogData(val dialogState: Boolean = false, val title:String = "", val value:String="0",
                                 val min: Int = 0, val max: Int = 360, val model: AppViewModel,
                                 val names: List<String> = listOf(),
                                 val intSequences: List<List<Int>> = listOf(),
                                 val anySequence: List<Any> = listOf(),
                                 val dispatchIntLists: (List<List<Int>>) -> Unit = {},
                                 val dispatchCsv: (String) -> Unit = {},
                                 val dispatchResolutio: (Triple<Set<Int>,String,Int>) -> Unit = {},
                                 val onSubmitButtonClick: (String) -> Unit = {})

data class MultiFloatDialogData(val dialogState: Boolean = false, val title:String = "", val value:String="1.0",
                                val min: Float = 0f, val max: Float = 1f, val model: AppViewModel,
                                val onSubmitButtonClick: (String) -> Unit = {})

data class CustomColorsDialogData(val dialogState: Boolean = false, val title:String = "", val arrayColorIndex: Int = 0,
                                  val model: AppViewModel, val firstRendering: Boolean = true, val isRefreshing: Boolean = false,
                                  val onSubmitButtonClick: (Int) -> Unit = {})
data class PercentageDialogData(val dialogState: Boolean = false, val title:String = "", val percentageValue: Float = 0f,
                                val firstRendering: Boolean = true, val isRefreshing: Boolean = false, val model: AppViewModel,
                                  val onSubmitButtonClick: (Float) -> Unit = {})
data class ButtonsDialogData(
    val dialogState: Boolean = false, val title:String = "",
    val model: AppViewModel, val buttonSize: Dp = 60.dp, val fontSize: Int = 18,
    val isActiveWaves: Boolean = false, val isActivePedals: Boolean = false, val isActiveSpecialFunctions1: Boolean = true,
    val onWave3: () -> Unit = {}, val onWave4: () -> Unit = {}, val onWave6: () -> Unit = {}, val onQuote: () -> Unit = {},
    val onPedal1: () -> Unit = {}, val onPedal3: () -> Unit = {}, val onPedal5: () -> Unit = {},
    val onTritoneSubstitution: () -> Unit = {}, val onRound: () -> Unit = {},
    val onCadenza: () -> Unit = {}, val onScarlatti: () -> Unit = {},
    val onOverlap: () -> Unit = {}, val onCrossover: () -> Unit = {},
    val onGlue: () -> Unit = {}, val onFlourish: () -> Unit = {},
    val onEraseIntervals: () -> Unit = {}, val onSingle: () -> Unit = {},
    val onMK5reducted: () -> Unit = {}, val onMK6reducted: () -> Unit = {},
    val onMaze: () -> Unit = {}, val onDoppelgänger: () -> Unit = {},
    val onSort: (Int) -> Unit = {}, val onUpsideDown: () -> Unit = {},
    val onArpeggio: (ARPEGGIO) -> Unit = {},
    val onCounterpointSelected: (Int) -> Unit = {}, val onEWH: (Int) -> Unit = {},
    val onResolutio: () -> Unit = {}, val onDoubling: () -> Unit = {}, val onParade: () -> Unit = {},
    val progressiveEWH: () -> Unit = {}, val onChess: () -> Unit = {},
    val onSubmitButtonClick: (Any?) -> Unit = {}, val onFormat: () -> Unit = {}, )


data class ExportDialogData(val dialogState: Boolean = false, val title:String = "", val path:String = "",
                            val error:String = "",
                            val onSubmitButtonClick: () -> Unit = {})
data class TextDialogData(val dialogState: Boolean = false, val title:String = "",
                             val onSubmitButtonClick: () -> Unit = {})
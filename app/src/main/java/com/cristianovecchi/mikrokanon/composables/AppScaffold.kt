package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristianovecchi.mikrokanon.*
import com.cristianovecchi.mikrokanon.AIMUSIC.RhythmPatterns
import com.cristianovecchi.mikrokanon.composables.dialogs.*
import com.cristianovecchi.mikrokanon.db.UserOptionsData
import com.cristianovecchi.mikrokanon.locale.*
import com.cristianovecchi.mikrokanon.ui.AppColorThemes
import com.cristianovecchi.mikrokanon.ui.extractColorDefs
import com.cristianovecchi.mikrokanon.ui.shift
import kotlinx.coroutines.flow.Flow
import kotlin.math.absoluteValue

@Composable
fun AppScaffold(model: AppViewModel, userOptionsDataFlow: Flow<List<UserOptionsData>>, content: @Composable () -> Unit) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val userOptionsData = model.userOptionsData.observeAsState(initial = listOf()).value // to force recomposing when options change
    val colors = model.appColors
    val titleStyle = SpanStyle(
        fontSize = 18.sp,
        color = colors.cellTextColorSelected)
    val creditStyle = SpanStyle(
        fontSize = 14.sp,
        color = colors.cellTextColorUnselected)

    Scaffold(
        //modifier = Modifier
            //.background(colors.selCardBorderColorSelected),
            //.border(1.dp, Color.Transparent),
        scaffoldState = scaffoldState,
        drawerContent = { SettingsDrawer(model, userOptionsDataFlow)},
        topBar = {
            val creditsDialogData by lazy { mutableStateOf(CreditsDialogData())}
            CreditsDialog(creditsDialogData)
            TopAppBar(Modifier.border(1.dp,colors.selCardBorderColorSelected)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(colors.selCardBorderColorSelected), //color of the top app bar
                        //.border(1.dp, Color.Transparent),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                )
                {
                    IconButton(
                        onClick = {scope.launch { scaffoldState.drawerState.open() }  }
                    ) {
                        Icon(Icons.Filled.Menu,"", tint = colors.cellTextColorSelected)
                    }
                    ClickableText(text = buildAnnotatedString {
                        withStyle(titleStyle){
                            append("MikroKanon\n")
                        }
                        withStyle(creditStyle) {
                            append("by Cristiano Vecchi")
                        }
                    },onClick = {
                        creditsDialogData.value = CreditsDialogData(true,"Credits:",
                        ) {
                            creditsDialogData.value = CreditsDialogData()
                        }
                    })
                }
            }
        },
        content = {
            content()
        }
    )
}


data class ListDialogData(val dialogState: Boolean = false, val itemList: List<String> = listOf(),
                          val selectedListDialogItem: Int = -1,
                          val dialogTitle: String = "", val onSubmitButtonClick: (Int) -> Unit = {} )
data class MultiListDialogData(val dialogState: Boolean = false, val itemList: List<String> = listOf(),
                               val selectedListDialogItems: Set<Int> = setOf(),
                          val dialogTitle: String = "", val onSubmitButtonClick: (List<Int>) -> Unit = {} )
data class NumberDialogData(val dialogState: Boolean = false, val title:String = "", val value:Int = 0,
                            val min: Int = 0, val max: Int = 360, val onSubmitButtonClick: (Int) -> Unit = {})
data class MultiNumberDialogData(val dialogState: Boolean = false, val title:String = "", val value:String="0",
                                 val min: Int = 0, val max: Int = 360, val model: AppViewModel, val onSubmitButtonClick: (String) -> Unit = {})
data class MultiFloatDialogData(val dialogState: Boolean = false, val title:String = "", val value:String="1.0",
                                 val min: Float = 0f, val max: Float = 1f, val model: AppViewModel, val onSubmitButtonClick: (String) -> Unit = {})
data class CustomColorsDialogData(val dialogState: Boolean = false, val title:String = "", val arrayColorIndex: Int = 0,
                                  val model: AppViewModel, val firstRendering: Boolean = true, val isRefreshing: Boolean = false,
                                  val onSubmitButtonClick: (Int) -> Unit = {})
data class ButtonsDialogData(
    val dialogState: Boolean = false, val title:String = "",
    val model: AppViewModel, val buttonSize: Dp = 60.dp, val fontSize: Int = 18,
    val isActiveWaves: Boolean = false, val isActivePedals: Boolean = false,
    val onWave3: () -> Unit = {}, val onWave4: () -> Unit = {}, val onWave6: () -> Unit = {},
    val onPedal1: () -> Unit = {}, val onPedal3: () -> Unit = {}, val onPedal5: () -> Unit = {},
    val onTritoneSubstitution: () -> Unit = {}, val onRound: () -> Unit = {},
    val onCadenza: () -> Unit = {}, val onSingle: () -> Unit = {},
    val onMK5reducted: () -> Unit = {},
    val onSubmitButtonClick: () -> Unit = {})

data class ExportDialogData(val dialogState: Boolean = false, val title:String = "", val path:String = "",
                            val error:String = "", val onSubmitButtonClick: () -> Unit = {})
data class CreditsDialogData(val dialogState: Boolean = false, val title:String = "",  val onSubmitButtonClick: () -> Unit = {})



@Composable
fun SettingsDrawer(model: AppViewModel, userOptionsDataFlow: Flow<List<UserOptionsData>>){



    val listDialogData by lazy { mutableStateOf(ListDialogData())}
    //val nuancesDialogData by lazy { mutableStateOf(ListDialogData())}
    //val bpmDialogData by lazy { mutableStateOf(NumberDialogData())}
    val multiBpmDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val multiFloatDialogData by lazy { mutableStateOf(MultiFloatDialogData(model = model))}
    val transposeDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val rowFormsDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val doublingDialogData by lazy { mutableStateOf(MultiListDialogData())}
    val glissandoDialogData by lazy { mutableStateOf(MultiListDialogData())}
    val exportDialogData by lazy { mutableStateOf(ExportDialogData())}
    val creditsDialogData by lazy { mutableStateOf(CreditsDialogData())}
    //val intervalSetDialogData by lazy { mutableStateOf(MultiListDialogData())}
    val detectorDialogData by lazy { mutableStateOf(MultiListDialogData())}
    val detExtensionDialogData by lazy { mutableStateOf(ListDialogData())}
    val colorsDialogData by lazy { mutableStateOf(ListDialogData())}
    val customColorsDialogData by lazy { mutableStateOf(CustomColorsDialogData(model = model))}
    val ritornelloDialogData by lazy {mutableStateOf(ListDialogData())}
    val zodiacDialogData by lazy{ mutableStateOf(MultiListDialogData())}

    val dimensions = model.dimensions
    val colors = model.appColors
    val optionNames= listOf("Ensemble", "Range","Melody", "Glissando","Nuances", "Dynamic", "BPM", "Rhythm",  "Rhythm Shuffle", "Parts Shuffle",
        "Row Forms","Ritornello","Transpose","Doubling",
        "Spread where possible", "Deep Search in 4 part MK", "Detector","Detector Extension",
        "Export MIDI", "Colors", "Custom Colors","Language","Zodiac","Credits")
    //val userOptionsData by model.userOptionsData.asFlow().collectAsState(initial = listOf())
    val userOptionsData by userOptionsDataFlow.collectAsState(initial = listOf())
    val lang = Lang.provideLanguage(model.getUserLangDef())
    val userOptions = if(userOptionsData.isEmpty()) UserOptionsData.getDefaultUserOptionsData()
                        else userOptionsData[0]
    val listState = rememberLazyListState()
    var isFirstTab by remember{ mutableStateOf(model.isFirstTab)}
    ListDialog(listDialogData, lang.OKbutton,dimensions.sequenceDialogFontSize)
    MultiListDialog(doublingDialogData, dimensions.sequenceDialogFontSize, lang.OKbutton)
    //BpmDialog(bpmDialogData, lang.OKbutton)
    val intervalsForGlissando = createGlissandoIntervals(lang.doublingNames)
    MultiBpmDialog(multiBpmDialogData, lang.OKbutton)
    MultiDynamicDialog(multiFloatDialogData, lang.OKbutton)
    val intervalsForTranspose = listOf("U","2m","2M","3m","3M","4","4A","5","6m","6M","7m","7M")
    TransposeDialog(transposeDialogData,
        intervalsForTranspose, lang.OKbutton)
    val formsNames = listOf("unrelated", lang.original, lang.inverse, lang.retrograde, lang.invRetrograde)
    RowFormsDialog(rowFormsDialogData,
        formsNames, lang.OKbutton)
    ExportDialog(exportDialogData, lang.OKbutton)
    CreditsDialog(creditsDialogData, lang.OKbutton)
    //MultiListDialog(intervalSetDialogData, dimensions.sequenceDialogFontSize, lang.OKbutton)
    MultiListDialog(detectorDialogData, dimensions.sequenceDialogFontSize, lang.OKbutton)
    ListDialog(detExtensionDialogData, lang.OKbutton,dimensions.sequenceDialogFontSize, fillPrevious = true)
    ListDialog(colorsDialogData, lang.OKbutton,dimensions.sequenceDialogFontSize)
    MultiListDialog(zodiacDialogData, dimensions.sequenceDialogFontSize, lang.OKbutton)
    CustomColorsDialog(customColorsDialogData, lang.OKbutton)
    ListDialog(ritornelloDialogData, lang.OKbutton, dimensions.sequenceDialogFontSize, fillPrevious = true)

//    userOptionsData.forEach{
//        Text("#${it.id} = ens_type: ${it.ensembleType} - bpm: ${it.bpm} ")
//    }
Row(Modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
    Column(Modifier
        .weight(1f)
        .background(
            if (isFirstTab) colors.drawerBackgroundColor
            else colors.drawerBackgroundColor.shift(-0.2f)
        )
        .clickable(onClick = { isFirstTab = true; model.isFirstTab = true }), horizontalAlignment = Alignment.CenterHorizontally){
        IconButton(modifier = Modifier
            .background(
                if (isFirstTab) colors.drawerBackgroundColor else colors.drawerBackgroundColor.shift(
                    -0.2f
                ), RoundedCornerShape(4.dp)
            )
            .then(Modifier.size(50.dp)), onClick = { isFirstTab = true; model.isFirstTab = true }
        )
        {
            Icon(
                painter = painterResource(id = model.iconMap["music"]!!),
                contentDescription = null, // decorative element
                tint = if(isFirstTab) colors.selCardTextColorSelected else colors.selCardTextColorUnselected.shift(-0.2f)
            )
        }
    }
    Column(
        Modifier
            .weight(1f)
            .background(
                if (!isFirstTab) colors.drawerBackgroundColor
                else colors.drawerBackgroundColor.shift(-0.2f)
            )
            .clickable(onClick = { isFirstTab = false; model.isFirstTab = false }), horizontalAlignment = Alignment.CenterHorizontally,
        )
           {
        IconButton(modifier = Modifier
            .background(
                if (!isFirstTab) colors.drawerBackgroundColor else colors.drawerBackgroundColor.shift(
                    -0.2f
                ), RoundedCornerShape(4.dp)
            )
            .then(Modifier.size(50.dp)), onClick = { isFirstTab = false; model.isFirstTab = false }
        )
        {
            Icon(
                painter = painterResource(id = model.iconMap["settings"]!!),
                contentDescription = null, // decorative element
                tint = if(!isFirstTab) colors.selCardTextColorSelected else colors.selCardTextColorUnselected.shift(-0.2f)
            )
        }
    }

}

    if(isFirstTab){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.drawerBackgroundColor),
            state = listState,
        ) { items(optionNames) { optionName ->
            val fontSize = dimensions.optionsFontSize
            when (optionName) {
                "Ensemble" -> {
                    val ensNames: List<String> = lang.ensembleNames
                    val ensIndex = userOptions.ensembleType
                    SelectableCard(
                        text = "${lang.ensemble}: ${ensNames[ensIndex]}",
                        fontSize = fontSize,
                        colors = colors,
                        isSelected = true,
                        onClick = {
                            listDialogData.value = ListDialogData(
                                true, ensNames, ensIndex, lang.selectEnsemble
                            ) { index ->
                                model.updateUserOptions(
                                    "ensembleType",
                                    index
                                )
                                listDialogData.value =
                                    ListDialogData(itemList = listDialogData.value.itemList)
                            }
                        })
                }
                "Range" -> {
                    val rangeOptions: List<String> = lang.rangeOptions
                    val rangeIndex = userOptions.rangeType
                    SelectableCard(
                        text = "${lang.range}: ${rangeOptions[rangeIndex]}",
                        fontSize = fontSize,
                        colors = colors,
                        isSelected = true,
                        onClick = {
                            listDialogData.value = ListDialogData(
                                true, rangeOptions, rangeIndex, lang.selectRange
                            ) { index ->
                                model.updateUserOptions(
                                    "rangeType",
                                    index
                                )
                                listDialogData.value =
                                    ListDialogData(itemList = listDialogData.value.itemList)
                            }
                        })
                }
                "Melody" -> {
                    val melodyOptions: List<String> = lang.melodyOptions
                    val melodyIndex = userOptions.melodyType
                    SelectableCard(
                        text = "${lang.melody}: ${melodyOptions[melodyIndex]}",
                        fontSize = fontSize,
                        colors = colors,
                        isSelected = true,
                        onClick = {
                            listDialogData.value = ListDialogData(
                                true, melodyOptions, melodyIndex, lang.selectMelody
                            ) { index ->
                                model.updateUserOptions(
                                    "melodyType",
                                    index
                                )
                                listDialogData.value =
                                    ListDialogData(itemList = listDialogData.value.itemList)
                            }
                        })
                }
                "Glissando" -> {
                    val flags = userOptions.glissandoFlags
                    val intsFromFlags = convertFlagsToInts(flags).map { it - 1 }
                    val isOn = flags > 0
                    val text = if (!isOn) lang.glissando else "${lang.glissando}: ${
                        intsFromFlags.joinToString(separator = ", ") { intervalsForGlissando[it] }
                    }"
                    SelectableCard(
                        text = text,
                        fontSize = fontSize,
                        colors = colors,
                        isSelected = isOn,
                        onClick = { _ ->
                            doublingDialogData.value = MultiListDialogData(
                                true, intervalsForGlissando, intsFromFlags.toSet(), lang.selectGlissando
                            ) { indexes ->
                                model.updateUserOptions(
                                    "glissandoFlags",
                                    convertIntsToFlags(indexes.map { it + 1 }.toSortedSet())
                                )
                                doublingDialogData.value =
                                    MultiListDialogData(itemList = doublingDialogData.value.itemList)
                            }
                        })
                }
                "Nuances" -> {
                    val nuancesOptions: List<String> = lang.nuancesOptions
                    val nuancesIndex = userOptions.nuances
                    val isOn = nuancesIndex != 0
                    SelectableCard(
                        text = if(isOn) "${lang.nuances}: ${nuancesOptions[nuancesIndex]}" else lang.nuances,
                        fontSize = fontSize,
                        colors = colors,
                        isSelected = isOn,
                        onClick = {
                            listDialogData.value = ListDialogData(
                                true, nuancesOptions, nuancesIndex, lang.selectNuances
                            ) { index ->
                                model.updateUserOptions(
                                    "nuances",
                                    index
                                )
                                listDialogData.value =
                                    ListDialogData(itemList = listDialogData.value.itemList)
                            }
                        })
                }
                "Dynamic" -> {
                    val dynamics = userOptions.dynamics
                    val symbols = getDynamicSymbols()
                    SelectableCard(
                        text = "${lang.dynamic}: ${dynamics.describeForDynamic(model.dynamicMap, symbols[12], symbols[13])}",
                        fontSize = fontSize,
                        colors = colors,
                        isSelected = true,
                        onClick = {
                            multiFloatDialogData.value = MultiFloatDialogData(
                                true, "${lang.selectDynamicAlterations}", dynamics,
                                model = model) { dynamics ->
                                model.updateUserOptions(
                                    "dynamics",
                                    dynamics
                                )
                                listDialogData.value =
                                    ListDialogData(itemList = listDialogData.value.itemList)
                            }
                        })
                }
                "BPM" -> {
                    val bpms = userOptions.bpms
                    SelectableCard(
                        text = "${lang.bpm}: ${bpms.describe()}",
                        fontSize = fontSize,
                        colors = colors,
                        isSelected = true,
                        onClick = {
                            multiBpmDialogData.value = MultiNumberDialogData(
                                true, "${lang.beatsPerMinute}:", bpms, 18, 600,
                            model = model) { bpms ->
                                model.updateUserOptions(
                                    "bpms",
                                    bpms
                                )
                                listDialogData.value =
                                    ListDialogData(itemList = listDialogData.value.itemList)
                            }
                        })
                }
                "Rhythm" -> {
                    val rhythmNames = RhythmPatterns.getTitles()
                    val rhythmIndex = userOptions.rhythm
                    SelectableCard(
                        text = "${lang.rhythm}: ${rhythmNames[rhythmIndex]}",
                        fontSize = fontSize,
                        colors = colors,
                        isSelected = true,
                        onClick = {
                            listDialogData.value = ListDialogData(
                                true, rhythmNames, rhythmIndex, lang.selectRhythm
                            ) { index ->
                                model.updateUserOptions(
                                    "rhythm",
                                    index
                                )
                                listDialogData.value =
                                    ListDialogData(itemList = listDialogData.value.itemList)
                            }
                        })
                }
                "Rhythm Shuffle" -> {
                    var isOn = userOptions.rhythmShuffle != 0
                    SelectableCard(
                        text = lang.rhythmShuffle,
                        fontSize = fontSize,
                        colors = colors,
                        isSelected = isOn,
                        onClick = {
                            isOn = !isOn
                            model.updateUserOptions(
                                "rhythmShuffle",
                                if (isOn) 1 else 0
                            )
                        })
                }
                "Parts Shuffle" -> {
                    var isOn = userOptions.partsShuffle != 0
                    SelectableCard(
                        text = lang.partsShuffle,
                        fontSize = fontSize,
                        colors = colors,
                        isSelected = isOn,
                        onClick = {
                            isOn = !isOn
                            model.updateUserOptions(
                                "partsShuffle",
                                if (isOn) 1 else 0
                            )
                        })
                }
                "Row Forms" -> {
                    val formsCsv = userOptions.rowForms
                    val isOn = formsCsv != "1"
                    val formsNumbers = formsCsv.extractIntsFromCsv()
                    SelectableCard(
                        text = if(isOn)
                            "${lang.rowForms}: ${formsNumbers.map{ 
                                if(it<0) "${rowFormsMap[it.absoluteValue]} |" 
                                else "${rowFormsMap[it]}"}.joinToString(" ")}"
                                else lang.rowForms,
                                fontSize = fontSize,
                                colors = colors,
                                isSelected = isOn,
                                onClick = {
                                    rowFormsDialogData.value = MultiNumberDialogData(
                                        true, lang.selectRowForms, formsCsv,
                                        model = model) { rowForms ->
                                        model.updateUserOptions(
                                            "rowForms",
                                            rowForms
                                        )
                                        listDialogData.value =
                                            ListDialogData(itemList = listDialogData.value.itemList)
                                    }
                                })
                            }

                "Ritornello" -> {
                    val timeIndices: List<String> = (1..128).map { it.toString() }
                    val nTimes = userOptions.ritornello
                    val isOn = nTimes != 0
                    val text = if (nTimes == 0) lang.ritornello else "${lang.ritornello} x ${nTimes+1}"
                    SelectableCard(
                        text = text,
                        fontSize = fontSize,
                        colors = colors,
                        isSelected = isOn,
                        onClick = {
                            ritornelloDialogData.value = ListDialogData(
                                true, timeIndices, nTimes, lang.selectRitornello
                            ) { index ->
                                model.updateUserOptions(
                                    "ritornello",
                                    index
                                )
                                ritornelloDialogData.value =
                                    ListDialogData(itemList = ritornelloDialogData.value.itemList)
                            }
                        })
                }
                "Transpose" -> {
                    val transposeCsv = userOptions.transpose
                    val isOn = transposeCsv != "0"
                    SelectableCard(
                        text = if(isOn) "${lang.transpose}: ${transposeCsv.describeForTranspose(intervalsForTranspose)}" else lang.transpose,
                        fontSize = fontSize,
                        colors = colors,
                        isSelected = isOn,
                        onClick = {
                            transposeDialogData.value = MultiNumberDialogData(
                                true, "${lang.selectTranspositions}", transposeCsv,
                                model = model) { transpositions ->
                                model.updateUserOptions(
                                    "transpose",
                                    transpositions
                                )
                                listDialogData.value =
                                    ListDialogData(itemList = listDialogData.value.itemList)
                            }
                        })
                }
                "Doubling" -> {
                    val flags = userOptions.doublingFlags
                    val intsFromFlags = convertFlagsToInts(flags).map { it - 1 }
                    val isOn = flags > 0
                    val text = if (!isOn) lang.doubling else "${lang.doubling}: ${
                        intsFromFlags.joinToString(
                            separator = ", "
                        ) { lang.doublingNames[it] }
                    }"
                    SelectableCard(
                        text = text,
                        fontSize = fontSize,
                        colors = colors,
                        isSelected = isOn,
                        onClick = { _ ->
                            doublingDialogData.value = MultiListDialogData(
                                true, lang.doublingNames, intsFromFlags.toSet(), lang.selectDoubling
                            ) { indexes ->
                                model.updateUserOptions(
                                    "doublingFlags",
                                    convertIntsToFlags(indexes.map { it + 1 }.toSortedSet())
                                )
                                doublingDialogData.value =
                                    MultiListDialogData(itemList = doublingDialogData.value.itemList)
                            }
                        })
                }
                "Spread where possible" -> {
                    var isOn = userOptions.spread != 0
                    SelectableCard(
                        text = lang.spreadWherePossible,
                        colors = colors,
                        fontSize = fontSize,
                        isSelected = isOn,
                        onClick = {
                            isOn = !isOn
                            model.updateUserOptions(
                                "spread",
                                if (isOn) 1 else 0
                            )
                        })
                }
                "Deep Search in 4 part MK" -> {
                    var isOn = userOptions.deepSearch != 0
                    SelectableCard(
                        text = lang.deepSearch,
                        fontSize = fontSize,
                        colors = colors,
                        isSelected = isOn,
                        onClick = {
                            isOn = !isOn
                            model.updateUserOptions(
                                "deepSearch",
                                if (isOn) 1 else 0
                            )
                        })
                     }
                "Export MIDI" -> {
                    SelectableCard(text = lang.exportMidi, fontSize = fontSize, colors = colors, isSelected = true, onClick = {
                        val path = model.midiPath.absolutePath.toString()
                        var error = model.onPlay(false)
                        if (error.isEmpty()){
                            model.shareMidi(model.midiPath)
                        } else {
                            exportDialogData.value = ExportDialogData(true,"EXPORT MIDI",
                                "", error = lang.playToCreate
                            ) {

                                exportDialogData.value = ExportDialogData(path = path, error = error)
                            }
                        }
                    })
                }
                }
            }
        }
    } else { // Settings List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.drawerBackgroundColor),
            state = listState,
        ) { items(optionNames) { optionName ->
            val fontSize = dimensions.optionsFontSize
            when (optionName) {
            "Detector" -> {
            val flags = userOptions.detectorFlags
            val intsFromFlags = convertFlagsToInts(flags)
            val isOn = flags != 0
            val intervalNames = lang.intervalSet.map{ it.replace("\n"," / ") }
            val text = if(!isOn) lang.detector else "${lang.detector}: ${
                intsFromFlags.joinToString(
                    separator = ", "
                ) { intervalNames[it] }
            }"
            SelectableCard(text = text, fontSize = fontSize, colors = colors, isSelected = isOn, onClick = {
                detectorDialogData.value = MultiListDialogData(true, intervalNames,
                    intsFromFlags.toSet(), dialogTitle = lang.selectIntervalsToDetect
                ) { indexes ->
                    model.updateUserOptions(
                        "detectorFlags",
                        convertIntsToFlags(indexes.toSortedSet())
                    )
                    detectorDialogData.value = MultiListDialogData(itemList = detectorDialogData.value.itemList)
                }
            })
        }
            "Detector Extension" -> {
            val extensions: List<String> = (1..16).map{ it.toString()}
            val extIndex = userOptions.detectorExtension
            val isOn = userOptions.detectorFlags != 0
            SelectableCard(text = "${lang.detectorExtension}: ${extensions[extIndex-1]}", fontSize = fontSize, colors = colors, isSelected = isOn, onClick = {
                if(isOn){
                    detExtensionDialogData.value = ListDialogData(true,extensions,extIndex-1,lang.selectDetectorExtension
                    ) { index ->
                        model.updateUserOptions(
                            "detectorExtension",
                            index +1
                        )
                        detExtensionDialogData.value = ListDialogData(itemList = detExtensionDialogData.value.itemList)
                    }
                }

            })
        }

            "Colors" -> {
                val colorDefs = extractColorDefs(userOptions.colors)
                val appColors = AppColorThemes.values().map{ it.title }
                val appColorsName = if(colorDefs.app == "System") model.getSystemAppColorsName() else colorDefs.app
                val colorsIndex = appColors.indexOf(appColorsName)
                SelectableCard(text = "App Colors: $appColorsName", fontSize = fontSize, colors = colors, isSelected = !colorDefs.isCustom, onClick = {
                    colorsDialogData.value = ListDialogData(true, appColors, colorsIndex, "Choose a Color Set"){ index ->
                        model.updateUserOptions(
                            "colors",
                            "${AppColorThemes.values()[index].title}||${colorDefs.custom}"
                        )
                        colorsDialogData.value = ListDialogData(itemList = colorsDialogData.value.itemList)
                    }
                })
            }
            "Custom Colors"-> {
            val colorDefs = extractColorDefs(userOptions.colors)
            SelectableCard(text = "Custom Colors: ${colorDefs.custom}", fontSize = fontSize, colors = colors, isSelected = colorDefs.isCustom,onClick = {
                customColorsDialogData.value = CustomColorsDialogData(true, "Create a Color Set", colorDefs.custom, model
                ) { colorIndex ->
                    model.updateUserOptions(
                        "colors",
                        "$colorIndex||${colorDefs.app}"
                    )
                    customColorsDialogData.value = CustomColorsDialogData(model = model)
                }
            })
        }
            "Language" -> {
            val languages = LANGUAGES.values().map{ it.language }
            val langDef: String = if(userOptions.language == "System") model.getSystemLangDef() else userOptions.language
            val languageName = LANGUAGES.languageNameFromDef(langDef)
            val languageIndex= languages.indexOf(languageName)
            SelectableCard(text = "${lang.language}: $languageName", fontSize = fontSize, colors = colors, isSelected = true,onClick = {
                listDialogData.value = ListDialogData(true,languages,languageIndex,"Select a Language!"
                ) { index ->
                    model.updateUserOptions(
                        "language",
                        LANGUAGES.values()[index].toString()
                    )
                    listDialogData.value = ListDialogData(itemList = listDialogData.value.itemList)
                }
            })
        }
                "Zodiac" -> {
                    val flags = userOptions.zodiacFlags
                    val intsFromFlags = convertFlagsToInts(flags)
                    val isOn = flags > 0
                    val text = if (!isOn) lang.zodiac else "${lang.zodiac}: ${
                        intsFromFlags.joinToString(
                            separator = ", "
                        ) { lang.zodiacOptions[it] }
                    }"
                    SelectableCard(
                        text = text,
                        fontSize = fontSize,
                        colors = colors,
                        isSelected = isOn,
                        onClick = { _ ->
                            zodiacDialogData.value = MultiListDialogData(
                                true, lang.zodiacOptions, intsFromFlags.toSet(), lang.selectZodiac
                            ) { indexes ->
                                val actualIndexes = if(indexes.contains(2)) listOf(indexes[0],1,2) else indexes
                                model.updateUserOptions(
                                    "zodiacFlags",
                                    convertIntsToFlags(actualIndexes.toSortedSet())
                                )
                                zodiacDialogData.value =
                                    MultiListDialogData(itemList = zodiacDialogData.value.itemList)
                            }
                        })
                }
            "Credits" -> {
            SelectableCard(text = lang.credits, fontSize = fontSize, colors = colors, isSelected = true, onClick = {
                creditsDialogData.value = CreditsDialogData(true,"Credits:",
                ) {
                    creditsDialogData.value = CreditsDialogData()
                }
            }
            )
        }
        }
    }

        }
    }
}


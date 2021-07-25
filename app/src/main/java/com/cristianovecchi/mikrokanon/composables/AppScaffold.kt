package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import com.cristianovecchi.mikrokanon.AppViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristianovecchi.mikrokanon.AIMUSIC.RhythmPatterns
import com.cristianovecchi.mikrokanon.AIMUSIC.RowForm
import com.cristianovecchi.mikrokanon.convertFlagsToInts
import com.cristianovecchi.mikrokanon.convertIntsToFlags
import com.cristianovecchi.mikrokanon.locale.LANGUAGES
import com.cristianovecchi.mikrokanon.db.UserOptionsData
import com.cristianovecchi.mikrokanon.locale.Lang
import com.cristianovecchi.mikrokanon.ui.drawerBackgroundColor
import kotlinx.coroutines.flow.Flow

@Composable
fun AppScaffold(model: AppViewModel, userOptionsDataFlow: Flow<List<UserOptionsData>>, content: @Composable () -> Unit) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val titleStyle = SpanStyle(
        fontSize = 18.sp,
        color = Color.White)
    val creditStyle = SpanStyle(
        fontSize = 14.sp,
        color = Color.LightGray)

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colors.drawerBackgroundColor)
            .border(1.dp, MaterialTheme.colors.drawerBackgroundColor),
        scaffoldState = scaffoldState,
        drawerContent = { SettingsDrawer(model, userOptionsDataFlow)},
        topBar = {
            val creditsDialogData by lazy { mutableStateOf(CreditsDialogData())}
            CreditsDialog(creditsDialogData)
            TopAppBar() {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.drawerBackgroundColor)
                        .border(1.dp, MaterialTheme.colors.drawerBackgroundColor),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                )
                {
                    IconButton(
                        onClick = {scope.launch { scaffoldState.drawerState.open() }  }
                    ) {
                        Icon(Icons.Filled.Menu,"")
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
data class ButtonsDialogData(
    val dialogState: Boolean = false, val title:String = "",
    val model: AppViewModel, val buttonSize: Dp = 60.dp, val fontSize: Int = 18,
    val isActiveWaves: Boolean = false, val isActivePedals: Boolean = false,
    val onWave3: () -> Unit = {}, val onWave4: () -> Unit = {}, val onWave6: () -> Unit = {},
    val onPedal1: () -> Unit = {}, val onPedal3: () -> Unit = {}, val onPedal5: () -> Unit = {},
    val onTritoneSubstitution: () -> Unit = {}, val onRound: () -> Unit = {}, val onPedal: () -> Unit = {},
    val onSubmitButtonClick: () -> Unit = {}, )

data class ExportDialogData(val dialogState: Boolean = false, val title:String = "", val path:String = "",
                            val error:String = "", val onSubmitButtonClick: () -> Unit = {})
data class CreditsDialogData(val dialogState: Boolean = false, val title:String = "",  val onSubmitButtonClick: () -> Unit = {})



@Composable
fun SettingsDrawer(model: AppViewModel, userOptionsDataFlow: Flow<List<UserOptionsData>>){

    val listDialogData by lazy { mutableStateOf(ListDialogData())}
    val bpmDialogData by lazy { mutableStateOf(NumberDialogData())}
    val multiListDialogData by lazy { mutableStateOf(MultiListDialogData())}
    val exportDialogData by lazy { mutableStateOf(ExportDialogData())}
    val creditsDialogData by lazy { mutableStateOf(CreditsDialogData())}
    //val intervalSetDialogData by lazy { mutableStateOf(MultiListDialogData())}
    val detectorDialogData by lazy { mutableStateOf(MultiListDialogData())}
    val detExtensionDialogData by lazy { mutableStateOf(ListDialogData())}
    val colorsDialogData by lazy { mutableStateOf(ListDialogData())}
    val ritornelloDialogData by lazy {mutableStateOf(ListDialogData())}

    val dimensions = model.dimensions
    val optionNames= listOf("Ensemble", "BPM", "Rhythm",  "Rhythm Shuffle", "Parts Shuffle",
        "Retrograde", "Inverse",  "Inv-Retrograde", "Separator","Ritornello","Doubling",
        "Spread where possible", "Deep Search in 4 part MK", "Detector","Detector Extension",
        "Export MIDI", "Colors", "Custom Colors","Language","Credits")
    //val userOptionsData by model.userOptionsData.asFlow().collectAsState(initial = listOf())
    val userOptionsData by userOptionsDataFlow.collectAsState(initial = listOf())
    val lang = Lang.provideLanguage(model.getUserLangDef())
    val userOptions = if(userOptionsData.isEmpty()) UserOptionsData.getDefaultUserOptionsData()
                        else userOptionsData[0]
    val listState = rememberLazyListState()

    ListDialog(listDialogData, lang.OKbutton,dimensions.sequenceDialogFontSize)
    MultiListDialog(multiListDialogData, dimensions.sequenceDialogFontSize, lang.OKbutton)
    BpmDialog(bpmDialogData, lang.OKbutton)
    ExportDialog(exportDialogData, lang.OKbutton)
    CreditsDialog(creditsDialogData, lang.OKbutton)
    //MultiListDialog(intervalSetDialogData, dimensions.sequenceDialogFontSize, lang.OKbutton)
    MultiListDialog(detectorDialogData, dimensions.sequenceDialogFontSize, lang.OKbutton)
    ListDialog(detExtensionDialogData, lang.OKbutton,dimensions.sequenceDialogFontSize, fillPrevious = true)
    ListDialog(colorsDialogData, lang.OKbutton,dimensions.sequenceDialogFontSize)
    ListDialog(ritornelloDialogData, lang.OKbutton, dimensions.sequenceDialogFontSize, fillPrevious = true)

//    userOptionsData.forEach{
//        Text("#${it.id} = ens_type: ${it.ensembleType} - bpm: ${it.bpm} ")
//    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.drawerBackgroundColor),
        state = listState,
    ) { items(optionNames) { optionName ->
            val fontSize = dimensions.optionsFontSize
            when(optionName){
                "Ensemble" -> {
                    val ensNames: List<String> = lang.ensembleNames
                    val ensIndex = userOptions.ensembleType
                        SelectableCard(text = "${lang.ensemble}: ${ensNames[ensIndex]}", fontSize = fontSize, isSelected = true, onClick = {
                            listDialogData.value = ListDialogData(true,ensNames,ensIndex,lang.selectEnsemble
                            ) { index ->
                                model.updateUserOptions(
                                    "ensemble_type",
                                    index
                                )
                                listDialogData.value = ListDialogData(itemList = listDialogData.value.itemList)
                            }
                        })
                    }
                "BPM" -> {
                    val bpm = userOptions.bpm
                    SelectableCard(text = "${lang.bpm}: $bpm", fontSize = fontSize, isSelected = true, onClick = {
                        bpmDialogData.value = NumberDialogData(
                            true, "${lang.beatsPerMinute}:", bpm, 18, 600
                        ) { bpm ->
                            model.updateUserOptions(
                                "bpm",
                                bpm
                            )
                            listDialogData.value =
                                ListDialogData(itemList = listDialogData.value.itemList)
                        }
                    })
                }
                "Rhythm" -> {
                    val rhythmNames = RhythmPatterns.getTitles()
                    val rhythmIndex = userOptions.rhythm
                    SelectableCard(text = "${lang.rhythm}: ${rhythmNames[rhythmIndex]}", fontSize = fontSize, isSelected = true,onClick = {
                        listDialogData.value = ListDialogData(true,rhythmNames,rhythmIndex,lang.selectRhythm
                        ) { index ->
                            model.updateUserOptions(
                                "rhythm",
                                index
                            )
                            listDialogData.value = ListDialogData(itemList = listDialogData.value.itemList)
                        }
                    })
                }
                "Rhythm Shuffle" -> {
                    var isOn = userOptions.rhythmShuffle != 0
                    SelectableCard(text = lang.rhythmShuffle, fontSize = fontSize, isSelected = isOn, onClick = {
                        isOn = !isOn
                        model.updateUserOptions(
                            "rhythmShuffle",
                            if(isOn) 1 else 0
                        )
                    })
                }
                "Parts Shuffle" -> {
                    var isOn = userOptions.partsShuffle != 0
                    SelectableCard(text = lang.partsShuffle, fontSize = fontSize, isSelected = isOn, onClick = {
                        isOn = !isOn
                        model.updateUserOptions(
                            "partsShuffle",
                            if(isOn) 1 else 0
                        )
                    })
                }
                "Inverse" -> {
                    val flags = userOptions.rowFormsFlags
                    val isOn = flags and RowForm.INVERSE.flag != 0
                    SelectableCard(text = lang.inverse, fontSize = fontSize, isSelected = isOn, onClick = {
                        val newFlags = flags xor RowForm.INVERSE.flag // ^ toggles the flag
                        model.updateUserOptions(
                            "rowFormsFlags",
                            newFlags
                        )
                    })
                }
                "Retrograde" -> {
                    val flags = userOptions.rowFormsFlags
                    val isOn = flags and RowForm.RETROGRADE.flag != 0
                    SelectableCard(text = lang.retrograde, fontSize = fontSize, isSelected = isOn, onClick = {
                        val newFlags = flags xor RowForm.RETROGRADE.flag // ^ toggles the flag
                        model.updateUserOptions(
                            "rowFormsFlags",
                            newFlags
                        )
                    })
                }
                "Inv-Retrograde" -> {
                    val flags = userOptions.rowFormsFlags
                    val isOn = flags and RowForm.INV_RETROGRADE.flag != 0
                    SelectableCard(text = lang.invRetrograde, fontSize = fontSize, isSelected = isOn, onClick = {
                        val newFlags = flags xor RowForm.INV_RETROGRADE.flag // ^ toggles the flag
                        model.updateUserOptions(
                            "rowFormsFlags",
                            newFlags
                        )
                    })
                }
                "Separator" -> {
                    val flags = userOptions.rowFormsFlags
                    val isOn = flags and 0b10000 != 0
                    SelectableCard(text = lang.rowFormSeparator, fontSize = fontSize, isSelected = isOn, onClick = {
                        if(flags != 1) { // don't need a separator if row forms are unactive
                            val newFlags = flags xor 0b10000 // ^ toggles the flag
                            model.updateUserOptions(
                                "rowFormsFlags",
                                newFlags
                            )
                        }

                    })
                }
                "Ritornello" -> {
                    val timeIndices: List<String> = (0..127).map{ it.toString()}
                    val nTimes = userOptions.ritornello
                    val isOn = nTimes != 0
                    val text = if(nTimes == 0) lang.ritornello else "${lang.ritornello} x $nTimes"
                    SelectableCard(text = text, fontSize = fontSize, isSelected = isOn, onClick = {
                            ritornelloDialogData.value = ListDialogData(true, timeIndices,nTimes, lang.selectRitornello
                            ) { index ->
                                model.updateUserOptions(
                                    "ritornello",
                                    index
                                )
                                ritornelloDialogData.value = ListDialogData(itemList = ritornelloDialogData.value.itemList)
                            }
                    })
                }
                "Doubling" -> {
                    val flags = userOptions.doublingFlags
                    val intsFromFlags = convertFlagsToInts(flags).map{ it - 1 }
                    val isOn = flags > 0
                    val text = if(!isOn) lang.doubling else "${lang.doubling}: ${
                        intsFromFlags.joinToString(
                            separator = ", "
                        ) { lang.doublingNames[it] }
                    }"
                    SelectableCard(text = text, fontSize = fontSize, isSelected = isOn, onClick = { _ ->
                        multiListDialogData.value = MultiListDialogData(true, lang.doublingNames, intsFromFlags.toSet() ,lang.selectDoubling
                        ) { indexes ->
                            model.updateUserOptions(
                                "doublingFlags",
                                convertIntsToFlags(indexes.map{it + 1}.toSortedSet())
                            )
                            multiListDialogData.value = MultiListDialogData(itemList = multiListDialogData.value.itemList)
                        }
                    })
                }
                "Spread where possible" -> {
                    var isOn = userOptions.spread != 0
                    SelectableCard(text = lang.spreadWherePossible, fontSize = fontSize, isSelected = isOn, onClick = {
                        isOn = !isOn
                        model.updateUserOptions(
                            "spread",
                            if(isOn) 1 else 0
                        )
                    })
                }
                "Deep Search in 4 part MK" -> {
                    var isOn = userOptions.deepSearch != 0
                    SelectableCard(text = lang.deepSearch, fontSize = fontSize, isSelected = isOn, onClick = {
                        isOn = !isOn
                        model.updateUserOptions(
                            "deepSearch",
                            if(isOn) 1 else 0
                        )
                    })
                }
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
                    SelectableCard(text = text, fontSize = fontSize, isSelected = isOn, onClick = {
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
                    SelectableCard(text = "${lang.detectorExtension}: ${extensions[extIndex-1]}", fontSize = fontSize, isSelected = isOn, onClick = {
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
                "Export MIDI" -> {
                    SelectableCard(text = lang.exportMidi, fontSize = fontSize, isSelected = true, onClick = {
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
                "Colors" -> {

                }
                "Custom Colors"-> {

                }
                "Language" -> {
                    val languages = LANGUAGES.values().map{ it.language }
                    val langDef: String = if(userOptions.language == "System") model.getSystemLangDef() else userOptions.language
                    val languageName = LANGUAGES.languageNameFromDef(langDef)
                    val languageIndex= languages.indexOf(languageName)
                        SelectableCard(text = "${lang.language}: $languageName", fontSize = fontSize, isSelected = true,onClick = {
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
                "Credits" -> {
                    SelectableCard(text = lang.credits, fontSize = fontSize, isSelected = true, onClick = {
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


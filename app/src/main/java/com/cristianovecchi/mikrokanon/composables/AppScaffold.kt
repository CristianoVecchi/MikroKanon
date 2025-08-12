package com.cristianovecchi.mikrokanon.composables

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.*
import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.AIMUSIC.CheckAndReplaceData.Companion.insertInMultiCheckAndReplaceCsv
import com.cristianovecchi.mikrokanon.composables.dialogs.*
import com.cristianovecchi.mikrokanon.db.CounterpointData
import com.cristianovecchi.mikrokanon.db.UserOptionsData
import com.cristianovecchi.mikrokanon.locale.*
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationData
import com.cristianovecchi.mikrokanon.AIMUSIC.HarmonizationType
import com.cristianovecchi.mikrokanon.AIMUSIC.chordsInstruments
import com.cristianovecchi.mikrokanon.ui.AppColorThemes
import com.cristianovecchi.mikrokanon.ui.AppColors
import com.cristianovecchi.mikrokanon.ui.Dimensions
import com.cristianovecchi.mikrokanon.ui.extractColorDefs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import kotlin.math.absoluteValue

@Composable
fun AppScaffold(model: AppViewModel,
                dimensionsFlow: Flow<Dimensions>,
                userOptionsDataFlow: Flow<List<UserOptionsData>>,
                counterpointsDataFlow: Flow<List<CounterpointData>>,
                content: @Composable () -> Unit) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val userOptionsData by model.userOptionsData.asFlow().collectAsState(initial = listOf())
    val colors by derivedStateOf {
        if(userOptionsData.isNotEmpty()) model.setAppColors(userOptionsData[0].colors)
        model.appColors // default ALL BLACK
    }
    val dimensions by model.dimensions.asFlow().collectAsState(initial = model.dimensions.value!!)
    val scaffoldModifier = if (Build.VERSION.SDK_INT >= 35) Modifier.background(colors.selCardBorderColorSelected).padding(PaddingValues(0.dp, 46.dp, 0.dp, 0.dp))
    else Modifier
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Scaffold(modifier = scaffoldModifier,
            //modifier = Modifier
            //.background(colors.selCardBorderColorSelected),
            //.border(1.dp, Color.Transparent),
            scaffoldState = scaffoldState,
            drawerContent = { SettingsDrawer(model, colors, dimensionsFlow, userOptionsDataFlow, counterpointsDataFlow)},
            topBar = {
                val creditsDialogData by lazy { mutableStateOf(TextDialogData())}
                CreditsDialog(creditsDialogData, dimensions)
                SmartTopAppBar(modifier = Modifier.height(dimensions.selectorButtonSize).border(1.dp,colors.selCardBorderColorSelected),
                    dimensions = dimensions, colors = colors, model = model,
                    onMenuClick = {
                        scope.launch { scaffoldState.drawerState.open() }
                    },
                    onCreditsClick = {
                        creditsDialogData.value = TextDialogData(true, "Credits:",
                        ) {
                            creditsDialogData.value = TextDialogData()
                        }
                    })
            },
            content = {
                content()
            }
        )
    }
    Scaffold(modifier = scaffoldModifier,
        //modifier = Modifier
            //.background(colors.selCardBorderColorSelected),
            //.border(1.dp, Color.Transparent),
        scaffoldState = scaffoldState,
        drawerContent = { SettingsDrawer(model, colors, dimensionsFlow, userOptionsDataFlow, counterpointsDataFlow)},
        topBar = {
                val creditsDialogData by lazy { mutableStateOf(TextDialogData())}
                CreditsDialog(creditsDialogData, dimensions)
                SmartTopAppBar(modifier = Modifier.height(dimensions.selectorButtonSize).border(1.dp,colors.selCardBorderColorSelected),
                    dimensions = dimensions, colors = colors, model = model,
                    onMenuClick = {
                        scope.launch { scaffoldState.drawerState.open() }
                    },
                    onCreditsClick = {
                        creditsDialogData.value = TextDialogData(true, "Credits:",
                        ) {
                            creditsDialogData.value = TextDialogData()
                        }
                    })
        },
        content = {
            content()
        }
    )
}


@Composable
fun SettingsDrawer(model: AppViewModel, colors:AppColors, dimensionsFlow: Flow<Dimensions>,
                   userOptionsDataFlow: Flow<List<UserOptionsData>>,
                   counterpointsDataFlow: Flow<List<CounterpointData>>){

    val listDialogData by lazy { mutableStateOf(ListDialogData())}
    val ensemblesDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val rhythmDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val multiBpmDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val melodyTypesDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val rangeTypesDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val legatoTypesDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val multiFloatDialogData by lazy { mutableStateOf(MultiFloatDialogData(model = model))}
    val transposeDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val rowFormsDialogData = remember { mutableStateOf(MultiNumberDialogData(model = model))} // "remember" for play button
    val doublingDialogData by lazy { mutableStateOf(MultiListDialogData())}
    val audio8DDialogData by lazy { mutableStateOf(MultiListDialogData())}
    val harmonyDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val drumsDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val checkAndReplaceDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val chordsToEnhanceDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val clearSlotsDialogData by lazy { mutableStateOf(MultiListDialogData())}
    val exportDialogData by lazy { mutableStateOf(ExportDialogData())}
    val privacyDialogData by lazy { mutableStateOf(TextDialogData())}
    val historiaDialogData by lazy { mutableStateOf(TextDialogData())}
    val creditsDialogData by lazy { mutableStateOf(TextDialogData())}
    val mbtiDialogData by lazy { mutableStateOf(MultiListDialogData())}
    val detectorDialogData by lazy { mutableStateOf(MultiListDialogData())}
    val detExtensionDialogData by lazy { mutableStateOf(ListDialogData())}
    val colorsDialogData by lazy { mutableStateOf(ListDialogData())}
    val customColorsDialogData by lazy { mutableStateOf(CustomColorsDialogData(model = model))}
    val ritornelloDialogData by lazy {mutableStateOf(ListDialogData())}
    val vibratoDialogData by lazy {mutableStateOf(ListDialogData())}
    val zodiacDialogData by lazy { mutableStateOf(MultiListDialogData())}
    val percentageDialogData by lazy { mutableStateOf(PercentageDialogData(model = model))}

    val verticalIntervals by model.intervalSetVertical.observeAsState(model.intervalSetVertical.value!!)
    val dimensions by dimensionsFlow.collectAsState(initial = model.dimensions.value!!)
    val percentageFormat: NumberFormat = NumberFormat.getPercentInstance()
    percentageFormat.setMinimumFractionDigits(1)
    //val colors = model.appColors
    val optionNames= listOf(
        "Ensemble", "Glissando","Vibrato","Nuances",
        "Rhythm", "Swing Shuffle", "Rhythm Shuffle", "Parts Shuffle",
        "Doubling","8D AUDIO",

        "BPM", "Dynamics",
        "Range","Melody","Articulation",
        "Spacer",
        "Ritornello", "Transpose", "Row Forms",

        "Harmony I", "Harmony II", "Harmony III",

        "Chords to Enhance", "Enhance in Transpositions", "Check and Replace",

        "Drums",

        "Clear Slots", "Spacer", "Export MIDI",

        "Spread where possible", "Deep Search in 4 part MK",
         "Detector","Detector Extension",
        //"Colors",
        "Custom Colors", "Counterpoint View", "Language","Zodiac",
        "MBTI",
        "Spacer", "Privacy Policy", "Credits", "Historia")

    val userOptionsData by userOptionsDataFlow.collectAsState(initial = listOf())
    val allCounterpointsData by counterpointsDataFlow.collectAsState(initial = listOf())
    val lang by model.language.asFlow().collectAsState(initial = Lang.provideLanguage(model.getUserLangDef()))
    val userOptions by derivedStateOf{
        if  (userOptionsData.isEmpty()) UserOptionsData . getDefaultUserOptionsData ()
        else userOptionsData[0]
    }
        val listState = rememberLazyListState()
        val selectedTab by model.lastScaffoldTab.asFlow().collectAsState(initial = ScaffoldTabs.SETTINGS)
        val affectsHorizontallySymbol = "→ ➚➘➚➘➚  "
        EnsembleDialog(ensemblesDialogData, dimensions)
        ListDialog(listDialogData, dimensions, lang.OkButton, colors)
        RhythmDialog(rhythmDialogData, dimensions, patterns = RhythmPatterns.values().toList() )
        MultiListDialog(doublingDialogData, dimensions, lang.OkButton, colors)
        MultiListDialog(audio8DDialogData, dimensions, lang.OkButton, colors)
        MultiListDialog(clearSlotsDialogData, dimensions, lang.OkButton, colors)
        MelodyTypeDialog(melodyTypesDialogData,dimensions, lang.melodyOptions)
        RangeTypeDialog(rangeTypesDialogData, dimensions, lang.rangeOptions)
        LegatoTypeDialog(legatoTypesDialogData, dimensions, lang.articulationOptions)
        MultiBpmDialog(multiBpmDialogData, dimensions, lang.OkButton)
        MultiDynamicDialog(multiFloatDialogData, dimensions, lang.OkButton)
        val intervalsForTranspose = getIntervalsForTranspose(lang.intervalSet)
        TransposeDialog(transposeDialogData, dimensions, intervalsForTranspose, lang.OkButton)
        val formsNames = listOf("unrelated", lang.original, lang.inverse, lang.retrograde, lang.invRetrograde)
        RowFormsDialog(rowFormsDialogData, dimensions, formsNames, lang.slotNumbers)
        ExportDialog(exportDialogData, lang.OkButton)
        PrivacyDialog(privacyDialogData, dimensions, lang.OkButton)
        CreditsDialog(creditsDialogData, dimensions, lang.OkButton)
        HistoriaDialog(historiaDialogData, dimensions, lang.OkButton)
        MultiListDialog(mbtiDialogData, dimensions, lang.OkButton, colors, affectsHorizontallySymbol)
        MultiListDialog(detectorDialogData, dimensions, lang.OkButton, colors)
        ListDialog(detExtensionDialogData, dimensions, lang.OkButton, colors, fillPrevious = true)
        ListDialog(colorsDialogData, dimensions, lang.OkButton, colors)
        MultiListDialog(zodiacDialogData, dimensions, lang.OkButton, colors)
        CustomColorsDialog(customColorsDialogData, dimensions, lang.OkButton)
        ListDialog(ritornelloDialogData, dimensions, lang.OkButton, colors, fillPrevious = true)
        ListDialog(vibratoDialogData, dimensions, lang.OkButton, colors, fillPrevious = true)
        HarmonyDialog(harmonyDialogData, dimensions)
        DrumsDialog(drumsDialogData, dimensions)
        CheckAndReplaceDialog(checkAndReplaceDialogData, dimensions)
        ChordsToEnhanceDialog(chordsToEnhanceDialogData, dimensions)
        PercentageDialog(percentageDialogData, dimensions, lang.OkButton, colors)

        SettingTabs(selectedTab = selectedTab, dimensions = dimensions, colors = colors, model = model)

        val tabModifier = Modifier
            .fillMaxSize()
            .background(colors.drawerBackgroundColor)
            .padding(start = 4.dp, top = 4.dp, end = 4.dp)
        val spacerHeight = 8
        when (selectedTab) {
            ScaffoldTabs.SOUND -> {
                LazyColumn( modifier = tabModifier,state = listState)
                { items(optionNames) { optionName ->
                    val fontSize = dimensions.optionsFontSize
                    when (optionName) {
                        "Ensemble" -> {
                            val ensNames: List<String> = lang.ensembleNames + synthsNames
                            val ensListIndexes = userOptions.ensemblesList.extractIntListsFromCsv()

                            SelectableCard(
                                text = "${lang.ensemble}: ${describeEnsembles(ensListIndexes, ensNames)}",
                                fontSize = fontSize,
                                colors = colors,
                                isSelected = true,
                                onClick = {
                                    ensemblesDialogData.value = MultiNumberDialogData(
                                        true, lang.selectEnsemble, userOptions.ensemblesList,
                                        model = model, names = ensNames,
                                        intSequences = ensListIndexes
                                    ) { ensListsList ->
                                        model.updateUserOptions(
                                            "ensemblesList",
                                            ensListsList
                                        )
                                        ensemblesDialogData.value =
                                            MultiNumberDialogData(model = model)
                                    }
                                })
                        }
                        "Glissando" -> {
                            val intervalsForGlissando = createGlissandoIntervals(lang.doublingNames)
                            val flags = userOptions.glissandoFlags.toInt()
                            val intsFromFlags = convertFlagsToInts(flags).map { it - 1 }
                            val isOn = flags > 0
                            val nl = newLineOrNot(intsFromFlags, 2)
                            val text = if (!isOn) lang.glissando else "${lang.glissando}: $nl${
                                intsFromFlags.joinToString(separator = ", ") { intervalsForGlissando[it] }
                            }"
                            SelectableCard(
                                text = text,
                                fontSize = fontSize,
                                colors = colors,
                                isSelected = isOn,
                                onClick = {
                                    doublingDialogData.value = MultiListDialogData(
                                        true, intervalsForGlissando, intsFromFlags.toSet(), lang.selectGlissando
                                    ) { indexes, _ ->
                                        model.updateUserOptions(
                                            "glissandoFlags",
                                            convertIntsToFlags(indexes.map { it + 1 }.toSortedSet()).toString()
                                        )
                                        doublingDialogData.value =
                                            MultiListDialogData(itemList = doublingDialogData.value.itemList)
                                    }
                                })
                        }
                        "Vibrato" -> {
                            var intensity = userOptions.vibrato.toInt()
                            //intensity = if(intensity >= 8) 7 else intensity
                            val sym = getVibratoSymbol()
                            //val timeIndices: List<String> = (0..AppViewModel.MAX_VIBRATO).map { if(it == 0) "-" else sym.repeat(it) }
                            val timeIndices: List<String> = (0..AppViewModel.MAX_VIBRATO).map { if(it == 0) "-" else "$sym$it" }
                            val isOn = intensity != 0
                            //val text = if (intensity == 0) lang.vibrato else "${lang.vibrato}: ${sym.repeat(intensity)}"
                            val text = if (intensity == 0) lang.vibrato else "${lang.vibrato}: $sym$intensity"
                            SelectableCard(
                                text = text,
                                fontSize = fontSize,
                                colors = colors,
                                isSelected = isOn,
                                onClick = {
                                    vibratoDialogData.value = ListDialogData(
                                        true, timeIndices, intensity, lang.selectVibrato
                                    ) { index ->
                                        model.updateUserOptions(
                                            "vibrato",
                                            index.toString()
                                        )
                                        ritornelloDialogData.value =
                                            ListDialogData(itemList = vibratoDialogData.value.itemList)
                                    }
                                })
                        }
                        "Nuances" -> {
                            val nuancesOptions: List<String> = lang.nuancesOptions
                            val nuancesIndex = userOptions.nuances.toInt()
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
                                            index.toString()
                                        )
                                        listDialogData.value =
                                            ListDialogData(itemList = listDialogData.value.itemList)
                                    }
                                })
                        }
                        "Rhythm" -> {
                            val rhythmNames = RhythmPatterns.getTitles()
                            val rhythmPatterns = userOptions.rhythm.extractIntPairsFromCsv()
                            val rhythmTexts = rhythmPatterns.map{
                                val arrow = if(it.second<0) "←" else ""
                                val feature = if(it.second.absoluteValue>1 ) " (${it.second.absoluteValue}x)" else ""
                                arrow + rhythmNames[it.first] + feature
                            }
                            val patterns = RhythmPatterns.values()
                            val totalNotes = rhythmPatterns.sumOf{
                                patterns[it.first].nPositiveValues() * it.second.absoluteValue
                            }
                            //val nl = newLineOrNot(rhythmTexts, 2)
                            SelectableCard(
                                text = "${lang.rhythm}: $totalNotes♪\n${rhythmTexts.joinToString(" + ")}",
                                fontSize = fontSize,
                                colors = colors,
                                isSelected = true,
                                onClick = {
                                    rhythmDialogData.value = MultiNumberDialogData(
                                        true, lang.selectRhythm, userOptions.rhythm, model = model
                                    ) { indices ->
                                        model.updateUserOptions(
                                            "rhythm",
                                            indices
                                        )
                                        listDialogData.value =
                                            ListDialogData(itemList = listDialogData.value.itemList)
                                    }
                                })
                        }
                        "Swing Shuffle" -> {
                            val shuffle = userOptions.swingShuffle.toFloat()
                            val isOn = (shuffle * 1000).toInt() != 500
                            SelectableCard(
                                text = if(isOn) "${lang.shuffle}: ${shuffle.formatDecimalWithoutZero()}%" else lang.shuffle,
                                fontSize = fontSize,
                                colors = colors,
                                isSelected = isOn,
                                onClick = {
                                            percentageDialogData.value = PercentageDialogData(
                                                true, lang.selectShuffle,
                                                shuffle, decimalsToKeep = 3, firstRendering = true, model = model
                                            ) { shufflePercentage ->

                                                    model.updateUserOptions("swingShuffle", shufflePercentage.toString())
                                                }
                                        }
                            )
                        }
                        "Rhythm Shuffle" -> {
                            var isOn = userOptions.rhythmShuffle.toInt() != 0
                            SelectableCard(
                                text = lang.rhythmShuffle,
                                fontSize = fontSize,
                                colors = colors,
                                isSelected = isOn,
                                onClick = {
                                    isOn = !isOn
                                    model.updateUserOptions(
                                        "rhythmShuffle",
                                        if (isOn) 1.toString() else 0.toString()
                                    )
                                })
                        }
                        "Parts Shuffle" -> {
                            var isOn = userOptions.partsShuffle.toInt() != 0
                            SelectableCard(
                                text = lang.partsShuffle,
                                fontSize = fontSize,
                                colors = colors,
                                isSelected = isOn,
                                onClick = {
                                    isOn = !isOn
                                    model.updateUserOptions(
                                        "partsShuffle",
                                        if (isOn) 1.toString() else 0.toString()
                                    )
                                })
                        }

                        "Doubling" -> {
                            val flags = userOptions.doublingFlags.toInt()
                            val intsFromFlags = convertFlagsToInts(flags).map { it - 1 }
                            val isOn = flags > 0
                            val nl = newLineOrNot(intsFromFlags, 2)
                            val text = if (!isOn) lang.doubling else "${lang.doubling}: $nl${
                                intsFromFlags.joinToString(
                                    separator = ", "
                                ) { lang.doublingNames[it] }
                            }"
                            SelectableCard(
                                text = text,
                                fontSize = fontSize,
                                colors = colors,
                                isSelected = isOn,
                                onClick = {
                                    doublingDialogData.value = MultiListDialogData(
                                        true, lang.doublingNames, intsFromFlags.toSet(), lang.selectDoubling
                                    ) { indexes, _ ->
                                        model.updateUserOptions(
                                            "doublingFlags",
                                            convertIntsToFlags(indexes.map { it + 1 }.toSortedSet()).toString()
                                        )
                                        doublingDialogData.value =
                                            MultiListDialogData(itemList = doublingDialogData.value.itemList)
                                    }
                                })
                        }
                        "8D AUDIO" -> {
                            val flags = userOptions.audio8DFlags.toInt()
                            val intsFromFlags = convertFlagsToInts(flags)
                            val isOn = flags > 0
                            val nl = newLineOrNot(intsFromFlags, 7)
                            val text = if (!isOn) lang.audio8D else "${lang.audio8D}: $nl${
                                intsFromFlags.joinToString(
                                    separator = ", "
                                ) { (it+1).toString() }
                            }"
                            SelectableCard(
                                text = text,
                                fontSize = fontSize,
                                colors = colors,
                                isSelected = isOn,
                                onClick = {
                                    doublingDialogData.value = MultiListDialogData(
                                        true, (1..12).map{it.toString()}, intsFromFlags.toSet(), lang.selectAudio8D
                                    ) { indexes, _  ->
                                        model.updateUserOptions(
                                            "audio8DFlags",
                                            convertIntsToFlags(indexes.toSortedSet()).toString()
                                        )
                                        doublingDialogData.value =
                                            MultiListDialogData(itemList = doublingDialogData.value.itemList)
                                    }
                                })
                        }


                    }
                }
                }
            }
            ScaffoldTabs.BUILDING -> {
                LazyColumn( modifier = tabModifier, state = listState)
                {
                    items(optionNames) { optionName ->
                        val fontSize = dimensions.optionsFontSize
                        when (optionName) {
                            "Range" -> {
                                //val rangeOptions: List<String> = lang.rangeOptions
                                val rangeTypes = userOptions.rangeTypes
                                val rangePairs = rangeTypes.extractIntPairsFromCsv()
                                val octaves = getOctaveSymbols()
                                val nl = newLineOrNot(rangePairs, 4)
                                SelectableCard(
                                    text = "${lang.range}: $nl${
                                        rangePairs.joinToString("  ") { "${rangeTypeMap[it.first]!!}${octaves[it.second + 2]}" }

                                    }",
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = true,
                                    onClick = {
                                        rangeTypesDialogData.value = MultiNumberDialogData(
                                            true, lang.selectRange, rangeTypes, model = model
                                        ) { index ->
                                            model.updateUserOptions(
                                                "rangeTypes",
                                                index
                                            )
                                            listDialogData.value =
                                                ListDialogData(itemList = listDialogData.value.itemList)
                                        }
                                    })
                            }
                            "Melody" -> {
                                //val melodyOptions: List<String> = lang.melodyOptions
                                val melodyTypes = userOptions.melodyTypes
                                val melodyInts = melodyTypes.extractIntsFromCsv()
                                val nl = newLineOrNot(melodyInts, 9)
                                SelectableCard(
                                    text = "${lang.melody}: $nl${melodyInts.map{melodyTypeMap[it]}.joinToString("  ") }",
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = true,
                                    onClick = {
                                        melodyTypesDialogData.value = MultiNumberDialogData(
                                            true,lang.selectMelody, melodyTypes, model = model
                                        ) { index ->
                                            model.updateUserOptions(
                                                "melodyTypes",
                                                index
                                            )
                                            listDialogData.value =
                                                ListDialogData(itemList = listDialogData.value.itemList)
                                        }
                                    })
                            }
                            "Articulation" -> {
                                //val rangeOptions: List<String> = lang.rangeOptions
                                val legatoTypes = userOptions.legatoTypes
                                //val ribattutos = getRibattutoSymbols()
                                val description = legatoTypes.describeForArticulation(legatoTypeMap)
                                val nl = if(description.length<11) "" else "\n"
                                SelectableCard(
                                    text = "${lang.articulation}: $nl$description",
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = true,
                                    onClick = {
                                        legatoTypesDialogData.value = MultiNumberDialogData(
                                            true, lang.selectArticulation, legatoTypes, model = model
                                        ) { index ->
                                            model.updateUserOptions(
                                                "legatoTypes",
                                                index
                                            )
                                            listDialogData.value =
                                                ListDialogData(itemList = listDialogData.value.itemList)
                                        }
                                    })
                            }

                            "Dynamics" -> {
                                val dynamics = userOptions.dynamics
                                val symbols = getDynamicSymbols()
                                val description = dynamics.describeForDynamic(model.dynamicMap, symbols[12], symbols[13])
                                val nl = if(description.length<21) "" else "\n"
                                SelectableCard(
                                    text = "${lang.dynamics}:  $nl$description",
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
                                val description = bpms.describe()
                                val nl = if(description.length<15) "" else "\n"
                                SelectableCard(
                                    text = "${lang.bpm}: $nl$description",
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
                            "Row Forms" -> {
                                val formsCsv = userOptions.rowForms
                                val isOn = formsCsv != "1"
                                val formPairs = formsCsv.extractIntPairsFromCsv()
                                val nl = newLineOrNot(formPairs, 5)
                                SelectableCard(
                                    text = if(isOn)
                                        "${lang.rowForms}: $nl${
                                            formPairs.joinToString(" ") {
                                                it.describeSingleRowForm(rowFormsMap, lang.slotNumbers)
                                            }
                                        }"
                                    else lang.rowForms,
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = isOn,
                                    onClick = {
                                        rowFormsDialogData.value = MultiNumberDialogData(
                                            true, lang.selectRowForms, formsCsv,
                                            model = model,
                                        ) { rowForms ->
                                            model.updateUserOptions(
                                                "rowForms",
                                                rowForms
                                            )
                                            listDialogData.value =
                                                ListDialogData(itemList = listDialogData.value.itemList)
                                        }
                                    })
                            }
                            "Spacer" -> {
                                Spacer(modifier = Modifier.height(spacerHeight.dp))
                            }
                            "Transpose" -> {
                                val transposeCsv = userOptions.transpose
                                val isOn = transposeCsv != "0|1"
                                val description = transposeCsv.describeForTranspose(intervalsForTranspose)
                                val nl = if(description.length<8) "" else "\n"
                                SelectableCard(
                                    text = if(isOn) "${lang.transpose}: $nl$description" else lang.transpose,
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = isOn,
                                    onClick = {
                                        transposeDialogData.value = MultiNumberDialogData(
                                            true, lang.selectTranspositions, transposeCsv,
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
                            "Ritornello" -> {
                                val timeIndices: List<String> = (1..128).map { it.toString() }
                                val nTimes = userOptions.ritornello.toInt()
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
                                                index.toString()
                                            )
                                            ritornelloDialogData.value =
                                                ListDialogData(itemList = ritornelloDialogData.value.itemList)
                                        }
                                    })
                            }
                        }
                    }

                }
            }
            ScaffoldTabs.ACCOMPANIST -> {
                LazyColumn(modifier = tabModifier, state = listState)
                {
                    val harmonizations = HarmonizationData.getHarmonizationsTriple(userOptions.harmonizations)
                    items(optionNames) { optionName ->
                        val fontSize = dimensions.optionsFontSize
                        when (optionName) {
                            "Harmony I" -> {
                                var harmDatas = harmonizations.first
                                harmDatas = harmDatas.ifEmpty { listOf(HarmonizationData()) }
                                val isSelected = !(harmDatas.size == 1 && harmDatas[0].type == HarmonizationType.NONE)
                                SelectableCard(
                                    text = if(!isSelected) lang.harmony + " I"
                                            else "${lang.harmony} I : \n\n${harmDatas.mapIndexed{i, hm -> 
                                        "${i+1}: ${hm.describe()}"}.joinToString("\n\n")}",
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = isSelected,
                                    onClick = {
                                        harmonyDialogData.value = MultiNumberDialogData(
                                            true, lang.selectHarmonizationType,
                                            model = model, names = chordsInstruments.map{ListaStrumenti.getNameByIndex(it)},
                                            anySequence = harmDatas,
                                        ) { harmonizationsCsv ->
                                            val ( _, csv2, csv3) = HarmonizationData.getHarmonizationsCsvValues(userOptions.harmonizations)
                                            model.updateUserOptions(
                                                "harmonizations",
                                                HarmonizationData.buildHarmonizationTriple(harmonizationsCsv, csv2, csv3)
                                            )
                                            harmonyDialogData.value =
                                                MultiNumberDialogData(model = model)
                                        }
                                    })
                            }
                            "Harmony II" -> {
                                var harmDatas = harmonizations.second
                                harmDatas = harmDatas.ifEmpty { listOf(HarmonizationData()) }
                                val isSelected = !(harmDatas.size == 1 && harmDatas[0].type == HarmonizationType.NONE)
                                SelectableCard(
                                    text = if(!isSelected) lang.harmony+ " II"
                                    else "${lang.harmony} II : \n\n${harmDatas.mapIndexed{i, hm ->
                                        "${i+1}: ${hm.describe()}"}.joinToString("\n\n")}",
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = isSelected,
                                    onClick = {
                                        harmonyDialogData.value = MultiNumberDialogData(
                                            true, lang.selectHarmonizationType,
                                            model = model, names = chordsInstruments.map{ListaStrumenti.getNameByIndex(it)},
                                            anySequence = harmDatas,
                                        ) { harmonizationsCsv ->
                                            val (csv1, _ , csv3) = HarmonizationData.getHarmonizationsCsvValues(userOptions.harmonizations)
                                            model.updateUserOptions(
                                                "harmonizations",
                                                HarmonizationData.buildHarmonizationTriple(csv1, harmonizationsCsv, csv3)
                                            )
                                            harmonyDialogData.value =
                                                MultiNumberDialogData(model = model)
                                        }
                                    })
                            }
                            "Harmony III" -> {
                                var harmDatas = harmonizations.third
                                harmDatas = harmDatas.ifEmpty { listOf(HarmonizationData()) }
                                val isSelected = !(harmDatas.size == 1 && harmDatas[0].type == HarmonizationType.NONE)
                                SelectableCard(
                                    text = if(!isSelected) lang.harmony+ " III"
                                    else "${lang.harmony} III : \n\n${harmDatas.mapIndexed{i, hm ->
                                        "${i+1}: ${hm.describe()}"}.joinToString("\n\n")}",
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = isSelected,
                                    onClick = {
                                        harmonyDialogData.value = MultiNumberDialogData(
                                            true, lang.selectHarmonizationType,
                                            model = model, names = chordsInstruments.map{ListaStrumenti.getNameByIndex(it)},
                                            anySequence = harmDatas,
                                        ) { harmonizationsCsv ->
                                            val (csv1, csv2, _ ) = HarmonizationData.getHarmonizationsCsvValues(userOptions.harmonizations)
                                            model.updateUserOptions(
                                                "harmonizations",
                                                HarmonizationData.buildHarmonizationTriple(csv1, csv2, harmonizationsCsv)
                                            )
                                            harmonyDialogData.value =
                                                MultiNumberDialogData(model = model)
                                        }
                                    })
                            }


                        }
                    }
                }
            }
            ScaffoldTabs.CHECK_N_REPLACE -> {
                LazyColumn(modifier = tabModifier, state = listState)
                {
                    items(optionNames) { optionName ->
                        val fontSize = dimensions.optionsFontSize
                        when (optionName) {
                            "Chords to Enhance" -> {
                                val absPitchNames = (0..11).map{Clip.convertAbsToClipText(it, lang.noteNames)}
                                var cteDatas = userOptions.chordsToEnhance.extractIntTriplesFromCsv()
                                    .map{ ChordToEnhanceData(convertFlagsToInts(it.first), it.second, it.third == 1)}
                                cteDatas = cteDatas.ifEmpty { listOf(ChordToEnhanceData(setOf(),1)) }
                                val isSelected = !cteDatas.all{it == ChordToEnhanceData(setOf(),1) || it == ChordToEnhanceData(setOf(),1, true) }
                                SelectableCard(
                                    text = if(!isSelected) lang.chordsToEnhance
                                    else "${lang.chordsToEnhance}: \n${
                                        cteDatas.joinToString("\n") {
                                            it.describe(absPitchNames)
                                        }}",
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = isSelected,
                                    onClick = {
                                        chordsToEnhanceDialogData.value = MultiNumberDialogData(
                                            true, lang.selectChordsToEnhance,
                                            model = model, names = absPitchNames,
                                            anySequence = cteDatas,
                                        ) { cteCsv ->
                                            model.updateUserOptions(
                                                "chordsToEnhance",
                                                cteCsv
                                            )
                                            chordsToEnhanceDialogData.value =
                                                MultiNumberDialogData(model = model)
                                        }
                                    })
                            }
                            "Enhance in Transpositions" -> {
                                var isOn = userOptions.enhanceChordsInTranspositions.toInt() != 0
                                SelectableCard(
                                    text = lang.enhanceChordsInTranspositions,
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = isOn,
                                    onClick = {
                                        isOn = !isOn
                                        model.updateUserOptions(
                                            "enhanceChordsInTranspositions",
                                            if (isOn) 1.toString() else 0.toString()
                                        )
                                    })
                            }
                            "Check and Replace" -> {
                                val optCnrMultiDatas = userOptions.checkAndReplace
                                val cnrMultiDatas = CheckAndReplaceData.createMultiCheckAndReplaceDatasFromCsv(optCnrMultiDatas).toMutableList()
                                val numerals = listOf("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X")
                                numerals.forEachIndexed{ index, num ->
                                    var cnrDatas = cnrMultiDatas.getOrElse(index) {listOf()}
                                    cnrDatas = cnrDatas.ifEmpty { listOf(CheckAndReplaceData()) }
                                    val isSelected = !(cnrDatas.size == 1 && cnrDatas[0].check is CheckType.None)
                                    SelectableCard(
                                        text = if(!isSelected) lang.checkAndReplace + " $num"
                                        else "${lang.checkAndReplace} $num: \n${cnrDatas.mapIndexed{i, cnr ->
                                            "${i+1}: ${cnr.describe(getGlissandoSymbols().first, lang.noteNames)}"}.joinToString("\n")}",
                                        fontSize = fontSize,
                                        colors = colors,
                                        isSelected = isSelected,
                                        onClick = {
                                            checkAndReplaceDialogData.value = MultiNumberDialogData(
                                                true, lang.selectCheckType,
                                                model = model, names = chordsInstruments.map{ListaStrumenti.getNameByIndex(it)},
                                                anySequence = cnrDatas,
                                            ) { cnrCsv ->
                                                model.updateUserOptions(
                                                    "checkAndReplace",
                                                    insertInMultiCheckAndReplaceCsv(index, cnrCsv, optCnrMultiDatas)
                                                )
                                                checkAndReplaceDialogData.value =
                                                    MultiNumberDialogData(model = model)
                                            }
                                        })
                                }
                            }

                        }
                    }
                }
            }
            ScaffoldTabs.DRUMS -> {
                LazyColumn(modifier = tabModifier, state = listState)
                {
                    items(optionNames) { optionName ->
                        val fontSize = dimensions.optionsFontSize
                        when (optionName) {
                            "Drums" -> {
                                var drumsDatas =
                                    DrumsData.createDrumsDatasFromCsv(userOptions.drums)
                                drumsDatas = drumsDatas.ifEmpty { listOf(DrumsData()) }
                                val isSelected =
                                    !(drumsDatas.size == 1 && drumsDatas[0].type == DrumsType.NONE)
                                SelectableCard(
                                    text = if (!isSelected) lang.drums
                                    else "${lang.drums}: \n\n${
                                        drumsDatas.mapIndexed { i, dd ->
                                            "${i + 1}: ${dd.describe()}"
                                        }.joinToString("\n\n")
                                    }",
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = isSelected,
                                    onClick = {
                                        drumsDialogData.value = MultiNumberDialogData(
                                            true,
                                            lang.selectDrumsType,
                                            model = model,
                                            names = DrumKits.values().map { it.title },
                                            anySequence = drumsDatas,
                                        ) { drumsCsv ->
                                            model.updateUserOptions(
                                                "drums",
                                                drumsCsv
                                            )
                                            drumsDialogData.value =
                                                MultiNumberDialogData(model = model)
                                        }
                                    })
                            }
                        }
                    }
                }
            }
            ScaffoldTabs.IO -> {
                LazyColumn(modifier = tabModifier, state = listState)
                {
                    items(optionNames) { optionName ->
                        val fontSize = dimensions.optionsFontSize
                        when (optionName) {
                            "Clear Slots" -> {
                                val timestamps =
                                    if (allCounterpointsData.isNotEmpty()) allCounterpointsData.map {
                                        //if (it == null || it?.timestamp == null || it.timestamp == -1L) ""
                                        if (it.timestamp == -1L) ""
                                        else it.timestamp.toString()
                                    } else List(16) { "" }
                                val timestampsToDates =
                                    convertToLocaleDate(timestamps, model.getUserLangDef())
                                val names =
                                    (0..15).map { "${lang.slotNumbers[it]}: ${timestampsToDates[it]}" }
                                SelectableCard(
                                    text = lang.clearSlots,
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = true,
                                    onClick = {
                                        doublingDialogData.value = MultiListDialogData(
                                            true, names, setOf(), lang.selectSlots
                                        ) { indexes, _ ->
                                            model.clearCounterpointsInDb(indexes.toSortedSet())
                                            model.retrieveCounterpointsFromDB()
                                            doublingDialogData.value =
                                                MultiListDialogData(itemList = doublingDialogData.value.itemList)
                                        }
                                    })
                            }
                            "Spacer" -> {
                                Spacer(modifier = Modifier.height(spacerHeight.dp))
                            }
                            "Export MIDI" -> {
                                val (nNotes, timestamp) = userOptions.lastPlayData.extractLongPairsFromCsv()[0]
                                val isOn = timestamp > -1
                                val text = if (isOn) ": $nNotes♪\n${
                                    convertToLocaleDate(
                                        listOf(timestamp.toString()),
                                        model.getUserLangDef()
                                    )[0]
                                }"
                                else ""
                                SelectableCard(text = lang.exportMidi + text,
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = isOn,
                                    onClick = {
                                        if(isOn) model.exportMidi(model.midiPath)
                                    })
                            }
                        }
                    }
                }
            }
            ScaffoldTabs.SETTINGS -> {
                LazyColumn( modifier = tabModifier,state = listState)
                {
                    items(optionNames) { optionName ->
                        val fontSize = dimensions.optionsFontSize
                        when (optionName) {
                            "Spread where possible" -> {
                                var isOn = userOptions.spread.toInt() != 0
                                SelectableCard(
                                    text = lang.spreadWherePossible,
                                    colors = colors,
                                    fontSize = fontSize,
                                    isSelected = isOn,
                                    onClick = {
                                        isOn = !isOn
                                        val newSpread = if (isOn) 1 else 0
                                        model.spread = newSpread
                                        model.updateUserOptions(
                                            "spread",
                                            newSpread.toString()
                                        )
                                        model.refreshComputation(false)
                                    })
                            }
                            "Deep Search in 4 part MK" -> {
                                var isOn = userOptions.deepSearch.toInt() != 0
                                SelectableCard(
                                    text = lang.deepSearch,
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = isOn,
                                    onClick = {
                                        isOn = !isOn
                                        model.updateUserOptions(
                                            "deepSearch",
                                            if (isOn) "1" else "0"
                                        )
                                    })
                            }
                            "Detector" -> {
                                val flags = userOptions.detectorFlags
                                val intsFromFlags = convertFlagsToInts(flags)
                                val isOn = flags != 0
                                val intervalNames = lang.intervalSet.map { it.replace("\n", " / ") }
                                val nl = newLineOrNot(intsFromFlags, 3)
                                val text = if (!isOn) lang.detector else "${lang.detector}: $nl${
                                    intsFromFlags.joinToString(
                                        separator = ", "
                                    ) { intervalNames[it] }
                                }"
                                SelectableCard(
                                    text = text,
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = isOn,
                                    onClick = {
                                        detectorDialogData.value = MultiListDialogData(
                                            true,
                                            intervalNames,
                                            intsFromFlags.toSet(),
                                            dialogTitle = lang.selectIntervalsToDetect
                                        ) { indexes, _ ->
                                            model.updateUserOptions(
                                                "detectorFlags",
                                                convertIntsToFlags(indexes.toSortedSet())
                                            )
                                            detectorDialogData.value =
                                                MultiListDialogData(itemList = detectorDialogData.value.itemList)
                                        }
                                    })
                            }
                            "Detector Extension" -> {
                                val extensions: List<String> = (1..16).map { it.toString() }
                                val extIndex = userOptions.detectorExtension
                                val isOn = userOptions.detectorFlags != 0
                                SelectableCard(
                                    text = "${lang.detectorExtension}: ${extensions[extIndex - 1]}",
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = isOn,
                                    onClick = {
                                        if (isOn) {
                                            detExtensionDialogData.value = ListDialogData(
                                                true,
                                                extensions,
                                                extIndex - 1,
                                                lang.selectDetectorExtension
                                            ) { index ->
                                                model.updateUserOptions(
                                                    "detectorExtension",
                                                    index + 1
                                                )
                                                detExtensionDialogData.value =
                                                    ListDialogData(itemList = detExtensionDialogData.value.itemList)
                                            }
                                        }

                                    })
                            }

                            "Colors" -> {
                                val colorDefs = extractColorDefs(userOptions.colors)
                                val appColors = AppColorThemes.values().map { it.title }
                                val appColorsName =
                                    if (colorDefs.app == "System") model.getSystemAppColorsName() else colorDefs.app
                                val colorsIndex = appColors.indexOf(appColorsName)
                                SelectableCard(
                                    text = "App Colors: $appColorsName",
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = !colorDefs.isCustom,
                                    onClick = {
                                        colorsDialogData.value = ListDialogData(
                                            true,
                                            appColors,
                                            colorsIndex,
                                            "Choose a Color Set"
                                        ) { index ->
                                            model.updateUserOptions(
                                                "colors",
                                                "${AppColorThemes.values()[index].title}||${colorDefs.custom}"
                                            )
                                            colorsDialogData.value =
                                                ListDialogData(itemList = colorsDialogData.value.itemList)
                                        }
                                    })
                            }
                            "Custom Colors" -> {
                                val colorDefs = extractColorDefs(userOptions.colors)
                                SelectableCard(
                                    text = "${lang.customColors}: ${colorDefs.custom}",
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = colorDefs.isCustom,
                                    onClick = {
                                        customColorsDialogData.value = CustomColorsDialogData(
                                            true, "Create a Color Set", colorDefs.custom, model
                                        ) { colorIndex ->
                                            model.updateUserOptions(
                                                "colors",
                                                "$colorIndex||${colorDefs.app}"
                                            )
                                            customColorsDialogData.value =
                                                CustomColorsDialogData(model = model)
                                        }
                                    })
                            }
                            "Counterpoint View" -> {
                                val counterpointViewOptions: List<String> = lang.counterpointViewOptions
                                val counterpointViewIndex = userOptions.counterpointView
                                SelectableCard(
                                    text = "${lang.counterpointView}: ${counterpointViewOptions[counterpointViewIndex]}",
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = true,
                                    onClick = {
                                        listDialogData.value = ListDialogData(
                                            true, counterpointViewOptions, counterpointViewIndex, lang.selectCounterpointView
                                        ) { index ->
                                            model.updateUserOptions(
                                                "counterpointView",
                                                index
                                            )
                                            listDialogData.value =
                                                ListDialogData(itemList = listDialogData.value.itemList)
                                        }
                                    })
                            }
                            "Language" -> {
                                val languages = LANGUAGES.values().map { it.language }
                                val langDef: String =
                                    if (userOptions.language == "System") model.getSystemLangDef() else userOptions.language
                                val languageName = LANGUAGES.languageNameFromDef(langDef)
                                val languageIndex = languages.indexOf(languageName)
                                SelectableCard(
                                    text = "${lang.language}: $languageName",
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = true,
                                    onClick = {
                                        listDialogData.value = ListDialogData(
                                            true, languages, languageIndex, "Select a Language!"
                                        ) { index ->
                                            model.updateUserOptions(
                                                "language",
                                                LANGUAGES.values()[index].toString()
                                            )
                                            listDialogData.value =
                                                ListDialogData(itemList = listDialogData.value.itemList)
                                        }
                                    })
                            }
                            "Zodiac" -> {
                                val flags = userOptions.zodiacFlags
                                val intsFromFlags = convertFlagsToInts(flags)
                                val isOn = flags > 0
                                val nl = newLineOrNot(intsFromFlags, 3)
                                val text = if (!isOn) lang.zodiac else "${lang.zodiac}: $nl${
                                    intsFromFlags.joinToString(
                                        separator = ", "
                                    ) { lang.zodiacOptions[it] }
                                }"
                                SelectableCard(
                                    text = text,
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = isOn,
                                    onClick = {
                                        zodiacDialogData.value = MultiListDialogData(
                                            true,
                                            lang.zodiacOptions,
                                            intsFromFlags.toSet(),
                                            lang.selectZodiac
                                        ) { indexes, _  ->
                                            val actualIndexes = if (indexes.contains(2)) listOf(
                                                indexes[0],
                                                1,
                                                2
                                            ) else indexes
                                            model.updateUserOptions(
                                                "zodiacFlags",
                                                convertIntsToFlags(actualIndexes.toSortedSet())
                                            )
                                            zodiacDialogData.value =
                                                MultiListDialogData(itemList = zodiacDialogData.value.itemList)
                                        }
                                    })
                            }
                            "MBTI" -> {
                                val mbtis = MBTI.listFromIntervals(verticalIntervals.toSortedSet())
                                val isOn = mbtis.isNotEmpty()
                                val nl = newLineOrNot(mbtis, 4)
                                val text = if (!isOn) lang.mbti else "${lang.mbti}: $nl${
                                    mbtis.joinToString(
                                        separator = " + "
                                    ) { it.name }
                                }"
                                val intsFromMbtis = MBTI.values().withIndex()
                                    .filter{mbtis.contains(it.value)}
                                    .map{ it.index}
                                SelectableCard(
                                    text = text,
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = isOn,
                                    onClick = {
                                        mbtiDialogData.value = MultiListDialogData(
                                            true,
                                            MBTI.values().map{"${it}    ${it.stringOfPlanets(getZodiacPlanets(model.zodiacFlags.value!!.third))}\n  ${it.character}"},
                                            intsFromMbtis.toSortedSet(),
                                            dialogTitle = lang.selectMbti,
                                            affectsHorizontallySymbol,
                                            model.MBTIaffectsHorizontally,
                                        ) { indices, affectsHorizontally ->
                                            if(indices.isNotEmpty()) {
                                                val intervals = MBTI.intervalsFromIndices(indices).toList()

                                                if(affectsHorizontally) {
                                                    model.MBTIaffectsHorizontally = true
                                                    model.createAndSaveAllIntervals(intervals)
                                                } else {
                                                    model.MBTIaffectsHorizontally = false
                                                    model.createVerticalIntervalSet(intervals, "AppScaffold")
                                                    model.saveVerticalIntervalSet("AppScaffold")
                                                    model.dispatchIntervals()
                                                }

                                            }
                                            mbtiDialogData.value =
                                                MultiListDialogData(itemList = detectorDialogData.value.itemList)

                                        }
                                    })
                            }
                            "Spacer" -> {
                                Spacer(modifier = Modifier.height(spacerHeight.dp))
                            }
                            "Privacy Policy" -> {
                                SelectableCard(
                                    text = lang.privacyPolicy,
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = true,
                                    onClick = {
                                        privacyDialogData.value = TextDialogData(
                                            true, "",
                                        ) {
                                            model.updateUserOptions("privacy", 1)
                                            privacyDialogData.value = TextDialogData()
                                        }
                                    }
                                )
                            }
                            "Credits" -> {
                                SelectableCard(
                                    text = lang.creditsAndLicenses,
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = true,
                                    onClick = {
                                        creditsDialogData.value = TextDialogData(
                                            true, lang.creditsAndLicenses,
                                        ) {
                                            creditsDialogData.value = TextDialogData()
                                        }
                                    }
                                )
                            }
                            "Historia" -> {
                                SelectableCard(
                                    text = lang.historia,
                                    fontSize = fontSize,
                                    colors = colors,
                                    isSelected = true,
                                    onClick = {
                                        historiaDialogData.value = TextDialogData(
                                            true, "",
                                        ) {
                                            historiaDialogData.value = TextDialogData()
                                        }
                                    }
                                )
                            }

                        }
                    }
                }
            }
        }
    }








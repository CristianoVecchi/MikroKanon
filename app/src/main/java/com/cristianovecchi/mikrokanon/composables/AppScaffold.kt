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
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.*
import com.cristianovecchi.mikrokanon.AIMUSIC.RhythmPatterns
import com.cristianovecchi.mikrokanon.composables.dialogs.*
import com.cristianovecchi.mikrokanon.db.CounterpointData
import com.cristianovecchi.mikrokanon.db.UserOptionsData
import com.cristianovecchi.mikrokanon.locale.*
import com.cristianovecchi.mikrokanon.ui.AppColorThemes
import com.cristianovecchi.mikrokanon.ui.extractColorDefs
import com.cristianovecchi.mikrokanon.ui.pxToSp
import com.cristianovecchi.mikrokanon.ui.shift
import kotlinx.coroutines.flow.Flow
import kotlin.math.absoluteValue

@Composable
fun AppScaffold(model: AppViewModel,
                userOptionsDataFlow: Flow<List<UserOptionsData>>,
                counterpointsDataFlow: Flow<List<CounterpointData>>,
                content: @Composable () -> Unit) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val userOptionsData = model.userOptionsData.observeAsState(initial = listOf()).value // to force recomposing when options change
    val colors = model.appColors
    val dimensions = model.dimensions
    val titleStyle = SpanStyle(
        fontSize = dimensions.titleTextSize.first.sp,
        color = colors.cellTextColorSelected)
    val creditStyle = SpanStyle(
        fontSize = dimensions.titleTextSize.second.sp,
        color = colors.cellTextColorUnselected)

    Scaffold(
        //modifier = Modifier
            //.background(colors.selCardBorderColorSelected),
            //.border(1.dp, Color.Transparent),
        scaffoldState = scaffoldState,
        drawerContent = { SettingsDrawer(model, userOptionsDataFlow, counterpointsDataFlow)},
        topBar = {
            val creditsDialogData by lazy { mutableStateOf(CreditsDialogData())}
            CreditsDialog(creditsDialogData, dimensions)
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
                        modifier = Modifier.size(dimensions.selectorButtonSize),
                        onClick = {scope.launch { scaffoldState.drawerState.open() }  }
                    ) {
                        Icon(
                            Icons.Filled.Menu, "",
                            modifier = Modifier.size(dimensions.selectorButtonSize/2),
                            tint = colors.cellTextColorSelected)
                    }
                    ClickableText(text = buildAnnotatedString {
                        withStyle(titleStyle){
                            append("MikroKanon\n")
                        }
                        withStyle(creditStyle) {
                            append("by Cristiano Vecchi")
                        }
                    },onClick = {
                        creditsDialogData.value = CreditsDialogData(true, "Credits:",
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


@Composable
fun SettingsDrawer(model: AppViewModel,
                   userOptionsDataFlow: Flow<List<UserOptionsData>>,
                   counterpointsDataFlow: Flow<List<CounterpointData>>){

    val listDialogData by lazy { mutableStateOf(ListDialogData())}
    val ensemblesDialogData by lazy { mutableStateOf(MultiListDialogData())}
    //val nuancesDialogData by lazy { mutableStateOf(ListDialogData())}
    //val bpmDialogData by lazy { mutableStateOf(NumberDialogData())}
    val rhythmDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val multiBpmDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val melodyTypesDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val rangeTypesDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val legatoTypesDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val multiFloatDialogData by lazy { mutableStateOf(MultiFloatDialogData(model = model))}
    val transposeDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val rowFormsDialogData by lazy { mutableStateOf(MultiNumberDialogData(model = model))}
    val doublingDialogData by lazy { mutableStateOf(MultiListDialogData())}
    val audio8DDialogData by lazy { mutableStateOf(MultiListDialogData())}
    val clearSlotsDialogData by lazy { mutableStateOf(MultiListDialogData())}
    val glissandoDialogData by lazy { mutableStateOf(MultiListDialogData())}
    val exportDialogData by lazy { mutableStateOf(ExportDialogData())}
    val creditsDialogData by lazy { mutableStateOf(CreditsDialogData())}
    //val intervalSetDialogData by lazy { mutableStateOf(MultiListDialogData())}
    val mbtiDialogData by lazy { mutableStateOf(MultiListDialogData())}
    val detectorDialogData by lazy { mutableStateOf(MultiListDialogData())}
    val detExtensionDialogData by lazy { mutableStateOf(ListDialogData())}
    val colorsDialogData by lazy { mutableStateOf(ListDialogData())}
    val customColorsDialogData by lazy { mutableStateOf(CustomColorsDialogData(model = model))}
    val ritornelloDialogData by lazy {mutableStateOf(ListDialogData())}
    val vibratoDialogData by lazy {mutableStateOf(ListDialogData())}
    val zodiacDialogData by lazy{ mutableStateOf(MultiListDialogData())}

    val dimensions = model.dimensions
    val colors = model.appColors
    val optionNames= listOf("Ensemble", "Glissando","Vibrato","Nuances",
        "Rhythm",  "Rhythm Shuffle", "Parts Shuffle","Doubling","8D AUDIO",

        "Spread where possible", "Deep Search in 4 part MK",
        "BPM", "Dynamics",
        "Range","Melody","Articulation",
        "Spacer",
        "Ritornello", "Transpose", "Row Forms",
        "Spacer",
        "Export MIDI",

        "Clear Slots", "Detector","Detector Extension",
        //"Colors",
        "Custom Colors","Language","Zodiac","MBTI","Spacer","Credits")
    //val userOptionsData by model.userOptionsData.asFlow().collectAsState(initial = listOf())
    val userOptionsData by userOptionsDataFlow.collectAsState(initial = listOf())
    val allCounterpointsData by counterpointsDataFlow.collectAsState(initial = listOf())
    //val userOptionsData = model.userOptionsData.observeAsState(initial = listOf()).value // to force recomposing when options change
    val lang = Lang.provideLanguage(model.getUserLangDef())
    val userOptions = if(userOptionsData.isEmpty()) UserOptionsData.getDefaultUserOptionsData()
                        else userOptionsData[0]
    val listState = rememberLazyListState()
    var selectedTab by remember{ mutableStateOf(model.lastScaffoldTab)}
    val verticalIntervals by model.intervalSet.observeAsState(listOf(-1))
    val ensNames: List<String> = lang.ensembleNames + synthsNames
    MultiListDialog(ensemblesDialogData, dimensions, lang.OKbutton)
    ListDialog(listDialogData, dimensions, lang.OKbutton)
    RhythmDialog(rhythmDialogData, dimensions, patterns = RhythmPatterns.values().toList() )
    MultiListDialog(doublingDialogData, dimensions, lang.OKbutton)
    MultiListDialog(audio8DDialogData, dimensions, lang.OKbutton)
    val timestamps = if(allCounterpointsData.isNotEmpty()) allCounterpointsData.map{
        if(it == null || it?.timestamp == null || it.timestamp == -1L) ""
        else it.timestamp.toString()} else List(16){""}
    MultiListDialog(clearSlotsDialogData, dimensions, lang.OKbutton)
    //BpmDialog(bpmDialogData, lang.OKbutton)
    val intervalsForGlissando = createGlissandoIntervals(lang.doublingNames)

    MelodyTypeDialog(melodyTypesDialogData,dimensions, lang.melodyOptions)
    RangeTypeDialog(rangeTypesDialogData, dimensions, lang.rangeOptions)
    LegatoTypeDialog(legatoTypesDialogData, dimensions, lang.articulationOptions)
    MultiBpmDialog(multiBpmDialogData, dimensions, lang.OKbutton)
    MultiDynamicDialog(multiFloatDialogData, dimensions, lang.OKbutton)
    //val intervalsForTranspose = listOf("U","2m","2M","3m","3M","4","4A","5","6m","6M","7m","7M")
    val intervalsForTranspose = getIntervalsForTranspose(lang.intervalSet)
    TransposeDialog(transposeDialogData, dimensions,
        intervalsForTranspose, lang.OKbutton)
    val formsNames = listOf("unrelated", lang.original, lang.inverse, lang.retrograde, lang.invRetrograde)
    RowFormsDialog(rowFormsDialogData, dimensions, formsNames, lang.slotNumbers)
    ExportDialog(exportDialogData, lang.OKbutton)
    CreditsDialog(creditsDialogData, dimensions, lang.OKbutton)
    //MultiListDialog(intervalSetDialogData, dimensions.sequenceDialogFontSize, lang.OKbutton)
    MultiListDialog(mbtiDialogData, dimensions, lang.OKbutton)
    MultiListDialog(detectorDialogData, dimensions, lang.OKbutton)
    ListDialog(detExtensionDialogData, dimensions, lang.OKbutton, fillPrevious = true)
    ListDialog(colorsDialogData, dimensions, lang.OKbutton)
    MultiListDialog(zodiacDialogData, dimensions, lang.OKbutton)
    CustomColorsDialog(customColorsDialogData, dimensions, lang.OKbutton)
    ListDialog(ritornelloDialogData, dimensions, lang.OKbutton, fillPrevious = true)
    ListDialog(vibratoDialogData, dimensions, lang.OKbutton,  fillPrevious = true)

//    userOptionsData.forEach{
//        Text("#${it.id} = ens_type: ${it.ensembleType} - bpm: ${it.bpm} ")
//    }
Row(Modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
    val buttonSize = dimensions.selectorButtonSize
    val iconSize = dimensions.selectorButtonSize / 2
    Column(Modifier
        .weight(1f)
        .background(
            if (selectedTab == ScaffoldTabs.SOUND) colors.drawerBackgroundColor
            else colors.drawerBackgroundColor.shift(-0.2f)
        )
        .clickable(onClick = {
            selectedTab = ScaffoldTabs.SOUND; model.lastScaffoldTab = ScaffoldTabs.SOUND
        }), horizontalAlignment = Alignment.CenterHorizontally){
        IconButton(modifier = Modifier
            .background(
                if (selectedTab == ScaffoldTabs.SOUND) colors.drawerBackgroundColor else colors.drawerBackgroundColor.shift(
                    -0.2f
                ), RoundedCornerShape(4.dp)
            )
            .then(Modifier.size(buttonSize / 4 * 3)), onClick = { selectedTab = ScaffoldTabs.SOUND; model.lastScaffoldTab = ScaffoldTabs.SOUND }
        )
        {
            Icon(
                modifier = Modifier.size(iconSize),
                painter = painterResource(id = model.iconMap["sound"]!!),
                contentDescription = null, // decorative element
                tint = if(selectedTab == ScaffoldTabs.SOUND) colors.selCardTextColorSelected else colors.selCardTextColorUnselected.shift(-0.2f)
            )
        }
    }
    Column(Modifier
        .weight(1f)
        .background(
            if (selectedTab == ScaffoldTabs.BUILDING) colors.drawerBackgroundColor
            else colors.drawerBackgroundColor.shift(-0.2f)
        )
        .clickable(onClick = {
            selectedTab = ScaffoldTabs.BUILDING; model.lastScaffoldTab = ScaffoldTabs.BUILDING
        }), horizontalAlignment = Alignment.CenterHorizontally){
        IconButton(modifier = Modifier
            .background(
                if (selectedTab == ScaffoldTabs.BUILDING) colors.drawerBackgroundColor else colors.drawerBackgroundColor.shift(
                    -0.2f
                ), RoundedCornerShape(4.dp)
            )
            .then(Modifier.size(buttonSize / 4 * 3)), onClick = { selectedTab = ScaffoldTabs.BUILDING ; model.lastScaffoldTab = ScaffoldTabs.BUILDING  }
        )
        {
            Icon(
                modifier = Modifier.size(iconSize),
                painter = painterResource(id = model.iconMap["building"]!!),
                contentDescription = null, // decorative element
                tint = if(selectedTab == ScaffoldTabs.BUILDING) colors.selCardTextColorSelected else colors.selCardTextColorUnselected.shift(-0.2f)
            )
        }
    }
    Column(
        Modifier
            .weight(1f)
            .background(
                if (selectedTab == ScaffoldTabs.SETTINGS) colors.drawerBackgroundColor
                else colors.drawerBackgroundColor.shift(-0.2f)
            )
            .clickable(onClick = {
                selectedTab = ScaffoldTabs.SETTINGS; model.lastScaffoldTab = ScaffoldTabs.SETTINGS
            }), horizontalAlignment = Alignment.CenterHorizontally,
        )
           {
        IconButton(modifier = Modifier
            .background(
                if (selectedTab == ScaffoldTabs.SETTINGS) colors.drawerBackgroundColor else colors.drawerBackgroundColor.shift(
                    -0.2f
                ), RoundedCornerShape(4.dp)
            )
            .then(Modifier.size(buttonSize / 4 * 3)), onClick = { selectedTab = ScaffoldTabs.SETTINGS; model.lastScaffoldTab = ScaffoldTabs.SETTINGS }
        )
        {
            Icon(
                modifier = Modifier.size(iconSize),
                painter = painterResource(id = model.iconMap["settings"]!!),
                contentDescription = null, // decorative element
                tint = if(selectedTab == ScaffoldTabs.SETTINGS) colors.selCardTextColorSelected else colors.selCardTextColorUnselected.shift(-0.2f)
            )
        }
    }

}
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
                        val ensIndexes = userOptions.ensembleTypes.extractIntsFromCsv()
                        SelectableCard(
                            text = "${lang.ensemble}: ${ensIndexes.joinToString(" + ") { ensNames[it] }}",
                            fontSize = fontSize,
                            colors = colors,
                            isSelected = true,
                            onClick = {
                                ensemblesDialogData.value = MultiListDialogData(
                                    true, ensNames, ensIndexes.toSet(), lang.selectEnsemble
                                ) { indexes ->
                                    model.updateUserOptions(
                                        "ensembleTypes",
                                        if(indexes.isEmpty()) "2" else indexes.joinToString(",")
                                    )
                                    ensemblesDialogData.value =
                                        MultiListDialogData(itemList = ensemblesDialogData.value.itemList)
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
                    "Vibrato" -> {
                        val intensity = userOptions.vibrato
                        val sym = getVibratoSymbol()
                        val timeIndices: List<String> = (0 until 9).map { if(it == 0) "-" else sym.repeat(it) }
                        val isOn = intensity != 0
                        val text = if (intensity == 0) lang.vibrato else "${lang.vibrato}: ${sym.repeat(intensity)}"
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
                                        index
                                    )
                                    ritornelloDialogData.value =
                                        ListDialogData(itemList = vibratoDialogData.value.itemList)
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
                    "Rhythm" -> {
                        val rhythmNames = RhythmPatterns.getTitles()
                        val rhythmPatterns = userOptions.rhythm.extractIntPairsFromCsv()
                        val rhythmTexts = rhythmPatterns.map{
                            val arrow = if(it.first<0) "←" else ""
                            val feature = if(it.second>1 ) " (${it.second}x)" else ""
                            arrow + rhythmNames[it.first.absoluteValue - 1] + feature
                        }
                        SelectableCard(
                            text = "${lang.rhythm}: ${rhythmTexts.joinToString(" + ")}",
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
                    "8D AUDIO" -> {
                        val flags = userOptions.audio8DFlags
                        val intsFromFlags = convertFlagsToInts(flags)
                        val isOn = flags > 0
                        val text = if (!isOn) lang.audio8D else "${lang.audio8D}: ${
                            intsFromFlags.joinToString(
                                separator = ", "
                            ) { (it+1).toString() }
                        }"
                        SelectableCard(
                            text = text,
                            fontSize = fontSize,
                            colors = colors,
                            isSelected = isOn,
                            onClick = { _ ->
                                doublingDialogData.value = MultiListDialogData(
                                    true, (1..12).map{it.toString()}, intsFromFlags.toSet(), lang.selectAudio8D
                                ) { indexes ->
                                    model.updateUserOptions(
                                        "audio8DFlags",
                                        convertIntsToFlags(indexes.toSortedSet())
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
                            val octaves = getOctaveSymbols()
                            SelectableCard(
                                text = "${lang.range}: ${
                                    rangeTypes.extractIntPairsFromCsv()
                                        .joinToString("  ") { "${rangeTypeMap[it.first]!!}${octaves[it.second + 2]}" }
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
                            SelectableCard(
                                text = "${lang.melody}: ${melodyTypes.extractIntsFromCsv().map{melodyTypeMap[it]}.joinToString("  ") }",
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
                            val ribattutos = getRibattutoSymbols()
                            SelectableCard(
                                text = "${lang.articulation}: ${
                                    legatoTypes.describeForArticulation(legatoTypeMap)
                                }",
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
                            SelectableCard(
                                text = "${lang.dynamics}: ${dynamics.describeForDynamic(model.dynamicMap, symbols[12], symbols[13])}",
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
                        "Row Forms" -> {
                            val formsCsv = userOptions.rowForms
                            val isOn = formsCsv != "1"
                            val formPairs = formsCsv.extractIntPairsFromCsv()
                            SelectableCard(
                                text = if(isOn)
                                    "${lang.rowForms}: ${
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
                                var error = model.onPlay(false, false)
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
        }
        ScaffoldTabs.SETTINGS -> {
            LazyColumn( modifier = tabModifier,state = listState)
            {
                items(optionNames) { optionName ->
                    val fontSize = dimensions.optionsFontSize
                    when (optionName) {
                        "Clear Slots" -> {
                            val names = (0..15).map{ "${lang.slotNumbers[it]}: ${timestamps[it]}"}
                            SelectableCard(
                                text = lang.clearSlots,
                                fontSize = fontSize,
                                colors = colors,
                                isSelected = true,
                                onClick = {
                                    doublingDialogData.value = MultiListDialogData(
                                        true, names, setOf(), lang.selectSlots
                                    ) { indexes ->
                                        model.clearCounterpointsInDb(indexes.toSortedSet())
                                        model.retrieveCounterpointsFromDB()
                                        doublingDialogData.value =
                                            MultiListDialogData(itemList = doublingDialogData.value.itemList)
                                    }
                                })
                        }
                        "Detector" -> {
                            val flags = userOptions.detectorFlags
                            val intsFromFlags = convertFlagsToInts(flags)
                            val isOn = flags != 0
                            val intervalNames = lang.intervalSet.map { it.replace("\n", " / ") }
                            val text = if (!isOn) lang.detector else "${lang.detector}: ${
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
                                    ) { indexes ->
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
                                        true,
                                        lang.zodiacOptions,
                                        intsFromFlags.toSet(),
                                        lang.selectZodiac
                                    ) { indexes ->
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
                            //model.saveVerticalIntervalSet()
                            val mbtis = MBTI.listFromIntervals(
                                if(verticalIntervals.contains(-1)) createIntervalSetFromFlags(userOptions.intSetVertFlags).toSet()
                                 else verticalIntervals.toSet()
                            )
                            val isOn = mbtis.isNotEmpty()
                            val text = if (!isOn) lang.mbti else "${lang.mbti}: ${
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
                                        MBTI.values().map{"${it}\n  ${it.character}"},
                                        intsFromMbtis.toSet(),
                                        dialogTitle = lang.selectMbti
                                    ) { indices ->
                                        model.createVerticalIntervalSet(MBTI.intervalsFromIndices(indices).toList(), "AppScaffold")
                                        model.saveVerticalIntervalSet("AppScaffold")
                                        mbtiDialogData.value =
                                            MultiListDialogData(itemList = detectorDialogData.value.itemList)

                                    }
                                })
                        }
                        "Spacer" -> {
                            Spacer(modifier = Modifier.height(spacerHeight.dp))
                        }
                        "Credits" -> {
                            SelectableCard(
                                text = lang.credits,
                                fontSize = fontSize,
                                colors = colors,
                                isSelected = true,
                                onClick = {
                                    creditsDialogData.value = CreditsDialogData(
                                        true, "Credits:",
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
}



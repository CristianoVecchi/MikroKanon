package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import com.cristianovecchi.mikrokanon.AIMUSIC.EnsembleType
import com.cristianovecchi.mikrokanon.AIMUSIC.Ensembles
import com.cristianovecchi.mikrokanon.AppViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.AIMUSIC.RhythmPatterns
import com.cristianovecchi.mikrokanon.AIMUSIC.RhythmPatterns.Companion.getTitles
import com.cristianovecchi.mikrokanon.dao.UserOptionsData
import com.cristianovecchi.mikrokanon.toStringAll
import java.io.File

@Composable
fun AppScaffold(model: AppViewModel, content: @Composable () -> Unit) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val titleStyle = SpanStyle(
        fontSize = 18.sp,
        color = Color.White)
    val creditStyle = SpanStyle(
        fontSize = 14.sp,
        color = Color.LightGray)
    val uriHandler = LocalUriHandler.current
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = { SettingsDrawer(model = model)},
        topBar = {
            TopAppBar() {
                Row(Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, ){
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
                        uriHandler.openUri(model.creditsUri)
                    })
                }
            }
        },
        content = {
            content()
        }
    )
}


data class ListDialogData(val dialogState: Boolean = false, val itemList: List<String> = listOf(), val selectedListDialogItem: Int = -1,
                          val dialogTitle: String = "", val onSubmitButtonClick: (Int) -> Unit = {} )
data class NumberDialogData(val dialogState: Boolean = false, val title:String = "", val value:Int = 0,
                            val min: Int = 0, val max: Int = 360, val onSubmitButtonClick: (Int) -> Unit = {})
data class ExportDialogData(val dialogState: Boolean = false, val title:String = "", val path:String = "",
                            val error:String = "", val onSubmitButtonClick: () -> Unit = {})
@Composable
fun SettingsDrawer(model: AppViewModel){



    val listDialogData by lazy { mutableStateOf(ListDialogData())}
    val bpmDialogData by lazy { mutableStateOf(NumberDialogData())}
    val exportDialogData by lazy { mutableStateOf(ExportDialogData())}

    ListDialog(listDialogData)
    BpmDialog(bpmDialogData)
    ExportDialog(exportDialogData)
    val optionNames= listOf<String>("Ensemble", "BPM", "Rhythm", "Export MIDI", "Rhythm Shuffle", "Parts Shuffle")
    val userOptionsData by model.userOptionsData.asFlow().collectAsState(initial = listOf())
    val userOptions = if(userOptionsData.isEmpty()) UserOptionsData(0,"0","90", "0","0","0")
                        else userOptionsData[0]
    val listState = rememberLazyListState()

//    userOptionsData.forEach{
//        Text("#${it.id} = ens_type: ${it.ensembleType} - bpm: ${it.bpm} ")
//    }

    LazyColumn(
        state = listState,
    ) { items(optionNames) { optionName ->
            val fontSize = 18
            when(optionName){
                "Ensemble" -> {
                    val ensNames: List<String> = EnsembleType.values().map{ it.toString()}
                    val ensIndex = Integer.parseInt(userOptions.ensembleType)
                        SelectableCard(text = "Ensemble: ${ensNames[ensIndex]}", fontSize = fontSize, isSelected = true, onClick = { _ ->
                            listDialogData.value = ListDialogData(true,ensNames,ensIndex,"Select an Ensemble!"
                            ) { index ->
                                model.updateUserOptions(
                                    "ensemble_type",
                                    index.toString()
                                )
                                listDialogData.value = ListDialogData(itemList = listDialogData.value.itemList)
                            }
                        })
                    }
                "BPM" -> {
                    val bpm = Integer.parseInt(userOptions.bpm)
                    SelectableCard(text = "BPM: $bpm", fontSize = fontSize, isSelected = true, onClick = { _ ->
                        bpmDialogData.value = NumberDialogData(
                            true, "Beats Per Measure:", bpm, 18, 360
                        ) { bpm ->
                            model.updateUserOptions(
                                "bpm",
                                bpm.toString()
                            )
                            listDialogData.value =
                                ListDialogData(itemList = listDialogData.value.itemList)
                        }
                    })
                }
                "Rhythm" -> {
                    val rhythmNames = RhythmPatterns.getTitles()
                    val rhythmIndex = Integer.parseInt(userOptions.rhythm)
                    SelectableCard(text = "Rhythm: ${rhythmNames[rhythmIndex]}", fontSize = fontSize, isSelected = true,onClick = { _ ->
                        listDialogData.value = ListDialogData(true,rhythmNames,rhythmIndex,"Select a Rhythm!"
                        ) { index ->
                            model.updateUserOptions(
                                "rhythm",
                                index.toString()
                            )
                            listDialogData.value = ListDialogData(itemList = listDialogData.value.itemList)
                        }
                    })
                }
                "Export MIDI" -> {
                    SelectableCard(text = "Export MIDI", fontSize = fontSize, isSelected = true, onClick = { _ ->
                        val path = model.midiPath.absolutePath.toString()
                        var error = model.onPlay(false)
                        if (error.isNotEmpty()){
                           // TODO sending to Google Drive
                        }

                        exportDialogData.value = ExportDialogData(true,"Exporting MIDI File:",
                            path = model.midiPath.absolutePath.toString(), error = error
                        ) {

                            exportDialogData.value = ExportDialogData(path = path, error = error)
                        }
                    })
                }
                "Rhythm Shuffle" -> {
                    var isOn = userOptions.rhythmShuffle != "0"
                    val onOff = if(isOn) "On" else "Off"
                    SelectableCard(text = "Rhythm Shuffle: $onOff", fontSize = fontSize, isSelected = isOn, onClick = { _ ->
                        isOn = !isOn
                        model.updateUserOptions(
                            "rhythmShuffle",
                            if(isOn) "1" else "0"
                        )

                    })
                }
                "Parts Shuffle" -> {
                    var isOn = userOptions.partsShuffle != "0"
                    val onOff = if(isOn) "On" else "Off"
                    SelectableCard(text = "Parts Shuffle: $onOff", fontSize = fontSize, isSelected = isOn, onClick = { _ ->
                        isOn = !isOn
                        model.updateUserOptions(
                            "partsShuffle",
                            if(isOn) "1" else "0"
                        )
                    })
                }
            }
        }
    }
}

enum class Rhythms {

}

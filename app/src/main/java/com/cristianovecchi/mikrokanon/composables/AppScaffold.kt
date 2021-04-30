package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.Modifier
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.toStringAll

@Composable
fun AppScaffold(model: AppViewModel, content: @Composable () -> Unit) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = { SettingsDrawer(model = model)},
        topBar = {
            TopAppBar() {
                IconButton(
                    onClick = {
                        scope.launch { scaffoldState.drawerState.open() }
                    }
                ) {
                    Icon(Icons.Filled.Menu,"")
                }
                Text("MikroKanon")
            }
        },
        content = {
            content()
        })
}

data class ListDialogData(val dialogState: Boolean = false, val itemList: List<String> = listOf(), val selectedListDialogItem: Int = -1,
                          val dialogTitle: String = "", val onSubmitButtonClick: (Int) -> Unit = {} )
@Composable
fun SettingsDrawer(model: AppViewModel){



    val listDialogData by lazy { mutableStateOf(ListDialogData())}

    ListDialog(listDialogData)
    val optionNames= listOf<String>("Ensemble", "BPM")
    val options by model.userOptions.observeAsState()
    val listState = rememberLazyListState()
    options!!.forEach{
        Text("{$it.key!!} = {$it.value!!}")
    }
    LazyColumn(state = listState,
        ) { items(optionNames) { optionName ->
            when(optionName){
                "Ensemble" -> {
                    val ensNames: List<String> = EnsembleType.values().map{ it.toString()}
                    val ensIndex = options!!.get("ensemble_type")?.let {
                        Integer.parseInt( it )
                    } ?: 0
                    Card(Modifier.clickable {
                        listDialogData.value = ListDialogData(true,ensNames,ensIndex,"Select an Ensemble!"
                        ) { index ->
                            model.updateUserOptions(
                                "ensemble_type",
                                index.toString()
                            )
                            listDialogData.value = ListDialogData(itemList = listDialogData.value.itemList)
                        }
                    }) {
                        Text(text = "Ensemble: ${ensNames[ensIndex]}")
                    }

                }
                "BPM" -> {

                }
                else -> {}
            }
        }
    }
}
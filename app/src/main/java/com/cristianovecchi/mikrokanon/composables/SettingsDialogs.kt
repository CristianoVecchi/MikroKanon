package com.cristianovecchi.mikrokanon.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.G
import com.cristianovecchi.mikrokanon.toDp
import com.cristianovecchi.mikrokanon.toHexString

@Composable
fun CreditsDialog(creditsDialogData: MutableState<CreditsDialogData>, okText: String = "OK",
                 onDismissRequest: () -> Unit = {creditsDialogData.value = CreditsDialogData()})
{
    if (creditsDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        val nameStyle = SpanStyle(
            fontSize = 22.sp,
            color = Color.Black)
        val commentStyle = SpanStyle(
            fontSize = 18.sp,
            color = Color.DarkGray)
        val uriStyle = SpanStyle(
            fontSize = 20.sp,
            color = Color.Blue)
        val uriHandler = LocalUriHandler.current
        val youtubeChannelUri = "https://www.youtube.com/channel/UCe9Kd87V90fbPsUBU5gaXKw/playlists?view=1&sort=dd&shelf_id=0"
        val youtubeMikroKanonExamplesUri = "https://www.youtube.com/watch?v=zaa3d3FVqA4&list=PLO0dKPP71phouGDmrOQA_yXEp0Z1L1PLV&index=2"
        val instagramUri = "https://www.instagram.com/cristiano.vecchi"
        val linkedinUri = "https://www.linkedin.com/in/cristiano-vecchi-ba1a311a"
        val githubUri = "https://github.com/CristianoVecchi"
        val githubLeffelManiaUri = "https://github.com/LeffelMania"
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier.width(300.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = creditsDialogData.value.title)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = buildAnnotatedString {
                        withStyle(commentStyle){
                            append("the MikroKanon App has been conceived and developed in 2021 by\n")
                        }
                        withStyle(nameStyle){
                            append("Cristiano Vecchi")
                        }
                    })
                    ClickableText(text = buildAnnotatedString {
                        withStyle(uriStyle){
                            append("Youtube Channel")
                        }
                    },onClick = {
                        uriHandler.openUri(youtubeChannelUri)
                    })
                    ClickableText(text = buildAnnotatedString {
                        withStyle(uriStyle){
                            append("Youtube MK examples")
                        }
                    },onClick = {
                        uriHandler.openUri(youtubeMikroKanonExamplesUri)
                    })
                    ClickableText(text = buildAnnotatedString {
                        withStyle(uriStyle){
                            append("Instagram")
                        }
                    },onClick = {
                        uriHandler.openUri(instagramUri)
                    })
                    ClickableText(text = buildAnnotatedString {
                        withStyle(uriStyle){
                            append("Linkedin")
                        }
                    },onClick = {
                        uriHandler.openUri(linkedinUri)
                    })
                    ClickableText(text = buildAnnotatedString {
                        withStyle(uriStyle){
                            append("GitHub")
                        }
                    },onClick = {
                        uriHandler.openUri(githubUri)
                    })
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = buildAnnotatedString {
                        withStyle(commentStyle){
                            append("this App uses the android-midi-lib library by\n")
                        }
                        withStyle(nameStyle){
                            append("Alex Leffelman")
                        }
                    })
                    ClickableText(text = buildAnnotatedString {
                        withStyle(uriStyle){
                            append("GitHub")
                        }
                    },onClick = {
                        uriHandler.openUri(githubLeffelManiaUri)
                    })
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            creditsDialogData.value.onSubmitButtonClick.invoke()
                            onDismissRequest.invoke()
                        },
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(text = okText)
                    }
                }
            }
        }
    }

}
@Composable
fun ExportDialog(exportDialogData: MutableState<ExportDialogData>, okText: String = "OK",
                 onDismissRequest: () -> Unit = { exportDialogData.value = ExportDialogData(path = exportDialogData.value.path, error = exportDialogData.value.error)})
{
    if (exportDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier.width(300.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = exportDialogData.value.title)
                    Spacer(modifier = Modifier.height(10.dp))

                   // var error by remember { mutableStateOf(exportDialogData.value.error) }
                    val fontSize = 26.sp
                    val fontWeight = FontWeight.Normal
                    Spacer(modifier = Modifier.height(10.dp))
                    if(exportDialogData.value.path.isNotEmpty()){
                        Text(text = "MIDI path: ${exportDialogData.value.path}", style = TextStyle(fontSize = 12.sp,fontWeight = FontWeight.Bold) )
                    }

                        if(exportDialogData.value.error.isNotEmpty()){
                            Text(text = "${exportDialogData.value.error}", style = TextStyle(fontSize = 23.sp,fontWeight = FontWeight.Bold) )
                        } else {
//                            Text(
//                                text = "Here you can find your MIDI!!!",
//                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
//                            )
                        }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            exportDialogData.value.onSubmitButtonClick.invoke()
                            onDismissRequest.invoke()
                        },
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(text = okText)
                    }
                }
            }
        }
    }

}
@Composable
fun CustomColorsDialog(customColorsDialogData: MutableState<CustomColorsDialogData>,okText: String = "OK",
                       onDismissRequest: () -> Unit = { customColorsDialogData.value = CustomColorsDialogData(model = customColorsDialogData.value.model)})
{
    if(customColorsDialogData.value.dialogState){
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier.width(300.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Spacer(modifier = Modifier.height(10.dp))
                    var firstRendering by remember{ mutableStateOf(true)}
                    val fontColor: Color
                    val back1Color: Color
                    val back2Color: Color
                    val beatColor: Color
                    val pass1Color: Color
                    val pass2Color: Color
                    val radarColor: Color
                    var indexColors = customColorsDialogData.value.arrayColorIndex
                    var customColor by remember{ mutableStateOf(Color.Black) }
                    val context = customColorsDialogData.value.model.getContext()
                    if(firstRendering){
                        if(G.loadColorArrays(context)) G.setColorArray(context,indexColors)
                            fontColor = Color(G.colorFont)
                            back1Color = Color(G.colorBackground1)
                            back2Color = Color(G.colorBackground2)
                            beatColor = Color(G.colorBeatNotes)
                            pass1Color = Color(G.colorPassageNotes1)
                            pass2Color = Color(G.colorPassageNotes2)
                            radarColor = Color(G.colorRadar)
                            indexColors = G.indexColorArray
                            firstRendering = false
                    } else {
                        if(G.loadColorArrays(context))
                            G.setColorArrayBySearch(context, customColor.toArgb())
                        fontColor = Color(G.colorFont)
                        back1Color = Color(G.colorBackground1)
                        back2Color = Color(G.colorBackground2)
                        beatColor = Color(G.colorBeatNotes)
                        pass1Color = Color(G.colorPassageNotes1)
                        pass2Color = Color(G.colorPassageNotes2)
                        radarColor = Color(G.colorRadar)
                        indexColors = G.indexColorArray
                    }
                    val h = 80.dp
                    var size by remember { mutableStateOf(IntSize.Zero) }
                    val w = if (size.width == 0) 0.dp else (size.width / 6).toDp().dp + 1.dp
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(h)
                            .onGloballyPositioned { coordinates ->
                                size = coordinates.size
                            }){
                        Row(
                            Modifier
                                .height(h)
                                .background(customColor), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Box(
                                Modifier
                                    .size(w, h)
                                    .background(radarColor))
                            Box(
                                Modifier
                                    .size(w, h)
                                    .background(beatColor))
                            Box(
                                Modifier
                                    .size(w, h)
                                    .background(back1Color))
                            Box(
                                Modifier
                                    .size(w, h)
                                    .background(back2Color))
                            Box(
                                Modifier
                                    .size(w, h)
                                    .background(pass1Color))
                            Box(
                                Modifier
                                    .size(w, h)
                                    .background(pass2Color))
                        }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(h),
                            horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                            Text( text = "$indexColors",
                                style = TextStyle(fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = fontColor) )
                        }
                    }

                    Row(Modifier.fillMaxWidth()) {
                        ColorSelector(height = 300.dp, startColor = back1Color) { color ->
                            customColor =  color.copy()
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            customColorsDialogData.value.onSubmitButtonClick.invoke(indexColors)
                            onDismissRequest.invoke()
                        },
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(text = okText)
                    }
                }
            }
        }
    }
}




@Composable
fun BpmDialog(numberDialogData: MutableState<NumberDialogData>, okText: String = "OK",
              onDismissRequest: () -> Unit = { numberDialogData.value = NumberDialogData(value = numberDialogData.value.value)})
{
    if (numberDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier.width(300.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = numberDialogData.value.title)
                    Spacer(modifier = Modifier.height(10.dp))

                    var bpm by remember { mutableStateOf(numberDialogData.value.value) }
                    val setBpm = { bpmToCheck: Int ->
                        val newBpm = bpmToCheck.coerceIn(numberDialogData.value.min, numberDialogData.value.max )
                        bpm = newBpm
                    }
                    val fontSize = 26.sp
                    val fontWeight = FontWeight.Normal
                    val buttonPadding = 4.dp
                    Spacer(modifier = Modifier.height(10.dp))
                    Row() {
                        Text(text = "$bpm", style = TextStyle(fontSize = 32.sp,fontWeight = FontWeight.Bold) )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    // 240 | 150 | 60
                    // +30 | +6 | +1
                    // -30 | -6 | -1
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column(
                            modifier = Modifier.width(IntrinsicSize.Max),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(modifier = Modifier
                                .fillMaxWidth()
                                .padding(buttonPadding), onClick = { setBpm(240) })
                            {
                                Text(
                                    text = "240",
                                    style = TextStyle(fontSize = fontSize, fontWeight = fontWeight)
                                )
                            }
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                onClick = { setBpm(bpm + 30) })
                            {
                                Text(
                                    text = "+30",
                                    style = TextStyle(fontSize = fontSize, fontWeight = fontWeight)
                                )
                            }
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(buttonPadding),
                                onClick = { setBpm(bpm - 30) })
                            {
                                Text(
                                    text = "-30",
                                    style = TextStyle(fontSize = fontSize, fontWeight = fontWeight)
                                )
                            }

                        }
                        Column(
                            modifier = Modifier.width(IntrinsicSize.Max),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(modifier = Modifier
                                .fillMaxWidth()
                                .padding(buttonPadding), onClick = { setBpm(150) })
                            {
                                Text(
                                    text = "150",
                                    style = TextStyle(fontSize = fontSize, fontWeight = fontWeight)
                                )
                            }
                            Button(modifier = Modifier
                                .fillMaxWidth()
                                .padding(buttonPadding), onClick = { setBpm(bpm + 6) })
                            {
                                Text(
                                    text = "+6",
                                    style = TextStyle(fontSize = fontSize, fontWeight = fontWeight)
                                )
                            }
                            Button(modifier = Modifier
                                .fillMaxWidth()
                                .padding(buttonPadding), onClick = { setBpm(bpm - 6) })
                            {
                                Text(
                                    text = "-6",
                                    style = TextStyle(fontSize = fontSize, fontWeight = fontWeight)
                                )
                            }

                        }
                        Column(
                            modifier = Modifier.width(IntrinsicSize.Max),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(modifier = Modifier
                                .fillMaxWidth()
                                .padding(buttonPadding), onClick = { setBpm(60) })
                            {
                                Text(
                                    text = "60",
                                    style = TextStyle(fontSize = fontSize, fontWeight = fontWeight)
                                )
                            }
                            Button(modifier = Modifier
                                .fillMaxWidth()
                                .padding(buttonPadding), onClick = { setBpm(bpm + 1) })
                            {
                                Text(
                                    text = "+1",
                                    style = TextStyle(fontSize = fontSize, fontWeight = fontWeight)
                                )
                            }
                            Button(modifier = Modifier
                                .fillMaxWidth()
                                .padding(buttonPadding), onClick = { setBpm(bpm - 1) })
                            {
                                Text(
                                    text = "-1",
                                    style = TextStyle(fontSize = fontSize, fontWeight = fontWeight)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            numberDialogData.value.onSubmitButtonClick.invoke(bpm)
                            onDismissRequest.invoke()
                        },
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(text = okText)
                    }
                }
            }
        }
    }
}
@Composable
fun ButtonsDialog(buttonsDialogData: MutableState<ButtonsDialogData>, okText: String = "OK", model: AppViewModel,
              onDismissRequest: () -> Unit = { buttonsDialogData.value = ButtonsDialogData(model = model)})
{
    if (buttonsDialogData.value.dialogState) {
        // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier.width(300.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = buttonsDialogData.value.title)
                    Spacer(modifier = Modifier.height(10.dp))
                    val listState = rememberLazyListState()
                    LazyColumn( state = listState,
                        modifier = Modifier.height(420.dp)
                    ) {
                        items((0..2).toList()) { item ->
                            when (item) {
                                0 -> SpecialFunctions1Buttons(
                                    model = buttonsDialogData.value.model,
                                    buttonSize = buttonsDialogData.value.buttonSize,
                                    fontSize = buttonsDialogData.value.fontSize,
                                    colors = model.appColors,
                                    onRound = buttonsDialogData.value.onRound,
                                    onCadenza = buttonsDialogData.value.onCadenza,
                                    onSingle = buttonsDialogData.value.onSingle,
                                    onTritoneSubstitution = buttonsDialogData.value.onTritoneSubstitution)
                                1 -> WavesButtons(
                                    model = buttonsDialogData.value.model,
                                    isActive = buttonsDialogData.value.isActiveWaves,
                                    buttonSize = buttonsDialogData.value.buttonSize,
                                    fontSize = buttonsDialogData.value.fontSize,
                                    colors = model.appColors,
                                    onWave3Click = buttonsDialogData.value.onWave3,
                                    onWave4Click = buttonsDialogData.value.onWave4,
                                    onWave6Click = buttonsDialogData.value.onWave6
                                )
                                2 -> PedalsButtons(
                                    model = buttonsDialogData.value.model,
                                    isActive = buttonsDialogData.value.isActivePedals,
                                    buttonSize = buttonsDialogData.value.buttonSize,
                                    fontSize = buttonsDialogData.value.fontSize,
                                    colors = model.appColors,
                                    onPedal1Click = buttonsDialogData.value.onPedal1,
                                    onPedal3Click = buttonsDialogData.value.onPedal3,
                                    onPedal5Click = buttonsDialogData.value.onPedal5
                                )

                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            //buttonDialogData.value.onSubmitButtonClick.invoke(bpm)
                            onDismissRequest.invoke()
                        },
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(text = okText)
                    }
                }
            }
        }
    }
}
@Composable
fun NumberDialog(numberDialogData: MutableState<NumberDialogData>, okText: String = "OK",
                 onDismissRequest: () -> Unit = { numberDialogData.value = NumberDialogData(value = numberDialogData.value.value)})
{
    if (numberDialogData.value.dialogState) {
       // var selectedValue by remember{ mutableStateOf(numberDialogData.value.value)}
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier.width(300.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = numberDialogData.value.title)
                    Spacer(modifier = Modifier.height(10.dp))

                    val textState = remember { mutableStateOf(TextFieldValue("${numberDialogData.value.value}")) }
                    TextField(
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        value = textState.value,
                        onValueChange = { textState.value = it }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val bpm = if(textState.value.text.isBlank()) numberDialogData.value.value
                                else Integer.parseInt(textState.value.text)
                            val newBpm = bpm.coerceIn(numberDialogData.value.min, numberDialogData.value.max )
                            numberDialogData.value.onSubmitButtonClick.invoke(newBpm)
                            onDismissRequest.invoke()
                        },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = okText)
                    }
                }
            }
        }
    }
}
@Composable
fun MultiListDialog(listDialogData: MutableState<MultiListDialogData>, fontSize: TextUnit, okText: String = "OK") {
    MultiSelectListDialog(
        listDialogData = listDialogData,
        fontSize = fontSize, okText = okText,
        onDismissRequest = { listDialogData.value = MultiListDialogData(itemList = listDialogData.value.itemList)  }
    )
}
@Composable
fun MultiSelectListDialog(
    listDialogData: MutableState<MultiListDialogData>,
    fontSize: TextUnit, okText: String = "OK",
    onDismissRequest: () -> Unit
) {
    if (listDialogData.value.dialogState) {
        var selectedOptions by remember{ mutableStateOf(listDialogData.value.selectedListDialogItems) }
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier.width(300.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = listDialogData.value.dialogTitle)
                    Spacer(modifier = Modifier.height(10.dp))
                    val listState = rememberLazyListState()
                    if(listDialogData.value.itemList.isNotEmpty()){
                        LazyColumn( state = listState,
                            modifier = Modifier.height(420.dp)
                        ) { items(listDialogData.value.itemList) { item ->
                            val selected = if (selectedOptions.isEmpty()) {
                                listOf<String>()
                            } else {
                                listDialogData.value.itemList.filterIndexed{ index, _ -> selectedOptions.contains(index)}
                                //sequencesList[selectedOption.value]
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            MultiRadioButton(item, selected, fontSize) { selectedValue ->
                                val index = listDialogData.value.itemList.indexOf(selectedValue)
                                selectedOptions = if(selectedOptions.contains(index)){
                                    selectedOptions.toMutableSet().also{
                                        it.remove(index)}.sorted().toSet()
                                } else {
                                    selectedOptions.toMutableSet().also{
                                        it.add(index)}.sorted().toSet()
                                }

                            }
                        }

                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                listDialogData.value.onSubmitButtonClick.invoke(selectedOptions.toList())
                                onDismissRequest.invoke()
                            },
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(text = okText)
                        }
                    }

                }
            }
        }
    }
}
@Composable
fun MultiRadioButton(text: String, selectedValues: List<String>, fontSize: TextUnit,
                     onClickListener: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = (selectedValues.contains(text)),
                onClick = {
                    onClickListener(text)
                }
            )
            .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        // The Default Radio Button in Jetpack Compose doesn't accept text as an argument.
        // So have Text Composable to show text.
        androidx.compose.material.RadioButton(
            selected = (selectedValues.contains(text)),
            onClick = {
                onClickListener(text)
            }
        )
        Text(
            text = text,
            style = MaterialTheme.typography.body1.merge().copy(fontSize = fontSize),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun ListDialog(listDialogData: MutableState<ListDialogData>, okText: String = "OK", fontSize: TextUnit, fillPrevious: Boolean = false) {
    SingleSelectListDialog(
        listDialogData = listDialogData,
        fontSize = fontSize, okText = okText, fillPrevious,
        onDismissRequest = { listDialogData.value = ListDialogData(itemList = listDialogData.value.itemList)  }
    )
}
@Composable
fun SingleSelectListDialog(
    listDialogData: MutableState<ListDialogData>,
    fontSize: TextUnit, okText: String = "OK", fillPrevious: Boolean = false,
    onDismissRequest: () -> Unit
) {
    if (listDialogData.value.dialogState) {
        var selectedOption by remember{ mutableStateOf(listDialogData.value.selectedListDialogItem) }
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier.width(300.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = listDialogData.value.dialogTitle)
                    Spacer(modifier = Modifier.height(10.dp))
                    val listState = rememberLazyListState()
                    if(listDialogData.value.itemList.isNotEmpty()){
                        LazyColumn( state = listState,
                            modifier = Modifier.height(420.dp)
                        ) { itemsIndexed(listDialogData.value.itemList) { index, item ->
                            val selected = if (selectedOption == -1) {
                                ""
                            } else {
                                listDialogData.value.itemList[selectedOption]
                                //sequencesList[selectedOption.value]
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            val showAsSelected = if (!fillPrevious) false
                               else index <= listDialogData.value.itemList.indexOf(selected)
                            RadioButton(item, selected, showAsSelected, fontSize) { selectedValue ->
                                selectedOption = listDialogData.value.itemList.indexOf(selectedValue)
                            }
                        }

                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                listDialogData.value.onSubmitButtonClick.invoke(selectedOption)
                                onDismissRequest.invoke()
                            },
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(text = okText)
                        }
                    }

                }
            }
        }
    }
}
@Composable
fun RadioButton(text: String, selectedValue: String, showAsSelected: Boolean = false, fontSize: TextUnit, onClickListener: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = (text == selectedValue),
                onClick = {
                    onClickListener(text)
                }
            )
            .background(if (showAsSelected) Color.LightGray else Color.White)
            .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        // The Default Radio Button in Jetpack Compose doesn't accept text as an argument.
        // So have Text Composable to show text.
        androidx.compose.material.RadioButton(
            selected = (text == selectedValue),
            onClick = {
                onClickListener(text)
            }
        )
        Text(
            text = text,
            style = MaterialTheme.typography.body1.merge().copy(fontSize = fontSize),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

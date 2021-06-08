package com.cristianovecchi.mikrokanon.composables

import android.os.Parcelable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristianovecchi.mikrokanon.AIMUSIC.Clip
import com.cristianovecchi.mikrokanon.AIMUSIC.Counterpoint
import com.cristianovecchi.mikrokanon.db.ClipData
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun NoteClipDisplay(
    modifier: Modifier, clips: List<Clip>, notesNames: List<String>,
    cursor: MutableState<Int> = mutableStateOf(-1),
    nCols: Int = 6, dispatch: (Int) -> Unit) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val selectionBackColor = Color.White
    val selectionTextColor = Color.Red
    val selectionBorderColor = Color.Black
    val unselectionBackColor = Color.LightGray
    val unselectionTextColor = Color.Blue
    val unselectionBorderColor = Color.DarkGray
    val fontSize = 18.sp
    val intervalPadding = 4.dp
    val innerPadding = 10.dp

    if (clips.isEmpty()) {
        Text(text = "ENTER SOME NOTES", modifier = Modifier.padding(16.dp))

    } else {
        Column(modifier = modifier) {
            //Text(text= "${ArrayList(noteClips).toStringAll()}", modifier = Modifier.padding(16.dp))
            val nRows = (clips.size / nCols) + 1
            val rows = (0 until nRows).toList()
            LazyColumn(state = listState) {
                items(rows) { row ->
                    var index = row * nCols
                    //Text(text = "ROW #$row")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        for (j in 0 until nCols) {
                            if (index != clips.size) {
                                val clip = clips[index]

                                Card(
                                    modifier = Modifier
                                        .background(Color.White)
                                        .clip(RoundedCornerShape(6.dp))
                                        .padding(intervalPadding)
                                        .clickable { dispatch(clip.id) },
                                    backgroundColor = if (cursor.value == index) selectionBackColor else unselectionBackColor,
                                    contentColor = if (cursor.value == index) selectionTextColor else unselectionTextColor,
                                    border = BorderStroke(
                                        2.dp,
                                        if (cursor.value == index) selectionBorderColor else unselectionBorderColor
                                    ),
                                    elevation = if (cursor.value == index) 4.dp else 4.dp
                                )
                                {
                                    Text(
                                        text = clip.findText(notesNames = notesNames), modifier = Modifier.padding(innerPadding),
                                        style = TextStyle(fontSize = if (cursor.value == index) fontSize else fontSize),
                                        fontWeight = if (cursor.value == index) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                                index++
                            }
                        }
                    }
                }
                if (cursor.value > -1) coroutineScope.launch {
                    val rowIndex = if (clips.size <= nCols) 1 else cursor.value / nCols
                    listState.animateScrollToItem(rowIndex)
                }
            }
        }
    }
}



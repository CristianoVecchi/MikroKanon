package com.cristianovecchi.mikrokanon.composables

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cristianovecchi.mikrokanon.AIMUSIC.Clip
import com.cristianovecchi.mikrokanon.ui.AppColors
import kotlinx.coroutines.launch

@Composable
fun NoteClipDisplay(
    modifier: Modifier, clips: List<Clip>, notesNames: List<String>,
    zodiacSigns: Boolean = false, emoji: Boolean = false,
    colors: AppColors, hintText: String = "",
    cursor: MutableState<Int> = mutableStateOf(-1), fontSize: Int = 18,
    nCols: Int = 6, dispatch: (Int) -> Unit)
{
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val selectionBackColor = colors.selCardBackColorSelected
    val selectionTextColor = colors.selCardTextColorSelected
    val selectionBorderColor = colors.selCardBorderColorSelected
    val unselectionBackColor = colors.selCardBackColorUnselected
    val unselectionTextColor = colors.selCardTextColorUnselected
    val unselectionBorderColor = colors.selCardBorderColorUnselected
    val intervalPadding = 4.dp
    val innerPadding = 10.dp

    if (clips.isEmpty() or notesNames.isEmpty()) {
        Text(text = hintText, modifier = Modifier.padding(20.dp),
        style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic, color = colors.selCardTextColorUnselected),
                            overflow = TextOverflow.Ellipsis)

    } else {
        Column(modifier = modifier.background(colors.inputBackgroundColor)) {
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
                                val text = if(zodiacSigns) clip.findZodiacSign(emoji) else clip.findText(notesNames = notesNames)
                                Card(
                                    modifier = Modifier
                                        .background(colors.inputBackgroundColor)
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
                                        text = text,
                                        modifier = Modifier.padding(innerPadding),
                                        style = TextStyle(fontSize = if(text.length>3) (fontSize/3 * 2).sp else fontSize.sp),
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



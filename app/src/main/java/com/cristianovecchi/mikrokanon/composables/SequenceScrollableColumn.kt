package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cristianovecchi.mikrokanon.AIMUSIC.Clip
import com.cristianovecchi.mikrokanon.AIMUSIC.toStringAll
import com.cristianovecchi.mikrokanon.ui.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SequenceScrollableColumn(
   // listState: LazyListState,
    modifier: Modifier, fontSize:Int, notesNames: List<String>, zodiacSigns: Boolean, emoji: Boolean,
    colors: AppColors,
    sequences: List<ArrayList<Clip>>, selected:Int, onSelect: (Int) -> Unit
)
{
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    if(sequences.isNotEmpty()){
        val padding = 8.dp
        LazyColumn(state = listState, modifier = modifier.padding(top = padding))

        {
            itemsIndexed(items = sequences) { index, sequence ->
                Row(modifier = Modifier.padding(padding)){
                    if (index == selected) {
                        SelectableCard(sequence.toStringAll(notesNames, zodiacSigns, emoji), fontSize + 2, isSelected = true, colors = colors,
                            selectedFontWeight = FontWeight.ExtraBold,
                            onClick = {})
                    } else {
                        SelectableCard(text = sequence.toStringAll(notesNames, zodiacSigns, emoji), fontSize - (fontSize / 20), isSelected = false,colors = colors, onClick = {
                            onSelect(index)})
                    }
                }
            }

            coroutineScope.launch {
                delay(200)
                if(sequences.isNotEmpty() && (selected == -1 || selected >= sequences.size))
                    listState.animateScrollToItem(sequences.size -1)
            }
        }
    } else {
        Column(modifier){}
    }
}
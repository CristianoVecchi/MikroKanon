package com.cristianovecchi.mikrokanon.composables.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cristianovecchi.mikrokanon.findGroupIndex
import com.cristianovecchi.mikrokanon.findRanges
import com.cristianovecchi.mikrokanon.indexInTotalOf
import com.cristianovecchi.mikrokanon.retrieveByIndexInTotal
import com.cristianovecchi.mikrokanon.ui.AppColors
import com.cristianovecchi.mikrokanon.ui.Dimensions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GroupingListDialog(
    groupingDialogData: MutableState<GroupingDialogData>, dimensions: Dimensions,
    okText: String = "OK", appColors: AppColors,
    onDismissRequest:() -> Unit =  { groupingDialogData.value = GroupingDialogData(itemGroups = groupingDialogData.value.itemGroups)}
) {
    if (groupingDialogData.value.dialogState) {
        val fontColor = appColors.dialogFontColor
        val backgroundColor = appColors.dialogBackgroundColor
        val fontSize = dimensions.dialogFontSize
        var selectedOption by remember{ mutableStateOf(groupingDialogData.value.selectedListDialogItem) }
        Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
            Surface(
                modifier = Modifier
                    .width(dimensions.dialogWidth)
                    .height(dimensions.dialogHeight),
                color = backgroundColor,
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = groupingDialogData.value.dialogTitle, fontWeight = FontWeight.Bold, color = fontColor, style = TextStyle(fontSize = fontSize.sp))
                    Spacer(modifier = Modifier.height(5.dp))
                    val listState = rememberLazyListState()
                    val weights = dimensions.listDialogWeights
                    val groups = groupingDialogData.value.itemGroups
                    val groupNames = groupingDialogData.value.groupNames
                    var groupIndex by remember {
                        mutableStateOf(
                            groups.findGroupIndex(
                                selectedOption
                            )
                        )
                    }
                    val scope = rememberCoroutineScope()
                    if(groups.isNotEmpty() && groupNames.isNotEmpty()) {
                        val modifierA = Modifier.weight(weights.first / 6 * 5)
                        val modifierB = Modifier.weight(weights.first / 6)
                        val modifierC = Modifier.weight(weights.second)
                        val groupRanges = groups.findRanges()

                        val selectedGroup by derivedStateOf {
                            groups[groupIndex ?: 0]
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Row( modifier = modifierB.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically ){
                            Text(
                                text = if(groupNames.isNotEmpty() && groupIndex!=-1) groupNames[groupIndex ?: 0] else "",
                                color = fontColor,
                                fontSize = (fontSize  * 2).sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        LazyColumn(
                            state = listState,
                            modifier = modifierA.padding(end = 5.dp)
                        ) {
                            itemsIndexed(selectedGroup) { index, item ->
                                val selected = if (selectedOption == -1 || groupRanges[groupIndex
                                        ?: 0] == null
                                ) {
                                    ""
                                } else {
                                    groups.retrieveByIndexInTotal(selectedOption) ?: ""
                                    //sequencesList[selectedOption.value]
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                val showAsSelected = false
                                RadioButton(
                                    item,
                                    selected,
                                    showAsSelected,
                                    fontSize.sp,
                                    appColors
                                ) { selectedValue ->
                                    selectedOption = groups.indexInTotalOf(selectedValue)
                                }
                            }
//                            item {
//                                if (selectedOption != -1) scope.launch {
//                                    listState.scrollToItem(selectedOption)
//                                }
//                            }
                        }
//                        LaunchedEffect(key1 = groupIndex) {
//                            if(selectedOption != -1) {
//                                if(selectedOption > listState.firstVisibleItemIndex){
//                                    delay(50)
//                                    listState.animateScrollToItem(selectedOption)
//                                }
//                            }
//                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Row( modifier = modifierC.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically )
                        {
                            Button(
                                onClick = {
                                    groupingDialogData.value.onSubmitButtonClick.invoke(selectedOption)
                                    onDismissRequest.invoke()
                                },
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(text = okText, style = TextStyle(
                                    fontSize = dimensions.dialogFontSize.sp ) )
                            }
                            Row{
                                Button(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    onClick = {
                                        groupIndex?.let {
                                            groupIndex = if(it-1 < 0) groups.size -1 else it-1
                                        }
                                    },
                                    shape = MaterialTheme.shapes.large
                                ) {
                                    //Text(text = "←", fontSize = fontSize.sp)
                                    Text(text = "<<", fontSize = fontSize.sp)
                                }
                                Button(
                                    onClick = {
                                        groupIndex?.let {
                                            groupIndex = if(it+1 >= groups.size) 0 else it+1
                                        }
                                    },
                                    shape = MaterialTheme.shapes.large
                                ) {
                                    //Text(text = "→", fontSize = fontSize.sp)
                                    Text(text = ">>", fontSize = fontSize.sp)
                                }
                            }
                        }

                    }

                }
            }
        }
    }
}
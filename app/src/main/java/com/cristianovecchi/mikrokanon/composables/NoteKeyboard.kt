package com.cristianovecchi.mikrokanon.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class NoteNamesEn(val abs:Int) {
    C(0),D(2),E(4),F(5),G(7),A(9),B(11),EMPTY(-1)
}
enum class NoteNamesIt {
    Do,Re,Mi,Fa,Sol,La,Si,EMPTY
}
enum class Accidents(val ax : String, val sum : Int){
    //SHARP("\uF023"), FLAT("\uF062") , D_SHARP("\uF045"), D_FLAT("\uF0BA"), NATURAL("\uF06E")
    SHARP("#", 1), FLAT("b", -1) , D_SHARP("x",2), D_FLAT("bb",-2), NATURAL("§",0), EMPTY("",0)
}
// Maestro Regular font
// SHARP 61475 0xF023 // FLAT 61538 0xF062 // D_SHARP 61517 0xF045 //D_FLAT 61626 0xF0BA // NATURAL 61550 0xF06E
sealed class Out {
    data class Note(val note: NoteNamesEn) : Out()
    data class Accident(val ax: Accidents) : Out()
    object Delete: Out()
    object Forward: Out()
    object Back: Out()
    object Enter: Out()
    object Undo: Out()
    object FullBack : Out()
    object FullForward : Out()
    object Analysis : Out()
}

private data class ButtonInfo(val text: String, val output: Out)

@Composable
fun NoteKeyboard(
    names : List<String> = NoteNamesIt.values().map{it.toString()},
    nRows: Int = 5, nCols: Int = 4, res_done: Int? = null,
    dispatch : (Out, String) -> Unit ) {
    val buttonInfos = listOf(
            ButtonInfo(text = Accidents.D_FLAT.ax, output = Out.Accident(Accidents.D_FLAT)),
            ButtonInfo(text = Accidents.D_SHARP.ax, output = Out.Accident(Accidents.D_SHARP)),
            ButtonInfo(text = Accidents.FLAT.ax, output = Out.Accident(Accidents.FLAT)),
            ButtonInfo(text = Accidents.SHARP.ax, output = Out.Accident(Accidents.SHARP)),

            ButtonInfo(text = "DEL", output = Out.Delete),
            ButtonInfo(text = "UN", output = Out.Undo),
            ButtonInfo(text = Accidents.NATURAL.ax, output = Out.Accident(Accidents.NATURAL)),
            ButtonInfo(text = names[3], output = Out.Note(NoteNamesEn.F)),

            ButtonInfo(text = "|<-", output = Out.FullBack),
            ButtonInfo(text = "->|", output = Out.FullForward),
            ButtonInfo(text = names[6], output = Out.Note(NoteNamesEn.B)),
            ButtonInfo(text = names[2], output = Out.Note(NoteNamesEn.E)),

            ButtonInfo(text = "<-", output = Out.Back),
            ButtonInfo(text = "->", output = Out.Forward),
            ButtonInfo(text = names[5], output = Out.Note(NoteNamesEn.A)),
            ButtonInfo(text = names[1], output = Out.Note(NoteNamesEn.D)),

            ButtonInfo(text = "OK", output = Out.Enter),
            ButtonInfo(text = "AN", output = Out.Analysis),
            ButtonInfo(text = names[4], output = Out.Note(NoteNamesEn.G)),
            ButtonInfo(text = names[0], output = Out.Note(NoteNamesEn.C)),

    )
    var buttonIndex = 0
    Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,){

        for (i in 0 until nRows){
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,)
            {
                for (j in 0 until nCols) {
                    if( buttonIndex == buttonInfos.size ){
                        Text(text = "  ")
                    } else
                    {
                        val buttonInfo = buttonInfos[buttonIndex++]
                        val text = buttonInfo.text

                        if(text == "OK" && res_done != null) {

                            IconButton(modifier = Modifier.padding(2.dp).
                                                    background(Color.White, RoundedCornerShape(4.dp))
                                .then(Modifier.size(72.dp).border(2.dp, Color.Black)),
                                        onClick = {dispatch(buttonInfo.output, buttonInfo.text)})
                            {
                                Icon(
                                    painter = painterResource(id = res_done),
                                    contentDescription = null, // decorative element
                                    tint = Color.Green)
                            }
                        } else
                        {
                            Button(modifier= Modifier.padding(2.dp),
                                    onClick = {dispatch(buttonInfo.output, buttonInfo.text)})
                            {
                                Text(text = text,
                                        style = TextStyle(fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold) )
                            }
                        }
                    }
                }
            }
        }
    }
}
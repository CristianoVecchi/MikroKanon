package com.cristianovecchi.mikrokanon.composables


import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.locale.Lang
import com.cristianovecchi.mikrokanon.locale.getZodiacSigns
import com.cristianovecchi.mikrokanon.ui.AppColors
import com.cristianovecchi.mikrokanon.ui.shift

enum class NoteNamesEn(val abs:Int) {
    C(0),D(2),E(4),F(5),G(7),A(9),B(11),EMPTY(-1)
}
val doubleSharp = if(android.os.Build.VERSION.SDK_INT >=android.os.Build.VERSION_CODES.O) String(Character.toChars(0x1D12A)) else "♯"+"♯"
val doubleFlat = if(android.os.Build.VERSION.SDK_INT >=android.os.Build.VERSION_CODES.O) String(Character.toChars(0x1D12B)) else "\u266D" + "\u266D"
enum class Accidents(val ax : String, val sum : Int){
    //SHARP("\uF023"), FLAT("\uF062") , D_SHARP("\uF045"), D_FLAT("\uF0BA"), NATURAL("\uF06E")
    SHARP("\u266F", 1), FLAT("\u266D", -1) ,
    D_SHARP(doubleSharp,2),D_FLAT(doubleFlat,-2), NATURAL("\u266E",0), EMPTY("",0)
    //D_SHARP("𝄪",2),D_FLAT("𝄫",-2), NATURAL("\u266E",0), EMPTY("",0)


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
    object PlaySequence : Out()
}

private data class ButtonInfo(val text: String, val output: Out, val resId: Int = -1)

@Composable
fun NoteKeyboard(
    model: AppViewModel, colors: AppColors,
    nRows: Int = 5, nCols: Int = 4, iconMap: Map<String,Int> = HashMap(),
    dispatch : (Out) -> Unit ) {
    model.userOptionsData.observeAsState(initial = listOf()).value // to force recomposing when options change
    val language = Lang.provideLanguage(model.getUserLangDef())
    val zodiacFlags by model.zodiacFlags.asFlow().collectAsState(initial = Triple(false,false,false))
    val (zodiacPlanetsActive, zodiacSignsActive, zodiacEmojisActive) = zodiacFlags
    val zodiacSigns = getZodiacSigns(zodiacEmojisActive)
    val names = if(zodiacSignsActive) listOf(zodiacSigns[0], zodiacSigns[2], zodiacSigns[4],
                                    zodiacSigns[5], zodiacSigns[7], zodiacSigns[9], zodiacSigns[11])
                else language.noteNames
    val playing by model.playing.asFlow().collectAsState(initial = false)
    val dimensions  by model.dimensions.asFlow().collectAsState(initial = model.dimensions.value!!)
    val buttonSize = dimensions.inputButtonSize
    val fontSize = dimensions.inputButtonFontSize
    val buttonInfos = listOf(
            ButtonInfo(text = Accidents.D_FLAT.ax, output = Out.Accident(Accidents.D_FLAT)),
            ButtonInfo(text = Accidents.D_SHARP.ax, output = Out.Accident(Accidents.D_SHARP)),
            ButtonInfo(text = Accidents.FLAT.ax, output = Out.Accident(Accidents.FLAT)),
            ButtonInfo(text = Accidents.SHARP.ax, output = Out.Accident(Accidents.SHARP)),

            ButtonInfo(text = "DEL", output = Out.Delete, resId = iconMap["delete"] ?: -1),
            ButtonInfo(text = "UN", output = Out.Undo, resId = iconMap["undo"] ?: -1),
            ButtonInfo(text = Accidents.NATURAL.ax, output = Out.Accident(Accidents.NATURAL)),
            ButtonInfo(text = names[3], output = Out.Note(NoteNamesEn.F)),

            ButtonInfo(text = "|<-", output = Out.FullBack,resId = iconMap["full_back"] ?: -1),
            ButtonInfo(text = "->|", output = Out.FullForward, resId = iconMap["full_forward"] ?: -1),
            ButtonInfo(text = names[6], output = Out.Note(NoteNamesEn.B)),
            ButtonInfo(text = names[2], output = Out.Note(NoteNamesEn.E)),

            ButtonInfo(text = "<-", output = Out.Back,resId = iconMap["back"] ?: -1),
            ButtonInfo(text = "->", output = Out.Forward, resId = iconMap["forward"] ?: -1),
            ButtonInfo(text = names[5], output = Out.Note(NoteNamesEn.A)),
            ButtonInfo(text = names[1], output = Out.Note(NoteNamesEn.D)),

            ButtonInfo(text = "OK", output = Out.Enter, resId = iconMap["done"] ?: -1),
            ButtonInfo(text = "PL", output = Out.PlaySequence,
                resId = if(!playing) iconMap["play"] ?: -1 else iconMap["stop"] ?: -1),
            ButtonInfo(text = names[4], output = Out.Note(NoteNamesEn.G)),
            ButtonInfo(text = names[0], output = Out.Note(NoteNamesEn.C)),

    )
    var buttonIndex = 0
    Column(modifier = Modifier.fillMaxSize().padding(top = 8.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.Center){

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
                        val resId = buttonInfo.resId
                        val onClick = {dispatch(buttonInfo.output)}
                        val color = if(buttonInfo.text == "OK") Color.Green else {
                            if(resId == -1 ) colors.iconButtonIconColor.shift(-0.2f) else colors.iconButtonIconColor
                        }
                        val actualColors = if(resId == -1) colors.copy(iconButtonBackgroundColor = colors.iconButtonBackgroundColor.shift(-0.15f)) else colors
                        val actualFontSize = when (buttonInfo.text.length){
                            in 0..1 -> fontSize / 2 * 3
                            2 -> fontSize
                            3 -> fontSize / 3 * 2
                            4 -> fontSize / 2
                            else -> fontSize
                        }
                        val text = if (resId == -1) buttonInfo.text else ""
                        CustomButton(
                            adaptSizeToIconButton = true,
                            text = text,
                            iconId = resId,
                            fontSize = actualFontSize,
                            buttonSize = buttonSize,
                            iconColor = color,
                            colors = actualColors
                        ) {
                            onClick()
                        }
                    }
                }
            }
        }
    }
}
package com.cristianovecchi.mikrokanon.midi

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.midi.accompanimentStyles.*
import com.leff.midi.MidiTrack
import com.leff.midi.event.MidiEvent
import com.leff.midi.event.ProgramChange

fun findAbsPitches(barGroup: List<Bar>, harmonizationType: HarmonizationType,
                   chordsTrack: MidiTrack, chordsChannel: Int): List<List<Int>> {
    barGroup.findChordSequence(harmonizationType)
    val absPitches: List<List<Int>> = barGroup.extractAbsPitchesFromDodecaBytes(harmonizationType)
    return absPitches
}
fun addHarmonizationsToTrack(chordsTrack: MidiTrack, barGroups: List<List<Bar>>,
                             harmonizations: List<HarmonizationData>, justVoicing: Boolean, chordsChannel: Int){
    barGroups.forEachIndexed{ index, barGroup ->
        //barGroup.forEachIndexed { i, bar -> println("bar#$i $bar)")}
        val harmonizationData = harmonizations[index]
        val (type, instruments, volume, style, _, direction, isFlow, density) = harmonizationData
        val octaves = harmonizationData.convertFromOctavesByte()

        if (type != HarmonizationType.NONE){
            val diffChordVelocity = 40 - (volume * 40).toInt()  // 1f = 0, 0f = 40
           // println("diff: $diffChordVelocity")
            when(style){
                HarmonizationStyle.ACCORDO -> when (type){
                    HarmonizationType.POP -> createPopChordsTrack(chordsTrack, barGroup, false, diffChordVelocity, justVoicing, octaves, chordsChannel)
                    HarmonizationType.POP7 -> createPopChordsTrack(chordsTrack, barGroup, true, diffChordVelocity, justVoicing, octaves, chordsChannel)
                    HarmonizationType.LIBERTY -> createLibertyChordsTrack(chordsTrack, barGroup, diffChordVelocity, justVoicing, octaves, chordsChannel)
                    HarmonizationType.JAZZ -> createJazzChordsTrack(chordsTrack, barGroup, false, diffChordVelocity, justVoicing, octaves, chordsChannel)
                    HarmonizationType.JAZZ11 -> createJazzChordsTrack(chordsTrack, barGroup, true, diffChordVelocity, justVoicing, octaves, chordsChannel)
                    HarmonizationType.XWH -> createExtendedWeightedHarmonyTrack(chordsTrack, barGroup,  diffChordVelocity, justVoicing, octaves, chordsChannel)
                    HarmonizationType.FULL12 -> createFull12HarmonizedTrack(chordsTrack, barGroup,  diffChordVelocity, octaves, chordsChannel)
                    else -> {}
                }

                HarmonizationStyle.RIBATTUTO -> {
                    val absPitches = findAbsPitches(barGroup, type, chordsTrack, chordsChannel)
                    createRibattuto(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, density)
                }
                HarmonizationStyle.POLIRITMIA -> {
                    val absPitches = findAbsPitches(barGroup, type, chordsTrack, chordsChannel)
                    when (density) {
                        2, 3 -> createMultiPoliritmia(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                            diffChordVelocity, diffChordVelocity / 2, justVoicing, direction, isFlow, density)
                        else -> createPoliritmia(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                            diffChordVelocity, diffChordVelocity / 2, justVoicing, direction, isFlow)
                    }
                }
                HarmonizationStyle.ACCIACCATURA -> {
                    val absPitches = findAbsPitches(barGroup, type, chordsTrack, chordsChannel)
                    createAcciaccatura(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction, density)
                }
                HarmonizationStyle.SINCOPATO -> {
                    val absPitches = findAbsPitches(barGroup, type, chordsTrack, chordsChannel)
                    createSincopato(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction, isFlow)
                }
                HarmonizationStyle.BERLINESE -> {
                    val absPitches = findAbsPitches(barGroup, type, chordsTrack, chordsChannel)
                    createBerlinese(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction, density)
                }
                HarmonizationStyle.CONTROTEMPO -> {
                    val absPitches = findAbsPitches(barGroup, type, chordsTrack, chordsChannel)
                    createControtempo(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, density)
                }
                HarmonizationStyle.ALBERTI -> {
                    val absPitches = findAbsPitches(barGroup, type, chordsTrack, chordsChannel)
                    createAlberti(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction, isFlow)
                }
                HarmonizationStyle.RICAMATO -> {
                    val absPitches = findAbsPitches(barGroup, type, chordsTrack, chordsChannel)
                    createRicamato(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction, isFlow, density)
                }
                HarmonizationStyle.TRILLO -> {
                    val absPitches = findAbsPitches(barGroup, type, chordsTrack, chordsChannel)
                    createTrillo(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, isFlow)
                }
                HarmonizationStyle.TUTTITRILLI -> {
                    val absPitches = findAbsPitches(barGroup, type, chordsTrack, chordsChannel)
                    createMultiTrillo(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction, isFlow)
                }
                HarmonizationStyle.ARPEGGIO, HarmonizationStyle.SCAMBIARPEGGIO ->{
                    val absPitches = findAbsPitches(barGroup, type, chordsTrack, chordsChannel)
                    createArpeggio(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                }
                HarmonizationStyle.PASSAGGIO ->{
                    val absPitches = findAbsPitches(barGroup, type, chordsTrack, chordsChannel)
                    createPassaggio(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction, isFlow)
                }
                HarmonizationStyle.CAPRICCIO ->{
                    val absPitches = findAbsPitches(barGroup, type, chordsTrack, chordsChannel)
                    createCapriccio(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction, isFlow, density)
                }
                HarmonizationStyle.FARFALLA ->{
                    val absPitches = findAbsPitches(barGroup, type, chordsTrack, chordsChannel)
                    createFarfalla(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction, isFlow)
                }
                HarmonizationStyle.GRAZIOSO ->{
                    val absPitches = findAbsPitches(barGroup, type, chordsTrack, chordsChannel)
                    createGrazioso(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction, isFlow, density)
                }
                HarmonizationStyle.LINEA, HarmonizationStyle.ACCUMULO  -> {
                    val absPitches = findAbsPitches(barGroup, type, chordsTrack, chordsChannel)
                    createNoteLine(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction, isFlow)
                }
                HarmonizationStyle.ECO -> {
                    val absPitches = findAbsPitches(barGroup, type, chordsTrack, chordsChannel)
                    createEco(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction, isFlow, density)
                }
                HarmonizationStyle.BICINIUM, HarmonizationStyle.TRICINIUM -> {
                    val absPitches = findAbsPitches(barGroup, type, chordsTrack, chordsChannel)
                    createNoteDoubleTripleLine(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction, isFlow)
                }
                HarmonizationStyle.RADIO -> {
                    val absPitches = findAbsPitches(barGroup, type, chordsTrack, chordsChannel)
                    createRadio(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, isFlow)
                }
                HarmonizationStyle.NEVE -> {
                    val absPitches = findAbsPitches(barGroup, type, chordsTrack, chordsChannel)
                    when (density) {
                        2 -> createNeve2(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                            diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                        else -> createNeve(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                            diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                    }

                }
            }
            chordsTrack.addInstrumentsToTrackCycling(barGroup.map { it.tick }, chordsChannel, instruments)
        }
    }
}

fun MidiTrack.addInstrumentsToTrackCycling(barStarts: List<Long>, chordsChannel: Int, instruments: List<Int>) {
    if(instruments.size == 1){
        this.initializeChordTrack(barStarts[0], chordsChannel, instruments[0])
    } else {
        barStarts.forEachIndexed { i, start ->
            this.initializeChordTrack(start, chordsChannel, instruments[i % instruments.size])
        }
    }
}

fun MidiTrack.initializeChordTrack(startTick:Long, chordsChannel: Int, chordsInstrument: Int){
    //println("tick: $startTick  instrument: $chordsInstrument  channel: $chordsChannel")
    val pc: MidiEvent = ProgramChange(startTick, chordsChannel, chordsInstrument) // cambia strumento
    this.insertEvent(pc)
}

fun List<Bar>.extractAbsPitchesFromDodecaBytes(type: HarmonizationType): List<List<Int>> {
    return when (type){
        HarmonizationType.XWH -> {
            this.map { bar ->
                val root = bar.chord1!!.root
                val pitches = Insieme.absPitchesFromDodecaByte(bar.dodecaByte1stHalf!!).toMutableList()
                if(!pitches.contains(root)){pitches.add(root)}
                pitches.sorted()
//                    .also{
//                        println(it)
//                    }
            }
        }
        HarmonizationType.FULL12 -> {
            this.map { bar ->
                Insieme.absPitchesFromDodecaByte(bar.dodecaByte1stHalf!! xor 0B111111111111 ).toList()
//                .also{
//                    println("${bar.dodecaByte1stHalf!!.toString(2)} -> ${(bar.dodecaByte1stHalf!! xor 0B111111111111).toString(2)}")
//                }
            }
        }
        else -> {
            this.map { bar -> bar.extractChordAbsPitches().sorted() }
        }
    }
}

//private fun Int.selectAbsPitchesFromDodecaByte(type: HarmonizationType): List<Int> {
//    val pitches = Insieme.absPitchesFromDodecaByte(this)
//    val sortedPitches = when(type){
//        HarmonizationType.FULL12, HarmonizationType.NONE -> pitches
//        else ->
//    }
//}

fun ArrayList<MidiTrack>.addChordTrack(harmonizations: List<HarmonizationData>, bars: List<Bar>,
                                       trackData: List<TrackData>, audio8D: List<Int>,
                                       totalLength: Long, justVoicing: Boolean, channel: Int){
    if(harmonizations.isNotEmpty() && !harmonizations.all { it.type == HarmonizationType.NONE }) {
        val doubledBars = bars.mergeOnesInMetro()
            .resizeLastBar(totalLength)
            .splitBarsInTwoParts()
        // using trackDatas without replacing for a better chord definition
        assignDodecaBytesToBars(doubledBars.toTypedArray(), trackData, false)
        val barGroups = if(harmonizations.size == 1) listOf(doubledBars)
        else doubledBars.splitBarsInGroups(harmonizations.size)
        val chordsTrack = MidiTrack()
        addHarmonizationsToTrack(chordsTrack, barGroups, harmonizations, justVoicing, channel)
        if(audio8D.isNotEmpty()){
            setAudio8D(chordsTrack, channel, channel)
        }
        this.add(chordsTrack)
    }
}



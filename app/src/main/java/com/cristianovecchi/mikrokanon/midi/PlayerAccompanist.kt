package com.cristianovecchi.mikrokanon.midi

import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.AIMUSIC.JazzChord.*
import com.cristianovecchi.mikrokanon.convertDodecabyteToInts
import com.cristianovecchi.mikrokanon.midi.accompanimentStyles.*
import com.leff.midi.MidiTrack
import com.leff.midi.event.MidiEvent
import com.leff.midi.event.ProgramChange

fun foundAbsPitchesAndInitialize(barGroup: List<Bar>, harmonizationType: HarmonizationType,
chordsTrack: MidiTrack, chordsChannel: Int, chordsInstrument: Int): List<List<Int>> {
    barGroup.findChordSequence(harmonizationType)
    val absPitches: List<List<Int>> = barGroup.extractAbsPitchesFromDodecaBytes(harmonizationType)
    chordsTrack.initializeChordTrack(barGroup[0].tick, chordsChannel, chordsInstrument)
    return absPitches
}
fun addHarmonizationsToTrack(chordsTrack: MidiTrack, barGroups: List<List<Bar>>,
                             harmonizations: List<HarmonizationData>, justVoicing: Boolean, chordsChannel: Int){
    barGroups.forEachIndexed{ index, barGroup ->
        val harmonizationData = harmonizations[index]
        val (type, instrument, volume, style, _, direction) = harmonizationData
        val octaves = harmonizationData.convertFromOctavesByte()
        if (type != HarmonizationType.NONE){
            val diffChordVelocity = 40 - (volume * 40).toInt()  // 1f = 0, 0f = 40
           // println("diff: $diffChordVelocity")
            when(style){
                HarmonizationStyle.ACCORDO -> when (type){
                    HarmonizationType.POP -> createPopChordsTrack(chordsTrack, barGroup, false, instrument, diffChordVelocity, justVoicing, octaves)
                    HarmonizationType.POP7 -> createPopChordsTrack(chordsTrack, barGroup, true,instrument, diffChordVelocity, justVoicing, octaves)
                    HarmonizationType.JAZZ -> createJazzChordsTrack(chordsTrack, barGroup, false, instrument, diffChordVelocity, justVoicing, octaves)
                    HarmonizationType.JAZZ11 -> createJazzChordsTrack(chordsTrack, barGroup, true, instrument, diffChordVelocity, justVoicing, octaves)
                    HarmonizationType.XWH -> createExtendedWeightedHarmonyTrack(chordsTrack, barGroup, instrument,  diffChordVelocity, justVoicing, octaves)
                    HarmonizationType.FULL12 -> createFull12HarmonizedTrack(chordsTrack, barGroup, instrument,  diffChordVelocity, octaves)
                    else -> {}
                }

                HarmonizationStyle.DRAMMATICO, HarmonizationStyle.RIBATTUTO, HarmonizationStyle.RIBATTUTO_3,
                HarmonizationStyle.TREMOLO, HarmonizationStyle.TREMOLO_5, HarmonizationStyle.TREMOLO_6 -> {
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createRibattuto(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing)
                }
                HarmonizationStyle.POLIRITMIA -> {
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createPoliritmia(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                }
                HarmonizationStyle.ACCIACCATURA, HarmonizationStyle.ACCIACCATURA_2 -> {
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createAcciaccatura(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                }
                HarmonizationStyle.SINCOPATO -> {
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createSincopato(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                }
                HarmonizationStyle.CONTROTEMPO, HarmonizationStyle.CONTROTEMPO_4,
                HarmonizationStyle.CONTROTEMPO_6, HarmonizationStyle.CONTROTEMPO_8, HarmonizationStyle.CONTROTEMPO_10, HarmonizationStyle.CONTROTEMPO_12 -> {
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createControtempo(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing)
                }
                HarmonizationStyle.ALBERTI -> {
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createAlberti(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                }
                HarmonizationStyle.RICAMATO, HarmonizationStyle.RICAMATO_6, HarmonizationStyle.RICAMATO_8,
                HarmonizationStyle.RICAMATO_10, HarmonizationStyle.RICAMATO_12 -> {
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createRicamato(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                }
                HarmonizationStyle.TRILLO -> {
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createTrillo(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing)
                }
                HarmonizationStyle.TRILLO_2 -> {
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createMultiTrillo(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                }
                HarmonizationStyle.ARPEGGIO ->{
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createArpeggio(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                }
                HarmonizationStyle.PASSAGGIO ->{
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createPassaggio(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                }
                HarmonizationStyle.CAPRICCIO, HarmonizationStyle.CAPRICCIO_2 ->{
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createCapriccio(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                }
                HarmonizationStyle.FARFALLA ->{
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createFarfalla(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                }
                HarmonizationStyle.GRAZIOSO_3, HarmonizationStyle.GRAZIOSO_4 ->{
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createGrazioso(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                }
                HarmonizationStyle.LINEA, HarmonizationStyle.FLUSSO, HarmonizationStyle.ACCUMULO, HarmonizationStyle.ACCUMULO_FLUSSO -> {
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createNoteLine(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                }
                HarmonizationStyle.BICINIUM, HarmonizationStyle.TRICINIUM -> {
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createNoteDoubleTripleLine(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing, direction)
                }
                HarmonizationStyle.RADIO, HarmonizationStyle.RADIO_FLUSSO -> {
                    val absPitches = foundAbsPitchesAndInitialize(barGroup, type, chordsTrack, chordsChannel, instrument)
                    createRadio(style, chordsTrack, chordsChannel, barGroup, absPitches, octaves,
                        diffChordVelocity, diffChordVelocity / 2, justVoicing)
                }
            }

        }
    }
}

fun MidiTrack.initializeChordTrack(startTick:Long, chordsChannel: Int, chordsInstrument: Int){
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



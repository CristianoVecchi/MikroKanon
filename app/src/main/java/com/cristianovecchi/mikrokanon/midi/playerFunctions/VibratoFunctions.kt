package com.cristianovecchi.mikrokanon.midi.playerFunctions

import com.leff.midi.MidiTrack
import com.leff.midi.event.Controller

fun addVibratoToTrack(mt: MidiTrack, start: Long, duration: Long, channel: Int, vibratoDivisor: Int){
    if(duration > 5) {
        val nVibrations = (duration / vibratoDivisor).toInt() // 4 vibrations in a quarter
        if(nVibrations == 0 || duration in 6..12) { // add just one vibration if possible
            val expressionOn = Controller(start + duration / 3,channel,1, 0b1111111)
            val expressionOff = Controller(start + duration - 2,channel,1, 0)
            mt.insertEvent(expressionOn)
            mt.insertEvent(expressionOff)
        } else {
            val vibrationDur = duration / nVibrations
            val vibrationHalfDur = vibrationDur / 2
            val vibrationQuarterDur = vibrationDur / 4
            (0 until nVibrations).forEach(){
                val expressionMiddle1 = Controller(start + vibrationDur * it + vibrationQuarterDur , channel,1, 64)
                val expressionOn = Controller(start + vibrationDur * it + vibrationHalfDur , channel,1, 0b1111111)
                val expressionMiddle2 = Controller(start + vibrationDur * it + vibrationHalfDur + vibrationQuarterDur , channel,1, 64)
                val expressionOff = Controller(start +  vibrationDur * (it + 1) ,channel,1, 0)
                mt.insertEvent(expressionMiddle1)
                mt.insertEvent(expressionOn)
                mt.insertEvent(expressionMiddle2)
                mt.insertEvent(expressionOff)
            }
        }
    }
}
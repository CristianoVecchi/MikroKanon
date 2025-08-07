package com.cristianovecchi.mikrokanon.midi.playerFunctions

import com.cristianovecchi.mikrokanon.AIMUSIC.TrackData
import com.cristianovecchi.mikrokanon.AppViewModel.Companion.ATTACK_MIN
import com.cristianovecchi.mikrokanon.component5
import com.cristianovecchi.mikrokanon.component6
import com.cristianovecchi.mikrokanon.component7
import com.cristianovecchi.mikrokanon.component8
import com.cristianovecchi.mikrokanon.divideDistributingRest
import com.leff.midi.MidiTrack
import com.leff.midi.event.Controller
import kotlin.math.absoluteValue

fun addIncrementalAttack(midiTrack: MidiTrack, intTick: Int, duration: Int, newAttack: Int, channel: Int ) {
    //println("incremental attack: $newAttack")
    var tick = intTick.toLong()
    val attackDur = (duration * (newAttack.toFloat() / 100)).toInt()
    if(attackDur >= 12){
        var values = (ATTACK_MIN..127).filter{it % 2 != 0 }
        val size = values.size
        if(attackDur < size) {
            values = values.subList(size - attackDur, size).filter {it % 2 != 0 }
        }
        val step = attackDur / values.size // possibly reducted size
        //println("INCR-> tick: $tick dur: $duration  attackDur: $attackDur  step: $step  ${values.size} $values")
        values.forEach{
            val setAttack = Controller(tick, channel,MIDI_EFFECT, it)
            midiTrack.insertEvent(setAttack)
            //println( "Attack: ${setAttack.value}  tick: $tick")
            tick += step
        }
        //println("last tick: $tick")
    }
}
fun addDecrementalAttack(midiTrack: MidiTrack, intTick: Int, duration: Int, newAttack: Int, channel: Int) {
    //println("decremental attack: $newAttack")
    var tick = intTick.toLong()
    val attackDur = (duration * (newAttack.absoluteValue.toFloat() / 100)).toInt()
    if(attackDur >= 12){
        var values = (125 downTo ATTACK_MIN).filter{it % 2 != 0 }
        val size = values.size
        //println("newAttack:$newAttack attackDur:$attackDur values:$values")
        if(attackDur < size) {
            values = values.subList(0, attackDur).filter{it % 2 != 0 }
        }
        val step = attackDur / values.size // possibly reducted size
        // println("DECR-> tick: $tick dur: $duration  attackDur: $attackDur  step: $step  ${values.size} $values")
        val firstAttack = Controller(tick, channel,MIDI_EFFECT, 127)
        midiTrack.insertEvent(firstAttack)
        tick = tick + duration - attackDur // set the decrescendo at the end of the note
        values.forEach{
            val setAttack = Controller(tick, channel,MIDI_EFFECT, it)
            midiTrack.insertEvent(setAttack)
            //println( "Attack: ${setAttack.value}  tick: $tick")
            tick += step
        }

    }
}

fun TrackData.checkForNormalAttackAndSetIt(midiTrack: MidiTrack, index: Int){
    // after lowering attack should be reset to normal (127) for the next note
    val nextAttack = attacks.getOrNull(index)
    val nextIntTick = ticks.getOrNull(index)
    nextAttack?.let{
        if(nextAttack == 0){
            val setAttack = Controller(nextIntTick!!.toLong()-1, channel,MIDI_EFFECT, 127)
            // tick - 1 to avoid note-to-note click
            midiTrack.insertEvent(setAttack)
        }
    }
}
// 70 = Sound Variation
// 71 = Sound Timbre
// 72 = Release Time
// 73 = Attack Time
// 74 = Sound Brightness
// 91 = Effects Level
// 92 = Tremulo Level
// 93 = Chorus Level
// 94 = Celeste Level
// 95 = Phaser Level
const val MIDI_EFFECT = 11 // our Attack is 11 (expression)
fun TrackData.addAttackToMidiTrack(midiTrack: MidiTrack) {
    //println("attacks: ${this.attacks.contentToString()}")
    ticks.forEachIndexed { i, intTick ->
        val newAttack = attacks[i]
        // println("newAttack: $newAttack")
        when (newAttack) {
            0 -> {}
            in -100..100 -> {
                if(newAttack > 0){
                    addIncrementalAttack(midiTrack, intTick, durations[i], newAttack, this.channel)
                } else { // negative attack
                    addDecrementalAttack(midiTrack, intTick, durations[i], newAttack, this.channel)
                    // after lowering attack should be reset to normal (127) for the next note
                    this.checkForNormalAttackAndSetIt(midiTrack, i + 1)
                }
            }
            in 900..1100 -> {
                val realAttack = newAttack - 1000
                //println("realAttack: $realAttack")
                val halfDuration2 = durations[i] / 2
                val halfDuration1 = halfDuration2 + durations[i] % 2
                //println("Total duration: ${durations[i]}  half1: $halfDuration1  half2: $halfDuration2")
                if (realAttack > 0) {
                    addIncrementalAttack(midiTrack, intTick, halfDuration1, realAttack, this.channel)
                    addDecrementalAttack(midiTrack, intTick + halfDuration1, halfDuration2, -realAttack, this.channel)
                    // after lowering attack should be reset to normal (127) for the next note
                    this.checkForNormalAttackAndSetIt(midiTrack, i + 1)
                } else if(realAttack < 0) { //AttackCrescDim >< (Dim-Cresc)
                    addDecrementalAttack(midiTrack, intTick, halfDuration1, realAttack, this.channel)
                    addIncrementalAttack(midiTrack, intTick + halfDuration1, halfDuration2, -realAttack, this.channel)
                }
            }
            in 1900..2100 -> { //X2 <><>
                val realAttack = newAttack - 2000
                //println("realAttack: $realAttack")
                val (dur1, dur2, dur3, dur4) = durations[i].divideDistributingRest(4)
                //println("Total duration: ${durations[i]}  half1: $halfDuration1  half2: $halfDuration2")
                if (realAttack > 0) {
                    addIncrementalAttack(midiTrack, intTick, dur1, realAttack, this.channel)
                    addDecrementalAttack(midiTrack, intTick + dur1, dur2, -realAttack, this.channel)
                    addIncrementalAttack(midiTrack, intTick + dur1 +dur2, dur3, realAttack, this.channel)
                    addDecrementalAttack(midiTrack, intTick + dur1 + dur2 + dur3, dur4, -realAttack, this.channel)
                    // after lowering attack should be reset to normal (127) for the next note
                    this.checkForNormalAttackAndSetIt(midiTrack, i + 1)
                } else if(realAttack < 0) { //AttackCrescDim >< (Dim-Cresc)
                    addDecrementalAttack(midiTrack, intTick, dur1, realAttack, this.channel)
                    addIncrementalAttack(midiTrack, intTick + dur1, dur2, -realAttack, this.channel)
                    addDecrementalAttack(midiTrack, intTick + dur1 +dur2, dur3, realAttack, this.channel)
                    addIncrementalAttack(midiTrack, intTick + dur1 + dur2 + dur3, dur4, -realAttack, this.channel)
                }
            }
            in 2900..3100 -> { //X3 <><><>
                val realAttack = newAttack - 3000
                //println("realAttack: $realAttack")
                val (dur1, dur2, dur3, dur4, dur5, dur6) = durations[i].divideDistributingRest(6)
                //println("Total duration: ${durations[i]}  half1: $halfDuration1  half2: $halfDuration2")
                if (realAttack > 0) {
                    addIncrementalAttack(midiTrack, intTick, dur1, realAttack, this.channel)
                    addDecrementalAttack(midiTrack, intTick + dur1, dur2, -realAttack, this.channel)
                    addIncrementalAttack(midiTrack, intTick + dur1 +dur2, dur3, realAttack, this.channel)
                    addDecrementalAttack(midiTrack, intTick + dur1 + dur2 + dur3, dur4, -realAttack, this.channel)
                    addIncrementalAttack(midiTrack, intTick + dur1 +dur2 +dur3 +dur4, dur5, realAttack, this.channel)
                    addDecrementalAttack(midiTrack, intTick + dur1 + dur2 + dur3 +dur4 +dur5, dur6, -realAttack, this.channel)
                    // after lowering attack should be reset to normal (127) for the next note
                    this.checkForNormalAttackAndSetIt(midiTrack, i + 1)
                } else if(realAttack < 0) { //AttackCrescDim >< (Dim-Cresc)
                    addDecrementalAttack(midiTrack, intTick, dur1, realAttack, this.channel)
                    addIncrementalAttack(midiTrack, intTick + dur1, dur2, -realAttack, this.channel)
                    addDecrementalAttack(midiTrack, intTick + dur1 +dur2, dur3, realAttack, this.channel)
                    addIncrementalAttack(midiTrack, intTick + dur1 + dur2 + dur3, dur4, -realAttack, this.channel)
                    addDecrementalAttack(midiTrack, intTick + dur1 +dur2 +dur3 +dur4, dur5, realAttack, this.channel)
                    addIncrementalAttack(midiTrack, intTick + dur1 + dur2 + dur3 +dur4 +dur5, dur6, -realAttack, this.channel)
                }
            }
            in 3900..4100 -> { //x4
                val realAttack = newAttack - 4000
                //println("realAttack: $realAttack")
                val (dur1, dur2, dur3, dur4, dur5, dur6, dur7, dur8) = durations[i].divideDistributingRest(8)
                //println("Total duration: ${durations[i]}  half1: $halfDuration1  half2: $halfDuration2")
                if (realAttack > 0) {
                    addIncrementalAttack(midiTrack, intTick, dur1, realAttack, this.channel)
                    addDecrementalAttack(midiTrack, intTick + dur1, dur2, -realAttack, this.channel)
                    addIncrementalAttack(midiTrack, intTick + dur1 +dur2, dur3, realAttack, this.channel)
                    addDecrementalAttack(midiTrack, intTick + dur1 + dur2 + dur3, dur4, -realAttack, this.channel)
                    addIncrementalAttack(midiTrack, intTick + dur1 +dur2 +dur3 +dur4, dur5, realAttack, this.channel)
                    addDecrementalAttack(midiTrack, intTick + dur1 + dur2 + dur3 +dur4 +dur5, dur6, -realAttack, this.channel)
                    addIncrementalAttack(midiTrack, intTick + dur1 +dur2 +dur3 +dur4 +dur5 +dur6, dur7, realAttack, this.channel)
                    addDecrementalAttack(midiTrack, intTick + dur1 + dur2 + dur3 +dur4 +dur5 +dur6 +dur7, dur8, -realAttack, this.channel)
                    // after lowering attack should be reset to normal (127) for the next note
                    this.checkForNormalAttackAndSetIt(midiTrack, i + 1)
                } else if(realAttack < 0) { //AttackCrescDim >< (Dim-Cresc)
                    addDecrementalAttack(midiTrack, intTick, dur1, realAttack, this.channel)
                    addIncrementalAttack(midiTrack, intTick + dur1, dur2, -realAttack, this.channel)
                    addDecrementalAttack(midiTrack, intTick + dur1 +dur2, dur3, realAttack, this.channel)
                    addIncrementalAttack(midiTrack, intTick + dur1 + dur2 + dur3, dur4, -realAttack, this.channel)
                    addDecrementalAttack(midiTrack, intTick + dur1 +dur2 +dur3 +dur4, dur5, realAttack, this.channel)
                    addIncrementalAttack(midiTrack, intTick + dur1 + dur2 + dur3 +dur4 +dur5, dur6, -realAttack, this.channel)
                    addDecrementalAttack(midiTrack, intTick + dur1 +dur2 +dur3 +dur4 +dur5 +dur6, dur7, realAttack, this.channel)
                    addIncrementalAttack(midiTrack, intTick + dur1 + dur2 + dur3 +dur4 +dur5 +dur6 +dur7, dur8, -realAttack, this.channel)
                }
            }
        }
    }
}

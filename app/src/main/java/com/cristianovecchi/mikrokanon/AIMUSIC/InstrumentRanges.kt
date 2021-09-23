package com.cristianovecchi.mikrokanon.AIMUSIC

import android.util.Range

const val C1 = 24
const val C2 = 36
const val C3 = 48
const val C4 = 60 //MIDDLE C
const val C5 = 72
const val C6 = 84
const val C7 = 96
const val C8 = 108 //last piano key


const val A0 = 21 //first piano key
const val G3 = 55 // violin and alto flute low limit

val PIANO_ALL: IntRange = IntRange(A0, C8)

val PICCOLO_ALL: IntRange = IntRange(74, C8) // D5 - C8
val FLUTE_ALL: IntRange = IntRange(C4, C7)
val OBOE_ALL: IntRange = IntRange(58, 93) // Bb3 - A6
val ENGLISH_HORN_ALL: IntRange = IntRange(52, 81) // E3 - A5
val CLARINET_ALL: IntRange = IntRange(50, 94) // D3 - Bb6
val BASS_CLARINET_ALL: IntRange = IntRange(37, 82) // Db2 - Bb5
val BASSOON_ALL: IntRange = IntRange(34, 79) // Bb1 - G5
val FRENCH_HORN_ALL: IntRange = IntRange(33, 77) // A1 - F5

val VIOLIN_ALL: IntRange = IntRange(G3, 105) // G3 - A7
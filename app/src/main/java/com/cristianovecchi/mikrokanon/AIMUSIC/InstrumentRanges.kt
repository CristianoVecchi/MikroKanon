package com.cristianovecchi.mikrokanon.AIMUSIC

import android.util.Range
import com.cristianovecchi.mikrokanon.rangeTo

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
const val G4 = 67
const val G5 = 79
const val G6 = 91

// All the ranges refer to the Vienna Symphonic Library specifications (for the lowest pitch)
// for LOW3 and MIDDLE3 a Major Third is added at the end, for HIGH3 at the start

val PIANO_ALL: IntRange = IntRange(A0, C8)

val PICCOLO_ALL: IntRange = IntRange(74, C8) // D5 - C8
val PICCOLO_LOW3: IntRange = IntRange(74, 90) // D5 - F#6
val PICCOLO_MIDDLE3: IntRange = IntRange(80, C7) // G#5 - C7
val PICCOLO_HIGH3: IntRange = IntRange(92, C8) // Ab6 - C8
val PART_PICCOLO_MIDDLE_HIGH = EnsemblePart(72, 6, PICCOLO_ALL, PICCOLO_MIDDLE3..PICCOLO_HIGH3)
val PART_PICCOLO_HIGH = EnsemblePart(72, 6, PICCOLO_ALL, PICCOLO_HIGH3)

val FLUTE_ALL: IntRange = IntRange(C4, C7)
val FLUTE_LOW3: IntRange = IntRange(C4, 76) // C4 - E5
val FLUTE_MIDDLE3: IntRange = IntRange(C5, 88) // C5 - E6
val FLUTE_HIGH3: IntRange = IntRange(80 ,C7) // Ab5 -C7
val PART_FLUTE_MIDDLE_HIGH = EnsemblePart(73, 5, FLUTE_ALL, FLUTE_MIDDLE3..FLUTE_HIGH3)

val OBOE_ALL: IntRange = IntRange(58, G6) // Bb3 - G6
val OBOE_LOW3: IntRange = IntRange(58, 74) // Bb3 - D5
val OBOE_MIDDLE3: IntRange = IntRange(70, 86) // Bb4 - D6
val OBOE_HIGH3: IntRange = IntRange(75, G6) // Eb5 - G6
val PART_OBOE_MIDDLE_HIGH = EnsemblePart(68, 5, OBOE_ALL, OBOE_MIDDLE3..OBOE_HIGH3)
val PART_OBOE_LOW_MIDDLE = EnsemblePart(68, 4, OBOE_ALL, OBOE_LOW3..OBOE_MIDDLE3)

val ENGLISH_HORN_ALL: IntRange = IntRange(52, 83) // E3 - B5
val ENGLISH_HORN_LOW3: IntRange = IntRange(52, 68) // E3 - G#4
val ENGLISH_HORN_MIDDLE3: IntRange = IntRange(58, 74) // Bb3 - D5
val ENGLISH_HORN_HIGH3: IntRange = IntRange(69, 83) // A4 - A5
val PART_ENGLISH_HORN_LOW_MIDDLE = EnsemblePart(69, 4, ENGLISH_HORN_ALL, ENGLISH_HORN_LOW3..ENGLISH_HORN_MIDDLE3)

val CLARINET_ALL: IntRange = IntRange(50, 94) // D3 - Bb6
val CLARINET_LOW3: IntRange = IntRange(50, 66) // D3 - F#4
val CLARINET_MIDDLE3: IntRange = IntRange(65, 81) // F4 - A5
val CLARINET_HIGH3: IntRange = IntRange(69, 85) // A4 - C#6 (3rd added at the end)
val CLARINET_HIGHEST3: IntRange = IntRange(78, 94) // Gb5 - Bb6 (3rd added at the start)
val PART_CLARINET_MIDDLE_HIGH = EnsemblePart(71, 5, CLARINET_ALL, CLARINET_MIDDLE3..CLARINET_HIGH3)
val PART_CLARINET_LOW_MIDDLE = EnsemblePart(71, 4, CLARINET_ALL, CLARINET_LOW3..CLARINET_MIDDLE3)
val PART_CLARINET_MIDDLE = EnsemblePart(71, 4, CLARINET_ALL, CLARINET_MIDDLE3)

val BASS_CLARINET_ALL: IntRange = IntRange(34, 83) // Bb1 - B5
val BASS_CLARINET_LOW5: IntRange = IntRange(34, 52) // Bb1 - E5 (added a fifth at the end to cover the whole range)
val BASS_CLARINET_MIDDLE3: IntRange = IntRange(52, 68) // E3 - G#4
val BASS_CLARINET_HIGH3: IntRange = IntRange(57, 73) // A3 - C#5
val BASS_CLARINET_HIGHEST3: IntRange = IntRange(G4, 83) // G4 - B5
val PART_BASS_CLARINET_LOW_MIDDLE = EnsemblePart(71, 3, BASS_CLARINET_ALL, BASS_CLARINET_LOW5..BASS_CLARINET_MIDDLE3)
val PART_BASS_CLARINET_MIDDLE = EnsemblePart(71, 3, BASS_CLARINET_ALL, BASS_CLARINET_MIDDLE3)

val BASSOON_ALL: IntRange = IntRange(34, 77) // Bb1 - F5
val BASSOON_LOW3: IntRange = IntRange(34, 50) // Bb1 - D3
val BASSOON_MIDDLE3: IntRange = IntRange(42, 58) // F#2 - Bb3
val BASSOON_HIGH5: IntRange = IntRange(58, 77) // Bb3 - F5 (added a fifth at the start to cover the whole range)
val PART_BASSOON_LOW_MIDDLE = EnsemblePart(70, 2, BASSOON_ALL, BASSOON_LOW3..BASSOON_MIDDLE3)
val PART_BASSOON_LOW = EnsemblePart(70, 2, BASSOON_ALL, BASSOON_LOW3)

val FRENCH_HORN_ALL: IntRange = IntRange(35, 77) // B1 - F5
val FRENCH_HORN_LOW3: IntRange = IntRange(35, 51) // B1 - D#3
val FRENCH_HORN_MIDDLE3: IntRange = IntRange(51, G4) // Eb3 - G4
val FRENCH_HORN_HIGH3: IntRange = IntRange(61, 77) // Db4 - F5
val PART_FRENCH_HORN_MIDDLE_HIGH = EnsemblePart(60, 3, FRENCH_HORN_ALL, FRENCH_HORN_MIDDLE3..FRENCH_HORN_HIGH3)
val PART_FRENCH_HORN_MIDDLE = EnsemblePart(60, 3, FRENCH_HORN_ALL, FRENCH_HORN_MIDDLE3)


val VIOLIN_ALL: IntRange = IntRange(G3, 105) // G3 - A7
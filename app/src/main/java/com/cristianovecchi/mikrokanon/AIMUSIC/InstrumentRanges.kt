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
const val G2 = 43
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
val PART_FRENCH_HORN_MIDDLE_HIGH = EnsemblePart(60, 4, FRENCH_HORN_ALL, FRENCH_HORN_MIDDLE3..FRENCH_HORN_HIGH3)
val PART_FRENCH_HORN_MIDDLE = EnsemblePart(60, 3, FRENCH_HORN_ALL, FRENCH_HORN_MIDDLE3)

val TRUMPET_ALL: IntRange = IntRange(54 ,C6) // F#3 - C6
val TRUMPET_LOW3: IntRange = IntRange(54, 70) // F#3 - Bb4
val TRUMPET_MIDDLE3: IntRange = IntRange(62, 78) // D4 - F#5 third added at the start
val TRUMPET_HIGH3: IntRange = IntRange(68, C6) // Ab4 - C6
val PART_TRUMPET_MIDDLE_HIGH = EnsemblePart(56, 5, TRUMPET_ALL, TRUMPET_MIDDLE3..TRUMPET_HIGH3)
val PART_TRUMPET_HIGH = EnsemblePart(56, 5, TRUMPET_ALL, TRUMPET_HIGH3)

val TROMBONE_ALL: IntRange = IntRange(34 ,77) // Bb1 - F5
val TROMBONE_LOW3: IntRange = IntRange(34 ,50) // Bb1 - D3
val TROMBONE_MIDDLE5: IntRange = IntRange(42 ,61) // F#2 - C#4 added a fifth to cover the whole range
val TROMBONE_HIGH3: IntRange = IntRange(61 ,77) // C#4 - F5
val PART_TROMBONE_LOW_MIDDLE = EnsemblePart(57, 3, TROMBONE_ALL, TROMBONE_LOW3..TROMBONE_MIDDLE5)
val PART_TROMBONE_MIDDLE = EnsemblePart(57, 3, TROMBONE_ALL, TROMBONE_MIDDLE5)
val PART_TROMBONE_MIDDLE_HIGH = EnsemblePart(57, 4, TROMBONE_ALL, TROMBONE_MIDDLE5..TROMBONE_HIGH3)
val PART_TROMBONE_HIGH = EnsemblePart(57, 4, TROMBONE_ALL, TROMBONE_HIGH3)

val TUBA_ALL: IntRange = IntRange(26, 65) // D1 - F4
val TUBA_LOW3: IntRange = IntRange(26, 42) // D1 - F#2
val TUBA_MIDDLE3: IntRange = IntRange(42, 58) // F#2 - Bb3
val TUBA_HIGH3: IntRange = IntRange(49, 65) // Db3 - F4
val PART_TUBA_LOW = EnsemblePart(58, 2, TUBA_ALL, TUBA_LOW3)

val VIOLIN_ALL: IntRange = IntRange(G3, 105) // G3 - A7
val VIOLIN_LOW3: IntRange = IntRange(G3, 71) // G3 - B4 (G string)
val VIOLIN_MIDDLE3: IntRange = IntRange(62, 78) // D4 - F#5 (D string)
val VIOLIN_HIGH3: IntRange = IntRange(69, 85) // A4 - C#6 (A string)
val VIOLIN_HIGHEST: IntRange = IntRange(69, 85) // E5 - A7 (E string)
val PART_VIOLIN_MIDDLE_HIGH_HIGHEST = EnsemblePart(40, 5,VIOLIN_ALL, VIOLIN_MIDDLE3..VIOLIN_HIGH3..VIOLIN_HIGHEST)
val PART_VIOLIN_HIGH_HIGHEST = EnsemblePart(40, 5,VIOLIN_ALL, VIOLIN_HIGH3..VIOLIN_HIGHEST)
val PART_VIOLIN_HIGHEST = EnsemblePart(40, 6, VIOLIN_ALL, VIOLIN_HIGHEST)

val VIOLA_ALL: IntRange = IntRange(C3, 93) // C3 - A6
val VIOLA_LOW3: IntRange = IntRange(C3, 64) // C3 - E4 (C string)
val VIOLA_MIDDLE3: IntRange = IntRange(G3, 71) // G3 - B4 (G string)
val VIOLA_HIGH3: IntRange = IntRange(62, 78) // D4 - F#5 (D string)
val VIOLA_HIGHEST: IntRange = IntRange(69, 93) // A4 - A6 (A string)
val PART_VIOLA_MIDDLE_HIGH = EnsemblePart(41, 4, VIOLA_ALL, VIOLA_MIDDLE3..VIOLA_HIGH3)
val PART_VIOLA_MIDDLE = EnsemblePart(41, 4, VIOLA_ALL, VIOLA_MIDDLE3)
val PART_VIOLA_LOW_MIDDLE = EnsemblePart(41, 3, VIOLA_ALL, VIOLA_MIDDLE3)

val CELLO_ALL: IntRange = IntRange(C2, 81) // C2 - A5
val CELLO_LOW3: IntRange = IntRange(C2, 52) // C2 - E3 (C string)
val CELLO_MIDDLE3: IntRange = IntRange(G2, 59) // G2 - B3 (G string)
val CELLO_HIGH3: IntRange = IntRange(50, 66) // D3 - F#4 (D string)
val CELLO_HIGHEST: IntRange = IntRange(57, 81) // A3 - A5 (A string)
val PART_CELLO_LOW_MIDDLE = EnsemblePart(42, 3, CELLO_ALL, CELLO_LOW3..CELLO_MIDDLE3)
val PART_CELLO_MIDDLE_HIGH = EnsemblePart(42, 3, CELLO_ALL, CELLO_MIDDLE3..CELLO_HIGH3)
val PART_CELLO_HIGH_HIGHEST = EnsemblePart(42, 4, CELLO_ALL, CELLO_HIGH3..CELLO_HIGHEST)

val DOUBLE_BASS_ALL: IntRange = IntRange(28, G4) // E1 - G4
val DOUBLE_BASS_LOW3: IntRange = IntRange(28, 44) // E1 - G#2 (E string)
val DOUBLE_BASS_MIDDLE3: IntRange = IntRange(33, 49) // A1 - C#3 (A string)
val DOUBLE_BASS_HIGH3: IntRange = IntRange(38, 54) // D2 - F#3 (D string)
val DOUBLE_BASS_HIGHEST: IntRange = IntRange(G2, 64) // G2 - E4 (G string)


val PART_STRING_ORCHESTRA_CELLO_LOW3 = EnsemblePart(STRING_ORCHESTRA, 2, CELLO_ALL, CELLO_LOW3)
val PART_STRING_ORCHESTRA_DB_LOW3 = EnsemblePart(STRING_ORCHESTRA, 1, DOUBLE_BASS_ALL, DOUBLE_BASS_LOW3)

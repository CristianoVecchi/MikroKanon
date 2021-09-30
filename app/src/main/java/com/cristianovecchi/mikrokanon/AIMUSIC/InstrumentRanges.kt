package com.cristianovecchi.mikrokanon.AIMUSIC

import android.util.Range
import com.cristianovecchi.mikrokanon.rangeTo

const val PIANO = 0
const val HARPSICHORD = 6
const val NYLON_GUITAR = 24
const val STEEL_GUITAR = 25
const val CLEAN_GUITAR = 27
const val GUITAR_HARMONICS= 31
const val ACOUSTIC_BASS= 32
const val VIOLIN = 40
const val VIOLA = 41
const val CELLO = 42
const val DOUBLEBASS = 43
const val PIZZICATO = 45
const val HARP = 46
const val STRING_ORCHESTRA = 48
const val TRUMPET = 56
const val TROMBONE = 57
const val TUBA = 58
const val FRENCH_HORN = 60
const val SAX_SOPRANO = 64
const val SAX_ALTO = 65
const val SAX_TENOR = 66
const val SAX_BARITONE = 67

const val OBOE = 68
const val ENGLISH_HORN = 69
const val BASSOON = 70
const val PICCOLO = 72
const val FLUTE = 73
const val RECORDER = 74
const val CLARINET = 71
const val SITAR = 104
const val BANJO = 105
const val SHAMISEN = 106
const val KOTO = 107

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
val PARTS_PIANO: List<EnsemblePart> = listOf( // the array index indicates the requested octave + 5M
    EnsemblePart(PIANO, 0, PIANO_ALL), //useless
    EnsemblePart(PIANO, 1, PIANO_ALL, IntRange(A0, 40)), // A0 - E2
    *(2..6).map{ val start = (it+1)*12; EnsemblePart(PIANO, it, PIANO_ALL, IntRange(start, start + 19))}.toTypedArray(),
    EnsemblePart(PIANO, 8, PIANO_ALL, IntRange(89, C8)) // F6 - C8
)
val HARPSICHORD_ALL: IntRange = IntRange(A0, C8)
val PARTS_HARPSICHORD: List<EnsemblePart> = listOf( // the array index indicates the requested octave + 5M
    EnsemblePart(HARPSICHORD, 0, HARPSICHORD_ALL), //useless
    EnsemblePart(HARPSICHORD, 1, HARPSICHORD_ALL, IntRange(A0, 40)), // A0 - E2
    *(2..6).map{ val start = (it+1)*12; EnsemblePart(HARPSICHORD, it, HARPSICHORD_ALL, IntRange(start, start + 19))}.toTypedArray(),
    EnsemblePart(HARPSICHORD, 8, HARPSICHORD_ALL, IntRange(89, C8)) // F6 - C8
)
val HARP_ALL: IntRange = IntRange(A0, C8)
val PARTS_HARP: List<EnsemblePart> = listOf( // the array index indicates the requested octave + 5M
    EnsemblePart(HARP, 0, HARP_ALL), //useless
    EnsemblePart(HARP, 1, HARP_ALL, IntRange(A0, 40)), // A0 - E2
    *(2..6).map{ val start = (it+1)*12; EnsemblePart(HARP, it, HARP_ALL, IntRange(start, start + 19))}.toTypedArray(),
    EnsemblePart(HARP, 8, HARP_ALL, IntRange(89, C8)) // F6 - C8
)

val PICCOLO_ALL: IntRange = IntRange(74, C8) // D5 - C8
val PICCOLO_LOW3: IntRange = IntRange(74, 90) // D5 - F#6
val PICCOLO_MIDDLE3: IntRange = IntRange(80, C7) // G#5 - C7
val PICCOLO_HIGH3: IntRange = IntRange(92, C8) // Ab6 - C8
val PART_PICCOLO_MIDDLE_HIGH = EnsemblePart(PICCOLO, 6, PICCOLO_ALL, PICCOLO_MIDDLE3..PICCOLO_HIGH3)
val PART_PICCOLO_HIGH = EnsemblePart(PICCOLO, 6, PICCOLO_ALL, PICCOLO_HIGH3)

val GUITAR_ALL: IntRange = IntRange(40, C6) // E2 - C6
val GUITAR_LOW5: IntRange = IntRange(40, 59) // E2 - B3
val GUITAR_MIDDLE5: IntRange = IntRange(50, 69) // D3 - A4
val GUITAR_HIGH5: IntRange = IntRange(59, 78) // B3 - F#5
val GUITAR_HIGHEST5: IntRange = IntRange(65, C6) // F4 - C6

val FLUTE_ALL: IntRange = IntRange(C4, C7)
val FLUTE_LOW3: IntRange = IntRange(C4, 76) // C4 - E5
val FLUTE_MIDDLE3: IntRange = IntRange(C5, 88) // C5 - E6
val FLUTE_HIGH3: IntRange = IntRange(80 ,C7) // Ab5 -C7
val PART_FLUTE_MIDDLE_HIGH = EnsemblePart(FLUTE, 5, FLUTE_ALL, FLUTE_MIDDLE3..FLUTE_HIGH3)
val PART_FLUTE_MIDDLE = EnsemblePart(FLUTE, 5, FLUTE_ALL, FLUTE_MIDDLE3)
val PART_FLUTE_LOW_MIDDLE = EnsemblePart(FLUTE, 4, FLUTE_ALL, FLUTE_LOW3..FLUTE_MIDDLE3)
val PART_FLUTE_LOW = EnsemblePart(FLUTE, 4, FLUTE_ALL, FLUTE_LOW3)

val BASS_FLUTE_ALL: IntRange = IntRange(C3, C6)
val BASS_FLUTE_LOW3: IntRange = IntRange(C3, 64) // C3 - E4
val BASS_FLUTE_MIDDLE3: IntRange = IntRange(C4, 76) // C4 - E5
val BASS_FLUTE_HIGH3: IntRange = IntRange(68 ,C6) // Ab4 -C6
val PART_BASS_FLUTE_MIDDLE_HIGH = EnsemblePart(FLUTE, 4, BASS_FLUTE_ALL, BASS_FLUTE_MIDDLE3..BASS_FLUTE_HIGH3)
val PART_BASS_FLUTE_LOW_MIDDLE = EnsemblePart(FLUTE, 3, BASS_FLUTE_ALL, BASS_FLUTE_LOW3..BASS_FLUTE_MIDDLE3)
val PART_BASS_FLUTE_LOW = EnsemblePart(FLUTE, 3, BASS_FLUTE_ALL, BASS_FLUTE_LOW3)

val OBOE_ALL: IntRange = IntRange(58, G6) // Bb3 - G6
val OBOE_LOW3: IntRange = IntRange(58, 74) // Bb3 - D5
val OBOE_MIDDLE3: IntRange = IntRange(70, 86) // Bb4 - D6
val OBOE_HIGH3: IntRange = IntRange(75, G6) // Eb5 - G6
val PART_OBOE_MIDDLE_HIGH = EnsemblePart(OBOE, 5, OBOE_ALL, OBOE_MIDDLE3..OBOE_HIGH3)
val PART_OBOE_MIDDLE = EnsemblePart(OBOE, 5, OBOE_ALL, OBOE_MIDDLE3)
val PART_OBOE_HIGH = EnsemblePart(OBOE, 6, OBOE_ALL, OBOE_HIGH3)
val PART_OBOE_LOW_MIDDLE = EnsemblePart(OBOE, 4, OBOE_ALL, OBOE_LOW3..OBOE_MIDDLE3)

val ENGLISH_HORN_ALL: IntRange = IntRange(52, 83) // E3 - B5
val ENGLISH_HORN_LOW3: IntRange = IntRange(52, 68) // E3 - G#4
val ENGLISH_HORN_MIDDLE3: IntRange = IntRange(58, 74) // Bb3 - D5
val ENGLISH_HORN_HIGH3: IntRange = IntRange(69, 83) // A4 - A5
val PART_ENGLISH_HORN_LOW_MIDDLE = EnsemblePart(ENGLISH_HORN, 4, ENGLISH_HORN_ALL, ENGLISH_HORN_LOW3..ENGLISH_HORN_MIDDLE3)
val PART_ENGLISH_HORN_MIDDLE_HIGH = EnsemblePart(ENGLISH_HORN, 5, ENGLISH_HORN_ALL, ENGLISH_HORN_MIDDLE3..ENGLISH_HORN_HIGH3)
val PART_ENGLISH_HORN_LOW = EnsemblePart(ENGLISH_HORN, 4, ENGLISH_HORN_ALL, ENGLISH_HORN_LOW3)

val CLARINET_ALL: IntRange = IntRange(50, 94) // D3 - Bb6
val CLARINET_LOW3: IntRange = IntRange(50, 66) // D3 - F#4
val CLARINET_MIDDLE3: IntRange = IntRange(65, 81) // F4 - A5
val CLARINET_HIGH3: IntRange = IntRange(69, 85) // A4 - C#6 (3rd added at the end)
val CLARINET_HIGHEST3: IntRange = IntRange(78, 94) // Gb5 - Bb6 (3rd added at the start)
val PART_CLARINET_MIDDLE_HIGH = EnsemblePart(CLARINET, 5, CLARINET_ALL, CLARINET_MIDDLE3..CLARINET_HIGH3)
val PART_CLARINET_HIGH = EnsemblePart(CLARINET, 5, CLARINET_ALL, CLARINET_HIGH3)
val PART_CLARINET_HIGHEST = EnsemblePart(CLARINET, 6, CLARINET_ALL, CLARINET_HIGHEST3)
val PART_CLARINET_HIGH_HIGHEST = EnsemblePart(CLARINET, 6, CLARINET_ALL, CLARINET_HIGH3..CLARINET_HIGHEST3)
val PART_CLARINET_LOW_MIDDLE = EnsemblePart(CLARINET, 4, CLARINET_ALL, CLARINET_LOW3..CLARINET_MIDDLE3)
val PART_CLARINET_LOW = EnsemblePart(CLARINET, 3, CLARINET_ALL, CLARINET_LOW3)
val PART_CLARINET_MIDDLE = EnsemblePart(CLARINET, 4, CLARINET_ALL, CLARINET_MIDDLE3)

val BASS_CLARINET_ALL: IntRange = IntRange(34, 83) // Bb1 - B5
val BASS_CLARINET_LOW5: IntRange = IntRange(34, 52) // Bb1 - E5 (added a fifth at the end to cover the whole range)
val BASS_CLARINET_MIDDLE3: IntRange = IntRange(52, 68) // E3 - G#4
val BASS_CLARINET_HIGH3: IntRange = IntRange(57, 73) // A3 - C#5
val BASS_CLARINET_HIGHEST3: IntRange = IntRange(G4, 83) // G4 - B5
val PART_BASS_CLARINET_LOW_MIDDLE = EnsemblePart(CLARINET, 3, BASS_CLARINET_ALL, BASS_CLARINET_LOW5..BASS_CLARINET_MIDDLE3)
val PART_BASS_CLARINET_LOW = EnsemblePart(CLARINET, 2, BASS_CLARINET_ALL, BASS_CLARINET_LOW5)
val PART_BASS_CLARINET_MIDDLE = EnsemblePart(CLARINET, 3, BASS_CLARINET_ALL, BASS_CLARINET_MIDDLE3)

val BASSOON_ALL: IntRange = IntRange(34, 77) // Bb1 - F5
val BASSOON_LOW3: IntRange = IntRange(34, 50) // Bb1 - D3
val BASSOON_MIDDLE3: IntRange = IntRange(42, 58) // F#2 - Bb3
val BASSOON_HIGH5: IntRange = IntRange(58, 77) // Bb3 - F5 (added a fifth at the start to cover the whole range)
val PART_BASSOON_LOW_MIDDLE = EnsemblePart(BASSOON, 3, BASSOON_ALL, BASSOON_LOW3..BASSOON_MIDDLE3)
val PART_BASSOON_MIDDLE_HIGH = EnsemblePart(BASSOON, 4, BASSOON_ALL, BASSOON_MIDDLE3..BASSOON_HIGH5)
val PART_BASSOON_HIGH = EnsemblePart(BASSOON, 5, BASSOON_ALL, BASSOON_HIGH5)
val PART_BASSOON_MIDDLE = EnsemblePart(BASSOON, 3, BASSOON_ALL, BASSOON_MIDDLE3)
val PART_BASSOON_LOW = EnsemblePart(BASSOON, 2, BASSOON_ALL, BASSOON_LOW3)

val FRENCH_HORN_ALL: IntRange = IntRange(35, 77) // B1 - F5
val FRENCH_HORN_LOW3: IntRange = IntRange(35, 51) // B1 - D#3
val FRENCH_HORN_MIDDLE3: IntRange = IntRange(51, G4) // Eb3 - G4
val FRENCH_HORN_HIGH3: IntRange = IntRange(61, 77) // Db4 - F5
val PART_FRENCH_HORN_MIDDLE_HIGH = EnsemblePart(FRENCH_HORN, 4, FRENCH_HORN_ALL, FRENCH_HORN_MIDDLE3..FRENCH_HORN_HIGH3)
val PART_FRENCH_HORN_MIDDLE = EnsemblePart(FRENCH_HORN, 3, FRENCH_HORN_ALL, FRENCH_HORN_MIDDLE3)

val TRUMPET_ALL: IntRange = IntRange(54 ,C6) // F#3 - C6
val TRUMPET_LOW3: IntRange = IntRange(54, 70) // F#3 - Bb4
val TRUMPET_MIDDLE3: IntRange = IntRange(62, 78) // D4 - F#5 third added at the start
val TRUMPET_HIGH3: IntRange = IntRange(68, C6) // Ab4 - C6
val PART_TRUMPET_MIDDLE_HIGH = EnsemblePart(TRUMPET, 5, TRUMPET_ALL, TRUMPET_MIDDLE3..TRUMPET_HIGH3)
val PART_TRUMPET_HIGH = EnsemblePart(TRUMPET, 5, TRUMPET_ALL, TRUMPET_HIGH3)

val TROMBONE_ALL: IntRange = IntRange(34 ,77) // Bb1 - F5
val TROMBONE_LOW3: IntRange = IntRange(34 ,50) // Bb1 - D3
val TROMBONE_MIDDLE5: IntRange = IntRange(42 ,61) // F#2 - C#4 added a fifth to cover the whole range
val TROMBONE_HIGH3: IntRange = IntRange(61 ,77) // C#4 - F5
val PART_TROMBONE_LOW_MIDDLE = EnsemblePart(TROMBONE, 3, TROMBONE_ALL, TROMBONE_LOW3..TROMBONE_MIDDLE5)
val PART_TROMBONE_MIDDLE = EnsemblePart(TROMBONE, 3, TROMBONE_ALL, TROMBONE_MIDDLE5)
val PART_TROMBONE_MIDDLE_HIGH = EnsemblePart(TROMBONE, 4, TROMBONE_ALL, TROMBONE_MIDDLE5..TROMBONE_HIGH3)
val PART_TROMBONE_HIGH = EnsemblePart(TROMBONE, 4, TROMBONE_ALL, TROMBONE_HIGH3)

val TUBA_ALL: IntRange = IntRange(26, 65) // D1 - F4
val TUBA_LOW3: IntRange = IntRange(26, 42) // D1 - F#2
val TUBA_MIDDLE3: IntRange = IntRange(42, 58) // F#2 - Bb3
val TUBA_HIGH3: IntRange = IntRange(49, 65) // Db3 - F4
val PART_TUBA_LOW = EnsemblePart(TUBA, 2, TUBA_ALL, TUBA_LOW3)

val VIOLIN_ALL: IntRange = IntRange(G3, 105) // G3 - A7
val VIOLIN_LOW3: IntRange = IntRange(G3, 71) // G3 - B4 (G string)
val VIOLIN_MIDDLE3: IntRange = IntRange(62, 78) // D4 - F#5 (D string)
val VIOLIN_HIGH3: IntRange = IntRange(69, 85) // A4 - C#6 (A string)
val VIOLIN_HIGHEST: IntRange = IntRange(69, 85) // E5 - A7 (E string)
val PART_VIOLIN_MIDDLE_HIGH_HIGHEST = EnsemblePart(VIOLIN, 5,VIOLIN_ALL, VIOLIN_MIDDLE3..VIOLIN_HIGH3..VIOLIN_HIGHEST)
val PART_VIOLIN_MIDDLE_HIGH = EnsemblePart(VIOLIN, 5,VIOLIN_ALL, VIOLIN_MIDDLE3..VIOLIN_HIGH3)
val PART_VIOLIN_HIGH_HIGHEST = EnsemblePart(VIOLIN, 5,VIOLIN_ALL, VIOLIN_HIGH3..VIOLIN_HIGHEST)
val PART_VIOLIN_HIGHEST = EnsemblePart(VIOLIN, 6, VIOLIN_ALL, VIOLIN_HIGHEST)

val VIOLA_ALL: IntRange = IntRange(C3, 93) // C3 - A6
val VIOLA_LOW3: IntRange = IntRange(C3, 64) // C3 - E4 (C string)
val VIOLA_MIDDLE3: IntRange = IntRange(G3, 71) // G3 - B4 (G string)
val VIOLA_HIGH3: IntRange = IntRange(62, 78) // D4 - F#5 (D string)
val VIOLA_HIGHEST: IntRange = IntRange(69, 93) // A4 - A6 (A string)
val PART_VIOLA_MIDDLE_HIGH = EnsemblePart(VIOLA, 4, VIOLA_ALL, VIOLA_MIDDLE3..VIOLA_HIGH3)
val PART_VIOLA_MIDDLE = EnsemblePart(VIOLA, 4, VIOLA_ALL, VIOLA_MIDDLE3)
val PART_VIOLA_LOW_MIDDLE = EnsemblePart(VIOLA, 3, VIOLA_ALL, VIOLA_MIDDLE3)

val CELLO_ALL: IntRange = IntRange(C2, 81) // C2 - A5
val CELLO_LOW3: IntRange = IntRange(C2, 52) // C2 - E3 (C string)
val CELLO_MIDDLE3: IntRange = IntRange(G2, 59) // G2 - B3 (G string)
val CELLO_HIGH3: IntRange = IntRange(50, 66) // D3 - F#4 (D string)
val CELLO_HIGHEST: IntRange = IntRange(57, 81) // A3 - A5 (A string)
val PART_CELLO_LOW_MIDDLE = EnsemblePart(CELLO, 3, CELLO_ALL, CELLO_LOW3..CELLO_MIDDLE3)
val PART_CELLO_LOW = EnsemblePart(CELLO, 2, CELLO_ALL, CELLO_LOW3)
val PART_CELLO_MIDDLE_HIGH = EnsemblePart(CELLO, 3, CELLO_ALL, CELLO_MIDDLE3..CELLO_HIGH3)
val PART_CELLO_MIDDLE = EnsemblePart(CELLO, 3, CELLO_ALL, CELLO_MIDDLE3)
val PART_CELLO_HIGH_HIGHEST = EnsemblePart(CELLO, 4, CELLO_ALL, CELLO_HIGH3..CELLO_HIGHEST)
val PART_CELLO_HIGH = EnsemblePart(CELLO, 4, CELLO_ALL, CELLO_HIGH3)
val PART_CELLO_HIGHEST = EnsemblePart(CELLO, 5, CELLO_ALL, CELLO_HIGHEST)

val DOUBLE_BASS_ALL: IntRange = IntRange(28, G4) // E1 - G4
val DOUBLE_BASS_LOW3: IntRange = IntRange(28, 44) // E1 - G#2 (E string)
val DOUBLE_BASS_MIDDLE3: IntRange = IntRange(33, 49) // A1 - C#3 (A string)
val DOUBLE_BASS_HIGH3: IntRange = IntRange(38, 54) // D2 - F#3 (D string)
val DOUBLE_BASS_HIGHEST: IntRange = IntRange(G2, 64) // G2 - E4 (G string)

val PART_STRING_ORCHESTRA_VIOLIN_LOW_MIDDLE_HIGH = EnsemblePart(STRING_ORCHESTRA, 4, VIOLIN_ALL, VIOLIN_LOW3..VIOLIN_MIDDLE3..VIOLIN_HIGH3)
val PART_STRING_ORCHESTRA_VIOLIN_MIDDLE_HIGH = EnsemblePart(STRING_ORCHESTRA, 5, VIOLIN_ALL, VIOLIN_MIDDLE3..VIOLIN_HIGH3)
val PART_STRING_ORCHESTRA_VIOLIN_HIGHEST = EnsemblePart(STRING_ORCHESTRA, 6, VIOLIN_ALL, VIOLIN_HIGHEST)
val PART_STRING_ORCHESTRA_VIOLIN_HIGH_HIGHEST = EnsemblePart(STRING_ORCHESTRA, 5, VIOLIN_ALL, VIOLIN_HIGH3..VIOLIN_HIGHEST)
val PART_STRING_ORCHESTRA_VIOLIN_MIDDLE_HIGH_HIGHEST = EnsemblePart(STRING_ORCHESTRA, 5, VIOLIN_ALL, VIOLIN_MIDDLE3..VIOLIN_HIGH3..VIOLIN_HIGHEST)
val PART_STRING_ORCHESTRA_VIOLA_LOW_MIDDLE = EnsemblePart(STRING_ORCHESTRA, 4, VIOLA_ALL, VIOLA_LOW3..VIOLA_MIDDLE3)
val PART_STRING_ORCHESTRA_VIOLA_LOW = EnsemblePart(STRING_ORCHESTRA, 3, VIOLA_ALL, VIOLA_LOW3)
val PART_STRING_ORCHESTRA_CELLO_LOW = EnsemblePart(STRING_ORCHESTRA, 2, CELLO_ALL, CELLO_LOW3)
val PART_STRING_ORCHESTRA_CELLO_LOW_MIDDLE = EnsemblePart(STRING_ORCHESTRA, 3, CELLO_ALL, CELLO_LOW3..CELLO_MIDDLE3)
val PART_STRING_ORCHESTRA_DB_LOW = EnsemblePart(STRING_ORCHESTRA, 1, DOUBLE_BASS_ALL, DOUBLE_BASS_LOW3)
val PART_STRING_ORCHESTRA_DB_LOW_MIDDLE = EnsemblePart(STRING_ORCHESTRA, 1, DOUBLE_BASS_ALL, DOUBLE_BASS_LOW3..DOUBLE_BASS_MIDDLE3)

val SAX_SOPRANO_ALL: IntRange = IntRange(56, 89) // Ab3 - F6
val SAX_SOPRANO_LOW3: IntRange = IntRange(56, 72) // Ab3 - C5
val SAX_SOPRANO_MIDDLE3: IntRange = IntRange(63, 79) // Eb4 - G5
val SAX_SOPRANO_HIGH3: IntRange = IntRange(73, 89) // Db5 - F6
val PART_SAX_SOPRANO_MIDDLE_HIGH = EnsemblePart(SAX_SOPRANO, 5, SAX_SOPRANO_ALL, SAX_SOPRANO_MIDDLE3..SAX_SOPRANO_HIGH3)
val PART_SAX_SOPRANO_HIGH = EnsemblePart(SAX_SOPRANO, 6, SAX_SOPRANO_ALL, SAX_SOPRANO_HIGH3)
val PART_SAX_SOPRANO_MIDDLE = EnsemblePart(SAX_SOPRANO, 5, SAX_SOPRANO_ALL, SAX_SOPRANO_MIDDLE3)

val SAX_ALTO_ALL: IntRange = IntRange(49, 82) // Db3 - Bb5
val SAX_ALTO_LOW3: IntRange = IntRange(49, 65) // Db3 - F4
val SAX_ALTO_MIDDLE3: IntRange = IntRange(56, 72) // Ab3 - C5
val SAX_ALTO_HIGH3: IntRange = IntRange(66, 82) // Gb4 - Bb5
val PART_SAX_ALTO_MIDDLE_HIGH = EnsemblePart(SAX_ALTO, 4, SAX_ALTO_ALL, SAX_ALTO_MIDDLE3..SAX_ALTO_HIGH3)
val PART_SAX_ALTO_MIDDLE = EnsemblePart(SAX_ALTO, 4, SAX_ALTO_ALL, SAX_ALTO_MIDDLE3)

val SAX_TENOR_ALL: IntRange = IntRange(44, 77) // Ab2 - F5
val SAX_TENOR_LOW3: IntRange = IntRange(44, 60) // Ab2 - C4
val SAX_TENOR_MIDDLE3: IntRange = IntRange(51, 67) // Eb3 - G4
val SAX_TENOR_HIGH3: IntRange = IntRange(61, 77) // Db4 - F5
val PART_SAX_TENOR_LOW_MIDDLE = EnsemblePart(SAX_TENOR, 3, SAX_TENOR_ALL, SAX_TENOR_LOW3..SAX_TENOR_MIDDLE3)
val PART_SAX_TENOR_MIDDLE = EnsemblePart(SAX_TENOR, 3, SAX_TENOR_ALL, SAX_TENOR_MIDDLE3)
val PART_SAX_TENOR_MIDDLE_HIGH = EnsemblePart(SAX_TENOR, 4, SAX_TENOR_ALL, SAX_TENOR_MIDDLE3..SAX_TENOR_HIGH3)


val SAX_BARITONE_ALL: IntRange = IntRange(37, 70) // Db2 - Bb4
val SAX_BARITONE_LOW3: IntRange = IntRange(37, 53) // Db2 - F3
val SAX_BARITONE_MIDDLE3: IntRange = IntRange(44, 60) // Ab2 - C4
val SAX_BARITONE_HIGH3: IntRange = IntRange(54, 70) // Gb2 - Bb4
val PART_SAX_BARITONE_LOW_MIDDLE = EnsemblePart(SAX_BARITONE, 2, SAX_BARITONE_ALL, SAX_BARITONE_LOW3..SAX_BARITONE_MIDDLE3)
val PART_SAX_BARITONE_LOW = EnsemblePart(SAX_BARITONE, 2, SAX_BARITONE_ALL, SAX_BARITONE_LOW3)
package com.cristianovecchi.mikrokanon.AIMUSIC


enum class HarmonizationType(val title: String) {
    NONE("No Harm."),
    POP("POP"), POP7("POP 7"),
    JAZZ("JAZZ"), JAZZ11("JAZZ 11"),
    XWH("XW HARMONY"), FULL12("FULL 12")
}
enum class HarmonizationDirection(val symbol: String) {
    ASCENDING("➚"), DESCENDING("➘"), RANDOM("~");

    fun applyDirection(pitches: List<Int>): List<Int>{
        return when (this){
            DESCENDING ->  pitches.sortedDescending()
            RANDOM ->  pitches.shuffled()
            else -> pitches
        }
    }
    fun invert(): HarmonizationDirection {
        return when (this) {
            DESCENDING -> ASCENDING
            ASCENDING -> DESCENDING
            else -> RANDOM
        }
    }
}
enum class StyleType {
    CHORDS, TEXTURES, PATTERNS, LINES, AERIALS
}
enum class HarmonizationStyle(val title: String, val hasDirection: Boolean, val increase: Int, val type: StyleType) {
    ACCORDO("Accordo", false, 0, StyleType.CHORDS),
    DRAMMATICO("Drammatico", false, 0, StyleType.CHORDS),
    RIBATTUTO("Ribattuto", false, 0, StyleType.CHORDS),
    RIBATTUTO_3("Ribattuto 3", false, 0, StyleType.CHORDS),
    TREMOLO("Tremolo", false, 0, StyleType.CHORDS),
    TREMOLO_5("Tremolo 5", false, 0, StyleType.CHORDS),
    TREMOLO_6("Tremolo 6", false, 0, StyleType.CHORDS),
    CONTROTEMPO("Controtempo", false, 20, StyleType.CHORDS),
    CONTROTEMPO_4("Controtempo 4", false, 20, StyleType.CHORDS),
    CONTROTEMPO_6("Controtempo 6", false, 20, StyleType.CHORDS),
    CONTROTEMPO_8("Controtempo 8", false, 20, StyleType.CHORDS),
    CONTROTEMPO_10("Controtempo 10", false, 20, StyleType.CHORDS),
    CONTROTEMPO_12("Controtempo 12", false, 20, StyleType.CHORDS),

    BICINIUM("Bicinium", true, 10, StyleType.TEXTURES),
    TRICINIUM("Tricinium", true, 10, StyleType.TEXTURES),
    POLIRITMIA("Poliritmia", true, 8, StyleType.TEXTURES),
    POLIRITMIA_2("Poliritmia 2", true, 8, StyleType.TEXTURES),
    POLIRITMIA_3("Poliritmia 3", true, 8, StyleType.TEXTURES),

    SINCOPATO("Sincopato", true, 26, StyleType.PATTERNS),
    ALBERTI("Alberti", true, 26, StyleType.PATTERNS),
    RICAMATO("Ricamato", true, 26, StyleType.PATTERNS),
    RICAMATO_6("Ricamato 6", true, 26, StyleType.PATTERNS),
    RICAMATO_8("Ricamato 8", true, 26, StyleType.PATTERNS),
    RICAMATO_10("Ricamato 10", true, 26, StyleType.PATTERNS),
    RICAMATO_12("Ricamato 12", true, 26, StyleType.PATTERNS),
    ACCIACCATURA("Acciaccatura", true, 0, StyleType.PATTERNS),
    ACCIACCATURA_2("Acciaccatura 2", true, 0, StyleType.PATTERNS),
    BERLINESE("Berlinese", true, 0, StyleType.PATTERNS),
    BERLINESE_2("Berlinese 2", true, 0, StyleType.PATTERNS),
    BERLINESE_4("Berlinese 4", true, 0, StyleType.PATTERNS),

    LINEA("Linea", true, 12, StyleType.LINES),
    ACCUMULO("Accumulo", true, 12, StyleType.LINES),
    FLUSSO("Flusso", true, 12, StyleType.LINES),
    ACCUMULO_FLUSSO("Flussaccumulo", true, 12, StyleType.LINES),

    TRILLO("Trillo", false, 12, StyleType.AERIALS),
    TRILLO_2("Trillo 2", true, 12, StyleType.AERIALS),
    ARPEGGIO("Arpeggio", true, 24, StyleType.AERIALS),
    SCAMBIARPEGGIO("Scambiarpeggio", true, 24, StyleType.AERIALS),
    PASSAGGIO("Passaggio", true, 24, StyleType.AERIALS),
    CAPRICCIO("Capriccio", true, 26, StyleType.AERIALS),
    CAPRICCIO_2("Capriccio 2", true, 26, StyleType.AERIALS),
    FARFALLA("Farfalla", true, 12, StyleType.AERIALS),
    GRAZIOSO_3("Grazioso 3", true, 12, StyleType.AERIALS),
    GRAZIOSO_4("Grazioso 4", true, 12, StyleType.AERIALS),
    RADIO("Radio", false, 12, StyleType.AERIALS),
    RADIO_FLUSSO("Radioflusso", false, 12, StyleType.AERIALS),

}

val starredChordsInstruments = listOf(
    STRING_ORCHESTRA, HAMMOND_ORGAN, ACCORDION,
    TREMOLO_STRINGS,
    SYNTH_STRINGS_1, SYN_FANTASIA, BRASS_ENSEMBLE,
    62,63, 52, 53,54,
    //80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,99,100,101,102,103
)
val chordsInstruments = (0..127).toList()

fun List<Int>.convertToOctavesByte(): Int {
    //println("Octave list: $this")
    return this.fold(0){ byte, octave ->
        byte or (1 shl(octave))
    }
        //.also{println("Converted to: "+ it.toString(2))}
}
data class HarmonizationData(val type: HarmonizationType = HarmonizationType.NONE,
                             val instrument: Int = 48, val volume: Float = 0.1f,
                             val style: HarmonizationStyle = HarmonizationStyle.ACCORDO,
                             val octavesByte: Int = 248, val direction: HarmonizationDirection = HarmonizationDirection.ASCENDING){ // from octave 3 to 7 (numbers 4 to 8)
    fun describe(): String {
        val direction = if(this.style.hasDirection) this.direction.symbol else ""
        return if(type == HarmonizationType.NONE) "  ---  ${this.type.title}  ---"
        else "${this.type.title} ${this.style.title}${direction}\n  ${ListaStrumenti.getNameByIndex(this.instrument)} ${this.describeOctaves()} ${String.format("%.0f%%",this.volume*100)}"
    }
    fun toCsv(): String {
        return "${this.type.ordinal}|${this.instrument}|${this.volume}|${this.style.ordinal}|${this.octavesByte}|${this.direction.ordinal}"
    }
    fun convertFromOctavesByte(): List<Int>{
        //println("Octaves Byte: " + octavesByte.toString(2))
        return IntRange(0,7).filter { position -> octavesByte and (1 shl position) > 0 }
            //.also{ println("Converted to: $it") }
    }
    fun describeOctaves(): String {
        if(this.octavesByte == 254) return "∞"
        return convertFromOctavesByte().joinToString("", "[", "]") { it.toString() }
    }
    companion object{
        fun getHarmonizationsCsvValues(csv: String): Triple<String, String, String>{
            if(csv.isBlank()) return Triple("", "", "")
            val values = csv.split("~")
            return Triple(values[0], values.getOrElse(1){""}, values.getOrElse(2){""})
        }
        fun getHarmonizationsTriple(csv: String): Triple<List<HarmonizationData>, List<HarmonizationData>, List<HarmonizationData>>{
            val triple = getHarmonizationsCsvValues(csv)
            return Triple(createHarmonizationsFromCsv(triple.first), createHarmonizationsFromCsv(triple.second), createHarmonizationsFromCsv(triple.third))
        }
        fun buildHarmonizationTriple(csv1: String, csv2: String, csv3: String): String {
            return "$csv1~$csv2~$csv3"
        }
        fun createHarmonizationsFromCsv(csv: String): List<HarmonizationData>{
            //println("Harmonization csv: $csv")
            if(csv.isBlank()) return listOf()
            val values = csv.split(",")
            val harmValues = HarmonizationType.values()
            val styleValues = HarmonizationStyle.values()
            val directionValues = HarmonizationDirection.values()
            return values.map{
                val subValues = it.split("|")
                HarmonizationData(harmValues[subValues[0].toInt()], subValues[1].toInt(),
                    subValues[2].toFloat(), styleValues[subValues.getOrElse(3){"0"}.toInt()],
                    subValues.getOrElse(4){"248"}.toInt(),
                    directionValues[subValues.getOrElse(5){"0"}.toInt()]
                )
            }
        }
    }
}

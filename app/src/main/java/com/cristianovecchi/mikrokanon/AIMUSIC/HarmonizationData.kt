package com.cristianovecchi.mikrokanon.AIMUSIC


enum class HarmonizationType(val title: String) {
    NONE("No Harm."),
    POP("POP"), POP7("POP 7"),
    LIBERTY("LIBERTY"),
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
    CHORDS, TEXTURES, PATTERNS, LINES, AERIALS, ALEA
}

enum class HarmonizationStyle(val title: String, val hasDirection: Boolean, val hasFlow: Boolean, val increase: Int, val type: StyleType, val maxDensity: Int = 1) {
    ACCORDO("Accordo", false, false,0, StyleType.CHORDS),
   // DRAMMATICO("Drammatico", false, false,0, StyleType.CHORDS),
    RIBATTUTO("Ribattuto", false, false,0, StyleType.CHORDS, 6),
    //RIBATTUTO_3("Ribattuto 3", false, false,0, StyleType.CHORDS),
    //TREMOLO("Tremolo", false, false,0, StyleType.CHORDS),
    //TREMOLO_5("Tremolo 5", false, false,0, StyleType.CHORDS),
    //TREMOLO_6("Tremolo 6", false, false,0, StyleType.CHORDS),
    CONTROTEMPO("Controtempo", false, false,8, StyleType.CHORDS, 6),
//    CONTROTEMPO_4("Controtempo 4", false, false,20, StyleType.CHORDS),
//    CONTROTEMPO_6("Controtempo 6", false, false,20, StyleType.CHORDS),
//    CONTROTEMPO_8("Controtempo 8", false, false,20, StyleType.CHORDS),
//    CONTROTEMPO_10("Controtempo 10", false, false,20, StyleType.CHORDS),
//    CONTROTEMPO_12("Controtempo 12", false, false,20, StyleType.CHORDS),

    BICINIUM("Bicinium", true, true,10, StyleType.TEXTURES),
    TRICINIUM("Tricinium", true, true,10, StyleType.TEXTURES),
    POLIRITMIA("Poliritmia", true, true,10, StyleType.TEXTURES, 3),
//    POLIRITMIA_2("Poliritmia 2", true, true,8, StyleType.TEXTURES),
//    POLIRITMIA_3("Poliritmia 3", true, true,8, StyleType.TEXTURES),

    SINCOPATO("Sincopato", true, true,26, StyleType.PATTERNS),
    ALBERTI("Alberti", true, true,26, StyleType.PATTERNS),
    RICAMATO("Ricamato", true, true,16, StyleType.PATTERNS, 5),
//    RICAMATO_6("Ricamato 6", true, true,26, StyleType.PATTERNS),
//    RICAMATO_8("Ricamato 8", true, true,26, StyleType.PATTERNS),
//    RICAMATO_10("Ricamato 10", true, true,26, StyleType.PATTERNS),
//    RICAMATO_12("Ricamato 12", true, true,26, StyleType.PATTERNS),
    ACCIACCATURA("Acciaccatura", true, false,0, StyleType.PATTERNS, 2),
   // ACCIACCATURA_2("Acciaccatura 2", true, false,0, StyleType.PATTERNS),
    BERLINESE("Berlinese", true, false,0, StyleType.PATTERNS, 3),
//    BERLINESE_2("Berlinese 2", true, false,0, StyleType.PATTERNS),
//    BERLINESE_4("Berlinese 4", true, false,0, StyleType.PATTERNS),

    LINEA("Linea", true, true,12, StyleType.LINES),
    ACCUMULO("Accumulo", true, true,12, StyleType.LINES),
    //FLUSSO("Flusso", true, true,12, StyleType.LINES),
    //ACCUMULO_FLUSSO("Flussaccumulo", true,true, 12, StyleType.LINES),
    ECO("Eco", true, true,6, StyleType.LINES, 6),
//    ECO_2("Eco 2", true, true,6, StyleType.LINES),
//    ECO_3("Eco 3", true, true,6, StyleType.LINES),
//    ECO_4("Eco 4", true, true,6, StyleType.LINES),
//    ECO_5("Eco 5", true, true,6, StyleType.LINES),
//    ECO_6("Eco 6", true, true,6, StyleType.LINES),

    TRILLO("Trillo", false, true,0, StyleType.AERIALS),
    TUTTITRILLI("Tuttitrilli", true, true,0, StyleType.AERIALS),
    ARPEGGIO("Arpeggio", true, false,12, StyleType.AERIALS),
    SCAMBIARPEGGIO("Scambiarpeggio", true,false, 12, StyleType.AERIALS),
    PASSAGGIO("Passaggio", true, true,12, StyleType.AERIALS),
    CAPRICCIO("Capriccio", true, true,14, StyleType.AERIALS, 2),
    //CAPRICCIO_2("Capriccio 2", true, true,26, StyleType.AERIALS),
    FARFALLA("Farfalla", true, true,12, StyleType.AERIALS),
    GRAZIOSO("Grazioso", true, true,12, StyleType.AERIALS, 2),
   // GRAZIOSO_4("Grazioso 4", true, true,12, StyleType.AERIALS),

    RADIO("Radio", false, true,18, StyleType.ALEA),
    //RADIO_FLUSSO("Radioflusso", false, true,12, StyleType.ALEA),
    NEVE("Neve", true, false,24, StyleType.ALEA, 2),
    //NEVE_2("Neve 2", true, false,24, StyleType.ALEA),

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
                             val instruments: List<Int> = listOf(48), val volume: Float = 0.1f,
                             val style: HarmonizationStyle = HarmonizationStyle.ACCORDO,
                             val octavesByte: Int = 248, val direction: HarmonizationDirection = HarmonizationDirection.ASCENDING,
                             val isFlow: Boolean = false, var density: Int = 1){ // from octave 3 to 7 (numbers 4 to 8)
    init {
        val maxDensity = style.maxDensity
        density = if(density > maxDensity) maxDensity else density
    }
    fun describe(): String {
        val direction = if(this.style.hasDirection) this.direction.symbol else ""
        val withFlow = if(this.style.hasFlow && isFlow) "§" else ""
        val densityString = if(density < 2) "" else "^$density"
        val instrumentList = instruments.joinToString(", ") { ListaStrumenti.getNameByIndex(it) }
        return if(type == HarmonizationType.NONE) "  ---  ${this.type.title}  ---"
        else "${this.type.title} ${this.style.title}$densityString ${withFlow}${direction}\n  $instrumentList ${this.describeOctaves()} ${String.format("%.0f%%",this.volume*100)}"
    }
    fun toCsv(): String {
        val flowBool = if(isFlow) 1 else 0
        val instrumentCsv = instruments.joinToString("§") { it.toString() }
        return "${this.type.ordinal}|${instrumentCsv}|${this.volume}|${this.style.ordinal}|${this.octavesByte}|${this.direction.ordinal}|$flowBool|$density"
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
                val instrumentList = subValues[1].split("§").map{ it.toInt() }
                val flowCsv = subValues.getOrElse(6) { "0" }
                val densityInt = subValues.getOrElse(7){ "1" }.toInt()

                HarmonizationData(harmValues[subValues[0].toInt()], instrumentList,
                    subValues[2].toFloat(), styleValues[subValues.getOrElse(3){"0"}.toInt()],
                    subValues.getOrElse(4){"248"}.toInt(),
                    directionValues[subValues.getOrElse(5){"0"}.toInt()],
                    flowCsv != "0",
                    densityInt
                )
            }
        }
    }
}

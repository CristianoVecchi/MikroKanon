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
}
enum class HarmonizationStyle(val title: String, val hasDirection: Boolean) {
    ACCORDI("Accordi", false),
    DRAMMATICO("Drammatico", false),
    RIBATTUTO("Ribattuto", false),
    RIBATTUTO_3("Ribattuto 3", false),
    TREMOLO("Tremolo", false),
    TREMOLO_5("Tremolo 5", false),
    TREMOLO_6("Tremolo 6", false),
    SINCOPATO("Sincopato", true),
    CONTROTEMPO("Controtempo", false),
    CONTROTEMPO_4("Controtempo 4", false),
    CONTROTEMPO_6("Controtempo 6", false),
    CONTROTEMPO_8("Controtempo 8", false),
    CONTROTEMPO_10("Controtempo 10", false),
    CONTROTEMPO_12("Controtempo 12", false),
    ALBERTI("Alberti", true),
    RICAMATO("Ricamato", true),
    RICAMATO_6("Ricamato 6", true),
    RICAMATO_8("Ricamato 8", true),
    RICAMATO_10("Ricamato 10", true),
    RICAMATO_12("Ricamato 12", true),
    TRILLO("Trillo", false),
    ARPEGGIO("Arpeggio", true),
    CAPRICCIO("Capriccio", true),
    LINEA("Linea", true),
    ACCUMULO("Accumulo", true),
    FLUSSO("Flusso", true),
    ACCUMULO_FLUSSO("Flussaccumulo", true),
    BICINIUM("Bicinium", true),
    TRICINIUM("Tricinium", true)
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
                             val style: HarmonizationStyle = HarmonizationStyle.ACCORDI,
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

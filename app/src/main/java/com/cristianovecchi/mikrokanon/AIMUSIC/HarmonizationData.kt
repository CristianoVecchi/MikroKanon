package com.cristianovecchi.mikrokanon.AIMUSIC


enum class HarmonizationType(val title: String) {
    NONE("No Harm."),
    POP("POP"), POP7("POP 7"),
    JAZZ("JAZZ"), JAZZ11("JAZZ 11"),
    XWH("XW HARMONY"), FULL12("FULL 12")
}
enum class HarmonizationStyle(val title: String) {
    CHORDS("Chords"),
    DRAMMATICO("Drammatico"),
    RIBATTUTO("Ribattuto"),
    TREMOLO("Tremolo"),
    ASCENDING_ARPEGGIO("Arpeggio➚"),
    DESCENDING_ARPEGGIO("Arpeggio➘"),
    ASCENDING_LINE("Line➚"),
    DESCENDING_LINE("Line➘"),
    RANDOM_LINE("Line~"),
    ASCENDING_FLOW("Flow➚"),
    DESCENDING_FLOW("Flow➘"),
    RANDOM_FLOW("Flow~"),
    ASCENDING_BICINIUM("Bicinium➚"),
    DESCENDING_BICINIUM("Bicinium➘"),
    RANDOM_BICINIUM("Bicinium~")

}

val starredChordsInstruments = listOf(
    STRING_ORCHESTRA, HAMMOND_ORGAN, ACCORDION,
    TREMOLO_STRINGS,
    SYNTH_STRINGS_1, SYN_FANTASIA, BRASS_ENSEMBLE,
    62,63, 52, 53,54,
    //80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,99,100,101,102,103
)
val chordsInstruments = (0..103).toList()

fun List<Int>.convertToOctavesByte(): Int {
    println("Octave list: $this")
    return this.fold(0){ byte, octave ->
        byte or (1 shl(octave))
    }
        .also{println("Converted to: "+ it.toString(2))}
}
data class HarmonizationData(val type: HarmonizationType = HarmonizationType.NONE,
                             val instrument: Int = 48, val volume: Float = 0.1f,
                             val style: HarmonizationStyle = HarmonizationStyle.CHORDS,
                             val octavesByte: Int = 248){ // from octave 3 to 7 (numbers 4 to 8)
    fun describe(): String {
        return if(type == HarmonizationType.NONE) "  ---  ${this.type.title}  ---"
        else "${this.type.title} ${this.style.title}\n  ${ListaStrumenti.getNameByIndex(this.instrument)} ${this.describeOctaves()} ${String.format("%.0f%%",this.volume*100)}"
    }
    fun toCsv(): String {
        return "${this.type.ordinal}|${this.instrument}|${this.volume}|${this.style.ordinal}|${this.octavesByte}"
    }
    fun convertFromOctavesByte(): List<Int>{
        println("Octaves Byte: " + octavesByte.toString(2))
        return IntRange(0,7).filter { position -> octavesByte and (1 shl position) > 0 }
            .also{ println("Converted to: $it") }
    }
    fun describeOctaves(): String {

        if(this.octavesByte == 254) return "∞"
        return convertFromOctavesByte().joinToString("", "[", "]") { it.toString() }
    }
    companion object{
        fun createHarmonizationsFromCsv(csv: String): List<HarmonizationData>{
            println("Harmonization csv: $csv")
            if(csv.isBlank()) return listOf()
            val values = csv.split(",")
            val harmValues = HarmonizationType.values()
            val styleValues = HarmonizationStyle.values()
            return values.map{
                val subValues = it.split("|")
                HarmonizationData(harmValues[subValues[0].toInt()], subValues[1].toInt(),
                    subValues[2].toFloat(), styleValues[subValues.getOrElse(3){"0"}.toInt()],
                    subValues.getOrElse(4){"248"}.toInt())
            }
        }

    }
}

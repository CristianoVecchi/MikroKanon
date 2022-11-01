package com.cristianovecchi.mikrokanon.AIMUSIC


enum class HarmonizationType(val title: String) {
    NONE("No Harm."),
    POP("POP"), POP7("POP 7"),
    JAZZ("JAZZ"), JAZZ11("JAZZ 11"),
    XWH("XW HARMONY"), FULL12("FULL 12")
}
val starredChordsInstruments = listOf(
    STRING_ORCHESTRA, HAMMOND_ORGAN, ACCORDION,
    TREMOLO_STRINGS,
    SYNTH_STRINGS_1, SYN_FANTASIA, BRASS_ENSEMBLE,
    62,63, 52, 53,54,
    //80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,99,100,101,102,103
)
val chordsInstruments = (0..103).toList()

data class HarmonizationData(val type: HarmonizationType = HarmonizationType.NONE,
                             val instrument: Int = 48, val volume: Float = 0.1f){
    fun describe(): String {
        return if(type == HarmonizationType.NONE) "  ---  ${this.type.title}  ---"
        else "  ---  ${this.type.title}  ---\n${ListaStrumenti.getNameByIndex(this.instrument)} ${String.format("%.0f%%",this.volume*100)}"
    }
    fun toCsv(): String {
        return "${this.type.ordinal}|${this.instrument}|${this.volume}"
    }
    companion object{
        fun createHarmonizationsFromCsv(csv: String): List<HarmonizationData>{
            if(csv.isBlank()) return listOf()
            val values = csv.split(",")
            val harmValues = HarmonizationType.values()
            return values.map{
                val subValues = it.split("|")
                HarmonizationData(harmValues[subValues[0].toInt()], subValues[1].toInt(), subValues[2].toFloat())
            }
        }
    }
}

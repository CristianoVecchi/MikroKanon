package com.cristianovecchi.mikrokanon.AIMUSIC

import com.cristianovecchi.mikrokanon.convertIntsToFlags

data class ChordToEnhanceData(val absPitches: Set<Int>,
                               val repetitions: Int, val isSubSet: Boolean = false) {

    fun describe(noteNames: List<String>): String {
        return if(isSubSet) {
            "x$repetitions: ${absPitches.joinToString(" ", "[ ... ", " ]") { noteNames[it] }}"
        } else {
            "x$repetitions: ${absPitches.joinToString(" ") { noteNames[it] }}"
        }
    }
    fun toCsv(): String {
        return "${
            convertIntsToFlags(this.absPitches)
        }|${this.repetitions}|${if(isSubSet) "1" else "0"}"
    }
}
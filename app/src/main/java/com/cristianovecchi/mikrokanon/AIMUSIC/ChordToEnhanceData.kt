package com.cristianovecchi.mikrokanon.AIMUSIC

import com.cristianovecchi.mikrokanon.convertIntsToFlags

data class ChordToEnhanceData(val absPitches: Set<Int>,
                               val repetitions: Int) {

    fun describe(noteNames: List<String>): String {
        return "x$repetitions: ${absPitches.joinToString(" ") { noteNames[it] }}"
    }

    fun toCsv(): String {
        return "${
            convertIntsToFlags(this.absPitches)
        }|${this.repetitions}"
    }
}
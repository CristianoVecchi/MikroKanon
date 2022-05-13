package com.cristianovecchi.mikrokanon.AIMUSIC

enum class CheckType(val title: String) {
    NONE("No action"), LONGER(">=")//, ALONE("].[ >=")
}
enum class ReplaceType(val title: String) {
   MORDENTE("Mordente")// , GRUPPETTO("Gruppetto"), TRILLO("Trillo")
}
data class CheckAndReplaceData(val check: CheckType = CheckType.NONE,
                               val checkValues: List<Int> = emptyList(),
                               val replace: ReplaceType = ReplaceType.MORDENTE,
                               val replaceValues: List<Int> = emptyList())

fun provideCheckFunction(checkType: CheckType, checkValues: List<Int>): (TrackData, Int) -> Boolean {
    return when(checkType){
        CheckType.NONE -> { _, _ -> false }
        CheckType.LONGER -> { trackData, index -> trackData.durations[index] >= checkValues[0] }
        //CheckType.ALONE -> TODO()
    }
}

fun provideReplaceFunction(replaceType: ReplaceType, replaceValues: List<Int>):
            (TrackData, Int) -> SubstitutionNotes {
    return when(replaceType){
        ReplaceType.MORDENTE -> { trackData, index ->
            val (pitch, thick, duration, velocity, glissando, attack, isPreviousRest, articulationDuration, ribattuto) = trackData.extractNoteDataAtIndex(index)
            val dur = 60 // 30 = 1/64  60 = 1/32  120 = 1/16  240 = 1/8  480 = 1/4
            val actualDuration = articulationDuration ?: duration + 1
            if(pitch == 21 || actualDuration < dur * 3) { // fake substitution
                //println("No Subs: actual duration = $actualDuration")
                SubstitutionNotes(-1 ) // will not be considered
            } else {
                val stress = replaceValues[0]
                val stressedVelocity = if(velocity + stress > 127) 127 else velocity + stress
                SubstitutionNotes(index, listOf(pitch, pitch-1, pitch), listOf(thick, thick + dur, thick + dur * 2),
                    listOf(dur, dur , duration - dur * 2),
                    listOf(stressedVelocity, velocity, velocity), listOf(glissando, glissando, glissando),
                    listOf(attack, attack, attack, ), listOf(isPreviousRest, false, false),
                    if(articulationDuration == null) null else listOf(dur,dur, articulationDuration - dur * 2),
                    if(ribattuto == null) null else listOf(ribattuto,ribattuto,ribattuto) )
            }
        }

    }
}
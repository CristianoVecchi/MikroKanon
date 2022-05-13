package com.cristianovecchi.mikrokanon.AIMUSIC


data class SubstitutionNotes(val index: Int, val newPitches: List<Int> = emptyList(),
                             val newTicks: List<Int> = emptyList(),
                             val newDurations: List<Int> = emptyList(), val newVelocities: List<Int> = emptyList(),
                             val newGlissando: List<Int> = emptyList(), val newAttacks: List<Int> = emptyList(),
                             val newIsPreviousRest: List<Boolean> = emptyList(),
                             val newArticulationDurations: List<Int>? = null,
                             val newRibattutos: List<Int>? = null)

data class NoteData(val pitch: Int, val tick: Int, val duration: Int, val velocity:Int, val glissando: Int,
val attack:Int, val isPreviousRest: Boolean, val articulationDuration: Int?, val ribattuto: Int?)

data class TrackData(val pitches: IntArray, val ticks: IntArray, var durations: IntArray,
                     val velocities: IntArray,val glissando: IntArray,  val attacks: IntArray,
                     val isPreviousRest: BooleanArray,
                     var articulationDurations: IntArray? = null,
                     var ribattutos: IntArray? = null,
                     val channel: Int,  val velocityOff: Int = 80,
                     val vibrato: Int, val doublingFlags: Int = 0,
                     val audio8D: Boolean = false, val partIndex: Int,
                     val changes: List<TickChangeData> = listOf()  )// tick + instrument
{
    fun extractNoteDataAtIndex(index: Int): NoteData{
        return NoteData(pitches[index], ticks[index], durations[index], velocities[index], glissando[index],
        attacks[index], isPreviousRest[index],
        if(articulationDurations == null) null else articulationDurations!![index],
        if(ribattutos == null) null else ribattutos!![index] )
    }
    fun checkAndReplace(checkAndReplaceData: CheckAndReplaceData,
                        start: Int = 0, end: Int = this.pitches.size-1 ): TrackData {

        val check = provideCheckFunction(checkAndReplaceData.check, checkAndReplaceData.checkValues)
        val replace = provideReplaceFunction(checkAndReplaceData.replace, checkAndReplaceData.replaceValues)

        val substitutions = mutableListOf<SubstitutionNotes>()
        (start..end).forEach{ index ->
            if(check(this, index)) substitutions.add(
                replace(this, index)
            )
        }
        return this.substitueNote(substitutions)
    }
    fun substitueNote(substitutionNotes: List<SubstitutionNotes>): TrackData {
        var subsIndex = 0
        val pitchesData = mutableListOf<Int>()
        val ticksData = mutableListOf<Int>()
        val durationsData = mutableListOf<Int>()
        val velocitiesData = mutableListOf<Int>()
        val glissandoData = mutableListOf<Int>()
        val previousIsRestData = mutableListOf<Boolean>()
        val artDurData = mutableListOf<Int>()
        val ribattutosData = mutableListOf<Int>()

        for(noteIndex in pitches.indices){
            if(subsIndex < substitutionNotes.size && noteIndex == substitutionNotes[subsIndex].index){
                val subs = substitutionNotes[subsIndex]
                pitchesData.addAll(subs.newPitches)
                ticksData.addAll(subs.newTicks)
                durationsData.addAll(subs.newDurations)
                velocitiesData.addAll(subs.newVelocities)
                glissandoData.addAll(subs.newGlissando)
                previousIsRestData.addAll(subs.newIsPreviousRest)
                articulationDurations?.let{
                    artDurData.addAll(subs.newArticulationDurations!!)
                }
                ribattutos?.let{
                    ribattutosData.addAll(subs.newRibattutos!!)
                }
                subsIndex++
            } else {
                pitchesData.add(pitches[noteIndex])
                ticksData.add(ticks[noteIndex])
                durationsData.add(durations[noteIndex])
                velocitiesData.add(velocities[noteIndex])
                glissandoData.add(glissando[noteIndex])
                previousIsRestData.add(isPreviousRest[noteIndex])
                articulationDurations?.let{
                    artDurData.add(articulationDurations!![noteIndex])
                }
                ribattutos?.let{
                    ribattutosData.add(ribattutos!![noteIndex])
                }
            }
        }


        return TrackData(pitchesData.toIntArray(), ticksData.toIntArray(), durationsData.toIntArray(),
            velocitiesData.toIntArray(), glissandoData.toIntArray(), IntArray(pitchesData.size),
            previousIsRestData.toBooleanArray(),
            if(this.articulationDurations == null) null else artDurData.toIntArray(),
            if(this.ribattutos == null) null else ribattutosData.toIntArray(),
            channel, 80, vibrato, doublingFlags,
            audio8D, partIndex, changes)
    }
}


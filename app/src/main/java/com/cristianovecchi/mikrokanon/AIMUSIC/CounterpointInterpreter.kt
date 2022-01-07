package com.cristianovecchi.mikrokanon.AIMUSIC


import android.os.Build
import androidx.annotation.RequiresApi
import com.cristianovecchi.mikrokanon.findMelodyWithStructure
import java.util.stream.Collectors

fun findTopNuances(stabilities: List<Float>, minNuance: Float, maxNuance: Float) : List<Float>{
    val n = stabilities.size
    val step = (maxNuance - minNuance) / n
    val steps = (0 until n).map{ maxNuance - (step * it)}
    val orderedStabilities = stabilities.sorted()
    return (0 until n).map{ steps[orderedStabilities.indexOf(stabilities[it])]}
}
data class TrackData(val pitches: IntArray, val ticks: IntArray, var durations: IntArray,
                     val velocities: IntArray,val glissando: IntArray,  val attacks: IntArray,
                     val isPreviousRest: BooleanArray,
                     var articulationDurations: IntArray? = null,
                     val channel: Int,  val velocityOff: Int = 80,
                     val vibrato: Int, val doublingFlags: Int = 0, val instrument: Int = 0,
                     val audio8D: Boolean = false, val partIndex: Int )

object CounterpointInterpreter {
    fun doTheMagic(counterpoint: Counterpoint,
                   durations: List<Int> = listOf(240), // 1/8
                   ensembleParts: List<EnsemblePart>,
                   nuances: Int,
                   doublingFlags: Int,
                   rangeTypes: List<Pair<Int,Int>>,
                   melodyTypes: List<Int>,
                   glissando: List<Int> = listOf(),
                   audio8D: List<Int> = listOf(),
                   vibrato: Int = 0
        ): List<TrackData> {
//        counterpoint.display()
//        durations.also{println("Durations: $it")}
        //var result = mutableListOf<TrackData>()

        if (counterpoint.parts.size > 15) {
            println("WARNING: Counterpoint n. parts: ${counterpoint.parts.size}")
        }

        counterpoint.parts.forEachIndexed() { i, part ->
            part.index = i
        }

        val computation = { part: AbsPart ->
            val partIndex = part.index!!
            val isUpperPart = partIndex < counterpoint.parts.size / 2
            val ensemblePart = ensembleParts[partIndex]
            val channel =
                if (partIndex < 9) partIndex else partIndex + 1 // skip percussion midi channel

            val pitchesData = mutableListOf<Int>()
            val ticksData = mutableListOf<Int>()
            val durationsData = mutableListOf<Int>()
            val velocitiesData = mutableListOf<Int>()
            val glissandoData = mutableListOf<Int>()
            val previousIsRestData = mutableListOf<Boolean>()


//            if(glissando.isNotEmpty()){
//                val omniOff = Controller(0, channel,124,0)
//                val omniOn = Controller(0, channel,125,0)
//                val monophonicOn = Controller(0, channel,126,1)
//                track.insertEvent(omniOff)
//                track.insertEvent(monophonicOn)
//            }
            var tick = 0
            var index = 0
            var durIndex = 0

            // RANGES EXTENSION
            val ranges = rangeTypes.map { ensemblePart.getOctavedRangeByType(it.first, it.second, isUpperPart) }
            val octaveTranspose = when(rangeTypes[0].second){
                3 -> if(isUpperPart) 1 else -1
                4 -> if(isUpperPart) 2 else -2
                else -> rangeTypes[0].second
            }
            val startOctave = (ensemblePart.octave + octaveTranspose).coerceIn(0, 8)
            //ACTUAL PITCHES
            val actualPitches = if (melodyTypes.size == 1 && rangeTypes.size == 1) {
                Insieme.findMelody(
                    startOctave,
                    part.absPitches.toIntArray(),
                    ranges[0].first,
                    ranges[0].last,
                    melodyTypes[0]
                )
            } else {
                findMelodyWithStructure(startOctave, part.absPitches.toIntArray(),
                    ranges.map { it.first }.toIntArray(), ranges.map { it.last }.toIntArray(),
                    melodyTypes.toIntArray()
                )
            }

            // NUANCES
            val lowLimit = 0.4f
            val minNuance = 0.51f
            val maxNuance = 0.95f
            val stabilities = counterpoint.findStabilities()
            val velocities: IntArray = when (nuances) {
                1 -> {
                    val mssq = MelodySubSequencer(actualPitches)
                    val topNuances = findTopNuances(stabilities, minNuance, maxNuance)
                    mssq.assignVelocities(topNuances[partIndex], lowLimit)
                    mssq.velocities
                }
                2 -> {
                    val mssq = MelodySubSequencer(actualPitches)
                    val topNuances = findTopNuances(stabilities, maxNuance, minNuance)
                    mssq.assignVelocities(topNuances[partIndex], lowLimit)
                    mssq.velocities
                }
                else -> IntArray(actualPitches.size) { 100 } // case 0: no Nuances
            }

            // ADDING NOTES
            val glissandoChecks =
                Insieme.checkIntervalsInPitches(actualPitches, glissando.toIntArray())
            var isPreviousRest = true
            while (index < actualPitches.size) {

                val pitch = actualPitches[index]
                val velocity = velocities[index]
                var gliss = glissandoChecks[index]
                //println("pitch: $pitch vel: $velocity")
                var dur = durations[durIndex % durations.size]
                if (dur < 0) { // negative values are considered as rests
                    dur *= -1
                    tick += dur
                    durIndex++
                } else {
                    if (pitch != -1) {
                        while (index + 1 < actualPitches.size && actualPitches[index + 1] == pitch) {
                            var nextDur = durations[(durIndex + 1) % durations.size]
                            gliss = glissandoChecks[index + 1]
                            if (nextDur < 0) {
                                gliss = 0
                                nextDur *= -1
                                dur += nextDur
                                durIndex++
                            } else {

                                dur += nextDur
                                index++
                                durIndex++
                            }
                        }
                        ticksData.add(tick)
                        durationsData.add(dur)
                        pitchesData.add(pitch)
                        velocitiesData.add(velocity)
                        glissandoData.add(gliss)
                        previousIsRestData.add(isPreviousRest)
                        isPreviousRest = false
                    } else {
                        isPreviousRest = true
                    }
                    tick += dur
                    index++
                    durIndex++
                }
            }

            TrackData(
                pitchesData.toIntArray(), ticksData.toIntArray(), durationsData.toIntArray(),
                velocitiesData.toIntArray(), glissandoData.toIntArray(), IntArray(pitchesData.size),
                previousIsRestData.toBooleanArray(),null,
                channel, 80, vibrato, doublingFlags,
                ensemblePart.instrument, audio8D.contains(partIndex), partIndex
            )
        }
        // CREATION OF TRACKS
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            counterpoint.parts.parallelStream().map { part ->
                computation(part)
            }.collect(Collectors.toList()).sortedBy { it.channel }
        } else {
            counterpoint.parts.map { part ->
                computation(part)
            }.sortedBy { it.channel }
        }

    }
}

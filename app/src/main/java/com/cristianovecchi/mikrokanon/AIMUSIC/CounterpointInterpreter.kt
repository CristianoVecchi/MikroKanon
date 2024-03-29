package com.cristianovecchi.mikrokanon.AIMUSIC


import android.os.Build
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.combineRangesAndEnsembleParts
import com.cristianovecchi.mikrokanon.findMelodyWithStructure
import kotlinx.coroutines.job
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.util.stream.Collectors
import kotlin.coroutines.CoroutineContext
import kotlin.math.absoluteValue

fun findTopNuances(stabilities: List<Float>, minNuance: Float, maxNuance: Float) : List<Float>{
    val n = stabilities.size
    val step = (maxNuance - minNuance) / n
    val steps = (0 until n).map{ maxNuance - (step * it)}
    val orderedStabilities = stabilities.sorted()
    return (0 until n).map{ steps[orderedStabilities.indexOf(stabilities[it])]}
}
data class ChangeData(val noteIndex: Int, val instrument: Int)
data class TickChangeData(val tick: Long, val instrument: Int, val noteIndex: Int)



object CounterpointInterpreter {
    fun calculateTotalLengthForEmptyCounterpoint(counterpoint: Counterpoint, durations: IntArray = intArrayOf(240) ): Long {
        var result = 0
        val nNotes = counterpoint.maxSize()
        var noteIndex = 0
        var durIndex = 0
        while(noteIndex < nNotes){
            val dur = durations[durIndex]//.also{println("new dur: $it")}
            if(dur > 0) {
                result += dur
                durIndex++
                noteIndex++
                while(durations[durIndex] < 0){
                    result += durations[durIndex].absoluteValue
                    durIndex++
                }
            } else {
                result += dur.absoluteValue
                durIndex++
            }
        }
        return result.toLong()
    }
    suspend fun doTheMagic(context: CoroutineContext,
                           dispatch: (Triple<AppViewModel.Building, Int, Int>) -> Unit,
                           counterpoint: Counterpoint,
                           durations: IntArray = intArrayOf(240), // 1/8
                           ensemblePartList: List<List<EnsemblePart>>,
                           nuances: Int,
                           doublingFlags: Int,
                           rangeTypes: List<Pair<Int,Int>>,
                           melodyTypes: List<Int>,
                           glissando: List<Int> = listOf(),
                           audio8D: List<Int> = listOf(),
                           vibrato: Int = 0
        ): List<TrackData> = withContext(context) {
//        counterpoint.display()
//        durations.also{println("Durations: $it")}
        //delay(1)
        yield()
        val durSize = durations.size
        val actualDurations =
            IntArray(counterpoint.nNotes() * 2 + 1)// note + optional rests + optional initial rest
        (0 until counterpoint.nNotes() * 2 + 1).forEach {
            actualDurations[it] = durations[it % durSize]
        }
        if (counterpoint.parts.size > 12) {
            println("WARNING: Counterpoint n. parts: ${counterpoint.parts.size}")
        }

        counterpoint.parts.forEachIndexed { i, part ->
            part.index = i
        }
        val job = context.job
        val computation = { part: AbsPart ->
            if (job.isActive) {
                val partIndex = part.index!!
                var changeIndex = 0
                val isUpperPart = partIndex < counterpoint.parts.size / 2
                val ensemblePartSequence = ensemblePartList.map { it[partIndex] }
                val channel =
                    if (partIndex < 9) partIndex else partIndex + 1 // skip percussion midi channel

                val pitchesData = mutableListOf<Int>()
                val ticksData = mutableListOf<Int>()
                val durationsData = mutableListOf<Int>()
                val velocitiesData = mutableListOf<Int>()
                val glissandoData = mutableListOf<Int>()
                val previousIsRestData = mutableListOf<Boolean>()
                val tickChangesData = mutableListOf<TickChangeData>()


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
                //COMBINE RANGE TYPES AND ENSEMBLE MIXES for each part

                val rangesAndEnsembleParts =
                    combineRangesAndEnsembleParts(rangeTypes, ensemblePartSequence)

                // RANGES EXTENSION
                //val ranges = rangeTypes.map { ensemblePart.getOctavedRangeByType(it.first, it.second, isUpperPart) }
                //println(rangesAndEnsembleParts)
                val ranges = rangesAndEnsembleParts.map {
                    //println(it)
                    it.second.getOctavedRangeByType(
                        it.first.first,
                        it.first.second,
                        isUpperPart,
                        it.second.familyRange
                    )
                }
                //println("rangeAndEnsembleParts: " + rangesAndEnsembleParts)
                val octaveTranspose = when (rangeTypes[0].second) {
                    3 -> if (isUpperPart) 1 else -1
                    4 -> if (isUpperPart) 2 else -2
                    else -> rangeTypes[0].second
                }
                val startOctave = (ensemblePartSequence[0].octave + octaveTranspose).coerceIn(0, 8)
                //ACTUAL PITCHES AND CHANGES
                // val (actualPitches, changes) =
                val (actualPitches, changesData) = if (melodyTypes.size == 1 && rangesAndEnsembleParts.size == 1) {
                    Pair(
                        Insieme.findMelody(
                            startOctave,
                            part.absPitches.toIntArray(),
                            ranges[0].first,
                            ranges[0].last,
                            melodyTypes[0]
                        ),
                        listOf(ChangeData(0, ensemblePartSequence[0].instrument))
                    )
                } else {
                    findMelodyWithStructure(startOctave, part.absPitches.toIntArray(),
                        ranges.map { it.first }.toIntArray(), ranges.map { it.last }.toIntArray(),
                        melodyTypes.toIntArray(), rangesAndEnsembleParts.map { it.second })

                }
                //println("ChangesData: $changesData")

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
                    if (changeIndex < changesData.size && index == changesData[changeIndex].noteIndex) {
                        tickChangesData.add(
                            TickChangeData(
                                tick.toLong(),
                                changesData[changeIndex].instrument,
                                index
                            )
                        )
                        changeIndex++
                    }
                    val pitch = actualPitches[index]
                    val velocity = velocities[index]
                    var gliss = glissandoChecks[index]
                    //println("pitch: $pitch vel: $velocity")
                    //var dur = durations[durIndex % durations.size]
                    var dur = actualDurations[durIndex]
                    if (dur < 0) { // negative values are considered as rests
                        dur *= -1
                        tick += dur
                        durIndex++
                    } else {
                        if (pitch != -1) {
                            while (index + 1 < actualPitches.size && actualPitches[index + 1] == pitch) {
                                //var nextDur = durations[(durIndex + 1) % durations.size]
                                var nextDur = actualDurations[durIndex + 1]
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
                                    if (changeIndex < changesData.size && index == changesData[changeIndex].noteIndex) {
                                        tickChangesData.add(
                                            TickChangeData(
                                                tick.toLong(),
                                                changesData[changeIndex].instrument,
                                                index
                                            )
                                        )
                                        changeIndex++
                                    }
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
                //println("Do the magic: Channel: $channel $tickChangesData")
                //dispatch("Do the magic: Channel: $channel $pitchesData")
                if(context.job.isActive) dispatch(Triple(AppViewModel.Building.DATATRACKS, channel, counterpoint.parts.size))
                TrackData(
                    pitchesData.toIntArray(),
                    ticksData.toIntArray(),
                    durationsData.toIntArray(),
                    velocitiesData.toIntArray(),
                    glissandoData.toIntArray(),
                    IntArray(pitchesData.size),
                    previousIsRestData.toBooleanArray(),
                    null,
                    null,
                    channel,
                    80,
                    vibrato,
                    doublingFlags,
                    audio8D.contains(partIndex),
                    partIndex,
                    tickChangesData
                )
            } else {
                //dispatch("Do the magic: empty track created.")
                TrackData.emptyTrack()
            }

        }

        // CREATION OF TRACKS
//        return counterpoint.parts.map { part ->
//            computation(part)
//        }.sortedBy { it.channel }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //println(counterpoint.parts)
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

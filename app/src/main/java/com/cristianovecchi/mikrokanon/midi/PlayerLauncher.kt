package com.cristianovecchi.mikrokanon.midi

import android.media.MediaPlayer
import com.cristianovecchi.mikrokanon.*
import com.cristianovecchi.mikrokanon.AIMUSIC.*
import com.cristianovecchi.mikrokanon.db.UserOptionsData
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.math.absoluteValue

suspend fun launchPlayer(
    userOptionsData: UserOptionsData?, createAndPlay: Boolean, simplify: Boolean,
    mediaPlayer: MediaPlayer?, midiPath: File,
    counterpoints: List<Counterpoint?>, context: CoroutineContext, dispatch: (Triple<AppViewModel.Building, Int, Int>) -> Unit
): String = withContext(context){
        val ensList: List<List<EnsembleType>> =
            userOptionsData?.let {
                userOptionsData.ensemblesList
                    .extractIntListsFromCsv().map { singleEnsembleGroup -> singleEnsembleGroup.map{EnsembleType.values()[it]} }
            }
                ?: listOf(listOf(EnsembleType.STRING_ORCHESTRA))
        val dynamics: List<Float> =
            if (simplify) {
                listOf(1f)
            } else {
                userOptionsData?.let {
                    userOptionsData.dynamics.extractFloatsFromCsv()
                }
            } ?: listOf(1f)
        val bpms: List<Float> =
            userOptionsData?.let {
                if (simplify) {
                    userOptionsData.bpms.extractIntsFromCsv()
                        .map { it.toFloat() }.take(1)
                } else {
                    userOptionsData.bpms.extractIntsFromCsv()
                        .map { it.toFloat() }
                }
            } ?: listOf(90f)
        val rhythm: List<Triple<RhythmPatterns,Boolean,Int>> =
            (userOptionsData?.let {
                val patterns = RhythmPatterns.values()
//                        if(simplify){
//                            val pair = userOptionsData.value!![0].rhythm.extractIntPairsFromCsv()[0]
//                            listOf(Triple(patterns[pair.first.absoluteValue-1], pair.first<0, pair.second))
//                        } else {
                val pairs = userOptionsData.rhythm.extractIntPairsFromCsv()
                pairs.map{Triple(patterns[it.first], it.second<0, it.second.absoluteValue) }
//                        }
            } ?: listOf(Triple(RhythmPatterns.PLAIN_4_4_R16,false,1)) )
        val swingShuffle: Float =
            (userOptionsData?.let { userOptionsData.swingShuffle.toFloat() }
            ?: 0.5f)
        val rhythmShuffle: Boolean =
            0 != (userOptionsData?.let { userOptionsData.rhythmShuffle.toInt() }
                ?: 0)
        val partsShuffle: Boolean =
            0 != (userOptionsData?.let { userOptionsData.partsShuffle.toInt() }
                ?: 0)
        val rowForms: List<Pair<Int, Int>> =
            userOptionsData?.let {
                if (simplify) {
                    listOf(Pair(1, 1))
                } else {
                    userOptionsData.rowForms.extractIntPairsFromCsv()
                }
            } ?: listOf(Pair(1, 1)) // ORIGINAL by default || 0 is unused
        val doublingFlags: Int =
            userOptionsData?.let { userOptionsData.doublingFlags.toInt() }
                ?: 0
        val audio8DFlags: Int =
            userOptionsData?.let {
                if (simplify) {
                    0
                } else {
                    userOptionsData.audio8DFlags.toInt()
                }
            } ?: 0
        val ritornello: Int =
            userOptionsData?.let {
                if (simplify) {
                    0
                } else {
                    userOptionsData.ritornello.toInt()
                }
            } ?: 0
        val transpose: List<Pair<Int,Int>> =
            userOptionsData?.let { userOptionsData.transpose.extractIntPairsFromCsv() }
                ?: listOf(Pair(0,1))
        val nuances: Int =
            userOptionsData?.let { userOptionsData.nuances.toInt() }
                ?: 1
        val rangeTypes: List<Pair<Int, Int>> =
            userOptionsData?.let {
                if (simplify) {
                    listOf(userOptionsData.rangeTypes.extractIntPairsFromCsv()[0])
                } else {
                    userOptionsData.rangeTypes.extractIntPairsFromCsv()
                }
            } ?: listOf(Pair(2, 0))
        val legatoTypes: List<Pair<Int, Int>> =
            userOptionsData?.let {
                if (simplify) {
                    listOf(userOptionsData.legatoTypes.extractIntPairsFromCsv()[0])
                } else {
                    userOptionsData.legatoTypes.extractIntPairsFromCsv()
                }
            } ?: listOf(Pair(4, 0))
        val melodyTypes: List<Int> =
            userOptionsData?.let {
                if (simplify) {
                    listOf(userOptionsData.melodyTypes.extractIntsFromCsv()[0])
                } else {
                    userOptionsData.melodyTypes.extractIntsFromCsv()
                }
            } ?: listOf(0)
        val glissandoFlags: Int =
            userOptionsData?.let { userOptionsData.glissandoFlags.toInt() }
                ?: 0
        val vibrato: Int =
            userOptionsData?.let { userOptionsData.vibrato.toInt() }
                ?: 0
    val checkAndReplace: List<List<CheckAndReplaceData>> =
        userOptionsData?.let {CheckAndReplaceData.createMultiCheckAndReplaceDatasFromCsv(userOptionsData.checkAndReplace)}
            ?: listOf()
    val harmonizations: List<HarmonizationData> =
        userOptionsData?.let {HarmonizationData.createHarmonizationsFromCsv(userOptionsData.harmonizations)}
            ?: listOf()
    val chordsToEnhance: List<ChordToEnhanceData> =
        userOptionsData?.let {
            userOptionsData.chordsToEnhance.extractIntPairsFromCsv().map{
                ChordToEnhanceData(convertFlagsToInts(it.first), it.second)}
        } ?: listOf()
    val enhanceChordsInTranspositions: Boolean =
        0 != (userOptionsData?.let { userOptionsData.enhanceChordsInTranspositions.toInt() }
            ?: 0)
        //selectedCounterpoint.value!!.display()
        Player.playCounterpoint(
            context,
            dispatch,
            mediaPlayer,
            false,
            counterpoints,
            dynamics,
            bpms,
            swingShuffle,
            rhythm,
            ensList,
            createAndPlay,
            midiPath,
            rhythmShuffle,
            partsShuffle,
            rowForms,
            ritornello,
            transpose,
            doublingFlags,
            nuances,
            rangeTypes,
            legatoTypes,
            melodyTypes,
            glissandoFlags,
            audio8DFlags,
            vibrato,
            checkAndReplace,
            harmonizations,
            chordsToEnhance,
            enhanceChordsInTranspositions
        )
    }


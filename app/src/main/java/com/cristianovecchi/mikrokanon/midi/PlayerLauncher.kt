package com.cristianovecchi.mikrokanon.midi

import android.media.MediaPlayer
import com.cristianovecchi.mikrokanon.AIMUSIC.Counterpoint
import com.cristianovecchi.mikrokanon.AIMUSIC.EnsembleType
import com.cristianovecchi.mikrokanon.AIMUSIC.RhythmPatterns
import com.cristianovecchi.mikrokanon.db.UserOptionsData
import com.cristianovecchi.mikrokanon.extractFloatsFromCsv
import com.cristianovecchi.mikrokanon.extractIntListsFromCsv
import com.cristianovecchi.mikrokanon.extractIntPairsFromCsv
import com.cristianovecchi.mikrokanon.extractIntsFromCsv
import java.io.File
import kotlin.math.absoluteValue

fun launchPlayer(userOptionsData: UserOptionsData?, createAndPlay: Boolean, simplify: Boolean,
                 mediaPlayer: MediaPlayer, midiPath: File, counterpoints: List<Counterpoint?>): String{
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
                pairs.map{Triple(patterns[it.first.absoluteValue-1], it.first<0, it.second) }
//                        }
            } ?: listOf(Triple(RhythmPatterns.PLAIN_4_4_R16,false,1)) )
        val rhythmShuffle: Boolean =
            0 != (userOptionsData?.let { userOptionsData.rhythmShuffle }
                ?: 0)
        val partsShuffle: Boolean =
            0 != (userOptionsData?.let { userOptionsData.partsShuffle }
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
            userOptionsData?.let { userOptionsData.doublingFlags }
                ?: 0
        val audio8DFlags: Int =
            userOptionsData?.let {
                if (simplify) {
                    0
                } else {
                    userOptionsData.audio8DFlags
                }
            } ?: 0
        val ritornello: Int =
            userOptionsData?.let {
                if (simplify) {
                    0
                } else {
                    userOptionsData.ritornello
                }
            } ?: 0
        val transpose: List<Pair<Int,Int>> =
            userOptionsData?.let { userOptionsData.transpose.extractIntPairsFromCsv() }
                ?: listOf(Pair(0,1))
        val nuances: Int =
            userOptionsData?.let { userOptionsData.nuances }
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
            userOptionsData?.let { userOptionsData.glissandoFlags }
                ?: 0
        val vibrato: Int =
            userOptionsData?.let { userOptionsData.vibrato }
                ?: 0
        //selectedCounterpoint.value!!.display()
        return Player.playCounterpoint(
            mediaPlayer,
            false,
            counterpoints,
            dynamics,
            bpms,
            0f,
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
            vibrato
        )
    }

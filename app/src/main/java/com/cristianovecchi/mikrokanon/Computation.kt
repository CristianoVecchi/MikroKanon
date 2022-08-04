package com.cristianovecchi.mikrokanon

import com.cristianovecchi.mikrokanon.AIMUSIC.ARPEGGIO
import com.cristianovecchi.mikrokanon.AIMUSIC.Clip
import com.cristianovecchi.mikrokanon.AIMUSIC.Counterpoint
import com.cristianovecchi.mikrokanon.AIMUSIC.TREND

sealed class Computation(open val icon: String = "") {
    data class MikroKanonOnly(val counterpoint: Counterpoint, val sequenceToMikroKanon: ArrayList<Clip>, val nParts: Int, override val icon: String = "mikrokanon"): Computation()
    data class FirstFromKP(val counterpoint: Counterpoint, val firstSequence: ArrayList<Clip>, val indexSequenceToAdd: Int, val repeat: Boolean, override val icon: String = "counterpoint"): Computation()
    data class FirstFromWave(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>, val nWaves: Int, override val icon: String = "waves"): Computation()
    data class FirstFromLoading(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>, val position: Int, override val icon: String = "save"): Computation()
    data class Pedal(val counterpoint: Counterpoint, val firstSequence: ArrayList<Clip>?, val nPedals: Int, override val icon: String = "pedal" ): Computation()
    data class FurtherFromKP(val counterpoint: Counterpoint, val indexSequenceToAdd: Int, val repeat: Boolean, override val icon: String = "counterpoint"): Computation()
    data class FurtherFromWave(val counterpoints: List<Counterpoint>, val nWaves: Int, override val icon: String = "waves"): Computation()
    data class FirstFromFreePart(val counterpoint: Counterpoint, val firstSequence: ArrayList<Clip>, val trend: TREND, override val icon: String = "free_parts"): Computation()
    data class FurtherFromFreePart(val counterpoint: Counterpoint, val firstSequence: ArrayList<Clip>, val trend: TREND, override val icon: String = "free_parts"): Computation()
    data class Fioritura(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, override val icon: String = "fioritura"): Computation()
    data class Round(val counterpoints: List<Counterpoint>, val index: Int, override val icon: String = "round"): Computation()
    data class Cadenza(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val values: List<Int>, override val icon: String = "cadenza"): Computation()
    data class Sort(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val sortType: Int, override val icon: String = "sort_up"): Computation()
    data class UpsideDown(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val index: Int, override val icon: String = "upside_down"): Computation()
    data class Arpeggio(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val index: Int, val arpeggioType: ARPEGGIO, override val icon: String = "arpeggio"): Computation()
    data class Scarlatti(val counterpoint: Counterpoint, val firstSequence: ArrayList<Clip>?, override val icon: String = "Scarlatti"): Computation()
    data class Overlap(val counterpoint1st: Counterpoint, val counterpoint2nd: Counterpoint, val firstSequence: ArrayList<Clip>?, override val icon: String = "overlap"): Computation()
    data class Crossover(val counterpoint1st: Counterpoint, val counterpoint2nd: Counterpoint, val firstSequence: ArrayList<Clip>?, override val icon: String = "crossover"): Computation()
    data class Glue(val counterpoint1st: Counterpoint, val counterpoint2nd: Counterpoint, val firstSequence: ArrayList<Clip>?, override val icon: String = "glue"): Computation()
    data class Maze(val intSequences: List<List<Int>>, override val icon: String = "maze"): Computation()
    data class EraseIntervals(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, override val icon: String = "erase"): Computation()
    data class Single(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, override val icon: String = "single"): Computation()
    data class Doppelgänger(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, override val icon: String = "doppelgänger"): Computation()
    data class Expand(val counterpoints: List<Counterpoint>, val index: Int, val extension: Int = 2, override val icon: String = "expand") : Computation()
    data class Transposition(val counterpoints: List<Counterpoint>, val transpositions: List<Pair<Int,Int>>, val index: Int, override val icon: String = "transpose") : Computation()
    data class TritoneSubstitution(val counterpoints: List<Counterpoint>, val intervalSet: List<Int>, val index: Int, override val icon: String = "tritone_substitution") : Computation()
    data class ExtendedWeightedHarmony(val counterpoints: List<Counterpoint>, val nParts: Int, override val icon: String = "accompanist") : Computation()
    data class Resolutio(val counterpoints: List<Counterpoint>, val resolutioData: Pair<Set<Int>,String>, override val icon: String = "resolutio") : Computation()
    data class Doubling(val counterpoints: List<Counterpoint>, val doublingData: List<Pair<Int,Int>>, override val icon: String = "doubling") : Computation()
}

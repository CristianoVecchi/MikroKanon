package com.cristianovecchi.mikrokanon.AIMUSIC

import androidx.lifecycle.LiveData
import com.cristianovecchi.mikrokanon.pmap
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs

suspend fun waves(counterpoints: List<Counterpoint>, intervalSet: List<Int>, horIntervalSet: List<Int>, nWaves: Int) : List<Counterpoint>{
     return Counterpoint.findAllWithWaves(counterpoints,  intervalSet, horIntervalSet, nWaves)
}
suspend fun freeParts(counterpoint: Counterpoint, intervalSet: List<Int>, directions: List<Int>) : List<Counterpoint>{
    return Counterpoint.findAllFreeParts(counterpoint, intervalSet, directions)
}
inline fun isIntervalInSet(intervalSet: IntArray, pitch1: Int, pitch2: Int): Boolean {
     val interval = abs(pitch2 - pitch1)
     return intervalSet.contains(interval)
}
fun isIntervalInSetBitwise(intervalSet: Int, pitch1: Int, pitch2: Int): Boolean {
     val intervalByte = 1 shl abs(pitch2 - pitch1)
     return intervalSet and intervalByte != 0
}
fun convertIntervalSetToByte(intervalSet: IntArray): Int {
     var byte = 0
     intervalSet.forEach{ byte = byte or (1 shl it)}
     return byte
}
suspend fun mikroKanons4(
     context: CoroutineContext,
     sequence: List<Int>,
     deepSearch: Boolean,
     intervalSet: List<Int>,
     maxNresults: Int
): List<Counterpoint> = withContext(context){
     val emptinessGate = if(!deepSearch) 1.0f else when (intervalSet.size) {
          0 -> 0.69f
          in 1..2 -> 0.66f
          in 3..4 -> 0.63f
          in 5..6 -> 0.60f
          in 7..8 -> 0.55f
          in 9..10 -> 0.25f
          in 11..12 -> 0.15f
          else -> 0.001f
     }
     val depth = if(deepSearch) 4 else 2
      MikroKanon.findAll4AbsPartMikroKanonsParallel(context, sequence,
           intervalSet, depth, emptinessGate, maxNresults)
          .pmap { it.toCounterpoint() }
}
suspend fun mikroKanons5reducted(
     context: CoroutineContext,
     sequence: List<Int>,
     intervalSet: List<Int>,
     maxNresults: Int
): List<Counterpoint> = withContext(context){
     val emptinessGate = 1.0f
     val depth = 2
     MikroKanon.findAll5AbsPartMikroKanonsParallelReducted(context, sequence,
          intervalSet, depth, emptinessGate, maxNresults)
          .pmap { it.toCounterpoint() }
}
suspend fun mikroKanons3(sequence: List<Int>, intervalSet: List<Int>, depth: Int = 6): List<Counterpoint>{
     return MikroKanon.findAll3AbsPartMikroKanonsParallel(sequence, intervalSet, depth)
                                   .pmap { it.toCounterpoint() }
}
suspend fun mikroKanons2(sequence: List<Int>, intervalSet: List<Int>, depth: Int = 6): List<Counterpoint>{
     return MikroKanon.findAll2AbsPartMikroKanons(sequence, intervalSet, depth)
          .pmap { it.toCounterpoint() }
}
suspend fun flourish(originalCounterpoints: List<Counterpoint>, intervalSet: List<Int>, horIntervalSet: List<Int>): List<Counterpoint>{
     return originalCounterpoints.map{Counterpoint.flourish(it, intervalSet, horIntervalSet)}
}
suspend fun buildRound(originalCounterpoints: List<Counterpoint> ): List<Counterpoint>{
     return originalCounterpoints.map{it.buildRound()}
}
suspend fun addCadenzasOnCounterpoints(horIntervalSet: List<Int>, originalCounterpoints: List<Counterpoint>, values: List<Int>): List<Counterpoint>{
     return originalCounterpoints.map{it.addCadenzas(horIntervalSet, values)}
}
suspend fun duplicateAllInCounterpoint(counterpoint: Counterpoint ): List<Counterpoint>{
     return counterpoint.duplicateAllPhrases()
}
suspend fun overlapCounterpointsSortingByFaults(context: CoroutineContext, counterpoint1st: Counterpoint,
                                                counterpoint2nd: Counterpoint, intervalSet: List<Int>,
                                                maxParts: Int, crossover: Boolean): List<Counterpoint> {
     val shouldCompress = counterpoint1st.parts.size + counterpoint2nd.parts.size > maxParts
     return counterpoint1st.transposingOverlap(context, counterpoint2nd, crossover, intervalSet, shouldCompress)
          .map{ it.cutExtraParts(maxParts)}
          .sortedBy{ it.checkVerticalFaults(intervalSet)}
}
suspend fun glueCounterpoints(counterpoint1st: Counterpoint, counterpoint2nd: Counterpoint): List<Counterpoint>{
     return counterpoint1st.transposingGlueWithRowFormsOf2nd(counterpoint2nd, true)
}
suspend fun eraseHorizontalIntervalsOnCounterpoints(horIntervalSet: List<Int>, originalCounterpoints: List<Counterpoint> ): List<Counterpoint>{
     return originalCounterpoints.map{it.eraseIntervalsOnBothNotes(horIntervalSet)}
}
suspend fun sortColumnsOnCounterpoints(originalCounterpoints: List<Counterpoint>, sortType: Int ): List<Counterpoint>{
     return originalCounterpoints.map{ it.sortColumns(sortType) }
}
suspend fun upsideDownCounterpoints(originalCounterpoints: List<Counterpoint>): List<Counterpoint>{
     return originalCounterpoints.map{ it.upsideDown() }
}
suspend fun reduceCounterpointsToSinglePart(originalCounterpoints: List<Counterpoint> ): List<Counterpoint>{
     return originalCounterpoints.map{ it.reduceToSinglePart() }
}
suspend fun explodeCounterpointsToDoppelgänger(originalCounterpoints: List<Counterpoint>, maxParts: Int, ensembleTypes: List<EnsembleType>, rangeType: Pair<Int,Int>, melodyType: Int): List<Counterpoint>{
     return originalCounterpoints.map{ it.explodeToDoppelgänger(maxParts, ensembleTypes, rangeType, melodyType) }
}
suspend fun findPedalsOnCounterpoint(nPedals:Int, counterpoint: Counterpoint, intervalSet: List<Int>): Counterpoint{
     return Counterpoint.addPedals(nPedals, counterpoint,  intervalSet)
}
suspend fun transposeAllCounterpoints(originalCounterpoints: List<Counterpoint>, transpositions: List<Int>): List<Counterpoint>{
     return originalCounterpoints.map{it.ritornello(transpositions.size - 1, transpositions)}
}
suspend fun expand(originalCounterpoints: List<Counterpoint>, extension: Int): List<Counterpoint>{
     return originalCounterpoints.map{Counterpoint.expand(it,extension)}
}
suspend fun addSequence(counterpoint: Counterpoint,sequence: List<Clip>, intervalSet: List<Int>, repeat: Boolean,depth: Int = 6): List<Counterpoint> {
     return if(repeat){
          Counterpoint.findAllCounterpointsWithRepeatedSequence(counterpoint, sequence.map { it.abstractNote }, intervalSet, depth)
     } else {
          Counterpoint.findAllCounterpoints(counterpoint, sequence.map { it.abstractNote }, intervalSet, depth)
     }
}
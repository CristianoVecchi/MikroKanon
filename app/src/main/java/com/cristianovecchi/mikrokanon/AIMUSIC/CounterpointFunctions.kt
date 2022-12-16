package com.cristianovecchi.mikrokanon.AIMUSIC

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
fun isIntervalInSet(intervalSet: IntArray, pitch1: Int, pitch2: Int): Boolean {
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
suspend fun mikroKanons5reducted(context: CoroutineContext, sequence: List<Int>,
                                 intervalSet: List<Int>, maxNresults: Int):
        List<Counterpoint> = withContext(context){
     val emptinessGate = 1.0f
     val depth = 2
     MikroKanon.findAll5AbsPartMikroKanonsParallelReducted(context, sequence,
          intervalSet, depth, emptinessGate, maxNresults)
          .pmap { it.toCounterpoint() }
}
suspend fun mikroKanons6reducted(context: CoroutineContext, sequence: List<Int>,
                                 intervalSet: List<Int>, maxNresults: Int):
        List<Counterpoint> = withContext(context){
     val emptinessGate = 1.0f
     val depth = 2
     MikroKanon.findAll6AbsPartMikroKanonsParallelReducted(context, sequence,
          intervalSet, depth, emptinessGate, maxNresults)
          .pmap { it.toCounterpoint() }
}
suspend fun maze(context: CoroutineContext, sequences: List<List<Int>>, intervalSet: List<Int>):
List<Counterpoint> = withContext(context){
     Counterpoint.findMazesWithRowForms( context, sequences, intervalSet)
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
suspend fun buildRound(originalCounterpoints: List<Counterpoint>, transpositions: List<Pair<Int,Int>> ): List<Counterpoint>{
     return originalCounterpoints.map{it.buildRound(transpositions)}
}
suspend fun addCadenzasOnCounterpoints(horIntervalSet: List<Int>, originalCounterpoints: List<Counterpoint>, values: List<Int>): List<Counterpoint>{
     return originalCounterpoints.map{it.addCadenzas(horIntervalSet, values)}
}
suspend fun addFormatOnCounterpoint(originalCounterpoint: Counterpoint, values: List<Int>): List<Counterpoint>{
     return listOf(originalCounterpoint).map{it.format(values)}
}
suspend fun addResolutioOnCounterpointsWithSet(originalCounterpoints: List<Counterpoint>, absPitchesSet: Set<Int>, resolutioForm: List<Int>): List<Counterpoint>{
     return originalCounterpoints.map{it.addResolutionesOnSet(absPitchesSet, resolutioForm)}
}
suspend fun addResolutioOnCounterpointsWithHarmony(originalCounterpoints: List<Counterpoint>, harmony: HarmonizationType, resolutioForm: List<Int>): List<Counterpoint>{
     return originalCounterpoints.map{it.addResolutionesOnHarmony(harmony, resolutioForm)}
}
suspend fun addDoublingOnCounterpoints(originalCounterpoints: List<Counterpoint>,  doublingList: List<Pair<Int,Int>>, maxParts: Int): List<Counterpoint>{
     return originalCounterpoints.map{it.addMultipleDoublingParts(doublingList, maxParts)}
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
suspend fun chessOnCounterpoints(originalCounterpoints: List<Counterpoint>, range: Int ): List<Counterpoint>{
     return originalCounterpoints.map{ it.chessboard(range) }
}
suspend fun upsideDownCounterpoints(originalCounterpoints: List<Counterpoint>): List<Counterpoint>{
     return originalCounterpoints.map{ it.upsideDown() }
}
suspend fun arpeggioCounterpoints(originalCounterpoints: List<Counterpoint>, arpeggioType: ARPEGGIO): List<Counterpoint>{
     return originalCounterpoints.map{ it.arpeggio(arpeggioType) }
}
suspend fun reduceCounterpointsToSinglePart(originalCounterpoints: List<Counterpoint> ): List<Counterpoint>{
     return originalCounterpoints.map{ it.reduceToSinglePart() }
}
suspend fun explodeCounterpointsToDoppelgänger(originalCounterpoints: List<Counterpoint>, maxParts: Int): List<Counterpoint>{
     return originalCounterpoints.map{ it.explodeToDoppelgänger(maxParts) }
}
suspend fun findPedalsOnCounterpoint(nPedals:Int, counterpoint: Counterpoint, intervalSet: List<Int>): Counterpoint{
     return Counterpoint.addPedals(nPedals, counterpoint,  intervalSet)
}
suspend fun transposeAllCounterpoints(originalCounterpoints: List<Counterpoint>, transpositions: List<Pair<Int,Int>>): List<Counterpoint>{
     return originalCounterpoints.map{it.ritornello(transpositions.size - 1, transpositions)}
}
suspend fun expand(originalCounterpoints: List<Counterpoint>, extension: Int): List<Counterpoint>{
     return originalCounterpoints.map{Counterpoint.expand(it,extension)}
}
suspend fun extendedWeightedHarmony(originalCounterpoints: List<Counterpoint>, nParts: Int, maxParts: Int): List<Counterpoint>{
     return originalCounterpoints.map{ it.addPartsOfExtendedWeightedHarmony(nParts, maxParts) }
}
suspend fun progressiveWeightedHarmony(originalCounterpoints: List<Counterpoint>): List<Counterpoint>{
     return originalCounterpoints.map{ it.sortColumnsByProgressiveEWH() }
}
suspend fun addSequence(counterpoint: Counterpoint,sequence: List<Clip>, intervalSet: List<Int>, repeat: Boolean,depth: Int = 6): List<Counterpoint> {
     return if(repeat){
          Counterpoint.findAllCounterpointsWithRepeatedSequence(counterpoint, sequence.map { it.abstractNote }, intervalSet, depth)
     } else {
          Counterpoint.findAllCounterpoints(counterpoint, sequence.map { it.abstractNote }, intervalSet, depth)
     }
}
suspend fun paradeAllOnCounterpoint(
     counterpoint: Counterpoint,
     howMany: Int,
     maxParts: Int,
     vertIntervalSet: List<Int>,
     horIntervalSet: List<Int>,
     cadenzaForm: List<Int>, resolutioAbsPitches: Set<Int>,
     resolutioForm: List<Int>, resolutioHarmony: HarmonizationType,): List<Counterpoint>{
     val partResult = mutableListOf<Counterpoint>()
     val list = listOf(counterpoint)

     partResult += freeParts(counterpoint, vertIntervalSet, TREND.ASCENDANT_DYNAMIC.directions.filter{ horIntervalSet.contains(it)}).sortedBy { it.emptiness }.take(howMany)
     partResult += freeParts(counterpoint, vertIntervalSet, TREND.DESCENDANT_DYNAMIC.directions.filter{ horIntervalSet.contains(it)}).sortedBy { it.emptiness }.take(howMany)
     partResult += freeParts(counterpoint, vertIntervalSet, TREND.ASCENDANT_STATIC.directions.filter{ horIntervalSet.contains(it)}).sortedBy { it.emptiness }.take(howMany)
     partResult += freeParts(counterpoint, vertIntervalSet, TREND.DESCENDANT_STATIC.directions.filter{ horIntervalSet.contains(it)}).sortedBy { it.emptiness }.take(howMany)
     // different trends could have the same result
     val result = partResult.distinctBy { it.getAbsPitches() }.toMutableList()
     result += sortColumnsOnCounterpoints(list, 0).sortedBy { it.emptiness }.take(howMany)
     result += sortColumnsOnCounterpoints(list, 1).sortedBy { it.emptiness }.take(howMany)
     result += arpeggioCounterpoints(list, ARPEGGIO.ASCENDANT).sortedBy { it.emptiness }.take(howMany)
     result += extendedWeightedHarmony(list, 1, maxParts).sortedBy { it.emptiness }.take(howMany)
     result += extendedWeightedHarmony(list, 2, maxParts).sortedBy { it.emptiness }.take(howMany)
     result += extendedWeightedHarmony(list, 3, maxParts).sortedBy { it.emptiness }.take(howMany)
     result += extendedWeightedHarmony(list, 4, maxParts).sortedBy { it.emptiness }.take(howMany)
     result += progressiveWeightedHarmony(list).sortedBy { it.emptiness }.take(howMany)
     result += chessOnCounterpoints(list,1).sortedBy { it.emptiness }.take(howMany)
     result += duplicateAllInCounterpoint(counterpoint).sortedBy { it.emptiness }.take(howMany)
     result += addCadenzasOnCounterpoints(horIntervalSet, list, cadenzaForm).sortedBy { it.emptiness }.take(howMany)
     result += addResolutioOnCounterpointsWithSet(list, resolutioAbsPitches, resolutioForm).sortedBy { it.emptiness }.take(howMany)
     result += addResolutioOnCounterpointsWithHarmony(list, resolutioHarmony, resolutioForm).sortedBy { it.emptiness }.take(howMany)
     result += eraseHorizontalIntervalsOnCounterpoints(horIntervalSet, list).sortedBy { it.emptiness }.take(howMany)
     result += reduceCounterpointsToSinglePart(list).sortedBy { it.emptiness }.take(howMany)
     result += flourish(list, vertIntervalSet, horIntervalSet).sortedBy { it.emptiness }.take(howMany)
     result += explodeCounterpointsToDoppelgänger(list, maxParts).sortedBy { it.emptiness }.take(howMany)
     result += buildRound(list, listOf(Pair(0,1))).sortedBy { it.emptiness }.take(howMany)
     result += waves(list, vertIntervalSet, horIntervalSet, 3).sortedBy { it.emptiness }.take(howMany)
     result += waves(list, vertIntervalSet, horIntervalSet, 4).sortedBy { it.emptiness }.take(howMany)
     result += waves(list, vertIntervalSet, horIntervalSet, 6).sortedBy { it.emptiness }.take(howMany)
     result += findPedalsOnCounterpoint(1, counterpoint, vertIntervalSet)
     result += findPedalsOnCounterpoint(3, counterpoint, vertIntervalSet)
     result += findPedalsOnCounterpoint(5, counterpoint, vertIntervalSet)

     return result
}
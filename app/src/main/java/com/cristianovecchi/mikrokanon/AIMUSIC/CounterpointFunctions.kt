package com.cristianovecchi.mikrokanon.AIMUSIC

import com.cristianovecchi.mikrokanon.pmap
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs

suspend fun waves(counterpoints: List<Counterpoint>, intervalSet: List<Int>, nWaves: Int) : List<Counterpoint>{
     return Counterpoint.findAllWithWaves(counterpoints,  intervalSet, nWaves)
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
     sequence: List<Clip>,
     deepSearch: Boolean,
     intervalSet: List<Int>
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
      MikroKanon.findAll4AbsPartMikroKanonsParallel(context, sequence.map { it.abstractNote },  intervalSet, depth, emptinessGate)
          .pmap { it.toCounterpoint() }
}
suspend fun mikroKanons3(sequence: List<Clip>, intervalSet: List<Int>, depth: Int = 6): List<Counterpoint>{
     return MikroKanon.findAll3AbsPartMikroKanonsParallel(sequence.map { it.abstractNote }, intervalSet, depth)
                                   .pmap { it.toCounterpoint() }
}
suspend fun mikroKanons2(sequence: List<Clip>, intervalSet: List<Int>, depth: Int = 6): List<Counterpoint>{
     return MikroKanon.findAll2AbsPartMikroKanons(sequence.map { it.abstractNote }, intervalSet, depth)
          .pmap { it.toCounterpoint() }
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
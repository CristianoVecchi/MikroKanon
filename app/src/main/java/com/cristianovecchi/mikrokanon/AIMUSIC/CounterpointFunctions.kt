package com.cristianovecchi.mikrokanon.AIMUSIC

import com.cristianovecchi.mikrokanon.pmap

suspend fun waves(counterpoints: List<Counterpoint>, intervalSet: List<Int>, nWaves: Int) : List<Counterpoint>{
     return Counterpoint.findAllWithWaves(counterpoints,  intervalSet, nWaves)
}
suspend fun freeParts(counterpoint: Counterpoint, intervalSet: List<Int>, directions: List<Int>) : List<Counterpoint>{
    return Counterpoint.findAllFreeParts(counterpoint, intervalSet, directions)
}
suspend fun mikroKanons4(sequence: List<Clip>, deepSearch:Boolean, intervalSet: List<Int>): List<Counterpoint> {
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
     return MikroKanon.findAll4AbsPartMikroKanonsParallel(sequence.map { it.abstractNote },  intervalSet, depth, emptinessGate)
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
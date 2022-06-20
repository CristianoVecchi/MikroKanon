package com.cristianovecchi.mikrokanon

import androidx.lifecycle.viewModelScope
import com.cristianovecchi.mikrokanon.AIMUSIC.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

fun AppViewModel.findCounterpointsByMikroKanons2(){
    var newList: List<Counterpoint>
    viewModelScope.launch(Dispatchers.Main){
        withContext(Dispatchers.Default){
            val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList().take(
                AppViewModel.MAX_NOTES_MK_2
            )
            newList = mikroKanons2(sequence,intervalSet.value!!, 7)
                .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }//.take(maxVisibleCounterpoints)
                .pmapIf(spread != 0){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
        }
        changeCounterpointsWithLimitAndCache(newList, true, AppViewModel.MAX_VISIBLE_COUNTERPOINTS * 2)
    }
}
fun AppViewModel.findCounterpointsByMikroKanons3(){
    viewModelScope.launch(Dispatchers.Main){
        if(sequenceToMikroKanons.value!!.isNotEmpty()) {
            val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList().take(
                AppViewModel.MAX_NOTES_MK_3
            )
            val key = AppViewModel.CacheKey(sequence, intervalSet.value!!)
            if(mk3cache.containsKey(key)) {
                changeCounterpointsWithLimitAndCache(mk3cache[key]!!.first, true)
            }else {
                val newList: List<Counterpoint>
                _elaborating.value = true
                withContext(Dispatchers.Default) {
                    newList = mikroKanons3(sequence,intervalSet.value!!,
                        AppViewModel.MAX_DEPTH_MK_3
                    )
                        .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }//.take(maxVisibleCounterpoints)
                        .pmapIf(spread != 0){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                        .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                }
                changeCounterpointsWithLimitAndCache(newList, true, AppViewModel.MAX_VISIBLE_COUNTERPOINTS * 2,
                    Pair(mk3cache, key))
                _elaborating.value = false
            }
        }
    }.also{  jobQueue.add(it)  }
}
fun AppViewModel.findCounterpointsByMikroKanons4(){
    viewModelScope.launch(Dispatchers.Main){
        val deepSearch = userOptionsData.value!![0].deepSearch != 0
        if(sequenceToMikroKanons.value!!.isNotEmpty()) {
            val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList()
                .take( if(deepSearch) AppViewModel.MAX_NOTES_MK_4DEEP else AppViewModel.MAX_NOTES_MK_4)
            val key = AppViewModel.CacheKey(sequence, intervalSet.value!!)
            if(mk4cache.containsKey(key) && !deepSearch) {
                changeCounterpointsWithLimitAndCache(mk4cache[key]!!.first, true)
            }else if(mk4deepSearchCache.containsKey(key) && deepSearch) {
                changeCounterpointsWithLimitAndCache(mk4deepSearchCache[key]!!.first, true)
            }else {
                measureTimeMillis{
                    _elaborating.value = true
                    // val def = async(Dispatchers.Default + MKjob) {
                    val newList = withContext(Dispatchers.Default){
                        mikroKanons4(this.coroutineContext.job,
                            sequence,
                            deepSearch,
                            intervalSet.value!!,
                            AppViewModel.MAX_VISIBLE_COUNTERPOINTS
                        )
                            .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                            .pmapIf(spread != 0) { it.spreadAsPossible(intervalSet = intervalSet.value!!) }
                            .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                    }
                    //val newList: List<Counterpoint> = def.await()
                    changeCounterpointsWithLimitAndCache(newList, true,
                        AppViewModel.MAX_VISIBLE_COUNTERPOINTS,
                        if(deepSearch) Pair(mk4deepSearchCache, key) else Pair(mk4cache, key))
                    _elaborating.value = false
                }.also { time -> println("MK4 executed in $time ms" )}
            }
        }
    }.also{  jobQueue.add(it)  }
}
fun AppViewModel.findCounterpointsByMikroKanons5reducted() {
    viewModelScope.launch(Dispatchers.Main) {
        if (sequenceToMikroKanons.value!!.isNotEmpty()) {
            val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList().take(
                AppViewModel.MAX_NOTES_MK_5RED
            )
            val key = AppViewModel.CacheKey(sequence, intervalSet.value!!)
            if (mk5reductedCache.containsKey(key) ) {
                changeCounterpointsWithLimitAndCache(mk5reductedCache[key]!!.first, true)
            } else {
                measureTimeMillis {
                    _elaborating.value = true
                    // val def = async(Dispatchers.Default + MKjob) {
                    val newList = withContext(Dispatchers.Default) {
                        mikroKanons5reducted(
                            this.coroutineContext.job,
                            sequence,
                            intervalSet.value!!,
                            AppViewModel.MAX_VISIBLE_COUNTERPOINTS
                        )
                            .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                            .pmapIf(spread != 0) { it.spreadAsPossible(intervalSet = intervalSet.value!!) }
                            .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                    }
                    //val newList: List<Counterpoint> = def.await()
                    changeCounterpointsWithLimitAndCache(newList, true,
                        AppViewModel.MAX_VISIBLE_COUNTERPOINTS, Pair(mk5reductedCache, key))
                    _elaborating.value = false
                }.also { time -> println("MK5reducted executed in $time ms") }
            }
        }
    }.also { jobQueue.add(it) }
}
fun AppViewModel.findCounterpointsByMikroKanons6reducted() {
    viewModelScope.launch(Dispatchers.Main) {
        if (sequenceToMikroKanons.value!!.isNotEmpty()) {
            val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList().take(
                AppViewModel.MAX_NOTES_MK_6RED
            )
            val key = AppViewModel.CacheKey(sequence, intervalSet.value!!)
            if (mk6reductedCache.containsKey(key) ) {
                changeCounterpointsWithLimitAndCache(mk6reductedCache[key]!!.first, true)
            } else {
                measureTimeMillis {
                    _elaborating.value = true
                    // val def = async(Dispatchers.Default + MKjob) {
                    val newList = withContext(Dispatchers.Default) {
                        mikroKanons6reducted(
                            this.coroutineContext.job,
                            sequence,
                            intervalSet.value!!,
                            AppViewModel.MAX_VISIBLE_COUNTERPOINTS
                        )
                            .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                            .pmapIf(spread != 0) { it.spreadAsPossible(intervalSet = intervalSet.value!!) }
                            .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                    }
                    //val newList: List<Counterpoint> = def.await()
                    changeCounterpointsWithLimitAndCache(newList, true,
                        AppViewModel.MAX_VISIBLE_COUNTERPOINTS, Pair(mk6reductedCache, key) )
                    _elaborating.value = false
                }.also { time -> println("MK5reducted executed in $time ms") }
            }
        }
    }.also { jobQueue.add(it) }
}
fun AppViewModel.findMazes(intSequences: List<List<Int>>) {
    viewModelScope.launch(Dispatchers.Main) {
        val sequence = intSequences.reduce{ acc, seq -> acc + seq }
        val key = AppViewModel.CacheKey(sequence, intervalSet.value!!)
        if (mazeCache.containsKey(key) ) {
            changeCounterpointsWithLimitAndCache(mazeCache[key]!!.first, true)
        } else {
            measureTimeMillis {
                _elaborating.value = true
                // val def = async(Dispatchers.Default + MKjob) {
                val maxNotesInMaze = AppViewModel.MAX_NOTES_IN_MAZE[intSequences.size]
                val newList = withContext(Dispatchers.Default) {
                    maze(this.coroutineContext.job, intSequences.map{it.take(maxNotesInMaze)}, intervalSet.value!!)
                        .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                        .pmapIf(spread != 0) { it.spreadAsPossible(intervalSet = intervalSet.value!!) }
                        .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                }
                changeCounterpointsWithLimitAndCache(newList, true, AppViewModel.MAX_VISIBLE_COUNTERPOINTS, Pair(mazeCache, key))
                println("Maze list size = ${newList.size}")
                _elaborating.value = false
            }.also { time -> println("Maze executed in $time ms ") }
        }
    }.also { jobQueue.add(it) }
}
fun AppViewModel.flourishCounterpoints(originalCounterpoints: List<Counterpoint>){
    if(!selectedCounterpoint.value!!.isEmpty()){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = flourish(originalCounterpoints, intervalSet.value!!, intervalSetHorizontal.value!!.toList())
                    .pmapIf(spread != 0){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                    .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
            }
            changeCounterpointsWithLimitAndCache(newList, true)
        }
    }
}
fun AppViewModel.cadenzasOnCounterpoints(originalCounterpoints: List<Counterpoint>,values: List<Int>){
    if(!selectedCounterpoint.value!!.isEmpty()){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = addCadenzasOnCounterpoints(intervalSetHorizontal.value!!, originalCounterpoints, values)
                    .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
            }
            changeCounterpointsWithLimitAndCache(newList, true)
        }
    }
}
fun AppViewModel.duplicateAllPhrasesInCounterpoint(originalCounterpoint: Counterpoint){
    if(!originalCounterpoint.isEmpty()){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = duplicateAllInCounterpoint(originalCounterpoint)
                    .pmapIf(spread != 0){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                    .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
            }
            changeCounterpointsWithLimitAndCache(newList, true)
        }
    }
}
fun AppViewModel.overlapBothCounterpoints(counterpoint1st: Counterpoint, counterpoint2nd: Counterpoint, crossover: Boolean){
    if(!counterpoint1st.isEmpty() && !counterpoint2nd.isEmpty()){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            _elaborating.value = true
            withContext(Dispatchers.Default){
                newList = overlapCounterpointsSortingByFaults(
                    this.coroutineContext.job,
                    counterpoint1st, counterpoint2nd, intervalSet.value!!,
                    AppViewModel.MAX_PARTS, crossover)
                newList = if(spread != 0) newList.pmap{it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                    .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                else newList
            }
            changeCounterpointsWithLimitAndCache(newList, true, AppViewModel.MAX_VISIBLE_COUNTERPOINTS * 2)
            _elaborating.value = false
        }.also{  jobQueue.add(it)  }
    }
}
fun AppViewModel.glueBothCounterpoints(counterpoint1st: Counterpoint, counterpoint2nd: Counterpoint){
    if(!counterpoint1st.isEmpty() && !counterpoint2nd.isEmpty()){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = glueCounterpoints(counterpoint1st, counterpoint2nd)
                    .pmapIf(spread != 0){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                    .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
            }
            changeCounterpointsWithLimitAndCache(newList, true)
        }.also{  jobQueue.add(it)  }
    }
}
fun AppViewModel.eraseIntervalsOnCounterpoints(originalCounterpoints: List<Counterpoint>){
    if(!selectedCounterpoint.value!!.isEmpty()){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = eraseHorizontalIntervalsOnCounterpoints(intervalSetHorizontal.value!!, originalCounterpoints)
                    .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
            }
            changeCounterpointsWithLimitAndCache(newList, true)
        }
    }
}
 fun AppViewModel.sortAllCounterpoints(originalCounterpoints: List<Counterpoint>, sortType: Int){
    if(!selectedCounterpoint.value!!.isEmpty()){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = sortColumnsOnCounterpoints(originalCounterpoints, sortType)
                    .pmapIf(spread != 0){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                    .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
            }
            changeCounterpointsWithLimitAndCache(newList, true)
        }
    }
}
 fun AppViewModel.upsideDownAllCounterpoints(originalCounterpoints: List<Counterpoint>,index: Int){
    if(!selectedCounterpoint.value!!.isEmpty()){
        var newList: List<Counterpoint>

        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = upsideDownCounterpoints(originalCounterpoints)
            }
            changeCounterpointsWithLimitAndCache(newList, false)
            changeSelectedCounterpoint(counterpoints.value!![index])
        }
    }
}
 fun AppViewModel.arpeggioAllCounterpoints(originalCounterpoints: List<Counterpoint>, index: Int, arpeggioType: ARPEGGIO){
    if(!selectedCounterpoint.value!!.isEmpty()){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = arpeggioCounterpoints(originalCounterpoints, arpeggioType)
                //.sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
            }
            changeCounterpointsWithLimitAndCache(newList, false)
            changeSelectedCounterpoint(counterpoints.value!![index])
        }
    }
}
fun AppViewModel.singleOnCounterpoints(originalCounterpoints: List<Counterpoint>){
    if(!selectedCounterpoint.value!!.isEmpty()){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = reduceCounterpointsToSinglePart(originalCounterpoints)
                    .pmapIf(spread != 0){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                    .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
            }
            changeCounterpointsWithLimitAndCache(newList, true)
        }
    }
}
 fun AppViewModel.doppelgängerOnCounterpoints(originalCounterpoints: List<Counterpoint>){
    if(!selectedCounterpoint.value!!.isEmpty()){
        var newList: List<Counterpoint>
//            val ensList: List<List<EnsembleType>> =
//                userOptionsData.value?.let { listOf(userOptionsData.value!![0].ensemblesList
//                    .extractIntListsFromCsv()[0].map{EnsembleType.values()[it]})}
//                    ?: listOf(listOf(EnsembleType.STRING_ORCHESTRA))
//            val rangeType: Pair<Int,Int> =
//                userOptionsData.value?.let { userOptionsData.value!![0].rangeTypes.extractIntPairsFromCsv()[0] }
//                    ?: Pair(2,0)
//            val melodyType: Int =
//                userOptionsData.value?.let { userOptionsData.value!![0].melodyTypes.extractIntsFromCsv()[0] }
//                    ?: 0
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = explodeCounterpointsToDoppelgänger(originalCounterpoints,
                    AppViewModel.MAX_PARTS
                )
                    .pmapIf(spread != 0){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                    .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
            }
            changeCounterpointsWithLimitAndCache(newList, true)
        }
    }
}
 fun AppViewModel.expandCounterpoints(originalCounterpoints: List<Counterpoint>, index: Int, extension: Int){
    if(!selectedCounterpoint.value!!.isEmpty()){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = expand(originalCounterpoints, extension)
            }
            changeCounterpointsWithLimitAndCache(newList, false)
            changeSelectedCounterpoint(counterpoints.value!![index])
        }
    }
}
 fun AppViewModel.transposeOnCounterpoints(originalCounterpoints: List<Counterpoint>, transpositions: List<Pair<Int,Int>>, index: Int){
    if(!selectedCounterpoint.value!!.isEmpty()){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = transposeAllCounterpoints(originalCounterpoints, transpositions)
            }
            changeCounterpointsWithLimitAndCache(newList, false)
            changeSelectedCounterpoint(counterpoints.value!![index])
        }
    }
}
fun AppViewModel.tritoneSubstitutionOnCounterpoints(originalCounterpoints: List<Counterpoint>, index: Int){
    if(!selectedCounterpoint.value!!.isEmpty()){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = originalCounterpoints.map{ it.tritoneSubstitution() }
            }
            changeIntervalSet(tritoneSubstitutionOnIntervalSet(intervalSet.value!!))
            changeCounterpointsWithLimitAndCache(newList, false)
            changeSelectedCounterpoint(counterpoints.value!![index])
        }
    }
}
fun AppViewModel.roundOnCounterpoints(originalCounterpoints: List<Counterpoint>, index: Int){
    if(!selectedCounterpoint.value!!.isEmpty()){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = buildRound(originalCounterpoints)
            }
            changeCounterpointsWithLimitAndCache(newList, false)
            changeSelectedCounterpoint(counterpoints.value!![index])
        }
    }
}
fun AppViewModel.findPedal(nPedals: Int, list: ArrayList<Clip>?){
    var newList: List<Counterpoint>
    //var newIntervalSet: List<Int>
    val counterpoint = list?.let{ Counterpoint.counterpointFromClipList(list)} ?: selectedCounterpoint.value!!
    viewModelScope.launch(Dispatchers.Main){
        withContext(Dispatchers.Default){
            newList = listOf(findPedalsOnCounterpoint(nPedals, counterpoint, intervalSet.value!!))
            //newIntervalSet = pair.second
        }
        //changeIntervalSet(newIntervalSet)
        changeCounterpointsWithLimitAndCache(newList, false)
    }
}
fun AppViewModel.findWavesFromSequence(nWaves: Int){
    var newList: List<Counterpoint>
    viewModelScope.launch(Dispatchers.Main){
        withContext(Dispatchers.Default){
            newList = waves(listOf(Counterpoint.counterpointFromClipList(firstSequence.value!!)), intervalSet.value!! ,intervalSetHorizontal.value!!, nWaves)
        }
        changeCounterpointsWithLimitAndCache(newList, true)
    }
}
fun AppViewModel.findWavesOnCounterpoints(originalCounterpoints: List<Counterpoint>, nWaves: Int){
    if(!selectedCounterpoint.value!!.isEmpty()){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = waves(originalCounterpoints,intervalSet.value!!, intervalSetHorizontal.value!!, nWaves)
                    .sortedBy { it.emptiness }//.take(maxVisibleCounterpoints)
                    .mapIf(spread != 0){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                    .sortedBy { it.emptiness }
            }
            changeCounterpointsWithLimitAndCache(newList, true)
        }
    }
}
fun AppViewModel.findFreeParts(trend: TREND){
    var newList: List<Counterpoint>
    val directions = trend.directions.filter{ intervalSetHorizontal.value!!.contains(it)}
    viewModelScope.launch(Dispatchers.Main){
        withContext(Dispatchers.Default){
            newList = freeParts(selectedCounterpoint.value!!,  intervalSet.value!!, directions)
                .sortedBy { it.emptiness }//.take(maxVisibleCounterpoints)
                .mapIf(spread != 0){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                .sortedBy { it.emptiness }
        }
        changeCounterpointsWithLimitAndCache(newList, true)
    }
}
fun AppViewModel.addSequenceToCounterpoint(repeat: Boolean){
    if(!selectedCounterpoint.value!!.isEmpty()){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = addSequence(selectedCounterpoint.value!! , sequenceToAdd.value!!, intervalSet.value!! ,repeat, 7)
                    .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }//.take(maxVisibleCounterpoints)
                    .pmapIf(spread != 0){
                        it.spreadAsPossible(true, intervalSet = intervalSet.value!!)}
                    //.map{ it.emptiness = it.findEmptiness(); it}
                    .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
            }
            changeCounterpointsWithLimitAndCache(newList, true)
        }
    }
}

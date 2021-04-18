package com.cristianovecchi.mikrokanon

import androidx.lifecycle.*
import com.cristianovecchi.mikrokanon.AIMUSIC.Counterpoint
import com.cristianovecchi.mikrokanon.AIMUSIC.Insieme
import com.cristianovecchi.mikrokanon.AIMUSIC.MikroKanon
import com.cristianovecchi.mikrokanon.AIMUSIC.TREND
import com.cristianovecchi.mikrokanon.composables.*
import com.cristianovecchi.mikrokanon.dao.SequenceData
import com.cristianovecchi.mikrokanon.dao.SequenceDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

sealed class Computation {
    data class MikroKanonOnly(val counterpoint: Counterpoint,val sequenceToMikroKanon: ArrayList<Clip>, val nParts: Int): Computation()
    data class FirstFromKP(val counterpoint: Counterpoint, val firstSequence: ArrayList<Clip>, val indexSequenceToAdd: Int): Computation()
    data class FurtherFromKP(val counterpoint: Counterpoint,val indexSequenceToAdd: Int): Computation()
    data class FirstFromFreePart(val counterpoint: Counterpoint,val firstSequence: ArrayList<Clip>, val trend: TREND): Computation()
    data class FurtherFromFreePart(val counterpoint: Counterpoint,val firstSequence: ArrayList<Clip>, val trend: TREND): Computation()
    data class Expand(val counterpoints: List<Counterpoint>) : Computation()
}


class AppViewModel(private val repository: SequenceDataRepository) : ViewModel() {

//    val seq1 = ArrayList<Clip>(randomClipSequence(NoteNamesIt.values().map{it.toString()},0,10, false))
//    val seq2 = ArrayList<Clip>(randomClipSequence(NoteNamesIt.values().map{it.toString()},0,7, false))

    private val SPREAD_AS_POSSIBLE = true
    private val MAX_VISIBLE_COUNTERPOINTS: Int = 18
    val allSequencesData: LiveData<List<SequenceData>> = repository.allSequences.asLiveData()
    private val _sequences = MutableLiveData<List<ArrayList<Clip>>>(listOf())
    val counterpointStack = Stack<Computation>()
    private var elaborating = false
    private data class CacheKey(val sequence: List<Int>, val intervalSet: List<Int>)
    private val mk3cache = HashMap<CacheKey, List<Counterpoint>>()
    private val mk4cache = HashMap<CacheKey, List<Counterpoint>>()
    // macro Functions called by fragments -----------------------------------------------------
    val dispatchIntervals = { newIntervals: List<Int> ->
        refreshComputation(false)
    }
    val onExpand = {
        val counterpointsClone = counterpoints.value!!.map{
            it.clone()
        }
        counterpointStack.push(Computation.Expand(counterpointsClone))
        val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        expandCounterpoints(index)
    }
    val onKPfurtherSelections = {index: Int ->
        counterpointStack.push(Computation.FurtherFromKP(selectedCounterpoint.value!!.clone(), index))
        changeSequenceToAdd(sequences.value!![index])
        addSequenceToCounterpoint()

    }
    val onKPfromFirstSelection = {list: ArrayList<Clip>, index: Int ->
        changeFirstSequence(list)
        counterpointStack.push(Computation.FirstFromKP(selectedCounterpoint.value!!.clone(),
                                ArrayList(firstSequence.value!!), index))
        convertFirstSequenceToSelectedCounterpoint()
        changeSequenceToAdd(sequences.value!![index])
        addSequenceToCounterpoint()

    }
    val onFreePartFromFirstSelection = { list: ArrayList<Clip>, trend: TREND ->
        changeFirstSequence(list)
        counterpointStack.push(Computation.FirstFromFreePart(selectedCounterpoint.value!!.clone(),ArrayList(firstSequence.value!!), trend))
        convertFirstSequenceToSelectedCounterpoint()
        findFreeParts(trend)
    }
    val onFreePartFurtherSelections = { trend: TREND ->
        counterpointStack.push(Computation.FurtherFromFreePart(selectedCounterpoint.value!!.clone(),ArrayList(firstSequence.value!!), trend))
        findFreeParts(trend)
    }
    val onMikroKanons = {list: ArrayList<Clip> ->
        counterpointStack.push(Computation.MikroKanonOnly(selectedCounterpoint.value!!.clone(),ArrayList(sequenceToMikroKanons.value!!),2))
        if(list.isNotEmpty()) changeSequenceToMikroKanons(list)
            findCounterpointsByMikroKanons()

    }
    val onMikroKanons3 = {list: ArrayList<Clip> ->
        counterpointStack.push(Computation.MikroKanonOnly(selectedCounterpoint.value!!.clone(),
                                ArrayList(sequenceToMikroKanons.value!!),3))
        if(list.isNotEmpty()) changeSequenceToMikroKanons(list)
        findCounterpointsByMikroKanons3()
    }
    val onMikroKanons4 = {list: ArrayList<Clip> ->
        counterpointStack.push(Computation.MikroKanonOnly(selectedCounterpoint.value!!.clone(),
                                ArrayList(sequenceToMikroKanons.value!!),4))
        if(list.isNotEmpty()) changeSequenceToMikroKanons(list)
        findCounterpointsByMikroKanons4()
    }
    val onBack = {
        if(counterpointStack.size > 1) {
            refreshComputation(true)
        }
    }
    //-------------end macro functions--------------------

    fun refreshComputation(stepBack: Boolean){
            if (!elaborating) {
                elaborating = true
                if (stepBack ) counterpointStack.pop()
                val previousComputation = when(counterpointStack.lastElement()){
                    is Computation.Expand -> counterpointStack.lastElement()
                    else -> counterpointStack.pop()
                }
                when (previousComputation) {
                    is Computation.FirstFromFreePart -> onFreePartFromFirstSelection(
                        previousComputation.firstSequence, previousComputation.trend
                    )
                    is Computation.FurtherFromFreePart -> {
                        changeSelectedCounterpoint(previousComputation.counterpoint)
                        onFreePartFurtherSelections(previousComputation.trend)
                    }
                    is Computation.FirstFromKP -> onKPfromFirstSelection(
                        previousComputation.firstSequence,
                        previousComputation.indexSequenceToAdd
                    )
                    is Computation.FurtherFromKP -> {
                        changeSelectedCounterpoint(previousComputation.counterpoint)
                        onKPfurtherSelections(previousComputation.indexSequenceToAdd)
                    }
                    is Computation.MikroKanonOnly -> {
                        println(sequenceToMikroKanons.value!!)
                        //changeSequenceToMikroKanons(previousComputation.sequenceToMikroKanon)
                        when (previousComputation.nParts) {
                            2 -> onMikroKanons(ArrayList(sequenceToMikroKanons.value!!))
                            3 -> onMikroKanons3(ArrayList(sequenceToMikroKanons.value!!))
                            4 -> onMikroKanons4(ArrayList(sequenceToMikroKanons.value!!))
                            else -> Unit
                        }
                    }
                    is Computation.Expand -> {
                        val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
                        changeCounterPoints(previousComputation.counterpoints)
                        expandCounterpoints(index)

                    }
                }
                elaborating = false
            }

    }
    fun findFreeParts(trend: TREND){
        _counterpoints.value = emptyList()
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = findFreePts(trend)
            }
            changeCounterPoints(newList)
            counterpoints.value?.get(0)?.let {changeSelectedCounterpoint(it)}
            // println("STACK SIZE: ${counterpointStack.size}")
        }
    }
    private suspend fun findFreePts(trend: TREND) : List<Counterpoint>{
        var newList = Counterpoint.findAllFreeParts(
            selectedCounterpoint.value!!,  intervalSet.value!!, trend.directions
        )
            .sortedBy { it.emptiness }.take(MAX_VISIBLE_COUNTERPOINTS)

        if(SPREAD_AS_POSSIBLE){
            newList = newList.map{it.spreadAsPossible()}.sortedBy { it.emptiness }
        }
        return newList
    }
    fun findCounterpointsByMikroKanons4(){
        _counterpoints.value = emptyList()
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = findCpByMikroKanons4()
            }
            changeCounterPoints(newList)
            counterpoints.value?.get(0)?.let {changeSelectedCounterpoint(it)}
        }
    }

    private suspend fun findCpByMikroKanons4(): List<Counterpoint> {
        if(sequenceToMikroKanons.value!!.isNotEmpty()) {
            val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList()
            val key = CacheKey(sequence, intervalSet.value!!)
            if(mk4cache.containsKey(key)){
                return mk4cache[key]!!
            } else {
                val counterpoints = MikroKanon.findAll4AbsPartMikroKanons(
                    sequence,  intervalSet.value!!, 2
                ).map { it.toCounterpoint() }.sortedBy { it.emptiness }.take(MAX_VISIBLE_COUNTERPOINTS)
                mk4cache[key] = counterpoints
                return counterpoints
            }

        } else {
            println("Sequence to MikroKanons is empty.")
            return emptyList()
        }
    }
    fun findCounterpointsByMikroKanons3(){
        _counterpoints.value = emptyList()
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
             withContext(Dispatchers.Default){
                    newList = findCpByMikroKanons3()
                }
                changeCounterPoints(newList)
                counterpoints.value?.get(0)?.let {changeSelectedCounterpoint(it)}
        }
    }
    private suspend fun findCpByMikroKanons3(): List<Counterpoint>{

        if(sequenceToMikroKanons.value!!.isNotEmpty()) {
            val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList()
            val key = CacheKey(sequence, intervalSet.value!!)
            if(mk3cache.containsKey(key)){
                return mk3cache[key]!!
            } else {
                val counterpoints = MikroKanon.findAll3AbsPartMikroKanons(
                    sequence,  intervalSet.value!!, 5
                ).map { it.toCounterpoint() }.sortedBy { it.emptiness }.take(MAX_VISIBLE_COUNTERPOINTS)
                mk3cache[key] = counterpoints
                return counterpoints
            }

        } else {
            println("Sequence to MikroKanons is empty.")
            return emptyList()
        }
    }
    fun findCounterpointsByMikroKanons(){
        _counterpoints.value = emptyList()
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = findCpByMikroKanons()
            }
            changeCounterPoints(newList)
            counterpoints.value?.get(0)?.let {changeSelectedCounterpoint(it)}
        }
    }
    private suspend fun findCpByMikroKanons(): List<Counterpoint>{
        if(sequenceToMikroKanons.value!!.isNotEmpty()) {
            return MikroKanon.findAll2AbsPartMikroKanons(
                sequenceToMikroKanons.value!!.map { it.abstractNote }.toList(),
                intervalSet.value!!, 5
            ).map { it.toCounterpoint() }.sortedBy { it.emptiness }.take(MAX_VISIBLE_COUNTERPOINTS)
        } else {
            println("Sequence to MikroKanons is empty.")
            return emptyList()
        }
    }
    fun expandCounterpoints(index: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = expandCps()
                }
                changeCounterPoints(newList)
                if(index in counterpoints.value!!.indices){
                    changeSelectedCounterpoint(counterpoints.value!![index])
                } else {
                    counterpoints.value?.get(0)?.let {changeSelectedCounterpoint(it)}
                }
                // println("STACK SIZE: ${counterpointStack.size}")
            }
        }
    }
    private suspend fun expandCps(): List<Counterpoint>{
        return counterpoints.value!!.map{
            Counterpoint.expand(it,2)
        }
    }
    fun addSequenceToCounterpoint(){
        if(!selectedCounterpoint.value!!.isEmpty()){
            _counterpoints.value = emptyList()
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = addSeqToCounterpoint()
                }
                changeCounterPoints(newList)
                counterpoints.value?.get(0)?.let {changeSelectedCounterpoint(it)}
                // println("STACK SIZE: ${counterpointStack.size}")
            }
        }
    }
    private suspend fun addSeqToCounterpoint(): List<Counterpoint> {
        var newList = Counterpoint.findAllCounterpoints(
            selectedCounterpoint.value!! , sequenceToAdd.value!!.map { it.abstractNote }.toList(),
            intervalSet.value!!, 5
        ).sortedBy { it.emptiness }.take(MAX_VISIBLE_COUNTERPOINTS)
        if(SPREAD_AS_POSSIBLE){
            newList = newList.map{it.spreadAsPossible()}.sortedBy { it.emptiness }
        }
        return newList
    }

    fun convertFirstSequenceToSelectedCounterpoint() {
        val newCounterpoint = Counterpoint.counterpointFromClipList(firstSequence.value!!)
        _selectedCounterpoint.value = newCounterpoint
    }

    fun convertMikroKanonSequenceToCounterpoint() {
        val newCounterpoint = Counterpoint.counterpointFromClipList(sequenceToMikroKanons.value!!)
        _selectedCounterpoint.value = newCounterpoint
    }

    fun setInitialBlankState() {
        counterpointStack.clear()
        changeCounterPoints(listOf())
        changeSequenceToMikroKanons(listOf())
        changeFirstSequence(listOf())
        changeSequenceToAdd(listOf())
        changeSelectedCounterpoint(Counterpoint.empty())
        changeSequenceSelection(-1)
    }

    val sequences : LiveData<List<ArrayList<Clip>>> = _sequences

    private var _firstSequence= MutableLiveData<List<Clip>>(listOf())
    val firstSequence : LiveData<List<Clip>> = _firstSequence

    private var _sequenceToAdd = MutableLiveData<List<Clip>>(listOf())
    val sequenceToAdd : LiveData<List<Clip>> = _sequenceToAdd

    private val _selectedSequence = MutableLiveData<Int>(-1)
    val selectedSequence : LiveData<Int> = _selectedSequence

    private var _sequenceToMikroKanons = MutableLiveData<List<Clip>>(listOf())
    val sequenceToMikroKanons : LiveData<List<Clip>> = _sequenceToMikroKanons

    private val _counterpoints = MutableLiveData<List<Counterpoint>>(listOf())
    val counterpoints : LiveData<List<Counterpoint>> = _counterpoints

    private val _intervalSet = MutableLiveData<List<Int>>(listOf(2, 10, 3, 9, 4, 8, 5, 7))
    val intervalSet : LiveData<List<Int>> = _intervalSet

    private var _selectedCounterpoint = MutableLiveData<Counterpoint>(Counterpoint.empty())
    val selectedCounterpoint : LiveData<Counterpoint> = _selectedCounterpoint


    fun changeSelectedCounterpoint(newCounterpoint: Counterpoint){
        _selectedCounterpoint.value = newCounterpoint
//       println("SELECTED COUNTERPOINT:")
//       println(selectedCounterpoint.value!!.display())
    }
    fun changeIntervalSet(newIntervalSet: List<Int>){
        val sortedList = newIntervalSet.sorted()
        _intervalSet.value = sortedList
    }
    fun changeCounterPoints(newCounterpoints: List<Counterpoint>){
        _counterpoints.value = newCounterpoints
    }
    fun changeSequenceToMikroKanons(newSequenceToMikroKanons: List<Clip>){
        _sequenceToMikroKanons.value = newSequenceToMikroKanons
    }
    fun changeSequenceToAdd(newSequenceToAdd: List<Clip>){
        _sequenceToAdd.value = newSequenceToAdd
    }
    fun changeFirstSequence(newFirstSequence: List<Clip>){
        _firstSequence.value = newFirstSequence
    }
    fun changeSequenceSelection(newIndex: Int) {
        _selectedSequence.value = newIndex
    }
    fun removeIntervals(list: List<Int>){
        val newList = intervalSet.value!!.toMutableList()
        newList.removeAll(list)
        changeIntervalSet(newList)
    }
    fun addIntervals(list: List<Int>){
        val newList = intervalSet.value!!.toMutableList()
        newList.addAll(list)
        changeIntervalSet(newList)
    }


    // ROOM ---------------------------------------------------------------------
    fun addSequence(sequence: ArrayList<Clip>){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(SequenceData(0,sequence.map { clipToDataClip(it) }))
        }
    }
    fun deleteSequence(index: Int){
        val sequence = sequences.value!![index]
        val sequenceData = map[sequence]
        viewModelScope.launch(Dispatchers.IO) {
            if (sequenceData != null) {
                repository.delete(sequenceData)
            }
        }
        changeSequenceSelection(-1)
    }
    fun updateSequence(index: Int, sequence: ArrayList<Clip>){
        val oldSequence = sequences.value!![index]
        val sequenceData = map[oldSequence]
        viewModelScope.launch(Dispatchers.IO) {
            if (sequenceData != null) {
                repository.delete(sequenceData)
                repository.insert(SequenceData(0,sequence.map { clipToDataClip(it) }))
            }
        }
    }

    fun retrieveSequencesFromDB(){
        map.clear()
        _sequences.value = allSequencesData.value!!.map{sequenceDataToSequence(it)}
    }
    val map = HashMap<ArrayList<Clip>, SequenceData>(emptyMap())
    val noteNames: List<String> = NoteNamesIt.values().map{it.toString()}
    fun sequenceDataToSequence(sequenceData: SequenceData) : ArrayList<Clip>{
        val sequence = ArrayList(sequenceData.clips.map { clipDataToClip(it)})
        map[sequence] = sequenceData
        return sequence
    }




}
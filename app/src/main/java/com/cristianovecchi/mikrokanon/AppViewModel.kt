package com.cristianovecchi.mikrokanon

import androidx.lifecycle.*
import com.cristianovecchi.mikrokanon.AIMUSIC.Counterpoint
import com.cristianovecchi.mikrokanon.AIMUSIC.MikroKanon
import com.cristianovecchi.mikrokanon.composables.*
import com.cristianovecchi.mikrokanon.dao.SequenceData
import com.cristianovecchi.mikrokanon.dao.SequenceDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AppViewModel(private val repository: SequenceDataRepository) : ViewModel() {

//    val seq1 = ArrayList<Clip>(randomClipSequence(NoteNamesIt.values().map{it.toString()},0,10, false))
//    val seq2 = ArrayList<Clip>(randomClipSequence(NoteNamesIt.values().map{it.toString()},0,7, false))

    private val MAX_VISIBLE_COUNTERPOINTS: Int = 18
    val allSequencesData: LiveData<List<SequenceData>> = repository.allSequences.asLiveData()
    private val _sequences = MutableLiveData<List<ArrayList<Clip>>>(listOf())
    private val counterpointStack = Stack<Counterpoint>()

    // macro Functions called by fragments -----------------------------------------------------
    val dispatchIntervals = { newIntervals: List<Int> ->
        //changeIntervalSet(newIntervals)
        if (isJustMikrokanonState()){ // handling just a mikrokanon
            //println("CALLING DISPATCHINTERVALS IN STATE: JUST MIKROKANON")
            counterpointStack.pop()
            findCounterpointsByMikroKanons()

        } else if(isFirstSequenceAddingState()) {
            //println("CALLING DISPATCHINTERVALS IN STATE: FIRST SEQUENCE ADDING")
            counterpointStack.pop()
            convertFirstSequenceToSelectedCounterpoint()
            addSequenceToCounterpoint()
        } else if(isFurtherSequenceAddingState())
        {
            //println("CALLING DISPATCHINTERVALS IN STATE: FURTHER SEQUENCE ADDING ")
            val previousCounterpoint = counterpointStack.pop()
            changeSelectedCounterpoint(previousCounterpoint)
            addSequenceToCounterpoint()
        }
    }
    val onKPfurtherSelections = {index: Int ->
        changeSequenceToAdd(sequences.value!![index])
        addSequenceToCounterpoint()

    }
    val onKPfromFirstSelection = {list: ArrayList<Clip>, index: Int ->
        changeFirstSequence(list)
        convertFirstSequenceToSelectedCounterpoint()
        changeSequenceToAdd(sequences.value!![index])
        addSequenceToCounterpoint()

    }
    val onMikroKanons = {list: ArrayList<Clip> ->
        if(list.isNotEmpty()) changeSequenceToMikroKanons(list)
            findCounterpointsByMikroKanons()
    }
    //-------------end macro functions--------------------

    fun isJustMikrokanonState() : Boolean {
        return sequenceToMikroKanons.value!!.isNotEmpty() && counterpointStack.size == 1
    }
    fun isFirstSequenceAddingState() : Boolean {
        return firstSequence.value!!.isNotEmpty() && counterpointStack.size == 1
    }
    fun isFurtherSequenceAddingState() : Boolean {
        return counterpointStack.size > 1
    }

    fun findCounterpointsByMikroKanons(){
        _counterpoints.value = emptyList()
        viewModelScope.launch(Dispatchers.Unconfined){
            counterpointStack.push(selectedCounterpoint!!.value) //saving the previous counterpoint
            findCounterpoints()
            counterpoints.value?.get(0)?.let {changeSelectedCounterpoint(it)}
            // println("STACK SIZE: ${counterpointStack.size}")
        }
    }
    private suspend fun findCounterpoints(){

        if(sequenceToMikroKanons.value!!.isNotEmpty()) {
            _counterpoints.value = MikroKanon.findAll2AbsPartMikroKanons(
                sequenceToMikroKanons.value!!.map { it.abstractNote }.toList(),
                intervalSet.value!!, 5
            ).map { it.toCounterpoint() }.sortedBy { it.emptiness }.take(MAX_VISIBLE_COUNTERPOINTS)
        } else {
            println("Sequence to MikroKanons is empty.")
        }
    }

    fun addSequenceToCounterpoint(){
        if(!selectedCounterpoint.value!!.isEmpty()){
            _counterpoints.value = emptyList()
            viewModelScope.launch(Dispatchers.Unconfined){
                counterpointStack.push(selectedCounterpoint!!.value) //saving the previous counterpoint
                addSeqToCounterpoint()
                counterpoints.value?.get(0)?.let {changeSelectedCounterpoint(it)}
                // println("STACK SIZE: ${counterpointStack.size}")
            }
        }
    }
    private suspend fun addSeqToCounterpoint() {
        _counterpoints.value = Counterpoint.findAllCounterpoints(
            selectedCounterpoint.value!! , sequenceToAdd.value!!.map { it.abstractNote }.toList(),
            intervalSet.value!!, 5
        ).sortedBy { it.emptiness }.take(MAX_VISIBLE_COUNTERPOINTS)
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
        println("SELECTED COUNTERPOINT:")
       println(selectedCounterpoint.value!!.display())
    }
    fun changeIntervalSet(newIntervalSet: List<Int>){
        _intervalSet.value = newIntervalSet
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
        _intervalSet.value = newList
    }
    fun addIntervals(list: List<Int>){
        val newList = intervalSet.value!!.toMutableList()
        newList.addAll(list)
        _intervalSet.value = newList
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
package com.cristianovecchi.mikrokanon

import android.content.Context
import androidx.lifecycle.*
import com.cristianovecchi.mikrokanon.composables.*
import com.cristianovecchi.mikrokanon.dao.SequenceData
import com.cristianovecchi.mikrokanon.dao.SequenceDataRepository
import com.cristianovecchi.mikrokanon.midi.Player
import com.leff.midi.MidiFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import android.media.MediaPlayer
import com.cristianovecchi.mikrokanon.AIMUSIC.*
import android.content.SharedPreferences
import com.cristianovecchi.mikrokanon.dao.UserOptionsData
import com.cristianovecchi.mikrokanon.dao.UserOptionsDataRepository


sealed class Computation {
    data class MikroKanonOnly(val counterpoint: Counterpoint,val sequenceToMikroKanon: ArrayList<Clip>, val nParts: Int): Computation()
    data class FirstFromKP(val counterpoint: Counterpoint, val firstSequence: ArrayList<Clip>, val indexSequenceToAdd: Int, val repeat: Boolean): Computation()
    data class FurtherFromKP(val counterpoint: Counterpoint,val indexSequenceToAdd: Int, val repeat: Boolean): Computation()
    data class FirstFromFreePart(val counterpoint: Counterpoint,val firstSequence: ArrayList<Clip>, val trend: TREND): Computation()
    data class FurtherFromFreePart(val counterpoint: Counterpoint,val firstSequence: ArrayList<Clip>, val trend: TREND): Computation()
    data class Expand(val counterpoints: List<Counterpoint>, val index: Int) : Computation()
}


class AppViewModel(private val repository: SequenceDataRepository, private val userRepository: UserOptionsDataRepository) : ViewModel() {

//    val seq1 = ArrayList<Clip>(randomClipSequence(NoteNamesIt.values().map{it.toString()},0,10, false))
//    val seq2 = ArrayList<Clip>(randomClipSequence(NoteNamesIt.values().map{it.toString()},0,7, false))
    private var lastIndex = 0
    private val SPREAD_AS_POSSIBLE = true
    private val MAX_VISIBLE_COUNTERPOINTS: Int = 18
    private var ensembleTypeSelected: EnsembleType = EnsembleType.STRING_ORCHESTRA
    val allSequencesData: LiveData<List<SequenceData>> = repository.allSequences.asLiveData()
    val userOptionsData: LiveData<List<UserOptionsData>> = userRepository.userOptions.asLiveData()
    private val _sequences = MutableLiveData<List<ArrayList<Clip>>>(listOf())
    val counterpointStack = Stack<Computation>()
    private var elaborating = false
    private data class CacheKey(val sequence: List<Int>, val intervalSet: List<Int>)
    private val mk3cache = HashMap<CacheKey, List<Counterpoint>>()
    private val mk4cache = HashMap<CacheKey, List<Counterpoint>>()
    private var mediaPlayer: MediaPlayer? = null




    // macro Functions called by fragments -----------------------------------------------------
    val onPlay = {
        if(userOptionsData.value!!.isEmpty()){
            retrieveUserOptionsFromDB() // Check if work at first installation (no previous DB)
        }
        if(!selectedCounterpoint.value!!.isEmpty()){
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
            val ensType: EnsembleType = EnsembleType.values()[userOptionsData.value?.let {
                Integer.parseInt(
                    userOptionsData.value!![0].ensembleType
                )
            } ?: 0]
            val bpm: Float = userOptionsData.value?.let {
                Integer.parseInt(
                    userOptionsData.value!![0].bpm
                ).toFloat()} ?: 90f
            val rhythm: RhythmPatterns = RhythmPatterns.values()[userOptionsData.value?.let {
                Integer.parseInt(
                    userOptionsData.value!![0].rhythm
                )
            } ?: 0]
            Player.playCounterpoint(mediaPlayer!!,false,selectedCounterpoint.value!!,
                bpm,0f,rhythm.values, ensType)
        }

    }
    val dispatchIntervals = {
        refreshComputation(false)
    }
    val onExpand = {
        val counterpointsClone = counterpoints.value!!.map{
            it.clone()
        }
        val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        counterpointStack.push(Computation.Expand(counterpointsClone, index))
        expandCounterpoints(index)
        println("ON EXPAND")
    }
    val onKPfurtherSelections = {index: Int , repeat: Boolean->
        counterpointStack.push(Computation.FurtherFromKP(selectedCounterpoint.value!!.clone(), index, repeat))
        changeSequenceToAdd(sequences.value!![index])
        if(!repeat) addSequenceToCounterpoint() else addRepeatedSequenceToCounterpoint()

    }
    val onKPfromFirstSelection = {list: ArrayList<Clip>, index: Int, repeat: Boolean ->
        //println(repeat)
        changeFirstSequence(list)
        counterpointStack.push(Computation.FirstFromKP(selectedCounterpoint.value!!.clone(),
                                ArrayList(firstSequence.value!!), index, repeat))
        convertFirstSequenceToSelectedCounterpoint()
        changeSequenceToAdd(sequences.value!![index])
        if(!repeat) addSequenceToCounterpoint() else addRepeatedSequenceToCounterpoint()

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
            println("ON BACK")
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
                        previousComputation.indexSequenceToAdd,
                        previousComputation.repeat
                    )
                    is Computation.FurtherFromKP -> {
                        changeSelectedCounterpoint(previousComputation.counterpoint)
                        onKPfurtherSelections(previousComputation.indexSequenceToAdd,previousComputation.repeat)
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
                        //val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
                        if(stepBack){
                            changeCounterPoints(previousComputation.counterpoints)
                            expandCounterpoints(previousComputation.index)
                        } else {
                            elaborating = false
                            var count = 0
                            var originalComputation: Computation

                            viewModelScope.launch(Dispatchers.Unconfined){
                                do {

                                    onBack()
                                    originalComputation = counterpointStack.lastElement()
//                                    if (originalComputation is Computation.Expand) {
//                                        counterpointStack.pop()
//                                        counterpointStack.pop()
//                                    }
                                    count++
                                } while(originalComputation is Computation.Expand )
                                //for(i in 0 until count) {println("count: $i"); onExpand() }
                            }
                            count = 0
//                            when(originalComputation){
//                                is Computation.MikroKanonOnly -> {
//                                    when (originalComputation.nParts) {
//                                        2 -> onMikroKanons(ArrayList(sequenceToMikroKanons.value!!))
//                                        3 -> onMikroKanons3(ArrayList(sequenceToMikroKanons.value!!))
//                                        4 -> onMikroKanons4(ArrayList(sequenceToMikroKanons.value!!))
//                                        else -> Unit
//                                    }
//                                }
//                                is Computation.FirstFromFreePart -> onFreePartFromFirstSelection(
//                                    originalComputation.firstSequence, originalComputation.trend
//                                )
//                                is Computation.FirstFromKP -> onKPfromFirstSelection(
//                                    originalComputation.firstSequence,
//                                    originalComputation.indexSequenceToAdd
//                                )
//                                is Computation.FurtherFromFreePart -> {
//                                    changeSelectedCounterpoint(originalComputation.counterpoint)
//                                    onFreePartFurtherSelections(originalComputation.trend)
//                                }
//                                is Computation.FurtherFromKP -> {
//                                    changeSelectedCounterpoint(originalComputation.counterpoint)
//                                    onKPfurtherSelections(originalComputation.indexSequenceToAdd)
//                                }
//                                else -> Unit
//                            }
//                            //elaborating = false
//                            println("REEXPANDE count=$count")

                        }
                    }
                }
                elaborating = false
            }

    }
    fun findFreeParts(trend: TREND){
        //_counterpoints.value = emptyList()
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
        //_counterpoints.value = emptyList()
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
        //_counterpoints.value = emptyList()
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
        //_counterpoints.value = emptyList()
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
    fun addRepeatedSequenceToCounterpoint(){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = addRepSeqToCounterpoint()
                }
                changeCounterPoints(newList)
                counterpoints.value?.get(0)?.let {changeSelectedCounterpoint(it)}
            }
        }
    }
    private suspend fun addRepSeqToCounterpoint(): List<Counterpoint> {
        var newList = Counterpoint.findAllCounterpointsWithRepeatedSequence(
            selectedCounterpoint.value!! , sequenceToAdd.value!!.map { it.abstractNote }.toList(),
            intervalSet.value!!, 5
        ).sortedBy { it.emptiness }.take(MAX_VISIBLE_COUNTERPOINTS)
        if(SPREAD_AS_POSSIBLE){
            newList = newList.map{it.spreadAsPossible()}.sortedBy { it.emptiness }
        }
        return newList
    }
    fun addSequenceToCounterpoint(){
        if(!selectedCounterpoint.value!!.isEmpty()){
            //_counterpoints.value = emptyList()
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
        lastIndex = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
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
    fun removeIntervalsAndRefresh(list: List<Int>){
        val newList = intervalSet.value!!.toMutableList()
        newList.removeAll(list)
        changeIntervalSet(newList)
        dispatchIntervals()
    }
    fun addIntervalsAndRefresh(list: List<Int>){
        val newList = intervalSet.value!!.toMutableList()
        newList.addAll(list)
        changeIntervalSet(newList)
        dispatchIntervals()
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
    fun retrieveUserOptionsFromDB(){

    }

    fun updateUserOptions(key: String, value: String){
        var newUserOptionsData: UserOptionsData? = null
        val optionsDataClone = if(userOptionsData.value!!.isEmpty())
                                UserOptionsData(0,"0","90","0")
                                else userOptionsData.value!![0].copy()
        when(key){
            "ensemble_type" -> {
                newUserOptionsData = optionsDataClone.copy(ensembleType = value)
            }
            "bpm" -> {
                newUserOptionsData = optionsDataClone.copy(bpm = value)
            }
            "rhythm" -> {
                newUserOptionsData = optionsDataClone.copy(rhythm = value)
            }
        }
        newUserOptionsData?.let {
            viewModelScope.launch(Dispatchers.IO) {
                if(userOptionsData.value!!.isNotEmpty()){
                    userRepository.deleteAllUserOptions()
                }
                userRepository.insertUserOptions(newUserOptionsData)
            }
        }


    }
}
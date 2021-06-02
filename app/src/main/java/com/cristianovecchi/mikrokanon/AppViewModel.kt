package com.cristianovecchi.mikrokanon

import android.app.Application
import android.content.Intent
import androidx.lifecycle.*
import com.cristianovecchi.mikrokanon.composables.*
import com.cristianovecchi.mikrokanon.db.SequenceData
import com.cristianovecchi.mikrokanon.db.SequenceDataRepository
import com.cristianovecchi.mikrokanon.midi.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import android.media.MediaPlayer
import androidx.compose.runtime.collectAsState
import com.cristianovecchi.mikrokanon.AIMUSIC.*
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.cristianovecchi.mikrokanon.db.UserOptionsData
import com.cristianovecchi.mikrokanon.db.UserOptionsDataRepository
import java.io.File


sealed class Computation {
    data class MikroKanonOnly(val counterpoint: Counterpoint,val sequenceToMikroKanon: ArrayList<Clip>, val nParts: Int): Computation()
    data class FirstFromKP(val counterpoint: Counterpoint, val firstSequence: ArrayList<Clip>, val indexSequenceToAdd: Int, val repeat: Boolean): Computation()
    data class FurtherFromKP(val counterpoint: Counterpoint,val indexSequenceToAdd: Int, val repeat: Boolean): Computation()
    data class FirstFromFreePart(val counterpoint: Counterpoint,val firstSequence: ArrayList<Clip>, val trend: TREND): Computation()
    data class FurtherFromFreePart(val counterpoint: Counterpoint,val firstSequence: ArrayList<Clip>, val trend: TREND): Computation()
    data class Expand(val counterpoints: List<Counterpoint>, val index: Int) : Computation()
}
enum class LANGUAGES(val language:String){
    en("English"), fr("Français"), it("Italiano")
}
data class ActiveButtons(val editing: Boolean = false, val mikrokanon: Boolean = false,
                         val undo: Boolean = false, val expand: Boolean = true,
                         val counterpoint: Boolean = false, val freeparts: Boolean = false, val play: Boolean = true)


class AppViewModel(application: Application, private val sequenceRepository: SequenceDataRepository, private val userRepository: UserOptionsDataRepository) : AndroidViewModel(application) {

    val creditsUri: String = "https://www.youtube.com/channel/UCe9Kd87V90fbPsUBU5gaXKw/playlists?view=1&sort=dd&shelf_id=0"

    val iconMap = mapOf(
       // "mikrokanon" to R.drawable.ic_baseline_account_tree_24,
        "mikrokanon" to R.drawable.ic_baseline_clear_all_24,
        "counterpoint" to R.drawable.ic_baseline_drag_handle_24,
        "done" to R.drawable.ic_baseline_done_24,
        "add" to R.drawable.ic_baseline_add_24,
        "delete" to R.drawable.ic_baseline_delete_forever_24,
        "edit" to R.drawable.ic_baseline_edit_24,
        "undo" to R.drawable.ic_baseline_undo_24,
        "forward" to R.drawable.ic_baseline_arrow_forward_24,
        "full_forward" to R.drawable.ic_baseline_fast_forward_24,
        "back" to R.drawable.ic_baseline_arrow_back_24,
        "full_back" to R.drawable.ic_baseline_fast_rewind_24,
        "play" to R.drawable.ic_baseline_play_arrow_24,
        "expand" to R.drawable.ic_baseline_sync_alt_24,
    )


    private fun Stack<Computation>.pushAndDispatch(computation: Computation){
        push(computation)
        this@AppViewModel._stackSize.value = this.size
    }
    private fun Stack<Computation>.popAndDispatch(){
        pop()
        this@AppViewModel._stackSize.value = this.size
    }
    private fun Stack<Computation>.clearAndDispatch(){
        clear()
        this@AppViewModel._stackSize.value = this.size
    }

    private var lastIndex = 0
    private val SPREAD_AS_POSSIBLE = true
    private val MAX_VISIBLE_COUNTERPOINTS: Int = 18
    private var ensembleTypeSelected: EnsembleType = EnsembleType.STRING_ORCHESTRA
    private val sequenceDataMap = HashMap<ArrayList<Clip>, SequenceData>(emptyMap())

    private val _activeButtons = MutableLiveData<ActiveButtons>(ActiveButtons())
    val activeButtons : LiveData<ActiveButtons> = _activeButtons

    private val _sequences = MutableLiveData<List<ArrayList<Clip>>>(listOf())
    val sequences : LiveData<List<ArrayList<Clip>>> = _sequences

    private var _notesNames = MutableLiveData<List<String>>(listOf("do","re","mi","fa","sol","la","si"))
    var notesNames: LiveData<List<String>> = _notesNames

    private var _elaborating = MutableLiveData<Boolean>(false)
    var elaborating: LiveData<Boolean> = _elaborating

    private var _firstSequence= MutableLiveData<List<Clip>>(listOf())
    val firstSequence : LiveData<List<Clip>> = _firstSequence

    private var _sequenceToAdd = MutableLiveData<List<Clip>>(listOf())
    val sequenceToAdd : LiveData<List<Clip>> = _sequenceToAdd

    private val _selectedSequence = MutableLiveData<Int>(-1)
    val selectedSequence : LiveData<Int> = _selectedSequence

    private val _stackSize = MutableLiveData<Int>(1)
    val stackSize : LiveData<Int> = _stackSize

    private var _sequenceToMikroKanons = MutableLiveData<List<Clip>>(listOf())
    val sequenceToMikroKanons : LiveData<List<Clip>> = _sequenceToMikroKanons

    private val _counterpoints = MutableLiveData<List<Counterpoint>>(listOf())
    val counterpoints : LiveData<List<Counterpoint>> = _counterpoints

    private val _intervalSet = MutableLiveData<List<Int>>(listOf(2, 10, 3, 9, 4, 8, 5, 7))
    val intervalSet : LiveData<List<Int>> = _intervalSet

    private var _selectedCounterpoint = MutableLiveData<Counterpoint>(Counterpoint.empty())
    val selectedCounterpoint : LiveData<Counterpoint> = _selectedCounterpoint
    val allSequencesData: LiveData<List<SequenceData>> = sequenceRepository.allSequences.asLiveData()
    val userOptionsData: LiveData<List<UserOptionsData>> = userRepository.userOptions.asLiveData()

    private val computationStack = Stack<Computation>()

    private data class CacheKey(val sequence: List<Int>, val intervalSet: List<Int>)
    private val mk3cache = HashMap<CacheKey, List<Counterpoint>>()
    private val mk4cache = HashMap<CacheKey, List<Counterpoint>>()
    private var mediaPlayer: MediaPlayer? = null

    val midiPath: File = File(getApplication<MikroKanonApplication>().applicationContext.filesDir, "MKexecution.mid")
//    val midiPath: File = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//        File(getApplication<MikroKanonApplication>().applicationContext.filesDir, "MKexecution.mid")
//     else {
//        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "MKexecution.mid")
//    }

    // macro Functions called by fragments -----------------------------------------------------
    val onPlay = { createAndPlay: Boolean ->
            var error = "No File Created yet!!!"
            if(userOptionsData.value!!.isEmpty()){
                insertUserOptionData(UserOptionsData.getDefaultUserOptionData())
            }
            if(!selectedCounterpoint.value!!.isEmpty()) {
                if (mediaPlayer == null) mediaPlayer = MediaPlayer()
                val ensType: EnsembleType =
                    EnsembleType.values()[userOptionsData.value?.let { userOptionsData.value!![0].ensembleType }
                        ?: 0]
                val bpm: Float =
                    userOptionsData.value?.let { userOptionsData.value!![0].bpm.toFloat() } ?: 90f
                val rhythm: RhythmPatterns =
                    RhythmPatterns.values()[userOptionsData.value?.let { userOptionsData.value!![0].rhythm }
                        ?: 0]
                val rhythmShuffle: Boolean =
                    0 != (userOptionsData.value?.let { userOptionsData.value!![0].rhythmShuffle }
                        ?: 0)
                val partsShuffle: Boolean =
                    0 != (userOptionsData.value?.let { userOptionsData.value!![0].partsShuffle }
                        ?: 0)
                val rowFormsFlags: Int =
                    userOptionsData.value?.let { userOptionsData.value!![0].rowFormsFlags }
                        ?: 1 // ORIGINAL by default
                val doublingFlags: Int =
                    userOptionsData.value?.let { userOptionsData.value!![0].doublingFlags }
                        ?: 1 // ORIGINAL by default
                error = Player.playCounterpoint(
                    mediaPlayer!!,
                    false,
                    selectedCounterpoint.value!!,
                    bpm,
                    0f,
                    rhythm.values,
                    ensType,
                    createAndPlay,
                    midiPath,
                    rhythmShuffle,
                    partsShuffle,
                    rowFormsFlags,
                    doublingFlags
                )
            }
            error
    }
    val dispatchIntervals = {
            refreshComputation(false)
    }
    val onExpand = {
        val counterpointsClone = counterpoints.value!!.map{
            it.clone()
        }
        val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        computationStack.pushAndDispatch(Computation.Expand(counterpointsClone, index))
        expandCounterpoints(index)
        println("ON EXPAND")
    }
    val onKPfurtherSelections = {index: Int , repeat: Boolean->
        computationStack.pushAndDispatch(Computation.FurtherFromKP(selectedCounterpoint.value!!.clone(), index, repeat))
        changeSequenceToAdd(sequences.value!![index])
        if(!repeat) addSequenceToCounterpoint() else addRepeatedSequenceToCounterpoint()

    }
    val onKPfromFirstSelection = {list: ArrayList<Clip>, index: Int, repeat: Boolean ->
        //println(repeat)
        changeFirstSequence(list)
        computationStack.pushAndDispatch(Computation.FirstFromKP(selectedCounterpoint.value!!.clone(),
                                ArrayList(firstSequence.value!!), index, repeat))
        convertFirstSequenceToSelectedCounterpoint()
        changeSequenceToAdd(sequences.value!![index])
        if(!repeat) addSequenceToCounterpoint() else addRepeatedSequenceToCounterpoint()

    }
    val onFreePartFromFirstSelection = { list: ArrayList<Clip>, trend: TREND ->
        changeFirstSequence(list)
        computationStack.pushAndDispatch(Computation.FirstFromFreePart(selectedCounterpoint.value!!.clone(),ArrayList(firstSequence.value!!), trend))
        convertFirstSequenceToSelectedCounterpoint()
        findFreeParts(trend)
    }
    val onFreePartFurtherSelections = { trend: TREND ->
        computationStack.pushAndDispatch(Computation.FurtherFromFreePart(selectedCounterpoint.value!!.clone(),ArrayList(firstSequence.value!!), trend))
        findFreeParts(trend)
    }
    val onMikroKanons2 = {list: ArrayList<Clip> ->
        computationStack.pushAndDispatch(Computation.MikroKanonOnly(selectedCounterpoint.value!!.clone(),ArrayList(sequenceToMikroKanons.value!!),2))
        if(list.isNotEmpty()) changeSequenceToMikroKanons(list)
            findCounterpointsByMikroKanons2()

    }
    val onMikroKanons3 = {list: ArrayList<Clip> ->
       // if(!elaborating.value!!) {
            computationStack.pushAndDispatch(Computation.MikroKanonOnly(selectedCounterpoint.value!!.clone(),
                ArrayList(sequenceToMikroKanons.value!!),3))
            if(list.isNotEmpty()) changeSequenceToMikroKanons(list)
            findCounterpointsByMikroKanons3()
       // }

    }
    val onMikroKanons4 = {list: ArrayList<Clip> ->
        //if(!elaborating.value!!) {
            computationStack.pushAndDispatch(
                Computation.MikroKanonOnly(
                    selectedCounterpoint.value!!.clone(),
                    ArrayList(sequenceToMikroKanons.value!!), 4
                )
            )
            if (list.isNotEmpty()) changeSequenceToMikroKanons(list)
            findCounterpointsByMikroKanons4()
        //}
    }
    val onBack = {
        if(computationStack.size > 1) {
            println("ON BACK")
            refreshComputation(true)
        }
    }
    //-------------end macro functions--------------------

    fun shareMidi(file: File){
        try {
            if(file.exists()) {
                val uri = FileProvider.getUriForFile(getApplication<MikroKanonApplication>().applicationContext,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    file)
                val intent = Intent(Intent.ACTION_SEND)
                //println(Intent.FLAG_ACTIVITY_NEW_TASK)
                //var flags = Intent.FLAG_GRANT_READ_URI_PERMISSION //or Intent.FLAG_ACTIVITY_CLEAR_TASK
                //val flags = Intent.FLAG_ACTIVITY_NEW_TASK + 1
                //println("FLAGS: ${flags.toByte()}")
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setType("audio/midi")
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                //println("FLAGS: ${intent.flags}")
                getApplication<MikroKanonApplication>()
                    .applicationContext
                    .startActivity(intent)
                    //.startActivity(Intent.createChooser(intent,"Share MIDI to..."))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
//        val shareIntent: Intent = Intent().apply {
//            action = Intent.ACTION_SEND
//            putExtra(Intent.EXTRA_STREAM, file.toURI() as Parcelable)
//            type = "audio/midi"
//        }
//        getApplication<MikroKanonApplication>().applicationContext
//
    }

    private fun refreshComputation(stepBack: Boolean){
            if (!elaborating.value!!) {
                _elaborating.value = true
                if (stepBack ) computationStack.popAndDispatch()
                val previousComputation = when(computationStack.lastElement()){
                    is Computation.Expand -> computationStack.lastElement()
                    else -> computationStack.pop() // do not Dispatch!!!
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
                            2 -> onMikroKanons2(ArrayList(sequenceToMikroKanons.value!!))
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
                            _elaborating.value = false
                            var count = 0
                            var originalComputation: Computation

                            viewModelScope.launch(Dispatchers.Unconfined){
                                do {

                                    onBack()
                                    originalComputation = computationStack.lastElement()
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
                _elaborating.value = false
            }

    }
    private fun findFreeParts(trend: TREND){
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
    private fun findCounterpointsByMikroKanons4(){
        viewModelScope.launch(Dispatchers.Main){
            if(sequenceToMikroKanons.value!!.isNotEmpty()) {
                val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList()
                val key = CacheKey(sequence, intervalSet.value!!)
                if(mk4cache.containsKey(key)) {
                    changeCounterPoints(mk4cache[key]!!)
                    counterpoints.value?.get(0)?.let {changeSelectedCounterpoint(it)}
                }else {
                    val newList: List<Counterpoint>
                    _elaborating.value = true
                    withContext(Dispatchers.Default) {
                        newList = findCpByMikroKanons4()
                    }
                    mk4cache[key] = newList
                    _elaborating.value = false
                    changeCounterPoints(newList)
                    counterpoints.value?.get(0)?.let { changeSelectedCounterpoint(it) }
                }
            }
        }
    }

    private suspend fun findCpByMikroKanons4(): List<Counterpoint> {
            val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList()

                val counterpoints = MikroKanon.findAll4AbsPartMikroKanons(
                    sequence,  intervalSet.value!!, 2
                ).map { it.toCounterpoint() }.sortedBy { it.emptiness }.take(MAX_VISIBLE_COUNTERPOINTS)

                return counterpoints
    }
    private fun findCounterpointsByMikroKanons3(){
        viewModelScope.launch(Dispatchers.Main){
            if(sequenceToMikroKanons.value!!.isNotEmpty()) {
                val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList()
                val key = CacheKey(sequence, intervalSet.value!!)
                if(mk3cache.containsKey(key)) {
                    changeCounterPoints(mk3cache[key]!!)
                    counterpoints.value?.get(0)?.let {changeSelectedCounterpoint(it)}
                }else {
                    val newList: List<Counterpoint>
                    _elaborating.value = true
                    withContext(Dispatchers.Default) {
                        newList = findCpByMikroKanons3()
                    }
                    mk3cache[key] = newList
                    _elaborating.value = false
                    changeCounterPoints(newList)
                    counterpoints.value?.get(0)?.let { changeSelectedCounterpoint(it) }
                }
            }
        }
    }
    private suspend fun findCpByMikroKanons3(): List<Counterpoint>{
            val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList()
            val counterpoints = MikroKanon.findAll3AbsPartMikroKanons(
                sequence, intervalSet.value!!, 2
            ).map { it.toCounterpoint() }.sortedBy { it.emptiness }.take(MAX_VISIBLE_COUNTERPOINTS)
            return counterpoints
    }
    private fun findCounterpointsByMikroKanons2(){
        //_counterpoints.value = emptyList()
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = findCpByMikroKanons2()
            }
            changeCounterPoints(newList)
            counterpoints.value?.get(0)?.let {changeSelectedCounterpoint(it)}
        }
    }
    private suspend fun findCpByMikroKanons2(): List<Counterpoint>{
        return if(sequenceToMikroKanons.value!!.isNotEmpty()) {
            MikroKanon.findAll2AbsPartMikroKanons(
                sequenceToMikroKanons.value!!.map { it.abstractNote }.toList(),
                intervalSet.value!!, 5
            ).map { it.toCounterpoint() }.sortedBy { it.emptiness }.take(MAX_VISIBLE_COUNTERPOINTS)
        } else {
            println("Sequence to MikroKanons is empty.")
            emptyList()
        }
    }
    private fun expandCounterpoints(index: Int){
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
    private fun addRepeatedSequenceToCounterpoint(){
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
    private fun addSequenceToCounterpoint(){
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

    private fun convertFirstSequenceToSelectedCounterpoint() {
        val newCounterpoint = Counterpoint.counterpointFromClipList(firstSequence.value!!)
        _selectedCounterpoint.value = newCounterpoint
    }

    fun setInitialBlankState() {
        computationStack.clearAndDispatch()
        changeCounterPoints(listOf())
        changeSequenceToMikroKanons(listOf())
        changeFirstSequence(listOf())
        changeSequenceToAdd(listOf())
        changeSelectedCounterpoint(Counterpoint.empty())
        changeSequenceSelection(-1)
    }

    fun changeActiveButtons(newActiveButtons: ActiveButtons){
        _activeButtons.value = newActiveButtons
    }
    fun changeSelectedCounterpoint(newCounterpoint: Counterpoint){
        _selectedCounterpoint.value = newCounterpoint

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
            sequenceRepository.insert(SequenceData(0,sequence.map { clipToDataClip(it) }))
        }
    }
    fun deleteSequence(index: Int){
        val sequence = sequences.value!![index]
        val sequenceData = sequenceDataMap[sequence]
        viewModelScope.launch(Dispatchers.IO) {
            if (sequenceData != null) {
                sequenceRepository.delete(sequenceData)
            }
        }
        changeSequenceSelection(-1)
    }
    fun updateSequence(index: Int, sequence: ArrayList<Clip>){
        val oldSequence = sequences.value!![index]
        val sequenceData = sequenceDataMap[oldSequence]
        viewModelScope.launch(Dispatchers.IO) {
            if (sequenceData != null) {
                sequenceRepository.delete(sequenceData)
                sequenceRepository.insert(SequenceData(0,sequence.map { clipToDataClip(it) }))
            }
        }
    }

    fun retrieveSequencesFromDB(){
        sequenceDataMap.clear()
        _sequences.value = allSequencesData.value!!.map{sequenceDataToSequence(it)}
    }

    private fun sequenceDataToSequence(sequenceData: SequenceData) : ArrayList<Clip>{
        val sequence = ArrayList(sequenceData.clips.map { clipDataToClip(it)})
        sequenceDataMap[sequence] = sequenceData
        return sequence
    }

    fun insertUserOptionData(newUserOptionsData: UserOptionsData){
        viewModelScope.launch(Dispatchers.IO) {
            if(userOptionsData.value!!.isNotEmpty()){
                userRepository.deleteAllUserOptions()
            }
            userRepository.insertUserOptions(newUserOptionsData)
        }
    }

    fun updateUserOptions(key: String, value: Any){
        var newUserOptionsData: UserOptionsData? = null
        val optionsDataClone = if(userOptionsData.value!!.isEmpty())
                                UserOptionsData.getDefaultUserOptionData()
                                else userOptionsData.value!![0].copy()
        when(key){
            "ensemble_type" -> {
                newUserOptionsData = optionsDataClone.copy(ensembleType = value as Int)
            }
            "bpm" -> {
                newUserOptionsData = optionsDataClone.copy(bpm = value as Int)
            }
            "rhythm" -> {
                newUserOptionsData = optionsDataClone.copy(rhythm = value as Int)
            }
            "rhythmShuffle" -> {
                newUserOptionsData = optionsDataClone.copy(rhythmShuffle = value as Int)
            }
            "partsShuffle" -> {
                newUserOptionsData = optionsDataClone.copy(partsShuffle = value as Int)
            }
            "rowFormsFlags" -> {
                newUserOptionsData  = optionsDataClone.copy(rowFormsFlags = value as Int)
            }
            "doublingFlags" -> {
                newUserOptionsData  = optionsDataClone.copy(doublingFlags = value as Int)
            }
            "language" -> {
                newUserOptionsData  = optionsDataClone.copy(language = value as String)
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
    fun selectNotesNames(): List<String> {
        val systemLangDef = Locale.getDefault().language
        val userLangDef = if(userOptionsData.value != null && userOptionsData.value!!.isNotEmpty()) {
            if(userOptionsData.value!![0].language == "System") systemLangDef else userOptionsData.value!![0].language
        } else systemLangDef

        val newNotesNames =
             when(userLangDef) {
                 "en" -> NoteNamesEn.values().map { it.toString() }
                 "it" -> NoteNamesIt.values().map { it.toString() }
                 "fr" -> NoteNamesFr.values().map { it.toString() }
                 else -> NoteNamesEn.values().map { it.toString() }
             }
        //println("new notes names: ${newNotesNames.toString()}")
        _notesNames.value = newNotesNames
        return newNotesNames
    }

    fun getSystemLanguage(): String {
        return when (Locale.getDefault().language){
            "en" -> "English"
            "fr" -> "Français"
            "it" -> "Italiano"
            else -> "English"
        }
    }
    fun getSystemLangDef(): String {
        return Locale.getDefault().language
    }
}
fun ArrayList<Clip>.toStringAll(notesNames: List<String>): String {
    return if (this.isNotEmpty()) {
        this.map { clip -> clip.findText(notesNames = notesNames) }.reduce { acc, string -> "$acc $string" }
    } else {
        "empty Sequence"
    }
}
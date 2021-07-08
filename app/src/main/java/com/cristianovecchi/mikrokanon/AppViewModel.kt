package com.cristianovecchi.mikrokanon

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Point
import androidx.lifecycle.*
import com.cristianovecchi.mikrokanon.composables.*
import com.cristianovecchi.mikrokanon.db.SequenceData
import com.cristianovecchi.mikrokanon.db.SequenceDataRepository
import com.cristianovecchi.mikrokanon.midi.Player
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import android.media.MediaPlayer
import android.os.Build
import com.cristianovecchi.mikrokanon.AIMUSIC.*
import androidx.core.content.FileProvider
import com.cristianovecchi.mikrokanon.db.UserOptionsData
import com.cristianovecchi.mikrokanon.db.UserOptionsDataRepository
import java.io.File
import androidx.lifecycle.Lifecycle

import androidx.lifecycle.OnLifecycleEvent
import android.view.WindowManager
import com.cristianovecchi.mikrokanon.ui.Dimensions
import kotlinx.coroutines.*


sealed class Computation {
    data class MikroKanonOnly(val counterpoint: Counterpoint,val sequenceToMikroKanon: ArrayList<Clip>, val nParts: Int): Computation()
    data class FirstFromKP(val counterpoint: Counterpoint, val firstSequence: ArrayList<Clip>, val indexSequenceToAdd: Int, val repeat: Boolean): Computation()
    data class FirstFromWave(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>, val nWaves: Int): Computation()
    data class FurtherFromKP(val counterpoint: Counterpoint,val indexSequenceToAdd: Int, val repeat: Boolean): Computation()
    data class FurtherFromWave(val counterpoints: List<Counterpoint>, val nWaves: Int): Computation()
    data class FirstFromFreePart(val counterpoint: Counterpoint,val firstSequence: ArrayList<Clip>, val trend: TREND): Computation()
    data class FurtherFromFreePart(val counterpoint: Counterpoint,val firstSequence: ArrayList<Clip>, val trend: TREND): Computation()
    data class Expand(val counterpoints: List<Counterpoint>, val index: Int, val extension: Int = 2 ) : Computation()
}

data class ActiveButtons(val editing: Boolean = false, val mikrokanon: Boolean = false,
                         val undo: Boolean = false, val expand: Boolean = true,
                         val counterpoint: Boolean = false, val freeparts: Boolean = false, val playOrStop: Boolean = true)

class AppViewModel(
    application: Application,
    private val sequenceRepository: SequenceDataRepository,
    private val userRepository: UserOptionsDataRepository,
) : AndroidViewModel(application), LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onLifeCycleStop() {
        onStop()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onLifeCyclePause() {
        onStop()
    }

    var dimensions: Dimensions
    val iconMap = mapOf(

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
        "stop" to R.drawable.ic_baseline_stop_24,
        "expand" to R.drawable.ic_baseline_sync_alt_24,
        "waves" to R.drawable.ic_baseline_waves_24,
        "special_functions" to R.drawable.ic_baseline_emoji_objects_24,
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
    private var mediaPlayer: MediaPlayer? = null
    private var lastIndex = 0
    var MKjob: Job? = null

    private val MAX_VISIBLE_COUNTERPOINTS: Int = 36
    private var ensembleTypeSelected: EnsembleType = EnsembleType.STRING_ORCHESTRA
    private val sequenceDataMap = HashMap<ArrayList<Clip>, SequenceData>(emptyMap())

    private val _activeButtons = MutableLiveData<ActiveButtons>(ActiveButtons())
    val activeButtons : LiveData<ActiveButtons> = _activeButtons

    private val _sequences = MutableLiveData<List<ArrayList<Clip>>>(listOf())
    val sequences : LiveData<List<ArrayList<Clip>>> = _sequences

//    private var _deepSearch = MutableLiveData<Boolean>(false)
//    var deepSearch: LiveData<Boolean> = _deepSearch

    private var _elaborating = MutableLiveData<Boolean>(false)
    var elaborating: LiveData<Boolean> = _elaborating

    private var _playing = MutableLiveData<Boolean>(false)
    var playing: LiveData<Boolean> = _playing

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
    private val mk4deepSearchCache = HashMap<CacheKey, List<Counterpoint>>()


    val midiPath: File = File(getApplication<MikroKanonApplication>().applicationContext.filesDir, "MKexecution.mid")
//    val midiPath: File = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//        File(getApplication<MikroKanonApplication>().applicationContext.filesDir, "MKexecution.mid")
//     else {
//        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "MKexecution.mid")
//    }
init{
    val size = getDeviceResolution()
    dimensions = Dimensions.provideDimensions(size.x, size.y)

}
    private fun getDeviceResolution(): Point {
        val windowManager: WindowManager = getApplication<MikroKanonApplication>()
            .applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            val windowMetrics = windowManager.currentWindowMetrics
            val w = windowMetrics.bounds.width()
            val h = windowMetrics.bounds.height()
            println("BOUNDS: WIDTH = $w HEIGHT = $h")
            Point(w,h)
        } else {
            val size = Point()
            windowManager.defaultDisplay.getRealSize(size)
            val w = size.x
            val h = size.y
            println("SIZE X = $w   SIZE Y = $h")
            size
        }
    }

    // macro Functions called by fragments -----------------------------------------------------
    val onStop = {
        mediaPlayer?.let{ if (it.isPlaying) it.stop() }
        mediaPlayer?.let{ if (!it.isPlaying) _playing.value = false}
        mediaPlayer?.release()
        mediaPlayer = null
    }
    val onPlaySequence = { clips: List<Clip> ->
        _selectedCounterpoint.value = Counterpoint.counterpointFromClipList(clips)
        onPlay(true)
    }
    val onPlay = { createAndPlay: Boolean ->
            var error = "No File Created yet!!!"
            if(userOptionsData.value!!.isEmpty()){
                insertUserOptionData(UserOptionsData.getDefaultUserOptionData())
            }
            if(!selectedCounterpoint.value!!.isEmpty()) {
                //mediaPlayer?.let{onStop()}
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer()
                    mediaPlayer?.setOnCompletionListener { onStop() }
                }
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
        mediaPlayer?.let{ if (it.isPlaying) _playing.value = true}
            error
    }
    val dispatchIntervals = {
            refreshComputation(false)
    }
    val onExpand = {
        val lastComputation = computationStack.peek()
        val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        val originalCounterpoints = if(lastComputation is Computation.Expand) lastComputation.counterpoints
                                    else counterpoints.value!!.map{ it.clone() }
        val expansion = if(lastComputation is Computation.Expand) lastComputation.extension + 1 else 2
        computationStack.pushAndDispatch(Computation.Expand(originalCounterpoints, index, expansion))
        expandCounterpoints(originalCounterpoints, index, expansion)
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
    val onWaveFromFirstSelection = { nWaves: Int, list: ArrayList<Clip> ->
        changeFirstSequence(list)
        computationStack.pushAndDispatch(Computation.FirstFromWave(listOf(selectedCounterpoint.value!!.clone()),
            ArrayList(firstSequence.value!!), nWaves))
        convertFirstSequenceToSelectedCounterpoint()
        findWavesFromSequence(nWaves)
    }
    val onWaveFurtherSelection = { nWaves: Int , stepBackCounterpoints: List<Counterpoint>? ->
        val lastComputation = computationStack.peek()
        val originalCounterpoints = stepBackCounterpoints ?: counterpoints.value!!.map{ it.clone() }

        computationStack.pushAndDispatch(Computation.FurtherFromWave(originalCounterpoints, nWaves))
        findWavesOnCounterpoints(originalCounterpoints, nWaves)
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
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setType("audio/midi")
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                val chooserIntent = Intent.createChooser(intent,"Share MIDI to...")
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                //println("FLAGS: ${intent.flags}")
                try{
                    getApplication<MikroKanonApplication>()
                        .applicationContext
                        //.startActivity(intent)
                        .startActivity(chooserIntent)
                } catch (ex: Exception){
                    println("Exception in Share Midi: ${ex.message}")
                    getApplication<MikroKanonApplication>()
                        .applicationContext
                        .startActivity(intent)
                }
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
                    is Computation.FirstFromWave -> onWaveFromFirstSelection(
                        previousComputation.nWaves,
                        previousComputation.firstSequence
                    )
                    is Computation.FurtherFromWave -> onWaveFurtherSelection(
                        previousComputation.nWaves, previousComputation.counterpoints
                    )
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
                            expandCounterpoints(previousComputation.counterpoints,
                                                previousComputation.index, previousComputation.extension)
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
    fun lastComputationIsExpansion(): Boolean{
        if (computationStack.isEmpty()) return true
        return when (computationStack.lastElement()) {
            is Computation.Expand -> true
            else -> false
        }
    }
    private fun findWavesFromSequence(nWaves: Int){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = findWvs(listOf(Counterpoint.counterpointFromClipList(firstSequence.value!!)), nWaves)
            }
            changeCounterPoints(newList)
            counterpoints.value?.let{
                if(it.isNotEmpty()) changeSelectedCounterpoint(it[0])
            }
        }
    }
    fun findWavesOnCounterpoints(originalCounterpoints: List<Counterpoint>, nWaves: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = findWvs(originalCounterpoints, nWaves)
                }
                changeCounterPoints(newList)
                counterpoints.value?.let{
                    if(it.isNotEmpty()) changeSelectedCounterpoint(it[0])
                }
                // println("STACK SIZE: ${counterpointStack.size}")
            }
        }
    }
    private suspend fun findWvs(counterpoints: List<Counterpoint>, nWaves: Int) : List<Counterpoint>{
        val spreadWherePossible = userOptionsData.value!![0].spread != 0
        var newList = Counterpoint.findAllWithWaves(
            counterpoints,  intervalSet.value!!, nWaves
        ).sortedBy { it.emptiness }.take(MAX_VISIBLE_COUNTERPOINTS)
        if(spreadWherePossible){
            newList = newList.map{it.spreadAsPossible()}.sortedBy { it.emptiness }
        }
        return newList
    }
    private fun findFreeParts(trend: TREND){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = findFreePts(trend)
            }
            changeCounterPoints(newList)
            counterpoints.value?.let{
                if(it.isNotEmpty()) changeSelectedCounterpoint(it[0])
            }
        }
    }
    private suspend fun findFreePts(trend: TREND) : List<Counterpoint>{
        val spreadWherePossible = userOptionsData.value!![0].spread != 0
        var newList = Counterpoint.findAllFreeParts(
            selectedCounterpoint.value!!,  intervalSet.value!!, trend.directions
        )
            .sortedBy { it.emptiness }.take(MAX_VISIBLE_COUNTERPOINTS)

        if(spreadWherePossible){
            newList = newList.map{it.spreadAsPossible()}.sortedBy { it.emptiness }
        }
        return newList
    }
    private fun findCounterpointsByMikroKanons4(){
        MKjob = viewModelScope.launch(Dispatchers.Main){
            val deepSearch = userOptionsData.value!![0].deepSearch != 0
            if(sequenceToMikroKanons.value!!.isNotEmpty()) {
                val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList()
                val key = CacheKey(sequence, intervalSet.value!!)
                if(mk4cache.containsKey(key) && !deepSearch) {
                    changeCounterPoints(mk4cache[key]!!)
                    counterpoints.value?.get(0)?.let {changeSelectedCounterpoint(it)}
                }else if(mk4deepSearchCache.containsKey(key) && deepSearch) {
                    changeCounterPoints(mk4deepSearchCache[key]!!)
                    counterpoints.value?.get(0)?.let {changeSelectedCounterpoint(it)}
                }else {
                    val newList: List<Counterpoint>
                    _elaborating.value = true
                    withContext(Dispatchers.Default) {
                        newList = findCpByMikroKanons4()
                    }
                    if(deepSearch){
                        mk4deepSearchCache[key] = newList
                    } else {
                        mk4cache[key] = newList
                    }
                    _elaborating.value = false
                    changeCounterPoints(newList)
                    counterpoints.value?.let{
                        if(it.isNotEmpty()) changeSelectedCounterpoint(it[0])
                    }
                }
            }
        }
    }


    private suspend fun findCpByMikroKanons4(): List<Counterpoint> {
        val spreadWherePossible = userOptionsData.value!![0].spread != 0
            val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList()
            val deepSearch = userOptionsData.value!![0].deepSearch != 0
            val emptinessGate = if(!deepSearch) 1.0f else when (intervalSet.value!!.size) {
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
            var counterpoints = MikroKanon.findAll4AbsPartMikroKanonsParallel(
                sequence,  intervalSet.value!!, depth, emptinessGate
            ).pmap { it.toCounterpoint() }.sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }.take(MAX_VISIBLE_COUNTERPOINTS * 2)
            if(spreadWherePossible){
                counterpoints = counterpoints.pmap{it.spreadAsPossible()}.sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
            }
            return counterpoints
    }
    private fun findCounterpointsByMikroKanons3(){
        MKjob = viewModelScope.launch(Dispatchers.Main){
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
                    counterpoints.value?.let{
                        if(it.isNotEmpty()) changeSelectedCounterpoint(it[0])
                    }
                }
            }
        }
    }
    private suspend fun findCpByMikroKanons3(): List<Counterpoint>{
        val spreadWherePossible = userOptionsData.value!![0].spread != 0
        val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList()
        var counterpoints = MikroKanon.findAll3AbsPartMikroKanonsParallel(
            sequence, intervalSet.value!!, 6
            ).pmap { it.toCounterpoint() }.sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }.take(MAX_VISIBLE_COUNTERPOINTS * 2)
        if(spreadWherePossible){
            counterpoints = counterpoints.pmap{it.spreadAsPossible()}.sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
        }
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
        val spreadWherePossible = userOptionsData.value!![0].spread != 0
        var counterpoints = if(sequenceToMikroKanons.value!!.isNotEmpty()) {
            MikroKanon.findAll2AbsPartMikroKanons(
                sequenceToMikroKanons.value!!.map { it.abstractNote }.toList(),
                intervalSet.value!!, 5
            ).map { it.toCounterpoint() }.sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }.take(MAX_VISIBLE_COUNTERPOINTS * 2)
        } else {
            println("Sequence to MikroKanons is empty.")
            emptyList()
        }
        if(spreadWherePossible){
            counterpoints = counterpoints.map{it.spreadAsPossible()}.sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
        }
        return counterpoints
    }
    private fun expandCounterpoints(originalCounterpoints: List<Counterpoint>, index: Int, extension: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = expandCps(originalCounterpoints, extension)
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
    private suspend fun expandCps(originalCounterpoints: List<Counterpoint>, extension: Int): List<Counterpoint>{
        return originalCounterpoints.map{
            Counterpoint.expand(it,extension)
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
        val spreadWherePossible = userOptionsData.value!![0].spread != 0
        var newList = Counterpoint.findAllCounterpointsWithRepeatedSequence(
            selectedCounterpoint.value!! , sequenceToAdd.value!!.map { it.abstractNote }.toList(),
            intervalSet.value!!, 5
        ).sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }.take(MAX_VISIBLE_COUNTERPOINTS)
        if(spreadWherePossible){
            newList = newList.map{it.spreadAsPossible()}.sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
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
        val spreadWherePossible = userOptionsData.value!![0].spread != 0
        var newList = Counterpoint.findAllCounterpoints(
            selectedCounterpoint.value!! , sequenceToAdd.value!!.map { it.abstractNote }.toList(),
            intervalSet.value!!, 5
        ).sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }.take(MAX_VISIBLE_COUNTERPOINTS)
        if(spreadWherePossible){
            newList = newList.map{it.spreadAsPossible()}.sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
        }
        return newList
    }

    private fun convertFirstSequenceToSelectedCounterpoint() {
        val newCounterpoint = Counterpoint.counterpointFromClipList(firstSequence.value!!)
        _selectedCounterpoint.value = newCounterpoint
    }
    fun setInitialBlankState() {
        viewModelScope.launch {
            MKjob?.cancelAndJoin()
        }
        _elaborating.value = false
        onStop()
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

    fun clearMKcaches() {
        mk3cache.clear()
        mk4cache.clear()
    }

    // ROOM ---------------------------------------------------------------------
    fun addSequence(sequence: ArrayList<Clip>){
        viewModelScope.launch(Dispatchers.IO) {
            sequenceRepository.insert(SequenceData(0,sequence.map { Clip.clipToDataClip(it) }))
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
                sequenceRepository.insert(SequenceData(0,sequence.map { Clip.clipToDataClip(it) }))
            }
        }
    }

    fun retrieveSequencesFromDB(){
        sequenceDataMap.clear()
        _sequences.value = allSequencesData.value!!.map{sequenceDataToSequence(it)}
    }

    private fun sequenceDataToSequence(sequenceData: SequenceData) : ArrayList<Clip>{
        val sequence = ArrayList(sequenceData.clips.map { Clip.clipDataToClip(it)})
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
            "spread" -> {
                newUserOptionsData  = optionsDataClone.copy(spread = value as Int)
                clearMKcaches()
            }
            "deepSearch" -> {
                newUserOptionsData  = optionsDataClone.copy(deepSearch = value as Int)
                clearMKcaches()
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
    fun getUserLangDef(): String {
        val systemLangDef = getSystemLangDef()
        return if(userOptionsData.value != null && userOptionsData.value!!.isNotEmpty()) {
            if(userOptionsData.value!![0].language == "System") systemLangDef else userOptionsData.value!![0].language
        } else systemLangDef
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

//TODO: implement in CounterpointInterpreter
suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
        map { async() { f(it) } }.awaitAll()
}



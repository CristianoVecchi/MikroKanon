package com.cristianovecchi.mikrokanon

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Point
import androidx.lifecycle.*
import com.cristianovecchi.mikrokanon.composables.*
import com.cristianovecchi.mikrokanon.midi.Player
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import android.media.MediaPlayer
import android.os.Build
import com.cristianovecchi.mikrokanon.AIMUSIC.*
import androidx.core.content.FileProvider
import java.io.File
import androidx.lifecycle.Lifecycle

import androidx.lifecycle.OnLifecycleEvent
import android.view.WindowManager
import com.cristianovecchi.mikrokanon.db.*
import com.cristianovecchi.mikrokanon.locale.getDynamicSymbols
import com.cristianovecchi.mikrokanon.midi.launchPlayer
import com.cristianovecchi.mikrokanon.ui.AppColorThemes
import com.cristianovecchi.mikrokanon.ui.AppColors
import com.cristianovecchi.mikrokanon.ui.Dimensions
import com.cristianovecchi.mikrokanon.ui.extractColorDefs
import kotlinx.coroutines.*
import kotlin.math.absoluteValue
import kotlin.system.measureTimeMillis


sealed class Computation(open val icon: String = "") {
    data class MikroKanonOnly(val counterpoint: Counterpoint, val sequenceToMikroKanon: ArrayList<Clip>, val nParts: Int, override val icon: String = "mikrokanon"): Computation()
    data class FirstFromKP(val counterpoint: Counterpoint, val firstSequence: ArrayList<Clip>, val indexSequenceToAdd: Int, val repeat: Boolean, override val icon: String = "counterpoint"): Computation()
    data class FirstFromWave(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>, val nWaves: Int, override val icon: String = "waves"): Computation()
    data class FirstFromLoading(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>, val position: Int, override val icon: String = "save"): Computation()
    data class Pedal(val counterpoint: Counterpoint, val firstSequence: ArrayList<Clip>?, val nPedals: Int, override val icon: String = "pedal"): Computation()
    data class FurtherFromKP(val counterpoint: Counterpoint, val indexSequenceToAdd: Int, val repeat: Boolean, override val icon: String = "counterpoint"): Computation()
    data class FurtherFromWave(val counterpoints: List<Counterpoint>, val nWaves: Int, override val icon: String = "waves"): Computation()
    data class FirstFromFreePart(val counterpoint: Counterpoint, val firstSequence: ArrayList<Clip>, val trend: TREND, override val icon: String = "free_parts"): Computation()
    data class FurtherFromFreePart(val counterpoint: Counterpoint, val firstSequence: ArrayList<Clip>, val trend: TREND, override val icon: String = "free_parts"): Computation()
    data class Fioritura(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val index: Int, override val icon: String = "fioritura"): Computation()
    data class Round(val counterpoints: List<Counterpoint>, val index: Int, override val icon: String = "round"): Computation()
    data class Cadenza(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val index: Int, val values: List<Int>, override val icon: String = "cadenza"): Computation()
    data class Sort(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val index: Int, val sortType: Int, override val icon: String = "sort_up"): Computation()
    data class UpsideDown(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val index: Int, override val icon: String = "upside_down"): Computation()
    data class Scarlatti(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val index: Int, override val icon: String = "Scarlatti"): Computation()
    data class Overlap(val counterpoint1st: Counterpoint, val counterpoint2nd: Counterpoint, val firstSequence: ArrayList<Clip>?, override val icon: String = "overlap"): Computation()
    data class Crossover(val counterpoint1st: Counterpoint, val counterpoint2nd: Counterpoint, val firstSequence: ArrayList<Clip>?, override val icon: String = "crossover"): Computation()
    data class Glue(val counterpoint1st: Counterpoint, val counterpoint2nd: Counterpoint, val firstSequence: ArrayList<Clip>?, override val icon: String = "glue"): Computation()
    data class Maze(val intSequences: List<List<Int>>, override val icon: String = "maze"): Computation()
    data class EraseIntervals(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val index: Int, override val icon: String = "erase"): Computation()
    data class Single(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val index: Int, override val icon: String = "single"): Computation()
    data class Doppelgänger(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val index: Int, override val icon: String = "doppelgänger"): Computation()
    data class Expand(val counterpoints: List<Counterpoint>, val index: Int, val extension: Int = 2, override val icon: String = "expand") : Computation()
    data class Transposition(val counterpoints: List<Counterpoint>, val transpositions: List<Pair<Int,Int>>, val index: Int, override val icon: String = "transpose") : Computation()
    data class TritoneSubstitution(val counterpoints: List<Counterpoint>, val intervalSet: List<Int>, val index: Int, override val icon: String = "tritone_substitution") : Computation()
}

data class ActiveButtons(val editing: Boolean = false, val mikrokanon: Boolean = false,
                         val undo: Boolean = false, val expand: Boolean = true,
                         val waves: Boolean = false, val pedals: Boolean = true,
                         val counterpoint: Boolean = false, val specialFunctions: Boolean = false,
                         val freeparts: Boolean = false, val playOrStop: Boolean = true)
enum class ScaffoldTabs {
    SOUND, BUILDING, SETTINGS
}

class AppViewModel(
    application: Application,
    private val sequenceRepository: SequenceDataRepository,
    private val counterpointRepository: CounterpointDataRepository,
    private val userRepository: UserOptionsDataRepository,
) : AndroidViewModel(application), LifecycleObserver {

    companion object{
        const val MAX_PITCHES_IN_CACHE = 60000
        const val MAX_PARTS = 12
        const val MAX_NOTES_MK_2= 200
        const val MAX_NOTES_MK_3 = 74
        const val MAX_NOTES_MK_4 = 32
        const val MAX_NOTES_MK_4DEEP = 18
        const val MAX_NOTES_MK_5RED = 25
        const val MAX_SEQUENCES_IN_MAZE = 10
        val MAX_NOTES_IN_MAZE = listOf(0, 99,99,99,99,99,99, 24,24, 16,14)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onLifeCycleStop() {
        onStop()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onLifeCyclePause() {
        onStop()
    }

    var dimensions: Dimensions
    var lastScaffoldTab = ScaffoldTabs.SETTINGS
    // + 0.86f
    val dynamicSteps = listOf(0.000001f, 0.14f, 0.226f, 0.312f,  0.398f, 0.484f, 0.57f, 0.656f,  0.742f, 0.828f, 0.914f,1f )
    var cadenzaValues ="0,1,0,1,1"
    val dynamicMap: Map<Float,String> =  dynamicSteps.zip(getDynamicSymbols()).toMap()
    val iconMap = mapOf(

        "mikrokanon" to R.drawable.ic_baseline_clear_all_24,
        "counterpoint" to R.drawable.ic_baseline_drag_handle_24,
        "special_functions" to R.drawable.ic_baseline_apps_24,
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
        "horizontal_movements" to R.drawable.ic_baseline_insights_24,
        "idea" to R.drawable.ic_baseline_emoji_objects_24,
        "tritone_substitution" to R.drawable.ic_baseline_360_24,
        "fioritura" to R.drawable.ic_baseline_wb_sunny_24,
        "round" to R.drawable.ic_baseline_directions_boat_24,
        "pedal" to R.drawable.ic_baseline_anchor_24,
        "cadenza" to R.drawable.ic_baseline_autofps_select_24,
        "single" to R.drawable.ic_baseline_single_24,
        "sound" to R.drawable.ic_baseline_music_note_24,
        "building" to R.drawable.ic_baseline_account_balance_24,
        "settings" to R.drawable.ic_baseline_settings_24,
        "doppelgänger" to R.drawable.ic_baseline_shuffle_24,
        "save" to R.drawable.ic_baseline_save_24,
        "erase" to R.drawable.ic_baseline_cleaning_services_24,
        "free_parts" to R.drawable.ic_baseline_queue_music_24,
        "Scarlatti" to R.drawable.ic_baseline_exposure_plus_1_24,
        "minus_one" to R.drawable.ic_baseline_exposure_neg_1_24,
        "transpose" to R.drawable.ic_baseline_unfold_more_24,
        "sort_up" to R.drawable.ic_baseline_trending_up_24,
        "sort_down" to R.drawable.ic_baseline_trending_down_24,
        "bar" to R.drawable.ic_baseline_bar_24,
        "upside_down" to R.drawable.ic_baseline_expand_24,
        "overlap" to R.drawable.ic_baseline_compress_24,
        "crossover" to R.drawable.ic_baseline_crossover_24,
        "glue" to R.drawable.ic_baseline_view_week_24,
        "maze" to R.drawable.ic_baseline_account_tree_24
    )
    val stackIcons = mutableListOf<String>()
    private fun Stack<Computation>.pushAndDispatch(computation: Computation){
        push(computation)
        stackIcons.add(computation.icon)
        this@AppViewModel._stackSize.value = this.size
    }
    private fun Stack<Computation>.popAndDispatch(){
        pop()
        if (stackIcons.size > 1) stackIcons.removeLast()
        this@AppViewModel._stackSize.value = this.size
    }
    private fun Stack<Computation>.clearAndDispatch(){
        clear()
        stackIcons.clear()
        this@AppViewModel._stackSize.value = this.size
    }
    private var mediaPlayer: MediaPlayer? = null
    private var lastIndex = 0

    private val  MAX_VISIBLE_COUNTERPOINTS: Int = 42
    private val sequenceDataMap = HashMap<ArrayList<Clip>, SequenceData>(emptyMap())

    private val _activeButtons = MutableLiveData(ActiveButtons())
    val activeButtons : LiveData<ActiveButtons> = _activeButtons

    private val _sequences = MutableLiveData<List<ArrayList<Clip>>>(listOf())
    val sequences : LiveData<List<ArrayList<Clip>>> = _sequences

    private var _elaborating = MutableLiveData(false)
    var elaborating: LiveData<Boolean> = _elaborating

    private var _playing = MutableLiveData(false)
    var playing: LiveData<Boolean> = _playing

    private var _firstSequence= MutableLiveData<List<Clip>>(listOf())
    val firstSequence : LiveData<List<Clip>> = _firstSequence

    private var _sequenceToAdd = MutableLiveData<List<Clip>>(listOf())
    val sequenceToAdd : LiveData<List<Clip>> = _sequenceToAdd

    private val _selectedSequence = MutableLiveData(-1)
    val selectedSequence : LiveData<Int> = _selectedSequence

    private val _stackSize = MutableLiveData(1)
    val stackSize : LiveData<Int> = _stackSize

    private var _sequenceToMikroKanons = MutableLiveData<List<Clip>>(listOf())
    val sequenceToMikroKanons : LiveData<List<Clip>> = _sequenceToMikroKanons

    private val _counterpoints = MutableLiveData<List<Counterpoint>>(listOf())
    val counterpoints : LiveData<List<Counterpoint>> = _counterpoints

    private val _intervalSet = MutableLiveData(listOf(2, 10, 3, 9, 4, 8, 5, 7))
    val intervalSet : LiveData<List<Int>> = _intervalSet
    private val _intervalSetHorizontal = MutableLiveData((0..11).toList())
    val intervalSetHorizontal : LiveData<List<Int>> = _intervalSetHorizontal

    private var _selectedCounterpoint = MutableLiveData(Counterpoint.empty())
    val selectedCounterpoint : LiveData<Counterpoint> = _selectedCounterpoint
    val allSequencesData: LiveData<List<SequenceData>> = sequenceRepository.allSequences.asLiveData()
    val allCounterpointsData: LiveData<List<CounterpointData>> = counterpointRepository.counterpoints.asLiveData()
    val userOptionsData: LiveData<List<UserOptionsData>> = userRepository.userOptions.asLiveData()

    private val computationStack = Stack<Computation>()

    data class CacheKey(val sequence: List<Int>, val intervalSet: List<Int>)
    private val mk3cache = HashMap<CacheKey, Pair<List<Counterpoint>,Long >>()
    private val mk4cache = HashMap<CacheKey, Pair<List<Counterpoint>,Long >>()
    private val mk4deepSearchCache = HashMap<CacheKey, Pair<List<Counterpoint>,Long >>()
    private val mk5reductedCache = HashMap<CacheKey, Pair<List<Counterpoint>,Long >>()
    private val mazeCache = HashMap<CacheKey, Pair<List<Counterpoint>,Long >>()
    private fun HashMap<CacheKey, Pair<List<Counterpoint>,Long>>.insertAndClear(key: CacheKey, counterpoints: List<Counterpoint>, timestamp: Long){

            val intAmount = this.values.map{it.first}.fold(0){ acc1, list -> acc1 + list.fold(0){acc2, counterpoint -> acc2 + counterpoint.countAbsPitches()} }
            val listAmount = counterpoints.fold(0){acc, counterpoint -> acc + counterpoint.countAbsPitches()}
        println("key: ${key.sequence} ${key.intervalSet}")
            println("Cache size: $intAmount, new list size: $listAmount, limit: $MAX_PITCHES_IN_CACHE")
            if(this.isNotEmpty()){
            if(intAmount + listAmount > MAX_PITCHES_IN_CACHE) {
                remove(keys.sortedBy { this[it]?.second }[0])
            }
        }
        this[key] = Pair(counterpoints, timestamp)
        println()
    }

    val savedCounterpoints: Array<Counterpoint?> = Array(16) { null }
    val midiPath: File = File(getApplication<MikroKanonApplication>().applicationContext.filesDir, "MK_lastPlay.mid")
//    val midiPath: File = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//        File(getApplication<MikroKanonApplication>().applicationContext.filesDir, "MKexecution.mid")
//     else {
//        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "MKexecution.mid")
//    }
init{

    val size = getDeviceResolution()
    val displayMetricsDensity = Resources.getSystem().displayMetrics.density
    dimensions = Dimensions.provideDimensions(size.x, size.y, displayMetricsDensity)
}
    fun getContext(): Context {
        return getApplication<MikroKanonApplication>()
            .applicationContext
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
        onPlay(true, true)
    }
    val onPlayExample = { counterpointSaved: Int, rowForm: Int ->
        //println("counterpointSaved: $counterpointSaved")
        // 1 or -1 is the original counterpoint; 2..7 or -2..-7 are the saved counterpoints
        val selCpSaved = _selectedCounterpoint.value?.clone()
        val counterpointExample: Counterpoint? = if(counterpointSaved.absoluteValue == 1) _selectedCounterpoint.value
        else savedCounterpoints[counterpointSaved.absoluteValue - 2]
       if(counterpointExample != null){
           val counterpointToPlay = if(counterpointSaved > 0) {
               Counterpoint.explodeRowForms(counterpointExample, listOf(rowForm.absoluteValue))
           } else {
               Counterpoint.explodeRowForms(counterpointExample.tritoneSubstitution(), listOf(rowForm.absoluteValue))
           }
           _selectedCounterpoint.value = counterpointToPlay
               onPlay(true, true)
        }
        if (selCpSaved != null) {
            _selectedCounterpoint.value = selCpSaved.clone()
        }
    }
    val onPlay = { createAndPlay: Boolean, simplify: Boolean  ->
        var error = "ERROR: NO FILE"
        if (userOptionsData.value!!.isEmpty()) {
            insertUserOptionData(UserOptionsData.getDefaultUserOptionsData())
        }
        if (!selectedCounterpoint.value!!.isEmpty()) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
                mediaPlayer?.setOnCompletionListener { onStop() }
            }
            val counterpoints = listOf(selectedCounterpoint.value!!) + savedCounterpoints.toList()
            userOptionsData.value?.let{
                error = launchPlayer(
                    userOptionsData.value!![0], createAndPlay, simplify,
                    mediaPlayer!!, midiPath, counterpoints)
            }
        }
        mediaPlayer?.let { if (it.isPlaying) _playing.value = true }
        error.also { println(it) }
    }
    val dispatchIntervals = {
        if(computationStack.isNotEmpty())
            refreshComputation(false)
    }
    val onTritoneSubstitutionFromSelector = { index: Int ->
        changeSequenceSelection(-1)
        updateSequence(index, ArrayList(sequences.value!![index].map{ it.tritoneSubstitution() }) )
    }

    val onTritoneSubstitution = {
        val previousComputation = computationStack.peek()!!
        if(previousComputation is Computation.TritoneSubstitution) {
            viewModelScope.launch(Dispatchers.Unconfined){
                onBack()
            } //calling twice is like not calling it at all
        } else {
            val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
            val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
            computationStack.pushAndDispatch(Computation.TritoneSubstitution(originalCounterpoints, intervalSet.value!!.toList(),index))
            tritoneSubstitutionOnCounterpoints(originalCounterpoints, index)
        }
    }
    val onCadenzaFromSelector = { list: ArrayList<Clip>, values: List<Int> ->
        changeFirstSequence(list)
        convertFirstSequenceToSelectedCounterpoint()
        computationStack.pushAndDispatch(Computation.Cadenza(listOf(selectedCounterpoint.value!!.clone()), list,0, values))
        cadenzasOnCounterpoints(listOf(selectedCounterpoint.value!!.clone()), 0, values)
    }
    val onCadenza = { values: List<Int> ->
        val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        computationStack.pushAndDispatch(Computation.Cadenza(originalCounterpoints, null, index, values))
        cadenzasOnCounterpoints(originalCounterpoints, index, values)
    }
    val onOverlapFromSelector = { list: ArrayList<Clip>, position: Int, crossover: Boolean ->
        if(savedCounterpoints[position] != null ){
            changeFirstSequence(list)
            convertFirstSequenceToSelectedCounterpoint()
            val computation = if(crossover) Computation.Crossover(selectedCounterpoint.value!!.clone(), savedCounterpoints[position]!!.clone(),list)
                else Computation.Overlap(selectedCounterpoint.value!!.clone(), savedCounterpoints[position]!!.clone(),list)
            computationStack.pushAndDispatch(computation)
            overlapBothCounterpoints(selectedCounterpoint.value!!.clone(), savedCounterpoints[position]!!.clone(), crossover)
        }
    }
    val onOverlap = { position: Int , crossover: Boolean->
        val counterpoint2nd = savedCounterpoints[position]
        if (counterpoint2nd != null ) {
            val computation = if(crossover) Computation.Crossover(selectedCounterpoint.value!!.clone(),counterpoint2nd.clone(), null)
                else Computation.Overlap(selectedCounterpoint.value!!.clone(),counterpoint2nd.clone(), null)
            computationStack.pushAndDispatch(computation)
            overlapBothCounterpoints(selectedCounterpoint.value!!.clone(), counterpoint2nd.clone(), crossover)
        }
    }
    val onGlueFromSelector = { list: ArrayList<Clip>, position: Int ->
        if(savedCounterpoints[position] != null ){
            changeFirstSequence(list)
            convertFirstSequenceToSelectedCounterpoint()
            computationStack.pushAndDispatch(Computation.Glue(selectedCounterpoint.value!!.clone(), savedCounterpoints[position]!!.clone(),list))
            glueBothCounterpoints(selectedCounterpoint.value!!.clone(), savedCounterpoints[position]!!.clone())
        }
    }
    val onGlue= { position: Int ->
        val counterpoint2nd = savedCounterpoints[position]
        if (counterpoint2nd != null) {
            computationStack.pushAndDispatch(Computation.Glue(selectedCounterpoint.value!!.clone(),counterpoint2nd.clone(), null))
            glueBothCounterpoints(selectedCounterpoint.value!!.clone(), counterpoint2nd.clone())
        }
    }
    val onScarlattiFromSelector = { list: ArrayList<Clip> ->
        changeFirstSequence(list)
        convertFirstSequenceToSelectedCounterpoint()
        computationStack.pushAndDispatch(Computation.Scarlatti(listOf(selectedCounterpoint.value!!.clone()), list,0))
        duplicateAllPhrasesInCounterpoint(selectedCounterpoint.value!!.clone(), 0)
    }
    val onScarlatti = {
        val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        computationStack.pushAndDispatch(Computation.Scarlatti(originalCounterpoints, null, index))
        duplicateAllPhrasesInCounterpoint(selectedCounterpoint.value!!.clone(), 0)
    }
    val onSortCounterpoints = { sortType: Int ->
        val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        val iconString = if(sortType == 0) "sort_up" else "sort_down"
        computationStack.pushAndDispatch(Computation.Sort(originalCounterpoints, null, index, sortType, iconString ))
        sortAllCounterpoints(originalCounterpoints, sortType, index)
    }
    val onUpsideDown = {
        val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        computationStack.pushAndDispatch(Computation.UpsideDown(originalCounterpoints, null, index))
        upsideDownAllCounterpoints(originalCounterpoints,index)
    }
    val onEraseIntervals = {
        val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        computationStack.pushAndDispatch(Computation.EraseIntervals(originalCounterpoints, null, index))
        eraseIntervalsOnCounterpoints(originalCounterpoints, index)
    }
    val onEraseIntervalsFromSelector = { list: ArrayList<Clip> ->
        changeFirstSequence(list)
        convertFirstSequenceToSelectedCounterpoint()
        computationStack.pushAndDispatch(Computation.EraseIntervals(listOf(selectedCounterpoint.value!!.clone()), list,0))
        eraseIntervalsOnCounterpoints(listOf(selectedCounterpoint.value!!.clone()), 0)
    }
    val onSingleFromSelector = { list: ArrayList<Clip> ->
        changeFirstSequence(list)
        convertFirstSequenceToSelectedCounterpoint()
        computationStack.pushAndDispatch(Computation.Single(listOf(selectedCounterpoint.value!!.clone()), list,0))
        singleOnCounterpoints(listOf(selectedCounterpoint.value!!.clone()), 0)
    }
    val onSingle= {
        val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        computationStack.pushAndDispatch(Computation.Single(originalCounterpoints, null, index))
        singleOnCounterpoints(originalCounterpoints, index)
    }
    val onSimpleTransposition = { transpositions: List<Pair<Int,Int>> ->
        if(transpositions != listOf( Pair(0,1) )){
            val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
            val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
            computationStack.pushAndDispatch(Computation.Transposition(originalCounterpoints, transpositions, index))
            transposeOnCounterpoints(originalCounterpoints, transpositions, index)
        }
    }
    val onDoppelgänger= {
        val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        computationStack.pushAndDispatch(Computation.Doppelgänger(originalCounterpoints, null, index))
        doppelgängerOnCounterpoints(originalCounterpoints, index)
    }
    val onDoppelgängerFromSelector = { list: ArrayList<Clip> ->
        changeFirstSequence(list)
        convertFirstSequenceToSelectedCounterpoint()
        computationStack.pushAndDispatch(Computation.Doppelgänger(listOf(selectedCounterpoint.value!!.clone()), list,0))
        doppelgängerOnCounterpoints(listOf(selectedCounterpoint.value!!.clone()), 0)
    }
    val onRoundFromSelector = { list: ArrayList<Clip> ->
        changeFirstSequence(list)
        convertFirstSequenceToSelectedCounterpoint()
        computationStack.pushAndDispatch(Computation.Round(listOf(selectedCounterpoint.value!!.clone()),0))
        //changeCounterpoints(listOf(selectedCounterpoint.value!!.clone()), true)
        roundOnCounterpoints(listOf(selectedCounterpoint.value!!.clone()), 0)
    }
    val onRound = {
        val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        computationStack.pushAndDispatch(Computation.Round(originalCounterpoints,index))
        roundOnCounterpoints(originalCounterpoints, index)
    }
    val onFlourishFromSelector = { list: ArrayList<Clip> ->
        changeFirstSequence(list)
        convertFirstSequenceToSelectedCounterpoint()
        computationStack.pushAndDispatch(Computation.Fioritura(listOf(selectedCounterpoint.value!!.clone()), list,0))
        flourishCounterpoints(listOf(selectedCounterpoint.value!!.clone()), 0)
    }
    val onFlourish = {
        val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        computationStack.pushAndDispatch(Computation.Fioritura(originalCounterpoints, null, index))
        flourishCounterpoints(originalCounterpoints, index)
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
        addSequenceToCounterpoint(repeat)

    }
    val onKPfromFirstSelection = {list: ArrayList<Clip>, index: Int, repeat: Boolean ->
        changeFirstSequence(list)
        computationStack.pushAndDispatch(Computation.FirstFromKP(selectedCounterpoint.value!!.clone(),
                                ArrayList(firstSequence.value!!), index, repeat))
        convertFirstSequenceToSelectedCounterpoint()
        changeSequenceToAdd(sequences.value!![index])
        addSequenceToCounterpoint(repeat)
    }
    val onLoadingCounterpointFromSelector = { position: Int ->
        val retrievedCounterpoint = savedCounterpoints[position]?.clone() ?: Counterpoint.empty(1,1)
        computationStack.pushAndDispatch(Computation.FirstFromLoading(listOf(retrievedCounterpoint),
            ArrayList(firstSequence.value!!), position))
        changeSelectedCounterpoint(retrievedCounterpoint)
        changeCounterpointsWithLimit(listOf(retrievedCounterpoint),true)
    }
    val onWaveFromFirstSelection = { nWaves: Int, list: ArrayList<Clip> ->
        changeFirstSequence(list)
        computationStack.pushAndDispatch(Computation.FirstFromWave(listOf(selectedCounterpoint.value!!.clone()),
            ArrayList(firstSequence.value!!), nWaves))
        convertFirstSequenceToSelectedCounterpoint()
        findWavesFromSequence(nWaves)
    }
    val onWaveFurtherSelection = { nWaves: Int , stepBackCounterpoints: List<Counterpoint>? ->
        val originalCounterpoints = stepBackCounterpoints ?: counterpoints.value!!.map{ it.clone() }
        computationStack.pushAndDispatch(Computation.FurtherFromWave(originalCounterpoints, nWaves))
        findWavesOnCounterpoints(originalCounterpoints, nWaves)
    }
    val onPedalFromSelector = { nPedals: Int, list: ArrayList<Clip>->
        changeFirstSequence(list)
        computationStack.pushAndDispatch(Computation.Pedal(selectedCounterpoint.value!!.clone(),
            ArrayList(firstSequence.value!!), nPedals))
        convertFirstSequenceToSelectedCounterpoint()
        findPedal(nPedals, list)
    }
    val onPedal= { nPedals: Int ->
        computationStack.pushAndDispatch(Computation.Pedal(selectedCounterpoint.value!!.clone(),
            null, nPedals))
        findPedal(nPedals,null)
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
            computationStack.pushAndDispatch(Computation.MikroKanonOnly(selectedCounterpoint.value!!.clone(),
                ArrayList(sequenceToMikroKanons.value!!),3))
            if(list.isNotEmpty()) changeSequenceToMikroKanons(list)
            findCounterpointsByMikroKanons3()
    }
    val onMikroKanons4 = {list: ArrayList<Clip> ->
            computationStack.pushAndDispatch(
                Computation.MikroKanonOnly(
                    selectedCounterpoint.value!!.clone(),
                    ArrayList(sequenceToMikroKanons.value!!), 4
                )
            )
            if (list.isNotEmpty()) changeSequenceToMikroKanons(list)
            findCounterpointsByMikroKanons4()
    }
    val onMikroKanons5reducted = {list: ArrayList<Clip> ->
        computationStack.pushAndDispatch(
            Computation.MikroKanonOnly(
                selectedCounterpoint.value!!.clone(),
                ArrayList(sequenceToMikroKanons.value!!), 5
            )
        )
        if (list.isNotEmpty()) changeSequenceToMikroKanons(list)
        findCounterpointsByMikroKanons5reducted()
    }
    val onMaze = {intSequences: List<List<Int>> ->
        println("Maze intSequences: $intSequences")
        computationStack.pushAndDispatch(Computation.Maze(intSequences))
            findMazes(intSequences)
    }
    val onBack = {
        if(computationStack.size > 1) {
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
                val previousIntervalSet: List<Int>? = if (computationStack.lastElement() is Computation.TritoneSubstitution)
                    (computationStack.lastElement() as Computation.TritoneSubstitution).intervalSet
//                    else if (computationStack.lastElement() is Computation.Pedal)
//                    (computationStack.lastElement() as Computation.Pedal).intervalSet
                    else null
                if (stepBack ) computationStack.popAndDispatch()
                val previousComputation = when(computationStack.lastElement()){
                    is Computation.FirstFromLoading -> computationStack.lastElement()
                    is Computation.Fioritura -> computationStack.lastElement()
                    is Computation.Expand -> computationStack.lastElement()
                    is Computation.Round -> computationStack.lastElement()
                    is Computation.Doppelgänger -> computationStack.lastElement()
                    is Computation.Cadenza -> computationStack.lastElement()
                    is Computation.Scarlatti -> computationStack.lastElement()
                    is Computation.Overlap -> computationStack.lastElement()
                    is Computation.Crossover -> computationStack.lastElement()
                    is Computation.Glue -> computationStack.lastElement()
                    is Computation.Maze -> computationStack.lastElement()
                    is Computation.Sort -> computationStack.lastElement()
                    is Computation.UpsideDown -> computationStack.lastElement()
                    is Computation.EraseIntervals -> computationStack.lastElement()
                    is Computation.Single-> computationStack.lastElement()
                    is Computation.Transposition-> computationStack.lastElement()
                    is Computation.Pedal -> computationStack.lastElement()
                    is Computation.TritoneSubstitution -> computationStack.lastElement()
                    else -> { stackIcons.removeLast(); computationStack.pop() } // do not Dispatch!!!
                }
                previousIntervalSet?.let { changeIntervalSet(previousIntervalSet)}
                when (previousComputation) {
                    is Computation.FirstFromLoading -> {
                        changeSelectedCounterpoint(previousComputation.counterpoints[0])
                        changeCounterpointsWithLimit(previousComputation.counterpoints, true)
                    }
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
                        when (previousComputation.nParts) {
                            2 -> onMikroKanons2(ArrayList(sequenceToMikroKanons.value!!))
                            3 -> onMikroKanons3(ArrayList(sequenceToMikroKanons.value!!))
                            4 -> onMikroKanons4(ArrayList(sequenceToMikroKanons.value!!))
                            5 -> onMikroKanons5reducted(ArrayList(sequenceToMikroKanons.value!!))
                            else -> Unit
                        }
                    }
                    is Computation.Maze -> {
                        findMazes(previousComputation.intSequences)
                    }
                    is Computation.TritoneSubstitution -> {
                            tritoneSubstitutionOnCounterpoints(previousComputation.counterpoints, previousComputation.index)
                    }
                    is Computation.Fioritura -> {
                            flourishCounterpoints(previousComputation.counterpoints, previousComputation.index)

                    }
                    is Computation.Doppelgänger -> {
                        doppelgängerOnCounterpoints( previousComputation.counterpoints,previousComputation.index)
                    }
                    is Computation.Round -> {
                        if(stepBack){
                            roundOnCounterpoints(previousComputation.counterpoints, previousComputation.index)
                        }
                    }
                    is Computation.Cadenza -> {
                        cadenzasOnCounterpoints( previousComputation.counterpoints,previousComputation.index, previousComputation.values)
                    }
                    is Computation.Sort -> {
                        sortAllCounterpoints( previousComputation.counterpoints,previousComputation.index, previousComputation.sortType)
                    }
                    is Computation.UpsideDown -> {
                        upsideDownAllCounterpoints( previousComputation.counterpoints,previousComputation.index)
                    }
                    is Computation.Scarlatti -> {
                        duplicateAllPhrasesInCounterpoint( previousComputation.counterpoints[previousComputation.index],previousComputation.index)
                    }
                    is Computation.Overlap -> {
                        overlapBothCounterpoints( previousComputation.counterpoint1st, previousComputation.counterpoint2nd, false)
                    }
                    is Computation.Crossover -> {
                        overlapBothCounterpoints( previousComputation.counterpoint1st, previousComputation.counterpoint2nd, true)
                    }
                    is Computation.Glue -> {
                        glueBothCounterpoints( previousComputation.counterpoint1st, previousComputation.counterpoint2nd)
                    }
                    is Computation.EraseIntervals -> {
                        eraseIntervalsOnCounterpoints( previousComputation.counterpoints,previousComputation.index)
                    }
                    is Computation.Single -> {
                        singleOnCounterpoints( previousComputation.counterpoints,previousComputation.index)
                    }
                    is Computation.Transposition -> {
                        transposeOnCounterpoints( previousComputation.counterpoints, previousComputation.transpositions, previousComputation.index)
                    }
                    is Computation.Pedal -> {
                            changeSelectedCounterpoint(previousComputation.counterpoint)
                            findPedal(previousComputation.nPedals, previousComputation.firstSequence)
                    }
                    is Computation.Expand -> {
                        if(stepBack){
                            expandCounterpoints(previousComputation.counterpoints,
                                                previousComputation.index, previousComputation.extension)
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
    private fun findPedal(nPedals: Int, list: ArrayList<Clip>?){
        var newList: List<Counterpoint>
        //var newIntervalSet: List<Int>
        val counterpoint = list?.let{ Counterpoint.counterpointFromClipList(list)} ?: selectedCounterpoint.value!!
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = listOf(findPedalsOnCounterpoint(nPedals, counterpoint, intervalSet.value!!))

                //newIntervalSet = pair.second
            }
            //changeIntervalSet(newIntervalSet)
            changeCounterpointsWithLimit(newList, true)
        }
    }



    private fun findWavesFromSequence(nWaves: Int){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = waves(listOf(Counterpoint.counterpointFromClipList(firstSequence.value!!)), intervalSet.value!! ,intervalSetHorizontal.value!!, nWaves)
            }
            changeCounterpointsWithLimit(newList, true)
        }
    }

    fun findWavesOnCounterpoints(originalCounterpoints: List<Counterpoint>, nWaves: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = waves(originalCounterpoints,intervalSet.value!!, intervalSetHorizontal.value!!, nWaves)
                            .sortedBy { it.emptiness }//.take(maxVisibleCounterpoints)
                            .mapIf(userOptionsData.value!![0].spread != 0){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                            .sortedBy { it.emptiness }
                }
                changeCounterpointsWithLimit(newList, true)
            }
        }
    }

    private fun findFreeParts(trend: TREND){
        var newList: List<Counterpoint>
        val spreadWherePossible = userOptionsData.value!![0].spread != 0
        val directions = trend.directions.filter{ intervalSetHorizontal.value!!.contains(it)}
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = freeParts(selectedCounterpoint.value!!,  intervalSet.value!!, directions)
                    .sortedBy { it.emptiness }//.take(maxVisibleCounterpoints)
                    .mapIf(spreadWherePossible){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                    .sortedBy { it.emptiness }
            }
            changeCounterpointsWithLimit(newList, true)
            counterpoints.value?.let{
                if(it.isNotEmpty()) changeSelectedCounterpoint(it[0])
            }
        }
    }

    private val jobQueue = LinkedList<Job>()
    private fun cancelPreviousMKjobs() {
        if(jobQueue.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.Main) {
                while ( jobQueue.isNotEmpty()){
                    val job = jobQueue.poll()
                    job?.cancel()
                }
            }
        }
    }
    private fun findCounterpointsByMikroKanons4(){
        viewModelScope.launch(Dispatchers.Main){
            val deepSearch = userOptionsData.value!![0].deepSearch != 0
            if(sequenceToMikroKanons.value!!.isNotEmpty()) {
                val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList()
                    .take( if(deepSearch) MAX_NOTES_MK_4DEEP else MAX_NOTES_MK_4 )
                val key = CacheKey(sequence, intervalSet.value!!)
                if(mk4cache.containsKey(key) && !deepSearch) {
                    changeCounterpointsWithLimit(mk4cache[key]!!.first, true)
                }else if(mk4deepSearchCache.containsKey(key) && deepSearch) {
                    changeCounterpointsWithLimit(mk4deepSearchCache[key]!!.first, true)
                }else {
                   measureTimeMillis{
                    _elaborating.value = true
                       // val def = async(Dispatchers.Default + MKjob) {
                           val newList = withContext(Dispatchers.Default){
                            mikroKanons4(this.coroutineContext.job,
                                sequence,
                                deepSearch,
                                intervalSet.value!!,
                                MAX_VISIBLE_COUNTERPOINTS
                            )
                                .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                                .pmapIf(userOptionsData.value!![0].spread != 0) { it.spreadAsPossible(intervalSet = intervalSet.value!!) }
                                .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                        }
                    //val newList: List<Counterpoint> = def.await()
                    if (deepSearch) {
                        mk4deepSearchCache.insertAndClear(key, newList.take(MAX_VISIBLE_COUNTERPOINTS), System.currentTimeMillis())
                    } else {
                        mk4cache.insertAndClear(key, newList.take(MAX_VISIBLE_COUNTERPOINTS), System.currentTimeMillis())
                    }
                    changeCounterpointsWithLimit(newList, true)
                    _elaborating.value = false
                    }.also { time -> println("MK4 executed in $time ms" )}
                }
            }
        }.also{  jobQueue.add(it)  }
    }
    private fun findCounterpointsByMikroKanons5reducted() {
        viewModelScope.launch(Dispatchers.Main) {
            if (sequenceToMikroKanons.value!!.isNotEmpty()) {
                val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList().take(MAX_NOTES_MK_5RED)
                val key = CacheKey(sequence, intervalSet.value!!)
                if (mk5reductedCache.containsKey(key) ) {
                    changeCounterpointsWithLimit(mk5reductedCache[key]!!.first, true)
                } else {
                    measureTimeMillis {
                        _elaborating.value = true
                        // val def = async(Dispatchers.Default + MKjob) {
                        val newList = withContext(Dispatchers.Default) {
                            mikroKanons5reducted(
                                this.coroutineContext.job,
                                sequence,
                                intervalSet.value!!,
                                MAX_VISIBLE_COUNTERPOINTS
                            )
                                .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                                .pmapIf(userOptionsData.value!![0].spread != 0) { it.spreadAsPossible(intervalSet = intervalSet.value!!) }
                                .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                        }
                        //val newList: List<Counterpoint> = def.await()
                        mk5reductedCache.insertAndClear(key, newList.take(MAX_VISIBLE_COUNTERPOINTS), System.currentTimeMillis())
                        changeCounterpointsWithLimit(newList, true, )
                        _elaborating.value = false
                    }.also { time -> println("MK5reducted executed in $time ms") }
                }
            }
        }.also { jobQueue.add(it) }
    }
    private fun findMazes(intSequences: List<List<Int>>) {
            viewModelScope.launch(Dispatchers.Main) {
                val sequence = intSequences.reduce{ acc, seq -> acc + seq }
                val key = CacheKey(sequence, intervalSet.value!!)
                if (mazeCache.containsKey(key) ) {
                    changeCounterpointsWithLimit(mazeCache[key]!!.first, true)
                } else {
                    measureTimeMillis {
                        _elaborating.value = true
                        // val def = async(Dispatchers.Default + MKjob) {
                        val maxNotesInMaze = MAX_NOTES_IN_MAZE[intSequences.size]
                        val newList = withContext(Dispatchers.Default) {
                            maze(this.coroutineContext.job, intSequences.map{it.take(maxNotesInMaze)}, intervalSet.value!!)
                                .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                                .pmapIf(userOptionsData.value!![0].spread != 0) { it.spreadAsPossible(intervalSet = intervalSet.value!!) }
                                .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                        }
                        mazeCache.insertAndClear(key, newList.take(MAX_VISIBLE_COUNTERPOINTS), System.currentTimeMillis())
                        changeCounterpointsWithLimit(newList, true)
                        println("Maze list size = ${newList.size}")
                        _elaborating.value = false
                    }.also { time -> println("Maze executed in $time ms ") }
                }
            }.also { jobQueue.add(it) }
    }
    private fun findCounterpointsByMikroKanons3(){
         viewModelScope.launch(Dispatchers.Main){
            if(sequenceToMikroKanons.value!!.isNotEmpty()) {
                val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList().take(MAX_NOTES_MK_3)
                val key = CacheKey(sequence, intervalSet.value!!)
                if(mk3cache.containsKey(key)) {
                    changeCounterpointsWithLimit(mk3cache[key]!!.first, true)
                }else {
                    val newList: List<Counterpoint>
                    _elaborating.value = true
                    withContext(Dispatchers.Default) {
                        newList = mikroKanons3(sequence,intervalSet.value!!, 6)
                            .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }//.take(maxVisibleCounterpoints)
                            .pmapIf(userOptionsData.value!![0].spread != 0){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                            .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                    }
                    mk3cache.insertAndClear(key, newList.take(MAX_VISIBLE_COUNTERPOINTS), System.currentTimeMillis())
                    _elaborating.value = false
                    changeCounterpointsWithLimit(newList, true)
                }
            }
        }.also{  jobQueue.add(it)  }
    }

    private fun findCounterpointsByMikroKanons2(){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList().take(MAX_NOTES_MK_2)
                newList = mikroKanons2(sequence,intervalSet.value!!, 7)
                    .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }//.take(maxVisibleCounterpoints)
                    .pmapIf(userOptionsData.value!![0].spread != 0){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                    .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
            }
            changeCounterpointsWithLimit(newList, true)
        }
    }
    private fun flourishCounterpoints(originalCounterpoints: List<Counterpoint>, index: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = flourish(originalCounterpoints, intervalSet.value!!, intervalSetHorizontal.value!!.toList())
                }
                changeCounterpointsWithLimit(newList, false)
                changeSelectedCounterpoint(counterpoints.value!![index])
            }
        }
    }
    private fun roundOnCounterpoints(originalCounterpoints: List<Counterpoint>, index: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = buildRound(originalCounterpoints)
                }
                changeCounterpointsWithLimit(newList, false)
                changeSelectedCounterpoint(counterpoints.value!![index])
            }
        }
    }
    private fun cadenzasOnCounterpoints(originalCounterpoints: List<Counterpoint>, index: Int, values: List<Int>){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = addCadenzasOnCounterpoints(intervalSetHorizontal.value!!, originalCounterpoints, values)
                }
                changeCounterpointsWithLimit(newList, false)
                changeSelectedCounterpoint(counterpoints.value!![index])
            }
        }
    }
    private fun duplicateAllPhrasesInCounterpoint(originalCounterpoint: Counterpoint,index: Int){
        if(!originalCounterpoint.isEmpty()){
            var newList: List<Counterpoint>
            val spread = userOptionsData.value!![0].spread != 0
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = duplicateAllInCounterpoint(originalCounterpoint)
                        .pmapIf(spread){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                        .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                }
                changeCounterpointsWithLimit(newList, true)
                changeSelectedCounterpoint(counterpoints.value!![index])
            }
        }
    }
    private fun overlapBothCounterpoints(counterpoint1st: Counterpoint, counterpoint2nd: Counterpoint, crossover: Boolean){
        if(!counterpoint1st.isEmpty() && !counterpoint2nd.isEmpty()){
            var newList: List<Counterpoint>
            val spread = userOptionsData.value!![0].spread != 0
            viewModelScope.launch(Dispatchers.Main){
                _elaborating.value = true
                withContext(Dispatchers.Default){
                    newList = overlapCounterpointsSortingByFaults(
                        this.coroutineContext.job,
                        counterpoint1st, counterpoint2nd, intervalSet.value!!, MAX_PARTS, crossover)
                    newList = if(spread) newList.pmap{it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                        .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                             else newList
                }
                changeCounterpointsWithLimit(newList, true, MAX_VISIBLE_COUNTERPOINTS * 2)
                _elaborating.value = false
            }.also{  jobQueue.add(it)  }
        }
    }
    private fun glueBothCounterpoints(counterpoint1st: Counterpoint, counterpoint2nd: Counterpoint){
        if(!counterpoint1st.isEmpty() && !counterpoint2nd.isEmpty()){
            var newList: List<Counterpoint>
            val spread = userOptionsData.value!![0].spread != 0
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = glueCounterpoints(counterpoint1st, counterpoint2nd)
                        .pmapIf(spread){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                        .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                }
                changeCounterpointsWithLimit(newList, true)
            }.also{  jobQueue.add(it)  }
        }
    }

    private fun eraseIntervalsOnCounterpoints(originalCounterpoints: List<Counterpoint>, index: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = eraseHorizontalIntervalsOnCounterpoints(intervalSetHorizontal.value!!, originalCounterpoints)
                }
                changeCounterpointsWithLimit(newList, false)
                changeSelectedCounterpoint(counterpoints.value!![index])
            }
        }
    }
    private fun sortAllCounterpoints(originalCounterpoints: List<Counterpoint>, sortType: Int, index: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>

            val spread = userOptionsData.value!![0].spread != 0
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = sortColumnsOnCounterpoints(originalCounterpoints, sortType)
                        .pmapIf(spread){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                        .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                }
                changeCounterpointsWithLimit(newList, spread)
                changeSelectedCounterpoint(counterpoints.value!![index])
            }
        }
    }
    private fun upsideDownAllCounterpoints(originalCounterpoints: List<Counterpoint>,index: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>

            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = upsideDownCounterpoints(originalCounterpoints)
                        .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                }
                changeCounterpointsWithLimit(newList, false)
                changeSelectedCounterpoint(counterpoints.value!![index])
            }
        }
    }
    private fun singleOnCounterpoints(originalCounterpoints: List<Counterpoint>, index: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            val spread = userOptionsData.value!![0].spread != 0
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = reduceCounterpointsToSinglePart(originalCounterpoints)
                        .pmapIf(spread){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                        .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                }
                changeCounterpointsWithLimit(newList, spread)
                changeSelectedCounterpoint(counterpoints.value!![index])
            }
        }
    }
    private fun doppelgängerOnCounterpoints(originalCounterpoints: List<Counterpoint>, index: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            val spread = userOptionsData.value!![0].spread != 0
            var newList: List<Counterpoint>
            val ensList: List<List<EnsembleType>> =
                userOptionsData.value?.let { listOf(userOptionsData.value!![0].ensemblesList
                    .extractIntListsFromCsv()[0].map{EnsembleType.values()[it]})}
                    ?: listOf(listOf(EnsembleType.STRING_ORCHESTRA))
            val rangeType: Pair<Int,Int> =
                userOptionsData.value?.let { userOptionsData.value!![0].rangeTypes.extractIntPairsFromCsv()[0] }
                    ?: Pair(2,0)
            val melodyType: Int =
                userOptionsData.value?.let { userOptionsData.value!![0].melodyTypes.extractIntsFromCsv()[0] }
                    ?: 0
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = explodeCounterpointsToDoppelgänger(originalCounterpoints,
                        MAX_PARTS, ensList[0], rangeType, melodyType )
                        .pmapIf(spread){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                        .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                }
                changeCounterpointsWithLimit(newList, true)
                changeSelectedCounterpoint(counterpoints.value!![index])
            }
        }
    }
    private fun expandCounterpoints(originalCounterpoints: List<Counterpoint>, index: Int, extension: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = expand(originalCounterpoints, extension)
                }
                changeCounterpointsWithLimit(newList, false)
                changeSelectedCounterpoint(counterpoints.value!![index])
            }
        }
    }
    private fun transposeOnCounterpoints(originalCounterpoints: List<Counterpoint>, transpositions: List<Pair<Int,Int>>, index: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = transposeAllCounterpoints(originalCounterpoints, transpositions)
                }
                changeCounterpointsWithLimit(newList, false)
                changeSelectedCounterpoint(counterpoints.value!![index])
            }
        }
    }
    private fun tritoneSubstitutionOnCounterpoints(originalCounterpoints: List<Counterpoint>, index: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = originalCounterpoints.map{ it.tritoneSubstitution() }
                }
                changeIntervalSet(tritoneSubstitutionOnIntervalSet(intervalSet.value!!))
                changeCounterpointsWithLimit(newList, false)
                changeSelectedCounterpoint(counterpoints.value!![index])
            }
        }
    }

    private fun addSequenceToCounterpoint(repeat: Boolean){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = addSequence(selectedCounterpoint.value!! , sequenceToAdd.value!!, intervalSet.value!! ,repeat, 7)
                        .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }//.take(maxVisibleCounterpoints)
                        .pmapIf(userOptionsData.value!![0].spread != 0){
                            it.spreadAsPossible(true, intervalSet = intervalSet.value!!)}
                        //.map{ it.emptiness = it.findEmptiness(); it}
                        .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                }
                changeCounterpointsWithLimit(newList, true)
            }
        }
    }

    private fun convertFirstSequenceToSelectedCounterpoint() {
        val newCounterpoint = Counterpoint.counterpointFromClipList(firstSequence.value!!)
        _selectedCounterpoint.value = newCounterpoint
    }

    fun setInitialBlankState() {
        cancelPreviousMKjobs()
        _elaborating.value = false
        onStop()
        computationStack.clearAndDispatch()
        changeCounterpointsWithLimit(listOf(), false)
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
    fun changeCounterpointsWithLimit(newCounterpoints: List<Counterpoint>, selectFirst: Boolean,
                                     take: Int = MAX_VISIBLE_COUNTERPOINTS){
        lastIndex = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        _counterpoints.value = newCounterpoints.take(take)
        if(selectFirst) counterpoints.value?.let{
            if(it.isNotEmpty()) changeSelectedCounterpoint(it[0])
        }
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
        mk4deepSearchCache.clear()
        mk5reductedCache.clear()
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
    fun retrieveCounterpointsFromDB(){
        allCounterpointsData.value?.forEachIndexed { index, counterpoint ->
            val newCounterpoint = if (counterpoint.parts.isEmpty()) null
            else Counterpoint.createFromCsv(counterpoint.parts, timestamp = counterpoint.timestamp)
            savedCounterpoints[index] = newCounterpoint
        }
    }
    fun saveCounterpointInDb(position: Int, counterpoint: Counterpoint) {
        val timestamp = System.currentTimeMillis()
        savedCounterpoints[position] = counterpoint.copy(timestamp = timestamp)
        val counterpointData = CounterpointData(position.toLong()+1L, counterpoint.convertPartsToCsv(), timestamp)
        viewModelScope.launch(Dispatchers.IO) {
            counterpointRepository.updateCounterpoint(counterpointData)
        }
    }
    fun clearCounterpointsInDb(numbers: Set<Int>){
        val defaultCounterpoint = CounterpointData.getDefaultCounterpointData()
        numbers.forEach {
            savedCounterpoints[it] = null
            viewModelScope.launch(Dispatchers.IO) {
                val clearedCounterpoint = defaultCounterpoint.copy(id = it.toLong()+1L)
                counterpointRepository.updateCounterpoint(clearedCounterpoint)
            }
        }
    }
    fun displaySavedCounterpoints(){
        allCounterpointsData.value?.let{
            it.forEach { each -> println(each) }
        }
    }

    private fun sequenceDataToSequence(sequenceData: SequenceData) : ArrayList<Clip>{
        val sequence = ArrayList(sequenceData.clips.map {  Clip.clipDataToClip(it) } )
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
        println("$key: $value")
        val optionsDataClone = if(userOptionsData.value!!.isEmpty())
                                UserOptionsData.getDefaultUserOptionsData()
                                else userOptionsData.value!![0].copy()
        when(key){
            "spread" -> clearMKcaches()
            "deepSearch" -> { mk5reductedCache.clear() ;mk4cache.clear(); mk4deepSearchCache.clear() }
        }
        UserOptionsData.updateUserOptionsData(optionsDataClone, key, value).apply{
            viewModelScope.launch(Dispatchers.IO) {
                if(userOptionsData.value!!.isNotEmpty()){
                    userRepository.deleteAllUserOptions()
                }
                userRepository.insertUserOptions(this@apply)
            }
        }
    }
    var usingCustomColors: Boolean = false
    var appColors: AppColors = AppColors.allBlack()
    var counterpointView = 0
    var lastIndexCustomColors = -1
    var lastAppColors = ""
    fun setAppColors(defs: String){
        println("COLORS: $defs")
        val colorDefs = extractColorDefs(defs)
        if(colorDefs.isCustom){
            if(lastIndexCustomColors != colorDefs.custom) {
                lastAppColors = ""
                lastIndexCustomColors = colorDefs.custom
                appColors = AppColors.getCustomColorsFromIndex(getContext(),colorDefs.custom)
            }
        } else {
            if(lastAppColors != colorDefs.app){
                lastIndexCustomColors = -1
                appColors = if(colorDefs.app == "System") AppColors.provideAppColors(AppColorThemes.GEMINI_BLUE)
                else AppColors.provideAppColors(AppColorThemes.values().first{ it.title == colorDefs.app })
            }
        }
    }
    fun getSystemAppColorsName(): String{
        return AppColorThemes.GEMINI_BLUE.title
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

    fun createHorizontalIntervalSet(horizontalIntervalSetFlag: Int) {
        _intervalSetHorizontal.value = createIntervalSetFromFlags(horizontalIntervalSetFlag)
    }
    fun createVerticalIntervalSet(verticalIntervalSetFlag: Int, from: String) {
        println("Creating Vert Interval Set: ${intervalSet.value!!} from $from")
        _intervalSet.value = createIntervalSetFromFlags(verticalIntervalSetFlag)
    }
    fun createVerticalIntervalSet(intervalSet: List<Int>, from: String) {
        println("Creating Vert Interval Set: $intervalSet from $from")
        _intervalSet.value = intervalSet.sorted()
    }
    fun saveVerticalIntervalSet(from: String) {
        println("Saving Vert Interval Set: ${intervalSet.value!!} from $from")
        val flags = createFlagsFromIntervalSet(intervalSet.value!!)
        updateUserOptions("intSetVertFlags", flags)
    }
    var zodiacPlanetsActive = false
    var zodiacSignsActive = false
    var zodiacEmojisActive = false
    fun refreshZodiacFlags() {
        if(userOptionsData.value != null && userOptionsData.value!!.isNotEmpty()){
            val flags = userOptionsData.value!![0].zodiacFlags
            zodiacPlanetsActive = (flags and 1) == 1
            zodiacSignsActive = (flags and 2) == 2
            zodiacEmojisActive = (flags and 4) == 4
        }
    }


}






package com.cristianovecchi.mikrokanon

import android.app.Application
import android.content.Context
import android.content.Intent
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
    data class Pedal(val counterpoint: Counterpoint, val firstSequence: ArrayList<Clip>?, val intervalSet: List<Int>, val nPedals: Int, override val icon: String = "pedal"): Computation()
    data class FurtherFromKP(val counterpoint: Counterpoint, val indexSequenceToAdd: Int, val repeat: Boolean, override val icon: String = "counterpoint"): Computation()
    data class FurtherFromWave(val counterpoints: List<Counterpoint>, val nWaves: Int, override val icon: String = "waves"): Computation()
    data class FirstFromFreePart(val counterpoint: Counterpoint, val firstSequence: ArrayList<Clip>, val trend: TREND, override val icon: String = "free_parts"): Computation()
    data class FurtherFromFreePart(val counterpoint: Counterpoint, val firstSequence: ArrayList<Clip>, val trend: TREND, override val icon: String = "free_parts"): Computation()
    data class Fioritura(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val index: Int, override val icon: String = "fioritura"): Computation()
    data class Round(val counterpoints: List<Counterpoint>, val index: Int, override val icon: String = "round"): Computation()
    data class Cadenza(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val index: Int, override val icon: String = "cadenza"): Computation()
    data class Scarlatti(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val index: Int, override val icon: String = "Scarlatti"): Computation()
    data class EraseIntervals(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val index: Int, override val icon: String = "erase"): Computation()
    data class Single(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val index: Int, override val icon: String = "single"): Computation()
    data class Doppelgänger(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val index: Int, override val icon: String = "doppelgänger"): Computation()
    data class Expand(val counterpoints: List<Counterpoint>, val index: Int, val extension: Int = 2, override val icon: String = "expand") : Computation()
    data class SimpleTransposition(val counterpoints: List<Counterpoint>, val transposition: Int, val index: Int, override val icon: String = "transpose") : Computation()
    data class TritoneSubstitution(val counterpoints: List<Counterpoint>, val intervalSet: List<Int>, val index: Int, override val icon: String = "tritone_substitution") : Computation()
}

data class ActiveButtons(val editing: Boolean = false, val mikrokanon: Boolean = false,
                         val undo: Boolean = false, val expand: Boolean = true,
                         val waves: Boolean = false, val pedals: Boolean = true,
                         val counterpoint: Boolean = false, val specialFunctions: Boolean = false,
                         val freeparts: Boolean = false, val playOrStop: Boolean = true) {
}

enum class ScaffoldTabs {
    SOUND, BUILDING, SETTINGS
}

class AppViewModel(
    application: Application,
    private val sequenceRepository: SequenceDataRepository,
    private val counterpointRepository: CounterpointDataRepository,
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
    var lastScaffoldTab = ScaffoldTabs.SETTINGS
    // + 0.86f
    val dynamicSteps = listOf(0.000001f, 0.14f, 0.226f, 0.312f,  0.398f, 0.484f, 0.57f, 0.656f,  0.742f, 0.828f, 0.914f,1f )
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
        "transpose" to R.drawable.ic_baseline_unfold_more_24,
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
    val MAX_PARTS = 12

    private val  maxVisibleCounterpoints: Int = 74
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

    private data class CacheKey(val sequence: List<Int>, val intervalSet: List<Int>)
    private val mk3cache = HashMap<CacheKey, List<Counterpoint>>()
    private val mk4cache = HashMap<CacheKey, List<Counterpoint>>()
    private val mk4deepSearchCache = HashMap<CacheKey, List<Counterpoint>>()
    private val mk5reductedCache = HashMap<CacheKey, List<Counterpoint>>()

    val savedCounterpoints: Array<Counterpoint?> = arrayOf(null, null, null, null, null, null)

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
            if(userOptionsData.value!!.isEmpty()){
                insertUserOptionData(UserOptionsData.getDefaultUserOptionsData())
            }
            if(!selectedCounterpoint.value!!.isEmpty()) {
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer()
                    mediaPlayer?.setOnCompletionListener { onStop() }
                }
                val ensTypes: List<EnsembleType> =
                    userOptionsData.value?.let { userOptionsData.value!![0].ensembleTypes
                        .extractIntsFromCsv().map{EnsembleType.values()[it]}}
                        ?: listOf(EnsembleType.STRING_ORCHESTRA)
                val dynamics: List<Float> =
                    if(simplify) {
                        listOf(1f)
                    } else {
                        userOptionsData.value?.let { userOptionsData.value!![0].dynamics.extractFloatsFromCsv()
                    }} ?: listOf(1f)
                val bpms: List<Float> =
                    userOptionsData.value?.let {
                        if(simplify) {
                            userOptionsData.value!![0].bpms.extractIntsFromCsv().map { it.toFloat() }.take(1)
                        } else {
                            userOptionsData.value!![0].bpms.extractIntsFromCsv().map { it.toFloat() }
                        } } ?: listOf(90f)
                val rhythm: RhythmPatterns =
                    RhythmPatterns.values()[userOptionsData.value?.let { userOptionsData.value!![0].rhythm }
                        ?: 0]
                val rhythmShuffle: Boolean =
                    0 != (userOptionsData.value?.let { userOptionsData.value!![0].rhythmShuffle }
                        ?: 0)
                val partsShuffle: Boolean =
                    0 != (userOptionsData.value?.let { userOptionsData.value!![0].partsShuffle }
                        ?: 0)
                val rowForms: List<Pair<Int,Int>> =
                    userOptionsData.value?.let {
                        if(simplify){
                            listOf(Pair(1,1))
                        } else {
                            userOptionsData.value!![0].rowForms.extractIntPairsFromCsv()
                        } }?: listOf(Pair(1,1)) // ORIGINAL by default || 0 is unused
                val doublingFlags: Int =
                    userOptionsData.value?.let { userOptionsData.value!![0].doublingFlags }
                        ?: 0
                val audio8DFlags: Int =
                    userOptionsData.value?.let {
                        if(simplify){
                            0
                        } else {
                            userOptionsData.value!![0].audio8DFlags
                        } } ?: 0
                val ritornello: Int =
                    userOptionsData.value?.let {
                        if(simplify){
                            0
                        } else {
                            userOptionsData.value!![0].ritornello
                        } } ?: 0
                val transpose: List<Int> =
                    userOptionsData.value?.let { userOptionsData.value!![0].transpose.extractIntsFromCsv() }
                        ?: listOf(0)
                val nuances: Int =
                    userOptionsData.value?.let { userOptionsData.value!![0].nuances }
                        ?: 1
                val rangeTypes: List<Pair<Int,Int>> =
                    userOptionsData.value?.let { userOptionsData.value!![0].rangeTypes.extractIntPairsFromCsv() }
                        ?: listOf(Pair(2,0))
                val melodyTypes: List<Int> =
                    userOptionsData.value?.let { userOptionsData.value!![0].melodyTypes.extractIntsFromCsv() }
                        ?: listOf(0)
                val glissandoFlags: Int =
                    userOptionsData.value?.let { userOptionsData.value!![0].glissandoFlags }
                        ?: 0
                val vibrato: Int =
                    userOptionsData.value?.let { userOptionsData.value!![0].vibrato }
                        ?: 0
                //selectedCounterpoint.value!!.display()
                error = Player.playCounterpoint(
                    mediaPlayer!!,
                    false,
                    listOf(selectedCounterpoint.value!!) + savedCounterpoints.toList(),
                    dynamics,
                    bpms,
                    0f,
                    rhythm,
                    ensTypes,
                    createAndPlay,
                    midiPath,
                    rhythmShuffle,
                    partsShuffle,
                    rowForms,
                    ritornello,
                    transpose,
                    doublingFlags,
                    nuances,
                    rangeTypes,
                    melodyTypes,
                    glissandoFlags,
                    audio8DFlags,
                    vibrato
                )
            }
        mediaPlayer?.let{ if (it.isPlaying) _playing.value = true}
            error.also{println(it)}
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
    val onCadenzaFromSelector = { list: ArrayList<Clip> ->
        changeFirstSequence(list)
        convertFirstSequenceToSelectedCounterpoint()
        computationStack.pushAndDispatch(Computation.Cadenza(listOf(selectedCounterpoint.value!!.clone()), list,0))
        cadenzasOnCounterpoints(listOf(selectedCounterpoint.value!!.clone()), 0)
    }
    val onCadenza = {
        val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        computationStack.pushAndDispatch(Computation.Cadenza(originalCounterpoints, null, index))
        cadenzasOnCounterpoints(originalCounterpoints, index)
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
    val onSimpleTransposition = { transposition: Int ->
        if(transposition != 0){
            val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
            val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
            computationStack.pushAndDispatch(Computation.SimpleTransposition(originalCounterpoints, transposition, index))
            transposeOnCounterpoints(originalCounterpoints, transposition, index)
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
            ArrayList(firstSequence.value!!), intervalSet.value!!.toList(), nPedals))
        convertFirstSequenceToSelectedCounterpoint()
        findPedal(nPedals, list)
    }
    val onPedal= { nPedals: Int ->
        computationStack.pushAndDispatch(Computation.Pedal(selectedCounterpoint.value!!.clone(),
            null,intervalSet.value!!.toList(), nPedals))
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
                    is Computation.Fioritura -> computationStack.lastElement()
                    is Computation.Expand -> computationStack.lastElement()
                    is Computation.Round -> computationStack.lastElement()
                    is Computation.Doppelgänger -> computationStack.lastElement()
                    is Computation.Cadenza -> computationStack.lastElement()
                    is Computation.Scarlatti -> computationStack.lastElement()
                    is Computation.EraseIntervals -> computationStack.lastElement()
                    is Computation.Single-> computationStack.lastElement()
                    is Computation.SimpleTransposition-> computationStack.lastElement()
                    is Computation.Pedal -> computationStack.lastElement()
                    is Computation.TritoneSubstitution -> computationStack.lastElement()
                    else -> { stackIcons.removeLast(); computationStack.pop() } // do not Dispatch!!!
                }
                previousIntervalSet?.let { changeIntervalSet(previousIntervalSet)}
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
                        when (previousComputation.nParts) {
                            2 -> onMikroKanons2(ArrayList(sequenceToMikroKanons.value!!))
                            3 -> onMikroKanons3(ArrayList(sequenceToMikroKanons.value!!))
                            4 -> onMikroKanons4(ArrayList(sequenceToMikroKanons.value!!))
                            5 -> onMikroKanons5reducted(ArrayList(sequenceToMikroKanons.value!!))
                            else -> Unit
                        }
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
                        cadenzasOnCounterpoints( previousComputation.counterpoints,previousComputation.index)
                    }
                    is Computation.Scarlatti -> {
                        duplicateAllPhrasesInCounterpoint( previousComputation.counterpoints[previousComputation.index],previousComputation.index)
                    }
                    is Computation.EraseIntervals -> {
                        eraseIntervalsOnCounterpoints( previousComputation.counterpoints,previousComputation.index)
                    }
                    is Computation.Single -> {
                        singleOnCounterpoints( previousComputation.counterpoints,previousComputation.index)
                    }
                    is Computation.SimpleTransposition -> {
                        transposeOnCounterpoints( previousComputation.counterpoints, previousComputation.transposition, previousComputation.index)
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
            changeCounterpoints(newList, true)
        }
    }



    private fun findWavesFromSequence(nWaves: Int){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = waves(listOf(Counterpoint.counterpointFromClipList(firstSequence.value!!)), intervalSet.value!!,nWaves)
            }
            changeCounterpoints(newList, true)
        }
    }

    fun findWavesOnCounterpoints(originalCounterpoints: List<Counterpoint>, nWaves: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = waves(originalCounterpoints,intervalSet.value!!, nWaves)
                            .sortedBy { it.emptiness }.take(maxVisibleCounterpoints)
                            .mapIf(userOptionsData.value!![0].spread != 0){it.spreadAsPossible()}
                            .sortedBy { it.emptiness }
                }
                changeCounterpoints(newList, true)
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
                    .sortedBy { it.emptiness }.take(maxVisibleCounterpoints)
                    .mapIf(spreadWherePossible){it.spreadAsPossible()}
                    .sortedBy { it.emptiness }
            }
            changeCounterpoints(newList, true)
            counterpoints.value?.let{
                if(it.isNotEmpty()) changeSelectedCounterpoint(it[0])
            }
        }
    }

    private val jobQueue = java.util.LinkedList<Job>()
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
                val key = CacheKey(sequence, intervalSet.value!!)
                if(mk4cache.containsKey(key) && !deepSearch) {
                    changeCounterpoints(mk4cache[key]!!, true)
                }else if(mk4deepSearchCache.containsKey(key) && deepSearch) {
                    changeCounterpoints(mk4deepSearchCache[key]!!, true)
                }else {
                   measureTimeMillis{
                    _elaborating.value = true
                       // val def = async(Dispatchers.Default + MKjob) {
                           val newList = withContext(Dispatchers.Default){
                            mikroKanons4(this.coroutineContext.job,
                                sequenceToMikroKanons.value!!,
                                deepSearch,
                                intervalSet.value!!
                            )
                                .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                                .take(maxVisibleCounterpoints)
                                .pmapIf(userOptionsData.value!![0].spread != 0) { it.spreadAsPossible() }
                                .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                        }
                    //val newList: List<Counterpoint> = def.await()
                    if (deepSearch) {
                        mk4deepSearchCache[key] = newList
                    } else {
                        mk4cache[key] = newList
                    }
                    changeCounterpoints(newList, true)
                    _elaborating.value = false
                    }.also { time -> println("MK4 executed in $time ms" )}
                }
            }
        }.also{  jobQueue.add(it)  }
    }
    private fun findCounterpointsByMikroKanons5reducted() {
        viewModelScope.launch(Dispatchers.Main) {
            val deepSearch = userOptionsData.value!![0].deepSearch != 0
            if (sequenceToMikroKanons.value!!.isNotEmpty()) {
                val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList()
                val key = CacheKey(sequence, intervalSet.value!!)
                if (mk5reductedCache.containsKey(key) ) {
                    changeCounterpoints(mk5reductedCache[key]!!, true)
                } else {
                    measureTimeMillis {
                        _elaborating.value = true
                        // val def = async(Dispatchers.Default + MKjob) {
                        val newList = withContext(Dispatchers.Default) {
                            mikroKanons5reducted(
                                this.coroutineContext.job,
                                sequenceToMikroKanons.value!!,
                                intervalSet.value!!
                            )
                                .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                                .take(maxVisibleCounterpoints)
                                .pmapIf(userOptionsData.value!![0].spread != 0) { it.spreadAsPossible() }
                                .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                        }
                        //val newList: List<Counterpoint> = def.await()
                        mk5reductedCache[key] = newList

                        changeCounterpoints(newList, true)
                        _elaborating.value = false
                    }.also { time -> println("MK5reducted executed in $time ms") }
                }
            }
        }.also { jobQueue.add(it) }
    }
    private fun findCounterpointsByMikroKanons3(){
         viewModelScope.launch(Dispatchers.Main){
            if(sequenceToMikroKanons.value!!.isNotEmpty()) {
                val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList()
                val key = CacheKey(sequence, intervalSet.value!!)
                if(mk3cache.containsKey(key)) {
                    changeCounterpoints(mk3cache[key]!!, true)
                }else {
                    val newList: List<Counterpoint>
                    _elaborating.value = true
                    withContext(Dispatchers.Default) {
                        newList = mikroKanons3(sequenceToMikroKanons.value!!,intervalSet.value!!, 6)
                            .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }.take(maxVisibleCounterpoints)
                            .pmapIf(userOptionsData.value!![0].spread != 0){it.spreadAsPossible()}
                            .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                    }
                    mk3cache[key] = newList
                    _elaborating.value = false
                    changeCounterpoints(newList, true)
                }
            }
        }.also{  jobQueue.add(it)  }
    }

    private fun findCounterpointsByMikroKanons2(){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = mikroKanons2(sequenceToMikroKanons.value!!,intervalSet.value!!, 7)
                    .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }.take(maxVisibleCounterpoints)
                    .pmapIf(userOptionsData.value!![0].spread != 0){it.spreadAsPossible()}
                    .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
            }
            changeCounterpoints(newList, true)
        }
    }
    private fun flourishCounterpoints(originalCounterpoints: List<Counterpoint>, index: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = flourish(originalCounterpoints, intervalSet.value!!, intervalSetHorizontal.value!!.toList())
                }
                changeCounterpoints(newList, false)
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
                changeCounterpoints(newList, false)
                changeSelectedCounterpoint(counterpoints.value!![index])
            }
        }
    }
    private fun cadenzasOnCounterpoints(originalCounterpoints: List<Counterpoint>, index: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = addCadenzasOnCounterpoints(intervalSetHorizontal.value!!, originalCounterpoints)
                }
                changeCounterpoints(newList, false)
                changeSelectedCounterpoint(counterpoints.value!![index])
            }
        }
    }
    private fun duplicateAllPhrasesInCounterpoint(originalCounterpoint: Counterpoint,index: Int){
        if(!originalCounterpoint.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = duplicateAllInCounterpoint(originalCounterpoint)
                }
                changeCounterpoints(newList, false)
                changeSelectedCounterpoint(counterpoints.value!![index])
            }
        }
    }
    private fun eraseIntervalsOnCounterpoints(originalCounterpoints: List<Counterpoint>, index: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = eraseHorizontalIntervalsOnCounterpoints(intervalSetHorizontal.value!!, originalCounterpoints)
                }
                changeCounterpoints(newList, false)
                changeSelectedCounterpoint(counterpoints.value!![index])
            }
        }
    }
    private fun singleOnCounterpoints(originalCounterpoints: List<Counterpoint>, index: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = reduceCounterpointsToSinglePart(originalCounterpoints)
                }
                changeCounterpoints(newList, false)
                changeSelectedCounterpoint(counterpoints.value!![index])
            }
        }
    }
    private fun doppelgängerOnCounterpoints(originalCounterpoints: List<Counterpoint>, index: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            val ensTypes: List<EnsembleType> =
                userOptionsData.value?.let { userOptionsData.value!![0].ensembleTypes
                    .extractIntsFromCsv().map{EnsembleType.values()[it]}}
                    ?: listOf(EnsembleType.STRING_ORCHESTRA)
            val rangeType: Pair<Int,Int> =
                userOptionsData.value?.let { userOptionsData.value!![0].rangeTypes.extractIntPairsFromCsv()[0] }
                    ?: Pair(2,0)
            val melodyType: Int =
                userOptionsData.value?.let { userOptionsData.value!![0].melodyTypes.extractIntsFromCsv()[0] }
                    ?: 0
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = explodeCounterpointsToDoppelgänger(originalCounterpoints,
                        MAX_PARTS, ensTypes, rangeType, melodyType )
                }
                changeCounterpoints(newList, false)
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
                changeCounterpoints(newList, false)
                changeSelectedCounterpoint(counterpoints.value!![index])
            }
        }
    }
    private fun transposeOnCounterpoints(originalCounterpoints: List<Counterpoint>, transposition:Int, index: Int){
        if(!selectedCounterpoint.value!!.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                withContext(Dispatchers.Default){
                    newList = transposeAllCounterpoints(originalCounterpoints, transposition)
                }
                changeCounterpoints(newList, false)
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
                changeCounterpoints(newList, false)
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
                        .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }.take(maxVisibleCounterpoints)
                        .pmapIf(userOptionsData.value!![0].spread != 0){it.spreadAsPossible()}
                        .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                }
                changeCounterpoints(newList, true)
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
        changeCounterpoints(listOf(), false)
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
    fun changeCounterpoints(newCounterpoints: List<Counterpoint>, selectFirst: Boolean){
        lastIndex = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        _counterpoints.value = newCounterpoints
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

        allCounterpointsData.value!!.forEachIndexed { index, counterpoint ->
            val newCounterpoint = if (counterpoint.parts.isEmpty()) null
            else Counterpoint.createFromCsv(counterpoint.parts)
            savedCounterpoints[index] = newCounterpoint
        }
    }
    fun saveCounterpointInDb(position: Int, counterpoint: Counterpoint) {
        val counterpointData = CounterpointData(position.toLong()+1L, counterpoint.convertPartsToCsv())
        viewModelScope.launch(Dispatchers.IO) {
            counterpointRepository.updateCounterpoint(counterpointData)
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
        var newUserOptionsData: UserOptionsData? = null
        val optionsDataClone = if(userOptionsData.value!!.isEmpty())
                                UserOptionsData.getDefaultUserOptionsData()
                                else userOptionsData.value!![0].copy()
        when(key){
            "ensembleTypes" -> {
                newUserOptionsData = optionsDataClone.copy(ensembleTypes = value as String)
            }
            "rangeTypes" -> {
                newUserOptionsData = optionsDataClone.copy(rangeTypes = value as String)
            }
            "melodyTypes" -> {
                newUserOptionsData = optionsDataClone.copy(melodyTypes = value as String)
            }
            "glissandoFlags" -> {
                newUserOptionsData  = optionsDataClone.copy(glissandoFlags = value as Int)
            }
            "vibrato" -> {
                newUserOptionsData  = optionsDataClone.copy(vibrato = value as Int)
            }
            "dynamics" -> {
                newUserOptionsData = optionsDataClone.copy(dynamics = value as String)
            }
            "bpms" -> {
                newUserOptionsData = optionsDataClone.copy(bpms = value as String)
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
            "rowForms" -> {
                newUserOptionsData = optionsDataClone.copy(rowForms = value as String)
            }
//            "rowFormsFlags" -> {
//                var flags = value as Int
//                flags = if(flags and 0b10000 > 0 && flags and 0b1110 == 0) 1 else flags // deactivate separator if row forms are unactive
//                newUserOptionsData  = optionsDataClone.copy(rowFormsFlags = flags)
//            }
            "ritornello" -> {
                newUserOptionsData  = optionsDataClone.copy(ritornello = value as Int)
            }
            "transpose" -> {
                newUserOptionsData = optionsDataClone.copy(transpose = value as String)
            }
            "doublingFlags" -> {
                newUserOptionsData  = optionsDataClone.copy(doublingFlags = value as Int)
            }
            "audio8DFlags" -> {
                newUserOptionsData  = optionsDataClone.copy(audio8DFlags = value as Int)
            }
            "intSetVertFlags" -> {
                newUserOptionsData  = optionsDataClone.copy(intSetVertFlags = value as Int)
            }
            "intSetHorFlags" -> {
                newUserOptionsData  = optionsDataClone.copy(intSetHorFlags = value as Int)
            }
            "spread" -> {
                newUserOptionsData  = optionsDataClone.copy(spread = value as Int)
                clearMKcaches()
            }
            "deepSearch" -> {
                newUserOptionsData  = optionsDataClone.copy(deepSearch = value as Int)
                mk4cache.clear()
                mk4deepSearchCache.clear()
            }
            "detectorFlags" -> {
                newUserOptionsData  = optionsDataClone.copy(detectorFlags = value as Int)
            }
            "detectorExtension" -> {
                newUserOptionsData  = optionsDataClone.copy(detectorExtension = value as Int)
            }
            "colors" -> {
                newUserOptionsData  = optionsDataClone.copy(colors = value as String)
            }
            "language" -> {
                newUserOptionsData  = optionsDataClone.copy(language = value as String)
            }
            "zodiacFlags" -> {
                newUserOptionsData  = optionsDataClone.copy(zodiacFlags = value as Int)
            }
            "nuances" -> {
                newUserOptionsData  = optionsDataClone.copy(nuances = value as Int)
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
    var usingCustomColors: Boolean = false
    var appColors: AppColors = AppColors.allBlack()
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
    fun createVerticalIntervalSet(verticalIntervalSetFlag: Int) {
        _intervalSet.value = createIntervalSetFromFlags(verticalIntervalSetFlag)
    }
    fun saveVerticalIntervalSet() {
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






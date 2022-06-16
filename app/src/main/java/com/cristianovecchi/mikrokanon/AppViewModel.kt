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
import com.cristianovecchi.mikrokanon.locale.Lang
import com.cristianovecchi.mikrokanon.locale.getDynamicSymbols
import com.cristianovecchi.mikrokanon.midi.launchPlayer
import com.cristianovecchi.mikrokanon.ui.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
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
    data class Fioritura(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, override val icon: String = "fioritura"): Computation()
    data class Round(val counterpoints: List<Counterpoint>, val index: Int, override val icon: String = "round"): Computation()
    data class Cadenza(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val values: List<Int>, override val icon: String = "cadenza"): Computation()
    data class Sort(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val sortType: Int, override val icon: String = "sort_up"): Computation()
    data class UpsideDown(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val index: Int, override val icon: String = "upside_down"): Computation()
    data class Arpeggio(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, val index: Int, val arpeggioType: ARPEGGIO, override val icon: String = "arpeggio"): Computation()
    data class Scarlatti(val counterpoint: Counterpoint, val firstSequence: ArrayList<Clip>?, override val icon: String = "Scarlatti"): Computation()
    data class Overlap(val counterpoint1st: Counterpoint, val counterpoint2nd: Counterpoint, val firstSequence: ArrayList<Clip>?, override val icon: String = "overlap"): Computation()
    data class Crossover(val counterpoint1st: Counterpoint, val counterpoint2nd: Counterpoint, val firstSequence: ArrayList<Clip>?, override val icon: String = "crossover"): Computation()
    data class Glue(val counterpoint1st: Counterpoint, val counterpoint2nd: Counterpoint, val firstSequence: ArrayList<Clip>?, override val icon: String = "glue"): Computation()
    data class Maze(val intSequences: List<List<Int>>, override val icon: String = "maze"): Computation()
    data class EraseIntervals(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, override val icon: String = "erase"): Computation()
    data class Single(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, override val icon: String = "single"): Computation()
    data class Doppelgänger(val counterpoints: List<Counterpoint>, val firstSequence: ArrayList<Clip>?, override val icon: String = "doppelgänger"): Computation()
    data class Expand(val counterpoints: List<Counterpoint>, val index: Int, val extension: Int = 2, override val icon: String = "expand") : Computation()
    data class Transposition(val counterpoints: List<Counterpoint>, val transpositions: List<Pair<Int,Int>>, val index: Int, override val icon: String = "transpose") : Computation()
    data class TritoneSubstitution(val counterpoints: List<Counterpoint>, val intervalSet: List<Int>, val index: Int, override val icon: String = "tritone_substitution") : Computation()
}
data class ActiveButtons(val editing: Boolean = false, val mikrokanon: Boolean = false,
                         val undo: Boolean = false, val expand: Boolean = true,
                         val waves: Boolean = false, val pedals: Boolean = true,
                         val counterpoint: Boolean = false, val specialFunctions: Boolean = false,
                         val freeParts: Boolean = false, val playOrStop: Boolean = true)
enum class ScaffoldTabs { SOUND, BUILDING, ACCOMPANIST, IO, SETTINGS }

class AppViewModel(
    application: Application,
    private val sequenceRepository: SequenceDataRepository,
    private val counterpointRepository: CounterpointDataRepository,
    private val userRepository: UserOptionsDataRepository,
) : AndroidViewModel(application), LifecycleObserver {

    companion object{
        const val MAX_VISIBLE_COUNTERPOINTS = 42
        const val MAX_PITCHES_IN_CACHE = 60000
        const val MAX_PARTS = 12
        const val MAX_DEPTH_MK_3 = 5
        const val MAX_NOTES_MK_2= 200
        const val MAX_NOTES_MK_3 = 74
        const val MAX_NOTES_MK_4 = 32
        const val MAX_NOTES_MK_4DEEP = 18
        const val MAX_NOTES_MK_5RED = 25
        const val MAX_NOTES_MK_6RED = 20
        const val MAX_SEQUENCES_IN_MAZE = 10
        val MAX_NOTES_IN_MAZE = listOf(0, 99,99,99,99,99,99, 24,18, 12,10)
    }
    var privacyIsAccepted = true
    val iconMap = Icons.provideIcons()
    var _dimensions: MutableLiveData<Dimensions> = MutableLiveData(Dimensions.default())
    val dimensions: LiveData<Dimensions> = _dimensions
    var _language = MutableLiveData(Lang.provideLanguage(getSystemLangDef()))
    var language: LiveData<Lang> = _language
    val _lastScaffoldTab = MutableLiveData(ScaffoldTabs.SETTINGS)
    val lastScaffoldTab: LiveData<ScaffoldTabs> = _lastScaffoldTab
    // + 0.86f
    val dynamicSteps = listOf(0.000001f, 0.14f, 0.226f, 0.312f,  0.398f, 0.484f, 0.57f, 0.656f,  0.742f, 0.828f, 0.914f,1f )
    var cadenzaValues = "0,1,0,1,1"
    val dynamicMap: Map<Float,String> =  dynamicSteps.zip(getDynamicSymbols()).toMap()

    val stackIcons = mutableListOf<String>()
    private fun Stack<Computation>.pushAndDispatch(computation: Computation){
        push(computation)
        stackIcons.add(computation.icon)
        this@AppViewModel._stackSize.value = this.size
    }
    private fun Stack<Computation>.popAndDispatch(removeLastInStackIcons: Boolean = true){
        pop()
        if (removeLastInStackIcons && stackIcons.size > 1) stackIcons.removeLast()
        this@AppViewModel._stackSize.value = this.size
    }
    private fun Stack<Computation>.clearAndDispatch(){
        clear()
        stackIcons.clear()
        this@AppViewModel._stackSize.value = this.size
    }
    private var mediaPlayer: MediaPlayer? = null
    private var lastIndex = 0
    private val sequenceDataMap = HashMap<ArrayList<Clip>, SequenceData>(emptyMap())
    var spread = 0

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
    val allSequencesData: LiveData<List<SequenceData>>
    val allCounterpointsData: LiveData<List<CounterpointData>>
    val userOptionsData: LiveData<List<UserOptionsData>>

    private val computationStack = Stack<Computation>()

    val savedCounterpoints: Array<Counterpoint?> = Array(16) { null }
    private val _filledSlots = MutableLiveData(setOf<Int>())
    val filledSlots: LiveData<Set<Int>> = _filledSlots
    fun refreshFilledSlots(){
            val result = mutableListOf<Int>()
            savedCounterpoints.forEachIndexed { index, counterpoint ->  counterpoint?.let{ result.add(index)}}
            _filledSlots.value = result.toSet()
        }
    val midiPath: File = File(getApplication<MikroKanonApplication>().applicationContext.filesDir, "MK_lastPlay.mid")
//    val midiPath: File = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//        File(getApplication<MikroKanonApplication>().applicationContext.filesDir, "MKexecution.mid")
//     else {
//        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "MKexecution.mid")
//    }
    init{
        //RhythmPatterns.checkIntegrity()
        val size = getDeviceResolution()
        val displayMetricsDensity = Resources.getSystem().displayMetrics.density
        _dimensions.value = Dimensions.provideDimensions(size.x, size.y, displayMetricsDensity)
        allSequencesData = sequenceRepository.allSequences.asLiveData()
        allCounterpointsData = counterpointRepository.counterpoints.asLiveData()
        userOptionsData = userRepository.userOptions.asLiveData()
    }

    fun getContext(): Context {
        return getApplication<MikroKanonApplication>().applicationContext
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
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onLifeCycleStop() {
        onStop()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onLifeCyclePause() {
        onStop()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE) // for foldable devices
    fun onLifeCycleCreate() {
        val size = getDeviceResolution()
        val displayMetricsDensity = Resources.getSystem().displayMetrics.density
        _dimensions.value = Dimensions.provideDimensions(size.x, size.y, displayMetricsDensity)
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
        error.also { println("MIDI building ends with: $it") }
        error.toIntOrNull()?.let{
            val timestamp = System.currentTimeMillis()
            updateUserOptions("lastPlayData", "$it|$timestamp")
        }
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
            scrollToTopList = true
            val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
            val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
            computationStack.pushAndDispatch(Computation.TritoneSubstitution(originalCounterpoints, intervalSet.value!!.toList(),index))
            tritoneSubstitutionOnCounterpoints(originalCounterpoints, index)
        }
    }
    val onCadenzaFromSelector = { list: ArrayList<Clip>, values: List<Int> ->
        changeFirstSequence(list)
        convertFirstSequenceToSelectedCounterpoint()
        computationStack.pushAndDispatch(Computation.Cadenza(listOf(selectedCounterpoint.value!!.clone()), list, values))
        cadenzasOnCounterpoints(listOf(selectedCounterpoint.value!!.clone()),  values)
    }
    val onCadenza = { values: List<Int> ->
        scrollToTopList = true
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        computationStack.pushAndDispatch(Computation.Cadenza(originalCounterpoints, null, values))
        cadenzasOnCounterpoints(originalCounterpoints, values)
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
        scrollToTopList = true
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
        scrollToTopList = true
        val counterpoint2nd = savedCounterpoints[position]
        if (counterpoint2nd != null) {
            computationStack.pushAndDispatch(Computation.Glue(selectedCounterpoint.value!!.clone(),counterpoint2nd.clone(), null))
            glueBothCounterpoints(selectedCounterpoint.value!!.clone(), counterpoint2nd.clone())
        }
    }
    val onScarlattiFromSelector = { list: ArrayList<Clip> ->
        changeFirstSequence(list)
        convertFirstSequenceToSelectedCounterpoint()
        computationStack.pushAndDispatch(Computation.Scarlatti(selectedCounterpoint.value!!.clone(), list))
        duplicateAllPhrasesInCounterpoint(selectedCounterpoint.value!!.clone())
    }
    val onScarlatti = {
        scrollToTopList = true
        val originalCounterpoint = selectedCounterpoint.value!!.clone()
        computationStack.pushAndDispatch(Computation.Scarlatti(originalCounterpoint, null))
        duplicateAllPhrasesInCounterpoint(selectedCounterpoint.value!!.clone())
    }
    val onSortCounterpoints = { sortType: Int ->
        scrollToTopList = true
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        val iconString = if(sortType == 0) "sort_up" else "sort_down"
        computationStack.pushAndDispatch(Computation.Sort(originalCounterpoints, null, sortType, iconString ))
        sortAllCounterpoints(originalCounterpoints, sortType)
    }
    val onUpsideDown = {
        val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        computationStack.pushAndDispatch(Computation.UpsideDown(originalCounterpoints, null, index))
        upsideDownAllCounterpoints(originalCounterpoints,index)
    }
    val onArpeggio = { arpeggioType: ARPEGGIO ->
        scrollToTopList = true
        val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        if(originalCounterpoints[0].parts.size > 1){
            computationStack.pushAndDispatch(Computation.Arpeggio(originalCounterpoints, null, index, arpeggioType))
            arpeggioAllCounterpoints(originalCounterpoints,index, arpeggioType)
        }
    }
    val onEraseIntervals = {
        scrollToTopList = true
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        computationStack.pushAndDispatch(Computation.EraseIntervals(originalCounterpoints, null))
        eraseIntervalsOnCounterpoints(originalCounterpoints)
    }
    val onEraseIntervalsFromSelector = { list: ArrayList<Clip> ->
        changeFirstSequence(list)
        convertFirstSequenceToSelectedCounterpoint()
        computationStack.pushAndDispatch(Computation.EraseIntervals(listOf(selectedCounterpoint.value!!.clone()), list))
        eraseIntervalsOnCounterpoints(listOf(selectedCounterpoint.value!!.clone()))
    }
    val onSingleFromSelector = { list: ArrayList<Clip> ->
        changeFirstSequence(list)
        convertFirstSequenceToSelectedCounterpoint()
        computationStack.pushAndDispatch(Computation.Single(listOf(selectedCounterpoint.value!!.clone()), list))
        singleOnCounterpoints(listOf(selectedCounterpoint.value!!.clone()))
    }
    val onSingle= {
        scrollToTopList = true
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        computationStack.pushAndDispatch(Computation.Single(originalCounterpoints, null))
        singleOnCounterpoints(originalCounterpoints)
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
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        computationStack.pushAndDispatch(Computation.Doppelgänger(originalCounterpoints, null))
        doppelgängerOnCounterpoints(originalCounterpoints)
    }
    val onDoppelgängerFromSelector = { list: ArrayList<Clip> ->
        changeFirstSequence(list)
        convertFirstSequenceToSelectedCounterpoint()
        computationStack.pushAndDispatch(Computation.Doppelgänger(listOf(selectedCounterpoint.value!!.clone()), list))
        doppelgängerOnCounterpoints(listOf(selectedCounterpoint.value!!.clone()))
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
    val onExpand = {
        //scrollToTopList = !lastComputationIsExpansion()
        val lastComputation = computationStack.peek()
        val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        val originalCounterpoints = if(lastComputation is Computation.Expand) lastComputation.counterpoints
                                    else counterpoints.value!!.map{ it.clone() }
        val expansion = if(lastComputation is Computation.Expand) lastComputation.extension + 1 else 2
        computationStack.pushAndDispatch(Computation.Expand(originalCounterpoints, index, expansion))
        expandCounterpoints(originalCounterpoints, index, expansion)
    }
    val onKPfurtherSelections = {index: Int , repeat: Boolean->
        scrollToTopList = true
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
        changeCounterpointsWithLimitAndCache(listOf(retrievedCounterpoint),true)
    }
    val onWaveFromFirstSelection = { nWaves: Int, list: ArrayList<Clip> ->
        changeFirstSequence(list)
        computationStack.pushAndDispatch(Computation.FirstFromWave(listOf(selectedCounterpoint.value!!.clone()),
            ArrayList(firstSequence.value!!), nWaves))
        convertFirstSequenceToSelectedCounterpoint()
        findWavesFromSequence(nWaves)
    }
    val onWaveFurtherSelection = { nWaves: Int  ->
        scrollToTopList = true
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        computationStack.pushAndDispatch(Computation.FurtherFromWave(originalCounterpoints, nWaves))
        findWavesOnCounterpoints(originalCounterpoints, nWaves)
    }
    val onFlourishFromSelector = { list: ArrayList<Clip> ->
        changeFirstSequence(list)
        convertFirstSequenceToSelectedCounterpoint()
        computationStack.pushAndDispatch(Computation.Fioritura(listOf(selectedCounterpoint.value!!.clone()), list))
        flourishCounterpoints(listOf(selectedCounterpoint.value!!.clone()))
    }
    val onFlourish = {
        scrollToTopList = true
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        computationStack.pushAndDispatch(Computation.Fioritura(originalCounterpoints, null))
        flourishCounterpoints(originalCounterpoints)
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
        scrollToTopList = true
        computationStack.pushAndDispatch(Computation.FurtherFromFreePart(selectedCounterpoint.value!!.clone(),ArrayList(firstSequence.value!!), trend))
        findFreeParts(trend)
    }
    val onMikroKanons2 = {list: ArrayList<Clip> ->
        computationStack.pushAndDispatch(Computation.MikroKanonOnly(selectedCounterpoint.value!!.clone(),
            ArrayList(sequenceToMikroKanons.value!!),2))
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
                Computation.MikroKanonOnly(selectedCounterpoint.value!!.clone(),
                    ArrayList(sequenceToMikroKanons.value!!), 4))
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
    val onMikroKanons6reducted = {list: ArrayList<Clip> ->
        computationStack.pushAndDispatch(
            Computation.MikroKanonOnly(
                selectedCounterpoint.value!!.clone(),
                ArrayList(sequenceToMikroKanons.value!!), 6
            )
        )
        if (list.isNotEmpty()) changeSequenceToMikroKanons(list)
        findCounterpointsByMikroKanons6reducted()
    }
    val onMaze = {intSequences: List<List<Int>> ->
        scrollToTopList = true
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
    var scrollToTopList = true
    fun refreshComputation(stepBack: Boolean){
            if (!elaborating.value!! && computationStack.isNotEmpty()) {
                _elaborating.value = true
                val previousIntervalSet: List<Int>? = if (computationStack.lastElement() is Computation.TritoneSubstitution)
                    (computationStack.lastElement() as Computation.TritoneSubstitution).intervalSet
//                    else if (computationStack.lastElement() is Computation.Pedal)
//                    (computationStack.lastElement() as Computation.Pedal).intervalSet
                    else null
                if (stepBack ) computationStack.popAndDispatch()
                val previousComputation = when(computationStack.lastElement()){
                    is Computation.FirstFromLoading -> computationStack.lastElement()
                    is Computation.FurtherFromFreePart -> computationStack.lastElement()
                    is Computation.FurtherFromWave -> computationStack.lastElement()
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
                    is Computation.Arpeggio -> computationStack.lastElement()
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
                        if(stepBack){
                            changeSelectedCounterpoint(previousComputation.counterpoints[0])
                            changeCounterpointsWithLimitAndCache(previousComputation.counterpoints, true)
                        }
                    }
                    is Computation.FirstFromFreePart -> onFreePartFromFirstSelection(
                        previousComputation.firstSequence, previousComputation.trend
                    )
                    is Computation.FurtherFromFreePart -> {
                        scrollToTopList = !stepBack
                        changeSelectedCounterpoint(previousComputation.counterpoint)
                        findFreeParts(previousComputation.trend)
                    }
                    is Computation.FirstFromWave -> onWaveFromFirstSelection(
                        previousComputation.nWaves,
                        previousComputation.firstSequence
                    )
                    is Computation.FurtherFromWave -> {
                        scrollToTopList = true
                        findWavesOnCounterpoints(previousComputation.counterpoints, previousComputation.nWaves)
                    }
                    is Computation.Fioritura -> {
                        scrollToTopList = true
                        flourishCounterpoints(previousComputation.counterpoints)
                    }
                    is Computation.FirstFromKP -> onKPfromFirstSelection(
                        previousComputation.firstSequence,
                        previousComputation.indexSequenceToAdd,
                        previousComputation.repeat
                    )
                    is Computation.FurtherFromKP -> {
                        scrollToTopList = true
                        changeSelectedCounterpoint(previousComputation.counterpoint)
                        onKPfurtherSelections(previousComputation.indexSequenceToAdd,previousComputation.repeat)
                    }
                    is Computation.MikroKanonOnly -> {
                        when (previousComputation.nParts) {
                            2 -> onMikroKanons2(ArrayList(sequenceToMikroKanons.value!!))
                            3 -> onMikroKanons3(ArrayList(sequenceToMikroKanons.value!!))
                            4 -> onMikroKanons4(ArrayList(sequenceToMikroKanons.value!!))
                            5 -> onMikroKanons5reducted(ArrayList(sequenceToMikroKanons.value!!))
                            6 -> onMikroKanons6reducted(ArrayList(sequenceToMikroKanons.value!!))
                            else -> Unit
                        }
                    }
                    is Computation.Maze -> {
                        scrollToTopList = true
                        findMazes(previousComputation.intSequences)
                    }
                    is Computation.TritoneSubstitution -> {
                        if(stepBack){
                            tritoneSubstitutionOnCounterpoints(previousComputation.counterpoints, previousComputation.index)
                        }
                    }
                    is Computation.Doppelgänger -> {
                        if(stepBack){
                            doppelgängerOnCounterpoints( previousComputation.counterpoints)
                        }
                    }
                    is Computation.Round -> {
                        if(stepBack){
                            roundOnCounterpoints(previousComputation.counterpoints, previousComputation.index)
                        }
                    }
                    is Computation.Cadenza -> {
                            cadenzasOnCounterpoints( previousComputation.counterpoints, previousComputation.values)
                    }
                    is Computation.Sort -> {
                        if(stepBack){
                            sortAllCounterpoints( previousComputation.counterpoints,previousComputation.sortType)
                        }
                    }
                    is Computation.UpsideDown -> {
                        if(stepBack){
                            upsideDownAllCounterpoints( previousComputation.counterpoints,previousComputation.index)
                        }
                    }
                    is Computation.Arpeggio -> {
                        if(stepBack){
                            arpeggioAllCounterpoints( previousComputation.counterpoints,previousComputation.index, previousComputation.arpeggioType)
                        }
                    }
                    is Computation.Scarlatti -> {
                        if(stepBack){
                            duplicateAllPhrasesInCounterpoint( previousComputation.counterpoint)
                        }
                    }
                    is Computation.Overlap -> {
                        scrollToTopList = true
                        overlapBothCounterpoints( previousComputation.counterpoint1st, previousComputation.counterpoint2nd, false)
                    }
                    is Computation.Crossover -> {
                        scrollToTopList = true
                        overlapBothCounterpoints( previousComputation.counterpoint1st, previousComputation.counterpoint2nd, true)
                    }
                    is Computation.Glue -> {
                        if(stepBack){
                            glueBothCounterpoints( previousComputation.counterpoint1st, previousComputation.counterpoint2nd)
                        }
                    }
                    is Computation.EraseIntervals -> {
//                        if(stepBack || previousIntervalSet != intervalSetHorizontal.value) {
                        eraseIntervalsOnCounterpoints(previousComputation.counterpoints)
                    }
                    is Computation.Single -> {
                        if(stepBack){
                            singleOnCounterpoints( previousComputation.counterpoints)
                        }
                    }
                    is Computation.Transposition -> {
                        if(stepBack){
                            transposeOnCounterpoints( previousComputation.counterpoints, previousComputation.transpositions,previousComputation.index)
                        }
                    }
                    is Computation.Pedal -> {
                            scrollToTopList = true
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
            changeCounterpointsWithLimitAndCache(newList, true)
        }
    }
    private fun findWavesFromSequence(nWaves: Int){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                newList = waves(listOf(Counterpoint.counterpointFromClipList(firstSequence.value!!)), intervalSet.value!! ,intervalSetHorizontal.value!!, nWaves)
            }
            changeCounterpointsWithLimitAndCache(newList, true)
        }
    }
    fun findWavesOnCounterpoints(originalCounterpoints: List<Counterpoint>, nWaves: Int){
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

    private fun findFreeParts(trend: TREND){
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
    private val jobQueue = LinkedList<Job>()
    private fun cancelPreviousMKjobs() {
        if(jobQueue.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.Main) {
                while ( jobQueue.isNotEmpty()){
                    val job = jobQueue.poll()
                    job?.cancelAndJoin()
                }
            }
        }
    }
    private fun findCounterpointsByMikroKanons2(){
        var newList: List<Counterpoint>
        viewModelScope.launch(Dispatchers.Main){
            withContext(Dispatchers.Default){
                val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList().take(MAX_NOTES_MK_2)
                newList = mikroKanons2(sequence,intervalSet.value!!, 7)
                    .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }//.take(maxVisibleCounterpoints)
                    .pmapIf(spread != 0){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                    .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
            }
            changeCounterpointsWithLimitAndCache(newList, true, MAX_VISIBLE_COUNTERPOINTS * 2)
        }
    }
    private fun findCounterpointsByMikroKanons3(){
        viewModelScope.launch(Dispatchers.Main){
            if(sequenceToMikroKanons.value!!.isNotEmpty()) {
                val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList().take(MAX_NOTES_MK_3)
                val key = CacheKey(sequence, intervalSet.value!!)
                if(mk3cache.containsKey(key)) {
                    changeCounterpointsWithLimitAndCache(mk3cache[key]!!.first, true)
                }else {
                    val newList: List<Counterpoint>
                    _elaborating.value = true
                    withContext(Dispatchers.Default) {
                        newList = mikroKanons3(sequence,intervalSet.value!!, MAX_DEPTH_MK_3)
                            .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }//.take(maxVisibleCounterpoints)
                            .pmapIf(spread != 0){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                            .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                    }
                    changeCounterpointsWithLimitAndCache(newList, true, MAX_VISIBLE_COUNTERPOINTS * 2,
                            mk3cache, key)
                    _elaborating.value = false
                }
            }
        }.also{  jobQueue.add(it)  }
    }
    private fun findCounterpointsByMikroKanons4(){
        viewModelScope.launch(Dispatchers.Main){
            val deepSearch = userOptionsData.value!![0].deepSearch != 0
            if(sequenceToMikroKanons.value!!.isNotEmpty()) {
                val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList()
                    .take( if(deepSearch) MAX_NOTES_MK_4DEEP else MAX_NOTES_MK_4 )
                val key = CacheKey(sequence, intervalSet.value!!)
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
                                MAX_VISIBLE_COUNTERPOINTS
                            )
                                .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                                .pmapIf(spread != 0) { it.spreadAsPossible(intervalSet = intervalSet.value!!) }
                                .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                        }
                    //val newList: List<Counterpoint> = def.await()
                    changeCounterpointsWithLimitAndCache(newList, true, MAX_VISIBLE_COUNTERPOINTS,
                        if(deepSearch) mk4deepSearchCache else mk4cache)
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
                                MAX_VISIBLE_COUNTERPOINTS
                            )
                                .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                                .pmapIf(spread != 0) { it.spreadAsPossible(intervalSet = intervalSet.value!!) }
                                .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                        }
                        //val newList: List<Counterpoint> = def.await()
                        changeCounterpointsWithLimitAndCache(newList, true, MAX_VISIBLE_COUNTERPOINTS, mk5reductedCache, key)
                        _elaborating.value = false
                    }.also { time -> println("MK5reducted executed in $time ms") }
                }
            }
        }.also { jobQueue.add(it) }
    }
    private fun findCounterpointsByMikroKanons6reducted() {
        viewModelScope.launch(Dispatchers.Main) {
            if (sequenceToMikroKanons.value!!.isNotEmpty()) {
                val sequence = sequenceToMikroKanons.value!!.map { it.abstractNote }.toList().take(MAX_NOTES_MK_6RED)
                val key = CacheKey(sequence, intervalSet.value!!)
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
                                MAX_VISIBLE_COUNTERPOINTS
                            )
                                .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                                .pmapIf(spread != 0) { it.spreadAsPossible(intervalSet = intervalSet.value!!) }
                                .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                        }
                        //val newList: List<Counterpoint> = def.await()
                        changeCounterpointsWithLimitAndCache(newList, true, MAX_VISIBLE_COUNTERPOINTS, mk6reductedCache, key )
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
                    changeCounterpointsWithLimitAndCache(mazeCache[key]!!.first, true)
                } else {
                    measureTimeMillis {
                        _elaborating.value = true
                        // val def = async(Dispatchers.Default + MKjob) {
                        val maxNotesInMaze = MAX_NOTES_IN_MAZE[intSequences.size]
                        val newList = withContext(Dispatchers.Default) {
                            maze(this.coroutineContext.job, intSequences.map{it.take(maxNotesInMaze)}, intervalSet.value!!)
                                .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                                .pmapIf(spread != 0) { it.spreadAsPossible(intervalSet = intervalSet.value!!) }
                                .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                        }
                        changeCounterpointsWithLimitAndCache(newList, true, MAX_VISIBLE_COUNTERPOINTS, mazeCache, key)
                        println("Maze list size = ${newList.size}")
                        _elaborating.value = false
                    }.also { time -> println("Maze executed in $time ms ") }
                }
            }.also { jobQueue.add(it) }
    }
    private fun flourishCounterpoints(originalCounterpoints: List<Counterpoint>){
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
    private fun roundOnCounterpoints(originalCounterpoints: List<Counterpoint>, index: Int){
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
    private fun cadenzasOnCounterpoints(originalCounterpoints: List<Counterpoint>,values: List<Int>){
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
    private fun duplicateAllPhrasesInCounterpoint(originalCounterpoint: Counterpoint){
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
    private fun overlapBothCounterpoints(counterpoint1st: Counterpoint, counterpoint2nd: Counterpoint, crossover: Boolean){
        if(!counterpoint1st.isEmpty() && !counterpoint2nd.isEmpty()){
            var newList: List<Counterpoint>
            viewModelScope.launch(Dispatchers.Main){
                _elaborating.value = true
                withContext(Dispatchers.Default){
                    newList = overlapCounterpointsSortingByFaults(
                        this.coroutineContext.job,
                        counterpoint1st, counterpoint2nd, intervalSet.value!!, MAX_PARTS, crossover)
                    newList = if(spread != 0) newList.pmap{it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                        .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                             else newList
                }
                changeCounterpointsWithLimitAndCache(newList, true, MAX_VISIBLE_COUNTERPOINTS * 2)
                _elaborating.value = false
            }.also{  jobQueue.add(it)  }
        }
    }
    private fun glueBothCounterpoints(counterpoint1st: Counterpoint, counterpoint2nd: Counterpoint){
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
    private fun eraseIntervalsOnCounterpoints(originalCounterpoints: List<Counterpoint>){
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
    private fun sortAllCounterpoints(originalCounterpoints: List<Counterpoint>, sortType: Int){
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
    private fun upsideDownAllCounterpoints(originalCounterpoints: List<Counterpoint>,index: Int){
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
    private fun arpeggioAllCounterpoints(originalCounterpoints: List<Counterpoint>, index: Int, arpeggioType: ARPEGGIO){
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
    private fun singleOnCounterpoints(originalCounterpoints: List<Counterpoint>){
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
    private fun doppelgängerOnCounterpoints(originalCounterpoints: List<Counterpoint>){
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
                    newList = explodeCounterpointsToDoppelgänger(originalCounterpoints, MAX_PARTS)
                        .pmapIf(spread != 0){it.spreadAsPossible(intervalSet = intervalSet.value!!)}
                        .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                }
                changeCounterpointsWithLimitAndCache(newList, true)
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
                changeCounterpointsWithLimitAndCache(newList, false)
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
                changeCounterpointsWithLimitAndCache(newList, false)
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
                changeCounterpointsWithLimitAndCache(newList, false)
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
                        .pmapIf(spread != 0){
                            it.spreadAsPossible(true, intervalSet = intervalSet.value!!)}
                        //.map{ it.emptiness = it.findEmptiness(); it}
                        .sortedBy { it.emptiness }.distinctBy { it.getAbsPitches() }
                }
                changeCounterpointsWithLimitAndCache(newList, true)
            }
        }
    }

    // LIVEDATA SETTERS ---------------------------------------------------------------------
    private fun convertFirstSequenceToSelectedCounterpoint() {
        val newCounterpoint = Counterpoint.counterpointFromClipList(firstSequence.value!!)
        _selectedCounterpoint.value = newCounterpoint
    }
    fun setInitialBlankState() {
        cancelPreviousMKjobs()
        _elaborating.value = false
        onStop()
        computationStack.clearAndDispatch()
        changeCounterpointsWithLimitAndCache(listOf(), false)
        changeSequenceToMikroKanons(listOf())
        changeFirstSequence(listOf())
        changeSequenceToAdd(listOf())
        changeSelectedCounterpoint(Counterpoint.empty())
        changeSequenceSelection(-1)
    }
    fun changeActiveButtons(newActiveButtons: ActiveButtons){
        _activeButtons.value = newActiveButtons
    }
    //val selectedCounterpointStack = Stack<Counterpoint>()
    fun changeSelectedCounterpoint(newCounterpoint: Counterpoint){
        _selectedCounterpoint.value = newCounterpoint
    //    selectedCounterpointStack.push(newCounterpoint)

    }
    fun changeIntervalSet(newIntervalSet: List<Int>){
        val sortedList = newIntervalSet.sorted()
        _intervalSet.value = sortedList
    }
    fun changeCounterpointsWithLimitAndCache(newCounterpoints: List<Counterpoint>, selectFirst: Boolean,
                                     take: Int = MAX_VISIBLE_COUNTERPOINTS,
                                             cache: HashMap<CacheKey, Pair<List<Counterpoint>,Long>>? = null,
                                                key: CacheKey? = null){
       // println("new counterpoints found:${newCounterpoints.size}")
        val limitedCounterpoints = newCounterpoints.take(take)
       // println("new counterpoints accepted:${limitedCounterpoints.size}")

        lastIndex = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        cache?.insertAndClear(key!!, limitedCounterpoints, System.currentTimeMillis())
        // if new counterpoints are equal to the previous ones 'remember' doesn't refresh them in composables
//        val timestamp = System.currentTimeMillis()
//        val timestampedCounterpoints = newCounterpoints.take(take).mapIndexed{ index, it -> if(index == 0) it.copy(timestamp = timestamp) else it}
        if(_counterpoints.value == limitedCounterpoints && computationStack.size > 1){

            if(computationStack.peek() is Computation.Fioritura
                || computationStack.peek() is Computation.EraseIntervals
                || computationStack.peek() is Computation.Cadenza
                || computationStack.peek() is Computation.Pedal
                || computationStack.peek() is Computation.FurtherFromWave){
                _counterpoints.value = listOf()
                _counterpoints.value = limitedCounterpoints
            } else {
                computationStack.popAndDispatch()
            }
        } else {
            _counterpoints.value = limitedCounterpoints
        }
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

    // INTERVAL SETS ----------------------------------------------------------------------
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
    fun indexOfSelectedCounterpoint() : Int {
        counterpoints.value?.let{
            selectedCounterpoint.value?.let{
                return counterpoints.value!!.indexOf(selectedCounterpoint.value)
            }
        }
        return -1
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
        //savedCounterpoints[position] = counterpoint.copy(timestamp = timestamp)
        val counterpointData = CounterpointData(position.toLong()+1L, counterpoint.convertPartsToCsv(), timestamp)
        viewModelScope.launch(Dispatchers.IO) {
            counterpointRepository.updateCounterpoint(counterpointData)
        }
    }
    fun clearCounterpointsInDb(numbers: Set<Int>){
        val defaultCounterpoint = CounterpointData.getDefaultCounterpointData()
        numbers.forEach {
            //savedCounterpoints[it] = null
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
        UserOptionsData.updateUserOptionsData(optionsDataClone, key, value, 1).apply{
            viewModelScope.launch(Dispatchers.IO) {
                userRepository.updateUserOptions(this@apply)
//                if(userOptionsData.value!!.isNotEmpty()){
//                    userRepository.deleteAllUserOptions()
//                }
//                userRepository.insertUserOptions(this@apply)
            }
        }
    }
    // COLORS -------------------------------------------------------------------------------------
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
    val shiftColors = { shift: Int ->
        val colorDefs = extractColorDefs(userOptionsData.value!![0].colors)
        val colorIndex = (lastIndexCustomColors + shift).coerceIn(0, G.getArraySize() - 1)
        updateUserOptions("colors", "$colorIndex||${colorDefs.app}")
    }
    // LANGUAGE --------------------------------------------------------------------------
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

    // MK CACHES ----------------------------------------------------------------------------
    data class CacheKey(val sequence: List<Int>, val intervalSet: List<Int>)
    private val mk3cache = HashMap<CacheKey, Pair<List<Counterpoint>,Long >>()
    private val mk4cache = HashMap<CacheKey, Pair<List<Counterpoint>,Long >>()
    private val mk4deepSearchCache = HashMap<CacheKey, Pair<List<Counterpoint>,Long >>()
    private val mk5reductedCache = HashMap<CacheKey, Pair<List<Counterpoint>,Long >>()
    private val mk6reductedCache = HashMap<CacheKey, Pair<List<Counterpoint>,Long >>()
    private val mazeCache = HashMap<CacheKey, Pair<List<Counterpoint>,Long >>()
    private fun HashMap<CacheKey, Pair<List<Counterpoint>,Long>>.insertAndClear(key: CacheKey, counterpoints: List<Counterpoint>, timestamp: Long){
        val intAmount = this.values.map{it.first}.fold(0){ acc1, list -> acc1 + list.fold(0){acc2, counterpoint -> acc2 + counterpoint.countAbsPitches()} }
        val listAmount = counterpoints.fold(0){acc, counterpoint -> acc + counterpoint.countAbsPitches()}
//        println("key: ${key.sequence} ${key.intervalSet}")
//        println("Cache size: $intAmount, new list size: $listAmount, limit: $MAX_PITCHES_IN_CACHE")
        if(this.isNotEmpty()){
            if(intAmount + listAmount > MAX_PITCHES_IN_CACHE) {
                remove(keys.sortedBy { this[it]?.second }[0])
            }
        }
        this[key] = Pair(counterpoints, timestamp)
        println()
    }
    fun clearMKcaches() {
        mk3cache.clear()
        mk4cache.clear()
        mk4deepSearchCache.clear()
        mk5reductedCache.clear()
        mk6reductedCache.clear()
    }
}






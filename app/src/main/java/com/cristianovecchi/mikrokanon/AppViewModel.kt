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

data class ActiveButtons(val editing: Boolean = false, val mikrokanon: Boolean = false,
                         val undo: Boolean = false, val expand: Boolean = true,
                         val waves: Boolean = false, val pedals: Boolean = true,
                         val counterpoint: Boolean = false,
                         val specialFunctions: Boolean = false, val specialFunctions1: Boolean = false,
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

    var _elaborating = MutableLiveData(false)
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
    val onEWH = { list: ArrayList<Clip>, nParts: Int ->
        val originalCounterpoints = if(list.isNotEmpty()) {
            changeFirstSequence(list)
            convertFirstSequenceToSelectedCounterpoint()
            listOf( selectedCounterpoint.value!!.clone())
        } else {
            counterpoints.value!!.map{ it.clone() }
        }
        computationStack.pushAndDispatch(Computation.ExtendedWeightedHarmony(originalCounterpoints, nParts))
        extendedWeightedHarmonyOnCounterpoints(originalCounterpoints, nParts)
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
            val previousIntervalSet = intervalSet.value!!
            computationStack.pushAndDispatch(Computation.TritoneSubstitution(originalCounterpoints, intervalSet.value!!.toList(),index))
            tritoneSubstitutionOnCounterpoints(originalCounterpoints, index, previousIntervalSet)
        }
    }
    val onCadenzaFromSelector = { list: ArrayList<Clip>, values: List<Int> ->
        changeFirstSequence(list)
        convertFirstSequenceToSelectedCounterpoint()
        computationStack.pushAndDispatch(Computation.Cadenza(listOf(selectedCounterpoint.value!!.clone()), list, values))
        cadenzasOnCounterpoints(listOf(selectedCounterpoint.value!!.clone()),  values)
    }
    val onCadenza = { values: List<Int> ->
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        computationStack.pushAndDispatch(Computation.Cadenza(originalCounterpoints, null, values))
        cadenzasOnCounterpoints(originalCounterpoints, values)
    }
    val onOverlapFromSelector = { list: ArrayList<Clip>, position: Int, crossover: Boolean ->
        if(position == -1 || savedCounterpoints[position] != null){
            changeFirstSequence(list)
            convertFirstSequenceToSelectedCounterpoint()
            val counterpoint2nd = if(position == -1) selectedCounterpoint.value!! else savedCounterpoints[position]
            val computation = if(crossover) Computation.Crossover(selectedCounterpoint.value!!.clone(), counterpoint2nd!!.clone(),list)
                else Computation.Overlap(selectedCounterpoint.value!!.clone(), counterpoint2nd!!.clone(),list)
            computationStack.pushAndDispatch(computation)
            overlapBothCounterpoints(selectedCounterpoint.value!!.clone(), counterpoint2nd!!.clone(), crossover)
        }
    }
    val onOverlap = { position: Int , crossover: Boolean->
        val counterpoint2nd = if(position == -1) selectedCounterpoint.value!! else savedCounterpoints[position]
        if (counterpoint2nd != null ) {
            val computation = if(crossover) Computation.Crossover(selectedCounterpoint.value!!.clone(),counterpoint2nd.clone(), null)
                else Computation.Overlap(selectedCounterpoint.value!!.clone(),counterpoint2nd.clone(), null)
            computationStack.pushAndDispatch(computation)
            overlapBothCounterpoints(selectedCounterpoint.value!!.clone(), counterpoint2nd.clone(), crossover)
        }
    }
    val onGlueFromSelector = { list: ArrayList<Clip>, position: Int ->
        if(position == -1 ||  savedCounterpoints[position] != null){
            changeFirstSequence(list)
            convertFirstSequenceToSelectedCounterpoint()
            val counterpoint2nd = if(position == -1) selectedCounterpoint.value!! else savedCounterpoints[position]
            computationStack.pushAndDispatch(Computation.Glue(selectedCounterpoint.value!!.clone(), counterpoint2nd!!.clone(),list))
            glueBothCounterpoints(selectedCounterpoint.value!!.clone(), counterpoint2nd.clone())
        }
    }
    val onGlue= { position: Int ->
        val counterpoint2nd = if(position == -1) selectedCounterpoint.value!! else savedCounterpoints[position]
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
        val originalCounterpoint = selectedCounterpoint.value!!.clone()
        computationStack.pushAndDispatch(Computation.Scarlatti(originalCounterpoint, null))
        duplicateAllPhrasesInCounterpoint(selectedCounterpoint.value!!.clone())
    }
    val onSortCounterpoints = { sortType: Int ->
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
        val index = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        if(originalCounterpoints[0].parts.size > 1){
            computationStack.pushAndDispatch(Computation.Arpeggio(originalCounterpoints, null, index, arpeggioType))
            arpeggioAllCounterpoints(originalCounterpoints,index, arpeggioType)
        }
    }
    val onEraseIntervals = {
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
    val onDoppelg??nger= {
        val originalCounterpoints = counterpoints.value!!.map{ it.clone() }
        computationStack.pushAndDispatch(Computation.Doppelg??nger(originalCounterpoints, null))
        doppelg??ngerOnCounterpoints(originalCounterpoints)
    }
    val onDoppelg??ngerFromSelector = { list: ArrayList<Clip> ->
        changeFirstSequence(list)
        convertFirstSequenceToSelectedCounterpoint()
        computationStack.pushAndDispatch(Computation.Doppelg??nger(listOf(selectedCounterpoint.value!!.clone()), list))
        doppelg??ngerOnCounterpoints(listOf(selectedCounterpoint.value!!.clone()))
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
                    is Computation.Doppelg??nger -> computationStack.lastElement()
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
                    is Computation.ExtendedWeightedHarmony -> computationStack.lastElement()
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
                        changeSelectedCounterpoint(previousComputation.counterpoint)
                        findFreeParts(previousComputation.trend)
                    }
                    is Computation.FirstFromWave -> onWaveFromFirstSelection(
                        previousComputation.nWaves,
                        previousComputation.firstSequence
                    )
                    is Computation.FurtherFromWave -> {
                        findWavesOnCounterpoints(previousComputation.counterpoints, previousComputation.nWaves)
                    }
                    is Computation.Fioritura -> {
                        flourishCounterpoints(previousComputation.counterpoints)
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
                        findMazes(previousComputation.intSequences)
                    }
                    is Computation.TritoneSubstitution -> {
                        if(stepBack){
                            tritoneSubstitutionOnCounterpoints(previousComputation.counterpoints, previousComputation.index, previousComputation.intervalSet)
                        }
                    }
                    is Computation.Doppelg??nger -> {
                        if(stepBack){
                            doppelg??ngerOnCounterpoints( previousComputation.counterpoints)
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
                    is Computation.ExtendedWeightedHarmony -> {
                        if(stepBack){
                            extendedWeightedHarmonyOnCounterpoints( previousComputation.counterpoints, previousComputation.nParts)
                        }
                    }
                    is Computation.Overlap -> {
                        overlapBothCounterpoints( previousComputation.counterpoint1st, previousComputation.counterpoint2nd, false)
                    }
                    is Computation.Crossover -> {
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
    val jobQueue = LinkedList<Job>()
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
                                     cacheAndKey: Pair<HashMap<CacheKey, Pair<List<Counterpoint>,Long>>, CacheKey>? = null ){
       // println("new counterpoints found:${newCounterpoints.size}")
        val limitedCounterpoints = newCounterpoints.take(take)
       // println("new counterpoints accepted:${limitedCounterpoints.size}")

        lastIndex = counterpoints.value!!.indexOf(selectedCounterpoint.value!!)
        cacheAndKey?.let {
            cacheAndKey.first.insertAndClear(cacheAndKey.second, limitedCounterpoints, System.currentTimeMillis())
        }
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
                scrollToTopList = true
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
    val mk3cache = HashMap<CacheKey, Pair<List<Counterpoint>,Long >>()
    val mk4cache = HashMap<CacheKey, Pair<List<Counterpoint>,Long >>()
    val mk4deepSearchCache = HashMap<CacheKey, Pair<List<Counterpoint>,Long >>()
    val mk5reductedCache = HashMap<CacheKey, Pair<List<Counterpoint>,Long >>()
    val mk6reductedCache = HashMap<CacheKey, Pair<List<Counterpoint>,Long >>()
    val mazeCache = HashMap<CacheKey, Pair<List<Counterpoint>,Long >>()
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






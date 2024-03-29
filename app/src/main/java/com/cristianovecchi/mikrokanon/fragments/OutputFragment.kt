package com.cristianovecchi.mikrokanon.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.asFlow
import androidx.navigation.findNavController
import com.cristianovecchi.mikrokanon.AppViewModel
import com.cristianovecchi.mikrokanon.MainActivity
import com.cristianovecchi.mikrokanon.R
import com.cristianovecchi.mikrokanon.composables.AppScaffold
import com.cristianovecchi.mikrokanon.composables.ResultDisplay
import com.cristianovecchi.mikrokanon.createIntervalSetFromFlags
import com.cristianovecchi.mikrokanon.locale.Lang
import com.cristianovecchi.mikrokanon.ui.MikroKanonTheme

class OutputFragment: Fragment() {
    lateinit var model: AppViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        model = (activity as MainActivity).model
        model.selectedCounterpoint.observe(viewLifecycleOwner){
            if(model.selectedCounterpoint.value!!.parts.isNotEmpty()) {
                model.changeActiveButtons( if(model.selectedCounterpoint.value!!.parts.size >= 12)
                    model.activeButtons.value!!.copy(counterpoint = false, freeParts = false, waves = false, pedals = false, specialFunctions1 = false)
                else model.activeButtons.value!!.copy(counterpoint = true, freeParts = true, waves = true, pedals = true, specialFunctions1 = true)
                )
            }
        }
        model.allCounterpointsData.observe(viewLifecycleOwner){
            model.retrieveCounterpointsFromDB()
            model.refreshFilledSlots()
        }
        model.buildingState.observe(viewLifecycleOwner){
            //println("Building phase: $it")
        }
//        if(model.userOptionsData.value != null && model.userOptionsData.value!!.isNotEmpty()){
//            val verticalIntervalSetFlag = model.userOptionsData.value!![0].intSetVertFlags
//            val horizontalIntervalSetFlag = model.userOptionsData.value!![0].intSetHorFlags
//            model.createVerticalIntervalSet(verticalIntervalSetFlag, "OutputFragment")
//            model.createHorizontalIntervalSet(horizontalIntervalSetFlag)
//        }
        model.userOptionsData.observe(viewLifecycleOwner){
            model.userOptionsData.value.let {
                if(it!!.isNotEmpty()) {
                    model._counterpointView.value = it[0].counterpointView
                    model.refreshZodiacFlags()
                    model._language.value = Lang.provideLanguage(model.getUserLangDef())
                    model.spread = it[0].spread.toInt()
                }
            }
        }

        model.stackSize.observe(viewLifecycleOwner){
            model.changeActiveButtons(  if(model.stackSize.value!! <= 1)
                model.activeButtons.value!!.copy(undo = false)
            else model.activeButtons.value!!.copy(undo = true)
            )
        }

        return ComposeView(requireContext()).apply {
            setContent {
                MikroKanonTheme(model) {
                    Surface(color = MaterialTheme.colors.background) {
                       AppScaffold(model = model, model.dimensions.asFlow(), model.userOptionsData.asFlow(), model.allCounterpointsData.asFlow()) {
                            ResultDisplay(
                                model = model,
                                model.dimensions.asFlow(),
                                model.iconMap,
                                model.selectedCounterpoint.asFlow(),
                                model.counterpoints.asFlow(),
                                model.elaborating.asFlow(),
                                onClick = { counterpoint ->
                                    if(counterpoint == model.selectedCounterpoint.value!!){
                                        model.onPlay(true, true) // play without build the entire structure
                                    } else {
                                        model.changeSelectedCounterpoint(
                                            counterpoint
                                        )
                                    }
                                },
                                onSort = { sortType ->
                                    model.onSortCounterpoints(sortType)
                                },
                                onUpsideDown = {
                                    model.onUpsideDown()
                                },
                                onArpeggio = { arpeggioType ->
                                    model.onArpeggio(arpeggioType)
                                },
                                onSavingCounterpoint= { position ->
                                    model.selectedCounterpoint.value?.let{
                                        model.saveCounterpointInDb(position, it.copy())
                                        model.retrieveCounterpointsFromDB()
                                    }
                                },
                                onKP = { index, repeat ->
                                    model.onKPfurtherSelections(index, repeat)
                                },
                                onTranspose = { transposition ->
                                    model.onSimpleTransposition(transposition)
                                },
                                onWave = { nWaves ->
                                    model.onWaveFurtherSelection(nWaves)
                                },
                                onQuote = { genres, repeat ->
                                    model.onQuoteFurtherSelections(genres, repeat)
                                },
                                onTritoneSubstitution = {
                                    model.onTritoneSubstitution()
                                },
                                onRound = { transpositions ->
                                    model.onRound(transpositions)
                                },
                                onCadenza = { values ->
                                    model.onCadenza(values)
                                },
                                onScarlatti = {
                                    model.onScarlatti()
                                },
                                onOverlap = { index, crossover ->
                                    model.onOverlap(index, crossover)
                                },
                                onGlue = { index ->
                                    model.onGlue(index)
                                },
                                onMaze = { intSequences ->
                                    model.onMaze(intSequences)
                                },
                                onEraseIntervals = {
                                    model.onEraseIntervals()
                                },
                                onSingle = {
                                    model.onSingle()
                                },
                                onDoppelgänger = {
                                    model.onDoppelgänger()
                                },
                                onPedal = { nPedals ->
                                    model.onPedal(nPedals)
                                },
                                onBack = { model.onBack() },
                                onFreePart = { trend ->
                                    model.onFreePartFurtherSelections(trend)
                                },
                                onExpand = { model.onExpand() },
                                onFlourish = { model.onFlourish() },
                                onEWH = { nParts -> model.onEWH(arrayListOf(), nParts)},
                                progressiveEWH = { model.progressiveEWH() },
                                onResolutio = { resolutioData ->
                                    model.onResolutio(null, resolutioData)
                                },
                                onDoubling = { transposition ->
                                    model.onDoubling(null, transposition)
                                },
                                onChess = { range ->
                                    model.onChess(null, range)
                                },
                                onFormat = { values ->
                                    model.onFormat(values)
                                },
                                onParade = {
                                    model.onParade()
                                },
                                onPlay = {
                                    //val executionTime = measureTimeMillis {
                                    model.onPlay(true, false) // play the entire structure
                                    //}.also{ println("MIDI file build in $it ms") }
                                },
                                onStop = { model.onStop() }
                            )
                        }
                    }
                }
            }
        }
    }


//    override fun onDestroyView() {
//       // println("OutputFragment view destroyed.")
////        model.createVerticalIntervalSet(model.intervalSet.value!!, "Destroy OutputFragment")
//       model.saveVerticalIntervalSet("Destroy view OutputFragment")
//        super.onDestroyView()
//    }
}
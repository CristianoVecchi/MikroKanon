package com.cristianovecchi.mikrokanon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.asFlow
import com.cristianovecchi.mikrokanon.composables.*
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
                    model.activeButtons.value!!.copy(counterpoint = false, freeparts = false, waves = false, pedals = false)
                else model.activeButtons.value!!.copy(counterpoint = true, freeparts = true, waves = true, pedals = true)
                )
            }
        }

        if(model.userOptionsData.value != null && model.userOptionsData.value!!.isNotEmpty()){
            val verticalIntervalSetFlag = model.userOptionsData.value!![0].intSetVertFlags
            model.createVerticalIntervalSet(verticalIntervalSetFlag)
        }
        model.userOptionsData.observe(viewLifecycleOwner){
            model.userOptionsData.value.let {
                if(it!!.isNotEmpty()) {
                    val newIntervalSet = createIntervalSetFromFlags(it[0].intSetHorFlags)
                    if(!newIntervalSet.equals(model.intervalSetHorizontal.value!!)){
                        model.createHorizontalIntervalSet(it[0].intSetHorFlags)
                        model.dispatchIntervals()
                    }
                    model.setAppColors(it[0].colors)
                }
            }
        }
//        println("vert: ${model.intervalSet.value!!}")
//        println("hor: ${model.intervalSetHorizontal.value!!}")

        model.stackSize.observe(viewLifecycleOwner){
            model.changeActiveButtons(  if(model.stackSize.value!! <= 1)
                model.activeButtons.value!!.copy(undo = false)
            else model.activeButtons.value!!.copy(undo = true )
            )
        }

        return ComposeView(requireContext()).apply {
            setContent {
                MikroKanonTheme(model) {
                    Surface(color = MaterialTheme.colors.background) {
                       AppScaffold(model = model, model.userOptionsData.asFlow()) {
                            ResultDisplay(
                                model = model,
                                model.iconMap,
                                onClick = { counterpoint ->
                                    if(counterpoint == model.selectedCounterpoint.value!!){
                                        model.onPlay(true)
                                    } else {
                                        model.changeSelectedCounterpoint(
                                            counterpoint
                                        )
                                    }
                                },
                                onKP = { index, repeat ->
                                    model.onKPfurtherSelections(index, repeat)
                                },
                                onWave = { nWaves ->
                                    model.onWaveFurtherSelection(nWaves, null)
                                },
                                onTritoneSubstitution = {
                                    model.onTritoneSubstitution()
                                },
                                onRound = {
                                    model.onRound()
                                },
                                onCadenza = {
                                    model.onCadenza()
                                },
                                onSingle = {
                                    model.onSingle()
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
                                onPlay = {
                                    model.onPlay(true)
                                },
                                onStop = { model.onStop() }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        model.saveVerticalIntervalSet()
    }
}
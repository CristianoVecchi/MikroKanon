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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val model = (activity as MainActivity).model
//        model.userOptionsData.observe(viewLifecycleOwner){
//            //model.selectLanguage(model.getUserLangDef())
//        }
        model.selectedCounterpoint.observe(viewLifecycleOwner){
            if(model.selectedCounterpoint.value!!.parts.isNotEmpty()) {
                model.changeActiveButtons( if(model.selectedCounterpoint.value!!.parts.size >= 12)
                    model.activeButtons.value!!.copy(counterpoint = false, freeparts = false)
                else model.activeButtons.value!!.copy(counterpoint = true, freeparts = true)
                )
            }
        }
        model.stackSize.observe(viewLifecycleOwner){
            model.changeActiveButtons(  if(model.stackSize.value!! <= 1)
                model.activeButtons.value!!.copy(undo = false)
            else model.activeButtons.value!!.copy(undo = true )
            )
        }

        return ComposeView(requireContext()).apply {
            setContent {
                MikroKanonTheme {
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
                                onBack = { model.onBack() },
                                onFreePart = { trend ->
                                    model.onFreePartFurtherSelections(trend)
                                },
                                onExpand = { model.onExpand() },
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
}
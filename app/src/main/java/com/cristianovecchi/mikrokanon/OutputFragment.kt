package com.cristianovecchi.mikrokanon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.cristianovecchi.mikrokanon.composables.*
import com.cristianovecchi.mikrokanon.ui.MikroKanonTheme

class OutputFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val model = (activity as MainActivity).model
        model.userOptionsData.observe(viewLifecycleOwner){
            model.selectNotesNames()
        }
        return ComposeView(requireContext()).apply {
            setContent {
                MikroKanonTheme {
                    Surface(color = MaterialTheme.colors.background) {
                       AppScaffold(model = model) {
                            ResultDisplay(
                                model = model,
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
                                onPlay = { model.onPlay(true) }
                            )
                        }
                    }
                }
            }
        }
    }
}
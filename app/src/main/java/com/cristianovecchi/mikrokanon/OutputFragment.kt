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

//    private var list: List<Clip> = emptyList()
//    private var sequenceToAdd: List<Clip> = emptyList()

    //private var mikrokanons: MutableList<MikroKanon> = mutableListOf<MikroKanon>()


    

 //   override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.getParcelableArrayList<Clip>("list")?.let {
//            this.list = it
//        }
//        arguments?.getParcelableArrayList<Clip>("sequenceToAdd")?.let {
//            this.sequenceToAdd = it
//        }
        
 //   }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //val model = ViewModelProvider(requireActivity()).get(AppViewModel::class.java)
        val model = (activity as MainActivity).model
        return ComposeView(requireContext()).apply {
            setContent {
                MikroKanonTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(color = MaterialTheme.colors.background) {
                      //  val absPitches = list.map{it.abstractNote}.toList()
                        ResultDisplay(
                                model = model,
                                onClick = {counterpoint -> model.changeSelectedCounterpoint(counterpoint) },
                                onKP = { index, repeat->
                                   model.onKPfurtherSelections(index, repeat)
                                },
                                onBack = { model.onBack()},
                                onFreePart= { trend ->
                                    model.onFreePartFurtherSelections(trend)
                                },
                                onExpand = { model.onExpand()},
                                dispatchIntervals = { newIntervals ->
                                    model.dispatchIntervals(newIntervals)
                                }
                        )
                    }
            }
        }
            }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
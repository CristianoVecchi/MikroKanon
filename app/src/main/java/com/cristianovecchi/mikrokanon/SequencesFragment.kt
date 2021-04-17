package com.cristianovecchi.mikrokanon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.platform.ComposeView

import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.cristianovecchi.mikrokanon.composables.SequenceSelector
import com.cristianovecchi.mikrokanon.ui.MikroKanonTheme

class SequencesFragment(): Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       // val model = ViewModelProvider(requireActivity()).get(AppViewModel::class.java)
        val model = (activity as MainActivity).model
        model.allSequencesData.observe(viewLifecycleOwner){
            model.retrieveSequencesFromDB()
        }
        model.setInitialBlankState()
        return ComposeView(requireContext()).apply {
            setContent {
                MikroKanonTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(color = MaterialTheme.colors.background) {
                        SequenceSelector( model = model,
                            onAdd = {list, editing ->
                            val bundle = Bundle()
                            bundle.putParcelableArrayList("list", list)
                            bundle.putBoolean("editing", editing)
                            findNavController().navigate(R.id.inputFragment, bundle)
                            },
                            onKP = { list, index ->
                                findNavController().navigate(R.id.outputFragment)
                                model.onKPfromFirstSelection(list, index)

                            },
                            onFreePart= { list ->
                                // val bundle = Bundle()
                                //bundle.putParcelableArrayList("list", list)
                                findNavController().navigate(R.id.outputFragment)
                                model.onFreePartFromFirstSelection(list)
                            },
                            onMikrokanons = { list ->
                               // val bundle = Bundle()
                               //bundle.putParcelableArrayList("list", list)
                                findNavController().navigate(R.id.outputFragment)
                                model.onMikroKanons(list)
                            },
                            onMikrokanons3 = { list ->
                                findNavController().navigate(R.id.outputFragment)
                                model.onMikroKanons3(list)
                            })
                    }
                }
            }
        }
    }
}
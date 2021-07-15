package com.cristianovecchi.mikrokanon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.*
import androidx.compose.ui.platform.ComposeView

import androidx.fragment.app.Fragment
import androidx.lifecycle.asFlow
import androidx.navigation.findNavController
import com.cristianovecchi.mikrokanon.composables.AppScaffold
import com.cristianovecchi.mikrokanon.composables.SequenceSelector
import com.cristianovecchi.mikrokanon.ui.MikroKanonTheme

class SequencesFragment(): Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       // val model = ViewModelProvider(requireActivity()).get(AppViewModel::class.java)
        val model = (activity as MainActivity).model
        model.allSequencesData.observe(viewLifecycleOwner){
            model.retrieveSequencesFromDB()
        }
        model.userOptionsData.observe(viewLifecycleOwner){
            model.userOptionsData.value.let {
                if(it!!.isNotEmpty()) model.createHorizontalIntervalSet(it[0].intSetHorFlags)
            }
        }
        model.selectedSequence.observe(viewLifecycleOwner){
            model.changeActiveButtons( if(model.selectedSequence.value!! != -1 )
                ActiveButtons(editing = true, mikrokanon = true, counterpoint = true, freeparts = true)
            else ActiveButtons() )
        }
//        model.userOptionsData.observe(viewLifecycleOwner){
//            //model.selectLanguage(model.getUserLangDef())
//        }
        model.setInitialBlankState()
        return ComposeView(requireContext()).apply {
            setContent {
                MikroKanonTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(color = MaterialTheme.colors.background) {

                        AppScaffold(model = model, model.userOptionsData.asFlow()) {
                               SequenceSelector(model = model,
                                   onAdd = { list, editing ->
                                       val bundle = Bundle()
                                       bundle.putParcelableArrayList("list", list)
                                       bundle.putBoolean("editing", editing)
                                       findNavController().navigate(R.id.inputFragment, bundle)
                                   },
                                   onWave = { nWaves, list ->
                                       findNavController().navigate(R.id.outputFragment)
                                       model.onWaveFromFirstSelection(nWaves, list)
                                   },
                                   onTritoneSubstitution = { index ->
                                       model.onTritoneSubstitutionFromSelector(index)
                                   },
                                   onKP = { list, index, repeat ->
                                       findNavController().navigate(R.id.outputFragment)
                                       model.onKPfromFirstSelection(list, index, repeat)
                                   },
                                   onFreePart = { list, trend ->
                                       findNavController().navigate(R.id.outputFragment)
                                       model.onFreePartFromFirstSelection(list, trend)
                                   },
                                   onMikroKanons2 = { list ->
                                       findNavController().navigate(R.id.outputFragment)
                                       model.onMikroKanons2(list)
                                   },
                                   onMikroKanons3 = { list ->
                                       findNavController().navigate(R.id.outputFragment)
                                       model.onMikroKanons3(list)
                                   },
                                   onMikroKanons4 = { list ->
                                       findNavController().navigate(R.id.outputFragment)
                                       model.onMikroKanons4(list)
                                   }
                               )
                           }
                    }
                }
            }
        }
    }
}
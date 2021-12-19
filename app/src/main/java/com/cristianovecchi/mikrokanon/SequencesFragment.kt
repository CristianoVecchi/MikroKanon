package com.cristianovecchi.mikrokanon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView

import androidx.fragment.app.Fragment
import androidx.lifecycle.asFlow
import androidx.navigation.findNavController
import com.cristianovecchi.mikrokanon.composables.AppScaffold
import com.cristianovecchi.mikrokanon.composables.SequenceSelector
import com.cristianovecchi.mikrokanon.ui.MikroKanonTheme

class SequencesFragment(): Fragment() {
var start = true
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
        model.allCounterpointsData.observe(viewLifecycleOwner){
            model.retrieveCounterpointsFromDB()
            //model.displaySavedCounterpoints()
        }
        model.userOptionsData.observe(viewLifecycleOwner){
            model.userOptionsData.value.let {
                if(it!!.isNotEmpty()) {
                    model.createHorizontalIntervalSet(it[0].intSetHorFlags)
                    model.setAppColors(it[0].colors)
                    model.refreshZodiacFlags()
                    if(start){
                        val verticalIntervalSetFlag = model.userOptionsData.value!![0].intSetVertFlags
                        model.createVerticalIntervalSet(verticalIntervalSetFlag, "SequencesFragment")
                        start = false
                    }

                }
            }
        }

        model.selectedSequence.observe(viewLifecycleOwner){
            model.changeActiveButtons( if(model.selectedSequence.value!! != -1 )
                ActiveButtons(editing = true, mikrokanon = true, counterpoint = true,
                    specialFunctions = true, freeparts = true, waves = true, pedals = true)
            else ActiveButtons() )
        }
//        model.userOptionsData.observe(viewLifecycleOwner){
//            //model.selectLanguage(model.getUserLangDef())
//        }
        model.setInitialBlankState()
//        if(model.userOptionsData.value != null && model.userOptionsData.value!!.isNotEmpty()){
//            val verticalIntervalSetFlag = model.userOptionsData.value!![0].intSetVertFlags
//            model.createVerticalIntervalSet(verticalIntervalSetFlag)
//        }
        return ComposeView(requireContext()).apply {
            if(true){
                setContent {
                    MikroKanonTheme(model) {
                        // A surface container using the 'background' color from the theme
                        Surface(color = MaterialTheme.colors.background) {

                            AppScaffold(model = model, model.userOptionsData.asFlow()) {
                                SequenceSelector(model = model, userOptionsDataFlow = model.userOptionsData.asFlow(),
                                    onAdd = { list, editing ->
                                        val bundle = Bundle()
                                        bundle.putParcelableArrayList("list", list)
                                        bundle.putBoolean("editing", editing)
                                        findNavController().navigate(R.id.inputFragment, bundle)
                                    },
                                    onLoadingCounterpoint = { position ->
                                        model.selectedCounterpoint.value?.let{
                                            model.onLoadingCounterpointFromSelector(position)
                                        }
                                        findNavController().navigate(R.id.outputFragment)
                                    },
                                    onSort = { sortType ->

                                    },
                                    onWave = { nWaves, list ->
                                        findNavController().navigate(R.id.outputFragment)
                                        model.onWaveFromFirstSelection(nWaves, list)
                                    },
                                    onTritoneSubstitution = { index ->
                                        model.onTritoneSubstitutionFromSelector(index)
                                    },
                                    onRound = { list ->
                                        findNavController().navigate(R.id.outputFragment)
                                        model.onRoundFromSelector(list)
                                    },
                                    onCadenza = { list, values ->
                                        findNavController().navigate(R.id.outputFragment)
                                        model.onCadenzaFromSelector(list, values)
                                    },
                                    onScarlatti = { list ->
                                        findNavController().navigate(R.id.outputFragment)
                                        model.onScarlattiFromSelector(list)
                                    },
                                    onFlourish = { list ->
                                        findNavController().navigate(R.id.outputFragment)
                                        model.onFlourishFromSelector(list)
                                    },
                                    onEraseIntervals = { list ->
                                        findNavController().navigate(R.id.outputFragment)
                                        model.onEraseIntervalsFromSelector(list)
                                    },
                                    onSingle = { list ->
                                        findNavController().navigate(R.id.outputFragment)
                                        model.onSingleFromSelector(list)
                                    },
                                    onDoppelgänger = { list ->
                                        findNavController().navigate(R.id.outputFragment)
                                        model.onDoppelgängerFromSelector(list)
                                    },
                                    onPedal = { nPedals, list ->
                                        findNavController().navigate(R.id.outputFragment)
                                        model.onPedalFromSelector(nPedals, list)
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
                                    },
                                    onMikroKanons5reducted = { list ->
                                        findNavController().navigate(R.id.outputFragment)
                                        model.onMikroKanons5reducted(list)
                                    }
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}
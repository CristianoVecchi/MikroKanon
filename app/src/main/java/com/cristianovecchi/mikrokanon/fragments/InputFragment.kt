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
import com.cristianovecchi.mikrokanon.composables.AbstractNoteSequenceEditor
import com.cristianovecchi.mikrokanon.AIMUSIC.Clip
import com.cristianovecchi.mikrokanon.MainActivity
import com.cristianovecchi.mikrokanon.composables.AppScaffold
import com.cristianovecchi.mikrokanon.locale.Lang
import com.cristianovecchi.mikrokanon.ui.MikroKanonTheme
import java.util.ArrayList

class InputFragment: Fragment() {
    private var list: ArrayList<Clip> = ArrayList()
    private var editing: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getParcelableArrayList<Clip>("list")?.let {
            this.list = it
        }
        arguments?.getBoolean("editing")?.let {
            editing = it
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val model = (activity as MainActivity).model
        model.userOptionsData.observe(viewLifecycleOwner){
            model.userOptionsData.value.let {
                if(it!!.isNotEmpty()) {
                    model.refreshZodiacFlags()
                    model._language.value = Lang.provideLanguage(model.getUserLangDef())
                    model.spread = it[0].spread
                }
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                MikroKanonTheme(model) {
                    Surface(color = MaterialTheme.colors.background) {
                        AppScaffold(model = model, model.dimensions.asFlow(), model.userOptionsData.asFlow(), model.allCounterpointsData.asFlow()) {
                            AbstractNoteSequenceEditor(list,
                                model = model,
                                model.dimensions.asFlow(),
                                editing,
                                iconMap = model.iconMap,
                                done_action = { list, editing ->
                                    findNavController().popBackStack()
                                    if (list.isNotEmpty()) {
                                        if (editing) {
                                            model.selectedSequence.value?.let {
                                                model.updateSequence(
                                                    it,
                                                    list
                                                )
                                            }
                                        } else {
                                            model.addSequence(list)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
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
import com.cristianovecchi.mikrokanon.composables.AbstractNoteSequenceEditor
import com.cristianovecchi.mikrokanon.composables.Clip
import com.cristianovecchi.mikrokanon.ui.MikroKanonTheme
import java.util.ArrayList

class InputFragment(): Fragment() {
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
            model.selectNotesNames()
        }
        return ComposeView(requireContext()).apply {
            setContent {
                MikroKanonTheme {
                    Surface(color = MaterialTheme.colors.background) {
                        AbstractNoteSequenceEditor(list, model = model, editing, iconMap = model.iconMap,
                            done_action = { list , editing ->
                                findNavController().popBackStack()
                                if(list.isNotEmpty()){
                                    if(editing){
                                        model.selectedSequence.value?.let { model.updateSequence(it, list) }
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
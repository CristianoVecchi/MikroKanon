package com.cristianovecchi.mikrokanon

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cristianovecchi.mikrokanon.composables.Clip
import com.cristianovecchi.mikrokanon.dao.SequenceDataRepository


class MainActivity : AppCompatActivity() {

   // val model: AppViewModel by viewModels()
    val model: AppViewModel by viewModels {
       AppViewModelFactory((application as MikroKanonApplication).repository)
   }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

fun ArrayList<Clip>.toStringAll(): String {
    if (this.isNotEmpty()) {
        return this.map { clip -> clip.text }.reduce { acc, string -> "$acc $string" }
    } else {
        return "empty Sequence"
    }
}


class AppViewModelFactory(private val repository: SequenceDataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


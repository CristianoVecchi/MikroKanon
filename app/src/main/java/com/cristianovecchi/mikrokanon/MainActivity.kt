package com.cristianovecchi.mikrokanon

import android.app.Application
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cristianovecchi.mikrokanon.db.CounterpointDataRepository
import com.cristianovecchi.mikrokanon.db.SequenceDataRepository
import com.cristianovecchi.mikrokanon.db.UserOptionsDataRepository

class MainActivity : AppCompatActivity() {

    val model: AppViewModel by viewModels {
       AppViewModelFactory(
           application,
           (application as MikroKanonApplication).sequenceRepository,
           (application as MikroKanonApplication).counterpointRepository,
           (application as MikroKanonApplication).userRepository
       )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        lifecycle.addObserver(model) // to stop the mediaPlayer onPause and onStop
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

}

class AppViewModelFactory(
    private val application: Application,
    private val sequenceRepository: SequenceDataRepository,
    private val counterpointRepository: CounterpointDataRepository,
    private val userRepository: UserOptionsDataRepository

) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(application, sequenceRepository, counterpointRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


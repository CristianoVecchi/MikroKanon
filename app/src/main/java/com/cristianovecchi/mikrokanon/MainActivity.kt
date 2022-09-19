package com.cristianovecchi.mikrokanon

import android.R.attr.bitmap
import android.R.attr.data
import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cristianovecchi.mikrokanon.db.CounterpointDataRepository
import com.cristianovecchi.mikrokanon.db.SequenceDataRepository
import com.cristianovecchi.mikrokanon.db.UserOptionsDataRepository
import com.cristianovecchi.mikrokanon.io.REQUEST_CODE_MIDI
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


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
        model.activity = this
        lifecycle.addObserver(model) // to stop the mediaPlayer onPause and onStop
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    var midiFile: File? = null
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK
            && requestCode == REQUEST_CODE_MIDI
            && data != null && midiFile != null) {
            val inputStream = FileInputStream(midiFile)
            val fileName = data.data!!
            val outputStream: FileOutputStream =
                contentResolver.openOutputStream(fileName) as FileOutputStream
            try {
                println(midiFile!!.absolutePath)
                //println("${externalFile.absolutePath}")
                val buffer = ByteArray(1024)
                var read: Int
                var total: Long = 0
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                    total += read.toLong()
                }
                Toast.makeText(this, "MIDI file saved!", Toast.LENGTH_LONG).show()
            } catch (e: Exception){
                //println("Write midi file: $e")
                Toast.makeText(this, "something went wrong" + e.message, Toast.LENGTH_LONG).show()
            } finally {
                inputStream.close()
                outputStream.close()
            }
        }
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


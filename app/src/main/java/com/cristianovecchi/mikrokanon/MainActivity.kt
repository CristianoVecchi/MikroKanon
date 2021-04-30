package com.cristianovecchi.mikrokanon

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cristianovecchi.mikrokanon.composables.Clip
import com.cristianovecchi.mikrokanon.dao.SequenceDataRepository
import com.cristianovecchi.mikrokanon.dao.UserOptionsDataRepository


class MainActivity : AppCompatActivity() {

    var PERMISSION_ALL = 1
    var PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

   // val model: AppViewModel by viewModels()
    val model: AppViewModel by viewModels {
       AppViewModelFactory((application as MikroKanonApplication).repository,
           (application as MikroKanonApplication).userRepository)
   }
    fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission!!
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!hasPermissions(this, *PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }
//    @SuppressLint("MissingSuperCall")
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
//                                            grantResults: IntArray) {
//        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            1 -> {
//                if (grantResults.isNotEmpty() && grantResults[0] ==
//                    PackageManager.PERMISSION_GRANTED) {
//                    if ((ContextCompat.checkSelfPermission(this@MainActivity,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE) ===
//                                PackageManager.PERMISSION_GRANTED && Manifest.permission.WRITE_EXTERNAL_STORAGE) !==
//                        PackageManager.PERMISSION_GRANTED)) {
//                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
//                }
//                return
//            }
//        }
//    }
}

fun ArrayList<Clip>.toStringAll(): String {
    if (this.isNotEmpty()) {
        return this.map { clip -> clip.text }.reduce { acc, string -> "$acc $string" }
    } else {
        return "empty Sequence"
    }
}


class AppViewModelFactory(private val repository: SequenceDataRepository, private val userRepository: UserOptionsDataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


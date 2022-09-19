package com.cristianovecchi.mikrokanon.io

//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import androidx.core.content.FileProvider
//import com.cristianovecchi.mikrokanon.BuildConfig
//import java.io.File

//fun shareMidi(file: File, activity: Activity, context: Context){
//        try {
//            if(file.exists()) {
//                val uri = FileProvider.getUriForFile(context,
//                    BuildConfig.APPLICATION_ID + ".fileprovider",
//                    file)
//                val intent = Intent(Intent.ACTION_SEND)
//                //println(Intent.FLAG_ACTIVITY_NEW_TASK)
//                //var flags = Intent.FLAG_GRANT_READ_URI_PERMISSION //or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                //val flags = Intent.FLAG_ACTIVITY_NEW_TASK + 1
//                //println("FLAGS: ${flags.toByte()}")
//                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                intent.setType("audio/midi")
//                intent.putExtra(Intent.EXTRA_STREAM, uri)
//                val chooserIntent = Intent.createChooser(intent,"Share MIDI to...")
//                chooserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                //println("FLAGS: ${intent.flags}")
//                try{
//                        context
//                        //.startActivity(intent)
//                        .startActivity(chooserIntent)
//                } catch (ex: Exception){
//                    println("Exception in Share Midi: ${ex.message}")
//                        context
//                        .startActivity(intent)
//                }
//            }
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//
//
////        val shareIntent: Intent = Intent().apply {
////            action = Intent.ACTION_SEND
////            putExtra(Intent.EXTRA_STREAM, file.toURI() as Parcelable)
////            type = "audio/midi"
////        }
////        getApplication<MikroKanonApplication>().applicationContext
////
//}
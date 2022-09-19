package com.cristianovecchi.mikrokanon.io
//
//import android.Manifest
//import android.app.Activity
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.os.Environment
//import android.provider.Settings
//import androidx.core.app.ActivityCompat
//import com.cristianovecchi.mikrokanon.BuildConfig
//import com.cristianovecchi.mikrokanon.ui.G
//import java.io.File
//import java.io.FileNotFoundException
//import java.io.FileOutputStream
//import java.io.IOException
//
//
//const val REQUEST_EXTERNAL_STORAGE: Int = 1
//const val APP_STORAGE_ACCESS_REQUEST_CODE = 501 // Any value
//
//val PERMISSIONS_STORAGE = if(android.os.Build.VERSION.SDK_INT >=android.os.Build.VERSION_CODES.R)
//    arrayOf(
//        Manifest.permission.READ_EXTERNAL_STORAGE,
//        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//        Manifest.permission.MANAGE_EXTERNAL_STORAGE
//    )
//    else arrayOf(
//        Manifest.permission.READ_EXTERNAL_STORAGE,
//        Manifest.permission.WRITE_EXTERNAL_STORAGE
//    )
//
//
//fun verifyStoragePermissions(activity: Activity): Boolean {
//    // Check if we have write permission
//    println("Verify permissions: ${PERMISSIONS_STORAGE.contentToString()}")
//    val permissions = PERMISSIONS_STORAGE.map {
//        ActivityCompat.checkSelfPermission(
//            activity,
//            it
//        )
//    }.also{println(it)}
//    return permissions.all{ it == PackageManager.PERMISSION_GRANTED }
//}
//fun requestPermissions(activity: Activity){
//    println("Permission requested for: ${PERMISSIONS_STORAGE.contentToString()}")
//        ActivityCompat.requestPermissions(
//            activity,
//            PERMISSIONS_STORAGE,
//            REQUEST_EXTERNAL_STORAGE
//        )
//    if(PERMISSIONS_STORAGE.contains(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
//        //if (!Environment.isExternalStorageManager()) {
//        val uri: Uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
//        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
//        activity.startActivity(intent)
//    }
//}
//
///// to SAVE a File
//fun canWriteOnExternalStorage(): Boolean {
//    // get the state of your external storage
//    val state = Environment.getExternalStorageState()
//    if (Environment.MEDIA_MOUNTED == state) {
//        // if storage is mounted return true
//        println("FileChooser: can write on External Storage")
//        return true
//    }
//    return false
//}
//private fun isExternalStorageReadOnly(): Boolean {
//    val extStorageState = Environment.getExternalStorageState()
//    return if (Environment.MEDIA_MOUNTED_READ_ONLY == extStorageState) {
//        true
//    } else false
//}
//private fun isExternalStorageAvailable(): Boolean {
//    val extStorageState = Environment.getExternalStorageState()
//    return if (Environment.MEDIA_MOUNTED == extStorageState) {
//        true
//    } else false
//}
//

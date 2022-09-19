package com.cristianovecchi.mikrokanon.io

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import com.cristianovecchi.mikrokanon.BuildConfig
import com.cristianovecchi.mikrokanon.MainActivity
import com.cristianovecchi.mikrokanon.locale.convertToFileDate
import java.io.*
import java.util.*

const val REQUEST_CODE_MIDI = 42
fun writeMidi(file: File, activity: Activity, timestamp: Long, langDef: String){
    (activity as MainActivity).midiFile = file
    val fileName = file.name.substringBefore('.') + "_" + convertToFileDate(timestamp, langDef) + ".mid"
    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.type = "audio/midi"
    intent.putExtra(Intent.EXTRA_TITLE, fileName)
    activity.startActivityForResult(intent, REQUEST_CODE_MIDI)
}
//fun writeMidi(file: File, activity: Activity, context: Context) {
//    if (!verifyStoragePermissions(activity)) {
//        requestPermissions(activity)
//    } else {
//        var inputStream: FileInputStream? = null
//        var outputStream: OutputStream? = null
//        try {
//            inputStream = FileInputStream(file)
//            val externalFile = File(activity.getExternalFilesDir(null), file.name)
//            outputStream = FileOutputStream(externalFile)
//            println("${file.absolutePath}")
//            println("${externalFile.absolutePath}")
//            val buffer = ByteArray(1024)
//            var read: Int
//            var total: Long = 0
//            while (inputStream.read(buffer).also { read = it } != -1) {
//                outputStream.write(buffer, 0, read)
//                total += read.toLong()
//            }
//        } catch (e: Exception){
//            println("Write Midi: $e")
//        } finally {
//            inputStream?.close()
//            outputStream?.close()
//        }
//        val contentUri = FileProvider.getUriForFile(
//            context,
//            "com.cristianovecchi.mikrokanon.fileprovider",
//            file
//        )
//        val contentUri = FileProvider.getUriForFile(
//            Objects.requireNonNull(context),
//            BuildConfig.APPLICATION_ID + ".fileprovider", file);
//        val shareIntent: Intent = Intent().apply {
//            action = Intent.ACTION_SEND
//            setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            putExtra(Intent.EXTRA_STREAM, file)
//            //putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
//            type = "audio/midi"
//            //setDataAndType(contentUri, context.contentResolver.getType(contentUri))
//        }
//        activity.startActivity(Intent.createChooser(shareIntent, "write MK_lastplay"))

        //val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
//        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "application/pdf"
//            putExtra(Intent.EXTRA_TITLE, "invoice.pdf")
//
//            // Optionally, specify a URI for the directory that should be opened in
//            // the system file picker before your app creates the document.
//            putExtra(DocumentsContract.EXTRA_INITIAL_URI, file)
//        }
//        activity.startActivityForResult(intent, CREATE_FILE)
//    }
//}
//fun actualWriteFile(file: File) {
//    val dir = File(Environment.getExternalStorageDirectory(), "MK_MIDI")
//    val mkdir: Boolean = dir.mkdir() // create this directory if not already created
//    val name = file.name
//    val newFile = File(
//        dir,
//        name + ".mid"
//    ) // create the file in which we will write the contents
//    var os: FileOutputStream? = null
//    try {
//        os = FileOutputStream(newFile)
//    } catch (e: FileNotFoundException) {
//        e.printStackTrace()
//    }
//
//    try {
//        //os?.write(data.toByteArray())
//
//    } catch (e: IOException) {
//        e.printStackTrace()
//    }
//    try {
//        os?.flush()
//        os?.close()
//    } catch (e: IOException) {
//        e.printStackTrace()
//    }
//}

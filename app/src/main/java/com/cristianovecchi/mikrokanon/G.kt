package com.cristianovecchi.mikrokanon

import android.content.res.TypedArray

import android.R
import android.R.array
import android.content.Context
import com.cristianovecchi.mikrokanon.ui.AIColor
import java.lang.Exception
import java.lang.reflect.Field


// General Constants
class G {
 companion object{
     const val quarter: Long = 480
     const val totalPieceTickDuration: Long = 96 * 4 * 64

     @JvmStatic
     fun convertIntegers(integers: List<Int>): IntArray {
         val ret = IntArray(integers.size)
         val iterator = integers.iterator()
         for (i in ret.indices) {
             ret[i] = iterator.next()
         }
         return ret
     }

     fun getMultiTypedArray(context: Context, key: String): List<TypedArray> {
         val array: MutableList<TypedArray> = ArrayList()
         try {
             val res: Class<out MutableList<TypedArray>> = array::class.java
             var field: Field?
             var counter = 0
             do {
                 field = res.getField(key + "_" + counter)
                 array.add(context.getResources().obtainTypedArray(field.getInt(null)))
                 counter++
             } while (field != null)
         } catch (e: Exception) {
             e.printStackTrace();
         } finally {
             return array
         }
     }

     var colorFont = 0
     var colorBackground1 = 0
     var colorBackground2 = 0
     var colorBeatNotes = 0
     var colorPassageNotes1 = 0
     var colorPassageNotes2 = 0
     var colorRadar = 0
     var indexColorArray = 534
     private var radarColorEditor = false
     private var colorArrays: List<TypedArray>? = null

     fun isRadarColorEditor(): Boolean {
         return radarColorEditor
     }

     fun setRadarColorEditor(isActive: Boolean, context: Context) {
         if (isActive) {
             colorArrays = getMultiTypedArray(context, "colors")
             radarColorEditor = isActive
         } else {
             colorArrays = null
             radarColorEditor = isActive
         }
     }
     fun loadColorArrays(context: Context): Boolean{
       //  if (colorArrays == null || colorArrays!!.isEmpty()){
                 colorArrays = AIColor.getMultiTypedArray(context, "colors")
       //      }
         println("ArraYColors = ${colorArrays?.size}")
         return colorArrays?.isNotEmpty() ?: false
     }

     fun setColorArrayBySearch(context: Context, desiredColor: Int) {
         var max = Int.MAX_VALUE
         val background1index = 1
         var choice = indexColorArray
         for (i in colorArrays!!.indices) {
             val aimColor = colorArrays!![i].getColor(background1index, 0)
             val diff: Int = AIColor.colorDistance(desiredColor, aimColor)
             if (diff < max) {
                 max = diff
                 choice = i
             }
         }
         indexColorArray = choice
         println("Color Array = $choice")
         setColorArray(context, choice)
     }

     fun setColorArray(context: Context, index: Int) {
         var index = index
         indexColorArray = index
         var i = 0
         if (index < 0) index = 0
         //val arrays = AIColor.getMultiTypedArray(context, "colors")
         val arrays = colorArrays!!
         val array = arrays[index % arrays.size]
         colorFont = array.getColor(i++, 0)
         colorBackground1 = array.getColor(i++, 0)
         colorBackground2 = array.getColor(i++, 0)
         colorBeatNotes = array.getColor(i++, 0)
         colorPassageNotes1 = array.getColor(i++, 0)
         colorPassageNotes2 = array.getColor(i++, 0)
         colorRadar = array.getColor(i++, 0)
         //((MainActivity)context).savePreferences();
         //G.radarMessage = "C.A.: " + index % arrays.size
     }

     fun selectRandomColors(context: Context) {
         val arrays = getMultiTypedArray(context, "colors")
         val size = arrays.size
         val random = (Math.random() * (size + 1)).toInt()
         setColorArray(context, random)
     }

 }
}
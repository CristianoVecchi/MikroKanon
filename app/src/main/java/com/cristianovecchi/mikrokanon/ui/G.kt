package com.cristianovecchi.mikrokanon.ui

import android.content.res.TypedArray

import android.content.Context
import java.lang.Exception
import java.lang.reflect.Field
import kotlin.math.absoluteValue


// General Constants and functions for AIColor converted in Kotlin
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

     //doesn't work
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
     var indexColorArray = 515
     private var radarColorEditor = false
     //private var colorArrays: List<IntArray>? = null
     fun getArraySize(): Int{
         return getColorArraysSize() * 3
         //return colorArrays!!.size * 3
     }
     fun isRadarColorEditor(): Boolean {
         return radarColorEditor
     }

//     fun setRadarColorEditor(isActive: Boolean, context: Context) {
//         if (isActive) {
//             //colorArrays = getMultiTypedArray(context, "colors")
//             radarColorEditor = isActive
//         } else {
//             colorArrays = null
//             radarColorEditor = isActive
//         }
//     }
//     fun loadColorArrays(context: Context): Boolean{
//         colorArrays = AIColor.getMultiTypedArray(context, "colors", -0.12f).map { it.toIntArray() }
//         //colorArrays!!.forEach{ println(it.contentToString())}
//         //println("ArraYColors = ${colorArrays?.size}")
//         return colorArrays?.isNotEmpty() ?: false
//     }
//     fun deleteColorArrays(){
//         colorArrays = null
//     }

     fun setColorArrayBySearch(context: Context, desiredColor: Int) {
         var max = Int.MAX_VALUE
         val background1index = 1

         var choice = indexColorArray
         for (i in colorArrays!!.indices) {
             val aimColor = colorArrays!![i][background1index]
             val diff: Int = AIColor.colorDistance(desiredColor, aimColor)
             if (diff < max) {
                 max = diff
                 choice = i
             }
         }
         indexColorArray = choice
         //println("Color Array = $choice")
         setColorArray(context, choice)
     }
     fun setColorArrayBySearchFromIndex(context: Context, desiredColor: Int, from: Int) {
         var max = Int.MAX_VALUE
         val background1index = 1

         var choice = indexColorArray
         for (i in colorArrays.indices) {
             val aimColor = colorArrays[i][background1index]
             val diff: Int = AIColor.colorDistance(desiredColor, aimColor)
             if (diff < max) {
                 max = diff
             }
         }
         val choices = mutableListOf<Int>()
         for (i in colorArrays.indices) {
             val aimColor = colorArrays[i][background1index]
             if(AIColor.colorDistance(desiredColor, aimColor) == max){
                 choices.add(i)
             }
         }
         var maxChoices = Int.MAX_VALUE
         for(i in choices){
             val diff = (i - from).absoluteValue
             if((i - from).absoluteValue < maxChoices){
                 maxChoices = diff
                 choice = i
             }
         }
         indexColorArray = choice
         //println("Color Array = $choice")
         //setColorArray(context, choice)
         setColorArray(getColorList(choice).toIntArray(), choice)
     }
     fun setColorArray(context: Context, index: Int) {
         var index = index
         indexColorArray = index
         var i = 0
         if (index < 0) index = 0
         //val arrays = AIColor.getMultiTypedArray(context, "colors")
         val arrays = colorArrays
         val array = if(arrays.size == 1) arrays[0] else arrays[index % arrays.size]
         //println("Array[0]= ${array.contentToString()}")
         //val array = arrays[index % arrays.size]
         colorFont = array[i++]
         colorBackground1 = array[i++]
         colorBackground2 = array[i++]
         colorBeatNotes = array[i++]
         colorPassageNotes1 = array[i++]
         colorPassageNotes2 = array[i++]
         colorRadar = array[i++]
         //((MainActivity)context).savePreferences();
         //G.radarMessage = "C.A.: " + index % arrays.size
     }
     fun setColorArray(colors: IntArray, index: Int) {
         indexColorArray = index
         var i = 0
         colorFont = colors[i++]
         colorBackground1 = colors[i++]
         colorBackground2 = colors[i++]
         colorBeatNotes = colors[i++]
         colorPassageNotes1 = colors[i++]
         colorPassageNotes2 = colors[i++]
         colorRadar = colors[i]
     }

     fun selectRandomColors(context: Context) {
         val arrays = getMultiTypedArray(context, "colors")
         val size = arrays.size
         val random = (Math.random() * (size + 1)).toInt()
         setColorArray(context, random)
     }


 }
}
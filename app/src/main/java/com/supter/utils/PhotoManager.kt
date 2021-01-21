package com.supter.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.*

object PhotoManager {

    private val IMAGES_FOLDER_NAME = "Supter"
    private val IMAGE_QUALITY = 90

    fun createFileFromBitmap(bitmap: Bitmap, context: Context): File {
        val fileName = System.currentTimeMillis().toString() + ".jpeg"
        val file = File(context.cacheDir, fileName)

        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, bos)
        val bitmapData = bos.toByteArray()

        var fos: FileOutputStream? = null

        try {
            fos = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            logException(e)
        }

        try {
            fos?.write(bitmapData)
            fos?.flush()
            fos?.close()
        } catch (e: IOException) {
            logException(e)
        }

        return file
    }
}
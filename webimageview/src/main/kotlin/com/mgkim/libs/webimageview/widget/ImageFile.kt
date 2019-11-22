package com.mgkim.libs.webimageview.widget

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mgkim.libs.webimageview.NetManager
import com.mgkim.libs.webimageview.utils.ImageUtil
import java.io.*
import java.nio.ByteBuffer

/**
 * ImageFile Wrapper Class
 * @author : mgkim
 * @version : 1.0.0
 * @since : 2019-11-20 오후 1:52
 * @param fileName : Image fileName
 * @param reqWidth : resize를 위한 image 너비
 * @param reqHeight : resize를 위한 image 높이
 **/
internal class ImageFile(
    private val fileName: String,
    private val reqWidth: Int = 0,
    private val reqHeight: Int = 0
) {
    /**
     * image가 저장될 file
     * url에 마지막 "/" 이후로 File 이름을 정하고,
     * reqWidth, reqHeight 가 있으면 뒤에 FileName_100x2000 형식으로 저장
     */
    private val imageFile: File by lazy {
        File(
            NetManager.cacheImgPath,
            if (reqWidth > 0 && reqHeight > 0) "${fileName}_${reqWidth}x${reqHeight}" else fileName
        )
    }
    private var bitmap: Bitmap? = null

    init {
        if (imageFile.exists()) {
            decodeBitmap(reqWidth, reqHeight)
        }
    }

    fun getBitmap(): Bitmap? {
        return bitmap
    }

    /**
     * inputStream을 읽어서 File에 저장 후 decodeBitmap() 수행
     */
    fun writeFile(inputStream: InputStream): Boolean {
        var isSuccess = false
        try {
            imageFile.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val buffSize = 2048
        BufferedInputStream(inputStream, buffSize).use { bis ->
            FileOutputStream(imageFile.absolutePath).use { fos ->
                var current = 0
                val buff = ByteBuffer.allocate(buffSize)
                val data = buff.array()
                while (current != -1) {
                    current = bis.read(data, 0, buffSize)
                    if (current == -1) {
                        break
                    }
                    fos.write(data, 0, current)
                }
            }
        }

        decodeBitmap(reqWidth, reqHeight)
        if (bitmap != null) {
            isSuccess = true
        }

        return isSuccess
    }

    /**
     * File을 읽어서 bitmap load
     */
    private fun decodeBitmap(
        reqWidth: Int = 0,
        reqHeight: Int = 0
    ): Boolean {
        try {
            var bitmap: Bitmap?
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.RGB_565
            if (reqHeight > 0 && reqWidth > 0) {
                //here only bounds are being decoded so no real memory
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(imageFile.absolutePath, options)

                // Calculate inSampleSize
                options.inSampleSize = ImageUtil.computeInitialSampleSize(options, if (reqWidth > reqHeight) reqWidth else reqHeight)

                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false
                // allocating memory now as per the required size of bit map
                bitmap = BitmapFactory.decodeFile(
                    imageFile.absolutePath,
                    options
                )
            } else {
                bitmap = BitmapFactory.decodeFile(
                    imageFile.absolutePath,
                    options
                )
            }

            if (bitmap != null) {
                this.bitmap = bitmap
                return true
            }

            imageFile.delete()
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            return false
        }

        return false
    }
}
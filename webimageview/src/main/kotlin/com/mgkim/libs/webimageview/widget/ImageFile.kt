package com.mgkim.libs.webimageview.widget

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mgkim.libs.webimageview.NetManager
import com.mgkim.libs.webimageview.NetManagerConfig
import com.mgkim.libs.webimageview.utils.FormatUtil
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
 * @param config : WebimageView 설정
 **/
internal class ImageFile(
    private val fileName: String,
    private val reqWidth: Int = 0,
    private val reqHeight: Int = 0,
    private val config : NetManagerConfig.WebImageViewConfig = NetManager.config.webImageViewConfig
) {
    /**
     * image가 저장될 file
     * url에 마지막 "/" 이후로 File 이름을 정하고,
     * reqWidth, reqHeight 가 있으면 뒤에 FileName_100x2000 형식으로 저장
     */
    private val imageFile: File by lazy {
        File(NetManager.cacheImgPath, fileName)
    }
    private val resizeImageFile: File by lazy {
        File(NetManager.cacheImgPath, "${fileName}_${reqWidth}x${reqHeight}")
    }
    private var bitmap: Bitmap? = null

    init {
        if (config.diskCacheOption and NetManagerConfig.DiskCacheOption.RESIZE_CACEH > 0 && resizeImageFile.exists()) {
            bitmap = decodeBitmap(resizeImageFile)
        }
        if(bitmap == null && imageFile.exists()) {
            bitmap = decodeBitmap(imageFile, reqWidth, reqHeight)
        }
    }

    fun getBitmap(): Bitmap? {
        return bitmap
    }

    /**
     * inputStream을 읽어서 File에 저장 후 createBitmap() 수행
     */
    fun writeFile(inputStream: InputStream): Boolean {
        var isSuccess = false
        bitmap = createBitmap(inputStream, reqWidth, reqHeight)

        if (bitmap != null) {
            isSuccess = true
        }

        return isSuccess
    }

    /**
     * File을 읽어서 bitmap load
     */
    private fun decodeBitmap(
        imageFile :File,
        reqWidth: Int = 0,
        reqHeight: Int = 0
    ): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = config.preferredConfig
            if (reqHeight > 0 && reqWidth > 0) {
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(imageFile.absolutePath, options)

                options.inSampleSize = ImageUtil.computeInitialSampleSize(options, if (reqWidth > reqHeight) reqWidth else reqHeight)
                options.inJustDecodeBounds = false
                bitmap = BitmapFactory.decodeFile(imageFile.absolutePath, options)

                if(config.diskCacheOption and NetManagerConfig.DiskCacheOption.RESIZE_CACEH > 0 && options.inSampleSize > 1) {
                    resizeImageFile.createNewFile()
                    FileOutputStream(resizeImageFile).use {
                        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100 / options.inSampleSize, it)
                    }
                }
            } else {
                bitmap = BitmapFactory.decodeFile(imageFile.absolutePath, options)
            }

            if (bitmap != null) {
                return bitmap
            }

            imageFile.delete()
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            return bitmap
        }

        return bitmap
    }

    /**
     * inputStream을 읽어서 bitmap load
     */
    private fun createBitmap(
        inputStream: InputStream,
        reqWidth: Int = 0,
        reqHeight: Int = 0
    ): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = config.preferredConfig
            if (reqHeight > 0 || reqWidth > 0) {
                val buffSize = 2048
                if (config.diskCacheOption and NetManagerConfig.DiskCacheOption.ORIGINAL_CACEH > 0) {
                    imageFile.createNewFile()
                    //org File save
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
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeFile(imageFile.absolutePath, options)

                    options.inSampleSize = ImageUtil.computeInitialSampleSize(options, if (reqWidth > reqHeight) reqWidth else reqHeight)
                    options.inJustDecodeBounds = false
                    bitmap = BitmapFactory.decodeFile(imageFile.absolutePath, options)
                    //resize file save
                    if(config.diskCacheOption and NetManagerConfig.DiskCacheOption.RESIZE_CACEH > 0 && options.inSampleSize > 1) {
                        FileOutputStream(resizeImageFile).use {
                            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100 / options.inSampleSize, it)
                        }
                    }
                } else {
                    val bos = ByteArrayOutputStream()
                    var current = 0
                    val buff = ByteBuffer.allocate(buffSize)
                    val data = buff.array()
                    while (current != -1) {
                        current = inputStream.read(data, 0, buffSize)
                        if (current == -1) {
                            break
                        }
                        bos.write(data, 0, current)
                    }
                    bos.flush()
                    bos.use {
                        ByteArrayInputStream(it.toByteArray()).use {bis ->
                            options.inJustDecodeBounds = true
                            BitmapFactory.decodeStream(bis, null, options)
                        }

                        options.inSampleSize = ImageUtil.computeInitialSampleSize(options, if (reqWidth > reqHeight) reqWidth else reqHeight)
                        ByteArrayInputStream(it.toByteArray()).use { bis->
                            // Decode bitmap with inSampleSize set
                            options.inJustDecodeBounds = false
                            // allocating memory now as per the required size of bit map
                            bitmap = BitmapFactory.decodeStream(bis, null, options)
                        }
                    }

                    //resize file save
                    if(config.diskCacheOption and NetManagerConfig.DiskCacheOption.RESIZE_CACEH > 0) {
                        if(options.inSampleSize > 1) {
                            resizeImageFile.createNewFile()
                            FileOutputStream(resizeImageFile).use {
                                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100 / options.inSampleSize, it)
                            }
                        } else {
                            imageFile.createNewFile()
                            FileOutputStream(imageFile).use {
                                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100 / options.inSampleSize, it)
                            }
                        }
                    }
                }
            } else {
                bitmap = BitmapFactory.decodeFile(imageFile.absolutePath, options)
                if(config.diskCacheOption != NetManagerConfig.DiskCacheOption.NO_DISK_CACEH) {
                    imageFile.createNewFile()
                    FileOutputStream(imageFile).use {
                        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    }
                }
            }

            if (bitmap != null) {
                return bitmap
            }

            imageFile.delete()
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            return bitmap
        }

        return bitmap
    }
}
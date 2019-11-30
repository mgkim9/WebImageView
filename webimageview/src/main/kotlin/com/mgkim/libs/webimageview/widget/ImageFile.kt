package com.mgkim.libs.webimageview.widget

import android.graphics.*
import com.mgkim.libs.webimageview.NetManager
import com.mgkim.libs.webimageview.NetManagerConfig
import com.mgkim.libs.webimageview.utils.FormatUtil.getFileName
import com.mgkim.libs.webimageview.utils.FormatUtil.getRoundedCacheName
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
            decodeBitmap(resizeImageFile)?.let {
                bitmap = if(config.roundedCornerPixel >= 0 && it != null) {
                    makeRounded(it, config.roundedCornerPixel, config.roundedCornerNoSquare)
                } else {
                    it
                }
                ImageCache.setBitmap(getRoundedCacheName(resizeImageFile.name, this@ImageFile.config.roundedCornerPixel), bitmap!!)
            }

        }
        if(bitmap == null && imageFile.exists()) {
            decodeBitmap(imageFile, reqWidth, reqHeight)?.let {
                bitmap = if (config.roundedCornerPixel >= 0 && it != null) {
                    makeRounded(it, config.roundedCornerPixel, config.roundedCornerNoSquare)
                } else {
                    it
                }
                ImageCache.setBitmap(getRoundedCacheName(getFileName(imageFile.name, reqWidth, reqHeight)!!, this@ImageFile.config.roundedCornerPixel), bitmap!!)
            }
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
        val buffSize = 2048
        var bitmap: Bitmap? = null
        try {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = config.preferredConfig
            if (reqHeight > 0 || reqWidth > 0) {
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
                                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
                            }
                        }
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

                    options.inSampleSize = ImageUtil.computeInitialSampleSize(options)
                    ByteArrayInputStream(it.toByteArray()).use { bis->
                        // Decode bitmap with inSampleSize set
                        options.inJustDecodeBounds = false
                        // allocating memory now as per the required size of bit map
                        bitmap = BitmapFactory.decodeStream(bis, null, options)
                    }
                }
                if(config.diskCacheOption != NetManagerConfig.DiskCacheOption.NO_DISK_CACEH) {
                    imageFile.createNewFile()
                    FileOutputStream(imageFile).use {
                        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    }
                }
            }

            if(bitmap != null && config.roundedCornerPixel >= 0) {
                return makeRounded(bitmap!!, config.roundedCornerPixel, config.roundedCornerNoSquare).apply {
                    bitmap = this
                    ImageCache.setBitmap(getRoundedCacheName(getFileName(fileName, reqWidth, reqHeight)!!, this@ImageFile.config.roundedCornerPixel), this)
                }
            }

            imageFile.delete()
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            return bitmap
        }

        return bitmap
    }

    /**
     * make rounded image
     * @param bitmap :원본 Image
     * @param roundedCornerPixel : Corner Radius (roundedCornerPixel <= 0 || roundedCornerPixel >= Math.max(width, height) / 2) 이면 isCircular = true)
     * @param roundedCornerNoSquare : 이미지 곡선처리 미사용 여부 bitmask ( 0b0001(좌상단), 0b0010(우상단), 0b0100(좌하단), 0b1000(우하단)
     */
    private fun makeRounded(bitmap: Bitmap, roundedCornerPixel: Float, roundedCornerNoSquare: Int): Bitmap {
        val maxSize: Int
        val minSize: Int
        if (bitmap.width > bitmap.height) {
            maxSize = bitmap.width
            minSize = bitmap.height
        } else {
            maxSize = bitmap.height
            minSize = bitmap.width
        }
        val isCircular = roundedCornerPixel <= 0F || roundedCornerPixel >= maxSize / 2
        val cornerPixel: Float
        val w: Int
        val h: Int
        if (isCircular) {
            w = minSize
            h = minSize
            cornerPixel = maxSize / 2F
        } else {
            w = bitmap.width
            h = bitmap.height
            cornerPixel = roundedCornerPixel
        }

        val roundedBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val paint = Paint()
        paint.isAntiAlias = true
        Canvas(roundedBitmap).apply {
            drawARGB(0, 0, 0, 0)
            drawRoundRect(RectF(Rect(0, 0, w, h)), cornerPixel, cornerPixel, paint)

            // draw rectangles over the corners we want to be square
            if (roundedCornerNoSquare and NetManagerConfig.RoundedCornerSquare.TOP_LEFT > 0) {
                drawRect(0f, 0f, (w / 2).toFloat(), (h / 2).toFloat(), paint)
            }
            if (roundedCornerNoSquare and NetManagerConfig.RoundedCornerSquare.TOP_RIGHT > 0) {
                drawRect((w / 2).toFloat(), 0f, w.toFloat(), (h / 2).toFloat(), paint)
            }
            if (roundedCornerNoSquare and NetManagerConfig.RoundedCornerSquare.BOTTOM_LEFT > 0) {
                drawRect(0f, (h / 2).toFloat(), (w / 2).toFloat(), h.toFloat(), paint)
            }
            if (roundedCornerNoSquare and NetManagerConfig.RoundedCornerSquare.BOTTOM_RIGHT > 0) {
                drawRect((w / 2).toFloat(), (h / 2).toFloat(), w.toFloat(), h.toFloat(), paint)
            }

            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            if (isCircular) {
                if (bitmap.width > bitmap.height) {
                    drawBitmap(bitmap, (minSize - maxSize) / 2F, 0f, paint)
                } else {
                    drawBitmap(bitmap, 0f, (minSize - maxSize) / 2F, paint)
                }
            } else {
                drawBitmap(bitmap, 0f, 0f, paint)
            }
        }
        return roundedBitmap
    }
}
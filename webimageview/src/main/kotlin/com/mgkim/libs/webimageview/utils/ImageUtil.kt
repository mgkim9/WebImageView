package com.mgkim.libs.webimageview.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Point
import android.view.WindowManager
import kotlin.math.ceil
import kotlin.math.sqrt

internal object ImageUtil {
    private val TAG: String = "ImageUtil"
    private const val UNCONSTRAINED = -1
    private const val MAX_NUM_PIXELS_MICRO_IMAGE = 1024 * 8 * 2048 * 8  //이미지 최대 사이즈

    /**
    * Device 가로 세로 사이즈 구하기
    * @author : mgkim
    * @version : 1.0.0
    * @since : 2019-11-20 오후 3:15
    **/
    fun getDeviceDisplaySize(context: Context): Point {
        val point = Point()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        display.getRealSize(point)
        return point
    }

    /*
     * Compute the sample size as a function of minSideLength
     * and maxNumOfPixels.
     * minSideLength is used to specify that minimal width or height of a
     * bitmap.
     * maxNumOfPixels is used to specify the maximal size in pixels that is
     * tolerable in terms of memory usage.
     *
     * The function returns a sample size based on the constraints.
     * Both size and minSideLength can be passed in as IImage.UNCONSTRAINED,
     * which indicates no care of the corresponding constraint.
     * The functions prefers returning a sample size that
     * generates a smaller bitmap, unless minSideLength = IImage.UNCONSTRAINED.
     *
     * Also, the function rounds up the sample size to a power of 2 or multiple
     * of 8 because BitmapFactory only honors sample size this way.
     * For example, BitmapFactory downsamples an image by 2 even though the
     * request is 3. So we round up the sample size to avoid OOM.
     */
    fun makeThumbnailSampleSize(
        options: BitmapFactory.Options,
        minSideLength: Int = UNCONSTRAINED,
        maxNumOfPixels: Int = MAX_NUM_PIXELS_MICRO_IMAGE
    ): Int {

        val initialSize = computeInitialSampleSize(
            options, minSideLength,
            maxNumOfPixels
        )

        var roundedSize: Int
        if (initialSize <= 8) {
            roundedSize = 1
            while (roundedSize < initialSize) {
                roundedSize = roundedSize shl 1
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8
        }

        return roundedSize
    }

    /**
     * Bitmap image sampleSize 구하기
     * maxNumOfPixels 보다 작거나
     * minSideLength 보다 크도록
     * @author : mgkim
     * @version : 1.0.0
     * @since : 2019-11-20 오후 7:54
     * @param options : BitmapFactory.Options
     * @param minSideLength : 이미지 최대 사이즈 (w, h 중 큰값)
     * @param maxNumOfPixels : 메모리 최대 사이즈 (w*h)
     **/
    fun computeInitialSampleSize(
        options: BitmapFactory.Options,
        minSideLength: Int = UNCONSTRAINED,
        maxNumOfPixels: Int = MAX_NUM_PIXELS_MICRO_IMAGE
    ): Int {
        val w = options.outWidth.toDouble()
        val h = options.outHeight.toDouble()
        val lowerBound = if (maxNumOfPixels == UNCONSTRAINED) 1 else ceil(sqrt(w * h / maxNumOfPixels)).toInt()
        val upperBound = if (minSideLength == UNCONSTRAINED) 1 else ceil(w / minSideLength).coerceAtLeast((ceil(h / minSideLength))).toInt()
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound
        }

        return if (maxNumOfPixels == UNCONSTRAINED && minSideLength == UNCONSTRAINED) {
            1
        } else if (minSideLength == UNCONSTRAINED) {
            lowerBound
        } else {
            upperBound
        }
    }
}
package com.mgkim.libs.webimageview.utils

import android.content.res.Resources
import java.util.regex.Pattern

internal object FormatUtil {
    /**
     * url에서 마지막 path를 FileName으로 설정
     * resize된경우  fileName_100x200 형태로 저장
     * @author : mgkim
     * @version : 1.0.0
     * @since : 2019-11-20 오후 1:55
     * @param url
     * @param layoutWidth : resize 한경우 Width
     * @param layoutHeight : resize 한경우 Height
     */
    fun getFileName(url: String, layoutWidth: Int = 0, layoutHeight: Int = 0): String? {
        // url이 '/' 으로 끝나는경우 마지막 '/' 제거
        var tempUrl = if (url[url.length - 1] == '/') url.substring(0, url.length - 2) else url
        tempUrl.split("/").apply {
            if(isNotEmpty()) {
                return if(layoutWidth > 0 &&  layoutHeight > 0) {
                    "${this[size - 1]}_${layoutWidth}x${layoutHeight}"
                } else {
                    this[size - 1]
                }
            }
        }
        return null
    }

    /**
     * RoundedCacheName
     * Rounded된경우 CacheName fileName_100x200_R_roundedCornerPixel 형태로 저장
     * @author : mgkim
     * @version : 1.0.4
     * @since : 2019-12-01 오전 3:55
     * @param fileName : fileName
     * @param roundedCornerPixel : roundedCornerPixel
     */
    fun getRoundedCacheName(fileName: String, roundedCornerPixel:Float): String {
        return if(roundedCornerPixel >= 0) {
            "${fileName}_R_$roundedCornerPixel"
        } else {
            fileName
        }
    }

    /**
     * layout size 파싱 px 로 리턴
     * @author : mgkim
     * @version : 1.0.0
     * @since : 2019-11-20 오후 1:55
     */
    fun getPxSize(sizeStr: String?): Int {
        if(sizeStr.isNullOrEmpty()) {
            return 0
        }
        var value = 0
        Pattern.compile("[\\d]+").matcher(sizeStr).apply {
            if(find()) {
                value = group().toInt()
            }
        }

        if(value <= 0) {
            return 0
        }

        return if (sizeStr.lastIndexOf("dip", ignoreCase = true) > 0) {
            (value * Resources.getSystem().displayMetrics.density).toInt()
        } else if (sizeStr.lastIndexOf("px", ignoreCase = true) > 0) {
            value
        } else {
            0
        }
    }
}
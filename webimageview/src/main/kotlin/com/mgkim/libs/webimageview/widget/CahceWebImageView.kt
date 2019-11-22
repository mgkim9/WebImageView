package com.mgkim.libs.webimageview.widget

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet

/**
 * Memory cache 먼저 체크하여 반영하거나 request 수행
 * Memory cache를 확인후 없으면 WebImageView 다운로드 로직 수행
 * @author : mgkim
 * @version : 1.0.0
 * @since : 2019-11-20 오후 10:51
 **/
class CahceWebImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebImageView(context, attrs, defStyleAttr) {

    /**
     * Memory cache확인 후 있으면 반영, 없으면 super() 수행
     */
    override fun checkRequestImage(url: String) {
        val fileName = getFileName(url)
        if (!fileName.isNullOrEmpty() && ImageCache.findCacheBitmap(fileName)) {
            super.applyImage(ImageCache.getBitmap(fileName), url, true)
        } else {
            super.checkRequestImage(url)
        }
    }

    /**
     * 이미지 load후 cache에 반영 후 super() 수행
     */
    override fun applyImage(bitmap: Bitmap?, url: String, isNoAnimation: Boolean) {
        if (bitmap != null) {
            getFileName(url)?.let {
                ImageCache.setBitmap(it, bitmap)
            }
        }
        super.applyImage(bitmap, url, isNoAnimation)
    }

}
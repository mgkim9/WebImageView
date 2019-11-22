package com.mgkim.libs.webimageview.widget

import android.graphics.Bitmap
import android.util.LruCache
/**
 * Memory cache
* 최대 가용 Memory의 1/8만큼만 저장가능
* @author : mgkim
* @version : 1.0.0
* @since : 2019-11-21 오후 7:55
**/
internal object ImageCache {
    private val cache: LruCache<String, Bitmap>

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() ushr 10).toInt()
        val cacheSize = maxMemory shr 3   //최대 memory에 1/8만 사용하도록
        cache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String?, value: Bitmap?): Int {
                return (value?.byteCount ?: 0) shr 10
            }
        }
    }

    fun findCacheBitmap(url: String): Boolean {
        return cache.get(url) != null
    }

    fun getBitmap(url: String): Bitmap? {
        return cache.get(url)
    }

    fun setBitmap(key: String, bitmap: Bitmap) {
        cache.put(key, bitmap)
    }

    fun clear() {
        cache.evictAll()
    }
}

package com.mgkim.libs.webimageview.widget

import android.graphics.Bitmap
import android.util.LruCache
import android.widget.ImageView
import com.mgkim.libs.webimageview.IRequest

/**
 * Memory cache
* 최대 가용 Memory의 1/8만큼만 저장가능
* @author : mgkim
* @version : 1.0.0
* @since : 2019-11-21 오후 7:55
**/
internal object ImageCache {
    private val TAG = javaClass.simpleName
    private val cache: LruCache<String, Bitmap>
    private val requestCache: LruCache<Int, IRequest<Bitmap?>> by lazy {
        LruCache<Int, IRequest<Bitmap?>>(100)
    }

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() ushr 10).toInt()
        val cacheSize = maxMemory shr 3   //최대 memory에 1/8만 사용하도록
        cache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String?, value: Bitmap?): Int {
                return (value?.byteCount ?: 0) shr 10
            }
        }
    }

    fun findCacheBitmap(key: String?): Boolean {
        return cache.get(key) != null
    }

    fun getBitmap(key: String?): Bitmap? {
        return cache.get(key)
    }

    fun setBitmap(key: String, bitmap: Bitmap) {
        cache.put(key, bitmap)
    }

    fun clear() {
        cache.evictAll()
    }

    fun findRequestCache(imageView: ImageView): Boolean {
        return requestCache.get(imageView.hashCode()) != null
    }

    fun getRequestCache(imageView: ImageView): IRequest<Bitmap?>? {
        return requestCache.get(imageView.hashCode())
    }

    fun setRequestCache(imageView: ImageView, request: IRequest<Bitmap?>) {
        requestCache.put(imageView.hashCode(), request)
    }

    fun removeRequestCache(imageView: ImageView) {
        requestCache.remove(imageView.hashCode())
    }

    fun clearRequestCache() {
        requestCache.evictAll()
    }
}

package com.mgkim.libs.webimageview

import android.graphics.Bitmap

/**
 * NetManager 설정 class
 * @author : mgkim
 * @version : 1.0.0
 * @since : 2019-11-26 오후 4:16
 * @param webImageViewConfig : WebImageView 설정
 * @param apiRequestConfig : APIRequest 설정
 * @param localRequestConfig : LocalRequest 설정
 **/
class NetManagerConfig(
    var webImageViewConfig: WebImageViewConfig = WebImageViewConfig(),
    var apiRequestConfig: APIRequestConfig = APIRequestConfig(),
    var localRequestConfig: LocalRequestConfig = LocalRequestConfig()
) {

    /**
     * BaseConfig 설정 base class
     * @author : mgkim
     * @version : 1.0.0
     * @since : 2019-11-26 오후 10:16
     * @param threadCount : WorkThread max count
     * @param isFIFO : RequestQueue 선입선출 여부
     **/
    abstract class BaseConfig(var threadCount: Int = 1, var isFIFO: Boolean = false)

    /**
     * WebImageView 설정 class
     * @author : mgkim
     * @version : 1.0.0
     * @since : 2019-11-26 오후 10:16
     * @param diskCacheOption : DiskCache Option #DiskCacheOption
     * @param isMemoryCache : MemoryCache 사용 여부
     * @throws preferredConfig : Bitmap preferredConfig
     * @param defaultImageResId : 기본 이미지 res id
     * @param failImageResId : Load 실패시 이미지 res id
     * @param animResId : 다운로드 성공 시 Animation
     * @param isResize : 이미지 다운로드 후 resize 여부
     * @param isBigSize : 큰 이미지 여부
     **/
    class WebImageViewConfig(
        var diskCacheOption: Int = DiskCacheOption.RESIZE_CACEH,
        var isMemoryCache: Boolean = true,
        var preferredConfig : Bitmap.Config = Bitmap.Config.RGB_565,
        var defaultImageResId: Int = -1,
        var failImageResId: Int = -1,
        var animResId: Int = -1,
        val progressResId: Int = -1,
        var isResize: Boolean = true,
        var isBigSize: Boolean = false,
        threadCount: Int = 3,
        isFIFO: Boolean = true
    ) : BaseConfig(threadCount, isFIFO)

    class APIRequestConfig(
        threadCount: Int = 2,
        isFIFO: Boolean = false
    ) : BaseConfig(threadCount, isFIFO)

    class LocalRequestConfig(
        threadCount: Int = 1,
        isFIFO: Boolean = false
    ) : BaseConfig(threadCount, isFIFO)

    /**
     * DiskCache Option
     * NO_DISK_CACEH : disk cache 미사용
     * ORIGINAL_CACEH : original file만 disk cache로 사용
     * RESIZE_CACEH : resize된 file만 disk cache로 사용(default)
     * ALL_DISK_CACEH : original file, resize file 모두 disk cache로 사용
     */
    object DiskCacheOption {
        const val NO_DISK_CACEH = 0b00
        const val ORIGINAL_CACEH = 0b01
        const val RESIZE_CACEH = 0b10
        const val ALL_DISK_CACEH = 0b11
    }
}
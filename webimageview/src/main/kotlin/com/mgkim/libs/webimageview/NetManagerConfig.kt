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
    fun clone(): NetManagerConfig = NetManagerConfig(
        webImageViewConfig.clone() as WebImageViewConfig
        , apiRequestConfig.clone() as APIRequestConfig
        , localRequestConfig.clone() as LocalRequestConfig
    )

    /**
     * BaseConfig 설정 base class
     * @author : mgkim
     * @version : 1.0.0
     * @since : 2019-11-26 오후 10:16
     * @param threadCount : WorkThread max count
     * @param isFIFO : RequestQueue 선입선출 여부
     **/
    abstract class BaseConfig(var threadCount: Int = 1, var isFIFO: Boolean = false) {
        abstract fun clone() : BaseConfig // clone
    }

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
     * @param roundedCornerPixel : 이미지 Corner Radius (roundedCornerPixel <= 0 || roundedCornerPixel >= Math.max(width, height) / 2) 이면 isCircular = true)
     * @param roundedCornerNoSquare : 이미지 곡선처리 미사용 여부 bitmask ( 0b0001(좌상단) | 0b0010(우상단) | 0b0100(좌하단) | 0b1000(우하단) )
     * @param isResize : 이미지 다운로드 후 resize 여부
     * @param isBigSize : 큰 이미지 여부
     **/
    class WebImageViewConfig(
        var diskCacheOption: Int = DiskCacheOption.RESIZE_CACEH,
        var isMemoryCache: Boolean = true,
        var preferredConfig : Bitmap.Config = Bitmap.Config.RGB_565,
        var defaultImageResId: Int = R.drawable.ic_default_picture,
        var failImageResId: Int = R.drawable.ic_frown,
        var animResId: Int = android.R.anim.fade_in,
        var progressResId: Int = -1,
        var isResize: Boolean = true,
        var isBigSize: Boolean = false,
        var roundedCornerPixel: Float = -1F,
        var roundedCornerNoSquare: Int = RoundedCornerSquare.CORNER_ALL,
        threadCount: Int = 3,
        isFIFO: Boolean = false
    ) : BaseConfig(threadCount, isFIFO) {
        override fun clone(): BaseConfig = WebImageViewConfig(
            diskCacheOption,
            isMemoryCache,
            preferredConfig,
            defaultImageResId,
            failImageResId,
            animResId,
            progressResId,
            isResize,
            isBigSize,
            roundedCornerPixel,
            roundedCornerNoSquare,
            threadCount,
            isFIFO
        )
    }

    class APIRequestConfig(
        threadCount: Int = 2,
        isFIFO: Boolean = true
    ) : BaseConfig(threadCount, isFIFO) {
        override fun clone(): BaseConfig = APIRequestConfig(
            threadCount,
            isFIFO
        )
    }

    class LocalRequestConfig(
        threadCount: Int = 1,
        isFIFO: Boolean = true
    ) : BaseConfig(threadCount, isFIFO) {
        override fun clone(): BaseConfig = LocalRequestConfig(
            threadCount,
            isFIFO
        )
    }

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

    /**
     * 이미지 곡선처리 방향
     * CORNER_ALL : 모든방향
     * TOP_LEFT : 좌상단
     * TOP_RIGHT : 우상단
     * BOTTOM_LEFT : 좌하단
     * BOTTOM_RIGHT : 우하단
     */
    object RoundedCornerSquare {
        const val CORNER_ALL = 0b0000
        const val TOP_LEFT = 0b0001
        const val TOP_RIGHT = 0b0010
        const val BOTTOM_LEFT = 0b0100
        const val BOTTOM_RIGHT = 0b1000
    }
}
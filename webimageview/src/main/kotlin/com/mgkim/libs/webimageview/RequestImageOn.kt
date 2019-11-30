package com.mgkim.libs.webimageview

import android.graphics.Bitmap
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.mgkim.libs.webimageview.utils.FormatUtil
import com.mgkim.libs.webimageview.widget.ImageCache

/**
 * Image를 받아오기위한 Request
 * @author : mgkim
 * @version : 1.0.4
 * @since : 2019-12-01 오전 5:55
 * @param url : Image Url
 * @param reqWidth : resize를 위한 image 너비
 * @param reqHeight : resize를 위한 image 높이
 * @param config : RequestImage 설정
 */
class RequestImageOn(
    url: String, reqWidth: Int = 0,
    reqHeight: Int = 0,
    config: NetManagerConfig.WebImageViewConfig = NetManager.config.webImageViewConfig
) : RequestImage(url, reqWidth, reqHeight, config) {

    /**
     * Cache된 Image혹은 다운로드 후 imageView에 apply 하는 함수
     * @param imageView : apply할 ImageView
     */
    fun into(imageView: ImageView) {
        if (config.isResize) {
            reqWidth = imageView.width
            reqHeight = imageView.height
        }
        ImageCache.getRequestCache(imageView)?.cancel()  // 해당뷰로 이미 Request가 있으면 Cancel요청
        if(config.isMemoryCache) { // memory cache hit
            FormatUtil.getFileName(url, reqWidth, reqHeight)?.let {
                FormatUtil.getRoundedCacheName(it, config.roundedCornerPixel).apply {
                    if (ImageCache.findCacheBitmap(this)) {
                        applyImage(imageView, ImageCache.getBitmap(this), true)
                        return
                    }
                }
            }
        }
        if (config.defaultImageResId != -1) {
            imageView.setImageResource(config.defaultImageResId)
        }
        ImageCache.setRequestCache(imageView, this as IRequest<Bitmap?>) // Request cache
        setReceiver{isSuccess, obj ->
            ImageCache.removeRequestCache(imageView)// Request cache remove
            if (isSuccess) {
                val requestImage = obj as RequestImage
                val bitmap = requestImage.getResult()
                if (bitmap != null) {
                    applyImage(imageView, bitmap, requestImage.isCacheHit)
                }
            } else {
                if (config.failImageResId != -1) {
                    imageView.setImageResource(config.failImageResId)
                }
            }
        }.useHandler().addReq()
    }

    /**
     * imageView에 적용
     * @param bitmap : 적용될 이미지
     * @param url : request url
     * @param isNoAnimation : Animation 여부
     */
    private fun applyImage(imageView: ImageView, bitmap: Bitmap?, isNoAnimation: Boolean = false) {
        imageView.setImageBitmap(bitmap)
        if (config.animResId != -1 && !isNoAnimation) {
            imageView.startAnimation(AnimationUtils.loadAnimation(imageView.context, config.animResId))
        } else {
            imageView.clearAnimation()
        }
    }

    /**
     * make rounded image
     * @param roundedCornerPixel : Corner Radius (roundedCornerPixel <= 0 || roundedCornerPixel >= Math.max(width, height) / 2) 이면 isCircular = true)
     * @param roundedCornerNoSquare : 이미지 곡선처리 미사용 여부 bitmask ( 0b0001(좌상단), 0b0010(우상단), 0b0100(좌하단), 0b1000(우하단)
     */
    fun makeRounded(roundedCornerPixel: Float, roundedCornerNoSquare: Int? = null): RequestImageOn {
        config.roundedCornerPixel = roundedCornerPixel
        roundedCornerNoSquare?.apply {
            config.roundedCornerNoSquare = this
        }
        return this
    }
}
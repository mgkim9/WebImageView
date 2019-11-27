package com.mgkim.libs.webimageview.widget

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import com.mgkim.libs.webimageview.*
import com.mgkim.libs.webimageview.utils.FormatUtil
import com.mgkim.libs.webimageview.utils.FormatUtil.getPxSize
import com.mgkim.libs.webimageview.utils.ImageUtil

/**
 * File cache 먼저 체크하여 반영하거나 request 수행
 * File cache 확인후 없으면 image를 다운로드하여 표시
 * @author : mgkim
 * @version : 1.0.0
 * @since : 2019-11-20 오후 1:55
 **/
open class WebImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), IResultReceiver<Bitmap?> {
    private val TAG: String = javaClass.simpleName
    private lateinit var ivImage: AppCompatImageView
    private lateinit var progress: ProgressBar
    private var req: IRequest<Bitmap?>? = null

    //attr start
    /**
     * 기본 이미지 res id
     * app:anim_id="@android:anim/ic_default_picture"
     */
    private val defaultImageResId: Int

    /**
     * Load 실패시 이미지 res id
     * app:default_image_id="@drawable/fade_in"
     */
    private val failImageResId: Int

    /**
     * 다운로드 성공 시 Animation
     * app:anim_id="@android:anim/fade_in"
     */
    private val animResId: Int

    /**
     * 로딩시 ProgressBar res id
     * app:progress_id="@android:drawable/progress_call"
     */
    private val progressResId: Int

    /**
     * DiskCache Option
     * no_disk_caceh#disk cache 미사용
     * original_caceh#original file만 disk cache로 사용
     * resize_caceh#resize된 file만 disk cache로 사용(default)
     * all_disk_caceh#original file, resize file 모두 disk cache로 사용
     * app:disk_cache_option="resize_caceh"
     */
    private val diskCacheOption: Int

    /**
     * MemoryCache 사용 여부
     * app:is_memory_cache="true"   (default : true)
     */
    private val isMemoryCache: Boolean

    /**
     * pixel 저장 방법
     * alpha_8
     * rgb_565 (default)
     * argb_8888
     * rgba_f16
     * hardware
     * app:preferred_config="rgb_565"
     */
    private val preferredConfig: Bitmap.Config


    /**
     * 이미지 다운로드 후 resize 여부
     * xml에 width와 height 값이 정의된 경우에 해당 size 에 맞게 resize
     * app:is_resize="true"
     */
    private val isResize: Boolean

    /**
     * 큰 이미지 여부
     * false : 최대 Device 사이즈보다 큰 이미지의 경우 Device 사이즈로 resize 함
     * true : 최대 메모리 사이즈(ImageUtil#MAX_NUM_PIXELS_MICRO_IMAGE) 보다 큰경우에만 resize 함
     * app:is_big_size="true"
     */
    private val isBigSize: Boolean

    /**
     * xml에 정의된 view width
     * android:layout_width="100dp"
     */
    private val layoutWidth: Int

    /**
     * xml에 정의된 view height
     * android:layout_height="100dp"
     */
    private val layoutHeight: Int
    //attr end

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.WebImageView, defStyleAttr, 0)
            .apply {
                val config = NetManager.config.webImageViewConfig   // global config
                defaultImageResId = getResourceId(R.styleable.WebImageView_default_image_id, config.defaultImageResId)
                failImageResId = getResourceId(R.styleable.WebImageView_fail_image_id, config.failImageResId)
                animResId = getResourceId(R.styleable.WebImageView_anim_id, config.animResId)
                progressResId = getInteger(R.styleable.WebImageView_progress_id, config.progressResId)
                diskCacheOption = getInteger(R.styleable.WebImageView_disk_cache_option, config.diskCacheOption)
                isMemoryCache = getBoolean(R.styleable.WebImageView_is_resize, config.isMemoryCache)
                preferredConfig = Constants.nativeToConfig(getInteger(R.styleable.WebImageView_preferred_config, config.preferredConfig.value))
                isResize = getBoolean(R.styleable.WebImageView_is_resize, config.isResize)
                isBigSize = getBoolean(R.styleable.WebImageView_is_big_size, config.isBigSize)
            }

        val layoutWidth:Int
        val layoutHeight:Int
        if(isResize) {
            layoutWidth = getPxSize(attrs?.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_width"))
            layoutHeight = getPxSize(attrs?.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height"))
        } else {
            layoutWidth = 0
            layoutHeight = 0
        }
        if (!isBigSize && layoutWidth == 0 && layoutHeight == 0) {
            //isBigSize 가 false인 경우 디바이스 사이즈보다 작게 resize한다
            ImageUtil.getDeviceDisplaySize(context).apply {
                this@WebImageView.layoutWidth = (x * Resources.getSystem().displayMetrics.density).toInt()
                this@WebImageView.layoutHeight = (y * Resources.getSystem().displayMetrics.density).toInt()
            }
        } else {
            this.layoutWidth = layoutWidth
            this.layoutHeight = layoutHeight
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        addView(LayoutInflater.from(context).inflate(R.layout.layout_web_image_view, this, false))
        ivImage = findViewById<View>(R.id.ivImage) as AppCompatImageView
        progress = findViewById<View>(R.id.progress) as ProgressBar
        if(progressResId != -1) {
            progress.indeterminateDrawable = context.getDrawable(progressResId)
        }
    }

    /**
     * 이미지 load
     */
    fun setUrl(url: String) {
        req?.apply {
            cancel()
            progress.visibility = View.GONE
        }
        if (defaultImageResId != -1) {
            setImageResource(defaultImageResId)
        }
        checkRequestImage(url)
    }

    /**
     * image request 시작
     */
    private fun checkRequestImage(url: String) {
        if(isMemoryCache) { // memory cache hit
            val fileName = getFileName(url)
            if (!fileName.isNullOrEmpty() && ImageCache.findCacheBitmap(fileName)) {
                applyImage(ImageCache.getBitmap(fileName), url, true)
                return
            }
        }

        progress.visibility = View.VISIBLE
        val config = NetManagerConfig.WebImageViewConfig(
            diskCacheOption = diskCacheOption,
            isMemoryCache = isMemoryCache,
            preferredConfig = preferredConfig,
            defaultImageResId = defaultImageResId,
            failImageResId = failImageResId,
            animResId = animResId,
            progressResId = progressResId,
            isResize = isResize,
            isBigSize = isBigSize
        )
        req = RequestImage(url, layoutWidth, layoutHeight, config).useHandler().setReceiver(this).addReq()
    }

    /**
     * imageView에 적용
     * @param bitmap : 적용될 이미지
     * @param url : request url
     * @param isNoAnimation : Animation 여부
     */
    private fun applyImage(bitmap: Bitmap?, url: String, isNoAnimation: Boolean = false) {
        if(isMemoryCache && bitmap != null) {
            getFileName(url)?.let { // memory cache set
                ImageCache.setBitmap(it, bitmap)
            }
        }
        ivImage.setImageBitmap(bitmap)
        progress.visibility = View.GONE
        if (animResId != -1 && !isNoAnimation) {
            startAnimation(AnimationUtils.loadAnimation(context, animResId))
        } else {
            clearAnimation()
        }
    }

    override fun onResult(isSuccess: Boolean, obj: IRequest<Bitmap?>) {
        if (isSuccess) {
            val requestImage = obj as RequestImage
            val bitmap = requestImage.getResult()
            if (bitmap != null) {
                applyImage(bitmap, requestImage.url, requestImage.isCacheHit)
            }
        } else { //fail image
            if (failImageResId != -1) {
                ivImage.setImageResource(failImageResId)
            }
            progress.visibility = View.GONE
        }
    }

    // imageView -start
    fun setImageBitmap(bitmap: Bitmap?) {
        ivImage.setImageBitmap(bitmap)
    }

    fun setImageResource(resId: Int) {
        ivImage.setImageResource(resId)
    }
    // imageView -end

    /**
     * 생성될 image file name
     */
    fun getFileName(url: String): String? {
        return FormatUtil.getFileName(url, layoutWidth, layoutHeight)
    }
}

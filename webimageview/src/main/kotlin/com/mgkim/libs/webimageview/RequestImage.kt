package com.mgkim.libs.webimageview

import android.graphics.Bitmap
import com.mgkim.libs.webimageview.utils.FormatUtil
import com.mgkim.libs.webimageview.widget.ImageFile
import okhttp3.Request
import okhttp3.Response
import java.io.InputStream

/**
 * Image를 받아오기위한 Request
 * @author : mgkim
 * @version : 1.0.0
 * @since : 2019-11-20 오후 1:55
 * @param url : Image Url
 * @param reqWidth : resize를 위한 image 너비
 * @param reqHeight : resize를 위한 image 높이
 * @param config : RequestImage 설정
 */
open class RequestImage(
    val url: String, private val reqWidth: Int = 0,
    private val reqHeight: Int = 0,
    protected val config: NetManagerConfig.WebImageViewConfig = NetManager.config.webImageViewConfig
) : RequestHttp<Bitmap?>() {
    /**
     * image가 저장될 file
     */
    private var imgFile: ImageFile? = null

    /**
     * preSend에서 cache hit 여부
     */
    var isCacheHit = false

    /**
     * 이미지 request 전 File cache check
     * @return : true : File cache 있음 sned() 미수행 , false : File cache 없음 sned() 수행
     * @throws : url이 잘못된 경우 NullPointerException 발생 가능
     */
    @Throws(NullPointerException::class)
    override fun preSend(): Boolean {
        FormatUtil.getFileName(url)!!.let {
            imgFile = ImageFile(it, reqWidth, reqHeight, config)
            isCacheHit = imgFile?.getBitmap() != null
        }
        return isCacheHit
    }

    /**
     * image 다운로드 후 File생성 및 Bitmap load
     */
    override fun onResult(res: Response): Boolean {
        var isSuccess = false
        res.use {
            val inputStream : InputStream? = res.body?.byteStream()
            inputStream?.use {
                isSuccess = imgFile?.writeFile(inputStream) ?: false
            }
        }
        return isSuccess
    }

    override fun getRequest(): Request = Request.Builder().url(url).build()

    override fun getResult(): Bitmap? {
        return imgFile?.getBitmap()
    }
}
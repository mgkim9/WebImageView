package com.mgkim.libs.webimageview

import android.content.Context
import android.os.Handler
import com.mgkim.libs.webimageview.utils.AssertUtil
import com.mgkim.libs.webimageview.widget.ImageCache
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File

/**
 * MainClass
 * 사용전 Application class 에서 init() 을 꼭 호출 해야함
 * @author : mgkim
 * @version : 1.0.0
 * @since : 2019-11-20 오후 2:27
 **/
object NetManager {
    private lateinit var context:Context
    private val PACKAGE_NAME : String by lazy {
        context.packageName
    }
    internal val cacheImgPath : String by lazy {
        context.cacheDir.path + "/Pictures/"
    }
    internal val mainHandler:Handler by lazy {
        Handler()
    }
    private val client by lazy {
        OkHttpClient()
    }
    private val imgThreadManager by lazy {
        ThreadManager(config.webImageViewConfig.threadCount, RequestQueue(config.webImageViewConfig.isFIFO))
    }
    private val apiThreadManager by lazy {
        ThreadManager(config.apiRequestConfig.threadCount, RequestQueue(config.apiRequestConfig.isFIFO))
    }
    private val localThreadManager by lazy {
        ThreadManager(config.localRequestConfig.threadCount, RequestQueue(config.localRequestConfig.isFIFO))
    }
    /**
    * 각종 Config
    **/
    internal var config = NetManagerConfig()
    fun init(context: Context, config:NetManagerConfig? = null) {
        this.context = context
        var cacheImgDir = File(cacheImgPath)
        if (!cacheImgDir.exists()) {
            cacheImgDir.mkdirs()
        }
        config?.let {
            this.config = it
        }
    }

    /**
     * http 통신 수행
     * @param req : okhttp3.Request
     * @return : okhttp3.Response
     */
    internal fun execute(req: Request): Response {
        return client.newCall(req).execute()
    }

    /**
     * Request 수행
     * @param : Request
     */
    internal fun addReq(req: IRequest<*>) {
        when(req) {
            is RequestImage -> imgThreadManager.addReq(req)
            is RequestAPI -> apiThreadManager.addReq(req)
            is RequestLocal -> localThreadManager.addReq(req)
        }
    }

    /**
     * 설정값 clone
     */
    fun getConfigClone(): NetManagerConfig = config.clone()

    /**
     * ImageCache clear
     */
    fun cacheClear() {
        ImageCache.clear()
    }

    /**
     * diskCache clear
     * This method should always be called on a background thread, since it is a blocking call.
     */
    fun diskCacheClear() {
        AssertUtil.assertBackgroundThread()
        var cacheFiles = File(cacheImgPath)
        cacheFiles.listFiles()?.forEach {
            it.delete()
        }
    }
}
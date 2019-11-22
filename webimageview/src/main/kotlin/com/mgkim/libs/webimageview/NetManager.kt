package com.mgkim.libs.webimageview

import android.content.Context
import android.os.Handler
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
    private lateinit var PACKAGE_NAME : String
    internal lateinit var cacheImgPath : String
    internal lateinit var cacheGifPath : String
    internal lateinit var mainHandler:Handler
    private val client by lazy {
        OkHttpClient()
    }
    private val imgThreadManager by lazy {
        ThreadManager(3, RequestQueue(false))
    }
    private val apiThreadManager by lazy {
        ThreadManager(2)
    }
    private val localThreadManager by lazy {
        ThreadManager(1)
    }

    fun init(context: Context) {
        PACKAGE_NAME = context.packageName
        cacheImgPath = context.cacheDir.path + "/Pictures/"
        cacheGifPath = context.cacheDir.path + "/Gifs/"
        mainHandler = Handler()
        var cacheImgDir = File(cacheImgPath)
        if (!cacheImgDir.exists()) {
            cacheImgDir.mkdirs()
        }
        var cacheGifDir = File(cacheGifPath)
        if (!cacheGifDir.exists()) {
            cacheGifDir.mkdirs()
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
     * ImageCache clear
     */
    fun cacheClear() {
        ImageCache.clear()
    }
}
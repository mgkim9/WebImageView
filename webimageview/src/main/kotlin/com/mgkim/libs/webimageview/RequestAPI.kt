package com.mgkim.libs.webimageview

import com.google.gson.Gson
import okhttp3.Headers
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

/**
 * Api 호출을 위한 Request
 * @author : mgkim
 * @version : 1.0.0
 * @since : 2019-11-20 오후 1:55
 * @param E : Api 결과물 Type
 * @param classNm : Api 결과물 Class Type
 * @param url : api Url
 * @param body : api body
 * @param headers : api headers
 */
class RequestAPI<E>(private val classNm: Class<E>, private val url:String, private val body:RequestBody? = null, private val headers: Headers? = null): RequestHttp<E>(){
    override fun preSend(): Boolean {
        return false
    }

    /**
     * api Request 결과물
     */
    private var resObj: E? = null

    override fun onResult(res: Response): Boolean {
        var isSuccess = false
        res.use {
            val resStr = res.body?.string()
            resStr?.apply {
                resObj = Gson().fromJson(resStr, classNm)
                isSuccess = true
            }
        }
        return isSuccess
    }

    override fun getRequest(): Request {
        return Request.Builder().apply {
            url(url)
            headers?.let {
                headers(it)
            }
            body?.let {
                post(it)
            }
        }.build()
    }
    override fun getResult(): E? {
        return resObj
    }
}
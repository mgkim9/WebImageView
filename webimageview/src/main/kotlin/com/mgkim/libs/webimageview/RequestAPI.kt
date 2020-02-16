package com.mgkim.libs.webimageview

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mgkim.libs.webimageview.utils.FormatUtil
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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
class RequestAPI<E>: RequestHttp<E>{
    private val classNm: Class<*>
    private val type: TypeToken<E>?
    private val url:String
    private val body:RequestBody?
    private val headers: Headers?

    constructor(type: TypeToken<E>, method:Method, url:String, body:Any? = null, headers: Headers? = null) {
        this.classNm = type.rawType as Class<*>
        this.type = type
        if(method == Method.POST) {
            this.url = url
            this.body = Gson().toJson(body).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        } else {
            this.url = if (body != null) "$url?${FormatUtil.mappingClass(body)}" else url
            this.body = null
        }
        this.headers = headers;
    }

    constructor(classNm: Class<E>, method:Method, url:String, body:Any? = null, headers: Headers? = null) {
        this.classNm = classNm;
        this.type = null
        if(method == Method.POST) {
            this.url = url
            this.body = Gson().toJson(body).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        } else {
            this.url = if (body != null) "$url?${FormatUtil.mappingClass(body)}" else url
            this.body = null
        }
        this.headers = headers;
    }
    constructor(classNm: Class<E>, url:String, headers: Headers? = null) {
        this.classNm = classNm;
        this.type = null
        this.url = url;
        this.body = null;
        this.headers = headers;
    }

    constructor(classNm: Class<E>, url:String) {
        this.classNm = classNm;
        this.type = null
        this.url = url;
        body = null;
        headers = null;
    }

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
                resObj = Gson().fromJson(resStr, if(type != null) type.type else classNm)
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
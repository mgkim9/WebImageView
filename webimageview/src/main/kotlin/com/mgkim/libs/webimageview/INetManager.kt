package com.mgkim.libs.webimageview

import okhttp3.Request
import okhttp3.Response

/**
 * NetManager interface
 * @author : mgkim
 * @version : 1.0.0
 * @since : 2019-11-20 오후 1:55
 */
interface INetManager {
    /**
     * Request 수행
     * @param req : okhttp3.Request
     * @return : okhttp3.Response
     */
    fun execute(req: Request): Response

    /**
     * Request add
     */
    fun addReq(req: IRequest<*>)
}
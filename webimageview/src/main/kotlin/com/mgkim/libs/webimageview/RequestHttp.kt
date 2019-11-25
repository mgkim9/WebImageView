package com.mgkim.libs.webimageview

import com.mgkim.libs.webimageview.utils.Log
import okhttp3.Response

/**
 * Http통신용 Request
 * @author : mgkim
 * @version : 1.0.0
 * @since : 2019-11-20 오후 1:55
 * @param E : Request 결과물 Type
 */
abstract class RequestHttp<E>: Request<E>() {
    /**
     * Http 결과를 받아서 후처리를 수행할 함수
     * @param res : okhttp3.Response
     * @return : 성공 여부
     */
    abstract fun onResult(res: Response): Boolean

    /**
     * Request 를 수행할 okhttp3.Request
     */
    abstract fun getRequest(): okhttp3.Request

    /**
     * send(본 작업) 을 수행하기 앞서 File/DB 등을 체크하기위한 전처리 함수
     * @return : true : send 수행하지 않고 notifyReceiver() 수행, false : send 수행
     */
    abstract fun preSend(): Boolean

    override fun send() {
        try {
            if(preSend()) {
                isSuccess = true
            } else {
                isSuccess = onResult(NetManager.execute(getRequest()))
            }
            if(isSuccess && !isCancel) {
                notifyReceiver()
            }
        } catch (e : Exception) {
            Log.e(TAG, "send error : $e")
        }
    }

    override fun failed() {
        isSuccess = false
        notifyReceiver()
    }
}
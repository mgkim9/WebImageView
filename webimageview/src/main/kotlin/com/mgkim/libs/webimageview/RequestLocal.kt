package com.mgkim.libs.webimageview

import com.mgkim.libs.webimageview.utils.Log

/**
 * Local 작업용 Request
 * @author : mgkim
 * @version : 1.0.0
 * @since : 2019-11-20 오후 1:55
 * @param E : Local 작업 결과물 Type
 */
class RequestLocal<E>: Request<E>() {
    /**
     * Local 작업 결과물
     */
    private var resObj: E? = null
    private var doInBackground: IDoInBackground<E>? = null
    fun setDoInBackground(receiver: IDoInBackground<E>): Request<E> {
        doInBackground = receiver
        return this
    }
    //lambda
    fun setDoInBackground(receiver: () -> E): Request<E> {
        this.doInBackground = object: IDoInBackground<E> {
            override fun doInBackground(): E {
                return receiver()
            }
        }
        return this
    }

    private fun doInBackground(): E? {
        return doInBackground?.doInBackground()
    }

    override fun send() {
        try {
            resObj = doInBackground()
            isSuccess = true
        } catch (e:Exception){
            errorMsg.append("send error : $e \n")
            exception = e
            Log.e(TAG, "send error : $e")
        }

        if(isSuccess) {
            notifyReceiver()
        }
    }
    override fun getResult(): E? = resObj
}
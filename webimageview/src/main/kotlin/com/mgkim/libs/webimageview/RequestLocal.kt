package com.mgkim.libs.webimageview

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

    private fun doInBackground(): E? {
        return doInBackground?.doInBackground()
    }

    override fun send() {
        try {
            resObj = doInBackground()
            isSuccess = true
        } catch (e:Exception){
            e.printStackTrace()
        }

        if(isSuccess) {
            notifyReceiver()
        }
    }
    override fun getResult(): E? = resObj
    override fun failed() {
        isSuccess = false
        notifyReceiver()
    }
}
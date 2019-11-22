package com.mgkim.libs.webimageview

import android.os.Handler

/**
 * 최상위 Rquest abstract class
 * @author : mgkim
 * @version : 1.0.0
 * @since : 2019-11-20 오후 1:55
 * @param E : Request 결과물 Type
 */
abstract class Request<E>: IRequest<E> {
    val TAG = javaClass.simpleName
    /**
     * Retry 횟수
     * Retry를 사용하려면 구현체에서 override하여 1 이상으로 설정 할것
     */
    protected open var needRetry = 0

    /**
     * onResult가 수행될 Handler
     */
    private var handler: Handler? = null

    /**
     * 결과를 mainHandler 로 전달 하려면 사용
     */
    fun useHandler() : Request<E> {
        if(handler == null) {
            handler = NetManager.mainHandler
        }
        return this
    }

    private var receiver : IResultReceiver<E>? = null
    fun setReceiver(receiver: IResultReceiver<E>) : Request<E> {
        this.receiver = receiver
        return this
    }

    abstract override fun send()
    override fun cancel() {
        isCancel = true
    }
    abstract override fun failed()
    override var isSuccess: Boolean = false
    override var isCancel: Boolean = false
    override fun needRetry(): Boolean = --needRetry > 0

    /**
     * 결과 전달
     */
    protected fun notifyReceiver() {
        receiver?.apply {
            if(handler != null) {
                handler?.post {
                    onResult(isSuccess, this@Request)
                }
            } else {
                onResult(isSuccess, this@Request)
            }
        }
    }

    /**
     * Request 수행
     */
    fun addReq(): IRequest<E> {
        NetManager.addReq(this)
        return this
    }

    fun release() {
        cancel()
        handler = null
        receiver = null
    }
}
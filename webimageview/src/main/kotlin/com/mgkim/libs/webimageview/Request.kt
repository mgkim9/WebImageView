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

    val errorMsg: StringBuilder by lazy { StringBuilder() }
    var exception: Exception? = null

    private var receiver : IResultReceiver<E>? = null
    fun setReceiver(receiver: IResultReceiver<E>) : Request<E> {
        this.receiver = receiver
        return this
    }
    //lambda
    fun setReceiver(receiver: (Boolean, Request<E>) -> Unit): Request<E> {
        this.receiver = object: IResultReceiver<E> {
            override fun onResult(isSuccess:Boolean, obj: Request<E>) {
                return receiver(isSuccess, obj)
            }
        }
        return this
    }

    abstract override fun send()
    override fun cancel() {
        isCancel = true
    }
    override fun failed() {
        isSuccess = false
        notifyReceiver()
    }
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
     * 결과를 mainHandler 로 전달 하려면 사용
     */
    fun useHandler() : Request<E> {
        if(handler == null) {
            handler = NetManager.mainHandler
        }
        return this
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
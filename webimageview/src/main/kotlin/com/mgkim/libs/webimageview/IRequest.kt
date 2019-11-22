package com.mgkim.libs.webimageview

/**
 * Request interface
 * @author : mgkim
 * @version : 1.0.0
 * @since : 2019-11-20 오후 1:55
 */
interface IRequest<E> {
    /**
     * WorkerThread 에서 수행 될 함수
     */
    fun send()

    /**
     * Request Cancel
     */
    fun cancel()

    /**
     * Fail 시 수행될 함수
     */
    fun failed()

    /**
     * 성공 여부
     */
    var isSuccess:Boolean

    /**
     * 취소 여부
     */
    var isCancel:Boolean

    /**
     * 재시도 여부
     */
    fun needRetry():Boolean

    /**
     * Request 결과물
     */
    fun getResult():E?
}

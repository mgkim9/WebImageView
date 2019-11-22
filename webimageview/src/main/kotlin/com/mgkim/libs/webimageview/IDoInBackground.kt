package com.mgkim.libs.webimageview

/**
 * LocalRequest 결과를 전달할 listener
 * @author : mgkim
 * @version : 1.0.0
 * @since : 2019-11-20 오후 1:55
 */
interface IDoInBackground<E> {
    /**
     * WorkerThread 에서 수행될 작업
     */
    fun doInBackground(): E
}

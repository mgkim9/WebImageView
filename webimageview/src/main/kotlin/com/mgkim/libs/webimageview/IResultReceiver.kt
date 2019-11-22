package com.mgkim.libs.webimageview

/**
 * Request 결과를 전달할 listener
 * @author : mgkim
 * @version : 1.0.0
 * @since : 2019-11-20 오후 1:55
 */
interface IResultReceiver<E> {
    /**
     * Request 결과를 전달할 callback
     * @param isSuccess : 성공 여부
     * @param obj : Request 자신 (obj.getResult() 로 결과물을 얻을 수 있다)
     */
    fun onResult(isSuccess:Boolean, obj: IRequest<E>)
}

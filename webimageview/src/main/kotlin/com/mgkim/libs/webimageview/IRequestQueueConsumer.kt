package com.mgkim.libs.webimageview

/**
 * Queue를 take하거나 WorkThread의 destroyed를 알리기위한 interface
 * @author : mgkim
 * @version : 1.0.0
 * @since : 2019-11-21 오후 8:10
 **/
interface IRequestQueueConsumer {
    /**
     * Queue에서 Request를 하나 꺼낸다
     **/
    fun take(): IRequest<*>?

    /**
     * WorkThread 종료 시 해야할 동작
     */
    fun destroyed()
}

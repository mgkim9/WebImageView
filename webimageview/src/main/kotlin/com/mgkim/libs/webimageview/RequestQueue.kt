package com.mgkim.libs.webimageview

import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit

/**
 * Rquest를 담아두는 Queue
 * LinkedBlockingDeque 으로 구현하였으며,
 * Queue가 모두 비워 진 후 WorkerThread가 바로 Destry되지 않도록 aliveTima만큼 대기
 * @author : mgkim
 * @version : 1.0.0
 * @since : 2019-11-20 오후 1:55
 * @param isFIFO : true = 선입선출, false = 후입선출
 * @param aliveTime(default : 10) : Queue가 모두 비워진 후 대기하는 시간(s 초)
 */
internal class RequestQueue(private val isFIFO: Boolean, private val aliveTime: Long = 10) {
    private val queue: BlockingDeque<IRequest<*>> by lazy {
        LinkedBlockingDeque<IRequest<*>>()
    }
    fun isEmpty():Boolean  = queue.isEmpty()
    fun add(req: IRequest<*>) {
        queue.add(req)
    }

    fun take(): IRequest<*>? {
        return if (aliveTime > 0) {
            if (isFIFO) queue.pollLast(aliveTime, TimeUnit.SECONDS) else queue.poll(aliveTime, TimeUnit.SECONDS)
        } else {
            if (isFIFO) queue.pollLast() else queue.poll()
        }
    }
}

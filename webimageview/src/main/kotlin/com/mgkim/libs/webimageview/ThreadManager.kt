package com.mgkim.libs.webimageview

import java.util.concurrent.atomic.AtomicInteger

/**
 * 지정된 갯수만큼 WorkThread를 생성해서 작업을 시작해주는 class
 * @author : mgkim
 * @version : 1.0.0
 * @since : 2019-11-21 오후 8:06
 * @param maxCnt : 최대로 생성될수 있는 WorkThread 갯수
 * @param queue : Request 가 저장될 Queue
 **/
internal class ThreadManager(private val maxCnt: Int, private val queue:RequestQueue = RequestQueue(true)): IRequestQueueConsumer {
    private val TAG = javaClass.simpleName
    @Volatile
    private  var curCnt: AtomicInteger = AtomicInteger(0)
    override fun take(): IRequest<*>? {
        return queue.take()
    }

    override fun destroyed() {
        curCnt.decrementAndGet()
    }

    fun addReq(req: IRequest<*>) {
        queue.add(req)
        if(!queue.isEmpty()) {  //대기하고 있던 WorkThread 있는지 체크
            synchronized(this) {
                if(curCnt.get() < maxCnt) {
                    WorkThread(this).start()
                    curCnt.incrementAndGet()
                }
            }
        }
    }
}

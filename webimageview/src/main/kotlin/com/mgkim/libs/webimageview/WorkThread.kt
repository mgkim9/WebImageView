package com.mgkim.libs.webimageview

/**
 * 작업 Thread
 * @author : mgkim
 * @version : 1.0.0
 * @since : 2019-11-20 오후 1:55
 * @param consumer : ThreadManager로부터 Queue에서 데이터를 받아오기위한 interface
 */
internal class WorkThread(private val consumer: IRequestQueueConsumer) : Thread() {
    override fun run() {
        var req: IRequest<*>?
        do {
            req = consumer.take() // Queue가 비어있으면 AliveTime만큼 지연이 발생한다
            if (req == null) {//Queue가 비었으면 작업 종료
                break
            }
            if (req.isCancel) { //isCancel이 호출되었으면 다음작업 탐색
                continue
            }
            req.send()  // 작업 시작
            if (req.isCancel) { //send() 작업중 isCancel이 호출되었으면 다음작업 탐색
                continue
            }
            if (!req.isCancel && !req.isSuccess) {  //실패한경우 needRetry 만큼 재시도
                while (!req.isCancel && !req.isSuccess && req.needRetry()) {
                    sleep(10)
                    req.send()
                    sleep(10)
                }
                if (req.isCancel) { //Retry 작업중 isCancel이 호출되었으면 다음작업 탐색
                    continue
                }
            }
            if (!req.isCancel && !req.isSuccess) { // Retry 횟수만큼 재시도 하였으나 실패한경우 failed 처리
                req.failed()
            }
        } while (req != null)
        consumer.destroyed() // 모든 작업이 종료 되었으므로 destroyed
        super.run()
    }
}

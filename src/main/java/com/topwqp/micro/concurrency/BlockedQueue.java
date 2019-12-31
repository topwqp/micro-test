package com.topwqp.micro.concurrency;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * MESA 管程里面，T2 通知完 T1 后，T2 还是会接着执行，
 * T1 并不立即执行，仅仅是从条件变量的等待队列进到入口等待队列里面。
 * 这样做的好处是 notify() 不用放到代码的最后，
 * T2 也没有多余的阻塞唤醒操作。但是也有个副作用，
 * 就是当 T1 再次执行的时候，可能曾经满足的条件，
 * 现在已经不满足了，所以需要以循环方式检验条件变量。
 * @param <T>
 */
public class BlockedQueue<T>{
//    final Lock lock = new ReentrantLock();
//    // 条件变量：队列不满
//    final Condition notFull = lock.newCondition();
//    // 条件变量：队列不空
//    final Condition notEmpty =
//            lock.newCondition();
//
//    // 入队
//    void enq(T x) {
//        lock.lock();
//        try {
//           // while (队列已满){
//            while(true){
//                // 等待队列不满
//                notFull.await();
//            }
//            // 省略入队操作...
//            //入队后,通知可出队
//            notEmpty.signal();
//        }finally {
//            lock.unlock();
//        }
//    }
//    // 出队
//    void deq(){
//        lock.lock();
//        try {
//            //while (队列已空){
//            while (true){
//                // 等待队列不空
//                notEmpty.await();
//            }
//            // 省略出队操作...
//            //出队后，通知可入队
//            notFull.signal();
//        }finally {
//            lock.unlock();
//        }
//    }
}

package com.topwqp.micro.concurrency;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author
 * @date 2019年12月26日16:08:17
 * @desc  锁的使用方法
 */
public class Counter {

    private final Lock lock = new ReentrantLock();

    private volatile int count;

    public void incr(){
        lock.lock();
        try {
            count++;
        }finally {
            lock.unlock();
        }
    }

    public int getCount(){
        return count;
    }

}

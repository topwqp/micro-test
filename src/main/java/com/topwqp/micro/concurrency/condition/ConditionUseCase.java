package com.topwqp.micro.concurrency.condition;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author wangqiupeng
 * @desc 条件变量使用
 * @date 2019年12月29日09:09:47
 */
public class ConditionUseCase {
    Lock lock = new ReentrantLock();

    Condition  condition = lock.newCondition();

    public void conditionWait() throws InterruptedException{
        lock.lock();
        try {
            condition.await();
        }finally {
            lock.unlock();
        }
    }

    public void conditionSignal() throws InterruptedException{
        lock.lock();
        try {
            condition.signal();
        }finally {
            lock.unlock();
        }
    }
}

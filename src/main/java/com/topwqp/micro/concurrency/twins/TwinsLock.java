package com.topwqp.micro.concurrency.twins;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedLongSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author wangqiupeng
 * @desc  AQS 自定义
 */
public class TwinsLock implements Lock {

    private final Sync sync = new Sync(2);

    private static final class Sync extends AbstractQueuedLongSynchronizer{
        Sync(int count){
            if (count <= 0){
                throw new IllegalArgumentException("count must large than zero");
            }
            setState(count);
        }


        @Override
        public long  tryAcquireShared(long reduceCount){
            for (;;){
                long current = getState();
                long  newCount = current - reduceCount;
                if (newCount < 0 || compareAndSetState(current,newCount)){
                    return newCount;
                }
            }
        }

        @Override
        public boolean tryReleaseShared(long returnCount){
            for (;;){
                long current = getState();
                long newCount = current + returnCount;
                if (compareAndSetState(current,newCount)){
                    return true;
                }
            }
        }
    }

    @Override
    public void lock() {
        sync.acquireShared(1);
    }

    @Override
    public void unlock() {
        sync.releaseShared(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }


    @Override
    public Condition newCondition() {
        return null;
    }
}

package com.topwqp.micro.concurrency.twins;


import org.junit.Test;

import java.util.concurrent.locks.Lock;

/**
 * @author  wangqiupeng
 * @desc
 * @date 2019年12月26日14:20:10
 */
public class TwinsLockTest {
    @Test
    public void test()   {
        final Lock lock  = new TwinsLock();
        class Worker extends Thread{
            @Override
            public void run(){
                while (true){
                    lock.lock();
                    try {
                        Thread.sleep(1000);
                        System.out.println(Thread.currentThread().getName());
                        Thread.sleep(1000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        lock.unlock();
                    }
                }
            }
        }
        //启动10个线程
        for (int i = 0; i < 10; i++){
            Worker w = new Worker();
            w.setName("Thread :" + i);
            w.setDaemon(true);
            w.start();
        }
        //没隔1秒换行
        for (int i = 0; i < 20 ; i++){
            try {
                Thread.sleep(1000);
                System.out.println();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

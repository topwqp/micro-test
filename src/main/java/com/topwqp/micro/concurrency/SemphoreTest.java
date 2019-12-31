package com.topwqp.micro.concurrency;

import java.util.concurrent.Semaphore;

public class SemphoreTest {

    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(1);
        class Worker extends Thread{
            @Override
            public void run(){
                while (true){
                    try {
                        semaphore.acquire();
                        Thread.sleep(1000);
                        System.out.println(Thread.currentThread().getName());
                        Thread.sleep(1000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        semaphore.release();
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
    }


}

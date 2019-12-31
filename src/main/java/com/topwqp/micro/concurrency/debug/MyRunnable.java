package com.topwqp.micro.concurrency.debug;


import java.util.concurrent.TimeUnit;

public class MyRunnable implements Runnable {

    @Override
    public void run() {
        Thread  currentThread = Thread.currentThread();
        System.out.println(currentThread.getName()  + "-------进入");
        try{
            TimeUnit.SECONDS.sleep(5);
        }catch(InterruptedException e){
            e.printStackTrace();
        }finally {
            System.out.println(currentThread.getName() + "----离开");
        }
    }


}

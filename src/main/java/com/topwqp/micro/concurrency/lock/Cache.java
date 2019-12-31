package com.topwqp.micro.concurrency.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author  wangqiupeng
 * @date  2019年12月29日07:34:55
 * @desc 可重入读写锁用法
 */
public class Cache {
    Map<String,Object> map = new HashMap<String,Object>();
    ReentrantReadWriteLock  readWriteLock = new ReentrantReadWriteLock();
    ReentrantReadWriteLock.ReadLock  readLock = readWriteLock.readLock();
    ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();

    public Object  get(String  key){
        readLock.lock();
        try{
            return map.get(key);
        }finally {
            readLock.unlock();
        }
    }

    public  void  put(String key,Object value){
        writeLock.lock();
        try{
            map.put(key,value);
        }finally {
            writeLock.unlock();
        }
    }

    public void clear(){
        writeLock.lock();
        try {
             map.clear();
        }finally {
            writeLock.unlock();
        }
    }


}

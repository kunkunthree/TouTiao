package com.xiepuxin.toutiao;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class MyThread extends Thread{
    private int tid;
    MyThread(int tid){
        this.tid = tid;
    }

    @Override
    public void run() {
        try{
            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000);
                System.out.println(String.format("T%d:%d",tid,i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

class Producer implements  Runnable{
    private BlockingQueue queue;
    Producer(BlockingQueue<String> queue){
        this.queue = queue;
    }
    @Override
    public void run() {
        try{
            for (int i = 0; i < 100; i++) {
                Thread.sleep(100);
                queue.offer(i+"");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
class Consumer implements Runnable{
    private BlockingQueue<String> queue;
    Consumer(BlockingQueue<String> queue){
        this.queue = queue;
    }
    @Override
    public void run() {
        try{
            while (true){
                System.out.println(Thread.currentThread() + " : " + queue.take() );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
public class MultiThread {
    private static int counter = 0;
    private static AtomicInteger atomicCounter = new AtomicInteger(0);

    public static void testWithAtomic(){
        for (int i = 0; i < 10; i++) {
            new Thread(){
                @Override
                public void run() {
                    for (int i = 0 ; i < 100 ; i++){
                        System.out.println("Atomic : "+atomicCounter.incrementAndGet());
                    }
                }
            }.start();
        }
    }

    public static void  testWithoutAtomic(){
        for (int i = 0; i < 100; i++) {
            new Thread(){
                @Override
                public void run() {
                    for (int i = 0 ; i < 100 ; i++){
                        System.out.println("Not Atomic : "+(++counter));
                    }
                }
            }.start();
        }
    }

    public static void testThread() {
        for (int i = 0; i < 10; i++) {
            new MyThread(i).start();
        }
    }

    public static void sleep(int mills) {
        try {
            //Thread.sleep(new Random().nextInt(mills));
            Thread.sleep(mills);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testExecutor(){
//        ExecutorService service = Executors.newSingleThreadExecutor();
        ExecutorService service = Executors.newCachedThreadPool();
        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    sleep(1000);
                    System.out.println("Executor1 : " + i);
                }
            }
        });
        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    sleep(1000);
                    System.out.println("Executor2 : " + i);
                }
            }
        });
        service.shutdown();
        while (!service.isTerminated()){
            sleep(1000);
            System.out.println("wait for termination");
        }
    }

    public static void testFuture(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                sleep(5000);
                return 1;
            }
        });
        service.shutdown();
        try{
            System.out.println(future.get());
            System.out.println(future.get(10000,TimeUnit.MILLISECONDS));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
//        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(10);
//        new Thread(new Producer(queue),"producer").start();
//        new Thread(new Consumer(queue),"consumer1").start();
//        new Thread(new Consumer(queue),"consumer2").start();
//        new Thread(new Consumer(queue),"consumer3").start();
//        testWithAtomic();
//        testWithoutAtomic();
//        testExecutor();
        testFuture();
    }


}

package org.umejug.java_9.reactive_streams;

import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IntegerSubscriber implements Flow.Subscriber<Integer> {

    private long received;
    private final String name;
    private final long chunkSize;
    private Flow.Subscription subscription;

    private final AtomicBoolean done = new AtomicBoolean(false);

    private Lock doneLock = new ReentrantLock();
    private Condition doneAlarm = doneLock.newCondition();

    public IntegerSubscriber(String name, long chunkSize) {
        this.name = name;
        this.chunkSize = chunkSize;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(chunkSize);
    }

    @Override
    public void onNext(Integer item) {
        ++received;
        // simulating some work load with a Thread.sleep()
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // ignore interruptions
        }
        subscription.request(chunkSize);
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println("done");
        done.set(true);
        signalDone();
    }

    private void signalDone() {
        doneLock.lock();
        try {
            doneAlarm.signal();
        } finally {
            doneLock.unlock();
        }
    }

    public void done() {
        subscription.cancel();
    }

    public void awaitDone() throws InterruptedException {
        doneLock.lock();
        try {
            if (!done.get()) {
                doneAlarm.await();
            }
        } finally {
            doneLock.unlock();
        }
        System.out.println(name + " received " + received + " values");
    }
}

package org.umejug.java_9.reactive_streams;

import org.umejug.java_9.reactive_streams.helper.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;

public class IntegerPublisher implements Flow.Publisher<Integer> {

    private ExecutorService executor = Executors.newFixedThreadPool(10);
    private List<Subscription<? super Integer>> suppliers = new ArrayList<>();

    @Override
    public void subscribe(Flow.Subscriber<? super Integer> subscriber) {
        if (subscriber == null) {
            throw new IllegalArgumentException("subscribe(subscriber): expected non-null subscriber, got subscriber = null");
        }

        Subscription<? super Integer> subscription = new Subscription<>(subscriber, executor);
        subscriber.onSubscribe(subscription);
        suppliers.add(subscription);
    }

    public void submit(Integer item) {
        suppliers.forEach(s -> {
            try {
                s.submit(item);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void done() {
        suppliers.forEach(Subscription::cancel);
        executor.shutdown();
    }
}

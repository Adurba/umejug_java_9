package org.umejug.java_9.reactive_streams.helper;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Subscription<T> implements Flow.Subscription {

    private static final int MAX_BUFFER_CAPACITY = 100;

    private final AtomicBoolean done = new AtomicBoolean(false);

    private final Flow.Subscriber<? super T> subscriber;
    private final OnDemandSupplier<T> supplier;

    public Subscription(Flow.Subscriber<? super T> subscriber, Executor executor) {
        this.subscriber = subscriber;
        this.supplier = new BlockingBackpreassureSupplier<T>(MAX_BUFFER_CAPACITY);

        Consumer<Stream<T>> consumeItems = items -> items.forEach(consumeItem());
        Runnable endProcessing = complete();

        executor.execute(() -> {
            while (!done.get()) {
                try {
                    Optional<Stream<T>> optionalItems = supplier.await();
                    optionalItems.ifPresentOrElse(consumeItems, endProcessing);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    if (done.compareAndSet(false, true)) {
                        subscriber.onError(e);
                    }
                }
            }
        });
    }

    private Consumer<T> consumeItem() {
        return item -> {
            try {
                this.subscriber.onNext(item);
            } catch (RuntimeException e) {
                if (done.compareAndSet(false, true)) {
                    this.subscriber.onError(e);
                }
            }
        };
    }

    private Runnable complete() {
        return () -> {
            if (done.compareAndSet(false, true)) {
                subscriber.onComplete();
            }
        };
    }


    @Override
    public void request(long n) {
        if (!done.get()) {
            try {
                supplier.request(n);
            } catch (RuntimeException e) {
                subscriber.onError(e);
            }
        }
    }

    @Override
    public void cancel() {
        supplier.done();
    }

    public void submit(T item) throws InterruptedException {
        supplier.submit(item);
    }
}

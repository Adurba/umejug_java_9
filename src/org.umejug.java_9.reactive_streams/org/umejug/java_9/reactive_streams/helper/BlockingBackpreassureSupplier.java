package org.umejug.java_9.reactive_streams.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class BlockingBackpreassureSupplier<T> implements OnDemandSupplier<T> {

    private final Lock supplierLock = new ReentrantLock();
    private final Condition supplierAlarm = supplierLock.newCondition();

    private final AtomicBoolean done = new AtomicBoolean(false);
    private final AtomicLong demand = new AtomicLong(0);

    private final BlockingQueue<T> queue;

    public BlockingBackpreassureSupplier(int maxBufferCapacity) {
        queue = new ArrayBlockingQueue<T>(maxBufferCapacity);
    }

    @Override
    public void request(long n) {
        if (n < 0) {
            throw new IllegalArgumentException("request(n): expected n >= 0, got n = " + n);
        }

        demand.getAndUpdate(d -> d + n > 0 ? d + n : Long.MAX_VALUE);
        awakeSupplier();
    }

    private void awakeSupplier() {
        supplierLock.lock();
        try {
            supplierAlarm.signal();
        } finally {
            supplierLock.unlock();
        }
    }

    /**
     * Submit the item to this supplier. This call will block if the buffer is full.
     *
     * @param item the item to submit
     */
    @Override
    public void submit(T item) throws InterruptedException {
        if (!done.get()) {
            queue.put(item);
            awakeSupplier();
        }
    }

    /**
     * Retrieve demanded items. This call will block until there are demand and items available.
     *
     * @return a stream of items
     */
    @Override
    public Optional<Stream<T>> await() throws InterruptedException {
        try {
            awaitSupplyAndDemand();

            if (done.get()) {
                return Optional.empty();
            }

            List<T> items = extractDemanded();
            demand.getAndUpdate(d -> d - items.size());

            return Optional.of(items.stream());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            done.set(true);
            throw e;
        }
    }

    @Override
    public void done() {
        done.set(true);
        awakeSupplier();
    }

    private void awaitSupplyAndDemand() throws InterruptedException {
        supplierLock.lock();
        try {
            while (!done.get() && (demand.get() == 0 || queue.size() == 0)) {
                supplierAlarm.await();
            }
        } finally {
            supplierLock.unlock();
        }
    }

    private List<T> extractDemanded() {
        long demanded = demand.get();
        List<T> items;
        int extract = queue.size();
        if (extract > demanded) {
            extract = (int) demanded;
        }

        items = new ArrayList<>(extract);
        queue.drainTo(items, extract);
        return items;
    }
}

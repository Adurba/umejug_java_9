package org.umejug.java_9.reactive_streams.helper;

import java.util.Optional;
import java.util.stream.Stream;

public interface OnDemandSupplier<T> {

    void request(long n);

    void submit(T item) throws InterruptedException;

    Optional<Stream<T>> await() throws InterruptedException;

    void done();
}

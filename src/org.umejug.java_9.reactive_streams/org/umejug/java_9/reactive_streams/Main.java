package org.umejug.java_9.reactive_streams;

import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        IntegerPublisher publisher = new IntegerPublisher();
        IntegerSubscriber subscriber1 = new IntegerSubscriber("subscriber 1", 1);
        IntegerSubscriber subscriber2 = new IntegerSubscriber("subscriber 2", 100);
        publisher.subscribe(subscriber1);
        publisher.subscribe(subscriber2);

        Stream.iterate(0, i -> i < 300, i -> i + 1).forEach(publisher::submit);
        publisher.done();
        subscriber1.awaitDone();
        subscriber2.awaitDone();
    }
}

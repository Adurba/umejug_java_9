#!/bin/sh

rm -rf mods/org.umejug.java_9.reactive_streams
javac -d mods/org.umejug.java_9.reactive_streams $(find src/org.umejug.java_9.reactive_streams -name *.java)
jar --create --file=mlib/org.umejug.java_9.reactive_streams.jar --main-class=org.umejug.java_9.reactive_streams.Main -C mods/org.umejug.java_9.reactive_streams .
java -p mlib -m org.umejug.java_9.reactive_streams


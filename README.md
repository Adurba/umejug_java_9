# UmeJUG Java 9
[Presentation (Google slides)](https://docs.google.com/presentation/d/1ymR_wJvIp3XoOg6Eiz0mTGHgYeqKBrFdR5btnfsS41k/edit?usp=sharing)

##Prerequisites
* Install Java 9
  * [General guide](https://jdk9.java.net/download/)
  * [Ubuntu](http://www.webupd8.org/2015/02/install-oracle-java-9-in-ubuntu-linux.html)
 
* Clone this repository

## JShell
```sh
jshell

jshell> /help
jshell> /exit
jshell> /imports
```

Test some of the examples from presentation:

```sh
List<String> immutableList = List.of("one","two","three");
immutableList.add("four");

Optional<Integer> opt1 = Optional.of(4)
opt1.ifPresentOrElse( x -> System.out.println("Result found: " + x), () -> System.out.println("Not Found."))

Stream<Integer> stream = Stream.of(1,2,3,4,5,6,7,8,9,10)

stream.takeWhile(x -> x < 4).forEach(a -> System.out.println(a))
stream.dropWhile(x -> x < 4).forEach(a -> System.out.println(a)) 

Supplier<Stream<Integer>> streamSupplier = () -> Stream.of(1,2,3,4,5,6,7,8,9,10);

IntStream.iterate(2, x -> x < 20, x -> x * x).forEach(System.out::println)

```

## Modules
${JAVA_HOME}/jmods  
/usr/lib/jvm/java-9-oracle/jmods  
java --list-modules


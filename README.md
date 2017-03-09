# UmeJUG Java 9

##Prerequisites
* Install Java 9
  * [General guide](https://jdk9.java.net/download/)
  * [Ubuntu](http://www.webupd8.org/2015/02/install-oracle-java-9-in-ubuntu-linux.html)
 
* Clone this repository

## JShell
```sh
jshell

jshell> /help
jshell> /open resource.txt
jshell> /methods /vars /types
jshell> streamS<tab>
jshell> /exit
```

Test some of the examples from presentation:

```sh
immutableList.add("four");

opt1.ifPresentOrElse( x -> System.out.println("Result found: " + x), () -> System.out.println("Not Found."))

stream.takeWhile(x -> x < 4).forEach(a -> System.out.println(a))
stream.dropWhile(x -> x < 4).forEach(a -> System.out.println(a)) 

IntStream.iterate(2, x -> x < 20, x -> x * x).forEach(System.out::println)

```

## Modules

###Check JDK modules
```sh
ls ${JAVA_HOME}/jmods  
ls /usr/lib/jvm/java-9-oracle/jmods  
java --list-modules
```

###Build dependent modules
```sh
#Build astro by defining module-info and java-files
javac -d mods/org.astro src/org.astro/module-info.java src/org.astro/org/astro/World.java

#Build greetings by adding path to astro-module
javac --module-path mods -d mods/com.greetings src/com.greetings/module-info.java src/com.greetings/com/greetings/Main.java

#Run greetings program by specifying module and package
java --module-path mods -m com.greetings/com.greetings.Main

#List modules in module greetings (class)
java --module-path mods --list-modules com.greetings

#Build all modules at the same time by specifying --module-source-path
javac -d mods --module-source-path src $(find src -name *.java)

#Build jar with version
jar --create --file=mlib/org.astro@1.0.jar --module-version=1.0 -C mods/org.astro .

#Alt1 - Build jar without version (top-level)
jar --create --file=mlib/com.greeting.jar -C mods/com.greetings .

#Alt1 - Run program by specifying module and Main.class
java --module-path mlib --module com.greetings/com.greetings.Main

#Alt2 - Build jar and specify main-class
jar --create --file=mlib/com.greeting.jar --main-class=com.greetings.Main -C mods/com.greetings .

# Alt2 - Run program by specifying module only
java -p mlib -m com.greetings

#List modules in module astro (jar) 
java -p mlib --list-modules org.astro
```

###Jlink
```sh
jlink -p /usr/lib/jvm/java-9-oracle/jmods:mlib --add-modules com.greetings --output greetingApp
greetingApp/bin/java --list-modules
greetingApp/bin/java -m com.greetings/com.greetings.Main
du -sh greetingApp
jlink --compress 2 --strip-debug -p /usr/lib/jvm/java-9-oracle/jmods:mlib --add-modules com.greetings --output greetingApp2
du -sh greetingApp2
jlink --compress 2 --strip-debug --launcher greetMe=com.greetings/com.greetings.Main  -p /usr/lib/jvm/java-9-oracle/jmods:mlib --add-modules com.greetings --output greetingApp3
greetingApp3/bin/greetMe

https://bugs.openjdk.java.net/browse/JDK-8156903
jlink --vm=minimal --compress 2 --strip-debug  -p /usr/lib/jvm/java-9-oracle/jmods:mlib --add-modules com.greetings --output greetingApp4
jlink --vm=server --compress 2 --strip-debug  -p mlib --add-modules com.greetings --output greetingApp4
```

####Module dependendcies
```sh
greetingApp/bin/java -Xdiag:resolver -m com.greetings/com.greetings.Main
greetingApp/bin/java -Xlog:modules=debug -m com.greetings/com.greetings.Main
java -Xdiag:resolver --module-path mlib --module com.greetings/com.greetings.Main
```

###Jmod
```sh
jmod create --class-path mods/com.greetings greet.jmod
jmod create --class-path mods/org.astro astro.jmod
unzip -l greet.jmod
greetingApp4/bin/java -m com.greetings/com.greetings.Main
```

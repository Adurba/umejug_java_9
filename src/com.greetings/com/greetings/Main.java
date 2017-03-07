package com.greetings;
public class Main {
  public static void main(String[] args) {
    System.out.println("Greetings!");
  }
}

// javac -d mods/com.greetings src/com.greetings/module-info.java src/com.greetings/com/greetings/Main.java
// java --module-path mods -m com.greetings/com.greetings.Main
// java --module-path mods --list-modules com.greetings
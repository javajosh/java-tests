package com.javajosh.javatests;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.IntBinaryOperator;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * We are testing both java and javac. Javac "tests" are implicit in the build, and they merely say that the
 * source code is valid. However, runtime tests check if our mental model of what the code does is correct.
 * <p>
 * In java, we organize code in class files, and further into methods. Much of what I want to do is easily
 * encapsulated in a method, but we need a way to run all the methods in a class. This is a very un-Java thing
 * to want! Luckily tooling can help us as we can start each test with a method call in main, and then allow
 * the tooling to create it for us.
 *
 * @author Josh Rehman
 * @version 0.1, 05/21/2017
 */
public class Main {

  static int count = 0;
  static boolean showThreadLogs = false;

  public static void log(String method, String msg) {
    if (method.startsWith("threads") && !showThreadLogs) return;
    System.out.printf("%d %2$s: %3$s \n", count++, method, msg);
  }

  public static void main(String[] args) {
    log("main", "Welcome to Java Tests! showThreadLogs = " + showThreadLogs);

    initializers();
    loops();
    iterators();
    strings();
    threads();
    forkJoin();
    abstractClasses();
    generics();
    lambdas();
    enums();
    reflection();
    tricks();
    bubble();
    adders();
    dateTime();
    kotlin();
    exit();
  }

  private static void kotlin() {

  }


  private static void dateTime() {
    LocalDateTime a = LocalDateTime.now();

    LocalDate b = LocalDate.of(2015, Month.APRIL, 10);
    LocalDate c = LocalDate.ofYearDay(2017, 123);
    LocalTime d = LocalTime.of(11, 48);
    LocalTime e = LocalTime.parse("11:48");

    LocalDate f = a.toLocalDate();
    Month month = f.getMonth();
    int second = a.getSecond();

    LocalDateTime g = a.plusDays(10);

    Period period = Period.of(0,9,0);
    LocalDate h = f.plus(period);

  }

  /**
   * New concurrent counters
   */
  private static void adders() {
    //We used to use AtomicLongCounter - uses sun.misc.Unsafe underneath to compare and swap.
    //Will spin under heavy contention.
    AtomicLong counter = new AtomicLong();
    counter.incrementAndGet();
    counter.incrementAndGet();
    assert counter.get() == 2L : "counter should be 2 but was " + counter.get();

    //Now we use LongAdder - doesn't spin, just saves delta and adds it along with other writes.
    //See: https://www.infoq.com/articles/Java-8-Quiet-Features/
    LongAdder adder = new LongAdder();
    adder.increment();
    adder.increment();
    assert adder.longValue() == 2L : "adder should be 2 but was " + adder.longValue();

  }

  private static void exit() {
    //This test has to go last because it exits the system, to demonstrate that finally doesn't execute.
    try{
      Thread.sleep(1000);
      System.exit(0); //finally will not execute.
      throw new RuntimeException();
    } catch (Throwable e){

    } finally {
      log("tricks/finally", "hello from the finally block");
    }
  }

  /**
   * Implement a bubble sort. Note this is O^n2, although this implementation hides that.
   */
  private static void bubble() {
    int[] arr = {5,21,8,9,3,2};
    int tmp;
    boolean flag = true;

    while(flag) {
      flag = false;
      for (int i = 0; i < arr.length-1; i++) {
        if (arr[i] > arr[i+1]){
          //swap
          tmp = arr[i];
          arr[i] = arr[i+1];
          arr[i+1] = tmp;
          flag = true;
        }
      }
    }

    log("bubble",  Arrays.toString(arr));

  }

  private static void tricks() {
    assert Math.min(Double.MIN_VALUE, 0.0d) == 0.0d : "Unlike Integer.MIN_VALUE, Double.MIN_VALUE is very small, but positive (2^(-1074))";

    assert 1.0/0.0 == Double.POSITIVE_INFINITY : "Double division by zero gives infinity, not an exception";

    //swap two strings without a third variable
    String a = "one";
    String b = "two";

    a = a + b;
    b = a.substring(0, (a.length() - b.length()));
    a = a.substring(b.length());
    assert a.equals("two") && b.equals("one") : "a should be two and b should be one";


    class A{
      private int i = 0;
      int get(){return this.i;}
    }
    class B extends A{
      private int i = 1;
    }

    assert new B().get() == 0: "you can't override private or static fields, but you can hide the parent method";

  }

  private static void reflection() {
    Person person = new Person("Doug");

    Map<String, Object> values = new HashMap<>();
    for (Field field : person.getClass().getDeclaredFields()) {
      try {
        field.setAccessible(true); //without this we can't get the private field.
        values.put(field.getName(), field.get(person));
      } catch (IllegalAccessException ignored) {}
    }

    try {
      Method m = Person.class.getDeclaredMethod("getName");
      m.setAccessible(true);
      String result = (String) m.invoke(person);
      assert result.equals("Doug") : "Expected the name to be Doug but was " + result;
    } catch (Throwable rethrown) {
      throw new RuntimeException("Problem with reflection", rethrown);
    }

    assert values.toString().equals("{name=Doug}") : "but was " + values.toString();

    log("reflection", "complete");
  }

  static enum Action {
    ADD("Add", (a, b) -> a + b),
    SUB("Subtract", (a, b) -> a - b),
    MUL("Multiply", (a, b) -> a * b),
    DIV("Divide", (a, b) -> a / b);

    String desc;
    IntBinaryOperator op;

    Action(String desc, IntBinaryOperator op) {
      this.desc = desc;
      this.op = op;
    }

    int compute(int a, int b) {
      return op.applyAsInt(a, b);
    }

  }

  private static void enums() {
    //An enum is basically a final class with a fixed number of instances.
    int a = Action.ADD.compute(2, 4);
    assert a == 6 : "Should be 6 but was " + a;

    log("enums", "complete");

  }

  static class Person {
    private final String name;

    Person(String name) {
      this.name = name;
    }

    String getName() {
      return this.name;
    }
  }

  /**
   * Lot's of funky lambda stuff to test!
   * https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
   */
  private static void lambdas() {

    List<String> names = asList("Alice", "Bob", "Charlie");

    //This is a method reference to a constructor
    List<Person> people = names.stream().map(Person::new).collect(toList());

    assert people.size() == 3 : "People size should be 3";
    assert people.get(0).getName().equals("Alice");

    log("lamdas", "complete");
  }


  private static void generics() {

    class Product {
      //This is called a covariant type, and accepts a list of anything subclassing Number.
      double product(List<? extends Number> numbers) {
        double result = 1;
        for (Number n : numbers) {
          result *= n.doubleValue();
        }
        return result;
      }

      //This is an interesting example of a backed map, one that modifies what it wraps.
      public <K, V> Map<K, V> whitelist(Map<K, V> map, K... allowedKeys) {
        Map<K, V> copy = new HashMap<>(map);
        copy.keySet().retainAll(asList(allowedKeys));
        return copy;
      }
    }

    List<Long> longs = asList(2L, 4L, 8L);
    double productOfLongs = new Product().product(longs);
    assert productOfLongs == 2 * 4 * 8 : "Product should be 64 but was " + productOfLongs;


    log("generics", "complete");

  }

  private static void abstractClasses() {

    abstract class Foo {
      protected final int value;

      Foo() {
        this.value = get();
      }

      abstract int get();
    }
    class Bar extends Foo {
      int val2;

      Bar(int value) {
        this.val2 = value;
      }

      int get() {
        return val2;
      }
    }

    assert new Bar(2).get() == 2 : "Actually I'm really surprised this is correct, since Foo() executes before Bar()";
    log("abstractClasses", "complete");

  }

  private static void forkJoin() {
    //TODO - implement a RecursiveTask
    log("forkJoin", "not implemented");
  }

  /**
   * Playing around with old-school Java concurrency structures.
   */
  private static void threads() {

    // Log, wait a sec, then log again.
    final Thread a = new Thread(() -> {
      log("threads/a", "start");
      try {
        Thread.sleep(1000);
      } catch (InterruptedException ignored) {
      }
      log("threads/a", "end");
    });

    //Log, wait half a sec, then interrupt thread a, then log.
    Thread b = new Thread(() -> {
      log("threads/b", "start");
      try {
        Thread.sleep(500);
        a.interrupt();
        log("threads/b", "interrupted threads/a");
      } catch (InterruptedException ignored) {
      }
      log("threads/b", "end");
    });

    //Log, join a, then log again, and log again.
    Thread c = new Thread(() -> {
      log("threads/c", "start");
      try {
        a.join();
        log("threads/c", "joined threads/a");
      } catch (InterruptedException ignored) {
      }
      log("threads/c", "end");
    });

    a.start();
    b.start();
    c.start();

    //Now lets test wait and notify
    //This is a simple mutable shared object
    class Shared {
      private long i = 0;

      long getI() {
        return i;
      }

      void setI(long i) {
        this.i = i;
      }
    }
    ;
    final Shared shared = new Shared();

    //This thread increments I every 100ms, and notifies any waiting threads that the object has been changed.
    final Thread d = new Thread(() -> {
      log("threads/d", "start");
      int time = 0;
      try {

        while (true) {
          Thread.sleep(100);
          synchronized (shared) {
            shared.setI(time++);
            shared.notifyAll();
          }
          log("threads/d", "called notify");
        }

      } catch (InterruptedException ignored) {
      }
      log("threads/d", "end");
    });

    //This thread waits for thread d to modify the object.
    Thread e = new Thread(() -> {
      log("threads/e", "start");
      int i = 10;
      while (i-- != 0)
        try {
          synchronized (shared) {
            shared.wait();
            log("threads/e", "wait returned " + shared.getI() + " " + ThreadLocalRandom.current().nextInt(1, 100));
          }
        } catch (InterruptedException ignored) {
          log("threads/e", "interrupted " + shared.getI());
        }
      d.interrupt();
      log("threads/e", "end");
    });
    d.start();
    e.start();

    //Volatile is interesting, because it's mainly useful for private static fields, mainly flags
    //who's value don't depend on previous values. We can rewrite threads d and e to use a shared flag.
    //However we can't define static variables in inner classes, so skip it.

    //Catch an exception in another thread using an uncaught exception handler.
    Thread f = new Thread(() -> {
      log("threads/f", "start");
      try {
        Thread.sleep(750);
        throw new RuntimeException("Exception thrown from f");
      } catch (InterruptedException e1) {
      }
      log("threads/f", "end"); //this is never reached
    });
    //The type here is Thread.UncaughtExceptionHandler
    f.setUncaughtExceptionHandler((t, e1) -> log("threads/handler", e1.getMessage()));
    f.start();

    log("threads", "complete (first-pass)");

    //TODO: Explore StampedLock.

  }


  private static void initializers() {
    int[] a = new int[10];
    int[] b = {3, 4, 6, 3, 4};
    int[] c = new int[]{5, 6, 7, 1, 3};

    int[][] d = {{1, 6}, {2, 7}, {3, 8}, {4, 9}, {5, 10}};
    log("initializers", "complete");
  }


  private static void loops() {
    int[] a = new int[10];

    for (int i = 0; i < a.length; i++) {
      int elt = a[i];
      assert elt == 0 : "Each element should be 0";
    }

    for (int elt : a) {
      assert elt == 0 : "Each elt should be 0";
    }

    log("loops", "complete");
  }

  private static void iterators() {
    //Fail-fast - throw an exception if concurrently modified.
    List<Integer> a = new ArrayList();
    Set<Integer> b = new HashSet();
    Map<String, Integer> c = new HashMap();

    //Let's work with ArrayList.
    assert a.size() == 0 : "list size should be 0";
    a.add(1);
    a.add(2);

    try {
      a.add(3, 3);
      assert false : "Cannot add by index unless the array is large enough";
    } catch (IndexOutOfBoundsException expected) {
    }
    a.add(1, 4); //will shift the current elt by one

    assert a.size() == 3 : "list size should be 2";
    assert a.contains(1) : "list should contain 1";

    boolean changed = a.add(1);
    assert changed : "add should return false when the collection didn't change";


    try {
      for (int elt : a) {
        a.remove(elt);
      }
      assert false : "Cannot remove an element within an iterator";
    } catch (ConcurrentModificationException expected) {
    }

    //An explicit iterator in a while loop
    Iterator<Integer> i = a.iterator();
    while (i.hasNext()) {
      assert i.next() != 0;
    }

    //Fail-safe operates on a cloned copy
    Map<String, Integer> d = new ConcurrentHashMap();
    List<Integer> e = new CopyOnWriteArrayList<>();
    log("iterators", "complete");

    assert e.size() == 0 : "Map should have no entries";
    e.add(1);
    e.add(2);
    e.add(3);
    assert e.size() == 3 : "Map should have three entries " + e.size();

    //This does not function as expected. TODO: figure out why this doesn't work.
//    for (int elt: e){
//      e.remove(elt);
//    }
    e.clear();
    assert e.size() == 0 : "Map should have no entries";
  }

  private static void strings() {
    //Strings are stored in the heap, references in the stack.
    //Prior to Java 1.7 interned strings were stored in permgen.
    String a = "This is stored in the heap.";
    //Character arrays are not stored in the heap, only in the stack
    char[] b = a.toCharArray();
    b[3] = 'X';
    log("strings", new String(b));

    //How much faster is StringBuilder to StringBuffer?
    //StringBuilder isn't synchronized, which is great if you're building a response in a single thread.
    //On my machine I get 400ms vs 80ms, which is a factor of 5.
    long start = System.currentTimeMillis();
    StringBuffer sb = new StringBuffer();
    int i = 10*1000*1000;
    while (i-- > 0) sb.append('a');
    long stop = System.currentTimeMillis();
    log("strings", "StringBuffer duration was " + (stop - start));

    start = System.currentTimeMillis();
    StringBuilder sb2 = new StringBuilder();
    i = 10*1000*1000;
    while (i-- > 0) sb2.append('a');
    stop = System.currentTimeMillis();
    log("strings", "StringBuilder duration was " + (stop - start));


    log("strings", "complete");
  }

}

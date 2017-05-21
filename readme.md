#Java Tests

2017 May 21, Josh Rehman <josh@joshrehman.com>

Java Tests (github.com/javajosh/java-tests) is a project that exercises elements of the Java language. The `master`
branch should always be compatible with the latest production release of Oracle's JVM.

Because I am only going to test the core language and standard libraries, we aim for zero runtime dependencies. We will
eschew even the nicities of testing frameworks like JUnit and Spock in order to keep things clear.

The basic Java runtime is a single thread moving through the main method. For long-running applications, aka daemons or servers,
this phase happens only once and very quickly, setting up for subsequent steady-state input processing.

For simplicity, and to keep things focused primarily on the language itself, we start off with only a very small subset.

That said, I've still opted to manage the project life-cycle with Maven, which is overly complex and difficult to work with,
but also offers familiarity to anyone who wants to check out and build the project. Frankly I'd prefer to publish this
with no build-tool at all, but this choice is comforting to some.

## What to test?

One of the more interesting ways to test things is to read the specification. Often corner-cases or funny observations
occur to an active reader, and these are pointers to a good test! Other times, questions arise during the execution of a project.
Sometimes we just want to demonstrate what we know.



## Adding runtimes

Different versions of the Java Virtual Machine exist in the wild, differing both in standard compliance and implementation.
For example, the JRE 1.5 is still in wide-spread use; two popular implementations exist Oracle and OpenJDK, among others.

I won't worry too much about this.


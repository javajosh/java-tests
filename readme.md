#Java Tests

2017 May 21, Josh Rehman <josh@joshrehman.com>

Java Tests (github.com/javajosh/java-tests) is a project that exercises elements of the Java language. The `master`
branch should always be compatible with the latest production release of Oracle's JVM (that's 1.8 as of this writing).

Because I am only going to test the core language and standard libraries, we aim for zero runtime dependencies. We will
eschew even the nicities of testing frameworks like JUnit and Spock in order to keep things clear.

Despite being mostly minimalist, I've opted to manage the project life-cycle with Maven, which is overly complex and difficult to work with,
but also offers familiarity to anyone who wants to check out and build the project. (Frankly I'd prefer to publish this
with no build-tool at all, but I'd like the project to be accessible and friendly, too.)

## How to use it?

If you have Java and Maven installed, and you're running on either Linux or macOS: checkout the code from github,
then run `run.sh` from the command line. This will run maven and invoke the projects main class. Also, if you're running
a recent copy of IntelliJ I've included project files so you can get going quickly.

## How does it work?

The basic Java runtime is a single thread moving through the main method. For long-running applications, aka daemons or servers,
this phase happens only once and very quickly, setting up for subsequent steady-state input processing. (For the essentially doing one-shot
tests, with the key exception of some of the threading tests).


## What to test?

One of the more interesting ways to test things is to read the specification. Often corner-cases or funny observations
occur to an active reader, and these are pointers to a good test! Other times, questions arise during the execution of a project.
Sometimes we just want to demonstrate what we know. I find myself first writing tests for a variety of things, including
loops, iterators, enums, threads, strings, lambdas, reflection, and more.


## Adding runtimes

Different versions of the Java Virtual Machine exist in the wild, differing both in standard compliance and implementation.
For example, the JRE 1.5 is still in wide-spread use; two popular implementations exist Oracle and OpenJDK, among others.
For now, this project only officially supports Oracle Java 1.8 (and Maven 3.3.6 or greater).

# JBanging Quarkus - Having Quarkus fun with Java scripting

## Intro

The script here automates building and testing of Quarkus and related projects.
At a high level, the works is divided into `build` and `test` phases.
Each of these steps has extra options that allows it to expand its work.

## Dependencies

Any dependencies required are installed by the script itself.
In particular, it installs:

* [sdkman](https://sdkman.io/) - a tool for managing parallel versions of multiple SDKs.
* Latest AdoptOpenJDK boot JDK - used to build the base JDK for GraalVM. Installed by `sdkman`.
* AdoptOpenJDK 14 - JDK version required to run the main Java script. Installed by `sdkman`.
* JBang - drives the execution of the main Java script. Installed by `sdkman`.
* Maven - to build and test Maven projects. Installed by `sdkman`.

### Why not Docker? 

Docker could be used to provide a similar environmen with all the dependencies set.
However, running Quarkus native tests on Docker is several times slower.
My home server is 8y+ old, so I didn't want to pay that cost.
Tools like [sdkman](https://sdkman.io/) make it easy to handle dependencies in a platform neutral way.

## Build Quarkus

To build Quarkus, execute:

```bash
$ ./quarkus.sh build
```

In the `build` step, the script first downloads the source code for:

* JDK required to build GraalVM.
* [mx](https://github.com/graalvm/mx).
* GraalVM.
* Quarkus.

Then, it builds them in the order above.

Each of these source dependencies can be tailored to download different repositories and branches.
As an example, the default JDK source tree downloaded is the one for the latest tag for Oracle Labs JDK 11.
Alternatively, you could use OpenJDK 11 update repository by calling:

```bash
./quarkus.sh build \
    --jdk-tree https://github.com/openjdk/jdk11u-dev/tree/master
```

Other times, you might want to test fixes for either GraalVM or Quarkus.
You can provide alternative repository links for them by doing:

```bash
./quarkus.sh build \
    --graal-tree https://github.com/galderz/graal/tree/t_catch_noclassdef_error
    --quarkus-tree https://github.com/galderz/quarkus/tree/t_7422_8081 
```

There might situations where you need a fix for one, or several,
dependencies of Quarkus.
To handle this situation, you can instruct the script to build a set of projects,
before building Quarkus:

```bash
./quarkus.sh build 
    --quarkus-tree https://github.com/galderz/quarkus/tree/t_7422_8081
    --pre-build https://github.com/quarkusio/quarkus-spring-api/tree/http-method-again
```

In the above case, the given `quarkus-spring-api` branch should be built before Quarkus.

Another common situation is to build extra projects after building Quarkus, e.g. Camel Quarkus.
You can instruct the script to build additional projects via:

```bash
./quarkus.sh build 
    --post-build https://github.com/apache/camel-quarkus/tree/master
```

More options might be added in the future.
You can find out about new options by caling:

```bash
./quarkus.sh build --help
...
```

## Test Quarkus

To test Quarkus, execute:

```bash
$ ./quarkus.sh test
```

By default it executes the main Quarkus native testsuite.

Supplementary test suites can be executed on top of the main Quarkus native one by doing:

```bash
$ ./quarkus.sh test \
    --also-test https://github.com/quarkusio/quarkus-platform/tree/master
```

In the example above, 
the script requests `quarkus-platform` testsuite to be executed after the main Quarkus native one.

Alternatively, 
you can instruct the script to only execute the `quarkus-platform` testsuite,
and ignore executing the main Quarkus native one:

```bash
$ ./quarkus.sh test \
    --suites quarkus-platform
    --also-test https://github.com/quarkusio/quarkus-platform/tree/master
```

Some times you might want to pass extra Maven parameters to the testing phase.
For example, you might want to test a fix, and you want to execute the tests starting at a particular project.
You can do so by calling:

```bash 
./quarkus.sh test \
    --additional-test-args quarkus=-rf,:quarkus-integration-test-tika
```

The additional test arguments need to be provided for a given suite.
Hence, adding `quarkus=` means the flags are for the main Quarkus native test execution.
If testing `quarkus-platform` repository, additional test arguments could be passed to that via:

```bash
./quarkus.sh test \
    --suites quarkus-platform
    --also-test https://github.com/quarkusio/quarkus-platform/tree/master
    --additional-test-args quarkus-platform=-rf,:quarkus-universe-integration-tests-camel-aws
```

If testing both main Quarkus native tests and `quarkus-platform`,
you can pass extra arguments to each using multiple occurrences of the same parameter:

```bash
./quarkus.sh test \
    --also-test https://github.com/quarkusio/quarkus-platform/tree/master
    --additional-test-args quarkus=-rf,:quarkus-integration-test-tika
    --additional-test-args quarkus-platform=-Dcamel-quarkus.version=1.1.0-SNAPSHOT,-Dquarkus.version=999-SNAPSHOT
```

If in doubt, you can inspect the help via:

```bash
./quarkus.sh test --help
...
```

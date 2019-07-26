# Benchmarking CLIJ using JMH

[JMH](https://openjdk.java.net/projects/code-tools/jmh/) is Open JDKs solution to benchmarking. We used JMH to benchmark some of CLIJs and ImageJ/Fiji operations for image processing.

## How to run the benchmark

In order to build this benchmark, clone it first:

```
git clone https://github.com/clij/clij-benchmarking-jmh
cd clij-benchmarking-jmh
```

And build it using maven:

```
mvn -Denforcer.skip -Dmaven.test.skip=true clean install
```

Afterwards you can run the benchmark for all operations:

```
java -jar target/benchmarks3.jar -rf csv
```

For individual operations (full list [here](https://github.com/clij/clij-benchmarking-jmh/tree/master/src/main/java/net/haesleinhuepf/clij/benchmark/jmh))

```
java -jar target/benchmarks3.jar Mean3D -rf csv
```

Or only for operation running on the GPU:

```
java -jar target/benchmarks3.jar clij -rf csv
```

[Back to CLIJ documentaion](https://clij.github.io)/
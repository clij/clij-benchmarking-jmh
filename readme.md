# Benchmarking CLIJ using JMH

[JMH](https://openjdk.java.net/projects/code-tools/jmh/) is Open JDKs solution for benchmarking Java code. We used JMH to benchmark some of CLIJs and ImageJ/Fiji operations for image processing.

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
java -jar target/benchmarks3.jar 'clij$' -rf csv
```

For comparing one particular method between ImageJ and CLIJ, you can call:
```
java -jar target/benchmarks3.jar 'GaussianBlur2D.(clij|ijapi)$' -rf csv
```

To execute a benchmark for specific image sizes and parameters such as radius, one can call:
```
java -jar target\benchmarks3.jar .AddScalar2D.ijrun -p size=1 -p radius=2 -rf csv
```

## Implementations / operations
In individual Operation classes, several methods have been implemented in order to allow comprehensive benchmarking. 
In particular, one will find these methods:

* `ijapi()` Runs using the ImageJ1 API on the same thread, for example adding a scalar to all pixels in an image is done with `imagePlus.getProcessor().add(5)`;
* `ijrun()` Runs using the ImageJ1 `run(...)` command which identifies the Java class and runs in a new thread. 
For example, for adding a scalar to all pixels in an image it runs `IJ.run("Add...", "value=5");`. This is the command saved by the ImageJ macro recorder.
* `ijOps()` Runs using ImageJ2 ImgLib2 OpService. For example for adding a scalar to all pixels of an image, it runs `opService.math().add(input, output, 5);`.
* `clij()` Runs CLIJ using its default infrastructure. For example for adding a scalar to all pixels of an inamge, it runs `clij.op().addImageAndScalar(input, output, 5)`;
* `clij_box()` / `clij_sphere`: Some filters are implemented for spherical and rectangular neighborhoods in CLIJ. Thus, there are separate implementations for these. 
In most cases, the `clij_sphere` method is closer to its ImageJ1 counter part while the `clij_box` is faster.
* `ijOpsCLIJ()` Runs CLIJ using the ImageJ2 ImgLib2 OpService. For example for adding a scalar to all pixels of an image, it executes `opService.run(CLIJ_addImageAndScalar.class, input, output, 5f);`.
* `vib()` / `mpicbg` / `mcib3d`: Some sparse operations (e.g. 
[Mean2D](https://github.com/clij/clij-benchmarking-jmh/blob/master/src/main/java/net/haesleinhuepf/clij/benchmark/jmh/Mean2D.java) and 
[Mean3D](https://github.com/clij/clij-benchmarking-jmh/blob/master/src/main/java/net/haesleinhuepf/clij/benchmark/jmh/Mean3D.java)) are also available in the libraries
[VIB](https://github.com/fiji/VIB),
[MCIB3D](https://github.com/mcib3d/mcib3d-core) and
[MPICBG](https://github.com/axtimwalde/mpicbg). Thus, we also implemented some operations using these libraries.

Furthermore, some operations exists `singlethreaded` and `multithreaded`. 
These implementations can help tracing if the tested implementations can make use of modern multi-core CPUs.

[Back to CLIJ documentation](https://clij.github.io)
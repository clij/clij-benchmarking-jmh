package net.haesleinhuepf.clij.benchmark.jmh;

import ij.plugin.ImageCalculator;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import org.openjdk.jmh.annotations.Benchmark;

public class AANoOp extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images) {
        return images.getImp2Da();
    }

    @Benchmark
    public Object clij(CLImages images) {
        return images.getCLImage2Da();
    }
}

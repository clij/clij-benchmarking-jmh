package net.haesleinhuepf.clij.benchmark.jmh;

import ij.plugin.ImageCalculator;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import org.openjdk.jmh.annotations.Benchmark;

public class AANoOp extends AbstractBenchmark {
    //static int counter = 0;
    @Benchmark
    public Object ij(Images images) {
        //counter ++;
        //System.out.println("hello world " + counter);
        return images.getImp2Da();
    }

    @Benchmark
    public Object clij(CLImages images) {
        return images.getCLImage2Da();
    }
}

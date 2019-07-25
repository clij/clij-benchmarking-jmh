package net.haesleinhuepf.clij.benchmark.jmh;

import ij.plugin.ImageCalculator;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import org.openjdk.jmh.annotations.Benchmark;

public class BinaryAnd3D extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images) {
        ImageCalculator ic = new ImageCalculator();
        return ic.run("AND create stack", images.getImp3Da(), images.getImp3Db());
    }

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Db = images.getCLImage3Db();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();

        images.clij.op().binaryAnd(clb3Da, clb3Db, clb3Dc);
        return clb3Dc;
    }
}
package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.ImageCalculator;
import ij.plugin.Thresholder;
import ij.process.AutoThresholder;
import ij.process.ByteProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import org.openjdk.jmh.annotations.Benchmark;

public class BinaryAnd2D extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images) {
        ImageCalculator ic = new ImageCalculator();
        return ic.run("AND create", images.getImp2Da(), images.getImp2Db());
    }

    @Benchmark
    public Object clij(CLImages images) {

        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Db = images.getCLImage2Db();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();

        images.clij.op().binaryAnd(clb2Da, clb2Db, clb2Dc);
        return clb2Dc;
    }
}
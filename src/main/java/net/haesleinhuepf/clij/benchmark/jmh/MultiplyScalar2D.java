package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import org.openjdk.jmh.annotations.Benchmark;

public class MultiplyScalar2D extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images) {
        images.getImp2Da().getProcessor().multiply(2);
        return images.getImp2Da();
    }

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();

        images.clij.op().multiplyImageAndScalar(clb2Da, clb2Dc, 2f);
        return clb2Dc;
    }

    @Benchmark
    public Object ijrun(Images images) {
        ImagePlus imp2D = images.getImp2Da();
        IJ.run(imp2D, "Multiply...", "value=2");
        return imp2D;
    }
}
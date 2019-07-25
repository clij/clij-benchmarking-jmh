package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import org.openjdk.jmh.annotations.Benchmark;

public class MultiplyScalar3D extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images) {
        ImagePlus imp3D = images.getImp3Da();
        for (int z = 0; z < imp3D.getNSlices(); z++) {
            imp3D.setZ(z + 1);
            imp3D.getProcessor().multiply(2);
        }
        return imp3D;
    }

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();

        images.clij.op().multiplyImageAndScalar(clb3Da, clb3Dc, 2f);
        return clb3Dc;
    }

    @Benchmark
    public Object ijrun(Images images) {
        ImagePlus imp3D = images.getImp3Da();
        IJ.run(imp3D, "Multiply...", "value=2 stack");
        return imp3D;
    }
}
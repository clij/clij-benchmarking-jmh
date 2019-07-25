package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.Binary;
import ij.plugin.filter.GaussianBlur;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import org.openjdk.jmh.annotations.Benchmark;

public class GaussianBlur2D extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images, Radius radius) {
        ImagePlus imp2D = images.getImp2Da();
        GaussianBlur gb = new GaussianBlur();
        gb.blurGaussian(imp2D.getProcessor(), radius.getRadiusF());
        return imp2D;
    }

    @Benchmark
    public Object clij(CLImages images, Radius radius) {
        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();
        Float sigma = radius.getRadiusF();
        images.clij.op().blur(clb2Da, clb2Dc, sigma, sigma);
        return clb2Dc;
    }

    @Benchmark
    public Object ijrun(Images images, Radius radius) {
        ImagePlus imp2D = images.getImp2Da();
        IJ.run(imp2D, "Gaussian Blur...", "sigma=" + radius.getRadiusF());
        return imp2D;
    }
}
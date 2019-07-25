package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.GaussianBlur;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import org.openjdk.jmh.annotations.Benchmark;

public class GaussianBlur3D extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images, Radius radius) {
        ImagePlus imp3D = images.getImp2Da();
        Float sigma = radius.getRadiusF();
        ij.plugin.GaussianBlur3D.blur(imp3D, sigma, sigma, sigma);
        return imp3D;
    }

    @Benchmark
    public Object clij(CLImages images, Radius radius) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();
        Float sigma = radius.getRadiusF();
        images.clij.op().blur(clb3Da, clb3Dc, sigma, sigma, sigma);
        return clb3Dc;
    }

    @Benchmark
    public Object ijrun(Images images, Radius radius) {
        ImagePlus imp3D = images.getImp3Da();
        Float sigma = radius.getRadiusF();
        IJ.run(imp3D, "Gaussian Blur 3D...", "x=" + sigma + " y=" + sigma + " z=" + sigma);
        return imp3D;
    }
}
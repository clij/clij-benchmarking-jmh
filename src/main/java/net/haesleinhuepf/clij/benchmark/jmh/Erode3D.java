package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.Binary;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import org.openjdk.jmh.annotations.Benchmark;
import process3d.Erode_;

public class Erode3D extends AbstractBenchmark {
    @Benchmark
    public Object vib(Images images) {
        ImagePlus imp3D = images.getImp3DBinarya();
        new Erode_().erode(imp3D, 1, false);
        return imp3D;
    }

    @Benchmark
    public Object clij_box(CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3DBinarya();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();
        images.clij.op().erodeBox(clb3Da, clb3Dc);
        return clb3Dc;
    }
    @Benchmark
    public Object clij_sphere(CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3DBinarya();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();
        images.clij.op().erodeSphere(clb3Da, clb3Dc);
        return clb3Dc;
    }
}
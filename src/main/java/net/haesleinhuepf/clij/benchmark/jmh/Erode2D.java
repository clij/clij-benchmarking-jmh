package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.WaitForUserDialog;
import ij.plugin.filter.Binary;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import org.openjdk.jmh.annotations.Benchmark;

public class Erode2D extends AbstractBenchmark implements BinaryImageBenchmark {
    @Benchmark
    public Object ij(Images images) {
        ImagePlus imp2D = images.getImp2DBinarya();
        Binary bin = new Binary();
        bin.setup("erode", imp2D);
        bin.run(imp2D.getProcessor());
        return imp2D;
    }

    @Benchmark
    public Object clij_box(CLImages images) {
        ClearCLBuffer clb2Da = images.getCLImage2DBinarya();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();
        images.clij.op().erodeBox(clb2Da, clb2Dc);
        return clb2Dc;
    }

    @Benchmark
    public Object clij_sphere(CLImages images) {
        ClearCLBuffer clb2Da = images.getCLImage2DBinarya();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();
        images.clij.op().erodeSphere(clb2Da, clb2Dc);
        return clb2Dc;
    }

    @Benchmark
    public Object ijrun(Images images) {
        ImagePlus imp2D = images.getImp2DBinarya();
        IJ.run(imp2D, "Erode", "");
        return imp2D;
    }
}
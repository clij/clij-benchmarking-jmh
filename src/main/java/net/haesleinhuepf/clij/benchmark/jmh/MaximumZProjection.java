package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.ZProjector;
import ij.plugin.filter.GaussianBlur;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import org.openjdk.jmh.annotations.Benchmark;

public class MaximumZProjection extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images) {
        ImagePlus imp3D = images.getImp2Da();
        ImagePlus imp2D = ZProjector.run(imp3D, "Max Intensity");
        return imp2D;
    }

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();
        images.clij.op().maximumZProjection(clb3Da, clb2Dc);
        return clb2Dc;
    }

    @Benchmark
    public Object ijrun(Images images) {
        ImagePlus imp3D = images.getImp3Da();
        IJ.run(imp3D, "Z Project...", "projection=[Max Intensity]");
        return IJ.getImage();
    }
}
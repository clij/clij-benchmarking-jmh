package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.Thresholder;
import ij.process.AutoThresholder;
import ij.process.ByteProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import org.openjdk.jmh.annotations.Benchmark;

public class AutoThreshold3D extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images) {
        ImagePlus imp3D = images.getImp3Da();

        AutoThresholder autoThresholder = new AutoThresholder();
        int[] histogram = imp3D.getStatistics().histogram;
        int threshold = autoThresholder.getThreshold(AutoThresholder.Method.Default, histogram);
        IJ.setThreshold(imp3D, threshold, Integer.MAX_VALUE);
        new Thresholder().run("mask");
        return imp3D;
    }

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();

        images.clij.op().automaticThreshold(clb3Da, clb3Dc, "Default");
        return clb3Dc;
    }

    @Benchmark
    public Object ijrun(Images images) {
        ImagePlus imp3D = images.getImp3Da();
        IJ.setAutoThreshold(imp3D, "Default dark");
        IJ.run(imp3D, "Convert to Mask", "method=Default background=Dark black");
        return imp3D;
    }
}
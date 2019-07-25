package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.Thresholder;
import ij.process.AutoThresholder;
import ij.process.ByteProcessor;
import ij.process.ImageStatistics;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import org.openjdk.jmh.annotations.Benchmark;

import java.awt.*;

public class AutoThreshold2D extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images) {
        ImagePlus imp2D = images.getImp2Da();
        AutoThresholder autoThresholder = new AutoThresholder();
        int[] histogram = imp2D.getStatistics().histogram;
        int threshold = autoThresholder.getThreshold(AutoThresholder.Method.Default, histogram);
        IJ.setThreshold(images.getImp2Da(), threshold, Integer.MAX_VALUE);
        ByteProcessor byteProcessor = Thresholder.createMask(imp2D);
        return new ImagePlus("binary", byteProcessor);
    }

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();

        images.clij.op().automaticThreshold(clb2Da, clb2Dc, "Default");
        return clb2Dc;
    }

    @Benchmark
    public Object ijrun(Images images) {
        ImagePlus imp2D = images.getImp2Da();
        IJ.setAutoThreshold(imp2D, "Default dark");
        IJ.run(imp2D, "Convert to Mask", "method=Default background=Dark black");
        return imp2D;
    }
}
package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.Thresholder;
import ij.process.AutoThresholder;
import ij.process.ByteProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import org.openjdk.jmh.annotations.Benchmark;

public class Threshold2D extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images) {
        ImagePlus imp2D = images.getImp2Da();
        IJ.setThreshold(images.getImp2Da(), 128, Integer.MAX_VALUE);
        ByteProcessor byteProcessor = Thresholder.createMask(imp2D);
        return new ImagePlus("binary", byteProcessor);
    }

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();

        images.clij.op().threshold(clb2Da, clb2Dc, 128f);
        return clb2Dc;
    }

    @Benchmark
    public Object ijrun(Images images) {
        ImagePlus imp2D = images.getImp2Da();
        IJ.setThreshold(imp2D, 128, 255);
        IJ.run(imp2D, "Convert to Mask", "method=Default background=Dark black");
        return imp2D;
    }
}
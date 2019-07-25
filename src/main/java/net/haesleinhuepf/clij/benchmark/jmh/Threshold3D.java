package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.Thresholder;
import ij.process.AutoThresholder;
import ij.process.ByteProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import org.openjdk.jmh.annotations.Benchmark;

public class Threshold3D extends AbstractBenchmark {

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();

        images.clij.op().threshold(clb3Da, clb3Dc, 128f);
        return clb3Dc;
    }

    @Benchmark
    public Object ijrun(Images images) {
        ImagePlus imp3D = images.getImp3Da();
        IJ.setThreshold(imp3D, 128, 255);
        IJ.run(imp3D, "Convert to Mask", "method=Default background=Dark black stack");
        return IJ.getImage();
    }
}
package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.Binary;
import ij.plugin.filter.Transformer;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import org.openjdk.jmh.annotations.Benchmark;

public class Flip2D extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images) {
        ImagePlus imp2D = images.getImp2Da();
        Transformer transformer = new Transformer();
        transformer.setup("fliph", imp2D);
        transformer.run(imp2D.getProcessor());
        return imp2D;
    }

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();
        images.clij.op().flip(clb2Da, clb2Dc, true, false);
        return clb2Dc;
    }

    @Benchmark
    public Object ijrun(Images images) {
        ImagePlus imp2D = images.getImp2Da();
        IJ.run(imp2D, "Flip Horizontally", "");
        return imp2D;
    }
}
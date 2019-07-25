package net.haesleinhuepf.clij.benchmark.jmh;

import ij.ImagePlus;
import ij.plugin.ImageCalculator;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import org.openjdk.jmh.annotations.Benchmark;

public class AddImagesWeighted2D extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images) {
        ImageCalculator ic = new ImageCalculator();
        return ic.run("Add create", images.getImp2Da(), images.getImp2Db());
    }

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Db = images.getCLImage2Db();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();

        images.clij.op().addImagesWeighted(clb2Da, clb2Db, clb2Dc, 1f, 1f);
        return clb2Dc;
    }
}

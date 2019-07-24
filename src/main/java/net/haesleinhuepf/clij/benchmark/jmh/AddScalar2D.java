package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.plugin.ImageCalculator;
import org.openjdk.jmh.annotations.Benchmark;

public class AddScalar2D extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images) {
        images.getImp2Da().getProcessor().add(1);
        return images.getImp2Da();
    }

    @Benchmark
    public Object clij(CLImages images) {
        images.clij.op().addImageAndScalar(images.getCLImage2Da(), images.getCLImage2Dc(), 1f);
        return images.getCLImage2Dc();
    }

    @Benchmark
    public Object ijrun(Images images) {
        IJ.run(images.getImp2Da(), "Add...", "value=1");
        return images.getImp2Da();
    }
}
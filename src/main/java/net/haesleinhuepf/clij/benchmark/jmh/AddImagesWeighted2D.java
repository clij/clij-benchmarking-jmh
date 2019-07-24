package net.haesleinhuepf.clij.benchmark.jmh;

import ij.plugin.ImageCalculator;
import org.openjdk.jmh.annotations.Benchmark;

public class AddImagesWeighted2D extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images) {
        ImageCalculator ic = new ImageCalculator();
        return ic.run("Add create", images.getImp2Da(), images.getImp2Db());
    }

    @Benchmark
    public Object clij(CLImages images) {
        images.clij.op().addImagesWeighted(images.getCLImage2Da(), images.getCLImage2Db(), images.getCLImage2Dc(), 1f, 1f);
        return images.getCLImage2Dc();
    }
}

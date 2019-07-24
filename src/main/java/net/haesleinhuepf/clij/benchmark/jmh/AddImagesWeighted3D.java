package net.haesleinhuepf.clij.benchmark.jmh;

import ij.plugin.ImageCalculator;
import org.openjdk.jmh.annotations.Benchmark;

public class AddImagesWeighted3D extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images) {
        ImageCalculator ic = new ImageCalculator();
        return ic.run("Add create stack", images.getImp3Da(), images.getImp3Db());
    }

    @Benchmark
    public Object clij(CLImages images) {
        images.clij.op().addImagesWeighted(images.getCLImage3Da(), images.getCLImage3Db(), images.getCLImage3Dc(), 1f, 1f);
        return images.getCLImage3Dc();
    }
}

package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import org.openjdk.jmh.annotations.Benchmark;

public class AddScalar3D extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images) {
        ImagePlus imp3D = images.getImp2Da();
        for (int z = 0; z < imp3D.getNSlices(); z++) {
            imp3D.setZ(z + 1);
            imp3D.getProcessor().add(1);
        }
        return images.getImp2Da();
    }

    @Benchmark
    public Object clij(CLImages images) {
        images.clij.op().addImageAndScalar(images.getCLImage2Da(), images.getCLImage2Dc(), 1f);
        return images.getCLImage2Dc();
    }

    @Benchmark
    public Object ijrun(Images images) {
        IJ.run(images.getImp2Da(), "Add...", "value=1 stack");
        return images.getImp2Da();
    }
}
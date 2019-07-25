package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.Transformer;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import org.openjdk.jmh.annotations.Benchmark;

public class Flip3D extends AbstractBenchmark {
    /*@Benchmark
    public Object ij(Images images) {
        ImagePlus imp3D = images.getImp3Da();
        Transformer transformer = new Transformer();
        transformer.setup("fliph", imp3D);
        for (int z = 0; z < imp3D.getNSlices(); z++ ) {
            imp3D.setSlice(z + 1);
            transformer.run(imp3D.getProcessor());
        }
        return imp3D;
    }*/

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();
        images.clij.op().flip(clb3Da, clb3Dc, true, false, false);
        return clb3Dc;
    }

    @Benchmark
    public Object ijrun(Images images) {
        ImagePlus imp3D = images.getImp3Da();
        IJ.run(imp3D, "Flip Horizontally", "stack");
        return imp3D;
    }
}
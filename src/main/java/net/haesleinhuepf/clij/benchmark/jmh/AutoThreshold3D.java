package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.ops.CLIJ_automaticThreshold.CLIJ_automaticThreshold;
import net.imglib2.img.Img;
import org.openjdk.jmh.annotations.Benchmark;

public class AutoThreshold3D extends AbstractBenchmark implements BinaryImageBenchmark {
    /*@Benchmark
    public Object ij(Images images) {
        ImagePlus imp3D = images.getImp3Da();
        AutoThresholder autoThresholder = new AutoThresholder();
        int[] histogram = imp3D.getStatistics().histogram;
        int threshold = autoThresholder.getThreshold(AutoThresholder.Method.Default, histogram);
        IJ.setThreshold(imp3D, threshold, Integer.MAX_VALUE);
        imp3D.show();
        new Thresholder().run("mask");
        imp3D.hide();
        return imp3D;
    }*/

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();

        images.clij.op().automaticThreshold(clb3Da, clb3Dc, "Default");
        return clb3Dc;
    }

    @Benchmark
    public Object ijrun(Images images) {
        ImagePlus imp3D = images.getImp3Da();
        IJ.setAutoThreshold(imp3D, "Default dark");
        IJ.run(imp3D, "Convert to Mask", "method=Default background=Dark black");
        return imp3D;
    }

    @Benchmark
    public Object ijOps(ImgLib2Images images) {
        Img img3Da = images.getImg3Da();
        Img img3Dbinarya = images.getImg3Dbinarya();
        images.getOpService().threshold().otsu(img3Dbinarya, img3Da);
        return img3Dbinarya;
    }

    @Benchmark
    public Object ijOpsCLIJ(IJ2CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();

        images.getOpService().run(CLIJ_automaticThreshold.class, clb3Dc, clb3Da, "Default");
        return clb3Dc;
    }
}
package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.Thresholder;
import ij.process.AutoThresholder;
import ij.process.ByteProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.ops.CLIJ_threshold.CLIJ_threshold;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import org.openjdk.jmh.annotations.Benchmark;

public class Threshold3D extends AbstractBenchmark implements BinaryImageBenchmark {

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
        IJ.run(imp3D, "Convert to Mask", "method=Default background=Dark black");
        return imp3D;
    }

    @Benchmark
    public <T extends RealType> Object ijOps(ImgLib2Images images) {
        Img img3Da = images.getImg3Da();
        Img img3Dbinarya = images.getImg3Dbinarya();
        T val = ((Img<T>) img3Da).firstElement();
        val.setReal(128);
        images.getOpService().threshold().apply(img3Dbinarya, img3Da, val);
        return img3Dbinarya;
    }

    @Benchmark
    public Object ijOpsCLIJ(IJ2CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();
        images.getOpService().run(CLIJ_threshold.class, clb3Dc, clb3Da, 128f);
        return clb3Dc;
    }
}
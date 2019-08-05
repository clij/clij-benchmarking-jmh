package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.Thresholder;
import ij.process.ByteProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.ops.CLIJ_automaticThreshold.CLIJ_automaticThreshold;
import net.haesleinhuepf.clij.ops.CLIJ_threshold.CLIJ_threshold;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import org.openjdk.jmh.annotations.Benchmark;

public class FixedThreshold2D extends AbstractBenchmark implements BinaryImageBenchmark {
    @Benchmark
    public Object ijapi(Images images) {
        ImagePlus imp2D = images.getImp2Da();
        IJ.setThreshold(images.getImp2Da(), 128, Integer.MAX_VALUE);
        ByteProcessor byteProcessor = Thresholder.createMask(imp2D);
        return new ImagePlus("binary", byteProcessor);
    }

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();

        images.clij.op().threshold(clb2Da, clb2Dc, 128f);
        return clb2Dc;
    }

    @Benchmark
    public Object ijrun(Images images) {
        ImagePlus imp2D = images.getImp2Da();
        IJ.setThreshold(imp2D, 128, 255);
        IJ.run(imp2D, "Convert to Mask", "method=Default background=Dark black");
        return imp2D;
    }

    @Benchmark
    public <T extends RealType> Object ijOps(ImgLib2Images images) {
        Img img2Da = images.getImg2Da();
        Img img2Dbinarya = images.getImg2Dbinarya();
        T val = (T) ((Img<T>) img2Da).firstElement().copy();
        val.setReal(128);
        images.getOpService().threshold().apply(img2Dbinarya, img2Da, val);
        return img2Dbinarya;
    }

    @Benchmark
    public Object ijOpsCLIJ(IJ2CLImages images) {
        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();
        images.getOpService().run(CLIJ_threshold.class, clb2Dc, clb2Da, 128f);
        return clb2Dc;
    }
}
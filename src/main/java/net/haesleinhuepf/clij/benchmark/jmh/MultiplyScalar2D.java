package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.ops.CLIJ_flip.CLIJ_flip;
import net.haesleinhuepf.clij.ops.CLIJ_multiplyImageAndScalar.CLIJ_multiplyImageAndScalar;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;
import org.openjdk.jmh.annotations.Benchmark;

public class MultiplyScalar2D extends AbstractBenchmark {
    @Benchmark
    public Object ijapi(Images images) {
        images.getImp2Da().getProcessor().multiply(2);
        return images.getImp2Da();
    }

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();

        images.clij.op().multiplyImageAndScalar(clb2Da, clb2Dc, 2f);
        return clb2Dc;
    }

    @Benchmark
    public Object ijrun(Images images) {
        ImagePlus imp2D = images.getImp2Da();
        IJ.run(imp2D, "Multiply...", "value=2");
        return imp2D;
    }

    @Benchmark
    public <T extends RealType> Object ijOps(ImgLib2Images images) {
        Img img2Da = images.getImg2Da();
        Img img2Dc = images.getImg2Dc();
        T val = (T) ((Img<T>) img2Da).firstElement().copy();
        val.setReal(2);
        images.getOpService().math().multiply(Views.iterable(img2Dc), img2Da, val);
        return img2Dc;
    }

    @Benchmark
    public Object ijOpsCLIJ(IJ2CLImages images) {
        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();
        images.getOpService().run(CLIJ_multiplyImageAndScalar.class, clb2Dc, clb2Da, 2f);
        return clb2Dc;
    }
}
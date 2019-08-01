package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.ops.CLIJ_multiplyImageAndScalar.CLIJ_multiplyImageAndScalar;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;
import org.openjdk.jmh.annotations.Benchmark;

public class MultiplyScalar3D extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images) {
        ImagePlus imp3D = images.getImp3Da();
        for (int z = 0; z < imp3D.getNSlices(); z++) {
            imp3D.setZ(z + 1);
            imp3D.getProcessor().multiply(2);
        }
        return imp3D;
    }

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();

        images.clij.op().multiplyImageAndScalar(clb3Da, clb3Dc, 2f);
        return clb3Dc;
    }

    @Benchmark
    public Object ijrun(Images images) {
        ImagePlus imp3D = images.getImp3Da();
        IJ.run(imp3D, "Multiply...", "value=2 stack");
        return imp3D;
    }

    @Benchmark
    public <T extends RealType> Object ijOps(ImgLib2Images images) {
        Img img3Da = images.getImg3Da();
        Img img3Dc = images.getImg3Dc();
        T val = (T) ((Img<T>) img3Da).firstElement().copy();
        val.setReal(2);
        images.getOpService().math().multiply(Views.iterable(img3Dc), img3Da, val);
        return img3Dc;
    }

    @Benchmark
    public Object ijOpsCLIJ(IJ2CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();
        images.getOpService().run(CLIJ_multiplyImageAndScalar.class, clb3Dc, clb3Da, 2f);
        return clb3Dc;
    }
}
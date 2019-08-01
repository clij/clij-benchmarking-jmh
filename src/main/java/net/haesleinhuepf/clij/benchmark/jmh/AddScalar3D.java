package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.ops.CLIJ_addImageAndScalar.CLIJ_addImageAndScalar;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;
import org.openjdk.jmh.annotations.Benchmark;

public class AddScalar3D extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images) {
        ImagePlus imp3D = images.getImp3Da();
        for (int z = 0; z < imp3D.getNSlices(); z++) {
            imp3D.setZ(z + 1);
            imp3D.getProcessor().add(1);
        }
        return imp3D;
    }

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();

        images.clij.op().addImageAndScalar(clb3Da, clb3Dc, 1f);
        return clb3Dc;
    }

    @Benchmark
    public Object ijrun(Images images) {
        ImagePlus imp3D = images.getImp3Da();
        IJ.run(imp3D, "Add...", "value=1 stack");
        return imp3D;
    }

    @Benchmark
    public <T extends RealType> Object ijOps(ImgLib2Images images) {
        RandomAccessibleInterval<T> img3Da = images.getImg3Da();
        RandomAccessibleInterval<T> img3Dc = images.getImg3Dc();
        T val = (T) ((Img<T>) img3Da).firstElement().copy();
        val.setReal(1);
        images.getOpService().math().add(Views.iterable(img3Dc), Views.iterable(img3Da), val);
        return img3Dc;
    }

    @Benchmark
    public Object ijOpsCLIJ(IJ2CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();

        images.getOpService().run(CLIJ_addImageAndScalar.class, clb3Dc, clb3Da, 1f);
        return clb3Dc;
    }
}
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

public class AddScalar2D extends AbstractBenchmark {
    @Benchmark
    public Object ijapi(Images images) {
        images.getImp2Da().getProcessor().add(1);
        return images.getImp2Da();
    }

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();

        images.clij.op().addImageAndScalar(clb2Da, clb2Dc, 1f);
        return clb2Dc;
    }

    @Benchmark
    public Object ijrun(Images images) {
        ImagePlus imp2D = images.getImp2Da();
        IJ.run(imp2D, "Add...", "value=1");
        return imp2D;
    }

    @Benchmark
    public <T extends RealType> Object ijOps(ImgLib2Images images) {
        RandomAccessibleInterval<T> img2Da = images.getImg2Da();
        RandomAccessibleInterval<T> img2Dc = images.getImg2Dc();
        T val = (T) ((Img<T>) img2Da).firstElement().copy();
        val.setReal(1);
        images.getOpService().math().add(Views.iterable(img2Dc), Views.iterable(img2Da), val);
        return img2Dc;
    }

    @Benchmark
    public Object ijOpsCLIJ(IJ2CLImages images) {
        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();

        images.getOpService().run(CLIJ_addImageAndScalar.class, clb2Dc, clb2Da, 1f);
        return clb2Dc;
    }
}
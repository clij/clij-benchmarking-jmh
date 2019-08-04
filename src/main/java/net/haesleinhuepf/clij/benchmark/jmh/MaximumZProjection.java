package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.ZProjector;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.ops.CLIJ_maxProjection.CLIJ_maximumZProjection;
import net.imagej.ops.Ops;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.view.Views;
import org.openjdk.jmh.annotations.Benchmark;

public class MaximumZProjection extends AbstractBenchmark {
    @Benchmark
    public Object ijapi(Images images) {
        ImagePlus imp3D = images.getImp2Da();
        ImagePlus imp2D = ZProjector.run(imp3D, "Max Intensity");
//        System.out.println(Arrays.toString(imp2D.getPixel(0,0)));
        return imp2D;
    }

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();
        images.clij.op().maximumZProjection(clb3Da, clb2Dc);
//        System.out.println(Arrays.toString(images.clij.pull(clb2Dc).getPixel(0,0)));
        return clb2Dc;
    }

    @Benchmark
    public Object ijrun(Images images) {
        ImagePlus imp3D = images.getImp3Da();
        IJ.run(imp3D, "Z Project...", "projection=[Max Intensity]");
        return null;
    }

    @Benchmark
    public <T> Object ijOps(ImgLib2Images images) {
        RandomAccessibleInterval<T> img3Da = images.getImg3Da();
        IterableInterval<T> img2Dc = Views.iterable(images.getImg2Dc());
        UnaryComputerOp<Iterable<T>, T> maxOp = Computers.unary(images.opService, Ops.Stats.Max.class, (T) null, img2Dc);
        images.getOpService().transform().project(img2Dc, img3Da, maxOp, 2);
//        System.out.println(img2Dc.firstElement());
        return img2Dc;
    }

    @Benchmark
    public Object ijOpsCLIJ(IJ2CLImages images) {
        Object img3Da = images.getCLImage3Da();
        Object img2Dc = images.getCLImage2Dc();
        images.getOpService().run(CLIJ_maximumZProjection.class, img2Dc, img3Da);
        return img2Dc;
    }

}
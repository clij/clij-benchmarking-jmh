package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.Binary;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.ops.CLIJ_erodeBox.CLIJ_erodeBox;
import net.imglib2.algorithm.neighborhood.CenteredRectangleShape;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.img.Img;
import org.openjdk.jmh.annotations.Benchmark;

public class Erode2D extends AbstractBenchmark implements BinaryImageBenchmark {
    @Benchmark
    public Object ij(Images images) {
        ImagePlus imp2D = images.getImp2DBinarya();
        Binary bin = new Binary();
        bin.setup("erode", imp2D);
        bin.run(imp2D.getProcessor());
        return imp2D;
    }

    @Benchmark
    public Object clij_box(CLImages images) {
        ClearCLBuffer clb2Da = images.getCLImage2DBinarya();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();
        images.clij.op().erodeBox(clb2Da, clb2Dc);
        return clb2Dc;
    }

    @Benchmark
    public Object clij_sphere(CLImages images) {
        ClearCLBuffer clb2Da = images.getCLImage2DBinarya();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();
        images.clij.op().erodeSphere(clb2Da, clb2Dc);
        return clb2Dc;
    }

    @Benchmark
    public Object ijrun(Images images) {
        ImagePlus imp2D = images.getImp2DBinarya();
        IJ.run(imp2D, "Erode", "");
        return imp2D;
    }

    @Benchmark
    public Object ijOps_sphere(ImgLib2Images images) {
        Img img2Dbinarya = images.getImg2Dbinarya();
        Img img2Dbinaryb = images.getImg2Dbinaryb();
        images.getOpService().morphology().erode(img2Dbinaryb, img2Dbinarya, new HyperSphereShape(1));
        return img2Dbinaryb;
    }

    @Benchmark
    public Object ijOps_box(ImgLib2Images images) {
        Img img2Dbinarya = images.getImg2Dbinarya();
        Img img2Dbinaryb = images.getImg2Dbinaryb();
        images.getOpService().morphology().erode(img2Dbinaryb, img2Dbinarya, new CenteredRectangleShape(new int[]{1,1}, false));
        return img2Dbinaryb;
    }

    @Benchmark
    public Object ijOpsCLIJ_box(IJ2CLImages images) {
        ClearCLBuffer clb2Da = images.getCLImage2DBinarya();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();
        images.getOpService().run(CLIJ_erodeBox.class, clb2Dc, clb2Da);
        return clb2Dc;
    }
}
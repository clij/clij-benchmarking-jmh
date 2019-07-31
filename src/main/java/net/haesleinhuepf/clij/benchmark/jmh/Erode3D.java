package net.haesleinhuepf.clij.benchmark.jmh;

import ij.ImagePlus;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.ops.CLIJ_erodeBox.CLIJ_erodeBox;
import net.imglib2.algorithm.neighborhood.CenteredRectangleShape;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.img.Img;
import org.openjdk.jmh.annotations.Benchmark;
import process3d.Erode_;

public class Erode3D extends AbstractBenchmark implements BinaryImageBenchmark {
    @Benchmark
    public Object vib(Images images) {
        ImagePlus imp3D = images.getImp3DBinarya();
        imp3D = new Erode_().erode(imp3D, 1, true);
        return imp3D;
    }

    @Benchmark
    public Object clij_box(CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3DBinarya();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();
        images.clij.op().erodeBox(clb3Da, clb3Dc);
        return clb3Dc;
    }
    @Benchmark
    public Object clij_sphere(CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3DBinarya();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();
        images.clij.op().erodeSphere(clb3Da, clb3Dc);
        return clb3Dc;
    }

    @Benchmark
    public Object ijOps_sphere(ImgLib2Images images) {
        Img img3Dbinarya = images.getImg3Dbinarya();
        Img img3Dbinaryb = images.getImg3Dbinaryb();
        images.getOpService().morphology().erode(img3Dbinaryb, img3Dbinarya, new HyperSphereShape(1));
        return img3Dbinaryb;
    }

    @Benchmark
    public Object ijOps_box(ImgLib2Images images) {
        Img img3Dbinarya = images.getImg3Dbinarya();
        Img img3Dbinaryb = images.getImg3Dbinaryb();
        images.getOpService().morphology().erode(img3Dbinaryb, img3Dbinarya, new CenteredRectangleShape(new int[]{1,1,1}, false));
        return img3Dbinaryb;
    }

    @Benchmark
    public Object ijOpsCLIJ_box(IJ2CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3DBinarya();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();
        images.getOpService().run(CLIJ_erodeBox.class, clb3Dc, clb3Da);
        return clb3Dc;
    }

}
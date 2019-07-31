package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.Filters3D;
import mcib3d.image3d.processing.FastFilters3D;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.ops.CLIJ_medianSphere.CLIJ_medianSphere;
import net.haesleinhuepf.clij.utilities.CLIJUtilities;
import net.imglib2.algorithm.neighborhood.CenteredRectangleShape;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.img.Img;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import process3d.Median_;

@State(Scope.Benchmark)
public class Median3D extends AbstractBenchmark {

    @Benchmark
    public Object ijrun(Images images, Radius radius) {
        ImagePlus imp3D = images.getImp3Da();
        int rad = (int)radius.getRadiusF();
        IJ.run(imp3D, "Median 3D...", "x=" + rad + " y=" + rad + " z=" + rad);
        return imp3D;
    }

    @Benchmark
    public Object clij_box(CLImages images, Radius radius) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();

        int kernelSize = CLIJUtilities.radiusToKernelSize((int)radius.getRadiusF());
        images.clij.op().medianBox(clb3Da, clb3Dc, kernelSize, kernelSize, kernelSize);
        return clb3Dc;
    }

    @Benchmark
    public Object clij_sphere(CLImages images, Radius radius) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();

        int kernelSize = CLIJUtilities.radiusToKernelSize((int)radius.getRadiusF());
        images.clij.op().medianSphere(clb3Da, clb3Dc, kernelSize, kernelSize, kernelSize);
        return clb3Dc;
    }

    @Benchmark
    public Object ij(Images images, Radius radius) {
        ImagePlus imp3D = images.getImp3Da();
        int rad = (int)radius.getRadiusF();
        imp3D = new ImagePlus("res", Filters3D.filter(imp3D.getStack(), Filters3D.MEDIAN, rad, rad, rad ));
        return imp3D;
    }

    @Benchmark
    public Object vib(Images images, Radius radius) {
        ImagePlus imp = images.getImp3Da();
        Median_ median = new Median_();
        median.setup("", imp);
        median.run(imp.getProcessor());
        return  imp;
    }

    @Benchmark
    public Object mcib3d(Images images, Radius radius) {
        ImagePlus imp = images.getImp3Da();
        int rad = (int)radius.getRadiusF();
        ImageStack res = FastFilters3D.filterImageStack(imp.getImageStack(), FastFilters3D.MEDIAN, rad, rad, rad, 0, false);
        return new ImagePlus("filtered", res);
    }

    @Benchmark
    public Object ijOps_box(ImgLib2Images images, Radius radius) {
        Img img3Da = images.getImg3Da();
        Img img3Dc = images.getImg3Dc();
        int rad = (int)radius.getRadiusF();
        images.getOpService().filter().median(img3Dc, img3Da, new CenteredRectangleShape(new int[]{rad, rad, rad}, false));
        return img3Dc;
    }

    @Benchmark
    public Object ijOps_sphere(ImgLib2Images images, Radius radius) {
        Img img3Da = images.getImg3Da();
        Img img3Dc = images.getImg3Dc();
        int rad = (int)radius.getRadiusF();
        images.getOpService().filter().median(img3Dc, img3Da, new HyperSphereShape(rad));
        return img3Dc;
    }

    @Benchmark
    public Object ijOpsCLIJ_sphere(IJ2CLImages images, Radius radius) {
        Object img3Da = images.getCLImage3Da();
        Object img3Dc = images.getCLImage3Dc();
        int rad = (int)radius.getRadiusF();
        images.getOpService().run(CLIJ_medianSphere.class, img3Dc, img3Da, rad, rad, rad);
        return img3Dc;
    }
}

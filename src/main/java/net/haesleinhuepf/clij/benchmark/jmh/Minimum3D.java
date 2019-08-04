package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.Filters3D;
import mcib3d.image3d.processing.FastFilters3D;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.ops.CLIJ_minimumBox.CLIJ_minimumBox;
import net.haesleinhuepf.clij.utilities.CLIJUtilities;
import net.imglib2.algorithm.neighborhood.CenteredRectangleShape;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.img.Img;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class Minimum3D extends AbstractBenchmark {

    @Benchmark
    public Object ijrun(Images images, Radius radius) {
        ImagePlus imp = images.getImp3Da();
        IJ.run(imp, "Minimum 3D...", "x=" + radius.getRadiusF() + " y=" + radius.getRadiusF() + " z=" + radius.getRadiusF());
        return imp;
    }

    @Benchmark
    public Object clij(CLImages images, Radius radius) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();

        int kernelSize = CLIJUtilities.radiusToKernelSize((int)radius.getRadiusF());
        images.clij.op().minimumSphere(clb3Da, clb3Dc,
                kernelSize, kernelSize, kernelSize);
        return clb3Dc;
    }

    @Benchmark
    public Object ijapi(Images images, Radius radius) {
        ImagePlus imp = images.getImp3Da();
        imp = new ImagePlus("res", Filters3D.filter(imp.getImageStack(), Filters3D.MIN, radius.getRadiusF(), radius.getRadiusF(), radius.getRadiusF()));
        return imp;
    }

    @Benchmark
    public Object mcib3d(Images images, Radius radius) {
        ImagePlus imp = images.getImp3Da();
        ImageStack res = FastFilters3D.filterImageStack(imp.getImageStack(), FastFilters3D.MIN, radius.getRadiusF(), radius.getRadiusF(), radius.getRadiusF(), 0, false);
        return new ImagePlus("filtered", res);
    }

    @Benchmark
    public Object ijOps_sphere(ImgLib2Images images, Radius radius) {
        Img img3Da = images.getImg3Da();
        Img img3Dc = images.getImg3Dc();
        int rad = (int)radius.getRadiusF();
        images.getOpService().filter().min(img3Dc, img3Da, new HyperSphereShape(rad));
        return img3Dc;
    }

    @Benchmark
    public Object ijOps_box(ImgLib2Images images, Radius radius) {
        Img img3Da = images.getImg3Da();
        Img img3Dc = images.getImg3Dc();
        int rad = (int)radius.getRadiusF();
        images.getOpService().filter().min(img3Dc, img3Da, new CenteredRectangleShape(new int[]{rad, rad, rad}, false));
        return img3Dc;
    }

    @Benchmark
    public Object ijOpsCLIJ_box(IJ2CLImages images, Radius radius) {
        Object img3Da = images.getCLImage3Da();
        Object img3Dc = images.getCLImage3Dc();
        int rad = (int)radius.getRadiusF();
        images.getOpService().run(CLIJ_minimumBox.class, img3Dc, img3Da, rad, rad, rad);
        return img3Dc;
    }
}

package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.plugin.Filters3D;
import mcib3d.image3d.processing.FastFilters3D;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.ops.CLIJ_meanBox.CLIJ_meanBox;
import net.haesleinhuepf.clij.ops.CLIJ_meanSphere.CLIJ_meanSphere;
import net.haesleinhuepf.clij.utilities.CLIJUtilities;
import net.imglib2.algorithm.neighborhood.CenteredRectangleShape;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.img.Img;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class Mean3D extends AbstractBenchmark {

    @Benchmark
    public Object ijrun(Images images, Radius radius) {
        ImagePlus imp3D = images.getImp3Da();
        int rad = (int)radius.getRadiusF();
        IJ.run(imp3D, "Mean 3D...", "x=" + rad + " y=" + rad + " z=" + rad);
        return imp3D;
    }

    @Benchmark
    public Object ijrun_singlethreaded(Images images, Radius radius) {
        int numThreads = Prefs.getThreads();
        Prefs.setThreads(1);
        ImagePlus imp3D = images.getImp3Da();
        int rad = (int)radius.getRadiusF();
        IJ.run(imp3D, "Mean 3D...", "x=" + rad + " y=" + rad + " z=" + rad);
        Prefs.setThreads(numThreads);
        return imp3D;
    }


    @Benchmark
    public Object ijrun_multithreaded(Images images, Radius radius) {
        int numThreads = Prefs.getThreads();
        Prefs.setThreads(Runtime.getRuntime().availableProcessors());
        ImagePlus imp3D = images.getImp3Da();
        int rad = (int)radius.getRadiusF();
        IJ.run(imp3D, "Mean 3D...", "x=" + rad + " y=" + rad + " z=" + rad);
        Prefs.setThreads(numThreads);
        return imp3D;
    }

    @Benchmark
    public Object clij_box(CLImages images, Radius radius) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();

        int rad = (int)radius.getRadiusF();
        images.clij.op().meanBox(clb3Da, clb3Dc, rad, rad, rad);
        return clb3Dc;
    }

    @Benchmark
    public Object clij_sphere(CLImages images, Radius radius) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();

        int kernelSize = CLIJUtilities.radiusToKernelSize((int)radius.getRadiusF());
        images.clij.op().meanSphere(clb3Da, clb3Dc, kernelSize, kernelSize, kernelSize);
        return clb3Dc;
    }

    @Benchmark
    public Object ij(Images images, Radius radius) {
        ImagePlus imp3D = images.getImp3Da();
        int rad = (int)radius.getRadiusF();
        imp3D = new ImagePlus("res", Filters3D.filter(imp3D.getStack(), Filters3D.MEAN, rad, rad, rad ));
        return imp3D;
    }

    @Benchmark
    public Object ij_singlethreaded(Images images, Radius radius) {
        int numThreads = Prefs.getThreads();
        Prefs.setThreads(1);
        ImagePlus imp3D = images.getImp3Da();
        int rad = (int)radius.getRadiusF();
        imp3D = new ImagePlus("res", Filters3D.filter(imp3D.getStack(), Filters3D.MEAN, rad, rad, rad ));
        Prefs.setThreads(numThreads);
        return imp3D;
    }

    @Benchmark
    public Object ij_multithreaded(Images images, Radius radius) {
        int numThreads = Prefs.getThreads();
        Prefs.setThreads( Runtime.getRuntime().availableProcessors());
        ImagePlus imp3D = images.getImp3Da();
        int rad = (int)radius.getRadiusF();
        imp3D = new ImagePlus("res", Filters3D.filter(imp3D.getStack(), Filters3D.MEAN, rad, rad, rad ));
        Prefs.setThreads(numThreads);
        return imp3D;
    }

    @Benchmark
    public Object mcib3d_singlethreaded(Images images, Radius radius) {
        ImagePlus imp = images.getImp3Da();
        int rad = (int)radius.getRadiusF();
        ImageStack res = FastFilters3D.filterImageStack(imp.getImageStack(), FastFilters3D.MEAN, rad, rad, rad, 1, false);
        return new ImagePlus("filtered", res);
    }

    @Benchmark
    public Object mcib3d_multithreaded(Images images, Radius radius) {
        ImagePlus imp = images.getImp3Da();
        int rad = (int)radius.getRadiusF();
        ImageStack res = FastFilters3D.filterImageStack(imp.getImageStack(), FastFilters3D.MEAN, rad, rad, rad, Runtime.getRuntime().availableProcessors(), false);
        return new ImagePlus("filtered", res);
    }

    @Benchmark
    public Object ijOps_box(ImgLib2Images images, Radius radius) {
        Img img3Da = images.getImg3Da();
        Img img3Dc = images.getImg3Dc();
        int rad = (int)radius.getRadiusF();
        images.getOpService().filter().mean(img3Dc, img3Da, new CenteredRectangleShape(new int[]{rad, rad, rad}, false));
        return img3Dc;
    }

    @Benchmark
    public Object ijOps_sphere(ImgLib2Images images, Radius radius) {
        Img img3Da = images.getImg3Da();
        Img img3Dc = images.getImg3Dc();
        int rad = (int)radius.getRadiusF();
        images.getOpService().filter().mean(img3Dc, img3Da, new HyperSphereShape(rad));
        return img3Dc;
    }

    @Benchmark
    public Object ijOpsCLIJ_box(IJ2CLImages images, Radius radius) {
        Object img3Da = images.getCLImage3Da();
        Object img3Dc = images.getCLImage3Dc();
        int rad = (int)radius.getRadiusF();
        images.getOpService().run(CLIJ_meanBox.class, img3Dc, img3Da, rad, rad, rad);
        return img3Dc;
    }

    @Benchmark
    public Object ijOpsCLIJ_sphere(IJ2CLImages images, Radius radius) {
        Object img3Da = images.getCLImage3Da();
        Object img3Dc = images.getCLImage3Dc();
        int rad = (int)radius.getRadiusF();
        images.getOpService().run(CLIJ_meanSphere.class, img3Dc, img3Da, rad, rad, rad);
        return img3Dc;
    }
}

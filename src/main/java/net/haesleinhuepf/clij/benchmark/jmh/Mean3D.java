package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.plugin.Filters3D;
import ij.plugin.filter.RankFilters;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ShortProcessor;
import mcib3d.image3d.processing.FastFilters3D;
import mpicbg.ij.integral.Mean;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.utilities.CLIJUtilities;
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
        Filters3D.filter(imp3D.getStack(), Filters3D.MEAN, rad, rad, rad );
        return imp3D;
    }

    @Benchmark
    public Object ij_singlethreaded(Images images, Radius radius) {
        int numThreads = Prefs.getThreads();
        Prefs.setThreads(1);
        ImagePlus imp3D = images.getImp3Da();
        int rad = (int)radius.getRadiusF();
        Filters3D.filter(imp3D.getStack(), Filters3D.MEAN, rad, rad, rad );
        Prefs.setThreads(numThreads);
        return imp3D;
    }

    @Benchmark
    public Object ij_multithreaded(Images images, Radius radius) {
        int numThreads = Prefs.getThreads();
        Prefs.setThreads( Runtime.getRuntime().availableProcessors());
        ImagePlus imp3D = images.getImp3Da();
        int rad = (int)radius.getRadiusF();
        Filters3D.filter(imp3D.getStack(), Filters3D.MEAN, rad, rad, rad );
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
}

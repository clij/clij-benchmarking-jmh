package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.Filters3D;
import mcib3d.image3d.processing.FastFilters3D;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.utilities.CLIJUtilities;
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
}

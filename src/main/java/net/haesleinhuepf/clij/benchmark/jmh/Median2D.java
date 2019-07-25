package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.RankFilters;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ShortProcessor;
import mpicbg.ij.integral.Mean;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.utilities.CLIJUtilities;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import process3d.Median_;

@State(Scope.Benchmark)
public class Median2D extends AbstractBenchmark {

    @Benchmark
    public Object ijrun(Images images, Radius radius) {
        ImagePlus imp2D = images.getImp2Da();
        IJ.run(imp2D, "Median...", "radius=" + radius.getRadiusF());
        return imp2D;
    }

    @Benchmark
    public Object clij_box(CLImages images, Radius radius) {
        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();

        int kernelSize = CLIJUtilities.radiusToKernelSize((int)radius.getRadiusF());
        //images.clij.op().medianBox(clb2Da, clb2Dc, kernelSize, kernelSize);

        return clb2Dc;
    }


    @Benchmark
    public Object clij_sphere(CLImages images, Radius radius) {
        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();

        int kernelSize = CLIJUtilities.radiusToKernelSize((int)radius.getRadiusF());
        images.clij.op().medianSphere(clb2Da, clb2Dc, kernelSize, kernelSize);
        return clb2Dc;
    }

    @Benchmark
    public Object ij(Images images, Radius radius) {
        RankFilters rankFilters = new RankFilters();
        ImagePlus imp = images.getImp2Da();
        rankFilters.setup("median", imp);
        rankFilters.rank(imp.getProcessor(), radius.getRadiusF(), RankFilters.MEDIAN);
        return imp;
    }
}

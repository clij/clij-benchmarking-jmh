package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.RankFilters;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.utilities.CLIJUtilities;
import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
public class Minimum2D extends AbstractBenchmark {

    @Benchmark
    public Object ijrun(Images images, Radius radius) {
        ImagePlus imp = images.getImp2Da();
        IJ.run(imp, "Minimum...", "radius=" + radius.getRadiusF());
        return imp;
    }

    @Benchmark
    public Object clij(CLImages images, Radius radius) {

        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();

        int kernelSize = CLIJUtilities.radiusToKernelSize((int)radius.getRadiusF());
        images.clij.op().minimumSphere(clb2Da,
                clb2Dc,
                kernelSize, kernelSize);
        return clb2Dc;
    }

    @Benchmark
    public Object ij(Images images, Radius radius) {
        RankFilters rankFilters = new RankFilters();
        ImagePlus imp = images.getImp2Da();
        rankFilters.setup("min", imp);
        rankFilters.rank(imp.getProcessor(), radius.getRadiusF(), RankFilters.MIN);
        return imp;
    }

}

package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.RankFilters;
import net.haesleinhuepf.clij.utilities.CLIJUtilities;
import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
public class Minimum2D extends AbstractBenchmark {

    @Benchmark
    public Object ijrun(Images images, Radius radius) {
        IJ.run(images.getImp2Da(), "Minimum...", "radius=" + radius.getRadiusF());
        return images.getImp2Da();
    }

    @Benchmark
    public Object clij(CLImages images, Radius radius) {
        int kernelSize = CLIJUtilities.radiusToKernelSize((int)radius.getRadiusF());
        images.clij.op().minimumSphere(images.getCLImage2Da(),
                images.getCLImage2Dc(),
                kernelSize, kernelSize);
        return images.getCLImage2Dc();
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

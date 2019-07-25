package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
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
public class Mean2D extends AbstractBenchmark {

    @Benchmark
    public Object ijrun(Images images, Radius radius) {
        ImagePlus imp2D = images.getImp2Da();
        IJ.run(imp2D, "Mean...", "radius=" + radius.getRadiusF());
        return imp2D;
    }

    @Benchmark
    public Object clij_box(CLImages images, Radius radius) {
        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();

        int rad = (int)radius.getRadiusF();
        images.clij.op().meanBox(clb2Da, clb2Dc, rad, rad, 0);

        return clb2Dc;
    }


    @Benchmark
    public Object clij_sphere(CLImages images, Radius radius) {
        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();

        int kernelSize = CLIJUtilities.radiusToKernelSize((int)radius.getRadiusF());
        images.clij.op().meanSphere(clb2Da, clb2Dc, kernelSize, kernelSize);
        return clb2Dc;
    }

    @Benchmark
    public Object ij(Images images, Radius radius) {
        RankFilters rankFilters = new RankFilters();
        ImagePlus imp = images.getImp2Da();
        rankFilters.setup("mean", imp);
        rankFilters.rank(imp.getProcessor(), radius.getRadiusF(), RankFilters.MEAN);
        return imp;
    }

    @Benchmark
    public Object mpicbg(Images images, Radius radius) {
        ImagePlus imp2D = images.getImp2Da();
        Mean mean = null;
        if (imp2D.getProcessor() instanceof ByteProcessor) {
            mean = new Mean((ByteProcessor) imp2D.getProcessor());
        } else if (imp2D.getProcessor() instanceof ColorProcessor) {
            mean = new Mean((ColorProcessor) imp2D.getProcessor());
        } else if (imp2D.getProcessor() instanceof ShortProcessor) {
            mean = new Mean((ShortProcessor) imp2D.getProcessor());
        } else if (imp2D.getProcessor() instanceof FloatProcessor) {
            mean = new Mean((FloatProcessor) imp2D.getProcessor());
        } else {
            throw new IllegalArgumentException();
        }
        mean.mean((int)radius.getRadiusF());
        return mean;
    }

}

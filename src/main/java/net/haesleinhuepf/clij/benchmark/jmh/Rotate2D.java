package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.RankFilters;
import ij.plugin.filter.Transformer;
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

@State(Scope.Benchmark)
public class Rotate2D extends AbstractBenchmark {

    @Benchmark
    public Object ijrun(Images images) {
        ImagePlus imp2D = images.getImp2Da();
        IJ.run(imp2D, "Rotate 90 Degrees Left", "");
        return imp2D;
    }

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();

        images.clij.op().rotateLeft(clb2Da, clb2Dc);

        return clb2Dc;
    }

    @Benchmark
    public Object ij(Images images) {
        ImagePlus imp2D = images.getImp2Da();
        Transformer transformer = new Transformer();
        transformer.setup("left", imp2D);
        transformer.run(imp2D.getProcessor());
        return imp2D;
    }

}

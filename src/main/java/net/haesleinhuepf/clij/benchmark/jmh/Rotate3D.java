package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.Transformer;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.ops.CLIJ_minimumBox.CLIJ_minimumBox;
import net.haesleinhuepf.clij.ops.CLIJ_rotateLeft.CLIJ_rotateLeft;
import net.imglib2.algorithm.neighborhood.CenteredRectangleShape;
import net.imglib2.img.Img;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class Rotate3D extends AbstractBenchmark {

    @Benchmark
    public Object ijrun(Images images) {
        ImagePlus imp2D = images.getImp2Da();
        IJ.run(imp2D, "Rotate 90 Degrees Left", "");
        return imp2D;
    }

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();

        images.clij.op().rotateLeft(clb3Da, clb3Dc);

        return clb3Dc;
    }

    @Benchmark
    public Object ijOpsCLIJ(IJ2CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();

        images.getOpService().run(CLIJ_rotateLeft.class, clb3Dc, clb3Da);

        return clb3Dc;
    }
}

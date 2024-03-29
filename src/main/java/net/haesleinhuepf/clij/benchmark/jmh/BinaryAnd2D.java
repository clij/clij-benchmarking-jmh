package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.ImageCalculator;
import ij.plugin.Thresholder;
import ij.process.AutoThresholder;
import ij.process.ByteProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.ops.CLIJ_binaryAnd.CLIJ_binaryAnd;
import org.openjdk.jmh.annotations.Benchmark;

public class BinaryAnd2D extends AbstractBenchmark implements BinaryImageBenchmark {
    @Benchmark
    public Object ijapi(Images images) {
        ImageCalculator ic = new ImageCalculator();
        return ic.run("AND create", images.getImp2DBinarya(), images.getImp2DBinaryb());
    }

    @Benchmark
    public Object clij(CLImages images) {

        ClearCLBuffer clb2Da = images.getCLImage2DBinarya();
        ClearCLBuffer clb2Db = images.getCLImage2DBinaryb();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();

        images.clij.op().binaryAnd(clb2Da, clb2Db, clb2Dc);
        return clb2Dc;
    }

    @Benchmark
    public Object ijOpsCLIJ(IJ2CLImages images) {

        ClearCLBuffer clb2Da = images.getCLImage2DBinarya();
        ClearCLBuffer clb2Db = images.getCLImage2DBinaryb();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();

        images.getOpService().run(CLIJ_binaryAnd.class, clb2Dc, clb2Da, clb2Db);
        return clb2Dc;
    }
}
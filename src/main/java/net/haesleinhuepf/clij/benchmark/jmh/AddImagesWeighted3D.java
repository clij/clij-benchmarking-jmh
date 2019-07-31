package net.haesleinhuepf.clij.benchmark.jmh;

import ij.plugin.ImageCalculator;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.ops.CLIJ_addImagesWeighted.CLIJ_addImagesWeighted;
import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import org.openjdk.jmh.annotations.Benchmark;

public class AddImagesWeighted3D extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images) {
        ImageCalculator ic = new ImageCalculator();
        return ic.run("Add create stack", images.getImp3Da(), images.getImp3Db());
    }

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Db = images.getCLImage3Db();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();

        images.clij.op().addImagesWeighted(clb3Da, clb3Db, clb3Dc, 1f, 1f);
        return clb3Dc;
    }

    @Benchmark
    public Object ijOps(ImgLib2Images images) {
        Img img3Da = images.getImg3Da();
        Img img3Db = images.getImg3Db();
        Img img3Dc = images.getImg3Dc();
        images.getOpService().math().add(img3Dc, img3Da, (IterableInterval)img3Db);
        return img3Dc;
    }

    @Benchmark
    public Object ijOpsCLIJ(IJ2CLImages images) {
        ClearCLBuffer clb3Da = images.getCLImage3Da();
        ClearCLBuffer clb3Db = images.getCLImage3Db();
        ClearCLBuffer clb3Dc = images.getCLImage3Dc();

        images.getOpService().run(CLIJ_addImagesWeighted.class, clb3Dc, clb3Da, clb3Db, 1f, 1f);
        return clb3Dc;
    }
}

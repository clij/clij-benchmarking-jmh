package net.haesleinhuepf.clij.benchmark.jmh;

import ij.plugin.ImageCalculator;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.ops.CLIJ_addImagesWeighted.CLIJ_addImagesWeighted;
import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import org.openjdk.jmh.annotations.Benchmark;

public class AddImagesWeighted2D extends AbstractBenchmark {
    @Benchmark
    public Object ij(Images images) {
        ImageCalculator ic = new ImageCalculator();
        return ic.run("Add create", images.getImp2Da(), images.getImp2Db());
    }

    @Benchmark
    public Object clij(CLImages images) {
        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Db = images.getCLImage2Db();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();

        images.clij.op().addImagesWeighted(clb2Da, clb2Db, clb2Dc, 1f, 1f);
        return clb2Dc;
    }

    @Benchmark
    public Object ijOps(ImgLib2Images images) {
        Img img2Da = images.getImg2Da();
        Img img2Db = images.getImg2Db();
        Img img2Dc = images.getImg2Dc();
        images.getOpService().math().add(img2Dc, img2Da, (IterableInterval)img2Db);
        return img2Dc;
    }

    @Benchmark
    public Object ijOpsCLIJ(IJ2CLImages images) {
        ClearCLBuffer clb2Da = images.getCLImage2Da();
        ClearCLBuffer clb2Db = images.getCLImage2Db();
        ClearCLBuffer clb2Dc = images.getCLImage2Dc();

        images.getOpService().run(CLIJ_addImagesWeighted.class, clb2Dc, clb2Da, clb2Db, 1f, 1f);
        return clb2Dc;
    }
}

package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.RankFilters;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.ops.CLIJ_minimumBox.CLIJ_minimumBox;
import net.haesleinhuepf.clij.utilities.CLIJUtilities;
import net.imglib2.algorithm.neighborhood.CenteredRectangleShape;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.img.Img;
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

    @Benchmark
    public Object ijOps_sphere(ImgLib2Images images, Radius radius) {
        Img img2Da = images.getImg2Da();
        Img img2Dc = images.getImg2Dc();
        int rad = (int)radius.getRadiusF();
        images.getOpService().filter().min(img2Dc, img2Da, new HyperSphereShape(rad));
        return img2Dc;
    }

    @Benchmark
    public Object ijOps_box(ImgLib2Images images, Radius radius) {
        Img img2Da = images.getImg2Da();
        Img img2Dc = images.getImg2Dc();
        int rad = (int)radius.getRadiusF();
        images.getOpService().filter().min(img2Dc, img2Da, new CenteredRectangleShape(new int[]{rad, rad}, false));
        return img2Dc;
    }

    @Benchmark
    public Object ijOpsCLIJ_box(IJ2CLImages images, Radius radius) {
        Object img2Da = images.getCLImage2Da();
        Object img2Dc = images.getCLImage2Dc();
        int rad = (int)radius.getRadiusF();
        images.getOpService().run(CLIJ_minimumBox.class, img2Dc, img2Da, rad, rad, rad);
        return img2Dc;
    }
}

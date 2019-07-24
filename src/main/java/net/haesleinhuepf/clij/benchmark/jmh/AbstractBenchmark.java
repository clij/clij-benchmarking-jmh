package net.haesleinhuepf.clij.benchmark.jmh;

import ij.ImagePlus;
import ij.Prefs;
import ij.gui.NewImage;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.RankFilters;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-server", "-Xms4G", "-Xmx4G"})
public class AbstractBenchmark {
    private static final int DEFAULT_THREADS = Prefs.getThreads();
    private RankFilters rankFilters = new RankFilters();
    private GaussianBlur gaussianBlur = new GaussianBlur();
    // Do not make final to avoid the JVM optimising use of this!
    private ImagePlus emptyImp = new ImagePlus();
    @State(Scope.Benchmark)
    public static class Radius {
        @Param({"1", "10"})
        int radius;
        float getRadiusF() {
            return radius;
        }
    }
    @State(Scope.Benchmark)
    public static class Images {
        // Use a single pixel for testing the clijNoOp
//@Param({"1"})
        //@Param({"1", "512", "1024", "2048", "4096"})
        @Param({"1", "2048"})
        int size;

        ImagePlus imp2Da;
        ImagePlus imp2Db;
        ImagePlus imp2Dc;
        ImagePlus imp3Da;
        ImagePlus imp3Db;
        ImagePlus imp3Dc;
        //ImageProcessor getImage2D() {
            //return imp2Da.getProcessor().duplicate();
        //}
        ImagePlus getImp2Da() {
            return imp2Da;
        }
        ImagePlus getImp2Db() {
            return imp2Db;
        }
        ImagePlus getImp2Dc() {
            return imp2Dc;
        }
        ImagePlus getImp3Da() {
            return imp3Da;
        }
        ImagePlus getImp3Db() {
            return imp3Db;
        }
        ImagePlus getImp3Dc() {
            return imp3Dc;
        }

        @Setup
        public void setup() {
            imp2Da = NewImage.createByteImage("title", size, size, 1,
                    NewImage.FILL_RANDOM);
            imp2Db = NewImage.createByteImage("title", size, size, 1,
                    NewImage.FILL_RANDOM);
            imp2Dc = NewImage.createByteImage("title", size, size, 1,
                    NewImage.FILL_RANDOM);
            imp3Da = NewImage.createByteImage("title", size, size, 10,
                    NewImage.FILL_RANDOM);
            imp3Db = NewImage.createByteImage("title", size, size, 10,
                    NewImage.FILL_RANDOM);
            imp3Dc = NewImage.createByteImage("title", size, size, 10,
                    NewImage.FILL_RANDOM);
        }
    }


        @State(Scope.Benchmark)
    public static class CLImages extends Images {
        CLIJ clij;
        ClearCLBuffer buffer2Da;
        ClearCLBuffer buffer2Db;
        ClearCLBuffer buffer2Dc;

        ClearCLBuffer buffer3Da;
        ClearCLBuffer buffer3Db;
        ClearCLBuffer buffer3Dc;

        ClearCLBuffer getCLImage2Da() {
            return buffer2Da;
        }
        ClearCLBuffer getCLImage2Db() {
            return buffer2Db;
        }
        ClearCLBuffer getCLImage2Dc() {
            return buffer2Dc;
        }

        ClearCLBuffer getCLImage3Da() {
            return buffer3Da;
        }
        ClearCLBuffer getCLImage3Db() {
            return buffer3Db;
        }
        ClearCLBuffer getCLImage3Dc() {
            return buffer3Dc;
        }

        @Override
        @Setup
        public void setup() {
            super.setup();
            clij = CLIJ.getInstance();
            System.out.println("CLIJ " + clij);

            buffer2Da = clij.convert(imp2Da, ClearCLBuffer.class);
            buffer2Db = clij.convert(imp2Db, ClearCLBuffer.class);
            buffer2Dc = clij.convert(imp2Dc, ClearCLBuffer.class);

            buffer3Da = clij.convert(imp3Da, ClearCLBuffer.class);
            buffer3Db = clij.convert(imp3Db, ClearCLBuffer.class);
            buffer3Dc = clij.convert(imp3Dc, ClearCLBuffer.class);
        }
        @TearDown
        public void tearDown() {
            buffer2Da.close();
            buffer2Db.close();
            buffer2Dc.close();
            buffer3Da.close();
            buffer3Db.close();
            buffer3Dc.close();
            clij.setConverterService(null);
            clij.close();
        }
    }
/*
    @Benchmark
    public Object ijNoOp() {
// The image will not have an ROI so the call to ImagePlus.deleteRoi() does nothing.
        IJ.run(emptyImp, "Select None", "");
        return emptyImp;
    }
    //@Benchmark
    public Object clijNoOp(CLImages images) {
// clij-core can be modified to run this as a true no-op:
// Comment out net.haesleinhuepf.clij.utilities.CLKernelExecutor Line 293
// kernel.run(waitToFinish);
// Ideally there should be a no-op kernel for testing CLIJ overhead.
        return images.clij.op().set(images.buffer2Dc, 0f);
    }

    @Benchmark
    public Object ijCopy(Images images) {
        return images.getImp2Da(); //.getImage2D();
    }

    @Benchmark
    public Object clijCopy(CLImages images) {
        return images.getCLImage2Da();
    }

    @Benchmark
    public Object ijMean(Images images, Radius radius) {
        ImageProcessor ip = images.getImage2D();
        rankFilters.rank(ip, radius.radius, RankFilters.MEAN);
        return ip;
    }
    @Benchmark
    public Object ijMeanM(Images images, Radius radius) {
        ImagePlus imp2Da = images.getImp2Da();
        IJ.run(imp2Da, "Mean...", "radius=" + radius.radius);
        return imp2Da;
    }
    @Benchmark
    public Object ijMeanOneThread(Images images, Radius radius) {
        Prefs.setThreads(1);
        ImageProcessor ip = images.getImage2D();
        new RankFilters().rank(ip, radius.radius, RankFilters.MEAN);
        Prefs.setThreads(DEFAULT_THREADS);
        return ip;
    }
    @Benchmark
    public Object clijMean(CLImages images, Radius radius) {
        images.clij.op().meanSphere(images.getCLImage2Da(),
                images.getCLImage2Dc(),
                radius.radius, radius.radius);
        return images.buffer2Dc;
    }
    @Benchmark
    public Object ijGaussianBlur(Images images, Radius radius) {
        ImageProcessor ip = images.getImage2D();
        gaussianBlur.blurGaussian(ip, radius.radius);
        return ip;
    }
    @Benchmark
    public Object ijGaussianBlurM(Images images, Radius radius) {
        ImagePlus imp2Da = images.getImp2Da();
        IJ.run(imp2Da, "Gaussian Blur...", "sigma=" + radius.radius);
        return imp2Da;
    }
    @Benchmark
    public Object ijGaussianBlurOneThread(Images images, Radius radius) {
        Prefs.setThreads(1);
        ImageProcessor ip = images.getImage2D();
        gaussianBlur.blurGaussian(ip, radius.radius);
        Prefs.setThreads(DEFAULT_THREADS);
        return ip;
    }
    @Benchmark
    public Object clijGaussianBlur(CLImages images, Radius radius) {
        images.clij.op().blur(images.getCLImage2Da(),
                images.getCLImage2Dc(),
                radius.getRadiusF(), radius.getRadiusF());
        return images.buffer2Dc;
    }*/
}

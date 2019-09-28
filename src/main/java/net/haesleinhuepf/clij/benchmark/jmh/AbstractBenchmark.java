package net.haesleinhuepf.clij.benchmark.jmh;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.gui.NewImage;
import ij.plugin.Duplicator;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.RankFilters;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.CLIJService;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.rgc.RessourceCleaner;
import net.imagej.ops.OpService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.view.Views;
import org.openjdk.jmh.annotations.*;
import org.scijava.Context;
import org.scijava.io.IOService;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

//@BenchmarkMode(Mode.AverageTime)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10, batchSize = 1)
@Measurement(iterations = 100, batchSize = 1)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-server", "-Xms2G", "-Xmx2G"})
public class AbstractBenchmark {
    private static final int DEFAULT_THREADS = Prefs.getThreads();
    private RankFilters rankFilters = new RankFilters();
    private GaussianBlur gaussianBlur = new GaussianBlur();
    // Do not make final to avoid the JVM optimising use of this!
    private ImagePlus emptyImp = new ImagePlus();
    @State(Scope.Benchmark)
    public static class Radius {
        @Param({"2", "4", "6", "10"})
        int radius;
        float getRadiusF() {
            return radius;
        }
        public void setRadius(int radius) {
            this.radius = radius;
        }
    }
    @State(Scope.Benchmark)
    public static class Images {
        // Use a single pixel for testing the clijNoOp
        //@Param({"1"})
        @Param({"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"})
        //@Param({"4096"/*, "2048"*/})
        int size;

        int[][] sizes2D = {
                {1, 1, 1},
                {128, 128, 1},
                {256, 256, 1},
                {512, 512, 1},
                {600, 600, 1},
                {800, 800, 1},
                {1024, 1024, 1},
                {1200, 1200, 1},
                {1400, 1400, 1},
                {1600, 1600, 1},
                {1800, 1800, 1},
                {2048, 2048, 1},
                {3072, 3072, 1},
                {4096, 4096, 1}
        };

        int[][] sizes3D = {
                {1, 1, 1},
                {1024, 1024, 4},
                {1024, 1024, 8},
                {1024, 1024, 12},
                {1024, 1024, 16},
                {1024, 1024, 20},
                {1024, 1024, 24},
                {1024, 1024, 32},
                {1024, 1024, 40},
                {1024, 1024, 50},
                {1024, 1024, 64},
                {1024, 1024, 80},
                {1024, 1024, 100},
                {1024, 1024, 128}
        };

        ImagePlus imp2Da;
        ImagePlus imp2Db;
        ImagePlus imp2Dc;
        ImagePlus imp3Da;
        ImagePlus imp3Db;
        ImagePlus imp3Dc;
        ImagePlus imp2Dbinarya;
        ImagePlus imp2Dbinaryb;
        ImagePlus imp3Dbinarya;
        ImagePlus imp3Dbinaryb;

        public void set2DImage(ImagePlus imp) {
            imp2Da = new Duplicator().run(imp);
            imp2Db = new Duplicator().run(imp);
            imp2Dc = new Duplicator().run(imp);
        }
        public void set2DBinaryImage(ImagePlus imp) {
            imp2Dbinarya = new Duplicator().run(imp);
            imp2Dbinaryb = new Duplicator().run(imp);
        }
        public void set3DImage(ImagePlus imp) {
            imp3Da = new Duplicator().run(imp, 1, imp.getNSlices());
            imp3Db = new Duplicator().run(imp, 1, imp.getNSlices());
            imp3Dc = new Duplicator().run(imp, 1, imp.getNSlices());
        }
        public void set3DBinaryImage(ImagePlus imp) {
            imp3Dbinarya = new Duplicator().run(imp, 1, imp.getNSlices());
            imp3Dbinaryb = new Duplicator().run(imp, 1, imp.getNSlices());
        }

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

        ImagePlus getImp2DBinarya() {
            return imp2Dbinarya;
        }
        ImagePlus getImp2DBinaryb() {
            return imp2Dbinaryb;
        }
        ImagePlus getImp3DBinarya() {
            return imp3Dbinarya;
        }
        ImagePlus getImp3DBinaryb() {
            return imp3Dbinaryb;
        }

        HashMap<String, ImagePlus> randomImages = new HashMap<String, ImagePlus>();
        int invocationCount = 0;

        ImagePlus openImage(String filename) {
            ImagePlus imp = null;
            //if (randomImages.containsKey(filename)) {
            //    imp = randomImages.get(filename);
            //} else {
                imp = IJ.openImage(filename);
            //    if (randomImages.size() >= 10) {
            //        randomImages.clear();
            //    }
            //    randomImages.put(filename, imp);
            //}
            //return new Duplicator().run(imp);
            return imp;
        }

        @Setup(Level.Invocation)
        public void setup() {
            //System.out.println("im setup");

            int size2D[] = sizes2D[size];
            int size3D[] = sizes3D[size];

            invocationCount = (invocationCount + 1) % 10;

            String filename2D = "./random" + invocationCount + "_" +size2D[0] + "_" + size2D[1] + "_" + size2D[2] + ".tif";
            checkExistingFile(size2D[0], size2D[1], size2D[2], filename2D);

            String filename3D = "./random" + invocationCount + "_" + size3D[0] + "_" + size3D[1] + "_" + size3D[2] + ".tif";
            checkExistingFile(size3D[0], size3D[1], size3D[2], filename3D);

            imp2Da = openImage(filename2D);
            imp2Db = new Duplicator().run(imp2Da);
            imp2Dc = new Duplicator().run(imp2Da);

            imp2Dbinarya = new Duplicator().run(imp2Da);
            IJ.setThreshold(imp2Dbinarya, 128, 255);
            IJ.run(imp2Dbinarya, "Convert to Mask", "method=Default background=Dark black");
            imp2Dbinaryb = new Duplicator().run(imp2Dbinarya);


            imp3Da = openImage(filename3D);
            imp3Db = new Duplicator().run(imp3Da, 1, imp3Da.getNSlices());
            imp3Dc = new Duplicator().run(imp3Da, 1, imp3Da.getNSlices());

            imp3Dbinarya = new Duplicator().run(imp3Da);
            IJ.setThreshold(imp3Dbinarya, 128, 255);
            IJ.run(imp3Dbinarya, "Convert to Mask", "method=Default background=Dark black");
            imp3Dbinaryb = new Duplicator().run(imp3Dbinarya);

        }


        @TearDown(Level.Invocation)
        public void tearDown() {
            IJ.run("Close All");
        }

        private void checkExistingFile(int sizeX, int sizeY, int sizeZ, String filename) {
            if (!new File(filename).exists()) {
                ImagePlus imp = NewImage.createShortImage("title", sizeX, sizeY, sizeZ,
                        NewImage.FILL_RANDOM);
                IJ.save(imp, filename);
            }
        }

        public void reinit(){};
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

        ClearCLBuffer buffer2DBinarya;
        ClearCLBuffer buffer2DBinaryb;
        ClearCLBuffer buffer3DBinarya;
        ClearCLBuffer buffer3DBinaryb;

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

        ClearCLBuffer getCLImage2DBinarya() {
            return buffer2DBinarya;
        }
        ClearCLBuffer getCLImage2DBinaryb() {
            return buffer2DBinaryb;
        }

        ClearCLBuffer getCLImage3DBinarya() {
            return buffer3DBinarya;
        }
        ClearCLBuffer getCLImage3DBinaryb() {
            return buffer3DBinaryb;
        }

            @Override
        @Setup(Level.Invocation)
        public void setup() {
                super.setup();
                reinit();
        }

        @Override
        public void reinit() {
            tearDown();
            //System.out.println("cl setup");
            clij = CLIJ.getInstance("");
//            clij = CLIJ.getInstance("Intel");
            //System.out.println("GPU: " + clij.getGPUName());
            //System.out.println("Number of registered objects: " + RessourceCleaner.getNumberOfRegisteredObjects());

            buffer2Da = clij.convert(imp2Da, ClearCLBuffer.class);
            buffer2Db = clij.convert(imp2Db, ClearCLBuffer.class);
            buffer2Dc = clij.convert(imp2Dc, ClearCLBuffer.class);

            buffer3Da = clij.convert(imp3Da, ClearCLBuffer.class);
            buffer3Db = clij.convert(imp3Db, ClearCLBuffer.class);
            buffer3Dc = clij.convert(imp3Dc, ClearCLBuffer.class);

            buffer2DBinarya = clij.convert(imp2Dbinarya, ClearCLBuffer.class);
            buffer2DBinaryb = clij.convert(imp2Dbinaryb, ClearCLBuffer.class);
            buffer3DBinarya = clij.convert(imp3Dbinarya, ClearCLBuffer.class);
            buffer3DBinaryb = clij.convert(imp3Dbinaryb, ClearCLBuffer.class);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            super.tearDown();
            //System.out.println("cl teardown");
            buffer2Da = clearIfNotNull(buffer2Da);
            buffer2Db = clearIfNotNull(buffer2Db);
            buffer2Dc = clearIfNotNull(buffer2Dc);
            buffer3Da = clearIfNotNull(buffer3Da);
            buffer3Db = clearIfNotNull(buffer3Db);
            buffer3Dc = clearIfNotNull(buffer3Dc);

            buffer2DBinarya = clearIfNotNull(buffer2DBinarya);
            buffer2DBinaryb = clearIfNotNull(buffer2DBinaryb);
            buffer3DBinarya = clearIfNotNull(buffer3DBinarya);
            buffer3DBinaryb = clearIfNotNull(buffer3DBinaryb);

            /*
            RessourceCleaner.cleanNow();
            System.gc();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Close");
            */
        }

        private ClearCLBuffer clearIfNotNull(ClearCLBuffer buffer) {
            synchronized (this) {
                if (buffer != null) {
                    buffer.close();
                }
                return null;
            }
        }

        @TearDown
        public void tearDownFinally() {
            clij.setConverterService(null);
            clij.close();
        }
    }

    @State(Scope.Benchmark)
    public static class ImgLib2Images extends Images {

        Img img2Da;
        Img img2Db;
        Img img2Dc;
        Img img3Da;
        Img img3Db;
        Img img3Dc;
        Img img2Dbinarya;
        Img img2Dbinaryb;
        Img img3Dbinarya;
        Img img3Dbinaryb;

        OpService opService;

        Img getImg2Da() { return img2Da; }
        Img getImg2Db() { return img2Db; }
        Img getImg2Dc() { return img2Dc; }
        Img getImg3Da() { return img3Da; }
        Img getImg3Db() { return img3Db; }
        Img getImg3Dc() { return img3Dc; }
        Img getImg2Dbinarya() { return img2Dbinarya; }
        Img getImg2Dbinaryb() { return img2Dbinaryb; }
        Img getImg3Dbinarya() { return img3Dbinarya; }
        Img getImg3Dbinaryb() { return img3Dbinaryb; }

        @Setup(Level.Invocation)
        public void setup() {

            super.setup();

            reinit();

        }

        @Override
        public void reinit() {

            Context context = new Context(OpService.class);
            opService = context.service(OpService.class);

            img2Da = ImageJFunctions.wrap(imp2Da).copy();
            img2Db = ImageJFunctions.wrap(imp2Db).copy();
            img2Dc = ImageJFunctions.wrap(imp2Dc).copy();

            img3Da = ImageJFunctions.wrap(imp3Da).copy();
            img3Db = ImageJFunctions.wrap(imp3Db).copy();
            img3Dc = ImageJFunctions.wrap(imp3Dc).copy();

            img2Dbinarya = opService.convert().bit(Views.iterable((RandomAccessibleInterval)ImageJFunctions.wrap(imp2Dbinarya))).copy();
            img2Dbinaryb = opService.convert().bit(Views.iterable((RandomAccessibleInterval)ImageJFunctions.wrap(imp2Dbinaryb))).copy();
            img3Dbinarya = opService.convert().bit(Views.iterable((RandomAccessibleInterval)ImageJFunctions.wrap(imp3Dbinarya))).copy();
            img3Dbinaryb = opService.convert().bit(Views.iterable((RandomAccessibleInterval)ImageJFunctions.wrap(imp3Dbinaryb))).copy();

        }

        public OpService getOpService() {
            return opService;
        }

        @TearDown(Level.Invocation)
        public void teardown() {
            super.tearDown();
        }
    }

    @State(Scope.Benchmark)
    public static class IJ2CLImages extends CLImages {

        OpService opService;

        @Setup(Level.Invocation)
        public void setup() {

            super.setup();

            reinit();

        }

        @Override
        public void reinit() {
            super.reinit();
            Context context = new Context(OpService.class, CLIJService.class);
            opService = context.service(OpService.class);
        }

        public OpService getOpService() {
            return opService;
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

package net.haesleinhuepf.clij.benchmark.imagecomparison;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.NewImage;
import ij.gui.Roi;
import ij.plugin.Duplicator;
import ij.process.ImageStatistics;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.benchmark.jmh.*;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.realtransform.AffineTransform2D;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;


public class ImageComparison {
    public static void main(String... args) throws InvocationTargetException, IllegalAccessException, IOException {
        AbstractBenchmark[] benchmarks = {
                new AANoOp(),
                new AddImagesWeighted2D(),
                new AddImagesWeighted3D(),
                new AddScalar2D(),
                new AddScalar3D(),
                new AutoThreshold2D(),
                new AutoThreshold3D(),
                new BinaryAnd2D(),
                new BinaryAnd3D(),
                new Erode2D(),
                new Erode3D(),
                new Flip2D(),
                new Flip3D(),
                new GaussianBlur2D(),
                new GaussianBlur3D(),
                new MaximumZProjection(),
                new Mean2D(),
                new Mean3D(),
                new Median2D(),
                new Median3D(),
                new Minimum2D(),
                new Minimum3D(),
                new MultiplyScalar2D(),
                new MultiplyScalar3D(),
                new RadialReslice(),
                new Rotate2D(),
                new Rotate3D(),
                new Threshold2D(),
                new Threshold3D()
        };


        Roi roi = new Roi(2, 2, 5, 5);

        ImagePlus imp2D = NewImage.createByteImage("2d", 10, 10, 1, NewImage.FILL_BLACK);
        imp2D.setRoi(roi);
        imp2D.getProcessor().add(255);
        imp2D.killRoi();

        ImagePlus imp2DBinary = new Duplicator().run(imp2D);
        IJ.setThreshold(imp2DBinary, 128, 255);
        IJ.run(imp2DBinary, "Convert to Mask", "method=Default background=Dark black");

        ImagePlus imp3D = NewImage.createByteImage("2d", 10, 10, 10, NewImage.FILL_BLACK);
        for (int z = 2; z <= 6; z++) {
            imp3D.setZ(z);
            imp3D.setRoi(roi);
            imp3D.getProcessor().add(255);
        }
        imp3D.killRoi();

        ImagePlus imp3DBinary = new Duplicator().run(imp3D, 1, imp3D.getNSlices());
        IJ.setThreshold(imp3DBinary, 128, 255);
        IJ.run(imp3DBinary, "Convert to Mask", "method=Default background=Dark black");

        String foldername = "imagecomparsion/";

        new File("./" + foldername).mkdirs();

        StringBuilder html = new StringBuilder();
        html.append("<html><body><table>");

        long scaleFactor = 10;

        for (AbstractBenchmark benchmark : benchmarks) {
            System.out.println("--------------" + benchmark.getClass().getSimpleName());

            StringBuilder headline = new StringBuilder();
            headline.append("<tr><td>" + benchmark.getClass().getSimpleName() + "</td>");

            StringBuilder contentline = new StringBuilder();
            contentline.append("<tr><td>&nbsp;</td>");

            StringBuilder statsline = new StringBuilder();
            statsline.append("<tr><td>&nbsp;</td>");

            for (Method method : benchmark.getClass().getMethods()) {
                String methodname = method.getName();
                System.out.println(methodname);
                if (
                        methodname.startsWith("clij") ||
                        methodname.startsWith("mpicbg") ||
                        methodname.startsWith("vib") ||
                        methodname.startsWith("ij") ||
                        methodname.startsWith("mcib3d")
                ) {
                    //System.out.println(methodname);
                    headline.append("<td>" + methodname + "</td>");

                    AbstractBenchmark.Radius radius = new AbstractBenchmark.Radius();
                    radius.setRadius(2);
                    final AbstractBenchmark.Images images;
                    if(methodname.startsWith("ijOps")) {
                        if(methodname.startsWith("ijOpsCLIJ")) {
                            images = new AbstractBenchmark.IJ2CLImages();
                        } else {
                            images = new AbstractBenchmark.ImgLib2Images();
                        }
                    }
                    else {
                        images = new AbstractBenchmark.CLImages();
                    }
                    images.set2DImage(imp2D);
                    images.set2DBinaryImage(imp2DBinary);
                    images.set3DImage(imp3D);
                    images.set3DBinaryImage(imp3DBinary);
                    images.reinit();

                    Object result = null;
                    if (method.getParameterCount() == 1) {
                        result = method.invoke(benchmark, images);
                    } else if (method.getParameterCount() == 2) {
                        result = method.invoke(benchmark, images, radius);
                    }
                    if (result != null) {
                        ImagePlus impResult = null;
                        if (result instanceof ImagePlus) {
                            impResult = (ImagePlus) result;
                        } else if (result instanceof ClearCLBuffer) {
                            impResult = CLIJ.getInstance().pull((ClearCLBuffer) result);
                        }else if (result instanceof RandomAccessibleInterval) {
	                        impResult = ImageJFunctions.wrap((RandomAccessibleInterval)result, "result");
                        }
                        if (impResult != null) {
                            if (impResult.getNSlices() > 1) {
                                impResult = new Duplicator().run(impResult, 4,4);
                            }

                            ImageStatistics stats = impResult.getStatistics();
                            statsline.append("<td>");
                            statsline.append("Mean: " + stats.mean);
                            statsline.append("<br/>Std: " + stats.stdDev);
                            statsline.append("<br/>Min: " + stats.min);
                            statsline.append("<br/>Max: " + stats.max);

                            //IJ.run(impResult, "Scale...", "x=10 y=10 width=100 height=100 interpolation=None create");
                            //new Scaler();
                            CLIJ clij = CLIJ.getInstance();
                            ClearCLBuffer clResult = clij.push(impResult);
                            ClearCLBuffer clResultUpsampled = clij.create(new long[]{clResult.getWidth() * scaleFactor, clResult.getHeight() * scaleFactor}, clResult.getNativeType());

                            AffineTransform2D at = new AffineTransform2D();
                            at.scale(scaleFactor);

                            if (benchmark instanceof BinaryImageBenchmark) {
                                ClearCLBuffer clTemp = clij.createCLBuffer(clResult);
                                clij.op().multiplyImageAndScalar(clResult, clTemp, 255f);
                                clij.op().affineTransform2D(clTemp, clResultUpsampled, at);
                                clTemp.close();
                            } else {
                                clij.op().affineTransform2D(clResult, clResultUpsampled, at);
                            }

                            impResult = clij.pull(clResultUpsampled);
                            clResult.close();
                            clResultUpsampled.close();

                            statsline.append("</td>");

                            String filename = benchmark.getClass().getSimpleName() + "." + methodname + ".png";
                            IJ.save(impResult, foldername + filename);
                            contentline.append("<td><img src=\"" + filename + "\" width=\"100\"></td>");
                        } else {
                            contentline.append("<td>no image</td>");
                            statsline.append("<td></td>");
                        }
                    } else {
                        contentline.append("<td>no result</td>");
                        statsline.append("<td></td>");
                    }
                    images.tearDown();
                }

            }
            statsline.append("</tr>");
            contentline.append("</tr>");
            headline.append("</tr>");

            html.append(headline.toString());
            html.append(contentline.toString());
            html.append(statsline.toString());

        }
        html.append("</table></body></html>");
        Files.write(Paths.get(foldername + "index.html"), html.toString().getBytes());

        System.out.println("Bye");
        IJ.exit();
    }
}

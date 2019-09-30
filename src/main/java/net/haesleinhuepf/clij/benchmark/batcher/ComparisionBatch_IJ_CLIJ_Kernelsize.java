package net.haesleinhuepf.clij.benchmark.batcher;

import net.haesleinhuepf.clij.benchmark.jmh.*;

import java.io.IOException;

/**
 * Generate a batch file for comparison of IJ and CLIJ calls using fixed image size and different radii/sigma parameters
 *
 * @author Robert Haase
 *         August 2019
 */
public class ComparisionBatch_IJ_CLIJ_Kernelsize extends AbstractBatchGenerator {
    public static void main(String[] args) throws IOException {
        String targetDir = "C:/structure/code/clij-benchmarking/data/benchmarking-jmh/kernelsize/";
        String batchName = "batch_ij__clij_comparison_radii_" + System.getenv().get("COMPUTERNAME")+ ".bat";

        StringBuilder batch = new StringBuilder();
        for (int radius = 1; radius <= 15; radius = radius + 1) {



            for (AbstractBenchmark benchmark : benchmarks) {
                if (benchmark instanceof GaussianBlur2D ||
                    benchmark instanceof GaussianBlur3D ||
                    benchmark instanceof Minimum2D ||
                    benchmark instanceof Minimum3D 
                ) {
                    int radiusDependingOnBenchmark = radius;
                    if (benchmark instanceof GaussianBlur2D ||
                            benchmark instanceof GaussianBlur3D) {
                        radiusDependingOnBenchmark = radius * 2;
                    }
                    String additionalParameters;
                    if (benchmark.getClass().getSimpleName().contains("2D")) {
                        additionalParameters = " -p size=11 -p radius=" + radiusDependingOnBenchmark + " ";
                    } else {
                        additionalParameters = " -p size=7 -p radius=" + radiusDependingOnBenchmark + " ";
                    }

                    String ij = getBatchEntry(new String[]{"ijrun", "ijapi", "vib"}, targetDir, benchmark, additionalParameters);
                    String clij = getBatchEntry(new String[]{"clij", "clij_sphere"}, targetDir, benchmark, additionalParameters);

                    ij = ij.replace(
                            "_." + benchmark.getClass().getSimpleName() + ".",
                            "_." + benchmark.getClass().getSimpleName() + "_radius_" + radiusDependingOnBenchmark + ".");
                    clij = clij.replace(
                            "_." + benchmark.getClass().getSimpleName() + ".",
                            "_." + benchmark.getClass().getSimpleName() + "_radius_" + radiusDependingOnBenchmark + ".");

                    if (clij.length() > 0 && ij.length() > 0) {
                        batch.append(ij.split("\r\n")[0] + "\r\n");
                        batch.append(ij.split("\r\n")[1] + "\r\n");
                        batch.append(clij.split("\r\n")[0] + "\r\n");
                        batch.append(clij.split("\r\n")[1] + "\r\n");
                    } else {
                        System.out.println("Benchmark not complete: " + benchmark);
                    }
                }
            }
        }
        writeFile(batchName, batch.toString());
    }
}

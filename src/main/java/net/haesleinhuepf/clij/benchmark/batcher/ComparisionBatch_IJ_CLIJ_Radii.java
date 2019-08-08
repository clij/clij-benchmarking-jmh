package net.haesleinhuepf.clij.benchmark.batcher;

import net.haesleinhuepf.clij.benchmark.jmh.*;

import java.io.IOException;

/**
 * Generate a batch file for comparison of IJ and CLIJ calls using fixed image size and different radii/sigma parameters
 *
 * @author Robert Haase
 *         August 2019
 */
public class ComparisionBatch_IJ_CLIJ_Radii extends AbstractBatchGenerator {
    public static void main(String[] args) throws IOException {
        String targetDir = "C:/structure/code/clij-benchmarking-revision/clij_benchmarking_jmh/ij_clij_radii_comparison/";
        String batchName = "batch_ij__clij_comparison_radii_" + System.getenv().get("COMPUTERNAME")+ ".bat";

        StringBuilder batch = new StringBuilder();
        for (int radius = 2; radius <= 64; radius = radius * 2) {

            String additionalParameters = " -p size=3 -p radius=" + radius + " ";


            for (AbstractBenchmark benchmark : benchmarks) {
                if (benchmark instanceof GaussianBlur2D ||
                    benchmark instanceof GaussianBlur3D ||
                    benchmark instanceof Minimum2D ||
                    benchmark instanceof Minimum3D ||
                    benchmark instanceof Mean2D ||
                    benchmark instanceof Mean3D
                ) {
                    String ij = getBatchEntry(new String[]{"ijrun", "ijapi", "vib"}, targetDir, benchmark, additionalParameters);
                    String clij = getBatchEntry(new String[]{"clij", "clij_sphere"}, targetDir, benchmark, additionalParameters);

                    ij = ij.replace(
                            "_." + benchmark.getClass().getSimpleName() + ".",
                            "_." + benchmark.getClass().getSimpleName() + "_radius_" + radius + ".");
                    clij = clij.replace(
                            "_." + benchmark.getClass().getSimpleName() + ".",
                            "_." + benchmark.getClass().getSimpleName() + "_radius_" + radius + ".");

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

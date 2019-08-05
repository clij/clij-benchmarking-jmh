package net.haesleinhuepf.clij.benchmark.batcher;

import net.haesleinhuepf.clij.benchmark.jmh.AbstractBenchmark;

import java.io.IOException;

/**
 * Generate a batch file for comparison of IJ and CLIJ calls
 *
 * @author Robert Haase
 *         August 2019
 */
public class ComparisionBatch_IJ_CLIJ extends AbstractBatchGenerator {
    public static void main(String[] args) throws IOException {
        String targetDir = "C:/structure/code/clij-benchmarking-revision/clij_benchmarking_jmh/ij_clij_comparison/";
        String batchName = "batch_ij__clij_comparison_" + System.getenv().get("COMPUTERNAME")+ ".bat";
        String additionalParameters = " -p radius=2 ";

        StringBuilder batch = new StringBuilder();

        for (AbstractBenchmark benchmark : benchmarks) {
            String ij = getBatchEntry(new String[]{"ijrun", "ijapi", "vib"}, targetDir, benchmark, additionalParameters);
            String clij = getBatchEntry(new String[]{"clij", "clij_sphere"}, targetDir, benchmark, additionalParameters);

            if (clij.length() > 0 && ij.length() > 0) {
                batch.append(ij.split("\r\n")[0] + "\r\n");
                batch.append(ij.split("\r\n")[1] + "\r\n");
                batch.append(clij.split("\r\n")[0] + "\r\n");
                batch.append(clij.split("\r\n")[1] + "\r\n");
            } else {
                System.out.println("Benchmark not complete: " + benchmark);
            }
        }
        writeFile(batchName, batch.toString());
    }
}

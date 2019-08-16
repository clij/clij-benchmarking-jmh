package net.haesleinhuepf.clij.benchmark.batcher;

import net.haesleinhuepf.clij.benchmark.jmh.AbstractBenchmark;

import java.io.IOException;

/**
 * Generate a batch file for comparison of IJ.run and IJ-API calls
 *
 * @author Robert Haase
 *         August 2019
 */
public class ComparisionBatch_IJapi_IJrun extends AbstractBatchGenerator {
    public static void main(String[] args) throws IOException {
        String targetDir = "C:/structure/code/clij-benchmarking/data/benchmarking-jmh/ijrun_ijapi/";
        String batchName = "batch_ijrun_ijapi_comparison_" + System.getenv().get("COMPUTERNAME") + ".bat";
        String additionalParameters = " -p size=1 -p radius=2 ";

        StringBuilder batch = new StringBuilder();

        for (AbstractBenchmark benchmark : benchmarks) {
            String ijrun = getBatchEntry(new String[]{"ijrun"}, targetDir, benchmark, additionalParameters);
            String ijapi = getBatchEntry(new String[]{"ijapi"}, targetDir, benchmark, additionalParameters);

            if (ijapi.length() > 0 && ijrun.length() > 0) {
                batch.append(ijrun.split("\r\n")[0] + "\r\n");
                batch.append(ijrun.split("\r\n")[1] + "\r\n");
                batch.append(ijapi.split("\r\n")[0] + "\r\n");
                batch.append(ijapi.split("\r\n")[1] + "\r\n");
            }
        }
        writeFile(batchName, batch.toString());
    }
}

package net.haesleinhuepf.clij.benchmark.batcher;

import ij.IJ;
import net.haesleinhuepf.clij.benchmark.jmh.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;


public class AbstractBatchGenerator {

    static final String batchTemplate = "java -Xms12G -Xmx12G  -jar target/benchmarks3.jar #METHOD# #ADDITIONAL_PARAMETERS# -rf csv\r\n" +
                            "copy jmh-result.csv #TARGET_DIR#jmh-result_#COMPUTERNAME#_#METHOD_WITHOUT_DOLLAR#.csv\r\n";

    protected static AbstractBenchmark[] benchmarks = {
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
            new FixedThreshold2D(),
            new FixedThreshold3D()
    };


    protected static String getBatchEntryForComparison(String[] methodCandidates, String targetDir, String additionalParameters) {
        StringBuilder batch = new StringBuilder();

        for (AbstractBenchmark benchmark : benchmarks) {
            batch.append(getBatchEntry(methodCandidates, targetDir, benchmark, additionalParameters));

        }
        return batch.toString();
    }

    protected static String getBatchEntry(String[] methodCandidates, String targetDir, AbstractBenchmark benchmark, String additionalParameters) {
        StringBuilder batch = new StringBuilder();

        Method[] methods = Arrays.copyOf(benchmark.getClass().getMethods(), benchmark.getClass().getMethods().length);
        Arrays.sort(methods, Comparator.comparing(Method::getName));
        for (String template : methodCandidates) {
            for (Method method : methods) {
                String methodname = method.getName();
                //System.out.println(methodname);
                if (methodname.compareTo(template) == 0) {
                    batch.append(
                            batchTemplate
                                    .replace("#METHOD#", "." + benchmark.getClass().getSimpleName() + "." + methodname + "$")
                                    .replace("#METHOD_WITHOUT_DOLLAR#", "." + benchmark.getClass().getSimpleName() + "." + methodname)
                                    .replace("#TARGET_DIR#", targetDir)
                                    .replace("#ADDITIONAL_PARAMETERS#", additionalParameters)
                                    .replace("#COMPUTERNAME#", System.getenv().get("COMPUTERNAME"))
                    );
                }
            }
        }
        return batch.toString();
    }

    protected static void writeFile(String filename, String content) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")){
            content = content.replace("/", "\\");
        }
        Files.write(Paths.get(filename), content.getBytes());

        System.out.println("Bye");
        IJ.exit();
    }
}

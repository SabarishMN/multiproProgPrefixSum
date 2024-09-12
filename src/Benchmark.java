public class Benchmark {
    /* The input file we would like to use */
    public static String inputFileName = "in.txt";

    /* Total number of elements to hold in memory at a time */
    public static int [] bufferSizes = {1000}; //, 10000, 100000};

    /* Find the average of numRuns # of runs for each type of prefix sum */
    public static int numRuns = 10;

    public static void main(String [] args) {

        PrefixInterface [] prefixSums = {new SequentialPrefix(), new ParallelPrefix(), new ParallelPrefixInternal(), new BetterParallelPrefix()};
        System.out.printf("Running benchmarks...\n");

        /* Run for various buffer sizes  */
        for (int bufferSize : bufferSizes) {
            System.out.printf("Using buffer size of %d...\n\n", bufferSize);
            float speedup = 1.0f; // Default speedup
            long sequentialTime = 0;
            long averageTime = 1;

            for (PrefixInterface s : prefixSums) {
                System.out.printf("Running %s...\n", s.getClass().getName());
//                long averageTime = 0;
//                float speedup = 0;
                /////////////
                long totalTime = 0;





                /* Run multiple times to calculate average time */
                for (int run = 0; run < numRuns; run++) {
                    long startTime = System.nanoTime();

                    // Run the prefix sum computation
                    s.run(inputFileName, bufferSize);

                    long endTime = System.nanoTime();
                    long elapsedTime = endTime - startTime; // Time in nanoseconds
                    totalTime += elapsedTime;
                }

                /* Calculate average time */
                averageTime = totalTime / numRuns;

                /* Since the first implementation is SequentialPrefix, use it as the baseline for speedup */


                if (s instanceof SequentialPrefix) {
                    sequentialTime = averageTime;
                    }
                    speedup = (float) sequentialTime / averageTime; // Calculate speedup against sequential time


                ////////////////
                /* Print results */
                System.out.printf("Average time: %o\n", averageTime);
                System.out.printf("Speedup: %.2f\n", speedup);
                System.out.println("----------------------------------------------------------\n");
            }


                //////////////

                }
        }
    }

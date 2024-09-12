import java.io.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class BetterParallelPrefix implements PrefixInterface {

    private static final int THRESHOLD = 1000; // Chunk size for breaking tasks

    @Override
    public void run(String filename, int bufferSize) {
        try {
            // Read the input file
            long[] input = readInput(filename, bufferSize);
            long[] output = new long[input.length];

            // Perform parallel prefix sum using ForkJoinPool
            ForkJoinPool pool = new ForkJoinPool();
            pool.invoke(new ParallelPrefixTask(input, output, 0, input.length));

            // Write the output to "out5.txt" instead of "out.txt"
            writeOutput("out5.txt", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new BetterParallelPrefix().run("in.txt", 1000);
    }

    // Reads the input file in chunks and returns an array of long numbers.
    private long[] readInput(String filename, int bufferSize) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            long[] numbers = new long[bufferSize];
            int index = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                numbers[index++] = Long.parseLong(line);
                // If the buffer is full, expand it dynamically
                if (index >= numbers.length) {
                    numbers = expandArray(numbers);
                }
            }
            return numbers;
        }
    }

    // Writes the output array to a file.
    private void writeOutput(String filename, long[] output) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (long value : output) {
                writer.write(Long.toString(value));
                writer.newLine();
            }
        }
    }

    // Expands the input array when more memory is required.
    private long[] expandArray(long[] array) {
        long[] newArray = new long[array.length * 2];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    // ForkJoin RecursiveTask for parallel prefix sum
    static class ParallelPrefixTask extends RecursiveTask<Void> {
        private final long[] input;
        private final long[] output;
        private final int start;
        private final int end;

        public ParallelPrefixTask(long[] input, long[] output, int start, int end) {
            this.input = input;
            this.output = output;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Void compute() {
            if (end - start <= THRESHOLD) {
                // Sequential computation when the task size is below the threshold
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += input[i];
                    output[i] = sum;
                }
            } else {
                // Recursive division of the task
                int mid = (start + end) / 2;
                ParallelPrefixTask leftTask = new ParallelPrefixTask(input, output, start, mid);
                ParallelPrefixTask rightTask = new ParallelPrefixTask(input, output, mid, end);

                leftTask.fork(); // Execute left task asynchronously
                rightTask.compute(); // Execute right task synchronously
                leftTask.join(); // Wait for left task to complete
            }
            return null;
        }
    }
}

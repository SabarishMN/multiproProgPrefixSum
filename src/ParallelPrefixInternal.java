import java.io.*;
import java.util.Arrays;
public class ParallelPrefixInternal implements PrefixInterface{
    public void run(String filename, int bufferSize) {
            try {
                // Step 1: Read the data from the file into memory
                long[] data = loadDataFromFile(filename, bufferSize);

                // Step 2: Use J
                // ava's built-in parallelPrefix function to compute the prefix sum
                Arrays.parallelPrefix(data, Long::sum);

                // Step 3: Write the results to the output file
                writeResultToFile("out4.txt", data);

            } catch (IOException e) {
                e.printStackTrace();
            }


    }
    // Function to load data from the file into an array
    private long[] loadDataFromFile(String filename, int bufferSize) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        return reader.lines().mapToLong(Long::parseLong).toArray(); // Reads file and converts lines to long array
    }
    // Function to write the result to an output file
    private void writeResultToFile(String outputFileName, long[] data) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
        for (long value : data) {
            writer.write(value + "\n");
        }
        writer.close();
    }

    public static void main(String[] args) {
        new ParallelPrefixInternal().run("in.txt", 1000);
    }
}
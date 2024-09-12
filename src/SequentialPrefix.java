import java.io.*;

public class SequentialPrefix implements PrefixInterface{
    public void run(String filename, int bufferSize) {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(filename), bufferSize);
            writer = new BufferedWriter(new FileWriter("out.txt"));

            String line = null;
            int prefixSum = 0;

            while((line = reader.readLine()) != null)
            {
                int number = Integer.parseInt(line.trim());
                prefixSum += number;

                writer.write(prefixSum + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error reading file" + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                System.out.println("Error reading file" + e.getMessage());
            }
        }

    }
    public static void main(String[] args) {
        new SequentialPrefix().run("in.txt", 1000);
    }
}

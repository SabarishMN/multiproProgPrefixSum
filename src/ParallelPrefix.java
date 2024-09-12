import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelPrefix implements PrefixInterface{
    public class Node {
        long value;
        Node right;
        Node left;
        public Node(long value) {
            this.value = value;
            this.right = null;
            this.left = null;
        }
        public Node(long value, Node left, Node right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }

    }
    public void run(String filename, int bufferSize) {
        try {
            long[] input = readInput(filename, bufferSize);
//            long[] output = new long[input.length];
            List<Long> output = new ArrayList<>();
            int maxThreads = 8; //Runtime.getRuntime().availableProcessors();
            Node[] rootArray = new Node[maxThreads];

            long batchSize = input.length/maxThreads;

//            System.out.println("Max threads: " + maxThreads);
//            Thread[] threads = new Thread[maxThreads];
            ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
            for (long i = 0; i < maxThreads; i++) {
                final long threadId = i;
                final long finalI = i;

                executor.submit(() -> {
                    System.out.println("Task running on thread " + threadId);
                    // Perform your task here
                    constructTree(Arrays.copyOfRange(input, (int) (finalI*batchSize), (int) ((finalI+1)*batchSize - 1)), finalI, rootArray);
                });
            }

            executor.shutdown();

            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                System.err.println("Interrupted while waiting for tasks to finish.");
            }
            Node masterRoot = constructRootTree(rootArray);
            System.out.println("Depth : "+ maxDepth(masterRoot));

            System.out.println("Root : " + masterRoot.value);
            // top down

            masterRoot.value = 0;
            System.out.println(masterRoot.left.value);
            System.out.println(masterRoot.right.value);
            fromLeftTraversal(masterRoot);

            long pos = 0;
            traverseLeafNodes(masterRoot, output, input, pos);
//            System.out.println("output length" + output.size());

            writeOutput("out3.txt", output);

            // assign each thread a task
               // task : create a tree bottom up approach with the array range and store in the common variable
            // wait for all the threads to finish
            // common variable looping and finalize the root

            // traverse top down

        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static int maxDepth(Node node) {
        if (node == null)
            return 0;

        // compute the depth of left and right
        // subtrees
        int lDepth = maxDepth(node.left);
        int rDepth = maxDepth(node.right);

        // use the larger one
        return Math.max(lDepth, rDepth) + 1;
    }

    public void fromLeftTraversal(Node node) {
        if (node.left == null || node.right == null) {
            return;
        }

            long fromLeft = node.left.value;
            node.left.value = node.value;
            node.right.value = fromLeft + node.left.value;
            fromLeftTraversal(node.left);
            fromLeftTraversal(node.right);

    }

    public void traverseLeafNodes(Node node, List<Long> output, long[] input, long pos) {
        if (node == null) {
            return;
        }

        if (node.left == null && node.right == null) {
            output.add(node.value + input[(int)pos]);
            pos++;
        }

        traverseLeafNodes(node.left, output, input, pos);
        traverseLeafNodes(node.right, output, input, pos);
    }

    private Node constructRootTree(Node[] rootArray) {
        if (rootArray.length == 0 || rootArray == null) {
            System.out.println("Root Tree is empty");
            return null;
        }

        Node root = constructRootSumTreeHelper(rootArray, 0, 7);
        return root;
    }

    private Node constructRootSumTreeHelper(Node[] rootArray, int start, int end) {
        if (start > end ) return null;
        long mid = (start + end) / 2;
        Node node = rootArray[(int)mid];

        node.left = constructRootSumTreeHelper(rootArray, start, (int)mid - 1);
        node.right = constructRootSumTreeHelper(rootArray, (int)mid + 1, end);

        if (node.left != null) {
            node.value += node.left.value;
        }

        if (node.right != null) {
            node.value += node.right.value;
        }

        return node;
    }

    private void constructTree(long[] input, long threadId, Node[] rootArray) {
        if (input == null || input.length == 0) {
            return;
        }
//        int start = 0;
//        int end = input.length - 1;
//        int mid = (start + end) / 2;
//        Node node = new Node(input[mid]);
//        rootArray[threadId] = node;

        rootArray[(int)threadId] = constructSumTreeHelper(input, 0, input.length - 1);
    }

    private Node constructSumTreeHelper(long[] input, long start, long end) {
        if (start > end ) return null;
        long mid = (start + end) / 2;
        Node node = new Node(input[(int)mid]);

        node.left = constructSumTreeHelper(input, start, mid - 1);
        node.right = constructSumTreeHelper(input, mid + 1, end);

        if (node.left != null) {
            node.value += node.left.value;
        }

        if (node.right != null) {
            node.value += node.right.value;
        }

        return node;

    }

    private long[] readInput(String filename, int bufferSize) throws IOException {
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            long[] numbers = new long[bufferSize];
            int index = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                numbers[index++] = Long.parseLong(line);

                if (index >= numbers.length) {
                    numbers = expandArray(numbers);
                }
            }
            return numbers;
        }
    }

    private long[] expandArray(long[] array) {
        long[] newArray = new long[array.length * 2];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    // Writes the output array to a file.
    private void writeOutput(String filename, List<Long> output) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (long value : output) {
                writer.write(Long.toString(value));
                writer.newLine();
            }
        }
    }


    public static void main(String[] args) {
        new ParallelPrefix().run("in.txt", 1000);
    }
}

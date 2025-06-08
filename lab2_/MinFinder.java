import java.util.Random;

class MinFinder extends Thread {
    private int[] arr;
    private int start, end;
    private static int globalMin = Integer.MAX_VALUE;
    private static int globalMinIndex = -1;
    private static final Object lock = new Object();
    private int threadId;

    public MinFinder(int threadId, int[] arr, int start, int end) {
        this.threadId = threadId;
        this.arr = arr;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        int localMin = Integer.MAX_VALUE;
        int localMinIndex = -1;
        for (int i = start; i <= end; i++) {
            if (arr[i] < localMin) {
                localMin = arr[i];
                localMinIndex = i;
            }
        }

        synchronized (lock) {
            if (localMin < globalMin) {
                globalMin = localMin;
                globalMinIndex = localMinIndex;
            }
        }
    }

    public static int getGlobalMin() {
        return globalMin;
    }

    public static int getGlobalMinIndex() {
        return globalMinIndex;
    }

    public static void main(String[] args) throws InterruptedException {
        int arraySize = 10000;
        int numThreads = 4;
        int[] array = new int[arraySize];
        Random rnd = new Random();

        for (int i = 0; i < arraySize; i++) {
            array[i] = rnd.nextInt(100000);
        }

        int negIndex = rnd.nextInt(arraySize);
        array[negIndex] = -rnd.nextInt(1000);

        MinFinder[] threads = new MinFinder[numThreads];
        int chunkSize = arraySize / numThreads;

        for (int i = 0; i < numThreads; i++) {
            int start = i * chunkSize;
            int end = (i == numThreads - 1) ? arraySize - 1 : (start + chunkSize - 1);
            threads[i] = new MinFinder(i + 1, array, start, end);
            threads[i].start();
        }

        for (MinFinder thread : threads) {
            thread.join();
        }

        System.out.println("Global minimum: " + MinFinder.getGlobalMin());
        System.out.println("Index of minimum: " + MinFinder.getGlobalMinIndex());
        System.out.println("Negative element index: " + negIndex);
    }
}

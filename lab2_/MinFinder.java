import java.util.Random;

class MinFinder extends Thread {
    private int[] arr;
    private int start, end;
    private static int globalMin = Integer.MAX_VALUE;
    private static int globalMinIndex = -1;
    private static final Object minLock = new Object();
    private static final Object syncLock = new Object();
    private static int completedThreads = 0;
    private static int totalThreads;

    public MinFinder(int[] arr, int start, int end) {
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

        synchronized (minLock) {
            if (localMin < globalMin) {
                globalMin = localMin;
                globalMinIndex = localMinIndex;
            }
        }

        synchronized (syncLock) {
            completedThreads++;
            if (completedThreads == totalThreads) {
                syncLock.notify(); 
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
        totalThreads = 4;
        int[] array = new int[arraySize];
        Random rnd = new Random();

        for (int i = 0; i < arraySize; i++) {
            array[i] = rnd.nextInt(100000);
        }

        int negIndex = rnd.nextInt(arraySize);
        array[negIndex] = -rnd.nextInt(1000);

        MinFinder[] threads = new MinFinder[totalThreads];
        int chunkSize = arraySize / totalThreads;

        for (int i = 0; i < totalThreads; i++) {
            int start = i * chunkSize;
            int end = (i == totalThreads - 1) ? arraySize - 1 : (start + chunkSize - 1);
            threads[i] = new MinFinder(array, start, end);
            threads[i].start();
        }

        synchronized (syncLock) {
            while (completedThreads < totalThreads) {
                syncLock.wait(); 
            }
        }

        System.out.println("Global minimum: " + getGlobalMin());
        System.out.println("Index of minimum: " + getGlobalMinIndex());
        System.out.println("Negative element index: " + negIndex);
    }
}

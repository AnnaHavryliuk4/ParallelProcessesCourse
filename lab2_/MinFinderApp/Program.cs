using System;
using System.Threading;

class MinFinder
{
    private int[] arr;
    private int start, end;
    private int threadId;
    private static int globalMin = int.MaxValue;
    private static int globalMinIndex = -1;
    private static object lockObj = new object();

    public MinFinder(int threadId, int[] arr, int start, int end)
    {
        this.threadId = threadId;
        this.arr = arr;
        this.start = start;
        this.end = end;
    }

    public void FindMin()
    {
        int localMin = int.MaxValue;
        int localMinIndex = -1;

        for (int i = start; i <= end; i++)
        {
            if (arr[i] < localMin)
            {
                localMin = arr[i];
                localMinIndex = i;
            }
        }

        lock (lockObj)
        {
            if (localMin < globalMin)
            {
                globalMin = localMin;
                globalMinIndex = localMinIndex;
            }
        }
    }

    public static int GetGlobalMin() => globalMin;
    public static int GetGlobalMinIndex() => globalMinIndex;
}

class Program
{
    static void Main()
    {
        int arraySize = 10000;
        int numThreads = 4;
        int[] array = new int[arraySize];
        Random rnd = new Random();

        for (int i = 0; i < arraySize; i++)
            array[i] = rnd.Next(100000);

        int negIndex = rnd.Next(arraySize);
        array[negIndex] = -rnd.Next(1000);

        MinFinder[] finders = new MinFinder[numThreads];
        Thread[] threads = new Thread[numThreads];
        int chunkSize = arraySize / numThreads;

        for (int i = 0; i < numThreads; i++)
        {
            int start = i * chunkSize;
            int end = (i == numThreads - 1) ? arraySize - 1 : (start + chunkSize - 1);
            finders[i] = new MinFinder(i + 1, array, start, end);
            threads[i] = new Thread(finders[i].FindMin);
            threads[i].Start();
        }

        for (int i = 0; i < numThreads; i++)
            threads[i].Join();

        Console.WriteLine("Global minimum: " + MinFinder.GetGlobalMin());
        Console.WriteLine("Index of minimum: " + MinFinder.GetGlobalMinIndex());
        Console.WriteLine("Negative element index: " + negIndex);
    }
}

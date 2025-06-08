using System;
using System.Threading;

class MinFinder
{
    private int[] arr;
    private int start, end;
    private static int globalMin = int.MaxValue;
    private static int globalMinIndex = -1;
    private static readonly object minLock = new object();
    private static readonly object syncLock = new object();
    private static int completedThreads = 0;
    private static int totalThreads;

    public MinFinder(int[] arr, int start, int end)
    {
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

        lock (minLock)
        {
            if (localMin < globalMin)
            {
                globalMin = localMin;
                globalMinIndex = localMinIndex;
            }
        }

        lock (syncLock)
        {
            completedThreads++;
            if (completedThreads == totalThreads)
            {
                Monitor.Pulse(syncLock);
            }
        }
    }

    public static int GetGlobalMin() => globalMin;
    public static int GetGlobalMinIndex() => globalMinIndex;

    public static void WaitForCompletion()
    {
        lock (syncLock)
        {
            while (completedThreads < totalThreads)
            {
                Monitor.Wait(syncLock);
            }
        }
    }

    public static void SetTotalThreads(int count)
    {
        totalThreads = count;
    }
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

        MinFinder.SetTotalThreads(numThreads);

        Thread[] threads = new Thread[numThreads];
        int chunkSize = arraySize / numThreads;

        for (int i = 0; i < numThreads; i++)
        {
            int start = i * chunkSize;
            int end = (i == numThreads - 1) ? arraySize - 1 : (start + chunkSize - 1);
            MinFinder finder = new MinFinder(array, start, end);
            threads[i] = new Thread(finder.FindMin);
            threads[i].Start();
        }

        MinFinder.WaitForCompletion();

        Console.WriteLine("Global minimum: " + MinFinder.GetGlobalMin());
        Console.WriteLine("Index of minimum: " + MinFinder.GetGlobalMinIndex());
        Console.WriteLine("Negative element index: " + negIndex);
    }
}

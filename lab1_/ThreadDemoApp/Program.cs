using System;
using System.Threading;

class SumThread {
    private int id, step, count = 0;
    private long sum = 0;
    private bool running = true;

    public SumThread(int id, int step) {
        this.id = id; this.step = step;
    }

    public void Run() {
        int i = 0;
        while (running) {
            sum += i;
            count++;
            i += step;
            Thread.Sleep(10);
        }
        Console.WriteLine($"Thread #{id} | Sum: {sum} | Count: {count}");
    }

    public void Stop() => running = false;
}

class ThreadDemo {
    static void Main() {
        int numThreads = 3;
        SumThread[] workers = new SumThread[numThreads];
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            int step = i + 1;
            workers[i] = new SumThread(i + 1, step);
            threads[i] = new Thread(workers[i].Run);
            threads[i].Start();
        }

        Thread.Sleep(2000);  

        for (int i = 0; i < numThreads; i++) {
            workers[i].Stop();
        }

        for (int i = 0; i < numThreads; i++) {
            threads[i].Join();
        }
    }
}

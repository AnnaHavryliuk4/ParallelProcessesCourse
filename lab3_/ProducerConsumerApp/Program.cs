using System;
using System.Collections.Generic;
using System.Threading;

class ProducerConsumer {
    const int BUFFER_CAPACITY = 5;
    const int TOTAL_PRODUCTION = 20;

    private readonly Queue<int> buffer = new Queue<int>();
    private readonly SemaphoreSlim empty = new SemaphoreSlim(BUFFER_CAPACITY);
    private readonly SemaphoreSlim full = new SemaphoreSlim(0);
    private readonly object mutex = new object();

    class Producer {
        private readonly ProducerConsumer pc;
        private readonly int toProduce;
        public Producer(ProducerConsumer pc, int toProduce) {
            this.pc = pc; this.toProduce = toProduce;
        }
        public void Run() {
            for (int i = 0; i < toProduce; i++) {
                pc.empty.Wait();
                lock (pc.mutex) {
                    pc.buffer.Enqueue(i);
                    Console.WriteLine($"{Thread.CurrentThread.Name} produced {i}");
                }
                pc.full.Release();
            }
        }
    }

    class Consumer {
        private readonly ProducerConsumer pc;
        private readonly int toConsume;
        public Consumer(ProducerConsumer pc, int toConsume) {
            this.pc = pc; this.toConsume = toConsume;
        }
        public void Run() {
            for (int i = 0; i < toConsume; i++) {
                pc.full.Wait();
                int item;
                lock (pc.mutex) {
                    item = pc.buffer.Dequeue();
                    Console.WriteLine($"{Thread.CurrentThread.Name} consumed {item}");
                }
                pc.empty.Release();
            }
        }
    }

    public void Execute(int numProducers, int numConsumers) {
        int perP = TOTAL_PRODUCTION / numProducers;
        int perC = TOTAL_PRODUCTION / numConsumers;

        Thread[] pThreads = new Thread[numProducers];
        Thread[] cThreads = new Thread[numConsumers];

        for (int i = 0; i < numProducers; i++) {
            var prod = new Producer(this, perP);
            pThreads[i] = new Thread(prod.Run) { Name = $"P{i+1}" };
        }
        for (int i = 0; i < numConsumers; i++) {
            var cons = new Consumer(this, perC);
            cThreads[i] = new Thread(cons.Run) { Name = $"C{i+1}" };
        }

        foreach (var t in pThreads) t.Start();
        foreach (var t in cThreads) t.Start();

        foreach (var t in pThreads) t.Join();
        foreach (var t in cThreads) t.Join();
    }

    static void Main() {
        var pc = new ProducerConsumer();
        pc.Execute(2, 3);
    }
}

using System;
using System.Threading;

class SumThread {
    private int id, step, count = 0;
    private long sum = 0;
    private volatile bool running = true;

    public SumThread(int id, int step) {
        this.id = id;
        this.step = step;
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
        Console.Write("Введіть бажану кількість потоків: ");
        int numThreads = 0;
        string input = Console.ReadLine();

        if (!int.TryParse(input, out numThreads) || numThreads <= 0) {
            Console.WriteLine("Некоректний ввід. Будь ласка, введіть ціле число більше нуля. Використано значення за замовчуванням: 3");
            numThreads = 3;
        }

        SumThread[] workers = new SumThread[numThreads];
        Thread[] threads = new Thread[numThreads];

        Console.WriteLine($"Запускаємо {numThreads} потоків...");
        for (int i = 0; i < numThreads; i++) {
            int step = i + 1;
            workers[i] = new SumThread(i + 1, step);
            threads[i] = new Thread(workers[i].Run);
            threads[i].Start();
        }

        Thread.Sleep(2000);

        Console.WriteLine("Надсилаємо сигнал на зупинку потоків...");
        for (int i = 0; i < numThreads; i++) {
            workers[i].Stop();
        }

        Console.WriteLine("Чекаємо завершення потоків...");
        for (int i = 0; i < numThreads; i++) {
            threads[i].Join();
        }
        Console.WriteLine("Всі потоки завершили роботу.");

        Console.ReadKey();
    }
}
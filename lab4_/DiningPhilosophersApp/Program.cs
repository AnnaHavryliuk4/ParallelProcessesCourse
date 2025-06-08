using System;
using System.Threading;

class DiningPhilosophers {
    const int NUM_PHILOSOPHERS = 5;
    const int MEALS = 5;
    static SemaphoreSlim[] forks = new SemaphoreSlim[NUM_PHILOSOPHERS];

    static void Main() {
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new SemaphoreSlim(1, 1);
        }

        Thread[] philosophers = new Thread[NUM_PHILOSOPHERS];

        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            int id = i;
            SemaphoreSlim left = forks[i];
            SemaphoreSlim right = forks[(i + 1) % NUM_PHILOSOPHERS];

            philosophers[i] = new Thread(() => Philosopher(id, left, right));
            philosophers[i].Name = $"Philosopher-{id}";
            philosophers[i].Start();
        }

        foreach (var p in philosophers) p.Join();
        Console.WriteLine("Dinner is over.");
    }

    static void Philosopher(int id, SemaphoreSlim left, SemaphoreSlim right) {
        Random rnd = new Random();
        for (int i = 0; i < MEALS; i++) {
            Console.WriteLine($"Philosopher {id} is thinking.");
            Thread.Sleep(rnd.Next(300, 600));

            if (id == NUM_PHILOSOPHERS - 1) {
                right.Wait();
                left.Wait();
            } else {
                left.Wait();
                right.Wait();
            }

            Console.WriteLine($"Philosopher {id} is eating (meal {i + 1}).");
            Thread.Sleep(rnd.Next(300, 600));

            left.Release();
            right.Release();
        }
    }
}

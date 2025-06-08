import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class ProducerConsumer {
    private static final int BUFFER_CAPACITY = 5;
    private static final int TOTAL_PRODUCTION = 20;

    private final Queue<Integer> buffer = new LinkedList<>();
    private final Semaphore empty = new Semaphore(BUFFER_CAPACITY);
    private final Semaphore full = new Semaphore(0);
    private final Semaphore mutex = new Semaphore(1);

    class Producer extends Thread {
        private final int toProduce;
        private final int id;

        public Producer(int toProduce, int id) {
            this.toProduce = toProduce;
            this.id = id;
        }

        public void run() {
            try {
                for (int i = 0; i < toProduce; i++) {
                    empty.acquire();
                    mutex.acquire();
                    int item = id * 100 + i;
                    buffer.add(item);
                    System.out.printf("Producer %d produced %d%n", id, item);
                    mutex.release();
                    full.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    class Consumer extends Thread {
        private final int toConsume;
        private final int id;

        public Consumer(int toConsume, int id) {
            this.toConsume = toConsume;
            this.id = id;
        }

        public void run() {
            try {
                for (int i = 0; i < toConsume; i++) {
                    full.acquire();
                    mutex.acquire();
                    int item = buffer.poll();
                    System.out.printf("Consumer %d consumed %d%n", id, item);
                    mutex.release();
                    empty.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void execute(int numProducers, int numConsumers) throws InterruptedException {
        Thread[] producers = new Thread[numProducers];
        Thread[] consumers = new Thread[numConsumers];

        int totalProduced = 0;
        int totalConsumed = 0;

        for (int i = 0; i < numProducers; i++) {
            int toProduce = TOTAL_PRODUCTION / numProducers + (i < TOTAL_PRODUCTION % numProducers ? 1 : 0);
            totalProduced += toProduce;
            producers[i] = new Producer(toProduce, i + 1);
        }

        for (int i = 0; i < numConsumers; i++) {
            int toConsume = TOTAL_PRODUCTION / numConsumers + (i < TOTAL_PRODUCTION % numConsumers ? 1 : 0);
            totalConsumed += toConsume;
            consumers[i] = new Consumer(toConsume, i + 1);
        }

        System.out.printf("Total to produce: %d, total to consume: %d%n", totalProduced, totalConsumed);

        for (Thread p : producers) p.start();
        for (Thread c : consumers) c.start();

        for (Thread p : producers) p.join();
        for (Thread c : consumers) c.join();
    }

    public static void main(String[] args) throws InterruptedException {
        new ProducerConsumer().execute(2, 3);
    }
}

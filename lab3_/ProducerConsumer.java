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
        public Producer(int toProduce) { this.toProduce = toProduce; }

        public void run() {
            try {
                for (int i = 0; i < toProduce; i++) {
                    empty.acquire();
                    mutex.acquire();
                    buffer.add(i);
                    System.out.printf("%s produced %d%n", getName(), i);
                    mutex.release();
                    full.release();
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    class Consumer extends Thread {
        private final int toConsume;
        public Consumer(int toConsume) { this.toConsume = toConsume; }

        public void run() {
            try {
                for (int i = 0; i < toConsume; i++) {
                    full.acquire();
                    mutex.acquire();
                    int item = buffer.poll();
                    System.out.printf("%s consumed %d%n", getName(), item);
                    mutex.release();
                    empty.release();
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    public void execute(int numProducers, int numConsumers) throws InterruptedException {
        int perProducer = TOTAL_PRODUCTION / numProducers;
        int perConsumer = TOTAL_PRODUCTION / numConsumers;

        Thread[] producers = new Thread[numProducers];
        Thread[] consumers = new Thread[numConsumers];

        for (int i = 0; i < numProducers; i++)
            producers[i] = new Producer(perProducer);
        for (int i = 0; i < numConsumers; i++)
            consumers[i] = new Consumer(perConsumer);

        for (Thread t : producers) t.start();
        for (Thread t : consumers) t.start();

        for (Thread t : producers) t.join();
        for (Thread t : consumers) t.join();
    }

    public static void main(String[] args) throws InterruptedException {
        new ProducerConsumer().execute(2, 3);
    }
}

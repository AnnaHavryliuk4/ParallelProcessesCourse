import java.util.concurrent.Semaphore;

public class DiningPhilosophers {
    private static final int NUM_PHILOSOPHERS = 5;
    private static final int MEALS = 5;

    static class Philosopher extends Thread {
        private final int id;
        private final Semaphore leftFork;
        private final Semaphore rightFork;

        public Philosopher(int id, Semaphore left, Semaphore right) {
            this.id = id;
            this.leftFork = left;
            this.rightFork = right;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < MEALS; i++) {
                    think();
                    if (id == NUM_PHILOSOPHERS - 1) {
                        rightFork.acquire();
                        leftFork.acquire();
                    } else {
                        leftFork.acquire();
                        rightFork.acquire();
                    }

                    eat(i);

                    leftFork.release();
                    rightFork.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void think() throws InterruptedException {
            System.out.println("Philosopher " + id + " is thinking.");
            Thread.sleep((int) (Math.random() * 500));
        }

       private void eat(int meal) throws InterruptedException {
    System.out.println("Philosopher " + id + " is eating (meal " + (meal + 1) + ").");
    Thread.sleep((int) (Math.random() * 500));
}

    }

    public static void main(String[] args) {
        Semaphore[] forks = new Semaphore[NUM_PHILOSOPHERS];
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new Semaphore(1);
        }

        Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            Semaphore left = forks[i];
            Semaphore right = forks[(i + 1) % NUM_PHILOSOPHERS];
            philosophers[i] = new Philosopher(i, left, right);
            philosophers[i].start();
        }

        for (Philosopher p : philosophers) {
            try {
                p.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Dinner is over.");
    }
}

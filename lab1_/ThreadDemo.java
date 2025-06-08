class SumThread extends Thread {
    private int step;
    private boolean running = true;
    private int id;
    private long sum = 0;
    private int count = 0;

    public SumThread(int id, int step) {
        this.id = id;
        this.step = step;
    }

    public void stopThread() {
        running = false;
    }

    public void run() {
        int i = 0;
        while (running) {
            sum += i;
            count++;
            i += step;
            try { Thread.sleep(10); } catch (InterruptedException e) {}
        }
        System.out.println("Thread #" + id + " | Sum: " + sum + " | Count: " + count);
    }
}

public class ThreadDemo {
   public static void main(String[] args) throws InterruptedException {
    int numThreads = 3;
    SumThread[] threads = new SumThread[numThreads];

    for (int i = 0; i < numThreads; i++) {
        threads[i] = new SumThread(i + 1, i + 1);
        threads[i].start();
    }

    Thread.sleep(2000);  // Зачекати 2 секунди

    for (int i = 0; i < numThreads; i++) {
        threads[i].stopThread();  // Зупинити всі потоки одночасно
    }

    for (int i = 0; i < numThreads; i++) {
        threads[i].join();
    }
}

}

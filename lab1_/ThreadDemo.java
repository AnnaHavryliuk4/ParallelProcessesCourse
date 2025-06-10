import java.util.Scanner;

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
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread #" + id + " was interrupted.");
                running = false;
            }
        }
        System.out.println("Thread #" + id + " | Sum: " + sum + " | Count: " + count);
    }
}

public class ThreadDemo {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введіть бажану кількість потоків: ");
        int numThreads = 0;
        try {
            numThreads = scanner.nextInt();
            if (numThreads <= 0) {
                System.out.println("Кількість потоків повинна бути більше нуля. Використано значення за замовчуванням: 3");
                numThreads = 3;
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("Некоректний ввід. Будь ласка, введіть ціле число. Використано значення за замовчуванням: 3");
            numThreads = 3;
        } finally {
            scanner.close();
        }

        SumThread[] threads = new SumThread[numThreads];

        System.out.println("Запускаємо " + numThreads + " потоків...");
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new SumThread(i + 1, i + 1);
            threads[i].start();
        }

        Thread.sleep(2000);

        System.out.println("Надсилаємо сигнал на зупинку потоків...");
        for (int i = 0; i < numThreads; i++) {
            threads[i].stopThread();
        }

        System.out.println("Чекаємо завершення потоків...");
        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }
        System.out.println("Всі потоки завершили роботу.");
    }
}
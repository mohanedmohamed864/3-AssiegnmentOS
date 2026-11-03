
public class Semaphore {

    private int value;

    public Semaphore(int permits) {
        value = permits;
    }

    public synchronized void acquire() throws InterruptedException {

        while (value == 0) {
            wait();
        }

        value--;
    }

    public synchronized void release() {

        value++;

        notifyAll();
    }

    public synchronized boolean tryAcquire(long timeoutMillis)
            throws InterruptedException {

        long startTime = System.currentTimeMillis();

        while (value == 0) {

            long elapsedTime =
                    System.currentTimeMillis() - startTime;

            long remainingTime = timeoutMillis - elapsedTime;

            if (remainingTime <= 0) {
                return false;
            }

            wait(remainingTime);
        }

        value--;

        return true;
    }
}

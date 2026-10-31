// TEMPORARY STUB so your code compiles. Teammate 2 replaces this file with the
// real implementation. Agreed API:
//   Semaphore(int permits)
//   void acquire() throws InterruptedException
//   void release()
//   boolean tryAcquire(long timeoutMillis) throws InterruptedException
public class Semaphore {
    private int value;

    public Semaphore(int permits) { this.value = permits; }

    public synchronized void acquire() throws InterruptedException {
        while (value == 0) wait();
        value--;
    }

    public synchronized void release() {
        value++;
        notify();
    }

    public synchronized boolean tryAcquire(long timeoutMillis) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (value == 0) {
            long left = deadline - System.currentTimeMillis();
            if (left <= 0) return false;
            wait(left);
        }
        value--;
        return true;
    }
}
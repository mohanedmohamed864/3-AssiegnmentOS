import java.util.concurrent.atomic.AtomicInteger;

public class Patient extends Thread {

    private static final AtomicInteger treatedCount = new AtomicInteger(0);

    private final int id;
    private final WaitingRoom waitingRoom;
    private final Semaphore treated = new Semaphore(0);

    public Patient(int id, WaitingRoom waitingRoom) {
        this.id = id;
        this.waitingRoom = waitingRoom;
    }

    public int getId2() {
        return id;
    }

    public String getPatientName() {
        return "P" + id;
    }

    public void finishTreatment() {
        treatedCount.incrementAndGet();
        treated.release();
    }

    public static int getTreatedCount() {
        return treatedCount.get();
    }

    @Override
    public void run() {
        try {
            Output.print("ARRIVAL", getPatientName() + " has arrived.");

            waitingRoom.put(this);

            treated.acquire();

            Output.print("DEPARTURE",
                    getPatientName() + " left the hospital.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
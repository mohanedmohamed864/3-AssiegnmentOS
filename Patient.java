public class Patient extends Thread {

    private static int treatedCount = 0;

    private final int id;
    private final WaitingRoom waitingRoom;
    private final Semaphore treated = new Semaphore(0);

    public Patient(int id, WaitingRoom waitingRoom) {
        this.id = id;
        this.waitingRoom = waitingRoom;
    }

    public int getPatientId() {
        return id;
    }

    public String getPatientName() {
        return "P" + id;
    }

    public static synchronized void addTreated() {
        treatedCount++;
    }

    public static synchronized int getTreatedCount() {
        return treatedCount;
    }

    public void finishTreatment() {
        addTreated();
        treated.release();
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
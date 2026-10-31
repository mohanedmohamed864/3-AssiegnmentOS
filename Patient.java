public class Patient extends Thread {
    private final int id;
    private final WaitingRoom waitingRoom;
    // Released by the doctor when treatment finishes; the patient thread
    // blocks on it so "P# left." is printed after treatment.
    private final Semaphore treated = new Semaphore(0);

    public Patient(int id, WaitingRoom waitingRoom) {
        this.id = id;
        this.waitingRoom = waitingRoom;
    }

    public int getId2() { return id; }

    public String getPatientName() { return "P" + id; }

    // Called by the doctor once treatment is done.
    public void finishTreatment() {
        treated.release();
    }

    @Override
    public void run() {
        try {
            Hospital.log(getPatientName() + " arrived.");
            // put() prints "full / waiting outside" if needed, blocks until
            // a seat is free, then prints "entered the waiting room".
            waitingRoom.put(this);

            treated.acquire(); // wait until a doctor finishes treating us
            Hospital.log(getPatientName() + " left.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
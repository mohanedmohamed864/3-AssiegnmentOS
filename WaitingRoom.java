import java.util.LinkedList;
import java.util.Queue;

public class WaitingRoom {

    private final Queue<Patient> queue = new LinkedList<>();
    private final Semaphore emptySeats;
    private final Semaphore fullSeats;
    private final Semaphore mutex = new Semaphore(1);

    public WaitingRoom(int capacity) {
        emptySeats = new Semaphore(capacity);
        fullSeats = new Semaphore(0);
    }

    public void put(Patient p) throws InterruptedException {

        boolean seatAcquired = false;
        boolean mutexAcquired = false;
        boolean patientAdded = false;

        try {

            seatAcquired = emptySeats.tryAcquire(0);

            if (!seatAcquired) {

                Output.print("WAITING", "Waiting room is full.");

                Output.print("OUTSIDE",
                        p.getPatientName()
                                + " is waiting outside for an available seat.");

                emptySeats.acquire();
                seatAcquired = true;
            }

            
            mutex.acquire();
            mutexAcquired = true;

            queue.add(p);
            patientAdded = true;

            Output.print("ADMITTED",
                    p.getPatientName() + " entered the waiting room.");

        } finally {

            if (mutexAcquired) {
                mutex.release();
            }

            if (patientAdded) {
                fullSeats.release();

            } else if (seatAcquired) {
                emptySeats.release();
            }
        }
    }

    public Patient take() throws InterruptedException {

        fullSeats.acquire();

        boolean mutexAcquired = false;

        try {
            mutex.acquire();
            mutexAcquired = true;

            Patient p = queue.poll();

            if (p == null) {
                fullSeats.release();
                return null;
            }

            emptySeats.release();

            return p;

        } catch (InterruptedException e) {

            if (!mutexAcquired) {
                fullSeats.release();
            }

            throw e;

        } finally {

            if (mutexAcquired) {
                mutex.release();
            }
        }
    }
}
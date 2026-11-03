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
            // Try to get an available seat immediately.
            seatAcquired = emptySeats.tryAcquire(0);

            // If the waiting room is full, wait for an empty seat.
            if (!seatAcquired) {

                Output.print("WAITING", "Waiting room is full.");

                Output.print("OUTSIDE",
                        p.getPatientName()
                                + " is waiting outside for an available seat.");

                emptySeats.acquire();
                seatAcquired = true;
            }

            // Lock the queue before modifying it.
            mutex.acquire();
            mutexAcquired = true;

            queue.add(p);
            patientAdded = true;

            Output.print("ADMITTED",
                    p.getPatientName() + " entered the waiting room.");

        } finally {

            // Always release the mutex if it was acquired.
            if (mutexAcquired) {
                mutex.release();
            }

            if (patientAdded) {
                fullSeats.release();

            } else if (seatAcquired) {
                // Return the reserved seat if the patient was not added.
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

        } finally {

            if (mutexAcquired) {
                mutex.release();
            }
        }
    }
}
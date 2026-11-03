
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

    // Patient enters the waiting room
    public void put(Patient p) throws InterruptedException {

        if (!emptySeats.tryAcquire(0)) {

            Hospital.log("The waiting room is full.");

            Hospital.log(p.getPatientName()
                    + " is waiting outside for an available seat.");

            emptySeats.acquire();
        }

        mutex.acquire();

        try {

            queue.add(p);

            Hospital.log(p.getPatientName()
                    + " entered the waiting room.");

        } finally {

            mutex.release();
            fullSeats.release();
        }
    }

    // Doctor takes a patient
    public Patient take() throws InterruptedException {

        fullSeats.acquire();

        boolean locked = false;

        try {

            mutex.acquire();
            locked = true;

            Patient p = queue.poll();

            if (p == null) {
                fullSeats.release();
                return null;
            }

            emptySeats.release();

            return p;

        } finally {

            if (locked) {
                mutex.release();
            }
        }
    }
}

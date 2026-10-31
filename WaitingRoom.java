import java.util.LinkedList;
import java.util.Queue;

// Bounded buffer: patients are producers, doctors are consumers.
public class WaitingRoom {
    private final Queue<Patient> queue = new LinkedList<>(); // FIFO
    private final Semaphore emptySeats; // counts free seats
    private final Semaphore fullSeats;  // counts waiting patients
    private final Semaphore mutex = new Semaphore(1); // protects the queue

    public WaitingRoom(int capacity) {
        emptySeats = new Semaphore(capacity);
        fullSeats = new Semaphore(0);
    }

    // Used by patients (arrival) and by doctors (sending a patient back).
    // Blocks the caller while the room is full.
    public void put(Patient p) throws InterruptedException {
        if (!emptySeats.tryAcquire(0)) { // no seat right now
            Hospital.log("The waiting room is full.");
            Hospital.log(p.getPatientName() + " is waiting outside for an available seat.");
            emptySeats.acquire();
        }
        mutex.acquire();
        queue.add(p);
        Hospital.log(p.getPatientName() + " entered the waiting room.");
        mutex.release();
        fullSeats.release();
    }

    // Used by doctors. Blocks (doctor idle) until a patient is available.
    public Patient take() throws InterruptedException {
        fullSeats.acquire();
        mutex.acquire();
        Patient p = queue.poll();
        mutex.release();
        emptySeats.release(); // seat is free again
        return p;
    }
}
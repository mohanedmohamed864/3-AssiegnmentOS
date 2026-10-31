import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Hospital {

    // Single place for all output so lines never interleave.
    public static synchronized void log(String msg) {
        System.out.println(msg);
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner in = new Scanner(System.in);
        System.out.print("Waiting room capacity: ");
        int capacity = in.nextInt();
        System.out.print("Number of doctors: ");
        int numDoctors = in.nextInt();
        System.out.print("Number of patients: ");
        int numPatients = in.nextInt();
        in.close();

        if (capacity <= 0 || numDoctors <= 0 || numPatients <= 0) {
            System.out.println("All inputs must be positive integers.");
            return;
        }

        // Shared resources, synchronized independently.
        WaitingRoom waitingRoom = new WaitingRoom(capacity);
        Semaphore treatmentRooms = new Semaphore(2); // only 2 rooms exist

        // Doctors (Doctor class is written by teammate 3).
        List<Doctor> doctors = new ArrayList<>();
        for (int i = 1; i <= numDoctors; i++) {
            Doctor d = new Doctor(i, waitingRoom, treatmentRooms);
            doctors.add(d);
            d.start();
        }

        // Patients
        List<Patient> patients = new ArrayList<>();
        for (int i = 1; i <= numPatients; i++) {
            patients.add(new Patient(i, waitingRoom));
        }

        StringBuilder sb = new StringBuilder("Patients arriving:\n");
        for (Patient p : patients) sb.append(p.getPatientName()).append(' ');
        log(sb.toString().trim());

        Random rnd = new Random();
        for (Patient p : patients) {
            p.start();
            Thread.sleep(rnd.nextInt(500)); // staggered arrivals
        }

        // Wait until every patient has been treated and has left.
        for (Patient p : patients) p.join();

        // Doctors are blocked waiting for patients: stop them.
        for (Doctor d : doctors) d.interrupt();
        for (Doctor d : doctors) d.join();

        log("All patients have been treated.");
        log("Simulation completed.");
    }
}
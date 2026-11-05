import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Hospital {

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

        Output.header("HOSPITAL EMERGENCY SYSTEM");

        Output.section("SYSTEM CONFIGURATION");

        System.out.println("Waiting Room Capacity : " + capacity);
        System.out.println("Number of Doctors     : " + numDoctors);
        System.out.println("Number of Patients    : " + numPatients);
        System.out.println("Treatment Rooms       : 2");

        WaitingRoom waitingRoom = new WaitingRoom(capacity);

        Semaphore treatmentRooms = new Semaphore(2);

        List<Doctor> doctors = new ArrayList<>();

        for (int i = 1; i <= numDoctors; i++) {
            Doctor d = new Doctor(i, waitingRoom, treatmentRooms);
            doctors.add(d);
            d.start();
        }

        List<Patient> patients = new ArrayList<>();

        for (int i = 1; i <= numPatients; i++) {
            patients.add(new Patient(i, waitingRoom));
        }

        StringBuilder sb = new StringBuilder();

        for (Patient p : patients) {
            sb.append(p.getPatientName()).append(", ");
        }

        Output.section("PATIENT ARRIVAL");

        System.out.println("Patients arriving: "
                + sb.substring(0, sb.length() - 2));

        Output.section("LIVE SIMULATION");

        Random rnd = new Random();

        for (Patient p : patients) {
            p.start();
            Thread.sleep(rnd.nextInt(500));
        }

        for (Patient p : patients) {
            p.join();
        }

        for (Doctor d : doctors) {
            d.interrupt();
        }

        for (Doctor d : doctors) {
            d.join();
        }

        Output.header("SIMULATION SUMMARY");

        int treated = Patient.getTreatedCount();
int remaining = numPatients - treated;

System.out.println("Total Patients       : " + numPatients);
System.out.println("Successfully Treated : " + treated);
System.out.println("Remaining Patients   : " + remaining);

        Output.success("All patients have been treated.");
        Output.success("Simulation completed successfully.");

        System.out.println("============================================================");
    }
}

// github link : https://github.com/mohanedmohamed864/3-AssiegnmentOS.git
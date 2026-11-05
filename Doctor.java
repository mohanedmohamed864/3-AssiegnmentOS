import java.util.Random;

public class Doctor extends Thread {

    private static final long TIMEOUT_MS = 3000;

    private final int doctorId;
    private final WaitingRoom waitingRoom;
    private final Semaphore treatmentRooms;
    private final Random random = new Random();

    public Doctor(int id, WaitingRoom waitingRoom, Semaphore treatmentRooms) {
        this.doctorId = id;
        this.waitingRoom = waitingRoom;
        this.treatmentRooms = treatmentRooms;
    }

    private String name() {
        return "Doctor " + doctorId;
    }

    @Override
    public void run() {

        try {

            while (!Thread.currentThread().isInterrupted()) {

                Patient patient = waitingRoom.take();

                if (patient == null) {
                    continue;
                }

                Output.print("ASSIGNED",
                        name() + " is assigned to "
                                + patient.getPatientName() + ".");

                handlePatient(patient);

                if (!Thread.currentThread().isInterrupted()) {
                    Output.print("AVAILABLE",
                            name() + " is now available.");
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void handlePatient(Patient patient) {

        Output.print("TRY ROOM",
                name() + " is trying to acquire a treatment room.");

        boolean gotRoom;

        try {

            gotRoom = treatmentRooms.tryAcquire(TIMEOUT_MS);

        } catch (InterruptedException e) {

            Output.print("INTERRUPTED",
                    name() + " was interrupted while waiting for a treatment room.");

            returnPatient(patient);

            Thread.currentThread().interrupt();
            return;
        }

        if (!gotRoom) {

            Output.print("TIMEOUT",
                    name() + " could not acquire a treatment room.");

            Output.print("RETURNING",
                    name() + " is sending "
                            + patient.getPatientName()
                            + " back to the waiting room.");

            returnPatient(patient);
            return;
        }

        boolean completed = false;

        try {

            Output.print("ROOM",
                    name() + " acquired a treatment room.");

            Output.print("TREATMENT",
                    name() + " begins treating "
                            + patient.getPatientName() + ".");

            Thread.sleep(1000 + random.nextInt(4001));

            completed = true;

        } catch (InterruptedException e) {

            Output.print("INTERRUPTED",
                    name() + " was interrupted while treating "
                            + patient.getPatientName() + ".");

        } finally {

            if (completed) {

                Output.print("COMPLETED",
                        name() + " finished treating "
                                + patient.getPatientName() + ".");
            }

            treatmentRooms.release();

            Output.print("ROOM",
                    name() + " released the treatment room.");
        }

        if (completed) {

            patient.finishTreatment();

        } else {

            Output.print("RETURNING",
                    name() + " is sending "
                            + patient.getPatientName()
                            + " back to the waiting room.");

            returnPatient(patient);

            Thread.currentThread().interrupt();
        }
    }

    private void returnPatient(Patient patient) {

        boolean interrupted = false;

        while (true) {

            try {

                waitingRoom.put(patient);
                break;

            } catch (InterruptedException e) {

                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }
}

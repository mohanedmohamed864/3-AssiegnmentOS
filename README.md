# Hospital Emergency Room Simulation

A Java-based Operating Systems project that simulates a hospital
emergency room using multithreading, semaphores, and the
Producer--Consumer pattern.

## Table of Contents

-   [Overview](#overview)
-   [Features](#features)
-   [Operating Systems Concepts](#operating-systems-concepts)
-   [Project Structure](#project-structure)
-   [Requirements](#requirements)
-   [How to Run](#how-to-run)
-   [Test Cases](#test-cases)
-   [Sample Output](#sample-output)
-   [Simulation Summary](#simulation-summary)
-   [Notes](#notes)

## Overview

The simulation models patients arriving at a hospital and waiting to be
treated by a team of doctors.

The waiting room has a user-defined capacity. When it is full, arriving
patients wait outside until a seat becomes available. Doctors take
patients from the waiting room and attempt to acquire one of the
hospital's **two treatment rooms**.

A doctor waits up to **3 seconds** to acquire a treatment room. If no
room becomes available within that period, the doctor returns the
patient to the waiting room so the patient can be treated later.

The simulation ends after all patient threads have completed.

## Features

-   User-configurable waiting room capacity, number of doctors, and
    number of patients.
-   Multiple doctor and patient threads running concurrently.
-   Bounded waiting-room queue.
-   Patients wait outside when the waiting room is full.
-   Exactly two shared treatment rooms.
-   A 3-second timeout when a doctor attempts to acquire a treatment
    room.
-   Patients are returned to the waiting room after a treatment-room
    timeout.
-   Synchronized console output showing simulation events.
-   A final summary showing the number of treated and remaining
    patients.

## Operating Systems Concepts

The project demonstrates the following concepts:

-   **Multithreading:** doctors and patients execute in separate
    threads.
-   **Producer--Consumer:** patients add themselves to the waiting room,
    while doctors remove patients for treatment.
-   **Bounded Buffer:** the waiting room queue has a fixed capacity.
-   **Semaphores:** custom semaphore objects coordinate access to
    waiting-room seats, queued patients, mutual exclusion, and treatment
    rooms.
-   **Mutual Exclusion:** a mutex protects operations on the shared
    waiting-room queue.
-   **Synchronization:** threads coordinate when patients enter the
    queue, doctors take patients, and treatment rooms become available.
-   **Timed Resource Acquisition:** doctors wait for a treatment room
    for a limited time before returning the patient to the queue.

## Project Structure

  -----------------------------------------------------------------------
  File                                Responsibility
  ----------------------------------- -----------------------------------
  `Hospital.java`                     Reads the simulation configuration,
                                      creates the waiting room and
                                      treatment-room semaphore, starts
                                      the doctor and patient threads, and
                                      prints the final summary.

  `Doctor.java`                       Implements doctor threads. Doctors
                                      take patients, attempt to acquire a
                                      treatment room, perform treatment,
                                      and return patients to the waiting
                                      room if acquisition times out.

  `Patient.java`                      Implements patient threads. A
                                      patient arrives, enters the waiting
                                      room, waits for treatment to
                                      finish, and then leaves the
                                      hospital.

  `WaitingRoom.java`                  Implements the bounded waiting-room
                                      queue and coordinates access using
                                      semaphores.

  `Semaphore.java`                    Provides the custom semaphore
                                      operations used by the simulation,
                                      including blocking acquisition,
                                      release, and timed acquisition.

  `Output.java`                       Formats and synchronizes simulation
                                      messages printed to the console.
  -----------------------------------------------------------------------

## Requirements

-   Java Development Kit (JDK) installed.
-   A terminal or command prompt.
-   All six `.java` source files in the same project directory.

## How to Run

Open a terminal in the project directory and compile the Java source
files:

``` bash
javac *.java
```

Run the simulation:

``` bash
java Hospital
```

The program asks for three values:

1.  Waiting room capacity.
2.  Number of doctors.
3.  Number of patients.

All three values must be positive integers. The hospital uses exactly
two treatment rooms.

## Test Cases

The following test cases were executed against the simulation. The
observed results below are based on the recorded terminal runs.

### Test Case 01 --- Normal Concurrent Execution

  Setting                   Value
  ----------------------- -------
  Waiting room capacity         5
  Number of doctors             2
  Number of patients            5

**Objective:** Verify normal patient admission and concurrent treatment
with two doctors.

**Expected behavior:** - Patients enter the waiting room. - Both doctors
can treat patients concurrently. - No more than two treatment rooms can
be occupied at once. - All patients complete treatment and leave.

**Observed result:** All five patients were treated and left the
hospital. The run completed without an observed deadlock.

**Status:** **PASS**

### Test Case 02 --- Waiting Room Capacity Stress Test

  Setting                   Value
  ----------------------- -------
  Waiting room capacity         1
  Number of doctors             1
  Number of patients            6

**Objective:** Verify that the waiting-room capacity is enforced and
that patients wait outside when it is full.

**Expected behavior:** - Only one patient can occupy the waiting room at
a time. - Other patients wait outside while the seat is occupied. - When
the doctor takes a patient, a seat becomes available. - Waiting patients
can enter as seats become available. - All six patients eventually
complete treatment and leave.

**Observed result:** The output showed patients waiting outside while
the single waiting-room seat was occupied. As the doctor took patients,
seats became available and the waiting patients entered. All six
patients were treated and left.

**Status:** **PASS**

### Test Case 03 --- Multiple Doctors and Treatment-Room Timeout

  Setting                   Value
  ----------------------- -------
  Waiting room capacity        10
  Number of doctors             5
  Number of patients           10

**Objective:** Verify concurrent doctor activity, the two-room limit,
and the timeout-and-return behavior.

**Expected behavior:** - Five doctor threads operate concurrently. - No
more than two treatment rooms are occupied at once. - A doctor that
cannot acquire a room within three seconds times out. - The affected
patient returns to the waiting room and can be treated later. - All ten
patients eventually complete treatment and leave.

**Observed result:** The output showed five doctors working concurrently
and a timeout for patient P7. Doctor 1 returned P7 to the waiting room.
P7 was later assigned to Doctor 5, treated successfully, and left the
hospital. The final summary reported 10 successfully treated patients
and 0 remaining patients.

**Status:** **PASS**

## Sample Output

The following is a shortened representative excerpt from the third test
case. It illustrates the timeout and retry path; it is not the complete
execution log.

``` text
[18:38:46] [TIMEOUT   ] Doctor 1 could not acquire a treatment room.
[18:38:46] [RETURNING ] Doctor 1 is sending P7 back to the waiting room.
[18:38:46] [ADMITTED  ] P7 entered the waiting room.
...
[18:38:48] [TREATMENT ] Doctor 5 begins treating P7.
...
[18:38:50] [COMPLETED ] Doctor 5 finished treating P7.
[18:38:50] [DEPARTURE ] P7 left the hospital.

SIMULATION SUMMARY
Total Patients       : 10
Successfully Treated : 10
Remaining Patients   : 0
```

The exact timestamps and event order can vary between runs because the
threads execute concurrently.

## Simulation Summary

At the end of a run, the program displays:

-   **Total Patients:** the number of patients entered by the user.
-   **Successfully Treated:** the number of patients whose treatment
    completed successfully.
-   **Remaining Patients:** the total number of patients minus the
    successfully treated count.

For the third test case, the observed summary was:

  Metric                   Result
  ---------------------- --------
  Total patients               10
  Successfully treated         10
  Remaining patients            0

## Notes

-   The number of treatment rooms is fixed at two.
-   Treatment durations and patient arrival delays can cause different
    thread interleavings on different runs.
-   A timeout does not mean that a patient is rejected; the patient is
    returned to the waiting room for another attempt.
-   The test results demonstrate the behavior observed in these runs.
    They do not constitute a formal proof that deadlocks or race
    conditions are impossible under every possible execution.

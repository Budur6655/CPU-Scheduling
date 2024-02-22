
package cpu;
/**
 * @author budur
 */
import java.util.LinkedList;
import java.util.Queue;

class Burst {
    int burstTime;
    //is it for cpu or i/o
    boolean isCpuBurst;
    //constructor
    public Burst(int burstTime, boolean isCpuBurst) {
        this.burstTime = burstTime;
        this.isCpuBurst = isCpuBurst;
    }
    @Override
    public String toString() {
        return "Burst{" +
                "burstTime=" + burstTime +
                ", isCpuBurst=" + isCpuBurst +
                '}';
    }
}

class ProcessRR {
    int processId;
    int arrivalTime;
    Queue<Burst> bursts;
    //constructor
    public ProcessRR(int processId, int arrivalTime, Queue<Burst> bursts) {
        this.processId = processId;
        this.arrivalTime = arrivalTime;
        this.bursts = bursts;
    }
    @Override
    public String toString() {
        return "ProcessRR{" +
                "processId=" + processId +
                ", arrivalTime=" + arrivalTime +
                ", bursts=" + bursts +
                '}';
    }
}

class RoundRobinScheduling {
    // Method to schedule processes using Round Robin algorithm
    public static void scheduleProcesses(Queue<ProcessRR> processes, int timeQuantum) {
        //number of process 
        int n = processes.size();
        int currentTime = 0;
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;
        int totalCpuTime = 0;
        int totalContextSwitches = 0;
        /// This line creates a new LinkedList and initializes it with the elements from the processes queue.
        // Any modifications made to readyQueue will not affect the original processes queue.
        // This line is useful for creating a copy of the processes queue, allowing operations to be performed on the readyQueue without altering the original queue.
        Queue<ProcessRR> readyQueue = new LinkedList<>(processes);
        ProcessRR currentProcess = null;
        Burst currentBurst = null;
        // Execute processes until the ready queue is empty
        while (!readyQueue.isEmpty()) {
            currentProcess = readyQueue.poll();
            // Skip the process if it has no more bursts
            if (currentProcess.bursts.isEmpty()) {
                continue;
            }
            // Check if the process has arrived
            if (currentProcess.arrivalTime > currentTime) {
                currentTime++;
                readyQueue.add(currentProcess);
                continue;
            }
            currentBurst = currentProcess.bursts.poll();
            // Check for context switch
            if (currentBurst != null && currentBurst.isCpuBurst && currentBurst.burstTime > timeQuantum) {
                totalContextSwitches++;
            }
            // Execute the burst for the time quantum or until it completes
            int executionTime = Math.min(timeQuantum, currentBurst.burstTime);
            currentTime += executionTime;
            currentBurst.burstTime -= executionTime;
            // Check if the burst is completed
            if (currentBurst.burstTime > 0) {
                // Add the process back to the queue
                readyQueue.add(currentProcess);
            } else {
                // Burst completed, check for more bursts
                if (!currentProcess.bursts.isEmpty()) {
                    // Move to the next burst
                    currentBurst = currentProcess.bursts.poll();
                    if (!currentBurst.isCpuBurst) {
                        // I/O burst, add the process back to the queue
                        readyQueue.add(currentProcess);
                    }
                }
            }
            totalCpuTime += executionTime;
        }
        // Calculate waiting time and turnaround time for each process
        for (ProcessRR process : processes) {
            int waitingTime = currentTime - process.arrivalTime - process.bursts.stream()
                    .filter(burst -> burst.isCpuBurst)
                    .mapToInt(burst -> burst.burstTime)
                    .sum();
            int turnaroundTime = waitingTime + process.bursts.stream()
                    .mapToInt(burst -> burst.burstTime)
                    .sum();
            totalWaitingTime += waitingTime;
            totalTurnaroundTime += turnaroundTime;
        }
        int averageWaitingTime = totalWaitingTime / n;
        int averageTurnaroundTime = totalTurnaroundTime / n;
        System.out.println("Average Waiting Time: " + averageWaitingTime);
        System.out.println("Average Turnaround Time: " + averageTurnaroundTime);
        System.out.println("Total CPU Time: " + totalCpuTime);
        System.out.println("Total Context Switches: " + totalContextSwitches);
    }
}

class FCFS_Scheduling {
    // Method to schedule processes using First Come First Serve (FCFS) algorithm
    public static void scheduleProcesses(Queue<ProcessRR> processes) {
        int n = processes.size();
        int currentTime = 0;
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;
        int totalCpuTime = 0;
        int totalContextSwitches = 0;
        // Convert the processes queue to an array
        ProcessRR[] Processes = processes.toArray(new ProcessRR[processes.size()]);
        ProcessRR currentProcess = null;
        Burst currentBurst = null;
        // Iterate through each process
        for (ProcessRR process : Processes) {
            // Check if the process has arrived
            if (process.arrivalTime > currentTime) {
                currentTime = process.arrivalTime;
            }
            // Set the current process
            currentProcess = process;
            // Execute the bursts for the process
            for (Burst burst : process.bursts) {
                // Check for context switch
                if (currentBurst != null && currentBurst.isCpuBurst && !burst.isCpuBurst) {
                    totalContextSwitches++;
                }
                // Update the current burst
                currentBurst = burst;
                // Update the current time
                currentTime += burst.burstTime;
                // Calculate waiting time and turnaround time
                int waitingTime = currentTime - process.arrivalTime - burst.burstTime;
                int turnaroundTime = currentTime - process.arrivalTime;
                totalWaitingTime += waitingTime;
                totalTurnaroundTime += turnaroundTime;
                if (burst.isCpuBurst) {
                    totalCpuTime += burst.burstTime;
                }
            }
        }
        int averageWaitingTime = totalWaitingTime / n;
        int averageTurnaroundTime = totalTurnaroundTime / n;
        System.out.println("Average Waiting Time: " + averageWaitingTime);
        System.out.println("Average Turnaround Time: " + averageTurnaroundTime);
        System.out.println("Total CPU Time: " + totalCpuTime);
        System.out.println("Total Context Switches: " + totalContextSwitches);
    }
}
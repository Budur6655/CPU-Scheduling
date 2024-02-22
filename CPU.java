
package cpu;
/**
 *
 * @author budur
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class CPU {
    // Method to read the input file and return a list of processes
    //The outer linked list holds all the processes, 
    // the inner linked lists hold the attributes of each process (such as process ID, arrival time, etc.).
    public static LinkedList<LinkedList<Integer>> readFile(String path) throws FileNotFoundException {
        LinkedList<LinkedList<Integer>> processes = new LinkedList<LinkedList<Integer>>();
        File f1 = new File(path);
        Scanner fileRead = new Scanner(f1);
        // Read each line of the file
        while (fileRead.hasNextLine()) {
            LinkedList<Integer> process = new LinkedList<Integer>();
            String s = fileRead.nextLine();
            String[] subStr = s.split(" ");
            // Convert each number in the line to an integer and add it to the process list
            for (int i = 0; i < subStr.length; i++) {
                process.add(Integer.parseInt(subStr[i]));
            }
            processes.add(process);
        }
        // Return the list of processes
        return processes;
    }
    // Method to create a queue of processes from the list of process attributes
    public static Queue<ProcessRR> createProcess(LinkedList<LinkedList<Integer>> processess) {
        //declares a variable named processes of type Queue<ProcessRR> and initializes it with a new instance of the LinkedList class.
        Queue<ProcessRR> processes = new LinkedList<ProcessRR>();
        // Process each list of attributes
        while (!processess.isEmpty()) {
            //This line retrieves and removes the head of the processess list, storing it in a new LinkedList<Integer> variable named time
            LinkedList<Integer> time = processess.poll();
            //declaring a variable named bursts of type Queue<Burst>, and initializing it with a new instance of the LinkedList class.
            Queue<Burst> bursts = new LinkedList<>();
            int id = time.poll();
            int arrivalTime = time.poll();
            boolean isCPU = true;
            // Convert each attribute into a Burst object and add it to the bursts queue
            while (!time.isEmpty()) {
                bursts.add(new Burst(time.poll(), isCPU));
                isCPU = !isCPU;
            }
            ProcessRR process = new ProcessRR(id, arrivalTime, bursts);
            processes.add(process);
        }
        // Return the queue of processes
        return processes;
    }

    public static void main(String[] args) throws FileNotFoundException {
    Scanner scanner = new Scanner(System.in);
    System.out.println("\nPlease select from the following:\n1. First Come First Serve Method\n2. Round Robin Method");
    int choice = scanner.nextInt();
    String path = "C:/Users/vip/OneDrive/سطح المكتب/java/CPU/CPUData.txt";
    LinkedList<LinkedList<Integer>> data = readFile(path);
    switch (choice) {
        case 1:
            // Create queue of processes for FCFS scheduling
            Queue<ProcessRR> processesFCFS = createProcess(data);           
            System.out.println("First Come First Serve Method");
            // Perform FCFS scheduling
            FCFS_Scheduling.scheduleProcesses(processesFCFS);
            break;
        case 2:
            // Create queue of processes for Round Robin scheduling
            Queue<ProcessRR> processesRR = createProcess(data);        
            System.out.println("Round Robin Method");
            System.out.println("Choose the quantum number:");
            int quantum = scanner.nextInt();          
            // Perform Round Robin scheduling with the specified time quantum
            RoundRobinScheduling.scheduleProcesses(processesRR, quantum);
            break;
        default:
            System.out.println("Invalid choice. Please try again.");
            break;
    }
}
}
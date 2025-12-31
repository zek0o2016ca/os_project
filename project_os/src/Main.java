

import Clock.ClockManager;
import FIFO.FIFO_main;
import LFU.LFUPageReplacement;
import LRU.LRUCache;
import MFU.MFU_main;


import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("       UNIFIED PAGE REPLACEMENT SYSTEM           ");
        System.out.println("==================================================");

        // 1. ROBUST INPUT (Collect data once)
        int frameCount = getValidPositiveInt("Enter number of frames: ");
        int n = getValidPositiveInt("Enter the number of pages in the reference string: ");
        int[] pages = getValidReferenceString(n);

        // 2. MENU LOOP
        while (true) {
            System.out.println("\n--------------------------------------------------");
            System.out.println("Select Algorithm:");
            System.out.println("1. Clock (Second Chance)");
            System.out.println("2. LFU  (Least Frequently Used)");
            System.out.println("3. LRU  (Least Recently Used)");
            System.out.println("4. MFU  (Most Frequently Used)");
            System.out.println("5. FIFO (First-In, First-Out)");
            System.out.println("0. Exit");
            System.out.print(">> Choice: ");

            String input = scanner.nextLine().trim();
            if (!input.matches("\\d+")) {
                System.out.println("Error: Please enter a number.");
                continue;
            }

            int choice = Integer.parseInt(input);
            System.out.println("\n--- Simulation Output ---");

            switch (choice) {
                case 1:
                    runClockDelegate(frameCount, pages);
                    break;
                case 2:
                    runLFUDelegate(frameCount, pages);
                    break;
                case 3:
                    runLRUDelegate(frameCount, pages);
                    break;
                case 4:
                    runMFUDelegate(frameCount, pages);
                    break;
                case 5:
                    runFIFODelegate(frameCount, pages);
                    break;
                case 0:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }

            System.out.println("\n(Press Enter to continue...)");
            scanner.nextLine();
        }
    }

    // =========================================================
    //      DELEGATION METHODS (Connecting to your Classes)
    // =========================================================

    // 1. CLOCK: Uses your existing 'Clock.ClockManager'
    private static void runClockDelegate(int frames, int[] pages) {
        System.out.println("[Calling ClockManager...]");
        ClockManager manager = new ClockManager(frames);
        for (int page : pages) {
            manager.accessPage(page);
        }
        manager.printSummary();
    }

    // 2. LFU: Uses 'LFU.LFUPageReplacement.pageFaults'
    private static void runLFUDelegate(int frames, int[] pages) {
        System.out.println("[Calling LFUPageReplacement...]");
        // Your Class 2 has a static method 'pageFaults'. We call it directly.
        // Note: Your LFU class prints output inside the method, which is perfect.
        int faults = LFUPageReplacement.pageFaults(pages,frames);

        // Print final stats if your class doesn't print the rate automatically
        double rate = (faults * 100.0) / pages.length;
        System.out.printf("Final Fault Rate: %.2f%%\n", rate);
    }

    // 3. LRU: Uses 'LRU.LRUCache'
    private static void runLRUDelegate(int frames, int[] pages) {
        System.out.println("[Calling LRUCache...]");
        LRUCache lru = new LRUCache(frames);

        // Your Class 3 Main loop logic:
        lru.simulateLRU(pages,frames);
        // Assuming LRUCache has a method to show stats, or we rely on accessPage output
        // If LRUCache has getStatistics().display(), call it:
        //lru.getStatistics().display();
    }

    // 4. MFU: Uses 'MFU.Main.simulateMFU'
    private static void runMFUDelegate(int frames, int[] pages) {
        System.out.println("[Calling MFU Simulation...]");
        // You provided Class 4 as 'Main'. You should ideally rename it to 'MFUManager'

        // If you kept it as 'Main' in package 'MFU':
        MFU_main.simulateMFU(pages, frames);
    }

    // 5. FIFO: Logic Adaptor
    private static void runFIFODelegate(int frames, int[] pages) {
        System.out.println("[Running FIFO Logic]");
        // Since Class 5 (FIFo_main) puts all logic in 'main' and doesn't expose a helper method,
        // we must implement the loop here using standard Java collections to respect "FIFO".
        // (Alternatively, you would need to refactor Class 5 to expose a 'public static void run(frames, pages)' method).

        FIFO_main.simulateFIFO(frames, pages);
    }

    // =========================================================
    //      INPUT VALIDATION HELPERS
    // =========================================================

    private static int getValidPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(input);
                if (val > 0) return val;
                System.out.println("Error: Must be > 0.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Enter a valid integer.");
            }
        }
    }

    private static int[] getValidReferenceString(int n) {
        int[] result = new int[n];
        while (true) {
            System.out.println("Enter exactly " + n + " page IDs (space/comma separated):");
            String line = scanner.nextLine().trim();
            String[] parts = line.split("[,\\s]+");

            if (parts.length != n) {
                System.out.println("Error: You entered " + parts.length + " values. Expected " + n + ".");
                continue;
            }
            try {
                for (int i=0; i<n; i++) {
                    int val = Integer.parseInt(parts[i]);
                    if (val < 0) throw new NumberFormatException();
                    result[i] = val;
                }
                return result;
            } catch (NumberFormatException e) {
                System.out.println("Error: Pages must be non-negative integers.");
            }
        }
    }
}
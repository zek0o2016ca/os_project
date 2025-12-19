package MFU;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- Virtual Memory MFU Simulator (Robust Version) ---");

        // 1. Get Frame Count (Must be > 0)

        int capacity = getValidPositiveInt(scanner, "Enter the number of frames: ");

        // 2. Get Reference Count (Must be > 0)
        int n = getValidPositiveInt(scanner, "Enter the number of page references: ");

        // 3. Get Reference Sequence (Must be >= 0 and not text)
        int[] referenceString = new int[n];
        System.out.println("Enter page reference sequence:");
        for (int i = 0; i < n; i++) {
            referenceString[i] = getValidNonNegativeInt(scanner, "Page " + (i + 1) + ": ");
        }

        // 4. Run Simulation
        try {
            simulateMFU(referenceString, capacity);
        } catch (Exception e) {
            System.out.println("\n[Logic Error]: " + e.getMessage());
        }

        scanner.close();
    }

    public static void simulateMFU(int[] pages, int capacity) {
        List<Integer> frames = new ArrayList<>();
        Map<Integer, Integer> currentFrequency = new HashMap<>();
        Map<Integer, Integer> arrivalTime = new HashMap<>();

        int pageFaults = 0;
        int timer = 0;

        System.out.println("\nRef | Frames               | Status");
        System.out.println("------------------------------------");

        for (int page : pages) {
            timer++;
            String status;

            if (frames.contains(page)) {
                status = "Hit";
                currentFrequency.put(page, currentFrequency.get(page) + 1);
            } else {
                status = "Fault";
                pageFaults++;

                if (frames.size() < capacity) {
                    frames.add(page);
                } else {
                    int victimIndex = findVictimIndex(frames, currentFrequency, arrivalTime);
                    int removedPage = frames.get(victimIndex);

                    // Reset stats for evicted page
                    currentFrequency.remove(removedPage);
                    arrivalTime.remove(removedPage);

                    // Replace in the specific frame slot
                    frames.set(victimIndex, page);
                }
                currentFrequency.put(page, 1);
                arrivalTime.put(page, timer);
            }
            System.out.printf("%-3d | %-20s | %s\n", page, frames.toString(), status);
        }

        System.out.println("------------------------------------");
        System.out.println("Total Hits: " + (pages.length - pageFaults));
        System.out.println("Total Page Faults: " + pageFaults);
        System.out.printf("Fault Rate: %.2f%%\n", (double) pageFaults / pages.length * 100);
    }

    private static int findVictimIndex(List<Integer> frames, Map<Integer, Integer> freqMap, Map<Integer, Integer> arrivalMap) {
        int victimIdx = 0;
        int maxFreq = -1;

        for (int i = 0; i < frames.size(); i++) {
            int currentPage = frames.get(i);
            int currentFreq = freqMap.get(currentPage);

            if (currentFreq > maxFreq) {
                maxFreq = currentFreq;
                victimIdx = i;
            } else if (currentFreq == maxFreq) {
                // FIFO Tie-breaker
                if (arrivalMap.get(currentPage) < arrivalMap.get(frames.get(victimIdx))) {
                    victimIdx = i;
                }
            }
        }
        return victimIdx;
    }

    // NEW HELPER: Ensures page numbers are 0 or higher and not text
    private static int getValidNonNegativeInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextInt()) {
                int val = sc.nextInt();
                if (val >= 0) return val;
                System.out.println("Error: Page numbers cannot be negative.");
            } else {
                System.out.println("Error: Invalid input. Please enter a whole number.");
                sc.next(); // Clear the invalid text
            }
        }
    }

    // HELPER: Ensures positive numbers for counts (frames/references)
    private static int getValidPositiveInt(Scanner sc, String prompt) {
        while (true) {
            int val = getValidNonNegativeInt(sc, prompt);
            if (val > 0) return val;
            System.out.println("Error: This value must be greater than 0.");
        }
    }
}
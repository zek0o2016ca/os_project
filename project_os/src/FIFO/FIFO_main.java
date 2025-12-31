package FIFO;

import java.util.*;

public class FIFO_main {

    // Helper class to track page validity and frame location
    static class PageTableEntry {
        int frameNumber = -1;
        boolean valid = false;
    }

    /**
     * Public helper method to be called from UnifiedPageReplacement.
     * Takes frames and pages as input instead of asking the user.
     */
    public static void simulateFIFO(int frameCount, int[] pageRequests) {
        System.out.println("\n[Running FIFO Simulation...]");

        // Physical memory representation
        int[] frames = new int[frameCount];
        Arrays.fill(frames, -1);

        // Page table to track where pages are
        Map<Integer, PageTableEntry> pageTable = new HashMap<>();

        // FIFO queue to track the order of arrival
        Queue<Integer> fifoQueue = new LinkedList<>();

        int pageFaults = 0;

        // --- Core FIFO Logic ---
        for (int page : pageRequests) {
            PageTableEntry entry = pageTable.getOrDefault(page, new PageTableEntry());

            // Check if page is valid and in a valid frame
            if (entry.valid && entry.frameNumber != -1) {
                System.out.println("Page " + page + " -> HIT");
            } else {
                System.out.println("Page " + page + " -> FAULT");
                pageFaults++;

                if (fifoQueue.size() < frameCount) {
                    // Scenario A: Empty frame available
                    for (int i = 0; i < frames.length; i++) {
                        if (frames[i] == -1) {
                            frames[i] = page;
                            entry.frameNumber = i;
                            entry.valid = true;
                            fifoQueue.add(page);
                            break;
                        }
                    }
                } else {
                    // Scenario B: Memory full -> Evict oldest (FIFO)
                    int victimPage = fifoQueue.poll();
                    int victimFrame = pageTable.get(victimPage).frameNumber;

                    System.out.println("   -> Evicting Page " + victimPage + " from Frame " + victimFrame);

                    // Update victim entry
                    PageTableEntry victimEntry = pageTable.get(victimPage);
                    victimEntry.valid = false;
                    victimEntry.frameNumber = -1;
                    pageTable.put(victimPage, victimEntry);

                    // Place new page in the freed frame
                    frames[victimFrame] = page;
                    entry.frameNumber = victimFrame;
                    entry.valid = true;
                    fifoQueue.add(page);
                }
            }

            // Update Page Table and Print State
            pageTable.put(page, entry);
            printFrames(frames);
        }

        // Print Final Statistics
        System.out.println("----------------------------------");
        System.out.println("Total Page Faults: " + pageFaults);
        double rate = ((double) pageFaults / pageRequests.length) * 100;
        System.out.printf("Fault Rate: %.2f%%\n", rate);
        System.out.println("----------------------------------");
    }

    // Helper to print frames neatly
    private static void printFrames(int[] frames) {
        System.out.print("   Frames: ");
        for (int f : frames) {
            if (f == -1) System.out.print("[ ] ");
            else System.out.print("[" + f + "] ");
        }
        System.out.println();
    }
}
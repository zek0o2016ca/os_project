package LRU;

import java.util.*;

public class LRUCache{

    private final int numFrames;
    private final Page[] frames;
    private int pageFaults = 0;
    private int currentTime = 0;

    public LRUCache(int numFrames) {
        this.numFrames = numFrames;
        this.frames = new Page[numFrames];
    }

    public String processReference(int refId) {
        currentTime++;
        int hitIndex = -1;

        // 1. Check for Hit (Is the page already in memory?)
        for (int i = 0; i < numFrames; i++) {
            if (frames[i] != null && frames[i].id == refId) {
                hitIndex = i;
                break;
            }
        }

        if (hitIndex != -1) {
            // HIT: Update the last used time to the current clock
            frames[hitIndex].lastUsedTime = currentTime;
            return "Hit";
        }

        // 2. Handle FAULT
        pageFaults++;
        int replaceIndex = findReplacementIndex();

        // Insert new page with current timestamp
        frames[replaceIndex] = new Page(refId, currentTime);
        return "Fault";
    }

    private int findReplacementIndex() {
        // First check for empty slots (initial filling)
        for (int i = 0; i < numFrames; i++) {
            if (frames[i] == null) return i;
        }

        // LRU Logic: Find the page with the SMALLEST lastUsedTime
        // (the one used furthest back in the past)
        int leastUsedTime = Integer.MAX_VALUE;
        int replaceIndex = 0;

        for (int i = 0; i < numFrames; i++) {
            if (frames[i].lastUsedTime < leastUsedTime) {
                leastUsedTime = frames[i].lastUsedTime;
                replaceIndex = i;
            }
        }
        return replaceIndex;
    }

    public String getFrameState() {
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        for (Page p : frames) {
            if (p != null) sj.add(String.valueOf(p.id));
        }
        return sj.toString();
    }

    public int getPageFaults() { return pageFaults; }

    public void simulateLRU(int[] pages, int capacity) {
        List<Integer> frames = new ArrayList<>();
        Map<Integer, Integer> lastUsedTime = new HashMap<>();

        int pageFaults = 0;
        int timer = 0;

        System.out.println("\n--- LRU Simulation ---");
        System.out.println("Ref | Frames               | Status");
        System.out.println("------------------------------------");

        for (int page : pages) {
            timer++;
            String status;

            // 1. Check for Hit
            if (frames.contains(page)) {
                status = "Hit";
                // Update timestamp on Hit
                lastUsedTime.put(page, timer);
            }
            // 2. Handle Fault
            else {
                status = "Fault";
                pageFaults++;

                if (frames.size() < capacity) {
                    frames.add(page);
                } else {
                    // Find replacement index logic from LRUCache
                    int victimIndex = -1;
                    int minTime = Integer.MAX_VALUE;

                    for (int i = 0; i < frames.size(); i++) {
                        int p = frames.get(i);
                        int lastUsed = lastUsedTime.get(p);

                        if (lastUsed < minTime) {
                            minTime = lastUsed;
                            victimIndex = i;
                        }
                    }

                    int removedPage = frames.get(victimIndex);

                    // Remove old map entry
                    lastUsedTime.remove(removedPage);

                    // Replace page in frame
                    frames.set(victimIndex, page);
                }
                // Set timestamp for new/faulted page
                lastUsedTime.put(page, timer);
            }
            System.out.printf("%-3d | %-20s | %s\n", page, frames.toString(), status);
        }

        System.out.println("------------------------------------");
        System.out.println("Total Hits: " + (pages.length - pageFaults));
        System.out.println("Total Page Faults: " + pageFaults);
        System.out.printf("Fault Rate: %.2f%%\n", (double) pageFaults / pages.length * 100);
    }
}

package main;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("==================================================================");
        System.out.println("|   ####   ####      #    #      ####  #### #    # #     #####  |");
        System.out.println("|  #    # #         # #   #     #        #  ##  ## #     #    # |");
        System.out.println("|  #    #  ####    #####  #      ####    #  # ## # #     #####  |");
        System.out.println("|  #    #      #  #     # #          #   #  #    # #     #   #  |");
        System.out.println("|   ####   ####  #       # #####  ####  #### #    # ##### #    # |");
        System.out.println("==================================================================");
        System.out.println("| 1 | FIFO Page Replacement       |");
        System.out.println("====================================");
        System.out.println("| 2 | Clock Page Replacement      |");
        System.out.println("====================================");
        System.out.println("| 3 | LRU Page Replacement        |");
        System.out.println("====================================");
        System.out.println("| 4 | LFU Page Replacement        |");
        System.out.println("====================================");
        System.out.println("| 5 | MFU Page Replacement        |");
        System.out.println("====================================");

        System.out.print("Choose algorithm (1-5): ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                runPageReplacement(new fifo(), scanner);
                break;
            case 2:
                runPageReplacement(new clock(), scanner);
                break;
            case 3:
                runPageReplacement(new LRU(), scanner);
                break;
            case 4:
                runPageReplacement(new LFU(), scanner);
                break;
            case 5:
                runPageReplacement(new MFU(), scanner);
                break;
            default:
                System.out.println("Invalid choice!");
        }

        scanner.close();
    }

    // ==================== GENERIC PAGE REPLACEMENT RUNNER ====================
    public static void runPageReplacement(PageReplacementAlgorithm algorithm, Scanner sc) {
        try {
            int frameCount = 0;
            while (frameCount <= 0) {
                System.out.print("Enter number of frames (positive integer): ");
                if (sc.hasNextInt()) {
                    frameCount = sc.nextInt();
                    if (frameCount <= 0) System.out.println("Frame count must be positive.");
                } else {
                    System.out.println("Invalid input. Please enter a positive integer.");
                    sc.next();
                }
            }

            int n = 0;
            while (n <= 0) {
                System.out.print("Enter number of page requests (positive integer): ");
                if (sc.hasNextInt()) {
                    n = sc.nextInt();
                    if (n <= 0) System.out.println("Number of page requests must be positive.");
                } else {
                    System.out.println("Invalid input. Please enter a positive integer.");
                    sc.next();
                }
            }

            int[] pageRequests = new int[n];
            System.out.println("Enter page numbers (non-negative integers):");
            for (int i = 0; i < n; i++) {
                while (true) {
                    if (sc.hasNextInt()) {
                        int page = sc.nextInt();
                        if (page < 0) {
                            System.out.println("Page number cannot be negative. Enter again:");
                        } else {
                            pageRequests[i] = page;
                            break;
                        }
                    } else {
                        System.out.println("Invalid input. Enter a non-negative integer:");
                        sc.next();
                    }
                }
            }

            algorithm.initialize(frameCount);
            System.out.println("\n========== " + algorithm.getName() + " SIMULATION ==========\n");

            for (int page : pageRequests) {
                algorithm.accessPage(page);
            }

            algorithm.printSummary();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


}

// ==================== PAGE REPLACEMENT INTERFACE ====================
interface PageReplacementAlgorithm {
    void initialize(int frameCount);
    void accessPage(int page);
    void printSummary();
    String getName();
}

// ==================== FIFO PAGE REPLACEMENT ====================
class fifo implements PageReplacementAlgorithm {
    private int[] frames;
    private Queue<Integer> fifoQueue;
    private Map<Integer, Boolean> pageInMemory;
    private int pageFaults;
    private int pageHits;

    @Override
    public void initialize(int frameCount) {
        frames = new int[frameCount];
        Arrays.fill(frames, -1);
        fifoQueue = new LinkedList<>();
        pageInMemory = new HashMap<>();
        pageFaults = 0;
        pageHits = 0;
    }

    @Override
    public void accessPage(int page) {
        if (pageInMemory.containsKey(page) && pageInMemory.get(page)) {
            System.out.println("Page " + page + " HIT");
            pageHits++;
        } else {
            System.out.println("Page " + page + " FAULT");
            pageFaults++;

            if (fifoQueue.size() < frames.length) {
                for (int i = 0; i < frames.length; i++) {
                    if (frames[i] == -1) {
                        frames[i] = page;
                        fifoQueue.add(page);
                        pageInMemory.put(page, true);
                        break;
                    }
                }
            } else {
                int victimPage = fifoQueue.poll();
                System.out.println("  → Evicting page " + victimPage);
                pageInMemory.put(victimPage, false);

                for (int i = 0; i < frames.length; i++) {
                    if (frames[i] == victimPage) {
                        frames[i] = page;
                        break;
                    }
                }
                fifoQueue.add(page);
                pageInMemory.put(page, true);
            }
        }

        System.out.print("Frames: ");
        for (int f : frames) {
            if (f == -1) System.out.print("- ");
            else System.out.print(f + " ");
        }
        System.out.println("\n");
    }

    @Override
    public void printSummary() {
        int total = pageFaults + pageHits;
        System.out.println("========== SUMMARY ==========");
        System.out.println("Total Requests: " + total);
        System.out.println("Page Faults: " + pageFaults);
        System.out.println("Page Hits: " + pageHits);
        System.out.printf("Hit Rate: %.2f%%\n", (pageHits * 100.0 / total));
    }

    @Override
    public String getName() {
        return "FIFO";
    }
}

// ==================== CLOCK PAGE REPLACEMENT ====================
class clock implements PageReplacementAlgorithm {
    private int[] frames;
    private boolean[] referenceBits;
    private int clockHand;
    private int pageFaults;
    private int pageHits;
    private Map<Integer, Integer> pageToFrame;

    @Override
    public void initialize(int frameCount) {
        frames = new int[frameCount];
        referenceBits = new boolean[frameCount];
        Arrays.fill(frames, -1);
        clockHand = 0;
        pageFaults = 0;
        pageHits = 0;
        pageToFrame = new HashMap<>();
    }

    @Override
    public void accessPage(int page) {
        if (pageToFrame.containsKey(page)) {
            System.out.println("Page " + page + " HIT");
            pageHits++;
            int frameIndex = pageToFrame.get(page);
            referenceBits[frameIndex] = true;
        } else {
            System.out.println("Page " + page + " FAULT");
            pageFaults++;

            int emptyFrame = findEmptyFrame();
            if (emptyFrame != -1) {
                frames[emptyFrame] = page;
                referenceBits[emptyFrame] = true;
                pageToFrame.put(page, emptyFrame);
            } else {
                while (referenceBits[clockHand]) {
                    referenceBits[clockHand] = false;
                    clockHand = (clockHand + 1) % frames.length;
                }

                int victimPage = frames[clockHand];
                System.out.println("  → Evicting page " + victimPage);
                pageToFrame.remove(victimPage);

                frames[clockHand] = page;
                referenceBits[clockHand] = true;
                pageToFrame.put(page, clockHand);

                clockHand = (clockHand + 1) % frames.length;
            }
        }

        System.out.print("Frames: ");
        for (int f : frames) {
            if (f == -1) System.out.print("- ");
            else System.out.print(f + " ");
        }
        System.out.println();
    }

    private int findEmptyFrame() {
        for (int i = 0; i < frames.length; i++) {
            if (frames[i] == -1) return i;
        }
        return -1;
    }

    @Override
    public void printSummary() {
        int total = pageFaults + pageHits;
        System.out.println("\n========== SUMMARY ==========");
        System.out.println("Total Requests: " + total);
        System.out.println("Page Faults: " + pageFaults);
        System.out.println("Page Hits: " + pageHits);
        System.out.printf("Hit Rate: %.2f%%\n", (pageHits * 100.0 / total));
    }

    @Override
    public String getName() {
        return "CLOCK";
    }
}

// ==================== LRU PAGE REPLACEMENT ====================
class LRU implements PageReplacementAlgorithm {
    private int[] frames;
    private Map<Integer, Integer> pageToFrame;
    private Map<Integer, Integer> lastUsed;
    private int pageFaults;
    private int pageHits;
    private int timestamp;

    @Override
    public void initialize(int frameCount) {
        frames = new int[frameCount];
        Arrays.fill(frames, -1);
        pageToFrame = new HashMap<>();
        lastUsed = new HashMap<>();
        pageFaults = 0;
        pageHits = 0;
        timestamp = 0;
    }

    @Override
    public void accessPage(int page) {
        timestamp++;

        if (pageToFrame.containsKey(page)) {
            System.out.println("Page " + page + " HIT");
            pageHits++;
            lastUsed.put(page, timestamp);
        } else {
            System.out.println("Page " + page + " FAULT");
            pageFaults++;

            int emptyFrame = findEmptyFrame();
            if (emptyFrame != -1) {
                frames[emptyFrame] = page;
                pageToFrame.put(page, emptyFrame);
                lastUsed.put(page, timestamp);
            } else {
                int lruPage = findLRUPage();
                int victimFrame = pageToFrame.get(lruPage);
                System.out.println("  → Evicting page " + lruPage + " (LRU)");

                pageToFrame.remove(lruPage);
                lastUsed.remove(lruPage);

                frames[victimFrame] = page;
                pageToFrame.put(page, victimFrame);
                lastUsed.put(page, timestamp);
            }
        }

        System.out.print("Frames: ");
        for (int f : frames) {
            if (f == -1) System.out.print("- ");
            else System.out.print(f + " ");
        }
        System.out.println();
    }

    private int findEmptyFrame() {
        for (int i = 0; i < frames.length; i++) {
            if (frames[i] == -1) return i;
        }
        return -1;
    }

    private int findLRUPage() {
        int lruPage = -1;
        int minTime = Integer.MAX_VALUE;
        for (Map.Entry<Integer, Integer> entry : lastUsed.entrySet()) {
            if (entry.getValue() < minTime) {
                minTime = entry.getValue();
                lruPage = entry.getKey();
            }
        }
        return lruPage;
    }

    @Override
    public void printSummary() {
        int total = pageFaults + pageHits;
        System.out.println("\n========== SUMMARY ==========");
        System.out.println("Total Requests: " + total);
        System.out.println("Page Faults: " + pageFaults);
        System.out.println("Page Hits: " + pageHits);
        System.out.printf("Hit Rate: %.2f%%\n", (pageHits * 100.0 / total));
    }

    @Override
    public String getName() {
        return "LRU";
    }
}

// ==================== LFU PAGE REPLACEMENT ====================
class LFU implements PageReplacementAlgorithm {
    private int[] frames;
    private Map<Integer, Integer> pageToFrame;
    private Map<Integer, Integer> frequency;
    private int pageFaults;
    private int pageHits;

    @Override
    public void initialize(int frameCount) {
        frames = new int[frameCount];
        Arrays.fill(frames, -1);
        pageToFrame = new HashMap<>();
        frequency = new HashMap<>();
        pageFaults = 0;
        pageHits = 0;
    }

    @Override
    public void accessPage(int page) {
        if (pageToFrame.containsKey(page)) {
            System.out.println("Page " + page + " HIT");
            pageHits++;
            frequency.put(page, frequency.get(page) + 1);
        } else {
            System.out.println("Page " + page + " FAULT");
            pageFaults++;

            int emptyFrame = findEmptyFrame();
            if (emptyFrame != -1) {
                frames[emptyFrame] = page;
                pageToFrame.put(page, emptyFrame);
                frequency.put(page, 1);
            } else {
                int lfuPage = findLFUPage();
                int victimFrame = pageToFrame.get(lfuPage);
                System.out.println("  → Evicting page " + lfuPage + " (freq: " + frequency.get(lfuPage) + ")");

                pageToFrame.remove(lfuPage);
                frequency.remove(lfuPage);

                frames[victimFrame] = page;
                pageToFrame.put(page, victimFrame);
                frequency.put(page, 1);
            }
        }

        System.out.print("Frames: ");
        for (int f : frames) {
            if (f == -1) System.out.print("- ");
            else System.out.print(f + " ");
        }
        System.out.print(" | Frequencies: ");
        for (int f : frames) {
            if (f == -1) System.out.print("- ");
            else System.out.print(frequency.get(f) + " ");
        }
        System.out.println();
    }

    private int findEmptyFrame() {
        for (int i = 0; i < frames.length; i++) {
            if (frames[i] == -1) return i;
        }
        return -1;
    }

    private int findLFUPage() {
        int lfuPage = -1;
        int minFreq = Integer.MAX_VALUE;
        for (Map.Entry<Integer, Integer> entry : frequency.entrySet()) {
            if (pageToFrame.containsKey(entry.getKey()) && entry.getValue() < minFreq) {
                minFreq = entry.getValue();
                lfuPage = entry.getKey();
            }
        }
        return lfuPage;
    }

    @Override
    public void printSummary() {
        int total = pageFaults + pageHits;
        System.out.println("\n========== SUMMARY ==========");
        System.out.println("Total Requests: " + total);
        System.out.println("Page Faults: " + pageFaults);
        System.out.println("Page Hits: " + pageHits);
        System.out.printf("Hit Rate: %.2f%%\n", (pageHits * 100.0 / total));
    }

    @Override
    public String getName() {
        return "LFU";
    }
}

// ==================== MFU PAGE REPLACEMENT ====================
class MFU implements PageReplacementAlgorithm {
    private int[] frames;
    private Map<Integer, Integer> pageToFrame;
    private Map<Integer, Integer> frequency;
    private int pageFaults;
    private int pageHits;

    @Override
    public void initialize(int frameCount) {
        frames = new int[frameCount];
        Arrays.fill(frames, -1);
        pageToFrame = new HashMap<>();
        frequency = new HashMap<>();
        pageFaults = 0;
        pageHits = 0;
    }

    @Override
    public void accessPage(int page) {
        if (pageToFrame.containsKey(page)) {
            System.out.println("Page " + page + " HIT");
            pageHits++;
            frequency.put(page, frequency.get(page) + 1);
        } else {
            System.out.println("Page " + page + " FAULT");
            pageFaults++;

            int emptyFrame = findEmptyFrame();
            if (emptyFrame != -1) {
                frames[emptyFrame] = page;
                pageToFrame.put(page, emptyFrame);
                frequency.put(page, 1);
            } else {
                int mfuPage = findMFUPage();
                int victimFrame = pageToFrame.get(mfuPage);
                System.out.println("  → Evicting page " + mfuPage + " (freq: " + frequency.get(mfuPage) + ")");

                pageToFrame.remove(mfuPage);
                frequency.remove(mfuPage);

                frames[victimFrame] = page;
                pageToFrame.put(page, victimFrame);
                frequency.put(page, 1);
            }
        }

        System.out.print("Frames: ");
        for (int f : frames) {
            if (f == -1) System.out.print("- ");
            else System.out.print(f + " ");
        }
        System.out.print(" | Frequencies: ");
        for (int f : frames) {
            if (f == -1) System.out.print("- ");
            else System.out.print(frequency.get(f) + " ");
        }
        System.out.println();
    }

    private int findEmptyFrame() {
        for (int i = 0; i < frames.length; i++) {
            if (frames[i] == -1) return i;
        }
        return -1;
    }

    private int findMFUPage() {
        int mfuPage = -1;
        int maxFreq = Integer.MIN_VALUE;
        for (Map.Entry<Integer, Integer> entry : frequency.entrySet()) {
            if (pageToFrame.containsKey(entry.getKey()) && entry.getValue() > maxFreq) {
                maxFreq = entry.getValue();
                mfuPage = entry.getKey();
            }
        }
        return mfuPage;
    }

    @Override
    public void printSummary() {
        int total = pageFaults + pageHits;
        System.out.println("\n========== SUMMARY ==========");
        System.out.println("Total Requests: " + total);
        System.out.println("Page Faults: " + pageFaults);
        System.out.println("Page Hits: " + pageHits);
        System.out.printf("Hit Rate: %.2f%%\n", (pageHits * 100.0 / total));
    }

    @Override
    public String getName() {
        return "MFU";
    }
}
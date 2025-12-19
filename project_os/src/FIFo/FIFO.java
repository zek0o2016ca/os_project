package FIFo;
import java.util.*;

class PageTableEntry {
    boolean valid;
    int frameNumber;

    public PageTableEntry() {
        valid = false;
        frameNumber = -1;
    }
}

public class FIFO {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            //Number of frames in physical memory and input validation
            int frameCount = 0;
            while (frameCount <= 0) {
                System.out.print("Enter number of frames (positive integer): ");
                if (sc.hasNextInt()) {
                    frameCount = sc.nextInt();
                    if (frameCount <= 0) System.out.println("Frame count must be positive.");
                } else {
                    System.out.println("Invalid input. Please enter a positive integer.");
                    sc.next(); // discard invalid input
                }
            }

            // Number of page requests and input validation
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

            // Physical memory
            int[] frames = new int[frameCount];
            Arrays.fill(frames, -1);

            // Page table
            Map<Integer, PageTableEntry> pageTable = new HashMap<>();

            // FIFO queue
            Queue<Integer> fifoQueue = new LinkedList<>();

            int pageFaults = 0;

            System.out.println("\nSimulation starting...\n");

            for (int page : pageRequests) {
                PageTableEntry entry = pageTable.getOrDefault(page, new PageTableEntry());

                if (entry.valid) {
                    System.out.println("Page " + page + " HIT");
                } else {
                    System.out.println("Page " + page + " FAULT");
                    pageFaults++;

                    if (fifoQueue.size() < frameCount) {
                        // يوجد Frame فاضي
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
                        // Frame ممتلئ → نخرج أقدم صفحة (FIFO)
                        int victimPage = fifoQueue.poll();
                        int victimFrame = pageTable.get(victimPage).frameNumber;

                        System.out.println("Removing page " + victimPage + " from frame " + victimFrame);

                        // استبدال الصفحة
                        frames[victimFrame] = page;
                        entry.frameNumber = victimFrame;
                        entry.valid = true;
                        fifoQueue.add(page);

                        // تحديث Page Table للصفحة التي أخرجناها
                        PageTableEntry victimEntry = pageTable.get(victimPage);
                        victimEntry.valid = false;
                        victimEntry.frameNumber = -1;
                        pageTable.put(victimPage, victimEntry);
                    }
                }

                // تحديث Page Table للصفحة الحالية
                pageTable.put(page, entry);

                // طباعة حالة الذاكرة الحالية
                System.out.print("Frames: ");
                for (int f : frames) {
                    if (f == -1) System.out.print("- ");
                    else System.out.print(f + " ");
                }
                System.out.println("\n");
            }

            System.out.println("Total Page Faults: " + pageFaults);

        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        } finally {
            sc.close();
        }
    }
}

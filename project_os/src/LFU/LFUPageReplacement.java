package LFU;

import utils.InputHandler;

import java.util.*;

public class LFUPageReplacement {
    public static void main(String[] args) {
        DisplayManager display = new DisplayManager();
        InputHandler input = new InputHandler();

        // Display header
        display.printHeader();

        // Get user input
        int frames = input.getFrameCount();
        int[] pages = input.getPageReferenceString();
//        int[] pages = {1, 2, 3, 4, 7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 0, 3, 2,
//                1, 2, 0, 1, 7, 0, 1};
//        int[] pages = { 1, 2, 3, 4, 2, 1, 5 };
//        int[] pages = {5, 0, 1, 3, 2, 4, 1, 0, 5};
//        int frames = 4;
        display.printPageReferenceString(pages);

        int pagefaults = pageFaults(pages, frames);

        System.out.println("Page Faults = "
                + pagefaults);
        System.out.println("Page Hits = "
                + (pages.length - pagefaults));
        double faultRate = (pagefaults * 100.0) / pages.length;
        System.out.println("Fault Rate = " + faultRate + "%");
    }

   public static int pageFaults(int[] pages, int frames) {
        int faults = 0;

        LinkedHashSet<Integer> memory = new LinkedHashSet<>();

        Map<Integer, Integer> freq = new HashMap<>();

        System.out.println("\n--- LFU Simulation Steps ---\n");

        for (int i = 0; i < pages.length; i++) {
            int currentPage = pages[i];
            boolean isFault = false;

            //if vaild in memory
            if (memory.contains(currentPage)) {
                // increment the freq
                freq.put(currentPage, freq.get(currentPage) + 1);
                isFault = false;

                memory.remove(currentPage);
                memory.add(currentPage);
            }
            // if page is invaild
            else {
                isFault = true;
                faults++;

                // if memory is full remove the least freq and not vaild in memory
                if (memory.size() == frames) {
                    int pageToRemove = findLFUPage(memory, freq);
                    memory.remove(pageToRemove);
                    freq.remove(pageToRemove);
                }

                // add page in memory
                memory.add(currentPage);
                freq.put(currentPage, 1);
            }
            // 🔹 PRINT STEP DETAILS
            printStep(i, currentPage, memory, freq, isFault);
        }

        return faults;
    }

    private static int findLFUPage(LinkedHashSet<Integer> memory, Map<Integer, Integer> freq) {
        int lfuPage = -1;
        int minFreq = Integer.MAX_VALUE;

        // find laest freq if all
        for (int page : memory) {
            if (freq.get(page) < minFreq) {
                minFreq = freq.get(page);
                lfuPage = page;
            }
        }

        return lfuPage;
    }

    private static void printStep(int step, int page,
                                  LinkedHashSet<Integer> memory,
                                  Map<Integer, Integer> freq,
                                  boolean isFault) {
        System.out.println("Step " + (step + 1) + ": Page " + page +
                (isFault ? " → FAULT" : " → HIT"));

        System.out.print("Memory: [ ");
        for (int p : memory) {
            System.out.print(p + " ");
        }
        System.out.println("]");

        System.out.print("Frequencies: { ");
        for (int p : memory) {
            System.out.print(p + "=" + freq.get(p) + " ");
        }
        System.out.println("}");

        System.out.println("----------------------------------");
    }
}
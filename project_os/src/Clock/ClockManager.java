package Clock;

class ClockManager {
    private PageFrame[] frames;
    private int pointer; // The "Clock Hand"
    private int pageFaults;
    private int totalRequests;

    public ClockManager(int frameCount) {
        frames = new PageFrame[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = new PageFrame();
        }
        this.pointer = 0;
        this.pageFaults = 0;
        this.totalRequests = 0;
    }

    public void accessPage(int requestedPage) {
        totalRequests++;
        System.out.println("\nRequesting Page: " + requestedPage);

        // 1. Check for a HIT
        for (int i = 0; i < frames.length; i++) {
            if (frames[i].pageId == requestedPage) {
                frames[i].referenceBit = 1; // Set reference bit on hit
                System.out.println("Result: HIT");
                displayMemory();
                return;
            }
        }

        // 2. Handle a MISS (Page Fault)
        pageFaults++;
        System.out.println("Result: PAGE FAULT");

        while (true) {
            PageFrame currentFrame = frames[pointer];

            if (currentFrame.referenceBit == 0) {
                // Replace this page (or fill empty slot)
                currentFrame.pageId = requestedPage;
                currentFrame.referenceBit = 1;

                // Move hand forward after replacement
                pointer = (pointer + 1) % frames.length;
                break;
            } else {
                // Give a second chance
                currentFrame.referenceBit = 0;
                pointer = (pointer + 1) % frames.length;
            }
        }
        displayMemory();
    }

    public void displayMemory() {
        System.out.print("Current Frames: ");
        for (int i = 0; i < frames.length; i++) {
            String handIndicator = (i == pointer) ? " <-- Hand" : "";
            String page = (frames[i].pageId == -1) ? "Empty" : String.valueOf(frames[i].pageId);
            System.out.print("[" + page + " | Bit: " + frames[i].referenceBit + "]" + handIndicator + " ");
        }
        System.out.println();
    }

    public void printSummary() {
        System.out.println("\n--- Final Summary ---");
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Total Page Faults: " + pageFaults);
        double faultRate = ((double) pageFaults / totalRequests) * 100;
        System.out.printf("Fault Rate: %.2f%%\n", faultRate);
    }
}


//package LRU;
//import utils.InputHandler;
//public class LRUPageReplacement {
//
//    public static void main(String[] args) {
//        // Initialize components
//        DisplayManager display = new DisplayManager();
//        InputHandler input = new InputHandler();
//
//        // Display header
//        display.printHeader();
//
//        // Get user input
//        int frames = input.getFrameCount();
//        int[] pages = input.getPageReferenceString();
//
//        // Display page reference string
//        display.printPageReferenceString(pages);
//
//        // Initialize LRU cache
//        LRUCache lru = new LRUCache(frames);
//
//        // Start simulation
//        display.printSimulationStart();
//
//        // Process each page reference
//        for (int page : pages) {
//            lru.accessPage(page);
//        }
//
//        // Display statistics
//        lru.getStatistics().display();
//
//        // Cleanup
//        input.close();
//    }
//}
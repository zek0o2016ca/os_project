package Clock;

import java.util.InputMismatchException;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int frameCount = 0;
        int n = 0;

        // 1. Handle Frame Count Input
        while (true) {
            try {
                System.out.print("Enter number of frames (positive integer): ");
                frameCount = scanner.nextInt();
                if (frameCount <= 0) {
                    System.out.println("Error: Number of frames must be greater than 0.");
                    continue;
                }
                break; // Valid input, exit loop
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid integer.");
                scanner.next(); // Clear the invalid input
            }
        }

        ClockManager manager = new ClockManager(frameCount);

        // 2. Handle Reference String Length
        while (true) {
            try {
                System.out.print("Enter the number of pages in the reference string: ");
                n = scanner.nextInt();
                if (n <= 0) {
                    System.out.println("Error: Reference string length must be positive.");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid integer.");
                scanner.next();
            }
        }

        // 3. Handle Individual Page IDs with flexible formatting
        int[] referenceString = new int[n];
        scanner.nextLine(); // Clear the buffer

        while (true) {
            System.out.println("\nEnter exactly " + n + " page IDs (you can use spaces or commas):");
            String inputLine = scanner.nextLine().trim();

            // Split by any combination of commas and whitespace
            // Example: "7, 0, 1" or "7 0 1" or "7,0,1" or "7, 0 1" will all work!
            String[] parts = inputLine.split("[,\\s]+");

            if (parts.length != n) {
                System.out.println("Error: Mismatch detected!");
                System.out.println("Expected: " + n + " values. You provided: " + parts.length);
                continue;
            }

            try {
                boolean allValid = true;
                for (int i = 0; i < n; i++) {
                    // Trim each part just in case
                    int p = Integer.parseInt(parts[i].trim());
                    if (p < 0) {
                        System.out.println("Error: Page ID '" + p + "' is negative.");
                        allValid = false;
                        break;
                    }
                    referenceString[i] = p;
                }

                if (allValid) break; // Valid input, exit loop

            } catch (NumberFormatException e) {
                System.out.println("Error: One of your inputs ('" + inputLine + "') contains invalid characters.");
                System.out.println("Please use only numbers, spaces, or commas.");
            }
        }

        // Execute simulation
        for (int page : referenceString) {
            manager.accessPage(page);
        }

        manager.printSummary();
        scanner.close();
    }
}
//7 0 1 2 0 3 0 4 2 3

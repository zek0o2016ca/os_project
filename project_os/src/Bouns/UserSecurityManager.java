package Bouns;

import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * OS Bonus Task: User Inactivity Detection
 * Features: lastlog (HashMap), Idle Time tracking, and Audit logging (PrintWriter).
 */
public class UserSecurityManager {
    // Configuration
    private static final long IDLE_THRESHOLD_MS = 5000; // 5 seconds for testing
    private static final String AUDIT_FILE = "security_audit.log";

    // Session Data
    private static String currentUser = "";
    private static long loginTime;           // Tracks start of session
    private static long lastActivityTime;    // Resets on every action
    private static boolean performedAction = false;
    private static Map<String, String> lastLogMap = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- OS User Security Manager ---");

        // 1. Validate Username (Alphanumeric only)
        while (true) {
            System.out.print("Enter username: ");
            currentUser = scanner.nextLine().trim();
            if (currentUser.matches("^[a-zA-Z0-9]+$")) break;
            System.out.println("Error: Username must be alphanumeric (no symbols or spaces).");
        }

        performLogin(currentUser);

        System.out.println("\nCommands: 'act' (Reset Idle), 'check' (Status), 'exit' (Logout)");

        while (true) {
            try {
                System.out.print("\n[" + currentUser + " @ System]> ");
                String input = scanner.nextLine().toLowerCase().trim();

                long currentTime = System.currentTimeMillis();
                long idleGap = currentTime - lastActivityTime;

                // Process Commands
                if (input.equals("exit")) {
                    handleLogout(currentTime, idleGap);
                    break;
                } else if (input.equals("act")) {
                    // Check if they were idle BEFORE this action
                    if (idleGap > IDLE_THRESHOLD_MS) {
                        System.out.println("!!! SECURITY ALERT: You were idle for " + (idleGap / 1000) + "s before this action.");
                        logAudit("Idle Alert: " + currentUser + " was idle for " + (idleGap / 1000) + "s before acting.");
                    }
                    lastActivityTime = System.currentTimeMillis();
                    performedAction = true;
                    System.out.println("Action performed. Idle timer reset.");
                    logAudit("Action: Page access/command by " + currentUser);
                } else if (input.equals("check")) {
                    System.out.println("User: " + currentUser);
                    System.out.println("Last Login (lastlog): " + lastLogMap.get(currentUser));
                    System.out.println("Current Idle Gap: " + (idleGap / 1000) + "s");
                } else if (!input.isEmpty()) {
                    System.out.println("Unknown command.");
                }

            } catch (Exception e) {
                System.out.println("Input Error: System recovered.");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    private static void performLogin(String user) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        lastLogMap.put(user, timestamp);
        loginTime = System.currentTimeMillis();
        lastActivityTime = loginTime;

        System.out.println("Login recorded in lastlog at " + timestamp);
        logAudit("LOGIN: User '" + user + "' started session.");
    }

    private static void handleLogout(long currentTime, long idleGap) {
        long totalSession = currentTime - loginTime;

        // Logic for "Entire Session" vs "Late Gap"
        if (!performedAction && totalSession > IDLE_THRESHOLD_MS) {
            System.out.println("!!! SECURITY ALERT: Entire session (" + (totalSession / 1000) + "s) was idle.");
            logAudit("ABANDONED SESSION: " + currentUser + " was idle for the full " + (totalSession / 1000) + "s.");
        } else if (idleGap > IDLE_THRESHOLD_MS) {
            System.out.println("!!! SECURITY ALERT: Idle for " + (idleGap / 1000) + "s before logout.");
            logAudit("IDLE LOGOUT: " + currentUser + " was idle for " + (idleGap / 1000) + "s at end of session.");
        }

        System.out.println("Logging out... Total Session Duration: " + (totalSession / 1000) + "s");
        logAudit("LOGOUT: User '" + currentUser + "' exited. Total duration: " + (totalSession / 1000) + "s.");
    }

    private static void logAudit(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // Using PrintWriter for convenient Character Stream writing
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(AUDIT_FILE, true)))) {
            out.println("[" + timestamp + "] " + message);
        } catch (IOException e) {
            System.err.println("Audit writing failed.");
        }
    }
}

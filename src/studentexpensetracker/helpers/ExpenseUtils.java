/**
 * 
 */
package studentexpensetracker.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import studentexpensetracker.exceptions.InvalidChoiceException;
import studentexpensetracker.manager.ExpenseManager;

/**
 * Utility helper class for formatting and date operations.
 */
public final class ExpenseUtils {
	
	public static final String RESET = "\u001B[0m";
	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String YELLOW = "\u001B[33m";
	public static final String BLUE = "\u001B[34m";
	public static final String PURPLE = "\u001B[35m";
	public static final String CYAN = "\u001B[36m";
	public static final String WHITE = "\u001B[37m";
	public static final String BOLD = "\u001B[1m";
	public static final String MAGENTA = "\u001B[35m";
	
	public static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    public static String formatCurrency(double amount) {
        return String.format("€%.2f", amount);
    }
    
    public static void validateChoice(int choice, int min, int max) {
        if (choice < min || choice > max) {
            throw new InvalidChoiceException("Invalid choice. Please enter a number between " + min + " and " + max + ".");
        }
    }
    
    public static Map<String, String> loadUserData() {
        Map<String, String> userData = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("UserData.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                	// name,budget,month
                    String name = parts[0].trim();
                    String budget = parts[1].trim();
                    String month = (parts.length == 3) ? parts[2].trim() : java.time.LocalDate.now().getMonth().toString();

                    // Store combined value (budget + month)
                    userData.put(name, budget + "," + month);                    
                }
            }
        } catch (IOException e) {
        }
        return userData;
    }
    
    public static void saveUserData(Map<String, String> userData) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("UserData.txt"))) {
            for (Map.Entry<String, String> entry : userData.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }
    
    public static void updateUserData(String name, double budget, String month) {
    	Map<String, String> userData = loadUserData();
    	userData.put(name, budget + "," + month);
    	saveUserData(userData);
    }
    
    public static void printSpendingByCategory(ExpenseManager manager) {
        System.out.println("\nSpending by Category:");
        manager.getTotalByCategory().forEach((cat, total) ->
            System.out.println("- " + cat.name().replace("_", " & ") + ": " + formatCurrency(total))
        );
    }
    
    public static void printSpendingDistribution(ExpenseManager manager, double total) {

        System.out.println("\nSpending Distribution:");
        
        // Find the maximum percentage to scale bars properly
        double maxPercentage = manager.getTotalByCategory().values().stream()
                .mapToDouble(value -> (value / total) * 100)
                .max().orElse(100);

        final int MAX_BAR_LENGTH = 40;

        manager.getTotalByCategory().forEach((cat, value) -> {
            double percentage = (value / total) * 100;
            int barLength = (int) Math.max((percentage / maxPercentage) * MAX_BAR_LENGTH, 2);
            String bar = "\u2588".repeat(barLength);
            
            // Choose color based on category
            String color = switch (cat) {
                case FOOD -> YELLOW;
                case TRAVEL -> CYAN;
                case EDUCATION -> BLUE;
                case ENTERTAINMENT -> GREEN;
                case RENT_UTILITY -> RED;
                case MISCELLANEOUS -> MAGENTA;
            };

            System.out.printf("%-20s %6.2f%% | %s%s%s%n",
                    cat.name().replace("_", " & "),
                    percentage,
                    color,
                    bar,
                    RESET
                    );
        });
    }

    
    public static void writeSpendingByCategory(FileWriter writer, ExpenseManager manager) throws IOException {
        writer.write("Spending by Category:\n");
        manager.getTotalByCategory().forEach((cat, totalValue) -> {
            try {
                writer.write("- " + cat.name().replace("_", " & ")
                        + ": " + formatCurrency(totalValue) + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    public static void writeSpendingDistribution(FileWriter writer, ExpenseManager manager, double total) throws IOException {
        writer.write("\nSpending Distribution:\n");
        
        // Find the maximum percentage to scale bars properly
        double maxPercentage = manager.getTotalByCategory().values().stream()
                .mapToDouble(value -> (value / total) * 100)
                .max().orElse(100);

        final int MAX_BAR_LENGTH = 40;
        manager.getTotalByCategory().forEach((cat, value) -> {
            try {
                double percentage = (value / total) * 100;
                int barLength = (int) Math.max((percentage / maxPercentage) * MAX_BAR_LENGTH, 2);
                String bar = "\u2588".repeat(barLength);
                writer.write(String.format("%-20s %6.2f%% | %s%n",
                        cat.name().replace("_", " & "), percentage, bar));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    public static double getLastCumulativeTotal(String userName) {
        String fileName = userName + "_ExpenseReport.txt";
        File file = new File(fileName);
        if (!file.exists() || file.length() == 0) return 0.0;

        double lastTotal = 0.0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Cumulative Total (All Sessions): €")) {
                    String value = line.replace("Cumulative Total (All Sessions): €", "").trim();
                    try {
                        lastTotal = Double.parseDouble(value);
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading report file: " + e.getMessage());
        }
        return lastTotal;
    }
    
    public static void resetCumulativeTotal(String userName) {
        String reportFile = userName + "_ExpenseReport.txt";
        File file = new File(reportFile);

        if (file.exists()) {
            File backup = new File(userName + "_ExpenseReport_" + java.time.LocalDate.now().minusMonths(1).getMonth() + "_ARCHIVE.txt");
            file.renameTo(backup);
        }

        // Start a fresh report file for the new month
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("=== Monthly Expense Report for " + java.time.LocalDate.now().getMonth() +
                         " " + java.time.LocalDate.now().getYear() + " ===\n");
        } catch (IOException e) {
            System.err.println("Error resetting monthly report for " + userName + ": " + e.getMessage());
        }

        System.out.println(colorText("Starting fresh tracking for " + java.time.LocalDate.now().getMonth() + ".", GREEN));
    }
    
    public static String colorText(String text, String color) {
        return color + text + RESET;
    }
}
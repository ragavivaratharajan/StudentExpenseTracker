package studentexpensetracker.manager;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class BudgetHistoryManager {
	
	private static final String FILE_NAME = "BudgetHistory.txt";

    public static void logBudgetChange(String userName, double oldBudget, double newBudget, String reason) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            writer.write(String.format(
                "%s | User: %s | Old Budget: €%.2f | New Budget: €%.2f | Change: €%.2f | Reason: %s%n",
                LocalDateTime.now(), userName, oldBudget, newBudget, newBudget - oldBudget, reason
            ));
        } catch (IOException e) {
            System.err.println("Error logging budget change: " + e.getMessage());
        }
    }
}

/**
 * 
 */
package studentexpensetracker.manager;

import static studentexpensetracker.helpers.ExpenseUtils.*;

import java.time.LocalDateTime;

/**
 * Defines common calculation behavior that could be used for expense related operations.
 */
//Sealed interface - Restricting implementation to ExpenseManager
public sealed interface Calculatable permits ExpenseManager {
	
	double calculateTotal();
	
	default void printTotal() {
        logCalculationTime();
        System.out.println("Total Expenses: " + formatCurrency(calculateTotal()));
    }
	
	private void logCalculationTime() {
        System.out.println("[LOG] Total calculated at: " + LocalDateTime.now());
    }
	
	static String format(double value) {
        return String.format("%.2f", value);
    }
}

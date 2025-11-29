/**
 * 
 */
package studentexpensetracker.manager;

import java.time.Month;

import studentexpensetracker.domain.ExpenseCategory;

/**
 * Immutable record representing a monthly expense report.
 */
//@Record: immutable record for clean reporting.
public record ExpenseReport(Month month, double totalSpent, ExpenseCategory topCategory) {
	
	public double averageDaily() {
        return totalSpent / month.length(false);
    }
}

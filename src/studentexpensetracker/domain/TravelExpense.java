/**
 * 
 */
package studentexpensetracker.domain;

import static studentexpensetracker.helpers.ExpenseUtils.*;

/**
 * Represents travel related expenses sich as bus, taxi, train or private vehicle fares.
 */
public sealed class TravelExpense extends Expense permits BusExpense, TrainExpense {
	
    private String mode;

	public TravelExpense(double amount, String mode) {
		super(amount, ExpenseCategory.TRAVEL, "Travel Mode Type: " + mode);
		this.mode = mode;
	}

	@Override
	public String getLabel() {
		return "TRAVEL";
	}

	@Override
	public double calculateExpense() {
		return getAmount();
	}

	@Override
	public String getExpenseDetails() {
		return "Travel Mode: " + mode + " | Amount: " + formatCurrency(calculateExpense());
	}

	@Override
	public Boolean isRecurring() {
		return false;
	}
	
    public String getMode() { 
    	return mode; 
    }
}

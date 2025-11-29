/**
 * 
 */
package studentexpensetracker.domain;

import static studentexpensetracker.helpers.ExpenseUtils.*;

/**
 * Represents travel related expenses sich as bus, taxi, train or private vehicle fares.
 */
//@Inheritance: subclass extends the sealed Expense base class and base class permitting bus and train expense classes.
public sealed class TravelExpense extends Expense permits BusExpense, TrainExpense {
	
    private String mode;

	public TravelExpense(double amount, String mode) {
		super(amount, ExpenseCategory.TRAVEL, "Travel Mode Type: " + mode);
		this.mode = mode;
	}
	
	// @Method overriding: Different expense types return different labels.
	@Override
	public String getLabel() {
		return "TRAVEL";
	}
	
	// @Method overriding: Each expense type calculates expense differently
	@Override
	public double calculateExpense() {
		return getAmount();
	}

	// @Method overriding: Each expense type process expense details differently.
	@Override
	public String getExpenseDetails() {
		return "Travel Mode: " + mode + " | Amount: " + formatCurrency(calculateExpense());
	}
	
    // @Method overriding: Recurring payments are based on expense types.
	@Override
	public Boolean isRecurring() {
		return false;
	}
	
    public String getMode() { 
    	return mode; 
    }
}

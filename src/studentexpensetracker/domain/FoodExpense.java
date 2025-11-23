/**
 * 
 */
package studentexpensetracker.domain;

import static studentexpensetracker.helpers.ExpenseUtils.*;

/**
 * Represents food expenses such as breakfast, lunch, dinner, groceries etc. 
 */
public final class FoodExpense extends Expense {
	
	private String type;

	public FoodExpense(double amount, String type) {
		super(amount, ExpenseCategory.FOOD, "Food Expense Type: " + type);
		this.type = type;
	}

	@Override
	public String getLabel() {
		return "FOOD";
	}

	@Override
	public double calculateExpense() {
		return getAmount();
	}

	@Override
	public String getExpenseDetails() {
        return "Food (" + type + "): " + formatCurrency(calculateExpense());
	}

	@Override
	public Boolean isRecurring() {
		return false;
	}
	
	public String getType() {
	    return type;
	}
}

/**
 * 
 */
package studentexpensetracker.domain;

import static studentexpensetracker.helpers.ExpenseUtils.*;

/**
 * Represents food expenses such as breakfast, lunch, dinner, groceries etc. 
 */
//@Inheritance: subclass extends the sealed Expense base class
public final class FoodExpense extends Expense {
	
	private String type;

	public FoodExpense(double amount, String type) {
		// @super(): calls constructor of parent class Expense
		super(amount, ExpenseCategory.FOOD, "Food Expense Type: " + type);
		this.type = type;
	}
	
	// @Method overriding: each expense type has different label
	@Override
	public String getLabel() {
		return "FOOD";
	}
	
	// @Method overriding: each expense type calculates cost differently
	@Override
	public double calculateExpense() {
		return getAmount();
	}
	
	// @Method overriding
	@Override
	public String getExpenseDetails() {
        return "Food (" + type + "): " + formatCurrency(calculateExpense());
	}
	
	// @Method overriding: Recurring expenses differ for each expense type
	@Override
	public Boolean isRecurring() {
		return false;
	}
	
	public String getType() {
	    return type;
	}
}

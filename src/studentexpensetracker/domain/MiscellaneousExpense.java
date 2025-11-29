/**
 * 
 */
package studentexpensetracker.domain;

import static studentexpensetracker.helpers.ExpenseUtils.*;

/**
 * Represents miscellaneous expenses.
 */
//@Inheritance: subclass extends the sealed Expense base class
public non-sealed class MiscellaneousExpense extends Expense {
	
	private String type;

	public MiscellaneousExpense(double amount, String type, String description) {
        super(amount, ExpenseCategory.MISCELLANEOUS, description);
        this.type = type;
    }

	// @Method overriding: Different expense types return different labels.
	@Override
    public String getLabel() {
        return "MISCELLANEOUS";
    }

	// @Method overriding: each expense type calculates differently.
    @Override
    public double calculateExpense() {
        return getAmount();
    }
    
    // @Method overriding: Different methods process expense details differently.
    @Override
    public String getExpenseDetails() {
        return String.format("Miscellaneous (%s): %s | Note: %s",
                type,
                formatCurrency(calculateExpense()),
                // @super. accesses parent class method toString().
                super.toString());
    }

    // @Method overriding: Recurring payments are based on expense types.
    @Override
    public Boolean isRecurring() {
        return false;
    }
}

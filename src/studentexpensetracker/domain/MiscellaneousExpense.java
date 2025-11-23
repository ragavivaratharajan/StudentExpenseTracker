/**
 * 
 */
package studentexpensetracker.domain;

import static studentexpensetracker.helpers.ExpenseUtils.*;

/**
 * Represents miscellaneous expenses.
 */
public non-sealed class MiscellaneousExpense extends Expense {
	
	private String type;

	public MiscellaneousExpense(double amount, String type, String description) {
        super(amount, ExpenseCategory.MISCELLANEOUS, description);
        this.type = type;
    }

	@Override
    public String getLabel() {
        return "MISCELLANEOUS";
    }

    @Override
    public double calculateExpense() {
        return getAmount();
    }

    @Override
    public String getExpenseDetails() {
        return String.format("Miscellaneous (%s): %s | Note: %s",
                type,
                formatCurrency(calculateExpense()),
                super.toString());
    }

    @Override
    public Boolean isRecurring() {
        return false;
    }
}

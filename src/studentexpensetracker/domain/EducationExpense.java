/**
 * 
 */
package studentexpensetracker.domain;

import static studentexpensetracker.helpers.ExpenseUtils.*;

import studentexpensetracker.exceptions.InvalidExpenseException;

/**
 * Represents education related expenses like books, stationery, or purchase of online courses etc.
 */
public final class EducationExpense extends Expense {
	
	private String itemType;
	private int bwPages;
    private int colorPages;

	public EducationExpense(double amount, String itemType) {
		super(amount, ExpenseCategory.EDUCATION, "Education Expense Type: " + itemType);
		this.itemType = itemType;
	}
	
	public EducationExpense(String itemType, int bwPages, int colorPages) {
        super(0, ExpenseCategory.EDUCATION, "Printouts");
        this.itemType = itemType;
        this.bwPages = bwPages;
        this.colorPages = colorPages;
        
        if (bwPages < 0 || colorPages < 0) {
            throw new InvalidExpenseException("Number of pages cannot be negative.");
        }
    }

	@Override
	public String getLabel() {
		return "EDUCATION";
	}
    
	// Calculate the amout for printouts. For b&w 6 cents and color costs 10 cents
	@Override
    public double calculateExpense() {
		if (itemType.equalsIgnoreCase("printouts")) {
            double cost = (bwPages * 0.06) + (colorPages * 0.10);
            return cost;
        } else {
            return getAmount();
        }
    }
	
	@Override
    public String getExpenseDetails() {
        return "Education (" + itemType + "): " + formatCurrency(calculateExpense());
    }

	@Override
    public Boolean isRecurring() {
        return false;
    }

    public String getItemType() {
        return itemType;
    }
}

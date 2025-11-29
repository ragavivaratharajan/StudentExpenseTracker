/**
 * 
 */
package studentexpensetracker.domain;

import static studentexpensetracker.helpers.ExpenseUtils.*;

import java.time.LocalDate;

/**
 * Represents rent and utility related expenses such as electricity, water, bins, Wifi etc.
 */
//@Inheritance: subclass extends the sealed Expense base class
public final class RentAndUtilityExpense extends Expense {
	
	private String type;
    private LocalDate dueDate;
    private boolean recurring;

    public RentAndUtilityExpense(double amount, String type, LocalDate dueDate, boolean recurring) {
        super(amount, ExpenseCategory.RENT_UTILITY, "Home Expense Type: " + type);
        this.type = type;
        this.dueDate = dueDate;
        this.recurring = recurring;
    }

	// @Method overriding: Different expense types return different labels.
    @Override
    public String getLabel() {
        return "RENT/UTILITY";
    }
    
	// @Method overriding: each expense type calculates differently.
    @Override
    public double calculateExpense() {
    	// @Modern switch:returning computed values
        return switch (type.toLowerCase()) {
            case "electricity" -> getAmount() * 1.05;
            case "wifi" -> getAmount();
            case "water" -> getAmount() * 1.02;
            default -> getAmount();
        };
    }
    
    // @Method overriding: Different methods process expense details differently.
    @Override
    public String getExpenseDetails() {
        return String.format("Rent/Utility (%s): %s | Due: %s | Recurring: %s",
                type, formatCurrency(calculateExpense()), formatDate(dueDate), recurring ? "Yes" : "No");
    }
    
    // @Method overriding: Recurring payments are based on expense types.
    @Override
    public Boolean isRecurring() {
        return recurring;
    }

    public String getType() {
        return type;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
}

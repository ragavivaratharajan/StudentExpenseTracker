/**
 * 
 */
package studentexpensetracker.domain;

import static studentexpensetracker.helpers.ExpenseUtils.*;

import java.time.LocalDate;

/**
 * Represents rent and utility related expenses such as electricity, water, bins, Wifi etc.
 */
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

    @Override
    public String getLabel() {
        return "RENT/UTILITY";
    }

    @Override
    public double calculateExpense() {
        return switch (type.toLowerCase()) {
            case "electricity" -> getAmount() * 1.05;
            case "wifi" -> getAmount();
            case "water" -> getAmount() * 1.02;
            default -> getAmount();
        };
    }

    @Override
    public String getExpenseDetails() {
        return String.format("Rent/Utility (%s): %s | Due: %s | Recurring: %s",
                type, formatCurrency(calculateExpense()), formatDate(dueDate), recurring ? "Yes" : "No");
    }

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

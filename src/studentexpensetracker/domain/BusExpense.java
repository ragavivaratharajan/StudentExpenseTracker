/**
 * 
 */
package studentexpensetracker.domain;

import static studentexpensetracker.helpers.ExpenseUtils.*;

/**
 * Represents the bus fare and discounts if applicable.
 */
//@Inheritance: subclass extends the sealed TravelExpense base class
public final class BusExpense extends TravelExpense {
	
    private String hasStudentDiscountCard;

    public BusExpense(double amount, String hasStudentDiscountCard) {
        super(amount, "Bus");
        this.hasStudentDiscountCard = hasStudentDiscountCard;
    }
    
	// @Method overriding: each expense type calculates differently.
    @Override
    public double calculateExpense() {
        // Apply 50% discount if student has a valid discount card
        return hasStudentDiscountCard.trim().toLowerCase().matches("yes|y") ? getAmount() * 0.5 : getAmount();
    }
    
	// @Method overriding: each expense type returns different details based on logic.
    @Override
    public String getExpenseDetails() {
        return hasStudentDiscountCard.trim().toLowerCase().matches("yes|y") ? "Bus fare (Student discount - 50% off): " + formatCurrency(calculateExpense())
                : "Bus fare (standard): " + formatCurrency(calculateExpense());
    }
    
	// @Method overriding: each expense type has different label.
    @Override
    public String getLabel() {
        return "BUS";
    }
    
    // @Method overriding: recurring payments are different for different expense types.
    @Override
    public Boolean isRecurring() {
        return false;
    }

    public String hasStudentDiscount() {
        return hasStudentDiscountCard;
    }

}

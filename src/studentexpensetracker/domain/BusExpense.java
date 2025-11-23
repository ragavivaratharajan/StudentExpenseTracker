/**
 * 
 */
package studentexpensetracker.domain;

import static studentexpensetracker.helpers.ExpenseUtils.*;

/**
 * Represents the bus fare and discounts if applicable.
 */
public final class BusExpense extends TravelExpense {
	
    private String hasStudentDiscountCard;

    public BusExpense(double amount, String hasStudentDiscountCard) {
        super(amount, "Bus");
        this.hasStudentDiscountCard = hasStudentDiscountCard;
    }
    
    @Override
    public double calculateExpense() {
        // Apply 50% discount if student has a valid discount card
        return hasStudentDiscountCard.trim().toLowerCase().matches("yes|y") ? getAmount() * 0.5 : getAmount();
    }
    
    @Override
    public String getExpenseDetails() {
        return hasStudentDiscountCard.trim().toLowerCase().matches("yes|y") ? "Bus fare (Student discount - 50% off): " + formatCurrency(calculateExpense())
                : "Bus fare (standard): " + formatCurrency(calculateExpense());
    }

    @Override
    public String getLabel() {
        return "BUS";
    }
    
    @Override
    public Boolean isRecurring() {
        return false;
    }

    public String hasStudentDiscount() {
        return hasStudentDiscountCard;
    }

}

/**
 * 
 */
package studentexpensetracker.domain;

import static studentexpensetracker.helpers.ExpenseUtils.*;

/**
 * Represents the train fare based on the type of journey and discount if applicable.
 */
public final class TrainExpense extends TravelExpense {

	private String isLongJourney;
    private String hasStudentDiscountCard;

    public TrainExpense(double amount, String isLongJourney, String hasStudentDiscountCard) {
        super(amount, "Train");
        this.isLongJourney = isLongJourney;
        this.hasStudentDiscountCard = hasStudentDiscountCard;
    }
    
    @Override
    public double calculateExpense() {
        double total = getAmount();
        if (isLongJourney.trim().toLowerCase().matches("yes|y")) total *= 1.10; // 10% surcharge
        if (hasStudentDiscountCard.trim().toLowerCase().matches("yes|y")) total *= 0.5; // 50% student discount
        return total;
    }
    
    @Override
    public String getExpenseDetails() {
        String journeyType = isLongJourney.trim().toLowerCase().matches("yes|y") ? "Long journey (+10%)" : "Short journey";
        if (hasStudentDiscountCard.trim().toLowerCase().matches("yes|y")) {
            return "Train fare (" + journeyType + ", Student discount 50%): " + formatCurrency(calculateExpense());
        } else {
            return "Train fare (" + journeyType + "): " + formatCurrency(calculateExpense());
        }
    }
    
    @Override
    public String getLabel() {
        return "TRAIN";
    }
    
    @Override
    public Boolean isRecurring() {
        return false;
    }

    public boolean isLongJourney() {
        return isLongJourney.trim().toLowerCase().matches("yes|y");
    }

    public boolean hasStudentDiscount() {
        return hasStudentDiscountCard.trim().toLowerCase().matches("yes|y");
    }

}

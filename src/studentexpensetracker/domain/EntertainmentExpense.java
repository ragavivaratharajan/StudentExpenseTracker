/**
 * 
 */
package studentexpensetracker.domain;

import static studentexpensetracker.helpers.ExpenseUtils.*;

/**
 * Represents entertainment related expenses like movies or streaming subscriptions, concerts, events etc.
 */
public final class EntertainmentExpense extends Expense{
	
	private String type;

	public EntertainmentExpense(double amount, String type) {
        super(amount, ExpenseCategory.ENTERTAINMENT, "Entertainment Expense Type: " + type);
        this.type = type;
    }

	@Override
    public String getLabel() {
        return "ENTERTAINMENT";
    }

    @Override
    public double calculateExpense() {
        double cost = getAmount();

    	// Add a 10% tax for events or concerts.
        if (type.equalsIgnoreCase("concert") || type.equalsIgnoreCase("event")) {
        	cost *= 1.10;
        }
        
        return cost;
    }

    @Override
    public String getExpenseDetails() {
        return "Entertainment (" + type + "): " + formatCurrency(calculateExpense());
    }

    @Override
    public Boolean isRecurring() {
        // Streaming subscriptions could be recurring
        return type.equalsIgnoreCase("subscription");
    }

    public String getType() {
        return type;
    }
}

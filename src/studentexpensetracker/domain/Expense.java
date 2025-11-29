/**
 * 
 */
package studentexpensetracker.domain;

import static studentexpensetracker.helpers.ExpenseUtils.*;

import java.time.LocalDate;

import studentexpensetracker.exceptions.InvalidExpenseException;


/**
 * Base class for all the Expense categories.
 */
// @Sealed class: restricts which subclasses can extend this class 
public sealed abstract class Expense permits EducationExpense, EntertainmentExpense, FoodExpense,
        RentAndUtilityExpense, TravelExpense, MiscellaneousExpense {
	
	// @Encapsulation: private fields with public getter methods.
	private static int counter = 0;
	private final int id;
	private final double amount;
	private final LocalDate date;
	private final ExpenseCategory expenseCategory;
	private final String description;
	
	public Expense(double amount, ExpenseCategory expenseCategory, String description) {
		if (amount < 0) {
            throw new InvalidExpenseException("Expense amount must be positive.");
        }
        if (expenseCategory == null) {
            throw new InvalidExpenseException("Expense category cannot be null.");
        }
        if (description == null || description.isBlank()) {
            throw new InvalidExpenseException("Expense description cannot be empty.");
        }
        
		this.id = ++counter;
		this.amount = amount;
		this.expenseCategory = expenseCategory;
		this.date = LocalDate.now();
		this.description = description;	
	}
	
	// @abstract methods: implemented in sub classes
	public abstract String getLabel();
	
	public abstract double calculateExpense();
	
	public abstract String getExpenseDetails();
	
	public abstract Boolean isRecurring();
	
	public double getAmount( ) {
		return amount;
	}
	
	public ExpenseCategory getCategory() {
		return expenseCategory;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public String toString() {
		return String.format("[%d] %s %s (%s) on %s", id, expenseCategory, formatCurrency(amount), description, formatDate(date));
	}	
}

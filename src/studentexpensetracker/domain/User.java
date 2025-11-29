/**
 * 
 */
package studentexpensetracker.domain;

import static studentexpensetracker.helpers.ExpenseUtils.*;

import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import studentexpensetracker.exceptions.InvalidExpenseException;

/**
 * Represents a personal user of the expense tracker.
 */
public class User {
	
	// @Encapsulation: private fields with public getter and setter methods.
	private String name;
    private double budget;
    private List<Expense> expenses;
    private double exceededAmount = 0.0;
    private Month budgetMonth;
    
    // @Method overloading: same method name (constructor) with different parameters.
    public User(String name) {
    	// @this(): calling another constructor User(name, budget)
        this(name, 0.0);
    }

    // Constructor with budget
    public User(String name, double budget) {
    	// @this.: refers to the current object's fields
        this.name = name;
        this.budget = budget;
        this.expenses = new ArrayList<>();
    }
    
    // @Varargs: passing multiple Expense objects in a single method call
    public void addExpense(Expense... expenseArray) {
        if (expenseArray == null || expenseArray.length == 0) {
            throw new InvalidExpenseException("No expenses provided to add.");
        }

        for (Expense e : expenseArray) {
            if (e == null) {
                throw new InvalidExpenseException("Attempted to add a null expense.");
            }

            if (expenses.contains(e)) {
                throw new InvalidExpenseException("Duplicate expense detected: " + e);
            }

            if (e.getAmount() < 0) {
                throw new InvalidExpenseException("Expense amount must be positive: " + e);
            }
        }

        Collections.addAll(expenses, expenseArray);
    }
    
    // @Defensive copying: prevents external code from modifying private field expense.
    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses);
    }

    public double calculateTotal() {
        return expenses.stream()
                       .mapToDouble(Expense::calculateExpense)
                       .sum();
    }

    public double getRemainingBudget() {
        return budget - calculateTotal();
    }

    public String getName() {
        return name;
    }

    public double getBudget() {
        return budget;
    }
    
    public void setBudget(double budget) {
        this.budget = budget;
    }
    
    public void showSummary() {
        System.out.println("------------------------------------------------");
        System.out.println("Expense Summary for " + name);
        System.out.println("------------------------------------------------");
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded yet.");
        } else {
            for (Expense e : expenses) {
                System.out.println("• " + e.getExpenseDetails());
            }
        }
        System.out.println("------------------------------------------------");
        System.out.printf("Total Spent: €%s%n", formatCurrency(calculateTotal()));
        System.out.printf("Remaining Budget: €%s%n", formatCurrency(getRemainingBudget()));
        System.out.println("------------------------------------------------\n");
    }

    @Override
    public String toString() {
        return String.format("%s | Budget: €%s | Remaining: €%s",
                name, formatCurrency(budget), formatCurrency(getRemainingBudget()));
    }
    
    public double getExceededAmount() {
        return exceededAmount;
    }
    
    public void setExceededAmount(double exceededAmount) {
        this.exceededAmount = exceededAmount;
    }
    
    public Month getBudgetMonth() {
        return budgetMonth;
    }

    public void setBudgetMonth(Month budgetMonth) {
        this.budgetMonth = budgetMonth;
    }
}

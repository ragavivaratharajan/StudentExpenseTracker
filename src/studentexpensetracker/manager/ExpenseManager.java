/**
 * 
 */
package studentexpensetracker.manager;


import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import studentexpensetracker.domain.Expense;
import studentexpensetracker.domain.ExpenseCategory;
import studentexpensetracker.domain.User;
import studentexpensetracker.exceptions.BudgetExceededException;
import studentexpensetracker.helpers.ExpenseUtils;

/**
 * Handles management and operations on expenses.
 */

// @Implements interface Calculatable
public class ExpenseManager implements Calculatable {
	
    private List<Expense> expenses;
    
    public ExpenseManager() {
    	// @ArrayList: to store dynamic data
        this.expenses = new ArrayList<>();
    }
    
    // @call-by-value: copy of reference user
    public void addExpense(User user, Expense expense, Scanner sc) throws BudgetExceededException {
        // Calculate what the total would be if we add this expense
        double previousCumulativeTotal = ExpenseUtils.getLastCumulativeTotal(user.getName());
        double projectedTotal = previousCumulativeTotal 
                              + user.calculateTotal() 
                              + expense.calculateExpense();

        if (projectedTotal > user.getBudget()) {
            double exceededBy = projectedTotal - user.getBudget();
            user.setExceededAmount(exceededBy);

            System.err.println("\nBudget exceeded for user: " + user.getName());
            System.err.println("Attempted Total: €" + String.format("%.2f", projectedTotal));
            System.err.println("Budget: €" + String.format("%.2f", user.getBudget()));
            System.err.println("Exceeded by: €" + String.format("%.2f", exceededBy));

            System.out.print("Would you like to increase your budget? (yes/no): ");
            String choice = sc.nextLine().trim().toLowerCase();

            if (choice.equals("yes")) {
                System.out.print("Enter additional budget amount (€): ");
                try {
                	double extra = sc.nextDouble();
                	sc.nextLine();

                	double oldBudget = user.getBudget();
                	user.setBudget(oldBudget + extra);
                	System.out.println("Budget successfully updated to €" + String.format("%.2f", user.getBudget()));

                	// Save updated budget persistently
                	Map<String, String> userData = ExpenseUtils.loadUserData();
                	String updatedValue = user.getBudget() + "," + java.time.LocalDate.now().getMonth();
                	userData.put(user.getName(), updatedValue);
                	ExpenseUtils.saveUserData(userData);
                	System.out.println("User data updated successfully.");

                	// Log the change
                	BudgetHistoryManager.logBudgetChange(user.getName(), oldBudget, user.getBudget(), "Exceeded limit - manual increase");

                    // Re check after increasing the budget
                    if (projectedTotal > user.getBudget()) {
                        throw new BudgetExceededException("Even after increase, budget still exceeded by €"
                                + String.format("%.2f", projectedTotal - user.getBudget()));
                    }
                // @JAVA 22 FEATURE: Unnamed variable
                } catch (InputMismatchException _) {
                    sc.nextLine();
                    throw new BudgetExceededException("Invalid input for budget increase.");
                }
            } else {
                throw new BudgetExceededException("Expense not added — budget exceeded by €"
                        + String.format("%.2f", exceededBy));
            }
        }

        // Only add the expense after all checks are passed
        expenses.add(expense);
        user.getExpenses().add(expense);
    }


    
    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }
    
    // @Lambda expression + Predicate used for filtering expenses
    public List<Expense> filterExpenses(Predicate<Expense> condition) {
        return expenses.stream().filter(condition).toList();
    }

	@Override
	public double calculateTotal() {
		// @Method reference (Expense::calculateExpense)
        return expenses.stream().mapToDouble(Expense::calculateExpense).sum();
	}
	
	public Map<ExpenseCategory, Double> getTotalByCategory() {
	    return expenses.stream()
	            .collect(Collectors.groupingBy(
	                    Expense::getCategory,
	                    Collectors.summingDouble(Expense::calculateExpense)
	            ));
	}
	
	public ExpenseReport generateReport() {
        var total = calculateTotal();
        Map<ExpenseCategory, Double> grouped = getTotalByCategory();
        ExpenseCategory topCategory = grouped.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return new ExpenseReport(Month.from(LocalDate.now()), total, topCategory);
    }
	
	public void displayAll() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded yet.");
        } else {
        	// @lambda: used to print each expense in the expenses list
        	java.util.function.Consumer<Expense> printer =
        	        exp -> System.out.println("• " + exp.getExpenseDetails());

        	expenses.forEach(printer);
        }
    }
	
	public List<Expense> getExpensesSortedByAmount() {
	    return expenses.stream()
	            .sorted(java.util.Comparator.comparing(Expense::calculateExpense))
	            .toList();
	}
	
	public Map<Boolean, List<Expense>> partitionRecurringExpenses() {
	    return expenses.stream()
	            .collect(Collectors.partitioningBy(Expense::isRecurring));
	}
	
	public List<Expense> getTopThreeExpenses() {
	    return expenses.stream()
	            .sorted(java.util.Comparator.comparing(Expense::calculateExpense).reversed())
	            .limit(Math.min(3, expenses.size()))
	            .toList();
	}
	
	public List<ExpenseCategory> getDistinctCategoriesUsed() {
	    return expenses.stream()
	            .map(Expense::getCategory)
	            .distinct()
	            .sorted()
	            .toList();
	}
	
	public void showExpenseAnalytics() {

	    System.out.println("\nExpense Analytics:");

	    expenses.stream()
	            .min(java.util.Comparator.comparing(Expense::calculateExpense))
	            .ifPresent(e -> System.out.println("Cheapest expense: " + e.getExpenseDetails()));

	    expenses.stream()
	            .max(java.util.Comparator.comparing(Expense::calculateExpense))
	            .ifPresent(e -> System.out.println("Most expensive expense: " + e.getExpenseDetails()));

	    long count = expenses.stream().count();
	    System.out.println("Total number of expenses: " + count);

	    expenses.stream()
	            .findFirst()
	            .ifPresent(e -> System.out.println("First expense entered: " + e.getExpenseDetails()));

	    boolean hasRecurring = expenses.stream()
	            .anyMatch(Expense::isRecurring);

	    System.out.println("Contains recurring expenses? " + hasRecurring);

	    boolean noneRecurring = expenses.stream()
	            .noneMatch(Expense::isRecurring);

	    System.out.println("No recurring expenses present? " + noneRecurring);
	    
	    expenses.stream()
        .filter(Expense::isRecurring)
        .findAny()
        .ifPresent(exp -> System.out.println(
                "Example recurring expense found: " + exp.getExpenseDetails()
        ));
	    
	    boolean allPositive = expenses.stream()
	            .allMatch(e -> e.calculateExpense() > 0);

	    System.out.println("All expenses are positive? " + allPositive);
	}
}

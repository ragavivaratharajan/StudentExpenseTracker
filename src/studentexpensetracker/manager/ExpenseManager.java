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
public class ExpenseManager implements Calculatable {
	
    private List<Expense> expenses;
    
    public ExpenseManager() {
        this.expenses = new ArrayList<>();
    }
    
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
                    user.setBudget(user.getBudget() + extra);
                    System.out.println("Budget successfully updated to €" + String.format("%.2f", user.getBudget()));
                    
                    // Update the budget in UserData.txt
                    Map<String, Double> userData = ExpenseUtils.loadUserData();
                    userData.put(user.getName(), user.getBudget());
                    ExpenseUtils.saveUserData(userData);
                    System.out.println("User data updated successfully.");
                    
                    double oldBudget = user.getBudget();
                    user.setBudget(user.getBudget() + extra);
                    BudgetHistoryManager.logBudgetChange(user.getName(), oldBudget, user.getBudget(), "Exceeded limit - manual increase");

                    // Re check after increasing the budget
                    if (projectedTotal > user.getBudget()) {
                        throw new BudgetExceededException("Even after increase, budget still exceeded by €"
                                + String.format("%.2f", projectedTotal - user.getBudget()));
                    }
                } catch (InputMismatchException e) {
                    sc.nextLine();
                    throw new BudgetExceededException("Invalid input for budget increase.");
                }
            } else {
                throw new BudgetExceededException("Expense not added — budget exceeded by €"
                        + String.format("%.2f", exceededBy));
            }
        }

        // Only add after all checks are passed
        expenses.add(expense);
        user.getExpenses().add(expense);
        System.out.println("Expense added successfully!");
    }


    
    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }
    
    public List<Expense> filterExpenses(Predicate<Expense> condition) {
        return expenses.stream().filter(condition).toList();
    }

	@Override
	public double calculateTotal() {
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
            expenses.forEach(e -> System.out.println("• " + e.getExpenseDetails()));
        }
    }
}

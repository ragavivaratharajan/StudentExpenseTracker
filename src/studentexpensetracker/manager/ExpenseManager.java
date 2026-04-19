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
import java.util.stream.Gatherers;

import studentexpensetracker.app.ExpenseMain;
import studentexpensetracker.domain.Expense;
import studentexpensetracker.domain.ExpenseCategory;
import studentexpensetracker.exceptions.BudgetExceededException;
import studentexpensetracker.helpers.ExpenseUtils;

/**
 * Handles management and operations on expenses.
 */

// @Implements interface Calculatable
public final class ExpenseManager implements Calculatable {
	
    private List<Expense> expenses;
    
    public ExpenseManager() {
        this.expenses = new ArrayList<>();
    }
    
    public void addExpense(Expense expense, Scanner sc) throws BudgetExceededException {
        // Calculate what the total would be if we add this expense
    	double previousCumulativeTotal = ExpenseUtils.getLastCumulativeTotal(ExpenseMain.CURRENT_USER.get().getName());
        double projectedTotal = previousCumulativeTotal 
                              + ExpenseMain.CURRENT_USER.get().calculateTotal() 
                              + expense.calculateExpense();

        if (projectedTotal > ExpenseMain.CURRENT_USER.get().getBudget()) {
            double exceededBy = projectedTotal - ExpenseMain.CURRENT_USER.get().getBudget();
            ExpenseMain.CURRENT_USER.get().setExceededAmount(exceededBy);

            System.err.println("\nBudget exceeded for user: " + ExpenseMain.CURRENT_USER.get().getName());
            System.err.println("Attempted Total: €" + String.format("%.2f", projectedTotal));
            System.err.println("Budget: €" + String.format("%.2f", ExpenseMain.CURRENT_USER.get().getBudget()));
            System.err.println("Exceeded by: €" + String.format("%.2f", exceededBy));

            System.out.print("Would you like to increase your budget? (yes/no): ");
            String choice = sc.nextLine().trim().toLowerCase();

            if (choice.equals("yes")) {
                System.out.print("Enter additional budget amount (€): ");
                try {
                	double extra = sc.nextDouble();
                	sc.nextLine();

                	double oldBudget = ExpenseMain.CURRENT_USER.get().getBudget();
                	ExpenseMain.CURRENT_USER.get().setBudget(oldBudget + extra);
                	System.out.println("Budget successfully updated to €" + String.format("%.2f", ExpenseMain.CURRENT_USER.get().getBudget()));

                	// Save updated budget persistently
                	Map<String, String> userData = ExpenseUtils.loadUserData();
                	String updatedValue = ExpenseMain.CURRENT_USER.get().getBudget() + "," + java.time.LocalDate.now().getMonth();
                	userData.put(ExpenseMain.CURRENT_USER.get().getName(), updatedValue);
                	ExpenseUtils.saveUserData(userData);
                	System.out.println("User data updated successfully.");

                	// Log the change
                	BudgetHistoryManager.logBudgetChange(ExpenseMain.CURRENT_USER.get().getName(), oldBudget,ExpenseMain.CURRENT_USER.get().getBudget(), "Exceeded limit - manual increase");

                    // Re check after increasing the budget
                    if (projectedTotal > ExpenseMain.CURRENT_USER.get().getBudget()) {
                        throw new BudgetExceededException("Even after increase, budget still exceeded by €"
                                + String.format("%.2f", projectedTotal - ExpenseMain.CURRENT_USER.get().getBudget()));
                    }
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
        ExpenseMain.CURRENT_USER.get().getExpenses().add(expense);
    }


    
    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }
    
    // @Lambda expression: Predicate used for filtering expenses
    public List<Expense> filterExpenses(Predicate<Expense> condition) {
        return expenses.stream().filter(condition).toList();
    }

	@Override
	public double calculateTotal() {
		// @Method reference (Expense::calculateExpense)
        return expenses.stream().mapToDouble(Expense::calculateExpense).sum();
	}
	
	public Map<ExpenseCategory, Double> getTotalByCategory() {
		// collect() - Collectors.groupingBy()
	    return expenses.stream()
	            .collect(Collectors.groupingBy(
	                    Expense::getCategory,
	                    Collectors.summingDouble(Expense::calculateExpense)
	            ));
	}
	
	public ExpenseReport generateReport() {
		
		// Concurrency using ExecutorService to process Callable tasks
	    java.util.concurrent.ExecutorService executor =
	            java.util.concurrent.Executors.newFixedThreadPool(2);

	    try {

	        java.util.concurrent.Callable<Double> totalTask =
	                this::calculateTotal;

	        java.util.concurrent.Callable<ExpenseCategory> topCategoryTask =
	                () -> getTotalByCategory().entrySet().stream()
	                        .max(Map.Entry.comparingByValue())
	                        .map(Map.Entry::getKey)
	                        .orElse(null);

	        java.util.concurrent.Future<Double> totalFuture =
	                executor.submit(totalTask);

	        java.util.concurrent.Future<ExpenseCategory> categoryFuture =
	                executor.submit(topCategoryTask);

	        double total = totalFuture.get();

	        ExpenseCategory topCategory = categoryFuture.get();

	        return new ExpenseReport(
	                Month.from(LocalDate.now()),
	                total,
	                topCategory
	        );

	    } catch (Exception e) {

	        throw new RuntimeException("Error generating report concurrently", e);

	    } finally {

	        executor.shutdown();
	    }
	}
	
	public void displayAll() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded yet.");
        } else {
        	// @Lamba Expression : Consumer
        	java.util.function.Consumer<Expense> printer =
        	        exp -> System.out.println("• " + exp.getExpenseDetails());

        	expenses.forEach(printer);
        }
    }
	
	public List<Expense> getExpensesSortedByAmount() {
		// Sorting using Comparator.comparing()
	    return expenses.stream()
	            .sorted(java.util.Comparator.comparing(Expense::calculateExpense))
	            .toList();
	}
	
	public Map<Boolean, List<Expense>> partitionRecurringExpenses() {
		// collect() - Collectors.partitioningBy()
	    return expenses.stream()
	            .collect(Collectors.partitioningBy(Expense::isRecurring));
	}
	
	public List<Expense> getTopExpenses(int limit) {

	    return expenses.stream()

	            .sorted(java.util.Comparator
	                    .comparing(Expense::calculateExpense)
	                    .reversed())
	             // @Java 25 Stream Gatherers (Gatherers.scan)
	            .gather(Gatherers.scan(
	                    java.util.ArrayList<Expense>::new,
	                    (list, exp) -> {
	                        if (list.size() < limit) {
	                            list.add(exp);
	                        }
	                        return list;
	                    }
	            ))

	            .reduce((_, second) -> second)

	            .orElse(new java.util.ArrayList<>());
	}
	
	public List<ExpenseCategory> getDistinctCategoriesUsed() {
		// @Stream : Terminal operations
	    return expenses.stream()
	            .map(Expense::getCategory)
	            .distinct()
	            .sorted()
	            .toList();
	}
	
	public void showExpenseAnalytics() {

	    System.out.println("\nExpense Analytics:");
	    
	    // Streams terminal operations (min, max, count, anyMatch, etc.)
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

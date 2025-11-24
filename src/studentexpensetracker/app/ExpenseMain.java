/**
 * 
 */
package studentexpensetracker.app;
import studentexpensetracker.domain.*;
import studentexpensetracker.exceptions.BudgetExceededException;
import studentexpensetracker.helpers.ExpenseUtils;
import studentexpensetracker.manager.*;
import static studentexpensetracker.helpers.ExpenseUtils.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Scanner;

/**
 * 
 */
public class ExpenseMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        ExpenseManager manager = new ExpenseManager();
        Scanner sc = new Scanner(System.in);
        
        System.out.println("===========================================");
        System.out.println(colorText("      Student Expense Tracker App", BLUE));
        System.out.println("===========================================");
        
        Map<String, Double> userData = ExpenseUtils.loadUserData();
       
        System.out.print("Enter your name: ");
        String name = sc.nextLine();
        
        double budget;
        
        // If user already exists, fetch existing budget.
        if (userData.containsKey(name)) {
            budget = userData.get(name);
            System.out.println(colorText("Welcome back, " + name + "! Your saved monthly budget is €" + budget, PURPLE));
        // For new users, input the budget and save the user data to a text file
        } else {
            System.out.print("Enter your monthly budget (€): ");
            budget = sc.nextDouble();
            sc.nextLine();
            userData.put(name, budget);
            ExpenseUtils.saveUserData(userData);
            System.out.println(colorText("User saved successfully!", GREEN));
        }
        
        User user = new User(name, budget);
        
        // Set the budget month
        user.setBudgetMonth(java.time.LocalDate.now().getMonth());
        
        boolean budgeting = true;
        
        // Menu display for different expense cattegories and summary
        while (budgeting) {
            System.out.println("\n-------------------------------------------");
            System.out.println("Choose an expense category:");
            System.out.println(colorText("1. Food", BLUE));
            System.out.println(colorText("2. Travel", BLUE));
            System.out.println(colorText("3. Education", BLUE));
            System.out.println(colorText("4. Entertainment", BLUE));
            System.out.println(colorText("5. Rent & Utilities", BLUE));
            System.out.println(colorText("6. Miscellaneous", BLUE));
            System.out.println(colorText("7. Show summary & exit", BLUE));
            System.out.print("\nEnter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter amount (€): ");
                    double amt = sc.nextDouble(); sc.nextLine();
                    System.out.print("Enter meal type (Breakfast/Lunch/Dinner/Groceries): ");
                    String mealType = sc.nextLine();
                    
                    try {
                        manager.addExpense(user, new FoodExpense(amt, mealType), sc);
                        System.out.println(colorText("Food expense added!", GREEN));
                    } catch (BudgetExceededException e) {
                        System.err.println(e.getMessage());
                    }  
                }

                case 2 -> {
                    System.out.println("Choose travel mode:" );
                    System.out.println(colorText("1. Bus (Student discount eligible)", BLUE));
                    System.out.println(colorText("2. Train (Student discount eligible)", BLUE));
                    System.out.println(colorText("3. Private Transport (No discounts)", BLUE));
                    int mode = sc.nextInt(); sc.nextLine();
                    ExpenseUtils.validateChoice(mode, 1, 3);
                    System.out.print("Enter amount (€): ");
                    double amt = sc.nextDouble(); sc.nextLine();
                    if (mode == 1 || mode == 2) {
                    	System.out.print("Do you have a student discount/leap card? (yes/no): ");
                        String hasCard = sc.next(); sc.nextLine();
                        
                        if (mode == 1)
                        	try {
                                manager.addExpense(user, new BusExpense(amt, hasCard), sc);
                            } catch (BudgetExceededException e) {
                                System.err.println(e.getMessage());
                            }  
                        
                        else {
                        	System.out.println("Is it a long journey? Surcharges might apply for long journeys (yes/no): ");
                        	String islongjourney = sc.next(); sc.nextLine();
                        	try {
                                manager.addExpense(user, new TrainExpense(amt, islongjourney, hasCard), sc); 	
                            } catch (BudgetExceededException e) {
                                System.err.println(e.getMessage());
                            }   
                        }
                    }
                    
                    else
                    	try {
                        	manager.addExpense(user, new TravelExpense(amt, "Private Transport"), sc);
                        } catch (BudgetExceededException e) {
                            System.err.println(e.getMessage());
                        }   
                    System.out.println(colorText("Travel expense added!", GREEN));
                }

                case 3 -> {
                    System.out.println("Choose education item type:");
                    System.out.println(colorText("1. Books", BLUE));
                    System.out.println(colorText("2. Online Course purchase ", BLUE));
                    System.out.println(colorText("3. Stationery", BLUE));
                    System.out.println(colorText("4. Printouts (only on campus)", BLUE));
                    System.out.print("Enter choice: ");
                    int eduChoice = sc.nextInt();
                    sc.nextLine();

                    switch (eduChoice) {
                        case 1, 2, 3 -> {
                            System.out.print("Enter amount (€): ");
                            double amt = sc.nextDouble();
                            sc.nextLine();
                            String type = (eduChoice == 1) ? "Books"
                                    : (eduChoice == 2) ? "Tuition"
                                    : "Stationery";
                            
                            try {
                            	manager.addExpense(user, new EducationExpense(amt, type), sc);
                            	System.out.println(colorText("Education expense added!", GREEN));
                            } catch (BudgetExceededException e) {
                                System.err.println(e.getMessage());
                            }                            
                        }

                        case 4 -> {
                            System.out.print("Enter number of black & white pages: ");
                            int bw = sc.nextInt();
                            System.out.print("Enter number of colour pages: ");
                            int color = sc.nextInt();
                            sc.nextLine();
                            
                            try {
                            	manager.addExpense(user, new EducationExpense("Printouts", bw, color), sc);
                            	System.out.println(colorText("Printout expense added!", GREEN));
                            } catch (BudgetExceededException e) {
                                System.err.println(e.getMessage());
                            }    
                        }

                        default -> ExpenseUtils.validateChoice(eduChoice, 1, 4);

                    }
                }

                case 4 -> {
                    System.out.print("Enter amount (€): ");
                    double amt = sc.nextDouble(); sc.nextLine();
                    System.out.print("Enter activity type (Subscription/Movie/Event/TUSTrips): ");
                    String act = sc.nextLine();
                    
                    try {
                        manager.addExpense(user, new EntertainmentExpense(amt, act), sc);
                        System.out.println(colorText("Entertainment expense added!", GREEN));
                    } catch (BudgetExceededException e) {
                        System.err.println(e.getMessage());
                    }                      
                }

                case 5 -> {
                    System.out.print("Enter amount (€): ");
                    double amt = sc.nextDouble(); sc.nextLine();
                    System.out.print("Enter type (Electricity/Internet/Water/Rent): ");
                    String type = sc.nextLine();
                    System.out.print("Enter due date (YYYY-MM-DD): ");
                    LocalDate dueDate = LocalDate.parse(sc.nextLine());
                    System.out.print("Is this recurring? (true/false): ");
                    boolean recurring = sc.nextBoolean(); sc.nextLine();
                    
                    try {
                    	manager.addExpense(user, new RentAndUtilityExpense(amt, type, dueDate, recurring), sc);
                        System.out.println(colorText("Rent/Utility expense added!", GREEN));
                    } catch (BudgetExceededException e) {
                        System.err.println(e.getMessage());
                    }                    
                }

                case 6 -> {
                    System.out.print("Enter amount (€): ");
                    double amt = sc.nextDouble(); sc.nextLine();
                    System.out.print("Enter type (Gift/Trip/Donation/Other): ");
                    String type = sc.nextLine();
                    System.out.print("Add a short note/description: ");
                    String desc = sc.nextLine();
                    
                    try {
                        manager.addExpense(user, new MiscellaneousExpense(amt, type, desc), sc);
                        System.out.println(colorText("Miscellaneous expense added!", GREEN));
                    } catch (BudgetExceededException e) {
                        System.err.println(e.getMessage());
                    }
                }

                case 7 -> {
                    budgeting = false;
                    System.out.println("\n-------------------------------------------");
                    System.out.println("Generating report for " + name + "...");
                    ExpenseReport report = manager.generateReport();
                    showSummary(user, manager, report);
                }

                default -> ExpenseUtils.validateChoice(choice, 1, 7);
            }
        }
        
        sc.close();
    }
	
	private static void showSummary(User user, ExpenseManager manager, ExpenseReport report) {
	    System.out.println("\n===========================================");
	    System.out.println(colorText("             Expense Summary", MAGENTA));
	    System.out.println("===========================================");
	    manager.displayAll();
	    
	    System.out.println("\nBudget for " + user.getBudgetMonth() + ": " + colorText(formatCurrency(user.getBudget()), GREEN));

	    System.out.println("\nTotal Spent: " + colorText(formatCurrency(report.totalSpent()), RED));
	    printSpendingByCategory(manager);
	    System.out.println("\nTop Category: " +
	            report.topCategory().name().replace("_", " & ").toLowerCase());
	    System.out.println("-------------------------------------------");
	    printSpendingDistribution(manager, report.totalSpent());

	    String fileName = user.getName() + "_ExpenseReport.txt";
	    File file = new File(fileName);
	    boolean fileExistsAndHasData = file.exists() && file.length() > 0;

	    try (FileWriter writer = new FileWriter(fileName, true)) {

	        StringBuilder reportBuilder = new StringBuilder();

	        // Report Header
	        if (fileExistsAndHasData) {
	            reportBuilder.append("\n=== New Session Report (")
	                         .append(java.time.LocalDate.now())
	                         .append(") ===\n");
	        } else {
	        	reportBuilder.append("=== Monthly Expense Report for ")
	            .append(user.getBudgetMonth())
	            .append(" ")
	            .append(java.time.LocalDate.now().getYear())
	            .append(" for " + user.getName() + " ")
	            .append(" ===\n")
	            .append("Total Budget: ").append(formatCurrency(user.getBudget())).append("\n\n");
	        }
	        
	        // Update current budget
	        reportBuilder.append("Current Budget: ").append(formatCurrency(user.getBudget())).append("\n");

	        // Summary Section
	        reportBuilder.append("Total Spent: ").append(formatCurrency(report.totalSpent())).append("\n")
            .append("Top Category: ").append(report.topCategory()).append("\n");
	        
	        if (user.getExceededAmount() > 0) {
	            writer.write("Budget was exceeded by: €" + String.format("%.2f", user.getExceededAmount()) + "\n");
	            System.out.println("Budget was exceeded by: €" + String.format("%.2f", user.getExceededAmount()));
	        }
	        
	        reportBuilder.append("Expenses Recorded: ").append(manager.getAllExpenses().size()).append("\n")
            .append("Generated on: ").append(java.time.LocalDateTime.now()).append("\n");
	        
	        // Write main summary
	        writer.write(reportBuilder.toString());

	        // Category and distribution
	        writeSpendingByCategory(writer, manager);
	        writeSpendingDistribution(writer, manager, report.totalSpent());

	        // Cumulative totals
	        double previousCumulativeTotal = getLastCumulativeTotal(user.getName());
	        double newCumulativeTotal = previousCumulativeTotal + report.totalSpent();

	        StringBuilder cumulativeBuilder = new StringBuilder();
	        cumulativeBuilder.append("\nCumulative Total (All Sessions): ")
	                         .append(formatCurrency(newCumulativeTotal))
	                         .append("\n-------------------------------------------\n\n");

	        writer.write(cumulativeBuilder.toString());
	        writer.flush();

	        System.out.println("\nReport saved successfully as " + fileName);
	    } catch (IOException e) {
	        System.err.println("\nError writing report: " + e.getMessage());
	    }

	    System.out.println("===========================================");
	    System.out.println(colorText("         Thank you for using the app!", BLUE));
	    System.out.println("===========================================");
	}
}

/**
 * 
 */
package studentexpensetracker.exceptions;

/**
 * Custom checked exception which is thrown when expenses exceed the user's budget.
 */
public class BudgetExceededException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public BudgetExceededException(String message) {
        super(message);
    }
}

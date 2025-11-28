/**
 * 
 */
package studentexpensetracker.exceptions;

/**
 * Custom checked exception which is thrown when expenses exceed the user's budget.
 */
public final class BudgetExceededException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private final String message;
	
	public BudgetExceededException(String message) {
        super(message);
        this.message = message;
    }
	
	public String getMessage() {
		return message;
	}
}

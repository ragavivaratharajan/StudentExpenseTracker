/**
 * 
 */
package studentexpensetracker.exceptions;

/**
 * Custom checked exception which is thrown when an invalid expense is seen.
 */
public class InvalidExpenseException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public InvalidExpenseException(String message) {
        super(message);
    }
	
	public InvalidExpenseException(String message, Throwable cause) {
        super(message, cause);
    }
}
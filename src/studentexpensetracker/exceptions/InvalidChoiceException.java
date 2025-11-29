/**
 * 
 */
package studentexpensetracker.exceptions;

/**
 * 
 */
// @UncheckedException: Used for validation failures.
public class InvalidChoiceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

    public InvalidChoiceException(String message) {
        super(message);
    }

    public InvalidChoiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

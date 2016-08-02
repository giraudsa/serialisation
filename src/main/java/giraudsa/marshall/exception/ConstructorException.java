package giraudsa.marshall.exception;

public class ConstructorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4329443859372394647L;

	public ConstructorException() {
	
	}

	public ConstructorException(String message) {
		super(message);
	}

	public ConstructorException(Throwable cause) {
		super(cause);
	}

	public ConstructorException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConstructorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

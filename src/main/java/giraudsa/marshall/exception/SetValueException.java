package giraudsa.marshall.exception;

public class SetValueException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2530428821952692358L;

	public SetValueException() {
		
	}

	public SetValueException(String message) {
		super(message);
	}

	public SetValueException(Throwable cause) {
		super(cause);
	}

	public SetValueException(String message, Throwable cause) {
		super(message, cause);
	}

	public SetValueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}

package giraudsa.marshall.exception;

public class ConstructorException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -4329443859372394647L;

	public ConstructorException() {

	}

	public ConstructorException(final String message) {
		super(message);
	}

	public ConstructorException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ConstructorException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ConstructorException(final Throwable cause) {
		super(cause);
	}

}

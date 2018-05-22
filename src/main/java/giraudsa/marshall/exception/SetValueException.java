package giraudsa.marshall.exception;

public class SetValueException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = -2530428821952692358L;

	public SetValueException() {

	}

	public SetValueException(final String message) {
		super(message);
	}

	public SetValueException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public SetValueException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SetValueException(final Throwable cause) {
		super(cause);
	}
}

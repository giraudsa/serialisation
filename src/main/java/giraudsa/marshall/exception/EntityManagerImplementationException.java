package giraudsa.marshall.exception;

public class EntityManagerImplementationException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 2176109186455553906L;

	public EntityManagerImplementationException() {
	}

	public EntityManagerImplementationException(final String message) {
		super(message);
	}

	public EntityManagerImplementationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public EntityManagerImplementationException(final String message, final Throwable cause,
			final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EntityManagerImplementationException(final Throwable cause) {
		super(cause);
	}

}

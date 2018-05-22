package giraudsa.marshall.exception;

public class ChampNotFound extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -3125450588795982887L;

	public ChampNotFound() {
		super();
	}

	public ChampNotFound(final String message) {
		super(message);
	}

	public ChampNotFound(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ChampNotFound(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ChampNotFound(final Throwable cause) {
		super(cause);
	}

}

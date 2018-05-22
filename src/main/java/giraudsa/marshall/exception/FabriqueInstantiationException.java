package giraudsa.marshall.exception;

public class FabriqueInstantiationException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 4034549541142947425L;

	public FabriqueInstantiationException() {

	}

	public FabriqueInstantiationException(final String message) {
		super(message);
	}

	public FabriqueInstantiationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public FabriqueInstantiationException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FabriqueInstantiationException(final Throwable cause) {
		super(cause);
	}

}

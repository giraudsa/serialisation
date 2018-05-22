package giraudsa.marshall.exception;

public class InstanciationException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -6471150466593908669L;

	public InstanciationException() {
	};

	public InstanciationException(final String message) {
		super(message);
	}

	public InstanciationException(final String message, final Throwable innerException) {
		super(message, innerException);
	}

	public InstanciationException(final String message, final Throwable innerException, final boolean arg2,
			final boolean arg3) {
		super(message, innerException, arg2, arg3);
	}

	public InstanciationException(final Throwable innerException) {
		super(innerException);
	}

}

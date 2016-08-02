package giraudsa.marshall.exception;

public class EntityManagerImplementationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2176109186455553906L;

	public EntityManagerImplementationException() {
	}

	public EntityManagerImplementationException(String message) {
		super(message);
	}

	public EntityManagerImplementationException(Throwable cause) {
		super(cause);
	}

	public EntityManagerImplementationException(String message, Throwable cause) {
		super(message, cause);
	}

	public EntityManagerImplementationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

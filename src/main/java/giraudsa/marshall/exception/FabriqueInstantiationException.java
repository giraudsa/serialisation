package giraudsa.marshall.exception;

public class FabriqueInstantiationException extends Exception {

	public FabriqueInstantiationException() {
		
	}

	public FabriqueInstantiationException(String message) {
		super(message);
	}

	public FabriqueInstantiationException(Throwable cause) {
		super(cause);
	}

	public FabriqueInstantiationException(String message, Throwable cause) {
		super(message, cause);
	}

	public FabriqueInstantiationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

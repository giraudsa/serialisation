package giraudsa.marshall.exception;

public class ChampNotFound extends RuntimeException {

	public ChampNotFound() {
		super();
	}

	public ChampNotFound(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ChampNotFound(String message, Throwable cause) {
		super(message, cause);
	}

	public ChampNotFound(String message) {
		super(message);
	}

	public ChampNotFound(Throwable cause) {
		super(cause);
	}

}

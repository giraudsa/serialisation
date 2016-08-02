package giraudsa.marshall.exception;

public class InstanciationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6471150466593908669L;
	

	public InstanciationException() {};

	public InstanciationException(String message) {
		super(message);
	}

	public InstanciationException(Throwable innerException) {
		super(innerException);
	}

	public InstanciationException(String message, Throwable innerException) {
		super(message, innerException);
	}

	public InstanciationException(String message, Throwable innerException, boolean arg2, boolean arg3) {
		super(message, innerException, arg2, arg3);
	}

}

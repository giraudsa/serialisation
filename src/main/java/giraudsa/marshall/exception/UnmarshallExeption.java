package giraudsa.marshall.exception;

public class UnmarshallExeption extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7789266222347116285L;

	public UnmarshallExeption(String message, Exception e2) {
		super(message, e2);
	}

	public UnmarshallExeption(String message) {
		super(message);
	}
}

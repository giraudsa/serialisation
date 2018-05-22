package giraudsa.marshall.exception;

public class UnmarshallExeption extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = -7789266222347116285L;

	public UnmarshallExeption(final String message) {
		super(message);
	}

	public UnmarshallExeption(final String message, final Exception e2) {
		super(message, e2);
	}
}

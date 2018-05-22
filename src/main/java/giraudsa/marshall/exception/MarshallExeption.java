package giraudsa.marshall.exception;

public class MarshallExeption extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = -7789266222347116285L;

	public MarshallExeption(final Exception e) {
		super(e);
	}

	public MarshallExeption(final String explication) {
		super(explication);
	}
}

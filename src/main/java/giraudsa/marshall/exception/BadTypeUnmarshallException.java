package giraudsa.marshall.exception;


public class BadTypeUnmarshallException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 274896312780441614L;

	public BadTypeUnmarshallException(String string) {
		super(string);
	}

	public BadTypeUnmarshallException(String string, Exception e) {
		super(string,e);
	}

}

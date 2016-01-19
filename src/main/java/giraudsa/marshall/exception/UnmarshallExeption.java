package giraudsa.marshall.exception;

public class UnmarshallExeption extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7789266222347116285L;
	private Exception e;
	private String explication;

	public UnmarshallExeption(Exception e) {
		this.e = e;
	}
	public UnmarshallExeption(String explication) {
		this.explication = explication;
	}
	public UnmarshallExeption(String message, Exception e2) {
		explication = message;
		e = e2;
	}
	@Override
	public String getMessage() {
		return explication + e.getMessage();
	}


}

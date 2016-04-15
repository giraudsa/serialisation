package giraudsa.marshall.exception;

public class MarshallExeption extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7789266222347116285L;
	
	public MarshallExeption(Exception e) {
		super(e);
	}
	public MarshallExeption(String explication){
		super(explication);
	}
}

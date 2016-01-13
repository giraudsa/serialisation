package giraudsa.marshall.exception;

public class MarshallExeption extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7789266222347116285L;
	private Exception e;
	private String explication;

	public MarshallExeption(Exception e) {
		this.e = e;
	}
	public MarshallExeption(String explication){
		this.explication = explication;
	}
	@Override
	public String getMessage() {
		if (e != null)
			return e.getMessage();
		return explication;
	}


}

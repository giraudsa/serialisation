package giraudsa.marshall.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

public class UnmarshallExeption extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7789266222347116285L;
	private Exception e;
	private String explication;

	public UnmarshallExeption(String message, Exception e2) {
		explication = message;
		e = e2;
	}
	@Override
	public String getMessage() {
		return explication + e.getMessage();
	}
	@Override
	public String getLocalizedMessage() {
		return e.getLocalizedMessage();
	}
	@Override
	public synchronized Throwable getCause() {
		
		return e.getCause();
	}
	@Override
	public synchronized Throwable initCause(Throwable paramThrowable) {
		
		return e.initCause(paramThrowable);
	}
	@Override
	public String toString() {
		
		return e.toString();
	}
	@Override
	public void printStackTrace() {
		
		e.printStackTrace();
	}
	@Override
	public void printStackTrace(PrintStream paramPrintStream) {
		
		e.printStackTrace(paramPrintStream);
	}
	@Override
	public void printStackTrace(PrintWriter paramPrintWriter) {
		
		e.printStackTrace(paramPrintWriter);
	}
	
	@Override
	public StackTraceElement[] getStackTrace() {
		
		return e.getStackTrace();
	}
	@Override
	public void setStackTrace(StackTraceElement[] paramArrayOfStackTraceElement) {
		
		e.setStackTrace(paramArrayOfStackTraceElement);
	}

	

}

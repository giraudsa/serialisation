package giraudsa.marshall.serialisation.text;

import giraudsa.marshall.serialisation.ActionAbstrait;

import java.io.IOException;
import java.text.DateFormat;

public abstract class ActionText<T> extends ActionAbstrait<T> {
	
	protected DateFormat getDateFormat(){
		return getTextMarshaller().df;
	}

	protected TextMarshaller getTextMarshaller(){
		return (TextMarshaller)marshaller;
	}
	
	public ActionText(Class<T> type, TextMarshaller textM) {
		super(type, textM);
	}

	protected void write(String s) throws IOException {
		getTextMarshaller().write(s);
	}
}

package giraudsa.marshall.serialisation.text;

import giraudsa.marshall.serialisation.ActionAbstrait;

import java.io.IOException;

public abstract class ActionText<T> extends ActionAbstrait<T> {

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

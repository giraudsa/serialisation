package giraudsa.marshall.deserialisation.text;

import java.text.DateFormat;

import giraudsa.marshall.deserialisation.ActionAbstrait;

public class ActionText<T> extends ActionAbstrait<T> {
	
	protected DateFormat getDateFormat(){
		return ((TextUnmarshaller<?>)unmarshaller).df;
	}

	public ActionText(Class<T> type, String nom, TextUnmarshaller<?> textUnmarshaller) {
		super(type, nom, textUnmarshaller);
	}

}

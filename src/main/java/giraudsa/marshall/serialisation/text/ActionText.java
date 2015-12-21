package giraudsa.marshall.serialisation.text;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.ActionAbstrait;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;

public abstract class ActionText<T> extends ActionAbstrait<T> {
	
	protected DateFormat getDateFormat(){
		return getTextMarshaller().df;
	}

	protected TextMarshaller getTextMarshaller(){
		return (TextMarshaller)marshaller;
	}
	
	public ActionText(TextMarshaller textM) {
		super(textM);
	}

	protected void write(String s) throws IOException {
		getTextMarshaller().write(s);
	}

	public abstract void marshall(Object obj, TypeRelation relation, String nomClef,
			boolean typeDevinable) throws IOException, InstantiationException,
					IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
					SecurityException, NotImplementedSerializeException;
	
}

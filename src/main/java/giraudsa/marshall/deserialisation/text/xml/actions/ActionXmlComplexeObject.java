package giraudsa.marshall.deserialisation.text.xml.actions;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import giraudsa.marshall.deserialisation.text.xml.ActionXml;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

public abstract class ActionXmlComplexeObject<T> extends ActionXml<T> {

	protected ActionXmlComplexeObject(Class<T> type, XmlUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@Override
	protected void rempliData(String donnees) throws ParseException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		//Rien a faire
	}

}

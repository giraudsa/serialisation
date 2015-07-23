package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.text.xml.ActionXml;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringEscapeUtils;

public class ActionXmlSimpleComportement<T> extends ActionXml<T> {

	public ActionXmlSimpleComportement(Class<T> type, String nom) {
		super(type, nom);
	}

	@Override
	protected void rempliData(String donnees) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		obj = type.getConstructor(String.class).newInstance(StringEscapeUtils.unescapeXml(donnees));
	}
}
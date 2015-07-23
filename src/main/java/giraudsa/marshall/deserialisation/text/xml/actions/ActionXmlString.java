package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.Unmarshaller;

import java.lang.reflect.InvocationTargetException;

public class ActionXmlString extends ActionXmlSimpleComportement<String>{

	StringBuilder sb = new StringBuilder();
	
	public ActionXmlString(Class<String> type, String nom) {
		super(type, nom);
	}
	
	@Override protected void rempliData(String donnees) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		sb.append(donnees);
	}
	
	@Override protected <U> void construitObjet(Unmarshaller<U> um) throws InstantiationException, IllegalAccessException {
		obj = sb.toString();
	}

}

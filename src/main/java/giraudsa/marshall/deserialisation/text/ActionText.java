package giraudsa.marshall.deserialisation.text;

import java.text.DateFormat;

import giraudsa.marshall.deserialisation.ActionAbstrait;

public abstract class ActionText<T> extends ActionAbstrait<T> {
	
	protected String nom;
	
	void setNom(String nom){
		this.nom = nom;
	}
	String getNom() {
		return nom;
	}
	
	protected DateFormat getDateFormat(){
		return ((TextUnmarshaller<?>)unmarshaller).df;
	}

	protected ActionText(Class<T> type, TextUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}
	

	Class<?> getType(String nomAttribut) {
		return getTypeAttribute(nomAttribut);
	}
	protected Class<?> getTypeAttribute(String nomAttribut) {
		return null;
	}

}

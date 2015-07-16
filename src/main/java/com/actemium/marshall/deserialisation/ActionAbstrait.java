package com.actemium.marshall.deserialisation;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import com.actemium.marshall.deserialisation.xml.XmlUnmarshaller;

public abstract class ActionAbstrait<T> {
	
	protected Class<T> type;
	protected T obj;
	protected String nom;
	
	public ActionAbstrait(Class<T> type, String nom){
		this.type = type;
		this.nom = nom;
	}
	
	protected <W,X> W getObject(Unmarshaller<X> unmarshaller, String id, Class<W> type) throws InstantiationException, IllegalAccessException{
		return unmarshaller.getObject(id, type);
	}
	
	protected String getNom() {
		return nom;
	}
	
	protected T getObjet(){
		return obj;
	}
	
	protected <W> void integreObjet(String nomAttribut, W objet){}
	protected void rempliData(String donnees) throws ParseException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{}
	protected <U> void construitObjet(Unmarshaller<U> unmarshaller) throws InstantiationException, IllegalAccessException{}
	
}

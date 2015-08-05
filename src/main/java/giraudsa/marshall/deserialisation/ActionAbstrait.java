package giraudsa.marshall.deserialisation;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

public abstract class ActionAbstrait<T> {
	
	protected Class<? extends T> type;
	protected T obj;
	protected String nom;
	protected Unmarshaller<?> unmarshaller;
	protected TypeRelation relation;
	protected boolean leTypeEstEvident;
	
	public ActionAbstrait(Class<? extends T> type, String nom, Unmarshaller<?> b){
		this.type = type;
		this.nom = nom;
		this.unmarshaller = b;
	}
	public ActionAbstrait(Class<? extends T> type, TypeRelation relation, Unmarshaller<?> b) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException{
		this.type = type;
		this.relation = relation;
		this.unmarshaller = b;
		construitObjet();
	}
	public ActionAbstrait(Class<? extends T> type, Unmarshaller<?> un){
		this.type = type;
		this.unmarshaller = un;
	}
	
	protected void construitObjet() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {}
	
	
	protected <W> W getObject(String id, Class<W> type, boolean isFakeId) throws InstantiationException, IllegalAccessException{
		return unmarshaller.getObject(id, type, isFakeId);
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

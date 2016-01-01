package giraudsa.marshall.deserialisation;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

public abstract class ActionAbstrait<T> {
	
	protected Class<T> type;
	protected Object obj;
	protected Unmarshaller<?> unmarshaller;
	protected TypeRelation relation;
	protected boolean typeDevinable;
	
	
	
	void setRelation (TypeRelation relation){
		this.relation = relation;
	}
	
	void  setTypeDevinable(boolean typeDevinable){
		this.typeDevinable = typeDevinable;
	}
	
	public abstract <U extends T>  ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller);
	
	protected ActionAbstrait(Class<T> type, Unmarshaller<?> unmarshaller){
		this.type = type;
		this.unmarshaller = unmarshaller;
	}
	
	protected abstract void construitObjet() throws InstantiationException, IllegalAccessException;
	
	
	protected <W> W getObject(String id, Class<W> type) throws InstantiationException, IllegalAccessException{
		return unmarshaller.getObject(id, type);
	}
	

	
	
	
	protected Object getObjetDejaVu(){
		return obj;
	}
		
	protected abstract <W> void integreObjet(String nomAttribut, W objet);
	protected abstract void rempliData(String donnees) throws ParseException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

}

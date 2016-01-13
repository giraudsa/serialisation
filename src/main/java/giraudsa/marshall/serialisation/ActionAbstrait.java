package giraudsa.marshall.serialisation;


import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Deque;

import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public abstract class ActionAbstrait<T> {
	protected Marshaller marshaller;
	protected ActionAbstrait(Marshaller marshaller){
		this.marshaller = marshaller;
	}

	protected Class<?> getType(T obj){
		if (obj == null)
			return Void.class;
		return obj.getClass();
	}
	
	protected abstract void marshall(Object obj, FieldInformations fieldInformations);

	protected <U> boolean isDejaVu(U objet){
		return marshaller.isDejaVu(objet);
	}
	
	protected <U> void setDejaVu(U objet){
		marshaller.setDejaVu(objet);
	}
	
	protected boolean isUniversalId(){
		return true;
	}
	
	protected <U> boolean isDejaTotalementSerialise(U object){
		return marshaller.isDejaTotalementSerialise(object);
	}
	
	protected <U> void setDejaTotalementSerialise(U object){
		marshaller.setDejaTotalementSerialise(object);
	}
	
	
	protected boolean isTypeDevinable(Object value, FieldInformations fieldInformations){
		if (value == null)
			return false;
		if(isDejaVu(value) && isUniversalId())
			return true;
		return fieldInformations.isTypeDevinable(value);
	}
	
	protected <V> void marshallValue(V value, FieldInformations fieldInformations) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException{
		marshaller.marshall(value, fieldInformations);
	}
	
	protected Comportement traiteChamp(Object obj, FieldInformations fieldInformations, boolean ecrisSeparateur) throws InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException, IllegalAccessException {
		Object value = fieldInformations.get(obj);
		if(aTraiter(value, fieldInformations)){
			return new ComportementMarshallValue(value, fieldInformations, ecrisSeparateur);
		}
		return null;
	}

	protected Comportement traiteChamp(Object obj, FieldInformations fieldInformations) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException{
		return traiteChamp(obj, fieldInformations, true);
	}
	
	@SuppressWarnings("rawtypes")
	protected <V> boolean aTraiter(V value, FieldInformations fieldInformations) throws IOException {
		if(fieldInformations instanceof FakeChamp)
			return true;
		if(value != null && !(value instanceof String && ((String)value).isEmpty())) 
				return !(value instanceof Collection && ((Collection)value).isEmpty());
		return false;
	}

	protected void writeSeparator() throws IOException {}
	
	protected void traiteChampsComplexes(Object objetASerialiser, TypeRelation typeRelation){}

	protected void pushComportement(Comportement comportement) {
		marshaller.aFaire.push(comportement);
	}
	
	protected void pushComportements(Deque<Comportement> comportements){
		while(!comportements.isEmpty()){
			pushComportement(comportements.pop());
		}
	}
	
	protected boolean isCompleteMarshalling(){ //ignore relation
		return marshaller.isCompleteSerialisation;
	}
	
	protected abstract class Comportement {
		protected abstract void evalue() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption;
	}
	
	protected class ComportementMarshallValue extends Comportement{
		private Object value;
		private FieldInformations fieldInformations;
		private boolean writeSeparateur;
		
		protected ComportementMarshallValue(Object value, FieldInformations fieldInformations, boolean writeSeparateur) {
			super();
			this.value = value;
			this.fieldInformations = fieldInformations;
			this.writeSeparateur = writeSeparateur;
		}

		protected ComportementMarshallValue(Object value, FieldInformations fieldInformations) {//XML Collection et Dictionnaire
			super();
			this.value = value;
			this.fieldInformations = fieldInformations;
			this.writeSeparateur = false;
		}
		
		
		@Override
		protected void evalue() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException{
			if(writeSeparateur)
					writeSeparator();
			marshallValue(value, fieldInformations);
			
		}
		
	}
}

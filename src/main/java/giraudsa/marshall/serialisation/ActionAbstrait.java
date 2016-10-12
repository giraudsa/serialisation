package giraudsa.marshall.serialisation;


import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;

import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public abstract class ActionAbstrait<T> {

	protected ActionAbstrait(){
		super();
	}

	protected Class<?> getType(T obj){
		if (obj == null)
			return Void.class;
		return obj.getClass();
	}
	
	protected abstract void marshall(Marshaller marshaller, Object obj, FieldInformations fieldInformations) throws MarshallExeption;

	protected <U> boolean isDejaVu(Marshaller marshaller,U objet){
		return marshaller.isDejaVu(objet);
	}
	
	protected <U> void setDejaVu(Marshaller marshaller, U objet){
		marshaller.setDejaVu(objet);
	}
	
	protected boolean isUniversalId(Marshaller marshaller){
		return true;
	}
	
	protected <U> boolean isDejaTotalementSerialise(Marshaller marshaller, U object){
		return marshaller.isDejaTotalementSerialise(object);
	}
	
	protected <U> void setDejaTotalementSerialise(Marshaller marshaller, U object){
		marshaller.setDejaTotalementSerialise(object);
	}
	
	
	protected boolean isTypeDevinable(Marshaller marshaller, Object value, FieldInformations fieldInformations){
		if (value == null)
			return false;
		if(isDejaVu(marshaller, value) && isUniversalId(marshaller))
			return true;
		return fieldInformations.isTypeDevinable(value);
	}
	
	protected Comportement traiteChamp(Marshaller marshaller, Object obj, FieldInformations fieldInformations, boolean ecrisSeparateur) throws InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException, IllegalAccessException, MarshallExeption {
		Object value = fieldInformations.get(obj, getDicoObjToFakeId(marshaller));
		if(aTraiter(marshaller, value, fieldInformations)){
			return new ComportementMarshallValue(value, fieldInformations, ecrisSeparateur);
		}
		return null;
	}

	protected Comportement traiteChamp(Marshaller marshaller, Object obj, FieldInformations fieldInformations) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException, MarshallExeption{
		return traiteChamp(marshaller, obj, fieldInformations, true);
	}
	
	protected <V> boolean aTraiter(Marshaller marshaller, V value, FieldInformations fieldInformations) throws IOException, MarshallExeption {
		if(fieldInformations instanceof FakeChamp)
			return true;
		if(fieldInformations.isChampId() && value == null)
			throw new MarshallExeption("l'objet a un id null");
		return value != null;
	}

	protected void writeSeparator(Marshaller marshaller) throws IOException {}
	
	protected void pushComportement(Marshaller marshaller, Comportement comportement) {
		marshaller.aFaire.push(comportement);
	}
	
	protected void pushComportements(Marshaller marshaller, Deque<Comportement> comportements){
		while(!comportements.isEmpty()){
			pushComportement(marshaller, comportements.pop());
		}
	}
	
	protected boolean strategieSerialiseTout(Marshaller marshaller, FieldInformations fieldInformations) {
		return marshaller.getStrategie().serialiseTout(marshaller.getProfondeur(), fieldInformations);
	}
	
	protected void diminueProfondeur(Marshaller marshaller) {
		marshaller.diminueProfondeur();
	}
	
	protected void augmenteProdondeur(Marshaller marshaller){
		marshaller.augmenteProdondeur();
	}
	
	protected abstract class Comportement {
		protected abstract void evalue(Marshaller marshaller) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption;
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

		@Override
		protected void evalue(Marshaller marshaller) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption{
			if(writeSeparateur)
					writeSeparator(marshaller);
			marshaller.marshall(value, fieldInformations);
			
		}
	}
	
	protected Map<Object, UUID> getDicoObjToFakeId(Marshaller marshaller) {
		return marshaller.getDicoObjToFakeId();
	}
}

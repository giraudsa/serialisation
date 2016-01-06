package giraudsa.marshall.serialisation;


import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Stack;

import utils.champ.Champ;

public abstract class ActionAbstrait<T> {

	protected Marshaller marshaller;
	protected Class<?> getType(T obj){
		if (obj == null) return Void.class;
		return obj.getClass();
	}
	
	public ActionAbstrait(Marshaller marshaller){
		this.marshaller = marshaller;
	}
	
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
	
	
	protected boolean isTypeDevinable(Object value, Champ champ){
		if (value == null) return false;
		if(isDejaVu(value) && isUniversalId()) return true;
		Class<?> valueType = value.getClass();
		boolean typeDevinable = true;
		if(!champ.isSimple && champ.valueType != valueType) typeDevinable = false;
		return typeDevinable;
	}
	
	protected <TypeValue> void marshallValue(TypeValue value, String nom, TypeRelation relation, boolean typeDevinable ) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, IOException{
		marshaller.marshall(value, relation, nom, typeDevinable);
	}
	
	protected Comportement traiteChamp(T obj, Champ champ, boolean ecrisSeparateur) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, IOException {
		String nom = champ.name;
		TypeRelation relation = champ.relation;
		Object value = champ.get(obj);
		boolean typeDevinable = isTypeDevinable(value, champ);
		if(aTraiter(value)){
			return new ComportementMarshallValue(value, nom, relation, typeDevinable, ecrisSeparateur);
		}
		return null;
	}
	
	protected Comportement traiteChamp(T obj, Champ champ) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, IOException{
		return traiteChamp(obj, champ, true);
	}
	
	@SuppressWarnings("rawtypes")
	protected <TypeValue> boolean aTraiter(TypeValue value) throws IOException {
		boolean aTraiter = value != null
				&& !(value instanceof String && ((String)value).isEmpty()) 
				&& !(value instanceof Collection && ((Collection)value).isEmpty());
		return aTraiter;
	}

	protected void writeSeparator() throws IOException {}
	
	protected void traiteChampsComplexes(Object objetASerialiser, TypeRelation typeRelation){}

	protected void pushComportement(Comportement comportement) {
		marshaller.aFaire.push(comportement);
	}
	
	protected void pushComportements(Stack<Comportement> comportements){
		while(!comportements.isEmpty()){
			pushComportement(comportements.pop());
		}
	}
	
	protected boolean isCompleteMarshalling(){ //ignore relation
		return marshaller.isCompleteSerialisation;
	}
	
	protected abstract class Comportement {
		public abstract void evalue() throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException;
	}
	
	protected class ComportementMarshallValue extends Comportement{
		private Object value;
		private String nom;
		private TypeRelation relation;
		private boolean typeDevinable;
		private boolean writeSeparateur;
		
		public ComportementMarshallValue(Object value, String nom, TypeRelation relation, boolean typeDevinable, boolean writeSeparateur) {
			super();
			this.value = value;
			this.nom = nom;
			this.relation = relation;
			this.typeDevinable = typeDevinable;
			this.writeSeparateur = writeSeparateur;
		}

		public ComportementMarshallValue(Object value, String nom, TypeRelation relation, boolean typeDevinable) {//XML Collection et Dictionnaire
			super();
			this.value = value;
			this.nom = nom;
			this.relation = relation;
			this.typeDevinable = typeDevinable;
			this.writeSeparateur = false;
		}
		
		public ComportementMarshallValue(Object value, TypeRelation relation, boolean typeDevinable) {//Binary Collection et Dictionnaire
			super();
			this.value = value;
			this.relation = relation;
			this.typeDevinable = typeDevinable;
			this.writeSeparateur = false;
		}
		
		@Override
		public void evalue() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, IOException{
			if(writeSeparateur) writeSeparator();
			marshallValue(value, nom, relation, typeDevinable);
		}
		
	}
}

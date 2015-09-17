package giraudsa.marshall.serialisation;


import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import utils.TypeExtension;
import utils.champ.Champ;

public abstract class ActionAbstrait<T> {
	protected T obj;
	protected TypeRelation relation;
	protected Marshaller marshaller;
	protected Class<? super T> type;
	protected Class<?> getType(){
		return type;
	}
	
	public ActionAbstrait(Class<? super T> type, Marshaller marshaller){
		this.type = type;
		this.marshaller = marshaller;
	}

	protected abstract void marshall(T obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException;
	
	protected <U> boolean isDejaVu(U objet){
		return marshaller.isDejaVu(objet);
	}
	
	void stockeDejaVu(Object obj, int smallId){
		marshaller.stockDejaVu(obj, smallId);
	}
	
	protected <U> int getSmallIdAndStockObj(U obj){
		return marshaller.getSmallIdAndStockObj(obj);
	}
	
	protected void traiteChamp(T obj, Champ champ) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, IOException {
		String nom = champ.name;
		TypeRelation relation = champ.relation;
		Object value = champ.get(obj);
		if(aTraiter(value)){
			writeSeparator();
			marshallValue(value, nom, relation);
		}
	}
	
	protected <TypeValue> void marshallValue(TypeValue value, String nom, TypeRelation relation) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, IOException{
		marshaller.marshall(value, relation, nom);
	}

	@SuppressWarnings("rawtypes")
	private <TypeValue> boolean aTraiter(TypeValue value) throws IOException {
		boolean aTraiter = value != null
				&& !(value instanceof String && ((String)value).isEmpty()) 
				&& !(value instanceof Collection && ((Collection)value).isEmpty());
		return aTraiter;
	}
	

	@SuppressWarnings("unchecked")
	protected void ecritValeur(T obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		Class<T> typeObj = (Class<T>) obj.getClass();
		List<Champ> champs = TypeExtension.getSerializableFields(typeObj);
		Champ champId = TypeExtension.getChampId(typeObj);
		boolean onlyWriteReference = relation != TypeRelation.COMPOSITION || isDejaVu(obj);
		traiteChamp(obj, champId);
		if(!onlyWriteReference){//on ecrit tout
			getSmallIdAndStockObj(obj);
			for (Champ champ : champs){
				if (champ != champId){
					traiteChamp(obj, champ);
				}
			}
		}else{//on le met dans la file pour le traiter plus tard
			stockeASerialiser(obj);
		}
	}

	protected void writeSeparator() throws IOException {}
	
	protected void stockeASerialiser(Object objet){
		if(marshaller.aSerialiser != null && !marshaller.estSerialise.contains(objet)) marshaller.aSerialiser.add(objet);
	}
	
	protected boolean setEstSerialiseEtRetourneSiLObjetEtaitDejaSerialise(Object objet) {
		boolean ret = marshaller.estSerialise.add(objet);
		if(marshaller.aSerialiser != null) marshaller.aSerialiser.remove(objet);
		return !ret;
	}
}

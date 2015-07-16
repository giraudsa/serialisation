package com.actemium.marshall.serialisation;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import utils.TypeExtension;
import utils.champ.Champ;

import com.actemium.marshall.annotations.TypeRelation;
import com.actemium.marshall.exception.NotImplementedSerializeException;
import com.actemium.marshall.serialisation.Marshaller.SetQueue;

public abstract class ActionAbstrait<T> {
	protected Class<T> type;
	protected Class<?> getType(){
		return type;
	}
	
	public ActionAbstrait(Class<T> type){
		this.type = type;
	}
	
	protected void write(Marshaller marshaller, String s) throws IOException {
		marshaller.write(s);
	}

	public abstract void marshall(T obj, TypeRelation relation, Marshaller marchaller, SetQueue<Object> aSerialiser) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException;
	
	protected <U> boolean dejaVu(Marshaller m, U obj){
		return m.dejaVu.contains(obj);
	}
	
	protected <U> void setDejaVu(Marshaller m, U obj){
		m.dejaVu.add(obj);
	}
	
	protected void traiteChamp(Marshaller marshaller, T obj, Champ champ) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, IOException {
		String nom = champ.name;
		TypeRelation relation = champ.relation;
		Object value = champ.get(obj);
		if(aTraiter(value)){
			writeSeparator(marshaller);
			marshallValue(marshaller, value, nom, relation);
		}
	}
	
	protected <TypeValue> void marshallValue(Marshaller marshaller, TypeValue value, String nom, TypeRelation relation) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, IOException{
		marshaller.marshall(value, relation, nom);
	}

	private <TypeValue> boolean aTraiter(TypeValue value) {
		return value != null && !(value instanceof String && ((String)value).isEmpty());
	}
	
	@SuppressWarnings("unchecked")
	protected void ecritValeur(T obj, TypeRelation relation, Marshaller marshaller, SetQueue<Object> aSerialiser) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		boolean onlyWriteReference = relation != TypeRelation.COMPOSITION || dejaVu(marshaller, obj);
		Class<T> typeObj = (Class<T>) obj.getClass();
		List<Champ> champs = TypeExtension.getSerializableFields(typeObj);
		Champ champId = TypeExtension.getChampId(typeObj);
		traiteChamp(marshaller, obj, champId);
		if(!onlyWriteReference){//on ecrit tout
			setDejaVu(marshaller, obj);
			for (Champ champ : champs){
				if (champ != champId){
					traiteChamp(marshaller, obj, champ);
				}
			}
		}else if(aSerialiser != null){//on le met dans la file pour le traiter plus tard
			aSerialiser.add(obj);
		}
	}

	protected void writeSeparator(Marshaller marshaller) throws IOException {}
}

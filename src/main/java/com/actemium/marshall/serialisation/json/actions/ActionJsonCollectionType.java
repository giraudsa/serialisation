package com.actemium.marshall.serialisation.json.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import com.actemium.marshall.annotations.TypeRelation;
import com.actemium.marshall.exception.NotImplementedSerializeException;
import com.actemium.marshall.serialisation.Marshaller;
import com.actemium.marshall.serialisation.Marshaller.SetQueue;
import com.actemium.marshall.serialisation.json.ActionJson;
import com.actemium.marshall.serialisation.json.JsonMarshaller;

public class ActionJsonCollectionType<T> extends ActionJson<T> {
	
	Class<?> _type;
	@Override
	protected Class<?> getType() {
		if(_type != null) return _type;
		Class<?> t = null;
		try{
			type.newInstance();
			t = type;
		}catch(Exception e){
			t = ArrayList.class;
		}
		_type = t;
		return t;
	}

	public ActionJsonCollectionType(Class<T> type, String nomClef) {
		super(type, nomClef);
	}
	
	@Override
	protected boolean isTypeConnu() {
		return getType() == ArrayList.class;
	}
	
	@Override
	protected void ouvreAccolade(JsonMarshaller jsonM) throws IOException {
		if(isTypeConnu()){
			write(jsonM, "[");
		}else{//type inconnu pour deserialisation
			write(jsonM, "{");
			ecritType(jsonM);
			writeSeparator(jsonM);
			ecritClef(jsonM, "valeur");
			write(jsonM, "[");
		}
		
	}
	@Override
	protected void fermeAccolade(JsonMarshaller jsonM, T obj) throws IOException {
		if(isTypeConnu()){
			write(jsonM, "]");
		}else{
			write(jsonM, "]}");
		}
	}

	@Override
	protected void ecritValeur(T obj, TypeRelation relation, Marshaller jsonM, SetQueue<Object> aSerialiser) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		Collection<?> collection = (Collection<?>) obj;
		int i=0;
		for (Object value : collection) {
			if(i++ > 0) write(jsonM,",");
			marshallValue(jsonM, value, null, relation);
		}
	}

}

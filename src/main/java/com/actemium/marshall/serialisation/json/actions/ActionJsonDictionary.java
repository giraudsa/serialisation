package com.actemium.marshall.serialisation.json.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.actemium.marshall.annotations.TypeRelation;
import com.actemium.marshall.exception.NotImplementedSerializeException;
import com.actemium.marshall.serialisation.Marshaller;
import com.actemium.marshall.serialisation.Marshaller.SetQueue;
import com.actemium.marshall.serialisation.json.JsonMarshaller;
import com.actemium.marshall.serialisation.json.Pair;

@SuppressWarnings("rawtypes")
public class ActionJsonDictionary<T extends Map> extends ActionJsonCollectionType<T> {
	
	@Override
	protected Class<?> getType() {
		Class<?> t = null;
		try{
			type.newInstance();
			t = type;
		}catch(Exception e){
			t = HashMap.class;
		}
		return t;
	}
	
	public ActionJsonDictionary(Class<T> type, String nomClef) {
		super(type, nomClef);
	}

	@Override
	protected void ecritValeur(T obj, TypeRelation relation, Marshaller jsonM, SetQueue<Object> aSerialiser) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		Map<?,?> map = (Map<?,?>) obj;
		int i = 0;
		for (Entry<?, ?> entry : map.entrySet()) {
			Pair pair = new Pair(entry.getKey(), entry.getValue());
			if(i++ > 0) writeSeparator(jsonM);
			marshallValue(jsonM, pair, null, relation);
		}
	}
	
	@Override
	protected void ouvreAccolade(JsonMarshaller jsonM) throws IOException {
		write(jsonM, "{");
		ecritType(jsonM);
		write(jsonM, ",");
		ecritClef(jsonM, "valeur");
		write(jsonM, "[");		
	}
	@Override
	protected void fermeAccolade(JsonMarshaller jsonM, T obj) throws IOException {
		if(isTypeConnu()){
			write(jsonM, "]");
		}else{
			write(jsonM, "]}");
		}
	}
}

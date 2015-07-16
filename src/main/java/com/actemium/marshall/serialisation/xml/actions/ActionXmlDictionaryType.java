package com.actemium.marshall.serialisation.xml.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.actemium.marshall.annotations.TypeRelation;
import com.actemium.marshall.exception.NotImplementedSerializeException;
import com.actemium.marshall.serialisation.Marshaller;
import com.actemium.marshall.serialisation.Marshaller.SetQueue;
import com.actemium.marshall.serialisation.xml.ActionXml;

@SuppressWarnings("rawtypes")
public class ActionXmlDictionaryType<T extends Map> extends ActionXml<T> {
	
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

	public ActionXmlDictionaryType(Class<T> type, String nomBalise) {
		super(type, nomBalise);
		if(nomBalise == null) balise = "Dico";
	}
	@Override
	protected void ecritValeur(T obj, TypeRelation relation, Marshaller xmlM, SetQueue<Object> aSerialiser) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		Map<?,?> map = (Map<?,?>) obj;
		for (Entry<?, ?> entry : map.entrySet()) {
			marshallValue(xmlM, entry.getKey(), "K", relation);
			marshallValue(xmlM, entry.getValue(), "V", relation);
		}
	}
}

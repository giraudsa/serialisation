package com.actemium.marshall.serialisation.xml.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import com.actemium.marshall.annotations.TypeRelation;
import com.actemium.marshall.exception.NotImplementedSerializeException;
import com.actemium.marshall.serialisation.Marshaller;
import com.actemium.marshall.serialisation.Marshaller.SetQueue;
import com.actemium.marshall.serialisation.xml.ActionXml;

@SuppressWarnings("rawtypes")
public class ActionXmlCollectionType<T extends Collection> extends ActionXml<T> {

	@Override
	protected Class<?> getType() {
		Class<?> t = null;
		try{
			type.newInstance();
			t = type;
		}catch(Exception e){
			t = ArrayList.class;
		}
		return t;
	}
	public ActionXmlCollectionType(Class<T> type, String nomBalise) {
		super(type, nomBalise);
		if(nomBalise == null) balise = "liste";
	}

	@Override
	protected void ecritValeur(T obj, TypeRelation relation, Marshaller xmlM, SetQueue<Object> aSerialiser) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		Collection<?> collection = (Collection<?>) obj;
		for (Object value : collection) {
			marshallValue(xmlM, value, "V", relation);
		}
	}

}

package com.actemium.marshall.serialisation.xml.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.actemium.marshall.annotations.TypeRelation;
import com.actemium.marshall.exception.NotImplementedSerializeException;
import com.actemium.marshall.serialisation.Marshaller;
import com.actemium.marshall.serialisation.Marshaller.SetQueue;
import com.actemium.marshall.serialisation.xml.ActionXml;

public class ActionXmlVoid<T> extends ActionXml<T> {

	public ActionXmlVoid(Class<T> type, String nomBalise) {
		super(type, nomBalise);
		if(nomBalise == null) balise = "vide";
	}

	@Override
	protected void ecritValeur(T obj, TypeRelation relation, Marshaller xmlM, SetQueue<Object> aSerialiser) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		write(xmlM, "null");
	}
	
}

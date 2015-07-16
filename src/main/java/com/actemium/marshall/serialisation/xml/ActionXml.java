package com.actemium.marshall.serialisation.xml;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.actemium.marshall.annotations.TypeRelation;
import com.actemium.marshall.exception.NotImplementedSerializeException;
import com.actemium.marshall.serialisation.ActionAbstrait;
import com.actemium.marshall.serialisation.Marshaller;
import com.actemium.marshall.serialisation.Marshaller.SetQueue;

public class ActionXml<T> extends ActionAbstrait<T> {
	protected String balise;
	
	public ActionXml(Class<T> type, String nomBalise){
		super(type);
		balise = nomBalise;
		if(nomBalise == null) balise = type.getSimpleName();
	}
	
	@Override
	public void marshall(T obj, TypeRelation relation, Marshaller marchaller, SetQueue<Object> aSerialiser) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		XmlMarshaller xmlM = (XmlMarshaller)marchaller;
		xmlM.openTag(balise, getType());
		ecritValeur(obj, relation, xmlM, aSerialiser);
		xmlM.closeTag(balise);
	}
}

package com.actemium.marshall.serialisation.xml.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringEscapeUtils;

import com.actemium.marshall.annotations.TypeRelation;
import com.actemium.marshall.exception.NotImplementedSerializeException;
import com.actemium.marshall.serialisation.Marshaller;
import com.actemium.marshall.serialisation.Marshaller.SetQueue;
import com.actemium.marshall.serialisation.xml.ActionXml;

public class ActionXmlSimpleComportement<T> extends ActionXml<T> {

	public ActionXmlSimpleComportement(Class<T> type, String nomBalise) {
		super(type, nomBalise);
	}

	@Override
	protected void ecritValeur(T obj, TypeRelation relation, Marshaller xmlM, SetQueue<Object> aSerialiser) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		write(xmlM, StringEscapeUtils.escapeXml10(obj.toString()));
	}
}

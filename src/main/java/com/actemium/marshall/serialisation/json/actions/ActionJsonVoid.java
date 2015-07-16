package com.actemium.marshall.serialisation.json.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.actemium.marshall.annotations.TypeRelation;
import com.actemium.marshall.exception.NotImplementedSerializeException;
import com.actemium.marshall.serialisation.Marshaller;
import com.actemium.marshall.serialisation.Marshaller.SetQueue;
import com.actemium.marshall.serialisation.json.ActionJson;

public class ActionJsonVoid<T> extends ActionJson<T> {

	public ActionJsonVoid(Class<T> type, String nomClef) {
		super(type, nomClef);
	}

	@Override
	protected void ecritValeur(T obj, TypeRelation relation, Marshaller jsonM, SetQueue<Object> aSerialiser) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		write(jsonM, "null");
	}

}

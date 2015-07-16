package com.actemium.marshall.serialisation.json.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringEscapeUtils;

import com.actemium.marshall.annotations.TypeRelation;
import com.actemium.marshall.exception.NotImplementedSerializeException;
import com.actemium.marshall.serialisation.Marshaller;
import com.actemium.marshall.serialisation.Marshaller.SetQueue;
import com.actemium.marshall.serialisation.json.ActionJson;
import com.actemium.marshall.serialisation.json.JsonMarshaller;

public class ActionJsonSimpleComportement<T> extends ActionJson<T> {

	public ActionJsonSimpleComportement(Class<T> type, String nomClef) {
		super(type, nomClef);
	}

	@Override
	protected void ecritValeur(T obj, TypeRelation relation, Marshaller jsonM, SetQueue<Object> aSerialiser) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		write(jsonM, StringEscapeUtils.escapeJson(obj.toString()));
	}
	
	@Override
	protected void ouvreAccolade(JsonMarshaller jsonM) throws IOException {
		if(!isTypeConnu()){
			write(jsonM, "{");
			ecritType(jsonM);
			write(jsonM, ",");
			ecritClef(jsonM, "valeur");
		}
	}
	
	@Override
	protected void fermeAccolade(JsonMarshaller jsonM, T obj) throws IOException {
		if(!isTypeConnu()){
			write(jsonM, "}");
		}
	}

}

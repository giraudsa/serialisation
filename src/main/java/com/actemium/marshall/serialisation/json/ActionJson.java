package com.actemium.marshall.serialisation.json;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import utils.Constants;

import com.actemium.marshall.annotations.TypeRelation;
import com.actemium.marshall.exception.NotImplementedSerializeException;
import com.actemium.marshall.serialisation.ActionAbstrait;
import com.actemium.marshall.serialisation.Marshaller;
import com.actemium.marshall.serialisation.Marshaller.SetQueue;

public class ActionJson<T> extends ActionAbstrait<T>  {
	
	private final String nomClef;
	
	public ActionJson(Class<T> type, String nomClef) {
		super(type);
		this.nomClef = nomClef;
	}

	@Override
	public void marshall(T obj, TypeRelation relation, Marshaller marchaller, SetQueue<Object> aSerialiser) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		JsonMarshaller jsonM = (JsonMarshaller)marchaller;
		ecritClef(jsonM, nomClef);
		ouvreAccolade(jsonM);
		ecritValeur(obj, relation, jsonM, aSerialiser);
		fermeAccolade(jsonM, obj);
	}
	
	protected void fermeAccolade(JsonMarshaller jsonM, T obj) throws IOException {
		write(jsonM, "}");
	}

	protected void ouvreAccolade(JsonMarshaller jsonM) throws IOException {
		write(jsonM, "{");
		ecritType(jsonM);
	}

	protected void ecritClef(JsonMarshaller jsonM, String clef) throws IOException{
		jsonM.ecritClef(clef);
	}
	protected void ecritType(JsonMarshaller jsonM) throws IOException{
		jsonM.ecritType(getType());
	}
	
	protected boolean isTypeConnu(){
		return (nomClef != null && (!nomClef.equals(Constants.MAP_CLEF) || !nomClef.equals(Constants.MAP_VALEUR)))
				|| type == Integer.class || type == int.class || type == boolean.class || type == Boolean.class;
	}
	protected void writeWithQuote(JsonMarshaller jsonM, String string) throws IOException{
		jsonM.writeWithQuote(string);
	}
	
	@Override
	protected void writeSeparator(Marshaller marshaller) throws IOException {
		write(marshaller, ",");
	}
}
